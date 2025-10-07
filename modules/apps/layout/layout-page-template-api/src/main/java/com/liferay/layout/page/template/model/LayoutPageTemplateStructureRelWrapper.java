/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model;

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
 * This class is a wrapper for {@link LayoutPageTemplateStructureRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRel
 * @generated
 */
public class LayoutPageTemplateStructureRelWrapper
	extends BaseModelWrapper<LayoutPageTemplateStructureRel>
	implements LayoutPageTemplateStructureRel,
			   ModelWrapper<LayoutPageTemplateStructureRel> {

	public LayoutPageTemplateStructureRelWrapper(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		super(layoutPageTemplateStructureRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put(
			"layoutPageTemplateStructureRelId",
			getLayoutPageTemplateStructureRelId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put(
			"layoutPageTemplateStructureId",
			getLayoutPageTemplateStructureId());
		attributes.put("segmentsExperienceId", getSegmentsExperienceId());
		attributes.put("data", getData());
		attributes.put("lastPublishDate", getLastPublishDate());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

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

		Long layoutPageTemplateStructureRelId = (Long)attributes.get(
			"layoutPageTemplateStructureRelId");

		if (layoutPageTemplateStructureRelId != null) {
			setLayoutPageTemplateStructureRelId(
				layoutPageTemplateStructureRelId);
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

		Long layoutPageTemplateStructureId = (Long)attributes.get(
			"layoutPageTemplateStructureId");

		if (layoutPageTemplateStructureId != null) {
			setLayoutPageTemplateStructureId(layoutPageTemplateStructureId);
		}

		Long segmentsExperienceId = (Long)attributes.get(
			"segmentsExperienceId");

		if (segmentsExperienceId != null) {
			setSegmentsExperienceId(segmentsExperienceId);
		}

		String data = (String)attributes.get("data");

		if (data != null) {
			setData(data);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}

		Long statusByUserId = (Long)attributes.get("statusByUserId");

		if (statusByUserId != null) {
			setStatusByUserId(statusByUserId);
		}

		String statusByUserName = (String)attributes.get("statusByUserName");

		if (statusByUserName != null) {
			setStatusByUserName(statusByUserName);
		}

		Date statusDate = (Date)attributes.get("statusDate");

		if (statusDate != null) {
			setStatusDate(statusDate);
		}
	}

	@Override
	public LayoutPageTemplateStructureRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this layout page template structure rel.
	 *
	 * @return the company ID of this layout page template structure rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout page template structure rel.
	 *
	 * @return the create date of this layout page template structure rel
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this layout page template structure rel.
	 *
	 * @return the ct collection ID of this layout page template structure rel
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the data of this layout page template structure rel.
	 *
	 * @return the data of this layout page template structure rel
	 */
	@Override
	public String getData() {
		return model.getData();
	}

	@Override
	public com.liferay.portal.kernel.json.JSONObject getDataJSONObject() {
		return model.getDataJSONObject();
	}

	/**
	 * Returns the group ID of this layout page template structure rel.
	 *
	 * @return the group ID of this layout page template structure rel
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the last publish date of this layout page template structure rel.
	 *
	 * @return the last publish date of this layout page template structure rel
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the layout page template structure ID of this layout page template structure rel.
	 *
	 * @return the layout page template structure ID of this layout page template structure rel
	 */
	@Override
	public long getLayoutPageTemplateStructureId() {
		return model.getLayoutPageTemplateStructureId();
	}

	/**
	 * Returns the layout page template structure rel ID of this layout page template structure rel.
	 *
	 * @return the layout page template structure rel ID of this layout page template structure rel
	 */
	@Override
	public long getLayoutPageTemplateStructureRelId() {
		return model.getLayoutPageTemplateStructureRelId();
	}

	/**
	 * Returns the modified date of this layout page template structure rel.
	 *
	 * @return the modified date of this layout page template structure rel
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout page template structure rel.
	 *
	 * @return the mvcc version of this layout page template structure rel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this layout page template structure rel.
	 *
	 * @return the primary key of this layout page template structure rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the segments experience ID of this layout page template structure rel.
	 *
	 * @return the segments experience ID of this layout page template structure rel
	 */
	@Override
	public long getSegmentsExperienceId() {
		return model.getSegmentsExperienceId();
	}

	/**
	 * Returns the status of this layout page template structure rel.
	 *
	 * @return the status of this layout page template structure rel
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this layout page template structure rel.
	 *
	 * @return the status by user ID of this layout page template structure rel
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this layout page template structure rel.
	 *
	 * @return the status by user name of this layout page template structure rel
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this layout page template structure rel.
	 *
	 * @return the status by user uuid of this layout page template structure rel
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this layout page template structure rel.
	 *
	 * @return the status date of this layout page template structure rel
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the user ID of this layout page template structure rel.
	 *
	 * @return the user ID of this layout page template structure rel
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout page template structure rel.
	 *
	 * @return the user name of this layout page template structure rel
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout page template structure rel.
	 *
	 * @return the user uuid of this layout page template structure rel
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this layout page template structure rel.
	 *
	 * @return the uuid of this layout page template structure rel
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is approved.
	 *
	 * @return <code>true</code> if this layout page template structure rel is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is denied.
	 *
	 * @return <code>true</code> if this layout page template structure rel is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is a draft.
	 *
	 * @return <code>true</code> if this layout page template structure rel is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is expired.
	 *
	 * @return <code>true</code> if this layout page template structure rel is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is inactive.
	 *
	 * @return <code>true</code> if this layout page template structure rel is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is incomplete.
	 *
	 * @return <code>true</code> if this layout page template structure rel is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is pending.
	 *
	 * @return <code>true</code> if this layout page template structure rel is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this layout page template structure rel is scheduled.
	 *
	 * @return <code>true</code> if this layout page template structure rel is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this layout page template structure rel.
	 *
	 * @param companyId the company ID of this layout page template structure rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout page template structure rel.
	 *
	 * @param createDate the create date of this layout page template structure rel
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this layout page template structure rel.
	 *
	 * @param ctCollectionId the ct collection ID of this layout page template structure rel
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the data of this layout page template structure rel.
	 *
	 * @param data the data of this layout page template structure rel
	 */
	@Override
	public void setData(String data) {
		model.setData(data);
	}

	/**
	 * Sets the group ID of this layout page template structure rel.
	 *
	 * @param groupId the group ID of this layout page template structure rel
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this layout page template structure rel.
	 *
	 * @param lastPublishDate the last publish date of this layout page template structure rel
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the layout page template structure ID of this layout page template structure rel.
	 *
	 * @param layoutPageTemplateStructureId the layout page template structure ID of this layout page template structure rel
	 */
	@Override
	public void setLayoutPageTemplateStructureId(
		long layoutPageTemplateStructureId) {

		model.setLayoutPageTemplateStructureId(layoutPageTemplateStructureId);
	}

	/**
	 * Sets the layout page template structure rel ID of this layout page template structure rel.
	 *
	 * @param layoutPageTemplateStructureRelId the layout page template structure rel ID of this layout page template structure rel
	 */
	@Override
	public void setLayoutPageTemplateStructureRelId(
		long layoutPageTemplateStructureRelId) {

		model.setLayoutPageTemplateStructureRelId(
			layoutPageTemplateStructureRelId);
	}

	/**
	 * Sets the modified date of this layout page template structure rel.
	 *
	 * @param modifiedDate the modified date of this layout page template structure rel
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout page template structure rel.
	 *
	 * @param mvccVersion the mvcc version of this layout page template structure rel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this layout page template structure rel.
	 *
	 * @param primaryKey the primary key of this layout page template structure rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the segments experience ID of this layout page template structure rel.
	 *
	 * @param segmentsExperienceId the segments experience ID of this layout page template structure rel
	 */
	@Override
	public void setSegmentsExperienceId(long segmentsExperienceId) {
		model.setSegmentsExperienceId(segmentsExperienceId);
	}

	/**
	 * Sets the status of this layout page template structure rel.
	 *
	 * @param status the status of this layout page template structure rel
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this layout page template structure rel.
	 *
	 * @param statusByUserId the status by user ID of this layout page template structure rel
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this layout page template structure rel.
	 *
	 * @param statusByUserName the status by user name of this layout page template structure rel
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this layout page template structure rel.
	 *
	 * @param statusByUserUuid the status by user uuid of this layout page template structure rel
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this layout page template structure rel.
	 *
	 * @param statusDate the status date of this layout page template structure rel
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the user ID of this layout page template structure rel.
	 *
	 * @param userId the user ID of this layout page template structure rel
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout page template structure rel.
	 *
	 * @param userName the user name of this layout page template structure rel
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout page template structure rel.
	 *
	 * @param userUuid the user uuid of this layout page template structure rel
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this layout page template structure rel.
	 *
	 * @param uuid the uuid of this layout page template structure rel
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
	public Map<String, Function<LayoutPageTemplateStructureRel, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<LayoutPageTemplateStructureRel, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected LayoutPageTemplateStructureRelWrapper wrap(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		return new LayoutPageTemplateStructureRelWrapper(
			layoutPageTemplateStructureRel);
	}

}