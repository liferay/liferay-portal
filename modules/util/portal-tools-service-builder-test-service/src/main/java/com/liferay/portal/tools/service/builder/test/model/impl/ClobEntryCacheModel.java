/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.ClobEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing ClobEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ClobEntryCacheModel
	implements CacheModel<ClobEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ClobEntryCacheModel)) {
			return false;
		}

		ClobEntryCacheModel clobEntryCacheModel = (ClobEntryCacheModel)object;

		if (clobEntryId == clobEntryCacheModel.clobEntryId) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, clobEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(5);

		sb.append("{clobEntryId=");
		sb.append(clobEntryId);
		sb.append(", content=");
		sb.append(content);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ClobEntry toEntityModel() {
		ClobEntryImpl clobEntryImpl = new ClobEntryImpl();

		clobEntryImpl.setClobEntryId(clobEntryId);

		if (content == null) {
			clobEntryImpl.setContent("");
		}
		else {
			clobEntryImpl.setContent(content);
		}

		clobEntryImpl.resetOriginalValues();

		return clobEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		clobEntryId = objectInput.readLong();
		content = (String)objectInput.readObject();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(clobEntryId);

		if (content == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(content);
		}
	}

	public long clobEntryId;
	public String content;

}
// LIFERAY-SERVICE-BUILDER-HASH:-709882502