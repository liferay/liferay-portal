/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.sort;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.odata.sort.InvalidSortException;

import java.util.List;

/**
 * @author Carlos Correa
 */
public class SortDSLQueryVisitor extends BaseSortDSLQueryVisitor {

	public SortDSLQueryVisitor(
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		super(objectFieldLocalService, objectRelationshipLocalService);
	}

	@Override
	public DSLQuery visit(DSLQuery dslQuery, Sort sort) throws PortalException {
		ObjectDefinition objectDefinition = sort.getObjectDefinition();

		List<String> fieldPathParts = StringUtil.split(
			sort.getFieldPath(), CharPool.FORWARD_SLASH);

		List<String> objectRelationshipNames = ListUtil.subList(
			fieldPathParts, 0, fieldPathParts.size() - 1);

		for (int i = 0; i < objectRelationshipNames.size(); i++) {
			String objectRelationshipName = objectRelationshipNames.get(i);

			ObjectRelationship objectRelationship =
				objectRelationshipLocalService.
					getObjectRelationshipByObjectDefinitionId(
						objectDefinition.getObjectDefinitionId(),
						objectRelationshipName);

			ObjectDefinition relatedObjectDefinition =
				ObjectRelationshipUtil.getRelatedObjectDefinition(
					objectDefinition, objectRelationship);

			dslQuery = _visit(
				dslQuery, objectDefinition, objectRelationship,
				StringUtil.merge(
					objectRelationshipNames.subList(0, i + 1),
					StringPool.FORWARD_SLASH),
				relatedObjectDefinition, sort);

			objectDefinition = relatedObjectDefinition;
		}

		ObjectEntryFieldSortDSLQueryVisitor
			objectEntryFieldSortDSLQueryVisitor =
				new ObjectEntryFieldSortDSLQueryVisitor(
					objectFieldLocalService);

		return objectEntryFieldSortDSLQueryVisitor.visit(
			dslQuery, new Sort(objectDefinition, sort));
	}

	private DSLQuery _visit(
			DSLQuery dslQuery, ObjectDefinition objectDefinition,
			ObjectRelationship objectRelationship, String path,
			ObjectDefinition relatedObjectDefinition, Sort sort)
		throws PortalException {

		if (!objectRelationship.hasRelationshipObjectField()) {
			String relationshipType = CamelCaseUtil.fromCamelCase(
				objectRelationship.getType(), CharPool.SPACE);

			throw new InvalidSortException(
				"Unable to sort by a " + relationshipType +
					" related object field");
		}

		BaseSortDSLQueryVisitor baseSortDSLQueryVisitor = null;

		if (objectDefinition.getObjectDefinitionId() !=
				objectRelationship.getObjectDefinitionId1()) {

			baseSortDSLQueryVisitor =
				new ObjectEntryMTo1RelationshipSortDSLQueryVisitor(
					objectFieldLocalService, objectRelationshipLocalService);
		}
		else {
			baseSortDSLQueryVisitor =
				new ObjectEntry1ToMRelationshipSortDSLQueryVisitor(
					objectFieldLocalService, objectRelationshipLocalService);
		}

		return baseSortDSLQueryVisitor.visit(
			dslQuery,
			new RelationshipSort(
				objectDefinition, objectRelationship, path,
				relatedObjectDefinition, sort));
	}

}