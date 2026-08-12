/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link CTCollection}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see CTCollection
 * @generated
 */
public class CTCollectionWrapper
	extends BaseModelWrapper<CTCollection>
	implements CTCollection, ModelWrapper<CTCollection> {

	public CTCollectionWrapper(CTCollection ctCollection) {
		super(ctCollection);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("ctRemoteId", getCtRemoteId());
		attributes.put("schemaVersionId", getSchemaVersionId());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("onDemandUserId", getOnDemandUserId());
		attributes.put("shareable", isShareable());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusDate", getStatusDate());

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

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long ctRemoteId = (Long)attributes.get("ctRemoteId");

		if (ctRemoteId != null) {
			setCtRemoteId(ctRemoteId);
		}

		Long schemaVersionId = (Long)attributes.get("schemaVersionId");

		if (schemaVersionId != null) {
			setSchemaVersionId(schemaVersionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		Long onDemandUserId = (Long)attributes.get("onDemandUserId");

		if (onDemandUserId != null) {
			setOnDemandUserId(onDemandUserId);
		}

		Boolean shareable = (Boolean)attributes.get("shareable");

		if (shareable != null) {
			setShareable(shareable);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public CTCollection cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this ct collection.
	 *
	 * @return the company ID of this ct collection
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this ct collection.
	 *
	 * @return the create date of this ct collection
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this ct collection.
	 *
	 * @return the ct collection ID of this ct collection
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the ct remote ID of this ct collection.
	 *
	 * @return the ct remote ID of this ct collection
	 */
	@Override
	public long getCtRemoteId() {
		return model.getCtRemoteId();
	}

	/**
	 * Returns the description of this ct collection.
	 *
	 * @return the description of this ct collection
	 */
	@Override
	public String getDescription() {
		return model.getDescription();
	}

	/**
	 * Returns the external reference code of this ct collection.
	 *
	 * @return the external reference code of this ct collection
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the modified date of this ct collection.
	 *
	 * @return the modified date of this ct collection
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this ct collection.
	 *
	 * @return the mvcc version of this ct collection
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this ct collection.
	 *
	 * @return the name of this ct collection
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the on demand user ID of this ct collection.
	 *
	 * @return the on demand user ID of this ct collection
	 */
	@Override
	public long getOnDemandUserId() {
		return model.getOnDemandUserId();
	}

	/**
	 * Returns the on demand user uuid of this ct collection.
	 *
	 * @return the on demand user uuid of this ct collection
	 */
	@Override
	public String getOnDemandUserUuid() {
		return model.getOnDemandUserUuid();
	}

	/**
	 * Returns the primary key of this ct collection.
	 *
	 * @return the primary key of this ct collection
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the schema version ID of this ct collection.
	 *
	 * @return the schema version ID of this ct collection
	 */
	@Override
	public long getSchemaVersionId() {
		return model.getSchemaVersionId();
	}

	@Override
	public int getScore() {
		return model.getScore();
	}

	@Override
	public String getScoreSizeClassification() {
		return model.getScoreSizeClassification();
	}

	/**
	 * Returns the shareable of this ct collection.
	 *
	 * @return the shareable of this ct collection
	 */
	@Override
	public boolean getShareable() {
		return model.getShareable();
	}

	/**
	 * Returns the status of this ct collection.
	 *
	 * @return the status of this ct collection
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this ct collection.
	 *
	 * @return the status by user ID of this ct collection
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user uuid of this ct collection.
	 *
	 * @return the status by user uuid of this ct collection
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this ct collection.
	 *
	 * @return the status date of this ct collection
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the user ID of this ct collection.
	 *
	 * @return the user ID of this ct collection
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this ct collection.
	 *
	 * @return the user uuid of this ct collection
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this ct collection.
	 *
	 * @return the uuid of this ct collection
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public boolean isEmpty() {
		return model.isEmpty();
	}

	@Override
	public boolean isInProgress() {
		return model.isInProgress();
	}

	@Override
	public boolean isProduction() {
		return model.isProduction();
	}

	@Override
	public boolean isReadOnly() {
		return model.isReadOnly();
	}

	/**
	 * Returns <code>true</code> if this ct collection is shareable.
	 *
	 * @return <code>true</code> if this ct collection is shareable; <code>false</code> otherwise
	 */
	@Override
	public boolean isShareable() {
		return model.isShareable();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this ct collection.
	 *
	 * @param companyId the company ID of this ct collection
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this ct collection.
	 *
	 * @param createDate the create date of this ct collection
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this ct collection.
	 *
	 * @param ctCollectionId the ct collection ID of this ct collection
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the ct remote ID of this ct collection.
	 *
	 * @param ctRemoteId the ct remote ID of this ct collection
	 */
	@Override
	public void setCtRemoteId(long ctRemoteId) {
		model.setCtRemoteId(ctRemoteId);
	}

	/**
	 * Sets the description of this ct collection.
	 *
	 * @param description the description of this ct collection
	 */
	@Override
	public void setDescription(String description) {
		model.setDescription(description);
	}

	/**
	 * Sets the external reference code of this ct collection.
	 *
	 * @param externalReferenceCode the external reference code of this ct collection
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the modified date of this ct collection.
	 *
	 * @param modifiedDate the modified date of this ct collection
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this ct collection.
	 *
	 * @param mvccVersion the mvcc version of this ct collection
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this ct collection.
	 *
	 * @param name the name of this ct collection
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the on demand user ID of this ct collection.
	 *
	 * @param onDemandUserId the on demand user ID of this ct collection
	 */
	@Override
	public void setOnDemandUserId(long onDemandUserId) {
		model.setOnDemandUserId(onDemandUserId);
	}

	/**
	 * Sets the on demand user uuid of this ct collection.
	 *
	 * @param onDemandUserUuid the on demand user uuid of this ct collection
	 */
	@Override
	public void setOnDemandUserUuid(String onDemandUserUuid) {
		model.setOnDemandUserUuid(onDemandUserUuid);
	}

	/**
	 * Sets the primary key of this ct collection.
	 *
	 * @param primaryKey the primary key of this ct collection
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the schema version ID of this ct collection.
	 *
	 * @param schemaVersionId the schema version ID of this ct collection
	 */
	@Override
	public void setSchemaVersionId(long schemaVersionId) {
		model.setSchemaVersionId(schemaVersionId);
	}

	/**
	 * Sets whether this ct collection is shareable.
	 *
	 * @param shareable the shareable of this ct collection
	 */
	@Override
	public void setShareable(boolean shareable) {
		model.setShareable(shareable);
	}

	/**
	 * Sets the status of this ct collection.
	 *
	 * @param status the status of this ct collection
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this ct collection.
	 *
	 * @param statusByUserId the status by user ID of this ct collection
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user uuid of this ct collection.
	 *
	 * @param statusByUserUuid the status by user uuid of this ct collection
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this ct collection.
	 *
	 * @param statusDate the status date of this ct collection
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the user ID of this ct collection.
	 *
	 * @param userId the user ID of this ct collection
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this ct collection.
	 *
	 * @param userUuid the user uuid of this ct collection
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this ct collection.
	 *
	 * @param uuid the uuid of this ct collection
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
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected CTCollectionWrapper wrap(CTCollection ctCollection) {
		return new CTCollectionWrapper(ctCollection);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1040640958