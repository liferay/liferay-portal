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
import java.util.List;

/**
 * This class is used by SOAP remote services.
 *
 * @author Calvin Keum
 * @generated
 */
public class PatcherBuildRelSoap implements Serializable {
	public static PatcherBuildRelSoap toSoapModel(PatcherBuildRel model) {
		PatcherBuildRelSoap soapModel = new PatcherBuildRelSoap();

		soapModel.setPatcherBuildRelId(model.getPatcherBuildRelId());
		soapModel.setChildPatcherBuildId(model.getChildPatcherBuildId());
		soapModel.setParentPatcherBuildId(model.getParentPatcherBuildId());

		return soapModel;
	}

	public static PatcherBuildRelSoap[] toSoapModels(PatcherBuildRel[] models) {
		PatcherBuildRelSoap[] soapModels = new PatcherBuildRelSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherBuildRelSoap[][] toSoapModels(
		PatcherBuildRel[][] models) {
		PatcherBuildRelSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherBuildRelSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherBuildRelSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherBuildRelSoap[] toSoapModels(
		List<PatcherBuildRel> models) {
		List<PatcherBuildRelSoap> soapModels = new ArrayList<PatcherBuildRelSoap>(models.size());

		for (PatcherBuildRel model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherBuildRelSoap[soapModels.size()]);
	}

	public PatcherBuildRelSoap() {
	}

	public long getPrimaryKey() {
		return _patcherBuildRelId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherBuildRelId(pk);
	}

	public long getPatcherBuildRelId() {
		return _patcherBuildRelId;
	}

	public void setPatcherBuildRelId(long patcherBuildRelId) {
		_patcherBuildRelId = patcherBuildRelId;
	}

	public long getChildPatcherBuildId() {
		return _childPatcherBuildId;
	}

	public void setChildPatcherBuildId(long childPatcherBuildId) {
		_childPatcherBuildId = childPatcherBuildId;
	}

	public long getParentPatcherBuildId() {
		return _parentPatcherBuildId;
	}

	public void setParentPatcherBuildId(long parentPatcherBuildId) {
		_parentPatcherBuildId = parentPatcherBuildId;
	}

	private long _patcherBuildRelId;
	private long _childPatcherBuildId;
	private long _parentPatcherBuildId;
}