/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.model;

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
 * This class is a wrapper for {@link FragmentEntry}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntry
 * @generated
 */
public class FragmentEntryWrapper
	extends BaseModelWrapper<FragmentEntry>
	implements FragmentEntry, ModelWrapper<FragmentEntry> {

	public FragmentEntryWrapper(FragmentEntry fragmentEntry) {
		super(fragmentEntry);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("headId", getHeadId());
		attributes.put("fragmentEntryId", getFragmentEntryId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("fragmentCollectionId", getFragmentCollectionId());
		attributes.put("fragmentEntryKey", getFragmentEntryKey());
		attributes.put("name", getName());
		attributes.put("css", getCss());
		attributes.put("html", getHtml());
		attributes.put("js", getJs());
		attributes.put("cacheable", isCacheable());
		attributes.put("configuration", getConfiguration());
		attributes.put("icon", getIcon());
		attributes.put("previewFileEntryId", getPreviewFileEntryId());
		attributes.put("marketplace", isMarketplace());
		attributes.put("readOnly", isReadOnly());
		attributes.put("type", getType());
		attributes.put("typeOptions", getTypeOptions());
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

		Long headId = (Long)attributes.get("headId");

		if (headId != null) {
			setHeadId(headId);
		}

		Long fragmentEntryId = (Long)attributes.get("fragmentEntryId");

		if (fragmentEntryId != null) {
			setFragmentEntryId(fragmentEntryId);
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

		Long fragmentCollectionId = (Long)attributes.get(
			"fragmentCollectionId");

		if (fragmentCollectionId != null) {
			setFragmentCollectionId(fragmentCollectionId);
		}

		String fragmentEntryKey = (String)attributes.get("fragmentEntryKey");

		if (fragmentEntryKey != null) {
			setFragmentEntryKey(fragmentEntryKey);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String css = (String)attributes.get("css");

		if (css != null) {
			setCss(css);
		}

		String html = (String)attributes.get("html");

		if (html != null) {
			setHtml(html);
		}

		String js = (String)attributes.get("js");

		if (js != null) {
			setJs(js);
		}

		Boolean cacheable = (Boolean)attributes.get("cacheable");

		if (cacheable != null) {
			setCacheable(cacheable);
		}

		String configuration = (String)attributes.get("configuration");

		if (configuration != null) {
			setConfiguration(configuration);
		}

		String icon = (String)attributes.get("icon");

		if (icon != null) {
			setIcon(icon);
		}

		Long previewFileEntryId = (Long)attributes.get("previewFileEntryId");

		if (previewFileEntryId != null) {
			setPreviewFileEntryId(previewFileEntryId);
		}

		Boolean marketplace = (Boolean)attributes.get("marketplace");

		if (marketplace != null) {
			setMarketplace(marketplace);
		}

		Boolean readOnly = (Boolean)attributes.get("readOnly");

		if (readOnly != null) {
			setReadOnly(readOnly);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		String typeOptions = (String)attributes.get("typeOptions");

		if (typeOptions != null) {
			setTypeOptions(typeOptions);
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
	public FragmentEntry cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public FragmentEntry fetchDraftFragmentEntry() {
		return model.fetchDraftFragmentEntry();
	}

	/**
	 * Returns the cacheable of this fragment entry.
	 *
	 * @return the cacheable of this fragment entry
	 */
	@Override
	public boolean getCacheable() {
		return model.getCacheable();
	}

	/**
	 * Returns the company ID of this fragment entry.
	 *
	 * @return the company ID of this fragment entry
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the configuration of this fragment entry.
	 *
	 * @return the configuration of this fragment entry
	 */
	@Override
	public String getConfiguration() {
		return model.getConfiguration();
	}

	@Override
	public String getContent() {
		return model.getContent();
	}

	/**
	 * Returns the create date of this fragment entry.
	 *
	 * @return the create date of this fragment entry
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the css of this fragment entry.
	 *
	 * @return the css of this fragment entry
	 */
	@Override
	public String getCss() {
		return model.getCss();
	}

	/**
	 * Returns the ct collection ID of this fragment entry.
	 *
	 * @return the ct collection ID of this fragment entry
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the external reference code of this fragment entry.
	 *
	 * @return the external reference code of this fragment entry
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the fragment collection ID of this fragment entry.
	 *
	 * @return the fragment collection ID of this fragment entry
	 */
	@Override
	public long getFragmentCollectionId() {
		return model.getFragmentCollectionId();
	}

	/**
	 * Returns the fragment entry ID of this fragment entry.
	 *
	 * @return the fragment entry ID of this fragment entry
	 */
	@Override
	public long getFragmentEntryId() {
		return model.getFragmentEntryId();
	}

	/**
	 * Returns the fragment entry key of this fragment entry.
	 *
	 * @return the fragment entry key of this fragment entry
	 */
	@Override
	public String getFragmentEntryKey() {
		return model.getFragmentEntryKey();
	}

	@Override
	public int getGlobalUsageCount() {
		return model.getGlobalUsageCount();
	}

	/**
	 * Returns the group ID of this fragment entry.
	 *
	 * @return the group ID of this fragment entry
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the head ID of this fragment entry.
	 *
	 * @return the head ID of this fragment entry
	 */
	@Override
	public long getHeadId() {
		return model.getHeadId();
	}

	/**
	 * Returns the html of this fragment entry.
	 *
	 * @return the html of this fragment entry
	 */
	@Override
	public String getHtml() {
		return model.getHtml();
	}

	/**
	 * Returns the icon of this fragment entry.
	 *
	 * @return the icon of this fragment entry
	 */
	@Override
	public String getIcon() {
		return model.getIcon();
	}

	@Override
	public String getImagePreviewURL(
		com.liferay.portal.kernel.theme.ThemeDisplay themeDisplay) {

		return model.getImagePreviewURL(themeDisplay);
	}

	/**
	 * Returns the js of this fragment entry.
	 *
	 * @return the js of this fragment entry
	 */
	@Override
	public String getJs() {
		return model.getJs();
	}

	/**
	 * Returns the last publish date of this fragment entry.
	 *
	 * @return the last publish date of this fragment entry
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the marketplace of this fragment entry.
	 *
	 * @return the marketplace of this fragment entry
	 */
	@Override
	public boolean getMarketplace() {
		return model.getMarketplace();
	}

	/**
	 * Returns the modified date of this fragment entry.
	 *
	 * @return the modified date of this fragment entry
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this fragment entry.
	 *
	 * @return the mvcc version of this fragment entry
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this fragment entry.
	 *
	 * @return the name of this fragment entry
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the preview file entry ID of this fragment entry.
	 *
	 * @return the preview file entry ID of this fragment entry
	 */
	@Override
	public long getPreviewFileEntryId() {
		return model.getPreviewFileEntryId();
	}

	/**
	 * Returns the primary key of this fragment entry.
	 *
	 * @return the primary key of this fragment entry
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the read only of this fragment entry.
	 *
	 * @return the read only of this fragment entry
	 */
	@Override
	public boolean getReadOnly() {
		return model.getReadOnly();
	}

	@Override
	public String getScopeERC() {
		return model.getScopeERC();
	}

	/**
	 * Returns the status of this fragment entry.
	 *
	 * @return the status of this fragment entry
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this fragment entry.
	 *
	 * @return the status by user ID of this fragment entry
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this fragment entry.
	 *
	 * @return the status by user name of this fragment entry
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this fragment entry.
	 *
	 * @return the status by user uuid of this fragment entry
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this fragment entry.
	 *
	 * @return the status date of this fragment entry
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the type of this fragment entry.
	 *
	 * @return the type of this fragment entry
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	@Override
	public String getTypeLabel() {
		return model.getTypeLabel();
	}

	/**
	 * Returns the type options of this fragment entry.
	 *
	 * @return the type options of this fragment entry
	 */
	@Override
	public String getTypeOptions() {
		return model.getTypeOptions();
	}

	@Override
	public int getUsageCount() {
		return model.getUsageCount();
	}

	/**
	 * Returns the user ID of this fragment entry.
	 *
	 * @return the user ID of this fragment entry
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this fragment entry.
	 *
	 * @return the user name of this fragment entry
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this fragment entry.
	 *
	 * @return the user uuid of this fragment entry
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this fragment entry.
	 *
	 * @return the uuid of this fragment entry
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is approved.
	 *
	 * @return <code>true</code> if this fragment entry is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is cacheable.
	 *
	 * @return <code>true</code> if this fragment entry is cacheable; <code>false</code> otherwise
	 */
	@Override
	public boolean isCacheable() {
		return model.isCacheable();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is denied.
	 *
	 * @return <code>true</code> if this fragment entry is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is a draft.
	 *
	 * @return <code>true</code> if this fragment entry is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is expired.
	 *
	 * @return <code>true</code> if this fragment entry is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is inactive.
	 *
	 * @return <code>true</code> if this fragment entry is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is incomplete.
	 *
	 * @return <code>true</code> if this fragment entry is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is marketplace.
	 *
	 * @return <code>true</code> if this fragment entry is marketplace; <code>false</code> otherwise
	 */
	@Override
	public boolean isMarketplace() {
		return model.isMarketplace();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is pending.
	 *
	 * @return <code>true</code> if this fragment entry is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is read only.
	 *
	 * @return <code>true</code> if this fragment entry is read only; <code>false</code> otherwise
	 */
	@Override
	public boolean isReadOnly() {
		return model.isReadOnly();
	}

	/**
	 * Returns <code>true</code> if this fragment entry is scheduled.
	 *
	 * @return <code>true</code> if this fragment entry is scheduled; <code>false</code> otherwise
	 */
	@Override
	public boolean isScheduled() {
		return model.isScheduled();
	}

	@Override
	public boolean isTypeComponent() {
		return model.isTypeComponent();
	}

	@Override
	public boolean isTypeInput() {
		return model.isTypeInput();
	}

	@Override
	public boolean isTypeReact() {
		return model.isTypeReact();
	}

	@Override
	public boolean isTypeSection() {
		return model.isTypeSection();
	}

	@Override
	public void persist() {
		model.persist();
	}

	@Override
	public void populateVersionModel(
		FragmentEntryVersion fragmentEntryVersion) {

		model.populateVersionModel(fragmentEntryVersion);
	}

	@Override
	public void populateZipWriter(
			com.liferay.portal.kernel.zip.ZipWriter zipWriter, String path)
		throws Exception {

		model.populateZipWriter(zipWriter, path);
	}

	/**
	 * Sets whether this fragment entry is cacheable.
	 *
	 * @param cacheable the cacheable of this fragment entry
	 */
	@Override
	public void setCacheable(boolean cacheable) {
		model.setCacheable(cacheable);
	}

	/**
	 * Sets the company ID of this fragment entry.
	 *
	 * @param companyId the company ID of this fragment entry
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the configuration of this fragment entry.
	 *
	 * @param configuration the configuration of this fragment entry
	 */
	@Override
	public void setConfiguration(String configuration) {
		model.setConfiguration(configuration);
	}

	/**
	 * Sets the create date of this fragment entry.
	 *
	 * @param createDate the create date of this fragment entry
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the css of this fragment entry.
	 *
	 * @param css the css of this fragment entry
	 */
	@Override
	public void setCss(String css) {
		model.setCss(css);
	}

	/**
	 * Sets the ct collection ID of this fragment entry.
	 *
	 * @param ctCollectionId the ct collection ID of this fragment entry
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets the external reference code of this fragment entry.
	 *
	 * @param externalReferenceCode the external reference code of this fragment entry
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the fragment collection ID of this fragment entry.
	 *
	 * @param fragmentCollectionId the fragment collection ID of this fragment entry
	 */
	@Override
	public void setFragmentCollectionId(long fragmentCollectionId) {
		model.setFragmentCollectionId(fragmentCollectionId);
	}

	/**
	 * Sets the fragment entry ID of this fragment entry.
	 *
	 * @param fragmentEntryId the fragment entry ID of this fragment entry
	 */
	@Override
	public void setFragmentEntryId(long fragmentEntryId) {
		model.setFragmentEntryId(fragmentEntryId);
	}

	/**
	 * Sets the fragment entry key of this fragment entry.
	 *
	 * @param fragmentEntryKey the fragment entry key of this fragment entry
	 */
	@Override
	public void setFragmentEntryKey(String fragmentEntryKey) {
		model.setFragmentEntryKey(fragmentEntryKey);
	}

	/**
	 * Sets the group ID of this fragment entry.
	 *
	 * @param groupId the group ID of this fragment entry
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the head ID of this fragment entry.
	 *
	 * @param headId the head ID of this fragment entry
	 */
	@Override
	public void setHeadId(long headId) {
		model.setHeadId(headId);
	}

	/**
	 * Sets the html of this fragment entry.
	 *
	 * @param html the html of this fragment entry
	 */
	@Override
	public void setHtml(String html) {
		model.setHtml(html);
	}

	/**
	 * Sets the icon of this fragment entry.
	 *
	 * @param icon the icon of this fragment entry
	 */
	@Override
	public void setIcon(String icon) {
		model.setIcon(icon);
	}

	@Override
	public void setImagePreviewURL(String imagePreviewURL) {
		model.setImagePreviewURL(imagePreviewURL);
	}

	/**
	 * Sets the js of this fragment entry.
	 *
	 * @param js the js of this fragment entry
	 */
	@Override
	public void setJs(String js) {
		model.setJs(js);
	}

	/**
	 * Sets the last publish date of this fragment entry.
	 *
	 * @param lastPublishDate the last publish date of this fragment entry
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets whether this fragment entry is marketplace.
	 *
	 * @param marketplace the marketplace of this fragment entry
	 */
	@Override
	public void setMarketplace(boolean marketplace) {
		model.setMarketplace(marketplace);
	}

	/**
	 * Sets the modified date of this fragment entry.
	 *
	 * @param modifiedDate the modified date of this fragment entry
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this fragment entry.
	 *
	 * @param mvccVersion the mvcc version of this fragment entry
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this fragment entry.
	 *
	 * @param name the name of this fragment entry
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the preview file entry ID of this fragment entry.
	 *
	 * @param previewFileEntryId the preview file entry ID of this fragment entry
	 */
	@Override
	public void setPreviewFileEntryId(long previewFileEntryId) {
		model.setPreviewFileEntryId(previewFileEntryId);
	}

	/**
	 * Sets the primary key of this fragment entry.
	 *
	 * @param primaryKey the primary key of this fragment entry
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets whether this fragment entry is read only.
	 *
	 * @param readOnly the read only of this fragment entry
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		model.setReadOnly(readOnly);
	}

	/**
	 * Sets the status of this fragment entry.
	 *
	 * @param status the status of this fragment entry
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this fragment entry.
	 *
	 * @param statusByUserId the status by user ID of this fragment entry
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this fragment entry.
	 *
	 * @param statusByUserName the status by user name of this fragment entry
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this fragment entry.
	 *
	 * @param statusByUserUuid the status by user uuid of this fragment entry
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this fragment entry.
	 *
	 * @param statusDate the status date of this fragment entry
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the type of this fragment entry.
	 *
	 * @param type the type of this fragment entry
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the type options of this fragment entry.
	 *
	 * @param typeOptions the type options of this fragment entry
	 */
	@Override
	public void setTypeOptions(String typeOptions) {
		model.setTypeOptions(typeOptions);
	}

	/**
	 * Sets the user ID of this fragment entry.
	 *
	 * @param userId the user ID of this fragment entry
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this fragment entry.
	 *
	 * @param userName the user name of this fragment entry
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this fragment entry.
	 *
	 * @param userUuid the user uuid of this fragment entry
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this fragment entry.
	 *
	 * @param uuid the uuid of this fragment entry
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
	public Map<String, Function<FragmentEntry, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<FragmentEntry, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	public boolean isHead() {
		return model.isHead();
	}

	@Override
	protected FragmentEntryWrapper wrap(FragmentEntry fragmentEntry) {
		return new FragmentEntryWrapper(fragmentEntry);
	}

}