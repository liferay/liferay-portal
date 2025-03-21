/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.web.internal.servlet.taglib.util;

import com.liferay.blogs.constants.BlogsPortletKeys;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.web.internal.security.permission.resource.BlogsEntryPermission;
import com.liferay.blogs.web.internal.sharing.BlogsEntrySharingUtil;
import com.liferay.blogs.web.internal.util.BlogsEntryUtil;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandler;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Adolfo Pérez
 */
public class BlogsEntryActionDropdownItemsProvider {

	public BlogsEntryActionDropdownItemsProvider(
		RenderRequest renderRequest, RenderResponse renderResponse,
		PermissionChecker permissionChecker, ResourceBundle resourceBundle,
		TrashHelper trashHelper) {

		_renderResponse = renderResponse;
		_permissionChecker = permissionChecker;
		_resourceBundle = resourceBundle;
		_trashHelper = trashHelper;

		_httpServletRequest = PortalUtil.getHttpServletRequest(renderRequest);
	}

	public List<DropdownItem> getActionDropdownItems(BlogsEntry blogsEntry)
		throws PortalException {

		boolean sharingEnabled = BlogsEntrySharingUtil.isSharingEnabled(
			blogsEntry.getGroupId());
		boolean hasDeletePermission = _hasDeletePermission(blogsEntry);
		boolean trashEnabled = _isTrashEnabled();

		return DropdownItemListBuilder.add(
			() -> _hasUpdatePermission(blogsEntry),
			_getEditEntryActionUnsafeConsumer(blogsEntry)
		).add(
			() ->
				sharingEnabled &&
				BlogsEntrySharingUtil.containsSharePermission(
					_permissionChecker, blogsEntry),
			BlogsEntrySharingUtil.createShareDropdownItem(
				blogsEntry, _httpServletRequest)
		).add(
			() ->
				sharingEnabled &&
				BlogsEntrySharingUtil.containsManageCollaboratorsPermission(
					_permissionChecker, blogsEntry),
			BlogsEntrySharingUtil.createManageCollaboratorsDropdownItem(
				blogsEntry, _httpServletRequest)
		).add(
			() -> _hasPermissionsPermission(blogsEntry),
			_getPermissionsActionUnsafeConsumer(blogsEntry)
		).add(
			() -> hasDeletePermission && trashEnabled,
			_getMoveEntryToTrashActionUnsafeConsumer(blogsEntry)
		).add(
			() -> hasDeletePermission && !trashEnabled,
			_getDeleteEntryActionUnsafeConsumer(blogsEntry)
		).add(
			() ->
				_isShowPublishMenuItem(blogsEntry) &&
				_hasExportImportPortletInfoPermission(blogsEntry),
			_getPublishToLiveEntryActionUnsafeConsumer(blogsEntry)
		).build();
	}

	/**
	 * @see com.liferay.exportimport.changeset.taglib.internal.display.context.ChangesetTaglibDisplayContext#isShowPublishMenuItem(
	 *      Group, String)
	 */
	private static boolean _isShowPublishMenuItem(
		Group group, String portletId) {

		try {
			if (group.isLayout()) {
				return false;
			}

			if ((group.isStagingGroup() || group.isStagedRemotely()) &&
				group.isStagedPortlet(portletId)) {

				return true;
			}

			return false;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	/**
	 * @see com.liferay.exportimport.changeset.taglib.internal.display.context.ChangesetTaglibDisplayContext#isShowPublishMenuItem(
	 *      Group, String, String, String)
	 */
	private static boolean _isShowPublishMenuItem(
		Group group, String portletId, String className, String uuid) {

		try {
			StagedModelDataHandler<?> stagedModelDataHandler =
				StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(
					className);

			StagedModel stagedModel =
				stagedModelDataHandler.fetchStagedModelByUuidAndGroupId(
					uuid, group.getGroupId());

			if (stagedModel == null) {
				return false;
			}

			if (stagedModel instanceof WorkflowedModel) {
				WorkflowedModel workflowedModel = (WorkflowedModel)stagedModel;

				if (!ArrayUtil.contains(
						stagedModelDataHandler.getExportableStatuses(),
						workflowedModel.getStatus())) {

					return false;
				}
			}

			return _isShowPublishMenuItem(group, portletId);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getDeleteEntryActionUnsafeConsumer(BlogsEntry blogsEntry) {

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData(
				"deleteURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/blogs/edit_entry"
				).setCMD(
					Constants.DELETE
				).setRedirect(
					_getRedirectURL()
				).setParameter(
					"entryId", blogsEntry.getEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditEntryActionUnsafeConsumer(BlogsEntry blogsEntry) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return dropdownItem -> {
			String portletResource = StringPool.BLANK;

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			if (!Objects.equals(
					portletDisplay.getPortletName(),
					BlogsPortletKeys.BLOGS_ADMIN)) {

				portletResource = portletDisplay.getPortletName();
			}

			dropdownItem.setHref(
				PortalUtil.getControlPanelPortletURL(
					_httpServletRequest, themeDisplay.getScopeGroup(),
					BlogsPortletKeys.BLOGS_ADMIN, 0, themeDisplay.getPlid(),
					PortletRequest.RENDER_PHASE),
				"mvcRenderCommandName", "/blogs/edit_entry", "redirect",
				_getRedirectURL(), "portletResource", portletResource,
				"entryId", blogsEntry.getEntryId());
			dropdownItem.setIcon("edit");
			dropdownItem.setLabel(LanguageUtil.get(_resourceBundle, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveEntryToTrashActionUnsafeConsumer(BlogsEntry blogsEntry) {

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData(
				"deleteURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/blogs/edit_entry"
				).setCMD(
					Constants.MOVE_TO_TRASH
				).setRedirect(
					_getRedirectURL()
				).setParameter(
					"entryId", blogsEntry.getEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "delete"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPermissionsActionUnsafeConsumer(BlogsEntry blogsEntry) {

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData(
				"permissionsURL", _getPermissionsURL(blogsEntry));
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private String _getPermissionsURL(BlogsEntry blogsEntry) {
		try {
			return PermissionsURLTag.doTag(
				StringPool.BLANK, BlogsEntry.class.getName(),
				BlogsEntryUtil.getDisplayTitle(_resourceBundle, blogsEntry),
				null, String.valueOf(blogsEntry.getEntryId()),
				LiferayWindowState.POP_UP.toString(), null,
				_httpServletRequest);
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPublishToLiveEntryActionUnsafeConsumer(BlogsEntry blogsEntry) {

		return dropdownItem -> {
			dropdownItem.putData("action", "publishToLive");
			dropdownItem.putData(
				"publishEntryURL",
				PortletURLBuilder.createActionURL(
					_renderResponse
				).setActionName(
					"/blogs/publish_entry"
				).setBackURL(
					_getRedirectURL()
				).setParameter(
					"entryId", blogsEntry.getEntryId()
				).buildString());
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "publish-to-live"));
		};
	}

	private String _getRedirectURL() {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			return redirect;
		}

		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCRenderCommandName(
			"/blogs/view"
		).buildString();
	}

	private boolean _hasDeletePermission(BlogsEntry blogsEntry) {
		try {
			return BlogsEntryPermission.contains(
				_permissionChecker, blogsEntry, ActionKeys.DELETE);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private boolean _hasExportImportPortletInfoPermission(
		BlogsEntry blogsEntry) {

		try {
			return GroupPermissionUtil.contains(
				_permissionChecker, blogsEntry.getGroupId(),
				ActionKeys.EXPORT_IMPORT_PORTLET_INFO);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private boolean _hasPermissionsPermission(BlogsEntry blogsEntry) {
		try {
			return BlogsEntryPermission.contains(
				_permissionChecker, blogsEntry, ActionKeys.PERMISSIONS);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private boolean _hasUpdatePermission(BlogsEntry blogsEntry) {
		try {
			return BlogsEntryPermission.contains(
				_permissionChecker, blogsEntry, ActionKeys.UPDATE);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private boolean _isShowPublishMenuItem(BlogsEntry blogsEntry) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return _isShowPublishMenuItem(
			themeDisplay.getScopeGroup(), BlogsPortletKeys.BLOGS_ADMIN,
			BlogsEntry.class.getName(), blogsEntry.getUuid());
	}

	private boolean _isTrashEnabled() {
		try {
			return _trashHelper.isTrashEnabled(
				PortalUtil.getScopeGroupId(_httpServletRequest));
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BlogsEntryActionDropdownItemsProvider.class);

	private final HttpServletRequest _httpServletRequest;
	private final PermissionChecker _permissionChecker;
	private final RenderResponse _renderResponse;
	private final ResourceBundle _resourceBundle;
	private final TrashHelper _trashHelper;

}