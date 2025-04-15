/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.menu.item.display.page.internal.portlet.action;

import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.HierarchicalInfoItemReference;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageMultiSelectionProvider;
import com.liferay.layout.display.page.LayoutDisplayPageMultiSelectionProviderRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.navigation.admin.constants.SiteNavigationAdminPortletKeys;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemService;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SiteNavigationAdminPortletKeys.SITE_NAVIGATION_ADMIN,
		"mvc.command.name=/navigation_menu/add_multiple_display_page_type_site_navigation_menu_item"
	},
	service = MVCActionCommand.class
)
public class AddMultipleDisplayPageTypeSiteNavigationMenuItemMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		String siteNavigationMenuItemTypeString = ParamUtil.getString(
			actionRequest, "siteNavigationMenuItemType");
		long siteNavigationMenuId = ParamUtil.getLong(
			actionRequest, "siteNavigationMenuId");

		if (Validator.isNotNull(siteNavigationMenuItemTypeString) &&
			(siteNavigationMenuId > 0)) {

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				actionRequest);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			try {
				List<InfoItemReference> infoItemReferences = new ArrayList<>();
				Map<Long, JSONObject> jsonObjects = new HashMap<>();

				JSONArray jsonArray = _jsonFactory.createJSONArray(
					ParamUtil.getString(actionRequest, "items"));

				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject itemJSONObject = jsonArray.getJSONObject(i);

					if ((itemJSONObject != null) &&
						Objects.equals(
							itemJSONObject.getString("className"),
							siteNavigationMenuItemTypeString)) {

						infoItemReferences.add(
							new InfoItemReference(
								itemJSONObject.getString("className"),
								itemJSONObject.getLong("classPK")));

						jsonObjects.put(
							itemJSONObject.getLong("classPK"), itemJSONObject);
					}
				}

				LayoutDisplayPageMultiSelectionProvider<?>
					layoutDisplayPageMultiSelectionProvider =
						_layoutDisplayPageMultiSelectionProviderRegistry.
							getLayoutDisplayPageMultiSelectionProvider(
								siteNavigationMenuItemTypeString);

				if (layoutDisplayPageMultiSelectionProvider != null) {
					infoItemReferences =
						layoutDisplayPageMultiSelectionProvider.process(
							infoItemReferences);
				}

				int order = ParamUtil.getInteger(actionRequest, "order", -1);
				long parentSiteNavigationMenuItemId = ParamUtil.getLong(
					actionRequest, "parentSiteNavigationMenuItemId");

				for (int i = 0; i < infoItemReferences.size(); i++) {
					InfoItemReference infoItemReference =
						infoItemReferences.get(i);

					_addSiteNavigationMenuItem(
						themeDisplay.getScopeGroupId(), infoItemReference,
						jsonObjects, order + i, parentSiteNavigationMenuItemId,
						serviceContext, siteNavigationMenuId,
						siteNavigationMenuItemTypeString);
				}

				SiteNavigationMenuItemType siteNavigationMenuItemType =
					_siteNavigationMenuItemTypeRegistry.
						getSiteNavigationMenuItemType(
							siteNavigationMenuItemTypeString);

				String message = _language.format(
					themeDisplay.getLocale(), "x-x-was-added-to-this-menu",
					Arrays.asList(
						jsonArray.length(),
						siteNavigationMenuItemType.getLabel(
							themeDisplay.getLocale())));

				if ((jsonArray.length() > 1) &&
					(layoutDisplayPageMultiSelectionProvider != null)) {

					message = _language.format(
						themeDisplay.getLocale(), "x-x-were-added-to-this-menu",
						Arrays.asList(
							jsonArray.length(),
							layoutDisplayPageMultiSelectionProvider.
								getPluralLabel(themeDisplay.getLocale())));
				}

				SessionMessages.add(
					actionRequest, "siteNavigationMenuItemsAdded", message);
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				jsonObject.put(
					"errorMessage",
					_language.get(
						_portal.getHttpServletRequest(actionRequest),
						"an-unexpected-error-occurred"));
			}
		}
		else {
			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Unable to add multiple site navigation menu items ",
						"for site navigation menu ID ", siteNavigationMenuId,
						" and type ", siteNavigationMenuItemTypeString));
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

	private void _addSiteNavigationMenuItem(
			long groupId, InfoItemReference infoItemReference,
			Map<Long, JSONObject> jsonObjects, int order,
			long parentSiteNavigationMenuItemId, ServiceContext serviceContext,
			long siteNavigationMenuId, String siteNavigationMenuItemType)
		throws PortalException {

		JSONObject jsonObject = jsonObjects.get(_getClassPK(infoItemReference));

		if (jsonObject == null) {
			return;
		}

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemService.addSiteNavigationMenuItem(
				null, groupId, siteNavigationMenuId,
				parentSiteNavigationMenuItemId, siteNavigationMenuItemType,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", jsonObject.getString("className")
				).put(
					"classNameId", jsonObject.getString("classNameId")
				).put(
					"classPK", jsonObject.getString("classPK")
				).put(
					"classTypeId", jsonObject.getString("classTypeId")
				).put(
					"title", jsonObject.getString("title")
				).put(
					"type", jsonObject.getString("type")
				).buildString(),
				serviceContext);

		if (order >= 0) {
			_siteNavigationMenuItemService.updateSiteNavigationMenuItem(
				siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				parentSiteNavigationMenuItemId, order);
		}

		if (!(infoItemReference instanceof HierarchicalInfoItemReference)) {
			return;
		}

		HierarchicalInfoItemReference hierarchicalInfoItemReference =
			(HierarchicalInfoItemReference)infoItemReference;

		for (HierarchicalInfoItemReference childHierarchicalInfoItemReference :
				hierarchicalInfoItemReference.
					getChildrenHierarchicalInfoItemReferences()) {

			_addSiteNavigationMenuItem(
				groupId, childHierarchicalInfoItemReference, jsonObjects, -1,
				siteNavigationMenuItem.getSiteNavigationMenuItemId(),
				serviceContext, siteNavigationMenuId,
				siteNavigationMenuItemType);
		}
	}

	private long _getClassPK(InfoItemReference infoItemReference) {
		if (infoItemReference.getInfoItemIdentifier() instanceof
				ClassPKInfoItemIdentifier) {

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			return classPKInfoItemIdentifier.getClassPK();
		}

		return 0;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddMultipleDisplayPageTypeSiteNavigationMenuItemMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutDisplayPageMultiSelectionProviderRegistry
		_layoutDisplayPageMultiSelectionProviderRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private SiteNavigationMenuItemService _siteNavigationMenuItemService;

	@Reference
	private SiteNavigationMenuItemTypeRegistry
		_siteNavigationMenuItemTypeRegistry;

}