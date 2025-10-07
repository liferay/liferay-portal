/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context;

import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.category.facet.configuration.CategoryFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Lino Alves
 */
public class AssetCategoriesSearchFacetDisplayContext
	implements FacetDisplayContext, Serializable {

	public AssetCategoriesSearchFacetDisplayContext(
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_categoryFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				CategoryFacetPortletInstanceConfiguration.class, _themeDisplay);
	}

	@Override
	public List<BucketDisplayContext> getBucketDisplayContexts() {
		return _bucketDisplayContexts;
	}

	public List<BucketDisplayContext> getBucketDisplayContexts(
		String vocabularyName) {

		List<BucketDisplayContext> bucketDisplayContexts =
			_bucketDisplayContextsMap.get(vocabularyName);

		if (bucketDisplayContexts == null) {
			return new ArrayList<>();
		}

		return bucketDisplayContexts;
	}

	public CategoryFacetPortletInstanceConfiguration
		getCategoryFacetPortletInstanceConfiguration() {

		return _categoryFacetPortletInstanceConfiguration;
	}

	@Override
	public long getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_categoryFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	public List<String> getGroupVocabularyExternalReferenceCodes() {
		return _groupVocabularyExternalReferenceCodes;
	}

	@Override
	public String getPaginationStartParameterName() {
		return _paginationStartParameterName;
	}

	@Override
	public String getParameterName() {
		return _parameterName;
	}

	@Override
	public String getParameterValue() {
		return _parameterValue;
	}

	@Override
	public List<String> getParameterValues() {
		return _parameterValues;
	}

	public List<Long> getVocabularyIds() {
		return _vocabularyIds;
	}

	public List<String> getVocabularyNames() {
		return _vocabularyNames;
	}

	public boolean isCloud() {
		return _cloud;
	}

	@Override
	public boolean isNothingSelected() {
		return _nothingSelected;
	}

	@Override
	public boolean isRenderNothing() {
		return _renderNothing;
	}

	public void setBucketDisplayContexts(
		List<BucketDisplayContext> bucketDisplayContexts) {

		_bucketDisplayContexts = bucketDisplayContexts;
	}

	public void setBucketDisplayContextsMap(
		Map<String, List<BucketDisplayContext>> bucketDisplayContextsMap) {

		_bucketDisplayContextsMap = bucketDisplayContextsMap;
	}

	public void setCloud(boolean cloud) {
		_cloud = cloud;
	}

	public void setGroupVocabularyExternalReferenceCodes(
		List<String> groupVocabularyExternalReferenceCodes) {

		_groupVocabularyExternalReferenceCodes =
			groupVocabularyExternalReferenceCodes;
	}

	public void setNothingSelected(boolean nothingSelected) {
		_nothingSelected = nothingSelected;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValue(String paramValue) {
		_parameterValue = paramValue;
	}

	public void setParameterValues(List<String> parameterValues) {
		_parameterValues = parameterValues;
	}

	public void setRenderNothing(boolean renderNothing) {
		_renderNothing = renderNothing;
	}

	public void setVocabularyIds(List<Long> vocabularyIds) {
		_vocabularyIds = vocabularyIds;
	}

	public void setVocabularyNames(List<String> vocabularyNames) {
		_vocabularyNames = vocabularyNames;
	}

	private List<BucketDisplayContext> _bucketDisplayContexts;
	private Map<String, List<BucketDisplayContext>> _bucketDisplayContextsMap;
	private final CategoryFacetPortletInstanceConfiguration
		_categoryFacetPortletInstanceConfiguration;
	private boolean _cloud;
	private List<String> _groupVocabularyExternalReferenceCodes;
	private boolean _nothingSelected;
	private String _paginationStartParameterName;
	private String _parameterName;
	private String _parameterValue;
	private List<String> _parameterValues;
	private boolean _renderNothing;
	private final ThemeDisplay _themeDisplay;
	private List<Long> _vocabularyIds;
	private List<String> _vocabularyNames;

}