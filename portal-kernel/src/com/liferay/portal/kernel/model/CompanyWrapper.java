/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link Company}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Company
 * @generated
 */
public class CompanyWrapper
	extends BaseModelWrapper<Company>
	implements Company, ModelWrapper<Company> {

	public CompanyWrapper(Company company) {
		super(company);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("webId", getWebId());
		attributes.put("mx", getMx());
		attributes.put("maxUsers", getMaxUsers());
		attributes.put("active", isActive());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long mvccVersion = (Long)attributes.get("mvccVersion");

		if (mvccVersion != null) {
			setMvccVersion(mvccVersion);
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

		String webId = (String)attributes.get("webId");

		if (webId != null) {
			setWebId(webId);
		}

		String mx = (String)attributes.get("mx");

		if (mx != null) {
			setMx(mx);
		}

		Integer maxUsers = (Integer)attributes.get("maxUsers");

		if (maxUsers != null) {
			setMaxUsers(maxUsers);
		}

		Boolean active = (Boolean)attributes.get("active");

		if (active != null) {
			setActive(active);
		}
	}

	@Override
	public Company cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	@Override
	public int compareTo(Company company) {
		return model.compareTo(company);
	}

	/**
	 * Returns the active of this company.
	 *
	 * @return the active of this company
	 */
	@Override
	public boolean getActive() {
		return model.getActive();
	}

	@Override
	public String getAdminName() {
		return model.getAdminName();
	}

	@Override
	public String getAuthType() {
		return model.getAuthType();
	}

	/**
	 * Returns the company ID of this company.
	 *
	 * @return the company ID of this company
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	@Override
	public CompanyInfo getCompanyInfo() {
		return model.getCompanyInfo();
	}

	/**
	 * Returns the create date of this company.
	 *
	 * @return the create date of this company
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link #getGuestUser}
	 */
	@Deprecated
	@Override
	public User getDefaultUser()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getDefaultUser();
	}

	@Override
	public String getDefaultWebId() {
		return model.getDefaultWebId();
	}

	@Override
	public String getEmailAddress() {
		return model.getEmailAddress();
	}

	@Override
	public Group getGroup()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getGroup();
	}

	@Override
	public long getGroupId() {
		return model.getGroupId();
	}

	@Override
	public User getGuestUser()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getGuestUser();
	}

	@Override
	public String getHomeURL() {
		return model.getHomeURL();
	}

	@Override
	public String getIndexNameCurrent() {
		return model.getIndexNameCurrent();
	}

	@Override
	public String getIndexNameNext() {
		return model.getIndexNameNext();
	}

	@Override
	public String getIndustry() {
		return model.getIndustry();
	}

	@Override
	public String getKey() {
		return model.getKey();
	}

	@Override
	public java.security.Key getKeyObj() {
		return model.getKeyObj();
	}

	@Override
	public String getLegalId() {
		return model.getLegalId();
	}

	@Override
	public String getLegalName() {
		return model.getLegalName();
	}

	@Override
	public String getLegalType() {
		return model.getLegalType();
	}

	@Override
	public java.util.Locale getLocale()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getLocale();
	}

	@Override
	public long getLogoId() {
		return model.getLogoId();
	}

	/**
	 * Returns the max users of this company.
	 *
	 * @return the max users of this company
	 */
	@Override
	public int getMaxUsers() {
		return model.getMaxUsers();
	}

	/**
	 * Returns the modified date of this company.
	 *
	 * @return the modified date of this company
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this company.
	 *
	 * @return the mvcc version of this company
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the mx of this company.
	 *
	 * @return the mx of this company
	 */
	@Override
	public String getMx() {
		return model.getMx();
	}

	@Override
	public String getName() {
		return model.getName();
	}

	@Override
	public String getPortalURL(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getPortalURL(groupId);
	}

	@Override
	public String getPortalURL(long groupId, boolean privateLayout)
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getPortalURL(groupId, privateLayout);
	}

	/**
	 * Returns the primary key of this company.
	 *
	 * @return the primary key of this company
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	@Override
	public String getShortName()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getShortName();
	}

	@Override
	public String getSicCode() {
		return model.getSicCode();
	}

	@Override
	public String getSize() {
		return model.getSize();
	}

	@Override
	public String getTickerSymbol() {
		return model.getTickerSymbol();
	}

	@Override
	public java.util.TimeZone getTimeZone()
		throws com.liferay.portal.kernel.exception.PortalException {

		return model.getTimeZone();
	}

	@Override
	public String getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this company.
	 *
	 * @return the user ID of this company
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this company.
	 *
	 * @return the user name of this company
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this company.
	 *
	 * @return the user uuid of this company
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	@Override
	public String getVirtualHostname() {
		return model.getVirtualHostname();
	}

	/**
	 * Returns the web ID of this company.
	 *
	 * @return the web ID of this company
	 */
	@Override
	public String getWebId() {
		return model.getWebId();
	}

	@Override
	public boolean hasCompanyMx(String emailAddress) {
		return model.hasCompanyMx(emailAddress);
	}

	/**
	 * Returns <code>true</code> if this company is active.
	 *
	 * @return <code>true</code> if this company is active; <code>false</code> otherwise
	 */
	@Override
	public boolean isActive() {
		return model.isActive();
	}

	@Override
	public boolean isAutoLogin() {
		return model.isAutoLogin();
	}

	@Override
	public boolean isSendPasswordResetLink() {
		return model.isSendPasswordResetLink();
	}

	@Override
	public boolean isSiteLogo() {
		return model.isSiteLogo();
	}

	@Override
	public boolean isStrangers() {
		return model.isStrangers();
	}

	@Override
	public boolean isStrangersVerify() {
		return model.isStrangersVerify();
	}

	@Override
	public boolean isStrangersWithMx() {
		return model.isStrangersWithMx();
	}

	@Override
	public boolean isUpdatePasswordRequired() {
		return model.isUpdatePasswordRequired();
	}

	@Override
	public void persist() {
		model.persist();
	}

	/**
	 * Sets whether this company is active.
	 *
	 * @param active the active of this company
	 */
	@Override
	public void setActive(boolean active) {
		model.setActive(active);
	}

	/**
	 * Sets the company ID of this company.
	 *
	 * @param companyId the company ID of this company
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this company.
	 *
	 * @param createDate the create date of this company
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	@Override
	public void setGroupId(long groupId) {
		model.setGroupId(groupId);
	}

	@Override
	public void setHomeURL(String homeURL) {
		model.setHomeURL(homeURL);
	}

	@Override
	public void setIndexNameCurrent(String indexNameCurrent) {
		model.setIndexNameCurrent(indexNameCurrent);
	}

	@Override
	public void setIndexNameNext(String indexNameNext) {
		model.setIndexNameNext(indexNameNext);
	}

	@Override
	public void setIndustry(String industry) {
		model.setIndustry(industry);
	}

	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	@Override
	public void setKeyObj(java.security.Key keyObj) {
		model.setKeyObj(keyObj);
	}

	@Override
	public void setLegalId(String legalId) {
		model.setLegalId(legalId);
	}

	@Override
	public void setLegalName(String legalName) {
		model.setLegalName(legalName);
	}

	@Override
	public void setLegalType(String legalType) {
		model.setLegalType(legalType);
	}

	@Override
	public void setLogoId(long logoId) {
		model.setLogoId(logoId);
	}

	/**
	 * Sets the max users of this company.
	 *
	 * @param maxUsers the max users of this company
	 */
	@Override
	public void setMaxUsers(int maxUsers) {
		model.setMaxUsers(maxUsers);
	}

	/**
	 * Sets the modified date of this company.
	 *
	 * @param modifiedDate the modified date of this company
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this company.
	 *
	 * @param mvccVersion the mvcc version of this company
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the mx of this company.
	 *
	 * @param mx the mx of this company
	 */
	@Override
	public void setMx(String mx) {
		model.setMx(mx);
	}

	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets the primary key of this company.
	 *
	 * @param primaryKey the primary key of this company
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	@Override
	public void setSicCode(String sicCode) {
		model.setSicCode(sicCode);
	}

	@Override
	public void setSize(String size) {
		model.setSize(size);
	}

	@Override
	public void setTickerSymbol(String tickerSymbol) {
		model.setTickerSymbol(tickerSymbol);
	}

	@Override
	public void setType(String type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this company.
	 *
	 * @param userId the user ID of this company
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this company.
	 *
	 * @param userName the user name of this company
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this company.
	 *
	 * @param userUuid the user uuid of this company
	 */
	@Override
	public void setUserUuid(String userUuid) {
		model.setUserUuid(userUuid);
	}

	@Override
	public void setVirtualHostname(String virtualHostname) {
		model.setVirtualHostname(virtualHostname);
	}

	/**
	 * Sets the web ID of this company.
	 *
	 * @param webId the web ID of this company
	 */
	@Override
	public void setWebId(String webId) {
		model.setWebId(webId);
	}

	@Override
	public String toXmlString() {
		return model.toXmlString();
	}

	@Override
	protected CompanyWrapper wrap(Company company) {
		return new CompanyWrapper(company);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1560710173