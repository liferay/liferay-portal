/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal.display.context;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.portal.kernel.bean.BeanParamUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class DLSelectFolderDisplayContext {

	public DLSelectFolderDisplayContext(
		long blockedFolderId, DLAppService dlAppService, Folder folder,
		ModelResourcePermission<Folder> folderModelResourcePermission,
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		long repositoryId, long selectedFolderId, long selectedRepositoryId,
		boolean showGroupSelector) {

		_blockedFolderId = blockedFolderId;
		_dlAppService = dlAppService;
		_folder = folder;
		_folderModelResourcePermission = folderModelResourcePermission;
		_httpServletRequest = httpServletRequest;
		_portletURL = portletURL;
		_repositoryId = repositoryId;
		_selectedFolderId = selectedFolderId;
		_selectedRepositoryId = selectedRepositoryId;
		_showGroupSelector = showGroupSelector;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public PortletURL getAddFolderPortletURL() {
		return PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				_httpServletRequest, DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/edit_folder"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"ignoreRootFolder", true
		).setParameter(
			"parentFolderId", getFolderId()
		).setParameter(
			"repositoryId", getRepositoryId()
		).buildRenderURL();
	}

	public Folder getFolder() {
		return _folder;
	}

	public int getFolderFileEntriesCount(Folder folder) {
		try {
			return _dlAppService.getFoldersFileEntriesCount(
				folder.getRepositoryId(),
				Collections.singletonList(folder.getFolderId()),
				WorkflowConstants.STATUS_APPROVED);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return 0;
		}
	}

	public int getFolderFoldersCount(Folder folder) {
		try {
			return _dlAppService.getFoldersCount(
				folder.getRepositoryId(), folder.getFolderId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return 0;
		}
	}

	public long getFolderId() {
		return BeanParamUtil.getLong(
			_folder, _httpServletRequest, "folderId",
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	public String getFolderName() {
		if (_folder == null) {
			return LanguageUtil.get(_httpServletRequest, "home");
		}

		return _folder.getName();
	}

	public List<Folder> getFolders(int start, int end) throws PortalException {
		return _dlAppService.getFolders(
			getRepositoryId(), getFolderId(), _isMountFolderVisible(), start,
			end);
	}

	public int getFoldersCount() throws PortalException {
		return _dlAppService.getFoldersCount(
			getRepositoryId(), getFolderId(), _isMountFolderVisible());
	}

	public String getIconCssClass(Folder folder) throws PortalException {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				DLFolder.class.getName());

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(
			folder.getFolderId());

		return assetRenderer.getIconCssClass();
	}

	public PortletURL getIteratorPortletURL(
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException, PortletException {

		return _getFolderPortletURL(getFolderId(), liferayPortletResponse);
	}

	public long getRepositoryId() {
		if (_repositoryId != 0) {
			return _repositoryId;
		}

		if (_folder != null) {
			return _folder.getRepositoryId();
		}

		return _themeDisplay.getScopeGroupId();
	}

	public PortletURL getRowPortletURL(
			Folder folder, LiferayPortletResponse liferayPortletResponse)
		throws PortalException, PortletException {

		if (folder.getFolderId() == _blockedFolderId) {
			return null;
		}

		return _getFolderPortletURL(
			folder.getFolderId(), liferayPortletResponse);
	}

	public long getSelectedFolderId() {
		return _selectedFolderId;
	}

	public long getSelectedRepositoryId() {
		return _selectedRepositoryId;
	}

	public Map<String, Object> getSelectorButtonData() {
		return getSelectorButtonData(_folder);
	}

	public Map<String, Object> getSelectorButtonData(Folder folder) {
		return HashMapBuilder.<String, Object>put(
			"folderissupportsmetadata",
			() -> {
				if (folder != null) {
					return folder.isSupportsMetadata();
				}

				return true;
			}
		).put(
			"folderissupportssocial",
			() -> {
				if (folder != null) {
					return folder.isSupportsSocial();
				}

				return true;
			}
		).put(
			"repositoryid", getRepositoryId()
		).put(
			"repositoryname",
			() -> {
				if ((folder == null) ||
					(getRepositoryId() == folder.getGroupId())) {

					Group group = GroupServiceUtil.getGroup(getRepositoryId());

					return group.getDescriptiveName(_themeDisplay.getLocale());
				}

				Repository repository =
					RepositoryLocalServiceUtil.fetchRepository(
						getRepositoryId());

				return repository.getName();
			}
		).put(
			"resourceid",
			() -> {
				if (folder != null) {
					return folder.getFolderId();
				}

				return DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}
		).put(
			"resourcename",
			() -> {
				if (folder != null) {
					return folder.getName();
				}

				return getFolderName();
			}
		).build();
	}

	public boolean hasAddFolderPermission() throws PortalException {
		if (_isAddFolderButtonVisible() &&
			_contains(
				_themeDisplay.getPermissionChecker(), getRepositoryId(),
				getFolderId(), ActionKeys.ADD_FOLDER)) {

			return true;
		}

		return false;
	}

	public boolean isSelectButtonDisabled() {
		return isSelectButtonDisabled(getFolderId(), getRepositoryId());
	}

	public boolean isSelectButtonDisabled(long folderId, long repositoryId) {
		if ((((DLFolderConstants.DEFAULT_PARENT_FOLDER_ID !=
				_blockedFolderId) &&
			  (folderId == _blockedFolderId)) ||
			 (folderId == getSelectedFolderId())) &&
			(repositoryId == getSelectedRepositoryId())) {

			return true;
		}

		return false;
	}

	public boolean isShowGroupSelector() {
		return _showGroupSelector;
	}

	private boolean _contains(
			PermissionChecker permissionChecker, long groupId, long folderId,
			String actionId)
		throws PortalException {

		return ModelResourcePermissionUtil.contains(
			_folderModelResourcePermission, permissionChecker, groupId,
			folderId, actionId);
	}

	private PortletURL _getFolderPortletURL(
			long folderId, LiferayPortletResponse liferayPortletResponse)
		throws PortletException {

		return PortletURLBuilder.create(
			PortletURLUtil.clone(_portletURL, liferayPortletResponse)
		).setParameter(
			"folderId", folderId
		).setParameter(
			"ignoreRootFolder", true
		).setParameter(
			"repositoryId", getRepositoryId()
		).setParameter(
			"selectedFolderId", getSelectedFolderId()
		).setParameter(
			"showMountFolder", _isMountFolderVisible()
		).buildRenderURL();
	}

	private boolean _isAddFolderButtonVisible() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		String portletName = portletDisplay.getPortletName();

		if (portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY) ||
			portletName.equals(DLPortletKeys.DOCUMENT_LIBRARY_ADMIN)) {

			return true;
		}

		return false;
	}

	private boolean _isMountFolderVisible() {
		return ParamUtil.getBoolean(
			_httpServletRequest, "showMountFolder", true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLSelectFolderDisplayContext.class);

	private final long _blockedFolderId;
	private final DLAppService _dlAppService;
	private final Folder _folder;
	private final ModelResourcePermission<Folder>
		_folderModelResourcePermission;
	private final HttpServletRequest _httpServletRequest;
	private final PortletURL _portletURL;
	private final long _repositoryId;
	private final long _selectedFolderId;
	private final long _selectedRepositoryId;
	private final boolean _showGroupSelector;
	private final ThemeDisplay _themeDisplay;

}