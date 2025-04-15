/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.taglib.servlet.JspFactorySwapper;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.apache.jasper.runtime.JspFactoryImpl;
import org.apache.jasper.runtime.TagHandlerPool;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Raymond Augé
 */
public class JspServlet extends HttpServlet {

	public JspServlet(Set<String> fragmentHosts) {
		_fragmentHosts = fragmentHosts;
	}

	@Override
	public void destroy() {
		_jspServlet.destroy();

		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}

		_serviceRegistrations.clear();
	}

	@Override
	public boolean equals(Object object) {
		return _jspServlet.equals(object);
	}

	@Override
	public String getInitParameter(String name) {
		return _jspServlet.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return _jspServlet.getInitParameterNames();
	}

	@Override
	public ServletConfig getServletConfig() {
		return _jspServlet.getServletConfig();
	}

	@Override
	public ServletContext getServletContext() {
		return _jspServlet.getServletContext();
	}

	@Override
	public String getServletInfo() {
		return _jspServlet.getServletInfo();
	}

	@Override
	public String getServletName() {
		return _jspServlet.getServletName();
	}

	@Override
	public int hashCode() {
		return _jspServlet.hashCode();
	}

	@Override
	public void init() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void init(final ServletConfig servletConfig)
		throws ServletException {

		final ServletContext servletContext = servletConfig.getServletContext();

		servletContext.setAttribute(
			InstanceManager.class.getName(), new JspBundleInstanceManager());

		ClassLoader classLoader = servletContext.getClassLoader();

		if (!(classLoader instanceof BundleReference)) {
			throw new IllegalStateException();
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				classLoader)) {

			JspFactory.setDefaultFactory(new JspFactoryImpl());

			JspFactorySwapper.swap();
		}

		List<Bundle> bundles = new ArrayList<>();

		BundleReference bundleReference = (BundleReference)classLoader;

		_bundle = bundleReference.getBundle();

		bundles.add(_bundle);

		bundles.add(_jspBundle);

		bundles.add(_utilTaglibBundle);

		_collectTaglibProviderBundles(bundles);

		_allParticipatingBundles = bundles.toArray(new Bundle[0]);

		_jspBundleClassloader = new JspBundleClassloader(
			_allParticipatingBundles);

		File scratchDir = new File(
			StringBundler.concat(
				_WORK_DIR, _bundle.getSymbolicName(), StringPool.DASH,
				_bundle.getVersion(), StringPool.SLASH));

		scratchDir.mkdirs();

		Map<String, String> defaults = HashMapBuilder.put(
			_INIT_PARAMETER_NAME_SCRATCH_DIR, scratchDir.getPath()
		).put(
			"compilerClassName",
			"com.liferay.portal.jsp.engine.internal.compiler.BridgeCompiler"
		).put(
			"compilerSourceVM", "1.8"
		).put(
			"compilerTargetVM", "1.8"
		).put(
			"development", "false"
		).put(
			"keepgenerated", "false"
		).put(
			"strictQuoteEscaping", "false"
		).build();

		if (_fragmentHosts.contains(_bundle.getSymbolicName())) {
			defaults.put("hasFragment", "true");
		}

		defaults.put(
			TagHandlerPool.OPTION_TAGPOOL, JspTagHandlerPool.class.getName());

		for (Map.Entry<Object, Object> entry : _initParams.entrySet()) {
			defaults.put(
				String.valueOf(entry.getKey()),
				String.valueOf(entry.getValue()));
		}

		Set<String> nameSet = new HashSet<>(
			Collections.list(servletConfig.getInitParameterNames()));

		nameSet.addAll(defaults.keySet());

		final Enumeration<String> enumeration = Collections.enumeration(
			nameSet);

		_jspServlet.init(
			new ServletConfig() {

				@Override
				public String getInitParameter(String name) {
					String value = servletConfig.getInitParameter(name);

					if (value == null) {
						value = defaults.get(name);
					}

					return value;
				}

				@Override
				public Enumeration<String> getInitParameterNames() {
					return enumeration;
				}

				@Override
				public ServletContext getServletContext() {
					return _jspServletContext;
				}

				@Override
				public String getServletName() {
					return servletConfig.getServletName();
				}

				private final ServletContext _jspServletContext =
					ProxyUtil.newDelegateProxyInstance(
						ServletContext.class.getClassLoader(),
						ServletContext.class,
						new ServletContextDelegate(servletContext),
						servletContext);

			});

		_logVerbosityLevelDebug = Objects.equals(
			_jspServlet.getInitParameter("logVerbosityLevel"), "DEBUG");
	}

	@Override
	public void log(String msg) {
		_jspServlet.log(msg);
	}

	@Override
	public void log(String message, Throwable throwable) {
		_jspServlet.log(message, throwable);
	}

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				_jspBundleClassloader)) {

			if (_logVerbosityLevelDebug) {
				String path = (String)httpServletRequest.getAttribute(
					RequestDispatcher.INCLUDE_SERVLET_PATH);

				if (path != null) {
					String pathInfo = (String)httpServletRequest.getAttribute(
						RequestDispatcher.INCLUDE_PATH_INFO);

					if (pathInfo != null) {
						path += pathInfo;
					}
				}
				else {
					path = httpServletRequest.getServletPath();

					String pathInfo = httpServletRequest.getPathInfo();

					if (pathInfo != null) {
						path += pathInfo;
					}
				}

				_jspServlet.log(
					StringBundler.concat(
						"[JSP DEBUG] ", _bundle, " invoking ", path));
			}

			_jspServlet.service(httpServletRequest, httpServletResponse);
		}
	}

	@Override
	public void service(
			ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException {

		service(
			(HttpServletRequest)servletRequest,
			(HttpServletResponse)servletResponse);
	}

	@Override
	public String toString() {
		return _jspServlet.toString();
	}

	private void _collectTaglibProviderBundles(List<Bundle> bundles) {
		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		for (BundleWire bundleWire :
				bundleWiring.getRequiredWires("osgi.extender")) {

			BundleCapability bundleCapability = bundleWire.getCapability();

			Map<String, Object> attributes = bundleCapability.getAttributes();

			Object value = attributes.get("osgi.extender");

			if (value.equals("jsp.taglib")) {
				BundleRevision bundleRevision = bundleWire.getProvider();

				Bundle bundle = bundleRevision.getBundle();

				if (!bundles.contains(bundle)) {
					bundles.add(bundle);
				}
			}
		}
	}

	private static final String _DIR_NAME_RESOURCES = "/META-INF/resources";

	private static final String _INIT_PARAMETER_NAME_SCRATCH_DIR = "scratchdir";

	private static final String _WORK_DIR = StringBundler.concat(
		PropsValues.LIFERAY_HOME, File.separator, "work", File.separator);

	private static final Log _log = LogFactoryUtil.getLog(JspServlet.class);

	private static final MethodHandle _defineClassMethodHandle;
	private static final Properties _initParams = PropsUtil.getProperties(
		"jsp.servlet.init.param.", true);
	private static final Bundle _jspBundle = FrameworkUtil.getBundle(
		JspServlet.class);
	private static final Pattern _originalJspPattern = Pattern.compile(
		"^(?<file>.*)(\\.(portal|original))(?<extension>\\.(jsp|jspf))$");
	private static final Bundle _utilTaglibBundle = FrameworkUtil.getBundle(
		JspFactorySwapper.class);

	static {
		try {
			MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

			_defineClassMethodHandle = lookup.findVirtual(
				ClassLoader.class, "defineClass",
				MethodType.methodType(
					Class.class, String.class, byte[].class, int.class,
					int.class));
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

	private Bundle[] _allParticipatingBundles;
	private Bundle _bundle;
	private final Set<String> _fragmentHosts;
	private JspBundleClassloader _jspBundleClassloader;
	private final HttpServlet _jspServlet =
		new org.apache.jasper.servlet.JspServlet();
	private boolean _logVerbosityLevelDebug;
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new CopyOnWriteArrayList<>();

	private static class JspBundleInstanceManager
		extends SimpleInstanceManager {

		@Override
		public Object newInstance(String className, ClassLoader classLoader)
			throws ClassNotFoundException, IllegalAccessException,
				   InstantiationException, InvocationTargetException,
				   NamingException, NoSuchMethodException {

			Class<?> clazz = null;

			try {
				clazz = classLoader.loadClass(className);
			}
			catch (ClassNotFoundException classNotFoundException) {
				ClassLoader parentClassLoader = classLoader.getParent();

				String resourceName = StringUtil.replace(
					className, CharPool.PERIOD, CharPool.SLASH);

				URL url = parentClassLoader.getResource(
					resourceName + ".class");

				if (url == null) {
					throw classNotFoundException;
				}

				clazz = _loadClass(
					className, classLoader, classNotFoundException, url);

				if (clazz == null) {
					throw classNotFoundException;
				}

				// Preload inner classes

				List<URL> innerClassURLs = _getInnerClassURLs(
					url, resourceName);

				for (URL innerClassURL : innerClassURLs) {
					_loadClass(
						_getClassName(innerClassURL), classLoader,
						classNotFoundException, innerClassURL);
				}

				if (ArrayUtil.isNotEmpty(
						classNotFoundException.getSuppressed())) {

					throw classNotFoundException;
				}
			}

			Constructor<?> constructor = clazz.getConstructor();

			return constructor.newInstance();
		}

		private long _extractBundleId(URL url) {
			String path = url.getHost();

			String[] strings = StringUtil.split(path, CharPool.PERIOD);

			if (strings.length > 1) {
				return GetterUtil.getLong(strings[0]);
			}

			return -1;
		}

		private String _getClassName(URL url) {
			String path = url.getPath();

			if (path.startsWith(StringPool.SLASH)) {
				path = path.substring(1);
			}

			path = path.substring(0, path.indexOf(".class"));

			return StringUtil.replace(path, CharPool.SLASH, CharPool.PERIOD);
		}

		private List<URL> _getInnerClassURLs(URL url, String resourceName) {
			String protocol = url.getProtocol();

			if (protocol.equals("bundle") ||
				protocol.equals("bundleresource")) {

				BundleContext bundleContext = _jspBundle.getBundleContext();

				long bundleId = _extractBundleId(url);

				Bundle bundle = bundleContext.getBundle(bundleId);

				if (bundle == null) {
					return Collections.emptyList();
				}

				int index = resourceName.lastIndexOf(CharPool.SLASH);

				Enumeration<URL> urlEnumeration = bundle.findEntries(
					resourceName.substring(0, index),
					resourceName.substring(index + 1) + "$*.class", false);

				if (urlEnumeration == null) {
					return Collections.emptyList();
				}

				return ListUtil.fromEnumeration(urlEnumeration);
			}

			return Collections.emptyList();
		}

		private Class<?> _loadClass(
			String className, ClassLoader classLoader,
			ClassNotFoundException classNotFoundException, URL url) {

			Class<?> clazz = null;

			try {
				byte[] bytes = StreamUtil.toByteArray(url.openStream());

				if (bytes != null) {
					clazz = (Class<?>)_defineClassMethodHandle.invokeExact(
						classLoader, className, bytes, 0, bytes.length);
				}
			}
			catch (Throwable throwable) {
				classNotFoundException.addSuppressed(throwable);
			}

			return clazz;
		}

	}

	private class ServletContextDelegate {

		public ClassLoader getClassLoader() {
			return _jspBundleClassloader;
		}

		public String getContextPath() {
			return _contextPath;
		}

		public URL getResource(String path) {
			try {
				if ((path == null) || path.equals(StringPool.BLANK)) {
					return null;
				}

				if (path.charAt(0) != '/') {
					path = '/' + path;
				}

				URL url = _getExtension(path);

				if (url != null) {
					return url;
				}

				url = _servletContext.getResource(path);

				if (url != null) {
					return url;
				}

				ClassLoader classLoader = _servletContext.getClassLoader();

				url = classLoader.getResource(path);

				if (url != null) {
					return url;
				}

				if (!path.startsWith("/META-INF/")) {
					url = _servletContext.getResource(
						_DIR_NAME_RESOURCES.concat(path));
				}

				if (url != null) {
					return url;
				}

				for (int i = 2; i < _allParticipatingBundles.length; i++) {
					url = _allParticipatingBundles[i].getEntry(path);

					if (url != null) {
						return url;
					}
				}

				return _jspBundle.getResource(path);
			}
			catch (MalformedURLException malformedURLException) {
				if (_log.isDebugEnabled()) {
					_log.debug(malformedURLException);
				}
			}

			return null;
		}

		public InputStream getResourceAsStream(String path) {
			URL url = getResource(path);

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

		public Set<String> getResourcePaths(String path) {
			Set<String> paths = _servletContext.getResourcePaths(path);

			Enumeration<URL> enumeration = _jspBundle.findEntries(
				path, null, false);

			if (enumeration != null) {
				if ((paths == null) && enumeration.hasMoreElements()) {
					paths = new HashSet<>();
				}

				while (enumeration.hasMoreElements()) {
					URL url = enumeration.nextElement();

					paths.add(url.getPath());
				}
			}

			return paths;
		}

		public String getServletContextName() {
			return _servletContextName;
		}

		private ServletContextDelegate(ServletContext servletContext) {
			_servletContext = servletContext;

			_contextPath = servletContext.getContextPath();
			_servletContextName = servletContext.getServletContextName();
		}

		private URL _getExtension(String path) {
			Matcher matcher = _originalJspPattern.matcher(path);

			if (matcher.matches()) {
				path = matcher.group("file") + matcher.group("extension");

				return _bundle.getEntry(_DIR_NAME_RESOURCES + path);
			}

			Enumeration<URL> enumeration = _bundle.findEntries(
				_DIR_NAME_RESOURCES, path.substring(1), false);

			if (enumeration == null) {
				return null;
			}

			List<URL> urls = Collections.list(enumeration);

			return urls.get(urls.size() - 1);
		}

		private final String _contextPath;
		private final ServletContext _servletContext;
		private final String _servletContextName;

	}

}