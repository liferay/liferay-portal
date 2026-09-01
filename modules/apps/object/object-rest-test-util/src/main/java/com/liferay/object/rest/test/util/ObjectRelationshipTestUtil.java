/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.test.util;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

/**
 * @author Luis Miguel Barcos
 */
public class ObjectRelationshipTestUtil {

	public static ObjectRelationship addObjectRelationship(
			ObjectDefinition objectDefinition,
			ObjectDefinition relatedObjectDefinition, long userId, String type)
		throws Exception {

		return addObjectRelationship(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, objectDefinition,
			relatedObjectDefinition, userId, type);
	}

	public static ObjectRelationship addObjectRelationship(
			String deletionType, ObjectDefinition objectDefinition,
			ObjectDefinition relatedObjectDefinition, long userId, String type)
		throws Exception {

		return addObjectRelationship(
			deletionType, objectDefinition, relatedObjectDefinition, userId,
			StringUtil.randomId(), type);
	}

	public static ObjectRelationship addObjectRelationship(
			String deletionType, ObjectDefinition objectDefinition,
			ObjectDefinition relatedObjectDefinition, long userId, String name,
			String type)
		throws Exception {

		return ObjectRelationshipLocalServiceUtil.addObjectRelationship(
			null, userId, objectDefinition.getObjectDefinitionId(),
			relatedObjectDefinition.getObjectDefinitionId(), 0, deletionType,
			false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			name, false, type, null);
	}

	public static void relateObjectEntries(
			long objectEntryId1, long objectEntryId2,
			ObjectRelationship objectRelationship, long userId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAssetTagNames(null);

		ObjectRelationshipLocalServiceUtil.
			addObjectRelationshipMappingTableValues(
				userId, objectRelationship.getObjectRelationshipId(),
				objectEntryId1, objectEntryId2, serviceContext);
	}

	public static ObjectRelationship updateObjectRelationship(
			String deletionType, long objectRelationshipId)
		throws Exception {

		return ObjectRelationshipLocalServiceUtil.updateObjectRelationship(
			null, objectRelationshipId, 0, deletionType, false, null, null);
	}

}