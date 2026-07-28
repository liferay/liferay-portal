/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.CompoundPKEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.CompoundPKEntryPK;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing CompoundPKEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CompoundPKEntryCacheModel
	implements CacheModel<CompoundPKEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CompoundPKEntryCacheModel)) {
			return false;
		}

		CompoundPKEntryCacheModel compoundPKEntryCacheModel =
			(CompoundPKEntryCacheModel)object;

		if (compoundPKEntryPK.equals(
				compoundPKEntryCacheModel.compoundPKEntryPK)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, compoundPKEntryPK);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{companyId=");
		sb.append(companyId);
		sb.append(", classNameId=");
		sb.append(classNameId);
		sb.append(", name=");
		sb.append(name);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CompoundPKEntry toEntityModel() {
		CompoundPKEntryImpl compoundPKEntryImpl = new CompoundPKEntryImpl();

		compoundPKEntryImpl.setCompanyId(companyId);
		compoundPKEntryImpl.setClassNameId(classNameId);

		if (name == null) {
			compoundPKEntryImpl.setName("");
		}
		else {
			compoundPKEntryImpl.setName(name);
		}

		compoundPKEntryImpl.resetOriginalValues();

		return compoundPKEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		companyId = objectInput.readLong();

		classNameId = objectInput.readLong();
		name = objectInput.readUTF();

		compoundPKEntryPK = new CompoundPKEntryPK(companyId, classNameId);
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(companyId);

		objectOutput.writeLong(classNameId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}
	}

	public long companyId;
	public long classNameId;
	public String name;
	public transient CompoundPKEntryPK compoundPKEntryPK;

}
// LIFERAY-SERVICE-BUILDER-HASH:997900118