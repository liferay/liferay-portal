/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemList;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.seo.studio.web.internal.constants.SEOStudioFDSNames;

import jakarta.servlet.http.HttpServletRequest;

import java.text.Format;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Noor Najjar
 */
public class ViewOnPageDisplayContext {

	public ViewOnPageDisplayContext(
		JSONArray filtersJSONArray, HttpServletRequest httpServletRequest,
		Language language, ObjectEntry objectEntry, List<Long> seoStudioScanIds,
		JSONArray viewsJSONArray) {

		_filtersJSONArray = filtersJSONArray;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_objectEntry = objectEntry;
		_seoStudioScanIds = seoStudioScanIds;
		_viewsJSONArray = viewsJSONArray;
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		return FDSActionDropdownItemList.of(
			new FDSActionDropdownItem(
				getViewInsightDetailsURL() +
					"?objectEntryExternalReferenceCode={externalReferenceCode}",
				"view", "view-details",
				_language.get(_httpServletRequest, "view-details"), "get", null,
				null));
	}

	public Map<String, Object> getReactData() throws PortalException {
		String lastScanDateString = null;

		if (_objectEntry != null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(
				"MMM d, yyyy 'at' h:mm a", themeDisplay.getLocale(),
				themeDisplay.getTimeZone());

			Map<String, Object> properties = _objectEntry.getProperties();

			Date requestDate = GetterUtil.getDate(
				properties.get("requestDate"),
				DateFormatFactoryUtil.getSimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
				null);

			lastScanDateString = format.format(requestDate);
		}

		return HashMapBuilder.<String, Object>put(
			"apiURL", _getAPIURL()
		).put(
			"emptyState", _getEmptyState()
		).put(
			"fdsActionDropdownItems", getFDSActionDropdownItems()
		).put(
			"fdsId", SEOStudioFDSNames.INSIGHT_TYPE_SECTION
		).put(
			"filters", _filtersJSONArray
		).put(
			"insightDetailsURL", getViewInsightDetailsURL()
		).put(
			"lastScanDate", lastScanDateString
		).put(
			"views", _viewsJSONArray
		).build();
	}

	public String getViewInsightDetailsURL() throws PortalException {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = LayoutLocalServiceUtil.fetchLayoutByFriendlyURL(
			themeDisplay.getScopeGroupId(), false, "/on-page-insight-details");

		if (layout == null) {
			return StringPool.BLANK;
		}

		return PortalUtil.getLayoutFullURL(layout, themeDisplay);
	}

	private String _getAPIURL() {
		List<Long> seoStudioScanIds = _seoStudioScanIds;

		if (seoStudioScanIds.isEmpty()) {
			seoStudioScanIds = Collections.singletonList(-1L);
		}

		StringBundler sb = new StringBundler();

		for (Long seoStudioScanId : seoStudioScanIds) {
			if (sb.length() > 0) {
				sb.append(" or ");
			}

			sb.append(
				"r_seoStudioScanToSEOStudioInsightTypes_seoStudioScanId eq '");
			sb.append(seoStudioScanId);
			sb.append("'");
		}

		return "/o/seo-studio/insight-types?filter=" +
			URLCodec.encodeURL(sb.toString(), true) + "&sort=severity:desc";
	}

	private Map<String, Object> _getEmptyState() {
		if (_objectEntry == null) {
			return HashMapBuilder.<String, Object>put(
				"description",
				_language.get(_httpServletRequest, "run-a-scan-to-see-insights")
			).put(
				"title", _language.get(_httpServletRequest, "no-scans-yet")
			).build();
		}

		return HashMapBuilder.<String, Object>put(
			"description",
			_language.get(
				_httpServletRequest, "there-are-no-insights-for-the-last-scan")
		).put(
			"filtered",
			HashMapBuilder.<String, Object>put(
				"filters",
				HashMapBuilder.<String, Object>put(
					"title",
					_language.get(
						_httpServletRequest, "no-insights-match-these-filters")
				).build()
			).build()
		).put(
			"title", _language.get(_httpServletRequest, "no-insights-found")
		).build();
	}

	private final JSONArray _filtersJSONArray;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final ObjectEntry _objectEntry;
	private final List<Long> _seoStudioScanIds;
	private final JSONArray _viewsJSONArray;

}