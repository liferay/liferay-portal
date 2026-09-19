/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.repository.model;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Kyle Stiemann
 */
public class FolderWrapper implements Folder, ModelWrapper<Folder> {

	public FolderWrapper(Folder folder) {
		_folder = folder;
	}

	@Override
	public Object clone() {
		return new FolderWrapper((Folder)_folder.clone());
	}

	@Override
	public boolean containsPermission(
			PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		return _folder.containsPermission(permissionChecker, actionId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FolderWrapper)) {
			return false;
		}

		FolderWrapper folderWrapper = (FolderWrapper)object;

		return Objects.equals(_folder, folderWrapper._folder);
	}

	@Override
	public void execute(RepositoryModelOperation repositoryModelOperation)
		throws PortalException {

		repositoryModelOperation.execute(this);
	}

	@Override
	public List<Long> getAncestorFolderIds() throws PortalException {
		return _folder.getAncestorFolderIds();
	}

	@Override
	public List<Folder> getAncestors() throws PortalException {
		return _folder.getAncestors();
	}

	@Override
	public Map<String, Serializable> getAttributes() {
		return _folder.getAttributes();
	}

	@Override
	public long getCompanyId() {
		return _folder.getCompanyId();
	}

	@Override
	public Date getCreateDate() {
		return _folder.getCreateDate();
	}

	@Override
	public String getDescription() {
		return _folder.getDescription();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _folder.getExpandoBridge();
	}

	@Override
	public long getFolderId() {
		return _folder.getFolderId();
	}

	@Override
	public long getGroupId() {
		return _folder.getGroupId();
	}

	@Override
	public Date getLastPostDate() {
		return _folder.getLastPostDate();
	}

	@Override
	public Date getLastPublishDate() {
		return _folder.getLastPublishDate();
	}

	@Override
	public Object getModel() {
		return _folder.getModel();
	}

	@Override
	public Class<?> getModelClass() {
		return Folder.class;
	}

	@Override
	public String getModelClassName() {
		return Folder.class.getName();
	}

	@Override
	public Date getModifiedDate() {
		return _folder.getModifiedDate();
	}

	@Override
	public String getName() {
		return _folder.getName();
	}

	@Override
	public Folder getParentFolder() throws PortalException {
		return _folder.getParentFolder();
	}

	@Override
	public long getParentFolderId() {
		return _folder.getParentFolderId();
	}

	@Override
	public long getPrimaryKey() {
		return _folder.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _folder.getPrimaryKeyObj();
	}

	@Override
	public <T extends Capability> T getRepositoryCapability(
		Class<T> capabilityClass) {

		return _folder.getRepositoryCapability(capabilityClass);
	}

	@Override
	public long getRepositoryId() {
		return _folder.getRepositoryId();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return _folder.getStagedModelType();
	}

	@Override
	public long getUserId() {
		return _folder.getUserId();
	}

	@Override
	public String getUserName() {
		return _folder.getUserName();
	}

	@Override
	public String getUserUuid() {
		return _folder.getUserUuid();
	}

	@Override
	public String getUuid() {
		return _folder.getUuid();
	}

	@Override
	public Folder getWrappedModel() {
		return _folder;
	}

	@Override
	public boolean hasInheritableLock() {
		return _folder.hasInheritableLock();
	}

	@Override
	public boolean hasLock() {
		return _folder.hasLock();
	}

	@Override
	public int hashCode() {
		return _folder.hashCode();
	}

	@Override
	public boolean isDefaultRepository() {
		return _folder.isDefaultRepository();
	}

	@Override
	public boolean isEscapedModel() {
		return _folder.isEscapedModel();
	}

	@Override
	public boolean isLocked() {
		return _folder.isLocked();
	}

	@Override
	public boolean isMountPoint() {
		return _folder.isMountPoint();
	}

	@Override
	public <T extends Capability> boolean isRepositoryCapabilityProvided(
		Class<T> capabilityClass) {

		return _folder.isRepositoryCapabilityProvided(capabilityClass);
	}

	@Override
	public boolean isRoot() {
		return _folder.isRoot();
	}

	@Override
	public boolean isSupportsLocking() {
		return _folder.isSupportsLocking();
	}

	@Override
	public boolean isSupportsMetadata() {
		return _folder.isSupportsMetadata();
	}

	@Override
	public boolean isSupportsMultipleUpload() {
		return _folder.isSupportsMultipleUpload();
	}

	@Override
	public boolean isSupportsShortcuts() {
		return _folder.isSupportsShortcuts();
	}

	@Override
	public boolean isSupportsSocial() {
		return _folder.isSupportsSocial();
	}

	@Override
	public boolean isSupportsSubscribing() {
		return _folder.isSupportsSubscribing();
	}

	@Override
	public void setCompanyId(long companyId) {
		_folder.setCompanyId(companyId);
	}

	@Override
	public void setCreateDate(Date createDate) {
		_folder.setCreateDate(createDate);
	}

	@Override
	public void setGroupId(long groupId) {
		_folder.setGroupId(groupId);
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_folder.setLastPublishDate(lastPublishDate);
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_folder.setModifiedDate(modifiedDate);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_folder.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public void setUserId(long userId) {
		_folder.setUserId(userId);
	}

	@Override
	public void setUserName(String userName) {
		_folder.setUserName(userName);
	}

	@Override
	public void setUserUuid(String userUuid) {
		_folder.setUserUuid(userUuid);
	}

	@Override
	public void setUuid(String uuid) {
		_folder.setUuid(uuid);
	}

	@Override
	public Folder toEscapedModel() {
		return new FolderWrapper(_folder.toEscapedModel());
	}

	@Override
	public String toString() {
		return _folder.toString();
	}

	@Override
	public Folder toUnescapedModel() {
		return new FolderWrapper(_folder.toUnescapedModel());
	}

	private final Folder _folder;

}