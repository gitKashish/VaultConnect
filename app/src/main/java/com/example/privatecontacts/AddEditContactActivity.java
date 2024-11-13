    package com.example.privatecontacts;

    import android.os.Bundle;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.material.button.MaterialButton;
    import com.google.android.material.textfield.TextInputEditText;

    public class AddEditContactActivity extends AppCompatActivity {
        private DatabaseHelper dbHelper;
        private TextInputEditText contactNameEditText;
        private TextInputEditText contactPhoneEditText;
        private int contactId = -1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_add_edit_contact);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            dbHelper = new DatabaseHelper(this);
            contactNameEditText = findViewById(R.id.contactName);
            contactPhoneEditText = findViewById(R.id.contactPhone);
            MaterialButton saveButton = findViewById(R.id.saveButton);

            Intent intent = getIntent();
            if (intent.hasExtra("contactId")) {
                contactId = intent.getIntExtra("contactId", -1);
                Contact contact = dbHelper.getContactById(contactId);
                if (contact != null) {
                    contactNameEditText.setText(contact.getName());
                    contactPhoneEditText.setText(contact.getPhone());
                }
            }

            saveButton.setOnClickListener(v -> {
                String name = contactNameEditText.getText().toString().trim();
                String phone = contactPhoneEditText.getText().toString().trim();

                if (name.isEmpty()) {
                    contactNameEditText.setError("Contact Name cannot be empty");
                    return;
                }

                if (phone.isEmpty()) {
                    contactPhoneEditText.setError("Phone Number cannot be empty");
                    return;
                } else if (phone.length() < 7) {
                    contactPhoneEditText.setError("Phone Number is too short!");
                    return;
                }

                boolean isDuplicate = dbHelper.checkDuplicateContact(name, phone);
                if (isDuplicate) {
                    Toast.makeText(AddEditContactActivity.this, "Contact already exists.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (contactId == -1) {
                    // Add new contact
                    dbHelper.addContact(name, phone);
                } else {
                    // Update existing contact
                    dbHelper.updateContact(contactId, name, phone);
                }
                setResult(RESULT_OK);
                finish();
            });
        }
    }