/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing CompanyInfo in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class CompanyInfoCacheModel
	implements CacheModel<CompanyInfo>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CompanyInfoCacheModel)) {
			return false;
		}

		CompanyInfoCacheModel companyInfoCacheModel =
			(CompanyInfoCacheModel)object;

		if ((companyInfoId == companyInfoCacheModel.companyInfoId) &&
			(mvccVersion == companyInfoCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, companyInfoId);

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
		StringBundler sb = new StringBundler(35);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", companyInfoId=");
		sb.append(companyInfoId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", homeURL=");
		sb.append(homeURL);
		sb.append(", indexNameCurrent=");
		sb.append(indexNameCurrent);
		sb.append(", indexNameNext=");
		sb.append(indexNameNext);
		sb.append(", industry=");
		sb.append(industry);
		sb.append(", key=");
		sb.append(key);
		sb.append(", legalId=");
		sb.append(legalId);
		sb.append(", legalName=");
		sb.append(legalName);
		sb.append(", legalType=");
		sb.append(legalType);
		sb.append(", logoId=");
		sb.append(logoId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", sicCode=");
		sb.append(sicCode);
		sb.append(", size=");
		sb.append(size);
		sb.append(", tickerSymbol=");
		sb.append(tickerSymbol);
		sb.append(", type=");
		sb.append(type);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CompanyInfo toEntityModel() {
		CompanyInfoImpl companyInfoImpl = new CompanyInfoImpl();

		companyInfoImpl.setMvccVersion(mvccVersion);
		companyInfoImpl.setCompanyInfoId(companyInfoId);
		companyInfoImpl.setCompanyId(companyId);

		if (homeURL == null) {
			companyInfoImpl.setHomeURL("");
		}
		else {
			companyInfoImpl.setHomeURL(homeURL);
		}

		if (indexNameCurrent == null) {
			companyInfoImpl.setIndexNameCurrent("");
		}
		else {
			companyInfoImpl.setIndexNameCurrent(indexNameCurrent);
		}

		if (indexNameNext == null) {
			companyInfoImpl.setIndexNameNext("");
		}
		else {
			companyInfoImpl.setIndexNameNext(indexNameNext);
		}

		if (industry == null) {
			companyInfoImpl.setIndustry("");
		}
		else {
			companyInfoImpl.setIndustry(industry);
		}

		if (key == null) {
			companyInfoImpl.setKey("");
		}
		else {
			companyInfoImpl.setKey(key);
		}

		if (legalId == null) {
			companyInfoImpl.setLegalId("");
		}
		else {
			companyInfoImpl.setLegalId(legalId);
		}

		if (legalName == null) {
			companyInfoImpl.setLegalName("");
		}
		else {
			companyInfoImpl.setLegalName(legalName);
		}

		if (legalType == null) {
			companyInfoImpl.setLegalType("");
		}
		else {
			companyInfoImpl.setLegalType(legalType);
		}

		companyInfoImpl.setLogoId(logoId);

		if (name == null) {
			companyInfoImpl.setName("");
		}
		else {
			companyInfoImpl.setName(name);
		}

		if (sicCode == null) {
			companyInfoImpl.setSicCode("");
		}
		else {
			companyInfoImpl.setSicCode(sicCode);
		}

		if (size == null) {
			companyInfoImpl.setSize("");
		}
		else {
			companyInfoImpl.setSize(size);
		}

		if (tickerSymbol == null) {
			companyInfoImpl.setTickerSymbol("");
		}
		else {
			companyInfoImpl.setTickerSymbol(tickerSymbol);
		}

		if (type == null) {
			companyInfoImpl.setType("");
		}
		else {
			companyInfoImpl.setType(type);
		}

		companyInfoImpl.resetOriginalValues();

		return companyInfoImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		companyInfoId = objectInput.readLong();

		companyId = objectInput.readLong();
		homeURL = objectInput.readUTF();
		indexNameCurrent = objectInput.readUTF();
		indexNameNext = objectInput.readUTF();
		industry = objectInput.readUTF();
		key = (String)objectInput.readObject();
		legalId = objectInput.readUTF();
		legalName = objectInput.readUTF();
		legalType = objectInput.readUTF();

		logoId = objectInput.readLong();
		name = objectInput.readUTF();
		sicCode = objectInput.readUTF();
		size = objectInput.readUTF();
		tickerSymbol = objectInput.readUTF();
		type = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(companyInfoId);

		objectOutput.writeLong(companyId);

		if (homeURL == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(homeURL);
		}

		if (indexNameCurrent == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(indexNameCurrent);
		}

		if (indexNameNext == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(indexNameNext);
		}

		if (industry == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(industry);
		}

		if (key == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(key);
		}

		if (legalId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(legalId);
		}

		if (legalName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(legalName);
		}

		if (legalType == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(legalType);
		}

		objectOutput.writeLong(logoId);

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (sicCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(sicCode);
		}

		if (size == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(size);
		}

		if (tickerSymbol == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(tickerSymbol);
		}

		if (type == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(type);
		}
	}

	public long mvccVersion;
	public long companyInfoId;
	public long companyId;
	public String homeURL;
	public String indexNameCurrent;
	public String indexNameNext;
	public String industry;
	public String key;
	public String legalId;
	public String legalName;
	public String legalType;
	public long logoId;
	public String name;
	public String sicCode;
	public String size;
	public String tickerSymbol;
	public String type;

}
// LIFERAY-SERVICE-BUILDER-HASH:-880529563