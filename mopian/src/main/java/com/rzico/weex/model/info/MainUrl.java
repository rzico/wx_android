package com.rzico.weex.model.info;

/**
 * Created by Jinlesoft on 2017/11/18.
 * 主页导航路由路径
 */

public class MainUrl extends BaseEntity{

    private data data;

    public MainUrl.data getData() {
        return data;
    }

    public void setData(MainUrl.data data) {
        this.data = data;
    }

    public class data{
        private tabnav tabnav;

        public MainUrl.data.tabnav getTabnav() {
            return tabnav;
        }

        public void setTabnav(MainUrl.data.tabnav tabnav) {
            this.tabnav = tabnav;
        }

        public class tabnav{
            private String member;
            private String message;
            private String home;
            private String friend;
            private String add;

            public String getMember() {
                return member;
            }

            public void setMember(String member) {
                this.member = member;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public String getHome() {
                return home;
            }

            public void setHome(String home) {
                this.home = home;
            }

            public String getFriend() {
                return friend;
            }

            public void setFriend(String friend) {
                this.friend = friend;
            }

            public String getAdd() {
                return add;
            }

            public void setAdd(String add) {
                this.add = add;
            }
        }
    }
}
