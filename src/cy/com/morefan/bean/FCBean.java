package cy.com.morefan.bean;

/**
 * Created by Administrator on 2015/7/29.
 */
public class FCBean {
    public float getFee() {
        return fee;
    }

    public String getFrom() {
        return from;
    }

    public String getFromName() {
        return fromName;
    }

    public String getFromPicUrl() {
        return fromPicUrl;
    }

    public int getFromSex() {
        return fromSex;
    }

    public String getFromTele() {
        return fromTele;
    }

    public int getInfoId() {
        return infoId;
    }

    public String getMessage() {
        return message;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public void setFromPicUrl(String fromPicUrl) {
        this.fromPicUrl = fromPicUrl;
    }

    public void setFromSex(int fromSex) {
        this.fromSex = fromSex;
    }

    public void setFromTele(String fromTele) {
        this.fromTele = fromTele;
    }

    public void setInfoId(int infoId) {
        this.infoId = infoId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private float fee;
    private String from;
    private String fromName;
    private String fromPicUrl;
    private int fromSex;
    private String fromTele;
    private int infoId;
    private String message;

}
