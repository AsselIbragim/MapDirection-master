package map.cheena.kz.mapdirection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        LoginFragment login = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.loginContainer, login).commit();
    }
}
