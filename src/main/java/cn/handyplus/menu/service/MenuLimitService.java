package cn.handyplus.menu.service;

import cn.handyplus.lib.core.CollUtil;
import cn.handyplus.lib.db.Db;
import cn.handyplus.menu.enter.MenuLimit;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
     */
    public void add(MenuLimit menuLimit) {
        Db.use(MenuLimit.class).execution().insert(menuLimit);
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
        this.setClickTimeById(limit.getId(), new Date());
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
        if (menuItemId == null) {
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
        if (menuItemId == null) {
            return null;
        }
        Optional<MenuLimit> menuLimitOptional = this.findByPlayerUuid(playerUuid, menuItemId);
        return menuLimitOptional.map(MenuLimit::getClickTime).orElse(null);
    }

    /**
     * 根据菜单id清理数据
     * @param menuItemIds 菜单id
     * @since 1.5.8
     */
    public void deleteByMenuItemIds(List<Integer> menuItemIds) {
        if (CollUtil.isEmpty(menuItemIds)) {
            return;
        }
        Db<MenuLimit> use = Db.use(MenuLimit.class);
        use.where().in(MenuLimit::getMenuItemId, menuItemIds);
        use.execution().delete();
    }

    /**
     * 查询当前全部菜单id
     * @return 菜单id
     * @since 1.5.8
     */
    public List<String> selectMenuItemIds() {
        Db<MenuLimit> use = Db.use(MenuLimit.class);
        use.select(MenuLimit::getMenuItemId);
        use.where().groupBy(MenuLimit::getMenuItemId);
        List<MenuLimit> list = use.execution().list();
        return list.stream().map(menuLimit -> String.valueOf(menuLimit.getMenuItemId())).collect(Collectors.toList());
    }

}