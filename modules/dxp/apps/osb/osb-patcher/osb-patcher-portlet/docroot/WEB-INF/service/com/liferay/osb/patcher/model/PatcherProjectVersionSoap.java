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
public class PatcherProjectVersionSoap implements Serializable {
	public static PatcherProjectVersionSoap toSoapModel(
		PatcherProjectVersion model) {
		PatcherProjectVersionSoap soapModel = new PatcherProjectVersionSoap();

		soapModel.setPatcherProjectVersionId(model.getPatcherProjectVersionId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setPatcherProductVersionId(model.getPatcherProductVersionId());
		soapModel.setRootPatcherProjectVersionId(model.getRootPatcherProjectVersionId());
		soapModel.setName(model.getName());
		soapModel.setCombinedBranch(model.getCombinedBranch());
		soapModel.setHide(model.getHide());
		soapModel.setCommittish(model.getCommittish());
		soapModel.setRepositoryName(model.getRepositoryName());
		soapModel.setFixedIssues(model.getFixedIssues());
		soapModel.setProductVersion(model.getProductVersion());

		return soapModel;
	}

	public static PatcherProjectVersionSoap[] toSoapModels(
		PatcherProjectVersion[] models) {
		PatcherProjectVersionSoap[] soapModels = new PatcherProjectVersionSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherProjectVersionSoap[][] toSoapModels(
		PatcherProjectVersion[][] models) {
		PatcherProjectVersionSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherProjectVersionSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherProjectVersionSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherProjectVersionSoap[] toSoapModels(
		List<PatcherProjectVersion> models) {
		List<PatcherProjectVersionSoap> soapModels = new ArrayList<PatcherProjectVersionSoap>(models.size());

		for (PatcherProjectVersion model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherProjectVersionSoap[soapModels.size()]);
	}

	public PatcherProjectVersionSoap() {
	}

	public long getPrimaryKey() {
		return _patcherProjectVersionId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherProjectVersionId(pk);
	}

	public long getPatcherProjectVersionId() {
		return _patcherProjectVersionId;
	}

	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherProjectVersionId = patcherProjectVersionId;
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

	public long getRootPatcherProjectVersionId() {
		return _rootPatcherProjectVersionId;
	}

	public void setRootPatcherProjectVersionId(long rootPatcherProjectVersionId) {
		_rootPatcherProjectVersionId = rootPatcherProjectVersionId;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public boolean getCombinedBranch() {
		return _combinedBranch;
	}

	public boolean isCombinedBranch() {
		return _combinedBranch;
	}

	public void setCombinedBranch(boolean combinedBranch) {
		_combinedBranch = combinedBranch;
	}

	public boolean getHide() {
		return _hide;
	}

	public boolean isHide() {
		return _hide;
	}

	public void setHide(boolean hide) {
		_hide = hide;
	}

	public String getCommittish() {
		return _committish;
	}

	public void setCommittish(String committish) {
		_committish = committish;
	}

	public String getRepositoryName() {
		return _repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		_repositoryName = repositoryName;
	}

	public String getFixedIssues() {
		return _fixedIssues;
	}

	public void setFixedIssues(String fixedIssues) {
		_fixedIssues = fixedIssues;
	}

	public int getProductVersion() {
		return _productVersion;
	}

	public void setProductVersion(int productVersion) {
		_productVersion = productVersion;
	}

	private long _patcherProjectVersionId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _patcherProductVersionId;
	private long _rootPatcherProjectVersionId;
	private String _name;
	private boolean _combinedBranch;
	private boolean _hide;
	private String _committish;
	private String _repositoryName;
	private String _fixedIssues;
	private int _productVersion;
}