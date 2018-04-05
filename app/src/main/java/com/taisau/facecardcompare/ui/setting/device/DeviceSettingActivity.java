package com.taisau.facecardcompare.ui.setting.device;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.taisau.facecardcompare.R;
import com.taisau.facecardcompare.ui.BaseActivity;

import java.util.ArrayList;

public class DeviceSettingActivity extends BaseActivity implements View.OnClickListener, IDeviceSettingView {
    private TextView serialNumberTv, serverIpTv, serverPortTv, softStorageTv, listStorageTv;
    private Spinner provincesSpinner, citiesSpinner, countiesSpinner;
    private CheckBox readTypeCheckBox;
    private String changeText;
    private DeviceSettingPresenter presenter;
    private ArrayAdapter<String> adapterCity;
    private ArrayAdapter<String> adapterCon;
    private boolean isFirstTime = true;
    private String provinceNameCopy;
    private static final String TAG = "DeviceSettingActivity";
//    private String selectProvince, selectCity, selectCounty;

    // 省数据集合
    private ArrayList<String> mListProvince = new ArrayList<>();
    // 市数据集合
    private ArrayList<String> mListCity = new ArrayList<>();
    // 区数据集合
    private ArrayList<String> mListArea = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        presenter = new DeviceSettingPresenter(this);
        SparseArray<String> contents = presenter.getCurrentStatuses();

        initView();
        initStatuses(contents);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.releaseCityJson();
    }

    private void initStatuses(SparseArray<String> contents) {
        serialNumberTv.setText(contents.get(0));
        serverIpTv.setText(contents.get(1));
        serverPortTv.setText(contents.get(2));
        softStorageTv.setText(/*contents.get(3)*/"计算中...");
        listStorageTv.setText(/*contents.get(4)*/"计算中...");
        ArrayAdapter<String> adapterPro;
        if (contents.get(5).contains("请选择省")) {//contents.get(5) 获取省份名
            Log.e(TAG, "初始化状态，省份未选");
            mListProvince.add("请选择省");
            mListProvince.addAll(presenter.getProvincesData());
            mListCity.add("请选择市");
            mListArea.add("请选择县");

            adapterPro = new ArrayAdapter<>(this, R.layout.content_spinner_tiem, mListProvince);
            adapterPro.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            provincesSpinner.setAdapter(adapterPro);

            adapterCity = new ArrayAdapter<>(this, R.layout.content_spinner_tiem, mListCity);
            adapterCity.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            citiesSpinner.setAdapter(adapterCity);

            adapterCon = new ArrayAdapter<>(this, R.layout.content_spinner_tiem, mListArea);
            adapterCon.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            countiesSpinner.setAdapter(adapterCon);
        } else {
            Log.e(TAG, "初始化状态，省份已选");
            mListProvince.add("请选择省");
            mListProvince.addAll(presenter.getProvincesData());//添加 省份 列表
            mListCity.addAll(presenter.getCitiesData(contents.get(5)));//根据 省名 获取城市列表
            provinceNameCopy = contents.get(5);
            mListArea.addAll(presenter.getCountriesData(contents.get(5), contents.get(6)));//根据 城市名 获取县区列表
            adapterPro = new ArrayAdapter<>(this, R.layout.content_spinner_tiem, mListProvince);
            adapterPro.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            provincesSpinner.setAdapter(adapterPro);

            adapterCity = new ArrayAdapter<>(this, R.layout.content_spinner_tiem, mListCity);
            adapterCity.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            citiesSpinner.setAdapter(adapterCity);

            adapterCon = new ArrayAdapter<>(this, R.layout.content_spinner_tiem, mListArea);
            adapterCon.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            countiesSpinner.setAdapter(adapterCon);

            //Spinner 指定到返回的数据来显示
            setSpinnerItemSelectedByValue(provincesSpinner, contents.get(5));
            setSpinnerItemSelectedByValue(citiesSpinner, contents.get(6));
            setSpinnerItemSelectedByValue(countiesSpinner, contents.get(7));
        }
        presenter.getOccupation();
        //readTypeCheckBox.setChecked(contents.get(8).equalsIgnoreCase("1"));

    }

    private void initView() {
        findViewById(R.id.rl_back).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_setting_title)).setText("设备设置");
        findViewById(R.id.rl_soft_storage).setOnClickListener(this);
        findViewById(R.id.rl_name_list_storage).setOnClickListener(this);
        serialNumberTv = (TextView) findViewById(R.id.tv_serial_number);
        serverIpTv = (TextView) findViewById(R.id.tv_server_ip);
        serverPortTv = (TextView) findViewById(R.id.tv_server_port);
        serialNumberTv.setOnClickListener(this);
        serverIpTv.setOnClickListener(this);
        serverPortTv.setOnClickListener(this);
        softStorageTv = (TextView) findViewById(R.id.tv_soft_storage);
        listStorageTv = (TextView) findViewById(R.id.tv_name_list_storage);

        provincesSpinner = (Spinner) findViewById(R.id.spinner_provinces);

        provincesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (isFirstTime) return;
                Log.e(TAG, "provincesSpinner   onItemSelected: pos=" + pos);
                presenter.setStatusChange(5, mListProvince.get(pos));
                hideSystemUi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        citiesSpinner = (Spinner) findViewById(R.id.spinner_cities);
        citiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (isFirstTime) return;
                Log.e(TAG, "citiesSpinner   onItemSelected: pos=" + pos);
                presenter.setStatusChange(6, mListCity.get(pos));
                hideSystemUi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        countiesSpinner = (Spinner) findViewById(R.id.spinner_counties);
        countiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                if (isFirstTime) {
                    isFirstTime = false;
                    return;
                }
                presenter.setStatusChange(7, mListArea.get(pos));
                Log.e(TAG, "countiesSpinner   onItemSelected: pos=" + pos);
                hideSystemUi();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
       /* readTypeCheckBox = (CheckBox) findViewById(R.id.cb_read_type);
        readTypeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.setStatusChange(8, isChecked ? "1" : "0");
            }
        });*/
    }

    public static void setSpinnerItemSelectedByValue(Spinner spinner, String value) {
        SpinnerAdapter adapter = spinner.getAdapter(); //得到Spinner Adapter对象
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            if (adapter.getItem(i).toString().contains(value)) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.tv_serial_number:
                changeTextDialog(0);
                break;
            case R.id.tv_server_ip:
                changeTextDialog(1);
                break;
            case R.id.tv_server_port:
                changeTextDialog(2);
                break;
            case R.id.rl_soft_storage:
                if (!softStorageTv.getText().toString().contains("B")) {
                    return;
                }
                new AlertDialog.Builder(DeviceSettingActivity.this).setTitle("清除记录")
                        .setMessage("警告：该操作将会清除掉历史记录，请谨慎操作！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.clearSoft();
                                softStorageTv.setText("清除中...");
                                hideSystemUi();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                hideSystemUi();
                            }
                        }).show();
                break;
            case R.id.rl_name_list_storage:
                if (!listStorageTv.getText().toString().contains("B")) {
                    return;
                }
                new AlertDialog.Builder(DeviceSettingActivity.this).setTitle("清除名单")
                        .setMessage("警告：该操作将会清除掉名单库，请谨慎操作！")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                presenter.clearList();
                                listStorageTv.setText("清除中...");
                                hideSystemUi();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                hideSystemUi();
                            }
                        }).show();
                break;
        }
    }


    private void changeTextDialog(final int flag) {
        String title = "";
        switch (flag) {
            case 0:
                title = "序列号";
                break;
            case 1:
                title = "服务器IP地址";
                break;
            case 2:
                title = "服务器端口";
                break;
        }
        final EditText editText = new EditText(DeviceSettingActivity.this);
        editText.setSingleLine(true);
        if (flag == 0) {
            InputFilter[] filters = {new InputFilter.LengthFilter(20)};
            editText.setFilters(filters);
            editText.setHint("最长 20 字");
//            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (flag == 1) {
            InputFilter[] filters = {new InputFilter.LengthFilter(15)};
            editText.setFilters(filters);
            editText.setHint("格式示例：192.168.1.200");
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
        } else {
            InputFilter[] filters = {new InputFilter.LengthFilter(4)};
            editText.setFilters(filters);
            editText.setHint("最长 4 个数字");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        new AlertDialog.Builder(DeviceSettingActivity.this).setTitle("设置" + title)
                .setMessage("请注意输入的格式或字数限制")
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeText = editText.getText().toString();
                        if (changeText.equals("")) {
                            Toast.makeText(DeviceSettingActivity.this, "输入不能空，本次输入不保存", Toast.LENGTH_SHORT).show();
                        } else {
                            if (flag == 0) {
                                Toast.makeText(DeviceSettingActivity.this, "正在后台确认序列号...", Toast.LENGTH_SHORT).show();
                                presenter.checkSerialNum(changeText);
                                return;
                            }
                            if (flag == 1) {
                                boolean isMatches = changeText.matches("((?:(?:25[0-5]|2[0-4]\\d|(?:1\\d{2}|[1-9]?\\d))\\.){3}(?:25[0-5]|2[0-4]\\d|(?:1\\d{2}|[1-9]?\\d)))");
                                if (!isMatches) {
                                    Toast.makeText(DeviceSettingActivity.this, "格式错误，本次输入不保存", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } else if (flag == 2) {
                                Log.e(TAG, "onClick: 端口长度=" + changeText.length());
                                boolean isMatches = changeText.matches("^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)");
                                if (changeText.length() != 4 || !isMatches) {
                                    Toast.makeText(DeviceSettingActivity.this, "格式错误，本次输入不保存", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            presenter.setStatusChange(flag, changeText);
                            hideSystemUi();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideSystemUi();
                    }
                }).show();
    }

    @Override
    public void showChangeResult(int flag, String value) {
        Log.e(TAG, "showChangeResult: flag=" + flag + ",value=" + value);

        if (value == null) {
            Toast.makeText(DeviceSettingActivity.this, "设置失败", Toast.LENGTH_SHORT).show();
            return;
        } else {
//            if (flag > 6 || flag < 5 ) { //地址选择，每次会保存省市县三个数据，但只需要提示一次，flag==7才提示
            Toast.makeText(DeviceSettingActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
//            }
        }
        switch (flag) {
            case 0:
                serialNumberTv.setText(value);
                break;
            case 1:
                serverIpTv.setText(value);
                break;
            case 2:
                serverPortTv.setText(value);
                break;
            case 3:
                softStorageTv.setText(value);
                break;
            case 4:
                listStorageTv.setText(value);
                break;
            case 5:
                provinceNameCopy = value;
                mListCity.clear();
                mListCity.addAll(presenter.getCitiesData(value));
                adapterCity.notifyDataSetChanged();
                setSpinnerItemSelectedByValue(citiesSpinner, mListCity.get(0));
                break;
            case 6:
                mListArea.clear();
                mListArea.addAll(presenter.getCountriesData(provinceNameCopy, value));
                adapterCon.notifyDataSetChanged();
                setSpinnerItemSelectedByValue(countiesSpinner, mListArea.get(0));
                break;
            case 7:
//                setSpinnerItemSelectedByValue(countiesSpinner, value);
                break;
            case 8:
                readTypeCheckBox.setChecked(value.equalsIgnoreCase("1"));
                break;
        }
    }

    @Override
    public void showCheckSerialNumResult(boolean result, String value) {
        Log.e(TAG, "showCheckSerialNumResult: result=" + result + ",value=" + value);
        if (result) {
            presenter.setStatusChange(0, value);
        } else {
            Toast.makeText(DeviceSettingActivity.this, value + ",本次修改不保存", Toast.LENGTH_SHORT).show();
            serialNumberTv.setText(presenter.getCurrentStatuses().get(0));
        }
    }

    @Override
    public void clearComplete(int flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (flag) {
                    case 3:
                        softStorageTv.setText("0B");
                        break;
                    case 4:
                        listStorageTv.setText("0B");
                        break;
                }
                hideSystemUi();
            }
        });

    }

    @Override
    public void showOccupation(int flag, String occupation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (flag) {
                    case 3:
                        softStorageTv.setText(occupation);
                        break;
                    case 4:
                        listStorageTv.setText(occupation);
                        break;
                }
                hideSystemUi();
            }
        });
    }
}
