/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.sql.Blob;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link ERCVersionedEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ERCVersionedEntry
 * @generated
 */
public class ERCVersionedEntryWrapper
	extends BaseModelWrapper<ERCVersionedEntry>
	implements ERCVersionedEntry, ModelWrapper<ERCVersionedEntry> {

	public ERCVersionedEntryWrapper(ERCVersionedEntry ercVersionedEntry) {
		super(ercVersionedEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("headId", getHeadId());
		attributes.put("ercVersionedEntryId", getErcVersionedEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("blob", getBlob());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long headId = (Long)attributes.get("headId");

		if (headId != null) {
			setHeadId(headId);
		}

		Long ercVersionedEntryId = (Long)attributes.get("ercVersionedEntryId");

		if (ercVersionedEntryId != null) {
			setErcVersionedEntryId(ercVersionedEntryId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Blob blob = (Blob)attributes.get("blob");

		if (blob != null) {
			setBlob(blob);
		}
	}

	@Override
	public ERCVersionedEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the blob of this erc versioned entry.
	 *
	 * @return the blob of this erc versioned entry
	 */
	@Override
	public Blob getBlob() {
		return model.getBlob();
	}

	/**
	 * Returns the company ID of this erc versioned entry.
	 *
	 * @return the company ID of this erc versioned entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the erc versioned entry ID of this erc versioned entry.
	 *
	 * @return the erc versioned entry ID of this erc versioned entry
	 */
	@Override
	public long getErcVersionedEntryId() {
		return model.getErcVersionedEntryId();
	}

	/**
	 * Returns the external reference code of this erc versioned entry.
	 *
	 * @return the external reference code of this erc versioned entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this erc versioned entry.
	 *
	 * @return the group ID of this erc versioned entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the head ID of this erc versioned entry.
	 *
	 * @return the head ID of this erc versioned entry
	 */
	@Override
	public long getHeadId() {
		return model.getHeadId();
	}

	/**
	 * Returns the mvcc version of this erc versioned entry.
	 *
	 * @return the mvcc version of this erc versioned entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this erc versioned entry.
	 *
	 * @return the primary key of this erc versioned entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the uuid of this erc versioned entry.
	 *
	 * @return the uuid of this erc versioned entry
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the blob of this erc versioned entry.
	 *
	 * @param blob the blob of this erc versioned entry
	 */
	@Override
	public void setBlob(Blob blob) {
		model.setBlob(blob);
	}

	/**
	 * Sets the company ID of this erc versioned entry.
	 *
	 * @param companyId the company ID of this erc versioned entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the erc versioned entry ID of this erc versioned entry.
	 *
	 * @param ercVersionedEntryId the erc versioned entry ID of this erc versioned entry
	 */
	@Override
	public void setErcVersionedEntryId(long ercVersionedEntryId) {
		model.setErcVersionedEntryId(ercVersionedEntryId);
	}

	/**
	 * Sets the external reference code of this erc versioned entry.
	 *
	 * @param externalReferenceCode the external reference code of this erc versioned entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this erc versioned entry.
	 *
	 * @param groupId the group ID of this erc versioned entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the head ID of this erc versioned entry.
	 *
	 * @param headId the head ID of this erc versioned entry
	 */
	@Override
	public void setHeadId(long headId) {
		model.setHeadId(headId);
	}

	/**
	 * Sets the mvcc version of this erc versioned entry.
	 *
	 * @param mvccVersion the mvcc version of this erc versioned entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this erc versioned entry.
	 *
	 * @param primaryKey the primary key of this erc versioned entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the uuid of this erc versioned entry.
	 *
	 * @param uuid the uuid of this erc versioned entry
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public boolean isHead() {
		return model.isHead();
	}

	@Override
	public void populateVersionModel(
		ERCVersionedEntryVersion ercVersionedEntryVersion) {

		model.populateVersionModel(ercVersionedEntryVersion);
	}

	@Override
	protected ERCVersionedEntryWrapper wrap(
		ERCVersionedEntry ercVersionedEntry) {

		return new ERCVersionedEntryWrapper(ercVersionedEntry);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:352160151