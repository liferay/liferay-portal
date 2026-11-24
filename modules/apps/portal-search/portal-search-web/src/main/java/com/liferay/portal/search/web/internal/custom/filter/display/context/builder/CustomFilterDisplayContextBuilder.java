/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.filter.display.context.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.custom.filter.configuration.CustomFilterPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterDisplayContext;
import com.liferay.portal.search.web.internal.util.DisplayContextUtil;

/**
 * @author André de Oliveira
 */
public class CustomFilterDisplayContextBuilder {

	public static CustomFilterDisplayContextBuilder builder() {
		return new CustomFilterDisplayContextBuilder();
	}

	public CustomFilterDisplayContext build() throws ConfigurationException {
		CustomFilterDisplayContext customFilterDisplayContext =
			new CustomFilterDisplayContext();

		customFilterDisplayContext.setCustomFilterPortletInstanceConfiguration(
			getCustomFilterPortletInstanceConfiguration());
		customFilterDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		customFilterDisplayContext.setFilterValue(getFilterValue());
		customFilterDisplayContext.setHeading(getHeading());
		customFilterDisplayContext.setImmutable(_immutable);
		customFilterDisplayContext.setParameterName(_parameterName);
		customFilterDisplayContext.setRenderNothing(isRenderNothing());
		customFilterDisplayContext.setSearchURL(_getURLCurrentPath());

		return customFilterDisplayContext;
	}

	public CustomFilterDisplayContextBuilder customHeading(
		String customHeading) {

		_customHeading = customHeading;

		return this;
	}

	public CustomFilterDisplayContextBuilder disabled(boolean disabled) {
		_disabled = disabled;

		return this;
	}

	public CustomFilterDisplayContextBuilder filterField(String filterField) {
		_filterField = filterField;

		return this;
	}

	public CustomFilterDisplayContextBuilder filterValue(String filterValue) {
		_filterValue = filterValue;

		return this;
	}

	public CustomFilterDisplayContextBuilder immutable(boolean immutable) {
		_immutable = immutable;

		return this;
	}

	public CustomFilterDisplayContextBuilder parameterName(
		String parameterName) {

		_parameterName = parameterName;

		return this;
	}

	public CustomFilterDisplayContextBuilder parameterValue(
		String parameterValue) {

		_parameterValue = parameterValue;

		return this;
	}

	public CustomFilterDisplayContextBuilder queryName(String queryName) {
		_queryName = queryName;

		return this;
	}

	public CustomFilterDisplayContextBuilder renderNothing(
		boolean renderNothing) {

		_renderNothing = renderNothing;

		return this;
	}

	public CustomFilterDisplayContextBuilder themeDisplay(
		ThemeDisplay themeDisplay) {

		_themeDisplay = themeDisplay;

		return this;
	}

	protected CustomFilterPortletInstanceConfiguration
			getCustomFilterPortletInstanceConfiguration()
		throws ConfigurationException {

		return ConfigurationProviderUtil.getPortletInstanceConfiguration(
			CustomFilterPortletInstanceConfiguration.class, _themeDisplay);
	}

	protected long getDisplayStyleGroupId() throws ConfigurationException {
		CustomFilterPortletInstanceConfiguration
			customFilterPortletInstanceConfiguration =
				getCustomFilterPortletInstanceConfiguration();

		return DisplayContextUtil.getDisplayStyleGroupId(
			customFilterPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	protected String getFilterValue() {
		if (_immutable) {
			if (Validator.isNotNull(_filterValue)) {
				return _filterValue;
			}

			return StringPool.BLANK;
		}

		if (Validator.isNotNull(_parameterValue)) {
			return _parameterValue;
		}

		if (Validator.isNotNull(_filterValue)) {
			return _filterValue;
		}

		return StringPool.BLANK;
	}

	protected String getHeading() {
		if (Validator.isNotNull(_customHeading)) {
			return _customHeading;
		}

		if (Validator.isNotNull(_queryName)) {
			return _queryName;
		}

		if (Validator.isNotNull(_filterField)) {
			return _filterField;
		}

		return "custom";
	}

	protected boolean isRenderNothing() {
		if (_disabled || _renderNothing) {
			return true;
		}

		return false;
	}

	private String _getURLCurrentPath() {
		return HttpComponentsUtil.getPath(_themeDisplay.getURLCurrent());
	}

	private String _customHeading;
	private boolean _disabled;
	private String _filterField;
	private String _filterValue;
	private boolean _immutable;
	private String _parameterName;
	private String _parameterValue;
	private String _queryName;
	private boolean _renderNothing;
	private ThemeDisplay _themeDisplay;

}