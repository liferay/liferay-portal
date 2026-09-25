/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.suggestions.spi.asah.user.activity;

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
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.internal.configuration.UserActivityAsahConfiguration;
import com.liferay.portal.search.internal.suggestions.spi.asah.BaseAsahSuggestionsContributor;
import com.liferay.portal.search.internal.web.cache.AsahWebCacheItem;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorConfiguration;

import java.security.MessageDigest;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gustavo Lima
 */
public abstract class BaseUserActivityAsahSuggestionsContributor
	extends BaseAsahSuggestionsContributor {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_userActivityAsahConfiguration = ConfigurableUtil.createConfigurable(
			UserActivityAsahConfiguration.class, properties);
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

		String contentTypes = _getContentTypes(attributes);
		String displayLanguageId = getDisplayLanguageId(
			attributes, searchContext.getLocale());
		long groupId = getGroupId(searchContext);
		String hashedEmailAddress = _getHashedEmailAddress(
			searchContext.getUserId());
		int minCounts = getMinCounts(attributes, 0);
		int page = _getPage(attributes);
		int rangeKey = _getRangeKey(attributes);
		int size = GetterUtil.getInteger(
			suggestionsContributorConfiguration.getSize(), 5);

		return AsahWebCacheItem.get(
			analyticsConfiguration, _userActivityAsahConfiguration,
			searchContext.getCompanyId(),
			_getURL(
				analyticsConfiguration,
				StringBundler.concat(
					basePath, StringPool.FORWARD_SLASH, hashedEmailAddress),
				contentTypes,
				GetterUtil.getLong(
					analyticsConfiguration.liferayAnalyticsDataSourceId()),
				displayLanguageId, groupId, minCounts, page, path, rangeKey,
				size, sort),
			StringBundler.concat(
				StringPool.POUND, contentTypes, StringPool.POUND,
				displayLanguageId, StringPool.POUND, groupId, StringPool.POUND,
				hashedEmailAddress, StringPool.POUND, minCounts,
				StringPool.POUND, page, StringPool.POUND, path,
				StringPool.POUND, rangeKey, StringPool.POUND, size,
				StringPool.POUND, sort));
	}

	@Override
	protected boolean isEnabled(
		AnalyticsSettingsManager analyticsSettingsManager, long companyId) {

		try {
			if (FeatureFlagManagerUtil.isEnabled("LPS-176691") &&
				analyticsSettingsManager.isAnalyticsEnabled(companyId)) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Reference
	protected Portal portal;

	private String _getContentTypes(Map<String, Object> attributes) {
		if (attributes == null) {
			return StringPool.BLANK;
		}

		return MapUtil.getString(attributes, "contentTypes", StringPool.BLANK);
	}

	private String _getHashedEmailAddress(long userId) {
		try {
			StringBuilder sb = new StringBuilder();

			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			String userEmailAddress = portal.getUserEmailAddress(userId);

			messageDigest.update(userEmailAddress.getBytes());

			byte[] digest = messageDigest.digest();

			for (byte b : digest) {
				sb.append(String.format("%02x", b));
			}

			return sb.toString();
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return StringPool.BLANK;
	}

	private int _getPage(Map<String, Object> attributes) {
		if (attributes == null) {
			return 0;
		}

		return MapUtil.getInteger(attributes, "page", 0);
	}

	private int _getRangeKey(Map<String, Object> attributes) {
		if (attributes == null) {
			return 0;
		}

		return MapUtil.getInteger(attributes, "rangeKey", 0);
	}

	private String _getURL(
		AnalyticsConfiguration analyticsConfiguration, String basePath,
		String contentTypes, long dataSourceId, String displayLanguageId,
		long groupId, long minCounts, int page, String path, int rangeKey,
		int size, String sort) {

		StringBundler sb = new StringBundler(31);

		sb.append(analyticsConfiguration.liferayAnalyticsFaroBackendURL());
		sb.append("/api/1.0/");
		sb.append(basePath);
		sb.append("/");
		sb.append(path);
		sb.append("?");

		if (!Validator.isBlank(contentTypes)) {
			sb.append("contentTypes=");
			sb.append(contentTypes);
			sb.append("&");
		}

		sb.append("dataSourceId=");
		sb.append(dataSourceId);
		sb.append("&");

		if (!Validator.isBlank(displayLanguageId)) {
			sb.append("displayLanguageId=");
			sb.append(displayLanguageId);
			sb.append("&");
		}

		if (groupId > 0) {
			sb.append("groupId=");
			sb.append(groupId);
			sb.append("&");
		}

		if (minCounts > 0) {
			sb.append("minCounts=");
			sb.append(minCounts);
			sb.append("&");
		}

		if (page > 0) {
			sb.append("page=");
			sb.append(page);
			sb.append("&");
		}

		if (rangeKey != 7) {
			sb.append("rangeKey=");
			sb.append(rangeKey);
			sb.append("&");
		}

		sb.append("size=");
		sb.append(size);
		sb.append("&sort=");
		sb.append(sort);

		return sb.toString();
	}

	private static final int _CHARACTER_THRESHOLD = 0;

	private static final Log _log = LogFactoryUtil.getLog(
		BaseUserActivityAsahSuggestionsContributor.class);

	private volatile UserActivityAsahConfiguration
		_userActivityAsahConfiguration;

}