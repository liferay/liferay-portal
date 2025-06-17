/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.model.wrapper.BaseModelWrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is a wrapper for {@link PatcherBuild}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuild
 * @generated
 */
public class PatcherBuildWrapper
	extends BaseModelWrapper<PatcherBuild>
	implements ModelWrapper<PatcherBuild>, PatcherBuild {

	public PatcherBuildWrapper(PatcherBuild patcherBuild) {
		super(patcherBuild);
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("mvccVersion", getMvccVersion());
		attributes.put("patcherBuildId", getPatcherBuildId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("hotfixId", getHotfixId());
		attributes.put("patcherAccountId", getPatcherAccountId());
		attributes.put("patcherFixId", getPatcherFixId());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("ticketEntryId", getTicketEntryId());
		attributes.put("accountEntryCode", getAccountEntryCode());
		attributes.put("childBuild", isChildBuild());
		attributes.put("comments", getComments());
		attributes.put("fileName", getFileName());
		attributes.put("initialName", getInitialName());
		attributes.put("key", getKey());
		attributes.put("keyVersion", getKeyVersion());
		attributes.put("latestBuild", isLatestBuild());
		attributes.put("latestKeyBuild", isLatestKeyBuild());
		attributes.put("latestLESATicketBuild", isLatestLESATicketBuild());
		attributes.put(
			"latestSupportTicketBuild", isLatestSupportTicketBuild());
		attributes.put("lesaTicket", getLesaTicket());
		attributes.put("lesaTicketVersion", getLesaTicketVersion());
		attributes.put("name", getName());
		attributes.put("notified", isNotified());
		attributes.put("productVersion", getProductVersion());
		attributes.put("qaComments", getQaComments());
		attributes.put("qaStatus", getQaStatus());
		attributes.put("requestKey", getRequestKey());
		attributes.put("sourceName", getSourceName());
		attributes.put("supportTicket", getSupportTicket());
		attributes.put("supportTicketVersion", getSupportTicketVersion());
		attributes.put("type", getType());
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

		Long hotfixId = (Long)attributes.get("hotfixId");

		if (hotfixId != null) {
			setHotfixId(hotfixId);
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

		String accountEntryCode = (String)attributes.get("accountEntryCode");

		if (accountEntryCode != null) {
			setAccountEntryCode(accountEntryCode);
		}

		Boolean childBuild = (Boolean)attributes.get("childBuild");

		if (childBuild != null) {
			setChildBuild(childBuild);
		}

		String comments = (String)attributes.get("comments");

		if (comments != null) {
			setComments(comments);
		}

		String fileName = (String)attributes.get("fileName");

		if (fileName != null) {
			setFileName(fileName);
		}

		String initialName = (String)attributes.get("initialName");

		if (initialName != null) {
			setInitialName(initialName);
		}

		String key = (String)attributes.get("key");

		if (key != null) {
			setKey(key);
		}

		Double keyVersion = (Double)attributes.get("keyVersion");

		if (keyVersion != null) {
			setKeyVersion(keyVersion);
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

		String lesaTicket = (String)attributes.get("lesaTicket");

		if (lesaTicket != null) {
			setLesaTicket(lesaTicket);
		}

		Double lesaTicketVersion = (Double)attributes.get("lesaTicketVersion");

		if (lesaTicketVersion != null) {
			setLesaTicketVersion(lesaTicketVersion);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Boolean notified = (Boolean)attributes.get("notified");

		if (notified != null) {
			setNotified(notified);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
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

		String sourceName = (String)attributes.get("sourceName");

		if (sourceName != null) {
			setSourceName(sourceName);
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

		Integer type = (Integer)attributes.get("type");

		if (type != null) {
			setType(type);
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
	public PatcherBuild cloneWithOriginalValues() {
		return wrap(model.cloneWithOriginalValues());
	}

	/**
	 * Returns the account entry code of this patcher build.
	 *
	 * @return the account entry code of this patcher build
	 */
	@Override
	public String getAccountEntryCode() {
		return model.getAccountEntryCode();
	}

	/**
	 * Returns the child build of this patcher build.
	 *
	 * @return the child build of this patcher build
	 */
	@Override
	public boolean getChildBuild() {
		return model.getChildBuild();
	}

	/**
	 * Returns the comments of this patcher build.
	 *
	 * @return the comments of this patcher build
	 */
	@Override
	public String getComments() {
		return model.getComments();
	}

	/**
	 * Returns the company ID of this patcher build.
	 *
	 * @return the company ID of this patcher build
	 */
	@Override
	public long getCompanyId() {
		return model.getCompanyId();
	}

	/**
	 * Returns the create date of this patcher build.
	 *
	 * @return the create date of this patcher build
	 */
	@Override
	public Date getCreateDate() {
		return model.getCreateDate();
	}

	/**
	 * Returns the file name of this patcher build.
	 *
	 * @return the file name of this patcher build
	 */
	@Override
	public String getFileName() {
		return model.getFileName();
	}

	/**
	 * Returns the hotfix ID of this patcher build.
	 *
	 * @return the hotfix ID of this patcher build
	 */
	@Override
	public long getHotfixId() {
		return model.getHotfixId();
	}

	/**
	 * Returns the initial name of this patcher build.
	 *
	 * @return the initial name of this patcher build
	 */
	@Override
	public String getInitialName() {
		return model.getInitialName();
	}

	/**
	 * Returns the key of this patcher build.
	 *
	 * @return the key of this patcher build
	 */
	@Override
	public String getKey() {
		return model.getKey();
	}

	/**
	 * Returns the key version of this patcher build.
	 *
	 * @return the key version of this patcher build
	 */
	@Override
	public double getKeyVersion() {
		return model.getKeyVersion();
	}

	/**
	 * Returns the latest build of this patcher build.
	 *
	 * @return the latest build of this patcher build
	 */
	@Override
	public boolean getLatestBuild() {
		return model.getLatestBuild();
	}

	/**
	 * Returns the latest key build of this patcher build.
	 *
	 * @return the latest key build of this patcher build
	 */
	@Override
	public boolean getLatestKeyBuild() {
		return model.getLatestKeyBuild();
	}

	/**
	 * Returns the latest lesa ticket build of this patcher build.
	 *
	 * @return the latest lesa ticket build of this patcher build
	 */
	@Override
	public boolean getLatestLESATicketBuild() {
		return model.getLatestLESATicketBuild();
	}

	/**
	 * Returns the latest support ticket build of this patcher build.
	 *
	 * @return the latest support ticket build of this patcher build
	 */
	@Override
	public boolean getLatestSupportTicketBuild() {
		return model.getLatestSupportTicketBuild();
	}

	/**
	 * Returns the lesa ticket of this patcher build.
	 *
	 * @return the lesa ticket of this patcher build
	 */
	@Override
	public String getLesaTicket() {
		return model.getLesaTicket();
	}

	/**
	 * Returns the lesa ticket version of this patcher build.
	 *
	 * @return the lesa ticket version of this patcher build
	 */
	@Override
	public double getLesaTicketVersion() {
		return model.getLesaTicketVersion();
	}

	/**
	 * Returns the modified date of this patcher build.
	 *
	 * @return the modified date of this patcher build
	 */
	@Override
	public Date getModifiedDate() {
		return model.getModifiedDate();
	}

	/**
	 * Returns the mvcc version of this patcher build.
	 *
	 * @return the mvcc version of this patcher build
	 */
	@Override
	public long getMvccVersion() {
		return model.getMvccVersion();
	}

	/**
	 * Returns the name of this patcher build.
	 *
	 * @return the name of this patcher build
	 */
	@Override
	public String getName() {
		return model.getName();
	}

	/**
	 * Returns the notified of this patcher build.
	 *
	 * @return the notified of this patcher build
	 */
	@Override
	public boolean getNotified() {
		return model.getNotified();
	}

	/**
	 * Returns the patcher account ID of this patcher build.
	 *
	 * @return the patcher account ID of this patcher build
	 */
	@Override
	public long getPatcherAccountId() {
		return model.getPatcherAccountId();
	}

	/**
	 * Returns the patcher build ID of this patcher build.
	 *
	 * @return the patcher build ID of this patcher build
	 */
	@Override
	public long getPatcherBuildId() {
		return model.getPatcherBuildId();
	}

	/**
	 * Returns the patcher fix ID of this patcher build.
	 *
	 * @return the patcher fix ID of this patcher build
	 */
	@Override
	public long getPatcherFixId() {
		return model.getPatcherFixId();
	}

	/**
	 * Returns the patcher product version ID of this patcher build.
	 *
	 * @return the patcher product version ID of this patcher build
	 */
	@Override
	public long getPatcherProductVersionId() {
		return model.getPatcherProductVersionId();
	}

	/**
	 * Returns the patcher project version ID of this patcher build.
	 *
	 * @return the patcher project version ID of this patcher build
	 */
	@Override
	public long getPatcherProjectVersionId() {
		return model.getPatcherProjectVersionId();
	}

	/**
	 * Returns the primary key of this patcher build.
	 *
	 * @return the primary key of this patcher build
	 */
	@Override
	public long getPrimaryKey() {
		return model.getPrimaryKey();
	}

	/**
	 * Returns the product version of this patcher build.
	 *
	 * @return the product version of this patcher build
	 */
	@Override
	public int getProductVersion() {
		return model.getProductVersion();
	}

	/**
	 * Returns the qa comments of this patcher build.
	 *
	 * @return the qa comments of this patcher build
	 */
	@Override
	public String getQaComments() {
		return model.getQaComments();
	}

	/**
	 * Returns the qa status of this patcher build.
	 *
	 * @return the qa status of this patcher build
	 */
	@Override
	public int getQaStatus() {
		return model.getQaStatus();
	}

	/**
	 * Returns the request key of this patcher build.
	 *
	 * @return the request key of this patcher build
	 */
	@Override
	public String getRequestKey() {
		return model.getRequestKey();
	}

	/**
	 * Returns the source name of this patcher build.
	 *
	 * @return the source name of this patcher build
	 */
	@Override
	public String getSourceName() {
		return model.getSourceName();
	}

	/**
	 * Returns the status of this patcher build.
	 *
	 * @return the status of this patcher build
	 */
	@Override
	public int getStatus() {
		return model.getStatus();
	}

	/**
	 * Returns the status by user ID of this patcher build.
	 *
	 * @return the status by user ID of this patcher build
	 */
	@Override
	public long getStatusByUserId() {
		return model.getStatusByUserId();
	}

	/**
	 * Returns the status by user name of this patcher build.
	 *
	 * @return the status by user name of this patcher build
	 */
	@Override
	public String getStatusByUserName() {
		return model.getStatusByUserName();
	}

	/**
	 * Returns the status by user uuid of this patcher build.
	 *
	 * @return the status by user uuid of this patcher build
	 */
	@Override
	public String getStatusByUserUuid() {
		return model.getStatusByUserUuid();
	}

	/**
	 * Returns the status date of this patcher build.
	 *
	 * @return the status date of this patcher build
	 */
	@Override
	public Date getStatusDate() {
		return model.getStatusDate();
	}

	/**
	 * Returns the support ticket of this patcher build.
	 *
	 * @return the support ticket of this patcher build
	 */
	@Override
	public String getSupportTicket() {
		return model.getSupportTicket();
	}

	/**
	 * Returns the support ticket version of this patcher build.
	 *
	 * @return the support ticket version of this patcher build
	 */
	@Override
	public double getSupportTicketVersion() {
		return model.getSupportTicketVersion();
	}

	/**
	 * Returns the ticket entry ID of this patcher build.
	 *
	 * @return the ticket entry ID of this patcher build
	 */
	@Override
	public long getTicketEntryId() {
		return model.getTicketEntryId();
	}

	/**
	 * Returns the type of this patcher build.
	 *
	 * @return the type of this patcher build
	 */
	@Override
	public int getType() {
		return model.getType();
	}

	/**
	 * Returns the user ID of this patcher build.
	 *
	 * @return the user ID of this patcher build
	 */
	@Override
	public long getUserId() {
		return model.getUserId();
	}

	/**
	 * Returns the user name of this patcher build.
	 *
	 * @return the user name of this patcher build
	 */
	@Override
	public String getUserName() {
		return model.getUserName();
	}

	/**
	 * Returns the user uuid of this patcher build.
	 *
	 * @return the user uuid of this patcher build
	 */
	@Override
	public String getUserUuid() {
		return model.getUserUuid();
	}

	/**
	 * Returns <code>true</code> if this patcher build is approved.
	 *
	 * @return <code>true</code> if this patcher build is approved; <code>false</code> otherwise
	 */
	@Override
	public boolean isApproved() {
		return model.isApproved();
	}

	/**
	 * Returns <code>true</code> if this patcher build is child build.
	 *
	 * @return <code>true</code> if this patcher build is child build; <code>false</code> otherwise
	 */
	@Override
	public boolean isChildBuild() {
		return model.isChildBuild();
	}

	/**
	 * Returns <code>true</code> if this patcher build is denied.
	 *
	 * @return <code>true</code> if this patcher build is denied; <code>false</code> otherwise
	 */
	@Override
	public boolean isDenied() {
		return model.isDenied();
	}

	/**
	 * Returns <code>true</code> if this patcher build is a draft.
	 *
	 * @return <code>true</code> if this patcher build is a draft; <code>false</code> otherwise
	 */
	@Override
	public boolean isDraft() {
		return model.isDraft();
	}

	/**
	 * Returns <code>true</code> if this patcher build is expired.
	 *
	 * @return <code>true</code> if this patcher build is expired; <code>false</code> otherwise
	 */
	@Override
	public boolean isExpired() {
		return model.isExpired();
	}

	/**
	 * Returns <code>true</code> if this patcher build is inactive.
	 *
	 * @return <code>true</code> if this patcher build is inactive; <code>false</code> otherwise
	 */
	@Override
	public boolean isInactive() {
		return model.isInactive();
	}

	/**
	 * Returns <code>true</code> if this patcher build is incomplete.
	 *
	 * @return <code>true</code> if this patcher build is incomplete; <code>false</code> otherwise
	 */
	@Override
	public boolean isIncomplete() {
		return model.isIncomplete();
	}

	/**
	 * Returns <code>true</code> if this patcher build is latest build.
	 *
	 * @return <code>true</code> if this patcher build is latest build; <code>false</code> otherwise
	 */
	@Override
	public boolean isLatestBuild() {
		return model.isLatestBuild();
	}

	/**
	 * Returns <code>true</code> if this patcher build is latest key build.
	 *
	 * @return <code>true</code> if this patcher build is latest key build; <code>false</code> otherwise
	 */
	@Override
	public boolean isLatestKeyBuild() {
		return model.isLatestKeyBuild();
	}

	/**
	 * Returns <code>true</code> if this patcher build is latest lesa ticket build.
	 *
	 * @return <code>true</code> if this patcher build is latest lesa ticket build; <code>false</code> otherwise
	 */
	@Override
	public boolean isLatestLESATicketBuild() {
		return model.isLatestLESATicketBuild();
	}

	/**
	 * Returns <code>true</code> if this patcher build is latest support ticket build.
	 *
	 * @return <code>true</code> if this patcher build is latest support ticket build; <code>false</code> otherwise
	 */
	@Override
	public boolean isLatestSupportTicketBuild() {
		return model.isLatestSupportTicketBuild();
	}

	/**
	 * Returns <code>true</code> if this patcher build is notified.
	 *
	 * @return <code>true</code> if this patcher build is notified; <code>false</code> otherwise
	 */
	@Override
	public boolean isNotified() {
		return model.isNotified();
	}

	/**
	 * Returns <code>true</code> if this patcher build is pending.
	 *
	 * @return <code>true</code> if this patcher build is pending; <code>false</code> otherwise
	 */
	@Override
	public boolean isPending() {
		return model.isPending();
	}

	/**
	 * Returns <code>true</code> if this patcher build is scheduled.
	 *
	 * @return <code>true</code> if this patcher build is scheduled; <code>false</code> otherwise
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
	 * Sets the account entry code of this patcher build.
	 *
	 * @param accountEntryCode the account entry code of this patcher build
	 */
	@Override
	public void setAccountEntryCode(String accountEntryCode) {
		model.setAccountEntryCode(accountEntryCode);
	}

	/**
	 * Sets whether this patcher build is child build.
	 *
	 * @param childBuild the child build of this patcher build
	 */
	@Override
	public void setChildBuild(boolean childBuild) {
		model.setChildBuild(childBuild);
	}

	/**
	 * Sets the comments of this patcher build.
	 *
	 * @param comments the comments of this patcher build
	 */
	@Override
	public void setComments(String comments) {
		model.setComments(comments);
	}

	/**
	 * Sets the company ID of this patcher build.
	 *
	 * @param companyId the company ID of this patcher build
	 */
	@Override
	public void setCompanyId(long companyId) {
		model.setCompanyId(companyId);
	}

	/**
	 * Sets the create date of this patcher build.
	 *
	 * @param createDate the create date of this patcher build
	 */
	@Override
	public void setCreateDate(Date createDate) {
		model.setCreateDate(createDate);
	}

	/**
	 * Sets the file name of this patcher build.
	 *
	 * @param fileName the file name of this patcher build
	 */
	@Override
	public void setFileName(String fileName) {
		model.setFileName(fileName);
	}

	/**
	 * Sets the hotfix ID of this patcher build.
	 *
	 * @param hotfixId the hotfix ID of this patcher build
	 */
	@Override
	public void setHotfixId(long hotfixId) {
		model.setHotfixId(hotfixId);
	}

	/**
	 * Sets the initial name of this patcher build.
	 *
	 * @param initialName the initial name of this patcher build
	 */
	@Override
	public void setInitialName(String initialName) {
		model.setInitialName(initialName);
	}

	/**
	 * Sets the key of this patcher build.
	 *
	 * @param key the key of this patcher build
	 */
	@Override
	public void setKey(String key) {
		model.setKey(key);
	}

	/**
	 * Sets the key version of this patcher build.
	 *
	 * @param keyVersion the key version of this patcher build
	 */
	@Override
	public void setKeyVersion(double keyVersion) {
		model.setKeyVersion(keyVersion);
	}

	/**
	 * Sets whether this patcher build is latest build.
	 *
	 * @param latestBuild the latest build of this patcher build
	 */
	@Override
	public void setLatestBuild(boolean latestBuild) {
		model.setLatestBuild(latestBuild);
	}

	/**
	 * Sets whether this patcher build is latest key build.
	 *
	 * @param latestKeyBuild the latest key build of this patcher build
	 */
	@Override
	public void setLatestKeyBuild(boolean latestKeyBuild) {
		model.setLatestKeyBuild(latestKeyBuild);
	}

	/**
	 * Sets whether this patcher build is latest lesa ticket build.
	 *
	 * @param latestLESATicketBuild the latest lesa ticket build of this patcher build
	 */
	@Override
	public void setLatestLESATicketBuild(boolean latestLESATicketBuild) {
		model.setLatestLESATicketBuild(latestLESATicketBuild);
	}

	/**
	 * Sets whether this patcher build is latest support ticket build.
	 *
	 * @param latestSupportTicketBuild the latest support ticket build of this patcher build
	 */
	@Override
	public void setLatestSupportTicketBuild(boolean latestSupportTicketBuild) {
		model.setLatestSupportTicketBuild(latestSupportTicketBuild);
	}

	/**
	 * Sets the lesa ticket of this patcher build.
	 *
	 * @param lesaTicket the lesa ticket of this patcher build
	 */
	@Override
	public void setLesaTicket(String lesaTicket) {
		model.setLesaTicket(lesaTicket);
	}

	/**
	 * Sets the lesa ticket version of this patcher build.
	 *
	 * @param lesaTicketVersion the lesa ticket version of this patcher build
	 */
	@Override
	public void setLesaTicketVersion(double lesaTicketVersion) {
		model.setLesaTicketVersion(lesaTicketVersion);
	}

	/**
	 * Sets the modified date of this patcher build.
	 *
	 * @param modifiedDate the modified date of this patcher build
	 */
	@Override
	public void setModifiedDate(Date modifiedDate) {
		model.setModifiedDate(modifiedDate);
	}

	/**
	 * Sets the mvcc version of this patcher build.
	 *
	 * @param mvccVersion the mvcc version of this patcher build
	 */
	@Override
	public void setMvccVersion(long mvccVersion) {
		model.setMvccVersion(mvccVersion);
	}

	/**
	 * Sets the name of this patcher build.
	 *
	 * @param name the name of this patcher build
	 */
	@Override
	public void setName(String name) {
		model.setName(name);
	}

	/**
	 * Sets whether this patcher build is notified.
	 *
	 * @param notified the notified of this patcher build
	 */
	@Override
	public void setNotified(boolean notified) {
		model.setNotified(notified);
	}

	/**
	 * Sets the patcher account ID of this patcher build.
	 *
	 * @param patcherAccountId the patcher account ID of this patcher build
	 */
	@Override
	public void setPatcherAccountId(long patcherAccountId) {
		model.setPatcherAccountId(patcherAccountId);
	}

	/**
	 * Sets the patcher build ID of this patcher build.
	 *
	 * @param patcherBuildId the patcher build ID of this patcher build
	 */
	@Override
	public void setPatcherBuildId(long patcherBuildId) {
		model.setPatcherBuildId(patcherBuildId);
	}

	/**
	 * Sets the patcher fix ID of this patcher build.
	 *
	 * @param patcherFixId the patcher fix ID of this patcher build
	 */
	@Override
	public void setPatcherFixId(long patcherFixId) {
		model.setPatcherFixId(patcherFixId);
	}

	/**
	 * Sets the patcher product version ID of this patcher build.
	 *
	 * @param patcherProductVersionId the patcher product version ID of this patcher build
	 */
	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		model.setPatcherProductVersionId(patcherProductVersionId);
	}

	/**
	 * Sets the patcher project version ID of this patcher build.
	 *
	 * @param patcherProjectVersionId the patcher project version ID of this patcher build
	 */
	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		model.setPatcherProjectVersionId(patcherProjectVersionId);
	}

	/**
	 * Sets the primary key of this patcher build.
	 *
	 * @param primaryKey the primary key of this patcher build
	 */
	@Override
	public void setPrimaryKey(long primaryKey) {
		model.setPrimaryKey(primaryKey);
	}

	/**
	 * Sets the product version of this patcher build.
	 *
	 * @param productVersion the product version of this patcher build
	 */
	@Override
	public void setProductVersion(int productVersion) {
		model.setProductVersion(productVersion);
	}

	/**
	 * Sets the qa comments of this patcher build.
	 *
	 * @param qaComments the qa comments of this patcher build
	 */
	@Override
	public void setQaComments(String qaComments) {
		model.setQaComments(qaComments);
	}

	/**
	 * Sets the qa status of this patcher build.
	 *
	 * @param qaStatus the qa status of this patcher build
	 */
	@Override
	public void setQaStatus(int qaStatus) {
		model.setQaStatus(qaStatus);
	}

	/**
	 * Sets the request key of this patcher build.
	 *
	 * @param requestKey the request key of this patcher build
	 */
	@Override
	public void setRequestKey(String requestKey) {
		model.setRequestKey(requestKey);
	}

	/**
	 * Sets the source name of this patcher build.
	 *
	 * @param sourceName the source name of this patcher build
	 */
	@Override
	public void setSourceName(String sourceName) {
		model.setSourceName(sourceName);
	}

	/**
	 * Sets the status of this patcher build.
	 *
	 * @param status the status of this patcher build
	 */
	@Override
	public void setStatus(int status) {
		model.setStatus(status);
	}

	/**
	 * Sets the status by user ID of this patcher build.
	 *
	 * @param statusByUserId the status by user ID of this patcher build
	 */
	@Override
	public void setStatusByUserId(long statusByUserId) {
		model.setStatusByUserId(statusByUserId);
	}

	/**
	 * Sets the status by user name of this patcher build.
	 *
	 * @param statusByUserName the status by user name of this patcher build
	 */
	@Override
	public void setStatusByUserName(String statusByUserName) {
		model.setStatusByUserName(statusByUserName);
	}

	/**
	 * Sets the status by user uuid of this patcher build.
	 *
	 * @param statusByUserUuid the status by user uuid of this patcher build
	 */
	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		model.setStatusByUserUuid(statusByUserUuid);
	}

	/**
	 * Sets the status date of this patcher build.
	 *
	 * @param statusDate the status date of this patcher build
	 */
	@Override
	public void setStatusDate(Date statusDate) {
		model.setStatusDate(statusDate);
	}

	/**
	 * Sets the support ticket of this patcher build.
	 *
	 * @param supportTicket the support ticket of this patcher build
	 */
	@Override
	public void setSupportTicket(String supportTicket) {
		model.setSupportTicket(supportTicket);
	}

	/**
	 * Sets the support ticket version of this patcher build.
	 *
	 * @param supportTicketVersion the support ticket version of this patcher build
	 */
	@Override
	public void setSupportTicketVersion(double supportTicketVersion) {
		model.setSupportTicketVersion(supportTicketVersion);
	}

	/**
	 * Sets the ticket entry ID of this patcher build.
	 *
	 * @param ticketEntryId the ticket entry ID of this patcher build
	 */
	@Override
	public void setTicketEntryId(long ticketEntryId) {
		model.setTicketEntryId(ticketEntryId);
	}

	/**
	 * Sets the type of this patcher build.
	 *
	 * @param type the type of this patcher build
	 */
	@Override
	public void setType(int type) {
		model.setType(type);
	}

	/**
	 * Sets the user ID of this patcher build.
	 *
	 * @param userId the user ID of this patcher build
	 */
	@Override
	public void setUserId(long userId) {
		model.setUserId(userId);
	}

	/**
	 * Sets the user name of this patcher build.
	 *
	 * @param userName the user name of this patcher build
	 */
	@Override
	public void setUserName(String userName) {
		model.setUserName(userName);
	}

	/**
	 * Sets the user uuid of this patcher build.
	 *
	 * @param userUuid the user uuid of this patcher build
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
	protected PatcherBuildWrapper wrap(PatcherBuild patcherBuild) {
		return new PatcherBuildWrapper(patcherBuild);
	}

}