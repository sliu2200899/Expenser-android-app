package cmu.edu.expenser;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InputActivity extends AppCompatActivity {

    private static EditText totalEditText;
    private static EditText dateEditText;
    private static Spinner categorySpinner;
    private static EditText peopleEditText;
    private static Button saveItemButton;
    private static String partFilename = "";

    View.OnClickListener saveItemButtonClicked = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Context context = getApplicationContext();
            saveItem();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        totalEditText = (EditText) findViewById(R.id.totalEditText);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        String[] items = new String[]{"Meal", "Shopping", "House", "Transportation", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        categorySpinner.setAdapter(adapter);
        categorySpinner.setSelection(0);

        peopleEditText = (EditText) findViewById(R.id.peopleEditText);
        saveItemButton = (Button) findViewById(R.id.saveItemButton);
        saveItemButton.setOnClickListener(saveItemButtonClicked);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(calendar.getTime());
            if (dateEditText != null) {
                dateEditText.setText(dateString);
            }
        }
    }

    private void saveItem() {
        ItemDAO dbhelper = new ItemDAO(this);
        String userId = "test";

        String totalStr = totalEditText.getText().toString().trim();
        if (totalStr == null || totalStr.length() == 0) {
            Toast.makeText(this, "Please input your total expense amount!", Toast.LENGTH_SHORT).show();
            return;
        }
        double total = Double.valueOf(totalStr);
        String dateString = dateEditText.getText().toString();
        if (dateString == null || dateString.length() == 0) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateString = sdf.format(calendar.getTime());
            dateEditText.setText(dateString);
        }
        String category = categorySpinner.getSelectedItem().toString();

        int people = 1;
        String peopleStr = peopleEditText.getText().toString();
        if (peopleStr != null && peopleStr.length() != 0) {
            people = Integer.valueOf(peopleStr);
        }
        dbhelper.insertItem(userId, total, dateString, category, people, partFilename);
    }
}
