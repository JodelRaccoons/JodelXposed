package com.jodelXposed.activities;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jodelXposed.R;
import com.jodelXposed.models.Beta;
import com.jodelXposed.models.Hookvalues;
import com.jodelXposed.models.Location;
import com.jodelXposed.models.Theme;
import com.jodelXposed.models.UDI;
import com.jodelXposed.retrofit.Classes;
import com.jodelXposed.retrofit.HooksResponse;
import com.jodelXposed.retrofit.Methods;
import com.jodelXposed.retrofit.RetrofitProvider;
import com.jodelXposed.utils.Options;
import com.jodelXposed.utils.Picker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("deprecation")
public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private SwitchPreference locationSwitch;
    private Location location;
    private Options options;
    private UDI udi;
    private Beta beta;
    private SwitchPreference themeSwitch;
    private Theme theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();

        location = Options.getInstance().getLocationObject();
        options = Options.getInstance();
        udi = Options.getInstance().getUDIObject();
        beta = Options.getInstance().getBetaObject();
        theme = Options.getInstance().getThemeObject();

        addPreferencesFromResource(R.xml.pref_general);


        locationSwitch = (SwitchPreference) findPreference("switch_location");
        themeSwitch = (SwitchPreference) findPreference("switch_theme");

        findPreference("button_choose_location").setOnPreferenceClickListener(this);
        locationSwitch.setOnPreferenceChangeListener(this);
        themeSwitch.setOnPreferenceChangeListener(this);

        findPreference("button_update").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                RetrofitProvider.getJodelXposedService().getHooks(Options.getInstance().getHooks().versionCode).enqueue(new Callback<HooksResponse>() {
                    @Override
                    public void onResponse(Call<HooksResponse> call, Response<HooksResponse> response) {
                        Hookvalues hooks = Options.getInstance().getHooks();
                        HooksResponse rhooks = response.body();
                        Methods methods = rhooks.getMethods();
                        Classes classes = rhooks.getClasses();
                        hooks.BetaHook_UnlockFeatures = methods.getBetaHookUnlockFeatures();
                        hooks.ImageHookValues_ImageView = methods.getImageHookValuesImageView();
                        hooks.PostStuff_ColorField = methods.getPostStuffColorField();
                        hooks.PostStuff_TrackPostsMethod = methods.getPostStuffTrackPostsMethod();
                        hooks.Settings_AddEntriesMethod = methods.getSettingsAddEntriesMethod();
                        hooks.Settings_HandleClickEventsMethod = methods.getSettingsHandleClickEventsMethod();
                        hooks.Theme_GCMReceiverMethod = methods.getThemeGCMReceiverMethod();
                        hooks.UDI_GetUdiMethod = methods.getUDIGetUdiMethod();

                        hooks.Class_CreateTextPostFragment = classes.getClassCreateTextPostFragment();
                        hooks.Class_MyGcmListenerService = classes.getClassMyGcmListenerService();
                        hooks.Class_MyMenuPresenter = classes.getClassMyMenuPresenter();
                        hooks.Class_PhotoEditFragment = classes.getClassPhotoEditFragment();
                        hooks.Class_PostDetailRecyclerAdapter = classes.getClassPostDetailRecyclerAdapter();
                        hooks.Class_Storage = classes.getClassStorage();
                        hooks.Class_UniqueDeviceIdentifier = classes.getClassUniqueDeviceIdentifier();
                        Options.getInstance().save();
                        Toast.makeText(SettingsActivity.this, rhooks.getUpdatemessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<HooksResponse> call, Throwable t) {
                        Toast.makeText(SettingsActivity.this, "Failed updating hooks, "+t.getLocalizedMessage()+" !", Toast.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
                return true;
            }
        });

        updateFieldsFromSettings();
    }

    private void setupToolbar() {
        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_settings, root, false);
        root.addView(toolbar, 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateFieldsFromSettings();
    }

    private void updateFieldsFromSettings() {
        locationSwitch.setChecked(location.isActive());
        locationSwitch.setSummary("Current location: " + location.getCity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(0, 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        switch (preference.getKey()) {
            case "switch_location":
                if (location.getLat() == 0 && location.getLng() == 0) {
                    locationSwitch.setChecked(false);
                    Toast.makeText(SettingsActivity.this, "Please choose a location first", Toast.LENGTH_SHORT).show();
                } else {
                    location.setActive((boolean) o);
                }
                break;
            case "switch_udi":
                udi.setActive((boolean) o);
                break;
            case "switch_beta":
                beta.setActive((boolean) o);
                break;
            case "switch_theme":
                theme.setActive((boolean) o);
                break;
        }
        options.save();
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "button_choose_location":
                startActivity(new Intent(getApplicationContext(), Picker.class).putExtra("choice", 1));
                return true;
            case "button_edit_udi":
//                View v = getLayoutInflater().inflate(R.layout.dialog_udi, null);
//                Spinner udiSpinner = (Spinner) v.findViewById(R.id.udiSpinner);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, (String[]) udi.getUdis().keySet().toArray());
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                udiSpinner.setAdapter(adapter);
//
//                new AlertDialog.Builder(this).setTitle("Choose your UDI").setView(v).show();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, 0);
    }
}
