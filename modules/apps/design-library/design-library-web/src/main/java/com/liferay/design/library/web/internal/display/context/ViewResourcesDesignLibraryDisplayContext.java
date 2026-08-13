/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.design.library.resource.type.DesignLibraryResourceCreationItem;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributor;
import com.liferay.design.library.resource.type.DesignLibraryResourceTypeContributorRegistry;
import com.liferay.design.library.web.internal.constants.DesignLibraryAdminFDSNames;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
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
import com.liferay.portal.kernel.servlet.taglib.aui.ESImport;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	public String getAPIURL() {
		Set<String> entryClassNames = new LinkedHashSet<>();
		List<String> typeExpressions = new ArrayList<>();

		for (DesignLibraryResourceTypeContributor
				designLibraryResourceTypeContributor :
					_getViewableDesignLibraryResourceTypeContributors()) {

			String entryClassName =
				designLibraryResourceTypeContributor.getEntryClassName();

			entryClassNames.add(entryClassName);

			long classNameId = PortalUtil.getClassNameId(entryClassName);

			String type = designLibraryResourceTypeContributor.getType();

			if (type == null) {
				typeExpressions.add("classNameId eq " + classNameId);
			}
			else {
				typeExpressions.add(
					StringBundler.concat(
						"(classNameId eq ", classNameId, " and type eq '", type,
						"')"));
			}
		}

		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true&entryClassNames=",
			StringUtil.merge(entryClassNames, StringPool.COMMA),
			"&filter=groupIds/any(g:g eq ", depotEntry.getGroupId(), ") and (",
			StringUtil.merge(typeExpressions, " or "), ")",
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

		String viewResourcesURL = getViewResourcesURL(_liferayPortletResponse);

		for (DesignLibraryResourceTypeContributor
				designLibraryResourceTypeContributor :
					_getViewableDesignLibraryResourceTypeContributors()) {

			for (FDSActionDropdownItem fdsActionDropdownItem :
					designLibraryResourceTypeContributor.
						getFDSActionDropdownItems(
							httpServletRequest, depotEntry, viewResourcesURL)) {

				fdsActionDropdownItem.setVisibilityFilters(
					_getVisibilityFilters(
						designLibraryResourceTypeContributor));

				fdsActionDropdownItems.add(fdsActionDropdownItem);
			}
		}

		return fdsActionDropdownItems;
	}

	public Map<String, Object> getFDSAdditionalProps() throws PortalException {
		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();
		String viewResourcesURL = getViewResourcesURL(_liferayPortletResponse);

		List<Map<String, Object>> resourceTypes = new ArrayList<>();

		for (DesignLibraryResourceTypeContributor
				designLibraryResourceTypeContributor :
					_getViewableDesignLibraryResourceTypeContributors()) {

			Map<String, Object> resourceType =
				HashMapBuilder.<String, Object>put(
					"color", designLibraryResourceTypeContributor.getColor()
				).put(
					"defaultActionId",
					designLibraryResourceTypeContributor.getDefaultActionId()
				).put(
					"entryClassName",
					designLibraryResourceTypeContributor.getEntryClassName()
				).put(
					"key", designLibraryResourceTypeContributor.getKey()
				).put(
					"label",
					designLibraryResourceTypeContributor.getLabel(
						themeDisplay.getLocale())
				).put(
					"symbol", designLibraryResourceTypeContributor.getIcon()
				).put(
					"type", designLibraryResourceTypeContributor.getType()
				).build();

			if (designLibraryResourceTypeContributor.hasAddPermission(
					permissionChecker, depotEntry)) {

				List<DesignLibraryResourceCreationItem>
					designLibraryResourceCreationItems =
						designLibraryResourceTypeContributor.getCreationItems(
							httpServletRequest, depotEntry, viewResourcesURL);

				if (!designLibraryResourceCreationItems.isEmpty()) {
					resourceType.put(
						"creationItems",
						_toCreationItemMaps(
							designLibraryResourceCreationItems));
				}
			}

			resourceTypes.add(resourceType);
		}

		return HashMapBuilder.<String, Object>put(
			"resourceTypes", resourceTypes
		).build();
	}

	public boolean hasContentAccess() {
		return ListUtil.isNotEmpty(
			_getViewableDesignLibraryResourceTypeContributors());
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

	private List<DesignLibraryResourceTypeContributor>
		_getViewableDesignLibraryResourceTypeContributors() {

		DesignLibraryResourceTypeContributorRegistry
			designLibraryResourceTypeContributorRegistry =
				_designLibraryResourceTypeContributorRegistrySnapshot.get();

		if (designLibraryResourceTypeContributorRegistry == null) {
			return Collections.emptyList();
		}

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		List<DesignLibraryResourceTypeContributor>
			designLibraryResourceTypeContributors = new ArrayList<>();

		for (DesignLibraryResourceTypeContributor
				designLibraryResourceTypeContributor :
					designLibraryResourceTypeContributorRegistry.
						getDesignLibraryResourceTypeContributors()) {

			if (designLibraryResourceTypeContributor.hasViewPermission(
					permissionChecker, depotEntry)) {

				designLibraryResourceTypeContributors.add(
					designLibraryResourceTypeContributor);
			}
		}

		return designLibraryResourceTypeContributors;
	}

	private Map<String, Object> _getVisibilityFilters(
		DesignLibraryResourceTypeContributor
			designLibraryResourceTypeContributor) {

		String type = designLibraryResourceTypeContributor.getType();

		return HashMapBuilder.<String, Object>put(
			"entryClassName",
			designLibraryResourceTypeContributor.getEntryClassName()
		).put(
			() -> (type == null) ? null : "type", type
		).build();
	}

	private String _resolveESImport(String module) {
		if (module == null) {
			return null;
		}

		AbsolutePortalURLBuilderFactory absolutePortalURLBuilderFactory =
			_absolutePortalURLBuilderFactorySnapshot.get();

		if (absolutePortalURLBuilderFactory == null) {
			return null;
		}

		ESImport esImport = ESImportUtil.getESImport(
			absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest),
			module);

		return StringBundler.concat(
			"{", esImport.getSymbol(), "} from ", esImport.getModule());
	}

	private List<Map<String, Object>> _toCreationItemMaps(
		List<DesignLibraryResourceCreationItem>
			designLibraryResourceCreationItems) {

		List<Map<String, Object>> maps = new ArrayList<>();

		for (DesignLibraryResourceCreationItem
				designLibraryResourceCreationItem :
					designLibraryResourceCreationItems) {

			maps.add(
				HashMapBuilder.<String, Object>put(
					"id", designLibraryResourceCreationItem.getId()
				).put(
					"label", designLibraryResourceCreationItem.getLabel()
				).put(
					"module",
					_resolveESImport(
						designLibraryResourceCreationItem.getModule())
				).put(
					"moduleProps",
					designLibraryResourceCreationItem.getModuleProps()
				).build());
		}

		return maps;
	}

	private static final Snapshot<AbsolutePortalURLBuilderFactory>
		_absolutePortalURLBuilderFactorySnapshot = new Snapshot<>(
			ViewResourcesDesignLibraryDisplayContext.class,
			AbsolutePortalURLBuilderFactory.class);
	private static final Snapshot<DesignLibraryResourceTypeContributorRegistry>
		_designLibraryResourceTypeContributorRegistrySnapshot = new Snapshot<>(
			ViewResourcesDesignLibraryDisplayContext.class,
			DesignLibraryResourceTypeContributorRegistry.class);

	private final LiferayPortletResponse _liferayPortletResponse;

}