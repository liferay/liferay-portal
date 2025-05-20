/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherBuild}.
 * </p>
 *
 * @author Calvin Keum
 * @see PatcherBuild
 * @generated
 */
public class PatcherBuildWrapper implements PatcherBuild,
	ModelWrapper<PatcherBuild> {
	public PatcherBuildWrapper(PatcherBuild patcherBuild) {
		_patcherBuild = patcherBuild;
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherBuild.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherBuild.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherBuildId", getPatcherBuildId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherAccountId", getPatcherAccountId());
		attributes.put("patcherFixId", getPatcherFixId());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("ticketEntryId", getTicketEntryId());
		attributes.put("hotfixId", getHotfixId());
		attributes.put("name", getName());
		attributes.put("originalName", getOriginalName());
		attributes.put("key", getKey());
		attributes.put("keyVersion", getKeyVersion());
		attributes.put("type", getType());
		attributes.put("latestBuild", getLatestBuild());
		attributes.put("latestKeyBuild", getLatestKeyBuild());
		attributes.put("latestLESATicketBuild", getLatestLESATicketBuild());
		attributes.put("latestSupportTicketBuild", getLatestSupportTicketBuild());
		attributes.put("accountEntryCode", getAccountEntryCode());
		attributes.put("lesaTicket", getLesaTicket());
		attributes.put("lesaTicketVersion", getLesaTicketVersion());
		attributes.put("supportTicket", getSupportTicket());
		attributes.put("supportTicketVersion", getSupportTicketVersion());
		attributes.put("fileName", getFileName());
		attributes.put("sourceName", getSourceName());
		attributes.put("childBuild", getChildBuild());
		attributes.put("comments", getComments());
		attributes.put("qaComments", getQaComments());
		attributes.put("qaStatus", getQaStatus());
		attributes.put("requestKey", getRequestKey());
		attributes.put("notified", getNotified());
		attributes.put("productVersion", getProductVersion());
		attributes.put("status", getStatus());
		attributes.put("statusByUserId", getStatusByUserId());
		attributes.put("statusByUserName", getStatusByUserName());
		attributes.put("statusDate", getStatusDate());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherBuildId = (Long)attributes.get("patcherBuildId");

		if (patcherBuildId != null) {
			setPatcherBuildId(patcherBuildId);
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

		Long patcherAccountId = (Long)attributes.get("patcherAccountId");

		if (patcherAccountId != null) {
			setPatcherAccountId(patcherAccountId);
		}

		Long patcherFixId = (Long)attributes.get("patcherFixId");

		if (patcherFixId != null) {
			setPatcherFixId(patcherFixId);
		}

		Long patcherProductVersionId = (Long)attributes.get(
				"patcherProductVersionId");

		if (patcherProductVersionId != null) {
			setPatcherProductVersionId(patcherProductVersionId);
		}

		Long patcherProjectVersionId = (Long)attributes.get(
				"patcherProjectVersionId");

		if (patcherProjectVersionId != null) {
			setPatcherProjectVersionId(patcherProjectVersionId);
		}

		Long ticketEntryId = (Long)attributes.get("ticketEntryId");

		if (ticketEntryId != null) {
			setTicketEntryId(ticketEntryId);
		}

		Long hotfixId = (Long)attributes.get("hotfixId");

		if (hotfixId != null) {
			setHotfixId(hotfixId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String originalName = (String)attributes.get("originalName");

		if (originalName != null) {
			setOriginalName(originalName);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Double keyVersion = (Double)attributes.get("keyVersion");

		if (keyVersion != null) {
			setKeyVersion(keyVersion);
		}

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
		}

		Boolean latestBuild = (Boolean)attributes.get("latestBuild");

		if (latestBuild != null) {
			setLatestBuild(latestBuild);
		}

		Boolean latestKeyBuild = (Boolean)attributes.get("latestKeyBuild");

		if (latestKeyBuild != null) {
			setLatestKeyBuild(latestKeyBuild);
		}

		Boolean latestLESATicketBuild = (Boolean)attributes.get(
				"latestLESATicketBuild");

		if (latestLESATicketBuild != null) {
			setLatestLESATicketBuild(latestLESATicketBuild);
		}

		Boolean latestSupportTicketBuild = (Boolean)attributes.get(
				"latestSupportTicketBuild");

		if (latestSupportTicketBuild != null) {
			setLatestSupportTicketBuild(latestSupportTicketBuild);
		}

		String accountEntryCode = (String)attributes.get("accountEntryCode");

		if (accountEntryCode != null) {
			setAccountEntryCode(accountEntryCode);
		}

		String lesaTicket = (String)attributes.get("lesaTicket");

		if (lesaTicket != null) {
			setLesaTicket(lesaTicket);
		}

		Double lesaTicketVersion = (Double)attributes.get("lesaTicketVersion");

		if (lesaTicketVersion != null) {
			setLesaTicketVersion(lesaTicketVersion);
		}

		String supportTicket = (String)attributes.get("supportTicket");

		if (supportTicket != null) {
			setSupportTicket(supportTicket);
		}

		Double supportTicketVersion = (Double)attributes.get(
				"supportTicketVersion");

		if (supportTicketVersion != null) {
			setSupportTicketVersion(supportTicketVersion);
		}

		String fileName = (String)attributes.get("fileName");

		if (fileName != null) {
			setFileName(fileName);
		}

		String sourceName = (String)attributes.get("sourceName");

		if (sourceName != null) {
			setSourceName(sourceName);
		}

		Boolean childBuild = (Boolean)attributes.get("childBuild");

		if (childBuild != null) {
			setChildBuild(childBuild);
		}

		String comments = (String)attributes.get("comments");

		if (comments != null) {
			setComments(comments);
		}

		String qaComments = (String)attributes.get("qaComments");

		if (qaComments != null) {
			setQaComments(qaComments);
		}

		Integer qaStatus = (Integer)attributes.get("qaStatus");

		if (qaStatus != null) {
			setQaStatus(qaStatus);
		}

		String requestKey = (String)attributes.get("requestKey");

		if (requestKey != null) {
			setRequestKey(requestKey);
		}

		Boolean notified = (Boolean)attributes.get("notified");

		if (notified != null) {
			setNotified(notified);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
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

	/**
	* Returns the primary key of this patcher build.
	*
	* @return the primary key of this patcher build
	*/
	@Override
	public long getPrimaryKey() {
		return _patcherBuild.getPrimaryKey();
	}

	/**
	* Sets the primary key of this patcher build.
	*
	* @param primaryKey the primary key of this patcher build
	*/
	@Override
	public void setPrimaryKey(long primaryKey) {
		_patcherBuild.setPrimaryKey(primaryKey);
	}

	/**
	* Returns the patcher build ID of this patcher build.
	*
	* @return the patcher build ID of this patcher build
	*/
	@Override
	public long getPatcherBuildId() {
		return _patcherBuild.getPatcherBuildId();
	}

	/**
	* Sets the patcher build ID of this patcher build.
	*
	* @param patcherBuildId the patcher build ID of this patcher build
	*/
	@Override
	public void setPatcherBuildId(long patcherBuildId) {
		_patcherBuild.setPatcherBuildId(patcherBuildId);
	}

	/**
	* Returns the company ID of this patcher build.
	*
	* @return the company ID of this patcher build
	*/
	@Override
	public long getCompanyId() {
		return _patcherBuild.getCompanyId();
	}

	/**
	* Sets the company ID of this patcher build.
	*
	* @param companyId the company ID of this patcher build
	*/
	@Override
	public void setCompanyId(long companyId) {
		_patcherBuild.setCompanyId(companyId);
	}

	/**
	* Returns the user ID of this patcher build.
	*
	* @return the user ID of this patcher build
	*/
	@Override
	public long getUserId() {
		return _patcherBuild.getUserId();
	}

	/**
	* Sets the user ID of this patcher build.
	*
	* @param userId the user ID of this patcher build
	*/
	@Override
	public void setUserId(long userId) {
		_patcherBuild.setUserId(userId);
	}

	/**
	* Returns the user uuid of this patcher build.
	*
	* @return the user uuid of this patcher build
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuild.getUserUuid();
	}

	/**
	* Sets the user uuid of this patcher build.
	*
	* @param userUuid the user uuid of this patcher build
	*/
	@Override
	public void setUserUuid(java.lang.String userUuid) {
		_patcherBuild.setUserUuid(userUuid);
	}

	/**
	* Returns the user name of this patcher build.
	*
	* @return the user name of this patcher build
	*/
	@Override
	public java.lang.String getUserName() {
		return _patcherBuild.getUserName();
	}

	/**
	* Sets the user name of this patcher build.
	*
	* @param userName the user name of this patcher build
	*/
	@Override
	public void setUserName(java.lang.String userName) {
		_patcherBuild.setUserName(userName);
	}

	/**
	* Returns the create date of this patcher build.
	*
	* @return the create date of this patcher build
	*/
	@Override
	public java.util.Date getCreateDate() {
		return _patcherBuild.getCreateDate();
	}

	/**
	* Sets the create date of this patcher build.
	*
	* @param createDate the create date of this patcher build
	*/
	@Override
	public void setCreateDate(java.util.Date createDate) {
		_patcherBuild.setCreateDate(createDate);
	}

	/**
	* Returns the modified date of this patcher build.
	*
	* @return the modified date of this patcher build
	*/
	@Override
	public java.util.Date getModifiedDate() {
		return _patcherBuild.getModifiedDate();
	}

	/**
	* Sets the modified date of this patcher build.
	*
	* @param modifiedDate the modified date of this patcher build
	*/
	@Override
	public void setModifiedDate(java.util.Date modifiedDate) {
		_patcherBuild.setModifiedDate(modifiedDate);
	}

	/**
	* Returns the patcher account ID of this patcher build.
	*
	* @return the patcher account ID of this patcher build
	*/
	@Override
	public long getPatcherAccountId() {
		return _patcherBuild.getPatcherAccountId();
	}

	/**
	* Sets the patcher account ID of this patcher build.
	*
	* @param patcherAccountId the patcher account ID of this patcher build
	*/
	@Override
	public void setPatcherAccountId(long patcherAccountId) {
		_patcherBuild.setPatcherAccountId(patcherAccountId);
	}

	/**
	* Returns the patcher fix ID of this patcher build.
	*
	* @return the patcher fix ID of this patcher build
	*/
	@Override
	public long getPatcherFixId() {
		return _patcherBuild.getPatcherFixId();
	}

	/**
	* Sets the patcher fix ID of this patcher build.
	*
	* @param patcherFixId the patcher fix ID of this patcher build
	*/
	@Override
	public void setPatcherFixId(long patcherFixId) {
		_patcherBuild.setPatcherFixId(patcherFixId);
	}

	/**
	* Returns the patcher product version ID of this patcher build.
	*
	* @return the patcher product version ID of this patcher build
	*/
	@Override
	public long getPatcherProductVersionId() {
		return _patcherBuild.getPatcherProductVersionId();
	}

	/**
	* Sets the patcher product version ID of this patcher build.
	*
	* @param patcherProductVersionId the patcher product version ID of this patcher build
	*/
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherBuild.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	* Returns the patcher project version ID of this patcher build.
	*
	* @return the patcher project version ID of this patcher build
	*/
	@Override
	public long getPatcherProjectVersionId() {
		return _patcherBuild.getPatcherProjectVersionId();
	}

	/**
	* Sets the patcher project version ID of this patcher build.
	*
	* @param patcherProjectVersionId the patcher project version ID of this patcher build
	*/
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherBuild.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	* Returns the ticket entry ID of this patcher build.
	*
	* @return the ticket entry ID of this patcher build
	*/
	@Override
	public long getTicketEntryId() {
		return _patcherBuild.getTicketEntryId();
	}

	/**
	* Sets the ticket entry ID of this patcher build.
	*
	* @param ticketEntryId the ticket entry ID of this patcher build
	*/
	@Override
	public void setTicketEntryId(long ticketEntryId) {
		_patcherBuild.setTicketEntryId(ticketEntryId);
	}

	/**
	* Returns the hotfix ID of this patcher build.
	*
	* @return the hotfix ID of this patcher build
	*/
	@Override
	public long getHotfixId() {
		return _patcherBuild.getHotfixId();
	}

	/**
	* Sets the hotfix ID of this patcher build.
	*
	* @param hotfixId the hotfix ID of this patcher build
	*/
	@Override
	public void setHotfixId(long hotfixId) {
		_patcherBuild.setHotfixId(hotfixId);
	}

	/**
	* Returns the name of this patcher build.
	*
	* @return the name of this patcher build
	*/
	@Override
	public java.lang.String getName() {
		return _patcherBuild.getName();
	}

	/**
	* Sets the name of this patcher build.
	*
	* @param name the name of this patcher build
	*/
	@Override
	public void setName(java.lang.String name) {
		_patcherBuild.setName(name);
	}

	/**
	* Returns the original name of this patcher build.
	*
	* @return the original name of this patcher build
	*/
	@Override
	public java.lang.String getOriginalName() {
		return _patcherBuild.getOriginalName();
	}

	/**
	* Sets the original name of this patcher build.
	*
	* @param originalName the original name of this patcher build
	*/
	@Override
	public void setOriginalName(java.lang.String originalName) {
		_patcherBuild.setOriginalName(originalName);
	}

	/**
	* Returns the key of this patcher build.
	*
	* @return the key of this patcher build
	*/
	@Override
	public java.lang.String getKey() {
		return _patcherBuild.getKey();
	}

	/**
	* Sets the key of this patcher build.
	*
	* @param key the key of this patcher build
	*/
	@Override
	public void setKey(java.lang.String key) {
		_patcherBuild.setKey(key);
	}

	/**
	* Returns the key version of this patcher build.
	*
	* @return the key version of this patcher build
	*/
	@Override
	public double getKeyVersion() {
		return _patcherBuild.getKeyVersion();
	}

	/**
	* Sets the key version of this patcher build.
	*
	* @param keyVersion the key version of this patcher build
	*/
	@Override
	public void setKeyVersion(double keyVersion) {
		_patcherBuild.setKeyVersion(keyVersion);
	}

	/**
	* Returns the type of this patcher build.
	*
	* @return the type of this patcher build
	*/
	@Override
	public int getType() {
		return _patcherBuild.getType();
	}

	/**
	* Sets the type of this patcher build.
	*
	* @param type the type of this patcher build
	*/
	@Override
	public void setType(int type) {
		_patcherBuild.setType(type);
	}

	/**
	* Returns the latest build of this patcher build.
	*
	* @return the latest build of this patcher build
	*/
	@Override
	public boolean getLatestBuild() {
		return _patcherBuild.getLatestBuild();
	}

	/**
	* Returns <code>true</code> if this patcher build is latest build.
	*
	* @return <code>true</code> if this patcher build is latest build; <code>false</code> otherwise
	*/
	@Override
	public boolean isLatestBuild() {
		return _patcherBuild.isLatestBuild();
	}

	/**
	* Sets whether this patcher build is latest build.
	*
	* @param latestBuild the latest build of this patcher build
	*/
	@Override
	public void setLatestBuild(boolean latestBuild) {
		_patcherBuild.setLatestBuild(latestBuild);
	}

	/**
	* Returns the latest key build of this patcher build.
	*
	* @return the latest key build of this patcher build
	*/
	@Override
	public boolean getLatestKeyBuild() {
		return _patcherBuild.getLatestKeyBuild();
	}

	/**
	* Returns <code>true</code> if this patcher build is latest key build.
	*
	* @return <code>true</code> if this patcher build is latest key build; <code>false</code> otherwise
	*/
	@Override
	public boolean isLatestKeyBuild() {
		return _patcherBuild.isLatestKeyBuild();
	}

	/**
	* Sets whether this patcher build is latest key build.
	*
	* @param latestKeyBuild the latest key build of this patcher build
	*/
	@Override
	public void setLatestKeyBuild(boolean latestKeyBuild) {
		_patcherBuild.setLatestKeyBuild(latestKeyBuild);
	}

	/**
	* Returns the latest l e s a ticket build of this patcher build.
	*
	* @return the latest l e s a ticket build of this patcher build
	*/
	@Override
	public boolean getLatestLESATicketBuild() {
		return _patcherBuild.getLatestLESATicketBuild();
	}

	/**
	* Returns <code>true</code> if this patcher build is latest l e s a ticket build.
	*
	* @return <code>true</code> if this patcher build is latest l e s a ticket build; <code>false</code> otherwise
	*/
	@Override
	public boolean isLatestLESATicketBuild() {
		return _patcherBuild.isLatestLESATicketBuild();
	}

	/**
	* Sets whether this patcher build is latest l e s a ticket build.
	*
	* @param latestLESATicketBuild the latest l e s a ticket build of this patcher build
	*/
	@Override
	public void setLatestLESATicketBuild(boolean latestLESATicketBuild) {
		_patcherBuild.setLatestLESATicketBuild(latestLESATicketBuild);
	}

	/**
	* Returns the latest support ticket build of this patcher build.
	*
	* @return the latest support ticket build of this patcher build
	*/
	@Override
	public boolean getLatestSupportTicketBuild() {
		return _patcherBuild.getLatestSupportTicketBuild();
	}

	/**
	* Returns <code>true</code> if this patcher build is latest support ticket build.
	*
	* @return <code>true</code> if this patcher build is latest support ticket build; <code>false</code> otherwise
	*/
	@Override
	public boolean isLatestSupportTicketBuild() {
		return _patcherBuild.isLatestSupportTicketBuild();
	}

	/**
	* Sets whether this patcher build is latest support ticket build.
	*
	* @param latestSupportTicketBuild the latest support ticket build of this patcher build
	*/
	@Override
	public void setLatestSupportTicketBuild(boolean latestSupportTicketBuild) {
		_patcherBuild.setLatestSupportTicketBuild(latestSupportTicketBuild);
	}

	/**
	* Returns the account entry code of this patcher build.
	*
	* @return the account entry code of this patcher build
	*/
	@Override
	public java.lang.String getAccountEntryCode() {
		return _patcherBuild.getAccountEntryCode();
	}

	/**
	* Sets the account entry code of this patcher build.
	*
	* @param accountEntryCode the account entry code of this patcher build
	*/
	@Override
	public void setAccountEntryCode(java.lang.String accountEntryCode) {
		_patcherBuild.setAccountEntryCode(accountEntryCode);
	}

	/**
	* Returns the lesa ticket of this patcher build.
	*
	* @return the lesa ticket of this patcher build
	*/
	@Override
	public java.lang.String getLesaTicket() {
		return _patcherBuild.getLesaTicket();
	}

	/**
	* Sets the lesa ticket of this patcher build.
	*
	* @param lesaTicket the lesa ticket of this patcher build
	*/
	@Override
	public void setLesaTicket(java.lang.String lesaTicket) {
		_patcherBuild.setLesaTicket(lesaTicket);
	}

	/**
	* Returns the lesa ticket version of this patcher build.
	*
	* @return the lesa ticket version of this patcher build
	*/
	@Override
	public double getLesaTicketVersion() {
		return _patcherBuild.getLesaTicketVersion();
	}

	/**
	* Sets the lesa ticket version of this patcher build.
	*
	* @param lesaTicketVersion the lesa ticket version of this patcher build
	*/
	@Override
	public void setLesaTicketVersion(double lesaTicketVersion) {
		_patcherBuild.setLesaTicketVersion(lesaTicketVersion);
	}

	/**
	* Returns the support ticket of this patcher build.
	*
	* @return the support ticket of this patcher build
	*/
	@Override
	public java.lang.String getSupportTicket() {
		return _patcherBuild.getSupportTicket();
	}

	/**
	* Sets the support ticket of this patcher build.
	*
	* @param supportTicket the support ticket of this patcher build
	*/
	@Override
	public void setSupportTicket(java.lang.String supportTicket) {
		_patcherBuild.setSupportTicket(supportTicket);
	}

	/**
	* Returns the support ticket version of this patcher build.
	*
	* @return the support ticket version of this patcher build
	*/
	@Override
	public double getSupportTicketVersion() {
		return _patcherBuild.getSupportTicketVersion();
	}

	/**
	* Sets the support ticket version of this patcher build.
	*
	* @param supportTicketVersion the support ticket version of this patcher build
	*/
	@Override
	public void setSupportTicketVersion(double supportTicketVersion) {
		_patcherBuild.setSupportTicketVersion(supportTicketVersion);
	}

	/**
	* Returns the file name of this patcher build.
	*
	* @return the file name of this patcher build
	*/
	@Override
	public java.lang.String getFileName() {
		return _patcherBuild.getFileName();
	}

	/**
	* Sets the file name of this patcher build.
	*
	* @param fileName the file name of this patcher build
	*/
	@Override
	public void setFileName(java.lang.String fileName) {
		_patcherBuild.setFileName(fileName);
	}

	/**
	* Returns the source name of this patcher build.
	*
	* @return the source name of this patcher build
	*/
	@Override
	public java.lang.String getSourceName() {
		return _patcherBuild.getSourceName();
	}

	/**
	* Sets the source name of this patcher build.
	*
	* @param sourceName the source name of this patcher build
	*/
	@Override
	public void setSourceName(java.lang.String sourceName) {
		_patcherBuild.setSourceName(sourceName);
	}

	/**
	* Returns the child build of this patcher build.
	*
	* @return the child build of this patcher build
	*/
	@Override
	public boolean getChildBuild() {
		return _patcherBuild.getChildBuild();
	}

	/**
	* Returns <code>true</code> if this patcher build is child build.
	*
	* @return <code>true</code> if this patcher build is child build; <code>false</code> otherwise
	*/
	@Override
	public boolean isChildBuild() {
		return _patcherBuild.isChildBuild();
	}

	/**
	* Sets whether this patcher build is child build.
	*
	* @param childBuild the child build of this patcher build
	*/
	@Override
	public void setChildBuild(boolean childBuild) {
		_patcherBuild.setChildBuild(childBuild);
	}

	/**
	* Returns the comments of this patcher build.
	*
	* @return the comments of this patcher build
	*/
	@Override
	public java.lang.String getComments() {
		return _patcherBuild.getComments();
	}

	/**
	* Sets the comments of this patcher build.
	*
	* @param comments the comments of this patcher build
	*/
	@Override
	public void setComments(java.lang.String comments) {
		_patcherBuild.setComments(comments);
	}

	/**
	* Returns the qa comments of this patcher build.
	*
	* @return the qa comments of this patcher build
	*/
	@Override
	public java.lang.String getQaComments() {
		return _patcherBuild.getQaComments();
	}

	/**
	* Sets the qa comments of this patcher build.
	*
	* @param qaComments the qa comments of this patcher build
	*/
	@Override
	public void setQaComments(java.lang.String qaComments) {
		_patcherBuild.setQaComments(qaComments);
	}

	/**
	* Returns the qa status of this patcher build.
	*
	* @return the qa status of this patcher build
	*/
	@Override
	public int getQaStatus() {
		return _patcherBuild.getQaStatus();
	}

	/**
	* Sets the qa status of this patcher build.
	*
	* @param qaStatus the qa status of this patcher build
	*/
	@Override
	public void setQaStatus(int qaStatus) {
		_patcherBuild.setQaStatus(qaStatus);
	}

	/**
	* Returns the request key of this patcher build.
	*
	* @return the request key of this patcher build
	*/
	@Override
	public java.lang.String getRequestKey() {
		return _patcherBuild.getRequestKey();
	}

	/**
	* Sets the request key of this patcher build.
	*
	* @param requestKey the request key of this patcher build
	*/
	@Override
	public void setRequestKey(java.lang.String requestKey) {
		_patcherBuild.setRequestKey(requestKey);
	}

	/**
	* Returns the notified of this patcher build.
	*
	* @return the notified of this patcher build
	*/
	@Override
	public boolean getNotified() {
		return _patcherBuild.getNotified();
	}

	/**
	* Returns <code>true</code> if this patcher build is notified.
	*
	* @return <code>true</code> if this patcher build is notified; <code>false</code> otherwise
	*/
	@Override
	public boolean isNotified() {
		return _patcherBuild.isNotified();
	}

	/**
	* Sets whether this patcher build is notified.
	*
	* @param notified the notified of this patcher build
	*/
	@Override
	public void setNotified(boolean notified) {
		_patcherBuild.setNotified(notified);
	}

	/**
	* Returns the product version of this patcher build.
	*
	* @return the product version of this patcher build
	*/
	@Override
	public int getProductVersion() {
		return _patcherBuild.getProductVersion();
	}

	/**
	* Sets the product version of this patcher build.
	*
	* @param productVersion the product version of this patcher build
	*/
	@Override
	public void setProductVersion(int productVersion) {
		_patcherBuild.setProductVersion(productVersion);
	}

	/**
	* Returns the status of this patcher build.
	*
	* @return the status of this patcher build
	*/
	@Override
	public int getStatus() {
		return _patcherBuild.getStatus();
	}

	/**
	* Sets the status of this patcher build.
	*
	* @param status the status of this patcher build
	*/
	@Override
	public void setStatus(int status) {
		_patcherBuild.setStatus(status);
	}

	/**
	* Returns the status by user ID of this patcher build.
	*
	* @return the status by user ID of this patcher build
	*/
	@Override
	public long getStatusByUserId() {
		return _patcherBuild.getStatusByUserId();
	}

	/**
	* Sets the status by user ID of this patcher build.
	*
	* @param statusByUserId the status by user ID of this patcher build
	*/
	@Override
	public void setStatusByUserId(long statusByUserId) {
		_patcherBuild.setStatusByUserId(statusByUserId);
	}

	/**
	* Returns the status by user uuid of this patcher build.
	*
	* @return the status by user uuid of this patcher build
	* @throws SystemException if a system exception occurred
	*/
	@Override
	public java.lang.String getStatusByUserUuid()
		throws com.liferay.portal.kernel.exception.SystemException {
		return _patcherBuild.getStatusByUserUuid();
	}

	/**
	* Sets the status by user uuid of this patcher build.
	*
	* @param statusByUserUuid the status by user uuid of this patcher build
	*/
	@Override
	public void setStatusByUserUuid(java.lang.String statusByUserUuid) {
		_patcherBuild.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	* Returns the status by user name of this patcher build.
	*
	* @return the status by user name of this patcher build
	*/
	@Override
	public java.lang.String getStatusByUserName() {
		return _patcherBuild.getStatusByUserName();
	}

	/**
	* Sets the status by user name of this patcher build.
	*
	* @param statusByUserName the status by user name of this patcher build
	*/
	@Override
	public void setStatusByUserName(java.lang.String statusByUserName) {
		_patcherBuild.setStatusByUserName(statusByUserName);
	}

	/**
	* Returns the status date of this patcher build.
	*
	* @return the status date of this patcher build
	*/
	@Override
	public java.util.Date getStatusDate() {
		return _patcherBuild.getStatusDate();
	}

	/**
	* Sets the status date of this patcher build.
	*
	* @param statusDate the status date of this patcher build
	*/
	@Override
	public void setStatusDate(java.util.Date statusDate) {
		_patcherBuild.setStatusDate(statusDate);
	}

	/**
	* @deprecated As of 6.1.0, replaced by {@link #isApproved()}
	*/
	@Override
	public boolean getApproved() {
		return _patcherBuild.getApproved();
	}

	/**
	* Returns <code>true</code> if this patcher build is approved.
	*
	* @return <code>true</code> if this patcher build is approved; <code>false</code> otherwise
	*/
	@Override
	public boolean isApproved() {
		return _patcherBuild.isApproved();
	}

	/**
	* Returns <code>true</code> if this patcher build is denied.
	*
	* @return <code>true</code> if this patcher build is denied; <code>false</code> otherwise
	*/
	@Override
	public boolean isDenied() {
		return _patcherBuild.isDenied();
	}

	/**
	* Returns <code>true</code> if this patcher build is a draft.
	*
	* @return <code>true</code> if this patcher build is a draft; <code>false</code> otherwise
	*/
	@Override
	public boolean isDraft() {
		return _patcherBuild.isDraft();
	}

	/**
	* Returns <code>true</code> if this patcher build is expired.
	*
	* @return <code>true</code> if this patcher build is expired; <code>false</code> otherwise
	*/
	@Override
	public boolean isExpired() {
		return _patcherBuild.isExpired();
	}

	/**
	* Returns <code>true</code> if this patcher build is inactive.
	*
	* @return <code>true</code> if this patcher build is inactive; <code>false</code> otherwise
	*/
	@Override
	public boolean isInactive() {
		return _patcherBuild.isInactive();
	}

	/**
	* Returns <code>true</code> if this patcher build is incomplete.
	*
	* @return <code>true</code> if this patcher build is incomplete; <code>false</code> otherwise
	*/
	@Override
	public boolean isIncomplete() {
		return _patcherBuild.isIncomplete();
	}

	/**
	* Returns <code>true</code> if this patcher build is pending.
	*
	* @return <code>true</code> if this patcher build is pending; <code>false</code> otherwise
	*/
	@Override
	public boolean isPending() {
		return _patcherBuild.isPending();
	}

	/**
	* Returns <code>true</code> if this patcher build is scheduled.
	*
	* @return <code>true</code> if this patcher build is scheduled; <code>false</code> otherwise
	*/
	@Override
	public boolean isScheduled() {
		return _patcherBuild.isScheduled();
	}

	@Override
	public boolean isNew() {
		return _patcherBuild.isNew();
	}

	@Override
	public void setNew(boolean n) {
		_patcherBuild.setNew(n);
	}

	@Override
	public boolean isCachedModel() {
		return _patcherBuild.isCachedModel();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		_patcherBuild.setCachedModel(cachedModel);
	}

	@Override
	public boolean isEscapedModel() {
		return _patcherBuild.isEscapedModel();
	}

	@Override
	public java.io.Serializable getPrimaryKeyObj() {
		return _patcherBuild.getPrimaryKeyObj();
	}

	@Override
	public void setPrimaryKeyObj(java.io.Serializable primaryKeyObj) {
		_patcherBuild.setPrimaryKeyObj(primaryKeyObj);
	}

	@Override
	public com.liferay.portlet.expando.model.ExpandoBridge getExpandoBridge() {
		return _patcherBuild.getExpandoBridge();
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.model.BaseModel<?> baseModel) {
		_patcherBuild.setExpandoBridgeAttributes(baseModel);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portlet.expando.model.ExpandoBridge expandoBridge) {
		_patcherBuild.setExpandoBridgeAttributes(expandoBridge);
	}

	@Override
	public void setExpandoBridgeAttributes(
		com.liferay.portal.service.ServiceContext serviceContext) {
		_patcherBuild.setExpandoBridgeAttributes(serviceContext);
	}

	@Override
	public java.lang.Object clone() {
		return new PatcherBuildWrapper((PatcherBuild)_patcherBuild.clone());
	}

	@Override
	public int compareTo(
		com.liferay.osb.patcher.model.PatcherBuild patcherBuild) {
		return _patcherBuild.compareTo(patcherBuild);
	}

	@Override
	public int hashCode() {
		return _patcherBuild.hashCode();
	}

	@Override
	public com.liferay.portal.model.CacheModel<com.liferay.osb.patcher.model.PatcherBuild> toCacheModel() {
		return _patcherBuild.toCacheModel();
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuild toEscapedModel() {
		return new PatcherBuildWrapper(_patcherBuild.toEscapedModel());
	}

	@Override
	public com.liferay.osb.patcher.model.PatcherBuild toUnescapedModel() {
		return new PatcherBuildWrapper(_patcherBuild.toUnescapedModel());
	}

	@Override
	public java.lang.String toString() {
		return _patcherBuild.toString();
	}

	@Override
	public java.lang.String toXmlString() {
		return _patcherBuild.toXmlString();
	}

	@Override
	public void persist()
		throws com.liferay.portal.kernel.exception.SystemException {
		_patcherBuild.persist();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherBuildWrapper)) {
			return false;
		}

		PatcherBuildWrapper patcherBuildWrapper = (PatcherBuildWrapper)obj;

		if (Validator.equals(_patcherBuild, patcherBuildWrapper._patcherBuild)) {
			return true;
		}

		return false;
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #getWrappedModel}
	 */
	public PatcherBuild getWrappedPatcherBuild() {
		return _patcherBuild;
	}

	@Override
	public PatcherBuild getWrappedModel() {
		return _patcherBuild;
	}

	@Override
	public void resetOriginalValues() {
		_patcherBuild.resetOriginalValues();
	}

	private PatcherBuild _patcherBuild;
}