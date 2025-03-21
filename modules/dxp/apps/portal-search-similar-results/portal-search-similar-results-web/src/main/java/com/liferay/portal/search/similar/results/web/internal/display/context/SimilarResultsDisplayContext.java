/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.display.context;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.similar.results.web.internal.configuration.SimilarResultsPortletInstanceConfiguration;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Tan
 */
public class SimilarResultsDisplayContext {

	public SimilarResultsDisplayContext(HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_httpServletRequest = httpServletRequest;

		_similarResultsPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				SimilarResultsPortletInstanceConfiguration.class,
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY));
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != 0) {
			return _displayStyleGroupId;
		}

		_displayStyleGroupId =
			_similarResultsPortletInstanceConfiguration.displayStyleGroupId();

		if (_displayStyleGroupId <= 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			_displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public List<Document> getDocuments() {
		return _documents;
	}

	public List<SimilarResultsDocumentDisplayContext>
		getSimilarResultsDocumentDisplayContexts() {

		if (_similarResultsDocumentDisplayContexts != null) {
			return _similarResultsDocumentDisplayContexts;
		}

		return new ArrayList<>();
	}

	public SimilarResultsPortletInstanceConfiguration
		getSimilarResultsPortletInstanceConfiguration() {

		return _similarResultsPortletInstanceConfiguration;
	}

	public int getTotalHits() {
		return _totalHits;
	}

	public void setDocuments(List<Document> documents) {
		_documents = documents;
	}

	public void setSimilarResultsDocumentDisplayContexts(
		List<SimilarResultsDocumentDisplayContext>
			similarResultsDocumentDisplayContexts) {

		_similarResultsDocumentDisplayContexts =
			similarResultsDocumentDisplayContexts;
	}

	public void setTotalHits(int totalHits) {
		_totalHits = totalHits;
	}

	private long _displayStyleGroupId;
	private List<Document> _documents;
	private final HttpServletRequest _httpServletRequest;
	private List<SimilarResultsDocumentDisplayContext>
		_similarResultsDocumentDisplayContexts;
	private final SimilarResultsPortletInstanceConfiguration
		_similarResultsPortletInstanceConfiguration;
	private int _totalHits;

}