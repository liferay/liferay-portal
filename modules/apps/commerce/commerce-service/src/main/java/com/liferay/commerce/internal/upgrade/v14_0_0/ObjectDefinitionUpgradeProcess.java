/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.upgrade.v14_0_0;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Pedro Leite
 */
public class ObjectDefinitionUpgradeProcess extends UpgradeProcess {

	public ObjectDefinitionUpgradeProcess(
		CompanyLocalService companyLocalService,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		_companyLocalService = companyLocalService;
		_objectDefinitionLocalService = objectDefinitionLocalService;
		_objectFieldLocalService = objectFieldLocalService;
		_objectRelationshipLocalService = objectRelationshipLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> {
				ObjectDefinition objectDefinition =
					_objectDefinitionLocalService.
						fetchObjectDefinitionByExternalReferenceCode(
							"L_COMMERCE_PRODUCT_DEFINITION", companyId);

				if (objectDefinition == null) {
					return;
				}

				objectDefinition.setPKObjectFieldDBColumnName(
					_NEW_PK_OBJECT_FIELD_NAME);
				objectDefinition.setPKObjectFieldName(
					_NEW_PK_OBJECT_FIELD_NAME);

				objectDefinition =
					_objectDefinitionLocalService.updateObjectDefinition(
						objectDefinition);

				_updateObjectField(
					_NEW_PK_OBJECT_FIELD_NAME,
					_objectFieldLocalService.getObjectField(
						objectDefinition.getObjectDefinitionId(), "id"));
				_updateObjectField(
					_NEW_PK_OBJECT_FIELD_NAME,
					_objectFieldLocalService.getObjectField(
						objectDefinition.getObjectDefinitionId(), "productId"));

				_updateDynamicObjectDefinitionTable(
					objectDefinition.getExtensionDBTableName(), "LONG not null",
					objectDefinition, _OLD_PK_OBJECT_FIELD_NAME);
				_updateDynamicObjectDefinitionTable(
					objectDefinition.getLocalizationDBTableName(),
					"LONG not null", objectDefinition,
					_OLD_PK_OBJECT_FIELD_NAME);

				List<ObjectRelationship> objectRelationships =
					_objectRelationshipLocalService.getObjectRelationships(
						objectDefinition.getObjectDefinitionId());

				for (ObjectRelationship objectRelationship :
						objectRelationships) {

					String dbTableName = null;
					String dbType = "LONG";
					String oldDBColumnName = null;

					if (Objects.equals(
							objectRelationship.getType(),
							ObjectRelationshipConstants.TYPE_ONE_TO_ONE) ||
						Objects.equals(
							objectRelationship.getType(),
							ObjectRelationshipConstants.TYPE_ONE_TO_MANY)) {

						ObjectField objectField =
							_objectFieldLocalService.getObjectField(
								objectRelationship.getObjectFieldId2());

						dbTableName = objectField.getDBTableName();
						oldDBColumnName = objectField.getDBColumnName();

						objectField.setName(
							_replaceLast(objectField.getName()));

						_updateObjectField(
							_replaceLast(objectField.getDBColumnName()),
							objectField);
					}
					else if (Objects.equals(
								objectRelationship.getType(),
								ObjectRelationshipConstants.
									TYPE_MANY_TO_MANY) &&
							 !objectRelationship.isReverse()) {

						dbTableName = objectRelationship.getDBTableName();
						dbType += " not null";
						oldDBColumnName = _OLD_PK_OBJECT_FIELD_NAME;
					}

					_updateDynamicObjectDefinitionTable(
						dbTableName, dbType, objectDefinition, oldDBColumnName);
				}
			});
	}

	private String _replaceLast(String name) {
		return StringUtil.replaceLast(
			name, _OLD_PK_OBJECT_FIELD_NAME, _NEW_PK_OBJECT_FIELD_NAME);
	}

	private void _updateDynamicObjectDefinitionTable(
			String dbTableName, String dbType,
			ObjectDefinition objectDefinition, String oldDBColumnName)
		throws Exception {

		if ((dbTableName == null) || !hasTable(dbTableName) ||
			!hasColumn(dbTableName, oldDBColumnName)) {

			return;
		}

		runSQL(
			StringBundler.concat(
				"update ", dbTableName, " set ", oldDBColumnName, " = (select ",
				_NEW_PK_OBJECT_FIELD_NAME, " from ",
				objectDefinition.getDBTableName(), " where ",
				_OLD_PK_OBJECT_FIELD_NAME, " = ", dbTableName, ".",
				oldDBColumnName, ")"));

		alterColumnName(
			dbTableName, oldDBColumnName,
			_replaceLast(oldDBColumnName) + StringPool.SPACE + dbType);
	}

	private void _updateObjectField(
		String dbColumnName, ObjectField objectField) {

		objectField.setDBColumnName(dbColumnName);

		_objectFieldLocalService.updateObjectField(objectField);
	}

	private static final String _NEW_PK_OBJECT_FIELD_NAME = "CProductId";

	private static final String _OLD_PK_OBJECT_FIELD_NAME = "CPDefinitionId";

	private final CompanyLocalService _companyLocalService;
	private final ObjectDefinitionLocalService _objectDefinitionLocalService;
	private final ObjectFieldLocalService _objectFieldLocalService;
	private final ObjectRelationshipLocalService
		_objectRelationshipLocalService;

}