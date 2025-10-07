/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.report.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ExportImportReportEntry}.
 * </p>
 *
 * @author Carlos Correa
 * @see ExportImportReportEntry
 * @generated
 */
public class ExportImportReportEntryWrapper
	extends BaseModelWrapper<ExportImportReportEntry>
	implements ExportImportReportEntry, ModelWrapper<ExportImportReportEntry> {

	public ExportImportReportEntryWrapper(
		ExportImportReportEntry exportImportReportEntry) {

		super(exportImportReportEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put(
			"exportImportReportEntryId", getExportImportReportEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put(
			"classExternalReferenceCode", getClassExternalReferenceCode());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put(
			"exportImportConfigurationId", getExportImportConfigurationId());
		attributes.put("errorMessage", getErrorMessage());
		attributes.put("errorStacktrace", getErrorStacktrace());
		attributes.put("modelName", getModelName());
		attributes.put("origin", getOrigin());
		attributes.put("scope", getScope());
		attributes.put("scopeKey", getScopeKey());
		attributes.put("type", getType());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long exportImportReportEntryId = (Long)attributes.get(
			"exportImportReportEntryId");

		if (exportImportReportEntryId != null) {
			setExportImportReportEntryId(exportImportReportEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		String classExternalReferenceCode = (String)attributes.get(
			"classExternalReferenceCode");

		if (classExternalReferenceCode != null) {
			setClassExternalReferenceCode(classExternalReferenceCode);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		Long exportImportConfigurationId = (Long)attributes.get(
			"exportImportConfigurationId");

		if (exportImportConfigurationId != null) {
			setExportImportConfigurationId(exportImportConfigurationId);
		}

		String errorMessage = (String)attributes.get("errorMessage");

		if (errorMessage != null) {
			setErrorMessage(errorMessage);
		}

		String errorStacktrace = (String)attributes.get("errorStacktrace");

		if (errorStacktrace != null) {
			setErrorStacktrace(errorStacktrace);
		}

		String modelName = (String)attributes.get("modelName");

		if (modelName != null) {
			setModelName(modelName);
		}

		Integer origin = (Integer)attributes.get("origin");

		if (origin != null) {
			setOrigin(origin);
		}

		String scope = (String)attributes.get("scope");

		if (scope != null) {
			setScope(scope);
		}

		String scopeKey = (String)attributes.get("scopeKey");

		if (scopeKey != null) {
			setScopeKey(scopeKey);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public ExportImportReportEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the class external reference code of this export import report entry.
	 *
	 * @return the class external reference code of this export import report entry
	 */
	@Override
	public String getClassExternalReferenceCode() {
		return model.getClassExternalReferenceCode();
	}

	/**
	 * Returns the fully qualified class name of this export import report entry.
	 *
	 * @return the fully qualified class name of this export import report entry
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this export import report entry.
	 *
	 * @return the class name ID of this export import report entry
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this export import report entry.
	 *
	 * @return the class pk of this export import report entry
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the company ID of this export import report entry.
	 *
	 * @return the company ID of this export import report entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this export import report entry.
	 *
	 * @return the create date of this export import report entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the error message of this export import report entry.
	 *
	 * @return the error message of this export import report entry
	 */
	@Override
	public String getErrorMessage() {
		return model.getErrorMessage();
	}

	/**
	 * Returns the error stacktrace of this export import report entry.
	 *
	 * @return the error stacktrace of this export import report entry
	 */
	@Override
	public String getErrorStacktrace() {
		return model.getErrorStacktrace();
	}

	/**
	 * Returns the export import configuration ID of this export import report entry.
	 *
	 * @return the export import configuration ID of this export import report entry
	 */
	@Override
	public long getExportImportConfigurationId() {
		return model.getExportImportConfigurationId();
	}

	/**
	 * Returns the export import report entry ID of this export import report entry.
	 *
	 * @return the export import report entry ID of this export import report entry
	 */
	@Override
	public long getExportImportReportEntryId() {
		return model.getExportImportReportEntryId();
	}

	/**
	 * Returns the group ID of this export import report entry.
	 *
	 * @return the group ID of this export import report entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the model name of this export import report entry.
	 *
	 * @return the model name of this export import report entry
	 */
	@Override
	public String getModelName() {
		return model.getModelName();
	}

	/**
	 * Returns the modified date of this export import report entry.
	 *
	 * @return the modified date of this export import report entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this export import report entry.
	 *
	 * @return the mvcc version of this export import report entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the origin of this export import report entry.
	 *
	 * @return the origin of this export import report entry
	 */
	@Override
	public int getOrigin() {
		return model.getOrigin();
	}

	/**
	 * Returns the primary key of this export import report entry.
	 *
	 * @return the primary key of this export import report entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the scope of this export import report entry.
	 *
	 * @return the scope of this export import report entry
	 */
	@Override
	public String getScope() {
		return model.getScope();
	}

	/**
	 * Returns the scope key of this export import report entry.
	 *
	 * @return the scope key of this export import report entry
	 */
	@Override
	public String getScopeKey() {
		return model.getScopeKey();
	}

	/**
	 * Returns the status of this export import report entry.
	 *
	 * @return the status of this export import report entry
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the type of this export import report entry.
	 *
	 * @return the type of this export import report entry
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the class external reference code of this export import report entry.
	 *
	 * @param classExternalReferenceCode the class external reference code of this export import report entry
	 */
	@Override
	public void setClassExternalReferenceCode(
		String classExternalReferenceCode) {

		model.setClassExternalReferenceCode(classExternalReferenceCode);
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this export import report entry.
	 *
	 * @param classNameId the class name ID of this export import report entry
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this export import report entry.
	 *
	 * @param classPK the class pk of this export import report entry
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the company ID of this export import report entry.
	 *
	 * @param companyId the company ID of this export import report entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this export import report entry.
	 *
	 * @param createDate the create date of this export import report entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the error message of this export import report entry.
	 *
	 * @param errorMessage the error message of this export import report entry
	 */
	@Override
	public void setErrorMessage(String errorMessage) {
		model.setErrorMessage(errorMessage);
	}

	/**
	 * Sets the error stacktrace of this export import report entry.
	 *
	 * @param errorStacktrace the error stacktrace of this export import report entry
	 */
	@Override
	public void setErrorStacktrace(String errorStacktrace) {
		model.setErrorStacktrace(errorStacktrace);
	}

	/**
	 * Sets the export import configuration ID of this export import report entry.
	 *
	 * @param exportImportConfigurationId the export import configuration ID of this export import report entry
	 */
	@Override
	public void setExportImportConfigurationId(
		long exportImportConfigurationId) {

		model.setExportImportConfigurationId(exportImportConfigurationId);
	}

	/**
	 * Sets the export import report entry ID of this export import report entry.
	 *
	 * @param exportImportReportEntryId the export import report entry ID of this export import report entry
	 */
	@Override
	public void setExportImportReportEntryId(long exportImportReportEntryId) {
		model.setExportImportReportEntryId(exportImportReportEntryId);
	}

	/**
	 * Sets the group ID of this export import report entry.
	 *
	 * @param groupId the group ID of this export import report entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the model name of this export import report entry.
	 *
	 * @param modelName the model name of this export import report entry
	 */
	@Override
	public void setModelName(String modelName) {
		model.setModelName(modelName);
	}

	/**
	 * Sets the modified date of this export import report entry.
	 *
	 * @param modifiedDate the modified date of this export import report entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this export import report entry.
	 *
	 * @param mvccVersion the mvcc version of this export import report entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the origin of this export import report entry.
	 *
	 * @param origin the origin of this export import report entry
	 */
	@Override
	public void setOrigin(int origin) {
		model.setOrigin(origin);
	}

	/**
	 * Sets the primary key of this export import report entry.
	 *
	 * @param primaryKey the primary key of this export import report entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the scope of this export import report entry.
	 *
	 * @param scope the scope of this export import report entry
	 */
	@Override
	public void setScope(String scope) {
		model.setScope(scope);
	}

	/**
	 * Sets the scope key of this export import report entry.
	 *
	 * @param scopeKey the scope key of this export import report entry
	 */
	@Override
	public void setScopeKey(String scopeKey) {
		model.setScopeKey(scopeKey);
	}

	/**
	 * Sets the status of this export import report entry.
	 *
	 * @param status the status of this export import report entry
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the type of this export import report entry.
	 *
	 * @param type the type of this export import report entry
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected ExportImportReportEntryWrapper wrap(
		ExportImportReportEntry exportImportReportEntry) {

		return new ExportImportReportEntryWrapper(exportImportReportEntry);
	}

}