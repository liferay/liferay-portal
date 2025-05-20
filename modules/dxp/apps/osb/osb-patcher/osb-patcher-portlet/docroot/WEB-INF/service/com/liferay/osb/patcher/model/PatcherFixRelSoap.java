/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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