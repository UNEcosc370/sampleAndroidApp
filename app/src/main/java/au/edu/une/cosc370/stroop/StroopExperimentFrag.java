package au.edu.une.cosc370.stroop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * UI for a simple Stroop test. The model is in {@link StroopExperiment}.
 */
public class StroopExperimentFrag extends Fragment {

    private OnExptFragmentInteractionListener mListener;

    /**
     * The model, containing the experimental data
     */
    private StroopExperiment model = new StroopExperiment();

    /**
     * Generated buttons for each colour in the experiment
     */
    private ArrayList<Button> colourButtons = new ArrayList<>();

    /**
     * Starts the test
     */
    private Button startButton;

    /**
     * Starts over, when the test has been completed
     */
    private Button restartButton;

    /**
     * Shows the word to the user
     */
    private TextView word;

    /**
     * Shows the results at the end of the test
     */
    private TextView results;

    /**
     * Handler for starting the test
     */
    private void start() {
        hideResults();
        enableColourButtons();
        startButton.setEnabled(false);
        startButton.setVisibility(View.INVISIBLE);
        model.start();
        updateItem();
    }

    /**
     * Handler for resetting the test
     */
    private void restart() {
        model.reset();
        start();
    }

    /**
     * Updates the word on the screen depending on the state of the test
     */
    private void updateItem() {
        if (model.currentItem == null) {
            word.setVisibility(View.INVISIBLE);
            showResults();
        } else {
            word.setVisibility(View.VISIBLE);
            word.setText(model.currentItem.word.name());
            word.setTextColor(model.currentItem.col.col);
        }
    }

    /**
     * Hides the results and restart button
     */
    private void hideResults() {
        results.setVisibility(View.INVISIBLE);
        restartButton.setVisibility(View.INVISIBLE);
    }

    /**
     * Shows the results and restart button
     */
    private void showResults() {
        results.setVisibility(View.VISIBLE);
        results.setText(model.results());
        restartButton.setVisibility(View.VISIBLE);
    }

    /**
     * Handler for the user answering an item
     * @param c
     */
    private void answerItem(StroopExperiment.Colour c) {
        model.answerItem(c);
        updateItem();

        if (model.currentItem == null) {
            disableColourButtons();
        }
    }

    /**
     * Shows the colour buttons
     */
    private void enableColourButtons() {
        for (Button b : colourButtons) {
            b.setVisibility(View.VISIBLE);
            b.setEnabled(true);
        }
    }

    /**
     * Hides the colour buttons
     */
    private void disableColourButtons() {
        for (Button b : colourButtons) {
            b.setVisibility(View.INVISIBLE);
            b.setEnabled(false);
        }
    }


    public StroopExperimentFrag() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment StroopExperimentFrag.
     */
    public static StroopExperimentFrag newInstance() {
        StroopExperimentFrag fragment = new StroopExperimentFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // No arguments to do anything with
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_stroop_experiment, container, false);

        // Fill the GridView with buttons for the colours
        GridLayout grid = (GridLayout) v.findViewById(R.id.colourButtons);
        for (StroopExperiment.Colour c : StroopExperiment.Colour.values()) {
            Button button = new Button(v.getContext());
            button.setText(c.name());
            button.setOnClickListener((evt) -> answerItem(c));
            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);
            grid.addView(button);
            colourButtons.add(button);
        }

        // Get the buttons and remember them

        startButton = (Button) v.findViewById(R.id.startButton);
        startButton.setOnClickListener((evt) -> start());

        word = (TextView) v.findViewById(R.id.word);

        results = (TextView) v.findViewById(R.id.results);
        results.setVisibility(View.INVISIBLE);

        restartButton = (Button) v.findViewById(R.id.restartButton);
        restartButton.setOnClickListener((evt) -> restart());
        restartButton.setVisibility(View.INVISIBLE);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExptFragmentInteractionListener) {
            mListener = (OnExptFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExptFragmentInteractionListener");
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
    public interface OnExptFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
