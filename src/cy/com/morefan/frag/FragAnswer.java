package cy.com.morefan.frag;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonSyntaxException;
import com.sina.weibo.sdk.utils.LogUtil;

import cy.com.morefan.bean.FMTaskDetail;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.ui.user.LoginActivity;
import cy.com.morefan.util.HttpUtil;
import cy.com.morefan.util.JSONUtil;
import cy.com.morefan.util.ObtainParamsMap;
import cy.com.morefan.util.ToastUtils;


/**
 *答题类/问卷类
 * @类名称：FragAnswer
 * @类描述：答题类/问卷类
 * @创建人：jinxiangdong
 * @修改人：
 * @修改时间：2015年5月27日 下午7:00:59
 * @修改备注：
 * @version:
 */

public class FragAnswer extends FragAnswerBase {

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onFragPasue() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == android.app.Activity.RESULT_OK) {
			// new TaskDetailAsyncTask().execute(taskData.getTaskId());
			startNextFragment();
		}
	}

	@Override
	protected void startNextFragment() {
		BaseFragment frag = new FragAnswerList();
		Bundle bd = new Bundle();
		bd.putSerializable("questions", (Serializable) taskData.getQuestions());
		bd.putInt("taskid", taskData.getTaskId());
		frag.setArguments(bd);
		((AnswerActivity) getActivity()).switchFragment(frag, "answerlist");
	}

	@Override
	public void onClick(View view) {
		saveReadTask(taskData.getTaskId());

		if (view == btnAnswer) {
			if (false == app.isLogin(getActivity())) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivityForResult(intent, 1000);
			} else {
				startNextFragment();
			}
		}
	}
}
