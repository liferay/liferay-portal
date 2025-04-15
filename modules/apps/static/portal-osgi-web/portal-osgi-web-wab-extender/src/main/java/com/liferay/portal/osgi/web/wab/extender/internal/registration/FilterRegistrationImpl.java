/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.wab.extender.internal.registration;

import com.liferay.petra.string.StringPool;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Juan González
 */
public class FilterRegistrationImpl implements FilterRegistration.Dynamic {

	@Override
	public void addMappingForServletNames(
		EnumSet<DispatcherType> dispatcherTypes, boolean matchAfter,
		String... servletNames) {

		_filterMapping.setDispatcher(dispatcherTypes);

		int i = 0;

		for (String servletName : servletNames) {
			if (matchAfter) {
				_filterMapping.addServletName(servletName);
			}
			else {
				_filterMapping.addServletName(i, servletName);

				i++;
			}
		}
	}

	@Override
	public void addMappingForUrlPatterns(
		EnumSet<DispatcherType> dispatcherTypes, boolean matchAfter,
		String... urlPatterns) {

		_filterMapping.setDispatcher(dispatcherTypes);

		int i = 0;

		for (String servletName : urlPatterns) {
			if (matchAfter) {
				_filterMapping.addURLPattern(servletName);
			}
			else {
				_filterMapping.addURLPattern(i, servletName);

				i++;
			}
		}
	}

	@Override
	public String getClassName() {
		return _className;
	}

	public FilterMapping getFilterMapping() {
		return _filterMapping;
	}

	@Override
	public String getInitParameter(String name) {
		return _initParameters.get(name);
	}

	@Override
	public Map<String, String> getInitParameters() {
		return _initParameters;
	}

	public Filter getInstance() {
		return _instance;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Collection<String> getServletNameMappings() {
		return _filterMapping.getServletNames();
	}

	@Override
	public Collection<String> getUrlPatternMappings() {
		return _filterMapping.getURLPatterns();
	}

	public boolean isAsyncSupported() {
		return _asyncSupported;
	}

	@Override
	public void setAsyncSupported(boolean asyncSupported) {
		_asyncSupported = asyncSupported;
	}

	public void setClassName(String className) {
		_className = className;
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		boolean exists = _initParameters.containsKey(name);

		_initParameters.put(name, value);

		return exists;
	}

	@Override
	public Set<String> setInitParameters(Map<String, String> initParameters) {
		_initParameters = initParameters;

		return new HashSet<>();
	}

	public void setInstance(Filter instance) {
		_instance = instance;
	}

	public void setName(String name) {
		_name = name;
	}

	public class FilterMapping {

		public void addServletName(int index, String servletName) {
			FilterMappingItem filterMappingItem = new FilterMappingItem();

			filterMappingItem.setItemContent(servletName);

			_filterMappingItems.add(index, filterMappingItem);
		}

		public void addServletName(String servletName) {
			addServletName(_filterMappingItems.size(), servletName);
		}

		public void addURLPattern(int index, String urlPattern) {
			FilterMappingItem filterMappingItem = new FilterMappingItem();

			filterMappingItem.setUrlPattern(true);
			filterMappingItem.setItemContent(urlPattern);

			_filterMappingItems.add(index, filterMappingItem);
		}

		public void addURLPattern(String urlPattern) {
			addURLPattern(_filterMappingItems.size(), urlPattern);
		}

		public EnumSet<DispatcherType> getDispatchers() {
			return _dispatchers;
		}

		public List<String> getServletNames() {
			return Collections.unmodifiableList(_getFilterMappingItems(false));
		}

		public List<String> getURLPatterns() {
			return Collections.unmodifiableList(_getFilterMappingItems(true));
		}

		public void setDispatcher(EnumSet<DispatcherType> dispatcher) {
			_dispatchers = dispatcher;
		}

		private List<String> _getFilterMappingItems(boolean urlPattern) {
			List<String> filterMappingItems = new ArrayList<>();

			for (FilterMappingItem filterMappingItem : _filterMappingItems) {
				if (urlPattern == filterMappingItem.isUrlPattern()) {
					filterMappingItems.add(filterMappingItem.getItemContent());
				}
			}

			return filterMappingItems;
		}

		private EnumSet<DispatcherType> _dispatchers = EnumSet.noneOf(
			DispatcherType.class);
		private final List<FilterMappingItem> _filterMappingItems =
			new ArrayList<>();

	}

	private boolean _asyncSupported;
	private String _className = StringPool.BLANK;
	private final FilterMapping _filterMapping = new FilterMapping();
	private Map<String, String> _initParameters = new HashMap<>();
	private Filter _instance;
	private String _name = StringPool.BLANK;

	private static class FilterMappingItem {

		public String getItemContent() {
			return _itemContent;
		}

		public boolean isUrlPattern() {
			return _urlPattern;
		}

		public void setItemContent(String itemContent) {
			_itemContent = itemContent;
		}

		public void setUrlPattern(boolean urlPattern) {
			_urlPattern = urlPattern;
		}

		private String _itemContent = StringPool.BLANK;
		private boolean _urlPattern;

	}

}