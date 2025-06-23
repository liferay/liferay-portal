/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.ObjectEntryTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.junit.Assert;

/**
 * @author Feliphe Marinho
 */
public class TreeTestUtil {

	public static void assertObjectDefinitionTree(
			Map<String, String[]> expectedMap, Tree actualTree,
			ObjectDefinitionLocalService objectDefinitionLocalService)
		throws PortalException {

		_assertTree(
			expectedMap, actualTree,
			node -> _getShortName(node, objectDefinitionLocalService));
	}

	public static void assertObjectEntryTree(
			Map<String, String[]> expectedMap, Tree actualTree,
			ObjectEntryLocalService objectEntryLocalService)
		throws PortalException {

		_assertTree(
			expectedMap, actualTree,
			node -> _getExternalReferenceCode(node, objectEntryLocalService));
	}

	public static void bind(
			ObjectRelationshipLocalService objectRelationshipLocalService,
			List<ObjectRelationship> objectRelationships)
		throws PortalException {

		for (ObjectRelationship objectRelationship : objectRelationships) {
			objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(),
				objectRelationship.getParameterObjectFieldId(),
				objectRelationship.getDeletionType(), true,
				objectRelationship.getLabelMap(), null);
		}
	}

	public static Tree createObjectDefinitionTree(
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			boolean published, Map<String, String[]> treeMap)
		throws Exception {

		for (Map.Entry<String, String[]> entry : treeMap.entrySet()) {
			ObjectDefinition parentObjectDefinition =
				objectDefinitionLocalService.fetchObjectDefinition(
					TestPropsValues.getCompanyId(), "C_" + entry.getKey());

			if (parentObjectDefinition == null) {
				parentObjectDefinition =
					ObjectDefinitionTestUtil.addCustomObjectDefinition(
						entry.getKey());
			}

			if (!parentObjectDefinition.isApproved() && published) {
				objectDefinitionLocalService.publishCustomObjectDefinition(
					TestPropsValues.getUserId(),
					parentObjectDefinition.getObjectDefinitionId());
			}

			for (String childObjectDefinitionName : entry.getValue()) {
				ObjectDefinition childObjectDefinition =
					ObjectDefinitionTestUtil.addCustomObjectDefinition(
						childObjectDefinitionName);

				if (published) {
					objectDefinitionLocalService.publishCustomObjectDefinition(
						TestPropsValues.getUserId(),
						childObjectDefinition.getObjectDefinitionId());
				}

				bind(
					objectRelationshipLocalService,
					Collections.singletonList(
						ObjectRelationshipTestUtil.addObjectRelationship(
							objectRelationshipLocalService,
							parentObjectDefinition, childObjectDefinition)));
			}
		}

		Set<String> keys = treeMap.keySet();

		Iterator<String> iterator = keys.iterator();

		ObjectDefinition rootObjectDefinition =
			objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_" + iterator.next());

		ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
			new ObjectDefinitionTreeFactory(
				objectDefinitionLocalService, objectRelationshipLocalService);

		return objectDefinitionTreeFactory.create(
			rootObjectDefinition.getObjectDefinitionId());
	}

	public static Tree createObjectEntryTree(
			String externalReferenceCodeSuffix,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectFieldLocalService objectFieldLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			long rootObjectDefinitionId)
		throws PortalException {

		ObjectDefinitionTreeFactory objectDefinitionTreeFactory =
			new ObjectDefinitionTreeFactory(
				objectDefinitionLocalService, objectRelationshipLocalService);

		Tree objectDefinitionTree = objectDefinitionTreeFactory.create(
			rootObjectDefinitionId);

		Iterator<Node> iterator = objectDefinitionTree.iterator();

		Node rootNode = iterator.next();

		Queue<String> externalReferenceCodes = new ArrayDeque<>(
			Arrays.asList("A", "AA", "AB", "AAA", "AAB"));

		ObjectEntry rootObjectEntry = objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(), rootNode.getPrimaryKey(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode",
				externalReferenceCodes.poll() + externalReferenceCodeSuffix
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Map<Long, Long> objectEntryIds = HashMapBuilder.put(
			rootNode.getPrimaryKey(), rootObjectEntry.getObjectEntryId()
		).build();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			ObjectEntry objectEntry = objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(), node.getPrimaryKey(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"externalReferenceCode",
					externalReferenceCodes.poll() + externalReferenceCodeSuffix
				).put(
					() -> {
						ObjectRelationship objectRelationship =
							objectRelationshipLocalService.
								getObjectRelationship(
									node.getEdge(
									).getObjectRelationshipId());

						ObjectField objectField =
							objectFieldLocalService.getObjectField(
								objectRelationship.getObjectFieldId2());

						return objectField.getName();
					},
					() -> {
						Node parentNode = node.getParentNode();

						return objectEntryIds.get(parentNode.getPrimaryKey());
					}
				).build(),
				ServiceContextTestUtil.getServiceContext());

			objectEntryIds.put(
				node.getPrimaryKey(), objectEntry.getObjectEntryId());
		}

		ObjectEntryTreeFactory objectEntryTreeFactory =
			new ObjectEntryTreeFactory(
				objectEntryLocalService, objectRelationshipLocalService);

		return objectEntryTreeFactory.create(
			rootObjectEntry.getObjectEntryId());
	}

	public static void deleteObjectDefinitionHierarchy(
			ObjectDefinitionLocalService objectDefinitionLocalService,
			String[] objectDefinitionNames,
			ObjectEntryLocalService objectEntryLocalService,
			ObjectRelationshipLocalService objectRelationshipLocalService)
		throws Exception {

		for (String objectDefinitionName : objectDefinitionNames) {
			ObjectDefinition objectDefinition =
				objectDefinitionLocalService.fetchObjectDefinition(
					TestPropsValues.getCompanyId(), objectDefinitionName);

			if (objectDefinition == null) {
				continue;
			}

			List<ObjectEntry> objectEntries =
				objectEntryLocalService.getObjectEntries(
					0, objectDefinition.getObjectDefinitionId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

			for (ObjectEntry objectEntry : objectEntries) {
				objectEntryLocalService.deleteObjectEntry(objectEntry);
			}

			if (objectDefinition.getRootObjectDefinitionId() != 0) {
				unbind(
					objectDefinition.getObjectDefinitionId(),
					objectRelationshipLocalService);
			}

			objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition.getObjectDefinitionId());
		}
	}

	public static void forEachNodeObjectDefinition(
			Iterator<Node> iterator,
			ObjectDefinitionLocalService objectDefinitionLocalService,
			UnsafeConsumer<ObjectDefinition, Exception> unsafeConsumer)
		throws Exception {

		while (iterator.hasNext()) {
			Node node = iterator.next();

			unsafeConsumer.accept(
				objectDefinitionLocalService.getObjectDefinition(
					node.getPrimaryKey()));
		}
	}

	public static void forEachNodeObjectEntry(
			Iterator<Node> iterator,
			ObjectEntryLocalService objectEntryLocalService,
			UnsafeConsumer<ObjectEntry, Exception> unsafeConsumer)
		throws Exception {

		while (iterator.hasNext()) {
			Node node = iterator.next();

			unsafeConsumer.accept(
				objectEntryLocalService.getObjectEntry(node.getPrimaryKey()));
		}
	}

	public static ObjectRelationship getEdgeObjectRelationship(
			ObjectDefinition objectDefinition,
			ObjectRelationshipLocalService objectRelationshipLocalService,
			Tree tree)
		throws PortalException {

		Node node = tree.getNode(objectDefinition.getObjectDefinitionId());

		Edge edge = node.getEdge();

		return objectRelationshipLocalService.getObjectRelationship(
			edge.getObjectRelationshipId());
	}

	public static void unbind(
			Long objectDefinitionId,
			ObjectRelationshipLocalService objectRelationshipLocalService)
		throws PortalException {

		List<ObjectRelationship> objectRelationships =
			objectRelationshipLocalService.getObjectRelationships(
				objectDefinitionId, true);

		for (ObjectRelationship objectRelationship : objectRelationships) {
			objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(),
				objectRelationship.getParameterObjectFieldId(),
				objectRelationship.getDeletionType(), false,
				objectRelationship.getLabelMap(), null);
		}
	}

	private static void _assertTree(
			Map<String, String[]> expectedMap, Tree actualTree,
			UnsafeFunction<Node, String, PortalException> unsafeFunction)
		throws PortalException {

		Map<String, String[]> actualMap = new LinkedHashMap<>();

		Iterator<Node> iterator = actualTree.iterator();

		while (iterator.hasNext()) {
			Node node = iterator.next();

			actualMap.put(
				unsafeFunction.apply(node),
				TransformUtil.transformToArray(
					node.getChildNodes(), unsafeFunction, String.class));
		}

		Assert.assertEquals(
			actualMap.toString(), expectedMap.size(), actualMap.size());

		for (Map.Entry<String, String[]> entry : expectedMap.entrySet()) {
			String[] expectedValues = entry.getValue();
			String[] actualValues = actualMap.get(entry.getKey());

			if ((expectedValues.length == 0) && (actualValues.length == 0)) {
				continue;
			}

			Assert.assertEquals(
				Arrays.toString(actualValues), expectedValues.length,
				actualValues.length);
			Assert.assertTrue(
				ArrayUtil.containsAll(expectedValues, actualValues));
		}
	}

	private static String _getExternalReferenceCode(
			Node node, ObjectEntryLocalService objectEntryLocalService)
		throws PortalException {

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			node.getPrimaryKey());

		return objectEntry.getExternalReferenceCode();
	}

	private static String _getShortName(
			Node node,
			ObjectDefinitionLocalService objectDefinitionLocalService)
		throws PortalException {

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				node.getPrimaryKey());

		return objectDefinition.getShortName();
	}

}