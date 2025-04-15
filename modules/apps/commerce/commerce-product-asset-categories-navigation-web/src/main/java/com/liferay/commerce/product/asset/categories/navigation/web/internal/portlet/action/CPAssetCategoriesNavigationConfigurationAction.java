/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.navigation.web.internal.portlet.action;

import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.asset.categories.navigation.web.internal.display.context.CPAssetCategoriesNavigationDisplayContext;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.commerce.product.url.CPFriendlyURL;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
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
 * @author Marco Leo
 */
@Component(
	property = "jakarta.portlet.name=" + CPPortletKeys.CP_ASSET_CATEGORIES_NAVIGATION,
	service = ConfigurationAction.class
)
public class CPAssetCategoriesNavigationConfigurationAction
	extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		try {
			CPAssetCategoriesNavigationDisplayContext
				cpAssetCategoryNavigationDisplayContext =
					new CPAssetCategoriesNavigationDisplayContext(
						_assetCategoryService, _assetVocabularyService,
						_commerceMediaResolver, _cpAttachmentFileEntryService,
						_cpFriendlyURL, _friendlyURLEntryLocalService,
						_groupLocalService, httpServletRequest, _portal);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpAssetCategoryNavigationDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return "/configuration.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPAssetCategoriesNavigationConfigurationAction.class);

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetVocabularyService _assetVocabularyService;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private CPFriendlyURL _cpFriendlyURL;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}