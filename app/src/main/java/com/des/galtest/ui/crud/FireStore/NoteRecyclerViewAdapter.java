package com.des.galtest.ui.crud.FireStore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.des.galtest.R;
import com.des.galtest.model.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {
    private CrudFireStoreViewModel crudFireStoreViewModel ;
    private List<Note> notesList;
    private Context context;


 /*  public NoteRecyclerViewAdapter(List<Note> notesList, Context context, FirebaseFirestore firestoreDB) {
        this.notesList = notesList;
        this.context = context;
        this.firestoreDB = firestoreDB;
    }*/
public NoteRecyclerViewAdapter(List<Note> notesList, Context context, CrudFireStoreViewModel crudFireStoreViewModel) {
    this.notesList = notesList;
    this.context = context;
    this.crudFireStoreViewModel = crudFireStoreViewModel;
}
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int itemPosition = position;
        final Note note = notesList.get(itemPosition);

        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote(note);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(note.getId(), itemPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content;
        ImageView edit;
        ImageView delete;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.tvTitle);
            content = view.findViewById(R.id.tvContent);

            edit = view.findViewById(R.id.ivEdit);
            delete = view.findViewById(R.id.ivDelete);
        }
    }

    private void updateNote(Note note) {
        Intent intent = new Intent(context, NoteActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("UpdateNoteId", note.getId());
        intent.putExtra("UpdateNoteTitle", note.getTitle());
        intent.putExtra("UpdateNoteContent", note.getContent());
        context.startActivity(intent);
    }

    private void deleteNote(String id, final int position) {
            if(id!=null){
                crudFireStoreViewModel.deleteFireStore(id,position,notesList);
                notesList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notesList.size());
                Toast.makeText(context, "Eliminacion exitosa!", Toast.LENGTH_SHORT).show();

            }
        /*firestoreDB.collection("notes")
                .document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notesList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, notesList.size());
                        Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show();
                    }
                });*/
    }
}
