/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.asset;

import com.liferay.asset.kernel.model.BaseJSPAssetRenderer;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Danny Situ
 */
public class CPDefinitionLinkAssetRenderer
	extends BaseJSPAssetRenderer<CPDefinitionLink> {

	public CPDefinitionLinkAssetRenderer(
		CPDefinitionLink cpDefinitionLink,
		CPDefinitionLocalService cpDefinitionLocalService) {

		_cpDefinitionLink = cpDefinitionLink;
		_cpDefinitionLocalService = cpDefinitionLocalService;
	}

	@Override
	public CPDefinitionLink getAssetObject() {
		return _cpDefinitionLink;
	}

	@Override
	public String getClassName() {
		return CPDefinitionLink.class.getName();
	}

	@Override
	public long getClassPK() {
		return _cpDefinitionLink.getCPDefinitionLinkId();
	}

	@Override
	public long getGroupId() {
		return _cpDefinitionLink.getGroupId();
	}

	@Override
	public String getJspPath(
		HttpServletRequest httpServletRequest, String template) {

		return "/asset/cp_definition_link_full_content.jsp";
	}

	@Override
	public int getStatus() {
		return _cpDefinitionLink.getStatus();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return StringPool.BLANK;
	}

	@Override
	public String getTitle(Locale locale) {
		CPDefinition cpDefinition = _cpDefinitionLink.getCPDefinition();

		return cpDefinition.getName(LanguageUtil.getLanguageId(locale));
	}

	@Override
	public long getUserId() {
		return _cpDefinitionLink.getUserId();
	}

	@Override
	public String getUserName() {
		return _cpDefinitionLink.getUserName();
	}

	@Override
	public String getUuid() {
		return _cpDefinitionLink.getUuid();
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		httpServletRequest.setAttribute(
			CPWebKeys.CP_DEFINITION_LINK, ListUtil.toList(_cpDefinitionLink));

		return super.include(httpServletRequest, httpServletResponse, template);
	}

	private final CPDefinitionLink _cpDefinitionLink;
	private final CPDefinitionLocalService _cpDefinitionLocalService;

}