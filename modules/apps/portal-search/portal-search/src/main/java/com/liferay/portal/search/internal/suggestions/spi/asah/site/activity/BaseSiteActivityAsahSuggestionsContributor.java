/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.suggestions.spi.asah.site.activity;

import com.liferay.analytics.settings.configuration.AnalyticsConfiguration;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.internal.configuration.SiteActivityAsahConfiguration;
import com.liferay.portal.search.internal.suggestions.spi.asah.BaseAsahSuggestionsContributor;
import com.liferay.portal.search.internal.web.cache.AsahWebCacheItem;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;

/**
 * @author Petteri Karttunen
 */
public abstract class BaseSiteActivityAsahSuggestionsContributor
	extends BaseAsahSuggestionsContributor {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_siteActivityAsahConfiguration = ConfigurableUtil.createConfigurable(
			SiteActivityAsahConfiguration.class, properties);
	}

	@Override
	protected int getCharacterThreshold(Map<String, Object> attributes) {
		if (attributes == null) {
			return _CHARACTER_THRESHOLD;
		}

		return MapUtil.getInteger(
			attributes, "characterThreshold", _CHARACTER_THRESHOLD);
	}

	@Override
	protected JSONObject getJSONObject(
		AnalyticsConfiguration analyticsConfiguration,
		Map<String, Object> attributes, String basePath, String path,
		SearchContext searchContext, String sort,
		SuggestionsContributorConfiguration
			suggestionsContributorConfiguration) {

		String displayLanguageId = getDisplayLanguageId(
			attributes, searchContext.getLocale());
		long groupId = getGroupId(searchContext);
		int minCounts = getMinCounts(attributes, 5);
		int size = GetterUtil.getInteger(
			suggestionsContributorConfiguration.getSize(), 5);

		return AsahWebCacheItem.get(
			analyticsConfiguration, _siteActivityAsahConfiguration,
			searchContext.getCompanyId(),
			_getURL(
				analyticsConfiguration, basePath, displayLanguageId, groupId,
				minCounts, path, size, sort),
			StringBundler.concat(
				StringPool.POUND, searchContext.getCompanyId(),
				StringPool.POUND, displayLanguageId, StringPool.POUND, groupId,
				StringPool.POUND, minCounts, StringPool.POUND, size,
				StringPool.POUND, sort));
	}

	@Override
	protected boolean isEnabled(
		AnalyticsSettingsManager analyticsSettingsManager, long companyId) {

		try {
			if (FeatureFlagManagerUtil.isEnabled("LPS-159643") &&
				analyticsSettingsManager.isAnalyticsEnabled(companyId)) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	private String _getURL(
		AnalyticsConfiguration analyticsConfiguration, String basePath,
		String displayLanguageId, long groupId, int minCounts, String path,
		int size, String sort) {

		StringBundler sb = new StringBundler(18);

		sb.append(analyticsConfiguration.liferayAnalyticsFaroBackendURL());
		sb.append("/api/1.0/");
		sb.append(basePath);
		sb.append("/");
		sb.append(path);
		sb.append("?");

		if (!Validator.isBlank(displayLanguageId)) {
			sb.append("displayLanguageId=");
			sb.append(displayLanguageId);
			sb.append("&");
		}

		if (groupId > 0) {
			sb.append("&groupId=");
			sb.append(groupId);
			sb.append("&");
		}

		sb.append("minCounts=");
		sb.append(minCounts);
		sb.append("&size=");
		sb.append(size);
		sb.append("&sort=");
		sb.append(sort);

		return sb.toString();
	}

	private static final int _CHARACTER_THRESHOLD = 2;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseSiteActivityAsahSuggestionsContributor.class);

	private volatile SiteActivityAsahConfiguration
		_siteActivityAsahConfiguration;

}