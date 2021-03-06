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
package com.github.clboettcher.bonappetit.app.data.menu.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Entity class for options. Is modeled as single table because ORMlite does not
 * support hierarchy mapping (like hibernates table per subclass, single table).
 */
@DatabaseTable(tableName = "OPTION")
public class OptionEntity implements CheckboxOption, ValueOption, RadioOption {

    /*
     *****************************************************************************************************************
     * General properties
     *****************************************************************************************************************
     */

    @DatabaseField(id = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "TITLE")
    private String title;

    @DatabaseField(columnName = "INDEX")
    private Integer index;

    @DatabaseField(columnName = "OPTION_TYPE")
    private OptionEntityType type;

    @DatabaseField(columnName = "PRICE_DIFF")
    private BigDecimal priceDiff;

    /**
     * Required by ORMLite to be able to query the foreign
     * collection {@link ItemEntity#getOptions()}.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private ItemEntity item;

     /*
     *****************************************************************************************************************
     * Checkbox option properties
     *****************************************************************************************************************
     */

    @DatabaseField(columnName = "DEFAULT_CHECKED")
    private Boolean defaultChecked;

    /*
     *****************************************************************************************************************
     * Value option properties
     *****************************************************************************************************************
     */

    @DatabaseField(columnName = "DEFAULT_VALUE")
    private Integer defaultValue;

     /*
     *****************************************************************************************************************
     * Radio option properties
     *****************************************************************************************************************
     */

    @ForeignCollectionField(eager = true)
    private Collection<RadioItemEntity> radioItemEntities;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private RadioItemEntity defaultSelectedItem;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public OptionEntityType getType() {
        return type;
    }

    public void setType(OptionEntityType type) {
        this.type = type;
    }

    @Override
    public BigDecimal getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(BigDecimal priceDiff) {
        this.priceDiff = priceDiff;
    }

    @Override
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @Override
    public Boolean getDefaultChecked() {
        return defaultChecked;
    }

    public void setDefaultChecked(Boolean defaultChecked) {
        this.defaultChecked = defaultChecked;
    }

    @Override
    public Integer getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public Collection<RadioItemEntity> getRadioItemEntities() {
        return radioItemEntities;
    }

    @Override
    public void setRadioItemEntities(Collection<RadioItemEntity> radioItemEntities) {
        this.radioItemEntities = radioItemEntities;
    }

    @Override
    public RadioItemEntity getDefaultSelectedItem() {
        return defaultSelectedItem;
    }

    @Override
    public void setDefaultSelectedItem(RadioItemEntity defaultSelectedItem) {
        this.defaultSelectedItem = defaultSelectedItem;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("title", title)
                .append("index", index)
                .append("type", type)
                .append("priceDiff", priceDiff)
                .append("item", item)
                .append("defaultChecked", defaultChecked)
                .append("defaultValue", defaultValue)
                .append("radioItemEntities.size()", CollectionUtils.size(radioItemEntities))
                .append("defaultSelectedItem.title", defaultSelectedItem != null ? defaultSelectedItem.getTitle() : "--")
                .toString();
    }
}
