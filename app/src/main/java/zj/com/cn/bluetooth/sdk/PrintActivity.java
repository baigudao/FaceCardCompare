package zj.com.cn.bluetooth.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taisau.facecardcompare.FaceCardApplication;
import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.model.HistoryList;
import com.taisau.facecardcompare.model.Person;
import com.taisau.facecardcompare.util.ImgUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import zj.com.command.sdk.Command;
import zj.com.command.sdk.PrintPicture;
import zj.com.command.sdk.PrinterCommand;

public class PrintActivity extends Activity implements OnClickListener{
/******************************************************************************************************/
	// Debugging
	private static final String TAG = "Print_Activity";
	private static final boolean DEBUG = true;
/******************************************************************************************************/
	// Message types sent from the BluetoothService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_CONNECTION_LOST = 6;
	public static final int MESSAGE_UNABLE_CONNECT = 7;
/*******************************************************************************************************/
	// Key names received from the BluetoothService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int REQUEST_CHOSE_BMP = 3;
	
	//QRcode
	private static final int QR_WIDTH = 350;
	private static final int QR_HEIGHT = 350;
/*******************************************************************************************************/

/*********************************************************************************/	
	//private TextView mTitle;
	EditText editText;
	ImageView imageViewPicture;
	private Button sendButton = null;
	private Button btnScanButton = null;
	private Button btnClose = null;
	private RelativeLayout print_preview;
	private ImageView pre_test;
	private ImageView img1,img2;
	private TextView name,time,date,result,count,idCard,mobile,company,visitor,visitorID;
	private long his_id,white_id;
	private HistoryList historyList;
	private Person person;
/******************************************************************************************************/
	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the services
	private BluetoothService mService = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (DEBUG)
			Log.e(TAG, "+++ ON CREATE +++");

		setContentView(R.layout.main);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		print_preview=(RelativeLayout)findViewById(R.id.print_preview);
		pre_test=(ImageView)findViewById(R.id.img_test);

		name=(TextView)findViewById(R.id.print_preview_name);
		time=(TextView)findViewById(R.id.print_preview_time);
		date=(TextView)findViewById(R.id.print_preview_date);
		result=(TextView)findViewById(R.id.print_preview_result);
		count=(TextView)findViewById(R.id.print_preview_count);
		idCard=(TextView)findViewById(R.id.print_preview_idcard);
		mobile=(TextView)findViewById(R.id.print_preview_mobile);
		company=(TextView)findViewById(R.id.print_preview_company);
		visitor=(TextView)findViewById(R.id.print_preview_visitor_name);
		visitorID=(TextView)findViewById(R.id.print_preview_visitor_idcard);

		img1=(ImageView)findViewById(R.id.print_preview_img1);
		img2=(ImageView)findViewById(R.id.print_preview_img2);

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
		}
		initData();
	}

	public void initData(){
		his_id=getIntent().getLongExtra("his_id",0);
		historyList= FaceCardApplication.getApplication().getDaoSession().getHistoryListDao().load(his_id);
		white_id=getIntent().getLongExtra("white_id",0);
		person=FaceCardApplication.getApplication().getDaoSession().getPersonDao().load(white_id);
		name.setText("来宾姓名："+historyList.getPerson_name());
		date.setText("来访日期："+ DateFormat.format("yyyy-MM-dd HH:mm:ss",historyList.getTime()).toString().substring(0,10));
		time.setText("来访时间："+DateFormat.format("yyyy-MM-dd HH:mm:ss",historyList.getTime()).toString().substring(11));
		result.setText("核查结果："+historyList.getCom_status().substring(0,4));
		if(getIntent().getStringExtra("visitor_count")!=null)
			count.setText("同行人员："+getIntent().getStringExtra("visitor_count"));
		idCard.setText("身份证号："+historyList.getId_card());
		if(getIntent().getStringExtra("visitor_mobile")!=null)
			mobile.setText("联系电话："+getIntent().getStringExtra("visitor_mobile"));
		if(getIntent().getStringExtra("visitor_company")!=null)
			company.setText("来访单位："+getIntent().getStringExtra("visitor_company"));
		if (person!=null) {
			visitor.setText("拜访对象："+person.getPerson_name());
			visitorID.setText("身份证号："+person.getId_card());
		}
		img1.setImageBitmap(BitmapFactory.decodeFile(historyList.getFace_path()));
		img2.setImageBitmap(BitmapFactory.decodeFile(historyList.getCard_path()));
	}

	@Override
	public void onStart() {
		super.onStart();
		
		// If Bluetooth is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the session
		} else {
			if (mService == null)
				KeyListenerInit();//监听
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		
		if (mService != null) {
			
			if (mService.getState() == BluetoothService.STATE_NONE) {
				// Start the Bluetooth services
				mService.start();
			}
		}
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (DEBUG)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (DEBUG)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth services
		if (mService != null)
			mService.stop();
		if (DEBUG)
			Log.e(TAG, "--- ON DESTROY ---");
	}

/*****************************************************************************************************/	
	private void KeyListenerInit() {
		
		//editText = (EditText) findViewById(R.id.edit_text_out);

		sendButton = (Button) findViewById(R.id.Send_Button);
		sendButton.setOnClickListener(this);

		btnScanButton = (Button)findViewById(R.id.button_scan);
		btnScanButton.setOnClickListener(this);
	
		btnClose = (Button)findViewById(R.id.btn_close);
		btnClose.setOnClickListener(this);

		
		Bitmap bm = getImageFromAssetsFile("demo.bmp");
		if (null != bm) {
			imageViewPicture.setImageBitmap(bm);
		}

		sendButton.setEnabled(false);
		mService = new BluetoothService(this, mHandler);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.button_scan:{
			Intent serverIntent = new Intent(PrintActivity.this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			break;
		}
		case R.id.btn_close:{
			mService.stop();
			btnScanButton.setEnabled(true);
			sendButton.setEnabled(false);
			btnScanButton.setText(getText(R.string.connect));
			finish();
			break;
		}
		case R.id.Send_Button:{
			print_preview.setDrawingCacheEnabled(true);
			Bitmap bitmap=print_preview.getDrawingCache();
			bitmap= Bitmap.createBitmap(bitmap);
			ImgUtils.getUtils().saveBitmap(bitmap, Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.jpg");
			//pre_test.setImageBitmap(bitmap);
			Print_BMP(bitmap);
			print_preview.setDrawingCacheEnabled(false);
			break;
		}
		default:
			break;
		}
	}
		
/*****************************************************************************************************/
	/*
	 * SendDataString
	 */
	private void SendDataString(String data) {
		
		if (mService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (data.length() > 0) {				
			try {
				mService.write(data.getBytes("GBK"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 *SendDataByte 
	 */
	private void SendDataByte(byte[] data) {
		
		if (mService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}			
		mService.write(data);
	}

	/****************************************************************************************************/
	@SuppressLint("HandlerLeak") 
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (DEBUG)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothService.STATE_CONNECTED:
					btnScanButton.setText(getText(R.string.Connecting));
					btnScanButton.setEnabled(false);
					sendButton.setEnabled(true);
					btnClose.setEnabled(true);
					break;
				case BluetoothService.STATE_CONNECTING:
					//mTitle.setText(R.string.title_connecting);
					break;
				case BluetoothService.STATE_LISTEN:
				case BluetoothService.STATE_NONE:
				//	mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			case MESSAGE_CONNECTION_LOST:    //蓝牙已断开连接
                Toast.makeText(getApplicationContext(), "Device connection was lost",
                               Toast.LENGTH_SHORT).show();
				sendButton.setEnabled(false);
                break;
            case MESSAGE_UNABLE_CONNECT:     //无法连接设备
            	Toast.makeText(getApplicationContext(), "Unable to connect device",
                        Toast.LENGTH_SHORT).show();
            	break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (DEBUG)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:{
				// When DeviceListActivity returns with a device to connect
				if (resultCode == Activity.RESULT_OK) {
					// Get the device MAC address
					String address = data.getExtras().getString(
							DeviceListActivity.EXTRA_DEVICE_ADDRESS);
					// Get the BLuetoothDevice object
					if (BluetoothAdapter.checkBluetoothAddress(address)) {
						BluetoothDevice device = mBluetoothAdapter
								.getRemoteDevice(address);
						// Attempt to connect to the device
						mService.connect(device);
					}
				}
				break;
			}
			case REQUEST_ENABLE_BT:{
				// When the request to enable Bluetooth returns
				if (resultCode == Activity.RESULT_OK) {
					// Bluetooth is now enabled, so set up a session
					KeyListenerInit();
				} else {
					// User did not enable Bluetooth or an error occured
					Log.d(TAG, "BT not enabled");
					Toast.makeText(this, R.string.bt_not_enabled_leaving,
							Toast.LENGTH_SHORT).show();
					finish();
				}
				break;
			}
			case REQUEST_CHOSE_BMP:{
	        	if (resultCode == Activity.RESULT_OK){
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaColumns.DATA };
		
					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();
		
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();
		
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inJustDecodeBounds = true;
					BitmapFactory.decodeFile(picturePath, opts);
					opts.inJustDecodeBounds = false;
					if (opts.outWidth > 1200) {
						opts.inSampleSize = opts.outWidth / 1200;
					}
					Bitmap bitmap = BitmapFactory.decodeFile(picturePath, opts);
					if (null != bitmap) {
						imageViewPicture.setImageBitmap(bitmap);
					}
	        	}else{
					Toast.makeText(this, getString(R.string.msg_statev1), Toast.LENGTH_SHORT).show();
				}
				break;
			}
			/*case REQUEST_CAMER:{
	        	if (resultCode == Activity.RESULT_OK){
	       // 		handleSmallCameraPhoto(data);
	        	}else{
	        		Toast.makeText(this, getText(R.string.camer), Toast.LENGTH_SHORT).show();
	        	}
	        	break;
	        }*/
		}
	}

	/***
	 * 访客单打印
	 */



	/*
	 * 打印图片
	 */
	private void Print_BMP(Bitmap mBitmap){

		byte[] buffer = PrinterCommand.POS_Set_PrtInit();
		int nMode = 0;
		int nPaperWidth = 576;
		if(mBitmap != null)
		{
			/**
			 * Parameters:
			 * mBitmap  要打印的图片
			 * nWidth   打印宽度（58和80）
			 * nMode    打印模式
			 * Returns: byte[]
			 */
			byte[] data = PrintPicture.POS_PrintBMP(pre_test,mBitmap, nPaperWidth, nMode);
			SendDataByte(buffer);
			SendDataByte(Command.ESC_Init);
			SendDataByte(Command.LF);
			SendDataByte(data);
			SendDataByte(PrinterCommand.POS_Set_PrtAndFeedPaper(30));
			SendDataByte(PrinterCommand.POS_Set_Cut(1));
			SendDataByte(PrinterCommand.POS_Set_PrtInit());
		}		
	}
	 /**
	 * 加载assets文件资源
	 */
	private Bitmap getImageFromAssetsFile(String fileName) {
			Bitmap image = null;
			AssetManager am = getResources().getAssets();
			try {
				InputStream is = am.open(fileName);
				image = BitmapFactory.decodeStream(is);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return image;

		}

}