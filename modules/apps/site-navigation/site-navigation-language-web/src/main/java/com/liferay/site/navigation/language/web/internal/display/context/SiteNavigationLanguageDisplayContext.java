/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.language.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.KeyValuePairComparator;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.PortletDisplayTemplate;
import com.liferay.site.navigation.language.web.internal.configuration.SiteNavigationLanguagePortletInstanceConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Julio Camarero
 */
public class SiteNavigationLanguageDisplayContext {

	public SiteNavigationLanguageDisplayContext(
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_portletDisplayTemplate =
			(PortletDisplayTemplate)httpServletRequest.getAttribute(
				WebKeys.PORTLET_DISPLAY_TEMPLATE);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_siteNavigationLanguagePortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				SiteNavigationLanguagePortletInstanceConfiguration.class,
				_themeDisplay);
	}

	public List<KeyValuePair> getAvailableLanguageIdKVPs() {
		List<KeyValuePair> availableLanguageIdKVPs = new ArrayList<>();

		String[] languageIds = getLanguageIds();

		Arrays.sort(languageIds);

		Set<String> availableLanguageIdsSet = SetUtil.fromArray(
			getAvailableLanguageIds());

		for (String languageId : availableLanguageIdsSet) {
			if (Arrays.binarySearch(languageIds, languageId) < 0) {
				Locale locale = LocaleUtil.fromLanguageId(languageId);

				availableLanguageIdKVPs.add(
					new KeyValuePair(
						languageId,
						locale.getDisplayName(_themeDisplay.getLocale())));
			}
		}

		return ListUtil.sort(
			availableLanguageIdKVPs, new KeyValuePairComparator(false, true));
	}

	public String[] getAvailableLanguageIds() {
		if (_availableLanguageIds != null) {
			return _availableLanguageIds;
		}

		_availableLanguageIds = LocaleUtil.toLanguageIds(
			LanguageUtil.getAvailableLocales(_themeDisplay.getSiteGroupId()));

		return _availableLanguageIds;
	}

	public List<KeyValuePair> getCurrentLanguageIdKVPs() {
		List<KeyValuePair> currentLanguageIdKVPs = new ArrayList<>();

		String[] languageIds = getLanguageIds();

		for (String languageId : languageIds) {
			Locale locale = LocaleUtil.fromLanguageId(languageId);

			currentLanguageIdKVPs.add(
				new KeyValuePair(
					languageId,
					locale.getDisplayName(_themeDisplay.getLocale())));
		}

		return currentLanguageIdKVPs;
	}

	public String getDDMTemplateKey() {
		if (_ddmTemplateKey != null) {
			return _ddmTemplateKey;
		}

		String displayStyle =
			_siteNavigationLanguagePortletInstanceConfiguration.displayStyle();

		if (displayStyle != null) {
			_ddmTemplateKey = _portletDisplayTemplate.getDDMTemplateKey(
				displayStyle);
		}

		return _ddmTemplateKey;
	}

	public String getDisplayStyleGroupKey() {
		if (_displayStyleGroupKey != null) {
			return _displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			_siteNavigationLanguagePortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		Group group = _themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				_themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public String[] getLanguageIds() {
		if (_languageIds != null) {
			return _languageIds;
		}

		_languageIds = StringUtil.split(
			_siteNavigationLanguagePortletInstanceConfiguration.languageIds());

		if (ArrayUtil.isEmpty(_languageIds)) {
			_languageIds = getAvailableLanguageIds();
		}
		else {
			List<String> filteredLanguageIds = new ArrayList<>();

			for (String languageId : _languageIds) {
				if (ArrayUtil.contains(getAvailableLanguageIds(), languageId)) {
					filteredLanguageIds.add(languageId);
				}
			}

			_languageIds = ArrayUtil.toStringArray(filteredLanguageIds);
		}

		return _languageIds;
	}

	public SiteNavigationLanguagePortletInstanceConfiguration
		getSiteNavigationLanguagePortletInstanceConfiguration() {

		return _siteNavigationLanguagePortletInstanceConfiguration;
	}

	private String[] _availableLanguageIds;
	private String _ddmTemplateKey;
	private String _displayStyleGroupKey;
	private String[] _languageIds;
	private final PortletDisplayTemplate _portletDisplayTemplate;
	private final SiteNavigationLanguagePortletInstanceConfiguration
		_siteNavigationLanguagePortletInstanceConfiguration;
	private final ThemeDisplay _themeDisplay;

}