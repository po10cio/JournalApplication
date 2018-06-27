package com.aloineinc.journalapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aloineinc.journalapplication.Userauthentication.UserLoginActivity;
import com.aloineinc.journalapplication.localdb.JournalDbHelper;
import com.aloineinc.journalapplication.localdb.model.JournalModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private JournalsAdapter mJournalsAdapter;
    private List<JournalModel> journalsList = new ArrayList<>();
    private CoordinatorLayout mCoordinatorLayout;
    private RecyclerView mRecyclerView;
    private TextView mJournalsView;

    private JournalDbHelper mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        mJournalsView = findViewById(R.id.empty_journals_view);

        mDb = new JournalDbHelper(this);

        journalsList.addAll(mDb.getAllJournals());






        firebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                        startActivity(new Intent(MainActivity.this, UserLoginActivity.class));
                }

            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case R.id.logout:
                firebaseAuth.signOut();

        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void controlEmptyJournals() {
        // you can check journalsList.size() > 0

        if (mDb.getJournalsCount() > 0) {
            mJournalsView.setVisibility(View.GONE);
        } else {
            mJournalsView.setVisibility(View.VISIBLE);
        }
    }
    private void createJournal(String journal) {
        // inserting journal in db and getting
        // newly inserted journal id
        long id = mDb.insertJournal(journal);

        // get the newly inserted journal from db
        JournalModel n = mDb.getJournal(id);

        if (n != null) {
            // adding new journal to array list at 0 position
            journalsList.add(0, n);

            // refreshing the list
            mJournalsAdapter.notifyDataSetChanged();

            controlEmptyJournals();
        }
    }
    private void updateJournal(String journal, int position) {
        JournalModel n = journalsList.get(position);
        // updating journal text
        n.setJournal(journal);

        // updating journal in db
        mDb.updateJournal(n);

        // refreshing the list
        journalsList.set(position, n);
        mJournalsAdapter.notifyItemChanged(position);

        controlEmptyJournals();
    }

    private void deleteJournal(int position) {
        // deleting the journal from db
        mDb.deleteJournal(journalsList.get(position));

        // removing the journal from the list
        journalsList.remove(position);
        mJournalsAdapter.notifyItemRemoved(position);

        controlEmptyJournals();
    }



}