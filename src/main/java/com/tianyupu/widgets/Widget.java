package com.tianyupu.widgets;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Widget {
    private final AtomicInteger x;
    private final AtomicInteger y;
    private final AtomicInteger width;
    private final AtomicInteger height;
    private final AtomicInteger zIndex;
    private final AtomicReference<Date> lastModified;
    private final long id;

    public Widget(int x, int y, int width, int height, int zIndex, long id) {
        this.x = new AtomicInteger(x);
        this.y = new AtomicInteger(y);
        this.width = new AtomicInteger(width);
        this.height = new AtomicInteger(height);
        this.zIndex = new AtomicInteger(zIndex);
        this.lastModified = new AtomicReference<>(Date.from(Instant.now()));
        this.id = id;
    }

    public int getX() {
        return x.get();
    }

    public void setX(int x) {
        if (this.x.compareAndSet(getX(), x)) {
            updateLastModifiedTime();
        }
    }

    public int getY() {
        return y.get();
    }

    public void setY(int y) {
        if (this.y.compareAndSet(getY(), y)) {
            updateLastModifiedTime();
        }
    }

    public int getWidth() {
        return width.get();
    }

    public void setWidth(int width) {
        if (this.width.compareAndSet(getWidth(), width)) {
            updateLastModifiedTime();
        }
    }

    public int getHeight() {
        return height.get();
    }

    public void setHeight(int height) {
        if (this.height.compareAndSet(getHeight(), height)) {
            updateLastModifiedTime();
        }
    }

    public int getZIndex() {
        return zIndex.get();
    }

    public void setZIndex(int zIndex) {
        if (this.zIndex.compareAndSet(getZIndex(), zIndex)) {
            updateLastModifiedTime();
        }
    }

    public Date getLastModified() {
        return lastModified.get();
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Widget widget = (Widget) o;

        if (id != widget.id) return false;
        if (x != null ? !x.equals(widget.x) : widget.x != null) return false;
        if (y != null ? !y.equals(widget.y) : widget.y != null) return false;
        if (width != null ? !width.equals(widget.width) : widget.width != null) return false;
        if (height != null ? !height.equals(widget.height) : widget.height != null) return false;
        if (zIndex != null ? !zIndex.equals(widget.zIndex) : widget.zIndex != null) return false;
        return lastModified != null ? lastModified.equals(widget.lastModified) : widget.lastModified == null;
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (width != null ? width.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (zIndex != null ? zIndex.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", zIndex=" + zIndex +
                ", lastModified=" + lastModified +
                ", id=" + id +
                '}';
    }

    private void updateLastModifiedTime() {
        lastModified.compareAndSet(getLastModified(), Date.from(Instant.now()));
    }
}
