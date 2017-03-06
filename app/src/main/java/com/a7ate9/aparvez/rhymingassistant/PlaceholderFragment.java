package com.a7ate9.aparvez.rhymingassistant;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by aparvez on 3/6/17.
 */
public class PlaceholderFragment extends Fragment {

    private EditText wordTextBox;
    private Button rhymeButton;
    private ListView rhymeWordsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_main, container, false);
        return fragmentView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getWidgetsFromID();
        super.onActivityCreated(savedInstanceState);

        rhymeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRhymingWords();
                Toast.makeText(getActivity(), "Hi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRhymingWords() {
        if (HelperUtility.hasNetworkConnection(getActivity())) {
            try
            {
                FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
                FetchReviewTask fetchReviewTask = new FetchReviewTask();
                fetchTrailerTask.execute(movie.getId());
                fetchReviewTask.execute(movie.getId());
            }
            catch (Exception e){
                Log.d("Exception",e.toString());
            }
        }
    }

    public void getWidgetsFromID(){
        wordTextBox = (EditText) getActivity().findViewById(R.id.wordTextBox);
        rhymeButton = (Button) getActivity().findViewById(R.id.rhymeButton);
        rhymeWordsList = (ListView) getActivity().findViewById(R.id.rhymeWordList);
    }


}
