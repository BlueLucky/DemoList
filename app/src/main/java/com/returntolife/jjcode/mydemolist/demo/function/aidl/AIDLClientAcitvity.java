package com.returntolife.jjcode.mydemolist.demo.function.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.returntolife.jjcode.mydemolist.IOnNewBookArrivedListener;
import com.returntolife.jjcode.mydemolist.IPerson;
import com.returntolife.jjcode.mydemolist.R;
import com.returntolife.jjcode.mydemolist.bean.AIDLBook;
import com.returntolife.jjcode.mydemolist.demo.function.aidl.binderpool.AIDLBinderPoolClientAcitvity;
import com.tools.jj.tools.utils.LogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Create by JiaJun He on 2019/5/31$
 * Email:1021661582@qq.com
 * des:
 * version:1.0.0
 */
public class AIDLClientAcitvity extends Activity {


    @BindView(R.id.btn_bindservice)
    Button btnBindservice;
    @BindView(R.id.btn_setname)
    Button btnSetname;
    @BindView(R.id.btn_getName)
    Button btnGetName;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.btn_setBook)
    Button btnSetBook;
    @BindView(R.id.btn_getBook)
    Button btnGetBook;
    @BindView(R.id.btn_registerlistener)
    Button btnRegisterlistener;
    @BindView(R.id.btn_unregisterlistener)
    Button btnUnregisterlistener;
    @BindView(R.id.btn_pool)
    Button btnPool;


    private IPerson iPerson;
    private ServiceConnection conn;
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //解绑
            if (iPerson != null) {
                iPerson.asBinder().unlinkToDeath(deathRecipient, 0);
                iPerson = null;
            }
//            //断开重新绑定
//            bindServiceByAidl();
        }
    };

    private IOnNewBookArrivedListener iOnNewBookArrivedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_client);
        ButterKnife.bind(this);

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LogUtil.d("onServiceConnected");
                try {
                    //设置死亡代理
                    service.linkToDeath(deathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                iPerson = IPerson.Stub.asInterface(service);

                Toast.makeText(AIDLClientAcitvity.this, "onServiceConnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                LogUtil.d("onServiceDisconnected");
//                //断开重新绑定
//                bindServiceByAidl();
            }
        };

        iOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
            @Override
            public void onNewsBookArrived(AIDLBook book) throws RemoteException {
                Toast.makeText(AIDLClientAcitvity.this, "book=" + book + "--name=" + book.name, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void binderIsAlive() {
        if (iPerson != null) {
            iPerson.asBinder().isBinderAlive();
        }
    }

    @OnClick({R.id.btn_bindservice, R.id.btn_setname, R.id.btn_getName, R.id.btn_setBook, R.id.btn_getBook, R.id.btn_registerlistener, R.id.btn_unregisterlistener,R.id.btn_pool})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bindservice:
                bindServiceByAidl();
                break;
            case R.id.btn_setname:
                setName();
                break;
            case R.id.btn_getName:
                getName();
                break;
            case R.id.btn_setBook:
                setBook();
                break;
            case R.id.btn_getBook:
                getBook();
                break;
            case R.id.btn_registerlistener:
                registerListener();
                break;
            case R.id.btn_unregisterlistener:
                unregisterListener();
                break;
            case R.id.btn_pool:
                gotoPoolService();
                break;
            default:
                break;
        }
    }

    private void gotoPoolService() {
        startActivity(new Intent(this, AIDLBinderPoolClientAcitvity.class));
    }

    private void registerListener() {
        if (iPerson != null) {
            try {
                iPerson.registerListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void unregisterListener() {
        if (iPerson != null) {
            try {
                iPerson.unregisterListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void unbindService() {
        Intent intent = new Intent(this, AIDLService.class);
        stopService(intent);
    }

    private void bindServiceByAidl() {
        Intent intent = new Intent(this, AIDLService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }


    private void setName() {
        if (iPerson != null) {
            try {
                //该方法可能很耗时，正式开发中一般放于线程中调用
                iPerson.setName(etName.getText().toString().trim());
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "setName error=" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getName() {
        if (iPerson != null) {
            try {
                Toast.makeText(this, iPerson.getName(), Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "getName error=" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setBook() {
        if (iPerson != null) {
            try {
                AIDLBook book = new AIDLBook();
                book.name = etName.getText().toString().trim();
                book.id = 999;
                //该方法可能很耗时，正式开发中一般放于线程中调用
                iPerson.setBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "setName error=" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getBook() {
        if (iPerson != null) {
            try {
                AIDLBook book = iPerson.getBook();
                LogUtil.d("getBook=" + book);
                Toast.makeText(this, book.name, Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "getName error=" + e, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
