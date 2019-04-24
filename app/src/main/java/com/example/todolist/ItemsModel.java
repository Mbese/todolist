package com.example.todolist;

class ItemsModel {

    private String itemDescription;
    private boolean isCompleted;

    String getItemDescription() {
        return itemDescription;
    }

    void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    boolean isCompleted() {
        return isCompleted;
    }

    void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
