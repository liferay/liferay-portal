/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPOptionFacetsPortletInstanceConfiguration;
import com.liferay.commerce.product.content.search.web.internal.util.CPOptionFacetsUtil;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;

/**
 * @author Andrea Sbarra
 * @author Alessio Antonio Rendina
 */
public class CPOptionsSearchFacetDisplayContext implements Serializable {

	public CPOptionsSearchFacetDisplayContext(
			ConfigurationProvider configurationProvider,
			GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_configurationProvider = configurationProvider;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_cpOptionFacetsPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CPOptionFacetsPortletInstanceConfiguration.class,
				_cpRequestHelper.getThemeDisplay());
		_renderRequest = _cpRequestHelper.getRenderRequest();
	}

	public CPOption getCPOption(long companyId, String fieldName) {
		String cpOptionKey =
			CPOptionFacetsUtil.getCPOptionKeyFromIndexFieldName(fieldName);

		return _cpOptionLocalService.fetchCPOption(companyId, cpOptionKey);
	}

	public CPOptionFacetsPortletInstanceConfiguration
		getCPOptionFacetsPortletInstanceConfiguration() {

		return _cpOptionFacetsPortletInstanceConfiguration;
	}

	public String getCPOptionKey(long companyId, String fieldName)
		throws PortalException {

		CPOption cpOption = getCPOption(companyId, fieldName);

		return cpOption.getKey();
	}

	public String getCPOptionName(long companyId, String fieldName)
		throws PortalException {

		CPOption cpOption = getCPOption(companyId, fieldName);

		String name = StringPool.BLANK;

		if (cpOption != null) {
			name = cpOption.getName(_locale);
		}

		return name;
	}

	public String getDisplayStyle() {
		return _cpOptionFacetsPortletInstanceConfiguration.displayStyle();
	}

	public long getDisplayStyleGroupId() {
		if (_displayStyleGroupId != null) {
			return _displayStyleGroupId;
		}

		String displayStyleGroupExternalReferenceCode =
			_cpOptionFacetsPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupId = group.getGroupId();
		}
		else {
			_displayStyleGroupId = themeDisplay.getScopeGroupId();
		}

		return _displayStyleGroupId;
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			_cpOptionFacetsPortletInstanceConfiguration.
				displayStyleGroupExternalReferenceCode();

		ThemeDisplay themeDisplay = _cpRequestHelper.getThemeDisplay();

		Group group = themeDisplay.getScopeGroup();

		if (Validator.isNotNull(displayStyleGroupExternalReferenceCode)) {
			group = _groupLocalService.fetchGroupByExternalReferenceCode(
				displayStyleGroupExternalReferenceCode,
				themeDisplay.getCompanyId());
		}

		if (group != null) {
			_displayStyleGroupKey = group.getGroupKey();
		}
		else {
			_displayStyleGroupKey = StringPool.BLANK;
		}

		return _displayStyleGroupKey;
	}

	public List<Facet> getFacets() {
		return _facets;
	}

	public int getFrequencyThreshold() {
		return _cpOptionFacetsPortletInstanceConfiguration.frequencyThreshold();
	}

	public int getMaxOptions() {
		return _cpOptionFacetsPortletInstanceConfiguration.maxOptions();
	}

	public int getMaxTerms() {
		return _cpOptionFacetsPortletInstanceConfiguration.maxTerms();
	}

	public String getPaginationStartParameterName() {
		return _paginationStartParameterName;
	}

	public List<CPOptionsSearchFacetTermDisplayContext>
		getTermDisplayContexts() {

		return _cpOptionsSearchFacetTermDisplayContext;
	}

	public boolean hasCommerceChannel() throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		if (commerceContext == null) {
			return false;
		}

		long commerceChannelId = commerceContext.getCommerceChannelId();

		if (commerceChannelId > 0) {
			return true;
		}

		return false;
	}

	public boolean isCPOptionValueSelected(
		long companyId, String fieldName, String fieldValue) {

		CPOption cpOption = getCPOption(companyId, fieldName);

		return ArrayUtil.contains(
			_portletSharedSearchResponse.getParameterValues(
				cpOption.getKey(), _renderRequest),
			fieldValue);
	}

	public boolean isFrequenciesVisible() {
		return _cpOptionFacetsPortletInstanceConfiguration.frequenciesVisible();
	}

	public Boolean isShowClear(long companyId, String fieldName) {
		CPOption cpOption = getCPOption(companyId, fieldName);

		String[] parameterValues =
			_portletSharedSearchResponse.getParameterValues(
				cpOption.getKey(), _renderRequest);

		if (parameterValues != null) {
			return true;
		}

		return false;
	}

	public void setCPOptionLocalService(
		CPOptionLocalService cpOptionLocalService) {

		_cpOptionLocalService = cpOptionLocalService;
	}

	public void setFacets(List<Facet> facets) {
		_facets = facets;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setPaginationStartParameterName(
		String paginationStartParameterName) {

		_paginationStartParameterName = paginationStartParameterName;
	}

	public void setPortletSharedSearchResponse(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		_portletSharedSearchResponse = portletSharedSearchResponse;
	}

	public void setTermDisplayContexts(
		List<CPOptionsSearchFacetTermDisplayContext>
			cpOptionsSearchFacetTermDisplayContext) {

		_cpOptionsSearchFacetTermDisplayContext =
			cpOptionsSearchFacetTermDisplayContext;
	}

	private final ConfigurationProvider _configurationProvider;
	private final CPOptionFacetsPortletInstanceConfiguration
		_cpOptionFacetsPortletInstanceConfiguration;
	private CPOptionLocalService _cpOptionLocalService;
	private List<CPOptionsSearchFacetTermDisplayContext>
		_cpOptionsSearchFacetTermDisplayContext;
	private final CPRequestHelper _cpRequestHelper;
	private Long _displayStyleGroupId;
	private String _displayStyleGroupKey;
	private List<Facet> _facets;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private Locale _locale;
	private String _paginationStartParameterName;
	private PortletSharedSearchResponse _portletSharedSearchResponse;
	private final RenderRequest _renderRequest;

}