/*
 * Copyright 2009 Pierre-Yves Ricau
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License. 
 */

package info.piwai.yasdic.example.countadroid.activity;

import info.piwai.yasdic.ApplicationContainerUtil;
import info.piwai.yasdic.Container;
import info.piwai.yasdic.example.countadroid.R;
import info.piwai.yasdic.example.countadroid.tasks.CountTask;
import info.piwai.yasdic.example.countadroid.tasks.IncrementTask;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * The only activity of this example application. Any time you press the
 * increment button, a task is created and executed to increment the counter and
 * update the GUI with the new count value.
 * 
 * When you select a counter with the spinner, the counterId is update, a new
 * task is created (with the selected counter as a parameter), and the GUI is
 * updated with the count value of the selected counter.
 * 
 * @author Pierre-Yves Ricau (py.ricau+countadroid@gmail.com)
 * 
 */
public class StartActivity extends Activity implements ICountingActivity {

	/*
	 * The GUI Views
	 */
	private Button		incrementButton;
	private TextView	countText;
	private Spinner		counterSpinner;

	/*
	 * The bean container
	 */
	private Container	container2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ApplicationContainerUtil.manageContext(this);

		findViews();
		setContents();
		setListeners();

	}

	/**
	 * Retrieving the views from their ids
	 */
	private void findViews() {
		incrementButton = (Button) findViewById(R.id.increment_button);
		countText = (TextView) findViewById(R.id.count_text);
		counterSpinner = (Spinner) findViewById(R.id.counter_spinner);
	}

	/**
	 * Setting the content of the views
	 */
	private void setContents() {

		// Linking an adapter to the spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.counters, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		counterSpinner.setAdapter(adapter);

	}

	/**
	 * Adding listeners to the views
	 */
	private void setListeners() {
		// The increment button listens for on click events
		incrementButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (container2.hasStoredSingleton("counterId")) {
					IncrementTask task = container2.getBean("incrementTask");
					task.execute();
				}
			}
		});

		// onItemSelected will be called when the user makes a choice on the
		// spinner
		counterSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3) {
				/*
				 * Defining the id of the counter that should be used when
				 * creating tasks
				 */
				switch (parent.getSelectedItemPosition()) {
				case 0:
					container2.storeSingleton("counterId", "localCounter");
					break;
				case 1:
					container2.storeSingleton("counterId", "remoteCounter");
					break;
				default:
					throw new RuntimeException("What did you select ??");
				}
				// Updating the GUI by calling a count task
				CountTask task = container2.getBean("countTask");
				task.execute();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public void onCountComplete(Integer count) {
		// Updating the GUI
		countText.setText(count.toString());
	}

	public void setContainer2(Container container2) {
		this.container2 = container2;
	}

}