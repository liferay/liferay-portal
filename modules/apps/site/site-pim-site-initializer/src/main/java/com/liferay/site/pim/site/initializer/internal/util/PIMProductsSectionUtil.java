/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.constants.PIMObjectFolderConstants;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class PIMProductsSectionUtil {

	public static DropdownItem getCreateFolderDropdownItem(
		HttpServletRequest httpServletRequest,
		String parentObjectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay = _getThemeDisplay(httpServletRequest);

		return DropdownItemBuilder.putData(
			"action", "createFolder"
		).putData(
			"baseAssetLibraryViewURL",
			_getBaseURL(DepotEntry.class, "/e/space/", themeDisplay)
		).putData(
			"baseFolderViewURL",
			_getBaseURL(
				ObjectEntryFolder.class, "/e/view-folder/", themeDisplay)
		).putData(
			"parentObjectEntryFolderExternalReferenceCode",
			parentObjectEntryFolderExternalReferenceCode
		).setIcon(
			"folder"
		).setLabel(
			LanguageUtil.get(httpServletRequest, "folder")
		).build();
	}

	public static List<DropdownItem> getCreationMenuDropdownItems(
		HttpServletRequest httpServletRequest,
		String objectEntryFolderExternalReferenceCode) {

		ThemeDisplay themeDisplay = _getThemeDisplay(httpServletRequest);

		List<DropdownItem> dropdownItems = new ArrayList<>();

		dropdownItems.add(
			getCreateFolderDropdownItem(
				httpServletRequest, objectEntryFolderExternalReferenceCode));

		for (ObjectDefinition objectDefinition :
				ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
					themeDisplay.getCompanyId(),
					new String[] {
						PIMObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
					})) {

			dropdownItems.add(
				getProductTypeDropdownItem(
					objectDefinition, objectEntryFolderExternalReferenceCode,
					themeDisplay));
		}

		return dropdownItems;
	}

	public static DropdownItem getDeleteBulkActionDropdownItem(
		HttpServletRequest httpServletRequest) {

		return FDSActionDropdownItemBuilder.setHref(
			"#"
		).setIcon(
			"trash"
		).setLabel(
			LanguageUtil.get(httpServletRequest, "delete")
		).build(
			"delete"
		);
	}

	public static Map<String, Object> getEmptyState(
		HttpServletRequest httpServletRequest) {

		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				httpServletRequest, "click-new-to-create-your-first-product")
		).put(
			"symbol", "products"
		).put(
			"title", LanguageUtil.get(httpServletRequest, "no-products-yet")
		).build();
	}

	public static List<FDSActionDropdownItem> getFDSActionDropdownItems(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay = _getThemeDisplay(httpServletRequest);

		String baseViewFolderURL = _getBaseURL(
			ObjectEntryFolder.class, "/e/view-folder/", themeDisplay);

		Map<String, Object> folderVisibilityFilters =
			HashMapBuilder.<String, Object>put(
				"entryClassName", ObjectEntryFolder.class.getName()
			).build();

		return List.of(
			FDSActionDropdownItemBuilder.setHref(
				baseViewFolderURL + "{embedded.id}"
			).setIcon(
				"view"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "view-folder")
			).setMethod(
				"get"
			).setPermissionKey(
				"get"
			).setVisibilityFilters(
				folderVisibilityFilters
			).build(
				"actionLinkFolder"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					_getBaseURL(
						ObjectEntryFolder.class, "/e/edit-folder/",
						themeDisplay),
					"{embedded.id}?redirect=", themeDisplay.getURLCurrent())
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).setVisibilityFilters(
				folderVisibilityFilters
			).build(
				"editFolder"
			),
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&redirect=",
					themeDisplay.getURLCurrent())
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).build(
				"edit"
			),
			FDSActionDropdownItemBuilder.setHref(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						httpServletRequest,
						"com_liferay_portlet_configuration_web_portlet_" +
							"PortletConfigurationPortlet",
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_permissions.jsp"
				).setRedirect(
					themeDisplay.getURLCurrent()
				).setParameter(
					"modelResource", "{entryClassName}"
				).setParameter(
					"modelResourceDescription", "{embedded.name}"
				).setParameter(
					"resourceGroupId", "{embedded.scopeId}"
				).setParameter(
					"resourcePrimKey", "{embedded.id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString()
			).setIcon(
				"password-policies"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "permissions")
			).setMethod(
				"get"
			).setPermissionKey(
				"permissions"
			).setTarget(
				"modal-permissions"
			).build(
				"permissions"
			),
			FDSActionDropdownItemBuilder.setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(httpServletRequest, "delete")
			).setPermissionKey(
				"delete"
			).build(
				"delete"
			));
	}

	public static DropdownItem getProductTypeDropdownItem(
		ObjectDefinition objectDefinition,
		String objectEntryFolderExternalReferenceCode,
		ThemeDisplay themeDisplay) {

		return DropdownItemBuilder.putData(
			"action", "createAsset"
		).putData(
			"objectDefinitionId",
			String.valueOf(objectDefinition.getObjectDefinitionId())
		).putData(
			"redirect",
			StringBundler.concat(
				themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
				GroupConstants.CMS_FRIENDLY_URL,
				"/add_structured_content_item?objectDefinitionId=",
				objectDefinition.getObjectDefinitionId(),
				"&objectEntryFolderExternalReferenceCode=",
				objectEntryFolderExternalReferenceCode, "&plid=",
				themeDisplay.getPlid(), "&redirect=",
				themeDisplay.getURLCurrent())
		).putData(
			"title", objectDefinition.getLabel(themeDisplay.getLocale())
		).setIcon(
			"cards2"
		).setLabel(
			objectDefinition.getLabel(themeDisplay.getLocale())
		).build();
	}

	private static String _getBaseURL(
		Class<?> clazz, String path, ThemeDisplay themeDisplay) {

		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(),
			GroupConstants.CMS_FRIENDLY_URL, path,
			PortalUtil.getClassNameId(clazz), StringPool.SLASH);
	}

	private static ThemeDisplay _getThemeDisplay(
		HttpServletRequest httpServletRequest) {

		return (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

}