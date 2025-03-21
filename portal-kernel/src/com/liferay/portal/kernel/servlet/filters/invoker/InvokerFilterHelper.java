/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.servlet.LiferayFilter;
import com.liferay.portal.kernel.servlet.PluginContextListener;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.InstanceFactory;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.UnsecureSAXReaderUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
public class InvokerFilterHelper {

	public void clearFilterChainsCache() {
		for (InvokerFilter invokerFilter : _invokerFilters) {
			invokerFilter.clearFilterChainsCache();
		}
	}

	public void destroy() {
		_serviceTracker.close();

		for (FilterMapping[] filterMappings : _filterMappingsMap.values()) {
			FilterMapping filterMapping = filterMappings[0];

			Filter filter = filterMapping.getFilter();

			try {
				filter.destroy();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		_filterMappingsMap.clear();
		_filterNames.clear();
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			ServletContext servletContext = filterConfig.getServletContext();

			readLiferayFilterWebXML(servletContext, "/WEB-INF/liferay-web.xml");

			String servletContextName = GetterUtil.getString(
				servletContext.getServletContextName());

			String portalServletContextName =
				PortalUtil.getServletContextName();

			if (servletContextName.equals(portalServletContextName)) {
				servletContextName = StringPool.BLANK;
			}

			_serviceTracker = new ServiceTracker<>(
				_bundleContext,
				SystemBundleUtil.createFilter(
					StringBundler.concat(
						"(&(objectClass=", Filter.class.getName(),
						")(servlet-context-name=", servletContextName,
						")(servlet-filter-name=*))")),
				new FilterServiceTrackerCustomizer());

			_serviceTracker.open();
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new ServletException(exception);
		}
	}

	public void registerFilterMapping(
		FilterMapping filterMapping, String positionFilterName, boolean after) {

		String filterName = filterMapping.getFilterName();

		while (true) {
			FilterMapping[] oldFilterMappings = _filterMappingsMap.get(
				filterName);

			FilterMapping[] newFilterMappings = null;

			if (oldFilterMappings == null) {
				newFilterMappings = new FilterMapping[] {filterMapping};
			}
			else {
				newFilterMappings = Arrays.copyOf(
					oldFilterMappings, oldFilterMappings.length + 1);

				newFilterMappings[oldFilterMappings.length] = filterMapping;
			}

			if (newFilterMappings.length == 1) {
				FilterMapping[] filterMappings = _filterMappingsMap.putIfAbsent(
					filterName, newFilterMappings);

				if (filterMappings == null) {
					int index = _filterNames.indexOf(positionFilterName);

					if (index == -1) {
						_filterNames.add(filterName);
					}
					else if (after) {
						_filterNames.add(index + 1, filterName);
					}
					else {
						_filterNames.add(index, filterName);
					}

					break;
				}
			}
			else if (_filterMappingsMap.replace(
						filterName, oldFilterMappings, newFilterMappings)) {

				break;
			}
		}
	}

	public void unregisterFilterMapping(FilterMapping filterMapping) {
		String filterName = filterMapping.getFilterName();

		while (true) {
			FilterMapping[] oldFilterMappings = _filterMappingsMap.get(
				filterName);

			FilterMapping[] newFilterMappings = ArrayUtil.remove(
				oldFilterMappings, filterMapping);

			if (newFilterMappings.length == 0) {
				if (_filterMappingsMap.remove(filterName, oldFilterMappings)) {
					_filterNames.remove(filterName);

					break;
				}
			}
			else if (_filterMappingsMap.replace(
						filterName, oldFilterMappings, newFilterMappings)) {

				break;
			}
		}
	}

	public void unregisterFilterMappings(String filterName) {
		FilterMapping[] filterMappings = _filterMappingsMap.remove(filterName);

		if (filterMappings == null) {
			return;
		}

		FilterMapping filterMapping = filterMappings[0];

		Filter filter = filterMapping.getFilter();

		if (filter != null) {
			try {
				filter.destroy();
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		_filterNames.remove(filterName);

		clearFilterChainsCache();
	}

	public void updateFilterMappings(String filterName, Filter filter) {
		while (true) {
			FilterMapping[] oldFilterMappings = _filterMappingsMap.get(
				filterName);

			if (oldFilterMappings == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No filter mappings for filter name " + filterName);
				}

				return;
			}

			FilterMapping[] newFilterMappings =
				new FilterMapping[oldFilterMappings.length];

			for (int i = 0; i < oldFilterMappings.length; i++) {
				newFilterMappings[i] = oldFilterMappings[i].replaceFilter(
					filter);
			}

			if (_filterMappingsMap.replace(
					filterName, oldFilterMappings, newFilterMappings)) {

				break;
			}
		}
	}

	protected void addInvokerFilter(InvokerFilter invokerFilter) {
		_invokerFilters.add(invokerFilter);
	}

	protected InvokerFilterChain createInvokerFilterChain(
		HttpServletRequest httpServletRequest, Dispatcher dispatcher,
		String uri, FilterChain filterChain) {

		InvokerFilterChain invokerFilterChain = new InvokerFilterChain(
			filterChain);

		for (String filterName : _filterNames) {
			FilterMapping[] filterMappings = _filterMappingsMap.get(filterName);

			if (filterMappings == null) {
				continue;
			}

			for (FilterMapping filterMapping : filterMappings) {
				if (filterMapping.isMatch(
						httpServletRequest, dispatcher, uri)) {

					Filter filter = filterMapping.getFilter();

					if (filter instanceof LiferayFilter) {
						LiferayFilter liferayFilter = (LiferayFilter)filter;

						if (!liferayFilter.isFilterEnabled()) {
							continue;
						}
					}

					invokerFilterChain.addFilter(filter);
				}
			}
		}

		return invokerFilterChain;
	}

	protected Filter initFilter(
		ServletContext servletContext, String filterClassName,
		FilterConfig filterConfig) {

		ClassLoader pluginClassLoader =
			(ClassLoader)servletContext.getAttribute(
				PluginContextListener.PLUGIN_CLASS_LOADER);

		if (pluginClassLoader == null) {
			Thread currentThread = Thread.currentThread();

			pluginClassLoader = currentThread.getContextClassLoader();
		}

		ClassLoader portalClassLoader = PortalClassLoaderUtil.getClassLoader();

		if (portalClassLoader != pluginClassLoader) {
			pluginClassLoader = AggregateClassLoader.getAggregateClassLoader(
				portalClassLoader, pluginClassLoader);
		}

		try (SafeCloseable safeCloseable = ThreadContextClassLoaderUtil.swap(
				pluginClassLoader)) {

			Filter filter = (Filter)InstanceFactory.newInstance(
				pluginClassLoader, filterClassName);

			filter.init(filterConfig);

			return filter;
		}
		catch (Exception exception) {
			_log.error(
				"Unable to initialize filter " + filterClassName, exception);

			return null;
		}
	}

	protected void readLiferayFilterWebXML(
			ServletContext servletContext, String path)
		throws Exception {

		InputStream inputStream = servletContext.getResourceAsStream(path);

		if (inputStream == null) {
			return;
		}

		Document document = UnsecureSAXReaderUtil.read(inputStream, true);

		Element rootElement = document.getRootElement();

		Map<String, ObjectValuePair<Filter, FilterConfig>>
			filterObjectValuePairs = new HashMap<>();

		for (Element filterElement : rootElement.elements("filter")) {
			String filterName = filterElement.elementText("filter-name");
			String filterClassName = filterElement.elementText("filter-class");

			Map<String, String> initParameterMap = new HashMap<>();

			List<Element> initParamElements = filterElement.elements(
				"init-param");

			for (Element initParamElement : initParamElements) {
				String name = initParamElement.elementText("param-name");
				String value = initParamElement.elementText("param-value");

				initParameterMap.put(name, value);
			}

			FilterConfig filterConfig = new InvokerFilterConfig(
				servletContext, filterName, initParameterMap);

			Filter filter = initFilter(
				servletContext, filterClassName, filterConfig);

			if (filter != null) {
				filterObjectValuePairs.put(
					filterName, new ObjectValuePair<>(filter, filterConfig));
			}
		}

		List<Element> filterMappingElements = rootElement.elements(
			"filter-mapping");

		for (Element filterMappingElement : filterMappingElements) {
			String filterName = filterMappingElement.elementText("filter-name");

			List<String> urlPatterns = new ArrayList<>();

			List<Element> urlPatternElements = filterMappingElement.elements(
				"url-pattern");

			for (Element urlPatternElement : urlPatternElements) {
				urlPatterns.add(urlPatternElement.getTextTrim());
			}

			Set<Dispatcher> dispatchers = new HashSet<>();

			List<Element> dispatcherElements = filterMappingElement.elements(
				"dispatcher");

			for (Element dispatcherElement : dispatcherElements) {
				String dispatcher = StringUtil.toUpperCase(
					dispatcherElement.getTextTrim());

				dispatchers.add(Dispatcher.valueOf(dispatcher));
			}

			ObjectValuePair<Filter, FilterConfig> filterObjectValuePair =
				filterObjectValuePairs.get(filterName);

			if (filterObjectValuePair == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"No filter and filter config for filter name " +
							filterName);
				}

				continue;
			}

			FilterMapping filterMapping = new FilterMapping(
				filterName, filterObjectValuePair.getKey(),
				filterObjectValuePair.getValue(), urlPatterns, dispatchers);

			registerFilterMapping(filterMapping, null, true);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		InvokerFilterHelper.class);

	private final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private final ConcurrentMap<String, FilterMapping[]> _filterMappingsMap =
		new ConcurrentHashMap<>();
	private final List<String> _filterNames = new CopyOnWriteArrayList<>();
	private final List<InvokerFilter> _invokerFilters = new ArrayList<>();
	private ServiceTracker<Filter, FilterMapping> _serviceTracker;

	private class FilterServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<Filter, FilterMapping> {

		@Override
		public FilterMapping addingService(
			ServiceReference<Filter> serviceReference) {

			Filter filter = _bundleContext.getService(serviceReference);

			String servletContextName = GetterUtil.getString(
				serviceReference.getProperty("servlet-context-name"));

			if (Validator.isBlank(servletContextName)) {
				servletContextName = PortalUtil.getServletContextName();
			}

			String beforeFilter = GetterUtil.getString(
				serviceReference.getProperty("before-filter"));

			String positionFilterName = beforeFilter;

			boolean after = false;

			String afterFilter = GetterUtil.getString(
				serviceReference.getProperty("after-filter"));

			if (Validator.isNotNull(afterFilter)) {
				positionFilterName = afterFilter;

				after = true;
			}

			Map<String, String> initParameterMap = new HashMap<>();

			for (String key : serviceReference.getPropertyKeys()) {
				if (!key.startsWith("init-param.")) {
					continue;
				}

				String value = GetterUtil.getString(
					serviceReference.getProperty(key));

				initParameterMap.put(
					StringUtil.removeSubstring(key, "init-param."), value);
			}

			ServletContext servletContext = ServletContextPool.get(
				servletContextName);
			String servletFilterName = GetterUtil.getString(
				serviceReference.getProperty("servlet-filter-name"));

			FilterConfig filterConfig = new InvokerFilterConfig(
				servletContext, servletFilterName, initParameterMap);

			try {
				filter.init(filterConfig);
			}
			catch (ServletException servletException) {
				_log.error(servletException);

				_bundleContext.ungetService(serviceReference);

				return null;
			}

			updateFilterMappings(servletFilterName, filter);

			Set<Dispatcher> dispatchers = new HashSet<>();

			for (String dispatcherString :
					StringUtil.asList(
						serviceReference.getProperty("dispatcher"))) {

				dispatchers.add(Dispatcher.valueOf(dispatcherString));
			}

			FilterMapping filterMapping = new FilterMapping(
				servletFilterName, filter, filterConfig,
				StringUtil.asList(serviceReference.getProperty("url-pattern")),
				dispatchers);

			registerFilterMapping(filterMapping, positionFilterName, after);

			clearFilterChainsCache();

			return filterMapping;
		}

		@Override
		public void modifiedService(
			ServiceReference<Filter> serviceReference,
			FilterMapping filterMapping) {

			removedService(serviceReference, filterMapping);

			addingService(serviceReference);
		}

		@Override
		public void removedService(
			ServiceReference<Filter> serviceReference,
			FilterMapping filterMapping) {

			_bundleContext.ungetService(serviceReference);

			unregisterFilterMappings(
				GetterUtil.getString(
					serviceReference.getProperty("servlet-filter-name")));
		}

	}

}