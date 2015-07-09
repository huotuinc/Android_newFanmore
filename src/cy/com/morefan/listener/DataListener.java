package cy.com.morefan.listener;

import android.os.Bundle;
import cy.com.morefan.bean.IBaseData;


public interface DataListener {
	void onDataFinish(int type, String msg, Bundle extra, IBaseData... data);
	void onDataFail(int type, String msg, Bundle extra);


	public static final int DONE_UNIT_LIST = 1000;
	public static final int DONE_MARK	   = 1001;

	public static final int ERROR_UNIT_LIST = -1000;
	public static final int ERROR_MARK	    = -1001;
	//handler 更新title
	public static final int IS_SHARE = 1;
	public static final int IS_BUY = 2;
	public static final int IS_FLUSH = 3;
	public static final int LOGINOUT_FLUSH = 4;
	public static final int IS_HOME = 5;
	public static final int IS_USER = 6;
	public static final int IS_PRE = 7;
	public static final int IS_MASTER = 8;
	public static final int IS_MORE = 9;
	public static final int IS_MARK = 10;
	public static final int IS_FAQS = 11;

}
