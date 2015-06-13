package com.disycs.quizmo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.disycs.quizmo.api.HttpProxy;
import com.disycs.quizmo.database.QuizmooContentProvider;
import com.disycs.quizmo.database.tables.UserTable;
import com.disycs.quizmo.design.FontChangeCrawler;
import com.disycs.quizmo.model.MyCryptoHelper;
import com.disycs.quizmo.model.Token;
import com.disycs.quizmo.model.User;
import com.disycs.quizmo.services.QuizmooSyncAdapter;


public class MainActivity extends Activity implements OnClickListener {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();

	private static final int CHECK_USER = 1;
	private static final String PARAM_USER_PASS = "password";
	public static final String ARG_IS_ADDING_NEW_ACCOUNT ="is_adding_new_account" ;



	private ProgressBar loginProgressBar;
	private EditText txtLogin,txtPwd;
	private Button BtnLogin;
	private TextView labelFeedback;


	private AccountManager mAccountManager;
	
	@Override
	public void setContentView(int view)
	{
	    super.setContentView(view);
	    FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "font/montserratregular.ttf");
	    fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		mAccountManager = AccountManager.get(getApplicationContext());
		final String accountType= getString(R.string.sync_account_type);

		if(mAccountManager.getAccountsByType(accountType).length>0) {

			Account account = mAccountManager.getAccountsByType(accountType)[0];
			User.setUser(account.name,"");
			Token.setToken(mAccountManager.peekAuthToken(account,accountType),null,0,null);
			loginSuccess();
		}

	}


	
	@Override
	protected void onStart() {
		super.onStart();
		
		txtLogin = (EditText)findViewById(R.id.TxtUserName);
		
		txtPwd = (EditText)findViewById(R.id.TxtPassword);
		
		loginProgressBar = (ProgressBar) findViewById(R.id.LoadingBar);
		loginProgressBar.setVisibility(View.GONE);
		BtnLogin = (Button)findViewById(R.id.BtnLogin);
		
		labelFeedback = (TextView) findViewById(R.id.LabelFeedback);

		BtnLogin.setOnClickListener(this);
	}



	@Override
	public void onClick(View view) {

		// Login Button

		if (view.getId() == R.id.BtnLogin) {
			QuizmooSyncAdapter.initializeSyncAdapter(this);
			final String userName = txtLogin.getText().toString();
			final String password = txtPwd.getText().toString();
			if (isNetworkAvailable()) {
				 class LoginAsyncTask extends AsyncTask<Void,Void,Void>{
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						loginProgressBar.setVisibility(View.VISIBLE);

					}
					@Override
					protected Void doInBackground(Void... voids) {
						HttpProxy.Authentification(userName,password);
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						loginProgressBar.setVisibility(View.INVISIBLE);
						if(User.getUser()==null){
							labelFeedback.setText(getString(R.string.login_failure));
						}
						else{
							final Intent res = new Intent();
							res.putExtra(AccountManager.KEY_ACCOUNT_NAME, userName);
							res.putExtra(AccountManager.KEY_AUTHTOKEN, Token.getToken().toString());
							res.putExtra(PARAM_USER_PASS, password);
							finishLogin(res);


						}
					}
				}
				new LoginAsyncTask().execute();

			}else{
				Toast.makeText(getApplicationContext(),getString(R.string.offlinefailure),Toast.LENGTH_LONG).show();
			}


		}
	}

	private void loginSuccess() {
		Intent intent = new Intent(getApplicationContext(),QuestionnairesActivity.class);
		startActivity(intent);
		finish();
	}

	private void finishLogin(Intent intent) {

		String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
		String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
		final Account account = new Account(accountName, getString(R.string.sync_account_type));
			String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

			// Creating the account on the device and setting the auth token we got
			// (Not setting the auth token will cause another call to the server to authenticate the user)
			mAccountManager.addAccountExplicitly(account, accountPassword, null);
			mAccountManager.setAuthToken(account, accountName, authtoken);
			mAccountManager.setPassword(account, accountPassword);
		if(!getIntent().hasExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)){
			loginSuccess();
		}
		setResult(RESULT_OK, intent);
		finish();

	}





	/**
	 * Helper method to tell the network state
	 * @return
	 */
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}