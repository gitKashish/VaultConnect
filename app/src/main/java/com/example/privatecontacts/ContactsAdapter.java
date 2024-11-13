package com.example.privatecontacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contact> contacts;
    private OnContactClickListener listener;

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
        void onCallClick(Contact contact);
        void onMessageClick(Contact contact);
        void onDeleteClick(Contact contact);
        boolean onContactLongClick(Contact contact);
    }

    public ContactsAdapter(List<Contact> contacts, OnContactClickListener listener) {
        this.contacts = contacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contactName.setText(contact.getName());
        holder.contactPhone.setText(contact.getPhone());

        holder.contactLayout.setOnClickListener(v -> listener.onContactClick(contact));
        holder.contactLayout.setOnLongClickListener(v -> listener.onContactLongClick(contact));
        holder.callButton.setOnClickListener(v -> listener.onCallClick(contact));
        holder.messageButton.setOnClickListener(v -> listener.onMessageClick(contact));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(contact));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout contactLayout;
        TextView contactName, contactPhone;
        MaterialButton callButton, messageButton, deleteButton;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactLayout = itemView.findViewById(R.id.contactLayout);
            contactName = itemView.findViewById(R.id.contactName);
            contactPhone = itemView.findViewById(R.id.contactPhone);
            callButton = itemView.findViewById(R.id.callButton);
            messageButton = itemView.findViewById(R.id.messageButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
