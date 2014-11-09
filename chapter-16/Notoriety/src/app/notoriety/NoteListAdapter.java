package app.notoriety;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import app.notoriety.models.Note;

public class NoteListAdapter
  extends ArrayAdapter<Note>
{
  public NoteListAdapter( Context context, List<Note> notes) {
    super(context, android.R.layout.simple_list_item_activated_1, notes);
  }
}
