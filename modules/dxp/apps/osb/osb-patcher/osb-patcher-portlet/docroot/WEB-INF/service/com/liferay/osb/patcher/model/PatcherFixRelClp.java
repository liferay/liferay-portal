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
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;

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
public class PatcherFixRelClp extends BaseModelImpl<PatcherFixRel>
	implements PatcherFixRel {
	public PatcherFixRelClp() {
	}

	@Override
	public Class<?> getModelClass() {
		return PatcherFixRel.class;
	}

	@Override
	public String getModelClassName() {
		return PatcherFixRel.class.getName();
	}

	@Override
	public long getPrimaryKey() {
		return _patcherFixRelId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setPatcherFixRelId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _patcherFixRelId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("patcherFixRelId", getPatcherFixRelId());
		attributes.put("childPatcherFixId", getChildPatcherFixId());
		attributes.put("parentPatcherFixId", getParentPatcherFixId());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		Long patcherFixRelId = (Long)attributes.get("patcherFixRelId");

		if (patcherFixRelId != null) {
			setPatcherFixRelId(patcherFixRelId);
		}

		Long childPatcherFixId = (Long)attributes.get("childPatcherFixId");

		if (childPatcherFixId != null) {
			setChildPatcherFixId(childPatcherFixId);
		}

		Long parentPatcherFixId = (Long)attributes.get("parentPatcherFixId");

		if (parentPatcherFixId != null) {
			setParentPatcherFixId(parentPatcherFixId);
		}
	}

	@Override
	public long getPatcherFixRelId() {
		return _patcherFixRelId;
	}

	@Override
	public void setPatcherFixRelId(long patcherFixRelId) {
		_patcherFixRelId = patcherFixRelId;

		if (_patcherFixRelRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRelRemoteModel.getClass();

				Method method = clazz.getMethod("setPatcherFixRelId", long.class);

				method.invoke(_patcherFixRelRemoteModel, patcherFixRelId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getChildPatcherFixId() {
		return _childPatcherFixId;
	}

	@Override
	public void setChildPatcherFixId(long childPatcherFixId) {
		_childPatcherFixId = childPatcherFixId;

		if (_patcherFixRelRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRelRemoteModel.getClass();

				Method method = clazz.getMethod("setChildPatcherFixId",
						long.class);

				method.invoke(_patcherFixRelRemoteModel, childPatcherFixId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	@Override
	public long getParentPatcherFixId() {
		return _parentPatcherFixId;
	}

	@Override
	public void setParentPatcherFixId(long parentPatcherFixId) {
		_parentPatcherFixId = parentPatcherFixId;

		if (_patcherFixRelRemoteModel != null) {
			try {
				Class<?> clazz = _patcherFixRelRemoteModel.getClass();

				Method method = clazz.getMethod("setParentPatcherFixId",
						long.class);

				method.invoke(_patcherFixRelRemoteModel, parentPatcherFixId);
			}
			catch (Exception e) {
				throw new UnsupportedOperationException(e);
			}
		}
	}

	public BaseModel<?> getPatcherFixRelRemoteModel() {
		return _patcherFixRelRemoteModel;
	}

	public void setPatcherFixRelRemoteModel(
		BaseModel<?> patcherFixRelRemoteModel) {
		_patcherFixRelRemoteModel = patcherFixRelRemoteModel;
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

		Class<?> remoteModelClass = _patcherFixRelRemoteModel.getClass();

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

		Object returnValue = method.invoke(_patcherFixRelRemoteModel,
				remoteParameterValues);

		if (returnValue != null) {
			returnValue = ClpSerializer.translateOutput(returnValue);
		}

		return returnValue;
	}

	@Override
	public void persist() throws SystemException {
		if (this.isNew()) {
			PatcherFixRelLocalServiceUtil.addPatcherFixRel(this);
		}
		else {
			PatcherFixRelLocalServiceUtil.updatePatcherFixRel(this);
		}
	}

	@Override
	public PatcherFixRel toEscapedModel() {
		return (PatcherFixRel)ProxyUtil.newProxyInstance(PatcherFixRel.class.getClassLoader(),
			new Class[] { PatcherFixRel.class }, new AutoEscapeBeanHandler(this));
	}

	@Override
	public Object clone() {
		PatcherFixRelClp clone = new PatcherFixRelClp();

		clone.setPatcherFixRelId(getPatcherFixRelId());
		clone.setChildPatcherFixId(getChildPatcherFixId());
		clone.setParentPatcherFixId(getParentPatcherFixId());

		return clone;
	}

	@Override
	public int compareTo(PatcherFixRel patcherFixRel) {
		long primaryKey = patcherFixRel.getPrimaryKey();

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

		if (!(obj instanceof PatcherFixRelClp)) {
			return false;
		}

		PatcherFixRelClp patcherFixRel = (PatcherFixRelClp)obj;

		long primaryKey = patcherFixRel.getPrimaryKey();

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

		sb.append("{patcherFixRelId=");
		sb.append(getPatcherFixRelId());
		sb.append(", childPatcherFixId=");
		sb.append(getChildPatcherFixId());
		sb.append(", parentPatcherFixId=");
		sb.append(getParentPatcherFixId());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(13);

		sb.append("<model><model-name>");
		sb.append("com.liferay.osb.patcher.model.PatcherFixRel");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>patcherFixRelId</column-name><column-value><![CDATA[");
		sb.append(getPatcherFixRelId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>childPatcherFixId</column-name><column-value><![CDATA[");
		sb.append(getChildPatcherFixId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>parentPatcherFixId</column-name><column-value><![CDATA[");
		sb.append(getParentPatcherFixId());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private long _patcherFixRelId;
	private long _childPatcherFixId;
	private long _parentPatcherFixId;
	private BaseModel<?> _patcherFixRelRemoteModel;
	private Class<?> _clpSerializerClass = com.liferay.osb.patcher.service.ClpSerializer.class;
}