/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.sitemap.web.internal.display.context;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.site.configuration.manager.SitemapConfigurationManager;

/**
 * @author Lourdes Fernández Besada
 */
public class SitemapGroupConfigurationDisplayContext {

	public SitemapGroupConfigurationDisplayContext(
		SitemapConfigurationManager sitemapConfigurationManager,
		ThemeDisplay themeDisplay) {

		_sitemapConfigurationManager = sitemapConfigurationManager;
		_themeDisplay = themeDisplay;
	}

	public boolean includeCategories() throws ConfigurationException {
		return _sitemapConfigurationManager.includeCategoriesGroupEnabled(
			_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId());
	}

	public boolean includeCommerceProducts() throws ConfigurationException {
		return _sitemapConfigurationManager.includeCommerceProductsGroupEnabled(
			_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId());
	}

	public boolean includePages() throws ConfigurationException {
		return _sitemapConfigurationManager.includePagesGroupEnabled(
			_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId());
	}

	public boolean includeWebContent() throws ConfigurationException {
		return _sitemapConfigurationManager.includeWebContentGroupEnabled(
			_themeDisplay.getCompanyId(), _themeDisplay.getScopeGroupId());
	}

	public boolean isIncludeCategoriesDisabled() throws ConfigurationException {
		if (_includeCategoriesDisabled != null) {
			return _includeCategoriesDisabled;
		}

		if (_sitemapConfigurationManager.includeCategoriesCompanyEnabled(
				_themeDisplay.getCompanyId())) {

			_includeCategoriesDisabled = false;
		}
		else {
			_includeCategoriesDisabled = true;
		}

		return _includeCategoriesDisabled;
	}

	public boolean isIncludeCommerceProductsDisabled()
		throws ConfigurationException {

		if (_includeCommerceProductsDisabled != null) {
			return _includeCommerceProductsDisabled;
		}

		if (_sitemapConfigurationManager.includeCommerceProductsCompanyEnabled(
				_themeDisplay.getCompanyId())) {

			_includeCommerceProductsDisabled = false;
		}
		else {
			_includeCommerceProductsDisabled = true;
		}

		return _includeCommerceProductsDisabled;
	}

	public boolean isIncludePagesDisabled() throws ConfigurationException {
		if (_includePagesDisabled != null) {
			return _includePagesDisabled;
		}

		if (_sitemapConfigurationManager.includePagesCompanyEnabled(
				_themeDisplay.getCompanyId())) {

			_includePagesDisabled = false;
		}
		else {
			_includePagesDisabled = true;
		}

		return _includePagesDisabled;
	}

	public boolean isIncludeWebContentDisabled() throws ConfigurationException {
		if (_includeWebContentDisabled != null) {
			return _includeWebContentDisabled;
		}

		if (_sitemapConfigurationManager.includeWebContentCompanyEnabled(
				_themeDisplay.getCompanyId())) {

			_includeWebContentDisabled = false;
		}
		else {
			_includeWebContentDisabled = true;
		}

		return _includeWebContentDisabled;
	}

	private Boolean _includeCategoriesDisabled;
	private Boolean _includeCommerceProductsDisabled;
	private Boolean _includePagesDisabled;
	private Boolean _includeWebContentDisabled;
	private final SitemapConfigurationManager _sitemapConfigurationManager;
	private final ThemeDisplay _themeDisplay;

}