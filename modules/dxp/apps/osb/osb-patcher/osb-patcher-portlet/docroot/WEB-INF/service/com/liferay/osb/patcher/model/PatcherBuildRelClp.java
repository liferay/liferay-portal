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
import com.liferay.osb.patcher.service.PatcherBuildRelLocalServiceUtil;

import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.model.impl.BaseModelImpl;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Calvin Keum
 */
public class PatcherBuildRelClp extends BaseModelImpl<PatcherBuildRel>
	implements PatcherBuildRel {
	public PatcherBuildRelClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherBuildRel.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherBuildRel.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherBuildRelId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherBuildRelId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherBuildRelId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherBuildRelId", getPatcherBuildRelId());
		attributes.put("childPatcherBuildId", getChildPatcherBuildId());
		attributes.put("parentPatcherBuildId", getParentPatcherBuildId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherBuildRelId = (Long)attributes.get("patcherBuildRelId");

		if (patcherBuildRelId != null) {
			setPatcherBuildRelId(patcherBuildRelId);
		}

		Long childPatcherBuildId = (Long)attributes.get("childPatcherBuildId");

		if (childPatcherBuildId != null) {
			setChildPatcherBuildId(childPatcherBuildId);
		}

		Long parentPatcherBuildId = (Long)attributes.get("parentPatcherBuildId");

		if (parentPatcherBuildId != null) {
			setParentPatcherBuildId(parentPatcherBuildId);
		}
	}

	@Override
	public long getPatcherBuildRelId() {
		return _patcherBuildRelId;
	}

	@Override
	public void setPatcherBuildRelId(long patcherBuildRelId) {
		_patcherBuildRelId = patcherBuildRelId;

		if (_patcherBuildRelRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRelRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherBuildRelId",
						long.class);

				method.invoke(_patcherBuildRelRemoteModel, patcherBuildRelId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getChildPatcherBuildId() {
		return _childPatcherBuildId;
	}

	@Override
	public void setChildPatcherBuildId(long childPatcherBuildId) {
		_childPatcherBuildId = childPatcherBuildId;

		if (_patcherBuildRelRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRelRemoteModel.getClass();

				Method method = clazz.getMethod("setChildPatcherBuildId",
						long.class);

				method.invoke(_patcherBuildRelRemoteModel, childPatcherBuildId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getParentPatcherBuildId() {
		return _parentPatcherBuildId;
	}

	@Override
	public void setParentPatcherBuildId(long parentPatcherBuildId) {
		_parentPatcherBuildId = parentPatcherBuildId;

		if (_patcherBuildRelRemoteModel != null) {
			try {
				Class<?> clazz = _patcherBuildRelRemoteModel.getClass();

				Method method = clazz.getMethod("setParentPatcherBuildId",
						long.class);

				method.invoke(_patcherBuildRelRemoteModel, parentPatcherBuildId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	public BaseModel<?> getPatcherBuildRelRemoteModel() {
		return _patcherBuildRelRemoteModel;
	}

	public void setPatcherBuildRelRemoteModel(
		BaseModel<?> patcherBuildRelRemoteModel) {
		_patcherBuildRelRemoteModel = patcherBuildRelRemoteModel;
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

		Class<?> remoteModelClass = _patcherBuildRelRemoteModel.getClass();

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

		Object returnValue = method.invoke(_patcherBuildRelRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherBuildRelLocalServiceUtil.addPatcherBuildRel(this);
		}
		else {
			PatcherBuildRelLocalServiceUtil.updatePatcherBuildRel(this);
		}
	}

	@Override
	public PatcherBuildRel toEscapedModel() {
		return (PatcherBuildRel)ProxyUtil.newProxyInstance(PatcherBuildRel.class.getClassLoader(),
			new Class[] { PatcherBuildRel.class },
			new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherBuildRelClp clone = new PatcherBuildRelClp();

		clone.setPatcherBuildRelId(getPatcherBuildRelId());
		clone.setChildPatcherBuildId(getChildPatcherBuildId());
		clone.setParentPatcherBuildId(getParentPatcherBuildId());

		return clone;
	}

	@Override
	public int compareTo(PatcherBuildRel patcherBuildRel) {
		long primaryKey = patcherBuildRel.getPrimaryKey();

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

		if (!(obj instanceof PatcherBuildRelClp)) {
			return false;
		}

		PatcherBuildRelClp patcherBuildRel = (PatcherBuildRelClp)obj;

		long primaryKey = patcherBuildRel.getPrimaryKey();

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
		StringBundler sb = new StringBundler(7);

		sb.append("{patcherBuildRelId=");
		sb.append(getPatcherBuildRelId());
		sb.append(", childPatcherBuildId=");
		sb.append(getChildPatcherBuildId());
		sb.append(", parentPatcherBuildId=");
		sb.append(getParentPatcherBuildId());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(13);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherBuildRel");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherBuildRelId</column-name><column-value><![CDATA[");
		sb.append(getPatcherBuildRelId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>childPatcherBuildId</column-name><column-value><![CDATA[");
		sb.append(getChildPatcherBuildId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>parentPatcherBuildId</column-name><column-value><![CDATA[");
		sb.append(getParentPatcherBuildId());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherBuildRelId;
	private long _childPatcherBuildId;
	private long _parentPatcherBuildId;
	private BaseModel<?> _patcherBuildRelRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}