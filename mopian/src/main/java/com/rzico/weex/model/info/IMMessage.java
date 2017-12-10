package com.rzico.weex.model.info;

/**
 * Created by Jinlesoft on 2017/10/24.
 */

public class IMMessage {
    private String content;
    private String id;
    private String nickName;
    private String logo;
    private boolean isDraft;
    private long unRead;
    private long createDate;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getUnRead() {
        return unRead;
    }

    public void setUnRead(long unRead) {
        this.unRead = unRead;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }
}
