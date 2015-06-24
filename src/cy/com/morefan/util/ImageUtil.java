package cy.com.morefan.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

public class ImageUtil {
	public static void saveInputStreanToFile(InputStream in, String fullPath){
		String path = fullPath.substring(0, fullPath.lastIndexOf("/"));
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		File file = new File(fullPath);
		if(!file.exists()){
			FileOutputStream foutput = null ;
			BufferedOutputStream bos = null;

			try {
				foutput = new FileOutputStream(file);
				bos = new BufferedOutputStream(foutput);
				int b;
				while ((b = in.read()) != -1) {
					bos.write(b);
					bos.flush();
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(in != null)
					in.close();
					if(bos != null)
					bos.close();
					if(foutput != null)
					foutput.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}



	}
	/**
	 * 一种挺有效的方法，规避BitmapFactory.decodeStream或者decodeFile函数，使用BitmapFactory.decodeFileDescriptor
	 * @param path
	 * @return
	 */
	public static  Bitmap readBitmapByPath(String fullPath)   {
		if(TextUtils.isEmpty(fullPath))
			return null;
	    BitmapFactory.Options bfOptions=new BitmapFactory.Options();
	    bfOptions.inDither=false;
	    bfOptions.inPurgeable=true;
	    bfOptions.inInputShareable=true;
	    bfOptions.inTempStorage=new byte[32 * 1024];

	    File file=new File(fullPath);
	    FileInputStream fs=null;
	    try {
	    	fs = new FileInputStream(file);
	        if(fs!=null)
	        	return BitmapFactory.decodeFileDescriptor(fs.getFD(), null, bfOptions);
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally{
	        if(fs!=null) {
	                try {
						fs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        }
	    }
	    return null;
	}
	/**
	 * //取得文件夹大小
	 * @param f
	 * @return
	 */
    public static long getFileSize(String path ){
    	long size = 0;
    	File file  = new File(path);
    	if(!file.exists())
    		return size;

        File flist[] = file.listFiles();
        for (int i = 0; i < flist.length; i++)
        {
            if (flist[i].isDirectory()){
                size = size + getFileSize(flist[i].getAbsolutePath());
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }
	public static void deleteFileByPath(String path){
		if(path !=null){
			File imgDir = new File(path);
			if(!imgDir.exists()){
				return;
			}else{
				if(imgDir.isDirectory()){
					File[] files = imgDir.listFiles();
			        if (files != null)
			        	for(int i = 0; i < files.length; i ++){
			        		if(files[i].isDirectory())
			        			deleteFileByPath(files[i].getAbsolutePath());
			        		else
			        			files[i].delete();
			        	}

			        imgDir.delete();
				}
			}
		}
	}
	public static void checkFile(final String path, final List<String> imageUrls) {
		(new Thread(){
			public void run(){
				if(path !=null ){
					File imgDir = new File(path);
					if(!imgDir.exists()){
						return;
					}else{
						if(imgDir.isDirectory()){
							File[] files = imgDir.listFiles();
					        if (files != null) {
					        	for(int i = 0; i < files.length; i ++) {
					        		boolean needDelete = true;
					        		for(int m = 0; m < imageUrls.size(); m ++){
					        			String fileName = imageUrls.get(m).substring(imageUrls.get(m).lastIndexOf("/") + 1);
					        			if(files[i].getName().equals(fileName)){
					        				needDelete = false;
					        			}
					        		}

					        		if(needDelete){
					        			files[i].delete();
					        		}
						        }
					        }
						}
					}
				}
			}
		}).start();

	}

}
