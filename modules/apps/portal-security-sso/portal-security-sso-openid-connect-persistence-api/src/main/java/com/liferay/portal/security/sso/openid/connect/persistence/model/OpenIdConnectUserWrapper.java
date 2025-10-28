/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link OpenIdConnectUser}.
 * </p>
 *
 * @author Arthur Chan
 * @see OpenIdConnectUser
 * @generated
 */
public class OpenIdConnectUserWrapper
	extends BaseModelWrapper<OpenIdConnectUser>
	implements ModelWrapper<OpenIdConnectUser>, OpenIdConnectUser {

	public OpenIdConnectUserWrapper(OpenIdConnectUser openIdConnectUser) {
		super(openIdConnectUser);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("openIdConnectUserId", getOpenIdConnectUserId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("createDate", getCreateDate());
		attributes.put("issuer", getIssuer());
		attributes.put("subject", getSubject());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
		}

		Long openIdConnectUserId = (Long)attributes.get("openIdConnectUserId");

		if (openIdConnectUserId != null) {
			setOpenIdConnectUserId(openIdConnectUserId);
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

		String issuer = (String)attributes.get("issuer");

		if (issuer != null) {
			setIssuer(issuer);
		}

		String subject = (String)attributes.get("subject");

		if (subject != null) {
			setSubject(subject);
		}
	}

	@Override
	public OpenIdConnectUser cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the company ID of this open ID connect user.
	 *
	 * @return the company ID of this open ID connect user
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this open ID connect user.
	 *
	 * @return the create date of this open ID connect user
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the issuer of this open ID connect user.
	 *
	 * @return the issuer of this open ID connect user
	 */
	@Override
	public String getIssuer() {
		return model.getIssuer();
	}

	/**
	 * Returns the mvcc version of this open ID connect user.
	 *
	 * @return the mvcc version of this open ID connect user
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the open ID connect user ID of this open ID connect user.
	 *
	 * @return the open ID connect user ID of this open ID connect user
	 */
	@Override
	public long getOpenIdConnectUserId() {
		return model.getOpenIdConnectUserId();
	}

	/**
	 * Returns the open ID connect user uuid of this open ID connect user.
	 *
	 * @return the open ID connect user uuid of this open ID connect user
	 */
	@Override
	public String getOpenIdConnectUserUuid() {
		return model.getOpenIdConnectUserUuid();
	}

	/**
	 * Returns the primary key of this open ID connect user.
	 *
	 * @return the primary key of this open ID connect user
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the subject of this open ID connect user.
	 *
	 * @return the subject of this open ID connect user
	 */
	@Override
	public String getSubject() {
		return model.getSubject();
	}

	/**
	 * Returns the user ID of this open ID connect user.
	 *
	 * @return the user ID of this open ID connect user
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user uuid of this open ID connect user.
	 *
	 * @return the user uuid of this open ID connect user
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
	 * Sets the company ID of this open ID connect user.
	 *
	 * @param companyId the company ID of this open ID connect user
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this open ID connect user.
	 *
	 * @param createDate the create date of this open ID connect user
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the issuer of this open ID connect user.
	 *
	 * @param issuer the issuer of this open ID connect user
	 */
	@Override
	public void setIssuer(String issuer) {
		model.setIssuer(issuer);
	}

	/**
	 * Sets the mvcc version of this open ID connect user.
	 *
	 * @param mvccVersion the mvcc version of this open ID connect user
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the open ID connect user ID of this open ID connect user.
	 *
	 * @param openIdConnectUserId the open ID connect user ID of this open ID connect user
	 */
	@Override
	public void setOpenIdConnectUserId(long openIdConnectUserId) {
		model.setOpenIdConnectUserId(openIdConnectUserId);
	}

	/**
	 * Sets the open ID connect user uuid of this open ID connect user.
	 *
	 * @param openIdConnectUserUuid the open ID connect user uuid of this open ID connect user
	 */
	@Override
	public void setOpenIdConnectUserUuid(String openIdConnectUserUuid) {
		model.setOpenIdConnectUserUuid(openIdConnectUserUuid);
	}

	/**
	 * Sets the primary key of this open ID connect user.
	 *
	 * @param primaryKey the primary key of this open ID connect user
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the subject of this open ID connect user.
	 *
	 * @param subject the subject of this open ID connect user
	 */
	@Override
	public void setSubject(String subject) {
		model.setSubject(subject);
	}

	/**
	 * Sets the user ID of this open ID connect user.
	 *
	 * @param userId the user ID of this open ID connect user
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user uuid of this open ID connect user.
	 *
	 * @param userUuid the user uuid of this open ID connect user
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
	protected OpenIdConnectUserWrapper wrap(
		OpenIdConnectUser openIdConnectUser) {

		return new OpenIdConnectUserWrapper(openIdConnectUser);
	}

}