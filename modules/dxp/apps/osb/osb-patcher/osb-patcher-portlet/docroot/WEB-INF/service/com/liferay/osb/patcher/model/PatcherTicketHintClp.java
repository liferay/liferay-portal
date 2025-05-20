/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.osb.patcher.service.ClpSerializer;
import com.liferay.osb.patcher.service.PatcherTicketHintLocalServiceUtil;

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
public class PatcherTicketHintClp extends BaseModelImpl<PatcherTicketHint>
	implements PatcherTicketHint {
	public PatcherTicketHintClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherTicketHint.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherTicketHint.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherTicketHintId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherTicketHintId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherTicketHintId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherTicketHintId", getPatcherTicketHintId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("patcherProductVersionId", getPatcherProductVersionId());
		attributes.put("script", getScript());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherTicketHintId = (Long)attributes.get("patcherTicketHintId");

		if (patcherTicketHintId != null) {
			setPatcherTicketHintId(patcherTicketHintId);
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

		String script = (String)attributes.get("script");

		if (script != null) {
			setScript(script);
		}
	}

	@Override
	public long getPatcherTicketHintId() {
		return _patcherTicketHintId;
	}

	@Override
	public void setPatcherTicketHintId(long patcherTicketHintId) {
		_patcherTicketHintId = patcherTicketHintId;

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherTicketHintId",
						long.class);

				method.invoke(_patcherTicketHintRemoteModel, patcherTicketHintId);
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

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setCompanyId", long.class);

				method.invoke(_patcherTicketHintRemoteModel, companyId);
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

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setUserId", long.class);

				method.invoke(_patcherTicketHintRemoteModel, userId);
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

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setUserName", String.class);

				method.invoke(_patcherTicketHintRemoteModel, userName);
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

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setCreateDate", Date.class);

				method.invoke(_patcherTicketHintRemoteModel, createDate);
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

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setModifiedDate", Date.class);

				method.invoke(_patcherTicketHintRemoteModel, modifiedDate);
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

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherProductVersionId",
						long.class);

				method.invoke(_patcherTicketHintRemoteModel,
					patcherProductVersionId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public String getScript() {
		return _script;
	}

	@Override
	public void setScript(String script) {
		_script = script;

		if (_patcherTicketHintRemoteModel != null) {
			try {
				Class<?> clazz = _patcherTicketHintRemoteModel.getClass();

				Method method = clazz.getMethod("setScript", String.class);

				method.invoke(_patcherTicketHintRemoteModel, script);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	public BaseModel<?> getPatcherTicketHintRemoteModel() {
		return _patcherTicketHintRemoteModel;
	}

	public void setPatcherTicketHintRemoteModel(
		BaseModel<?> patcherTicketHintRemoteModel) {
		_patcherTicketHintRemoteModel = patcherTicketHintRemoteModel;
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

		Class<?> remoteModelClass = _patcherTicketHintRemoteModel.getClass();

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

		Object returnValue = method.invoke(_patcherTicketHintRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherTicketHintLocalServiceUtil.addPatcherTicketHint(this);
		}
		else {
			PatcherTicketHintLocalServiceUtil.updatePatcherTicketHint(this);
		}
	}

	@Override
	public PatcherTicketHint toEscapedModel() {
		return (PatcherTicketHint)ProxyUtil.newProxyInstance(PatcherTicketHint.class.getClassLoader(),
			new Class[] { PatcherTicketHint.class },
			new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherTicketHintClp clone = new PatcherTicketHintClp();

		clone.setPatcherTicketHintId(getPatcherTicketHintId());
		clone.setCompanyId(getCompanyId());
		clone.setUserId(getUserId());
		clone.setUserName(getUserName());
		clone.setCreateDate(getCreateDate());
		clone.setModifiedDate(getModifiedDate());
		clone.setPatcherProductVersionId(getPatcherProductVersionId());
		clone.setScript(getScript());

		return clone;
	}

	@Override
	public int compareTo(PatcherTicketHint patcherTicketHint) {
		long primaryKey = patcherTicketHint.getPrimaryKey();

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

		if (!(obj instanceof PatcherTicketHintClp)) {
			return false;
		}

		PatcherTicketHintClp patcherTicketHint = (PatcherTicketHintClp)obj;

		long primaryKey = patcherTicketHint.getPrimaryKey();

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

		sb.append("{patcherTicketHintId=");
		sb.append(getPatcherTicketHintId());
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
		sb.append(", script=");
		sb.append(getScript());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(28);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherTicketHint");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherTicketHintId</column-name><column-value><![CDATA[");
		sb.append(getPatcherTicketHintId());
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
			"<column><column-name>script</column-name><column-value><![CDATA[");
		sb.append(getScript());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherTicketHintId;
	private long _companyId;
	private long _userId;
	private String _userUuid;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private long _patcherProductVersionId;
	private String _script;
	private BaseModel<?> _patcherTicketHintRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}