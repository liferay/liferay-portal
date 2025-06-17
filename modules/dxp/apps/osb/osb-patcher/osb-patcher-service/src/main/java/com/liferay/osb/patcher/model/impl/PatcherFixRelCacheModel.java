/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model.impl;

import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing PatcherFixRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class PatcherFixRelCacheModel
	implements CacheModel<PatcherFixRel>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatcherFixRelCacheModel)) {
			return false;
		}

		PatcherFixRelCacheModel patcherFixRelCacheModel =
			(PatcherFixRelCacheModel)object;

		if ((patcherFixRelId == patcherFixRelCacheModel.patcherFixRelId) &&
			(mvccVersion == patcherFixRelCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, patcherFixRelId);

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
		sb.append(", patcherFixRelId=");
		sb.append(patcherFixRelId);
		sb.append(", companyId=");
		sb.append(companyId);
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

		patcherFixRelImpl.setMvccVersion(mvccVersion);
		patcherFixRelImpl.setPatcherFixRelId(patcherFixRelId);
		patcherFixRelImpl.setCompanyId(companyId);
		patcherFixRelImpl.setChildPatcherFixId(childPatcherFixId);
		patcherFixRelImpl.setParentPatcherFixId(parentPatcherFixId);

		patcherFixRelImpl.resetOriginalValues();

		return patcherFixRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		patcherFixRelId = objectInput.readLong();

		companyId = objectInput.readLong();

		childPatcherFixId = objectInput.readLong();

		parentPatcherFixId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(patcherFixRelId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(childPatcherFixId);

		objectOutput.writeLong(parentPatcherFixId);
	}

	public long mvccVersion;
	public long patcherFixRelId;
	public long companyId;
	public long childPatcherFixId;
	public long parentPatcherFixId;

}