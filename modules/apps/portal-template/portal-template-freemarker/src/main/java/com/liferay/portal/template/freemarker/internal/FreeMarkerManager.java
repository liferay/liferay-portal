/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.concurrent.ThreadPoolHandlerAdapter;
import com.liferay.petra.executor.PortalExecutorConfig;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.lang.ClassLoaderPool;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.thread.local.Lifecycle;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCacheManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.JSPSupportServlet;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.template.TemplateResource;
import com.liferay.portal.kernel.template.TemplateResourceCache;
import com.liferay.portal.kernel.template.TemplateResourceLoader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.NamedThreadFactory;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.template.BaseTemplateResourceCache;
import com.liferay.portal.template.BaseTemplateResourceLoader;
import com.liferay.portal.template.engine.BaseTemplateManager;
import com.liferay.portal.template.engine.TemplateContextHelper;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;
import com.liferay.portal.template.freemarker.internal.helper.FreeMarkerTemplateContextHelper;

import freemarker.cache.TemplateCache;

import freemarker.core.TemplateClassResolver;

import freemarker.debug.impl.DebuggerService;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jakarta.jsp.TaglibFactory;
import freemarker.ext.jakarta.servlet.HttpRequestHashModel;
import freemarker.ext.jakarta.servlet.ServletContextHashModel;
import freemarker.ext.jsp.internal.WriterFactoryUtil;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.net.URL;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Mika Koivisto
 * @author Tina Tina
 * @author Raymond Augé
 */
@Component(
	configurationPid = "com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration",
	property = "language.type=" + TemplateConstants.LANG_TYPE_FTL,
	service = TemplateManager.class
)
public class FreeMarkerManager extends BaseTemplateManager {

	@Override
	public String getName() {
		return TemplateConstants.LANG_TYPE_FTL;
	}

	@Override
	public String[] getRestrictedVariables() {
		return _freeMarkerEngineConfiguration.restrictedVariables();
	}

	public class FreeMarkerTemplateResourceCache
		extends BaseTemplateResourceCache {

		public FreeMarkerTemplateResourceCache() {
			init(
				Long.MIN_VALUE, _portalCacheName,
				StringBundler.concat(
					TemplateResource.class.getName(), StringPool.POUND,
					TemplateConstants.LANG_TYPE_FTL));
		}

		public void destroy() {
			super.destroy();
		}

		private final String _portalCacheName =
			FreeMarkerManager.FreeMarkerTemplateResourceCache.class.getName();

	}

	public class FreeMarkerTemplateResourceLoader
		extends BaseTemplateResourceLoader {

		public FreeMarkerTemplateResourceLoader(
			BundleContext bundleContext,
			TemplateResourceCache templateResourceCache) {

			init(
				bundleContext, TemplateConstants.LANG_TYPE_FTL,
				templateResourceCache);
		}

		public void destroy() {
			super.destroy();
		}

	}

	@Activate
	protected void activate(ComponentContext componentContext)
		throws TemplateException {

		BundleContext bundleContext = componentContext.getBundleContext();

		_bundle = bundleContext.getBundle();

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, new TaglibBundleTrackerCustomizer());

		_bundleTracker.open();

		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class,
			componentContext.getProperties());

		_freeMarkerTemplateResourceCache =
			new FreeMarkerTemplateResourceCache();

		_freeMarkerTemplateResourceLoader =
			new FreeMarkerTemplateResourceLoader(
				bundleContext, _freeMarkerTemplateResourceCache);

		_templateResourceLoaderServiceRegistration =
			bundleContext.registerService(
				TemplateResourceLoader.class, _freeMarkerTemplateResourceLoader,
				null);

		WriterFactoryUtil.setWriterFactory(new UnsyncStringWriterFactory());

		_initAsyncRender(bundleContext);

		_init();
	}

	protected void addTaglibSupport(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, ObjectWrapper objectWrapper) {

		ServletContext servletContext = httpServletRequest.getServletContext();

		contextObjects.put(
			"Application",
			_getServletContextHashModel(servletContext, objectWrapper));

		contextObjects.put(
			"Request",
			new HttpRequestHashModel(
				httpServletRequest, httpServletResponse, objectWrapper));

		// Legacy

		FreeMarkerBundleClassloader freeMarkerBundleClassloader =
			_freeMarkerBundleClassloader;

		if (freeMarkerBundleClassloader == null) {
			freeMarkerBundleClassloader = new FreeMarkerBundleClassloader(
				_taglibMappings.keySet());

			_freeMarkerBundleClassloader = freeMarkerBundleClassloader;
		}

		TaglibFactoryWrapper taglibFactoryWrapper = new TaglibFactoryWrapper(
			freeMarkerBundleClassloader, objectWrapper, servletContext);

		contextObjects.put("PortalJspTagLibs", taglibFactoryWrapper);
		contextObjects.put("PortletJspTagLibs", taglibFactoryWrapper);
		contextObjects.put("taglibLiferayHash", taglibFactoryWrapper);

		// Contributed

		for (Map<String, String> map : _taglibMappings.values()) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				try {
					contextObjects.put(
						entry.getKey(),
						taglibFactoryWrapper.get(entry.getValue()));
				}
				catch (TemplateModelException templateModelException) {
					_log.error(
						"Unable to add taglib " + entry.getKey() +
							" to context",
						templateModelException);
				}
			}
		}
	}

	@Deactivate
	protected void deactivate() {
		_destroy();

		_bundleTracker.close();

		if (_freeMarkerEngineConfiguration.asyncRenderTimeout() > 0) {
			_noticeableExecutorService.shutdownNow();

			_timeoutTemplateCounters.clear();

			_serviceRegistration.unregister();
		}

		_templateResourceLoaderServiceRegistration.unregister();

		_freeMarkerTemplateResourceCache.destroy();

		_freeMarkerTemplateResourceLoader.destroy();
	}

	@Override
	protected Template doGetTemplate(
		TemplateResource templateResource, boolean restricted,
		Map<String, Object> helperUtilities) {

		BeansWrapper beansWrapper = _defaultBeansWrapper;

		if (restricted) {
			beansWrapper = _restrictedBeansWrapper;
		}

		return new FreeMarkerTemplate(
			templateResource, helperUtilities, _configuration,
			_templateContextHelper, _freeMarkerTemplateResourceCache,
			restricted, beansWrapper, this);
	}

	@Override
	protected TemplateContextHelper getTemplateContextHelper() {
		return _templateContextHelper;
	}

	@Modified
	protected void modified(ComponentContext componentContext)
		throws TemplateException {

		if (_freeMarkerEngineConfiguration.asyncRenderTimeout() > 0) {
			_noticeableExecutorService.shutdownNow();

			_noticeableExecutorService = null;

			_serviceRegistration.unregister();

			_serviceRegistration = null;

			_timeoutTemplateCounters.clear();

			_timeoutTemplateCounters = null;
		}

		_freeMarkerEngineConfiguration = ConfigurableUtil.createConfigurable(
			FreeMarkerEngineConfiguration.class,
			componentContext.getProperties());

		_initAsyncRender(componentContext.getBundleContext());

		_destroy();

		_init();
	}

	protected void render(
			String templateId, Writer writer, boolean restricted,
			Callable<Void> callable)
		throws Exception {

		long timeout = _freeMarkerEngineConfiguration.asyncRenderTimeout();

		if ((timeout <= 0) || !restricted) {
			callable.call();

			return;
		}

		AtomicInteger timeoutCounter = _timeoutTemplateCounters.computeIfAbsent(
			templateId, key -> new AtomicInteger(0));

		if (timeoutCounter.get() >=
				_freeMarkerEngineConfiguration.asyncRenderTimeoutThreshold()) {

			throw new IllegalStateException(
				StringBundler.concat(
					"Skip processing FreeMarker template ", templateId,
					" since it has timed out ",
					_freeMarkerEngineConfiguration.
						asyncRenderTimeoutThreshold(),
					" times"));
		}

		Thread currentThread = Thread.currentThread();

		ClassLoader contextClassLoader = currentThread.getContextClassLoader();

		Object threadLocals = ThreadLocalUtil._cloneThreadLocals(currentThread);

		NoticeableFuture<?> noticeableFuture =
			_noticeableExecutorService.submit(
				(Callable<Void>)() -> {
					Thread thread = Thread.currentThread();

					try (SafeCloseable safeCloseable =
							ThreadContextClassLoaderUtil.swap(
								contextClassLoader)) {

						ThreadLocalUtil._setThreadLocals(thread, threadLocals);

						callable.call();
					}
					finally {
						ThreadLocalCacheManager.clearAll(Lifecycle.REQUEST);

						ThreadLocalUtil._setThreadLocals(thread, null);
					}

					return null;
				});

		try {
			noticeableFuture.get(timeout, TimeUnit.MILLISECONDS);
		}
		catch (ExecutionException executionException) {
			Throwable throwable = executionException.getCause();

			if (throwable instanceof Exception) {
				throw (Exception)throwable;
			}

			throw new Exception(throwable);
		}
		catch (TimeoutException timeoutException) {
			timeoutCounter.incrementAndGet();

			String errorMessage = StringBundler.concat(
				"FreeMarker template ", templateId, " processing timeout");

			writer.write(errorMessage);

			_log.error(errorMessage, timeoutException);

			ThreadLocalUtil._clearThreadLocals(threadLocals);
		}
	}

	private void _destroy() {
		if (_configuration == null) {
			return;
		}

		_configuration.clearEncodingMap();
		_configuration.clearSharedVariables();
		_configuration.clearTemplateCache();

		_configuration = null;

		_templateContextHelper.removeAllHelperUtilities();

		_templateModels.clear();

		if (_isEnableDebuggerService()) {
			//DebuggerService.shutdown();
		}
	}

	private String[] _filterRestrictedClasses(String[] restrictedClasses) {
		if (JavaDetector.isJDK21()) {

			// TODO Remove java.lang.Compiler from
			// FreeMarkerEngineConfiguration#restrictedClasses and this method
			// once we fully upgrade to JDK 21

			return ArrayUtil.remove(restrictedClasses, "java.lang.Compiler");
		}

		return restrictedClasses;
	}

	private String _getMacroLibrary() {
		Set<String> macroLibraries = SetUtil.fromArray(
			_freeMarkerEngineConfiguration.macroLibrary());

		macroLibraries.add(_LIFERAY_MACRO_LIBRARY);

		Class<?> clazz = getClass();

		String contextName = ClassLoaderPool.getContextName(
			clazz.getClassLoader());

		contextName = contextName.concat(
			TemplateConstants.CLASS_LOADER_SEPARATOR);

		StringBundler sb = new StringBundler(3 * macroLibraries.size());

		for (String library : macroLibraries) {
			if (_hasLibrary(library)) {
				sb.append(contextName);
				sb.append(library);
				sb.append(StringPool.COMMA);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn("Unable to find library: " + library);
			}
		}

		if (sb.index() > 0) {
			sb.setIndex(sb.index() - 1);
		}

		return sb.toString();
	}

	private ServletContextHashModel _getServletContextHashModel(
		ServletContext servletContext, ObjectWrapper objectWrapper) {

		GenericServlet genericServlet = new JSPSupportServlet(servletContext);

		return new ServletContextHashModel(genericServlet, objectWrapper);
	}

	private boolean _hasLibrary(String library) {
		int index = library.indexOf(CharPool.SPACE);

		if (index != -1) {
			library = library.substring(0, index);
		}

		if (_bundle.getResource(library) == null) {
			return false;
		}

		return true;
	}

	private void _init() throws TemplateException {
		if (_configuration != null) {
			return;
		}

		_configuration = new Configuration(Configuration.VERSION_2_3_33);

		_configuration.setAttemptExceptionReporter(
			(templateException, environment) -> {
			});
		_configuration.setDefaultEncoding(StringPool.UTF8);
		_configuration.setLocalizedLookup(
			_freeMarkerEngineConfiguration.localizedLookup());
		_configuration.setNewBuiltinClassResolver(_templateClassResolver);

		try {
			_configuration.setLogTemplateExceptions(
				_freeMarkerEngineConfiguration.logTemplateExceptions());
			_configuration.setSetting("auto_import", _getMacroLibrary());
			_configuration.setSetting(
				"template_exception_handler",
				_freeMarkerEngineConfiguration.templateExceptionHandler());

			Field field = ReflectionUtil.getDeclaredField(
				Configuration.class, "cache");

			PortalCache<TemplateResource, TemplateCache.MaybeMissingTemplate>
				portalCache =
					_freeMarkerTemplateResourceCache.
						getSecondLevelPortalCache();

			TemplateCache templateCache = new LiferayTemplateCache(
				_configuration, _freeMarkerTemplateResourceLoader, portalCache);

			field.set(_configuration, templateCache);

			_configuration.setSharedVariable(
				"loop-count-threshold",
				new SimpleNumber(
					_freeMarkerEngineConfiguration.loopCountThreshold()));
		}
		catch (Exception exception) {
			throw new TemplateException(
				"Unable to init FreeMarker manager", exception);
		}

		_defaultBeansWrapper = new LiferayObjectWrapper();
		_restrictedBeansWrapper = new RestrictedLiferayObjectWrapper(
			_freeMarkerEngineConfiguration.allowedClasses(),
			_filterRestrictedClasses(
				_freeMarkerEngineConfiguration.restrictedClasses()),
			_freeMarkerEngineConfiguration.restrictedMethods());

		if (_isEnableDebuggerService()) {
			DebuggerService.getBreakpoints("*");
		}

		FreeMarkerTemplateContextHelper freeMarkerTemplateContextHelper =
			(FreeMarkerTemplateContextHelper)_templateContextHelper;

		freeMarkerTemplateContextHelper.setDefaultBeansWrapper(
			_defaultBeansWrapper);
		freeMarkerTemplateContextHelper.setRestrictedBeansWrapper(
			_restrictedBeansWrapper);
	}

	private void _initAsyncRender(BundleContext bundleContext) {
		if (_freeMarkerEngineConfiguration.asyncRenderTimeout() <= 0) {
			return;
		}

		_noticeableExecutorService = _portalExecutorManager.getPortalExecutor(
			FreeMarkerManager.class.getName());
		_serviceRegistration = bundleContext.registerService(
			PortalExecutorConfig.class,
			new PortalExecutorConfig(
				FreeMarkerManager.class.getName(), 1,
				_freeMarkerEngineConfiguration.asyncRenderThreadPoolMaxSize(),
				60, TimeUnit.SECONDS,
				_freeMarkerEngineConfiguration.
					asyncRenderThreadPoolMaxQueueSize(),
				new NamedThreadFactory(
					FreeMarkerManager.class.getName(), Thread.NORM_PRIORITY,
					null),
				new ThreadPoolExecutor.AbortPolicy(),
				new ThreadPoolHandlerAdapter()),
			null);
		_timeoutTemplateCounters = new ConcurrentHashMap<>();
	}

	private boolean _isEnableDebuggerService() {
		if ((System.getProperty("freemarker.debug.password") != null) &&
			(System.getProperty("freemarker.debug.port") != null)) {

			return true;
		}

		return false;
	}

	private static final String _LIFERAY_MACRO_LIBRARY =
		"FTL_liferay.ftl as liferay";

	private static final Log _log = LogFactoryUtil.getLog(
		FreeMarkerManager.class);

	private static final Function<InvocationHandler, ServletContext>
		_servletContextProxyProviderFunction =
			ProxyUtil.getProxyProviderFunction(ServletContext.class);

	private Bundle _bundle;
	private BundleTracker<ClassLoader> _bundleTracker;
	private volatile Configuration _configuration;
	private volatile BeansWrapper _defaultBeansWrapper;
	private volatile FreeMarkerBundleClassloader _freeMarkerBundleClassloader;
	private volatile FreeMarkerEngineConfiguration
		_freeMarkerEngineConfiguration;
	private FreeMarkerTemplateResourceCache _freeMarkerTemplateResourceCache;
	private volatile FreeMarkerTemplateResourceLoader
		_freeMarkerTemplateResourceLoader;
	private volatile NoticeableExecutorService _noticeableExecutorService;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

	private volatile BeansWrapper _restrictedBeansWrapper;
	private volatile ServiceRegistration<PortalExecutorConfig>
		_serviceRegistration;
	private final Map<ClassLoader, Map<String, String>> _taglibMappings =
		new ConcurrentHashMap<>();

	@Reference
	private TemplateClassResolver _templateClassResolver;

	@Reference(
		target = "(component.name=com.liferay.portal.template.freemarker.internal.helper.FreeMarkerTemplateContextHelper)"
	)
	private TemplateContextHelper _templateContextHelper;

	private final Map<String, TemplateModel> _templateModels =
		new ConcurrentHashMap<>();
	private ServiceRegistration<TemplateResourceLoader>
		_templateResourceLoaderServiceRegistration;
	private volatile Map<String, AtomicInteger> _timeoutTemplateCounters;

	private static class ThreadLocalUtil {

		private static void _clearThreadLocals(Object threadLocals)
			throws Exception {

			_sizeField.set(threadLocals, 0);
			_tableField.set(threadLocals, Array.newInstance(_ENTRY_CLASS, 0));
			_thresholdField.set(threadLocals, 0);
		}

		private static Object _cloneThreadLocals(Thread thread)
			throws Exception {

			Object threadLocals = _threadLocalsField.get(thread);

			Object table = _tableField.get(threadLocals);

			int length = Array.getLength(table);

			try {
				_tableField.set(
					threadLocals, Array.newInstance(_ENTRY_CLASS, length));

				Object clonedThreadLocals = _createInheritedMapMethod.invoke(
					null, threadLocals);

				System.arraycopy(
					table, 0, _tableField.get(clonedThreadLocals), 0, length);

				_sizeField.set(
					clonedThreadLocals, _sizeField.get(threadLocals));

				return clonedThreadLocals;
			}
			finally {
				_tableField.set(threadLocals, table);
			}
		}

		private static void _setThreadLocals(Thread thread, Object threadLocals)
			throws Exception {

			_threadLocalsField.set(thread, threadLocals);
		}

		private static final Class<?> _ENTRY_CLASS;

		private static final Method _createInheritedMapMethod;
		private static final Field _sizeField;
		private static final Field _tableField;
		private static final Field _threadLocalsField;
		private static final Field _thresholdField;

		static {
			try {
				_threadLocalsField = ReflectionUtil.getDeclaredField(
					Thread.class, "threadLocals");

				Class<?> threadLocalMapClass = _threadLocalsField.getType();

				_createInheritedMapMethod = ReflectionUtil.getDeclaredMethod(
					ThreadLocal.class, "createInheritedMap",
					threadLocalMapClass);

				_sizeField = ReflectionUtil.getDeclaredField(
					threadLocalMapClass, "size");
				_tableField = ReflectionUtil.getDeclaredField(
					threadLocalMapClass, "table");
				_thresholdField = ReflectionUtil.getDeclaredField(
					threadLocalMapClass, "threshold");

				Class<?> tableFieldType = _tableField.getType();

				_ENTRY_CLASS = tableFieldType.getComponentType();
			}
			catch (Exception exception) {
				throw new ExceptionInInitializerError(exception);
			}
		}

	}

	private class ServletContextInvocationHandler implements InvocationHandler {

		public ServletContextInvocationHandler(
			FreeMarkerBundleClassloader freeMarkerBundleClassloader,
			ServletContext servletContext) {

			_freeMarkerBundleClassloader = freeMarkerBundleClassloader;
			_servletContext = servletContext;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			String methodName = method.getName();

			if (methodName.equals("getClassLoader")) {
				return _freeMarkerBundleClassloader;
			}
			else if (methodName.equals("getResource")) {
				return _getResource((String)args[0]);
			}
			else if (methodName.equals("getResourceAsStream")) {
				return _getResourceAsInputStream((String)args[0]);
			}
			else if (methodName.equals("getResourcePaths")) {
				return _getResourcePaths((String)args[0]);
			}

			return method.invoke(_servletContext, args);
		}

		private URL _getExtension(String path) {
			Enumeration<URL> enumeration = _bundle.findEntries(
				"META-INF/resources", path.substring(1), false);

			if (enumeration == null) {
				return null;
			}

			List<URL> urls = Collections.list(enumeration);

			return urls.get(urls.size() - 1);
		}

		private URL _getResource(String path) {
			if (path.charAt(0) != '/') {
				path = '/' + path;
			}

			URL url = _getExtension(path);

			if (url != null) {
				return url;
			}

			url = _freeMarkerBundleClassloader.getResource(path);

			if (url != null) {
				return url;
			}

			if (path.startsWith("/WEB-INF/tld/")) {
				String adaptedPath =
					"/META-INF/" + path.substring("/WEB-INF/tld/".length());

				url = _getExtension(adaptedPath);

				if (url == null) {
					url = _bundle.getResource(adaptedPath);
				}
			}

			if (url != null) {
				return url;
			}

			if (!path.startsWith("/META-INF/") &&
				!path.startsWith("/WEB-INF/")) {

				url = _bundle.getResource("/META-INF/resources" + path);
			}

			return url;
		}

		private InputStream _getResourceAsInputStream(String path) {
			URL url = _getResource(path);

			if (url == null) {
				return null;
			}

			try {
				return url.openStream();
			}
			catch (IOException ioException) {
				if (_log.isDebugEnabled()) {
					_log.debug(ioException);
				}

				return null;
			}
		}

		private Set<String> _getResourcePaths(String path) {
			Enumeration<URL> enumeration = _bundle.findEntries(
				path, null, true);

			if (enumeration == null) {
				return null;
			}

			Set<String> resourcePaths = new HashSet<>();

			while (enumeration.hasMoreElements()) {
				URL url = enumeration.nextElement();

				resourcePaths.add(url.getPath());
			}

			return resourcePaths;
		}

		private final FreeMarkerBundleClassloader _freeMarkerBundleClassloader;
		private final ServletContext _servletContext;

	}

	private class TaglibBundleTrackerCustomizer
		implements BundleTrackerCustomizer<ClassLoader> {

		@Override
		public ClassLoader addingBundle(
			Bundle bundle, BundleEvent bundleEvent) {

			URL url = bundle.getEntry("/META-INF/taglib-mappings.properties");

			if (url == null) {
				return null;
			}

			try {
				Properties properties = PropertiesUtil.load(url);

				@SuppressWarnings("unchecked")
				Map<String, String> map = PropertiesUtil.toMap(properties);

				if (map.isEmpty()) {
					return null;
				}

				BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

				_taglibMappings.put(bundleWiring.getClassLoader(), map);

				_freeMarkerBundleClassloader = null;

				return bundleWiring.getClassLoader();
			}
			catch (Exception exception) {
				_log.error(exception);
			}

			return null;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent, ClassLoader classLoader) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent, ClassLoader classLoader) {

			_taglibMappings.remove(classLoader);

			_templateModels.clear();

			_freeMarkerBundleClassloader = null;
		}

	}

	private class TaglibFactoryWrapper implements TemplateHashModel {

		public TaglibFactoryWrapper(
			FreeMarkerBundleClassloader freeMarkerBundleClassloader,
			ObjectWrapper objectWrapper, ServletContext servletContext) {

			_freeMarkerBundleClassloader = freeMarkerBundleClassloader;

			_taglibFactory = new TaglibFactory(
				_servletContextProxyProviderFunction.apply(
					new ServletContextInvocationHandler(
						_freeMarkerBundleClassloader, servletContext)));

			_taglibFactory.setObjectWrapper(objectWrapper);
		}

		@Override
		public TemplateModel get(String uri) throws TemplateModelException {
			TemplateModel templateModel = _templateModels.get(uri);

			if (templateModel == null) {
				try (SafeCloseable safeCloseable =
						ThreadContextClassLoaderUtil.swap(
							_freeMarkerBundleClassloader)) {

					templateModel = _taglibFactory.get(uri);
				}

				_templateModels.put(uri, templateModel);
			}

			return templateModel;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		private final FreeMarkerBundleClassloader _freeMarkerBundleClassloader;
		private final TaglibFactory _taglibFactory;

	}

}