/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.similar.results.web.internal.display.context;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
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
		long displayStyleGroupId = 0;

		String displayStyleGroupExternalReferenceCode =
			_similarResultsPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			displayStyleGroupId = group.getGroupId();
		}
		else {
			displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return displayStyleGroupId;
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

	private List<Document> _documents;
	private final HttpServletRequest _httpServletRequest;
	private List<SimilarResultsDocumentDisplayContext>
		_similarResultsDocumentDisplayContexts;
	private final SimilarResultsPortletInstanceConfiguration
		_similarResultsPortletInstanceConfiguration;
	private int _totalHits;

}