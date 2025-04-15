/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.asset;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.model.CPDefinitionLink;
import com.liferay.commerce.product.service.CPDefinitionLinkLocalService;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.portal.kernel.exception.PortalException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_DEFINITIONS,
	service = AssetRendererFactory.class
)
public class CPDefinitionLinkAssetRendererFactory
	extends BaseAssetRendererFactory<CPDefinitionLink> {

	public static final String TYPE = "product_link";

	public CPDefinitionLinkAssetRendererFactory() {
		setClassName(CPDefinitionLink.class.getName());
		setPortletId(CPPortletKeys.CP_DEFINITIONS);
	}

	@Override
	public AssetRenderer<CPDefinitionLink> getAssetRenderer(
			long classPK, int type)
		throws PortalException {

		return new CPDefinitionLinkAssetRenderer(
			_cpDefinitionLinkLocalService.getCPDefinitionLink(classPK),
			_cpDefinitionLocalService);
	}

	@Override
	public String getIconCssClass() {
		return "product";
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Reference
	private CPDefinitionLinkLocalService _cpDefinitionLinkLocalService;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

}