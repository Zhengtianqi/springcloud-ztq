package com.ztq.test.tree;

import com.alibaba.fastjson.JSON;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MenuTreeTest {


    public static void main(String[] args) {
        List<Menu> allMenuList = getMenu();
        List<Menu> menus = new ArrayList<>(6);
        // 先判断是一级菜单
        for (Menu menuItem : allMenuList) {
            if (0 == menuItem.getPid()) {
                // 找到一级菜单的一级菜单
                List<Menu> myChilds = getChildren(allMenuList, menuItem);
                menuItem.setChildren(myChilds);
                menus.add(menuItem);
            }
        }

        System.out.println(JSON.toJSONString(menus));

    }

    public static List<Menu> getChildren(List<Menu> allMenuList, Menu parentMenu) {
        List<Menu> childrenList = new ArrayList<>(6);
        for (Menu menuItem : allMenuList) {
            if (menuItem.getPid() == parentMenu.getId()) {
                // 递归
                List<Menu> myChilds = getChildren(allMenuList, menuItem);
                menuItem.setChildren(myChilds.isEmpty() ? null : myChilds);
                childrenList.add(menuItem);
            }
        }
        return childrenList;
    }

    public static List<Menu> getMenu() {
        Menu menu1 = new Menu(1, 0, "一级菜单1", 1);
        Menu menu2 = new Menu(2, 0, "一级菜单2", 2);
        Menu menu3 = new Menu(3, 0, "一级菜单3", 3);
        Menu menu4 = new Menu(4, 1, "二级菜单1", 1);
        Menu menu5 = new Menu(5, 1, "二级菜单2", 2);
        Menu menu6 = new Menu(6, 4, "三级菜单", 1);

        List<Menu> MenuList = new ArrayList<>(6);
        Collections.addAll(MenuList, menu1, menu2, menu3, menu4, menu5, menu6);
        return MenuList;
    }


    public static class Menu implements Serializable, Comparable<Menu> {

        private static final long serialVersionUID = 1L;

        private int id;

        private int pid;

        private String name;

        private int sort;

        private List<Menu> children;

        public Menu(int id, int pid, String name, int sort) {
            this.id = id;
            this.pid = pid;
            this.name = name;
            this.sort = sort;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public List<Menu> getChildren() {
            return children;
        }

        public void setChildren(List<Menu> children) {
            this.children = children;
        }

        @Override
        public int compareTo(Menu o) {
            return this.getSort() - o.getSort();
        }
    }


}
