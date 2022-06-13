package kr.archive.main.database;

public class User {
    String _id;
    String id;
    String email;
    String accessToken;
    String token;
    String refreshToken;
    String naver_accessToken;
    String naver_refreshToken;
    String naver_email;
    String naver_name;
    String google_accessToken;
    String kakao_accessToken;
    String kakao_email;
    String kakao_id;
    String kakao_name;
    String kakao_refreshToken;
    String minecraft_id;
    int battlebot_flags;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getNaver_accessToken() {
        return naver_accessToken;
    }

    public void setNaver_accessToken(String naver_accessToken) {
        this.naver_accessToken = naver_accessToken;
    }

    public String getNaver_refreshToken() {
        return naver_refreshToken;
    }

    public void setNaver_refreshToken(String naver_refreshToken) {
        this.naver_refreshToken = naver_refreshToken;
    }

    public String getNaver_email() {
        return naver_email;
    }

    public void setNaver_email(String naver_email) {
        this.naver_email = naver_email;
    }

    public String getNaver_name() {
        return naver_name;
    }

    public void setNaver_name(String naver_name) {
        this.naver_name = naver_name;
    }

    public String getGoogle_accessToken() {
        return google_accessToken;
    }

    public void setGoogle_accessToken(String google_accessToken) {
        this.google_accessToken = google_accessToken;
    }

    public String getKakao_accessToken() {
        return kakao_accessToken;
    }

    public void setKakao_accessToken(String kakao_accessToken) {
        this.kakao_accessToken = kakao_accessToken;
    }

    public String getKakao_email() {
        return kakao_email;
    }

    public void setKakao_email(String kakao_email) {
        this.kakao_email = kakao_email;
    }

    public String getKakao_id() {
        return kakao_id;
    }

    public void setKakao_id(String kakao_id) {
        this.kakao_id = kakao_id;
    }

    public String getKakao_name() {
        return kakao_name;
    }

    public void setKakao_name(String kakao_name) {
        this.kakao_name = kakao_name;
    }

    public String getKakao_refreshToken() {
        return kakao_refreshToken;
    }

    public void setKakao_refreshToken(String kakao_refreshToken) {
        this.kakao_refreshToken = kakao_refreshToken;
    }

    public int getBattlebot_flags() {
        return battlebot_flags;
    }

    public void setBattlebot_flags(int battlebot_flags) {
        this.battlebot_flags = battlebot_flags;
    }

    public String getMinecraft_id() {
        return minecraft_id;
    }

    public void setMinecraft_id(String minecraft_id) {
        this.minecraft_id = minecraft_id;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", naver_accessToken='" + naver_accessToken + '\'' +
                ", naver_refreshToken='" + naver_refreshToken + '\'' +
                ", naver_email='" + naver_email + '\'' +
                ", naver_name='" + naver_name + '\'' +
                ", google_accessToken='" + google_accessToken + '\'' +
                ", kakao_accessToken='" + kakao_accessToken + '\'' +
                ", kakao_email='" + kakao_email + '\'' +
                ", kakao_id='" + kakao_id + '\'' +
                ", kakao_name='" + kakao_name + '\'' +
                ", kakao_refreshToken='" + kakao_refreshToken + '\'' +
                ", minecraft_id='" + minecraft_id + '\'' +
                ", battlebot_flags=" + battlebot_flags +
                '}';
    }
}
