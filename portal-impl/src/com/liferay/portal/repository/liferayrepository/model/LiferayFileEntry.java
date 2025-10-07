/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.liferayrepository.model;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileVersionLocalServiceUtil;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.capabilities.Capability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.repository.model.RepositoryModelOperation;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionRegistryUtil;
import com.liferay.portal.kernel.trash.helper.TrashHelper;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portlet.documentlibrary.util.RepositoryModelUtil;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Chow
 */
public class LiferayFileEntry extends LiferayModel implements FileEntry {

	public LiferayFileEntry(DLFileEntry dlFileEntry) {
		this(dlFileEntry, dlFileEntry.isEscapedModel());
	}

	public LiferayFileEntry(DLFileEntry dlFileEntry, boolean escapedModel) {
		_dlFileEntry = dlFileEntry;
		_escapedModel = escapedModel;
	}

	@Override
	public Object clone() {
		return new LiferayFileEntry(_dlFileEntry);
	}

	@Override
	public boolean containsPermission(
			PermissionChecker permissionChecker, String actionId)
		throws PortalException {

		ModelResourcePermission<DLFileEntry>
			dlFileEntryModelResourcePermission =
				ModelResourcePermissionRegistryUtil.getModelResourcePermission(
					DLFileEntry.class.getName());

		return dlFileEntryModelResourcePermission.contains(
			permissionChecker, _dlFileEntry, actionId);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LiferayFileEntry)) {
			return false;
		}

		LiferayFileEntry liferayFileEntry = (LiferayFileEntry)object;

		return Objects.equals(_dlFileEntry, liferayFileEntry._dlFileEntry);
	}

	@Override
	public void execute(RepositoryModelOperation repositoryModelOperation)
		throws PortalException {

		repositoryModelOperation.execute(this);
	}

	@Override
	public Map<String, Serializable> getAttributes() {
		ExpandoBridge expandoBridge = getExpandoBridge();

		return expandoBridge.getAttributes();
	}

	public FileVersion getCachedFileVersion() {
		if (_dlFileVersion == null) {
			return null;
		}

		return new LiferayFileVersion(_dlFileVersion);
	}

	@Override
	public long getCompanyId() {
		return _dlFileEntry.getCompanyId();
	}

	@Override
	public InputStream getContentStream() throws PortalException {
		InputStream inputStream = _dlFileEntry.getContentStream();

		try {
			DLAppHelperLocalServiceUtil.getFileAsStream(
				PrincipalThreadLocal.getUserId(), this, true);
		}
		catch (Exception exception) {
			_log.error("Unable to get content stream", exception);
		}

		return inputStream;
	}

	@Override
	public InputStream getContentStream(String version) throws PortalException {
		InputStream inputStream = _dlFileEntry.getContentStream(version);

		try {
			DLAppHelperLocalServiceUtil.getFileAsStream(
				PrincipalThreadLocal.getUserId(), this, true);
		}
		catch (Exception exception) {
			_log.error("Error getting document stream", exception);
		}

		return inputStream;
	}

	@Override
	public Date getCreateDate() {
		return _dlFileEntry.getCreateDate();
	}

	@Override
	public long getCtCollectionId() {
		return _dlFileEntry.getCtCollectionId();
	}

	@Override
	public String getDescription() {
		return _dlFileEntry.getDescription();
	}

	@Override
	public Date getDisplayDate() {
		return _dlFileEntry.getDisplayDate();
	}

	public DLFileEntry getDLFileEntry() {
		return _dlFileEntry;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _dlFileEntry.getExpandoBridge();
	}

	@Override
	public Date getExpirationDate() {
		return _dlFileEntry.getExpirationDate();
	}

	@Override
	public String getExtension() {
		return _dlFileEntry.getExtension();
	}

	@Override
	public String getExternalReferenceCode() {
		return _dlFileEntry.getExternalReferenceCode();
	}

	@Override
	public long getFileEntryId() {
		return _dlFileEntry.getFileEntryId();
	}

	@Override
	public String getFileName() {
		return _dlFileEntry.getFileName();
	}

	@Override
	public List<FileShortcut> getFileShortcuts() {
		return RepositoryModelUtil.toFileShortcuts(
			_dlFileEntry.getFileShortcuts());
	}

	@Override
	public FileVersion getFileVersion() throws PortalException {
		DLFileVersion dlFileVersion = _dlFileVersion;

		if (dlFileVersion == null) {
			dlFileVersion = _dlFileEntry.getFileVersion();
		}

		return new LiferayFileVersion(dlFileVersion);
	}

	@Override
	public FileVersion getFileVersion(String version) throws PortalException {
		return new LiferayFileVersion(_dlFileEntry.getFileVersion(version));
	}

	@Override
	public List<FileVersion> getFileVersions(int status) {
		return RepositoryModelUtil.toFileVersions(
			_dlFileEntry.getFileVersions(status));
	}

	@Override
	public List<FileVersion> getFileVersions(int status, int start, int end) {
		return RepositoryModelUtil.toFileVersions(
			_dlFileEntry.getFileVersions(status, start, end));
	}

	@Override
	public int getFileVersionsCount(int status) {
		return _dlFileEntry.getFileVersionsCount(status);
	}

	@Override
	public Folder getFolder() {
		try {
			return new LiferayFolder(_dlFileEntry.getFolder());
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	@Override
	public long getFolderId() {
		return _dlFileEntry.getFolderId();
	}

	@Override
	public long getGroupId() {
		return _dlFileEntry.getGroupId();
	}

	@Override
	public String getIcon() {
		return _dlFileEntry.getIcon();
	}

	@Override
	public String getIconCssClass() {
		return _dlFileEntry.getIconCssClass();
	}

	@Override
	public Date getLastPublishDate() {
		return _dlFileEntry.getLastPublishDate();
	}

	@Override
	public FileVersion getLatestFileVersion() throws PortalException {
		return getLatestFileVersion(false);
	}

	@Override
	public FileVersion getLatestFileVersion(boolean trusted)
		throws PortalException {

		return new LiferayFileVersion(
			_dlFileEntry.getLatestFileVersion(trusted));
	}

	@Override
	public Lock getLock() {
		return _dlFileEntry.getLock();
	}

	@Override
	public String getMimeType() {
		return _dlFileEntry.getMimeType();
	}

	@Override
	public String getMimeType(String version) {
		try {
			DLFileVersion dlFileVersion =
				DLFileVersionLocalServiceUtil.getFileVersion(
					_dlFileEntry.getFileEntryId(), version);

			return dlFileVersion.getMimeType();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return ContentTypes.APPLICATION_OCTET_STREAM;
		}
	}

	@Override
	public Object getModel() {
		return _dlFileEntry;
	}

	@Override
	public Class<?> getModelClass() {
		return LiferayFileEntry.class;
	}

	@Override
	public String getModelClassName() {
		return LiferayFileEntry.class.getName();
	}

	@Override
	public Date getModifiedDate() {
		return _dlFileEntry.getModifiedDate();
	}

	@Override
	public long getPrimaryKey() {
		return _dlFileEntry.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return getPrimaryKey();
	}

	@Override
	public long getReadCount() {
		return _dlFileEntry.getReadCount();
	}

	@Override
	public <T extends Capability> T getRepositoryCapability(
		Class<T> capabilityClass) {

		Repository repository = getRepository();

		return repository.getCapability(capabilityClass);
	}

	@Override
	public long getRepositoryId() {
		return _dlFileEntry.getRepositoryId();
	}

	@Override
	public Date getReviewDate() {
		return _dlFileEntry.getReviewDate();
	}

	@Override
	public long getSize() {
		return _dlFileEntry.getSize();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(DLFileEntryConstants.getClassName());
	}

	@Override
	public String getTitle() {
		return _dlFileEntry.getTitle();
	}

	@Override
	public long getUserId() {
		return _dlFileEntry.getUserId();
	}

	@Override
	public String getUserName() {
		return _dlFileEntry.getUserName();
	}

	@Override
	public String getUserUuid() {
		return _dlFileEntry.getUserUuid();
	}

	@Override
	public String getUuid() {
		return _dlFileEntry.getUuid();
	}

	@Override
	public String getVersion() {
		return _dlFileEntry.getVersion();
	}

	@Override
	public int hashCode() {
		return _dlFileEntry.hashCode();
	}

	@Override
	public boolean hasLock() {
		return _dlFileEntry.hasLock();
	}

	@Override
	public boolean isCheckedOut() {
		return _dlFileEntry.isCheckedOut();
	}

	@Override
	public boolean isDefaultRepository() {
		if (_dlFileEntry.getGroupId() == _dlFileEntry.getRepositoryId()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isEscapedModel() {
		return _escapedModel;
	}

	@Override
	public boolean isInTrash() {
		return _dlFileEntry.isInTrash();
	}

	@Override
	public boolean isInTrashContainer() {
		try {
			TrashHelper trashHelper = _trashHelperSnapshot.get();

			return trashHelper.isInTrashContainer(_dlFileEntry);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return false;
		}
	}

	@Override
	public boolean isManualCheckInRequired() {
		return _dlFileEntry.isManualCheckInRequired();
	}

	@Override
	public <T extends Capability> boolean isRepositoryCapabilityProvided(
		Class<T> capabilityClass) {

		Repository repository = getRepository();

		return repository.isCapabilityProvided(capabilityClass);
	}

	@Override
	public boolean isSupportsLocking() {
		return true;
	}

	@Override
	public boolean isSupportsMetadata() {
		return true;
	}

	@Override
	public boolean isSupportsSocial() {
		return true;
	}

	public void setCachedFileVersion(FileVersion fileVersion) {
		_dlFileVersion = (DLFileVersion)fileVersion.getModel();
	}

	@Override
	public void setCompanyId(long companyId) {
		_dlFileEntry.setCompanyId(companyId);
	}

	@Override
	public void setCreateDate(Date createDate) {
		_dlFileEntry.setCreateDate(createDate);
	}

	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		_dlFileEntry.setExternalReferenceCode(externalReferenceCode);
	}

	@Override
	public void setGroupId(long groupId) {
		_dlFileEntry.setGroupId(groupId);
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_dlFileEntry.setLastPublishDate(lastPublishDate);
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_dlFileEntry.setModifiedDate(modifiedDate);
	}

	public void setPrimaryKey(long primaryKey) {
		_dlFileEntry.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey((Long)primaryKeyObj);
	}

	@Override
	public void setUserId(long userId) {
		_dlFileEntry.setUserId(userId);
	}

	@Override
	public void setUserName(String userName) {
		_dlFileEntry.setUserName(userName);
	}

	@Override
	public void setUserUuid(String userUuid) {
		_dlFileEntry.setUserUuid(userUuid);
	}

	@Override
	public void setUuid(String uuid) {
		_dlFileEntry.setUuid(uuid);
	}

	@Override
	public FileEntry toEscapedModel() {
		if (isEscapedModel()) {
			return this;
		}

		return new LiferayFileEntry(_dlFileEntry.toEscapedModel(), true);
	}

	@Override
	public String toString() {
		return _dlFileEntry.toString();
	}

	@Override
	public FileEntry toUnescapedModel() {
		if (isEscapedModel()) {
			return new LiferayFileEntry(_dlFileEntry.toUnescapedModel(), true);
		}

		return this;
	}

	protected Repository getRepository() {
		try {
			return RepositoryProviderUtil.getRepository(getRepositoryId());
		}
		catch (PortalException portalException) {
			throw new SystemException(
				"Unable to get repository for file entry " + getFileEntryId(),
				portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayFileEntry.class);

	private static final Snapshot<TrashHelper> _trashHelperSnapshot =
		new Snapshot<>(LiferayFileEntry.class, TrashHelper.class);

	private final DLFileEntry _dlFileEntry;
	private DLFileVersion _dlFileVersion;
	private final boolean _escapedModel;

}