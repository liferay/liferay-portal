/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.audiences.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link AudiencesEntryGroupRel}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AudiencesEntryGroupRel
 * @generated
 */
public class AudiencesEntryGroupRelWrapper
	extends BaseModelWrapper<AudiencesEntryGroupRel>
	implements AudiencesEntryGroupRel, ModelWrapper<AudiencesEntryGroupRel> {

	public AudiencesEntryGroupRelWrapper(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		super(audiencesEntryGroupRel);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put(
			"audiencesEntryGroupRelId", getAudiencesEntryGroupRelId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("audienceEntryERC", getAudienceEntryERC());
		attributes.put("groupERC", getGroupERC());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long audiencesEntryGroupRelId = (Long)attributes.get(
			"audiencesEntryGroupRelId");

		if (audiencesEntryGroupRelId != null) {
			setAudiencesEntryGroupRelId(audiencesEntryGroupRelId);
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

		String audienceEntryERC = (String)attributes.get("audienceEntryERC");

		if (audienceEntryERC != null) {
			setAudienceEntryERC(audienceEntryERC);
		}

		String groupERC = (String)attributes.get("groupERC");

		if (groupERC != null) {
			setGroupERC(groupERC);
		}
	}

	@Override
	public AudiencesEntryGroupRel cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the audience entry erc of this audiences entry group rel.
	 *
	 * @return the audience entry erc of this audiences entry group rel
	 */
	@Override
	public String getAudienceEntryERC() {
		return model.getAudienceEntryERC();
	}

	/**
	 * Returns the audiences entry group rel ID of this audiences entry group rel.
	 *
	 * @return the audiences entry group rel ID of this audiences entry group rel
	 */
	@Override
	public long getAudiencesEntryGroupRelId() {
		return model.getAudiencesEntryGroupRelId();
	}

	/**
	 * Returns the company ID of this audiences entry group rel.
	 *
	 * @return the company ID of this audiences entry group rel
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this audiences entry group rel.
	 *
	 * @return the create date of this audiences entry group rel
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the group erc of this audiences entry group rel.
	 *
	 * @return the group erc of this audiences entry group rel
	 */
	@Override
	public String getGroupERC() {
		return model.getGroupERC();
	}

	/**
	 * Returns the modified date of this audiences entry group rel.
	 *
	 * @return the modified date of this audiences entry group rel
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this audiences entry group rel.
	 *
	 * @return the mvcc version of this audiences entry group rel
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the primary key of this audiences entry group rel.
	 *
	 * @return the primary key of this audiences entry group rel
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the user ID of this audiences entry group rel.
	 *
	 * @return the user ID of this audiences entry group rel
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this audiences entry group rel.
	 *
	 * @return the user name of this audiences entry group rel
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this audiences entry group rel.
	 *
	 * @return the user uuid of this audiences entry group rel
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
	 * Sets the audience entry erc of this audiences entry group rel.
	 *
	 * @param audienceEntryERC the audience entry erc of this audiences entry group rel
	 */
	@Override
	public void setAudienceEntryERC(String audienceEntryERC) {
		model.setAudienceEntryERC(audienceEntryERC);
	}

	/**
	 * Sets the audiences entry group rel ID of this audiences entry group rel.
	 *
	 * @param audiencesEntryGroupRelId the audiences entry group rel ID of this audiences entry group rel
	 */
	@Override
	public void setAudiencesEntryGroupRelId(long audiencesEntryGroupRelId) {
		model.setAudiencesEntryGroupRelId(audiencesEntryGroupRelId);
	}

	/**
	 * Sets the company ID of this audiences entry group rel.
	 *
	 * @param companyId the company ID of this audiences entry group rel
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this audiences entry group rel.
	 *
	 * @param createDate the create date of this audiences entry group rel
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the group erc of this audiences entry group rel.
	 *
	 * @param groupERC the group erc of this audiences entry group rel
	 */
	@Override
	public void setGroupERC(String groupERC) {
		model.setGroupERC(groupERC);
	}

	/**
	 * Sets the modified date of this audiences entry group rel.
	 *
	 * @param modifiedDate the modified date of this audiences entry group rel
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this audiences entry group rel.
	 *
	 * @param mvccVersion the mvcc version of this audiences entry group rel
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the primary key of this audiences entry group rel.
	 *
	 * @param primaryKey the primary key of this audiences entry group rel
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the user ID of this audiences entry group rel.
	 *
	 * @param userId the user ID of this audiences entry group rel
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this audiences entry group rel.
	 *
	 * @param userName the user name of this audiences entry group rel
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this audiences entry group rel.
	 *
	 * @param userUuid the user uuid of this audiences entry group rel
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
	protected AudiencesEntryGroupRelWrapper wrap(
		AudiencesEntryGroupRel audiencesEntryGroupRel) {

		return new AudiencesEntryGroupRelWrapper(audiencesEntryGroupRel);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:591572139