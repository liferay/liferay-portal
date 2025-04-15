/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.display.context;

import com.liferay.commerce.product.content.search.web.internal.util.CPSpecificationOptionFacetsUtil;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.RenderRequest;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;

/**
 * @author Crescenzo Rega
 */
public class CPSpecificationOptionsSearchFacetDisplayContext
	implements Serializable {

	public CPSpecificationOption getCPSpecificationOption(String fieldName) {
		return _cpSpecificationOptionLocalService.fetchCPSpecificationOption(
			PortalUtil.getCompanyId(_renderRequest),
			CPSpecificationOptionFacetsUtil.
				getCPSpecificationOptionKeyFromIndexFieldName(fieldName));
	}

	public String getCPSpecificationOptionTitle(String fieldName) {
		CPSpecificationOption cpSpecificationOption = getCPSpecificationOption(
			fieldName);

		return cpSpecificationOption.getTitle(_locale);
	}

	public Facet getFacet() {
		return _facet;
	}

	public String getPaginationStartParameterName() {
		return _paginationStartParameterName;
	}

	public String getParameterName() {
		return _parameterName;
	}

	public String getParameterValue() {
		return _parameterValue;
	}

	public Double getPriority() {
		return _priority;
	}

	public List<CPSpecificationOptionsSearchFacetTermDisplayContext>
		getTermDisplayContexts() {

		return _cpSpecificationOptionsSearchFacetTermDisplayContext;
	}

	public boolean isShowClear() {
		return ListUtil.exists(
			_cpSpecificationOptionsSearchFacetTermDisplayContext,
			CPSpecificationOptionsSearchFacetTermDisplayContext::isSelected);
	}

	public void setCpSpecificationOptionLocalService(
		CPSpecificationOptionLocalService cpSpecificationOptionLocalService) {

		_cpSpecificationOptionLocalService = cpSpecificationOptionLocalService;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
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

	public void setPriority(Double priority) {
		_priority = priority;
	}

	public void setRenderRequest(RenderRequest renderRequest) {
		_renderRequest = renderRequest;
	}

	public void setTermDisplayContexts(
		List<CPSpecificationOptionsSearchFacetTermDisplayContext>
			assetCPSpecificationOptionsSearchFacetTermDisplayContext) {

		_cpSpecificationOptionsSearchFacetTermDisplayContext =
			assetCPSpecificationOptionsSearchFacetTermDisplayContext;
	}

	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;
	private List<CPSpecificationOptionsSearchFacetTermDisplayContext>
		_cpSpecificationOptionsSearchFacetTermDisplayContext;
	private Facet _facet;
	private Locale _locale;
	private String _paginationStartParameterName;
	private String _parameterName;
	private String _parameterValue;
	private Double _priority;
	private RenderRequest _renderRequest;

}