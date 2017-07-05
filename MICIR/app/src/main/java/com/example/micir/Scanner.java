package com.example.micir;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import com.example.micir.myParcelObject.*;
/**
 * Created by 正文 on 2016/10/6.
 */

public class Scanner extends BaseScannerActivity implements ZBarScannerView.ResultHandler {
    private ArrayAdapter<String> foosAdapter;
    private ZBarScannerView mScannerView;
    @Override
    public void onCreate(Bundle state){
        super.onCreate(state);
        setContentView(R.layout.scanner);
        setupToolbar();


        ViewGroup conteneFrame=(ViewGroup) findViewById(R.id.content_frame);
        mScannerView=new ZBarScannerView(this);
        conteneFrame.addView(mScannerView);
    }
    public void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }
    public void onPause(){
        super.onPause();
        mScannerView.stopCamera();
    }
    public void handleResult(Result result) {
        //Toast.makeText(this,"Content= "+result.getContents()+"    Fromat= " + result.getBarcodeFormat().getName(),Toast.LENGTH_SHORT).show();

        String message=result.getContents();
        Intent intent=new Intent(this,com.example.micir.Newfood.class);
        intent.putExtra("BarCode",new BarCodeParcel(1,message));
        int mode=getIntent().getIntExtra("mode",0);
        if (mode==1){
            setResult(2,intent);
            finish();
        }else if(mode==2){
            setResult(4,intent);
            finish();
        }else{
            Intent ointent=getIntent();
            intent.putExtra("foodclass",ointent.getIntExtra("foodclass",0));
            startActivityForResult(intent,1);
        }


        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(Scanner.this);
            }

        }, 2000);
    }
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==2){
            setResult(2,data);
            finish();
        }
    }
}
