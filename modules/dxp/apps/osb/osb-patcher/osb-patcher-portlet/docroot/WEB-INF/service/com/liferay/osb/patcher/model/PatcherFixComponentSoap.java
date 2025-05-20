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
public class PatcherFixComponentSoap implements Serializable {
	public static PatcherFixComponentSoap toSoapModel(PatcherFixComponent model) {
		PatcherFixComponentSoap soapModel = new PatcherFixComponentSoap();

		soapModel.setPatcherFixComponentId(model.getPatcherFixComponentId());
		soapModel.setCompanyId(model.getCompanyId());
		soapModel.setUserId(model.getUserId());
		soapModel.setUserName(model.getUserName());
		soapModel.setCreateDate(model.getCreateDate());
		soapModel.setModifiedDate(model.getModifiedDate());
		soapModel.setName(model.getName());

		return soapModel;
	}

	public static PatcherFixComponentSoap[] toSoapModels(
		PatcherFixComponent[] models) {
		PatcherFixComponentSoap[] soapModels = new PatcherFixComponentSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixComponentSoap[][] toSoapModels(
		PatcherFixComponent[][] models) {
		PatcherFixComponentSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherFixComponentSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherFixComponentSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixComponentSoap[] toSoapModels(
		List<PatcherFixComponent> models) {
		List<PatcherFixComponentSoap> soapModels = new ArrayList<PatcherFixComponentSoap>(models.size());

		for (PatcherFixComponent model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherFixComponentSoap[soapModels.size()]);
	}

	public PatcherFixComponentSoap() {
	}

	public long getPrimaryKey() {
		return _patcherFixComponentId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherFixComponentId(pk);
	}

	public long getPatcherFixComponentId() {
		return _patcherFixComponentId;
	}

	public void setPatcherFixComponentId(long patcherFixComponentId) {
		_patcherFixComponentId = patcherFixComponentId;
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

	private long _patcherFixComponentId;
	private long _companyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private String _name;
}