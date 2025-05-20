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
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;

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
public class PatcherFixPackClp extends BaseModelImpl<PatcherFixPack>
	implements PatcherFixPack {
	public PatcherFixPackClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherFixPack.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherFixPack.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherFixPackId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherFixPackId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherFixPackId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherFixPackId", getPatcherFixPackId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherBuildId", getPatcherBuildId());
		attributes.put("patcherFixComponentId", getPatcherFixComponentId());
		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("name", getName());
		attributes.put("version", getVersion());
		attributes.put("releasedDate", getReleasedDate());
		attributes.put("requirements", getRequirements());
		attributes.put("status", getStatus());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherFixPackId = (Long)attributes.get("patcherFixPackId");

		if (patcherFixPackId != null) {
			setPatcherFixPackId(patcherFixPackId);
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

		Long patcherBuildId = (Long)attributes.get("patcherBuildId");

		if (patcherBuildId != null) {
			setPatcherBuildId(patcherBuildId);
		}

		Long patcherFixComponentId = (Long)attributes.get(
				"patcherFixComponentId");

		if (patcherFixComponentId != null) {
			setPatcherFixComponentId(patcherFixComponentId);
		}

		Long patcherProjectVersionId = (Long)attributes.get(
				"patcherProjectVersionId");

		if (patcherProjectVersionId != null) {
			setPatcherProjectVersionId(patcherProjectVersionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Integer version = (Integer)attributes.get("version");

		if (version != null) {
			setVersion(version);
		}

		Date releasedDate = (Date)attributes.get("releasedDate");

		if (releasedDate != null) {
			setReleasedDate(releasedDate);
		}

		String requirements = (String)attributes.get("requirements");

		if (requirements != null) {
			setRequirements(requirements);
		}

		Integer status = (Integer)attributes.get("status");

		if (status != null) {
			setStatus(status);
		}
	}

	@Override
	public long getPatcherFixPackId() {
		return _patcherFixPackId;
	}

	@Override
	public void setPatcherFixPackId(long patcherFixPackId) {
		_patcherFixPackId = patcherFixPackId;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherFixPackId",
						long.class);

				method.invoke(_patcherFixPackRemoteModel, patcherFixPackId);
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

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setCompanyId", long.class);

				method.invoke(_patcherFixPackRemoteModel, companyId);
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

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setUserId", long.class);

				method.invoke(_patcherFixPackRemoteModel, userId);
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

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setUserName", String.class);

				method.invoke(_patcherFixPackRemoteModel, userName);
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

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setCreateDate", Date.class);

				method.invoke(_patcherFixPackRemoteModel, createDate);
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

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setModifiedDate", Date.class);

				method.invoke(_patcherFixPackRemoteModel, modifiedDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherBuildId() {
		return _patcherBuildId;
	}

	@Override
	public void setPatcherBuildId(long patcherBuildId) {
		_patcherBuildId = patcherBuildId;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherBuildId", long.class);

				method.invoke(_patcherFixPackRemoteModel, patcherBuildId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherFixComponentId() {
		return _patcherFixComponentId;
	}

	@Override
	public void setPatcherFixComponentId(long patcherFixComponentId) {
		_patcherFixComponentId = patcherFixComponentId;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherFixComponentId",
						long.class);

				method.invoke(_patcherFixPackRemoteModel, patcherFixComponentId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherProjectVersionId() {
		return _patcherProjectVersionId;
	}

	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherProjectVersionId = patcherProjectVersionId;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProjectVersionId",
						long.class);

				method.invoke(_patcherFixPackRemoteModel,
					patcherProjectVersionId);
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

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setName", String.class);

				method.invoke(_patcherFixPackRemoteModel, name);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getVersion() {
		return _version;
	}

	@Override
	public void setVersion(int version) {
		_version = version;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setVersion", int.class);

				method.invoke(_patcherFixPackRemoteModel, version);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public Date getReleasedDate() {
		return _releasedDate;
	}

	@Override
	public void setReleasedDate(Date releasedDate) {
		_releasedDate = releasedDate;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setReleasedDate", Date.class);

				method.invoke(_patcherFixPackRemoteModel, releasedDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getRequirements() {
		return _requirements;
	}

	@Override
	public void setRequirements(String requirements) {
		_requirements = requirements;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setRequirements", String.class);

				method.invoke(_patcherFixPackRemoteModel, requirements);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getStatus() {
		return _status;
	}

	@Override
	public void setStatus(int status) {
		_status = status;

		if (_patcherFixPackRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixPackRemoteModel.getClass();

				Method method = clazz.getMethod("setStatus", int.class);

				method.invoke(_patcherFixPackRemoteModel, status);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	public BaseModel<?> getPatcherFixPackRemoteModel() {
		return _patcherFixPackRemoteModel;
	}

	public void setPatcherFixPackRemoteModel(
		BaseModel<?> patcherFixPackRemoteModel) {
		_patcherFixPackRemoteModel = patcherFixPackRemoteModel;
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

		Class<?> remoteModelClass = _patcherFixPackRemoteModel.getClass();

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

		Object returnValue = method.invoke(_patcherFixPackRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherFixPackLocalServiceUtil.addPatcherFixPack(this);
		}
		else {
			PatcherFixPackLocalServiceUtil.updatePatcherFixPack(this);
		}
	}

	@Override
	public PatcherFixPack toEscapedModel() {
		return (PatcherFixPack)ProxyUtil.newProxyInstance(PatcherFixPack.class.getClassLoader(),
			new Class[] { PatcherFixPack.class },
			new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherFixPackClp clone = new PatcherFixPackClp();

		clone.setPatcherFixPackId(getPatcherFixPackId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setPatcherBuildId(getPatcherBuildId());
		clone.setPatcherFixComponentId(getPatcherFixComponentId());
		clone.setPatcherProjectVersionId(getPatcherProjectVersionId());
		clone.setName(getName());
		clone.setVersion(getVersion());
		clone.setReleasedDate(getReleasedDate());
		clone.setRequirements(getRequirements());
		clone.setStatus(getStatus());

		return clone;
	}

	@Override
	public int compareTo(PatcherFixPack patcherFixPack) {
		long primaryKey = patcherFixPack.getPrimaryKey();

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

		if (!(obj instanceof PatcherFixPackClp)) {
			return false;
		}

		PatcherFixPackClp patcherFixPack = (PatcherFixPackClp)obj;

		long primaryKey = patcherFixPack.getPrimaryKey();

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
		StringBundler sb = new StringBundler(29);

		sb.append("{patcherFixPackId=");
		sb.append(getPatcherFixPackId());
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
		sb.append(", patcherBuildId=");
		sb.append(getPatcherBuildId());
		sb.append(", patcherFixComponentId=");
		sb.append(getPatcherFixComponentId());
		sb.append(", patcherProjectVersionId=");
		sb.append(getPatcherProjectVersionId());
		sb.append(", name=");
		sb.append(getName());
		sb.append(", version=");
		sb.append(getVersion());
		sb.append(", releasedDate=");
		sb.append(getReleasedDate());
		sb.append(", requirements=");
		sb.append(getRequirements());
		sb.append(", status=");
		sb.append(getStatus());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(46);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherFixPack");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherFixPackId</column-name><column-value><![CDATA[");
		sb.append(getPatcherFixPackId());
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
			"<column><column-name>patcherBuildId</column-name><column-value><![CDATA[");
		sb.append(getPatcherBuildId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherFixComponentId</column-name><column-value><![CDATA[");
		sb.append(getPatcherFixComponentId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>patcherProjectVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProjectVersionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>name</column-name><column-value><![CDATA[");
		sb.append(getName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>version</column-name><column-value><![CDATA[");
		sb.append(getVersion());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>releasedDate</column-name><column-value><![CDATA[");
		sb.append(getReleasedDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>requirements</column-name><column-value><![CDATA[");
		sb.append(getRequirements());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>status</column-name><column-value><![CDATA[");
		sb.append(getStatus());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherFixPackId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
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
	private BaseModel<?> _patcherFixPackRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}