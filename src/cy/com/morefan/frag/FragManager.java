package cy.com.morefan.frag;



import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragManager  {

	public enum FragType{
		Home, Master, Pre, User, More, Mark, faqs,
	}
	private int viewId;
	private FragmentManager fragManager;
	private FragType preFragType;
	private FragType curFragType;
	//private HashMap<FragType, BaseFragment> frags;
	private static FragManager fragIns;
	public static FragManager getIns(FragmentActivity context, int viewId){
		if(null != fragIns)
			fragIns = null;
		return fragIns = new FragManager(context, viewId);
	}
	private FragManager(FragmentActivity context, int viewId){
		this.viewId = viewId;
		this.fragManager = context.getSupportFragmentManager();
		//frags = new HashMap<FragManager.FragType, BaseFragment>();

	}
	public FragType getCurrentFragType(){
		return this.curFragType;
	}
	public BaseFragment getCurrentFrag(){
		return getFragmentByType(preFragType);
	}

//	private static BaseFragment frag;
//	public static BaseFragment getIns(){
//		if(null == frag)
//			frag = new FragHome();
//		return frag;
//	}

	public void setCurrentFrag(FragType type){
		if(type == preFragType)
			return;
		curFragType = type;
		FragmentTransaction ft = fragManager.beginTransaction();
		String fragTag = makeFragmentName(viewId, type);
		BaseFragment frag =  (BaseFragment) fragManager.findFragmentByTag(fragTag);
		if(frag == null){
			switch (type) {
			case Home:
				frag = new FragHome();
				break;
			case Master:
				frag = new FragMaster();
				break;
			case Mark:
				frag = new FragMark();
				break;
			case More:
				frag = new FragMore();
				break;
			case Pre:
				frag = new FragPre();
				break;
			case User:
				frag = new FragUser();
				break;
			case faqs:
                frag = new FragFaqs();
                break;
			default:
				frag = new FragHome();
				break;
			}
			//frags.put(type, frag);
			ft.add(viewId, frag, fragTag);
		}else{
			frag.onReshow();
		}

		//frag.onReshow();
		ft.show(frag);
		if(preFragType != null){
			BaseFragment preFrag = getFragmentByType(preFragType);
			preFrag.onPause();
			ft.hide(preFrag);
		}



		//如果替换或者删除一个Fragment然后让用户可以导航到上一个Fragment，你必须在调用commit()方法之前调用addToBackStack()方法添加到回退栈
		//ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
		preFragType = type;



//		if(type == preFragType)
//			return;
//		FragmentTransaction ft = fragManager.beginTransaction();
//		BaseFragment frag = frags.get(type);
//		if(frag == null){
//			switch (type) {
//			case Goods:
//				frag = GoodsFrag.newInstance();
//				break;
//			case My:
//				frag = MyFrag.newInstance();
//				break;
//			case More:
//				frag = MoreFrag.newInstance();
//				break;
//			default:
//				frag = TaskFrag.newInstance();
//				break;
//			}
//			frags.put(type, frag);
//			ft.add(viewId, frag);
//		}else{
//			frag.onReshow();
//		}
//		ft.show(frag);
//		if(preFragType != null)
//			ft.hide(frags.get(preFragType));
//
//		//如果替换或者删除一个Fragment然后让用户可以导航到上一个Fragment，你必须在调用commit()方法之前调用addToBackStack()方法添加到回退栈
//		//ft.addToBackStack(null);
//		ft.commitAllowingStateLoss();
//		preFragType = type;
		//setUserVisibleHint();
	}
	public BaseFragment getFragmentByType(FragType type){
		return (BaseFragment) fragManager.findFragmentByTag(makeFragmentName(viewId, type));
	}

	private String makeFragmentName(int viewId, FragType type) {
		return "android:switcher:" + viewId + ":" + type;
	}
//	private void setUserVisibleHint(){
//		FragmentTransaction ft = fragManager.beginTransaction();
//		frags.get(preFragType).setMenuVisibility(false);
//		frags.get(preFragType).setUserVisibleHint(true);
//		ft.commitAllowingStateLoss();
//	}

	public void setPreFragType(FragType type ){
		preFragType=type;
	}

}
