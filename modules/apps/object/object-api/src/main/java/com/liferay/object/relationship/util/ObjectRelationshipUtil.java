/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.relationship.util;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.NoSuchObjectRelationshipException;
import com.liferay.object.exception.ObjectRelationshipReverseException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.object.system.JaxRsApplicationDescriptor;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Marcela Cunha
 */
public class ObjectRelationshipUtil {

	public static Set<String> getDefaultObjectRelationshipTypes() {
		return _defaultObjectRelationshipTypes;
	}

	public static String getNotificationTermNamePrefix(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship) {

		return StringUtil.toUpperCase(
			StringBundler.concat(
				objectRelationship.getName(), StringPool.UNDERLINE,
				objectDefinition.getShortName()));
	}

	public static ObjectRelationship getObjectRelationship(
			List<ObjectRelationship> objectRelationships)
		throws PortalException {

		if (ListUtil.isEmpty(objectRelationships)) {
			throw new NoSuchObjectRelationshipException();
		}

		if (objectRelationships.size() == 1) {
			return objectRelationships.get(0);
		}

		for (ObjectRelationship objectRelationship : objectRelationships) {
			if (!objectRelationship.isReverse()) {
				return objectRelationship;
			}
		}

		throw new ObjectRelationshipReverseException();
	}

	public static String getObjectRelationshipFieldName(
		ObjectDefinition objectDefinition, String objectRelationshipName) {

		return StringBundler.concat(
			ObjectRelationshipConstants.OBJECT_RELATIONSHIP_FIELD_NAME_PREFIX,
			objectRelationshipName, StringPool.UNDERLINE,
			objectDefinition.getPKObjectFieldName());
	}

	public static Set<String> getObjectRelationshipTypes(
		ObjectDefinition objectDefinition,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			return _defaultObjectRelationshipTypes;
		}

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		if (systemObjectDefinitionManager == null) {
			return Collections.emptySet();
		}

		return systemObjectDefinitionManager.
			getAllowedObjectRelationshipTypes();
	}

	public static Map<String, String> getPKObjectFieldDBColumnNames(
		ObjectDefinition objectDefinition1, ObjectDefinition objectDefinition2,
		boolean reverse) {

		String pkObjectFieldDBColumnName1 =
			objectDefinition1.getPKObjectFieldDBColumnName();
		String pkObjectFieldDBColumnName2 =
			objectDefinition2.getPKObjectFieldDBColumnName();

		if (objectDefinition1.getObjectDefinitionId() !=
				objectDefinition2.getObjectDefinitionId()) {

			return HashMapBuilder.put(
				"pkObjectFieldDBColumnName1", pkObjectFieldDBColumnName1
			).put(
				"pkObjectFieldDBColumnName2", pkObjectFieldDBColumnName2
			).build();
		}

		return HashMapBuilder.put(
			"pkObjectFieldDBColumnName1",
			pkObjectFieldDBColumnName1.concat(reverse ? "2" : "1")
		).put(
			"pkObjectFieldDBColumnName2",
			pkObjectFieldDBColumnName2.concat(reverse ? "1" : "2")
		).build();
	}

	public static String getRESTContextPath(
		ObjectDefinition objectDefinition,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		if (!objectDefinition.isUnmodifiableSystemObject()) {
			return objectDefinition.getRESTContextPath();
		}

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		if (systemObjectDefinitionManager == null) {
			return StringPool.BLANK;
		}

		JaxRsApplicationDescriptor jaxRsApplicationDescriptor =
			systemObjectDefinitionManager.getJaxRsApplicationDescriptor();

		return jaxRsApplicationDescriptor.getRESTContextPath();
	}

	public static ObjectDefinition getRelatedObjectDefinition(
		ObjectDefinition objectDefinition,
		ObjectRelationship objectRelationship) {

		if (objectRelationship.getObjectDefinitionId1() ==
				objectDefinition.getObjectDefinitionId()) {

			return ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
				objectRelationship.getObjectDefinitionId2());
		}

		return ObjectDefinitionLocalServiceUtil.fetchObjectDefinition(
			objectRelationship.getObjectDefinitionId1());
	}

	public static Set<ObjectDefinition> getRelatedObjectDefinitions(
		ObjectDefinition objectDefinition) {

		Set<ObjectDefinition> relatedObjectDefinitions = new HashSet<>();

		for (ObjectRelationship objectRelationship :
				ObjectRelationshipLocalServiceUtil.getAllObjectRelationships(
					objectDefinition.getObjectDefinitionId())) {

			if (objectRelationship.isSelf()) {
				continue;
			}

			relatedObjectDefinitions.add(
				getRelatedObjectDefinition(
					objectDefinition, objectRelationship));
		}

		return relatedObjectDefinitions;
	}

	public static boolean isParameterRequired(
		ObjectDefinition objectDefinition,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		String restContextPath = getRESTContextPath(
			objectDefinition, systemObjectDefinitionManagerRegistry);

		return restContextPath.matches(".*/\\{\\w+}/.*");
	}

	private static final Set<String> _defaultObjectRelationshipTypes =
		Collections.unmodifiableSet(
			SetUtil.fromArray(
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY));

}