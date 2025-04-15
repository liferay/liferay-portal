/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.servlet.taglib.util;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.web.internal.security.permission.resource.JournalFolderPermission;
import com.liferay.journal.web.internal.security.permission.resource.JournalPermission;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowHandlerRegistryUtil;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;
import com.liferay.taglib.security.PermissionsURLTag;
import com.liferay.trash.TrashHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class JournalFolderActionDropdownItems {

	public JournalFolderActionDropdownItems(
		JournalFolder folder, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse,
		TrashHelper trashHelper) {

		_folder = folder;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_trashHelper = trashHelper;

		_httpServletRequest = PortalUtil.getHttpServletRequest(
			liferayPortletRequest);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public List<DropdownItem> getActionDropdownItems() throws Exception {
		boolean hasUpdatePermission = JournalFolderPermission.contains(
			_themeDisplay.getPermissionChecker(), _folder, ActionKeys.UPDATE);

		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() ->
							hasUpdatePermission ||
							JournalFolderPermission.contains(
								_themeDisplay.getPermissionChecker(), _folder,
								ActionKeys.ADVANCED_UPDATE),
						_getEditFolderActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> hasUpdatePermission,
						_getMoveFolderActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalFolderPermission.contains(
							_themeDisplay.getPermissionChecker(), _folder,
							ActionKeys.PERMISSIONS),
						_getPermissionsFolderActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> JournalFolderPermission.contains(
							_themeDisplay.getPermissionChecker(), _folder,
							ActionKeys.DELETE),
						_getDeleteFolderActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					DropdownItemListBuilder.add(
						() -> {
							Group group = _themeDisplay.getScopeGroup();

							if (_isShowPublishFolderAction() &&
								!group.isLayout()) {

								return true;
							}

							return false;
						},
						_getPublishToLiveFolderActionUnsafeConsumer()
					).build());
				dropdownGroupItem.setSeparator(true);
			}
		).build();
	}

	public List<DropdownItem> getInfoPanelActionDropdownItems()
		throws Exception {

		if (_folder != null) {
			List<DropdownItem> actionDropdownItems = getActionDropdownItems();

			if (JournalFolderPermission.contains(
					_themeDisplay.getPermissionChecker(), _folder,
					ActionKeys.ADD_FOLDER)) {

				actionDropdownItems.add(
					0,
					DropdownItemBuilder.setHref(
						_liferayPortletResponse.createRenderURL(), "mvcPath",
						"/edit_folder.jsp", "redirect", _getRedirect(),
						"groupId", _folder.getGroupId(), "parentFolderId",
						_folder.getFolderId()
					).setLabel(
						LanguageUtil.get(_httpServletRequest, "add-subfolder")
					).build());
			}

			return actionDropdownItems;
		}

		return DropdownItemListBuilder.add(
			() -> JournalPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.ADD_FOLDER),
			_getAddHomeFolderActionUnsafeConsumer()
		).add(
			() -> {
				boolean workflowEnabled = false;

				WorkflowHandler<?> workflowHandler =
					WorkflowHandlerRegistryUtil.getWorkflowHandler(
						JournalArticle.class.getName());

				if (workflowHandler != null) {
					workflowEnabled = true;
				}

				if (workflowEnabled &&
					JournalFolderPermission.contains(
						_themeDisplay.getPermissionChecker(),
						_themeDisplay.getScopeGroupId(),
						JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
						ActionKeys.ADVANCED_UPDATE)) {

					return true;
				}

				return false;
			},
			_getEditHomeFolderActionUnsafeConsumer()
		).add(
			() -> JournalPermission.contains(
				_themeDisplay.getPermissionChecker(),
				_themeDisplay.getScopeGroupId(), ActionKeys.PERMISSIONS),
			_getPermissionsHomeFolderActionUnsafeConsumer()
		).add(
			() -> {
				Group group = _themeDisplay.getScopeGroup();

				if (_isShowPublishFolderAction() && !group.isLayout()) {
					return true;
				}

				return false;
			},
			_getPublishToLiveFolderActionUnsafeConsumer()
		).build();
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getAddHomeFolderActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/edit_folder.jsp", "redirect", _getRedirect(), "groupId",
				_themeDisplay.getScopeGroupId(), "parentFolderId",
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "add-folder"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getDeleteFolderActionUnsafeConsumer()
		throws Exception {

		String redirect = _getRedirect();

		long currentFolderId = ParamUtil.getLong(
			_httpServletRequest, "folderId");

		if (currentFolderId == _folder.getFolderId()) {
			redirect = PortletURLBuilder.createRenderURL(
				_liferayPortletResponse
			).setParameter(
				"folderId", _folder.getParentFolderId()
			).setParameter(
				"groupId", _folder.getGroupId()
			).buildString();
		}

		String actionName = "/journal/delete_folder";

		if (_trashHelper.isTrashEnabled(_themeDisplay.getScopeGroupId())) {
			actionName = "/journal/move_folder_to_trash";
		}

		String label = LanguageUtil.get(_httpServletRequest, "delete");

		String deleteURL = PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			actionName
		).setRedirect(
			redirect
		).setParameter(
			"folderId", _folder.getFolderId()
		).setParameter(
			"groupId", _folder.getGroupId()
		).buildString();

		return dropdownItem -> {
			dropdownItem.putData("action", "delete");
			dropdownItem.putData("deleteURL", deleteURL);
			dropdownItem.setIcon("trash");
			dropdownItem.setLabel(label);
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditFolderActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/edit_folder.jsp", "redirect", _getRedirect(), "groupId",
				_folder.getGroupId(), "folderId", _folder.getFolderId());
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getEditHomeFolderActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/edit_folder.jsp", "redirect", _getRedirect(), "groupId",
				_themeDisplay.getScopeGroupId(), "folderId",
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID, "rootFolder",
				true);
			dropdownItem.setIcon("pencil");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "edit"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getMoveFolderActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.setHref(
				_liferayPortletResponse.createRenderURL(), "mvcPath",
				"/move_articles_and_folders.jsp", "redirect", _getRedirect(),
				"rowIdsJournalFolder", _folder.getFolderId());
			dropdownItem.setIcon("move-folder");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "move"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsFolderActionUnsafeConsumer()
		throws Exception {

		String permissionsURL = PermissionsURLTag.doTag(
			StringPool.BLANK, JournalFolder.class.getName(), _folder.getName(),
			null, String.valueOf(_folder.getPrimaryKey()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData("permissionsURL", permissionsURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
			_getPermissionsHomeFolderActionUnsafeConsumer()
		throws Exception {

		String permissionsURL = PermissionsURLTag.doTag(
			StringPool.BLANK, "com.liferay.journal",
			_themeDisplay.getScopeGroupName(), null,
			String.valueOf(_themeDisplay.getScopeGroupId()),
			LiferayWindowState.POP_UP.toString(), null, _httpServletRequest);

		return dropdownItem -> {
			dropdownItem.putData("action", "permissions");
			dropdownItem.putData("permissionsURL", permissionsURL);
			dropdownItem.setIcon("password-policies");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "permissions"));
		};
	}

	private UnsafeConsumer<DropdownItem, Exception>
		_getPublishToLiveFolderActionUnsafeConsumer() {

		return dropdownItem -> {
			dropdownItem.putData("action", "publishFolderToLive");
			dropdownItem.putData(
				"publishFolderURL",
				PortletURLBuilder.createActionURL(
					_liferayPortletResponse
				).setActionName(
					"/journal/publish_folder"
				).setBackURL(
					_getRedirect()
				).setParameter(
					"folderId",
					() -> {
						if (_folder != null) {
							return _folder.getFolderId();
						}

						return null;
					}
				).buildString());
			dropdownItem.setIcon("live");
			dropdownItem.setLabel(
				LanguageUtil.get(_httpServletRequest, "publish-to-live"));
		};
	}

	private String _getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(
			_liferayPortletRequest, "redirect", _themeDisplay.getURLCurrent());

		return _redirect;
	}

	private boolean _isShowPublishAction() {
		PermissionChecker permissionChecker =
			_themeDisplay.getPermissionChecker();

		long scopeGroupId = _themeDisplay.getScopeGroupId();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		try {
			if (GroupPermissionUtil.contains(
					permissionChecker, scopeGroupId,
					ActionKeys.EXPORT_IMPORT_PORTLET_INFO) &&
				stagingGroupHelper.isStagingGroup(scopeGroupId) &&
				stagingGroupHelper.isStagedPortlet(
					scopeGroupId, JournalPortletKeys.JOURNAL)) {

				return true;
			}

			return false;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"An exception occured when checking if the publish " +
						"action should be displayed",
					portalException);
			}

			return false;
		}
	}

	private boolean _isShowPublishFolderAction() {
		if (_folder == null) {
			return false;
		}

		return _isShowPublishAction();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFolderActionDropdownItems.class);

	private final JournalFolder _folder;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private String _redirect;
	private final ThemeDisplay _themeDisplay;
	private final TrashHelper _trashHelper;

}