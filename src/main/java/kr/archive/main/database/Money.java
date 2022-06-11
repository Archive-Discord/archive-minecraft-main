package kr.archive.main.database;

public class Money {
    String _id;
    String userid;
    String date;
    int money;
    String minecraftId;

    public int getMoney() {
        return money;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public String getMinecraftId() {
        return minecraftId;
    }

    public String getUserid() {
        return userid;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMinecraftId(String minecraftId) {
        this.minecraftId = minecraftId;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
