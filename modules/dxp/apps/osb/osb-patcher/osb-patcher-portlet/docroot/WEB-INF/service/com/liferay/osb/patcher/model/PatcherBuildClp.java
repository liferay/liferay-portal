/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.model;

import com.liferay.osb.patcher.service.ClpSerializer;
import com.liferay.osb.patcher.service.PatcherBuildLocalServiceUtil;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.impl.BaseModelImpl;
import com.liferay.portal.util.PortalUtil;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Calvin Keum
 */
public class PatcherBuildClp extends BaseModelImpl<PatcherBuild>
	implements PatcherBuild {
	public PatcherBuildClp() {
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
	public long getPrimaryKey() {
		return _patcherBuildId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherBuildId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherBuildId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
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

	@Override
	public long getPatcherBuildId() {
		return _patcherBuildId;
	}

	@Override
	public void setPatcherBuildId(long patcherBuildId) {
		_patcherBuildId = patcherBuildId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherBuildId", long.class);

				method.invoke(_patcherBuildRemoteModel, patcherBuildId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		_companyId = companyId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setCompanyId", long.class);

				method.invoke(_patcherBuildRemoteModel, companyId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		_userId = userId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setUserId", long.class);

				method.invoke(_patcherBuildRemoteModel, userId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getUserUuid() throws SystemException {
		return PortalUtil.getUserValue(getUserId(), "uuid", _userUuid);
	}

	@Override
	public void setUserUuid(String userUuid) {
		_userUuid = userUuid;
	}

	@Override
	public String getUserName() {
		return _userName;
	}

	@Override
	public void setUserName(String userName) {
		_userName = userName;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setUserName", String.class);

				method.invoke(_patcherBuildRemoteModel, userName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		_createDate = createDate;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setCreateDate", Date.class);

				method.invoke(_patcherBuildRemoteModel, createDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setModifiedDate", Date.class);

				method.invoke(_patcherBuildRemoteModel, modifiedDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherAccountId() {
		return _patcherAccountId;
	}

	@Override
	public void setPatcherAccountId(long patcherAccountId) {
		_patcherAccountId = patcherAccountId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherAccountId",
						long.class);

				method.invoke(_patcherBuildRemoteModel, patcherAccountId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherFixId() {
		return _patcherFixId;
	}

	@Override
	public void setPatcherFixId(long patcherFixId) {
		_patcherFixId = patcherFixId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherFixId", long.class);

				method.invoke(_patcherBuildRemoteModel, patcherFixId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherProductVersionId() {
		return _patcherProductVersionId;
	}

	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProductVersionId = patcherProductVersionId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProductVersionId",
						long.class);

				method.invoke(_patcherBuildRemoteModel, patcherProductVersionId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherProjectVersionId() {
		return _patcherProjectVersionId;
	}

	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherProjectVersionId = patcherProjectVersionId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProjectVersionId",
						long.class);

				method.invoke(_patcherBuildRemoteModel, patcherProjectVersionId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getTicketEntryId() {
		return _ticketEntryId;
	}

	@Override
	public void setTicketEntryId(long ticketEntryId) {
		_ticketEntryId = ticketEntryId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setTicketEntryId", long.class);

				method.invoke(_patcherBuildRemoteModel, ticketEntryId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getHotfixId() {
		return _hotfixId;
	}

	@Override
	public void setHotfixId(long hotfixId) {
		_hotfixId = hotfixId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setHotfixId", long.class);

				method.invoke(_patcherBuildRemoteModel, hotfixId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public void setName(String name) {
		_name = name;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setName", String.class);

				method.invoke(_patcherBuildRemoteModel, name);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getOriginalName() {
		return _originalName;
	}

	@Override
	public void setOriginalName(String originalName) {
		_originalName = originalName;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setOriginalName", String.class);

				method.invoke(_patcherBuildRemoteModel, originalName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getKey() {
		return _key;
	}

	@Override
	public void setKey(String key) {
		_key = key;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setKey", String.class);

				method.invoke(_patcherBuildRemoteModel, key);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public double getKeyVersion() {
		return _keyVersion;
	}

	@Override
	public void setKeyVersion(double keyVersion) {
		_keyVersion = keyVersion;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setKeyVersion", double.class);

				method.invoke(_patcherBuildRemoteModel, keyVersion);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getType() {
		return _type;
	}

	@Override
	public void setType(int type) {
		_type = type;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setType", int.class);

				method.invoke(_patcherBuildRemoteModel, type);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getLatestBuild() {
		return _latestBuild;
	}

	@Override
	public boolean isLatestBuild() {
		return _latestBuild;
	}

	@Override
	public void setLatestBuild(boolean latestBuild) {
		_latestBuild = latestBuild;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setLatestBuild", boolean.class);

				method.invoke(_patcherBuildRemoteModel, latestBuild);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getLatestKeyBuild() {
		return _latestKeyBuild;
	}

	@Override
	public boolean isLatestKeyBuild() {
		return _latestKeyBuild;
	}

	@Override
	public void setLatestKeyBuild(boolean latestKeyBuild) {
		_latestKeyBuild = latestKeyBuild;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setLatestKeyBuild",
						boolean.class);

				method.invoke(_patcherBuildRemoteModel, latestKeyBuild);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getLatestLESATicketBuild() {
		return _latestLESATicketBuild;
	}

	@Override
	public boolean isLatestLESATicketBuild() {
		return _latestLESATicketBuild;
	}

	@Override
	public void setLatestLESATicketBuild(boolean latestLESATicketBuild) {
		_latestLESATicketBuild = latestLESATicketBuild;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setLatestLESATicketBuild",
						boolean.class);

				method.invoke(_patcherBuildRemoteModel, latestLESATicketBuild);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getLatestSupportTicketBuild() {
		return _latestSupportTicketBuild;
	}

	@Override
	public boolean isLatestSupportTicketBuild() {
		return _latestSupportTicketBuild;
	}

	@Override
	public void setLatestSupportTicketBuild(boolean latestSupportTicketBuild) {
		_latestSupportTicketBuild = latestSupportTicketBuild;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setLatestSupportTicketBuild",
						boolean.class);

				method.invoke(_patcherBuildRemoteModel, latestSupportTicketBuild);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getAccountEntryCode() {
		return _accountEntryCode;
	}

	@Override
	public void setAccountEntryCode(String accountEntryCode) {
		_accountEntryCode = accountEntryCode;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setAccountEntryCode",
						String.class);

				method.invoke(_patcherBuildRemoteModel, accountEntryCode);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getLesaTicket() {
		return _lesaTicket;
	}

	@Override
	public void setLesaTicket(String lesaTicket) {
		_lesaTicket = lesaTicket;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setLesaTicket", String.class);

				method.invoke(_patcherBuildRemoteModel, lesaTicket);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public double getLesaTicketVersion() {
		return _lesaTicketVersion;
	}

	@Override
	public void setLesaTicketVersion(double lesaTicketVersion) {
		_lesaTicketVersion = lesaTicketVersion;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setLesaTicketVersion",
						double.class);

				method.invoke(_patcherBuildRemoteModel, lesaTicketVersion);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getSupportTicket() {
		return _supportTicket;
	}

	@Override
	public void setSupportTicket(String supportTicket) {
		_supportTicket = supportTicket;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setSupportTicket", String.class);

				method.invoke(_patcherBuildRemoteModel, supportTicket);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public double getSupportTicketVersion() {
		return _supportTicketVersion;
	}

	@Override
	public void setSupportTicketVersion(double supportTicketVersion) {
		_supportTicketVersion = supportTicketVersion;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setSupportTicketVersion",
						double.class);

				method.invoke(_patcherBuildRemoteModel, supportTicketVersion);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getFileName() {
		return _fileName;
	}

	@Override
	public void setFileName(String fileName) {
		_fileName = fileName;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setFileName", String.class);

				method.invoke(_patcherBuildRemoteModel, fileName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getSourceName() {
		return _sourceName;
	}

	@Override
	public void setSourceName(String sourceName) {
		_sourceName = sourceName;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setSourceName", String.class);

				method.invoke(_patcherBuildRemoteModel, sourceName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getChildBuild() {
		return _childBuild;
	}

	@Override
	public boolean isChildBuild() {
		return _childBuild;
	}

	@Override
	public void setChildBuild(boolean childBuild) {
		_childBuild = childBuild;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setChildBuild", boolean.class);

				method.invoke(_patcherBuildRemoteModel, childBuild);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getComments() {
		return _comments;
	}

	@Override
	public void setComments(String comments) {
		_comments = comments;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setComments", String.class);

				method.invoke(_patcherBuildRemoteModel, comments);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getQaComments() {
		return _qaComments;
	}

	@Override
	public void setQaComments(String qaComments) {
		_qaComments = qaComments;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setQaComments", String.class);

				method.invoke(_patcherBuildRemoteModel, qaComments);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getQaStatus() {
		return _qaStatus;
	}

	@Override
	public void setQaStatus(int qaStatus) {
		_qaStatus = qaStatus;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setQaStatus", int.class);

				method.invoke(_patcherBuildRemoteModel, qaStatus);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getRequestKey() {
		return _requestKey;
	}

	@Override
	public void setRequestKey(String requestKey) {
		_requestKey = requestKey;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setRequestKey", String.class);

				method.invoke(_patcherBuildRemoteModel, requestKey);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getNotified() {
		return _notified;
	}

	@Override
	public boolean isNotified() {
		return _notified;
	}

	@Override
	public void setNotified(boolean notified) {
		_notified = notified;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setNotified", boolean.class);

				method.invoke(_patcherBuildRemoteModel, notified);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getProductVersion() {
		return _productVersion;
	}

	@Override
	public void setProductVersion(int productVersion) {
		_productVersion = productVersion;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setProductVersion", int.class);

				method.invoke(_patcherBuildRemoteModel, productVersion);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getStatus() {
		return _status;
	}

	@Override
	public void setStatus(int status) {
		_status = status;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setStatus", int.class);

				method.invoke(_patcherBuildRemoteModel, status);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getStatusByUserId() {
		return _statusByUserId;
	}

	@Override
	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setStatusByUserId", long.class);

				method.invoke(_patcherBuildRemoteModel, statusByUserId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getStatusByUserUuid() throws SystemException {
		return PortalUtil.getUserValue(getStatusByUserId(), "uuid",
			_statusByUserUuid);
	}

	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		_statusByUserUuid = statusByUserUuid;
	}

	@Override
	public String getStatusByUserName() {
		return _statusByUserName;
	}

	@Override
	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setStatusByUserName",
						String.class);

				method.invoke(_patcherBuildRemoteModel, statusByUserName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public Date getStatusDate() {
		return _statusDate;
	}

	@Override
	public void setStatusDate(Date statusDate) {
		_statusDate = statusDate;

		if (_patcherBuildRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRemoteModel.getClass();

				Method method = clazz.getMethod("setStatusDate", Date.class);

				method.invoke(_patcherBuildRemoteModel, statusDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	/**
	 * @deprecated As of 6.1.0, replaced by {@link #isApproved}
	 */
	@Override
	public boolean getApproved() {
		return isApproved();
	}

	@Override
	public boolean isApproved() {
		if (getStatus() == WorkflowConstants.STATUS_APPROVED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isDenied() {
		if (getStatus() == WorkflowConstants.STATUS_DENIED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isDraft() {
		if (getStatus() == WorkflowConstants.STATUS_DRAFT) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isExpired() {
		if (getStatus() == WorkflowConstants.STATUS_EXPIRED) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isInactive() {
		if (getStatus() == WorkflowConstants.STATUS_INACTIVE) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isIncomplete() {
		if (getStatus() == WorkflowConstants.STATUS_INCOMPLETE) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isPending() {
		if (getStatus() == WorkflowConstants.STATUS_PENDING) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public boolean isScheduled() {
		if (getStatus() == WorkflowConstants.STATUS_SCHEDULED) {
			return true;
		}
		else {
			return false;
		}
	}

	public BaseModel<?> getPatcherBuildRemoteModel() {
		return _patcherBuildRemoteModel;
	}

	public void setPatcherBuildRemoteModel(BaseModel<?> patcherBuildRemoteModel) {
		_patcherBuildRemoteModel = patcherBuildRemoteModel;
	}

	public Object invokeOnRemoteModel(String methodName,
		Class<?>[] parameterTypes, Object[] parameterValues)
		throws Exception {
		Object[] remoteParameterValues = new Object[parameterValues.length];

		for (int i = 0; i < parameterValues.length; i++) {
			if (parameterValues[i] != null) {
				remoteParameterValues[i] = ClpSerializer.translateInput(parameterValues[i]);
			}
		}

		Class<?> remoteModelClass = _patcherBuildRemoteModel.getClass();

		ClassLoader remoteModelClassLoader = remoteModelClass.getClassLoader();

		Class<?>[] remoteParameterTypes = new Class[parameterTypes.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			if (parameterTypes[i].isPrimitive()) {
				remoteParameterTypes[i] = parameterTypes[i];
			}
			else {
				String parameterTypeName = parameterTypes[i].getName();

				remoteParameterTypes[i] = remoteModelClassLoader.loadClass(parameterTypeName);
			}
		}

		Method method = remoteModelClass.getMethod(methodName,
				remoteParameterTypes);

		Object returnValue = method.invoke(_patcherBuildRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherBuildLocalServiceUtil.addPatcherBuild(this);
		}
		else {
			PatcherBuildLocalServiceUtil.updatePatcherBuild(this);
		}
	}

	@Override
	public PatcherBuild toEscapedModel() {
		return (PatcherBuild)ProxyUtil.newProxyInstance(PatcherBuild.class.getClassLoader(),
			new Class[] { PatcherBuild.class }, new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherBuildClp clone = new PatcherBuildClp();

		clone.setPatcherBuildId(getPatcherBuildId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setPatcherAccountId(getPatcherAccountId());
		clone.setPatcherFixId(getPatcherFixId());
		clone.setPatcherProductVersionId(getPatcherProductVersionId());
		clone.setPatcherProjectVersionId(getPatcherProjectVersionId());
		clone.setTicketEntryId(getTicketEntryId());
		clone.setHotfixId(getHotfixId());
		clone.setName(getName());
		clone.setOriginalName(getOriginalName());
		clone.setKey(getKey());
		clone.setKeyVersion(getKeyVersion());
		clone.setType(getType());
		clone.setLatestBuild(getLatestBuild());
		clone.setLatestKeyBuild(getLatestKeyBuild());
		clone.setLatestLESATicketBuild(getLatestLESATicketBuild());
		clone.setLatestSupportTicketBuild(getLatestSupportTicketBuild());
		clone.setAccountEntryCode(getAccountEntryCode());
		clone.setLesaTicket(getLesaTicket());
		clone.setLesaTicketVersion(getLesaTicketVersion());
		clone.setSupportTicket(getSupportTicket());
		clone.setSupportTicketVersion(getSupportTicketVersion());
		clone.setFileName(getFileName());
		clone.setSourceName(getSourceName());
		clone.setChildBuild(getChildBuild());
		clone.setComments(getComments());
		clone.setQaComments(getQaComments());
		clone.setQaStatus(getQaStatus());
		clone.setRequestKey(getRequestKey());
		clone.setNotified(getNotified());
		clone.setProductVersion(getProductVersion());
		clone.setStatus(getStatus());
		clone.setStatusByUserId(getStatusByUserId());
		clone.setStatusByUserName(getStatusByUserName());
		clone.setStatusDate(getStatusDate());

		return clone;
	}

	@Override
	public int compareTo(PatcherBuild patcherBuild) {
		long primaryKey = patcherBuild.getPrimaryKey();

		if (getPrimaryKey() < primaryKey) {
			return -1;
		}
		else if (getPrimaryKey() > primaryKey) {
			return 1;
		}
		else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof PatcherBuildClp)) {
			return false;
		}

		PatcherBuildClp patcherBuild = (PatcherBuildClp)obj;

		long primaryKey = patcherBuild.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	public Class<?> getClpSerializerClass() {
		return _clpSerializerClass;
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(79);

		sb.append("{patcherBuildId=");
		sb.append(getPatcherBuildId());
		sb.append(", companyId=");
		sb.append(getCompanyId());
		sb.append(", userId=");
		sb.append(getUserId());
		sb.append(", userName=");
		sb.append(getUserName());
		sb.append(", createDate=");
		sb.append(getCreateDate());
		sb.append(", modifiedDate=");
		sb.append(getModifiedDate());
		sb.append(", patcherAccountId=");
		sb.append(getPatcherAccountId());
		sb.append(", patcherFixId=");
		sb.append(getPatcherFixId());
		sb.append(", patcherProductVersionId=");
		sb.append(getPatcherProductVersionId());
		sb.append(", patcherProjectVersionId=");
		sb.append(getPatcherProjectVersionId());
		sb.append(", ticketEntryId=");
		sb.append(getTicketEntryId());
		sb.append(", hotfixId=");
		sb.append(getHotfixId());
		sb.append(", name=");
		sb.append(getName());
		sb.append(", originalName=");
		sb.append(getOriginalName());
		sb.append(", key=");
		sb.append(getKey());
		sb.append(", keyVersion=");
		sb.append(getKeyVersion());
		sb.append(", type=");
		sb.append(getType());
		sb.append(", latestBuild=");
		sb.append(getLatestBuild());
		sb.append(", latestKeyBuild=");
		sb.append(getLatestKeyBuild());
		sb.append(", latestLESATicketBuild=");
		sb.append(getLatestLESATicketBuild());
		sb.append(", latestSupportTicketBuild=");
		sb.append(getLatestSupportTicketBuild());
		sb.append(", accountEntryCode=");
		sb.append(getAccountEntryCode());
		sb.append(", lesaTicket=");
		sb.append(getLesaTicket());
		sb.append(", lesaTicketVersion=");
		sb.append(getLesaTicketVersion());
		sb.append(", supportTicket=");
		sb.append(getSupportTicket());
		sb.append(", supportTicketVersion=");
		sb.append(getSupportTicketVersion());
		sb.append(", fileName=");
		sb.append(getFileName());
		sb.append(", sourceName=");
		sb.append(getSourceName());
		sb.append(", childBuild=");
		sb.append(getChildBuild());
		sb.append(", comments=");
		sb.append(getComments());
		sb.append(", qaComments=");
		sb.append(getQaComments());
		sb.append(", qaStatus=");
		sb.append(getQaStatus());
		sb.append(", requestKey=");
		sb.append(getRequestKey());
		sb.append(", notified=");
		sb.append(getNotified());
		sb.append(", productVersion=");
		sb.append(getProductVersion());
		sb.append(", status=");
		sb.append(getStatus());
		sb.append(", statusByUserId=");
		sb.append(getStatusByUserId());
		sb.append(", statusByUserName=");
		sb.append(getStatusByUserName());
		sb.append(", statusDate=");
		sb.append(getStatusDate());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(121);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherBuild");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherBuildId</column-name><column-value><![CDATA[");
		sb.append(getPatcherBuildId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>companyId</column-name><column-value><![CDATA[");
		sb.append(getCompanyId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userId</column-name><column-value><![CDATA[");
		sb.append(getUserId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userName</column-name><column-value><![CDATA[");
		sb.append(getUserName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>createDate</column-name><column-value><![CDATA[");
		sb.append(getCreateDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>modifiedDate</column-name><column-value><![CDATA[");
		sb.append(getModifiedDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherAccountId</column-name><column-value><![CDATA[");
		sb.append(getPatcherAccountId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherFixId</column-name><column-value><![CDATA[");
		sb.append(getPatcherFixId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherProductVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProductVersionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherProjectVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProjectVersionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>ticketEntryId</column-name><column-value><![CDATA[");
		sb.append(getTicketEntryId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>hotfixId</column-name><column-value><![CDATA[");
		sb.append(getHotfixId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>name</column-name><column-value><![CDATA[");
		sb.append(getName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>originalName</column-name><column-value><![CDATA[");
		sb.append(getOriginalName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>key</column-name><column-value><![CDATA[");
		sb.append(getKey());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>keyVersion</column-name><column-value><![CDATA[");
		sb.append(getKeyVersion());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>type</column-name><column-value><![CDATA[");
		sb.append(getType());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>latestBuild</column-name><column-value><![CDATA[");
		sb.append(getLatestBuild());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>latestKeyBuild</column-name><column-value><![CDATA[");
		sb.append(getLatestKeyBuild());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>latestLESATicketBuild</column-name><column-value><![CDATA[");
		sb.append(getLatestLESATicketBuild());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>latestSupportTicketBuild</column-name><column-value><![CDATA[");
		sb.append(getLatestSupportTicketBuild());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>accountEntryCode</column-name><column-value><![CDATA[");
		sb.append(getAccountEntryCode());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>lesaTicket</column-name><column-value><![CDATA[");
		sb.append(getLesaTicket());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>lesaTicketVersion</column-name><column-value><![CDATA[");
		sb.append(getLesaTicketVersion());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>supportTicket</column-name><column-value><![CDATA[");
		sb.append(getSupportTicket());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>supportTicketVersion</column-name><column-value><![CDATA[");
		sb.append(getSupportTicketVersion());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>fileName</column-name><column-value><![CDATA[");
		sb.append(getFileName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>sourceName</column-name><column-value><![CDATA[");
		sb.append(getSourceName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>childBuild</column-name><column-value><![CDATA[");
		sb.append(getChildBuild());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>comments</column-name><column-value><![CDATA[");
		sb.append(getComments());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>qaComments</column-name><column-value><![CDATA[");
		sb.append(getQaComments());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>qaStatus</column-name><column-value><![CDATA[");
		sb.append(getQaStatus());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>requestKey</column-name><column-value><![CDATA[");
		sb.append(getRequestKey());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>notified</column-name><column-value><![CDATA[");
		sb.append(getNotified());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>productVersion</column-name><column-value><![CDATA[");
		sb.append(getProductVersion());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>status</column-name><column-value><![CDATA[");
		sb.append(getStatus());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>statusByUserId</column-name><column-value><![CDATA[");
		sb.append(getStatusByUserId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>statusByUserName</column-name><column-value><![CDATA[");
		sb.append(getStatusByUserName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>statusDate</column-name><column-value><![CDATA[");
		sb.append(getStatusDate());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherBuildId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _patcherAccountId;
	private long _patcherFixId;
	private long _patcherProductVersionId;
	private long _patcherProjectVersionId;
	private long _ticketEntryId;
	private long _hotfixId;
	private String _name;
	private String _originalName;
	private String _key;
	private double _keyVersion;
	private int _type;
	private boolean _latestBuild;
	private boolean _latestKeyBuild;
	private boolean _latestLESATicketBuild;
	private boolean _latestSupportTicketBuild;
	private String _accountEntryCode;
	private String _lesaTicket;
	private double _lesaTicketVersion;
	private String _supportTicket;
	private double _supportTicketVersion;
	private String _fileName;
	private String _sourceName;
	private boolean _childBuild;
	private String _comments;
	private String _qaComments;
	private int _qaStatus;
	private String _requestKey;
	private boolean _notified;
	private int _productVersion;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserUuid;
	private String _statusByUserName;
	private Date _statusDate;
	private BaseModel<?> _patcherBuildRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}