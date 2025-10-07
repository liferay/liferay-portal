/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.repository.proxy;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryModelOperation;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.InputStream;
import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Mika Koivisto
 */
public class FileEntryProxyBean
	extends RepositoryModelProxyBean implements FileEntry {

	public FileEntryProxyBean(FileEntry fileEntry, ClassLoader classLoader) {
		super(classLoader);

		_fileEntry = fileEntry;
	}

	@Override
	public Object clone() {
		return newFileEntryProxyBean(_fileEntry);
	}

	@Override
	public boolean containsPermission(
			PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		return _fileEntry.containsPermission(permissionChecker, actionId);
	}

	@Override
	public void execute(RepositoryModelOperation repositoryModelOperation)
		throws PortalException {

		repositoryModelOperation.execute(this);
	}

	@Override
	public Map<String, Serializable> getAttributes() {
		return _fileEntry.getAttributes();
	}

	@Override
	public long getCompanyId() {
		return _fileEntry.getCompanyId();
	}

	@Override
	public InputStream getContentStream() throws PortalException {
		return _fileEntry.getContentStream();
	}

	@Override
	public InputStream getContentStream(String version) throws PortalException {
		return _fileEntry.getContentStream(version);
	}

	@Override
	public Date getCreateDate() {
		return _fileEntry.getCreateDate();
	}

	@Override
	public String getDescription() {
		return _fileEntry.getDescription();
	}

	@Override
	public Date getDisplayDate() {
		return _fileEntry.getDisplayDate();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return newProxyInstance(
			_fileEntry.getExpandoBridge(), _expandoBridgeProxyProviderFunction);
	}

	@Override
	public Date getExpirationDate() {
		return _fileEntry.getExpirationDate();
	}

	@Override
	public String getExtension() {
		return _fileEntry.getExtension();
	}

	@Override
	public long getFileEntryId() {
		return _fileEntry.getFileEntryId();
	}

	@Override
	public String getFileName() {
		return _fileEntry.getFileName();
	}

	@Override
	public List<FileShortcut> getFileShortcuts() {
		return _fileEntry.getFileShortcuts();
	}

	@Override
	public FileVersion getFileVersion() throws PortalException {
		return newFileVersionProxyBean(_fileEntry.getFileVersion());
	}

	@Override
	public FileVersion getFileVersion(String version) throws PortalException {
		return newFileVersionProxyBean(_fileEntry.getFileVersion(version));
	}

	@Override
	public List<FileVersion> getFileVersions(int status) {
		return toFileVersionProxyBeans(_fileEntry.getFileVersions(status));
	}

	@Override
	public List<FileVersion> getFileVersions(int status, int start, int end) {
		return toFileVersionProxyBeans(
			_fileEntry.getFileVersions(status, start, end));
	}

	@Override
	public int getFileVersionsCount(int status) {
		return _fileEntry.getFileVersionsCount(status);
	}

	@Override
	public Folder getFolder() {
		return newFolderProxyBean(_fileEntry.getFolder());
	}

	@Override
	public long getFolderId() {
		return _fileEntry.getFolderId();
	}

	@Override
	public long getGroupId() {
		return _fileEntry.getGroupId();
	}

	@Override
	public String getIcon() {
		return _fileEntry.getIcon();
	}

	@Override
	public String getIconCssClass() {
		return _fileEntry.getIconCssClass();
	}

	@Override
	public Date getLastPublishDate() {
		return _fileEntry.getLastPublishDate();
	}

	@Override
	public FileVersion getLatestFileVersion() throws PortalException {
		FileVersion fileVersion = _fileEntry.getLatestFileVersion();

		return newFileVersionProxyBean(fileVersion);
	}

	@Override
	public FileVersion getLatestFileVersion(boolean trusted)
		throws PortalException {

		FileVersion fileVersion = _fileEntry.getLatestFileVersion(trusted);

		return newFileVersionProxyBean(fileVersion);
	}

	@Override
	public Lock getLock() {
		return newProxyInstance(
			_fileEntry.getLock(), _lockProxyProviderFunction);
	}

	@Override
	public String getMimeType() {
		return _fileEntry.getMimeType();
	}

	@Override
	public String getMimeType(String version) {
		return _fileEntry.getMimeType(version);
	}

	@Override
	public Object getModel() {
		return _fileEntry.getModel();
	}

	@Override
	public Class<?> getModelClass() {
		return _fileEntry.getModelClass();
	}

	@Override
	public String getModelClassName() {
		return _fileEntry.getModelClassName();
	}

	@Override
	public Date getModifiedDate() {
		return _fileEntry.getModifiedDate();
	}

	@Override
	public long getPrimaryKey() {
		return _fileEntry.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _fileEntry.getPrimaryKeyObj();
	}

	@Override
	public long getReadCount() {
		return _fileEntry.getReadCount();
	}

	@Override
	public <T extends Capability> T getRepositoryCapability(
		Class<T> capabilityClass) {

		return _fileEntry.getRepositoryCapability(capabilityClass);
	}

	@Override
	public long getRepositoryId() {
		return _fileEntry.getRepositoryId();
	}

	@Override
	public Date getReviewDate() {
		return _fileEntry.getReviewDate();
	}

	@Override
	public long getSize() {
		return _fileEntry.getSize();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return _fileEntry.getStagedModelType();
	}

	@Override
	public String getTitle() {
		return _fileEntry.getTitle();
	}

	@Override
	public long getUserId() {
		return _fileEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _fileEntry.getUserName();
	}

	@Override
	public String getUserUuid() {
		return _fileEntry.getUserUuid();
	}

	@Override
	public String getUuid() {
		return _fileEntry.getUuid();
	}

	@Override
	public String getVersion() {
		return _fileEntry.getVersion();
	}

	@Override
	public boolean hasLock() {
		return _fileEntry.hasLock();
	}

	@Override
	public boolean isCheckedOut() {
		return _fileEntry.isCheckedOut();
	}

	@Override
	public boolean isDefaultRepository() {
		return _fileEntry.isDefaultRepository();
	}

	@Override
	public boolean isEscapedModel() {
		return _fileEntry.isEscapedModel();
	}

	@Override
	public boolean isInTrash() {
		return _fileEntry.isInTrash();
	}

	@Override
	public boolean isInTrashContainer() {
		return _fileEntry.isInTrashContainer();
	}

	@Override
	public boolean isManualCheckInRequired() {
		return _fileEntry.isManualCheckInRequired();
	}

	@Override
	public <T extends Capability> boolean isRepositoryCapabilityProvided(
		Class<T> capabilityClass) {

		return _fileEntry.isRepositoryCapabilityProvided(capabilityClass);
	}

	@Override
	public boolean isSupportsLocking() {
		return _fileEntry.isSupportsLocking();
	}

	@Override
	public boolean isSupportsMetadata() {
		return _fileEntry.isSupportsMetadata();
	}

	@Override
	public boolean isSupportsSocial() {
		return _fileEntry.isSupportsSocial();
	}

	@Override
	public void setCompanyId(long companyId) {
		_fileEntry.setCompanyId(companyId);
	}

	@Override
	public void setCreateDate(Date createDate) {
		_fileEntry.setCreateDate(createDate);
	}

	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		_fileEntry.setExternalReferenceCode(externalReferenceCode);
	}

	@Override
	public void setGroupId(long groupId) {
		_fileEntry.setGroupId(groupId);
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_fileEntry.setLastPublishDate(lastPublishDate);
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_fileEntry.setModifiedDate(modifiedDate);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_fileEntry.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public void setUserId(long userId) {
		_fileEntry.setUserId(userId);
	}

	@Override
	public void setUserName(String userName) {
		_fileEntry.setUserName(userName);
	}

	@Override
	public void setUserUuid(String userUuid) {
		_fileEntry.setUserUuid(userUuid);
	}

	@Override
	public void setUuid(String uuid) {
		_fileEntry.setUuid(uuid);
	}

	@Override
	public FileEntry toEscapedModel() {
		FileEntry fileEntry = _fileEntry.toEscapedModel();

		return newFileEntryProxyBean(fileEntry);
	}

	@Override
	public FileEntry toUnescapedModel() {
		FileEntry fileEntry = _fileEntry.toUnescapedModel();

		return newFileEntryProxyBean(fileEntry);
	}

	private static final Function<InvocationHandler, ExpandoBridge>
		_expandoBridgeProxyProviderFunction =
			ProxyUtil.getProxyProviderFunction(ExpandoBridge.class);
	private static final Function<InvocationHandler, Lock>
		_lockProxyProviderFunction = ProxyUtil.getProxyProviderFunction(
			Lock.class);

	private final FileEntry _fileEntry;

}