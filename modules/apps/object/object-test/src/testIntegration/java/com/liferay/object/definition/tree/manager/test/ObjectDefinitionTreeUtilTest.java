/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.tree.manager.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationRegistryUtil;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.definition.tree.util.ObjectDefinitionTreeUtil;
import com.liferay.object.exception.ObjectRelationshipEdgeException;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectActionTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Feliphe Marinho
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@FeatureFlag("LPD-34594")
@RunWith(Arquillian.class)
public class ObjectDefinitionTreeUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_objectDefinitionTreeFactory = new ObjectDefinitionTreeFactory(
			_objectDefinitionLocalService, _objectRelationshipLocalService);

		_objectDefinitionA = _addAndPublishCustomObjectDefinition();
		_objectDefinitionAA = _addAndPublishCustomObjectDefinition();

		_objectRelationshipA_AA =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinitionA,
				_objectDefinitionAA);

		_objectRelationshipA_AAObjectField2 =
			_objectFieldLocalService.fetchObjectField(
				_objectRelationshipA_AA.getObjectFieldId2());

		_objectDefinitionAB = _addAndPublishCustomObjectDefinition();

		_objectRelationshipA_AB =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinitionA,
				_objectDefinitionAB);

		_objectDefinitionAAA = _addAndPublishCustomObjectDefinition();

		_objectRelationshipAA_AAA =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinitionAA,
				_objectDefinitionAAA);

		_objectRelationshipAA_AAAObjectField2 =
			_objectFieldLocalService.fetchObjectField(
				_objectRelationshipAA_AAA.getObjectFieldId2());

		_objectDefinitionAAAA = _addAndPublishCustomObjectDefinition();

		_objectRelationshipAAA_AAAA =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinitionAAA,
				_objectDefinitionAAAA);

		_objectDefinitionB = _addAndPublishCustomObjectDefinition();

		_objectRelationshipB_AA =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinitionB,
				_objectDefinitionAA);

		_objectRelationshipB_AAObjectField2 =
			_objectFieldLocalService.fetchObjectField(
				_objectRelationshipB_AA.getObjectFieldId2());
	}

	@After
	public void tearDown() {
		ObjectDefinitionTreeUtil.invalidate();
	}

	@Test
	public void testBindDraftObjectDefinitionAndPublishedObjectDefinition()
		throws Exception {

		// Bind a draft object definition as a child node in a published object
		// definition tree

		ObjectDefinition objectDefinitionAA =
			_addAndPublishCustomObjectDefinition("AA");
		ObjectDefinition objectDefinitionAAA =
			_addAndPublishCustomObjectDefinition("AAA");

		TreeTestUtil.bind(
			objectDefinitionAA.getObjectDefinitionId(),
			objectDefinitionAAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_testBindObjectDefinitions(
			objectDefinitionAAA,
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAAA"),
			(objectDefinition1, objectDefinition2) -> {
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getRootObjectDefinitionId()),
					_objectDefinitionLocalService);
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition2.getObjectDefinitionId()),
					_objectDefinitionLocalService);
			});

		_assertScreenNavigationCategories(2, "C_AA");
		_assertScreenNavigationCategories(1, "C_AAA");
		_assertScreenNavigationCategories(0, "C_AAAA");

		// Bind a draft object definition as a parent node in a published
		// object definition tree

		_testBindObjectDefinitions(
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A"),
			objectDefinitionAA,
			(objectDefinition1, objectDefinition2) -> {
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getObjectDefinitionId()),
					_objectDefinitionLocalService);
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition2.getObjectDefinitionId()),
					_objectDefinitionLocalService);
			});

		_assertScreenNavigationCategories(0, "C_A");
		_assertScreenNavigationCategories(2, "C_AA");
		_assertScreenNavigationCategories(1, "C_AAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind a draft object definition to a published object definition

		_testBindObjectDefinitions(
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A"),
			_addAndPublishCustomObjectDefinition("AA"),
			(objectDefinition1, objectDefinition2) -> {
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getObjectDefinitionId()),
					_objectDefinitionLocalService);
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition2.getObjectDefinitionId()),
					_objectDefinitionLocalService);
			});

		_assertScreenNavigationCategories(0, "C_A");
		_assertScreenNavigationCategories(1, "C_AA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind a draft object definition to a published object definition
		// with object entries

		objectDefinitionAA = _addAndPublishCustomObjectDefinition("AA");

		_addObjectEntry(objectDefinitionAA, Collections.emptyMap());

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A");

		_testBindObjectDefinitions(
			objectDefinitionA, objectDefinitionAA,
			(objectDefinition1, objectDefinition2) -> {
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getObjectDefinitionId()),
					_objectDefinitionLocalService);
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition2.getObjectDefinitionId()),
					_objectDefinitionLocalService);
			});

		_assertScreenNavigationCategories(0, "C_A");
		_assertScreenNavigationCategories(1, "C_AA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind a draft object definition tree to a published object definition
		// tree

		objectDefinitionA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"A");
		objectDefinitionAA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"AA");

		TreeTestUtil.bind(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		objectDefinitionAAA = _addAndPublishCustomObjectDefinition("AAA");
		ObjectDefinition objectDefinitionAAAA =
			_addAndPublishCustomObjectDefinition("AAAA");

		TreeTestUtil.bind(
			objectDefinitionAAA.getObjectDefinitionId(),
			objectDefinitionAAAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_testBindObjectDefinitions(
			objectDefinitionAA, objectDefinitionAAA,
			(objectDefinition1, objectDefinition2) -> {
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[] {"AA"}
					).put(
						"AA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getRootObjectDefinitionId()),
					_objectDefinitionLocalService);
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AAA", new String[] {"AAAA"}
					).put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition2.getRootObjectDefinitionId()),
					_objectDefinitionLocalService);
			});

		_assertScreenNavigationCategories(0, "C_A");
		_assertScreenNavigationCategories(0, "C_AA");
		_assertScreenNavigationCategories(2, "C_AAA");
		_assertScreenNavigationCategories(1, "C_AAAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind a published object definition to a draft object definition tree

		objectDefinitionA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"A");
		objectDefinitionAA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"AA");

		TreeTestUtil.bind(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_testBindObjectDefinitions(
			objectDefinitionAA, _addAndPublishCustomObjectDefinition("AAA"),
			(objectDefinition1, objectDefinition2) -> {
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[] {"AA"}
					).put(
						"AA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getRootObjectDefinitionId()),
					_objectDefinitionLocalService);
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition2.getObjectDefinitionId()),
					_objectDefinitionLocalService);
			});

		_assertScreenNavigationCategories(0, "C_A");
		_assertScreenNavigationCategories(0, "C_AA");
		_assertScreenNavigationCategories(1, "C_AAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA"}, _objectEntryLocalService,
			_objectRelationshipLocalService);
	}

	@Test
	public void testBindDraftObjectDefinitions() throws Exception {

		// Bind a draft object definition as a child node in a draft object
		// definition tree

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build());

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		_testBindObjectDefinitions(
			objectDefinitionAAA,
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAAA"),
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[] {"AAAA"}
					).put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getRootObjectDefinitionId()),
					_objectDefinitionLocalService));

		// Bind a draft object definition as a parent node in a draft object
		// definition tree

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		_testBindObjectDefinitions(
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A"),
			objectDefinitionAA,
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[] {"AA"}
					).put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[] {"AAAA"}
					).put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getObjectDefinitionId()),
					_objectDefinitionLocalService));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind two draft object definition trees

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A");
		objectDefinitionAA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"AA");

		TreeTestUtil.bind(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		objectDefinitionAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAA");
		ObjectDefinition objectDefinitionAAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAAA");

		TreeTestUtil.bind(
			objectDefinitionAAA.getObjectDefinitionId(),
			objectDefinitionAAAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_testBindObjectDefinitions(
			objectDefinitionAA, objectDefinitionAAA,
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[] {"AA"}
					).put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[] {"AAAA"}
					).put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getRootObjectDefinitionId()),
					_objectDefinitionLocalService));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind two draft object definitions

		_testBindObjectDefinitions(
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A"),
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA"),
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[] {"AA"}
					).put(
						"AA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getObjectDefinitionId()),
					_objectDefinitionLocalService));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind two nonroot draft object definitions from different object
		// definition trees

		Tree treeA = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());
		Tree treeB = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"B", new String[] {"BB"}
			).put(
				"BB", new String[] {"BBB"}
			).put(
				"BBB", new String[0]
			).build());

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AA"),
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_BB"))));

		Node rootNodeA = treeA.getRootNode();

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"BB"}
			).put(
				"BB", new String[] {"BBB"}
			).put(
				"BBB", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(rootNodeA.getPrimaryKey()),
			_objectDefinitionLocalService);

		Node rootNodeB = treeB.getRootNode();

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"B", new String[] {"BB"}
			).put(
				"BB", new String[] {"BBB"}
			).put(
				"BBB", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(rootNodeB.getPrimaryKey()),
			_objectDefinitionLocalService);
	}

	@Test
	public void testBindObjectDefinitionsWithGreaterThanTreeMaxHeight()
		throws Exception {

		// Bind an object definition to a tree that has reached the maximum
		// height

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[] {"AAAA"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build());

		ObjectDefinition objectDefinitionAAAAA =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAAA");
		ObjectDefinition objectDefinitionAAAAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAAAAA");

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"The object relationship cannot be an edge in the root context " +
				"because it would exceed the tree's maximum height",
			() -> TreeTestUtil.bind(
				objectDefinitionAAAAA.getObjectDefinitionId(),
				objectDefinitionAAAAAA.getObjectDefinitionId(),
				_objectRelationshipLocalService));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinitionAAAAAA);

		// Bind object definition trees into one so that the height
		// of the new tree exceeds the maximum height

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build());

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[] {"AAAAAA"}
			).put(
				"AAAAAA", new String[0]
			).build());

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");
		ObjectDefinition objectDefinitionAAAA =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAA");

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"The object relationship cannot be an edge in the root context " +
				"because it would exceed the tree's maximum height",
			() -> TreeTestUtil.bind(
				objectDefinitionAAA.getObjectDefinitionId(),
				objectDefinitionAAAA.getObjectDefinitionId(),
				_objectRelationshipLocalService));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA", "C_AAAAAA"
			},
			_objectEntryLocalService, _objectRelationshipLocalService);

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"B", new String[] {"BB"}
			).put(
				"BB", new String[] {"BBB"}
			).put(
				"BBB", new String[] {"BBBB"}
			).put(
				"BBBB", new String[] {"BBBBB"}
			).put(
				"BBBBB", new String[0]
			).build());

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"D", new String[] {"DD"}
			).put(
				"DD", new String[] {"DDD"}
			).put(
				"DDD", new String[0]
			).build());

		ObjectDefinition objectDefinitionBBB =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_BBB");
		ObjectDefinition objectDefinitionDDD =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_DDD");

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"The object relationship cannot be an edge in the root context " +
				"because it would exceed the tree's maximum height",
			() -> TreeTestUtil.bind(
				objectDefinitionDDD.getObjectDefinitionId(),
				objectDefinitionBBB.getObjectDefinitionId(),
				_objectRelationshipLocalService));

		ObjectDefinition objectDefinitionDD =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_DD");

		TreeTestUtil.bind(
			objectDefinitionDD.getObjectDefinitionId(),
			objectDefinitionBBB.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"F", new String[] {"FF"}
			).put(
				"FF", new String[] {"FFF"}
			).put(
				"FFF", new String[0]
			).build());

		ObjectDefinition objectDefinitionF =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_F");

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"The object relationship cannot be an edge in the root context " +
				"because it would exceed the tree's maximum height",
			() -> TreeTestUtil.bind(
				objectDefinitionBBB.getObjectDefinitionId(),
				objectDefinitionF.getObjectDefinitionId(),
				_objectRelationshipLocalService));

		ObjectDefinition objectDefinitionFF =
			_objectDefinitionLocalService.fetchObjectDefinition(
				TestPropsValues.getCompanyId(), "C_FF");

		TreeTestUtil.bind(
			objectDefinitionBBB.getObjectDefinitionId(),
			objectDefinitionFF.getObjectDefinitionId(),
			_objectRelationshipLocalService);
	}

	@Test
	public void testBindObjectDefinitionsWithObjectEntries() throws Exception {
		ObjectEntry objectEntryA1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinitionA.getObjectDefinitionId(), Map.of());

		ObjectEntry objectEntryAA1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinitionAA.getObjectDefinitionId(),
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA1.getObjectEntryId()));

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, 0L
			).put(
				objectEntryAA1, 0L
			).build());

		TreeTestUtil.bind(
			_objectRelationshipLocalService, List.of(_objectRelationshipA_AA));

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA1.getObjectEntryId()
			).build());
	}

	@Test
	public void testBindObjectDefinitionsWithOngoingWorkflowInstances()
		throws Exception {

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinitionA.getClassName(), 0, 0, "Single Approver", 1);

		ObjectEntry objectEntryA1 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinitionA.getObjectDefinitionId(), Map.of());
		ObjectEntry objectEntryA2 = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinitionA.getObjectDefinitionId(), Map.of());

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinitionAA.getClassName(), 0, 0, "Single Approver", 1);

		ObjectEntry objectEntryAA = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinitionAA.getObjectDefinitionId(),
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA1.getObjectEntryId()));

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			String.format(
				"These ongoing workflow instances must be completed to " +
					"enable inheritance: \"%s\" (\"%s\" object entries) and " +
						"\"%s\" (\"%s\" object entries)",
				_objectDefinitionA.getLabel(LocaleUtil.US), 2,
				_objectDefinitionAA.getLabel(LocaleUtil.US), 1),
			() -> TreeTestUtil.bind(
				_objectRelationshipLocalService,
				List.of(_objectRelationshipA_AA)));

		_completeWorkflowTask(
			_objectDefinitionA.getClassName(),
			objectEntryA1.getObjectEntryId());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			String.format(
				"These ongoing workflow instances must be completed to " +
					"enable inheritance: \"%s\" (\"%s\" object entries) and " +
						"\"%s\" (\"%s\" object entries)",
				_objectDefinitionA.getLabel(LocaleUtil.US), 1,
				_objectDefinitionAA.getLabel(LocaleUtil.US), 1),
			() -> TreeTestUtil.bind(
				_objectRelationshipLocalService,
				List.of(_objectRelationshipA_AA)));

		_completeWorkflowTask(
			_objectDefinitionA.getClassName(),
			objectEntryA2.getObjectEntryId());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			String.format(
				"These ongoing workflow instances must be completed to " +
					"enable inheritance: \"%s\" (\"%s\" object entries)",
				_objectDefinitionAA.getLabel(LocaleUtil.US), 1),
			() -> TreeTestUtil.bind(
				_objectRelationshipLocalService,
				List.of(_objectRelationshipA_AA)));

		_completeWorkflowTask(
			_objectDefinitionAA.getClassName(),
			objectEntryAA.getObjectEntryId());

		TreeTestUtil.bind(
			_objectRelationshipLocalService, List.of(_objectRelationshipA_AA));

		objectEntryAA = _objectEntryLocalService.getObjectEntry(
			objectEntryAA.getObjectEntryId());

		Assert.assertEquals(
			objectEntryA1.getObjectEntryId(),
			objectEntryAA.getRootObjectEntryId());
	}

	@Test
	public void testBindPublishedObjectDefinitions() throws Exception {

		// Bind a published object definition as a child node in a published
		// object definition tree

		ObjectDefinition objectDefinitionAA =
			_addAndPublishCustomObjectDefinition("AA");
		ObjectDefinition objectDefinitionAAA =
			_addAndPublishCustomObjectDefinition("AAA");

		TreeTestUtil.bind(
			objectDefinitionAA.getObjectDefinitionId(),
			objectDefinitionAAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		ObjectDefinition objectDefinitionAAAA =
			_addAndPublishCustomObjectDefinition("AAAA");

		_testBindObjectDefinitions(
			objectDefinitionAAA, objectDefinitionAAAA,
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[] {"AAAA"}
					).put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getRootObjectDefinitionId()),
					_objectDefinitionLocalService));

		_assertScreenNavigationCategories(2, "C_AA");
		_assertScreenNavigationCategories(2, "C_AAA");
		_assertScreenNavigationCategories(1, "C_AAAA");

		// Bind a published object definition as a parent node in a published
		// object definition tree

		_testBindObjectDefinitions(
			_addAndPublishCustomObjectDefinition("A"), objectDefinitionAA,
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[] {"AA"}
					).put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[] {"AAAA"}
					).put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getObjectDefinitionId()),
					_objectDefinitionLocalService));

		_assertScreenNavigationCategories(2, "C_A");
		_assertScreenNavigationCategories(2, "C_AA");
		_assertScreenNavigationCategories(2, "C_AAA");
		_assertScreenNavigationCategories(1, "C_AAAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind two published object definition trees

		ObjectDefinition objectDefinitionA =
			_addAndPublishCustomObjectDefinition("A");
		objectDefinitionAA = _addAndPublishCustomObjectDefinition("AA");

		TreeTestUtil.bind(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		objectDefinitionAAA = _addAndPublishCustomObjectDefinition("AAA");
		objectDefinitionAAAA = _addAndPublishCustomObjectDefinition("AAAA");

		TreeTestUtil.bind(
			objectDefinitionAAA.getObjectDefinitionId(),
			objectDefinitionAAAA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		_testBindObjectDefinitions(
			objectDefinitionAA, objectDefinitionAAA,
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"A", new String[] {"AA"}
					).put(
						"AA", new String[] {"AAA"}
					).put(
						"AAA", new String[] {"AAAA"}
					).put(
						"AAAA", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getRootObjectDefinitionId()),
					_objectDefinitionLocalService));

		_assertScreenNavigationCategories(2, "C_A");
		_assertScreenNavigationCategories(2, "C_AA");
		_assertScreenNavigationCategories(2, "C_AAA");
		_assertScreenNavigationCategories(1, "C_AAAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind two published object definitions

		ObjectDefinition objectDefinitionB =
			_addAndPublishCustomObjectDefinition("B");
		ObjectDefinition objectDefinitionBB =
			_addAndPublishCustomObjectDefinition("BB");

		_testBindObjectDefinitions(
			objectDefinitionB, objectDefinitionBB,
			(objectDefinition1, objectDefinition2) ->
				TreeTestUtil.assertObjectDefinitionTree(
					LinkedHashMapBuilder.put(
						"B", new String[] {"BB"}
					).put(
						"BB", new String[0]
					).build(),
					_objectDefinitionTreeFactory.create(
						objectDefinition1.getObjectDefinitionId()),
					_objectDefinitionLocalService));

		_assertScreenNavigationCategories(2, "C_B");
		_assertScreenNavigationCategories(1, "C_BB");

		// Object definitions must have the same scope to enable inheritance

		ObjectDefinition siteObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_SITE);

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			String.format(
				"The scope of \"%s\" is not the same as \"%s\". To enable " +
					"inheritance, the object definitions must have the same " +
						"scope",
				objectDefinitionB.getShortName(),
				siteObjectDefinition.getShortName()),
			() -> TreeTestUtil.bind(
				objectDefinitionB.getObjectDefinitionId(),
				siteObjectDefinition.getObjectDefinitionId(),
				_objectRelationshipLocalService));

		// Unable to bind the object definitions because the object relationship
		// must not create a circular reference in a root context

		ObjectDefinition objectDefinitionBBB =
			_addAndPublishCustomObjectDefinition("BBB");

		TreeTestUtil.bind(
			objectDefinitionBB.getObjectDefinitionId(),
			objectDefinitionBBB.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"The object relationship must not create a circular reference in " +
				"a root context",
			() -> TreeTestUtil.bind(
				objectDefinitionBBB.getObjectDefinitionId(),
				objectDefinitionB.getObjectDefinitionId(),
				_objectRelationshipLocalService));

		ObjectDefinition objectDefinitionC =
			_addAndPublishCustomObjectDefinition("C");
		ObjectDefinition objectDefinitionCC =
			_addAndPublishCustomObjectDefinition("CC");

		TreeTestUtil.bind(
			objectDefinitionC.getObjectDefinitionId(),
			objectDefinitionCC.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_B", "C_BB", "C_BBB"}, _objectEntryLocalService,
			_objectRelationshipLocalService);
		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_C", "C_CC"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testUnbindObjectDefinitions() throws Exception {
		TreeTestUtil.bind(
			_objectRelationshipLocalService, List.of(_objectRelationshipA_AA));

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA, new ObjectDefinition[] {_objectDefinitionA}
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipA_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[0]
			).put(
				_objectDefinitionAA, new ObjectDefinition[0]
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithDescendantNode()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(_objectRelationshipA_AA, _objectRelationshipAA_AAA));

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAAA,
				new ObjectDefinition[] {_objectDefinitionA}
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipA_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[0]
			).put(
				_objectDefinitionAA,
				new ObjectDefinition[] {_objectDefinitionAA}
			).put(
				_objectDefinitionAAA,
				new ObjectDefinition[] {_objectDefinitionAA}
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithDescendantNodeAndGrandParentNodes()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(
				_objectRelationshipA_AA, _objectRelationshipAA_AAA,
				_objectRelationshipAAA_AAAA, _objectRelationshipB_AA));

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionAAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionAAAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionB, new ObjectDefinition[] {_objectDefinitionB}
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipAA_AAA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionAAA,
				new ObjectDefinition[] {_objectDefinitionAAA}
			).put(
				_objectDefinitionAAAA,
				new ObjectDefinition[] {_objectDefinitionAAA}
			).put(
				_objectDefinitionB, new ObjectDefinition[] {_objectDefinitionB}
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithDescendantNodeAndParentNodes()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(
				_objectRelationshipA_AA, _objectRelationshipAA_AAA,
				_objectRelationshipB_AA));

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionAAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionB, new ObjectDefinition[] {_objectDefinitionB}
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipB_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAAA,
				new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionB, new ObjectDefinition[0]
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithGrandParentNodes()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(
				_objectRelationshipA_AA, _objectRelationshipAA_AAA,
				_objectRelationshipB_AA));

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionAAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionB, new ObjectDefinition[] {_objectDefinitionB}
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipAA_AAA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionAAA, new ObjectDefinition[0]
			).put(
				_objectDefinitionB, new ObjectDefinition[] {_objectDefinitionB}
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithObjectAction() throws Exception {
		Tree tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build());

		_testUnbindObjectDefinitionsWithObjectAction("AA", "A", tree);
		_testUnbindObjectDefinitionsWithObjectAction("AAA", "AA", tree);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA"}, _objectEntryLocalService,
			_objectRelationshipLocalService);
	}

	@Test
	public void testUnbindObjectDefinitionsWithObjectEntries()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService, List.of(_objectRelationshipA_AA));

		ObjectEntry objectEntryA1 = _addObjectEntry(
			_objectDefinitionA, Map.of());
		ObjectEntry objectEntryA2 = _addObjectEntry(
			_objectDefinitionA, Map.of());

		ObjectEntry objectEntryAA1 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA1.getObjectEntryId()));
		ObjectEntry objectEntryAA2 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA2.getObjectEntryId()));
		ObjectEntry objectEntryAA3 = _addObjectEntry(
			_objectDefinitionAA, Map.of());

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryAA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA3, 0L
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipA_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, 0L
			).put(
				objectEntryA2, 0L
			).put(
				objectEntryAA1, 0L
			).put(
				objectEntryAA2, 0L
			).put(
				objectEntryAA3, 0L
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithObjectEntriesWithDescendantAndParentNodes()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(
				_objectRelationshipA_AA, _objectRelationshipAA_AAA,
				_objectRelationshipB_AA));

		ObjectEntry objectEntryA = _addObjectEntry(
			_objectDefinitionA, Map.of());

		ObjectEntry objectEntryAA1 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA.getObjectEntryId()));

		ObjectEntry objectEntryB = _addObjectEntry(
			_objectDefinitionB, Map.of());

		ObjectEntry objectEntryAA2 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipB_AAObjectField2.getName(),
				objectEntryB.getObjectEntryId()));

		ObjectEntry objectEntryAA3 = _addObjectEntry(
			_objectDefinitionAA, Map.of());

		ObjectEntry objectEntryAAA1 = _addObjectEntry(
			_objectDefinitionAAA,
			Map.of(
				_objectRelationshipAA_AAAObjectField2.getName(),
				objectEntryAA1.getObjectEntryId()));
		ObjectEntry objectEntryAAA2 = _addObjectEntry(
			_objectDefinitionAAA,
			Map.of(
				_objectRelationshipAA_AAAObjectField2.getName(),
				objectEntryAA2.getObjectEntryId()));
		ObjectEntry objectEntryAAA3 = _addObjectEntry(
			_objectDefinitionAAA, Map.of());

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA2, objectEntryB.getObjectEntryId()
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryAAA1, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAAA2, objectEntryB.getObjectEntryId()
			).put(
				objectEntryAAA3, 0L
			).put(
				objectEntryB, objectEntryB.getObjectEntryId()
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipB_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA2, 0L
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryAAA1, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAAA2, 0L
			).put(
				objectEntryAAA3, 0L
			).put(
				objectEntryB, 0L
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithObjectEntriesWithDescendantNode()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(_objectRelationshipA_AA, _objectRelationshipAA_AAA));

		ObjectEntry objectEntryA1 = _addObjectEntry(
			_objectDefinitionA, Map.of());
		ObjectEntry objectEntryA2 = _addObjectEntry(
			_objectDefinitionA, Map.of());

		ObjectEntry objectEntryAA1 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA1.getObjectEntryId()));
		ObjectEntry objectEntryAA2 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA2.getObjectEntryId()));
		ObjectEntry objectEntryAA3 = _addObjectEntry(
			_objectDefinitionAA, Map.of());

		ObjectEntry objectEntryAAA1 = _addObjectEntry(
			_objectDefinitionAAA,
			Map.of(
				_objectRelationshipAA_AAAObjectField2.getName(),
				objectEntryAA1.getObjectEntryId()));
		ObjectEntry objectEntryAAA2 = _addObjectEntry(
			_objectDefinitionAAA,
			Map.of(
				_objectRelationshipAA_AAAObjectField2.getName(),
				objectEntryAA2.getObjectEntryId()));
		ObjectEntry objectEntryAAA3 = _addObjectEntry(
			_objectDefinitionAAA, Map.of());

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryAA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryAAA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryAAA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAAA3, 0L
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipA_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, 0L
			).put(
				objectEntryA2, 0L
			).put(
				objectEntryAA1, objectEntryAA1.getObjectEntryId()
			).put(
				objectEntryAA2, objectEntryAA2.getObjectEntryId()
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryAAA1, objectEntryAA1.getObjectEntryId()
			).put(
				objectEntryAAA2, objectEntryAA2.getObjectEntryId()
			).put(
				objectEntryAAA3, 0L
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithObjectEntriesWithParentNode()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(_objectRelationshipA_AA, _objectRelationshipAA_AAA));

		ObjectEntry objectEntryA1 = _addObjectEntry(
			_objectDefinitionA, Map.of());
		ObjectEntry objectEntryA2 = _addObjectEntry(
			_objectDefinitionA, Map.of());

		ObjectEntry objectEntryAA1 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA1.getObjectEntryId()));
		ObjectEntry objectEntryAA2 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA2.getObjectEntryId()));
		ObjectEntry objectEntryAA3 = _addObjectEntry(
			_objectDefinitionAA, Map.of());

		ObjectEntry objectEntryAAA1 = _addObjectEntry(
			_objectDefinitionAAA,
			Map.of(
				_objectRelationshipAA_AAAObjectField2.getName(),
				objectEntryAA1.getObjectEntryId()));
		ObjectEntry objectEntryAAA2 = _addObjectEntry(
			_objectDefinitionAAA,
			Map.of(
				_objectRelationshipAA_AAAObjectField2.getName(),
				objectEntryAA2.getObjectEntryId()));
		ObjectEntry objectEntryAAA3 = _addObjectEntry(
			_objectDefinitionAAA, Map.of());

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryAA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryAAA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryAAA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAAA3, 0L
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipAA_AAA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA1.getObjectEntryId()
			).put(
				objectEntryAA2, objectEntryA2.getObjectEntryId()
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryAAA1, 0L
			).put(
				objectEntryAAA2, 0L
			).put(
				objectEntryAAA3, 0L
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithObjectEntriesWithParentNodes()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(_objectRelationshipA_AA, _objectRelationshipB_AA));

		ObjectEntry objectEntryA = _addObjectEntry(
			_objectDefinitionA, Map.of());

		ObjectEntry objectEntryAA1 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipA_AAObjectField2.getName(),
				objectEntryA.getObjectEntryId()));

		ObjectEntry objectEntryB = _addObjectEntry(
			_objectDefinitionB, Map.of());

		ObjectEntry objectEntryAA2 = _addObjectEntry(
			_objectDefinitionAA,
			Map.of(
				_objectRelationshipB_AAObjectField2.getName(),
				objectEntryB.getObjectEntryId()));

		ObjectEntry objectEntryAA3 = _addObjectEntry(
			_objectDefinitionAA, Map.of());

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA2, objectEntryB.getObjectEntryId()
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryB, objectEntryB.getObjectEntryId()
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipB_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		_assertRootObjectEntryIds(
			LinkedHashMapBuilder.put(
				objectEntryA, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA1, objectEntryA.getObjectEntryId()
			).put(
				objectEntryAA2, 0L
			).put(
				objectEntryAA3, 0L
			).put(
				objectEntryB, 0L
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithOngoingWorkflowInstances()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService, List.of(_objectRelationshipA_AA));

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinitionA.getClassName(), 0, 0, "Single Approver", 1);

		Tree objectEntryTree1 = TreeTestUtil.createObjectEntryTree(
			"1", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			_objectDefinitionA.getObjectDefinitionId());
		Tree objectEntryTree2 = TreeTestUtil.createObjectEntryTree(
			"2", _objectDefinitionLocalService, _objectEntryLocalService,
			_objectFieldLocalService, _objectRelationshipLocalService,
			_objectDefinitionA.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			String.format(
				"These ongoing workflow instances must be completed to " +
					"disable inheritance: \"%s\" (\"%s\" object entries)",
				_objectDefinitionA.getLabel(LocaleUtil.US), 2),
			() -> TreeTestUtil.unbind(
				_objectRelationshipA_AA, _objectRelationshipLocalService));

		Node rootNode1 = objectEntryTree1.getRootNode();

		_completeWorkflowTask(
			_objectDefinitionA.getClassName(), rootNode1.getPrimaryKey());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			String.format(
				"These ongoing workflow instances must be completed to " +
					"disable inheritance: \"%s\" (\"%s\" object entries)",
				_objectDefinitionA.getLabel(LocaleUtil.US), 1),
			() -> TreeTestUtil.unbind(
				_objectRelationshipA_AA, _objectRelationshipLocalService));

		Node rootNode2 = objectEntryTree2.getRootNode();

		_completeWorkflowTask(
			_objectDefinitionA.getClassName(), rootNode2.getPrimaryKey());

		TreeTestUtil.unbind(
			_objectRelationshipA_AA, _objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[0]
			).put(
				_objectDefinitionAA, new ObjectDefinition[0]
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithParentNodes() throws Exception {
		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(_objectRelationshipA_AA, _objectRelationshipB_AA));

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA,
				new ObjectDefinition[] {_objectDefinitionA, _objectDefinitionB}
			).put(
				_objectDefinitionB, new ObjectDefinition[] {_objectDefinitionB}
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipB_AA.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionB, new ObjectDefinition[0]
			).build());
	}

	@Test
	public void testUnbindObjectDefinitionsWithResourcePermissions()
		throws Exception {

		TreeTestUtil.bind(
			_objectRelationshipLocalService, List.of(_objectRelationshipA_AA));

		Role organizationRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_ORGANIZATION);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), _objectDefinitionA.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			organizationRole.getRoleId(), new String[] {ActionKeys.UPDATE});

		Role regularRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), _objectDefinitionA.getPortletId(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			regularRole.getRoleId(),
			new String[] {ActionKeys.ACCESS_IN_CONTROL_PANEL});

		Role siteRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			_objectDefinitionA.getResourceName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			siteRole.getRoleId(),
			new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY});

		ObjectAction objectAction = ObjectActionTestUtil.addObjectAction(
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE, _objectDefinitionAA,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build());

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), _objectDefinitionAA.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			organizationRole.getRoleId(),
			new String[] {objectAction.getName()});

		TreeTestUtil.unbind(
			_objectRelationshipA_AA, _objectRelationshipLocalService);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				_objectDefinitionAA.getClassName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				organizationRole.getRoleId(), ActionKeys.UPDATE));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				_objectDefinitionAA.getPortletId(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				regularRole.getRoleId(), ActionKeys.ACCESS_IN_CONTROL_PANEL));
		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				_objectDefinitionAA.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				siteRole.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY));

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				_objectDefinitionAA.getClassName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				organizationRole.getRoleId(), objectAction.getName()));
	}

	@Test
	public void testUnbindObjectDefinitionsWithSiblingNode() throws Exception {
		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(_objectRelationshipA_AA, _objectRelationshipA_AB));

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAB, new ObjectDefinition[] {_objectDefinitionA}
			).build());

		TreeTestUtil.unbind(
			_objectRelationshipLocalService.getObjectRelationship(
				_objectRelationshipA_AB.getObjectRelationshipId()),
			_objectRelationshipLocalService);

		TreeTestUtil.assertRootObjectDefinitionIds(
			LinkedHashMapBuilder.put(
				_objectDefinitionA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAA, new ObjectDefinition[] {_objectDefinitionA}
			).put(
				_objectDefinitionAB, new ObjectDefinition[0]
			).build());
	}

	@Test
	public void testUpdateNodeObjectDefinition() throws Exception {
		ObjectDefinition objectDefinitionA =
			_addAndPublishCustomObjectDefinition("A");

		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA");

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA)));

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testUpdateNodeObjectDefinitionWithDescendantNodes()
		throws Exception {

		// Update a node with draft descendant nodes

		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());
		_testCreateObjectDefinitionTree(
			false,
			LinkedHashMapBuilder.put(
				"AAA", new String[] {"AAAA", "AAAB"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAB", new String[] {"AAABA"}
			).put(
				"AAAAA", new String[0]
			).put(
				"AAABA", new String[0]
			).build());

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AA"),
					objectDefinitionAAA)));

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAA", new String[] {"AAAA", "AAAB"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAB", new String[] {"AAABA"}
			).put(
				"AAAAA", new String[0]
			).put(
				"AAABA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAAA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAA");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAAB =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAB");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAAB", new String[] {"AAABA"}
			).put(
				"AAABA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAAB.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAB", "C_AAAAA", "C_AAABA"
			},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Update a node with published descendant nodes

		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());

		_addObjectAction("C_AA");

		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build());

		_updateWorkflowDefinitionLink("C_AAAA");

		_addObjectAction("C_AAAAA");

		objectDefinitionAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAA");

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Arrays.asList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AA"),
					objectDefinitionAAA),
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionAAA,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AAAA"))));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAAA.getObjectDefinitionId());

		objectDefinitionA = _objectDefinitionLocalService.getObjectDefinition(
			TestPropsValues.getCompanyId(), "C_A");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[] {"AAAA"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_updateWorkflowDefinitionLink("C_A");

		TreeTestUtil.unbind(
			objectDefinitionA.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		Assert.assertTrue(
			ListUtil.isEmpty(
				_workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
					objectDefinitionAA.getCompanyId(),
					objectDefinitionAA.getClassName())));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Update a node with published and draft descendant nodes

		Tree treeA = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());
		Tree treeB = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"B", new String[] {"BB"}
			).put(
				"BB", new String[0]
			).build());
		Tree treeC = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"C", new String[] {"CC"}
			).put(
				"CC", new String[0]
			).build());
		Tree treeD = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			false,
			LinkedHashMapBuilder.put(
				"D", new String[] {"DD"}
			).put(
				"DD", new String[0]
			).build());

		ObjectDefinition objectDefinitionDD =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_DD");

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionDD.getObjectDefinitionId());

		Node rootNodeD = treeD.getRootNode();

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"D", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(rootNodeD.getPrimaryKey()),
			_objectDefinitionLocalService);

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"DD", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionDD.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			List.of(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AA"),
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_D")),
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_BB"),
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_D")),
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_CC"),
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_D"))));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(), rootNodeD.getPrimaryKey());

		Node rootNodeA = treeA.getRootNode();

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"D"}
			).put(
				"D", new String[] {"DD"}
			).put(
				"DD", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(rootNodeA.getPrimaryKey()),
			_objectDefinitionLocalService);

		Node rootNodeB = treeB.getRootNode();

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"B", new String[] {"BB"}
			).put(
				"BB", new String[] {"D"}
			).put(
				"D", new String[] {"DD"}
			).put(
				"DD", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(rootNodeB.getPrimaryKey()),
			_objectDefinitionLocalService);

		Node rootNodeC = treeC.getRootNode();

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"C", new String[] {"CC"}
			).put(
				"CC", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(rootNodeC.getPrimaryKey()),
			_objectDefinitionLocalService);
	}

	@Test
	public void testUpdateRootNodeObjectDefinition() throws Exception {

		// publish a draft object definition

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("A");

		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA");

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAA.getObjectDefinitionId());

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA)));

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// publish a draft object definition from a draft object definition tree

		_testCreateObjectDefinitionTree(
			false,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build());
		_testCreateObjectDefinitionTree(
			true,
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build());

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AAA"),
					_objectDefinitionLocalService.getObjectDefinition(
						TestPropsValues.getCompanyId(), "C_AAAA"))));

		objectDefinitionA = _objectDefinitionLocalService.getObjectDefinition(
			TestPropsValues.getCompanyId(), "C_A");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAAA");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinitionAAA.getObjectDefinitionId());

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AAA", new String[] {"AAAA"}
			).put(
				"AAAA", new String[] {"AAAAA"}
			).put(
				"AAAAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	private ObjectDefinition _addAndPublishCustomObjectDefinition()
		throws Exception {

		return _addAndPublishCustomObjectDefinition(
			ObjectDefinitionTestUtil.getRandomName());
	}

	private ObjectDefinition _addAndPublishCustomObjectDefinition(String name)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				0, false, name,
				List.of(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						StringUtil.randomId()
					).build()));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private void _addObjectAction(String objectDefinitionName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		_objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, null,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_STANDALONE,
			UnicodePropertiesBuilder.put(
				"secret", "standalone"
			).put(
				"url", "https://standalone.com"
			).build(),
			false);
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null, values, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertRootObjectEntryIds(Map<ObjectEntry, Long> expectedMap)
		throws Exception {

		_entityCache.clearCache();

		for (Map.Entry<ObjectEntry, Long> entry : expectedMap.entrySet()) {
			ObjectEntry objectEntry = entry.getKey();

			objectEntry = _objectEntryLocalService.getObjectEntry(
				objectEntry.getObjectEntryId());

			Assert.assertEquals(
				GetterUtil.getLong(entry.getValue()),
				objectEntry.getRootObjectEntryId());
		}
	}

	private void _assertScreenNavigationCategories(
			int expectedSize, String objectDefinitionName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		List<ScreenNavigationCategory> screenNavigationCategories =
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				objectDefinition.getClassName(), TestPropsValues.getUser(),
				null);

		Assert.assertEquals(
			screenNavigationCategories.toString(), expectedSize,
			screenNavigationCategories.size());
	}

	private void _completeWorkflowTask(String className, long classPK)
		throws Exception {

		List<WorkflowInstance> workflowInstances =
			_workflowInstanceManager.getWorkflowInstances(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				className, classPK, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		WorkflowInstance workflowInstance = workflowInstances.get(0);

		for (WorkflowTask workflowTask :
				_workflowTaskManager.getWorkflowTasksBySubmittingUser(
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			if (workflowInstance.getWorkflowInstanceId() !=
					workflowTask.getWorkflowInstanceId()) {

				continue;
			}

			workflowTask = _workflowTaskManager.assignWorkflowTaskToUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
				StringPool.BLANK, null, null);

			_workflowTaskManager.completeWorkflowTask(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), Constants.APPROVE,
				StringPool.BLANK, null);
		}
	}

	private void _testBindObjectDefinitions(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2,
			UnsafeBiConsumer<ObjectDefinition, ObjectDefinition, Exception>
				biConsumer)
		throws Exception {

		ObjectRelationship objectRelationship = TreeTestUtil.bind(
			objectDefinition1.getObjectDefinitionId(),
			objectDefinition2.getObjectDefinitionId(),
			_objectRelationshipLocalService);

		Assert.assertTrue(objectRelationship.isEdge());
		Assert.assertEquals(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			objectRelationship.getDeletionType());

		biConsumer.accept(
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition1.getObjectDefinitionId()),
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition2.getObjectDefinitionId()));
	}

	private void _testCreateObjectDefinitionTree(
			boolean published, Map<String, String[]> treeMap)
		throws Exception {

		TreeTestUtil.assertObjectDefinitionTree(
			treeMap,
			TreeTestUtil.createObjectDefinitionTree(
				_objectDefinitionLocalService, _objectRelationshipLocalService,
				published, treeMap),
			_objectDefinitionLocalService);
	}

	private void _testUnbindObjectDefinitionsWithObjectAction(
			String objectDefinitionShortName,
			String rootObjectDefinitionShortName, Tree tree)
		throws Exception {

		ObjectDefinition rootObjectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(),
				"C_" + rootObjectDefinitionShortName);

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			rootObjectDefinition.getObjectDefinitionId(), true,
			StringPool.BLANK, RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(),
			ObjectActionExecutorConstants.KEY_WEBHOOK,
			ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE,
			UnicodePropertiesBuilder.put(
				"secret", "onafterrootupdate"
			).put(
				"url", "https://onafterrootupdate.com"
			).build(),
			false);

		_unbindObjectDefinitions(objectDefinitionShortName, tree);

		objectAction = _objectActionLocalService.fetchObjectAction(
			objectAction.getObjectActionId());

		Assert.assertEquals(
			objectAction.getObjectActionTriggerKey(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE);
		Assert.assertFalse(objectAction.isActive());
	}

	private void _unbindObjectDefinitions(
			String objectDefinition2ShortName, Tree tree)
		throws Exception {

		ObjectDefinition objectDefinition2 =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(),
				"C_" + objectDefinition2ShortName);

		Node node = tree.getNode(objectDefinition2.getPrimaryKey());

		Edge edge = node.getEdge();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.getObjectRelationship(
				edge.getObjectRelationshipId());

		objectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship.getExternalReferenceCode(),
				objectRelationship.getObjectRelationshipId(), 0,
				objectRelationship.getDeletionType(), false,
				objectRelationship.getLabelMap(), null);

		Assert.assertFalse(objectRelationship.isEdge());
	}

	private void _updateWorkflowDefinitionLink(String objectDefinitionName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), objectDefinitionName);

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			objectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		List<WorkflowDefinitionLink> workflowDefinitionLinks =
			_workflowDefinitionLinkLocalService.getWorkflowDefinitionLinks(
				objectDefinition.getCompanyId(),
				objectDefinition.getClassName());

		Assert.assertEquals(
			workflowDefinitionLinks.toString(), 1,
			workflowDefinitionLinks.size());

		WorkflowDefinitionLink workflowDefinitionLink =
			workflowDefinitionLinks.get(0);

		Assert.assertEquals(
			"Single Approver",
			workflowDefinitionLink.getWorkflowDefinitionName());
	}

	@Inject
	private EntityCache _entityCache;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	private ObjectDefinition _objectDefinitionA;
	private ObjectDefinition _objectDefinitionAA;
	private ObjectDefinition _objectDefinitionAAA;
	private ObjectDefinition _objectDefinitionAAAA;
	private ObjectDefinition _objectDefinitionAB;
	private ObjectDefinition _objectDefinitionB;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ObjectDefinitionTreeFactory _objectDefinitionTreeFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private ObjectRelationship _objectRelationshipA_AA;
	private ObjectField _objectRelationshipA_AAObjectField2;
	private ObjectRelationship _objectRelationshipA_AB;
	private ObjectRelationship _objectRelationshipAA_AAA;
	private ObjectField _objectRelationshipAA_AAAObjectField2;
	private ObjectRelationship _objectRelationshipAAA_AAAA;
	private ObjectRelationship _objectRelationshipB_AA;
	private ObjectField _objectRelationshipB_AAObjectField2;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}