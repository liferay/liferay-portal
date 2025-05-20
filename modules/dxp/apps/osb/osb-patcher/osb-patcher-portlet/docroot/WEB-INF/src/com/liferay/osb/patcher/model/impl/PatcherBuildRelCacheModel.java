/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherBuildRel;

import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing PatcherBuildRel in entity cache.
 *
 * @author Calvin Keum
 * @see PatcherBuildRel
 * @generated
 */
public class PatcherBuildRelCacheModel implements CacheModel<PatcherBuildRel>,
	Externalizable {
	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{patcherBuildRelId=");
		sb.append(patcherBuildRelId);
		sb.append(", childPatcherBuildId=");
		sb.append(childPatcherBuildId);
		sb.append(", parentPatcherBuildId=");
		sb.append(parentPatcherBuildId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public PatcherBuildRel toEntityModel() {
		PatcherBuildRelImpl patcherBuildRelImpl = new PatcherBuildRelImpl();

		patcherBuildRelImpl.setPatcherBuildRelId(patcherBuildRelId);
		patcherBuildRelImpl.setChildPatcherBuildId(childPatcherBuildId);
		patcherBuildRelImpl.setParentPatcherBuildId(parentPatcherBuildId);

		patcherBuildRelImpl.resetOriginalValues();

		return patcherBuildRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		patcherBuildRelId = objectInput.readLong();
		childPatcherBuildId = objectInput.readLong();
		parentPatcherBuildId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput)
		throws IOException {
		objectOutput.writeLong(patcherBuildRelId);
		objectOutput.writeLong(childPatcherBuildId);
		objectOutput.writeLong(parentPatcherBuildId);
	}

	public long patcherBuildRelId;
	public long childPatcherBuildId;
	public long parentPatcherBuildId;
}