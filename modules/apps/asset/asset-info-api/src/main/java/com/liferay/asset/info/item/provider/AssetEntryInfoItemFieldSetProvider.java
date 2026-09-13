/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.info.item.provider;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.field.InfoFieldSet;
import com.liferay.info.field.InfoFieldValue;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Jürgen Kappler
 * @author Jorge Ferrer
 */
@ProviderType
public interface AssetEntryInfoItemFieldSetProvider {

	public InfoFieldSet getInfoFieldSet(AssetEntry assetEntry);

	public InfoFieldSet getInfoFieldSet(String itemClassName);

	public InfoFieldSet getInfoFieldSet(
		String itemClassName, long itemClassTypeId, long scopeGroupId);

	public List<InfoFieldValue<Object>> getInfoFieldValues(
		AssetEntry assetEntry);

	public List<InfoFieldValue<Object>> getInfoFieldValues(
			long companyId, String itemClassName, long itemClassPK)
		throws NoSuchInfoItemException;

}