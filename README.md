## PlayerMenu 一款有点好用的菜单插件

![](https://bstats.org/signatures/bukkit/PlayerMenu.svg)

### 菜单主体配置

1. title 菜单标题
2. openCommand 打开命令
3. openItem 打开物品
4. size 菜单大小
5. menu 菜单节点
6. permission 打开权限

### menu支持节点说明

1. index 坐标
2. name 名称
3. material 材质
4. lore 介绍
5. isEnchant 是否附魔
6. custom-model-data 自定义材质(1.14+)
7. point 需要点券
8. money 需要金币
9. sound 播放声音(1.12+)
10. failSound 不满足条件时候播放的声音(1.12+)
11. limit 限制可以点击几次
12. commands 执行的命令(支持变量)
    * [message] 发送消息
    * [allMessage] 发送消息给全服
    * [title] 发送消息
    * [allTitle] 发送消息给全服 格式 title:subTitle
    * [actionbar] 发送消息
    * [allActionbar] 发送消息给全服
    * [command] 执行命令
    * [op] op身份执行命令(尽量不要使用这个)
    * [Console] 控制台执行命令(推荐使用这个)
    * [close] 关闭菜单
    ```
    例:
    commands:
      - '[message] 你好'
      - '[title] 大标题:小标题'
      - '[actionbar] 你好'
    ```
13. conditions 自定义条件(支持变量)
    * '='
    * '!='
    * '>' 必须数字
    * '<' 必须数字
    * '>=' 必须数字
    * '<=' 必须数字
    ```
    例: 
    # 自定义条件
    conditions:
      - '%player_name% = 米饭'
      - '%player_money% > 100'
    ```
    
## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=handy-git/PlayerMenu&type=Date)](https://star-history.com/#handy-git/PlayerMenu&Date)

