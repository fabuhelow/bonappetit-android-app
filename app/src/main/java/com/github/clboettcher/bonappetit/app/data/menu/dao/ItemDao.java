package com.github.clboettcher.bonappetit.app.data.menu.dao;


import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Data access facade for {@link ItemEntity}.
 */
public class ItemDao {

    private static final String TAG = ItemDao.class.getName();
    private RuntimeExceptionDao<ItemEntity, Long> dao;
    private BonAppetitDbHelper bonAppetitDbHelper;
    private OptionDao optionDao;

    @Inject
    public ItemDao(BonAppetitDbHelper bonAppetitDbHelper, OptionDao optionDao) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.optionDao = optionDao;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(ItemEntity.class);
    }

    public void save(Collection<ItemEntity> items) {
        Preconditions.checkNotNull(items, "items");

        for (ItemEntity item : items) {
            Log.i(TAG, String.format("Saving item %s to database", item));
            if (item.hasOptions()) {
                optionDao.save(item.getOptions());
            }
            dao.create(item);
        }
    }

    public List<ItemEntity> list() {
        return dao.queryForAll();
    }

    public int deleteAll() throws SQLException {
        return TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), ItemEntity.class);
    }

    public long count() {
        return dao.countOf();
    }

    public Optional<ItemEntity> get(Long itemId) {
        return Optional.fromNullable(dao.queryForId(itemId));

    }
}