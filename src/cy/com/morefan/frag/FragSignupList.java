package cy.com.morefan.frag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import cy.com.morefan.MyApplication;
import cy.com.morefan.R;
import cy.com.morefan.bean.Question;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.util.DensityUtils;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;

public class FragSignupList extends BaseFragment implements OnClickListener
{
    Button btnNext = null;

    TextView txtKey = null;

    EditText etValue = null;

    List<Question> questions = null;

    Question currentQuestion = null;

    int currentIdx = 0;

    Map<Integer, String> answers = null;

    View rootView = null;

    TextView tvSignBg = null;

    RelativeLayout rlSignUp = null;

    ScrollView svSignup = null;
    
    LinearLayout llSignlist=null;

    int taskid = 0;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.frag_signuplist, container, false);

        Bundle bd = this.getArguments();
        if (bd.containsKey("questions"))
        {
            questions = (List<Question>) bd.getSerializable("questions");
        }
        if (bd.containsKey("taskid"))
        {
            taskid = bd.getInt("taskid");
        }

        llSignlist = (LinearLayout)rootView.findViewById(R.id.llSignlist);
        loadControls();
        btnNext = (Button) rootView.findViewById(R.id.btnSignupNext);
        btnNext.setOnClickListener(this);
        btnNext.setVisibility(View.GONE);
        
        tvSignBg = (TextView) rootView.findViewById(R.id.tvsignbg);
        rlSignUp = (RelativeLayout) rootView.findViewById(R.id.rlSignup);
        svSignup = (ScrollView) rootView.findViewById(R.id.svsignup);
        svSignup.setVerticalScrollBarEnabled(false);
                
        
        if (questions == null || questions.size() < 1)
            return rootView;

        btnNext.setVisibility(View.VISIBLE);
        answers = new HashMap<Integer, String>();
        currentIdx = 0;
        currentQuestion = questions.get(currentIdx);
        
        //txtKey = (TextView) rootView.findViewById(R.id.txtKey);
        //txtKey.setText(currentQuestion.getFieldName());
        //etValue = (EditText) rootView.findViewById(R.id.etValue);
        //MyApplication application = ((BaseActivity) getActivity()).application;
        //if (application.personal != null)
        //{
            //etValue.setText(application.personal.getMobile());
        //}
       
        return rootView;
    }
    
    private void loadControls(){
        for( Question item : questions){
            EditText et=new EditText(getActivity());
            android.widget.LinearLayout.LayoutParams params =new  android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 30 , 30, 0);
            et.setPadding(5, 5, 5, 5);
            et.setLayoutParams(params);
            et.setTextSize(18);
            et.setGravity(Gravity.CENTER);
            et.setHint(item.getFieldName());
            et.setSingleLine(true);
            if( item.getFieldName().equals("姓名"))
            {                
                et.setText(MyApplication.readString(getActivity(), Constant.LOGIN_USER_INFO, Constant.LOGIN_USER_ACCOUNT));
            }else if( item.getFieldName().equals("手机"))
            {
                et.setText(MyApplication.readString(getActivity(), Constant.LOGIN_USER_INFO , Constant.LOGIN_USER_NAME));
            }            
            et.setBackgroundResource(R.drawable.shape_square_red);
            et.setTag( item );           
            
            llSignlist.addView(et);
        }        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        int height = DensityUtils.getScreenH(getActivity());
        int lheight = (int) (0.36 * height);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        // tvSignBg.setLayoutParams(params);
        params.setMargins(0, lheight, 0, 0);
        rlSignUp.setLayoutParams(params);

        RelativeLayout rltitile = (RelativeLayout) getActivity().findViewById(
                R.id.rlTitle);
        int tHeight = height - rltitile.getHeight();

        params = new LayoutParams(LayoutParams.MATCH_PARENT, tHeight);
        tvSignBg.setLayoutParams(params);
    }

    @Override
    public void onReshow()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFragPasue()
    {
        // TODO Auto-generated method stub

    }

    /**
     * 通过正则表达式 验证输入的内容
     *
     * @方法描述：
     * @方法名：check
     * @参数：@return
     * @返回：boolean
     * @exception
     * @since
     */
    private boolean check()
    {
        String value = etValue.getText().toString();
        if (currentQuestion != null
                && currentQuestion.getFieldPattern() != null)
        {
            Pattern pattern = Pattern
                    .compile(currentQuestion.getFieldPattern());
            Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        } else
        {
            return true;
        }
    }
    
    private void nextStep(){
        int count = llSignlist.getChildCount();
        boolean isok = true;
        String answerString = "";
        for( int i=0;i<count;i++){
            EditText et = (EditText)llSignlist.getChildAt(i);
            Question item = (Question)et.getTag();
            String key = String.valueOf(item.getQid()); //item.getFieldName();
            String context = et.getText().toString();
            if( item.getFieldPattern()!=null){
                String exp = item.getFieldPattern();
                Pattern pattern = Pattern.compile(exp );
                Matcher matcher = pattern.matcher(context);
                boolean isPass = matcher.matches();
                if( isPass==false){
                    isok = false;
                    et.setHint("请输入正确的内容");
                }
            }
            
            if (answerString.length() > 0)
            {
                answerString += "|";
            }
            answerString += key + ":" + context;           
            //Log.i("answer", answerString);
        }
        
        if(isok ==false){
            return;
        }
        
        
        BaseFragment frag = new FragAnswerPass();

        Bundle bd = new Bundle();
        bd.putInt("taskid", taskid);
        bd.putInt("rightcount", questions.size() );
        bd.putInt("wrongcount", 0);        
        bd.putString("answers", answerString);
        frag.setArguments(bd);

        ((AnswerActivity) getActivity()).switchFragment(frag, "answerpass");
        
    }

    @Override
    public void onClick(View view)
    {
        if( false == Util.isConnect(getActivity())){
            ToastUtils.showLongToast(getActivity(), "无法连接网络,请检查网络设置");
            return;
        }    
        
        // TODO Auto-generated method stub
        if (view == btnNext)
        {
            
            nextStep();

//            if (false == check())
//            {
//                etValue.setBackgroundResource(R.drawable.shape_square_red);
//                return;
//            }
//
//            String txt = etValue.getText().toString().trim();
//            answers.put(currentQuestion.getQid(), txt);
//
//            if (currentIdx < (questions.size()-1))
//            {
//                currentIdx++;
//                currentQuestion = questions.get(currentIdx);
//                txtKey.setText(currentQuestion.getFieldName());
//                etValue.setText("");
//                return;
//            }

//            BaseFragment frag = new FragAnswerPass();
//
//            Bundle bd = new Bundle();
//            bd.putInt("taskid", taskid);
//            bd.putInt("rightcount", 1);
//            bd.putInt("wrongcount", 0);
//
//            String answerString = "";
//            for (Map.Entry<Integer, String> entry : answers.entrySet())
//            {
//                String key = entry.getKey().toString();
//                String value = entry.getValue().toString();
//
//                if (answerString.length() > 0)
//                {
//                    answerString += "|";
//                }
//
//                answerString += key + ":" + value;
//                Log.i("answer", answerString);
//            }
//
//            bd.putString("answers", answerString);
//            frag.setArguments(bd);
//
//            ((AnswerActivity) getActivity()).SwitchFragment(frag, "answerpass");
        }
    }

}
