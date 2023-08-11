package cn.handyplus.menu.service;

import cn.handyplus.lib.db.Db;
import cn.handyplus.menu.enter.MenuLimit;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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
     * 设置点击时间
     *
     * @param menuLimit 入参
     */
    public void setClickTimeById(MenuLimit menuLimit) {
        Optional<MenuLimit> limitOptional = this.findByPlayerUuid(UUID.fromString(menuLimit.getPlayerUuid()), menuLimit.getMenuItemId());
        if (!limitOptional.isPresent()) {
            this.add(menuLimit);
            return;
        }
        MenuLimit limit = limitOptional.get();
        this.setClickTimeById(limit.getId(), limit.getClickTime());
    }

    /**
     * 设置限制次数
     *
     * @param menuLimit 入参
     */
    public void addNumberById(MenuLimit menuLimit) {
        Optional<MenuLimit> limitOptional = this.findByPlayerUuid(UUID.fromString(menuLimit.getPlayerUuid()), menuLimit.getMenuItemId());
        if (!limitOptional.isPresent()) {
            this.add(menuLimit);
            return;
        }
        this.addNumberById(limitOptional.get().getId());
    }

    /**
     * 根据id增加
     *
     * @param id id
     */
    private void addNumberById(Integer id) {
        Db<MenuLimit> use = Db.use(MenuLimit.class);
        use.update().add(MenuLimit::getNumber, MenuLimit::getNumber, 1);
        use.execution().updateById(id);
    }

    /**
     * 根据id设置
     *
     * @param id        id
     * @param clickTime 点击时间
     */
    private void setClickTimeById(Integer id, Date clickTime) {
        Db<MenuLimit> use = Db.use(MenuLimit.class);
        use.update().set(MenuLimit::getClickTime, clickTime);
        use.execution().updateById(id);
    }

    /**
     * 根据 玩家uid 查询
     *
     * @param playerUuid 玩家uid
     * @param menuItemId 菜单id
     * @return MenuLimit
     */
    public Optional<MenuLimit> findByPlayerUuid(UUID playerUuid, Integer menuItemId) {
        Db<MenuLimit> use = Db.use(MenuLimit.class);
        use.where().eq(MenuLimit::getPlayerUuid, playerUuid)
                .eq(MenuLimit::getMenuItemId, menuItemId);
        return use.execution().selectOne();
    }

    /**
     * 根据uid查询
     *
     * @param playerUuid 玩家uid
     * @param menuItemId 菜单id
     * @return MenuLimit
     */
    public Integer findCountByPlayerUuid(UUID playerUuid, Integer menuItemId) {
        if (menuItemId == null || menuItemId < 1) {
            return 0;
        }
        Optional<MenuLimit> menuLimitOptional = this.findByPlayerUuid(playerUuid, menuItemId);
        if (!menuLimitOptional.isPresent()) {
            return 0;
        }
        return menuLimitOptional.get().getNumber();
    }

    /**
     * 根据uid查询
     *
     * @param playerUuid 玩家uid
     * @param menuItemId 菜单id
     * @return MenuLimit
     */
    public Date findTimeByPlayerUuid(UUID playerUuid, Integer menuItemId) {
        if (menuItemId == null || menuItemId < 1) {
            return null;
        }
        Optional<MenuLimit> menuLimitOptional = this.findByPlayerUuid(playerUuid, menuItemId);
        return menuLimitOptional.map(MenuLimit::getClickTime).orElse(null);
    }

}