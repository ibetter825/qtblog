package com.mypro.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;
import com.mypro.annotation.Cron;
import com.mypro.annotation.FixedDelay;
import com.mypro.annotation.FixedRate;
import it.sauronsoftware.cron4j.Scheduler;

/**
 * @ClassName: SchedulerPlugin
 * @Description: 简单任务调度插件，整合了cron4j和ScheduledThreadPoolExecutor
 * @author 李飞
 * @date 2015年8月1日 下午10:01:37
 * @since V1.0.0
 */
public class SchedulerPlugin implements IPlugin {

	private static Log LOG = Log.getLog("SchedulerPlugin");

	/**
	 * cron调度器
	 */
	private Scheduler cronScheduler = null;
	
	/**
	 * 调度线程池
	 */
	private int scheduledThreadPoolSize = 10;

	/**
	 * ScheduledThreadPoolExecutor调度器
	 */
	private ScheduledThreadPoolExecutor fixedScheduler;

	/**
	 * 调度任务配置文件
	 */
	private final String jobConfigFile;
	
	/**
	 * 扫描根目录
	 */
	private String scanRootPackage = null;
	
	/**
	 * 是否又cron任务
	 */
	private boolean hasCronJob = false;
	
	/**
	 * 是否有ScheduledThreadPoolExecutor任务
	 */
	private boolean hasFixedJob = false;
	
	/**
	 * 
	 * <p>Title: SchedulerPlugin</p>  
	 * <p>Description: 构造函数(指定调度线程池大小、调度任务配置文件和扫描路径)</p>  
	 * @param scheduledThreadPoolSize
	 *            调度线程池大小
	 * @param jobConfigFile
	 *            调度任务配置文件
	 * @param scanRootPackage
	 *            扫描路径
	 * @since V1.0.0
	 */
	private SchedulerPlugin(int scheduledThreadPoolSize, String jobConfigFile, String scanRootPackage) {
		this.scheduledThreadPoolSize = scheduledThreadPoolSize;
		this.jobConfigFile = jobConfigFile;
		this.scanRootPackage = scanRootPackage;
	}
	
	/**
	 * @Title: ensureCronScheduler  
	 * @Description: 确保cronScheduler可用  
	 * @since V1.0.0
	 */
	private void ensureCronScheduler(){
		if(this.cronScheduler == null){
    		synchronized (this) {
				if(this.cronScheduler == null){
					this.cronScheduler = new Scheduler();
				}
			} 
    	}
	}

	/**
	 * @Title: scheduleCron
	 * @Description: 添加基于Linux下的crontab表达式的调度任务(Runnable)
	 * @param job
	 *            定期执行的任务(Runnable)
	 * @param cronExpression
	 *            cron调度表达式
	 * @since V1.0.0
	 */
	public void scheduleCron(Runnable job, String cronExpression) {
		ensureCronScheduler();
		this.hasCronJob = true;
		this.cronScheduler.schedule(cronExpression, job);
	}
	
	/**
	 * @Title: ensurFixedScheduler  
	 * @Description: 确保fixedScheduler可用  
	 * @since V1.0.0
	 */
	private void ensurFixedScheduler(){
		if(this.fixedScheduler == null){
    		synchronized (this) {
				if(this.fixedScheduler == null){
					this.fixedScheduler = new ScheduledThreadPoolExecutor(scheduledThreadPoolSize);
				}
			} 
    	}
	}

	/**
	 * @Title: scheduleAtFixedRate
	 * @Description: 延迟指定秒后启动，并以固定的频率来运行任务。后续任务的启动时间不受前次任务延时影响（并行）。
	 * @param job
	 *            定期执行的任务
	 * @param initialDelaySeconds
	 *            启动延迟时间
	 * @param periodSeconds
	 *            每次执行任务的间隔时间(单位秒)
	 * @return
	 * @since V1.0.0
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable job, int initialDelaySeconds, int periodSeconds) {
		ensurFixedScheduler();
		this.hasFixedJob = true;
		return fixedScheduler.scheduleAtFixedRate(job, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
	}

	/**
	 * @Title: scheduleWithFixedDelay
	 * @Description: 延迟指定秒后启动，两次任务间保持固定的时间间隔(任务串行执行，前一个结束之后间隔固定时间后一个才会启动)
	 * @param job
	 *            定期执行的任务
	 * @param initialDelaySeconds
	 *            启动延迟时间
	 * @param periodSeconds
	 *            每次执行任务的间隔时间(单位秒)
	 * @return
	 * @since V1.0.0
	 */
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable job, int initialDelaySeconds, int periodSeconds) {
		ensurFixedScheduler();
		this.hasFixedJob = true;
		return fixedScheduler.scheduleWithFixedDelay(job, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
	}

	@Override
	public boolean start() {
		//自动通过注解加载
		scanAnnotation();
		//通过文件记载
		loadJobsFromConfigFile();
		//启动cron任务
		if(this.hasCronJob){
			this.cronScheduler.setDaemon(true);
			this.cronScheduler.start();
			LOG.info("CronScheduler已启动");
		}
		//启动fixed任务
		if(this.hasFixedJob){
			LOG.info("ScheduledThreadPoolExecutor已启动");
		}
		return true;
	}

	@Override
	public boolean stop() {
		//停止cron任务
		if(this.hasCronJob){
			this.cronScheduler.stop();
			this.cronScheduler = null;
			LOG.info("CronScheduler已停止");
		}
		//停止fixed任务
		if(this.hasFixedJob){
			this.fixedScheduler.shutdown();
			this.fixedScheduler = null;
			LOG.info("ScheduledThreadPoolExecutor已停止");
		}
		return true;
	}

	/**
	 * @Title: loadJobsFromConfigFile
	 * @Description: 从配置文件汇总加载任务
	 * @since V1.0.0
	 */
	private void loadJobsFromConfigFile() {
		if(StrKit.isBlank(this.jobConfigFile)){
			return;
		}
		// 获取job配置文件
		Prop jobProp = PropKit.use(this.jobConfigFile);
		// 获得所有任务名
		Set<String> jobNames = this.getJobNamesFromProp(jobProp);
		if(jobNames.isEmpty()){
			return;
		}
		// 逐个加载任务
		for (String jobName : jobNames) {
			loadJob(jobProp, jobName);
		}
	}

	/**
	 * @Title: loadJob
	 * @Description: 加载一个任务
	 * @param jobProp
	 *            job配置
	 * @param jobName
	 *            job名
	 * @since V1.0.0
	 */
	private void loadJob(Prop jobProp, String jobName) {
		// 任务开关，默认开启
		Boolean enable = jobProp.getBoolean(jobName + ".enable", Boolean.TRUE);
		// 任务被禁用，直接返回
		if (!enable) {
			return;
		}
		// 任务类型
		String jobType = jobProp.get(jobName + ".type");
		if (StrKit.isBlank(jobType)) {
			throw new RuntimeException("请设定 " + jobName + ".type");
		}
		
		// 创建要执行的任务
		Runnable job = createRunnableJob(jobName, jobProp.get(jobName + ".class"));
		if ("cron".equals(jobType)) {
			loadCronJob(jobName, jobProp, job);
		}else{
			loadFixedJob(jobName, jobType, jobProp, job);
		}
	}
	
	/**
	 * @Title: loadCronJob  
	 * @Description: 加载cron任务  
	 * @param jobName
	 * @param jobProp
	 * @param job 
	 * @since V1.0.0
	 */
	private void loadCronJob(String jobName, Prop jobProp, Runnable job) {
		// 任务表达式
		String expr = jobProp.get(jobName + ".expr");
		if (StrKit.isBlank(expr)) {
			throw new RuntimeException("请设定 " + jobName + ".expr");
		}
		this.scheduleCron(job, expr);
		LOG.info("通过配置文件自动加载Cron类型定时任务( jobName=" + jobName + ",expr=" + expr + " )");
	}

	/**
	 * @Title: loadFixedJob  
	 * @Description: 加载Fixed任务  
	 * @param jobName
	 * @param jobType
	 * @param jobProp
	 * @param job 
	 * @since V1.0.0
	 */
	private void loadFixedJob(String jobName, String jobType, Prop jobProp, Runnable job) {
		
		Integer initialDelay = jobProp.getInt(jobName + ".initialDelay", 0);
		Integer period = jobProp.getInt(jobName + ".period", null);
		if(null == period){
			throw new RuntimeException("请设定 " + jobName + ".period");
		}
		if ("fixedRate".equals(jobType)) {
			this.scheduleAtFixedRate(job, initialDelay, period);
			LOG.info("通过配置文件自动加载FixedRate类型定时任务( jobName=" + jobName + ", initialDelay=" + initialDelay + "'s, period=" + period + "'s )");
		} else if ("fixedDelay".equals(jobType)) {
			this.scheduleWithFixedDelay(job, initialDelay, period);
			LOG.info("通过配置文件自动加载fixedDelay类型定时任务( jobName=" + jobName + ", initialDelay=" + initialDelay + "'s, period=" + period + "'s )");
		} else {
			throw new RuntimeException("请设定 " + jobName + ".type to cron/fixedRate/fixedDelay");
		}
	}

	/**
	 * @Title: createRunnableJob
	 * @Description: 创建任务
	 * @param jobName
	 *            任务名
	 * @param jobClassName
	 *            任务类名
	 * @return Runnable对象
	 * @since V1.0.0
	 */
	private Runnable createRunnableJob(String jobName, String jobClassName) {
		if (jobClassName == null) {
			throw new RuntimeException("请设定 " + jobName + ".class");
		}

		Object temp = null;
		try {
			temp = Class.forName(jobClassName).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("无法实例化类: " + jobClassName, e);
		}

		Runnable job = null;
		if (temp instanceof Runnable) {
			job = (Runnable) temp;
		} else {
			throw new RuntimeException("无法实例化类: " + jobClassName
					+ "，该类必须实现Runnable接口");
		}
		return job;
	}

	/**
	 * @Title: getJobNamesFromProp
	 * @Description: 获得所有任务名
	 * @param jobProp
	 *            job配置
	 * @return 任务名集合
	 * @since V1.0.0
	 */
	private Set<String> getJobNamesFromProp(Prop jobProp) {
		Map<String, Boolean> jobNames = new HashMap<String, Boolean>();
		for (Object item : jobProp.getProperties().keySet()) {
			String fullKeyName = item.toString();
			// 获得job名
			String jobName = fullKeyName.substring(0, fullKeyName.indexOf("."));
			jobNames.put(jobName, Boolean.TRUE);
		}
		return jobNames.keySet();
	}
	
	/**
	 * @Title: scanAnnotation  
	 * @Description: 扫描所有任务注解  
	 * @since V1.0.0
	 */
	private void scanAnnotation(){
		if(StrKit.isBlank(scanRootPackage)){
			return;
		}
		//Reflections reflections = new Reflections(scanRootPackage);
		Reflections reflections = new Reflections(new ConfigurationBuilder()
				 .addUrls(ClasspathHelper.forClass(Cron.class))
				 .addUrls(ClasspathHelper.forClass(FixedDelay.class))
				 .addUrls(ClasspathHelper.forClass(FixedRate.class))
	       	     .forPackages(scanRootPackage.split(","))
	       	     .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
	       	     .useParallelExecutor()
	       	     );
		scanCronJob(reflections);
		scanFixedRateJob(reflections);
		scanFixedDelayJob(reflections);
	}
	
	/**
	 * @Title: scanCronJob  
	 * @Description: 扫描cron任务 
	 * @param reflections 
	 * @since V1.0.0
	 */
	private void scanCronJob(Reflections reflections){
		Set<Class<?>> cronTaskClasses = reflections.getTypesAnnotatedWith(Cron.class);
		for (Class<?> mc : cronTaskClasses) {
			if(!Runnable.class.isAssignableFrom(mc)){
				throw new RuntimeException(mc.getName() + " 必须实现Runnable接口");
			}
			Cron cronJob = mc.getAnnotation(Cron.class);
			try {
				Runnable runnable = (Runnable) mc.newInstance();
				this.scheduleCron(runnable, cronJob.value());
				LOG.info("通过注解自动加载Cron类型定时任务( expr=" + cronJob.value() + ",job= "+mc.getName() +" )");
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			} 
		}
	}
	
	/**
	 * @Title: scanFixedRateJob  
	 * @Description: 扫描固定频率任务  
	 * @param reflections 
	 * @since V1.0.0
	 */
	private void scanFixedRateJob(Reflections reflections){
		Set<Class<?>> cronTaskClasses = reflections.getTypesAnnotatedWith(FixedRate.class);
		for (Class<?> mc : cronTaskClasses) {
			if(!Runnable.class.isAssignableFrom(mc)){
				throw new RuntimeException(mc.getName() + " 必须实现Runnable接口");
			}
			FixedRate fixedRateJob = mc.getAnnotation(FixedRate.class);
			try {
				Runnable runnable = (Runnable) mc.newInstance();
				this.scheduleAtFixedRate(runnable, fixedRateJob.initialDelay(), fixedRateJob.period());
				LOG.info("通过注解自动加载FixedRate类型定时任务( initialDelay=" + fixedRateJob.initialDelay() + "'s, period=" + fixedRateJob.period() + "'s, job= "+mc.getName() +" )");
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			} 
		}
	}
	
	/**
	 * @Title: scanFixedDelayJob  
	 * @Description: 扫描固定延迟任务 
	 * @param reflections 
	 * @since V1.0.0
	 */
	private void scanFixedDelayJob(Reflections reflections){
		Set<Class<?>> cronTaskClasses = reflections.getTypesAnnotatedWith(FixedDelay.class);
		for (Class<?> mc : cronTaskClasses) {
			if(!Runnable.class.isAssignableFrom(mc)){
				throw new RuntimeException(mc.getName() + " 必须实现Runnable接口");
			}
			FixedDelay fixedDelayJob = mc.getAnnotation(FixedDelay.class);
			try {
				Runnable runnable = (Runnable) mc.newInstance();
				this.scheduleWithFixedDelay(runnable, fixedDelayJob.initialDelay(), fixedDelayJob.period());
				LOG.info("通过注解自动加载FixedDelay类型定时任务( initialDelay=" + fixedDelayJob.initialDelay() + "'s, period=" + fixedDelayJob.period() + "'s, job= "+mc.getName() +" )");
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			} 
		}
	}
	
	/**
	 * @Title: builder  
	 * @Description: 返回一个构建器  
	 * @return 
	 * @since V1.0.0
	 */
	public static Builder builder(){
		return new Builder();
	}
	
	public static class Builder{
		/**
		 * 调度线程池大小
		 */
		private int scheduledThreadPoolSize;
		
		/**
		 * 调度任务配置文件
		 */
		private String jobConfigFile = null;
		
		/**
		 * 扫描根目录
		 */
		private String scanRootPackage = null;
		
		/**
		 * <p>Title: Builder</p>  
		 * <p>Description: 默认构造函数</p>  
		 * @since V1.0.0
		 */
		public Builder(){
			this.scheduledThreadPoolSize = this.getBestPoolSize();
		}
		
		/**
		 * @Title: scheduledThreadPoolSize  
		 * @Description: 配置调度线程池大小  
		 * @param scheduledThreadPoolSize
		 * @return 
		 * @since V1.0.0
		 */
		public Builder scheduledThreadPoolSize(int size){
			this.scheduledThreadPoolSize = size;
			return this;
		}
		
		/**
		 * @Title: enableConfigFile  
		 * @Description: 使能配置文件加载  
		 * @param jobConfigFile
		 * @return 
		 * @since V1.0.0
		 */
		public Builder enableConfigFile(String configFile){
			this.jobConfigFile = configFile;
			return this;
		}
		
		/**
		 * @Title: enableAnnotationScan  
		 * @Description:  使能注解扫描（进行类库检查）
		 * @param scanRootPackage
		 * @return 
		 * @since V1.0.0
		 */
		public Builder enableAnnotationScan(String rootPackage){
			this.scanRootPackage = rootPackage;
			try {
				//自动扫描相关类库检查
				Class.forName("org.reflections.Reflections");
				Class.forName("com.google.common.collect.Sets");
				Class.forName("javassist.bytecode.annotation.Annotation");
			} catch (Exception e) { 
				throw new RuntimeException("开启自动扫描加载定时任务需要Reflections、Guava、javassist类库，请导入相应的jar包");
			}
			return this;
		}
		
		/**
		 * @Title: getBestPoolSize
		 * @Description: 获得调度线程池大小
		 * @return
		 * @since V1.0.0
		 */
		private int getBestPoolSize() {
			try {
				final int cores = Runtime.getRuntime().availableProcessors();
				// 每个核有8个调度线程
				return cores * 8;
			} catch (Throwable e) {
				return 8;
			}
		}
		
		/**
		 * @Title: build  
		 * @Description: 构建一个调度器插件  
		 * @return 
		 * @since V1.0.0
		 */
		public SchedulerPlugin build(){
			return new SchedulerPlugin(this.scheduledThreadPoolSize,this.jobConfigFile,this.scanRootPackage);
		}
	}
}