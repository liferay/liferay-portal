/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.util;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.criteria.FolderItemSelectorReturnType;
import com.liferay.item.selector.criteria.folder.criterion.FolderItemSelectorCriterion;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RepositoryLocalServiceUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Adolfo Pérez
 */
public class FolderItemSelectorURLProvider {

	public FolderItemSelectorURLProvider(
		HttpServletRequest httpServletRequest, ItemSelector itemSelector) {

		_httpServletRequest = httpServletRequest;
		_itemSelector = itemSelector;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getSelectAddFileEntryFolderURL(long folderId) {
		FolderItemSelectorCriterion folderItemSelectorCriterion =
			new FolderItemSelectorCriterion();

		folderItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FolderItemSelectorReturnType());
		folderItemSelectorCriterion.setFolderId(folderId);

		return String.valueOf(_getItemSelectorURL(folderItemSelectorCriterion));
	}

	public String getSelectCopyToFolderURL(
			long repositoryId, long parentFolderId, long folderId)
		throws PortalException {

		return getSelectMoveToFolderURL(repositoryId, parentFolderId, folderId);
	}

	public String getSelectMoveToFolderURL(
			long repositoryId, long parentFolderId, long folderId)
		throws PortalException {

		FolderItemSelectorCriterion folderItemSelectorCriterion =
			new FolderItemSelectorCriterion();

		folderItemSelectorCriterion.setBlockedFolderId(folderId);
		folderItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FolderItemSelectorReturnType());
		folderItemSelectorCriterion.setFolderId(parentFolderId);
		folderItemSelectorCriterion.setIgnoreRootFolder(true);
		folderItemSelectorCriterion.setRepositoryId(repositoryId);
		folderItemSelectorCriterion.setSelectedFolderId(parentFolderId);
		folderItemSelectorCriterion.setSelectedRepositoryId(repositoryId);
		folderItemSelectorCriterion.setShowGroupSelector(true);
		folderItemSelectorCriterion.setShowMountFolder(false);

		return String.valueOf(
			_getItemSelectorURL(repositoryId, folderItemSelectorCriterion));
	}

	public String getSelectRootFolderURL(long repositoryId, long folderId)
		throws PortalException {

		FolderItemSelectorCriterion folderItemSelectorCriterion =
			new FolderItemSelectorCriterion();

		folderItemSelectorCriterion.setDesiredItemSelectorReturnTypes(
			new FolderItemSelectorReturnType());
		folderItemSelectorCriterion.setFolderId(folderId);
		folderItemSelectorCriterion.setIgnoreRootFolder(true);
		folderItemSelectorCriterion.setRepositoryId(repositoryId);
		folderItemSelectorCriterion.setSelectedFolderId(folderId);
		folderItemSelectorCriterion.setSelectedRepositoryId(repositoryId);
		folderItemSelectorCriterion.setShowGroupSelector(true);
		folderItemSelectorCriterion.setShowMountFolder(false);

		return String.valueOf(
			_getItemSelectorURL(repositoryId, folderItemSelectorCriterion));
	}

	private String _getItemSelectedEventName() {
		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		return portletDisplay.getNamespace() + "folderSelected";
	}

	private PortletURL _getItemSelectorURL(
		FolderItemSelectorCriterion folderItemSelectorCriterion) {

		return _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
			_getItemSelectedEventName(), folderItemSelectorCriterion);
	}

	private PortletURL _getItemSelectorURL(
			long repositoryId,
			FolderItemSelectorCriterion folderItemSelectorCriterion)
		throws PortalException {

		return _itemSelector.getItemSelectorURL(
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest),
			_getRepositoryGroup(repositoryId), _themeDisplay.getScopeGroupId(),
			_getItemSelectedEventName(), folderItemSelectorCriterion);
	}

	private Group _getRepositoryGroup(long repositoryId)
		throws PortalException {

		Repository repository = RepositoryLocalServiceUtil.fetchRepository(
			repositoryId);

		if (repository == null) {
			return GroupLocalServiceUtil.getGroup(repositoryId);
		}

		return GroupLocalServiceUtil.getGroup(repository.getGroupId());
	}

	private final HttpServletRequest _httpServletRequest;
	private final ItemSelector _itemSelector;
	private final ThemeDisplay _themeDisplay;

}