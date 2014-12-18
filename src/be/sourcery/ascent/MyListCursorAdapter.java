package be.sourcery.ascent;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder>{

    private final InternalDB db;
    private Context context;

    public MyListCursorAdapter(Context context, Cursor cursor, InternalDB db){
        super(context,cursor);
        this.context = context;
        this.db = db;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView nameView;
        public TextView dateView;
        public TextView gradeView;
        public TextView styleView;
        public TextView commentView;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            nameView = (TextView)view.findViewById(R.id.nameCell);
            dateView = (TextView)view.findViewById(R.id.dateCell);
            gradeView = (TextView)view.findViewById(R.id.gradeCell);
            styleView = (TextView)view.findViewById(R.id.styleCell);
            commentView = (TextView)view.findViewById(R.id.commentCell);

        }
        public void onClick(View v) {
            int position = getPosition();
            Ascent ascent = db.getAscent(position);
            editAscent(ascent);
        }
    }

    private void editAscent(Ascent ascent) {
        Intent myIntent = new Intent(context, EditAscentActivity.class);
        Bundle b = new Bundle();
        b.putLong("ascentId", ascent.getId());
        myIntent.putExtras(b);
        context.startActivity(myIntent);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ascent_list_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        Ascent ascent = Ascent.fromCursor(cursor);
        viewHolder.nameView.setText(ascent.getRoute().getName());
        viewHolder.dateView.setText(ascent.getDate().toString());
        viewHolder.gradeView.setText(ascent.getRoute().getGrade());
        viewHolder.styleView.setText(ascent.getStyle());
        viewHolder.commentView.setText(ascent.getComment());
    }
}