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
package com.github.clboettcher.bonappetit.app.data.order.entity;

import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntityType;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberEntity;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

@DatabaseTable(tableName = "ITEM_ORDER")
public class ItemOrderEntity {

    @DatabaseField(generatedId = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "ITEM_ID", canBeNull = false)
    private Long itemId;

    @DatabaseField(columnName = "ITEM_TYPE")
    private ItemEntityType itemType;

    @DatabaseField(columnName = "ITEM_TITLE")
    private String itemTitle;

    @DatabaseField(columnName = "ITEM_PRICE")
    private BigDecimal itemPrice;

    @ForeignCollectionField(eager = true, maxEagerLevel = 2)
    private Collection<OptionOrderEntity> optionOrderEntities = new ArrayList<>();

    @DatabaseField(columnName = "NOTE")
    private String note = "";

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    private CustomerEntity customer;

    @DatabaseField(columnName = "ORDER_TIME", dataType = DataType.DATE_TIME, canBeNull = false)
    private DateTime orderTime;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    private SelectedStaffMemberEntity selectedStaffMember;

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public ItemEntityType getItemType() {
        return itemType;
    }

    public void setItemType(ItemEntityType itemType) {
        this.itemType = itemType;
    }

    public Collection<OptionOrderEntity> getOptionOrderEntities() {
        return optionOrderEntities;
    }

    public void setOptionOrderEntities(Collection<OptionOrderEntity> optionOrderEntities) {
        this.optionOrderEntities = optionOrderEntities;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public DateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(DateTime orderTime) {
        this.orderTime = orderTime;
    }

    public SelectedStaffMemberEntity getSelectedStaffMember() {
        return selectedStaffMember;
    }

    public void setSelectedStaffMember(SelectedStaffMemberEntity selectedStaffMember) {
        this.selectedStaffMember = selectedStaffMember;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("itemId", itemId)
                .append("optionOrderEntities.size", CollectionUtils.size(optionOrderEntities))
                .append("note", note)
                .append("customer", customer)
                .append("orderTime", orderTime)
                .append("selectedStaffMember", selectedStaffMember)
                .toString();
    }
}
