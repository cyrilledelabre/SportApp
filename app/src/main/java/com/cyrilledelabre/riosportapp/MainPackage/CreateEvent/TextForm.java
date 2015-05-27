/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyrilledelabre.riosportapp.MainPackage.CreateEvent;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cyrilledelabre.riosportapp.R;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 */
public class TextForm extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;


    private static FormSlideActivity.ViewHolder mHolder;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static TextForm create(int pageNumber, FormSlideActivity.ViewHolder holder) {
        mHolder = holder;
        TextForm fragment = new TextForm();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TextForm() {setHasOptionsMenu(true);}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    /**
     * Add menu layout
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().onCreateOptionsMenu(menu);
    }

    /**
     * On Click Menu make some actions
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_create_events_text, container, false);

        mHolder.mSendButton = (Button) rootView.findViewById(R.id.sendButton);
        mHolder.mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new addEventAsyncTask(false, mHolder, null, getActivity()).execute();
            }

        });

        findViewById(rootView);

        return rootView;
    }


    private void findViewById(ViewGroup rootView)
    {
        CharSequence mTitle = mHolder.mTitleView.getText().toString();
        if(mTitle.length() >0)
        {
            TextView title = (TextView) rootView.findViewById(R.id.form_result_title);
            title.setText(mTitle);
        }
        CharSequence mDesc = mHolder.mDescriptionView.getText().toString();
        if(mDesc.length() >0)
        {
            TextView title = (TextView) rootView.findViewById(R.id.form_result_description);
            title.setText(mDesc);
        }
    }
}
