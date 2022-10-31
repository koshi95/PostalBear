package com.sanduni.koshila.postalbear.modal;

public class DashBoardItemModal {
    private String item_name;
    private int imageId;

    public DashBoardItemModal(String item_name, int imageId) {
        this.item_name = item_name;
        this.imageId = imageId;
    }

    public String getItemName() {
        return item_name;
    }

    public void setItemName(String item_name) {
        this.item_name = item_name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
