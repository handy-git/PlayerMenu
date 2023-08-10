package cn.handyplus.menu.service;

import cn.handyplus.lib.db.Db;
import cn.handyplus.menu.enter.MenuItem;

import java.util.Optional;

/**
 * 菜单物品
 *
 * @author handy
 */
public class MenuItemService {

    private MenuItemService() {
    }

    private static class SingletonHolder {
        private static final MenuItemService INSTANCE = new MenuItemService();
    }

    public static MenuItemService getInstance() {
        return MenuItemService.SingletonHolder.INSTANCE;
    }

    /**
     * 新增
     *
     * @param menuItem 入参
     * @return 成功
     */
    public int add(MenuItem menuItem) {
        return Db.use(MenuItem.class).execution().insert(menuItem);
    }

    /**
     * 根据id查询
     *
     * @param id id
     * @return 成功
     */
    public Optional<MenuItem> findById(Integer id) {
        return Db.use(MenuItem.class).execution().selectById(id);
    }

}