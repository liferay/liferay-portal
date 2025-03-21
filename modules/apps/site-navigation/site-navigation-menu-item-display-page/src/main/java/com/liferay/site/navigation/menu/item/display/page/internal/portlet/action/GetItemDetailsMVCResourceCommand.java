/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.display.page.internal.portlet.action;

import com.liferay.asset.display.page.util.AssetDisplayPageUtil;
import com.liferay.info.field.InfoField;
import com.liferay.info.item.InfoItemClassDetails;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.InfoItemFormVariation;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.info.item.provider.InfoItemFormVariationsProvider;
import com.liferay.layout.display.page.LayoutDisplayPageInfoItemFieldValuesProvider;
import com.liferay.layout.display.page.LayoutDisplayPageInfoItemFieldValuesProviderRegistry;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/navigation_menu/get_item_details"
	},
	service = MVCResourceCommand.class
)
public class GetItemDetailsMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long classNameId = ParamUtil.getLong(resourceRequest, "classNameId");
		long classPK = ParamUtil.getLong(resourceRequest, "classPK");
		long classTypeId = ParamUtil.getLong(resourceRequest, "classTypeId");

		String className = _portal.getClassName(classNameId);

		try {
			JSONObject jsonObject = JSONUtil.put(
				"hasDisplayPage",
				AssetDisplayPageUtil.hasAssetDisplayPage(
					themeDisplay.getScopeGroupId(), classNameId, classPK,
					classTypeId));

			String itemType = _getItemType(className, themeDisplay);

			if (Validator.isNotNull(itemType)) {
				jsonObject.put("itemType", itemType);
			}

			String itemSubtype = _getItemSubtype(
				className, classPK, classTypeId, themeDisplay);

			if (Validator.isNotNull(itemSubtype)) {
				jsonObject.put("itemSubtype", itemSubtype);
			}

			jsonObject.put(
				"data", _getDetailsJSONArray(className, classPK, themeDisplay));

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (Exception exception) {
			_log.error("Unable to get mapping fields", exception);

			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse,
				JSONUtil.put(
					"error",
					_language.get(
						themeDisplay.getRequest(),
						"an-unexpected-error-occurred")));
		}
	}

	private JSONArray _getDetailsJSONArray(
			String className, long classPK, ThemeDisplay themeDisplay)
		throws Exception {

		LayoutDisplayPageInfoItemFieldValuesProvider
			layoutDisplayPageInfoItemFieldValuesProvider =
				_layoutDisplayPageInfoItemFieldValuesProviderRegistry.
					getLayoutDisplayPageInfoItemFieldValuesProvider(className);

		if (layoutDisplayPageInfoItemFieldValuesProvider == null) {
			return _jsonFactory.createJSONArray();
		}

		InfoItemFieldValues infoItemFieldValues =
			layoutDisplayPageInfoItemFieldValuesProvider.getInfoItemFieldValues(
				classPK);

		return JSONUtil.toJSONArray(
			infoItemFieldValues.getInfoFieldValues(),
			infoFieldValue -> JSONUtil.put(
				"title",
				() -> {
					InfoField infoField = infoFieldValue.getInfoField();

					return infoField.getLabel(themeDisplay.getLocale());
				}
			).put(
				"value", infoFieldValue.getValue(themeDisplay.getLocale())
			));
	}

	private String _getItemSubtype(
		String className, long classPK, long classTypeId,
		ThemeDisplay themeDisplay) {

		InfoItemFormVariationsProvider<?> infoItemFormVariationsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFormVariationsProvider.class, className);

		if (infoItemFormVariationsProvider == null) {
			return StringPool.BLANK;
		}

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderRegistry.
				getLayoutDisplayPageProviderByClassName(className);

		if (layoutDisplayPageProvider == null) {
			return StringPool.BLANK;
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				new InfoItemReference(className, classPK));

		if (layoutDisplayPageObjectProvider == null) {
			return StringPool.BLANK;
		}

		InfoItemFormVariation infoItemFormVariation =
			infoItemFormVariationsProvider.getInfoItemFormVariation(
				layoutDisplayPageObjectProvider.getGroupId(),
				String.valueOf(classTypeId));

		if (infoItemFormVariation != null) {
			return infoItemFormVariation.getLabel(themeDisplay.getLocale());
		}

		return StringPool.BLANK;
	}

	private String _getItemType(String className, ThemeDisplay themeDisplay) {
		InfoItemDetailsProvider<?> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, className);

		if (infoItemDetailsProvider == null) {
			return StringPool.BLANK;
		}

		InfoItemClassDetails infoItemClassDetails =
			infoItemDetailsProvider.getInfoItemClassDetails();

		if (infoItemClassDetails != null) {
			return infoItemClassDetails.getLabel(themeDisplay.getLocale());
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetItemDetailsMVCResourceCommand.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutDisplayPageInfoItemFieldValuesProviderRegistry
		_layoutDisplayPageInfoItemFieldValuesProviderRegistry;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private Portal _portal;

}