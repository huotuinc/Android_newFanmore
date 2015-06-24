package cy.com.morefan.util;

import cy.com.morefan.view.WheelView;

public interface OnWheelClickedListener
{

    /**
     * Callback method to be invoked when current item clicked
     * @param wheel the wheel view
     * @param itemIndex the index of clicked item
     */
    void onItemClicked(WheelView wheel, int itemIndex);
}
