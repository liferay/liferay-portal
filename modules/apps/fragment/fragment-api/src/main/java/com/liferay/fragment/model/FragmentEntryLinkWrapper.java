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
 * This class is a wrapper for {@link FragmentEntryLink}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLink
 * @generated
 */
public class FragmentEntryLinkWrapper
	extends BaseModelWrapper<FragmentEntryLink>
	implements FragmentEntryLink, ModelWrapper<FragmentEntryLink> {

	public FragmentEntryLinkWrapper(FragmentEntryLink fragmentEntryLink) {
		super(fragmentEntryLink);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("ctCollectionId", getCtCollectionId());
		attributes.put("uuid", getUuid());
		attributes.put("externalReferenceCode", getExternalReferenceCode());
		attributes.put("fragmentEntryLinkId", getFragmentEntryLinkId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put(
			"originalFragmentEntryLinkId", getOriginalFragmentEntryLinkId());
		attributes.put("fragmentEntryId", getFragmentEntryId());
		attributes.put("segmentsExperienceId", getSegmentsExperienceId());
		attributes.put("classNameId", getClassNameId());
		attributes.put("classPK", getClassPK());
		attributes.put("plid", getPlid());
		attributes.put("css", getCss());
		attributes.put("html", getHtml());
		attributes.put("js", getJs());
		attributes.put("configuration", getConfiguration());
		attributes.put("deleted", isDeleted());
		attributes.put("editableValues", getEditableValues());
		attributes.put("namespace", getNamespace());
		attributes.put("position", getPosition());
		attributes.put("rendererKey", getRendererKey());
		attributes.put("type", getType());
		attributes.put("lastPropagationDate", getLastPropagationDate());
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

		String externalReferenceCode = (String)attributes.get(
			"externalReferenceCode");

		if (externalReferenceCode != null) {
			setExternalReferenceCode(externalReferenceCode);
		}

		Long fragmentEntryLinkId = (Long)attributes.get("fragmentEntryLinkId");

		if (fragmentEntryLinkId != null) {
			setFragmentEntryLinkId(fragmentEntryLinkId);
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

		Long originalFragmentEntryLinkId = (Long)attributes.get(
			"originalFragmentEntryLinkId");

		if (originalFragmentEntryLinkId != null) {
			setOriginalFragmentEntryLinkId(originalFragmentEntryLinkId);
		}

		Long fragmentEntryId = (Long)attributes.get("fragmentEntryId");

		if (fragmentEntryId != null) {
			setFragmentEntryId(fragmentEntryId);
		}

		Long segmentsExperienceId = (Long)attributes.get(
			"segmentsExperienceId");

		if (segmentsExperienceId != null) {
			setSegmentsExperienceId(segmentsExperienceId);
		}

		Long classNameId = (Long)attributes.get("classNameId");

		if (classNameId != null) {
			setClassNameId(classNameId);
		}

		Long classPK = (Long)attributes.get("classPK");

		if (classPK != null) {
			setClassPK(classPK);
		}

		Long plid = (Long)attributes.get("plid");

		if (plid != null) {
			setPlid(plid);
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

		String configuration = (String)attributes.get("configuration");

		if (configuration != null) {
			setConfiguration(configuration);
		}

		Boolean deleted = (Boolean)attributes.get("deleted");

		if (deleted != null) {
			setDeleted(deleted);
		}

		String editableValues = (String)attributes.get("editableValues");

		if (editableValues != null) {
			setEditableValues(editableValues);
		}

		String namespace = (String)attributes.get("namespace");

		if (namespace != null) {
			setNamespace(namespace);
		}

		Integer position = (Integer)attributes.get("position");

		if (position != null) {
			setPosition(position);
		}

		String rendererKey = (String)attributes.get("rendererKey");

		if (rendererKey != null) {
			setRendererKey(rendererKey);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Date lastPropagationDate = (Date)attributes.get("lastPropagationDate");

		if (lastPropagationDate != null) {
			setLastPropagationDate(lastPropagationDate);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}
	}

	@Override
	public FragmentEntryLink cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the fully qualified class name of this fragment entry link.
	 *
	 * @return the fully qualified class name of this fragment entry link
	 */
	@Override
	public String getClassName() {
		return model.getClassName();
	}

	/**
	 * Returns the class name ID of this fragment entry link.
	 *
	 * @return the class name ID of this fragment entry link
	 */
	@Override
	public long getClassNameId() {
		return model.getClassNameId();
	}

	/**
	 * Returns the class pk of this fragment entry link.
	 *
	 * @return the class pk of this fragment entry link
	 */
	@Override
	public long getClassPK() {
		return model.getClassPK();
	}

	/**
	 * Returns the company ID of this fragment entry link.
	 *
	 * @return the company ID of this fragment entry link
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the configuration of this fragment entry link.
	 *
	 * @return the configuration of this fragment entry link
	 */
	@Override
	public String getConfiguration() {
		return model.getConfiguration();
	}

	@Override
	public com.liferay.portal.kernel.json.JSONObject
		getConfigurationJSONObject() {

		return model.getConfigurationJSONObject();
	}

	@Override
	public com.liferay.portal.kernel.json.JSONObject getConfigurationJSONObject(
		boolean strict) {

		return model.getConfigurationJSONObject(strict);
	}

	/**
	 * Returns the create date of this fragment entry link.
	 *
	 * @return the create date of this fragment entry link
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the css of this fragment entry link.
	 *
	 * @return the css of this fragment entry link
	 */
	@Override
	public String getCss() {
		return model.getCss();
	}

	/**
	 * Returns the ct collection ID of this fragment entry link.
	 *
	 * @return the ct collection ID of this fragment entry link
	 */
	@Override
	public long getCtCollectionId() {
		return model.getCtCollectionId();
	}

	/**
	 * Returns the deleted of this fragment entry link.
	 *
	 * @return the deleted of this fragment entry link
	 */
	@Override
	public boolean getDeleted() {
		return model.getDeleted();
	}

	/**
	 * Returns the editable values of this fragment entry link.
	 *
	 * @return the editable values of this fragment entry link
	 */
	@Override
	public String getEditableValues() {
		return model.getEditableValues();
	}

	@Override
	public com.liferay.portal.kernel.json.JSONObject
		getEditableValuesJSONObject() {

		return model.getEditableValuesJSONObject();
	}

	@Override
	public com.liferay.portal.kernel.json.JSONObject
		getEditableValuesJSONObject(boolean strict) {

		return model.getEditableValuesJSONObject(strict);
	}

	/**
	 * Returns the external reference code of this fragment entry link.
	 *
	 * @return the external reference code of this fragment entry link
	 */
	@Override
	public String getExternalReferenceCode() {
		return model.getExternalReferenceCode();
	}

	/**
	 * Returns the fragment entry ID of this fragment entry link.
	 *
	 * @return the fragment entry ID of this fragment entry link
	 */
	@Override
	public long getFragmentEntryId() {
		return model.getFragmentEntryId();
	}

	/**
	 * Returns the fragment entry link ID of this fragment entry link.
	 *
	 * @return the fragment entry link ID of this fragment entry link
	 */
	@Override
	public long getFragmentEntryLinkId() {
		return model.getFragmentEntryLinkId();
	}

	/**
	 * Returns the group ID of this fragment entry link.
	 *
	 * @return the group ID of this fragment entry link
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the html of this fragment entry link.
	 *
	 * @return the html of this fragment entry link
	 */
	@Override
	public String getHtml() {
		return model.getHtml();
	}

	/**
	 * Returns the js of this fragment entry link.
	 *
	 * @return the js of this fragment entry link
	 */
	@Override
	public String getJs() {
		return model.getJs();
	}

	/**
	 * Returns the last propagation date of this fragment entry link.
	 *
	 * @return the last propagation date of this fragment entry link
	 */
	@Override
	public Date getLastPropagationDate() {
		return model.getLastPropagationDate();
	}

	/**
	 * Returns the last publish date of this fragment entry link.
	 *
	 * @return the last publish date of this fragment entry link
	 */
	@Override
	public Date getLastPublishDate() {
		return model.getLastPublishDate();
	}

	/**
	 * Returns the modified date of this fragment entry link.
	 *
	 * @return the modified date of this fragment entry link
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this fragment entry link.
	 *
	 * @return the mvcc version of this fragment entry link
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the namespace of this fragment entry link.
	 *
	 * @return the namespace of this fragment entry link
	 */
	@Override
	public String getNamespace() {
		return model.getNamespace();
	}

	/**
	 * Returns the original fragment entry link ID of this fragment entry link.
	 *
	 * @return the original fragment entry link ID of this fragment entry link
	 */
	@Override
	public long getOriginalFragmentEntryLinkId() {
		return model.getOriginalFragmentEntryLinkId();
	}

	/**
	 * Returns the plid of this fragment entry link.
	 *
	 * @return the plid of this fragment entry link
	 */
	@Override
	public long getPlid() {
		return model.getPlid();
	}

	/**
	 * Returns the position of this fragment entry link.
	 *
	 * @return the position of this fragment entry link
	 */
	@Override
	public int getPosition() {
		return model.getPosition();
	}

	/**
	 * Returns the primary key of this fragment entry link.
	 *
	 * @return the primary key of this fragment entry link
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the renderer key of this fragment entry link.
	 *
	 * @return the renderer key of this fragment entry link
	 */
	@Override
	public String getRendererKey() {
		return model.getRendererKey();
	}

	/**
	 * Returns the segments experience ID of this fragment entry link.
	 *
	 * @return the segments experience ID of this fragment entry link
	 */
	@Override
	public long getSegmentsExperienceId() {
		return model.getSegmentsExperienceId();
	}

	/**
	 * Returns the type of this fragment entry link.
	 *
	 * @return the type of this fragment entry link
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this fragment entry link.
	 *
	 * @return the user ID of this fragment entry link
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this fragment entry link.
	 *
	 * @return the user name of this fragment entry link
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this fragment entry link.
	 *
	 * @return the user uuid of this fragment entry link
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns the uuid of this fragment entry link.
	 *
	 * @return the uuid of this fragment entry link
	 */
	@Override
	public String getUuid() {
		return model.getUuid();
	}

	@Override
	public boolean isCacheable() {
		return model.isCacheable();
	}

	/**
	 * Returns <code>true</code> if this fragment entry link is deleted.
	 *
	 * @return <code>true</code> if this fragment entry link is deleted; <code>false</code> otherwise
	 */
	@Override
	public boolean isDeleted() {
		return model.isDeleted();
	}

	@Override
	public boolean isLatestVersion()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isLatestVersion();
	}

	@Override
	public boolean isSystem()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.isSystem();
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
	public boolean isTypePortlet() {
		return model.isTypePortlet();
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
	public void setClassName(String className) {
		model.setClassName(className);
	}

	/**
	 * Sets the class name ID of this fragment entry link.
	 *
	 * @param classNameId the class name ID of this fragment entry link
	 */
	@Override
	public void setClassNameId(long classNameId) {
		model.setClassNameId(classNameId);
	}

	/**
	 * Sets the class pk of this fragment entry link.
	 *
	 * @param classPK the class pk of this fragment entry link
	 */
	@Override
	public void setClassPK(long classPK) {
		model.setClassPK(classPK);
	}

	/**
	 * Sets the company ID of this fragment entry link.
	 *
	 * @param companyId the company ID of this fragment entry link
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the configuration of this fragment entry link.
	 *
	 * @param configuration the configuration of this fragment entry link
	 */
	@Override
	public void setConfiguration(String configuration) {
		model.setConfiguration(configuration);
	}

	/**
	 * Sets the create date of this fragment entry link.
	 *
	 * @param createDate the create date of this fragment entry link
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the css of this fragment entry link.
	 *
	 * @param css the css of this fragment entry link
	 */
	@Override
	public void setCss(String css) {
		model.setCss(css);
	}

	/**
	 * Sets the ct collection ID of this fragment entry link.
	 *
	 * @param ctCollectionId the ct collection ID of this fragment entry link
	 */
	@Override
	public void setCtCollectionId(long ctCollectionId) {
		model.setCtCollectionId(ctCollectionId);
	}

	/**
	 * Sets whether this fragment entry link is deleted.
	 *
	 * @param deleted the deleted of this fragment entry link
	 */
	@Override
	public void setDeleted(boolean deleted) {
		model.setDeleted(deleted);
	}

	/**
	 * Sets the editable values of this fragment entry link.
	 *
	 * @param editableValues the editable values of this fragment entry link
	 */
	@Override
	public void setEditableValues(String editableValues) {
		model.setEditableValues(editableValues);
	}

	/**
	 * Sets the external reference code of this fragment entry link.
	 *
	 * @param externalReferenceCode the external reference code of this fragment entry link
	 */
	@Override
	public void setExternalReferenceCode(String externalReferenceCode) {
		model.setExternalReferenceCode(externalReferenceCode);
	}

	/**
	 * Sets the fragment entry ID of this fragment entry link.
	 *
	 * @param fragmentEntryId the fragment entry ID of this fragment entry link
	 */
	@Override
	public void setFragmentEntryId(long fragmentEntryId) {
		model.setFragmentEntryId(fragmentEntryId);
	}

	/**
	 * Sets the fragment entry link ID of this fragment entry link.
	 *
	 * @param fragmentEntryLinkId the fragment entry link ID of this fragment entry link
	 */
	@Override
	public void setFragmentEntryLinkId(long fragmentEntryLinkId) {
		model.setFragmentEntryLinkId(fragmentEntryLinkId);
	}

	/**
	 * Sets the group ID of this fragment entry link.
	 *
	 * @param groupId the group ID of this fragment entry link
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the html of this fragment entry link.
	 *
	 * @param html the html of this fragment entry link
	 */
	@Override
	public void setHtml(String html) {
		model.setHtml(html);
	}

	/**
	 * Sets the js of this fragment entry link.
	 *
	 * @param js the js of this fragment entry link
	 */
	@Override
	public void setJs(String js) {
		model.setJs(js);
	}

	/**
	 * Sets the last propagation date of this fragment entry link.
	 *
	 * @param lastPropagationDate the last propagation date of this fragment entry link
	 */
	@Override
	public void setLastPropagationDate(Date lastPropagationDate) {
		model.setLastPropagationDate(lastPropagationDate);
	}

	/**
	 * Sets the last publish date of this fragment entry link.
	 *
	 * @param lastPublishDate the last publish date of this fragment entry link
	 */
	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		model.setLastPublishDate(lastPublishDate);
	}

	/**
	 * Sets the modified date of this fragment entry link.
	 *
	 * @param modifiedDate the modified date of this fragment entry link
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this fragment entry link.
	 *
	 * @param mvccVersion the mvcc version of this fragment entry link
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the namespace of this fragment entry link.
	 *
	 * @param namespace the namespace of this fragment entry link
	 */
	@Override
	public void setNamespace(String namespace) {
		model.setNamespace(namespace);
	}

	/**
	 * Sets the original fragment entry link ID of this fragment entry link.
	 *
	 * @param originalFragmentEntryLinkId the original fragment entry link ID of this fragment entry link
	 */
	@Override
	public void setOriginalFragmentEntryLinkId(
		long originalFragmentEntryLinkId) {

		model.setOriginalFragmentEntryLinkId(originalFragmentEntryLinkId);
	}

	/**
	 * Sets the plid of this fragment entry link.
	 *
	 * @param plid the plid of this fragment entry link
	 */
	@Override
	public void setPlid(long plid) {
		model.setPlid(plid);
	}

	/**
	 * Sets the position of this fragment entry link.
	 *
	 * @param position the position of this fragment entry link
	 */
	@Override
	public void setPosition(int position) {
		model.setPosition(position);
	}

	/**
	 * Sets the primary key of this fragment entry link.
	 *
	 * @param primaryKey the primary key of this fragment entry link
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the renderer key of this fragment entry link.
	 *
	 * @param rendererKey the renderer key of this fragment entry link
	 */
	@Override
	public void setRendererKey(String rendererKey) {
		model.setRendererKey(rendererKey);
	}

	/**
	 * Sets the segments experience ID of this fragment entry link.
	 *
	 * @param segmentsExperienceId the segments experience ID of this fragment entry link
	 */
	@Override
	public void setSegmentsExperienceId(long segmentsExperienceId) {
		model.setSegmentsExperienceId(segmentsExperienceId);
	}

	/**
	 * Sets the type of this fragment entry link.
	 *
	 * @param type the type of this fragment entry link
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this fragment entry link.
	 *
	 * @param userId the user ID of this fragment entry link
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this fragment entry link.
	 *
	 * @param userName the user name of this fragment entry link
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this fragment entry link.
	 *
	 * @param userUuid the user uuid of this fragment entry link
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	/**
	 * Sets the uuid of this fragment entry link.
	 *
	 * @param uuid the uuid of this fragment entry link
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
	public Map<String, Function<FragmentEntryLink, Object>>
		getAttributeGetterFunctions() {

		return model.getAttributeGetterFunctions();
	}

	@Override
	public Map<String, BiConsumer<FragmentEntryLink, Object>>
		getAttributeSetterBiConsumers() {

		return model.getAttributeSetterBiConsumers();
	}

	@Override
	public StagedModelType getStagedModelType() {
		return model.getStagedModelType();
	}

	@Override
	protected FragmentEntryLinkWrapper wrap(
		FragmentEntryLink fragmentEntryLink) {

		return new FragmentEntryLinkWrapper(fragmentEntryLink);
	}

}