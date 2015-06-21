package com.disycs.quizmo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.disycs.quizmo.api.HttpProxy;
import com.disycs.quizmo.database.QuizmooContentProvider;

import com.disycs.quizmo.database.tables.QuestionnaireTable;
import com.disycs.quizmo.model.Questionnaire;
import com.disycs.quizmo.model.Questionnaire.CATEGORY;
import com.disycs.quizmo.model.Questionnaire.STATE;

import com.disycs.quizmo.design.FontChangeCrawler;
import com.disycs.quizmo.navdrawer.QuestionDetailActivity;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuestionnairesActivity extends ActionBarActivity implements
        ActionBar.TabListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    public static String QUESTIONNAIRE_EXTRA = "Questionnaire";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    public void setContentView(int view) {
        super.setContentView(view);
        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "font/montserratregular.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaires);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(false);

        // Create the mAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager with the sections mAdapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the mAdapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab,
                              FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            String section = "";
            if (position == 0) {
                section = STATE.ONGOING.getCode();
            } else if (position == 1) {
                section = STATE.CLOSED.getCode();
            } else {
                section = STATE.DRAFT.getCode();
            }
            return PlaceholderFragment.newInstance(getResources().getStringArray(R.array.state)[position], section,getIntent().getBooleanExtra(MainActivity.FIRST_LOGIN,false));
        }

        @Override
        public int getCount() {
            Resources res = getResources();
            return (res.getStringArray(R.array.state)).length;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            Resources res = getResources();
            String[] titles = res.getStringArray(R.array.state);
            return titles[position].toUpperCase(l);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
        private static final String LOG_TAG =PlaceholderFragment.class.getSimpleName();
        private static final short QUESTIONNAIRE_TABLE_ID = 1;
        private static final String QUESTIONNAIRE_LIST_KEY = "QUESTIONNAIE_LIST";



        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_TYPE = "section_type";
        private static final String ARG_SECTION_NAME = "section_name";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(String sectionType, String sectionName , boolean isFistTimeLogging) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION_TYPE, sectionType);
            args.putString(ARG_SECTION_NAME, sectionName);
            args.putBoolean(MainActivity.FIRST_LOGIN,isFistTimeLogging);
            fragment.setArguments(args);
            return fragment;
        }

        private LoaderManager mLoaderManager;
        private List<Questionnaire>mQuestionnairesList;
        private ContentObserver mObserver;
        QuestionnaireAdapter mAdapter;

        ProgressBar mProgressBar;
        GridView mGridView;

        STATE mState;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_questionnaires,
                    container, false);

            mState = STATE.get(getArguments().getString(ARG_SECTION_NAME));
            mLoaderManager = getLoaderManager();
            if(savedInstanceState ==null){
                mLoaderManager.initLoader(QUESTIONNAIRE_TABLE_ID,null,this);
                mQuestionnairesList = new ArrayList<Questionnaire>();

            }
            else{
                mQuestionnairesList = (ArrayList<Questionnaire>) savedInstanceState.getSerializable(QUESTIONNAIRE_LIST_KEY);
            }
            TextView textView = (TextView) rootView
                    .findViewById(R.id.section_label);
            mGridView = (GridView) rootView
                    .findViewById(R.id.GRIDquest);
            mProgressBar = (ProgressBar) rootView.
                    findViewById(R.id.questionnaireLoadingPorgressBar);
            if(getActivity().getIntent().getBooleanExtra(MainActivity.FIRST_LOGIN, false)){
                mProgressBar.setVisibility(View.VISIBLE);
            }

            mAdapter = new QuestionnaireAdapter(getActivity(), mQuestionnairesList);
            mGridView.setAdapter(mAdapter);
    		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adpt, View view,
                                        int position, long arg3) {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(QUESTIONNAIRE_EXTRA, (Questionnaire) adpt.getItemAtPosition(position));
                    startActivity(intent);
                }
            });

            textView.setText(getArguments().getString(
                    ARG_SECTION_TYPE).toUpperCase());

            mObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {

                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    Log.v(LOG_TAG, "Observer notified");

                    updateView();
                }
            };
            getActivity().getContentResolver().registerContentObserver(QuizmooContentProvider.QUESTIONNAIRE_CONTENT_URI,false,mObserver);
            return rootView;
        }

        private void updateView() {
            //mQuestionnairesList= new ArrayList<Questionnaire>();
            mLoaderManager.restartLoader(QUESTIONNAIRE_TABLE_ID,null,this);
            if(getActivity().getIntent().getBooleanExtra(MainActivity.FIRST_LOGIN,false)){
                mProgressBar.setVisibility(View.GONE);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putSerializable(QUESTIONNAIRE_LIST_KEY, (Serializable) mQuestionnairesList);
        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {


            return new CursorLoader(getActivity().getApplicationContext(), QuizmooContentProvider.QUESTIONNAIRE_CONTENT_URI,
                    QuestionnaireTable.PROJECTION_ALL,
                    QuestionnaireTable.STATE_QUESTIONNAIRE+"=?",
                    new String[]{mState.getCode()},
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if(loader.getId() == QUESTIONNAIRE_TABLE_ID){
                Log.v(LOG_TAG, "Loading questions finished");
                if(data.moveToFirst()){
                    while(!data.isAfterLast()){

                        try {
                            mQuestionnairesList.add(createQuestionnaireItem(data));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (!data.isClosed()) {
                            data.moveToNext();
                        }

                    }
                    mAdapter.notifyDataSetChanged();
                }

                data.close();
                mLoaderManager.destroyLoader(QUESTIONNAIRE_TABLE_ID);

            }

        }

        private Questionnaire createQuestionnaireItem(Cursor data) throws ParseException {
            StringBuilder date =new StringBuilder(data.getString(data.getColumnIndex(QuestionnaireTable.DAT_CR_QUESTIONNAIRE)));
            date.delete(date.length() - 8, date.length());
            return
                    new Questionnaire(data.getInt(data.getColumnIndex(QuestionnaireTable.ID_QUESTIONNAIRE)),
                            data.getString(data.getColumnIndex(QuestionnaireTable.TITLE_QUESTIONNAIRE)),
                            data.getString(data.getColumnIndex(QuestionnaireTable.DESC_QUESTIONNAIRE)),
                            date.toString(),
                            data.getString(data.getColumnIndex(QuestionnaireTable.HASH_QUESTIONNAIRE)),
                            STATE.get(data.getString(data.getColumnIndex(QuestionnaireTable.STATE_QUESTIONNAIRE))),
                            CATEGORY.get(data.getString(data.getColumnIndex(QuestionnaireTable.CAT_QUESTIONNAIRE))),
                            data.getString(data.getColumnIndex(QuestionnaireTable.QUESTIONS))
                            );
        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

    }


    private void logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.logout);
        alertDialog.setMessage(R.string.logoutMsg);
        alertDialog.setNegativeButton(R.string.alertNegativeAnswer, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        alertDialog.setPositiveButton(R.string.alertPositiveAnswer, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        alertDialog.show();

    }


    public void showListItemPopup(final View v) {
        PopupMenu popupMenu = new PopupMenu(QuestionnairesActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.item_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
				/*Toast.makeText(QuestionnairesActivity.this,
						item.toString(),
						Toast.LENGTH_LONG).show();
				MySQLiteHelper db = new MySQLiteHelper(QuestionnairesActivity.this);
				PlaceholderFragment phf = getVisibleFragment();
				if(item.getItemId()==R.id.save)
				{					
					db.addQuestionnaire(((QuestionnaireViewInformations)
							v.getTag()).Q);	
					updateAdapter(phf);
				}
				else if(item.getItemId()==R.id.delete){
					db.delQuestionnaire(((QuestionnaireViewInformations)
							v.getTag()).Q);
					updateAdapter(phf);
				}*/

                return true;
            }
        });
        popupMenu.show();
    }

    private PlaceholderFragment getVisibleFragment() {
        List<Fragment> list = getSupportFragmentManager().getFragments();
        for (Fragment fragment : list) {
            if (fragment != null && fragment.getUserVisibleHint()) {
                return (PlaceholderFragment) fragment;
            }
        }
        return null;
    }

    private void updateAdapter(PlaceholderFragment phf) {
        if (phf != null) {
            phf.mAdapter.notifyDataSetChanged();
        }
    }


}


