/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.web.internal.display.context;

import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.util.comparator.PatcherAccountModifiedDateComparator;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Eudaldo Alonso
 */
public class PatcherAccountsDisplayContext {

	public PatcherAccountsDisplayContext(
		HttpServletRequest httpServletRequest, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public SearchContainer<PatcherAccount> getSearchContainer()
		throws Exception {

		if (_patcherAccountSearchContainer != null) {
			return _patcherAccountSearchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SearchContainer<PatcherAccount> patcherAccountSearchContainer =
			new SearchContainer<>(
				_renderRequest, _renderResponse.createRenderURL(), null,
				"there-are-no-accounts");

		patcherAccountSearchContainer.setResultsAndTotal(
			() -> PatcherAccountLocalServiceUtil.getPatcherAccounts(
				themeDisplay.getCompanyId(), _getKeywords(),
				patcherAccountSearchContainer.getStart(),
				patcherAccountSearchContainer.getEnd(),
				PatcherAccountModifiedDateComparator.getInstance(false)),
			PatcherAccountLocalServiceUtil.getPatcherAccountsCount(
				themeDisplay.getCompanyId(), _getKeywords()));

		_patcherAccountSearchContainer = patcherAccountSearchContainer;

		return _patcherAccountSearchContainer;
	}

	private String _getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(_httpServletRequest, "keywords");

		return _keywords;
	}

	private final HttpServletRequest _httpServletRequest;
	private String _keywords;
	private SearchContainer<PatcherAccount> _patcherAccountSearchContainer;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}