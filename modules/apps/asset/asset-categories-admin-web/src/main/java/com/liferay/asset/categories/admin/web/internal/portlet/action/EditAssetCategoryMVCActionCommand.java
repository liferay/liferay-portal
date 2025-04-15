/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.portlet.action;

import com.liferay.asset.categories.admin.web.constants.AssetCategoriesAdminPortletKeys;
import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.AssetCategoryPropertyLocalService;
import com.liferay.asset.display.page.portlet.AssetDisplayPageEntryFormProcessor;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Diego Hu
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AssetCategoriesAdminPortletKeys.ASSET_CATEGORIES_ADMIN,
		"mvc.command.name=/asset_categories_admin/edit_asset_category"
	},
	service = MVCActionCommand.class
)
public class EditAssetCategoryMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long categoryId = ParamUtil.getLong(actionRequest, "categoryId");

		long parentCategoryId = ParamUtil.getLong(
			actionRequest, "parentCategoryId");
		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");
		long vocabularyId = ParamUtil.getLong(actionRequest, "vocabularyId");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			AssetCategory.class.getName(), actionRequest);

		hideDefaultSuccessMessage(actionRequest);

		MultiSessionMessages.add(
			actionRequest, actionResponse.getNamespace() + "requestProcessed");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String title = titleMap.get(themeDisplay.getLocale());

		if (Validator.isBlank(title)) {
			title = titleMap.get(themeDisplay.getSiteDefaultLocale());
		}

		AssetCategory category = null;

		if (categoryId <= 0) {

			// Add category

			long groupId = ParamUtil.getLong(actionRequest, "groupId");

			category = _assetCategoryService.addCategory(
				groupId, parentCategoryId, titleMap, descriptionMap,
				vocabularyId, null, serviceContext);

			MultiSessionMessages.add(
				actionRequest, "categoryAdded",
				_language.format(
					_portal.getHttpServletRequest(actionRequest),
					"x-was-created-successfully",
					new Object[] {HtmlUtil.escape(title)}));
		}
		else {

			// Update category

			String[] categoryPropertiesArray = _getCategoryProperties(
				_assetCategoryPropertyLocalService.getCategoryProperties(
					categoryId));

			category = _assetCategoryService.updateCategory(
				categoryId, parentCategoryId, titleMap, descriptionMap,
				vocabularyId, categoryPropertiesArray, serviceContext);

			MultiSessionMessages.add(
				actionRequest, "categoryUpdated",
				_language.format(
					_portal.getHttpServletRequest(actionRequest),
					"x-was-updated-successfully",
					new Object[] {HtmlUtil.escape(title)}));
		}

		// Asset display page

		_assetDisplayPageEntryFormProcessor.process(
			AssetCategory.class.getName(), category.getCategoryId(),
			actionRequest);

		sendRedirect(actionRequest, actionResponse);
	}

	private String[] _getCategoryProperties(
		List<AssetCategoryProperty> assetCategoryProperties) {

		String[] assetCategoryPropertiesArray =
			new String[assetCategoryProperties.size()];

		for (int i = 0; i < assetCategoryProperties.size(); i++) {
			AssetCategoryProperty categoryProperty =
				assetCategoryProperties.get(i);

			assetCategoryPropertiesArray[i] =
				categoryProperty.getKey() +
					AssetCategoryConstants.PROPERTY_KEY_VALUE_SEPARATOR +
						categoryProperty.getValue();
		}

		return assetCategoryPropertiesArray;
	}

	@Reference
	private AssetCategoryPropertyLocalService
		_assetCategoryPropertyLocalService;

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private AssetDisplayPageEntryFormProcessor
		_assetDisplayPageEntryFormProcessor;

	@Reference
	private Language _language;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}