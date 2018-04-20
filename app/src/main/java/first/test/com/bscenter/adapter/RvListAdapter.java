package first.test.com.bscenter.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.File;
import java.util.List;

import first.test.com.bscenter.R;
import first.test.com.bscenter.model.FileParent;
import first.test.com.bscenter.model.FileSon;
import first.test.com.bscenter.utils.CommonUtil;
import first.test.com.bscenter.utils.OpenFileUtil;
import first.test.com.bscenter.utils.TextUtil;

/**
 * Created by Admin on 2018/4/20.
 */

public class RvListAdapter  extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public RvListAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.activity_bx_parent);
        addItemType(TYPE_LEVEL_1, R.layout.activity_bx_son);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                final FileParent parent = (FileParent) item;
                helper.setText(R.id.tvType,parent.getName());
                helper.setText(R.id.tvNumber, CommonUtil.isStrEmpty(parent.getExtra())?"0个":parent.getExtra()+"个");
                helper.setText(R.id.tvMuber, TextUtil.getSizeSting(parent.getSize()));
                final ImageView ivRight = helper.getView(R.id.ivRight);
//                expand(0);
                if (parent.isExpanded()){
                    ivRight.setBackgroundResource(R.mipmap.iv_up_gray2);
                }else {
                    ivRight.setBackgroundResource(R.mipmap.iv_down_gray2);
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = helper.getAdapterPosition();
                        if (parent.isExpanded()){
                            collapse(position);
                            ivRight.setBackgroundResource(R.mipmap.iv_down_gray2);
                        }else {
                            expand(position);
                            ivRight.setBackgroundResource(R.mipmap.iv_up_gray2);
                        }
                    }
                });
                break;
            case TYPE_LEVEL_1:
                final FileSon son = (FileSon) item;
                helper.setText(R.id.mTvFavoriteName,son.getName());
                helper.setText(R.id.mTvFavoritePath,son.getCanonicalPath());
                helper.setText(R.id.mTvFavoriteTime,son.getFavoriteTime()+"");
                helper.setText(R.id.mTvFavoriteSize,TextUtil.getSizeSting(son.getSize()));
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = son.getCanonicalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Intent intent = OpenFileUtil.openFile(path);
                            helper.itemView.getContext().startActivity(intent);
                        }

                    }
                });
                break;
        }
    }
}
