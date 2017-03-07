package com.a7ate9.aparvez.rhymingassistant;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.a7ate9.aparvez.rhymingassistant.utils.HelperUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_list_item_1;

/**
 * Created by aparvez on 3/6/17.
 */
public class PlaceholderFragment extends Fragment {

    public static final String NO_RHYMING_WORDS_FOUND_MESSAGE = "Dammit! This rhymes only with Rajnikanth";
    private EditText wordTextBox;
    private Button rhymeButton;
    private ListView rhymeWordsList;
    private List<String> rhymingWords;
    private String word;

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
                word = wordTextBox.getText().toString();
                getRhymingWords(word);
                if (word.length() == 0) {
                    Toast.makeText(getActivity(), "Really?", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getRhymingWords(String word) {
        if (HelperUtility.hasNetworkConnection(getActivity())) {
            try {
                FetchRhymingWords fetchRhymingWords = new FetchRhymingWords();
                fetchRhymingWords.execute(word);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        }
    }

    public void getWidgetsFromID() {
        wordTextBox = (EditText) getActivity().findViewById(R.id.wordTextBox);
        rhymeButton = (Button) getActivity().findViewById(R.id.rhymeButton);
        rhymeWordsList = (ListView) getActivity().findViewById(R.id.rhymeWordList);
    }


    private class FetchRhymingWords extends AsyncTask<String, Void, List<String>> {
        private final String LOG_TAG = FetchRhymingWords.class.getSimpleName();

        @Override
        protected List<String> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String rhymingWordsJsonString = null;

            try {
                final String DATA_MUSE_API_BASE_URL = "https://api.datamuse.com/words?rel_rhy=";
                URL url = new URL(DATA_MUSE_API_BASE_URL + params[0]);
                Log.v(LOG_TAG, url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0)
                    return null;

                rhymingWordsJsonString = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }

                try {
                    return getRhymingWordsFromJson(rhymingWordsJsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

        private List<String> getRhymingWordsFromJson(String rhymingWordsJsonString)
                throws JSONException {
            final String WORD = "word";
            rhymingWords = new ArrayList<>();
            JSONArray jsonWordsArray = new JSONArray(rhymingWordsJsonString);
            for (int i = 0; i < jsonWordsArray.length(); i++) {
                JSONObject currentJSONObject = jsonWordsArray.getJSONObject(i);
                String rhymedWord = currentJSONObject.get(WORD).toString();
                rhymingWords.add(rhymedWord);
            }
            return rhymingWords;
        }

        @Override
        protected void onPostExecute(List<String> rhymingWords) {
            super.onPostExecute(rhymingWords);
            if(rhymingWords.isEmpty()){
                Toast.makeText(getActivity(), "I can't climb & this doesn't rhyme.", Toast.LENGTH_SHORT).show();
            }
            rhymingWords.add(NO_RHYMING_WORDS_FOUND_MESSAGE);
            populateListViewWithWords(rhymingWords);
        }

        private void populateListViewWithWords(List<String> rhymingWords) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    getActivity(),
                    simple_list_item_1,
                    rhymingWords);
            rhymeWordsList.setAdapter(arrayAdapter);
        }
    }
}
