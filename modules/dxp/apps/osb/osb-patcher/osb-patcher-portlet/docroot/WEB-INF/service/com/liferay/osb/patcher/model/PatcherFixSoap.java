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
public class PatcherFixSoap implements Serializable {
	public static PatcherFixSoap toSoapModel(PatcherFix model) {
		PatcherFixSoap soapModel = new PatcherFixSoap();

		soapModel.setPatcherFixId(model.getPatcherFixId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setPatcherProductVersionId(model.getPatcherProductVersionId());
		soapModel.setPatcherProjectVersionId(model.getPatcherProjectVersionId());
		soapModel.setName(model.getName());
		soapModel.setKey(model.getKey());
		soapModel.setKeyVersion(model.getKeyVersion());
		soapModel.setType(model.getType());
		soapModel.setLatestFix(model.getLatestFix());
		soapModel.setObsolete(model.getObsolete());
		soapModel.setCommittish(model.getCommittish());
		soapModel.setGitHash(model.getGitHash());
		soapModel.setGitRemoteURL(model.getGitRemoteURL());
		soapModel.setDependencies(model.getDependencies());
		soapModel.setRequirements(model.getRequirements());
		soapModel.setRequestKey(model.getRequestKey());
		soapModel.setJenkinsResults(model.getJenkinsResults());
		soapModel.setComments(model.getComments());
		soapModel.setFixPackStatus(model.getFixPackStatus());
		soapModel.setNotified(model.getNotified());
		soapModel.setProductVersion(model.getProductVersion());
		soapModel.setStatus(model.getStatus());
		soapModel.setStatusByUserId(model.getStatusByUserId());
		soapModel.setStatusByUserName(model.getStatusByUserName());
		soapModel.setStatusDate(model.getStatusDate());

		return soapModel;
	}

	public static PatcherFixSoap[] toSoapModels(PatcherFix[] models) {
		PatcherFixSoap[] soapModels = new PatcherFixSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixSoap[][] toSoapModels(PatcherFix[][] models) {
		PatcherFixSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherFixSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherFixSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixSoap[] toSoapModels(List<PatcherFix> models) {
		List<PatcherFixSoap> soapModels = new ArrayList<PatcherFixSoap>(models.size());

		for (PatcherFix model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherFixSoap[soapModels.size()]);
	}

	public PatcherFixSoap() {
	}

	public long getPrimaryKey() {
		return _patcherFixId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherFixId(pk);
	}

	public long getPatcherFixId() {
		return _patcherFixId;
	}

	public void setPatcherFixId(long patcherFixId) {
		_patcherFixId = patcherFixId;
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

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
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

	public boolean getLatestFix() {
		return _latestFix;
	}

	public boolean isLatestFix() {
		return _latestFix;
	}

	public void setLatestFix(boolean latestFix) {
		_latestFix = latestFix;
	}

	public boolean getObsolete() {
		return _obsolete;
	}

	public boolean isObsolete() {
		return _obsolete;
	}

	public void setObsolete(boolean obsolete) {
		_obsolete = obsolete;
	}

	public String getCommittish() {
		return _committish;
	}

	public void setCommittish(String committish) {
		_committish = committish;
	}

	public String getGitHash() {
		return _gitHash;
	}

	public void setGitHash(String gitHash) {
		_gitHash = gitHash;
	}

	public String getGitRemoteURL() {
		return _gitRemoteURL;
	}

	public void setGitRemoteURL(String gitRemoteURL) {
		_gitRemoteURL = gitRemoteURL;
	}

	public String getDependencies() {
		return _dependencies;
	}

	public void setDependencies(String dependencies) {
		_dependencies = dependencies;
	}

	public String getRequirements() {
		return _requirements;
	}

	public void setRequirements(String requirements) {
		_requirements = requirements;
	}

	public String getRequestKey() {
		return _requestKey;
	}

	public void setRequestKey(String requestKey) {
		_requestKey = requestKey;
	}

	public String getJenkinsResults() {
		return _jenkinsResults;
	}

	public void setJenkinsResults(String jenkinsResults) {
		_jenkinsResults = jenkinsResults;
	}

	public String getComments() {
		return _comments;
	}

	public void setComments(String comments) {
		_comments = comments;
	}

	public int getFixPackStatus() {
		return _fixPackStatus;
	}

	public void setFixPackStatus(int fixPackStatus) {
		_fixPackStatus = fixPackStatus;
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

	private long _patcherFixId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _patcherProductVersionId;
	private long _patcherProjectVersionId;
	private String _name;
	private String _key;
	private double _keyVersion;
	private int _type;
	private boolean _latestFix;
	private boolean _obsolete;
	private String _committish;
	private String _gitHash;
	private String _gitRemoteURL;
	private String _dependencies;
	private String _requirements;
	private String _requestKey;
	private String _jenkinsResults;
	private String _comments;
	private int _fixPackStatus;
	private boolean _notified;
	private int _productVersion;
	private int _status;
	private long _statusByUserId;
	private String _statusByUserName;
	private Date _statusDate;
}