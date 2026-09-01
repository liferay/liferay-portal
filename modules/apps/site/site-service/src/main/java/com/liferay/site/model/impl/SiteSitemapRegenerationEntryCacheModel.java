/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.site.model.SiteSitemapRegenerationEntry;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing SiteSitemapRegenerationEntry in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class SiteSitemapRegenerationEntryCacheModel
	implements CacheModel<SiteSitemapRegenerationEntry>, Externalizable,
			   MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SiteSitemapRegenerationEntryCacheModel)) {
			return false;
		}

		SiteSitemapRegenerationEntryCacheModel
			siteSitemapRegenerationEntryCacheModel =
				(SiteSitemapRegenerationEntryCacheModel)object;

		if ((siteSitemapRegenerationEntryId ==
				siteSitemapRegenerationEntryCacheModel.
					siteSitemapRegenerationEntryId) &&
			(mvccVersion ==
				siteSitemapRegenerationEntryCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, siteSitemapRegenerationEntryId);

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
		sb.append(", siteSitemapRegenerationEntryId=");
		sb.append(siteSitemapRegenerationEntryId);
		sb.append(", groupId=");
		sb.append(groupId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", assetTypeKey=");
		sb.append(assetTypeKey);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public SiteSitemapRegenerationEntry toEntityModel() {
		SiteSitemapRegenerationEntryImpl siteSitemapRegenerationEntryImpl =
			new SiteSitemapRegenerationEntryImpl();

		siteSitemapRegenerationEntryImpl.setMvccVersion(mvccVersion);
		siteSitemapRegenerationEntryImpl.setSiteSitemapRegenerationEntryId(
			siteSitemapRegenerationEntryId);
		siteSitemapRegenerationEntryImpl.setGroupId(groupId);
		siteSitemapRegenerationEntryImpl.setCompanyId(companyId);

		if (assetTypeKey == null) {
			siteSitemapRegenerationEntryImpl.setAssetTypeKey("");
		}
		else {
			siteSitemapRegenerationEntryImpl.setAssetTypeKey(assetTypeKey);
		}

		siteSitemapRegenerationEntryImpl.resetOriginalValues();

		return siteSitemapRegenerationEntryImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		mvccVersion = objectInput.readLong();

		siteSitemapRegenerationEntryId = objectInput.readLong();

		groupId = objectInput.readLong();

		companyId = objectInput.readLong();
		assetTypeKey = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(siteSitemapRegenerationEntryId);

		objectOutput.writeLong(groupId);

		objectOutput.writeLong(companyId);

		if (assetTypeKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(assetTypeKey);
		}
	}

	public long mvccVersion;
	public long siteSitemapRegenerationEntryId;
	public long groupId;
	public long companyId;
	public String assetTypeKey;

}
// LIFERAY-SERVICE-BUILDER-HASH:-151138699