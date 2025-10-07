/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model.impl;

import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing CTSParent in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CTSParentCacheModel
	implements CacheModel<CTSParent>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CTSParentCacheModel)) {
			return false;
		}

		CTSParentCacheModel ctsParentCacheModel = (CTSParentCacheModel)object;

		if ((ctsParentId == ctsParentCacheModel.ctsParentId) &&
			(mvccVersion == ctsParentCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, ctsParentId);

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
		StringBundler sb = new StringBundler(13);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", ctsParentId=");
		sb.append(ctsParentId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", ctsGrandParentId=");
		sb.append(ctsGrandParentId);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CTSParent toEntityModel() {
		CTSParentImpl ctsParentImpl = new CTSParentImpl();

		ctsParentImpl.setMvccVersion(mvccVersion);
		ctsParentImpl.setCtCollectionId(ctCollectionId);
		ctsParentImpl.setCtsParentId(ctsParentId);
		ctsParentImpl.setCompanyId(companyId);
		ctsParentImpl.setCtsGrandParentId(ctsGrandParentId);

		if (name == null) {
			ctsParentImpl.setName("");
		}
		else {
			ctsParentImpl.setName(name);
		}

		ctsParentImpl.resetOriginalValues();

		return ctsParentImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();

		ctsParentId = objectInput.readLong();

		companyId = objectInput.readLong();

		ctsGrandParentId = objectInput.readLong();
		name = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(ctsParentId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(ctsGrandParentId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public long mvccVersion;
	public long ctCollectionId;
	public long ctsParentId;
	public long companyId;
	public long ctsGrandParentId;
	public String name;

}