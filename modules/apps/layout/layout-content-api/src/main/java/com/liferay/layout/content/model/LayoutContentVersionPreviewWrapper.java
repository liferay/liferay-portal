/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link LayoutContentVersionPreview}.
 * </p>
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreview
 * @generated
 */
public class LayoutContentVersionPreviewWrapper
	extends BaseModelWrapper<LayoutContentVersionPreview>
	implements LayoutContentVersionPreview,
			   ModelWrapper<LayoutContentVersionPreview> {

	public LayoutContentVersionPreviewWrapper(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		super(layoutContentVersionPreview);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put(
			"layoutContentVersionPreviewId",
			getLayoutContentVersionPreviewId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("layoutContentVersionId", getLayoutContentVersionId());
		attributes.put("html", getHtml());
		attributes.put("languageId", getLanguageId());
		attributes.put("segmentsExperienceERC", getSegmentsExperienceERC());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long layoutContentVersionPreviewId = (Long)attributes.get(
			"layoutContentVersionPreviewId");

		if (layoutContentVersionPreviewId != null) {
			setLayoutContentVersionPreviewId(layoutContentVersionPreviewId);
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

		Long layoutContentVersionId = (Long)attributes.get(
			"layoutContentVersionId");

		if (layoutContentVersionId != null) {
			setLayoutContentVersionId(layoutContentVersionId);
		}

		String html = (String)attributes.get("html");

		if (html != null) {
			setHtml(html);
		}

		String languageId = (String)attributes.get("languageId");

		if (languageId != null) {
			setLanguageId(languageId);
		}

		String segmentsExperienceERC = (String)attributes.get(
			"segmentsExperienceERC");

		if (segmentsExperienceERC != null) {
			setSegmentsExperienceERC(segmentsExperienceERC);
		}
	}

	@Override
	public LayoutContentVersionPreview cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this layout content version preview.
	 *
	 * @return the company ID of this layout content version preview
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this layout content version preview.
	 *
	 * @return the create date of this layout content version preview
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the group ID of this layout content version preview.
	 *
	 * @return the group ID of this layout content version preview
	 */
	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	/**
	 * Returns the html of this layout content version preview.
	 *
	 * @return the html of this layout content version preview
	 */
	@Override
	public String getHtml() {
		return model.getHtml();
	}

	/**
	 * Returns the language ID of this layout content version preview.
	 *
	 * @return the language ID of this layout content version preview
	 */
	@Override
	public String getLanguageId() {
		return model.getLanguageId();
	}

	/**
	 * Returns the layout content version ID of this layout content version preview.
	 *
	 * @return the layout content version ID of this layout content version preview
	 */
	@Override
	public long getLayoutContentVersionId() {
		return model.getLayoutContentVersionId();
	}

	/**
	 * Returns the layout content version preview ID of this layout content version preview.
	 *
	 * @return the layout content version preview ID of this layout content version preview
	 */
	@Override
	public long getLayoutContentVersionPreviewId() {
		return model.getLayoutContentVersionPreviewId();
	}

	/**
	 * Returns the modified date of this layout content version preview.
	 *
	 * @return the modified date of this layout content version preview
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this layout content version preview.
	 *
	 * @return the mvcc version of this layout content version preview
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this layout content version preview.
	 *
	 * @return the primary key of this layout content version preview
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the segments experience erc of this layout content version preview.
	 *
	 * @return the segments experience erc of this layout content version preview
	 */
	@Override
	public String getSegmentsExperienceERC() {
		return model.getSegmentsExperienceERC();
	}

	/**
	 * Returns the user ID of this layout content version preview.
	 *
	 * @return the user ID of this layout content version preview
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this layout content version preview.
	 *
	 * @return the user name of this layout content version preview
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this layout content version preview.
	 *
	 * @return the user uuid of this layout content version preview
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets the company ID of this layout content version preview.
	 *
	 * @param companyId the company ID of this layout content version preview
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this layout content version preview.
	 *
	 * @param createDate the create date of this layout content version preview
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the group ID of this layout content version preview.
	 *
	 * @param groupId the group ID of this layout content version preview
	 */
	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	/**
	 * Sets the html of this layout content version preview.
	 *
	 * @param html the html of this layout content version preview
	 */
	@Override
	public void setHtml(String html) {
		model.setHtml(html);
	}

	/**
	 * Sets the language ID of this layout content version preview.
	 *
	 * @param languageId the language ID of this layout content version preview
	 */
	@Override
	public void setLanguageId(String languageId) {
		model.setLanguageId(languageId);
	}

	/**
	 * Sets the layout content version ID of this layout content version preview.
	 *
	 * @param layoutContentVersionId the layout content version ID of this layout content version preview
	 */
	@Override
	public void setLayoutContentVersionId(long layoutContentVersionId) {
		model.setLayoutContentVersionId(layoutContentVersionId);
	}

	/**
	 * Sets the layout content version preview ID of this layout content version preview.
	 *
	 * @param layoutContentVersionPreviewId the layout content version preview ID of this layout content version preview
	 */
	@Override
	public void setLayoutContentVersionPreviewId(
		long layoutContentVersionPreviewId) {

		model.setLayoutContentVersionPreviewId(layoutContentVersionPreviewId);
	}

	/**
	 * Sets the modified date of this layout content version preview.
	 *
	 * @param modifiedDate the modified date of this layout content version preview
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this layout content version preview.
	 *
	 * @param mvccVersion the mvcc version of this layout content version preview
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this layout content version preview.
	 *
	 * @param primaryKey the primary key of this layout content version preview
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the segments experience erc of this layout content version preview.
	 *
	 * @param segmentsExperienceERC the segments experience erc of this layout content version preview
	 */
	@Override
	public void setSegmentsExperienceERC(String segmentsExperienceERC) {
		model.setSegmentsExperienceERC(segmentsExperienceERC);
	}

	/**
	 * Sets the user ID of this layout content version preview.
	 *
	 * @param userId the user ID of this layout content version preview
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this layout content version preview.
	 *
	 * @param userName the user name of this layout content version preview
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this layout content version preview.
	 *
	 * @param userUuid the user uuid of this layout content version preview
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected LayoutContentVersionPreviewWrapper wrap(
		LayoutContentVersionPreview layoutContentVersionPreview) {

		return new LayoutContentVersionPreviewWrapper(
			layoutContentVersionPreview);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1591225387