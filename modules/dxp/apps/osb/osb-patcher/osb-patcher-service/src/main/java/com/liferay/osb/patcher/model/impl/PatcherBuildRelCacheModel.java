/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

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
	implements CacheModel<PatcherBuildRel>, Externalizable, MVCCModel {

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

		if ((patcherBuildRelId ==
				patcherBuildRelCacheModel.patcherBuildRelId) &&
			(mvccVersion == patcherBuildRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherBuildRelId);

		return HashUtil.hash(hashCode, mvccVersion);
	}

	@Override
	public long getMvccVersion() {
		return mvccVersion;
	}

	@Override
	public void setMvccVersion(long mvccVersion) {
		this.mvccVersion = mvccVersion;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(11);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", patcherBuildRelId=");
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

		patcherBuildRelImpl.setMvccVersion(mvccVersion);
		patcherBuildRelImpl.setPatcherBuildRelId(patcherBuildRelId);
		patcherBuildRelImpl.setCompanyId(companyId);
		patcherBuildRelImpl.setChildPatcherBuildId(childPatcherBuildId);
		patcherBuildRelImpl.setParentPatcherBuildId(parentPatcherBuildId);

		patcherBuildRelImpl.resetOriginalValues();

		return patcherBuildRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		patcherBuildRelId = objectInput.readLong();

		companyId = objectInput.readLong();

		childPatcherBuildId = objectInput.readLong();

		parentPatcherBuildId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(patcherBuildRelId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(childPatcherBuildId);

		objectOutput.writeLong(parentPatcherBuildId);
	}

	public long mvccVersion;
	public long patcherBuildRelId;
	public long companyId;
	public long childPatcherBuildId;
	public long parentPatcherBuildId;

}