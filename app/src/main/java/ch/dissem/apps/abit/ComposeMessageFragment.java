package ch.dissem.apps.abit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;
import ch.dissem.bitmessage.BitmessageContext;
import ch.dissem.bitmessage.entity.BitmessageAddress;

import static ch.dissem.apps.abit.ComposeMessageActivity.EXTRA_IDENTITY;
import static ch.dissem.apps.abit.ComposeMessageActivity.EXTRA_RECIPIENT;

/**
 * Compose a new message.
 */
public class ComposeMessageFragment extends Fragment {
    private BitmessageContext bmCtx;
    private BitmessageAddress identity;
    private BitmessageAddress recipient;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ComposeMessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(EXTRA_IDENTITY)) {
                identity = (BitmessageAddress) getArguments().getSerializable(EXTRA_IDENTITY);
            }
            if (getArguments().containsKey(EXTRA_RECIPIENT)) {
                recipient = (BitmessageAddress) getArguments().getSerializable(EXTRA_RECIPIENT);
            }
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compose_message, container, false);
        if (recipient != null) {
            EditText recipientInput = (EditText) rootView.findViewById(R.id.recipient);
            recipientInput.setText(recipient.toString());
        }
        EditText body = (EditText) rootView.findViewById(R.id.body);
        body.setInputType(EditorInfo.TYPE_TEXT_VARIATION_SHORT_MESSAGE | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        body.setImeOptions(EditorInfo.IME_ACTION_SEND | EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.compose, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send:
                Toast.makeText(getActivity(), "TODO: Send", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

