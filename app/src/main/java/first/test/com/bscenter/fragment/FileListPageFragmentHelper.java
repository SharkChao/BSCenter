package first.test.com.bscenter.fragment;

import java.util.List;

import first.test.com.bscenter.model.SimpleFileInfo;
import first.test.com.bscenter.views.FileListBottomOperatorMenu;


/**
 * FileListPageFragment 的辅助类
 * 
 * @author Administrator
 * 
 */
public class FileListPageFragmentHelper {

    public static int hideShowBottomOperatorMenu(List<String> list, FileListBottomOperatorMenu view) {
        int count = 0;
        if (list.size() > 0) {
            view.show();
        } else {
            view.hide();
        }
        return count;
    }
    
    public static void refreshCheckList(List<SimpleFileInfo> infos, List<String> checkItems) {
        checkItems.clear();
        for (SimpleFileInfo info : infos) {
            if(info.isChecked()) {
                checkItems.add(info.getPath());
            }
        }
    }
    
}
