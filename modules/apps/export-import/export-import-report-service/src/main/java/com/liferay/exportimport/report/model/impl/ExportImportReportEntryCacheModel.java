/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.model.impl;

import com.liferay.exportimport.report.model.ExportImportReportEntry;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing ExportImportReportEntry in entity cache.
 *
 * @author Carlos Correa
 * @generated
 */
public class ExportImportReportEntryCacheModel
	implements CacheModel<ExportImportReportEntry>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExportImportReportEntryCacheModel)) {
			return false;
		}

		ExportImportReportEntryCacheModel exportImportReportEntryCacheModel =
			(ExportImportReportEntryCacheModel)object;

		if ((exportImportReportEntryId ==
				exportImportReportEntryCacheModel.exportImportReportEntryId) &&
			(mvccVersion == exportImportReportEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, exportImportReportEntryId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(37);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", exportImportReportEntryId=");
		sb.append(exportImportReportEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", classExternalReferenceCode=");
		sb.append(classExternalReferenceCode);
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", classPK=");
		sb.append(classPK);
		sb.append(", exportImportConfigurationId=");
		sb.append(exportImportConfigurationId);
		sb.append(", errorMessage=");
		sb.append(errorMessage);
		sb.append(", errorStacktrace=");
		sb.append(errorStacktrace);
		sb.append(", modelName=");
		sb.append(modelName);
		sb.append(", origin=");
		sb.append(origin);
		sb.append(", scope=");
		sb.append(scope);
		sb.append(", scopeKey=");
		sb.append(scopeKey);
		sb.append(", type=");
		sb.append(type);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ExportImportReportEntry toEntityModel() {
		ExportImportReportEntryImpl exportImportReportEntryImpl =
			new ExportImportReportEntryImpl();

		exportImportReportEntryImpl.setMvccVersion(mvccVersion);
		exportImportReportEntryImpl.setExportImportReportEntryId(
			exportImportReportEntryId);
		exportImportReportEntryImpl.setGroupId(groupId);
		exportImportReportEntryImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			exportImportReportEntryImpl.setCreateDate(null);
		}
		else {
			exportImportReportEntryImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			exportImportReportEntryImpl.setModifiedDate(null);
		}
		else {
			exportImportReportEntryImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (classExternalReferenceCode == null) {
			exportImportReportEntryImpl.setClassExternalReferenceCode("");
		}
		else {
			exportImportReportEntryImpl.setClassExternalReferenceCode(
				classExternalReferenceCode);
		}

		exportImportReportEntryImpl.setClassNameId(classNameId);
		exportImportReportEntryImpl.setClassPK(classPK);
		exportImportReportEntryImpl.setExportImportConfigurationId(
			exportImportConfigurationId);
		exportImportReportEntryImpl.setErrorMessage(errorMessage);
		exportImportReportEntryImpl.setErrorStacktrace(errorStacktrace);

		if (modelName == null) {
			exportImportReportEntryImpl.setModelName("");
		}
		else {
			exportImportReportEntryImpl.setModelName(modelName);
		}

		exportImportReportEntryImpl.setOrigin(origin);

		if (scope == null) {
			exportImportReportEntryImpl.setScope("");
		}
		else {
			exportImportReportEntryImpl.setScope(scope);
		}

		if (scopeKey == null) {
			exportImportReportEntryImpl.setScopeKey("");
		}
		else {
			exportImportReportEntryImpl.setScopeKey(scopeKey);
		}

		exportImportReportEntryImpl.setType(type);
		exportImportReportEntryImpl.setStatus(status);

		exportImportReportEntryImpl.resetOriginalValues();

		return exportImportReportEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		exportImportReportEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		classExternalReferenceCode = objectInput.readUTF();

		classNameId = objectInput.readLong();

		classPK = objectInput.readLong();

		exportImportConfigurationId = objectInput.readLong();
		errorMessage = (String)objectInput.readObject();
		errorStacktrace = (String)objectInput.readObject();
		modelName = objectInput.readUTF();

		origin = objectInput.readInt();
		scope = objectInput.readUTF();
		scopeKey = objectInput.readUTF();

		type = objectInput.readInt();

		status = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(exportImportReportEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (classExternalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(classExternalReferenceCode);
		}

		objectOutput.writeLong(classNameId);

		objectOutput.writeLong(classPK);

		objectOutput.writeLong(exportImportConfigurationId);

		if (errorMessage == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(errorMessage);
		}

		if (errorStacktrace == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(errorStacktrace);
		}

		if (modelName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(modelName);
		}

		objectOutput.writeInt(origin);

		if (scope == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(scope);
		}

		if (scopeKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(scopeKey);
		}

		objectOutput.writeInt(type);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public long exportImportReportEntryId;
	public long groupId;
	public long companyId;
	public long createDate;
	public long modifiedDate;
	public String classExternalReferenceCode;
	public long classNameId;
	public long classPK;
	public long exportImportConfigurationId;
	public String errorMessage;
	public String errorStacktrace;
	public String modelName;
	public int origin;
	public String scope;
	public String scopeKey;
	public int type;
	public int status;

}