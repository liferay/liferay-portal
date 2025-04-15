/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.web.internal.portlet.action;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.layout.seo.service.LayoutSEOEntryService;
import com.liferay.layout.seo.web.internal.util.LayoutTypeSettingsUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout/edit_seo"
	},
	service = MVCActionCommand.class
)
public class EditSEOMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");
		boolean privateLayout = ParamUtil.getBoolean(
			actionRequest, "privateLayout");
		long layoutId = ParamUtil.getLong(actionRequest, "layoutId");

		Layout layout = _layoutLocalService.getLayout(
			groupId, privateLayout, layoutId);

		Map<Locale, String> titleMap = _localization.getLocalizationMap(
			actionRequest, "title");
		Map<Locale, String> descriptionMap = _localization.getLocalizationMap(
			actionRequest, "description");

		Map<Locale, String> keywordsMap = _localization.getLocalizationMap(
			actionRequest, "keywords");
		Map<Locale, String> robotsMap = _localization.getLocalizationMap(
			actionRequest, "robots");

		ServiceContext serviceContext = _getServiceContent(
			actionRequest, layout);

		layout = _layoutService.updateLayout(
			groupId, privateLayout, layoutId, layout.getParentLayoutId(),
			layout.getNameMap(), titleMap, descriptionMap, keywordsMap,
			robotsMap, layout.getType(), layout.isHidden(),
			layout.getFriendlyURLMap(), layout.isIconImage(), null,
			layout.getStyleBookEntryId(), layout.getFaviconFileEntryId(),
			layout.getMasterLayoutPlid(), serviceContext);

		boolean canonicalURLEnabled = ParamUtil.getBoolean(
			actionRequest, "canonicalURLEnabled");
		Map<Locale, String> canonicalURLMap = _localization.getLocalizationMap(
			actionRequest, "canonicalURL");

		_layoutSEOEntryService.updateLayoutSEOEntry(
			groupId, privateLayout, layoutId, canonicalURLEnabled,
			canonicalURLMap, serviceContext);

		UnicodeProperties formTypeSettingsUnicodeProperties =
			PropertiesParamUtil.getProperties(
				actionRequest, "TypeSettingsProperties--");

		for (Map.Entry<Locale, String> entry : robotsMap.entrySet()) {
			String value = entry.getValue();

			if (Validator.isNotNull(value) &&
				(StringUtil.containsIgnoreCase(
					value, "nofollow", StringPool.BLANK) ||
				 StringUtil.containsIgnoreCase(
					 value, "noindex", StringPool.BLANK))) {

				formTypeSettingsUnicodeProperties.setProperty(
					LayoutTypePortletConstants.SITEMAP_INCLUDE, "0");

				break;
			}
		}

		themeDisplay.clearLayoutFriendlyURL(layout);

		layout = LayoutTypeSettingsUtil.updateTypeSettings(
			layout, _layoutService, formTypeSettingsUnicodeProperties);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNull(redirect)) {
			redirect = _portal.getLayoutFullURL(layout, themeDisplay);
		}

		String portletResource = ParamUtil.getString(
			actionRequest, "portletResource");

		MultiSessionMessages.add(
			actionRequest, portletResource + "layoutUpdated", layout);

		actionRequest.setAttribute(WebKeys.REDIRECT, redirect);
	}

	private ServiceContext _getServiceContent(
			ActionRequest actionRequest, Layout layout)
		throws Exception {

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Layout.class.getName(), actionRequest);

		AssetEntry assetEntry = _assetEntryLocalService.getEntry(
			Layout.class.getName(), layout.getPlid());

		serviceContext.setAssetCategoryIds(assetEntry.getCategoryIds());
		serviceContext.setAssetTagNames(assetEntry.getTagNames());

		if (layout.isTypeAssetDisplay() || layout.isTypeUtility()) {
			serviceContext.setAttribute(
				"layout.instanceable.allowed", Boolean.TRUE);
		}

		return serviceContext;
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSEOEntryService _layoutSEOEntryService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}