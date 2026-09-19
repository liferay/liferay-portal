/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.repository.external.model;

import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.document.library.repository.external.ExtRepositoryAdapter;
import com.liferay.document.library.repository.external.ExtRepositoryFileVersion;
import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.RepositoryModelOperation;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.io.InputStream;

import java.util.Date;

/**
 * @author Iván Zaera
 * @author Sergio González
 */
public class ExtRepositoryFileVersionAdapter
	extends ExtRepositoryModelAdapter<FileVersion> implements FileVersion {

	public ExtRepositoryFileVersionAdapter(
		ExtRepositoryAdapter extRepositoryAdapter, long extRepositoryObjectId,
		String uuid,
		ExtRepositoryFileEntryAdapter extRepositoryFileEntryAdapter,
		ExtRepositoryFileVersion extRepositoryFileVersion) {

		super(
			extRepositoryAdapter, extRepositoryObjectId, uuid,
			extRepositoryFileVersion);

		_extRepositoryFileEntryAdapter = extRepositoryFileEntryAdapter;
		_extRepositoryFileVersion = extRepositoryFileVersion;
	}

	@Override
	public void execute(RepositoryModelOperation repositoryModelOperation)
		throws PortalException {

		repositoryModelOperation.execute(this);
	}

	@Override
	public String getChangeLog() {
		return _extRepositoryFileVersion.getChangeLog();
	}

	@Override
	public InputStream getContentStream(boolean incrementCounter)
		throws PortalException {

		ExtRepositoryAdapter extRepositoryAdapter = getRepository();

		return extRepositoryAdapter.getContentStream(this);
	}

	@Override
	public Date getDisplayDate() {
		return null;
	}

	@Override
	public Date getExpirationDate() {
		return null;
	}

	@Override
	public ExtRepositoryFileVersion getExtRepositoryModel() {
		return _extRepositoryFileVersion;
	}

	@Override
	public String getExtension() {
		return _extRepositoryFileEntryAdapter.getExtension();
	}

	@Override
	public String getExtraSettings() {
		return null;
	}

	@Override
	@SuppressWarnings("unused")
	public FileEntry getFileEntry() throws PortalException {
		return _extRepositoryFileEntryAdapter;
	}

	@Override
	public long getFileEntryId() {
		return _extRepositoryFileEntryAdapter.getFileEntryId();
	}

	@Override
	public String getFileName() {
		return DLUtil.getSanitizedFileName(getTitle(), getExtension());
	}

	@Override
	public long getFileVersionId() {
		return getPrimaryKey();
	}

	@Override
	public String getIcon() {
		return DLUtil.getFileIcon(getExtension());
	}

	@Override
	public String getMimeType() {
		String mimeType = _extRepositoryFileVersion.getMimeType();

		if (Validator.isNull(mimeType)) {
			mimeType = MimeTypesUtil.getContentType(getTitle());
		}

		if (Validator.isNotNull(mimeType)) {
			return mimeType;
		}

		return StringPool.BLANK;
	}

	@Override
	public Class<?> getModelClass() {
		return FileVersion.class;
	}

	@Override
	public Date getModifiedDate() {
		return getCreateDate();
	}

	@Override
	public Date getReviewDate() {
		return null;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(FileVersion.class);
	}

	@Override
	public int getStatus() {
		return WorkflowConstants.STATUS_APPROVED;
	}

	@Override
	public long getStatusByUserId() {
		return getUserId();
	}

	@Override
	public String getStatusByUserName() {
		return getUserName();
	}

	@Override
	public String getStatusByUserUuid() {
		return getUserUuid();
	}

	@Override
	public Date getStatusDate() {
		return getCreateDate();
	}

	@Override
	public String getTitle() {
		return _extRepositoryFileEntryAdapter.getTitle();
	}

	@Override
	public String getVersion() {
		return _extRepositoryFileVersion.getVersion();
	}

	@Override
	public boolean isApproved() {
		return false;
	}

	@Override
	public boolean isDraft() {
		return false;
	}

	@Override
	public boolean isExpired() {
		return false;
	}

	@Override
	public boolean isPending() {
		return false;
	}

	@Override
	public boolean isScheduled() {
		return false;
	}

	private final ExtRepositoryFileEntryAdapter _extRepositoryFileEntryAdapter;
	private final ExtRepositoryFileVersion _extRepositoryFileVersion;

}