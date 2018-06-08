package co.edu.unadvirtual.computacion.movil.iam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import co.edu.unadvirtual.computacion.movil.AppSingleton;
import co.edu.unadvirtual.computacion.movil.MainActivity;
import co.edu.unadvirtual.computacion.movil.R;

/**
 * Captura los datos básicos del usuario y los envia al servidor.
 */
public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);

        Button buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(v -> registerUser(
                editTextEmail.getText().toString(),
                editTextPassword.getText().toString(),
                editTextFirstName.getText().toString(),
                editTextLastName.getText().toString()
        ));

    }

    /**
     * Método para hacer el registro del usuario
     *
     * @param email     Correo electrónico del usuario.
     * @param password  Contraseña
     * @param firstName Nombres
     * @param lastName  Apellidos
     */
    private void registerUser(String email, String password, String firstName, String lastName) {
        // Los datos del usuario para ser enviadas al servidor en formato JSON
        JSONObject params = new JSONObject();

        try {
            params.put("email", email);
            params.put("password", password);
            params.put("firstName", firstName);
            params.put("lastName", lastName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                AppSingleton.UNADROID_SERVER_ENDPOINT + "/register",
                params,
                this::userRegistedSuccessfully,
                this::errorRegisteringUser
        );

        AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(request, TAG);
    }

    /**
     * Procesa la respuesta del servidor si el usuario se ha podido registrar exitosamente.
     *
     * @param response Contiene el usuario recién creado.
     */
    private void userRegistedSuccessfully(JSONObject response) {
        try {
            boolean error = !response.isNull("error");

            if (!error) {
                Intent intent = new Intent(
                        RegistrationActivity.this,
                        MainActivity.class);
                intent.putExtra("email", response.getString("email"));
                startActivity(intent);
                finish();

            } else {
                String errorMsg = response.getString("error_msg");
                Toast.makeText(
                        getApplicationContext(),
                        errorMsg, Toast.LENGTH_LONG
                ).show();
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * Procesa las situaciones de error durante la comunicación con el servidor, mostrando el mensaje
     * de error.
     *
     * @param volleyError El error que se ha generado.
     */
    private void errorRegisteringUser(VolleyError volleyError) {
        Toast.makeText(
                RegistrationActivity.this.getApplicationContext(),
                volleyError.getMessage(),
                Toast.LENGTH_LONG
        ).show();

    }
}
