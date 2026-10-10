/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing FilterPrimaryEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class FilterPrimaryEntryCacheModel
	implements CacheModel<FilterPrimaryEntry>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FilterPrimaryEntryCacheModel)) {
			return false;
		}

		FilterPrimaryEntryCacheModel filterPrimaryEntryCacheModel =
			(FilterPrimaryEntryCacheModel)object;

		if (filterPrimaryEntryId ==
				filterPrimaryEntryCacheModel.filterPrimaryEntryId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, filterPrimaryEntryId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(11);

		sb.append("{filterPrimaryEntryId=");
		sb.append(filterPrimaryEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", resourcePrimKey=");
		sb.append(resourcePrimKey);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public FilterPrimaryEntry toEntityModel() {
		FilterPrimaryEntryImpl filterPrimaryEntryImpl =
			new FilterPrimaryEntryImpl();

		filterPrimaryEntryImpl.setFilterPrimaryEntryId(filterPrimaryEntryId);
		filterPrimaryEntryImpl.setGroupId(groupId);
		filterPrimaryEntryImpl.setCompanyId(companyId);
		filterPrimaryEntryImpl.setUserId(userId);
		filterPrimaryEntryImpl.setResourcePrimKey(resourcePrimKey);

		filterPrimaryEntryImpl.resetOriginalValues();

		return filterPrimaryEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		filterPrimaryEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();

		resourcePrimKey = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(filterPrimaryEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		objectOutput.writeLong(resourcePrimKey);
	}

	public long filterPrimaryEntryId;
	public long groupId;
	public long companyId;
	public long userId;
	public long resourcePrimKey;

}
// LIFERAY-SERVICE-BUILDER-HASH:1420443403