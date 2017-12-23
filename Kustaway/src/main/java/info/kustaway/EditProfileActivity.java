package info.kustaway;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.InputStream;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import info.kustaway.fragment.profile.UpdateProfileImageFragment;
import info.kustaway.model.TwitterManager;
import info.kustaway.task.VerifyCredentialsLoader;
import info.kustaway.util.FileUtil;
import info.kustaway.util.ImageUtil;
import info.kustaway.util.MessageUtil;
import info.kustaway.util.ThemeUtil;
import twitter4j.User;

public class EditProfileActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<User> {

    private static final int REQ_PICK_PROFILE_IMAGE = 1;

    @Bind(R.id.icon) ImageView mIcon;
    @Bind(R.id.name) EditText mName;
    @Bind(R.id.location) EditText mLocation;
    @Bind(R.id.url) EditText mUrl;
    @Bind(R.id.description) EditText mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.setTheme(this);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /**
         * onCreateLoader => onLoadFinished と繋がる
         */
        getSupportLoaderManager().initLoader(0, null, this);

    }

    @OnClick(R.id.icon)
    void updateProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_PICK_PROFILE_IMAGE);
    }

    @OnClick(R.id.save_button)
    void saveProfile() {
        MessageUtil.showProgressDialog(this, getString(R.string.progress_process));
        new UpdateProfileTask().execute();
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

    @Override
    public Loader<User> onCreateLoader(int id, Bundle args) {
        return new VerifyCredentialsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<User> loader, User user) {
        if (user == null) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        } else {
            mName.setText(user.getName());
            mLocation.setText(user.getLocation());
            mUrl.setText(user.getURLEntity().getExpandedURL());
            mDescription.setText(user.getDescription());
            ImageUtil.displayRoundedImage(user.getOriginalProfileImageURL(), mIcon);
        }
    }

    @Override
    public void onLoaderReset(Loader<User> arg0) {

    }

    private class UpdateProfileTask extends AsyncTask<Void, Void, User> {
        @Override
        protected User doInBackground(Void... params) {
            try {
                //noinspection ConstantConditions
                return TwitterManager.getTwitter().updateProfile(
                        mName.getText().toString(),
                        mUrl.getText().toString(),
                        mLocation.getText().toString(),
                        mDescription.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            MessageUtil.dismissProgressDialog();
            if (user != null) {
                MessageUtil.showToast(R.string.toast_update_profile_success);
                finish();
            } else {
                MessageUtil.showToast(R.string.toast_update_profile_failure);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_PICK_PROFILE_IMAGE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        if (uri == null) {
                            return;
                        }
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        File file = FileUtil.writeToTempFile(getCacheDir(), inputStream);
                        if (file != null) {
                            UpdateProfileImageFragment dialog = UpdateProfileImageFragment.newInstance(file, uri);
                            dialog.show(getSupportFragmentManager(), "dialog");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}