package com.example.brhee.allergysnap;

public abstract class UserItem implements Comparable<UserItem> {
    protected long timeAdded;
    protected String name, info;
    protected int id;
    protected String url;

    @Override
    public int compareTo(UserItem candidate) {
        return Long.compare(candidate.timeAdded, this.timeAdded);
    }
}
