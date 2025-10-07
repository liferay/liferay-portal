/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.model;

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
 * This class is a wrapper for {@link LayoutClassedModelUsage}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutClassedModelUsage
 * @generated
 */
public class LayoutClassedModelUsageWrapper
	extends BaseModelWrapper<LayoutClassedModelUsage>
	implements LayoutClassedModelUsage, ModelWrapper<LayoutClassedModelUsage> {

	public LayoutClassedModelUsageWrapper(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		super(layoutClassedModelUsage);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put(
			"layoutClassedModelUsageId", getLayoutClassedModelUsageId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put(
			"classExternalReferenceCode", getClassExternalReferenceCode());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("containerKey", getContainerKey());
		attributes.put("containerType", getContainerType());
		attributes.put("plid", getPlid());
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

		Long layoutClassedModelUsageId = (Long)attributes.get(
			"layoutClassedModelUsageId");

		if (layoutClassedModelUsageId != null) {
			setLayoutClassedModelUsageId(layoutClassedModelUsageId);
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

		String containerKey = (String)attributes.get("containerKey");

		if (containerKey != null) {
			setContainerKey(containerKey);
		}

		Long containerType = (Long)attributes.get("containerType");

		if (containerType != null) {
			setContainerType(containerType);
		}

		Long plid = (Long)attributes.get("plid");

		if (plid != null) {
			setPlid(plid);
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
	public LayoutClassedModelUsage cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the class external reference code of this layout classed model usage.
	 *
	 * @return the class external reference code of this layout classed model usage
	 */
	@Override
	public String getClassExternalReferenceCode() {
		return model.getClassExternalReferenceCode();
	}

	/**
	 * Returns the fully qualified class name of this layout classed model usage.
	 *
	 * @return the fully qualified class name of this layout classed model usage
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this layout classed model usage.
	 *
	 * @return the class name ID of this layout classed model usage
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this layout classed model usage.
	 *
	 * @return the class pk of this layout classed model usage
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the company ID of this layout classed model usage.
	 *
	 * @return the company ID of this layout classed model usage
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the container key of this layout classed model usage.
	 *
	 * @return the container key of this layout classed model usage
	 */
	@Override
	public String getContainerKey() {
		return model.getContainerKey();
	}

	/**
	 * Returns the container type of this layout classed model usage.
	 *
	 * @return the container type of this layout classed model usage
	 */
	@Override
	public long getContainerType() {
		return model.getContainerType();
	}

	/**
	 * Returns the create date of this layout classed model usage.
	 *
	 * @return the create date of this layout classed model usage
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this layout classed model usage.
	 *
	 * @return the ct collection ID of this layout classed model usage
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the group ID of this layout classed model usage.
	 *
	 * @return the group ID of this layout classed model usage
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this layout classed model usage.
	 *
	 * @return the last publish date of this layout classed model usage
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the layout classed model usage ID of this layout classed model usage.
	 *
	 * @return the layout classed model usage ID of this layout classed model usage
	 */
	@Override
	public long getLayoutClassedModelUsageId() {
		return model.getLayoutClassedModelUsageId();
	}

	/**
	 * Returns the modified date of this layout classed model usage.
	 *
	 * @return the modified date of this layout classed model usage
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout classed model usage.
	 *
	 * @return the mvcc version of this layout classed model usage
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the plid of this layout classed model usage.
	 *
	 * @return the plid of this layout classed model usage
	 */
	@Override
	public long getPlid() {
		return model.getPlid();
	}

	/**
	 * Returns the primary key of this layout classed model usage.
	 *
	 * @return the primary key of this layout classed model usage
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the type of this layout classed model usage.
	 *
	 * @return the type of this layout classed model usage
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the uuid of this layout classed model usage.
	 *
	 * @return the uuid of this layout classed model usage
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
	 * Sets the class external reference code of this layout classed model usage.
	 *
	 * @param classExternalReferenceCode the class external reference code of this layout classed model usage
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
	 * Sets the class name ID of this layout classed model usage.
	 *
	 * @param classNameId the class name ID of this layout classed model usage
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this layout classed model usage.
	 *
	 * @param classPK the class pk of this layout classed model usage
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the company ID of this layout classed model usage.
	 *
	 * @param companyId the company ID of this layout classed model usage
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the container key of this layout classed model usage.
	 *
	 * @param containerKey the container key of this layout classed model usage
	 */
	@Override
	public void setContainerKey(String containerKey) {
		model.setContainerKey(containerKey);
	}

	/**
	 * Sets the container type of this layout classed model usage.
	 *
	 * @param containerType the container type of this layout classed model usage
	 */
	@Override
	public void setContainerType(long containerType) {
		model.setContainerType(containerType);
	}

	/**
	 * Sets the create date of this layout classed model usage.
	 *
	 * @param createDate the create date of this layout classed model usage
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this layout classed model usage.
	 *
	 * @param ctCollectionId the ct collection ID of this layout classed model usage
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the group ID of this layout classed model usage.
	 *
	 * @param groupId the group ID of this layout classed model usage
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this layout classed model usage.
	 *
	 * @param lastPublishDate the last publish date of this layout classed model usage
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the layout classed model usage ID of this layout classed model usage.
	 *
	 * @param layoutClassedModelUsageId the layout classed model usage ID of this layout classed model usage
	 */
	@Override
	public void setLayoutClassedModelUsageId(long layoutClassedModelUsageId) {
		model.setLayoutClassedModelUsageId(layoutClassedModelUsageId);
	}

	/**
	 * Sets the modified date of this layout classed model usage.
	 *
	 * @param modifiedDate the modified date of this layout classed model usage
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout classed model usage.
	 *
	 * @param mvccVersion the mvcc version of this layout classed model usage
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the plid of this layout classed model usage.
	 *
	 * @param plid the plid of this layout classed model usage
	 */
	@Override
	public void setPlid(long plid) {
		model.setPlid(plid);
	}

	/**
	 * Sets the primary key of this layout classed model usage.
	 *
	 * @param primaryKey the primary key of this layout classed model usage
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the type of this layout classed model usage.
	 *
	 * @param type the type of this layout classed model usage
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the uuid of this layout classed model usage.
	 *
	 * @param uuid the uuid of this layout classed model usage
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
	public Map<String, Function<LayoutClassedModelUsage, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<LayoutClassedModelUsage, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected LayoutClassedModelUsageWrapper wrap(
		LayoutClassedModelUsage layoutClassedModelUsage) {

		return new LayoutClassedModelUsageWrapper(layoutClassedModelUsage);
	}

}