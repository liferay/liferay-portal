/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.storage.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.security.audit.storage.model.AuditEventAccountEntryRel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing AuditEventAccountEntryRel in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class AuditEventAccountEntryRelCacheModel
	implements CacheModel<AuditEventAccountEntryRel>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AuditEventAccountEntryRelCacheModel)) {
			return false;
		}

		AuditEventAccountEntryRelCacheModel
			auditEventAccountEntryRelCacheModel =
				(AuditEventAccountEntryRelCacheModel)object;

		if (auditEventAccountEntryRelId ==
				auditEventAccountEntryRelCacheModel.
					auditEventAccountEntryRelId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, auditEventAccountEntryRelId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(11);

		sb.append("{auditEventAccountEntryRelId=");
		sb.append(auditEventAccountEntryRelId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", accountEntryId=");
		sb.append(accountEntryId);
		sb.append(", auditEventId=");
		sb.append(auditEventId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public AuditEventAccountEntryRel toEntityModel() {
		AuditEventAccountEntryRelImpl auditEventAccountEntryRelImpl =
			new AuditEventAccountEntryRelImpl();

		auditEventAccountEntryRelImpl.setAuditEventAccountEntryRelId(
			auditEventAccountEntryRelId);
		auditEventAccountEntryRelImpl.setCompanyId(companyId);

		if (createDate == Long.MIN_VALUE) {
			auditEventAccountEntryRelImpl.setCreateDate(null);
		}
		else {
			auditEventAccountEntryRelImpl.setCreateDate(new Date(createDate));
		}

		auditEventAccountEntryRelImpl.setAccountEntryId(accountEntryId);
		auditEventAccountEntryRelImpl.setAuditEventId(auditEventId);

		auditEventAccountEntryRelImpl.resetOriginalValues();

		return auditEventAccountEntryRelImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		auditEventAccountEntryRelId = objectInput.readLong();

		companyId = objectInput.readLong();
		createDate = objectInput.readLong();

		accountEntryId = objectInput.readLong();

		auditEventId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(auditEventAccountEntryRelId);

		objectOutput.writeLong(companyId);
		objectOutput.writeLong(createDate);

		objectOutput.writeLong(accountEntryId);

		objectOutput.writeLong(auditEventId);
	}

	public long auditEventAccountEntryRelId;
	public long companyId;
	public long createDate;
	public long accountEntryId;
	public long auditEventId;

}
// LIFERAY-SERVICE-BUILDER-HASH:1399273896