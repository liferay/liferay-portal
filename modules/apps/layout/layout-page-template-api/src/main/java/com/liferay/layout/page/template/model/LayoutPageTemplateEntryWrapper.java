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
 * This class is a wrapper for {@link LayoutPageTemplateEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntry
 * @generated
 */
public class LayoutPageTemplateEntryWrapper
	extends BaseModelWrapper<LayoutPageTemplateEntry>
	implements LayoutPageTemplateEntry, ModelWrapper<LayoutPageTemplateEntry> {

	public LayoutPageTemplateEntryWrapper(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		super(layoutPageTemplateEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put(
			"layoutPageTemplateEntryId", getLayoutPageTemplateEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put(
			"layoutPageTemplateCollectionId",
			getLayoutPageTemplateCollectionId());
		attributes.put(
			"layoutPageTemplateEntryKey", getLayoutPageTemplateEntryKey());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classTypeId", getClassTypeId());
		attributes.put("classTypeKey", getClassTypeKey());
		attributes.put("name", getName());
		attributes.put("type", getType());
		attributes.put("previewFileEntryId", getPreviewFileEntryId());
		attributes.put("defaultTemplate", isDefaultTemplate());
		attributes.put("layoutPrototypeId", getLayoutPrototypeId());
		attributes.put("plid", getPlid());
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

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long layoutPageTemplateEntryId = (Long)attributes.get(
			"layoutPageTemplateEntryId");

		if (layoutPageTemplateEntryId != null) {
			setLayoutPageTemplateEntryId(layoutPageTemplateEntryId);
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

		Long layoutPageTemplateCollectionId = (Long)attributes.get(
			"layoutPageTemplateCollectionId");

		if (layoutPageTemplateCollectionId != null) {
			setLayoutPageTemplateCollectionId(layoutPageTemplateCollectionId);
		}

		String layoutPageTemplateEntryKey = (String)attributes.get(
			"layoutPageTemplateEntryKey");

		if (layoutPageTemplateEntryKey != null) {
			setLayoutPageTemplateEntryKey(layoutPageTemplateEntryKey);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classTypeId = (Long)attributes.get("classTypeId");

		if (classTypeId != null) {
			setClassTypeId(classTypeId);
		}

		String classTypeKey = (String)attributes.get("classTypeKey");

		if (classTypeKey != null) {
			setClassTypeKey(classTypeKey);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Long previewFileEntryId = (Long)attributes.get("previewFileEntryId");

		if (previewFileEntryId != null) {
			setPreviewFileEntryId(previewFileEntryId);
		}

		Boolean defaultTemplate = (Boolean)attributes.get("defaultTemplate");

		if (defaultTemplate != null) {
			setDefaultTemplate(defaultTemplate);
		}

		Long layoutPrototypeId = (Long)attributes.get("layoutPrototypeId");

		if (layoutPrototypeId != null) {
			setLayoutPrototypeId(layoutPrototypeId);
		}

		Long plid = (Long)attributes.get("plid");

		if (plid != null) {
			setPlid(plid);
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
	public LayoutPageTemplateEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the fully qualified class name of this layout page template entry.
	 *
	 * @return the fully qualified class name of this layout page template entry
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this layout page template entry.
	 *
	 * @return the class name ID of this layout page template entry
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class type ID of this layout page template entry.
	 *
	 * @return the class type ID of this layout page template entry
	 */
	@Override
	public long getClassTypeId() {
		return model.getClassTypeId();
	}

	/**
	 * Returns the class type key of this layout page template entry.
	 *
	 * @return the class type key of this layout page template entry
	 */
	@Override
	public String getClassTypeKey() {
		return model.getClassTypeKey();
	}

	/**
	 * Returns the company ID of this layout page template entry.
	 *
	 * @return the company ID of this layout page template entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout page template entry.
	 *
	 * @return the create date of this layout page template entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the ct collection ID of this layout page template entry.
	 *
	 * @return the ct collection ID of this layout page template entry
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the default template of this layout page template entry.
	 *
	 * @return the default template of this layout page template entry
	 */
	@Override
	public boolean getDefaultTemplate() {
		return model.getDefaultTemplate();
	}

	/**
	 * Returns the external reference code of this layout page template entry.
	 *
	 * @return the external reference code of this layout page template entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the group ID of this layout page template entry.
	 *
	 * @return the group ID of this layout page template entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	@Override
	public String getImagePreviewURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getImagePreviewURL(themeDisplay);
	}

	/**
	 * Returns the last publish date of this layout page template entry.
	 *
	 * @return the last publish date of this layout page template entry
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the layout page template collection ID of this layout page template entry.
	 *
	 * @return the layout page template collection ID of this layout page template entry
	 */
	@Override
	public long getLayoutPageTemplateCollectionId() {
		return model.getLayoutPageTemplateCollectionId();
	}

	/**
	 * Returns the layout page template entry ID of this layout page template entry.
	 *
	 * @return the layout page template entry ID of this layout page template entry
	 */
	@Override
	public long getLayoutPageTemplateEntryId() {
		return model.getLayoutPageTemplateEntryId();
	}

	/**
	 * Returns the layout page template entry key of this layout page template entry.
	 *
	 * @return the layout page template entry key of this layout page template entry
	 */
	@Override
	public String getLayoutPageTemplateEntryKey() {
		return model.getLayoutPageTemplateEntryKey();
	}

	/**
	 * Returns the layout prototype ID of this layout page template entry.
	 *
	 * @return the layout prototype ID of this layout page template entry
	 */
	@Override
	public long getLayoutPrototypeId() {
		return model.getLayoutPrototypeId();
	}

	/**
	 * Returns the modified date of this layout page template entry.
	 *
	 * @return the modified date of this layout page template entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout page template entry.
	 *
	 * @return the mvcc version of this layout page template entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this layout page template entry.
	 *
	 * @return the name of this layout page template entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the plid of this layout page template entry.
	 *
	 * @return the plid of this layout page template entry
	 */
	@Override
	public long getPlid() {
		return model.getPlid();
	}

	/**
	 * Returns the preview file entry ID of this layout page template entry.
	 *
	 * @return the preview file entry ID of this layout page template entry
	 */
	@Override
	public long getPreviewFileEntryId() {
		return model.getPreviewFileEntryId();
	}

	/**
	 * Returns the primary key of this layout page template entry.
	 *
	 * @return the primary key of this layout page template entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the status of this layout page template entry.
	 *
	 * @return the status of this layout page template entry
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this layout page template entry.
	 *
	 * @return the status by user ID of this layout page template entry
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this layout page template entry.
	 *
	 * @return the status by user name of this layout page template entry
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this layout page template entry.
	 *
	 * @return the status by user uuid of this layout page template entry
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this layout page template entry.
	 *
	 * @return the status date of this layout page template entry
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the type of this layout page template entry.
	 *
	 * @return the type of this layout page template entry
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this layout page template entry.
	 *
	 * @return the user ID of this layout page template entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout page template entry.
	 *
	 * @return the user name of this layout page template entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout page template entry.
	 *
	 * @return the user uuid of this layout page template entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this layout page template entry.
	 *
	 * @return the uuid of this layout page template entry
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is approved.
	 *
	 * @return <code>true</code> if this layout page template entry is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is default template.
	 *
	 * @return <code>true</code> if this layout page template entry is default template; <code>false</code> otherwise
	 */
	@Override
	public boolean isDefaultTemplate() {
		return model.isDefaultTemplate();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is denied.
	 *
	 * @return <code>true</code> if this layout page template entry is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is a draft.
	 *
	 * @return <code>true</code> if this layout page template entry is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is expired.
	 *
	 * @return <code>true</code> if this layout page template entry is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is inactive.
	 *
	 * @return <code>true</code> if this layout page template entry is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is incomplete.
	 *
	 * @return <code>true</code> if this layout page template entry is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is pending.
	 *
	 * @return <code>true</code> if this layout page template entry is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this layout page template entry is scheduled.
	 *
	 * @return <code>true</code> if this layout page template entry is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public void persist() {
		model.persist();
	}

	@Override
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this layout page template entry.
	 *
	 * @param classNameId the class name ID of this layout page template entry
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class type ID of this layout page template entry.
	 *
	 * @param classTypeId the class type ID of this layout page template entry
	 */
	@Override
	public void setClassTypeId(long classTypeId) {
		model.setClassTypeId(classTypeId);
	}

	/**
	 * Sets the class type key of this layout page template entry.
	 *
	 * @param classTypeKey the class type key of this layout page template entry
	 */
	@Override
	public void setClassTypeKey(String classTypeKey) {
		model.setClassTypeKey(classTypeKey);
	}

	/**
	 * Sets the company ID of this layout page template entry.
	 *
	 * @param companyId the company ID of this layout page template entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout page template entry.
	 *
	 * @param createDate the create date of this layout page template entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the ct collection ID of this layout page template entry.
	 *
	 * @param ctCollectionId the ct collection ID of this layout page template entry
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets whether this layout page template entry is default template.
	 *
	 * @param defaultTemplate the default template of this layout page template entry
	 */
	@Override
	public void setDefaultTemplate(boolean defaultTemplate) {
		model.setDefaultTemplate(defaultTemplate);
	}

	/**
	 * Sets the external reference code of this layout page template entry.
	 *
	 * @param externalReferenceCode the external reference code of this layout page template entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the group ID of this layout page template entry.
	 *
	 * @param groupId the group ID of this layout page template entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the last publish date of this layout page template entry.
	 *
	 * @param lastPublishDate the last publish date of this layout page template entry
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the layout page template collection ID of this layout page template entry.
	 *
	 * @param layoutPageTemplateCollectionId the layout page template collection ID of this layout page template entry
	 */
	@Override
	public void setLayoutPageTemplateCollectionId(
		long layoutPageTemplateCollectionId) {

		model.setLayoutPageTemplateCollectionId(layoutPageTemplateCollectionId);
	}

	/**
	 * Sets the layout page template entry ID of this layout page template entry.
	 *
	 * @param layoutPageTemplateEntryId the layout page template entry ID of this layout page template entry
	 */
	@Override
	public void setLayoutPageTemplateEntryId(long layoutPageTemplateEntryId) {
		model.setLayoutPageTemplateEntryId(layoutPageTemplateEntryId);
	}

	/**
	 * Sets the layout page template entry key of this layout page template entry.
	 *
	 * @param layoutPageTemplateEntryKey the layout page template entry key of this layout page template entry
	 */
	@Override
	public void setLayoutPageTemplateEntryKey(
		String layoutPageTemplateEntryKey) {

		model.setLayoutPageTemplateEntryKey(layoutPageTemplateEntryKey);
	}

	/**
	 * Sets the layout prototype ID of this layout page template entry.
	 *
	 * @param layoutPrototypeId the layout prototype ID of this layout page template entry
	 */
	@Override
	public void setLayoutPrototypeId(long layoutPrototypeId) {
		model.setLayoutPrototypeId(layoutPrototypeId);
	}

	/**
	 * Sets the modified date of this layout page template entry.
	 *
	 * @param modifiedDate the modified date of this layout page template entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout page template entry.
	 *
	 * @param mvccVersion the mvcc version of this layout page template entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this layout page template entry.
	 *
	 * @param name the name of this layout page template entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the plid of this layout page template entry.
	 *
	 * @param plid the plid of this layout page template entry
	 */
	@Override
	public void setPlid(long plid) {
		model.setPlid(plid);
	}

	/**
	 * Sets the preview file entry ID of this layout page template entry.
	 *
	 * @param previewFileEntryId the preview file entry ID of this layout page template entry
	 */
	@Override
	public void setPreviewFileEntryId(long previewFileEntryId) {
		model.setPreviewFileEntryId(previewFileEntryId);
	}

	/**
	 * Sets the primary key of this layout page template entry.
	 *
	 * @param primaryKey the primary key of this layout page template entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the status of this layout page template entry.
	 *
	 * @param status the status of this layout page template entry
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this layout page template entry.
	 *
	 * @param statusByUserId the status by user ID of this layout page template entry
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this layout page template entry.
	 *
	 * @param statusByUserName the status by user name of this layout page template entry
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this layout page template entry.
	 *
	 * @param statusByUserUuid the status by user uuid of this layout page template entry
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this layout page template entry.
	 *
	 * @param statusDate the status date of this layout page template entry
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the type of this layout page template entry.
	 *
	 * @param type the type of this layout page template entry
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this layout page template entry.
	 *
	 * @param userId the user ID of this layout page template entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout page template entry.
	 *
	 * @param userName the user name of this layout page template entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout page template entry.
	 *
	 * @param userUuid the user uuid of this layout page template entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this layout page template entry.
	 *
	 * @param uuid the uuid of this layout page template entry
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
	public Map<String, Function<LayoutPageTemplateEntry, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<LayoutPageTemplateEntry, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected LayoutPageTemplateEntryWrapper wrap(
		LayoutPageTemplateEntry layoutPageTemplateEntry) {

		return new LayoutPageTemplateEntryWrapper(layoutPageTemplateEntry);
	}

}