/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.repository.liferayrepository.model;

import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceUtil;
import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.RepositoryModelOperation;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.InputStream;
import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Chow
 */
public class LiferayFileVersion extends LiferayModel implements FileVersion {

	public LiferayFileVersion(DLFileVersion dlFileVersion) {
		this(dlFileVersion, dlFileVersion.isEscapedModel());
	}

	public LiferayFileVersion(
		DLFileVersion dlFileVersion, boolean escapedModel) {

		_dlFileVersion = dlFileVersion;
		_escapedModel = escapedModel;
	}

	@Override
	public Object clone() {
		return new LiferayFileVersion(_dlFileVersion);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LiferayFileVersion)) {
			return false;
		}

		LiferayFileVersion liferayFileVersion = (LiferayFileVersion)object;

		return Objects.equals(
			_dlFileVersion, liferayFileVersion._dlFileVersion);
	}

	@Override
	public void execute(RepositoryModelOperation repositoryModelOperation)
		throws PortalException {

		repositoryModelOperation.execute(this);
	}

	@Override
	public Map<String, Serializable> getAttributes() {
		ExpandoBridge expandoBridge = _dlFileVersion.getExpandoBridge();

		return expandoBridge.getAttributes();
	}

	public long getCTCollectionId() {
		return _dlFileVersion.getCtCollectionId();
	}

	@Override
	public String getChangeLog() {
		return _dlFileVersion.getChangeLog();
	}

	@Override
	public long getCompanyId() {
		return _dlFileVersion.getCompanyId();
	}

	@Override
	public InputStream getContentStream(boolean incrementCounter)
		throws PortalException {

		InputStream inputStream = _dlFileVersion.getContentStream(
			incrementCounter);

		try {
			DLAppHelperLocalServiceUtil.getFileAsStream(
				PrincipalThreadLocal.getUserId(), getFileEntry(),
				incrementCounter);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return inputStream;
	}

	@Override
	public Date getCreateDate() {
		return _dlFileVersion.getCreateDate();
	}

	@Override
	public long getCtCollectionId() {
		return _dlFileVersion.getCtCollectionId();
	}

	@Override
	public String getDescription() {
		return _dlFileVersion.getDescription();
	}

	@Override
	public Date getDisplayDate() {
		return _dlFileVersion.getDisplayDate();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return _dlFileVersion.getExpandoBridge();
	}

	@Override
	public Date getExpirationDate() {
		return _dlFileVersion.getExpirationDate();
	}

	@Override
	public String getExtension() {
		return _dlFileVersion.getExtension();
	}

	@Override
	public String getExtraSettings() {
		return _dlFileVersion.getExtraSettings();
	}

	@Override
	public FileEntry getFileEntry() throws PortalException {
		return new LiferayFileEntry(_dlFileVersion.getFileEntry());
	}

	@Override
	public long getFileEntryId() {
		return _dlFileVersion.getFileEntryId();
	}

	@Override
	public String getFileName() {
		return _dlFileVersion.getFileName();
	}

	@Override
	public long getFileVersionId() {
		return _dlFileVersion.getFileVersionId();
	}

	@Override
	public long getGroupId() {
		return _dlFileVersion.getGroupId();
	}

	@Override
	public String getIcon() {
		return _dlFileVersion.getIcon();
	}

	@Override
	public Date getLastPublishDate() {
		return _dlFileVersion.getLastPublishDate();
	}

	@Override
	public String getMimeType() {
		return _dlFileVersion.getMimeType();
	}

	@Override
	public Object getModel() {
		return _dlFileVersion;
	}

	@Override
	public Class<?> getModelClass() {
		return LiferayFileVersion.class;
	}

	@Override
	public String getModelClassName() {
		return LiferayFileVersion.class.getName();
	}

	@Override
	public Date getModifiedDate() {
		return _dlFileVersion.getModifiedDate();
	}

	@Override
	public long getPrimaryKey() {
		return _dlFileVersion.getPrimaryKey();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return getPrimaryKey();
	}

	@Override
	public long getRepositoryId() {
		return _dlFileVersion.getRepositoryId();
	}

	@Override
	public Date getReviewDate() {
		return _dlFileVersion.getReviewDate();
	}

	@Override
	public long getSize() {
		return _dlFileVersion.getSize();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(FileVersion.class);
	}

	@Override
	public int getStatus() {
		return _dlFileVersion.getStatus();
	}

	@Override
	public long getStatusByUserId() {
		return _dlFileVersion.getStatusByUserId();
	}

	@Override
	public String getStatusByUserName() {
		return _dlFileVersion.getStatusByUserName();
	}

	@Override
	public String getStatusByUserUuid() {
		return _dlFileVersion.getStatusByUserUuid();
	}

	@Override
	public Date getStatusDate() {
		return _dlFileVersion.getStatusDate();
	}

	@Override
	public String getTitle() {
		return _dlFileVersion.getTitle();
	}

	@Override
	public long getUserId() {
		return _dlFileVersion.getUserId();
	}

	@Override
	public String getUserName() {
		return _dlFileVersion.getUserName();
	}

	@Override
	public String getUserUuid() {
		return _dlFileVersion.getUserUuid();
	}

	@Override
	public String getUuid() {
		return _dlFileVersion.getUuid();
	}

	@Override
	public String getVersion() {
		return _dlFileVersion.getVersion();
	}

	@Override
	public int hashCode() {
		return _dlFileVersion.hashCode();
	}

	@Override
	public boolean isApproved() {
		return _dlFileVersion.isApproved();
	}

	@Override
	public boolean isDefaultRepository() {
		if (_dlFileVersion.getGroupId() == _dlFileVersion.getRepositoryId()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isDraft() {
		return _dlFileVersion.isDraft();
	}

	@Override
	public boolean isEscapedModel() {
		return _escapedModel;
	}

	@Override
	public boolean isExpired() {
		return _dlFileVersion.isExpired();
	}

	@Override
	public boolean isPending() {
		return _dlFileVersion.isPending();
	}

	@Override
	public boolean isScheduled() {
		if (getStatus() == WorkflowConstants.STATUS_SCHEDULED) {
			return true;
		}

		return false;
	}

	@Override
	public void setCompanyId(long companyId) {
		_dlFileVersion.setCompanyId(companyId);
	}

	@Override
	public void setCreateDate(Date createDate) {
		_dlFileVersion.setCreateDate(createDate);
	}

	@Override
	public void setGroupId(long groupId) {
		_dlFileVersion.setGroupId(groupId);
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_dlFileVersion.setLastPublishDate(lastPublishDate);
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
	}

	public void setPrimaryKey(long primaryKey) {
		_dlFileVersion.setPrimaryKey(primaryKey);
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey((Long)primaryKeyObj);
	}

	@Override
	public void setUserId(long userId) {
		_dlFileVersion.setUserId(userId);
	}

	@Override
	public void setUserName(String userName) {
		_dlFileVersion.setUserName(userName);
	}

	@Override
	public void setUserUuid(String userUuid) {
		_dlFileVersion.setUserUuid(userUuid);
	}

	@Override
	public void setUuid(String uuid) {
		_dlFileVersion.setUuid(uuid);
	}

	@Override
	public FileVersion toEscapedModel() {
		if (isEscapedModel()) {
			return this;
		}

		return new LiferayFileVersion(_dlFileVersion.toEscapedModel(), true);
	}

	@Override
	public String toString() {
		return _dlFileVersion.toString();
	}

	@Override
	public FileVersion toUnescapedModel() {
		if (isEscapedModel()) {
			return new LiferayFileVersion(
				_dlFileVersion.toUnescapedModel(), true);
		}

		return this;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayFileVersion.class);

	private final DLFileVersion _dlFileVersion;
	private final boolean _escapedModel;

}