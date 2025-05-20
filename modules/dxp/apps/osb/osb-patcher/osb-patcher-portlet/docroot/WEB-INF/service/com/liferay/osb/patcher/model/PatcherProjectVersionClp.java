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
import com.liferay.osb.patcher.service.PatcherProjectVersionLocalServiceUtil;

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
public class PatcherProjectVersionClp extends BaseModelImpl<PatcherProjectVersion>
	implements PatcherProjectVersion {
	public PatcherProjectVersionClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherProjectVersion.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherProjectVersion.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherProjectVersionId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherProjectVersionId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherProjectVersionId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherProjectVersionId", getPatcherProjectVersionId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("rootPatcherProjectVersionId",
			getRootPatcherProjectVersionId());
		attributes.put("name", getName());
		attributes.put("combinedBranch", getCombinedBranch());
		attributes.put("hide", getHide());
		attributes.put("committish", getCommittish());
		attributes.put("repositoryName", getRepositoryName());
		attributes.put("fixedIssues", getFixedIssues());
		attributes.put("productVersion", getProductVersion());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherProjectVersionId = (Long)attributes.get(
				"patcherProjectVersionId");

		if (patcherProjectVersionId != null) {
			setPatcherProjectVersionId(patcherProjectVersionId);
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

		Long patcherProductVersionId = (Long)attributes.get(
				"patcherProductVersionId");

		if (patcherProductVersionId != null) {
			setPatcherProductVersionId(patcherProductVersionId);
		}

		Long rootPatcherProjectVersionId = (Long)attributes.get(
				"rootPatcherProjectVersionId");

		if (rootPatcherProjectVersionId != null) {
			setRootPatcherProjectVersionId(rootPatcherProjectVersionId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		Boolean combinedBranch = (Boolean)attributes.get("combinedBranch");

		if (combinedBranch != null) {
			setCombinedBranch(combinedBranch);
		}

		Boolean hide = (Boolean)attributes.get("hide");

		if (hide != null) {
			setHide(hide);
		}

		String committish = (String)attributes.get("committish");

		if (committish != null) {
			setCommittish(committish);
		}

		String repositoryName = (String)attributes.get("repositoryName");

		if (repositoryName != null) {
			setRepositoryName(repositoryName);
		}

		String fixedIssues = (String)attributes.get("fixedIssues");

		if (fixedIssues != null) {
			setFixedIssues(fixedIssues);
		}

		Integer productVersion = (Integer)attributes.get("productVersion");

		if (productVersion != null) {
			setProductVersion(productVersion);
		}
	}

	@Override
	public long getPatcherProjectVersionId() {
		return _patcherProjectVersionId;
	}

	@Override
	public void setPatcherProjectVersionId(long patcherProjectVersionId) {
		_patcherProjectVersionId = patcherProjectVersionId;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProjectVersionId",
						long.class);

				method.invoke(_patcherProjectVersionRemoteModel,
					patcherProjectVersionId);
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

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setCompanyId", long.class);

				method.invoke(_patcherProjectVersionRemoteModel, companyId);
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

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setUserId", long.class);

				method.invoke(_patcherProjectVersionRemoteModel, userId);
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

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setUserName", String.class);

				method.invoke(_patcherProjectVersionRemoteModel, userName);
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

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setCreateDate", Date.class);

				method.invoke(_patcherProjectVersionRemoteModel, createDate);
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

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setModifiedDate", Date.class);

				method.invoke(_patcherProjectVersionRemoteModel, modifiedDate);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getPatcherProductVersionId() {
		return _patcherProductVersionId;
	}

	@Override
	public void setPatcherProductVersionId(long patcherProductVersionId) {
		_patcherProductVersionId = patcherProductVersionId;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProductVersionId",
						long.class);

				method.invoke(_patcherProjectVersionRemoteModel,
					patcherProductVersionId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getRootPatcherProjectVersionId() {
		return _rootPatcherProjectVersionId;
	}

	@Override
	public void setRootPatcherProjectVersionId(long rootPatcherProjectVersionId) {
		_rootPatcherProjectVersionId = rootPatcherProjectVersionId;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setRootPatcherProjectVersionId",
						long.class);

				method.invoke(_patcherProjectVersionRemoteModel,
					rootPatcherProjectVersionId);
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

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setName", String.class);

				method.invoke(_patcherProjectVersionRemoteModel, name);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getCombinedBranch() {
		return _combinedBranch;
	}

	@Override
	public boolean isCombinedBranch() {
		return _combinedBranch;
	}

	@Override
	public void setCombinedBranch(boolean combinedBranch) {
		_combinedBranch = combinedBranch;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setCombinedBranch",
						boolean.class);

				method.invoke(_patcherProjectVersionRemoteModel, combinedBranch);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public boolean getHide() {
		return _hide;
	}

	@Override
	public boolean isHide() {
		return _hide;
	}

	@Override
	public void setHide(boolean hide) {
		_hide = hide;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setHide", boolean.class);

				method.invoke(_patcherProjectVersionRemoteModel, hide);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getCommittish() {
		return _committish;
	}

	@Override
	public void setCommittish(String committish) {
		_committish = committish;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setCommittish", String.class);

				method.invoke(_patcherProjectVersionRemoteModel, committish);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getRepositoryName() {
		return _repositoryName;
	}

	@Override
	public void setRepositoryName(String repositoryName) {
		_repositoryName = repositoryName;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setRepositoryName",
						String.class);

				method.invoke(_patcherProjectVersionRemoteModel, repositoryName);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getFixedIssues() {
		return _fixedIssues;
	}

	@Override
	public void setFixedIssues(String fixedIssues) {
		_fixedIssues = fixedIssues;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setFixedIssues", String.class);

				method.invoke(_patcherProjectVersionRemoteModel, fixedIssues);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public int getProductVersion() {
		return _productVersion;
	}

	@Override
	public void setProductVersion(int productVersion) {
		_productVersion = productVersion;

		if (_patcherProjectVersionRemoteModel != null) {
			try {
				Class<?> clazz = _patcherProjectVersionRemoteModel.getClass();

				Method method = clazz.getMethod("setProductVersion", int.class);

				method.invoke(_patcherProjectVersionRemoteModel, productVersion);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	public BaseModel<?> getPatcherProjectVersionRemoteModel() {
		return _patcherProjectVersionRemoteModel;
	}

	public void setPatcherProjectVersionRemoteModel(
		BaseModel<?> patcherProjectVersionRemoteModel) {
		_patcherProjectVersionRemoteModel = patcherProjectVersionRemoteModel;
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

		Class<?> remoteModelClass = _patcherProjectVersionRemoteModel.getClass();

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

		Object returnValue = method.invoke(_patcherProjectVersionRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherProjectVersionLocalServiceUtil.addPatcherProjectVersion(this);
		}
		else {
			PatcherProjectVersionLocalServiceUtil.updatePatcherProjectVersion(this);
		}
	}

	@Override
	public PatcherProjectVersion toEscapedModel() {
		return (PatcherProjectVersion)ProxyUtil.newProxyInstance(PatcherProjectVersion.class.getClassLoader(),
			new Class[] { PatcherProjectVersion.class },
			new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherProjectVersionClp clone = new PatcherProjectVersionClp();

		clone.setPatcherProjectVersionId(getPatcherProjectVersionId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setPatcherProductVersionId(getPatcherProductVersionId());
		clone.setRootPatcherProjectVersionId(getRootPatcherProjectVersionId());
		clone.setName(getName());
		clone.setCombinedBranch(getCombinedBranch());
		clone.setHide(getHide());
		clone.setCommittish(getCommittish());
		clone.setRepositoryName(getRepositoryName());
		clone.setFixedIssues(getFixedIssues());
		clone.setProductVersion(getProductVersion());

		return clone;
	}

	@Override
	public int compareTo(PatcherProjectVersion patcherProjectVersion) {
		long primaryKey = patcherProjectVersion.getPrimaryKey();

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

		if (!(obj instanceof PatcherProjectVersionClp)) {
			return false;
		}

		PatcherProjectVersionClp patcherProjectVersion = (PatcherProjectVersionClp)obj;

		long primaryKey = patcherProjectVersion.getPrimaryKey();

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
		StringBundler sb = new StringBundler(31);

		sb.append("{patcherProjectVersionId=");
		sb.append(getPatcherProjectVersionId());
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
		sb.append(", patcherProductVersionId=");
		sb.append(getPatcherProductVersionId());
		sb.append(", rootPatcherProjectVersionId=");
		sb.append(getRootPatcherProjectVersionId());
		sb.append(", name=");
		sb.append(getName());
		sb.append(", combinedBranch=");
		sb.append(getCombinedBranch());
		sb.append(", hide=");
		sb.append(getHide());
		sb.append(", committish=");
		sb.append(getCommittish());
		sb.append(", repositoryName=");
		sb.append(getRepositoryName());
		sb.append(", fixedIssues=");
		sb.append(getFixedIssues());
		sb.append(", productVersion=");
		sb.append(getProductVersion());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(49);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherProjectVersion");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherProjectVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProjectVersionId());
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
			"<column><column-name>patcherProductVersionId</column-name><column-value><![CDATA[");
		sb.append(getPatcherProductVersionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>rootPatcherProjectVersionId</column-name><column-value><![CDATA[");
		sb.append(getRootPatcherProjectVersionId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>name</column-name><column-value><![CDATA[");
		sb.append(getName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>combinedBranch</column-name><column-value><![CDATA[");
		sb.append(getCombinedBranch());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>hide</column-name><column-value><![CDATA[");
		sb.append(getHide());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>committish</column-name><column-value><![CDATA[");
		sb.append(getCommittish());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>repositoryName</column-name><column-value><![CDATA[");
		sb.append(getRepositoryName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>fixedIssues</column-name><column-value><![CDATA[");
		sb.append(getFixedIssues());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>productVersion</column-name><column-value><![CDATA[");
		sb.append(getProductVersion());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherProjectVersionId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
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
	private BaseModel<?> _patcherProjectVersionRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}