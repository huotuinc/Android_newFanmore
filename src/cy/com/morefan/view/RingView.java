package cy.com.morefan.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;
import cy.com.morefan.R;

public class RingView extends TextView
{
    public RingView(Context context) {  
        super(context);  
    }  
  
    public RingView(Context context, AttributeSet attrs) {  
        super(context, attrs); 
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RingView);   
        
        description = array.getString(R.styleable.RingView_description);
        text= array.getString(R.styleable.RingView_title);
        subtextsize = array.getInteger(R.styleable.RingView_subtextsize, 20);
        textsize = array.getInteger(R.styleable.RingView_titlesize, 30);
        ringColor = array.getColor(R.styleable.RingView_ringcolor, R.color.ring_color);
        percentColor = array.getColor(R.styleable.RingView_percentcolor, R.color.pie_progress_fund);
        
        array.recycle();
        
        paint = new Paint();  
        paint.setStyle(Style.FILL);  
        paint.setAntiAlias(true);  
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//帮助消除锯齿
        paint.setColor(ringColor);//ring的颜色  
    }  
  
    public RingView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
      
    /** 
     * 环的颜色 
     * */  
    private int ringColor;//= Color.parseColor("#dbdbdb");  
      
    /** 
     * 进度的颜色 
     * */  
    private  int percentColor;// = Color.parseColor("#0096ff"); //Color.GREEN;  
      
    /** 
     * 画笔 
     */  
    private Paint paint;  
    /** 
     * 是否第一次 
     */  
    private boolean init = false;  
    /** 
     * 背景 
     */  
    private static final int BackGround = Color.parseColor("#FF0000");  
    /**
     * 文字的颜色
     */
    private static final int TextColor=Color.parseColor("#000000");
    
    /** 
     * 已经完成的颜色 
     */  
    private static final int CircleColor = Color.YELLOW;  
  
    /** 
     * 完成扇形角度 
     */  
    private static final float startAngle = -90;  
    /** 
     * 扇形中心点X轴 
     */  
    private float content_X;  
    /** 
     * 扇形中心点Y轴 
     */  
    private float content_Y;  
    /** 
     * 环形外半径 
     */  
    private float bigRadius;  
    /** 
     * 环形内半径 
     */  
    private float smallRadius;  
    /** 
     * 默认终点角度 
     */  
    private float SweepAngle = 150;  
    /** 
     * 控件宽 
     */  
    private int width;  
    /** 
     * 控件高 
     */  
    private int height;  
    /** 
     * 文件显示的文本 
     */  
    private String text;  
    private int textsize = 25;  
    /**
     * 描述文字
     */
    private String description;
    /**
     * 描述文字的大小
     */
    private int subtextsize=15;
  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
        
        int w= getMeasuredWidth();
        int h=getMeasuredHeight();
        
        //if (!init) { 
            //width = getCalcWidth(widthMeasureSpec);
            //height= getCalcHeight(heightMeasureSpec);
            
            initPaint();  
        //}  
    }  
  
    private void initPaint() {  
        setPadding(0, 0, 0, 0);  
//        paint = new Paint();  
//        paint.setStyle(Style.FILL);  
//        paint.setAntiAlias(true);  
//        paint.setFlags(Paint.ANTI_ALIAS_FLAG);//帮助消除锯齿
//        paint.setColor(RingColor);//ring的颜色  
        //this.setMeasuredDimension(measuredWidth, measuredHeight);
        width = getMeasuredWidth();  
        height =getMeasuredHeight();   
        
        //height=width;
        //width = width>height?height:width;
        //height = width;
        
        bigRadius = width>height? height/2:width/2; //((float) width / 2);  
        smallRadius = width>height? height/3: width/3; //(float) width / 3;  
        content_X = (float) ((float) width / 2);  
        content_Y = (float) ((float) height / 2);  
        init = true;  
    }  
    
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
               
        paint.setColor(ringColor);//ring的颜色 
        Path path = new Path();  
        path.reset();  
        /*画圆*/  
        path.addCircle(content_X, content_Y, bigRadius -3 , Path.Direction.CCW);  
        path.close();  
        canvas.drawPath(path, paint);  
        path.reset();  
        paint.setColor(Color.WHITE);  
        path.addCircle(content_X, content_Y, smallRadius, Path.Direction.CCW);  
        path.close();  
        canvas.drawPath(path, paint);  
        getSectorClip(canvas,startAngle);  
        path.reset();  
        paint.setColor(Color.WHITE);  
        path.addCircle(content_X, content_Y, smallRadius-3, Path.Direction.CCW);  
        path.close();  
        canvas.drawPath(path, paint);  
        if (text!=null) {  
            paint.setColor( TextColor );  
            paint.setFakeBoldText(true);  
            paint.setTextSize(textsize); 
            float textLocX= width/2- paint.measureText(text)/2;
            float textLocY = height/2 - 2;
            canvas.drawText(text, textLocX , textLocY , paint);  
        } 
        if(description !=null){
            paint.setColor(TextColor);
            paint.setTextSize(subtextsize);
            paint.setFakeBoldText(false);
            float textLocX= width/2 - paint.measureText(description)/2;
            float textHeight = paint.getFontMetrics().descent - paint.getFontMetrics().ascent;
            float textLocY = height/2 + textHeight;
            canvas.drawText(description, textLocX, textLocY , paint);
        }
    }  
    /** 
     * 返回一个扇形的剪裁区 
     *  
     * @param canvas 
     *            //画笔 
     * @param startAngle 
     *            //起始角度 
     */  
    private void getSectorClip(Canvas canvas,float startAngle) {  
        paint.setColor(percentColor);//进度的颜色  
        
        Path path = new Path();  
        // 下面是获得一个三角形的剪裁区  
        path.moveTo(content_X, content_Y); // 圆心  
        path.lineTo(  
                (float) (content_X + bigRadius * Math.cos(startAngle * Math.PI / 180)), // 起始点角度在圆上对应的横坐标  
  
                (float) (content_Y + bigRadius * Math.sin(startAngle * Math.PI / 180))); // 起始点角度在圆上对应的纵坐标  
        path.lineTo(  
                (float) (content_X + bigRadius * Math.cos(SweepAngle * Math.PI / 180)), // 终点角度在圆上对应的横坐标  
  
                (float) (content_Y + bigRadius * Math.sin(SweepAngle * Math.PI / 180))); // 终点点角度在圆上对应的纵坐标  
        path.close();  
        // //设置一个正方形，内切圆  
        RectF rectF = new RectF(content_X - bigRadius, content_Y - bigRadius, content_X + bigRadius,  
                content_Y + bigRadius);  
        // 下面是获得弧形剪裁区的方法  
        path.addArc(rectF, startAngle, SweepAngle - startAngle );       
        
        //canvas.drawPath(path,paint );  
        canvas.drawArc( rectF , startAngle , SweepAngle - startAngle , true , paint);  
          
    }  
      
    /** 
     * 返回一个扇形的剪裁区 
     *  
     * @param canvas 
     *            //画笔 
     * @param startAngle 
     *            //起始角度 
     */  
    private void getSmallSectorClip(Canvas canvas,float startAngle) {  
        paint.setColor(Color.WHITE);  
        Path path = new Path();  
        // 下面是获得一个三角形的剪裁区  
        path.moveTo(content_X, content_Y); // 圆心  
        path.lineTo(  
                (float) (content_X + smallRadius * Math.cos(startAngle * Math.PI / 180)), // 起始点角度在圆上对应的横坐标  
  
                (float) (content_Y + smallRadius * Math.sin(startAngle * Math.PI / 180))); // 起始点角度在圆上对应的纵坐标  
        path.lineTo(  
                (float) (content_X + smallRadius * Math.cos(SweepAngle * Math.PI / 180)), // 终点角度在圆上对应的横坐标  
  
                (float) (content_Y + smallRadius * Math.sin(SweepAngle * Math.PI / 180))); // 终点点角度在圆上对应的纵坐标  
        path.close();  
        // //设置一个正方形，内切圆  
        RectF rectF = new RectF(content_X - smallRadius, content_Y - smallRadius, content_X + smallRadius,  
                content_Y + smallRadius);  
        // 下面是获得弧形剪裁区的方法  
        path.addArc(rectF, startAngle, SweepAngle - startAngle);  
        canvas.drawPath(path,paint);  
          
          
    }  
      
    /** 
     * @param startAngle百分比 
     */  
    public void setAngle(float startAngle){  
        SweepAngle = startAngle/100*360 + this.startAngle;//(360*startAngle/100 + 270);   
    }  
      
    public void setText(String text){  
        this.text = text;  
        this.invalidate();
    } 
    public void setDescription(String description ){  
        this.description = description;  
        this.invalidate();
    } 
}
