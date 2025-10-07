/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model.impl;

import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing CTSGrandParent in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CTSGrandParentCacheModel
	implements CacheModel<CTSGrandParent>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CTSGrandParentCacheModel)) {
			return false;
		}

		CTSGrandParentCacheModel ctsGrandParentCacheModel =
			(CTSGrandParentCacheModel)object;

		if ((ctsGrandParentId == ctsGrandParentCacheModel.ctsGrandParentId) &&
			(mvccVersion == ctsGrandParentCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, ctsGrandParentId);

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
		sb.append(", ctsGrandParentId=");
		sb.append(ctsGrandParentId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", parentCTSGrandParentId=");
		sb.append(parentCTSGrandParentId);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CTSGrandParent toEntityModel() {
		CTSGrandParentImpl ctsGrandParentImpl = new CTSGrandParentImpl();

		ctsGrandParentImpl.setMvccVersion(mvccVersion);
		ctsGrandParentImpl.setCtsGrandParentId(ctsGrandParentId);
		ctsGrandParentImpl.setCompanyId(companyId);
		ctsGrandParentImpl.setParentCTSGrandParentId(parentCTSGrandParentId);

		if (name == null) {
			ctsGrandParentImpl.setName("");
		}
		else {
			ctsGrandParentImpl.setName(name);
		}

		ctsGrandParentImpl.resetOriginalValues();

		return ctsGrandParentImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctsGrandParentId = objectInput.readLong();

		companyId = objectInput.readLong();

		parentCTSGrandParentId = objectInput.readLong();
		name = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctsGrandParentId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(parentCTSGrandParentId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public long mvccVersion;
	public long ctsGrandParentId;
	public long companyId;
	public long parentCTSGrandParentId;
	public String name;

}