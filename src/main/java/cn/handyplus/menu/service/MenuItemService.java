package cn.handyplus.menu.service;

import cn.handyplus.lib.db.Db;
import cn.handyplus.menu.enter.MenuItem;

import java.util.List;
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
        Optional<MenuItem> menuItemOpt = this.findByItem(menuItem.getItemStack());
        if (menuItemOpt.isPresent()) {
            return menuItemOpt.get().getId();
        }
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

    /**
     * 根据item查询
     *
     * @param item 物品
     * @return 成功
     * @since 1.6.6
     */
    public Optional<MenuItem> findByItem(String item) {
        Db<MenuItem> use = Db.use(MenuItem.class);
        use.where().eq(MenuItem::getItemStack, item);
        return use.execution().selectOne();
    }

    /**
     * 查询全部
     *
     * @return list
     * @since 1.6.6
     */
    public List<MenuItem> findAll() {
        return Db.use(MenuItem.class).execution().list();
    }

    /**
     * 更新物品
     *
     * @param itemStack 物品
     * @param id        ID
     * @since 1.6.6
     */
    public void updateItemStack(String itemStack, Integer id) {
        Db<MenuItem> use = Db.use(MenuItem.class);
        use.update().set(MenuItem::getItemStack, itemStack);
        use.execution().updateById(id);
    }

}