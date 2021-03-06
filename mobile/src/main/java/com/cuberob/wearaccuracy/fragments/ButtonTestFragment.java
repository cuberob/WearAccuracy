package com.cuberob.wearaccuracy.fragments;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cuberob.Paths;
import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.interfaces.SendMessageListener;
import com.cuberob.wearaccuracy.views.PieChart;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rob.knegt on 11-3-2015.
 */
public class ButtonTestFragment extends Fragment implements MessageApi.MessageListener {

    private static final String TAG = "ButtonTestActivity";
    public static final String RESULTS = "results";
    public static final String RESULTS_CORRECT = "results_correct";
    public static final String RESULTS_INCORRECT = "results_incorrect";

    private SeekBar mSeekBar;
    private TextView mTestSizeTextView, mResultsTextView;
    private PieChart mPieChart;

    private SendMessageListener mListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_button_test, container, false);

        mSeekBar = (SeekBar) v.findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress += 2;
                mTestSizeTextView.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTestSizeTextView = (TextView) v.findViewById(R.id.testSizesTextView);
        mResultsTextView = (TextView) v.findViewById(R.id.resultsTextView);

        mPieChart = (PieChart) v.findViewById(R.id.pieChart);

        mPieChart.addItem(getString(R.string.correct_label), 4, getResources().getColor(android.R.color.holo_green_light));
        mPieChart.addItem(getString(R.string.incorrect_label), 1, getResources().getColor(android.R.color.holo_red_light));


        v.findViewById(R.id.two_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage(getTestSize(), Paths.START_TWO_BUTTON_TEST_PATH);
            }
        });
        v.findViewById(R.id.four_button).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.sendMessage(getTestSize(), Paths.START_FOUR_BUTTON_TEST_PATH);
            }
        });

        setRetainInstance(true);
        if(savedInstanceState != null){
            //Restore settings after rotation change
            mResultsTextView.setText(savedInstanceState.getString(RESULTS));
            mPieChart.clearData();
            mPieChart.addItem(getString(R.string.correct_label), savedInstanceState.getInt(RESULTS_CORRECT, 4), getResources().getColor(android.R.color.holo_green_light));
            mPieChart.addItem(getString(R.string.incorrect_label), savedInstanceState.getInt(RESULTS_INCORRECT, 1), getResources().getColor(android.R.color.holo_red_light));
        }

        return v;
    }

    private byte[] getTestSize(){
        return mTestSizeTextView.getText().toString().getBytes();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if(!messageEvent.getPath().equals(Paths.RESULTS_BUTTON_TEST_PATH)){
            Log.e(TAG, "Unexpected results path, changed fragment during test?");
            return;
        }

        String response = new String(messageEvent.getData());
        Gson gson = new Gson();
        TestResult result = null;
        try {
            result = gson.fromJson(response, TestResult.class);
        }catch (JsonSyntaxException e){
            Log.e(TAG, "Could not parse json!");
            return;
        }

        final TestResult finalResult = result;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mResultsTextView.setText(finalResult.getResultsString());
                mPieChart.clearData();
                mPieChart.addItem(getString(R.string.correct_label), finalResult.correct, getResources().getColor(android.R.color.holo_green_light));
                mPieChart.addItem(getString(R.string.incorrect_label), finalResult.incorrect, getResources().getColor(android.R.color.holo_red_light));

                revealView((View) mPieChart.getParent());

                if(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("log_to_disk", false)){
                    logToDisk(finalResult);
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealView(View myView){
        if(Build.VERSION.SDK_INT < 21){
            Log.i(TAG, "revealView only works on Lollipop");
            return;
        }
        // get the center for the clipping circle
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }

    private void logToDisk(final TestResult result) {
        if(result == null){
            Log.e(TAG, "TestResult cannot be null!");
            return;
        }

        final Dialog dialog = new Dialog(getActivity());

        dialog.setTitle(getActivity().getString(R.string.dialog_log_data_title));
        dialog.setContentView(R.layout.dialog_log_data);

        final RadioGroup radioSexGroup = (RadioGroup) dialog.findViewById(R.id.radioSex);
        final NumberPicker numberPicker = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        Button saveButton = (Button) dialog.findViewById(R.id.button_save_dialog);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int age = numberPicker.getValue();
                String gender = (radioSexGroup.getCheckedRadioButtonId() == R.id.radioMale) ? "male" : "female";

                StringBuilder contents = new StringBuilder();
                contents.append("age:").append(age).append("\n");
                contents.append("gender:").append(gender).append("\n");
                contents.append("test_size:").append(result.total()).append("\n");
                contents.append("correct:").append(result.correct).append("\n");
                contents.append("incorrect:").append(result.incorrect).append("\n");
                contents.append("input_speed_ms:").append(result.averageDuration()).append("\n");
                contents.append("accuracy:").append(String.format("%.2f", result.accuracy())).append("\n");
                contents.append("buttons:").append(result.buttons);

                writeToDisk(contents.toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Writes contents to storage/wearAccuracy/timestamp.txt
     * @param contents
     */
    private void writeToDisk(String contents) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/wearAccuracy");
        myDir.mkdirs();

        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String format = s.format(new Date());

        String filename = format + ".txt";
        File file = new File (myDir, filename);

        if (file.exists()){
            Log.e(TAG, "File Already Exists, cancelling write!");
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(contents.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, newConfig.toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SendMessageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SendMessageListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(RESULTS, mResultsTextView.getText().toString());
        List<PieChart.Item> data = mPieChart.getData();
        int correct = 0;
        int incorrect = 0;
        for(PieChart.Item item : data){
            if(item.mLabel.equals(getString(R.string.correct_label))){
                correct = (int)item.mValue;
            }else if(item.mLabel.equals(getString(R.string.incorrect_label))){
                incorrect = (int) item.mValue;
            }
        }
        outState.putInt(RESULTS_CORRECT, correct);
        outState.putInt(RESULTS_INCORRECT, incorrect);
    }

    private class TestResult {
        public int correct;
        public int incorrect;
        public long duration;
        public int buttons;

        public int total(){
            return correct + incorrect;
        }

        public double accuracy(){
            return (double)correct / (double)total();
        }

        public long averageDuration(){
            return duration / (total() - 1);
        }

        private String getResultsString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Test size: " + total());
            sb.append("\nCorrect: " + correct);
            sb.append("\nIncorrect: " + incorrect);
            sb.append("\nAccuracy: " + ((int)(accuracy() * 100)) + "%");
            sb.append("\nPress Speed: " + (averageDuration()) + "ms");
            sb.append("\nButtons: " + buttons);
            return sb.toString();
        }
    }
}
