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
 * This class is a wrapper for {@link ERCVersionedEntryVersion}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see ERCVersionedEntryVersion
 * @generated
 */
public class ERCVersionedEntryVersionWrapper
	extends BaseModelWrapper<ERCVersionedEntryVersion>
	implements ERCVersionedEntryVersion,
			   ModelWrapper<ERCVersionedEntryVersion> {

	public ERCVersionedEntryVersionWrapper(
		ERCVersionedEntryVersion ercVersionedEntryVersion) {

		super(ercVersionedEntryVersion);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put(
			"ercVersionedEntryVersionId", getErcVersionedEntryVersionId());
		attributes.put("version", getVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("ercVersionedEntryId", getErcVersionedEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("blob", getBlob());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long ercVersionedEntryVersionId = (Long)attributes.get(
			"ercVersionedEntryVersionId");

		if (ercVersionedEntryVersionId != null) {
			setErcVersionedEntryVersionId(ercVersionedEntryVersionId);
		}

		Integer version = (Integer)attributes.get("version");

		if (version != null) {
			setVersion(version);
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
	public ERCVersionedEntryVersion cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the blob of this erc versioned entry version.
	 *
	 * @return the blob of this erc versioned entry version
	 */
	@Override
	public Blob getBlob() {
		return model.getBlob();
	}

	/**
	 * Returns the company ID of this erc versioned entry version.
	 *
	 * @return the company ID of this erc versioned entry version
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the erc versioned entry ID of this erc versioned entry version.
	 *
	 * @return the erc versioned entry ID of this erc versioned entry version
	 */
	@Override
	public long getErcVersionedEntryId() {
		return model.getErcVersionedEntryId();
	}

	/**
	 * Returns the erc versioned entry version ID of this erc versioned entry version.
	 *
	 * @return the erc versioned entry version ID of this erc versioned entry version
	 */
	@Override
	public long getErcVersionedEntryVersionId() {
		return model.getErcVersionedEntryVersionId();
	}

	/**
	 * Returns the external reference code of this erc versioned entry version.
	 *
	 * @return the external reference code of this erc versioned entry version
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this erc versioned entry version.
	 *
	 * @return the group ID of this erc versioned entry version
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the primary key of this erc versioned entry version.
	 *
	 * @return the primary key of this erc versioned entry version
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the uuid of this erc versioned entry version.
	 *
	 * @return the uuid of this erc versioned entry version
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns the version of this erc versioned entry version.
	 *
	 * @return the version of this erc versioned entry version
	 */
	@Override
	public int getVersion() {
		return model.getVersion();
	}

	/**
	 * Sets the blob of this erc versioned entry version.
	 *
	 * @param blob the blob of this erc versioned entry version
	 */
	@Override
	public void setBlob(Blob blob) {
		model.setBlob(blob);
	}

	/**
	 * Sets the company ID of this erc versioned entry version.
	 *
	 * @param companyId the company ID of this erc versioned entry version
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the erc versioned entry ID of this erc versioned entry version.
	 *
	 * @param ercVersionedEntryId the erc versioned entry ID of this erc versioned entry version
	 */
	@Override
	public void setErcVersionedEntryId(long ercVersionedEntryId) {
		model.setErcVersionedEntryId(ercVersionedEntryId);
	}

	/**
	 * Sets the erc versioned entry version ID of this erc versioned entry version.
	 *
	 * @param ercVersionedEntryVersionId the erc versioned entry version ID of this erc versioned entry version
	 */
	@Override
	public void setErcVersionedEntryVersionId(long ercVersionedEntryVersionId) {
		model.setErcVersionedEntryVersionId(ercVersionedEntryVersionId);
	}

	/**
	 * Sets the external reference code of this erc versioned entry version.
	 *
	 * @param externalReferenceCode the external reference code of this erc versioned entry version
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this erc versioned entry version.
	 *
	 * @param groupId the group ID of this erc versioned entry version
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the primary key of this erc versioned entry version.
	 *
	 * @param primaryKey the primary key of this erc versioned entry version
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the uuid of this erc versioned entry version.
	 *
	 * @param uuid the uuid of this erc versioned entry version
	 */
	@Override
	public void setUuid(String uuid) {
		model.setUuid(uuid);
	}

	/**
	 * Sets the version of this erc versioned entry version.
	 *
	 * @param version the version of this erc versioned entry version
	 */
	@Override
	public void setVersion(int version) {
		model.setVersion(version);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	public long getVersionedModelId() {
		return model.getVersionedModelId();
	}

	@Override
	public void setVersionedModelId(long id) {
		model.setVersionedModelId(id);
	}

	@Override
	public void populateVersionedModel(ERCVersionedEntry ercVersionedEntry) {
		model.populateVersionedModel(ercVersionedEntry);
	}

	@Override
	public ERCVersionedEntry toVersionedModel() {
		return model.toVersionedModel();
	}

	@Override
	protected ERCVersionedEntryVersionWrapper wrap(
		ERCVersionedEntryVersion ercVersionedEntryVersion) {

		return new ERCVersionedEntryVersionWrapper(ercVersionedEntryVersion);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1599941869