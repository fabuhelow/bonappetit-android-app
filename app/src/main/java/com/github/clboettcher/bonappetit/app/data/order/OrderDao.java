package com.github.clboettcher.bonappetit.app.data.order;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

public class OrderDao {

    private static final String TAG = OrderDao.class.getName();
    private RuntimeExceptionDao<ItemOrderEntity, Long> itemOrderDao;
    private OptionOrderDao optionOrderDao;
    private BonAppetitDbHelper bonAppetitDbHelper;

    @Inject
    public OrderDao(BonAppetitDbHelper bonAppetitDbHelper,
                    OptionOrderDao optionOrderDao) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.itemOrderDao = bonAppetitDbHelper
                .getRuntimeExceptionDao(ItemOrderEntity.class);
        this.optionOrderDao = optionOrderDao;
    }

    public void save(ItemOrderEntity itemOrderEntity) {
        Log.i(TAG, String.format("Saving order %s", itemOrderEntity));

        itemOrderDao.create(itemOrderEntity);
        optionOrderDao.save(itemOrderEntity.getOptionOrderEntities());
    }

    public void update(ItemOrderEntity itemOrder) {
        Log.i(TAG, String.format("Updating order %s", itemOrder));
        itemOrderDao.update(itemOrder);
    }

    public ItemOrderEntity get(Long orderId) {
        return itemOrderDao.queryForId(orderId);
    }

    public List<ItemOrderEntity> list() {
        Log.i(TAG, "Returning all stored orders.");
        return itemOrderDao.queryForAll();
    }

    public long count() {
        return itemOrderDao.countOf();
    }

    public void delete(ItemOrderEntity itemOrder) {
        if (CollectionUtils.isNotEmpty(itemOrder.getOptionOrderEntities())) {
            optionOrderDao.delete(itemOrder.getOptionOrderEntities());
        }
        itemOrderDao.delete(itemOrder);
    }

    public void deleteAll() throws SQLException {
        optionOrderDao.deleteAll();
        TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), ItemOrderEntity.class);
    }
}
