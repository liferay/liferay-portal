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
public class PatcherAccountSoap implements Serializable {
	public static PatcherAccountSoap toSoapModel(PatcherAccount model) {
		PatcherAccountSoap soapModel = new PatcherAccountSoap();

		soapModel.setPatcherAccountId(model.getPatcherAccountId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setAccountEntryId(model.getAccountEntryId());
		soapModel.setAccountEntryCode(model.getAccountEntryCode());

		return soapModel;
	}

	public static PatcherAccountSoap[] toSoapModels(PatcherAccount[] models) {
		PatcherAccountSoap[] soapModels = new PatcherAccountSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherAccountSoap[][] toSoapModels(PatcherAccount[][] models) {
		PatcherAccountSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherAccountSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherAccountSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherAccountSoap[] toSoapModels(List<PatcherAccount> models) {
		List<PatcherAccountSoap> soapModels = new ArrayList<PatcherAccountSoap>(models.size());

		for (PatcherAccount model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherAccountSoap[soapModels.size()]);
	}

	public PatcherAccountSoap() {
	}

	public long getPrimaryKey() {
		return _patcherAccountId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherAccountId(pk);
	}

	public long getPatcherAccountId() {
		return _patcherAccountId;
	}

	public void setPatcherAccountId(long patcherAccountId) {
		_patcherAccountId = patcherAccountId;
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

	public long getAccountEntryId() {
		return _accountEntryId;
	}

	public void setAccountEntryId(long accountEntryId) {
		_accountEntryId = accountEntryId;
	}

	public String getAccountEntryCode() {
		return _accountEntryCode;
	}

	public void setAccountEntryCode(String accountEntryCode) {
		_accountEntryCode = accountEntryCode;
	}

	private long _patcherAccountId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _accountEntryId;
	private String _accountEntryCode;
}