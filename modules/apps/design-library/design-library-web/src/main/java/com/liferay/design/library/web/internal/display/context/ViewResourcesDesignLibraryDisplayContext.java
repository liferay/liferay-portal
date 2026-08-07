/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.design.library.resource.type.DesignLibraryResourceType;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeRegistry;
import com.liferay.design.library.web.internal.constants.DesignLibraryAdminFDSNames;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Prates
 * @author Thiago Buarque
 */
public class ViewResourcesDesignLibraryDisplayContext
	extends BaseDesignLibraryDisplayContext {

	public ViewResourcesDesignLibraryDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		super(httpServletRequest);

		_liferayPortletResponse = liferayPortletResponse;
	}

	public String getAPIURL() throws PortalException {
		List<String> entryClassNames = new ArrayList<>();

		for (DesignLibraryResourceType designLibraryResourceType :
				_getViewableDesignLibraryResourceTypes()) {

			entryClassNames.add(designLibraryResourceType.getEntryClassName());
		}

		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&entryClassNames=",
			StringUtil.merge(entryClassNames, StringPool.COMMA),
			"&filter=groupIds/any(g:g eq ", depotEntry.getGroupId(), ")",
			"&nestedFields=embedded");
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		Group group = getGroup();

		return HashMapBuilder.<String, Object>put(
			"actionItems", _getActionItemsJSONArray(group)
		).put(
			"breadcrumbItems", _getBreadcrumbItemsJSONArray(group)
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return buildEmptyState(
			"click-new-to-create-or-import-your-design-resource",
			"/states/resources_empty_state.svg", "no-design-resources-yet");
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		List<FDSActionDropdownItem> fdsActionDropdownItems = new ArrayList<>();

		Group depotGroup = getGroup();
		String viewResourcesURL = getViewResourcesURL(_liferayPortletResponse);

		for (DesignLibraryResourceType designLibraryResourceType :
				_getViewableDesignLibraryResourceTypes()) {

			for (FDSActionDropdownItem fdsActionDropdownItem :
					designLibraryResourceType.getFDSActionDropdownItems(
						httpServletRequest, depotGroup, viewResourcesURL)) {

				fdsActionDropdownItem.setVisibilityFilters(
					HashMapBuilder.<String, Object>put(
						"entryClassName",
						designLibraryResourceType.getEntryClassName()
					).build());

				fdsActionDropdownItems.add(fdsActionDropdownItem);
			}
		}

		return fdsActionDropdownItems;
	}

	public Map<String, Object> getFDSAdditionalProps() throws PortalException {
		Group depotGroup = getGroup();
		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();
		String viewResourcesURL = getViewResourcesURL(_liferayPortletResponse);

		List<Map<String, Object>> resourceTypes = new ArrayList<>();

		for (DesignLibraryResourceType designLibraryResourceType :
				_getViewableDesignLibraryResourceTypes()) {

			Map<String, Object> resourceType =
				HashMapBuilder.<String, Object>put(
					"color", designLibraryResourceType.getColor()
				).put(
					"defaultActionId",
					designLibraryResourceType.getDefaultActionId()
				).put(
					"entryClassName",
					designLibraryResourceType.getEntryClassName()
				).put(
					"label",
					designLibraryResourceType.getLabel(themeDisplay.getLocale())
				).put(
					"symbol", designLibraryResourceType.getSymbol()
				).build();

			String creationItemsModule =
				designLibraryResourceType.getCreationItemsModule();

			if ((creationItemsModule != null) &&
				designLibraryResourceType.hasAddPermission(
					permissionChecker, depotGroup)) {

				resourceType.put("creationItemsModule", creationItemsModule);
				resourceType.put(
					"creationItemsProps",
					designLibraryResourceType.getCreationItemsProps(
						httpServletRequest, depotGroup, viewResourcesURL));
			}

			resourceTypes.add(resourceType);
		}

		return HashMapBuilder.<String, Object>put(
			"resourceTypes", resourceTypes
		).build();
	}

	public boolean hasContentAccess() throws PortalException {
		return ListUtil.isNotEmpty(_getViewableDesignLibraryResourceTypes());
	}

	private JSONArray _getActionItemsJSONArray(Group group)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		boolean hasUpdatePermission = hasDepotEntryPermission(
			group, ActionKeys.UPDATE);

		if (hasUpdatePermission) {
			jsonArray.put(
				JSONUtil.put(
					"href",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/design_library/edit_design_library"
					).setParameter(
						"designLibraryEntryId", depotEntry.getDepotEntryId()
					).buildString()
				).put(
					"label", LanguageUtil.get(httpServletRequest, "settings")
				).put(
					"symbolLeft", "cog"
				));
		}

		boolean hasAssignMembersPermission = hasAssignMembersPermission(group);

		jsonArray.put(
			JSONUtil.put(
				"externalReferenceCode", group.getExternalReferenceCode()
			).put(
				"href", "#connected-sites"
			).put(
				"label", LanguageUtil.get(httpServletRequest, "connected-sites")
			).put(
				"refreshDataSetIds",
				JSONUtil.putAll(
					DesignLibraryAdminFDSNames.DESIGN_LIBRARY_CONNECTED_SITES)
			).put(
				"symbolLeft", "globe"
			).put(
				"target", "connected-sites"
			)
		).put(
			JSONUtil.put(
				"externalReferenceCode", group.getExternalReferenceCode()
			).put(
				"hasAssignMembersPermission", hasAssignMembersPermission
			).put(
				"href", "#manage-members"
			).put(
				"label",
				LanguageUtil.get(
					httpServletRequest,
					hasAssignMembersPermission ? "manage-members" :
						"view-members")
			).put(
				"ownerId", String.valueOf(group.getCreatorUserId())
			).put(
				"refreshDataSetIds",
				JSONUtil.putAll(
					DesignLibraryAdminFDSNames.
						DESIGN_LIBRARY_MEMBERS_USER_GROUPS,
					DesignLibraryAdminFDSNames.DESIGN_LIBRARY_MEMBERS_USERS)
			).put(
				"symbolLeft", "users"
			).put(
				"target", "manage-members"
			)
		);

		if (hasUpdatePermission) {
			jsonArray.put(
				JSONUtil.put(
					"href",
					_getExportImportPortletURL(
						group, ExportImportPortletKeys.EXPORT)
				).put(
					"label", LanguageUtil.get(httpServletRequest, "export")
				).put(
					"symbolLeft", "export"
				)
			).put(
				JSONUtil.put(
					"href",
					_getExportImportPortletURL(
						group, ExportImportPortletKeys.IMPORT)
				).put(
					"label", LanguageUtil.get(httpServletRequest, "import")
				).put(
					"symbolLeft", "import"
				)
			);
		}

		if (hasDepotEntryPermission(group, ActionKeys.DELETE)) {
			jsonArray.put(
				JSONUtil.put(
					"descriptiveName", group.getDescriptiveName()
				).put(
					"href", getAssetLibraryURL(group, StringPool.BLANK)
				).put(
					"label", LanguageUtil.get(httpServletRequest, "delete")
				).put(
					"redirect",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).buildString()
				).put(
					"symbolLeft", "trash"
				).put(
					"target", "delete"
				));
		}

		return jsonArray;
	}

	private JSONArray _getBreadcrumbItemsJSONArray(Group group) {
		return JSONUtil.putAll(
			JSONUtil.put(
				"active", false
			).put(
				"href",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).buildString()
			).put(
				"label",
				LanguageUtil.get(httpServletRequest, "design-libraries")
			),
			JSONUtil.put(
				"active", true
			).put(
				"href", "#top"
			).put(
				"label", group.getName(httpServletRequest.getLocale())
			));
	}

	private String _getExportImportPortletURL(Group group, String portletId) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, group, portletId, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setBackURL(
			PortalUtil.getCurrentURL(httpServletRequest)
		).buildString();
	}

	private List<DesignLibraryResourceType>
			_getViewableDesignLibraryResourceTypes()
		throws PortalException {

		DesignLibraryResourceTypeRegistry designLibraryResourceTypeRegistry =
			_designLibraryResourceTypeRegistrySnapshot.get();

		if (designLibraryResourceTypeRegistry == null) {
			return Collections.emptyList();
		}

		Group depotGroup = getGroup();
		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		List<DesignLibraryResourceType> designLibraryResourceTypes =
			new ArrayList<>();

		for (DesignLibraryResourceType designLibraryResourceType :
				designLibraryResourceTypeRegistry.
					getDesignLibraryResourceTypes()) {

			if (designLibraryResourceType.hasViewPermission(
					permissionChecker, depotGroup)) {

				designLibraryResourceTypes.add(designLibraryResourceType);
			}
		}

		return designLibraryResourceTypes;
	}

	private static final Snapshot<DesignLibraryResourceTypeRegistry>
		_designLibraryResourceTypeRegistrySnapshot = new Snapshot<>(
			ViewResourcesDesignLibraryDisplayContext.class,
			DesignLibraryResourceTypeRegistry.class);

	private final LiferayPortletResponse _liferayPortletResponse;

}