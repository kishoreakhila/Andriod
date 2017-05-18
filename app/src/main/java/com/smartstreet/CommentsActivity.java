package com.smartstreet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity that handles the typing and posting of comments.
 */
public class CommentsActivity extends FragmentActivity implements View.OnClickListener {
    private EditText mComment;
    private CommentsAdapter adapter;
    private ListView listView;

    public static Intent createIntent(Context context) {
        final Intent intent = new Intent(context, CommentsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mComment = (EditText) findViewById(R.id.add_comment);
        findViewById(R.id.post_comment).setOnClickListener(this);

        listView = (ListView) findViewById(R.id.comments_listview);

        // Create the comments adapter fromt the latest comments stored in the current SmartTree
        // instance.
        adapter = createLatestCommentsAdapter();
        listView.setAdapter(adapter);
    }

    private CommentsAdapter createLatestCommentsAdapter() {
        String[] commentsArray = new String[SmartCircuitTree.getInstance().getComments().size()];
        return new CommentsAdapter(this, R.layout.row_comments_adapter,
                SmartCircuitTree.getInstance().getComments().toArray(commentsArray));
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.post_comment) {
            // Clear the current comment, and add it to the list of comments, and refresh the listview.
            SmartCircuitTree.getInstance().addComment(mComment.getText().toString());
            mComment.setText(null);
            adapter = createLatestCommentsAdapter();
            listView.setAdapter(adapter);
        }
    }

    /**
     * The adapter that provides the comments for the listview.
     */
    private class CommentsAdapter extends ArrayAdapter<String> {
        private String[] items;

        public CommentsAdapter(Context context, int textViewResourceId,
                               String[] items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_comments_adapter, null);
            }

            String it = items[position];
            if (it != null) {
                TextView view = (TextView) v.findViewById(R.id.comment);
                if (view != null) {
                    view.setText(it);
                }
            }

            return v;
        }
    }
}
