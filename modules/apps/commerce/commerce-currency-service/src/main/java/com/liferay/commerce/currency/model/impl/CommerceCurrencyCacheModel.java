/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.model.impl;

import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.math.BigDecimal;

import java.util.Date;

/**
 * The cache model class for representing CommerceCurrency in entity cache.
 *
 * @author Andrea Di Giorgi
 * @generated
 */
public class CommerceCurrencyCacheModel
	implements CacheModel<CommerceCurrency>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CommerceCurrencyCacheModel)) {
			return false;
		}

		CommerceCurrencyCacheModel commerceCurrencyCacheModel =
			(CommerceCurrencyCacheModel)object;

		if ((commerceCurrencyId ==
				commerceCurrencyCacheModel.commerceCurrencyId) &&
			(mvccVersion == commerceCurrencyCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, commerceCurrencyId);

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
		StringBundler sb = new StringBundler(45);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", uuid=");
		sb.append(uuid);
		sb.append(", externalReferenceCode=");
		sb.append(externalReferenceCode);
		sb.append(", commerceCurrencyId=");
		sb.append(commerceCurrencyId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", userId=");
		sb.append(userId);
		sb.append(", userName=");
		sb.append(userName);
		sb.append(", createDate=");
		sb.append(createDate);
		sb.append(", modifiedDate=");
		sb.append(modifiedDate);
		sb.append(", code=");
		sb.append(code);
		sb.append(", name=");
		sb.append(name);
		sb.append(", symbol=");
		sb.append(symbol);
		sb.append(", rate=");
		sb.append(rate);
		sb.append(", formatPattern=");
		sb.append(formatPattern);
		sb.append(", maxFractionDigits=");
		sb.append(maxFractionDigits);
		sb.append(", minFractionDigits=");
		sb.append(minFractionDigits);
		sb.append(", roundingMode=");
		sb.append(roundingMode);
		sb.append(", primary=");
		sb.append(primary);
		sb.append(", priority=");
		sb.append(priority);
		sb.append(", active=");
		sb.append(active);
		sb.append(", lastPublishDate=");
		sb.append(lastPublishDate);
		sb.append(", status=");
		sb.append(status);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CommerceCurrency toEntityModel() {
		CommerceCurrencyImpl commerceCurrencyImpl = new CommerceCurrencyImpl();

		commerceCurrencyImpl.setMvccVersion(mvccVersion);

		if (uuid == null) {
			commerceCurrencyImpl.setUuid("");
		}
		else {
			commerceCurrencyImpl.setUuid(uuid);
		}

		if (externalReferenceCode == null) {
			commerceCurrencyImpl.setExternalReferenceCode("");
		}
		else {
			commerceCurrencyImpl.setExternalReferenceCode(
				externalReferenceCode);
		}

		commerceCurrencyImpl.setCommerceCurrencyId(commerceCurrencyId);
		commerceCurrencyImpl.setCompanyId(companyId);
		commerceCurrencyImpl.setUserId(userId);

		if (userName == null) {
			commerceCurrencyImpl.setUserName("");
		}
		else {
			commerceCurrencyImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			commerceCurrencyImpl.setCreateDate(null);
		}
		else {
			commerceCurrencyImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			commerceCurrencyImpl.setModifiedDate(null);
		}
		else {
			commerceCurrencyImpl.setModifiedDate(new Date(modifiedDate));
		}

		if (code == null) {
			commerceCurrencyImpl.setCode("");
		}
		else {
			commerceCurrencyImpl.setCode(code);
		}

		if (name == null) {
			commerceCurrencyImpl.setName("");
		}
		else {
			commerceCurrencyImpl.setName(name);
		}

		if (symbol == null) {
			commerceCurrencyImpl.setSymbol("");
		}
		else {
			commerceCurrencyImpl.setSymbol(symbol);
		}

		commerceCurrencyImpl.setRate(rate);

		if (formatPattern == null) {
			commerceCurrencyImpl.setFormatPattern("");
		}
		else {
			commerceCurrencyImpl.setFormatPattern(formatPattern);
		}

		commerceCurrencyImpl.setMaxFractionDigits(maxFractionDigits);
		commerceCurrencyImpl.setMinFractionDigits(minFractionDigits);

		if (roundingMode == null) {
			commerceCurrencyImpl.setRoundingMode("");
		}
		else {
			commerceCurrencyImpl.setRoundingMode(roundingMode);
		}

		commerceCurrencyImpl.setPrimary(primary);
		commerceCurrencyImpl.setPriority(priority);
		commerceCurrencyImpl.setActive(active);

		if (lastPublishDate == Long.MIN_VALUE) {
			commerceCurrencyImpl.setLastPublishDate(null);
		}
		else {
			commerceCurrencyImpl.setLastPublishDate(new Date(lastPublishDate));
		}

		commerceCurrencyImpl.setStatus(status);

		commerceCurrencyImpl.resetOriginalValues();

		return commerceCurrencyImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();
		uuid = objectInput.readUTF();
		externalReferenceCode = objectInput.readUTF();

		commerceCurrencyId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		code = objectInput.readUTF();
		name = objectInput.readUTF();
		symbol = objectInput.readUTF();
		rate = (BigDecimal)objectInput.readObject();
		formatPattern = objectInput.readUTF();

		maxFractionDigits = objectInput.readInt();

		minFractionDigits = objectInput.readInt();
		roundingMode = objectInput.readUTF();

		primary = objectInput.readBoolean();

		priority = objectInput.readDouble();

		active = objectInput.readBoolean();
		lastPublishDate = objectInput.readLong();

		status = objectInput.readInt();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		if (uuid == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(uuid);
		}

		if (externalReferenceCode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(externalReferenceCode);
		}

		objectOutput.writeLong(commerceCurrencyId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(userId);

		if (userName == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(userName);
		}

		objectOutput.writeLong(createDate);
		objectOutput.writeLong(modifiedDate);

		if (code == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(code);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (symbol == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(symbol);
		}

		objectOutput.writeObject(rate);

		if (formatPattern == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(formatPattern);
		}

		objectOutput.writeInt(maxFractionDigits);

		objectOutput.writeInt(minFractionDigits);

		if (roundingMode == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(roundingMode);
		}

		objectOutput.writeBoolean(primary);

		objectOutput.writeDouble(priority);

		objectOutput.writeBoolean(active);
		objectOutput.writeLong(lastPublishDate);

		objectOutput.writeInt(status);
	}

	public long mvccVersion;
	public String uuid;
	public String externalReferenceCode;
	public long commerceCurrencyId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String code;
	public String name;
	public String symbol;
	public BigDecimal rate;
	public String formatPattern;
	public int maxFractionDigits;
	public int minFractionDigits;
	public String roundingMode;
	public boolean primary;
	public double priority;
	public boolean active;
	public long lastPublishDate;
	public int status;

}
// LIFERAY-SERVICE-BUILDER-HASH:614832523