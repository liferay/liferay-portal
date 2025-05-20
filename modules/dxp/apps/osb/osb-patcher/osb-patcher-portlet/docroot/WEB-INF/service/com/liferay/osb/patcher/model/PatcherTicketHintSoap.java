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
public class PatcherTicketHintSoap implements Serializable {
	public static PatcherTicketHintSoap toSoapModel(PatcherTicketHint model) {
		PatcherTicketHintSoap soapModel = new PatcherTicketHintSoap();

		soapModel.setPatcherTicketHintId(model.getPatcherTicketHintId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setPatcherProductVersionId(model.getPatcherProductVersionId());
		soapModel.setScript(model.getScript());

		return soapModel;
	}

	public static PatcherTicketHintSoap[] toSoapModels(
		PatcherTicketHint[] models) {
		PatcherTicketHintSoap[] soapModels = new PatcherTicketHintSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherTicketHintSoap[][] toSoapModels(
		PatcherTicketHint[][] models) {
		PatcherTicketHintSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherTicketHintSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherTicketHintSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherTicketHintSoap[] toSoapModels(
		List<PatcherTicketHint> models) {
		List<PatcherTicketHintSoap> soapModels = new ArrayList<PatcherTicketHintSoap>(models.size());

		for (PatcherTicketHint model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherTicketHintSoap[soapModels.size()]);
	}

	public PatcherTicketHintSoap() {
	}

	public long getPrimaryKey() {
		return _patcherTicketHintId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherTicketHintId(pk);
	}

	public long getPatcherTicketHintId() {
		return _patcherTicketHintId;
	}

	public void setPatcherTicketHintId(long patcherTicketHintId) {
		_patcherTicketHintId = patcherTicketHintId;
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

	public String getScript() {
		return _script;
	}

	public void setScript(String script) {
		_script = script;
	}

	private long _patcherTicketHintId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _patcherProductVersionId;
	private String _script;
}