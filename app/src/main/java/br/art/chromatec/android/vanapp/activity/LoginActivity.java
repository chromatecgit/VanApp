package br.art.chromatec.android.vanapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.art.chromatec.android.vanapp.R;

/**
 * Created by Chromatec on 15/12/2016.
 */
public class LoginActivity extends AppCompatActivity {
    private Button buttonLogin;
    private EditText fieldPassword;
    private final int CODE_PERMISSION_REQUEST_READ_PHONE_STATE = 11;
    private static final String TAG_DEVICE_ID = "DEVICE_ID:";
    private static final String TAG_DEVICE_NUMBER = "DEVICE_NUMBER:";
    private static final String TAG_SIM_NUMBER = "SIM_NUMBER:";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fieldPassword = (EditText) this.findViewById(R.id.field_password);

        buttonLogin = (Button) this.findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = getPhoneDataForLogin(checkPermissionPhone(LoginActivity.this));
                if (!"".equals(data)) {
                    // TODO: Serviço de autenticacao envia os dados
                    // TODO: Aqui deve constar uma verificacao, caso o retorno do servico seja negativo. Neste caso deve se chamar uma tela de erro.
                    // TODO: TELA DE ERRO
                    //Service service = service.enviaDados(data + fieldPassword)
                    boolean permission = true;
                    startActivity(MainActivity.newIntent(LoginActivity.this, permission));
                } else {
                    //TODO: Exibir mensagem de que "sem a permissao de dados do telefone não será possivel receber notificacoes"
                }
            }
        });
    }

    private boolean checkPermissionPhone(Activity activity) {
        int permissionCode = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_PHONE_STATE);
        if (permissionCode == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private String getPhoneDataForLogin(boolean isAllowed) {
        if (isAllowed) {
            final TelephonyManager telephonyManager = (TelephonyManager) getBaseContext()
                    .getSystemService(Context.TELEPHONY_SERVICE);

            if (TelephonyManager.PHONE_TYPE_GSM == telephonyManager.getPhoneType()) {
                //FIXME: Pegar apenas uma parte desses numeros ou embaralha-los antes de enviar pro servidor
                final String simNumber = telephonyManager.getSimSerialNumber();
                final String phoneNumber = telephonyManager.getLine1Number();
                //FIXME: Adicao desse sufixo sera feita no backend
                //FIXME: Esse codigo sera usado para enviar notificacoes para os usuarios
                final String deviceID = telephonyManager.getDeviceId().toString() + " android";

                Log.i(TAG_SIM_NUMBER, simNumber);
                Log.i(TAG_DEVICE_NUMBER, phoneNumber);
                Log.i(TAG_DEVICE_ID, deviceID);
                return simNumber.concat(phoneNumber).concat(deviceID);
            }
            //TODO: Colocar aqui a condicao caso o aparelho nao seja GSM
            return "Seu smartphone não é GSM";
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    CODE_PERMISSION_REQUEST_READ_PHONE_STATE);
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

    }
}