package com.siddharthkumar.restaurantdeals;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;


public class RadiusFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    SeekBar seekBar;
    SeekBar seekBar2;
    TextView radius;
    int radius2=10;
    int results=25;

    public RadiusFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RadiusFragment newInstance() {
        RadiusFragment fragment = new RadiusFragment();

        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();

        if(b!=null) {
            radius2 = b.getInt("radius");
            results = b.getInt("results");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_radius, container, false);
        Bundle b = getActivity().getIntent().getExtras();

        if(b!=null) {
                radius2 = b.getInt("radius");
                results = b.getInt("results");
        }
        Log.e("Fragment",radius2+","+results);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        seekBar2 = (SeekBar)view.findViewById(R.id.seekBar2);
        TextView radiusText = (TextView)view.findViewById(R.id.radiusnumber);
        radiusText.setText("Radius = "+radius2);
        TextView resultstext = (TextView)view.findViewById(R.id.resultstext);
        String st = "Display "+results+" Results";
        SpannableString spannableString = new SpannableString(st);
        spannableString.setSpan(new UnderlineSpan(), 8,10,0);
        resultstext.setText(spannableString);

        if(seekBar!=null&&seekBar2!=null){
            if(radius2!=0)
            seekBar.setProgress(radius2);
            if(results!=0)
            seekBar2.setProgress(results);

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    onSeekChanged();
                    TextView radiusText = (TextView)getActivity().findViewById(R.id.radiusnumber);
                    radiusText.setText("Radius = "+progress);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    onSeekChanged();
                    TextView radiusText = (TextView)getActivity().findViewById(R.id.resultstext);
                    String st = "Display "+progress+" Results";
                    SpannableString spannableString = new SpannableString(st);
                    spannableString.setSpan(new UnderlineSpan(), 8,10,0);
                    radiusText.setText(spannableString);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            }
        else
            Log.e("FRAGMENT","NULL");

        radius = (TextView)view.findViewById(R.id.radiusnumber);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onSeekChanged() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
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
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
