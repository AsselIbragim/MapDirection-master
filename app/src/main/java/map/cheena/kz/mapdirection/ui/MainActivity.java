package map.cheena.kz.mapdirection.ui;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import map.cheena.kz.mapdirection.R;
import map.cheena.kz.mapdirection.ui.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openMainFragment();
    }

    private void openMainFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.main_frame, mainFragment, MainFragment.TAG);
        fragmentTransaction.addToBackStack(MainFragment.TAG);
        fragmentTransaction.commit();
    }
}
