/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing PatcherBuildRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherBuildRelCacheModel
	implements CacheModel<PatcherBuildRel>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherBuildRelCacheModel)) {
			return false;
		}

		PatcherBuildRelCacheModel patcherBuildRelCacheModel =
			(PatcherBuildRelCacheModel)object;

		if (patcherBuildRelId == patcherBuildRelCacheModel.patcherBuildRelId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, patcherBuildRelId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(9);

		sb.append("{patcherBuildRelId=");
		sb.append(patcherBuildRelId);
		sb.append(", companyId=");
		sb.append(companyId);
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
		patcherBuildRelImpl.setCompanyId(companyId);
		patcherBuildRelImpl.setChildPatcherBuildId(childPatcherBuildId);
		patcherBuildRelImpl.setParentPatcherBuildId(parentPatcherBuildId);

		patcherBuildRelImpl.resetOriginalValues();

		return patcherBuildRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		patcherBuildRelId = objectInput.readLong();

		companyId = objectInput.readLong();

		childPatcherBuildId = objectInput.readLong();

		parentPatcherBuildId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(patcherBuildRelId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(childPatcherBuildId);

		objectOutput.writeLong(parentPatcherBuildId);
	}

	public long patcherBuildRelId;
	public long companyId;
	public long childPatcherBuildId;
	public long parentPatcherBuildId;

}