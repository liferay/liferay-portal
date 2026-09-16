/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Olivia Yu
 */
public class SearchResultsPaginatorDisplayContext {

	public SearchResultsPaginatorDisplayContext(
		HttpServletRequest httpServletRequest,
		SearchContainer<?> searchContainer) {

		_httpServletRequest = httpServletRequest;
		_searchContainer = searchContainer;
	}

	public Map<String, Object> getProps() {
		if (!isAvailable()) {
			return null;
		}

		String[] urlArray = PortalUtil.stripURLAnchor(
			String.valueOf(_getIteratorURL()), StringPool.POUND);

		String urlAnchor = urlArray[1];

		String url = _getURL(urlArray[0]);

		return HashMapBuilder.<String, Object>put(
			"activeDelta", _searchContainer.getDelta()
		).put(
			"activePage", _searchContainer.getCur()
		).put(
			"deltas", _getDeltas(url, urlAnchor)
		).put(
			"paginationURLTemplate",
			_getPaginationURLTemplate(
				url, urlAnchor, _searchContainer.getCurParam())
		).put(
			"showDeltasDropDown", _searchContainer.isDeltaConfigurable()
		).put(
			"totalItems", _searchContainer.getTotal()
		).put(
			"totalItemsApproximate",
			FeatureFlagManagerUtil.isEnabled(
				PortalUtil.getCompanyId(_httpServletRequest), "LPD-98858")
		).build();
	}

	public boolean isAvailable() {
		if (!_isPaginationNeeded()) {
			return false;
		}

		if (_getIteratorURL() != null) {
			return true;
		}

		return false;
	}

	private String _appendParameterSeparator(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		if (url.indexOf(CharPool.QUESTION) == -1) {
			return url + StringPool.QUESTION;
		}

		if (!url.endsWith(StringPool.AMPERSAND) &&
			!url.endsWith(StringPool.QUESTION)) {

			return url + StringPool.AMPERSAND;
		}

		return url;
	}

	private List<Map<String, Object>> _getDeltas(String url, String urlAnchor) {
		List<Map<String, Object>> deltas = new ArrayList<>();

		String deltaParam = _searchContainer.getDeltaParam();

		for (int curDelta : PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES) {
			if (curDelta > SearchContainer.MAX_DELTA) {
				continue;
			}

			deltas.add(
				HashMapBuilder.<String, Object>put(
					"href",
					HttpComponentsUtil.sortParameters(
						HttpComponentsUtil.setParameter(
							url + urlAnchor, deltaParam, curDelta))
				).put(
					"label", curDelta
				).build());
		}

		return deltas;
	}

	private PortletURL _getIteratorURL() {
		PortletURL iteratorURL = _searchContainer.getIteratorURL();

		if (iteratorURL == null) {
			return null;
		}

		iteratorURL.setParameter("resetCur", Boolean.FALSE.toString());

		return iteratorURL;
	}

	private String _getPaginationURLTemplate(
		String url, String urlAnchor, String curParam) {

		String templateURL = HttpComponentsUtil.sortParameters(
			HttpComponentsUtil.addParameter(
				HttpComponentsUtil.removeParameter(url, curParam) + urlAnchor,
				curParam, _CUR_PLACEHOLDER));

		return StringUtil.replace(templateURL, _CUR_PLACEHOLDER, "{0}");
	}

	private String _getURL(String url) {
		url = _appendParameterSeparator(PortalUtil.escapeRedirect(url));

		if (_searchContainer.isDeltaConfigurable()) {
			url = HttpComponentsUtil.setParameter(
				url, _searchContainer.getDeltaParam(),
				String.valueOf(_searchContainer.getDelta()));
		}

		return url;
	}

	private boolean _isPaginationNeeded() {
		int total = _searchContainer.getTotal();

		if ((total <= 0) ||
			((total <= _searchContainer.getDelta()) &&
			 (total <= PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES[0]))) {

			return false;
		}

		return true;
	}

	private static final String _CUR_PLACEHOLDER = "__CUR__";

	private final HttpServletRequest _httpServletRequest;
	private final SearchContainer<?> _searchContainer;

}