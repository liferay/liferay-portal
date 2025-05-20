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
public class PatcherFixRelSoap implements Serializable {
	public static PatcherFixRelSoap toSoapModel(PatcherFixRel model) {
		PatcherFixRelSoap soapModel = new PatcherFixRelSoap();

		soapModel.setPatcherFixRelId(model.getPatcherFixRelId());
		soapModel.setChildPatcherFixId(model.getChildPatcherFixId());
		soapModel.setParentPatcherFixId(model.getParentPatcherFixId());

		return soapModel;
	}

	public static PatcherFixRelSoap[] toSoapModels(PatcherFixRel[] models) {
		PatcherFixRelSoap[] soapModels = new PatcherFixRelSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixRelSoap[][] toSoapModels(PatcherFixRel[][] models) {
		PatcherFixRelSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new PatcherFixRelSoap[models.length][models[0].length];
		}
		else {
			soapModels = new PatcherFixRelSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static PatcherFixRelSoap[] toSoapModels(List<PatcherFixRel> models) {
		List<PatcherFixRelSoap> soapModels = new ArrayList<PatcherFixRelSoap>(models.size());

		for (PatcherFixRel model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new PatcherFixRelSoap[soapModels.size()]);
	}

	public PatcherFixRelSoap() {
	}

	public long getPrimaryKey() {
		return _patcherFixRelId;
	}

	public void setPrimaryKey(long pk) {
		setPatcherFixRelId(pk);
	}

	public long getPatcherFixRelId() {
		return _patcherFixRelId;
	}

	public void setPatcherFixRelId(long patcherFixRelId) {
		_patcherFixRelId = patcherFixRelId;
	}

	public long getChildPatcherFixId() {
		return _childPatcherFixId;
	}

	public void setChildPatcherFixId(long childPatcherFixId) {
		_childPatcherFixId = childPatcherFixId;
	}

	public long getParentPatcherFixId() {
		return _parentPatcherFixId;
	}

	public void setParentPatcherFixId(long parentPatcherFixId) {
		_parentPatcherFixId = parentPatcherFixId;
	}

	private long _patcherFixRelId;
	private long _childPatcherFixId;
	private long _parentPatcherFixId;
}