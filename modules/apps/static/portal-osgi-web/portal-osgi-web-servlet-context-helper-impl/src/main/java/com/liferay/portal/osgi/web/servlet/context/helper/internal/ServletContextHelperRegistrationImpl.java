/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.context.helper.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.events.ShutdownHelperUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.PortletServlet;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.osgi.web.servlet.JSPServletFactory;
import com.liferay.portal.osgi.web.servlet.context.helper.ServletContextHelperRegistration;
import com.liferay.portal.osgi.web.servlet.context.helper.definition.WebXMLDefinition;
import com.liferay.portal.osgi.web.servlet.context.helper.internal.definition.WebXMLDefinitionLoader;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.management.ManagementFactory;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import javax.xml.parsers.SAXParserFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;

/**
 * @author Raymond Augé
 */
public class ServletContextHelperRegistrationImpl
	implements ServletContextHelperRegistration {

	public ServletContextHelperRegistrationImpl(
		Bundle bundle, JSPServletFactory jspServletFactory,
		SAXParserFactory saxParserFactory, Map<String, Object> properties,
		ExecutorService executorService) {

		_bundle = bundle;
		_jspServletFactory = jspServletFactory;
		_properties = properties;
		_executorService = executorService;

		String contextPath = _getContextPath();

		_servletContextName = getServletContextName(contextPath);

		URL url = bundle.getEntry("WEB-INF/");

		if (url != null) {
			_annotatedClasses = new HashSet<>();
			_classes = _loadClasses(bundle);
			_wabShapedBundle = true;

			WebXMLDefinitionLoader webXMLDefinitionLoader =
				new WebXMLDefinitionLoader(
					_bundle, _jspServletFactory, saxParserFactory, _classes,
					_annotatedClasses);

			WebXMLDefinition webXMLDefinition = null;

			try {
				webXMLDefinition = webXMLDefinitionLoader.loadWebXML();
			}
			catch (Exception exception) {
				webXMLDefinition = new WebXMLDefinition();

				webXMLDefinition.setException(exception);
			}

			_webXMLDefinition = webXMLDefinition;
		}
		else {
			_annotatedClasses = Collections.emptySet();
			_classes = Collections.emptySet();
			_wabShapedBundle = false;
			_webXMLDefinition = new WebXMLDefinition();
		}

		_bundleContext = _bundle.getBundleContext();

		_customServletContextHelper = new CustomServletContextHelper(
			_bundle, _webXMLDefinition.getWebResourceCollectionDefinitions());

		_servletContextHelperServiceRegistration = _createServletContextHelper(
			contextPath);

		_servletContextListenerServiceRegistration =
			_createServletContextListener();

		_registerServletContext();

		_defaultServletServiceRegistration = _createDefaultServlet();

		_jspServletServiceRegistration = _createJspServlet();

		_portletServletServiceRegistration = _createPortletServlet();
	}

	@Override
	public void close() {
		try {
			_servletContextServiceRegistration.unregister();
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}

			// Ignore since the service has been unregistered

		}

		try {
			_servletContextHelperServiceRegistration.unregister();
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}

			// Ignore since the service has been unregistered

		}

		try {
			_servletContextListenerServiceRegistration.unregister();
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}

			// Ignore since the service has been unregistered

		}

		try {
			_defaultServletServiceRegistration.unregister();
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}

			// Ignore since the service has been unregistered

		}

		try {
			_jspServletServiceRegistration.unregister();
		}
		catch (IllegalStateException illegalStateException) {
			if (_log.isDebugEnabled()) {
				_log.debug(illegalStateException);
			}

			// Ignore since the service has been unregistered

		}

		if (_portletServletServiceRegistration != null) {
			try {
				_portletServletServiceRegistration.unregister();
			}
			catch (IllegalStateException illegalStateException) {
				if (_log.isDebugEnabled()) {
					_log.debug(illegalStateException);
				}

				// Ignore since the service has been unregistered

			}
		}

		if (!ShutdownHelperUtil.isShutdown()) {
			BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

			_clearResidualMBeans(bundleWiring.getClassLoader());
		}
	}

	@Override
	public Set<Class<?>> getAnnotatedClasses() {
		return _annotatedClasses;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return _classes;
	}

	@Override
	public ServletContext getServletContext() {
		return _customServletContextHelper.getServletContext();
	}

	@Override
	public WebXMLDefinition getWebXMLDefinition() {
		return _webXMLDefinition;
	}

	@Override
	public boolean isWabShapedBundle() {
		return _wabShapedBundle;
	}

	@Override
	public void setProperties(Map<String, String> contextParameters) {
		if (contextParameters.isEmpty()) {
			return;
		}

		ServiceReference<ServletContextHelper> serviceReference =
			_servletContextHelperServiceRegistration.getReference();

		Dictionary<String, Object> properties = new Hashtable<>();

		for (String key : serviceReference.getPropertyKeys()) {
			properties.put(key, serviceReference.getProperty(key));
		}

		for (Map.Entry<String, String> entry : contextParameters.entrySet()) {
			String key = entry.getKey();

			properties.put(
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX + key,
				entry.getValue());
		}

		_servletContextHelperServiceRegistration.setProperties(properties);
	}

	protected String getServletContextName(String contextPath) {
		Dictionary<String, String> headers = _bundle.getHeaders(
			StringPool.BLANK);

		String header = headers.get("Web-ContextName");

		if (Validator.isNotNull(header)) {
			return header;
		}

		return contextPath.substring(1);
	}

	private void _clearResidualMBeans(ClassLoader classLoader) {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		for (ObjectName objectName : mBeanServer.queryNames(null, null)) {
			try {
				if (classLoader.equals(
						mBeanServer.getClassLoaderFor(objectName))) {

					mBeanServer.unregisterMBean(objectName);
				}
			}
			catch (InstanceNotFoundException instanceNotFoundException) {
				if (_log.isDebugEnabled()) {
					_log.debug(instanceNotFoundException);
				}
			}
			catch (JMException jmException) {
				_log.error(jmException);
			}
		}
	}

	private boolean _contains(String[] array, String classResource) {
		int index = Arrays.binarySearch(array, classResource);

		if (index >= -1) {
			return false;
		}

		return classResource.startsWith(array[-index - 2]);
	}

	private ServiceRegistration<?> _createDefaultServlet() {
		String prefix = "/META-INF/resources";

		if (_wabShapedBundle) {
			prefix = "/";
		}

		return _bundleContext.registerService(
			Object.class, new Object(),
			HashMapDictionaryBuilder.<String, Object>put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				_servletContextName
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PATTERN, "/*"
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_RESOURCE_PREFIX, prefix
			).build());
	}

	private ServiceRegistration<Servlet> _createJspServlet() {
		Dictionary<String, Object> properties = new HashMapDictionary<>();

		for (Map.Entry<String, Object> entry : _properties.entrySet()) {
			String key = entry.getKey();

			if (!key.startsWith(_JSP_SERVLET_INIT_PARAM_PREFIX)) {
				continue;
			}

			String name =
				_SERVLET_INIT_PARAM_PREFIX +
					key.substring(_JSP_SERVLET_INIT_PARAM_PREFIX.length());

			properties.put(name, entry.getValue());
		}

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
			_servletContextName);
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
			JspServletWrapper.class.getName());
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
			new String[] {"*.jsp", "*.jspx"});

		return _bundleContext.registerService(
			Servlet.class, _jspServletFactory.createJSPServlet(), properties);
	}

	private ServiceRegistration<Servlet> _createPortletServlet() {
		if (_wabShapedBundle) {
			return null;
		}

		return _bundleContext.registerService(
			Servlet.class,
			new PortletServlet() {
			},
			HashMapDictionaryBuilder.<String, Object>put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				_servletContextName
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME,
				PortletServlet.class.getName()
			).put(
				HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN,
				"/portlet-servlet/*"
			).build());
	}

	private ServiceRegistration<ServletContextHelper>
		_createServletContextHelper(String contextPath) {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME,
			_servletContextName);
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, contextPath);

		Map<String, String> contextParameters =
			_webXMLDefinition.getContextParameters();

		properties.put(
			"rtl.required", String.valueOf(_isRTLRequired(contextParameters)));

		for (Map.Entry<String, String> entry : contextParameters.entrySet()) {
			String key =
				HttpWhiteboardConstants.
					HTTP_WHITEBOARD_CONTEXT_INIT_PARAM_PREFIX + entry.getKey();

			properties.put(key, entry.getValue());
		}

		return _bundleContext.registerService(
			ServletContextHelper.class, _customServletContextHelper,
			properties);
	}

	private ServiceRegistration<ServletContextListener>
		_createServletContextListener() {

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
			_servletContextName);
		properties.put(
			HttpWhiteboardConstants.HTTP_WHITEBOARD_LISTENER,
			Boolean.TRUE.toString());

		return _bundleContext.registerService(
			ServletContextListener.class, _customServletContextHelper,
			properties);
	}

	private String _getContextPath() {
		Dictionary<String, String> headers = _bundle.getHeaders(
			StringPool.BLANK);

		String contextPath = headers.get("Web-ContextPath");

		if (Validator.isNotNull(contextPath)) {
			return contextPath;
		}

		return '/' + _bundle.getSymbolicName();
	}

	private boolean _isRTLRequired(Map<String, String> contextParameters) {
		String rtlRequired = contextParameters.get("rtl.required");

		if (Validator.isNotNull(rtlRequired)) {
			return GetterUtil.getBoolean(rtlRequired);
		}

		Dictionary<String, String> headers = _bundle.getHeaders(
			StringPool.BLANK);

		rtlRequired = headers.get("Liferay-RTL-Support-Required");

		if (Validator.isNotNull(rtlRequired)) {
			return GetterUtil.getBoolean(rtlRequired);
		}

		return true;
	}

	private Set<Class<?>> _loadClasses(Bundle bundle) {
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		ClassLoader classLoader = bundleWiring.getClassLoader();

		Set<Class<?>> classes = new HashSet<>();

		File annotatedClassesFile = _bundle.getDataFile("annotated.classes");

		if (annotatedClassesFile.exists()) {
			Properties properties = new Properties();

			try (InputStream inputStream = new FileInputStream(
					annotatedClassesFile)) {

				properties.load(inputStream);
			}
			catch (IOException ioException) {
				if (_log.isDebugEnabled()) {
					_log.debug(ioException);
				}
			}

			if (_bundle.getLastModified() == GetterUtil.getLong(
					properties.get("last.modified"))) {

				boolean failed = false;

				for (String className :
						StringUtil.split(
							properties.getProperty("annotated.classes"))) {

					try {
						classes.add(classLoader.loadClass(className));
					}
					catch (ClassNotFoundException classNotFoundException) {
						if (_log.isDebugEnabled()) {
							_log.debug(classNotFoundException);
						}

						failed = true;

						break;
					}
				}

				if (!failed) {
					return classes;
				}
			}
		}

		Collection<String> classResources = bundleWiring.listResources(
			"/", "*.class", BundleWiring.LISTRESOURCES_RECURSE);

		Iterator<String> iterator = classResources.iterator();

		while (iterator.hasNext()) {
			String classResource = iterator.next();

			if (_contains(_WHITELIST, classResource)) {
				continue;
			}

			if (_contains(_BLACKLIST, classResource)) {
				iterator.remove();
			}
		}

		if (classResources == null) {
			return Collections.emptySet();
		}

		List<Future<Class<?>>> futures = new ArrayList<>();

		for (String classResource : classResources) {
			futures.add(
				_executorService.submit(
					() -> {
						String className = classResource.substring(
							0, classResource.length() - 6);

						className = StringUtil.replace(
							className, CharPool.SLASH, CharPool.PERIOD);

						return classLoader.loadClass(className);
					}));
		}

		for (Future<Class<?>> future : futures) {
			try {
				classes.add(future.get());
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		return classes;
	}

	private void _registerServletContext() {
		ServletContext servletContext =
			_customServletContextHelper.getServletContext();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"osgi.web.contextname", servletContext.getServletContextName()
			).put(
				"osgi.web.contextpath", servletContext.getContextPath()
			).put(
				"osgi.web.symbolicname", _bundle.getSymbolicName()
			).put(
				"osgi.web.version", _bundle.getVersion()
			).build();

		_servletContextServiceRegistration = _bundleContext.registerService(
			ServletContext.class, servletContext, properties);
	}

	private static final String[] _BLACKLIST;

	private static final String _JSP_SERVLET_INIT_PARAM_PREFIX =
		"jsp.servlet.init.param.";

	private static final String _SERVLET_INIT_PARAM_PREFIX =
		HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_INIT_PARAM_PREFIX;

	private static final String[] _WHITELIST;

	private static final Log _log = LogFactoryUtil.getLog(
		ServletContextHelperRegistrationImpl.class);

	static {
		String[] blacklist =
			PropsValues.
				MODULE_FRAMEWORK_WEB_SERVLET_ANNOTATION_SCANNING_BLACKLIST;

		blacklist = Arrays.copyOf(blacklist, blacklist.length);

		Arrays.sort(blacklist);

		_BLACKLIST = blacklist;

		String[] whitelist =
			PropsValues.
				MODULE_FRAMEWORK_WEB_SERVLET_ANNOTATION_SCANNING_WHITELIST;

		whitelist = Arrays.copyOf(whitelist, whitelist.length);

		Arrays.sort(whitelist);

		_WHITELIST = whitelist;
	}

	private final Set<Class<?>> _annotatedClasses;
	private final Bundle _bundle;
	private final BundleContext _bundleContext;
	private final Set<Class<?>> _classes;
	private final CustomServletContextHelper _customServletContextHelper;
	private final ServiceRegistration<?> _defaultServletServiceRegistration;
	private final ExecutorService _executorService;
	private final JSPServletFactory _jspServletFactory;
	private final ServiceRegistration<Servlet> _jspServletServiceRegistration;
	private final ServiceRegistration<Servlet>
		_portletServletServiceRegistration;
	private final Map<String, Object> _properties;
	private final ServiceRegistration<ServletContextHelper>
		_servletContextHelperServiceRegistration;
	private final ServiceRegistration<ServletContextListener>
		_servletContextListenerServiceRegistration;
	private final String _servletContextName;
	private ServiceRegistration<ServletContext>
		_servletContextServiceRegistration;
	private final boolean _wabShapedBundle;
	private final WebXMLDefinition _webXMLDefinition;

}