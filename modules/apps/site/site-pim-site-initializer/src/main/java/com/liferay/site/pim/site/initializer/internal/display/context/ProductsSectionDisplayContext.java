/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryServiceUtil;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSActionDropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.object.constants.ObjectDefinitionSettingConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectDefinitionSetting;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionServiceUtil;
import com.liferay.object.service.ObjectDefinitionSettingLocalServiceUtil;
import com.liferay.object.service.ObjectEntryFolderLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectEntryFolderConstants;
import com.liferay.site.pim.site.initializer.internal.constants.PIMObjectFolderConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Stefano Motta
 */
public class ProductsSectionDisplayContext {

	public ProductsSectionDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;
	}

	public String getAPIURL() {
		return "/o/search/v1.0/search?emptySearch=true&filter=" +
			URLCodec.encodeURL("cmsSection eq 'products'") +
				"&nestedFields=embedded,systemProperties.objectDefinitionBrief";
	}

	public CreationMenu getCreationMenu() {
		CreationMenu creationMenu = new CreationMenu();

		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-96666")) {

			return creationMenu;
		}

		ModelResourcePermission<ObjectEntryFolder> modelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				ObjectEntryFolder.class.getName());

		if (modelResourcePermission == null) {
			return creationMenu;
		}

		List<Long> groupIds = TransformUtil.transform(
			DepotEntryServiceUtil.getDepotEntryGroupIds(
				themeDisplay.getCompanyId(), themeDisplay.getUserId(),
				DepotConstants.TYPE_SPACE),
			depotEntryGroupId -> {
				ObjectEntryFolder objectEntryFolder =
					ObjectEntryFolderLocalServiceUtil.
						fetchObjectEntryFolderByExternalReferenceCode(
							PIMObjectEntryFolderConstants.
								EXTERNAL_REFERENCE_CODE_PRODUCTS,
							depotEntryGroupId, themeDisplay.getCompanyId());

				if (objectEntryFolder == null) {
					return null;
				}

				try {
					if (modelResourcePermission.contains(
							themeDisplay.getPermissionChecker(),
							objectEntryFolder.getObjectEntryFolderId(),
							ActionKeys.ADD_ENTRY)) {

						return depotEntryGroupId;
					}
				}
				catch (PortalException portalException) {
					if (_log.isDebugEnabled()) {
						_log.debug(portalException);
					}
				}

				return null;
			});

		if (groupIds.isEmpty()) {
			return creationMenu;
		}

		for (ObjectDefinition objectDefinition :
				ObjectDefinitionServiceUtil.getCMSObjectDefinitions(
					themeDisplay.getCompanyId(),
					new String[] {
						PIMObjectFolderConstants.
							EXTERNAL_REFERENCE_CODE_PRODUCT_TYPES
					})) {

			JSONArray jsonArray = _getJSONArray(
				_getAcceptedGroupIds(
					groupIds, objectDefinition.getObjectDefinitionId()),
				themeDisplay.getLocale());

			if (jsonArray.length() == 0) {
				continue;
			}

			DropdownItem dropdownItem = _getDropdownItem(
				objectDefinition, themeDisplay);

			dropdownItem.putData("assetLibraries", jsonArray);

			creationMenu.addPrimaryDropdownItem(dropdownItem);
		}

		return creationMenu;
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest, "click-new-to-create-your-first-product")
		).put(
			"image", "/states/cms_empty_state_content.svg"
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-products-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		ThemeDisplay themeDisplay = _getThemeDisplay();

		if (!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-96666")) {

			return Collections.emptyList();
		}

		return ListUtil.fromArray(
			FDSActionDropdownItemBuilder.setHref(
				StringBundler.concat(
					themeDisplay.getPortalURL(), themeDisplay.getPathMain(),
					GroupConstants.CMS_FRIENDLY_URL,
					"/edit_content_item?objectEntryId={embedded.id}&redirect=",
					themeDisplay.getURLCurrent())
			).setIcon(
				"pencil"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "edit")
			).setMethod(
				"get"
			).setPermissionKey(
				"update"
			).build(
				"edit"
			),
			FDSActionDropdownItemBuilder.setConfirmationMessage(
				LanguageUtil.get(
					_httpServletRequest, "are-you-sure-you-want-to-delete-this")
			).setHref(
				"{embedded.actions.delete.href}"
			).setIcon(
				"trash"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "delete")
			).setMethod(
				"delete"
			).setPermissionKey(
				"delete"
			).setTarget(
				"headless"
			).build(
				"delete"
			));
	}

	private List<Long> _getAcceptedGroupIds(
		List<Long> groupIds, long objectDefinitionId) {

		ObjectDefinitionSetting objectDefinitionSetting =
			ObjectDefinitionSettingLocalServiceUtil.
				fetchObjectDefinitionSetting(
					objectDefinitionId,
					ObjectDefinitionSettingConstants.NAME_ACCEPT_ALL_GROUPS);

		if ((objectDefinitionSetting != null) &&
			GetterUtil.getBoolean(objectDefinitionSetting.getValue())) {

			return groupIds;
		}

		objectDefinitionSetting =
			ObjectDefinitionSettingLocalServiceUtil.
				fetchObjectDefinitionSetting(
					objectDefinitionId,
					ObjectDefinitionSettingConstants.NAME_ACCEPTED_GROUP_IDS);

		if ((objectDefinitionSetting == null) ||
			Validator.isNull(objectDefinitionSetting.getValue())) {

			return groupIds;
		}

		return TransformUtil.transform(
			ListUtil.fromArray(
				StringUtil.split(objectDefinitionSetting.getValue())),
			groupId -> {
				if (groupIds.contains(GetterUtil.getLong(groupId))) {
					return GetterUtil.getLong(groupId);
				}

				return null;
			});
	}

	private DropdownItem _getDropdownItem(
		ObjectDefinition objectDefinition, ThemeDisplay themeDisplay) {

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
				PIMObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_PRODUCTS,
				"&plid=", themeDisplay.getPlid(), "&redirect=",
				themeDisplay.getURLCurrent())
		).putData(
			"title", objectDefinition.getLabel(themeDisplay.getLocale())
		).setIcon(
			"cards2"
		).setLabel(
			objectDefinition.getLabel(themeDisplay.getLocale())
		).build();
	}

	private JSONArray _getJSONArray(List<Long> groupIds, Locale locale) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (long groupId : groupIds) {
			Group group = GroupLocalServiceUtil.fetchGroup(groupId);

			if (group == null) {
				continue;
			}

			jsonArray.put(
				JSONUtil.put(
					"externalReferenceCode", group.getExternalReferenceCode()
				).put(
					"groupId", group.getGroupId()
				).put(
					"name", group.getName(locale)
				));
		}

		return jsonArray;
	}

	private ThemeDisplay _getThemeDisplay() {
		return (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductsSectionDisplayContext.class);

	private final HttpServletRequest _httpServletRequest;

}