/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryPin;
import com.liferay.depot.service.DepotEntryPinLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenuBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;

import jakarta.portlet.ActionRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Marco Leo
 */
public class ViewAllSpacesDisplayContext {

	public ViewAllSpacesDisplayContext(
		DepotEntryPinLocalService entryPinLocalService,
		HttpServletRequest httpServletRequest, Language language,
		Portal portal) {

		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;

		_depotEntryPinLocalService = entryPinLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"baseSpaceURL", ActionUtil.getBaseSpaceURL(_themeDisplay)
		).put(
			"defaultPermissionAdditionalProps",
			_getDefaultPermissionAdditionalProps()
		).put(
			"pinnedAssetLibraryIds",
			TransformUtil.transformToArray(
				_depotEntryPinLocalService.getUserDepotEntryPins(
					_themeDisplay.getUserId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS),
				DepotEntryPin::getDepotEntryId, Long.class)
		).put(
			"spacePermissionAdditionalProps",
			_getSpacePermissionAdditionalProps()
		).build();
	}

	public String getAPIURL() {
		return "/o/headless-asset-library/v1.0/asset-libraries?filter=type " +
			"eq 'Space'&nestedFields=numberOfConnectedSites" +
				",numberOfUserAccounts,numberOfUserGroups";
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		_addBreadcrumbItem(jsonArray, false, null, _getLayoutName());

		return HashMapBuilder.<String, Object>put(
			"breadcrumbItems", jsonArray
		).put(
			"hideSpace", true
		).build();
	}

	public List<DropdownItem> getBulkActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"#", "document", "sampleBulkAction",
				LanguageUtil.get(_httpServletRequest, "label"), null, null,
				null),
			new FDSActionDropdownItem(
				StringPool.BLANK, "password-policies", "permissions",
				LanguageUtil.get(_httpServletRequest, "permissions"), null,
				null, null),
			new FDSActionDropdownItem(
				StringPool.BLANK, "password-policies", "default-permissions",
				LanguageUtil.get(_httpServletRequest, "default-permissions"),
				null, null, null));
	}

	public CreationMenu getCreationMenu() {
		return CreationMenuBuilder.addPrimaryDropdownItem(
			dropdownItem -> {
				dropdownItem.setHref(
					StringBundler.concat(
						_themeDisplay.getPathFriendlyURLPublic(),
						GroupConstants.CMS_FRIENDLY_URL, "/new-space"));
				dropdownItem.setIcon("forms");
				dropdownItem.setLabel(
					_language.get(_httpServletRequest, "add-space"));
			}
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest, "click-new-to-create-your-first-space")
		).put(
			"image", "/states/cms_empty_state.svg"
		).put(
			"title", LanguageUtil.get(_httpServletRequest, "no-spaces-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems() {
		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				"#", "pin", "pin",
				LanguageUtil.get(_httpServletRequest, "pin-to-product-menu"),
				"pin", "pin", "headless"),
			new FDSActionDropdownItem(
				"#", "unpin", "unpin",
				LanguageUtil.get(
					_httpServletRequest, "unpin-from-product-menu"),
				"unpin", "unpin", "headless"),
			new FDSActionDropdownItem(
				StringBundler.concat(
					ActionUtil.getBaseSpaceSettingsURL(_themeDisplay),
					"{id}?redirect=", _themeDisplay.getURLCurrent()),
				"cog", "edit",
				LanguageUtil.get(_httpServletRequest, "space-settings"), "get",
				"update", null),
			new FDSActionDropdownItem(
				null, "users", "view-members",
				LanguageUtil.get(_httpServletRequest, "view-members"), "get",
				"assign-members", null),
			new FDSActionDropdownItem(
				null, "users", "view-members",
				LanguageUtil.get(_httpServletRequest, "view-members"), "get",
				"view-members", null),
			new FDSActionDropdownItem(
				null, "globe", "view-connected-sites",
				LanguageUtil.get(_httpServletRequest, "view-connected-sites"),
				"get", "connect-sites", null),
			new FDSActionDropdownItem(
				null, "globe", "view-connected-sites",
				LanguageUtil.get(_httpServletRequest, "view-connected-sites"),
				"get", "view-connected-sites", null),
			new FDSActionDropdownItem(
				PortletURLBuilder.create(
					PortalUtil.getControlPanelPortletURL(
						_httpServletRequest,
						"com_liferay_portlet_configuration_web_portlet_" +
							"PortletConfigurationPortlet",
						ActionRequest.RENDER_PHASE)
				).setMVCPath(
					"/edit_permissions.jsp"
				).setRedirect(
					_themeDisplay.getURLCurrent()
				).setParameter(
					"modelResource", DepotEntry.class.getName()
				).setParameter(
					"modelResourceDescription", "{name}"
				).setParameter(
					"resourceGroupId", "{siteId}"
				).setParameter(
					"resourcePrimKey", "{id}"
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString(),
				"password-policies", "permissions",
				_language.get(_httpServletRequest, "permissions"), "get",
				"permissions", "modal-permissions"),
			new FDSActionDropdownItem(
				StringPool.BLANK, "password-policies", "default-permissions",
				LanguageUtil.get(_httpServletRequest, "default-permissions"),
				null, "permissions", null),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				_language.get(_httpServletRequest, "delete"), "delete",
				"delete", null));
	}

	private void _addBreadcrumbItem(
		JSONArray jsonArray, boolean active, String friendlyURL, String label) {

		jsonArray.put(
			JSONUtil.put(
				"active", active
			).put(
				"href", friendlyURL
			).put(
				"label", label
			));
	}

	private Map<String, Object> _getDefaultPermissionAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"actions",
			() -> HashMapBuilder.put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_CONTENTS,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_BASIC_WEB_CONTENT",
								_themeDisplay.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								_httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				ObjectEntryFolderConstants.EXTERNAL_REFERENCE_CODE_FILES,
				() -> {
					ObjectDefinition objectDefinition =
						ObjectDefinitionLocalServiceUtil.
							getObjectDefinitionByExternalReferenceCode(
								"L_BASIC_DOCUMENT",
								_themeDisplay.getCompanyId());

					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, objectDefinition.getClassName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							objectDefinition.getClassName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								_httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).put(
				"OBJECT_ENTRY_FOLDERS",
				() -> {
					List<String> guestUnsupportedActions =
						ResourceActionsUtil.getResourceGuestUnsupportedActions(
							null, ObjectEntryFolder.class.getName());

					return TransformUtil.transformToArray(
						ResourceActionsUtil.getResourceActions(
							ObjectEntryFolder.class.getName()),
						resourceAction -> HashMapBuilder.<String, Object>put(
							"guestUnsupported",
							guestUnsupportedActions.contains(resourceAction)
						).put(
							"key", resourceAction
						).put(
							"label",
							ResourceActionsUtil.getAction(
								_httpServletRequest, resourceAction)
						).build(),
						Map.class);
				}
			).build()
		).put(
			"roles",
			() -> TransformUtil.transformToArray(
				RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
					_themeDisplay.getCompanyId(), null,
					Arrays.asList(
						RoleConstants.ADMINISTRATOR,
						DepotRolesConstants.ASSET_LIBRARY_OWNER),
					null, null,
					new int[] {
						RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_DEPOT
					},
					0, 0, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
				role -> HashMapBuilder.put(
					"key", role.getName()
				).put(
					"name", role.getTitle(_themeDisplay.getLocale())
				).put(
					"type", String.valueOf(role.getType())
				).build(),
				Map.class)
		).build();
	}

	private String _getLayoutName() {
		Layout layout = _themeDisplay.getLayout();

		if (layout == null) {
			return null;
		}

		return layout.getName(_themeDisplay.getLocale(), true);
	}

	private Map<String, Object> _getSpacePermissionAdditionalProps() {
		return HashMapBuilder.<String, Object>put(
			"actions",
			() -> {
				List<String> guestUnsupportedActions =
					ResourceActionsUtil.getResourceGuestUnsupportedActions(
						null, DepotEntry.class.getName());

				return TransformUtil.transformToArray(
					ResourceActionsUtil.getResourceActions(
						DepotEntry.class.getName()),
					resourceAction -> HashMapBuilder.<String, Object>put(
						"guestUnsupported",
						guestUnsupportedActions.contains(resourceAction)
					).put(
						"key", resourceAction
					).put(
						"label",
						ResourceActionsUtil.getAction(
							_httpServletRequest, resourceAction)
					).build(),
					Map.class);
			}
		).put(
			"roles",
			() -> {
				Map<Long, Set<String>> availableResourcePermissionActionIds =
					ResourcePermissionLocalServiceUtil.
						getAvailableResourcePermissionActionIds(
							_themeDisplay.getCompanyId(),
							DepotEntry.class.getName(),
							ResourceConstants.SCOPE_COMPANY,
							String.valueOf(_themeDisplay.getCompanyId()),
							ResourceActionsUtil.getResourceActions(
								DepotEntry.class.getName()));

				return TransformUtil.transformToArray(
					RoleLocalServiceUtil.getGroupRolesAndTeamRoles(
						_themeDisplay.getCompanyId(), null,
						Arrays.asList(
							RoleConstants.ADMINISTRATOR,
							DepotRolesConstants.ASSET_LIBRARY_OWNER),
						null, null,
						new int[] {
							RoleConstants.TYPE_REGULAR, RoleConstants.TYPE_DEPOT
						},
						0, 0, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
					role -> HashMapBuilder.<String, Object>put(
						"actions",
						() -> ArrayUtil.toStringArray(
							availableResourcePermissionActionIds.getOrDefault(
								role.getRoleId(), new HashSet<>()))
					).put(
						"externalReferenceCode", role.getExternalReferenceCode()
					).put(
						"key", role.getName()
					).put(
						"name", role.getTitle(_themeDisplay.getLocale())
					).put(
						"type", String.valueOf(role.getType())
					).build(),
					Map.class);
			}
		).build();
	}

	private final DepotEntryPinLocalService _depotEntryPinLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;
	private final ThemeDisplay _themeDisplay;

}