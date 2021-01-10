package ir.smartdevelopers.smartfilebrowser;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileFilter;

import ir.smartdevelopers.smartfilebrowser.customClasses.FileUtil;
import ir.smartdevelopers.smartfilebrowser.customClasses.SFBFileFilter;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("ir.smartdevelopers.smartfilebrowser.test", appContext.getPackageName());
        String extension= FileUtil.getFileExtensionFromPath("/sdcard/Download");
        System.out.println("ext="+extension);
    }
    @Test
    public void fileFilterTest(){
        SFBFileFilter sfbFileFilter=new SFBFileFilter.Builder()
                .isFile(true).isFolder(true).includeExtension("pdf").build();
        Log.v("TTT","accespt=" + getFileFilterFromSfbFileFilter(sfbFileFilter)
                .accept(new File("/sdcard/Download")));
        Log.v("TTT","accespt=" + getFileFilterFromSfbFileFilter(sfbFileFilter)
                .accept(new File("/sdcard/Download/mm.pdf")));
    }
    private FileFilter getFileFilterFromSfbFileFilter(SFBFileFilter sfbFileFilter){
        return new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname==null){
                    return false;
                }
                if (pathname.isDirectory()){
                    return sfbFileFilter.isFolder();
                }else {
                    if (pathname.isFile()){
                        if (sfbFileFilter.getExtensionList().isEmpty()){
                            return sfbFileFilter.isFile();
                        }else {
                            if (sfbFileFilter.isFile()){
                                return sfbFileFilter.getExtensionList()
                                        .contains(FileUtil.getFileExtensionFromPath(pathname.getPath()));
                            }
                        }
                    }
                }
                return false;
            }
        };
    }
}