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

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Calvin Keum
 * @generated
 */
public class PatcherBuildSoap implements Serializable {
	public static PatcherBuildSoap toSoapModel(PatcherBuild model) {
		PatcherBuildSoap soapModel = new PatcherBuildSoap();

		soapModel.setPatcherBuildId(model.getPatcherBuildId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setPatcherAccountId(model.getPatcherAccountId());
		soapModel.setPatcherFixId(model.getPatcherFixId());
		soapModel.setPatcherProductVersionId(model.getPatcherProductVersionId());
		soapModel.setPatcherProjectVersionId(model.getPatcherProjectVersionId());
		soapModel.setTicketEntryId(model.getTicketEntryId());
		soapModel.setHotfixId(model.getHotfixId());
		soapModel.setName(model.getName());
		soapModel.setOriginalName(model.getOriginalName());
		soapModel.setKey(model.getKey());
		soapModel.setKeyVersion(model.getKeyVersion());
		soapModel.setType(model.getType());
		soapModel.setLatestBuild(model.getLatestBuild());
		soapModel.setLatestKeyBuild(model.getLatestKeyBuild());
		soapModel.setLatestLESATicketBuild(model.getLatestLESATicketBuild());
		soapModel.setLatestSupportTicketBuild(model.getLatestSupportTicketBuild());
		soapModel.setAccountEntryCode(model.getAccountEntryCode());
		soapModel.setLesaTicket(model.getLesaTicket());
		soapModel.setLesaTicketVersion(model.getLesaTicketVersion());
		soapModel.setSupportTicket(model.getSupportTicket());
		soapModel.setSupportTicketVersion(model.getSupportTicketVersion());
		soapModel.setFileName(model.getFileName());
		soapModel.setSourceName(model.getSourceName());
		soapModel.setChildBuild(model.getChildBuild());
		soapModel.setComments(model.getComments());
		soapModel.setQaComments(model.getQaComments());
		soapModel.setQaStatus(model.getQaStatus());
		soapModel.setRequestKey(model.getRequestKey());
		soapModel.setNotified(model.getNotified());
		soapModel.setProductVersion(model.getProductVersion());
		soapModel.setStatus(model.getStatus());
		soapModel.setStatusByUserId(model.getStatusByUserId());
		soapModel.setStatusByUserName(model.getStatusByUserName());
		soapModel.setStatusDate(model.getStatusDate());

		return soapModel;
	}

	public static PatcherBuildSoap[] toSoapModels(PatcherBuild[] models) {
		PatcherBuildSoap[] soapModels = new PatcherBuildSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherBuildSoap[][] toSoapModels(PatcherBuild[][] models) {
		PatcherBuildSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherBuildSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherBuildSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherBuildSoap[] toSoapModels(List<PatcherBuild> models) {
		List<PatcherBuildSoap> soapModels = new ArrayList<PatcherBuildSoap>(models.size());

		for (PatcherBuild model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherBuildSoap[soapModels.size()]);
	}

	public PatcherBuildSoap() {
	}

	public long getPrimaryKey() {
		return _patcherBuildId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherBuildId(pk);
	}

	public long getPatcherBuildId() {
		return _patcherBuildId;
	}

	public void setPatcherBuildId(long patcherBuildId) {
		_patcherBuildId = patcherBuildId;
	}

	public long getCompanyId() {
		return _companyId;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public Date getCreateDate() {
		return _createDate;
	}

	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public long getPatcherAccountId() {
		return _patcherAccountId;
	}

	public void setPatcherAccountId(long patcherAccountId) {
		_patcherAccountId = patcherAccountId;
	}

	public long getPatcherFixId() {
		return _patcherFixId;
	}

	public void setPatcherFixId(long patcherFixId) {
		_patcherFixId = patcherFixId;
	}

	public long getPatcherProductVersionId() {
		return _patcherProductVersionId;
	}

	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProductVersionId = patcherProductVersionId;
	}

	public long getPatcherProjectVersionId() {
		return _patcherProjectVersionId;
	}

	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherProjectVersionId = patcherProjectVersionId;
	}

	public long getTicketEntryId() {
		return _ticketEntryId;
	}

	public void setTicketEntryId(long ticketEntryId) {
		_ticketEntryId = ticketEntryId;
	}

	public long getHotfixId() {
		return _hotfixId;
	}

	public void setHotfixId(long hotfixId) {
		_hotfixId = hotfixId;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public String getOriginalName() {
		return _originalName;
	}

	public void setOriginalName(String originalName) {
		_originalName = originalName;
	}

	public String getKey() {
		return _key;
	}

	public void setKey(String key) {
		_key = key;
	}

	public double getKeyVersion() {
		return _keyVersion;
	}

	public void setKeyVersion(double keyVersion) {
		_keyVersion = keyVersion;
	}

	public int getType() {
		return _type;
	}

	public void setType(int type) {
		_type = type;
	}

	public boolean getLatestBuild() {
		return _latestBuild;
	}

	public boolean isLatestBuild() {
		return _latestBuild;
	}

	public void setLatestBuild(boolean latestBuild) {
		_latestBuild = latestBuild;
	}

	public boolean getLatestKeyBuild() {
		return _latestKeyBuild;
	}

	public boolean isLatestKeyBuild() {
		return _latestKeyBuild;
	}

	public void setLatestKeyBuild(boolean latestKeyBuild) {
		_latestKeyBuild = latestKeyBuild;
	}

	public boolean getLatestLESATicketBuild() {
		return _latestLESATicketBuild;
	}

	public boolean isLatestLESATicketBuild() {
		return _latestLESATicketBuild;
	}

	public void setLatestLESATicketBuild(boolean latestLESATicketBuild) {
		_latestLESATicketBuild = latestLESATicketBuild;
	}

	public boolean getLatestSupportTicketBuild() {
		return _latestSupportTicketBuild;
	}

	public boolean isLatestSupportTicketBuild() {
		return _latestSupportTicketBuild;
	}

	public void setLatestSupportTicketBuild(boolean latestSupportTicketBuild) {
		_latestSupportTicketBuild = latestSupportTicketBuild;
	}

	public String getAccountEntryCode() {
		return _accountEntryCode;
	}

	public void setAccountEntryCode(String accountEntryCode) {
		_accountEntryCode = accountEntryCode;
	}

	public String getLesaTicket() {
		return _lesaTicket;
	}

	public void setLesaTicket(String lesaTicket) {
		_lesaTicket = lesaTicket;
	}

	public double getLesaTicketVersion() {
		return _lesaTicketVersion;
	}

	public void setLesaTicketVersion(double lesaTicketVersion) {
		_lesaTicketVersion = lesaTicketVersion;
	}

	public String getSupportTicket() {
		return _supportTicket;
	}

	public void setSupportTicket(String supportTicket) {
		_supportTicket = supportTicket;
	}

	public double getSupportTicketVersion() {
		return _supportTicketVersion;
	}

	public void setSupportTicketVersion(double supportTicketVersion) {
		_supportTicketVersion = supportTicketVersion;
	}

	public String getFileName() {
		return _fileName;
	}

	public void setFileName(String fileName) {
		_fileName = fileName;
	}

	public String getSourceName() {
		return _sourceName;
	}

	public void setSourceName(String sourceName) {
		_sourceName = sourceName;
	}

	public boolean getChildBuild() {
		return _childBuild;
	}

	public boolean isChildBuild() {
		return _childBuild;
	}

	public void setChildBuild(boolean childBuild) {
		_childBuild = childBuild;
	}

	public String getComments() {
		return _comments;
	}

	public void setComments(String comments) {
		_comments = comments;
	}

	public String getQaComments() {
		return _qaComments;
	}

	public void setQaComments(String qaComments) {
		_qaComments = qaComments;
	}

	public int getQaStatus() {
		return _qaStatus;
	}

	public void setQaStatus(int qaStatus) {
		_qaStatus = qaStatus;
	}

	public String getRequestKey() {
		return _requestKey;
	}

	public void setRequestKey(String requestKey) {
		_requestKey = requestKey;
	}

	public boolean getNotified() {
		return _notified;
	}

	public boolean isNotified() {
		return _notified;
	}

	public void setNotified(boolean notified) {
		_notified = notified;
	}

	public int getProductVersion() {
		return _productVersion;
	}

	public void setProductVersion(int productVersion) {
		_productVersion = productVersion;
	}

	public int getStatus() {
		return _status;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public long getStatusByUserId() {
		return _statusByUserId;
	}

	public void setStatusByUserId(long statusByUserId) {
		_statusByUserId = statusByUserId;
	}

	public String getStatusByUserName() {
		return _statusByUserName;
	}

	public void setStatusByUserName(String statusByUserName) {
		_statusByUserName = statusByUserName;
	}

	public Date getStatusDate() {
		return _statusDate;
	}

	public void setStatusDate(Date statusDate) {
		_statusDate = statusDate;
	}

	private long _patcherBuildId;
	private long _companyId;
	private long _userId;
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
	private String _statusByUserName;
	private Date _statusDate;
}