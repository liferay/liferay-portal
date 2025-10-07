/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.model;

import com.liferay.exportimport.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * <p>
 * This class is a wrapper for {@link DepotEntryGroupRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRel
 * @generated
 */
public class DepotEntryGroupRelWrapper
	extends BaseModelWrapper<DepotEntryGroupRel>
	implements DepotEntryGroupRel, ModelWrapper<DepotEntryGroupRel> {

	public DepotEntryGroupRelWrapper(DepotEntryGroupRel depotEntryGroupRel) {
		super(depotEntryGroupRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("depotEntryGroupRelId", getDepotEntryGroupRelId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("ddmStructuresAvailable", isDdmStructuresAvailable());
		attributes.put("depotEntryId", getDepotEntryId());
		attributes.put("searchable", isSearchable());
		attributes.put("toGroupId", getToGroupId());
		attributes.put("type", getType());
		attributes.put("lastPublishDate", getLastPublishDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long ctCollectionId = (Long)attributes.get("ctCollectionId");

		if (ctCollectionId != null) {
			setCtCollectionId(ctCollectionId);
		}

		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long depotEntryGroupRelId = (Long)attributes.get(
			"depotEntryGroupRelId");

		if (depotEntryGroupRelId != null) {
			setDepotEntryGroupRelId(depotEntryGroupRelId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Boolean ddmStructuresAvailable = (Boolean)attributes.get(
			"ddmStructuresAvailable");

		if (ddmStructuresAvailable != null) {
			setDdmStructuresAvailable(ddmStructuresAvailable);
		}

		Long depotEntryId = (Long)attributes.get("depotEntryId");

		if (depotEntryId != null) {
			setDepotEntryId(depotEntryId);
		}

		Boolean searchable = (Boolean)attributes.get("searchable");

		if (searchable != null) {
			setSearchable(searchable);
		}

		Long toGroupId = (Long)attributes.get("toGroupId");

		if (toGroupId != null) {
			setToGroupId(toGroupId);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}
	}

	@Override
	public DepotEntryGroupRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this depot entry group rel.
	 *
	 * @return the company ID of this depot entry group rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this depot entry group rel.
	 *
	 * @return the create date of this depot entry group rel
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this depot entry group rel.
	 *
	 * @return the ct collection ID of this depot entry group rel
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the ddm structures available of this depot entry group rel.
	 *
	 * @return the ddm structures available of this depot entry group rel
	 */
	@Override
	public boolean getDdmStructuresAvailable() {
		return model.getDdmStructuresAvailable();
	}

	/**
	 * Returns the depot entry group rel ID of this depot entry group rel.
	 *
	 * @return the depot entry group rel ID of this depot entry group rel
	 */
	@Override
	public long getDepotEntryGroupRelId() {
		return model.getDepotEntryGroupRelId();
	}

	/**
	 * Returns the depot entry ID of this depot entry group rel.
	 *
	 * @return the depot entry ID of this depot entry group rel
	 */
	@Override
	public long getDepotEntryId() {
		return model.getDepotEntryId();
	}

	/**
	 * Returns the group ID of this depot entry group rel.
	 *
	 * @return the group ID of this depot entry group rel
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this depot entry group rel.
	 *
	 * @return the last publish date of this depot entry group rel
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this depot entry group rel.
	 *
	 * @return the modified date of this depot entry group rel
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this depot entry group rel.
	 *
	 * @return the mvcc version of this depot entry group rel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this depot entry group rel.
	 *
	 * @return the primary key of this depot entry group rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the searchable of this depot entry group rel.
	 *
	 * @return the searchable of this depot entry group rel
	 */
	@Override
	public boolean getSearchable() {
		return model.getSearchable();
	}

	/**
	 * Returns the to group ID of this depot entry group rel.
	 *
	 * @return the to group ID of this depot entry group rel
	 */
	@Override
	public long getToGroupId() {
		return model.getToGroupId();
	}

	/**
	 * Returns the type of this depot entry group rel.
	 *
	 * @return the type of this depot entry group rel
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this depot entry group rel.
	 *
	 * @return the user ID of this depot entry group rel
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this depot entry group rel.
	 *
	 * @return the user name of this depot entry group rel
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this depot entry group rel.
	 *
	 * @return the user uuid of this depot entry group rel
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this depot entry group rel.
	 *
	 * @return the uuid of this depot entry group rel
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this depot entry group rel is ddm structures available.
	 *
	 * @return <code>true</code> if this depot entry group rel is ddm structures available; <code>false</code> otherwise
	 */
	@Override
	public boolean isDdmStructuresAvailable() {
		return model.isDdmStructuresAvailable();
	}

	/**
	 * Returns <code>true</code> if this depot entry group rel is searchable.
	 *
	 * @return <code>true</code> if this depot entry group rel is searchable; <code>false</code> otherwise
	 */
	@Override
	public boolean isSearchable() {
		return model.isSearchable();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this depot entry group rel.
	 *
	 * @param companyId the company ID of this depot entry group rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this depot entry group rel.
	 *
	 * @param createDate the create date of this depot entry group rel
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this depot entry group rel.
	 *
	 * @param ctCollectionId the ct collection ID of this depot entry group rel
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets whether this depot entry group rel is ddm structures available.
	 *
	 * @param ddmStructuresAvailable the ddm structures available of this depot entry group rel
	 */
	@Override
	public void setDdmStructuresAvailable(boolean ddmStructuresAvailable) {
		model.setDdmStructuresAvailable(ddmStructuresAvailable);
	}

	/**
	 * Sets the depot entry group rel ID of this depot entry group rel.
	 *
	 * @param depotEntryGroupRelId the depot entry group rel ID of this depot entry group rel
	 */
	@Override
	public void setDepotEntryGroupRelId(long depotEntryGroupRelId) {
		model.setDepotEntryGroupRelId(depotEntryGroupRelId);
	}

	/**
	 * Sets the depot entry ID of this depot entry group rel.
	 *
	 * @param depotEntryId the depot entry ID of this depot entry group rel
	 */
	@Override
	public void setDepotEntryId(long depotEntryId) {
		model.setDepotEntryId(depotEntryId);
	}

	/**
	 * Sets the group ID of this depot entry group rel.
	 *
	 * @param groupId the group ID of this depot entry group rel
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this depot entry group rel.
	 *
	 * @param lastPublishDate the last publish date of this depot entry group rel
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this depot entry group rel.
	 *
	 * @param modifiedDate the modified date of this depot entry group rel
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this depot entry group rel.
	 *
	 * @param mvccVersion the mvcc version of this depot entry group rel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this depot entry group rel.
	 *
	 * @param primaryKey the primary key of this depot entry group rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this depot entry group rel is searchable.
	 *
	 * @param searchable the searchable of this depot entry group rel
	 */
	@Override
	public void setSearchable(boolean searchable) {
		model.setSearchable(searchable);
	}

	/**
	 * Sets the to group ID of this depot entry group rel.
	 *
	 * @param toGroupId the to group ID of this depot entry group rel
	 */
	@Override
	public void setToGroupId(long toGroupId) {
		model.setToGroupId(toGroupId);
	}

	/**
	 * Sets the type of this depot entry group rel.
	 *
	 * @param type the type of this depot entry group rel
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this depot entry group rel.
	 *
	 * @param userId the user ID of this depot entry group rel
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this depot entry group rel.
	 *
	 * @param userName the user name of this depot entry group rel
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this depot entry group rel.
	 *
	 * @param userUuid the user uuid of this depot entry group rel
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this depot entry group rel.
	 *
	 * @param uuid the uuid of this depot entry group rel
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
	public Map<String, Function<DepotEntryGroupRel, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<DepotEntryGroupRel, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected DepotEntryGroupRelWrapper wrap(
		DepotEntryGroupRel depotEntryGroupRel) {

		return new DepotEntryGroupRelWrapper(depotEntryGroupRel);
	}

}