/*
 * Copyright (c) 2016 Claudius Boettcher (pos.bonappetit@gmail.com)
 *
 * This file is part of BonAppetit. BonAppetit is an Android based
 * Point-of-Sale client-server application for small restaurants.
 *
 * BonAppetit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonAppetit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BonAppetit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.clboettcher.bonappetit.app.ui.takeorders;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.ui.BonAppetitBaseFragmentActivity;
import com.github.clboettcher.bonappetit.app.ui.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.ui.preferences.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.ui.selectstaffmember.StaffMembersListActivity;

import javax.inject.Inject;

public class TakeOrdersActivity extends BonAppetitBaseFragmentActivity implements ActionBar.TabListener, OnSwitchToTabListener {

    /**
     * The tag for logging.
     */
    public static final String TAG = TakeOrdersActivity.class.getName();

    /**
     * The index of the select customer tab.
     */
    public static final int TAB_SELECT_CUSTOMER = 0;

    /**
     * The index of the menu tab.
     */
    public static final int TAB_MENU = 1;

    /**
     * The index of the order overview tab.
     */
    public static final int TAB_OVERVIEW = 2;

    @Inject
    SelectedStaffMemberDao selectedStaffMemberDao;

    @Inject
    StaffMemberDao staffMemberDao;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private TakeOrdersPagerAdapter takeOrdersPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.takeOrdersActivityPager)
    ViewPager viewPager;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_orders);
        ButterKnife.bind(this);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        this.actionBar = getActionBar();

        // Set the action bar subtitle
        this.actionBar.setSubtitle(getString(R.string.general_action_bar_sub_headline_order));
        this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        this.actionBar.setHomeButtonEnabled(false);
        // comment this in to use the app icon as "home"
        // button (and comment the previous line out ofc)
        // actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        takeOrdersPagerAdapter = new TakeOrdersPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        viewPager.setAdapter(takeOrdersPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected(int position = " + position + ")");
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                onSwitchToTab(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < takeOrdersPagerAdapter.getCount(); i++) {
            // CREATE a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(takeOrdersPagerAdapter.getPageTitle(i)).setTabListener(this));
        }

        // Prompt the user to select who he is on startup
        Intent intent = new Intent(this, StaffMembersListActivity.class);
        startActivityForResult(intent, StaffMembersListActivity.SELECT_STAFF_MEMBER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == StaffMembersListActivity.SELECT_STAFF_MEMBER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Long staffMemberId = data.getLongExtra(StaffMembersListActivity.EXTRA_SELECTED_STAFF_MEMBER_ID, -1L);
                if (staffMemberId == -1) {
                    throw new IllegalStateException(String.format("Expected %s to return intent containing the " +
                                    "id of the selected staff member",
                            StaffMembersListActivity.class.getName()));
                }
                StaffMemberEntity selectedStaffMember = staffMemberDao.getById(staffMemberId);
                Log.i(TAG, String.format("Saving new selected staff member: %s", selectedStaffMember));
                selectedStaffMemberDao.save(selectedStaffMember);
            } else {
                Log.i(TAG, String.format("Received activity result for " +
                        "request code SELECT_STAFF_MEMBER_REQUEST with " +
                        "a result code different from RESULT_OK: %d", resultCode));
            }
        } else {
            Log.i(TAG, String.format("Ignoring activity result for request code %d", requestCode));
            // Give nested fragments a chance to handle the activity result.
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_orders, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.actionSettings:
                startActivity(new Intent(this, BonAppetitPreferencesActivity.class));
                return true;
            case R.id.actionRegister:
                Intent intent = new Intent(this, StaffMembersListActivity.class);
                startActivityForResult(intent, StaffMembersListActivity.SELECT_STAFF_MEMBER_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public void onSwitchToTab(int tabPos) {
        Log.d(TAG, "onSwitchToTab(int tabPos = " + tabPos + ")");
        actionBar.setSelectedNavigationItem(tabPos);
        viewPager.setCurrentItem(tabPos, true);
        takeOrdersPagerAdapter.update(tabPos);
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }


}
