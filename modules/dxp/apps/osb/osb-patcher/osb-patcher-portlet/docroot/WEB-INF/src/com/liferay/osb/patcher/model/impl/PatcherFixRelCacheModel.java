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

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFixRel;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing PatcherFixRel in entity cache.
 *
 * @author Calvin Keum
 * @see PatcherFixRel
 * @generated
 */
public class PatcherFixRelCacheModel implements CacheModel<PatcherFixRel>,
	Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{patcherFixRelId=");
		sb.append(patcherFixRelId);
		sb.append(", childPatcherFixId=");
		sb.append(childPatcherFixId);
		sb.append(", parentPatcherFixId=");
		sb.append(parentPatcherFixId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherFixRel toEntityModel() {
		PatcherFixRelImpl patcherFixRelImpl = new PatcherFixRelImpl();

		patcherFixRelImpl.setPatcherFixRelId(patcherFixRelId);
		patcherFixRelImpl.setChildPatcherFixId(childPatcherFixId);
		patcherFixRelImpl.setParentPatcherFixId(parentPatcherFixId);

		patcherFixRelImpl.resetOriginalValues();

		return patcherFixRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		patcherFixRelId = objectInput.readLong();
		childPatcherFixId = objectInput.readLong();
		parentPatcherFixId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(patcherFixRelId);
		objectOutput.writeLong(childPatcherFixId);
		objectOutput.writeLong(parentPatcherFixId);
	}

	public long patcherFixRelId;
	public long childPatcherFixId;
	public long parentPatcherFixId;
}