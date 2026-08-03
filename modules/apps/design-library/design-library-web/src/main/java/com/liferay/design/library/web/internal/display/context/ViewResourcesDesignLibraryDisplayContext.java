/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.design.library.web.internal.constants.DesignLibraryAdminFDSNames;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.style.book.constants.StyleBookActionKeys;
import com.liferay.style.book.constants.StyleBookConstants;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.util.StyleBookUtil;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

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

	public String getAPIURL() {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true",
			"&entryClassNames=com.liferay.fragment.model.FragmentCollection",
			",com.liferay.style.book.model.StyleBookEntry",
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

		Group depotGroup = getGroup();

		String designLibraryResourcesURL = getViewResourcesURL(
			_liferayPortletResponse);

		String viewFragmentCollectionURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT, 0,
				0, PortletRequest.RENDER_PHASE)
		).setBackURL(
			designLibraryResourcesURL
		).setParameter(
			"fragmentCollectionExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		).buildString();
		String editFragmentCollectionURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT, 0,
				0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/fragment/edit_fragment_collection"
		).setRedirect(
			designLibraryResourcesURL
		).setParameter(
			"fragmentCollectionExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		).buildString();
		String editStyleBookEntryURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup, StyleBookPortletKeys.STYLE_BOOK,
				0, 0, PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/style_book/edit_style_book_entry"
		).setRedirect(
			designLibraryResourcesURL
		).setParameter(
			"backURLTitle", depotGroup.getName(themeDisplay.getLocale())
		).setParameter(
			"styleBookEntryId", "{embedded.id}"
		).buildString();

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				viewFragmentCollectionURL, "view", "view",
				LanguageUtil.get(httpServletRequest, "view"), null, null,
				"link",
				HashMapBuilder.<String, Object>put(
					"entryClassName", FragmentCollection.class.getName()
				).build()),
			new FDSActionDropdownItem(
				editFragmentCollectionURL, "pencil", "edit",
				LanguageUtil.get(httpServletRequest, "edit"), null, null,
				"link",
				HashMapBuilder.<String, Object>put(
					"entryClassName", FragmentCollection.class.getName()
				).build()),
			new FDSActionDropdownItem(
				editStyleBookEntryURL, "pencil", "edit",
				LanguageUtil.get(
					httpServletRequest, "edit-in-style-book-editor"),
				null, "get", "link",
				HashMapBuilder.<String, Object>put(
					"entryClassName", StyleBookEntry.class.getName()
				).build()),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async",
				HashMapBuilder.<String, Object>put(
					"entryClassName", FragmentCollection.class.getName()
				).build()),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(httpServletRequest, "delete"), "delete",
				"delete", "async",
				HashMapBuilder.<String, Object>put(
					"entryClassName", StyleBookEntry.class.getName()
				).build()));
	}

	public Map<String, Object> getFDSAdditionalProps() throws PortalException {
		Group depotGroup = getGroup();

		boolean manageFragmentEntriesPermission =
			_hasManageFragmentEntriesPermission(depotGroup.getGroupId());
		boolean manageStyleBookEntriesPermission =
			_hasManageStyleBookEntriesPermission(depotGroup.getGroupId());

		return HashMapBuilder.<String, Object>put(
			"addFragmentCollectionURL",
			() -> {
				if (!manageFragmentEntriesPermission) {
					return null;
				}

				return _getAddFragmentCollectionURL(depotGroup);
			}
		).put(
			"addFragmentEntryURL",
			() -> {
				if (!manageFragmentEntriesPermission) {
					return null;
				}

				return _getAddFragmentEntryURL(depotGroup);
			}
		).put(
			"addStyleBookEntryURL",
			() -> {
				if (!manageStyleBookEntriesPermission) {
					return null;
				}

				return _getAddStyleBookEntryURL(depotGroup);
			}
		).put(
			"canAddStyleBook", manageStyleBookEntriesPermission
		).put(
			"canManageFragments", manageFragmentEntriesPermission
		).put(
			"fragmentCollections",
			() -> {
				if (!manageFragmentEntriesPermission) {
					return null;
				}

				return _getFragmentCollectionsJSONArray(
					depotGroup.getGroupId());
			}
		).put(
			"fragmentNamespace",
			() -> {
				if (!manageFragmentEntriesPermission) {
					return null;
				}

				return PortalUtil.getPortletNamespace(
					FragmentPortletKeys.FRAGMENT);
			}
		).put(
			"frontendTokenDefinitionProviders",
			() -> {
				if (!manageStyleBookEntriesPermission) {
					return null;
				}

				return StyleBookUtil.getFrontendTokenDefinitionProviders(
					themeDisplay.getCompanyId(), themeDisplay.getLocale());
			}
		).put(
			"styleBookNamespace",
			() -> {
				if (!manageStyleBookEntriesPermission) {
					return null;
				}

				return PortalUtil.getPortletNamespace(
					StyleBookPortletKeys.STYLE_BOOK);
			}
		).build();
	}

	public boolean hasContentAccess() {
		if (_hasManageFragmentEntriesPermission(depotEntry.getGroupId()) ||
			_hasManageStyleBookEntriesPermission(depotEntry.getGroupId())) {

			return true;
		}

		return false;
	}

	private JSONArray _getActionItemsJSONArray(Group group)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		boolean hasAssignMembersPermission = hasAssignMembersPermission(group);

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
					DesignLibraryAdminFDSNames.DESIGN_LIBRARY_MEMBERS_USERS,
					DesignLibraryAdminFDSNames.
						DESIGN_LIBRARY_MEMBERS_USER_GROUPS)
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

	private String _getAddFragmentCollectionURL(Group depotGroup) {
		LiferayPortletURL portletURL =
			(LiferayPortletURL)PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT, 0,
				0, PortletRequest.RESOURCE_PHASE);

		portletURL.setResourceID("/fragment/add_fragment_collection");

		return portletURL.toString();
	}

	private String _getAddFragmentEntryURL(Group depotGroup) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT, 0,
				0, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/fragment/add_fragment_entry"
		).setRedirect(
			getViewResourcesURL(_liferayPortletResponse)
		).setParameter(
			"type", FragmentConstants.TYPE_COMPONENT
		).buildString();
	}

	private String _getAddStyleBookEntryURL(Group depotGroup) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				httpServletRequest, depotGroup, StyleBookPortletKeys.STYLE_BOOK,
				0, 0, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/style_book/add_style_book_entry"
		).setRedirect(
			getViewResourcesURL(_liferayPortletResponse)
		).setParameter(
			"backURLTitle", depotGroup.getName(themeDisplay.getLocale())
		).buildString();
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

	private JSONArray _getFragmentCollectionsJSONArray(long groupId)
		throws Exception {

		FragmentCollectionLocalService fragmentCollectionLocalService =
			_fragmentCollectionLocalServiceSnapshot.get();

		if (fragmentCollectionLocalService == null) {
			return JSONFactoryUtil.createJSONArray();
		}

		return JSONUtil.toJSONArray(
			fragmentCollectionLocalService.getFragmentCollections(
				groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			fragmentCollection -> JSONUtil.put(
				"fragmentCollectionId",
				fragmentCollection.getFragmentCollectionId()
			).put(
				"name", fragmentCollection.getName()
			));
	}

	private boolean _hasManageFragmentEntriesPermission(long groupId) {
		PortletResourcePermission portletResourcePermission =
			_fragmentPortletResourcePermissionSnapshot.get();

		if (portletResourcePermission == null) {
			return false;
		}

		return portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);
	}

	private boolean _hasManageStyleBookEntriesPermission(long groupId) {
		PortletResourcePermission portletResourcePermission =
			_styleBookPortletResourcePermissionSnapshot.get();

		if (portletResourcePermission == null) {
			return false;
		}

		return portletResourcePermission.contains(
			themeDisplay.getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);
	}

	private static final Snapshot<FragmentCollectionLocalService>
		_fragmentCollectionLocalServiceSnapshot = new Snapshot<>(
			ViewResourcesDesignLibraryDisplayContext.class,
			FragmentCollectionLocalService.class);
	private static final Snapshot<PortletResourcePermission>
		_fragmentPortletResourcePermissionSnapshot = new Snapshot<>(
			ViewResourcesDesignLibraryDisplayContext.class,
			PortletResourcePermission.class,
			"(resource.name=" + FragmentConstants.RESOURCE_NAME + ")");
	private static final Snapshot<PortletResourcePermission>
		_styleBookPortletResourcePermissionSnapshot = new Snapshot<>(
			ViewResourcesDesignLibraryDisplayContext.class,
			PortletResourcePermission.class,
			"(resource.name=" + StyleBookConstants.RESOURCE_NAME + ")");

	private final LiferayPortletResponse _liferayPortletResponse;

}