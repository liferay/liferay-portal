/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.liferayrepository.model;

import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.ExternalReferenceCodeModel;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryModelOperation;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portlet.documentlibrary.util.RepositoryModelUtil;

import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Chow
 */
public class LiferayFolder
	extends LiferayModel implements ExternalReferenceCodeModel, Folder {

	public LiferayFolder(DLFolder dlFolder) {
		_dlFolder = dlFolder;

		if (dlFolder == null) {
			_escapedModel = false;
		}
		else {
			_escapedModel = dlFolder.isEscapedModel();
		}
	}

	public LiferayFolder(DLFolder dlFolder, boolean escapedModel) {
		_dlFolder = dlFolder;
		_escapedModel = escapedModel;
	}

	@Override
	public Object clone() {
		return new LiferayFolder(_dlFolder);
	}

	@Override
	public boolean containsPermission(
			PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		ModelResourcePermission<DLFolder> dlFolderModelResourcePermission =
			ModelResourcePermissionRegistryUtil.getModelResourcePermission(
				DLFolder.class.getName());

		return dlFolderModelResourcePermission.contains(
			permissionChecker, _dlFolder, actionId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LiferayFolder)) {
			return false;
		}

		LiferayFolder liferayFolder = (LiferayFolder)object;

		return Objects.equals(_dlFolder, liferayFolder._dlFolder);
	}

	@Override
	public void execute(RepositoryModelOperation repositoryModelOperation)
		throws PortalException {

		repositoryModelOperation.execute(this);
	}

	@Override
	public List<Long> getAncestorFolderIds() throws PortalException {
		return _dlFolder.getAncestorFolderIds();
	}

	@Override
	public List<Folder> getAncestors() throws PortalException {
		return RepositoryModelUtil.toFolders(_dlFolder.getAncestors());
	}

	@Override
	public Map<String, Serializable> getAttributes() {
		ExpandoBridge expandoBridge = getExpandoBridge();

		return expandoBridge.getAttributes();
	}

	@Override
	public long getCompanyId() {
		return _dlFolder.getCompanyId();
	}

	@Override
	public Date getCreateDate() {
		return _dlFolder.getCreateDate();
	}

	@Override
	public String getDescription() {
		return _dlFolder.getDescription();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _dlFolder.getExpandoBridge();
	}

	@Override
	public String getExternalReferenceCode() {
		return _dlFolder.getExternalReferenceCode();
	}

	@Override
	public long getFolderId() {
		return _dlFolder.getFolderId();
	}

	@Override
	public long getGroupId() {
		return _dlFolder.getGroupId();
	}

	@Override
	public Date getLastPostDate() {
		return _dlFolder.getLastPostDate();
	}

	@Override
	public Date getLastPublishDate() {
		return _dlFolder.getLastPublishDate();
	}

	@Override
	public Object getModel() {
		return _dlFolder;
	}

	@Override
	public Class<?> getModelClass() {
		return LiferayFolder.class;
	}

	@Override
	public String getModelClassName() {
		return LiferayFolder.class.getName();
	}

	@Override
	public Date getModifiedDate() {
		return _dlFolder.getModifiedDate();
	}

	@Override
	public String getName() {
		return _dlFolder.getName();
	}

	@Override
	public Folder getParentFolder() throws PortalException {
		DLFolder dlParentFolder = _dlFolder.getParentFolder();

		if (dlParentFolder == null) {
			return null;
		}

		return new LiferayFolder(dlParentFolder);
	}

	@Override
	public long getParentFolderId() {
		return _dlFolder.getParentFolderId();
	}

	@Override
	public long getPrimaryKey() {
		return _dlFolder.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return getPrimaryKey();
	}

	@Override
	public <T extends Capability> T getRepositoryCapability(
		Class<T> capabilityClass) {

		Repository repository = _getRepository();

		return repository.getCapability(capabilityClass);
	}

	@Override
	public long getRepositoryId() {
		return _dlFolder.getRepositoryId();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(DLFolderConstants.getClassName());
	}

	@Override
	public long getUserId() {
		return _dlFolder.getUserId();
	}

	@Override
	public String getUserName() {
		return _dlFolder.getUserName();
	}

	@Override
	public String getUserUuid() {
		return _dlFolder.getUserUuid();
	}

	@Override
	public String getUuid() {
		return _dlFolder.getUuid();
	}

	@Override
	public boolean hasInheritableLock() {
		return _dlFolder.hasInheritableLock();
	}

	@Override
	public boolean hasLock() {
		return _dlFolder.hasLock();
	}

	@Override
	public int hashCode() {
		return _dlFolder.hashCode();
	}

	@Override
	public boolean isDefaultRepository() {
		if (_dlFolder.getGroupId() == _dlFolder.getRepositoryId()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isEscapedModel() {
		return _escapedModel;
	}

	@Override
	public boolean isLocked() {
		return _dlFolder.isLocked();
	}

	@Override
	public boolean isMountPoint() {
		return _dlFolder.isMountPoint();
	}

	@Override
	public <T extends Capability> boolean isRepositoryCapabilityProvided(
		Class<T> capabilityClass) {

		Repository repository = _getRepository();

		return repository.isCapabilityProvided(capabilityClass);
	}

	@Override
	public boolean isRoot() {
		return _dlFolder.isRoot();
	}

	@Override
	public boolean isSupportsLocking() {
		return !isMountPoint();
	}

	@Override
	public boolean isSupportsMetadata() {
		return !isMountPoint();
	}

	@Override
	public boolean isSupportsMultipleUpload() {
		return !isMountPoint();
	}

	@Override
	public boolean isSupportsShortcuts() {
		return !isMountPoint();
	}

	@Override
	public boolean isSupportsSocial() {
		return !isMountPoint();
	}

	@Override
	public boolean isSupportsSubscribing() {
		return !isMountPoint();
	}

	@Override
	public void setCompanyId(long companyId) {
		_dlFolder.setCompanyId(companyId);
	}

	@Override
	public void setCreateDate(Date createDate) {
		_dlFolder.setCreateDate(createDate);
	}

	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		_dlFolder.setExternalReferenceCode(externalReferenceCode);
	}

	@Override
	public void setGroupId(long groupId) {
		_dlFolder.setGroupId(groupId);
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_dlFolder.setLastPublishDate(lastPublishDate);
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_dlFolder.setModifiedDate(modifiedDate);
	}

	public void setPrimaryKey(long primaryKey) {
		_dlFolder.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey((Long)primaryKeyObj);
	}

	@Override
	public void setUserId(long userId) {
		_dlFolder.setUserId(userId);
	}

	@Override
	public void setUserName(String userName) {
		_dlFolder.setUserName(userName);
	}

	@Override
	public void setUserUuid(String userUuid) {
		_dlFolder.setUserUuid(userUuid);
	}

	@Override
	public void setUuid(String uuid) {
		_dlFolder.setUuid(uuid);
	}

	@Override
	public Folder toEscapedModel() {
		if (isEscapedModel()) {
			return this;
		}

		return new LiferayFolder(_dlFolder.toEscapedModel(), true);
	}

	@Override
	public String toString() {
		return _dlFolder.toString();
	}

	@Override
	public Folder toUnescapedModel() {
		if (isEscapedModel()) {
			return new LiferayFolder(_dlFolder.toUnescapedModel(), true);
		}

		return this;
	}

	private Repository _getRepository() {
		try {
			return RepositoryProviderUtil.getRepository(getRepositoryId());
		}
		catch (PortalException portalException) {
			throw new SystemException(
				"Unable to get repository for folder " + getFolderId(),
				portalException);
		}
	}

	private final DLFolder _dlFolder;
	private final boolean _escapedModel;

}