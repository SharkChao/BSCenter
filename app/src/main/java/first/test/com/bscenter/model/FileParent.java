package first.test.com.bscenter.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by Admin on 2018/4/20.
 */

public class FileParent extends AbstractExpandableItem<FileSon> implements MultiItemEntity {

    private String canonicalPath = null;
    private String name = null;
    private String appName = null;
    private int fileType;
    private long favoriteTime = 0;
    private long size = 0;
    private String extra = null;
    private boolean isChecked = false;
    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return 0;
    }

    public String getCanonicalPath() {
        return canonicalPath;
    }

    public void setCanonicalPath(String canonicalPath) {
        this.canonicalPath = canonicalPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(long favoriteTime) {
        this.favoriteTime = favoriteTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
