package com.miral.nfcdetector.features.nfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.cardemulation.CardEmulation;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.miral.nfcdetector.features.nfc.adapter.DataAdapter;
import com.miral.nfcdetector.constants.AppConstants;
import com.miral.nfcdetector.constants.Util;
import com.miral.nfcdetector.manager.DatabaseManager;
import com.miral.nfcdetector.features.nfc.model.NfcData;
import com.miral.nfcdetector.R;
import com.miral.nfcdetector.databinding.ActivityMainBinding;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends NfcActivity implements View.OnClickListener {

    private final String TAG = AppConstants.APP_TAG;

    private Tag mTag;

    private ArrayList<NfcData> allData = new ArrayList<>();
    private DataAdapter mAdapter;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initView();
        initListener();

        Intent intent = new Intent();
        intent.setAction(CardEmulation.ACTION_CHANGE_DEFAULT);
        intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT,
                new ComponentName(this, com.miral.nfcdetector.features.nfc.MainActivity.class));
        intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);

        startActivity(intent);
    }

    private void initView() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.white));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.commandResponseList.setLayoutManager(linearLayoutManager);
        binding.commandResponseList.setHasFixedSize(true);

        allData = DatabaseManager.getInstance().getNfcData();

        if (allData.size() > 0 && allData != null) {
            binding.commandResponseList.setVisibility(View.VISIBLE);
            mAdapter = new DataAdapter(this, allData);
            binding.commandResponseList.setAdapter(mAdapter);
        } else {
            binding.commandResponseList.setVisibility(View.GONE);
            toast(getString(R.string.no_data_in_database));
        }
    }

    private void initListener() {
        binding.btnSubmit.setOnClickListener(this);
        binding.btnClear.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
    }

    private void startRunTheCommand () {
        if (mTag != null) {
            if (!binding.etCommand.getText().toString().isEmpty()) {
                discoverCommandResponse(mTag);
            } else {
                toast(getString(R.string.input_command));
            }
        } else {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (!nfcAdapter.isEnabled()) {
                checkSystemHasNFC();
            } else {
                alertShow(getString(R.string.tap_the_card ));
            }
        }
    }

    private void deleteFirstData () {
        DatabaseManager.getInstance().deleteNfcData();
        allData = DatabaseManager.getInstance().getNfcData();
        mAdapter = new DataAdapter(getApplicationContext(), allData);
        binding.commandResponseList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void clearAllData () {
        DatabaseManager.getInstance().clearNfcData();
        allData = DatabaseManager.getInstance().getNfcData();
        mAdapter = new DataAdapter(getApplicationContext(), allData);
        binding.commandResponseList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void alertShow(String msg){
        AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
        popupBuilder.setPositiveButton(getString(R.string.ok), null);
        popupBuilder.setMessage(msg);
        popupBuilder.show();
    }

    @Override
    protected void onNfcFeatureNotFound() {
        alertShow(getString(R.string.nfc_not_supported));
    }

    @Override
    protected void onNfcStateEnabled() {
        toast(getString(R.string.nfc_ready));
    }

    @Override
    protected void onNfcStateDisabled() {
        Intent setNfc = new Intent(Settings.ACTION_NFC_SETTINGS);
        startActivity(setNfc);
    }

    @Override
    protected void onTagDiscovered(Intent intent) {
        mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        discoverTag(mTag);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                hideKeyboard(view);
                startRunTheCommand();
                break;
            case R.id.btn_clear:
                clearAllData();
                break;
            case R.id.btn_delete:
                deleteFirstData();
                break;
        }
    }

    public static void hideKeyboard(final View view) {
        if (view != null && view.getWindowToken() != null) {
            ((InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        }
    }

    public static int getRandomNumber() {
        return (new Random()).nextInt((10000 - 1) + 1) + 1;
    }

    public void discoverCommandResponse(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep == null) {
            toast(getString(R.string.card_read_failed));
            return;
        }
        try {
            isoDep.connect();
            byte[] uid = isoDep.getTag().getId();
            binding.txtUid.setText(Util.byteArrayToHexString(uid));

            if (!binding.etCommand.getText().toString().isEmpty()) {
                StringBuilder responses = new StringBuilder();
                final String AID = binding.etCommand.getText().toString().trim();
                byte[] command = Util.hexStringToByteArray(AID);
                if (command != null) {
                    byte[] result = isoDep.transceive(command);
                    responses.append(Util.byteArrayToHexString(result));

                    String uidString = Util.byteArrayToHexString(uid) + AppConstants.SINGLE_SPACE_WITH_SEPARATOR + Util.bytesToString(uid);
                    String commandString = binding.etCommand.getText().toString();
                    String responseString = responses.toString();

                    Log.e("size byte", String.valueOf(responseString.getBytes(StandardCharsets.UTF_8).length));
                    Log.e("size byte 2", String.valueOf((Util.byteArrayToHexStringNotFormatted(result).getBytes(StandardCharsets.UTF_8).length - 4) / 2));

                    NfcData nfcData = new NfcData(getRandomNumber(), uidString, commandString, responseString);
                    DatabaseManager.getInstance().insertOrUpdateNfcData(nfcData);

                    allData = DatabaseManager.getInstance().getNfcData();
                    binding.commandResponseList.setVisibility(View.VISIBLE);
                    mAdapter = new DataAdapter(getApplicationContext(), allData);
                    binding.commandResponseList.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                } else {
                    alertShow(getString(R.string.command_must_have_even_number));
                }
            }
            isoDep.close();

        } catch (IOException e) {
            Log.e(TAG, getString(R.string.error) +  e.getMessage());
            alertShow(getString(R.string.tap_the_card));
            e.printStackTrace();
        }
    }

    public void discoverTag(Tag tag) {
        IsoDep isoDep = IsoDep.get(tag);
        if (isoDep == null) {
            toast(getString(R.string.card_read_failed));
            return;
        }
        try {
            isoDep.connect();
            byte[] uid = isoDep.getTag().getId();
            binding.txtUid.setText(Util.byteArrayToHexString(uid));

            String uidString = Util.byteArrayToHexString(uid) + AppConstants.SINGLE_SPACE_WITH_SEPARATOR + Util.bytesToString(uid);
            String commandString = AppConstants.EMPTY_STRING;
            String responseString = AppConstants.EMPTY_STRING;

            NfcData nfcData = new NfcData(getRandomNumber(), uidString, commandString, responseString);
            DatabaseManager.getInstance().insertOrUpdateNfcData(nfcData);

            allData = DatabaseManager.getInstance().getNfcData();
            binding.commandResponseList.setVisibility(View.VISIBLE);
            mAdapter = new DataAdapter(getApplicationContext(), allData);
            binding.commandResponseList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            isoDep.close();

        } catch (IOException e) {
            Log.e(TAG, getString(R.string.error) +  e.getMessage());
            e.printStackTrace();
        }
    }

    private void toast(String info) {
        Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
    }
}