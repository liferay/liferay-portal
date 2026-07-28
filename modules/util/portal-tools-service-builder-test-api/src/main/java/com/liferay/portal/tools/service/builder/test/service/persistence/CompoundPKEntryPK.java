/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CompoundPKEntryPK
	implements Comparable<CompoundPKEntryPK>, Serializable {

	public long companyId;
	public long classNameId;

	public CompoundPKEntryPK() {
	}

	public CompoundPKEntryPK(long companyId, long classNameId) {
		this.companyId = companyId;
		this.classNameId = classNameId;
	}

	public long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(long companyId) {
		this.companyId = companyId;
	}

	public long getClassNameId() {
		return classNameId;
	}

	public void setClassNameId(long classNameId) {
		this.classNameId = classNameId;
	}

	@Override
	public int compareTo(CompoundPKEntryPK pk) {
		if (pk == null) {
			return -1;
		}

		int value = 0;

		if (companyId < pk.companyId) {
			value = -1;
		}
		else if (companyId > pk.companyId) {
			value = 1;
		}
		else {
			value = 0;
		}

		if (value != 0) {
			return value;
		}

		if (classNameId < pk.classNameId) {
			value = -1;
		}
		else if (classNameId > pk.classNameId) {
			value = 1;
		}
		else {
			value = 0;
		}

		if (value != 0) {
			return value;
		}

		return 0;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CompoundPKEntryPK)) {
			return false;
		}

		CompoundPKEntryPK pk = (CompoundPKEntryPK)object;

		if ((companyId == pk.companyId) && (classNameId == pk.classNameId)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hashCode = 0;

		hashCode = HashUtil.hash(hashCode, companyId);
		hashCode = HashUtil.hash(hashCode, classNameId);

		return hashCode;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(6);

		sb.append("{");

		sb.append("companyId=");

		sb.append(companyId);
		sb.append(", classNameId=");

		sb.append(classNameId);

		sb.append("}");

		return sb.toString();
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-537360493