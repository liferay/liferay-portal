/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context.builder;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.ScopeSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.site.facet.configuration.SiteFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author André de Oliveira
 */
public class ScopeSearchFacetDisplayContextBuilder {

	public ScopeSearchFacetDisplayContextBuilder(RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_siteFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				SiteFacetPortletInstanceConfiguration.class, _themeDisplay);
	}

	public ScopeSearchFacetDisplayContext build() {
		ScopeSearchFacetDisplayContext scopeSearchFacetDisplayContext =
			new ScopeSearchFacetDisplayContext();

		scopeSearchFacetDisplayContext.setBucketDisplayContexts(
			_buildBucketDisplayContexts(getTermCollectors()));
		scopeSearchFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		scopeSearchFacetDisplayContext.setNothingSelected(isNothingSelected());
		scopeSearchFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		scopeSearchFacetDisplayContext.setParameterName(_parameterName);
		scopeSearchFacetDisplayContext.setParameterValue(
			getFirstParameterValueString());
		scopeSearchFacetDisplayContext.setParameterValues(
			getParameterValueStrings());
		scopeSearchFacetDisplayContext.setRenderNothing(isRenderNothing());
		scopeSearchFacetDisplayContext.setSiteFacetPortletInstanceConfiguration(
			_siteFacetPortletInstanceConfiguration);

		return scopeSearchFacetDisplayContext;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFilteredGroupIds(long[] groupIds) {
		_filteredGroupIds = groupIds;
	}

	public void setFrequenciesVisible(boolean frequenciesVisible) {
		_showCounts = frequenciesVisible;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_countThreshold = frequencyThreshold;
	}

	public void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	public void setLanguage(Language language) {
		_language = language;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setMaxTerms(int maxTerms) {
		_maxTerms = maxTerms;
	}

	public void setOrder(String order) {
		_order = order;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		setParameterValues(parameterValue);
	}

	public void setParameterValues(String... parameterValues) {
		_selectedGroupIds = TransformUtil.transformToList(
			parameterValues,
			value -> {
				long groupId = GetterUtil.getLong(value);

				if (groupId <= 0) {
					return null;
				}

				return groupId;
			});
	}

	public void setRequest(HttpServletRequest httpServletRequest) {
		_httpServletRequest = httpServletRequest;
	}

	protected BucketDisplayContext buildBucketDisplayContext(
		long groupId, int count, boolean selected) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(_getDescriptiveName(groupId));
		bucketDisplayContext.setFilterValue(String.valueOf(groupId));
		bucketDisplayContext.setFrequency(count);
		bucketDisplayContext.setFrequencyVisible(_showCounts);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(selected);

		return bucketDisplayContext;
	}

	protected BucketDisplayContext buildBucketDisplayContext(
		TermCollector termCollector) {

		int count = termCollector.getFrequency();

		if ((_countThreshold > 0) && (_countThreshold > count)) {
			return null;
		}

		return buildBucketDisplayContext(termCollector, count);
	}

	protected BucketDisplayContext buildBucketDisplayContext(
		TermCollector termCollector, int count) {

		long groupId = GetterUtil.getLong(termCollector.getTerm());

		return buildBucketDisplayContext(groupId, count, isSelected(groupId));
	}

	protected long getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_siteFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	protected List<BucketDisplayContext>
		getEmptySearchResultBucketDisplayContexts() {

		return TransformUtil.transform(
			_selectedGroupIds,
			groupId -> buildBucketDisplayContext(groupId, 0, true));
	}

	protected String getFirstParameterValueString() {
		if (_selectedGroupIds.isEmpty()) {
			return "0";
		}

		return String.valueOf(_selectedGroupIds.get(0));
	}

	protected List<String> getParameterValueStrings() {
		return TransformUtil.transform(_selectedGroupIds, String::valueOf);
	}

	protected List<TermCollector> getTermCollectors() {
		if (_facet == null) {
			return Collections.emptyList();
		}

		FacetCollector facetCollector = _facet.getFacetCollector();

		if (facetCollector != null) {
			return facetCollector.getTermCollectors();
		}

		return Collections.<TermCollector>emptyList();
	}

	protected boolean isNothingSelected() {
		return _selectedGroupIds.isEmpty();
	}

	protected boolean isRenderNothing() {
		if (_isFilteredByThisSite()) {
			return true;
		}

		if (!isNothingSelected()) {
			return false;
		}

		List<TermCollector> termCollectors = getTermCollectors();

		return termCollectors.isEmpty();
	}

	protected boolean isSelected(Long groupId) {
		return _selectedGroupIds.contains(groupId);
	}

	private List<BucketDisplayContext> _buildBucketDisplayContexts(
		List<TermCollector> termCollectors) {

		if (termCollectors.isEmpty()) {
			return getEmptySearchResultBucketDisplayContexts();
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>(
			termCollectors.size());

		int limit = termCollectors.size();

		if ((_maxTerms > 0) && (limit > _maxTerms)) {
			limit = _maxTerms;
		}

		for (int i = 0; i < limit; i++) {
			TermCollector termCollector = termCollectors.get(i);

			int count = termCollector.getFrequency();

			if (_countThreshold <= count) {
				bucketDisplayContexts.add(
					buildBucketDisplayContext(termCollector, count));
			}
		}

		if (_order != null) {
			bucketDisplayContexts.sort(
				BucketDisplayContextComparatorFactoryUtil.
					getBucketDisplayContextComparator(_order));
		}

		return bucketDisplayContexts;
	}

	private String _getDescriptiveName(long groupId) {
		Group group = _groupLocalService.fetchGroup(groupId);

		if (group == null) {
			return "[" + groupId + "]";
		}

		try {
			String name = group.getDescriptiveName(_locale);

			if (group.isStagingGroup()) {
				name = StringBundler.concat(
					name, StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
					_language.get(_httpServletRequest, "staged"),
					StringPool.CLOSE_PARENTHESIS);
			}

			return name;
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	private boolean _isFilteredByThisSite() {
		if (_filteredGroupIds.length == 1) {
			return true;
		}

		return false;
	}

	private int _countThreshold;
	private Facet _facet;
	private long[] _filteredGroupIds = {};
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private Language _language;
	private Locale _locale;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _parameterName;
	private List<Long> _selectedGroupIds = Collections.emptyList();
	private boolean _showCounts;
	private final SiteFacetPortletInstanceConfiguration
		_siteFacetPortletInstanceConfiguration;
	private final ThemeDisplay _themeDisplay;

}