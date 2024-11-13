package com.example.privatecontacts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private List<Contact> contactList;
    private ContactsAdapter contactsAdapter;
    private TextInputEditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacts);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchField = findViewById(R.id.searchField);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                refreshContacts();
            }
        });


        dbHelper = new DatabaseHelper(this);
        contactList = dbHelper.getAllContacts();

        RecyclerView contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        contactsAdapter = new ContactsAdapter(contactList, new ContactsAdapter.OnContactClickListener() {
            @Override
            public void onCallClick(Contact contact) {
                // Handle call intent
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contact.getPhone()));
                startActivity(callIntent);
            }

            @Override
            public void onMessageClick(Contact contact) {
                // Handle message intent
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contact.getPhone()));
                startActivity(smsIntent);
            }

            @Override
            public boolean onContactLongClick(Contact contact) {
                String text = contact.getName() + "\n" + contact.getPhone();
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);

                // Start the share intent
                Intent chooser = Intent.createChooser(shareIntent, "Share Contact");
                if (shareIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
                return true;
            }

            @Override
            public void onContactClick(Contact contact) {
                // Handle contact intent
                Intent contactIntent = new Intent(ContactsActivity.this, AddEditContactActivity.class);
                contactIntent.putExtra("contactId", contact.getId());
                startActivity(contactIntent);
            }

            @Override
            public void onDeleteClick(Contact contact) {
                dbHelper.deleteContact(contact.getId());
                refreshContacts();
            }
        });

        contactsRecyclerView.setAdapter(contactsAdapter);

        findViewById(R.id.addContactButton).setOnClickListener(v -> {
            // Handle add contact action
            Intent intent = new Intent(ContactsActivity.this, AddEditContactActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshContacts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            contactList.clear();
            contactList.addAll(dbHelper.getAllContacts());
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private void refreshContacts() {
        contactList.clear();
        contactList.addAll(dbHelper.queryContacts(searchField.getText().toString().trim()));
        contactsAdapter.notifyDataSetChanged();
    }
}
