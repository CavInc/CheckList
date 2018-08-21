package tk.cavinc.checklist.data.models;

/**
 * Created by cav on 21.08.18.
 */

public class ArhiveItemCommentModel {
    private String mGroupTitle;
    private String mTitle;
    private String mComment;
    private int mGroupId;
    private int mId;

    public ArhiveItemCommentModel(String groupTitle, String title, String comment, int groupId, int id) {
        mGroupTitle = groupTitle;
        mTitle = title;
        mComment = comment;
        mGroupId = groupId;
        mId = id;
    }

    public ArhiveItemCommentModel(String groupTitle, String title, String comment) {
        mGroupTitle = groupTitle;
        mTitle = title;
        mComment = comment;
    }

    public String getGroupTitle() {
        return mGroupTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getComment() {
        return mComment;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public int getId() {
        return mId;
    }
}
