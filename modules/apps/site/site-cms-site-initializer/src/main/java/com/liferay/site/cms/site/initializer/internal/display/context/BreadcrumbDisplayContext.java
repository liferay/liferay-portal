/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryPin;
import com.liferay.depot.service.DepotEntryPinLocalService;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.headless.asset.library.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.resource.v1_0.AssetLibraryResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSpaceConstants;
import com.liferay.site.cms.site.initializer.internal.util.ActionUtil;
import com.liferay.site.cms.site.initializer.internal.util.PermissionUtil;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Map;

/**
 * @author Marco Galluzzi
 * @author Roberto Díaz
 */
public class BreadcrumbDisplayContext {

	public BreadcrumbDisplayContext(
		AssetLibraryResource.Factory assetLibraryResourceFactory,
		DepotEntryPinLocalService depotEntryPinLocalService, long groupId,
		GroupLocalService groupLocalService,
		ModelResourcePermission<Group> groupModelResourcePermission,
		HttpServletRequest httpServletRequest, String size) {

		_assetLibraryResourceFactory = assetLibraryResourceFactory;
		_depotEntryPinLocalService = depotEntryPinLocalService;
		_groupId = groupId;
		_groupLocalService = groupLocalService;
		_groupModelResourcePermission = groupModelResourcePermission;
		_httpServletRequest = httpServletRequest;
		_size = GetterUtil.get(size, CMSSpaceConstants.SPACE_STICKER_LG);

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return "/o/headless-asset-library/v1.0/asset-libraries?filter=type " +
			"eq 'Space'&nestedFields=numberOfConnectedSites" +
				",numberOfUserAccounts,numberOfUserGroups";
	}

	public Map<String, Object> getProps() throws Exception {
		Group group = _getGroup();

		return HashMapBuilder.<String, Object>put(
			"actionItems",
			_putAll(
				unsafeConsumer -> {
					PermissionChecker permissionChecker =
						_themeDisplay.getPermissionChecker();

					if (permissionChecker.hasPermission(
							group, DepotEntry.class.getName(),
							group.getClassPK(), ActionKeys.UPDATE)) {

						DepotEntryPin depotEntryPin =
							_depotEntryPinLocalService.fetchGroupDepotEntryPin(
								_groupId, _themeDisplay.getUserId());

						if (depotEntryPin == null) {
							unsafeConsumer.accept(
								JSONUtil.put(
									"href",
									StringBundler.concat(
										"/o/headless-asset-library/v1.0",
										"/asset-libraries/",
										group.getExternalReferenceCode(),
										"/pins")
								).put(
									"label",
									LanguageUtil.get(
										_httpServletRequest,
										"pin-to-product-menu")
								).put(
									"redirect", _themeDisplay.getURLCurrent()
								).put(
									"successMessage",
									LanguageUtil.get(
										_httpServletRequest,
										"the-space-has-been-successfully-" +
											"pinned-to-the-product-menu")
								).put(
									"symbolLeft", "pin"
								).put(
									"target", "asyncPut"
								));
						}
						else {
							unsafeConsumer.accept(
								JSONUtil.put(
									"href",
									StringBundler.concat(
										"/o/headless-asset-library/v1.0",
										"/asset-libraries/",
										group.getExternalReferenceCode(),
										"/pins")
								).put(
									"label",
									LanguageUtil.get(
										_httpServletRequest,
										"unpin-from-product-menu")
								).put(
									"redirect", _themeDisplay.getURLCurrent()
								).put(
									"successMessage",
									LanguageUtil.get(
										_httpServletRequest,
										"the-space-has-been-successfully-" +
											"unpinned-from-the-product-menu")
								).put(
									"symbolLeft", "unpin"
								).put(
									"target", "asyncDelete"
								));
						}

						unsafeConsumer.accept(
							JSONUtil.put(
								"href",
								ActionUtil.getSpaceSettingsURL(
									group.getClassPK(),
									_themeDisplay.getURLCurrent(),
									_themeDisplay)
							).put(
								"label",
								LanguageUtil.get(
									_httpServletRequest, "space-settings")
							).put(
								"symbolLeft", "cog"
							));
						unsafeConsumer.accept(
							JSONUtil.put(
								"href",
								_getControlPanelPortletURL(
									ExportImportPortletKeys.EXPORT)
							).put(
								"label",
								LanguageUtil.get(_httpServletRequest, "export")
							).put(
								"symbolLeft", "export"
							));
						unsafeConsumer.accept(
							JSONUtil.put(
								"href",
								_getControlPanelPortletURL(
									ExportImportPortletKeys.IMPORT)
							).put(
								"label",
								LanguageUtil.get(_httpServletRequest, "import")
							).put(
								"symbolLeft", "import"
							));
					}

					if (permissionChecker.hasPermission(
							group, DepotEntry.class.getName(),
							group.getClassPK(), ActionKeys.VIEW)) {

						unsafeConsumer.accept(
							JSONUtil.put(
								"href", StringPool.BLANK
							).put(
								"label",
								LanguageUtil.get(
									_httpServletRequest, "view-members")
							).put(
								"manageMembersData",
								HashMapBuilder.<String, Object>put(
									"assetLibraryCreatorUserId",
									_themeDisplay.getUserId()
								).put(
									"externalReferenceCode",
									group.getExternalReferenceCode()
								).put(
									"hasAssignMembersPermission",
									_groupModelResourcePermission.contains(
										permissionChecker, group.getGroupId(),
										ActionKeys.ASSIGN_MEMBERS)
								).put(
									"title",
									LanguageUtil.get(
										_httpServletRequest, "all-members")
								).build()
							).put(
								"redirect", _themeDisplay.getURLCurrent()
							).put(
								"symbolLeft", "users"
							).put(
								"target", "manageMembersModal"
							));

						Map<String, Map<String, String>> actions =
							_getAssetLibraryActions(group);

						if (actions.containsKey("connect-sites") ||
							actions.containsKey("view-connected-sites")) {

							unsafeConsumer.accept(
								JSONUtil.put(
									"href", StringPool.BLANK
								).put(
									"label",
									LanguageUtil.get(
										_httpServletRequest,
										"view-connected-sites")
								).put(
									"manageConnectedSitesData",
									HashMapBuilder.<String, Object>put(
										"externalReferenceCode",
										group.getExternalReferenceCode()
									).put(
										"hasConnectSitesPermission",
										actions.containsKey("connect-sites")
									).build()
								).put(
									"redirect", _themeDisplay.getURLCurrent()
								).put(
									"symbolLeft", "globe"
								).put(
									"target", "manageConnectedSitesModal"
								));
						}
					}

					if (permissionChecker.hasPermission(
							group, DepotEntry.class.getName(),
							group.getClassPK(), ActionKeys.PERMISSIONS)) {

						unsafeConsumer.accept(
							JSONUtil.put(
								"href",
								PermissionsURLTag.doTag(
									StringPool.BLANK,
									DepotEntry.class.getName(), group.getName(),
									group.getGroupId(),
									String.valueOf(group.getClassPK()),
									LiferayWindowState.POP_UP.toString(), null,
									_httpServletRequest)
							).put(
								"label",
								LanguageUtil.get(
									_httpServletRequest, "permissions")
							).put(
								"symbolLeft", "password-policies"
							).put(
								"target", "modal"
							));
						unsafeConsumer.accept(
							JSONUtil.put(
								"defaultPermissionAdditionalProps",
								HashMapBuilder.putAll(
									PermissionUtil.
										getDefaultPermissionAdditionalProps(
											_httpServletRequest, _themeDisplay)
								).put(
									"classExternalReferenceCode",
									group.getExternalReferenceCode()
								).put(
									"className", DepotEntry.class.getName()
								).build()
							).put(
								"href", StringPool.BLANK
							).put(
								"label",
								LanguageUtil.get(
									_httpServletRequest, "default-permissions")
							).put(
								"symbolLeft", "password-policies"
							).put(
								"target", "defaultPermissionsModal"
							));
						unsafeConsumer.accept(
							JSONUtil.put(
								"defaultPermissionAdditionalProps",
								HashMapBuilder.putAll(
									PermissionUtil.
										getDefaultPermissionAdditionalProps(
											true, _httpServletRequest,
											_themeDisplay)
								).put(
									"classExternalReferenceCode",
									group.getExternalReferenceCode()
								).put(
									"className", DepotEntry.class.getName()
								).build()
							).put(
								"href", StringPool.BLANK
							).put(
								"label",
								LanguageUtil.get(
									_httpServletRequest,
									"edit-and-propagate-default-permissions")
							).put(
								"symbolLeft", "password-policies"
							).put(
								"target", "defaultPermissionsModal"
							));
					}

					if (permissionChecker.hasPermission(
							group, DepotEntry.class.getName(),
							group.getClassPK(), ActionKeys.DELETE)) {

						unsafeConsumer.accept(
							JSONUtil.put(
								"confirmationMessage",
								LanguageUtil.get(
									_httpServletRequest,
									"delete-space-confirmation-body")
							).put(
								"confirmationTitle",
								LanguageUtil.format(
									_httpServletRequest,
									"delete-space-confirmation-title",
									group.getDescriptiveName())
							).put(
								"href",
								StringBundler.concat(
									"/o/headless-asset-library/v1.0",
									"/asset-libraries/",
									group.getExternalReferenceCode())
							).put(
								"label",
								LanguageUtil.get(_httpServletRequest, "delete")
							).put(
								"redirect",
								StringBundler.concat(
									_themeDisplay.getPathFriendlyURLPublic(),
									GroupConstants.CMS_FRIENDLY_URL,
									"/all-spaces")
							).put(
								"successMessage",
								LanguageUtil.format(
									_httpServletRequest,
									"x-was-successfully-deleted",
									group.getDescriptiveName())
							).put(
								"symbolLeft", "trash"
							).put(
								"target", "asyncDelete"
							));
					}
				})
		).put(
			"breadcrumbItems",
			JSONUtil.put(
				JSONUtil.put(
					"active", false
				).put(
					"href", StringPool.BLANK
				).put(
					"label", group.getDescriptiveName(_themeDisplay.getLocale())
				))
		).put(
			"displayType",
			() -> {
				UnicodeProperties unicodeProperties =
					group.getTypeSettingsProperties();

				return GetterUtil.get(
					unicodeProperties.get("logoColor"), "outline-0");
			}
		).put(
			"size", _size
		).build();
	}

	private Map<String, Map<String, String>> _getAssetLibraryActions(
			Group group)
		throws Exception {

		AssetLibraryResource assetLibraryResource =
			_assetLibraryResourceFactory.create(
			).httpServletRequest(
				_httpServletRequest
			).preferredLocale(
				_themeDisplay.getLocale()
			).user(
				_themeDisplay.getUser()
			).build();

		AssetLibrary assetLibrary = assetLibraryResource.getAssetLibrary(
			group.getExternalReferenceCode());

		Map<String, Map<String, String>> actions = assetLibrary.getActions();

		if (actions == null) {
			return Collections.emptyMap();
		}

		return actions;
	}

	private String _getControlPanelPortletURL(String portletId)
		throws Exception {

		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			_httpServletRequest, _getGroup(), portletId, 0, 0,
			PortletRequest.RENDER_PHASE);

		return HttpComponentsUtil.addParameter(
			portletURL.toString(),
			PortalUtil.getPortletNamespace(portletId) + "backURL",
			_themeDisplay.getURLCurrent());
	}

	private Group _getGroup() throws Exception {
		return _groupLocalService.getGroup(_groupId);
	}

	private JSONArray _putAll(
			UnsafeConsumer<UnsafeConsumer<JSONObject, Exception>, Exception>
				unsafeConsumer)
		throws Exception {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		unsafeConsumer.accept(jsonArray::put);

		if (jsonArray.length() == 0) {
			return null;
		}

		return jsonArray;
	}

	private final AssetLibraryResource.Factory _assetLibraryResourceFactory;
	private final DepotEntryPinLocalService _depotEntryPinLocalService;
	private final long _groupId;
	private final GroupLocalService _groupLocalService;
	private final ModelResourcePermission<Group> _groupModelResourcePermission;
	private final HttpServletRequest _httpServletRequest;
	private final String _size;
	private final ThemeDisplay _themeDisplay;

}