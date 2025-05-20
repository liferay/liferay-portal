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
import com.liferay.osb.patcher.service.PatcherProductVersionLocalServiceUtil;

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
public class PatcherProductVersionClp extends BaseModelImpl<PatcherProductVersion>
	implements PatcherProductVersion {
	public PatcherProductVersionClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherProductVersion.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherProductVersion.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherProductVersionId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherProductVersionId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherProductVersionId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("name", getName());
		attributes.put("fixDeliveryMethod", getFixDeliveryMethod());
		attributes.put("moduleFolderName", getModuleFolderName());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherProductVersionId = (Long)attributes.get(
				"patcherProductVersionId");

		if (patcherProductVersionId != null) {
			setPatcherProductVersionId(patcherProductVersionId);
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

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Integer fixDeliveryMethod = (Integer)attributes.get("fixDeliveryMethod");

		if (fixDeliveryMethod != null) {
			setFixDeliveryMethod(fixDeliveryMethod);
		}

		String moduleFolderName = (String)attributes.get("moduleFolderName");

		if (moduleFolderName != null) {
			setModuleFolderName(moduleFolderName);
		}
	}

	@Override
	public long getPatcherProductVersionId() {
		return _patcherProductVersionId;
	}

	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProductVersionId = patcherProductVersionId;

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProductVersionId",
						long.class);

				method.invoke(_patcherProductVersionRemoteModel,
					patcherProductVersionId);
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

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setCompanyId", long.class);

				method.invoke(_patcherProductVersionRemoteModel, companyId);
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

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setUserId", long.class);

				method.invoke(_patcherProductVersionRemoteModel, userId);
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

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setUserName", String.class);

				method.invoke(_patcherProductVersionRemoteModel, userName);
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

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setCreateDate", Date.class);

				method.invoke(_patcherProductVersionRemoteModel, createDate);
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

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setModifiedDate", Date.class);

				method.invoke(_patcherProductVersionRemoteModel, modifiedDate);
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

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setName", String.class);

				method.invoke(_patcherProductVersionRemoteModel, name);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getFixDeliveryMethod() {
		return _fixDeliveryMethod;
	}

	@Override
	public void setFixDeliveryMethod(int fixDeliveryMethod) {
		_fixDeliveryMethod = fixDeliveryMethod;

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setFixDeliveryMethod",
						int.class);

				method.invoke(_patcherProductVersionRemoteModel,
					fixDeliveryMethod);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getModuleFolderName() {
		return _moduleFolderName;
	}

	@Override
	public void setModuleFolderName(String moduleFolderName) {
		_moduleFolderName = moduleFolderName;

		if (_patcherProductVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProductVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setModuleFolderName",
						String.class);

				method.invoke(_patcherProductVersionRemoteModel,
					moduleFolderName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	public BaseModel<?> getPatcherProductVersionRemoteModel() {
		return _patcherProductVersionRemoteModel;
	}

	public void setPatcherProductVersionRemoteModel(
		BaseModel<?> patcherProductVersionRemoteModel) {
		_patcherProductVersionRemoteModel = patcherProductVersionRemoteModel;
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

		Class<?> remoteModelClass = _patcherProductVersionRemoteModel.getClass();

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

		Object returnValue = method.invoke(_patcherProductVersionRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherProductVersionLocalServiceUtil.addPatcherProductVersion(this);
		}
		else {
			PatcherProductVersionLocalServiceUtil.updatePatcherProductVersion(this);
		}
	}

	@Override
	public PatcherProductVersion toEscapedModel() {
		return (PatcherProductVersion)ProxyUtil.newProxyInstance(PatcherProductVersion.class.getClassLoader(),
			new Class[] { PatcherProductVersion.class },
			new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherProductVersionClp clone = new PatcherProductVersionClp();

		clone.setPatcherProductVersionId(getPatcherProductVersionId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setName(getName());
		clone.setFixDeliveryMethod(getFixDeliveryMethod());
		clone.setModuleFolderName(getModuleFolderName());

		return clone;
	}

	@Override
	public int compareTo(PatcherProductVersion patcherProductVersion) {
		long primaryKey = patcherProductVersion.getPrimaryKey();

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

		if (!(obj instanceof PatcherProductVersionClp)) {
			return false;
		}

		PatcherProductVersionClp patcherProductVersion = (PatcherProductVersionClp)obj;

		long primaryKey = patcherProductVersion.getPrimaryKey();

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
		StringBundler sb = new StringBundler(19);

		sb.append("{patcherProductVersionId=");
		sb.append(getPatcherProductVersionId());
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
		sb.append(", name=");
		sb.append(getName());
		sb.append(", fixDeliveryMethod=");
		sb.append(getFixDeliveryMethod());
		sb.append(", moduleFolderName=");
		sb.append(getModuleFolderName());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(31);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherProductVersion");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherProductVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProductVersionId());
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
			"<column><column-name>name</column-name><column-value><![CDATA[");
		sb.append(getName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>fixDeliveryMethod</column-name><column-value><![CDATA[");
		sb.append(getFixDeliveryMethod());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>moduleFolderName</column-name><column-value><![CDATA[");
		sb.append(getModuleFolderName());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherProductVersionId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private String _name;
	private int _fixDeliveryMethod;
	private String _moduleFolderName;
	private BaseModel<?> _patcherProductVersionRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}