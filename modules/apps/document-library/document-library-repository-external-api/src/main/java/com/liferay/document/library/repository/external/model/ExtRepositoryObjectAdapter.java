/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.external.model;

import com.liferay.document.library.kernel.service.DLAppLocalServiceUtil;
import com.liferay.document.library.repository.external.ExtRepositoryAdapter;
import com.liferay.document.library.repository.external.ExtRepositoryObject;
import com.liferay.document.library.repository.external.ExtRepositoryObject.ExtRepositoryPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.RepositoryException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Iván Zaera
 * @author Sergio González
 */
public abstract class ExtRepositoryObjectAdapter<T>
	extends ExtRepositoryModelAdapter<T> {

	@SuppressWarnings("unused")
	public boolean containsPermission(
			PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		Boolean unsupportedActionIdValue = _unsupportedActionIds.get(actionId);

		if (unsupportedActionIdValue != null) {
			return unsupportedActionIdValue;
		}

		try {
			if (actionId.equals(ActionKeys.DOWNLOAD)) {
				actionId = ActionKeys.VIEW;
			}

			ExtRepositoryPermission extRepositoryPermission =
				ExtRepositoryPermission.valueOf(actionId);

			return _extRepositoryObject.containsPermission(
				extRepositoryPermission);
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new RepositoryException(
				"Unexpected permission action " + actionId,
				illegalArgumentException);
		}
	}

	public List<Long> getAncestorFolderIds() throws PortalException {
		List<Long> folderIds = new ArrayList<>();

		Folder folder = getParentFolder();

		while (!folder.isRoot()) {
			folderIds.add(folder.getFolderId());

			folder = folder.getParentFolder();
		}

		return folderIds;
	}

	public List<Folder> getAncestors() throws PortalException {
		List<Folder> folders = new ArrayList<>();

		Folder folder = getParentFolder();

		while ((folder != null) && !folder.isRoot()) {
			folders.add(folder);

			folder = folder.getParentFolder();
		}

		if (folder != null) {
			folders.add(folder);
		}

		return folders;
	}

	public String getExtension() {
		return _extRepositoryObject.getExtension();
	}

	@Override
	public ExtRepositoryObject getExtRepositoryModel() {
		return _extRepositoryObject;
	}

	@Override
	public Date getModifiedDate() {
		return _extRepositoryObject.getModifiedDate();
	}

	public abstract String getName();

	public Folder getParentFolder() throws PortalException {
		ExtRepositoryAdapter extRepositoryAdapter = getRepository();

		Folder parentFolder = extRepositoryAdapter.getParentFolder(this);

		if ((parentFolder == null) || parentFolder.isRoot()) {
			return DLAppLocalServiceUtil.getMountFolder(getRepositoryId());
		}

		return parentFolder;
	}

	public boolean isInTrash() {
		return false;
	}

	public boolean isInTrashContainer() {
		return false;
	}

	public boolean isSupportsMetadata() {
		return false;
	}

	public boolean isSupportsSocial() {
		return false;
	}

	protected ExtRepositoryObjectAdapter(
		ExtRepositoryAdapter extRepositoryAdapter, long extRepositoryObjectId,
		String uuid, ExtRepositoryObject extRepositoryObject) {

		super(
			extRepositoryAdapter, extRepositoryObjectId, uuid,
			extRepositoryObject);

		_extRepositoryObject = extRepositoryObject;
	}

	private static final Map<String, Boolean> _unsupportedActionIds =
		HashMapBuilder.put(
			ActionKeys.OVERRIDE_CHECKOUT, Boolean.FALSE
		).put(
			ActionKeys.SUBSCRIBE, Boolean.FALSE
		).build();

	private final ExtRepositoryObject _extRepositoryObject;

}