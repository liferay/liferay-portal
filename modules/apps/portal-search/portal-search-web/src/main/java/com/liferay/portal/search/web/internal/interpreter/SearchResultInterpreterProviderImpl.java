/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.interpreter;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreter;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreterProvider;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Wade Cao
 */
@Component(service = SearchResultInterpreterProvider.class)
public class SearchResultInterpreterProviderImpl
	implements SearchResultInterpreterProvider {

	@Override
	public SearchResultInterpreter getSearchResultInterpreter(
		String portletName) {

		SearchResultInterpreter searchResultInterpreter =
			_serviceTrackerMap.getService(portletName);

		if (searchResultInterpreter == null) {
			return _assetRendererSearchResultInterpreter;
		}

		return searchResultInterpreter;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SearchResultInterpreter.class,
			"jakarta.portlet.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private final AssetRendererSearchResultInterpreter
		_assetRendererSearchResultInterpreter =
			new AssetRendererSearchResultInterpreter();
	private ServiceTrackerMap<String, SearchResultInterpreter>
		_serviceTrackerMap;

}