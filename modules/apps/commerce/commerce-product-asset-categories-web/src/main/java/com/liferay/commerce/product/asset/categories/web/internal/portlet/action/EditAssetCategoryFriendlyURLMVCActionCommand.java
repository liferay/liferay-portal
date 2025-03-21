/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.product.asset.categories.web.internal.constants.CommerceProductAssetCategoriesPortletKeys;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceProductAssetCategoriesPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/commerce_product_asset_categories/edit_asset_category_friendly_url"
	},
	service = MVCActionCommand.class
)
public class EditAssetCategoryFriendlyURLMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long categoryId = ParamUtil.getLong(actionRequest, "categoryId");

		AssetCategory assetCategory = _assetCategoryService.getCategory(
			categoryId);

		Map<Locale, String> urlTitleMap = _localization.getLocalizationMap(
			actionRequest, "urlTitleMapAsXML");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AssetCategory.class.getName(), actionRequest);

		// Commerce product friendly URL

		try {
			FriendlyURLEntry friendlyURLEntry =
				_friendlyURLEntryLocalService.getMainFriendlyURLEntry(
					_portal.getClassNameId(AssetCategory.class), categoryId);

			_friendlyURLEntryLocalService.updateFriendlyURLEntry(
				friendlyURLEntry.getFriendlyURLEntryId(),
				friendlyURLEntry.getClassNameId(),
				friendlyURLEntry.getClassPK(),
				friendlyURLEntry.getDefaultLanguageId(),
				_getUniqueUrlTitles(assetCategory, urlTitleMap));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			_friendlyURLEntryLocalService.addFriendlyURLEntry(
				assetCategory.getGroupId(),
				_portal.getClassNameId(AssetCategory.class), categoryId,
				_getUniqueUrlTitles(assetCategory, urlTitleMap),
				serviceContext);
		}
	}

	private Map<String, String> _getUniqueUrlTitles(
			AssetCategory assetCategory, Map<Locale, String> urlTitleMap)
		throws PortalException {

		Map<String, String> newUrlTitleMap = new HashMap<>();

		long classNameId = _portal.getClassNameId(AssetCategory.class);

		for (Map.Entry<Locale, String> entry : urlTitleMap.entrySet()) {
			Locale locale = entry.getKey();

			String urlTitle = urlTitleMap.get(locale);

			if (Validator.isNotNull(urlTitle) ||
				((urlTitle != null) && urlTitle.equals(StringPool.BLANK))) {

				urlTitle = _friendlyURLEntryLocalService.getUniqueUrlTitle(
					assetCategory.getGroupId(), classNameId,
					assetCategory.getCategoryId(), urlTitle, null);

				newUrlTitleMap.put(LocaleUtil.toLanguageId(locale), urlTitle);
			}
		}

		return newUrlTitleMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditAssetCategoryFriendlyURLMVCActionCommand.class);

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}