/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.content.search.web.internal.configuration.CPSpecificationOptionFacetsPortletInstanceConfiguration;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Serializable;

import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CPSpecificationOptionFacetsDisplayContext implements Serializable {

	public CPSpecificationOptionFacetsDisplayContext(
			ConfigurationProvider configurationProvider,
			GroupLocalService groupLocalService,
			HttpServletRequest httpServletRequest)
		throws ConfigurationException {

		_configurationProvider = configurationProvider;
		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		_cpSpecificationOptionFacetsPortletInstanceConfiguration =
			configurationProvider.getPortletInstanceConfiguration(
				CPSpecificationOptionFacetsPortletInstanceConfiguration.class,
				_cpRequestHelper.getThemeDisplay());
	}

	public CPSpecificationOptionFacetsPortletInstanceConfiguration
		getCPSpecificationOptionFacetsPortletInstanceConfiguration() {

		return _cpSpecificationOptionFacetsPortletInstanceConfiguration;
	}

	public List<CPSpecificationOptionsSearchFacetDisplayContext>
		getCPSpecificationOptionsSearchFacetDisplayContexts() {

		return _cpSpecificationOptionsSearchFacetDisplayContexts;
	}

	public long getDisplayStyleGroupId() {
		return _cpSpecificationOptionFacetsPortletInstanceConfiguration.
			displayStyleGroupId();
	}

	public String getDisplayStyleGroupKey() {
		if (Validator.isNotNull(_displayStyleGroupKey)) {
			return _displayStyleGroupKey;
		}

		String displayStyleGroupExternalReferenceCode =
			_cpSpecificationOptionFacetsPortletInstanceConfiguration.
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

	public int getFrequencyThreshold() {
		return _cpSpecificationOptionFacetsPortletInstanceConfiguration.
			frequencyThreshold();
	}

	public int getMaxSpecifications() {
		return _cpSpecificationOptionFacetsPortletInstanceConfiguration.
			maxSpecifications();
	}

	public int getMaxTerms() {
		return _cpSpecificationOptionFacetsPortletInstanceConfiguration.
			maxTerms();
	}

	public String getSpecificationsOrder() {
		return _cpSpecificationOptionFacetsPortletInstanceConfiguration.
			specificationsOrder();
	}

	public boolean hasCommerceChannel() throws PortalException {
		CommerceContext commerceContext =
			(CommerceContext)_httpServletRequest.getAttribute(
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

	public boolean isFrequenciesVisible() {
		return _cpSpecificationOptionFacetsPortletInstanceConfiguration.
			frequenciesVisible();
	}

	public void setCPSpecificationOptionsSearchFacetDisplayContexts(
		List<CPSpecificationOptionsSearchFacetDisplayContext>
			cpSpecificationOptionsSearchFacetDisplayContexts) {

		_cpSpecificationOptionsSearchFacetDisplayContexts =
			cpSpecificationOptionsSearchFacetDisplayContexts;
	}

	private final ConfigurationProvider _configurationProvider;
	private final CPRequestHelper _cpRequestHelper;
	private final CPSpecificationOptionFacetsPortletInstanceConfiguration
		_cpSpecificationOptionFacetsPortletInstanceConfiguration;
	private List<CPSpecificationOptionsSearchFacetDisplayContext>
		_cpSpecificationOptionsSearchFacetDisplayContexts;
	private String _displayStyleGroupKey;
	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;

}