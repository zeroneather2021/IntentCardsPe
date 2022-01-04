package com.intent.cardspe.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.intent.cardspe.R;

public class MosambeeIntentActivity extends AppCompatActivity {


    private static final String MOS_INTENT_ACTION = "com.mosambee.softpos.login";
    private static final String MOS_PACKAGE_NAME = "com.mosambee.softpos";
    private Button btnIntent, btnLink;
    private EditText etAmount;
    private RadioButton rbCard, rbQR;
    private String paymentMode;
    private Button btnMosambee;
    private String sessionID;

    private Button btnLogin, btnHealthCheck;
    String userId;
    String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_mosambee);

        btnIntent = findViewById(R.id.btnIntent);
        btnLink = findViewById(R.id.btnLink);
        etAmount = findViewById(R.id.etAmount);
        rbCard = findViewById(R.id.rbCard);
        rbQR = findViewById(R.id.rbQR);
        btnMosambee = findViewById(R.id.btnMosambee);
        btnLogin = findViewById(R.id.btnLogin);
        btnHealthCheck = findViewById(R.id.btnHealthCheck);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAppInstalled("com.mosambee.mpos.softpos")) {
                    setPassword("1234");
                    setUserId("9999261237");

                    Intent intent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putString("userName", getUserId());
                    mBundle.putString("password", getPassword());
                    // mBundle.putString("key", "1234");
                    intent.putExtras(mBundle);
                    intent.setAction(Constants.SOFTPOS_INIT_ACTION);
                    intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MOSAMBEE);
                    startActivityForResult(intent, Constants.ActivityLoginRequestCode);

                    //  mActivityResultLauncher1.launch(intent);


                } else {
                    Toast.makeText(MosambeeIntentActivity.this, "App Not insatlled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnHealthCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAppInstalled("com.mosambee.mpos.softpos")) {
                    Intent intent = new Intent();
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    intent.setAction(Constants.SOFTPOS_HEALTHCHECK_ACTION);
                    intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MOSAMBEE);
                    Bundle bundle = new Bundle();
                    bundle.putString("sessionId", sessionID);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, Constants.ActivityHealthCheckRequestCode);


                } else {
                    Toast.makeText(MosambeeIntentActivity.this, "App Not insatlled", Toast.LENGTH_SHORT).show();
                }



                //   mActivityResultLauncher2.launch(intent);


            }
        });
        btnMosambee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isAppInstalled("com.mosambee.mpos.softpos")) {
                    int am = 0;

                    try {
                        am = Integer.parseInt(etAmount.getText().toString());

                    } catch (NumberFormatException e) {
                        Toast.makeText(MosambeeIntentActivity.this, "Not a valid integer", Toast.LENGTH_LONG).show();
                        return;
                    }
                    setUserId("9999261237");
                    Intent intent = new Intent();
                    intent.setPackage(Constants.SOFTPOS_PACKAGE_NAME_MOSAMBEE);
                    Bundle mBundle = new Bundle();
                    mBundle.putString("amount", String.format("%d", am));
                    mBundle.putString("sessionId", sessionID);
                    mBundle.putString("mobNo", getUserId());
                    mBundle.putString("description", "Test payment");

                    intent.putExtras(mBundle);
                    intent.setAction(Constants.SOFTPOS_PAYMENT_ACTION);
                    startActivityForResult(intent, Constants.ActivityPaymentRequestCode);


                    //mActivityResultLauncher3.launch(intent);


                } else {
                    Toast.makeText(MosambeeIntentActivity.this, "App Not insatlled", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    private boolean isAppInstalled(String packageName) {


        try {

            this.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            Log.e("PACKAGE_FOUND", "--------TRUE----------");
            return true;

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PACKAGE_FOUND", "--------FALSE----------" + e.toString());
            e.printStackTrace();
            return false;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.ActivityLoginRequestCode && data != null) {

            String sessionId = data.getStringExtra("sessionId");
            sessionID=sessionId;

            String responseCode = data.getStringExtra("responseCode");
            String description = data.getStringExtra("description");


            new AlertDialog.Builder(this)
                    .setTitle("Login")
                    .setMessage("Session Id : " + sessionId + "\n" + "Response Code : " + responseCode + "\n" + "Description : " + description)
                    .setPositiveButton("OK", null)
                    .show();
        } else if (requestCode == Constants.ActivityHealthCheckRequestCode && data != null) {
            String responseCode = data.getStringExtra("responseCode");
            String description = data.getStringExtra("description");
            String detailedAudit = data.getStringExtra("detailedAudit");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je;

            try {
                je = jp.parse(detailedAudit);
                detailedAudit = gson.toJson(je);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }

            new AlertDialog.Builder(this)
                    .setTitle("Message")
                    .setMessage("Response Code : " + responseCode + "\n" + "Description : " + description + "\n" + "Detailed Audit : " + detailedAudit)
                    .setPositiveButton("OK", null)
                    .show();
        } else if (requestCode == Constants.ActivityPaymentRequestCode || requestCode == Constants.ActivityRefundRequestCode && data != null) {

            String response = data.getStringExtra("receiptResponse");
            String paymentDescription = data.getStringExtra("paymentDescription");
            String paymentResponseCode = data.getStringExtra("paymentResponseCode");
            String responseCode = data.getStringExtra("responseCode");

            String showResponse = response;

            new AlertDialog.Builder(this)
                    .setTitle("Receipt")
                    .setMessage("ResponseCode : " + responseCode + "\n" +
                            "PaymentResponseCode : " + paymentResponseCode + "\n" +
                            "ReceiptResponse : " + showResponse + "\n" + "PaymentDescription : " + paymentDescription)
                    .setPositiveButton("OK", null)
                    .show();
        }

    }
}
