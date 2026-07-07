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
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTable;
import com.liferay.object.petra.sql.dsl.DynamicObjectRelationshipMappingTableFactory;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectEntryMtoMObjectRelatedModelsPredicateProviderImpl
	extends BaseObjectEntryObjectRelatedModelsPredicateProviderImpl {

	public ObjectEntryMtoMObjectRelatedModelsPredicateProviderImpl(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService) {

		super(objectDefinition, objectFieldLocalService);
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_MANY_TO_MANY;
	}

	@Override
	public Predicate getPredicate(
			Long[] groupIds, ObjectRelationship objectRelationship,
			Predicate predicate, ObjectDefinition relatedObjectDefinition)
		throws PortalException {

		Column<?, ?> dynamicObjectDefinitionTableColumn =
			getPKObjectFieldColumn(
				getDynamicObjectDefinitionTable(objectDefinition),
				objectDefinition.getPKObjectFieldDBColumnName());

		DynamicObjectRelationshipMappingTable
			dynamicObjectRelationshipMappingTable =
				DynamicObjectRelationshipMappingTableFactory.create(
					objectRelationship.getDBTableName(), objectDefinition,
					relatedObjectDefinition, objectRelationship.isReverse());

		Column<DynamicObjectRelationshipMappingTable, ?>
			dynamicObjectRelationshipMappingTableColumn =
				dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn2();

		DynamicObjectDefinitionLocalizationTable
			relatedDynamicObjectDefinitionLocalizationTable =
				DynamicObjectDefinitionLocalizationTableFactory.create(
					relatedObjectDefinition, objectFieldLocalService);
		DynamicObjectDefinitionTable relatedDynamicObjectDefinitionTable =
			getDynamicObjectDefinitionTable(relatedObjectDefinition);
		DynamicObjectDefinitionTable relatedObjectDefinitionExtensionTable =
			getExtensionDynamicObjectDefinitionTable(relatedObjectDefinition);

		return dynamicObjectDefinitionTableColumn.in(
			DSLQueryFactoryUtil.select(
				dynamicObjectRelationshipMappingTable.getPrimaryKeyColumn1()
			).from(
				dynamicObjectRelationshipMappingTable
			).where(
				dynamicObjectRelationshipMappingTableColumn.in(
					DSLQueryFactoryUtil.select(
						getPKObjectFieldColumn(
							relatedDynamicObjectDefinitionTable,
							relatedObjectDefinition.
								getPKObjectFieldDBColumnName())
					).from(
						relatedDynamicObjectDefinitionTable
					).innerJoinON(
						ObjectEntryTable.INSTANCE,
						ObjectEntryTable.INSTANCE.objectEntryId.eq(
							relatedDynamicObjectDefinitionTable.
								getPrimaryKeyColumn())
					).innerJoinON(
						relatedObjectDefinitionExtensionTable,
						relatedDynamicObjectDefinitionTable.getPrimaryKeyColumn(
						).eq(
							relatedObjectDefinitionExtensionTable.
								getPrimaryKeyColumn()
						)
					).leftJoinOn(
						relatedDynamicObjectDefinitionLocalizationTable,
						ObjectEntrySearchUtil.
							getLeftJoinLocalizationTablePredicate(
								relatedDynamicObjectDefinitionLocalizationTable,
								relatedDynamicObjectDefinitionTable, null)
					).where(
						ObjectEntrySearchUtil.getObjectEntryIndexPredicate(
							groupIds, relatedObjectDefinition, predicate)
					))
			));
	}

}