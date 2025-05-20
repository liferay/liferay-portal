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
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
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
public class PatcherAccountClp extends BaseModelImpl<PatcherAccount>
	implements PatcherAccount {
	public PatcherAccountClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherAccount.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherAccount.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherAccountId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherAccountId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherAccountId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherAccountId", getPatcherAccountId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("accountEntryId", getAccountEntryId());
		attributes.put("accountEntryCode", getAccountEntryCode());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherAccountId = (Long)attributes.get("patcherAccountId");

		if (patcherAccountId != null) {
			setPatcherAccountId(patcherAccountId);
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

		Long accountEntryId = (Long)attributes.get("accountEntryId");

		if (accountEntryId != null) {
			setAccountEntryId(accountEntryId);
		}

		String accountEntryCode = (String)attributes.get("accountEntryCode");

		if (accountEntryCode != null) {
			setAccountEntryCode(accountEntryCode);
		}
	}

	@Override
	public long getPatcherAccountId() {
		return _patcherAccountId;
	}

	@Override
	public void setPatcherAccountId(long patcherAccountId) {
		_patcherAccountId = patcherAccountId;

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherAccountId",
						long.class);

				method.invoke(_patcherAccountRemoteModel, patcherAccountId);
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

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setCompanyId", long.class);

				method.invoke(_patcherAccountRemoteModel, companyId);
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

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setUserId", long.class);

				method.invoke(_patcherAccountRemoteModel, userId);
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

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setUserName", String.class);

				method.invoke(_patcherAccountRemoteModel, userName);
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

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setCreateDate", Date.class);

				method.invoke(_patcherAccountRemoteModel, createDate);
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

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setModifiedDate", Date.class);

				method.invoke(_patcherAccountRemoteModel, modifiedDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getAccountEntryId() {
		return _accountEntryId;
	}

	@Override
	public void setAccountEntryId(long accountEntryId) {
		_accountEntryId = accountEntryId;

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setAccountEntryId", long.class);

				method.invoke(_patcherAccountRemoteModel, accountEntryId);
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

		if (_patcherAccountRemoteModel != null) {
			try {
				Class<?> clazz = _patcherAccountRemoteModel.getClass();

				Method method = clazz.getMethod("setAccountEntryCode",
						String.class);

				method.invoke(_patcherAccountRemoteModel, accountEntryCode);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	public BaseModel<?> getPatcherAccountRemoteModel() {
		return _patcherAccountRemoteModel;
	}

	public void setPatcherAccountRemoteModel(
		BaseModel<?> patcherAccountRemoteModel) {
		_patcherAccountRemoteModel = patcherAccountRemoteModel;
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

		Class<?> remoteModelClass = _patcherAccountRemoteModel.getClass();

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

		Object returnValue = method.invoke(_patcherAccountRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherAccountLocalServiceUtil.addPatcherAccount(this);
		}
		else {
			PatcherAccountLocalServiceUtil.updatePatcherAccount(this);
		}
	}

	@Override
	public PatcherAccount toEscapedModel() {
		return (PatcherAccount)ProxyUtil.newProxyInstance(PatcherAccount.class.getClassLoader(),
			new Class[] { PatcherAccount.class },
			new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherAccountClp clone = new PatcherAccountClp();

		clone.setPatcherAccountId(getPatcherAccountId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setAccountEntryId(getAccountEntryId());
		clone.setAccountEntryCode(getAccountEntryCode());

		return clone;
	}

	@Override
	public int compareTo(PatcherAccount patcherAccount) {
		long primaryKey = patcherAccount.getPrimaryKey();

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

		if (!(obj instanceof PatcherAccountClp)) {
			return false;
		}

		PatcherAccountClp patcherAccount = (PatcherAccountClp)obj;

		long primaryKey = patcherAccount.getPrimaryKey();

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
		StringBundler sb = new StringBundler(17);

		sb.append("{patcherAccountId=");
		sb.append(getPatcherAccountId());
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
		sb.append(", accountEntryId=");
		sb.append(getAccountEntryId());
		sb.append(", accountEntryCode=");
		sb.append(getAccountEntryCode());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(28);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherAccount");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherAccountId</column-name><column-value><![CDATA[");
		sb.append(getPatcherAccountId());
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
			"<column><column-name>accountEntryId</column-name><column-value><![CDATA[");
		sb.append(getAccountEntryId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>accountEntryCode</column-name><column-value><![CDATA[");
		sb.append(getAccountEntryCode());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherAccountId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _accountEntryId;
	private String _accountEntryCode;
	private BaseModel<?> _patcherAccountRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}