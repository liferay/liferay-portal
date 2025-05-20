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
public class PatcherFixPackSoap implements Serializable {
	public static PatcherFixPackSoap toSoapModel(PatcherFixPack model) {
		PatcherFixPackSoap soapModel = new PatcherFixPackSoap();

		soapModel.setPatcherFixPackId(model.getPatcherFixPackId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setPatcherBuildId(model.getPatcherBuildId());
		soapModel.setPatcherFixComponentId(model.getPatcherFixComponentId());
		soapModel.setPatcherProjectVersionId(model.getPatcherProjectVersionId());
		soapModel.setName(model.getName());
		soapModel.setVersion(model.getVersion());
		soapModel.setReleasedDate(model.getReleasedDate());
		soapModel.setRequirements(model.getRequirements());
		soapModel.setStatus(model.getStatus());

		return soapModel;
	}

	public static PatcherFixPackSoap[] toSoapModels(PatcherFixPack[] models) {
		PatcherFixPackSoap[] soapModels = new PatcherFixPackSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixPackSoap[][] toSoapModels(PatcherFixPack[][] models) {
		PatcherFixPackSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherFixPackSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherFixPackSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixPackSoap[] toSoapModels(List<PatcherFixPack> models) {
		List<PatcherFixPackSoap> soapModels = new ArrayList<PatcherFixPackSoap>(models.size());

		for (PatcherFixPack model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherFixPackSoap[soapModels.size()]);
	}

	public PatcherFixPackSoap() {
	}

	public long getPrimaryKey() {
		return _patcherFixPackId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherFixPackId(pk);
	}

	public long getPatcherFixPackId() {
		return _patcherFixPackId;
	}

	public void setPatcherFixPackId(long patcherFixPackId) {
		_patcherFixPackId = patcherFixPackId;
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

	public long getPatcherBuildId() {
		return _patcherBuildId;
	}

	public void setPatcherBuildId(long patcherBuildId) {
		_patcherBuildId = patcherBuildId;
	}

	public long getPatcherFixComponentId() {
		return _patcherFixComponentId;
	}

	public void setPatcherFixComponentId(long patcherFixComponentId) {
		_patcherFixComponentId = patcherFixComponentId;
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

	public int getVersion() {
		return _version;
	}

	public void setVersion(int version) {
		_version = version;
	}

	public Date getReleasedDate() {
		return _releasedDate;
	}

	public void setReleasedDate(Date releasedDate) {
		_releasedDate = releasedDate;
	}

	public String getRequirements() {
		return _requirements;
	}

	public void setRequirements(String requirements) {
		_requirements = requirements;
	}

	public int getStatus() {
		return _status;
	}

	public void setStatus(int status) {
		_status = status;
	}

	private long _patcherFixPackId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _patcherBuildId;
	private long _patcherFixComponentId;
	private long _patcherProjectVersionId;
	private String _name;
	private int _version;
	private Date _releasedDate;
	private String _requirements;
	private int _status;
}