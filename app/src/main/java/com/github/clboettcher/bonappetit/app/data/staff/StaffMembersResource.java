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
package com.github.clboettcher.bonappetit.app.data.staff;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.core.ConfigProvider;
import com.github.clboettcher.bonappetit.app.data.ErrorCode;
import com.github.clboettcher.bonappetit.app.data.ErrorMapper;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.staff.event.PerformStaffMembersUpdateEvent;
import com.github.clboettcher.bonappetit.app.data.staff.event.StaffMembersUpdateCompletedEvent;
import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import com.google.common.base.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class StaffMembersResource {

    private static final String TAG = StaffMembersResource.class.getName();

    private Context context;
    private StaffMemberService staffMemberService;
    private StaffMemberDao staffMemberDao;
    private SelectedStaffMemberDao selectedStaffMemberDao;
    private StaffMemberEntityMapper staffMemberEntityMapper;
    private EventBus bus;
    private ConfigProvider configProvider;
    private AtomicReference<Loadable<List<StaffMemberEntity>>> staffMembers = new
            AtomicReference<>(Loadable.<List<StaffMemberEntity>>initial());

    @Inject
    public StaffMembersResource(Context context,
                                StaffMemberService staffMemberService,
                                StaffMemberDao staffMemberDao,
                                SelectedStaffMemberDao selectedStaffMemberDao,
                                StaffMemberEntityMapper staffMemberEntityMapper,
                                EventBus bus,
                                ConfigProvider configProvider) {
        this.context = context;
        this.staffMemberService = staffMemberService;
        this.staffMemberDao = staffMemberDao;
        this.selectedStaffMemberDao = selectedStaffMemberDao;
        this.staffMemberEntityMapper = staffMemberEntityMapper;
        this.bus = bus;
        this.configProvider = configProvider;

        bus.register(this);
    }

    public Loadable<List<StaffMemberEntity>> getStaffMembers() {
        return staffMembers.get();
    }

    public void updateStaffMembers() {
        this.updateStaffMembers(null);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void updateStaffMembers(PerformStaffMembersUpdateEvent event) {
        this.staffMembers.set(Loadable.<List<StaffMemberEntity>>loading());
        if (configProvider.useTestData()) {
            updateStaffMembersWithTestData();
        } else {
            fetchStaffMembersFromServer();
        }
    }

    /**
     * Updates the staff member db content with content from local files.
     */
    private void updateStaffMembersWithTestData() {
        Log.i(TAG, "Test data enabled. Using local staff member test data.");
        List<StaffMemberDto> staffMemberDtos;
        try {
            staffMemberDtos = configProvider.readRawResourceAsJsonArray(
                    R.raw.staff_members, StaffMemberDto.class);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read staff member test data from resources. Update aborted.", e);
            this.staffMembers.set(Loadable.<List<StaffMemberEntity>>failed(ErrorCode.ERR_RESOURCE_ACCESS_FAILED));
            bus.post(new StaffMembersUpdateCompletedEvent());
            return;
        }

        List<StaffMemberEntity> staffMemberEntities = staffMemberEntityMapper
                .mapToStaffMemberEntities(staffMemberDtos);
        staffMemberDao.save(staffMemberEntities);
        this.staffMembers.set(Loadable.loaded(staffMemberEntities));
        // Select the first fetched staff member per default to
        // prevent errors
        this.selectStaffMemberIfNecessary(staffMemberEntities);

        bus.post(new StaffMembersUpdateCompletedEvent());
    }


    private void fetchStaffMembersFromServer() {
        Log.i(TAG, "Fetching the staff members from server.");
        staffMemberService.getStaffMembers(new Callback<List<StaffMemberDto>>() {
            @Override
            public void onResponse(Call<List<StaffMemberDto>> call, Response<List<StaffMemberDto>> response) {
                if (response.isSuccessful()) {
                    List<StaffMemberEntity> staffMemberEntities = staffMemberEntityMapper
                            .mapToStaffMemberEntities(response.body());
                    staffMemberDao.save(staffMemberEntities);
                    Log.i(TAG, "Staff member update successful");
                    StaffMembersResource.this.staffMembers.set(
                            Loadable.loaded(staffMemberEntities));
                    // Select the first fetched staff member per default to
                    // prevent errors
                    selectStaffMemberIfNecessary(staffMemberEntities);
                    bus.post(new StaffMembersUpdateCompletedEvent());
                } else {
                    String errorMsg = String.format("Staff member update failed: %d %s",
                            response.code(),
                            response.message());
                    Log.e(TAG, errorMsg);

                    ErrorCode errorCode = ErrorMapper.toErrorCode(response.code());

                    StaffMembersResource.this.staffMembers.set(
                            Loadable.<List<StaffMemberEntity>>failed(errorCode));
                    bus.post(new StaffMembersUpdateCompletedEvent());
                }
            }

            @Override
            public void onFailure(Call<List<StaffMemberDto>> call, Throwable t) {
                Log.e(TAG, "Staff member update failed", t);
                ErrorCode errorCode = ErrorMapper.toErrorCode(t);
                StaffMembersResource.this.staffMembers.set(
                        Loadable.<List<StaffMemberEntity>>failed(errorCode));
                bus.post(new StaffMembersUpdateCompletedEvent());

                if (BonAppetitApplication.DEBUG_TOASTS_ENABLED) {
                    String errorMsg = String.format("Staff members update failed: %s (%s)",
                            t.getMessage(),
                            t.getClass().getName());
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void selectStaffMemberIfNecessary(List<StaffMemberEntity> staffMemberEntities) {
        if (CollectionUtils.isNotEmpty(staffMemberEntities)) {
            Optional<SelectedStaffMemberEntity> selectedStaffMemberOpt = this.selectedStaffMemberDao.get();
            if (!selectedStaffMemberOpt.isPresent() || !this.staffMemberDao.exists(selectedStaffMemberOpt
                    .get()
                    .getStaffMemberId())) {
                StaffMemberEntity newSelectedStaffMember = staffMemberEntities.get(0);
                Log.i(TAG, String.format("Auto-selecting staff member %s because currently no staff member is " +
                                "selected or the selected staff member does not longer exist on the server.",
                        newSelectedStaffMember));
                this.selectedStaffMemberDao.save(newSelectedStaffMember);
            }
        }
    }

}
