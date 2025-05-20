/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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
public class PatcherProductVersionSoap implements Serializable {
	public static PatcherProductVersionSoap toSoapModel(
		PatcherProductVersion model) {
		PatcherProductVersionSoap soapModel = new PatcherProductVersionSoap();

		soapModel.setPatcherProductVersionId(model.getPatcherProductVersionId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setName(model.getName());
		soapModel.setFixDeliveryMethod(model.getFixDeliveryMethod());
		soapModel.setModuleFolderName(model.getModuleFolderName());

		return soapModel;
	}

	public static PatcherProductVersionSoap[] toSoapModels(
		PatcherProductVersion[] models) {
		PatcherProductVersionSoap[] soapModels = new PatcherProductVersionSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherProductVersionSoap[][] toSoapModels(
		PatcherProductVersion[][] models) {
		PatcherProductVersionSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherProductVersionSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherProductVersionSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherProductVersionSoap[] toSoapModels(
		List<PatcherProductVersion> models) {
		List<PatcherProductVersionSoap> soapModels = new ArrayList<PatcherProductVersionSoap>(models.size());

		for (PatcherProductVersion model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherProductVersionSoap[soapModels.size()]);
	}

	public PatcherProductVersionSoap() {
	}

	public long getPrimaryKey() {
		return _patcherProductVersionId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherProductVersionId(pk);
	}

	public long getPatcherProductVersionId() {
		return _patcherProductVersionId;
	}

	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProductVersionId = patcherProductVersionId;
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

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public int getFixDeliveryMethod() {
		return _fixDeliveryMethod;
	}

	public void setFixDeliveryMethod(int fixDeliveryMethod) {
		_fixDeliveryMethod = fixDeliveryMethod;
	}

	public String getModuleFolderName() {
		return _moduleFolderName;
	}

	public void setModuleFolderName(String moduleFolderName) {
		_moduleFolderName = moduleFolderName;
	}

	private long _patcherProductVersionId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private String _name;
	private int _fixDeliveryMethod;
	private String _moduleFolderName;
}