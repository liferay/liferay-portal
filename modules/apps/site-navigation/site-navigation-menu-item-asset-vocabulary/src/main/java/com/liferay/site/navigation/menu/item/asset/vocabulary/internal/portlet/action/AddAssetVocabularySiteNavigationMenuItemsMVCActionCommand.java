/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.asset.vocabulary.internal.portlet.action;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Arrays;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/navigation_menu/add_asset_vocabulary_type_site_navigation_menu_items"
	},
	service = MVCActionCommand.class
)
public class AddAssetVocabularySiteNavigationMenuItemsMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		long siteNavigationMenuId = ParamUtil.getLong(
			actionRequest, "siteNavigationMenuId");

		if (siteNavigationMenuId > 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			JSONArray jsonArray = _jsonFactory.createJSONArray(
				ParamUtil.getString(actionRequest, "items"));

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject assetVocabularyJSONObject = jsonArray.getJSONObject(
					i);

				if (assetVocabularyJSONObject == null) {
					continue;
				}

				long parentSiteNavigationMenuItemId = ParamUtil.getLong(
					actionRequest, "parentSiteNavigationMenuItemId");

				SiteNavigationMenuItem siteNavigationMenuItem =
					_siteNavigationMenuItemService.addSiteNavigationMenuItem(
						null, themeDisplay.getScopeGroupId(),
						siteNavigationMenuId, parentSiteNavigationMenuItemId,
						SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY,
						UnicodePropertiesBuilder.create(
							true
						).put(
							"classPK",
							assetVocabularyJSONObject.getString(
								"assetVocabularyId")
						).put(
							"groupId",
							assetVocabularyJSONObject.getString("groupId")
						).put(
							"title",
							assetVocabularyJSONObject.getString("title")
						).put(
							"type", "asset-vocabulary"
						).put(
							"uuid", assetVocabularyJSONObject.getString("uuid")
						).buildString(),
						serviceContext);

				int order = ParamUtil.getInteger(actionRequest, "order", -1);

				if (order >= 0) {
					_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
						siteNavigationMenuItem.getSiteNavigationMenuItemId(),
						parentSiteNavigationMenuItemId, order + i);
				}
			}

			String message = _language.format(
				themeDisplay.getLocale(), "x-x-was-added-to-this-menu",
				Arrays.asList(jsonArray.length(), "vocabulary"));

			if (jsonArray.length() > 1) {
				message = _language.format(
					themeDisplay.getLocale(), "x-x-were-added-to-this-menu",
					Arrays.asList(jsonArray.length(), "vocabularies"));
			}

			SessionMessages.add(
				actionRequest, "siteNavigationMenuItemsAdded", message);
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to add asset vocabulary site navigation menu ",
						"items for site navigation menu ID ",
						siteNavigationMenuId));
			}

			jsonObject.put(
				"errorMessage",
				_language.get(
					_portal.getHttpServletRequest(actionRequest),
					"an-unexpected-error-occurred"));
		}

		JSONPortletResponseUtil.writeJSON(
			actionRequest, actionResponse, jsonObject);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddAssetVocabularySiteNavigationMenuItemsMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

}