package edu.uw.tcss450.group8.frolicker;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * This fragment has a login and a register button;
 * clicking either will take the user to the corresponding fragment.
 *
 * @author Chris Dale
 */
public class LoginOrRegisterFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;


    public LoginOrRegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_or_register, container, false);
        Button b = (Button) v.findViewById(R.id.toTwo);
        b.setOnClickListener(this);
        b = (Button) v.findViewById(R.id.toThree);
        b.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view)
    {
        if (mListener != null) {
            switch (view.getId()) {
                case R.id.toTwo:
                    mListener.onFragmentInteraction(2);
                    break;
                case R.id.toThree:
                    mListener.onFragmentInteraction(3);
                    break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int theFrag);
    }
}
