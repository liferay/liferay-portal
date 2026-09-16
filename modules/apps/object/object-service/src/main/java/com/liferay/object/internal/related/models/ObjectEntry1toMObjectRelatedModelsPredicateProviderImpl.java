/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.internal.entry.util.ObjectEntrySearchUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntryTable;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTableFactory;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectEntry1toMObjectRelatedModelsPredicateProviderImpl
	extends BaseObjectEntryObjectRelatedModelsPredicateProviderImpl {

	public ObjectEntry1toMObjectRelatedModelsPredicateProviderImpl(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService) {

		super(objectDefinition, objectFieldLocalService);
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_MANY;
	}

	@Override
	public Predicate getPredicate(
			Long[] groupIds, ObjectRelationship objectRelationship,
			Predicate predicate, ObjectDefinition relatedObjectDefinition)
		throws PortalException {

		ObjectDefinition objectDefinition1 = _getObjectDefinition1(
			objectRelationship);

		DynamicObjectDefinitionTable
			objectDefinition1DynamicObjectDefinitionTable =
				getDynamicObjectDefinitionTable(objectDefinition1);

		ObjectDefinition objectDefinition2 = _getObjectDefinition2(
			objectRelationship);

		DynamicObjectDefinitionTable
			objectDefinition2DynamicObjectDefinitionTable =
				getDynamicObjectDefinitionTable(objectDefinition2);
		DynamicObjectDefinitionTable
			objectDefinition2ExtensionDynamicObjectDefinitionTable =
				getExtensionDynamicObjectDefinitionTable(objectDefinition2);

		Column<DynamicObjectDefinitionTable, ?> objectRelationshipColumn =
			_getObjectRelationshipColumn(
				objectDefinition2DynamicObjectDefinitionTable,
				objectDefinition2ExtensionDynamicObjectDefinitionTable,
				objectDefinition1, objectRelationship);

		if (objectDefinition.getObjectDefinitionId() ==
				objectRelationship.getObjectDefinitionId1()) {

			return _getPredicate(
				objectDefinition1DynamicObjectDefinitionTable.
					getPrimaryKeyColumn(),
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition2, objectFieldLocalService),
				objectDefinition2DynamicObjectDefinitionTable,
				objectDefinition2ExtensionDynamicObjectDefinitionTable,
				DSLQueryFactoryUtil.select(objectRelationshipColumn), groupIds,
				objectDefinition2, predicate);
		}

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			objectRelationshipColumn.getTable();

		Column<?, ?> objectDefinition2PKObjectFieldColumn =
			objectDefinition2DynamicObjectDefinitionTable.getPrimaryKeyColumn();

		return objectDefinition2PKObjectFieldColumn.in(
			DSLQueryFactoryUtil.select(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn()
			).from(
				dynamicObjectDefinitionTable
			).where(
				_getPredicate(
					objectRelationshipColumn,
					DynamicObjectDefinitionLocalizationTableFactory.create(
						objectDefinition1, objectFieldLocalService),
					objectDefinition1DynamicObjectDefinitionTable,
					getExtensionDynamicObjectDefinitionTable(objectDefinition1),
					DSLQueryFactoryUtil.select(
						objectDefinition1DynamicObjectDefinitionTable.
							getPrimaryKeyColumn()),
					groupIds, objectDefinition1, predicate)
			));
	}

	private ObjectDefinition _getObjectDefinition1(
			ObjectRelationship objectRelationship)
		throws PortalException {

		if (objectRelationship.getObjectDefinitionId1() ==
				objectDefinition.getObjectDefinitionId()) {

			return objectDefinition;
		}

		return ObjectDefinitionLocalServiceUtil.getObjectDefinition(
			objectRelationship.getObjectDefinitionId1());
	}

	private ObjectDefinition _getObjectDefinition2(
			ObjectRelationship objectRelationship)
		throws PortalException {

		if (objectRelationship.getObjectDefinitionId2() ==
				objectDefinition.getObjectDefinitionId()) {

			return objectDefinition;
		}

		return ObjectDefinitionLocalServiceUtil.getObjectDefinition(
			objectRelationship.getObjectDefinitionId2());
	}

	private Column<DynamicObjectDefinitionTable, ?>
		_getObjectRelationshipColumn(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable,
			ObjectDefinition objectDefinition1,
			ObjectRelationship objectRelationship) {

		String columnName =
			ObjectRelationshipUtil.getObjectRelationshipFieldName(
				objectDefinition1, objectRelationship.getName());

		Column<DynamicObjectDefinitionTable, ?> column =
			dynamicObjectDefinitionTable.getColumn(columnName);

		if (column != null) {
			return column;
		}

		return extensionDynamicObjectDefinitionTable.getColumn(columnName);
	}

	private Predicate _getPredicate(
			Column<?, ?> column,
			DynamicObjectDefinitionLocalizationTable
				dynamicObjectDefinitionLocalizationTable,
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			DynamicObjectDefinitionTable extensionDynamicObjectDefinitionTable,
			FromStep fromStep, Long[] groupIds,
			ObjectDefinition objectDefinition, Predicate predicate)
		throws PortalException {

		return column.in(
			fromStep.from(
				dynamicObjectDefinitionTable
			).innerJoinON(
				ObjectEntryTable.INSTANCE,
				ObjectEntryTable.INSTANCE.objectEntryId.eq(
					dynamicObjectDefinitionTable.getPrimaryKeyColumn())
			).leftJoinOn(
				extensionDynamicObjectDefinitionTable,
				getExtensionLeftJoinPredicate(
					dynamicObjectDefinitionTable,
					extensionDynamicObjectDefinitionTable)
			).leftJoinOn(
				dynamicObjectDefinitionLocalizationTable,
				ObjectEntrySearchUtil.getLeftJoinLocalizationTablePredicate(
					dynamicObjectDefinitionLocalizationTable,
					dynamicObjectDefinitionTable, null)
			).where(
				ObjectEntrySearchUtil.getObjectEntryIndexPredicate(
					groupIds, objectDefinition, predicate)
			));
	}

}