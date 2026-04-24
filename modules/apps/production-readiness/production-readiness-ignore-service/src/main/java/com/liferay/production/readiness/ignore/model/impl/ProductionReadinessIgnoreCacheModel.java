/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.production.readiness.ignore.model.impl;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.production.readiness.ignore.model.ProductionReadinessIgnore;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import java.util.Date;

/**
 * The cache model class for representing ProductionReadinessIgnore in entity cache.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class ProductionReadinessIgnoreCacheModel
	implements CacheModel<ProductionReadinessIgnore>, Externalizable {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductionReadinessIgnoreCacheModel)) {
			return false;
		}

		ProductionReadinessIgnoreCacheModel
			productionReadinessIgnoreCacheModel =
				(ProductionReadinessIgnoreCacheModel)object;

		if (productionReadinessIgnoreId ==
				productionReadinessIgnoreCacheModel.
					productionReadinessIgnoreId) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return HashUtil.hash(0, productionReadinessIgnoreId);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(17);

		sb.append("{productionReadinessIgnoreId=");
		sb.append(productionReadinessIgnoreId);
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
		sb.append(", ruleKey=");
		sb.append(ruleKey);
		sb.append(", reason=");
		sb.append(reason);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public ProductionReadinessIgnore toEntityModel() {
		ProductionReadinessIgnoreImpl productionReadinessIgnoreImpl =
			new ProductionReadinessIgnoreImpl();

		productionReadinessIgnoreImpl.setProductionReadinessIgnoreId(
			productionReadinessIgnoreId);
		productionReadinessIgnoreImpl.setCompanyId(companyId);
		productionReadinessIgnoreImpl.setUserId(userId);

		if (userName == null) {
			productionReadinessIgnoreImpl.setUserName("");
		}
		else {
			productionReadinessIgnoreImpl.setUserName(userName);
		}

		if (createDate == Long.MIN_VALUE) {
			productionReadinessIgnoreImpl.setCreateDate(null);
		}
		else {
			productionReadinessIgnoreImpl.setCreateDate(new Date(createDate));
		}

		if (modifiedDate == Long.MIN_VALUE) {
			productionReadinessIgnoreImpl.setModifiedDate(null);
		}
		else {
			productionReadinessIgnoreImpl.setModifiedDate(
				new Date(modifiedDate));
		}

		if (ruleKey == null) {
			productionReadinessIgnoreImpl.setRuleKey("");
		}
		else {
			productionReadinessIgnoreImpl.setRuleKey(ruleKey);
		}

		if (reason == null) {
			productionReadinessIgnoreImpl.setReason("");
		}
		else {
			productionReadinessIgnoreImpl.setReason(reason);
		}

		productionReadinessIgnoreImpl.resetOriginalValues();

		return productionReadinessIgnoreImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput) throws IOException {
		productionReadinessIgnoreId = objectInput.readLong();

		companyId = objectInput.readLong();

		userId = objectInput.readLong();
		userName = objectInput.readUTF();
		createDate = objectInput.readLong();
		modifiedDate = objectInput.readLong();
		ruleKey = objectInput.readUTF();
		reason = objectInput.readUTF();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(productionReadinessIgnoreId);

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

		if (ruleKey == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(ruleKey);
		}

		if (reason == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(reason);
		}
	}

	public long productionReadinessIgnoreId;
	public long companyId;
	public long userId;
	public String userName;
	public long createDate;
	public long modifiedDate;
	public String ruleKey;
	public String reason;

}
// LIFERAY-SERVICE-BUILDER-HASH:-454334627