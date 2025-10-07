/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.internal.odata.entity.v1_0;

import com.liferay.headless.asset.library.internal.util.AssetLibraryUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;

import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class AssetLibraryEntityModel implements EntityModel {

	public AssetLibraryEntityModel() {
		_entityFieldsMap = EntityModel.toEntityFieldsMap(
			new EntityField(
				"type", EntityField.Type.STRING, locale -> "type",
				locale -> "type",
				value -> String.valueOf(
					AssetLibraryUtil.getDepotEntryType((String)value))));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}