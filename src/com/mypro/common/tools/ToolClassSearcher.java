package com.mypro.common.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.apache.log4j.Logger;
import com.jfinal.kit.PathKit;
import com.mypro.common.DictKeys;
import com.mypro.plugin.PropertiesPlugin;

/**
 * 类文件检索
 * @author 董华健
 */
public class ToolClassSearcher {

    protected static final Logger LOG = Logger.getLogger(ToolClassSearcher.class);

    @SuppressWarnings("unchecked")
    private static <T> List<Class<? extends T>> extraction(Class<T> clazz, List<String> classFileList) {
        List<Class<? extends T>> classList = new ArrayList<Class<? extends T>>();
        for (String classFile : classFileList) {
        	if(!valiPkg(classFile)){
        		continue;
        	}
        	
            Class<?> classInFile = ToolReflect.on(classFile).get();
            if (clazz.isAssignableFrom(classInFile) && clazz != classInFile) {
                classList.add((Class<? extends T>) classInFile);
            }
        }

        return classList;
    }
    
    /**
     * 是否需要验证类路径
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValiPkg(){
    	List<String> pkgs = (List<String>) PropertiesPlugin.getParamMapValue(DictKeys.config_scan_package);
    	if(pkgs.size() > 0){
    		return true;
    	}
        return false;
    }
    
    /**
     * 验证类路径是否需要扫描
     * @param classFile
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean valiPkg(String classFile){
        List<String> pkgs = (List<String>) PropertiesPlugin.getParamMapValue(DictKeys.config_scan_package);
        for (String pkg : pkgs) {
        	if(classFile.startsWith(pkg)){
        		return true;
        	}
        }
        return false;
    }

    /**
     * 是否需要验证jar
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValiJar(){
    	List<String> jars = (List<String>) PropertiesPlugin.getParamMapValue(DictKeys.config_scan_jar);
    	if(jars.size() > 0){
    		return true;
    	}
        return false;
    }
    
    /**
     * 验证jar是否需要扫描
     * @param classFile
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean valiJar(String jarName){
        List<String> jars = (List<String>) PropertiesPlugin.getParamMapValue(DictKeys.config_scan_jar);
        for (String jar : jars) {
        	if(jarName.equals(jar)){
        		return true;
        	}
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
	public static ToolClassSearcher of(Class target) {
        return new ToolClassSearcher(target);
    }

    /**
     * 递归查找文件
     * 
     * @param baseDirName
     *            查找的文件夹路径
     * @param targetFileName
     *            需要查找的文件名
     */
    private static List<String> findFiles(String baseDirName, String targetFileName) {
        /**
         * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件， 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<String> classFiles = new ArrayList<String>();
        String tempName = null;
        // 判断目录是否存在
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            LOG.error("search error：" + baseDirName + "is not a dir！");
        } else {
            String[] filelist = baseDir.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(baseDirName + File.separator + filelist[i]);
                if (readfile.isDirectory()) {
                    classFiles.addAll(findFiles(baseDirName + File.separator + filelist[i], targetFileName));
                } else {
                    tempName = readfile.getName();
                    if (ToolClassSearcher.wildcardMatch(targetFileName, tempName)) {
                        String classname;
                        String tem = readfile.getAbsoluteFile().toString().replaceAll("\\\\", "/");
                        classname = tem.substring(tem.indexOf("/classes") + "/classes".length() + 1,
                                tem.indexOf(".class"));
                        classFiles.add(classname.replaceAll("/", "."));
                    }
                }
            }
        }
        return classFiles;
    }

    /**
     * 通配符匹配
     * 
     * @param pattern
     *            通配符模式
     * @param str
     *            待匹配的字符串 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     *            匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return strIndex == strLength;
    }

    private String classpath = PathKit.getRootClassPath();

    private boolean includeAllJarsInLib = false;

    private List<String> includeJars = new ArrayList<String>();;

    private String libDir = PathKit.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "lib";

    @SuppressWarnings("rawtypes")
	private Class target;

    @SuppressWarnings("rawtypes")
	public ToolClassSearcher(Class target) {
        this.target = target;
    }

    public ToolClassSearcher injars(List<String> jars) {
        if (jars != null) {
            includeJars.addAll(jars);
        }
        return this;
    }

    public ToolClassSearcher inJars(String... jars) {
        if (jars != null) {
            for (String jar : jars) {
                includeJars.add(jar);
            }
        }
        return this;
    }

    public ToolClassSearcher classpath(String classpath) {
        this.classpath = classpath;
        return this;
    }

    @SuppressWarnings("unchecked")
	public <T> List<Class<? extends T>> search() {
        List<String> classFileList = findFiles(classpath, "*.class");
        classFileList.addAll(findjarFiles(libDir, includeJars));
        return extraction(target, classFileList);
    }

    /**
     * 查找jar包中的class
     * 
     * @param baseDirName
     *            jar路径
     * @param includeJars
     * @param jarFileURL
     *            jar文件地址 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     */
    private List<String> findjarFiles(String baseDirName, final List<String> includeJars) {
        List<String> classFiles = new ArrayList<String>();;
        try {
            // 判断目录是否存在
            File baseDir = new File(baseDirName);
            if (!baseDir.exists() || !baseDir.isDirectory()) {
                LOG.error("file serach error：" + baseDirName + " is not a dir！");
            } else {
                String[] filelist = baseDir.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return includeAllJarsInLib || includeJars.contains(name);
                    }
                });
                for (int i = 0; i < filelist.length; i++) {
                    JarFile localJarFile = new JarFile(new File(baseDirName + File.separator + filelist[i]));
                    Enumeration<JarEntry> entries = localJarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        String entryName = jarEntry.getName();
                        if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                            String className = entryName.replaceAll("/", ".").substring(0, entryName.length() - 6);
                            classFiles.add(className);
                        }
                    }
                    localJarFile.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFiles;

    }

    public ToolClassSearcher includeAllJarsInLib(boolean includeAllJarsInLib) {
        this.includeAllJarsInLib = includeAllJarsInLib;
        return this;
    }

    public ToolClassSearcher libDir(String libDir) {
        this.libDir = libDir;
        return this;
    }
}
