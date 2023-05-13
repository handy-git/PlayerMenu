package cn.handyplus.menu.service;

import cn.handyplus.lib.db.Db;
import cn.handyplus.menu.enter.MenuLimit;

/**
 * 菜单点击限制
 *
 * @author handy
 */
public class MenuLimitService {
    private MenuLimitService() {
    }

    private static class SingletonHolder {
        private static final MenuLimitService INSTANCE = new MenuLimitService();
    }

    public static MenuLimitService getInstance() {
        return MenuLimitService.SingletonHolder.INSTANCE;
    }

    /**
     * 新增
     *
     * @param menuLimit 入参
     * @return 成功
     */
    public int add(MenuLimit menuLimit) {
        return Db.use(MenuLimit.class).execution().insert(menuLimit);
    }

    /**
     * 设置
     *
     * @param menuLimit 入参
     */
    public void set(MenuLimit menuLimit) {
        MenuLimit limit = this.findByPlayerName(menuLimit.getPlayerName(), menuLimit.getMenuItemId());
        if (limit == null) {
            this.add(menuLimit);
            return;
        }
        this.addNumberById(limit.getId());
    }

    /**
     * 根据id增加
     *
     * @param id id
     */
    public void addNumberById(Integer id) {
        Db<MenuLimit> use = Db.use(MenuLimit.class);
        use.update().add(MenuLimit::getNumber, MenuLimit::getNumber, 1);
        use.execution().updateById(id);
    }

    /**
     * 根据name查询
     *
     * @param playerName item
     * @return MenuLimit
     */
    public MenuLimit findByPlayerName(String playerName, Integer menuItemId) {
        Db<MenuLimit> use = Db.use(MenuLimit.class);
        use.where().eq(MenuLimit::getPlayerName, playerName)
                .eq(MenuLimit::getMenuItemId, menuItemId);
        return use.execution().selectOne();
    }

    /**
     * 根据name查询
     *
     * @param playerName item
     * @return MenuLimit
     */
    public Integer findCountByPlayerName(String playerName, Integer menuItemId) {
        if (menuItemId == null || menuItemId < 1) {
            return 0;
        }
        MenuLimit menuLimit = this.findByPlayerName(playerName, menuItemId);
        if (menuLimit == null) {
            return 0;
        }
        return menuLimit.getNumber();
    }

}