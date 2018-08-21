package tk.cavinc.checklist.data.models;

import java.util.ArrayList;

/**
 * Created by cav on 21.08.18.
 */

public class ArhiveDocModel {
    private ArrayList<ArhiveHeadModel> mArhive;
    private ArrayList<ArhiveItemCommentModel> mCommentModels;

    public ArhiveDocModel(ArrayList<ArhiveHeadModel> arhive, ArrayList<ArhiveItemCommentModel> commentModels) {
        mArhive = arhive;
        mCommentModels = commentModels;
    }

    public ArrayList<ArhiveHeadModel> getArhive() {
        return mArhive;
    }

    public ArrayList<ArhiveItemCommentModel> getCommentModels() {
        return mCommentModels;
    }
}
