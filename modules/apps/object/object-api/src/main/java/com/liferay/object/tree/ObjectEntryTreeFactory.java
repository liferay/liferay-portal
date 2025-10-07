/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.tree;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pedro Leite
 */
public class ObjectEntryTreeFactory extends BaseTreeFactory {

	public ObjectEntryTreeFactory(
		ObjectEntryLocalService objectEntryLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService) {

		super(objectRelationshipLocalService);

		_objectEntryLocalService = objectEntryLocalService;
	}

	public Tree create(long objectEntryId) throws PortalException {
		UnsafeFunction<Node, List<Node>, PortalException> unsafeFunction =
			node -> {
				ObjectEntry parentObjectEntry =
					_objectEntryLocalService.fetchObjectEntry(
						node.getPrimaryKey());

				List<Node> childrenNodes = new ArrayList<>();

				for (ObjectRelationship objectRelationship :
						objectRelationshipLocalService.getObjectRelationships(
							parentObjectEntry.getObjectDefinitionId(), true)) {

					childrenNodes.addAll(
						TransformUtil.transform(
							_objectEntryLocalService.getOneToManyObjectEntries(
								parentObjectEntry.getGroupId(),
								objectRelationship.getObjectRelationshipId(),
								null, false, parentObjectEntry.getPrimaryKey(),
								true, null, QueryUtil.ALL_POS,
								QueryUtil.ALL_POS, null),
							objectEntry -> new Node(
								new Edge(
									objectRelationship.
										getObjectRelationshipId()),
								node, objectEntry.getObjectEntryId())));
				}

				return childrenNodes;
			};

		return apply(objectEntryId, unsafeFunction);
	}

	private final ObjectEntryLocalService _objectEntryLocalService;

}