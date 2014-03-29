package info.justaway;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class CreateUserListActivity extends Activity {

    private EditText mListName;
    private EditText mDescription;
    private boolean mPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JustawayApplication.getApplication().setTheme(this);
        setContentView(R.layout.activity_create_user_list);

        final Activity activity = this;
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mListName = ((EditText) findViewById(R.id.list_name));
        mDescription = ((EditText) findViewById(R.id.list_description));
        final RadioGroup rg = ((RadioGroup) findViewById(R.id.privacy_radio_group));

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JustawayApplication.showProgressDialog(activity, getString(R.string.progress_process));
                if (rg.getCheckedRadioButtonId() == R.id.public_radio) {
                    mPrivacy = true;
                }
                new CreateUserListTask().execute();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private class CreateUserListTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // noinspection ConstantConditions
                JustawayApplication.getApplication().getTwitter().createUserList(
                        mListName.getText().toString(),
                        mPrivacy,
                        mDescription.getText().toString());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            JustawayApplication.dismissProgressDialog();
            if (success) {
                JustawayApplication.showToast(R.string.toast_reate_user_list_success);
                finish();
            } else {
                JustawayApplication.showToast(R.string.toast_create_user_list_failure);
            }
        }
    }
}