/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.category.web.internal.portlet.action;

import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.category.web.internal.display.context.CPCategoryContentDisplayContext;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_CATEGORY_CONTENT_WEB,
	service = ConfigurationAction.class
)
public class CPCategoryContentConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		try {
			CPCategoryContentDisplayContext cpCategoryContentDisplayContext =
				new CPCategoryContentDisplayContext(
					httpServletRequest, _assetCategoryService,
					_commerceMediaResolver, _cpAttachmentFileEntryService,
					_groupLocalService, _portal);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpCategoryContentDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return "/configuration.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPCategoryContentConfigurationAction.class);

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}