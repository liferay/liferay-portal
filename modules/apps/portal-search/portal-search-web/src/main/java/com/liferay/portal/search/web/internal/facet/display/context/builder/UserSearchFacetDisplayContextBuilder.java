/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet.display.context.builder;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.facet.display.context.BucketDisplayContext;
import com.liferay.portal.search.web.internal.facet.display.context.UserSearchFacetDisplayContext;
import com.liferay.portal.search.web.internal.user.facet.configuration.UserFacetPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.util.DisplayContextHelperUtil;
import com.liferay.portal.search.web.internal.util.comparator.BucketDisplayContextComparatorFactoryUtil;

import jakarta.portlet.RenderRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author André de Oliveira
 */
public class UserSearchFacetDisplayContextBuilder {

	public UserSearchFacetDisplayContextBuilder(RenderRequest renderRequest)
		throws ConfigurationException {

		_themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_userFacetPortletInstanceConfiguration =
			ConfigurationProviderUtil.getPortletInstanceConfiguration(
				UserFacetPortletInstanceConfiguration.class, _themeDisplay);
	}

	public UserSearchFacetDisplayContext build() {
		boolean nothingSelected = isNothingSelected();

		List<TermCollector> termCollectors = getTermCollectors();

		boolean renderNothing = false;

		if (nothingSelected && termCollectors.isEmpty()) {
			renderNothing = true;
		}

		UserSearchFacetDisplayContext userSearchFacetDisplayContext =
			new UserSearchFacetDisplayContext();

		userSearchFacetDisplayContext.setBucketDisplayContexts(
			_buildBucketDisplayContexts(termCollectors));
		userSearchFacetDisplayContext.setDisplayStyleGroupId(
			getDisplayStyleGroupId());
		userSearchFacetDisplayContext.setNothingSelected(nothingSelected);
		userSearchFacetDisplayContext.setPaginationStartParameterName(
			_paginationStartParameterName);
		userSearchFacetDisplayContext.setParameterName(_paramName);
		userSearchFacetDisplayContext.setParameterValue(_getFirstParamValue());
		userSearchFacetDisplayContext.setParameterValues(_paramValues);
		userSearchFacetDisplayContext.setRenderNothing(renderNothing);
		userSearchFacetDisplayContext.setUserFacetPortletInstanceConfiguration(
			_userFacetPortletInstanceConfiguration);

		return userSearchFacetDisplayContext;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFrequenciesVisible(boolean frequenciesVisible) {
		_frequenciesVisible = frequenciesVisible;
	}

	public void setFrequencyThreshold(int frequencyThreshold) {
		_frequencyThreshold = frequencyThreshold;
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

	public void setParamName(String paramName) {
		_paramName = paramName;
	}

	public void setParamValue(String paramValue) {
		paramValue = StringUtil.trim(Objects.requireNonNull(paramValue));

		if (paramValue.isEmpty()) {
			return;
		}

		_paramValues = Collections.singletonList(paramValue);
	}

	public void setParamValues(String... paramValues) {
		_paramValues = ListUtil.fromArray(paramValues);
	}

	public void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	protected BucketDisplayContext buildBucketDisplayContext(
		TermCollector termCollector) {

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		long userId = GetterUtil.getLong(termCollector.getTerm());

		bucketDisplayContext.setBucketText(_getDisplayName(userId));
		bucketDisplayContext.setFilterValue(String.valueOf(userId));

		bucketDisplayContext.setFrequency(termCollector.getFrequency());
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setLocale(_locale);
		bucketDisplayContext.setSelected(isSelected(String.valueOf(userId)));

		return bucketDisplayContext;
	}

	protected long getDisplayStyleGroupId() {
		return DisplayContextHelperUtil.getDisplayStyleGroupId(
			_userFacetPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode(),
			_themeDisplay);
	}

	protected List<BucketDisplayContext> getEmptyBucketDisplayContexts() {
		if (_paramValues.isEmpty()) {
			return Collections.emptyList();
		}

		BucketDisplayContext bucketDisplayContext = new BucketDisplayContext();

		bucketDisplayContext.setBucketText(
			_getDisplayName(GetterUtil.getLong(_paramValues.get(0))));
		bucketDisplayContext.setFilterValue(_paramValues.get(0));
		bucketDisplayContext.setFrequency(0);
		bucketDisplayContext.setFrequencyVisible(_frequenciesVisible);
		bucketDisplayContext.setSelected(true);

		return Collections.singletonList(bucketDisplayContext);
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
		return _paramValues.isEmpty();
	}

	protected boolean isSelected(String value) {
		return _paramValues.contains(value);
	}

	private List<BucketDisplayContext> _buildBucketDisplayContexts(
		List<TermCollector> termCollectors) {

		if (termCollectors.isEmpty()) {
			return getEmptyBucketDisplayContexts();
		}

		List<BucketDisplayContext> bucketDisplayContexts = new ArrayList<>(
			termCollectors.size());

		for (int i = 0; i < termCollectors.size(); i++) {
			TermCollector termCollector = termCollectors.get(i);

			if (((_maxTerms > 0) && (i >= _maxTerms)) ||
				((_frequencyThreshold > 0) &&
				 (_frequencyThreshold > termCollector.getFrequency()))) {

				break;
			}

			bucketDisplayContexts.add(buildBucketDisplayContext(termCollector));
		}

		if (_order != null) {
			bucketDisplayContexts.sort(
				BucketDisplayContextComparatorFactoryUtil.
					getBucketDisplayContextComparator(_order));
		}

		return bucketDisplayContexts;
	}

	private String _getDisplayName(long userId) {
		User user = _userLocalService.fetchUser(userId);

		if (user == null) {
			return "[" + userId + "]";
		}

		return user.getFullName();
	}

	private String _getFirstParamValue() {
		if (_paramValues.isEmpty()) {
			return StringPool.BLANK;
		}

		return _paramValues.get(0);
	}

	private Facet _facet;
	private boolean _frequenciesVisible;
	private int _frequencyThreshold;
	private Locale _locale;
	private int _maxTerms;
	private String _order;
	private String _paginationStartParameterName;
	private String _paramName;
	private List<String> _paramValues = Collections.emptyList();
	private final ThemeDisplay _themeDisplay;
	private final UserFacetPortletInstanceConfiguration
		_userFacetPortletInstanceConfiguration;
	private UserLocalService _userLocalService;

}