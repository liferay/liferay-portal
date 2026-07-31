/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.web.internal.display.context;

import com.liferay.depot.model.DepotEntry;
import com.liferay.design.library.web.internal.constants.DesignLibraryAdminFDSNames;
import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
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
public class ViewResourcesDesignLibraryDisplayContext {

	public ViewResourcesDesignLibraryDisplayContext(
		DepotEntry depotEntry, HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_depotEntry = depotEntry;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getAPIURL() {
		return StringBundler.concat(
			"/o/search/v1.0/search?emptySearch=true",
			"&entryClassNames=com.liferay.fragment.model.FragmentCollection",
			",com.liferay.style.book.model.StyleBookEntry",
			"&filter=groupIds/any(g:g eq ", _depotEntry.getGroupId(), ")",
			"&nestedFields=embedded");
	}

	public Map<String, Object> getBreadcrumbProps() throws PortalException {
		Group group = _depotEntry.getGroup();

		return HashMapBuilder.<String, Object>put(
			"actionItems", _getActionItemsJSONArray(group)
		).put(
			"breadcrumbItems", _getBreadcrumbItemsJSONArray(group)
		).build();
	}

	public Map<String, Object> getEmptyState() {
		return HashMapBuilder.<String, Object>put(
			"description",
			LanguageUtil.get(
				_httpServletRequest,
				"click-new-to-create-or-import-your-design-resource")
		).put(
			"image", "/states/resources_empty_state.svg"
		).put(
			"title",
			LanguageUtil.get(_httpServletRequest, "no-design-resources-yet")
		).build();
	}

	public List<FDSActionDropdownItem> getFDSActionDropdownItems()
		throws PortalException {

		Group depotGroup = _depotEntry.getGroup();

		String designLibraryResourcesURL = _getDesignLibraryResourcesURL();

		String viewFragmentCollectionURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT,
				0, 0, PortletRequest.RENDER_PHASE)
		).setBackURL(
			designLibraryResourcesURL
		).setParameter(
			"fragmentCollectionExternalReferenceCode",
			"{embedded.externalReferenceCode}"
		).buildString();
		String editFragmentCollectionURL = PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT,
				0, 0, PortletRequest.RENDER_PHASE)
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
				_httpServletRequest, depotGroup,
				StyleBookPortletKeys.STYLE_BOOK, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/style_book/edit_style_book_entry"
		).setRedirect(
			designLibraryResourcesURL
		).setParameter(
			"backURLTitle", depotGroup.getName(_themeDisplay.getLocale())
		).setParameter(
			"styleBookEntryId", "{embedded.id}"
		).buildString();

		return ListUtil.fromArray(
			new FDSActionDropdownItem(
				viewFragmentCollectionURL, "view", "view",
				LanguageUtil.get(_httpServletRequest, "view"), null, null,
				"link",
				HashMapBuilder.<String, Object>put(
					"entryClassName", FragmentCollection.class.getName()
				).build()),
			new FDSActionDropdownItem(
				editFragmentCollectionURL, "pencil", "edit",
				LanguageUtil.get(_httpServletRequest, "edit"), null, null,
				"link",
				HashMapBuilder.<String, Object>put(
					"entryClassName", FragmentCollection.class.getName()
				).build()),
			new FDSActionDropdownItem(
				editStyleBookEntryURL, "pencil", "edit",
				LanguageUtil.get(
					_httpServletRequest, "edit-in-style-book-editor"),
				null, "get", "link",
				HashMapBuilder.<String, Object>put(
					"entryClassName", StyleBookEntry.class.getName()
				).build()),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", "async",
				HashMapBuilder.<String, Object>put(
					"entryClassName", FragmentCollection.class.getName()
				).build()),
			new FDSActionDropdownItem(
				"{actions.delete.href}", "trash", "delete",
				LanguageUtil.get(_httpServletRequest, "delete"), "delete",
				"delete", "async",
				HashMapBuilder.<String, Object>put(
					"entryClassName", StyleBookEntry.class.getName()
				).build()));
	}

	public Map<String, Object> getFDSAdditionalProps() throws PortalException {
		Group depotGroup = _depotEntry.getGroup();

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
					_themeDisplay.getCompanyId(), _themeDisplay.getLocale());
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
		if (_hasManageFragmentEntriesPermission(_depotEntry.getGroupId()) ||
			_hasManageStyleBookEntriesPermission(_depotEntry.getGroupId())) {

			return true;
		}

		return false;
	}

	private JSONArray _getActionItemsJSONArray(Group group)
		throws PortalException {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		boolean hasAssignMembersPermission = GroupPermissionUtil.contains(
			_themeDisplay.getPermissionChecker(), group.getGroupId(),
			ActionKeys.ASSIGN_MEMBERS);

		boolean hasUpdatePermission = _hasPermission(group, ActionKeys.UPDATE);

		if (hasUpdatePermission) {
			jsonArray.put(
				JSONUtil.put(
					"href",
					PortletURLBuilder.createActionURL(
						_liferayPortletResponse
					).setMVCRenderCommandName(
						"/design_library/edit_design_library"
					).setParameter(
						"designLibraryEntryId", _depotEntry.getDepotEntryId()
					).buildString()
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "settings")
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
				"label",
				LanguageUtil.get(_httpServletRequest, "connected-sites")
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
					_httpServletRequest,
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
					"label", LanguageUtil.get(_httpServletRequest, "export")
				).put(
					"symbolLeft", "export"
				)
			).put(
				JSONUtil.put(
					"href",
					_getExportImportPortletURL(
						group, ExportImportPortletKeys.IMPORT)
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "import")
				).put(
					"symbolLeft", "import"
				)
			);
		}

		if (_hasPermission(group, ActionKeys.DELETE)) {
			jsonArray.put(
				JSONUtil.put(
					"descriptiveName", group.getDescriptiveName()
				).put(
					"href",
					"/o/headless-asset-library/v1.0/asset-libraries/" +
						group.getExternalReferenceCode()
				).put(
					"label", LanguageUtil.get(_httpServletRequest, "delete")
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
				_httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT,
				0, 0, PortletRequest.RESOURCE_PHASE);

		portletURL.setResourceID("/fragment/add_fragment_collection");

		return portletURL.toString();
	}

	private String _getAddFragmentEntryURL(Group depotGroup) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, depotGroup, FragmentPortletKeys.FRAGMENT,
				0, 0, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/fragment/add_fragment_entry"
		).setRedirect(
			_getDesignLibraryResourcesURL()
		).setParameter(
			"type", FragmentConstants.TYPE_COMPONENT
		).buildString();
	}

	private String _getAddStyleBookEntryURL(Group depotGroup) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, depotGroup,
				StyleBookPortletKeys.STYLE_BOOK, 0, 0,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/style_book/add_style_book_entry"
		).setRedirect(
			_getDesignLibraryResourcesURL()
		).setParameter(
			"backURLTitle", depotGroup.getName(_themeDisplay.getLocale())
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
				LanguageUtil.get(_httpServletRequest, "design-libraries")
			),
			JSONUtil.put(
				"active", true
			).put(
				"href", "#top"
			).put(
				"label", group.getName(_httpServletRequest.getLocale())
			));
	}

	private String _getDesignLibraryResourcesURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).setMVCRenderCommandName(
			"/design_library/view_resources_design_library"
		).setParameter(
			"designLibraryEntryId", _depotEntry.getDepotEntryId()
		).buildString();
	}

	private String _getExportImportPortletURL(Group group, String portletId) {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest, group, portletId, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setBackURL(
			PortalUtil.getCurrentURL(_httpServletRequest)
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
			_themeDisplay.getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);
	}

	private boolean _hasManageStyleBookEntriesPermission(long groupId) {
		PortletResourcePermission portletResourcePermission =
			_styleBookPortletResourcePermissionSnapshot.get();

		if (portletResourcePermission == null) {
			return false;
		}

		return portletResourcePermission.contains(
			_themeDisplay.getPermissionChecker(), groupId,
			StyleBookActionKeys.MANAGE_STYLE_BOOK_ENTRIES);
	}

	private boolean _hasPermission(Group group, String actionId) {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		return permissionChecker.hasPermission(
			group, DepotEntry.class.getName(), group.getClassPK(), actionId);
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

	private final DepotEntry _depotEntry;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ThemeDisplay _themeDisplay;

}