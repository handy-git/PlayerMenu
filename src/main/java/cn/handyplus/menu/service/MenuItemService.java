package cn.handyplus.menu.service;

import cn.handyplus.lib.core.SecureUtil;
import cn.handyplus.lib.db.Db;
import cn.handyplus.lib.db.Page;
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
        String md5 = SecureUtil.md5Str(menuItem.getItemStack());
        menuItem.setMd5(md5);
        Optional<MenuItem> menuItemOpt = this.findByMd5(md5);
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
     * @param md5 md5
     * @return 成功
     * @since 1.6.6
     */
    public Optional<MenuItem> findByMd5(String md5) {
        Db<MenuItem> use = Db.use(MenuItem.class);
        use.where().eq(MenuItem::getMd5, md5);
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
        use.update().set(MenuItem::getItemStack, itemStack)
                .set(MenuItem::getMd5, SecureUtil.md5Str(itemStack));
        use.execution().updateById(id);
    }

    /**
     * 分页查询
     *
     * @param pageNum  页数
     * @param pageSize 条数
     * @return MenuItem
     * @since 1.7.7
     */
    public Page<MenuItem> page(Integer pageNum, Integer pageSize) {
        Db<MenuItem> db = Db.use(MenuItem.class);
        db.where().limit(pageNum, pageSize).orderByDesc(MenuItem::getId);
        return db.execution().page();
    }

    /**
     * 根据id删除
     *
     * @param id ID
     * @since 1.7.7
     */
    public void delById(Integer id) {
        Db.use(MenuItem.class).execution().deleteById(id);
    }

}