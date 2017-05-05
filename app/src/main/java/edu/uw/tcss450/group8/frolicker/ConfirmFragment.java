package edu.uw.tcss450.group8.frolicker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmFragment extends Fragment {

    public ConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) {
            String user = getArguments().getString(getString(R.string.user_key));
            String pass = getArguments().getString(getString(R.string.pass_key));
            updateContent(user, pass);
        }
    }

    private void updateContent(String user, String pass)
    {
        TextView uv = (TextView) getActivity().findViewById(R.id.user_view);
        uv.setText(user);

        TextView pv = (TextView) getActivity().findViewById(R.id.pass_view);
        pv.setText(pass);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
