/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model.impl;

import com.liferay.commerce.product.model.CPDefinitionLocalization;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.MVCCModel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The cache model class for representing CPDefinitionLocalization in entity cache.
 *
 * @author Marco Leo
 * @generated
 */
public class CPDefinitionLocalizationCacheModel
	implements CacheModel<CPDefinitionLocalization>, Externalizable, MVCCModel {

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CPDefinitionLocalizationCacheModel)) {
			return false;
		}

		CPDefinitionLocalizationCacheModel cpDefinitionLocalizationCacheModel =
			(CPDefinitionLocalizationCacheModel)object;

		if ((cpDefinitionLocalizationId ==
				cpDefinitionLocalizationCacheModel.
					cpDefinitionLocalizationId) &&
			(mvccVersion == cpDefinitionLocalizationCacheModel.mvccVersion)) {

			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, cpDefinitionLocalizationId);

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
		StringBundler sb = new StringBundler(27);

		sb.append("{mvccVersion=");
		sb.append(mvccVersion);
		sb.append(", ctCollectionId=");
		sb.append(ctCollectionId);
		sb.append(", cpDefinitionLocalizationId=");
		sb.append(cpDefinitionLocalizationId);
		sb.append(", companyId=");
		sb.append(companyId);
		sb.append(", CPDefinitionId=");
		sb.append(CPDefinitionId);
		sb.append(", languageId=");
		sb.append(languageId);
		sb.append(", name=");
		sb.append(name);
		sb.append(", shortDescription=");
		sb.append(shortDescription);
		sb.append(", description=");
		sb.append(description);
		sb.append(", metaTitle=");
		sb.append(metaTitle);
		sb.append(", metaDescription=");
		sb.append(metaDescription);
		sb.append(", metaKeywords=");
		sb.append(metaKeywords);
		sb.append(", CProductId=");
		sb.append(CProductId);
		sb.append("}");

		return sb.toString();
	}

	@Override
	public CPDefinitionLocalization toEntityModel() {
		CPDefinitionLocalizationImpl cpDefinitionLocalizationImpl =
			new CPDefinitionLocalizationImpl();

		cpDefinitionLocalizationImpl.setMvccVersion(mvccVersion);
		cpDefinitionLocalizationImpl.setCtCollectionId(ctCollectionId);
		cpDefinitionLocalizationImpl.setCpDefinitionLocalizationId(
			cpDefinitionLocalizationId);
		cpDefinitionLocalizationImpl.setCompanyId(companyId);
		cpDefinitionLocalizationImpl.setCPDefinitionId(CPDefinitionId);

		if (languageId == null) {
			cpDefinitionLocalizationImpl.setLanguageId("");
		}
		else {
			cpDefinitionLocalizationImpl.setLanguageId(languageId);
		}

		if (name == null) {
			cpDefinitionLocalizationImpl.setName("");
		}
		else {
			cpDefinitionLocalizationImpl.setName(name);
		}

		if (shortDescription == null) {
			cpDefinitionLocalizationImpl.setShortDescription("");
		}
		else {
			cpDefinitionLocalizationImpl.setShortDescription(shortDescription);
		}

		if (description == null) {
			cpDefinitionLocalizationImpl.setDescription("");
		}
		else {
			cpDefinitionLocalizationImpl.setDescription(description);
		}

		if (metaTitle == null) {
			cpDefinitionLocalizationImpl.setMetaTitle("");
		}
		else {
			cpDefinitionLocalizationImpl.setMetaTitle(metaTitle);
		}

		if (metaDescription == null) {
			cpDefinitionLocalizationImpl.setMetaDescription("");
		}
		else {
			cpDefinitionLocalizationImpl.setMetaDescription(metaDescription);
		}

		if (metaKeywords == null) {
			cpDefinitionLocalizationImpl.setMetaKeywords("");
		}
		else {
			cpDefinitionLocalizationImpl.setMetaKeywords(metaKeywords);
		}

		cpDefinitionLocalizationImpl.setCProductId(CProductId);

		cpDefinitionLocalizationImpl.resetOriginalValues();

		return cpDefinitionLocalizationImpl;
	}

	@Override
	public void readExternal(ObjectInput objectInput)
		throws ClassNotFoundException, IOException {

		mvccVersion = objectInput.readLong();

		ctCollectionId = objectInput.readLong();

		cpDefinitionLocalizationId = objectInput.readLong();

		companyId = objectInput.readLong();

		CPDefinitionId = objectInput.readLong();
		languageId = objectInput.readUTF();
		name = objectInput.readUTF();
		shortDescription = objectInput.readUTF();
		description = (String)objectInput.readObject();
		metaTitle = objectInput.readUTF();
		metaDescription = objectInput.readUTF();
		metaKeywords = objectInput.readUTF();

		CProductId = objectInput.readLong();
	}

	@Override
	public void writeExternal(ObjectOutput objectOutput) throws IOException {
		objectOutput.writeLong(mvccVersion);

		objectOutput.writeLong(ctCollectionId);

		objectOutput.writeLong(cpDefinitionLocalizationId);

		objectOutput.writeLong(companyId);

		objectOutput.writeLong(CPDefinitionId);

		if (languageId == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(languageId);
		}

		if (name == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(name);
		}

		if (shortDescription == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(shortDescription);
		}

		if (description == null) {
			objectOutput.writeObject("");
		}
		else {
			objectOutput.writeObject(description);
		}

		if (metaTitle == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(metaTitle);
		}

		if (metaDescription == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(metaDescription);
		}

		if (metaKeywords == null) {
			objectOutput.writeUTF("");
		}
		else {
			objectOutput.writeUTF(metaKeywords);
		}

		objectOutput.writeLong(CProductId);
	}

	public long mvccVersion;
	public long ctCollectionId;
	public long cpDefinitionLocalizationId;
	public long companyId;
	public long CPDefinitionId;
	public String languageId;
	public String name;
	public String shortDescription;
	public String description;
	public String metaTitle;
	public String metaDescription;
	public String metaKeywords;
	public long CProductId;

}