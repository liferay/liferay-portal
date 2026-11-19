/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link AuditEventAccountEntryRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AuditEventAccountEntryRel
 * @generated
 */
public class AuditEventAccountEntryRelWrapper
	extends BaseModelWrapper<AuditEventAccountEntryRel>
	implements AuditEventAccountEntryRel,
			   ModelWrapper<AuditEventAccountEntryRel> {

	public AuditEventAccountEntryRelWrapper(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		super(auditEventAccountEntryRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put(
			"auditEventAccountEntryRelId", getAuditEventAccountEntryRelId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("accountEntryId", getAccountEntryId());
		attributes.put("auditEventId", getAuditEventId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long auditEventAccountEntryRelId = (Long)attributes.get(
			"auditEventAccountEntryRelId");

		if (auditEventAccountEntryRelId != null) {
			setAuditEventAccountEntryRelId(auditEventAccountEntryRelId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Long accountEntryId = (Long)attributes.get("accountEntryId");

		if (accountEntryId != null) {
			setAccountEntryId(accountEntryId);
		}

		Long auditEventId = (Long)attributes.get("auditEventId");

		if (auditEventId != null) {
			setAuditEventId(auditEventId);
		}
	}

	@Override
	public AuditEventAccountEntryRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the account entry ID of this audit event account entry rel.
	 *
	 * @return the account entry ID of this audit event account entry rel
	 */
	@Override
	public long getAccountEntryId() {
		return model.getAccountEntryId();
	}

	/**
	 * Returns the audit event account entry rel ID of this audit event account entry rel.
	 *
	 * @return the audit event account entry rel ID of this audit event account entry rel
	 */
	@Override
	public long getAuditEventAccountEntryRelId() {
		return model.getAuditEventAccountEntryRelId();
	}

	/**
	 * Returns the audit event ID of this audit event account entry rel.
	 *
	 * @return the audit event ID of this audit event account entry rel
	 */
	@Override
	public long getAuditEventId() {
		return model.getAuditEventId();
	}

	/**
	 * Returns the company ID of this audit event account entry rel.
	 *
	 * @return the company ID of this audit event account entry rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this audit event account entry rel.
	 *
	 * @return the create date of this audit event account entry rel
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the primary key of this audit event account entry rel.
	 *
	 * @return the primary key of this audit event account entry rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Sets the account entry ID of this audit event account entry rel.
	 *
	 * @param accountEntryId the account entry ID of this audit event account entry rel
	 */
	@Override
	public void setAccountEntryId(long accountEntryId) {
		model.setAccountEntryId(accountEntryId);
	}

	/**
	 * Sets the audit event account entry rel ID of this audit event account entry rel.
	 *
	 * @param auditEventAccountEntryRelId the audit event account entry rel ID of this audit event account entry rel
	 */
	@Override
	public void setAuditEventAccountEntryRelId(
		long auditEventAccountEntryRelId) {

		model.setAuditEventAccountEntryRelId(auditEventAccountEntryRelId);
	}

	/**
	 * Sets the audit event ID of this audit event account entry rel.
	 *
	 * @param auditEventId the audit event ID of this audit event account entry rel
	 */
	@Override
	public void setAuditEventId(long auditEventId) {
		model.setAuditEventId(auditEventId);
	}

	/**
	 * Sets the company ID of this audit event account entry rel.
	 *
	 * @param companyId the company ID of this audit event account entry rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this audit event account entry rel.
	 *
	 * @param createDate the create date of this audit event account entry rel
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the primary key of this audit event account entry rel.
	 *
	 * @param primaryKey the primary key of this audit event account entry rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected AuditEventAccountEntryRelWrapper wrap(
		AuditEventAccountEntryRel auditEventAccountEntryRel) {

		return new AuditEventAccountEntryRelWrapper(auditEventAccountEntryRel);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:943019354