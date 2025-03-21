/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.taglib.servlet.taglib;

import com.liferay.map.MapProvider;
import com.liferay.map.taglib.internal.servlet.ServletContextUtil;
import com.liferay.map.util.MapProviderHelperUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Chema Balsas
 */
public class MapDisplayTag extends IncludeTag {

	public double getLatitude() {
		return _latitude;
	}

	public double getLongitude() {
		return _longitude;
	}

	public String getMapProviderKey() {
		return _mapProviderKey;
	}

	public String getName() {
		return _name;
	}

	public String getPoints() {
		return _points;
	}

	public boolean isGeolocation() {
		return _geolocation;
	}

	public void setGeolocation(boolean geolocation) {
		_geolocation = geolocation;
	}

	public void setLatitude(double latitude) {
		_latitude = latitude;
	}

	public void setLongitude(double longitude) {
		_longitude = longitude;
	}

	public void setMapProviderKey(String mapProviderKey) {
		_mapProviderKey = mapProviderKey;
	}

	public void setName(String name) {
		_name = name;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setPoints(String points) {
		_points = points;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_geolocation = false;
		_latitude = 0;
		_longitude = 0;
		_mapProviderKey = null;
		_name = null;
		_points = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-map:map:geolocation", _geolocation);
		httpServletRequest.setAttribute("liferay-map:map:latitude", _latitude);
		httpServletRequest.setAttribute(
			"liferay-map:map:longitude", _longitude);
		httpServletRequest.setAttribute(
			"liferay-map:map:mapProvider", _getMapProvider());
		httpServletRequest.setAttribute("liferay-map:map:name", _name);
		httpServletRequest.setAttribute("liferay-map:map:points", _points);
	}

	private MapProvider _getMapProvider() {
		MapProvider mapProvider = null;

		String mapProviderKey = _getMapProviderKey();

		if (Validator.isNotNull(mapProviderKey)) {
			mapProvider = ServletContextUtil.getMapProvider(mapProviderKey);
		}

		if (mapProvider == null) {
			Collection<MapProvider> mapProviders =
				ServletContextUtil.getMapProviders();

			Iterator<MapProvider> iterator = mapProviders.iterator();

			if (iterator.hasNext()) {
				mapProvider = iterator.next();
			}
		}

		return mapProvider;
	}

	private String _getMapProviderKey() {
		String mapProviderKey = _mapProviderKey;

		if (Validator.isNull(mapProviderKey)) {
			HttpServletRequest httpServletRequest = getRequest();

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			mapProviderKey = MapProviderHelperUtil.getMapProviderKey(
				ServletContextUtil.getGroupLocalService(),
				themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId());
		}

		return mapProviderKey;
	}

	private static final String _PAGE = "/map_display/page.jsp";

	private boolean _geolocation;
	private double _latitude;
	private double _longitude;
	private String _mapProviderKey;
	private String _name;
	private String _points;

}