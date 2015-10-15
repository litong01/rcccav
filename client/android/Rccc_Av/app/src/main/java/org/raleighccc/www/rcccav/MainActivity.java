package org.raleighccc.www.rcccav;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Switch onoffSwitch;
    TextView prefEditText;
    TextView prefNetText;
    TextView prefMacText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* fields in the Settings menu page*/
        prefEditText = (TextView) findViewById(R.id.prefExitText);
        prefNetText = (TextView) findViewById(R.id.prefNetText);
        prefMacText = (TextView) findViewById(R.id.prefMacText);
        // let's keep them invisible
        prefEditText.setVisibility(View.INVISIBLE);
        prefNetText.setVisibility(View.INVISIBLE);
        prefMacText.setVisibility(View.INVISIBLE);

        /* The Wake On Lan stuff */
        final WakeOnLan wakeOnLan = new WakeOnLan();
        final RESTController restController = new RESTController();

        onoffSwitch = (Switch)findViewById(R.id.switch1);
        final TextView textView=(TextView)findViewById(R.id.textView);
        textView.setVisibility(View.INVISIBLE);

        final SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        onoffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                   @Override
                                                   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                       if (isChecked) {
                                                           textView.setText("System on");
                                                           textView.setVisibility(View.VISIBLE);

                                                           /*
                                                           if (! wakeOnLan.getReadyState()) {
                                                               // will need to create the WakeOnLan
                                                               System.out.println("-- we are to set the wakeOnLan properties");
                                                               String netStr = mySharedPreferences.getString("network_preference", "");
                                                               wakeOnLan.setNetStr(netStr);
                                                               String macStr = mySharedPreferences.getString("mac_preference", "");
                                                               wakeOnLan.setMacStr(macStr);
                                                               // set the proper state
                                                               wakeOnLan.setReadyState(true);
                                                               System.out.println("-- set wakeOnLan property, and its ready status");

                                                               // and we are to make the wake up call
                                                               wakeOnLan.wakeOnLan();
                                                           }
                                                           */

                                                           // now we need to wait for some time or to check for the controller host to be up

                                                           String hostStr = mySharedPreferences.getString("hostIP_preference", "");
                                                           restController.setHostIP(hostStr);

                                                           System.out.println("-- try to call the systemUp()");
                                                           restController.avsystemUp();


                                                       } else {
                                                           textView.setText("System off");

                                                           restController.avsystemOff();
                                                           // again, still make the text view invisible
                                                           textView.setVisibility(View.INVISIBLE);

                                                       }
                                                   }
                                               }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SetPreferenceActivity.class);
        startActivityForResult(intent, 0);

        return true;

        //return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /* to make it simple */
        loadPref();
    }

    private void loadPref() {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String my_hostip_preference = mySharedPreferences.getString("hostIP_preference", "");
        prefEditText.setText(my_hostip_preference);

        String my_net_preference = mySharedPreferences.getString("network_preference", "");
        prefNetText.setText(my_net_preference);

        String my_mac_preference = mySharedPreferences.getString("mac_preference", "");
        prefMacText.setText(my_mac_preference);
    }
}
