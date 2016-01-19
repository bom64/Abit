/*
 * Copyright 2016 Christian Basler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.dissem.apps.abit;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ch.dissem.apps.abit.listener.ActionBarListener;
import ch.dissem.apps.abit.listener.ListSelectionListener;
import ch.dissem.apps.abit.service.Singleton;
import ch.dissem.bitmessage.entity.Plaintext;
import ch.dissem.bitmessage.entity.valueobject.Label;
import ch.dissem.bitmessage.ports.MessageRepository;

/**
 * A list fragment representing a list of Messages. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link MessageDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ListSelectionListener}
 * interface.
 */
public class MessageListFragment extends AbstractItemListFragment<Plaintext> {

    private Label currentLabel;
    private MenuItem emptyTrashMenuItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        doUpdateList(((MainActivity) getActivity()).getSelectedLabel());
    }

    @Override
    public void updateList(Label label) {
        currentLabel = label;

        if (!isVisible()) return;

        doUpdateList(label);
    }

    private void doUpdateList(Label label) {
        setListAdapter(new ArrayAdapter<Plaintext>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                Singleton.getMessageRepository(getContext()).findMessages(label)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    convertView = inflater.inflate(R.layout.message_row, null, false);
                }
                Plaintext item = getItem(position);
                ((ImageView) convertView.findViewById(R.id.avatar)).setImageDrawable(new Identicon(item.getFrom()));
                TextView sender = (TextView) convertView.findViewById(R.id.sender);
                sender.setText(item.getFrom().toString());
                TextView subject = (TextView) convertView.findViewById(R.id.subject);
                subject.setText(item.getSubject());
                ((TextView) convertView.findViewById(R.id.text)).setText(item.getText());
                if (item.isUnread()) {
                    sender.setTypeface(Typeface.DEFAULT_BOLD);
                    subject.setTypeface(Typeface.DEFAULT_BOLD);
                } else {
                    sender.setTypeface(Typeface.DEFAULT);
                    subject.setTypeface(Typeface.DEFAULT);
                }
                return convertView;
            }
        });
        if (getActivity() instanceof ActionBarListener) {
            if (label != null) {
                ((ActionBarListener) getActivity()).updateTitle(label.toString());
            } else {
                ((ActionBarListener) getActivity()).updateTitle(getString(R.string.archive));
            }
        }
        if (emptyTrashMenuItem != null) {
            emptyTrashMenuItem.setVisible(label != null && label.getType() == Label.Type.TRASH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_message_list, container, false);

        // Show the dummy content as text in a TextView.
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab_compose_message);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ComposeMessageActivity.class);
                intent.putExtra(ComposeMessageActivity.EXTRA_IDENTITY, ((MainActivity) getActivity()).getSelectedIdentity());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.message_list, menu);
        emptyTrashMenuItem = menu.findItem(R.id.empty_trash);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.empty_trash:
                if (currentLabel.getType() != Label.Type.TRASH) return true;

                MessageRepository repo = Singleton.getMessageRepository(getContext());
                for (Plaintext message : repo.findMessages(currentLabel)) {
                    repo.remove(message);
                }
                updateList(currentLabel);
                return true;
            default:
                return false;
        }
    }

}
