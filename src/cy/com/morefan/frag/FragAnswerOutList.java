package cy.com.morefan.frag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cy.com.morefan.R;
import cy.com.morefan.bean.Answers;
import cy.com.morefan.bean.Question;
import cy.com.morefan.ui.answer.AnswerActivity;
import cy.com.morefan.util.DensityUtils;
import cy.com.morefan.util.SoundUtil;
import cy.com.morefan.util.ToastUtils;
import cy.com.morefan.util.Util;

public class FragAnswerOutList extends BaseFragment implements OnClickListener
{
    RelativeLayout rlAnsweroutList=null;
    
    List<Question> questions = null;

    List<Integer> seqList = null;

    FrameLayout llA = null;

    FrameLayout llB = null;

    FrameLayout llC = null;

    FrameLayout llD = null;

    Button btnA = null;

    Button btnB = null;

    Button btnC = null;

    Button btnD = null;

    Button btnFind = null;

    TextView txtTitle = null;

    int currentIdx = 0;

    Question currentQuestion = null;

    NetworkImageView imgPic = null;

    ImageView imgA = null;

    ImageView imgB = null;

    ImageView imgC = null;

    ImageView imgD = null;

    RelativeLayout rlWaiting = null;
    
    RelativeLayout rlMask=null;

    int taskId = 0;

    int rightCount = 0;

    int wrongCount = 0;

    String answerString = "";
    
    SoundUtil soundUtil=null;

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.frag_answeroutlist,
                container, false);

        Bundle bd = this.getArguments();
        if( bd.containsKey("questions"))
        {
            questions = (List<Question>) bd.getSerializable("questions");
        }
        if (bd.containsKey("taskid"))
        {
            taskId = bd.getInt("taskid");
        }

        if (null != questions)
        {
            seqList = new ArrayList<Integer>(questions.size());
            for (int i = 0; i < questions.size(); i++)
            {
                seqList.add(i);
            }
            Collections.shuffle(seqList);
        }

        initViews(rootView);

        return rootView;
    }

    private void initViews(View rootView)
    {
        soundUtil =new SoundUtil(getActivity());
        rlAnsweroutList = (RelativeLayout) rootView.findViewById(R.id.rlAnsweroutList);
        //rlAnsweroutList.setOnClickListener(this);
        // rlQuestion=(RelativeLayout)rootView.findViewById(R.id.rlQuestion);
        llA = (FrameLayout) rootView.findViewById(R.id.llA);
        llB = (FrameLayout) rootView.findViewById(R.id.llB);
        llC = (FrameLayout) rootView.findViewById(R.id.llC);
        llD = (FrameLayout) rootView.findViewById(R.id.llD);
        btnA = (Button) rootView.findViewById(R.id.btnA);
        btnA.setOnClickListener(this);
        btnB = (Button) rootView.findViewById(R.id.btnB);
        btnB.setOnClickListener(this);
        btnC = (Button) rootView.findViewById(R.id.btnC);
        btnC.setOnClickListener(this);
        btnD = (Button) rootView.findViewById(R.id.btnD);
        btnD.setOnClickListener(this);
        //btnFind = (Button) rootView.findViewById(R.id.btnFindAnswer);
        //btnFind.setOnClickListener(this);
        imgPic = (NetworkImageView) rootView.findViewById(R.id.imgPic);
        imgPic.setOnClickListener(this);

        imgA = (ImageView) rootView.findViewById(R.id.gifPicA);
        imgB = (ImageView) rootView.findViewById(R.id.gifPicB);
        imgC = (ImageView) rootView.findViewById(R.id.gifPicC);
        imgD = (ImageView) rootView.findViewById(R.id.gifPicD);
        rlWaiting = (RelativeLayout) rootView.findViewById(R.id.rlWaiting);
        
        rlMask = (RelativeLayout)rootView.findViewById(R.id.rlMask);
        rlMask.setOnClickListener(this);
        
        //控制标题的高度
        txtTitle = (TextView) rootView.findViewById(R.id.txtNo);
        int height = DensityUtils.getScreenH(getActivity());
        int topMargin = (int) (0.36 * height)+5;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, topMargin, 0, 0);
        txtTitle.setLayoutParams(params);
        //控制答题的高度，防止点击
      
        //rlMask.setBackgroundColor(Color.RED);
        params = new LayoutParams(LayoutParams.MATCH_PARENT , topMargin);
        rlMask.setLayoutParams(params);
        
        if (questions == null)
            return;
        currentIdx = 0;
        SetQuestion();
    }

    private void getQuestionImage()
    {
        if (currentQuestion == null)
            return;
        //String imageUrl = currentQuestion.getImageUrl();
        // RequestQueue queue = Volley.newRequestQueue(getActivity());
        // LruImageCache cache = LruImageCache.instance();
        // ImageLoader imageLoader = new ImageLoader(queue, cache);

        //imgPic.setDefaultImageResId(R.drawable.ic_launcher);
        //imgPic.setErrorImageResId(R.drawable.ic_launcher);
        //imgPic.setImageUrl(imageUrl, VolleyUtil.getImageLoader());
        
        imgPic.setBackgroundResource(R.drawable.dianwo);
    }

    private void SetQuestion()
    {
        currentQuestion = questions.get(currentIdx);

        // llA.setBackgroundResource(drawable.shape_square);
        // llB.setBackgroundResource(drawable.shape_square);
        // llC.setBackgroundResource(drawable.shape_square);
        // llD.setBackgroundResource(drawable.shape_square);
        llA.setVisibility(View.GONE);
        llB.setVisibility(View.GONE);
        llC.setVisibility(View.GONE);
        llD.setVisibility(View.GONE);
        btnA.setClickable(true);
        btnA.setBackgroundResource(R.drawable.a);
        btnA.setText("");
        btnB.setClickable(true);
        btnB.setBackgroundResource(R.drawable.b);
        btnB.setText("");
        btnC.setClickable(true);
        btnC.setBackgroundResource(R.drawable.c);
        btnC.setText("");
        btnD.setClickable(true);
        btnD.setBackgroundResource(R.drawable.d);
        btnD.setText("");
        txtTitle.setText("");
        imgA.setVisibility(View.GONE);
        imgB.setVisibility(View.GONE);
        imgC.setVisibility(View.GONE);
        imgD.setVisibility(View.GONE);
        rlWaiting.setVisibility(View.GONE);
        imgPic.setClickable(true);

        getQuestionImage();

        // if( currentQuestion ==null) return;
        String txt = "(" + (currentIdx + 1) + "/" + questions.size() + "题) "
                + currentQuestion.getContext();
        txtTitle.setText(txt);
        List<Answers> answers = currentQuestion.getAnswers();
        Collections.shuffle(answers);       
        

        if (answers.size() > 0)
        {
            llA.setVisibility(View.VISIBLE);
            btnA.setTag(answers.get(0));
            btnA.setText(answers.get(0).getName());
        }
        if (answers.size() > 1)
        {
            llB.setVisibility(View.VISIBLE);
            btnB.setTag(answers.get(1));
            btnB.setText(answers.get(1).getName());
        }
        if (answers.size() > 2)
        {
            llC.setVisibility(View.VISIBLE);
            btnC.setTag(answers.get(2));
            btnC.setText(answers.get(2).getName());
        }
        if (answers.size() > 3)
        {
            llD.setVisibility(View.VISIBLE);
            btnD.setTag(answers.get(3));
            btnD.setText(answers.get(3).getName());
        }
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
    
    

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if( soundUtil !=null){
            soundUtil.Release();
        }
    }

    @Override
    public void onClick(View view)
    {
        if( false == Util.isConnect(getActivity())){
            ToastUtils.showLongToast(getActivity(), "无法连接网络,请检查网络设置");
            return;
        }    
        
        if (view == btnA)
        {
            Answers vo = (Answers) btnA.getTag();
            boolean isRight = currentQuestion.getCorrentAid().equals(
                    String.valueOf(vo.getAid()));
            soundUtil.shakeSound( isRight? R.raw.right: R.raw.wrong);
            btnA.setBackgroundResource( isRight? R.drawable.a_d:R.drawable.a_c);
            
            SetAnswerImage(llA, isRight, currentQuestion.getCorrentAid(),
                    vo.getAid(), imgA);
        } else if (view == btnB)
        {
            Answers vo = (Answers) btnB.getTag();
            boolean isRight = currentQuestion.getCorrentAid().equals(
                    String.valueOf(vo.getAid()));
            soundUtil.shakeSound( isRight? R.raw.right: R.raw.wrong);
            btnB.setBackgroundResource( isRight? R.drawable.b_d:R.drawable.b_c);
            
            SetAnswerImage(llB, isRight, currentQuestion.getCorrentAid(),
                    vo.getAid(), imgB);
        } else if (view == btnC)
        {
            Answers vo = (Answers) btnC.getTag();
            boolean isRight = currentQuestion.getCorrentAid().equals(
                    String.valueOf(vo.getAid()));
            soundUtil.shakeSound( isRight? R.raw.right: R.raw.wrong);
            
            btnC.setBackgroundResource( isRight? R.drawable.c_d:R.drawable.c_c);
            
            SetAnswerImage(llC, isRight, currentQuestion.getCorrentAid(),
                    vo.getAid(), imgC);
        } else if (view == btnD)
        {
            Answers vo = (Answers) btnD.getTag();
            boolean isRight = currentQuestion.getCorrentAid().equals(
                    String.valueOf(vo.getAid()));
            soundUtil.shakeSound( isRight? R.raw.right: R.raw.wrong);
            btnD.setBackgroundResource( isRight? R.drawable.d_d:R.drawable.d_c);
            
            SetAnswerImage(llD, isRight, currentQuestion.getCorrentAid(),
                    vo.getAid(), imgD);
        } else if (view == imgPic)
        {
            if(currentQuestion==null)return;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse( currentQuestion.getRelexUrl() ));
            getActivity().startActivity(intent);
        }else if( view == rlMask ){
            if(currentQuestion==null)return;
            try{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse( currentQuestion.getRelexUrl() ));
            getActivity().startActivity(intent);
            }catch(Exception ex)
            {
                Log.e("error", ex.getMessage());
            }
        }
    }
    
    private void setRight( boolean isRight , String correctId ){
        if (isRight == false)
        {
            if (((Answers) btnA.getTag()).getAid().toString().equals(correctId))
            {
                btnA.setBackgroundResource(R.drawable.a_d);
                //imgA.setVisibility(View.VISIBLE); 
                
            } else if (((Answers) btnB.getTag()).getAid().toString().equals(correctId))
            {
                btnB.setBackgroundResource(R.drawable.b_d);
                //imgB.setVisibility(View.VISIBLE); 
            } else if (((Answers) btnC.getTag()).getAid().toString().equals(correctId))
            {
                btnC.setBackgroundResource(R.drawable.c_d);           
                //imgC.setVisibility(View.VISIBLE); 
            } else if (((Answers) btnD.getTag()).getAid().toString().equals(correctId))
            {
                btnD.setBackgroundResource(R.drawable.d_d);
                //imgD.setVisibility(View.VISIBLE); 
            }
        }
    }

    private void SetAnswerImage(FrameLayout ll, Boolean isRight,
            String correctId, int aid, ImageView imgView)
    {
        // llA.setBackgroundResource(drawable.shape_square);
        // llB.setBackgroundResource(drawable.shape_square);
        // llC.setBackgroundResource(drawable.shape_square);
        // llD.setBackgroundResource(drawable.shape_square);

        // ll.setBackgroundResource( isRight ? R.drawable.yes : R.drawable.no );

        rightCount += isRight ? 1 : 0;
        wrongCount += isRight ? 0 : 1;
        if (answerString.length() > 0)
        {
            answerString += "|";
        }
        answerString += currentQuestion.getQid() + ":" + aid;
        
        
        setRight(isRight, correctId);
        
        

//        imgView.setBackgroundResource(isRight ? R.drawable.anim_right
//                : R.drawable.anim_error);
//        AnimationDrawable anim = new AnimationDrawable();
//        Object obj = imgView.getBackground();
//        anim = (AnimationDrawable) obj;
//        anim.start();
//        imgView.setVisibility(View.VISIBLE);
//
//        if (isRight == false)
//        {
//            if (((Answers) btnA.getTag()).getAid().toString().equals(correctId))
//            {
//                // llA.setBackgroundResource(R.drawable.yes);
//            } else if (((Answers) btnB.getTag()).getAid().toString().equals(correctId))
//            {
//                // llB.setBackgroundResource(R.drawable.yes);
//            } else if (((Answers) btnC.getTag()).getAid().toString().equals(correctId))
//            {
//                // llC.setBackgroundResource(R.drawable.yes);
//            } else if (((Answers) btnD.getTag()).getAid().toString().equals(correctId))
//            {
//                // llD.setBackgroundResource(R.drawable.yes);
//            }
//        }

        if (currentIdx < seqList.size() - 1)
        {
            currentIdx++;
            ShowWait wait = new ShowWait();
            wait.execute(1000);
        } else
        {
            currentIdx++;
            
            ShowWait wait = new ShowWait();
            wait.execute(1000);
            // BaseFragment frag= new FragAnswerPass();
            // ((AnswerActivity)getActivity()).SwitchFragment(frag,
            // "answerpass");
        }
    }

    class ShowWait extends AsyncTask<Integer, Void, Void>
    {

        @Override
        protected Void doInBackground(Integer... params)
        {
            // TODO Auto-generated method stub
            try
            {
                Thread.sleep(params[0]);
            } catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            // TODO Auto-generated method stub
            super.onPreExecute();

            rlWaiting.setVisibility(View.VISIBLE);
            btnA.setClickable(false);
            btnB.setClickable(false);
            btnC.setClickable(false);
            btnD.setClickable(false);
            imgPic.setClickable(false);
        }

        @Override
        protected void onPostExecute(Void result)
        {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (currentIdx + 1 > seqList.size())
            {
                BaseFragment frag = new FragAnswerPass();

                Bundle bd = new Bundle();
                bd.putInt("taskid", taskId);
                bd.putInt("rightcount", rightCount);
                bd.putInt("wrongcount", wrongCount);
                bd.putSerializable("answers", answerString);
                frag.setArguments(bd);

                ((AnswerActivity) getActivity()).switchFragment(frag,
                        "answerpass");
                return;
            }

            SetQuestion();
        }
    }

}
