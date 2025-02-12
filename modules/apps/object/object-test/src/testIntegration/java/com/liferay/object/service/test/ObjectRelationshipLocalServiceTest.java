/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationRegistryUtil;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionKeys;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.DuplicateObjectRelationshipException;
import com.liferay.object.exception.ObjectDefinitionScopeException;
import com.liferay.object.exception.ObjectRelationshipDeletionTypeException;
import com.liferay.object.exception.ObjectRelationshipEdgeException;
import com.liferay.object.exception.ObjectRelationshipNameException;
import com.liferay.object.exception.ObjectRelationshipParameterObjectFieldIdException;
import com.liferay.object.exception.ObjectRelationshipReverseException;
import com.liferay.object.exception.ObjectRelationshipSystemException;
import com.liferay.object.exception.ObjectRelationshipTypeException;
import com.liferay.object.field.builder.ObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectActionLocalService;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.test.system.TestSystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.object.tree.Edge;
import com.liferay.object.tree.Node;
import com.liferay.object.tree.ObjectDefinitionTreeFactory;
import com.liferay.object.tree.Tree;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.db.IndexMetadataFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.sql.Connection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Brian Wing Shun Chan
 */
@FeatureFlags("LPD-34594")
@RunWith(Arquillian.class)
public class ObjectRelationshipLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_systemObjectDefinition1 = _addSystemObjectDefinition(
			"/o/test-endpoint/rel/{relId}/entries");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_objectDefinitionLocalService.deleteObjectDefinition(
			_systemObjectDefinition1);
	}

	@Before
	public void setUp() throws Exception {
		_modifiableSystemObjectDefinition =
			_addAndPublishModifiableSystemObjectDefinition();
		_objectDefinition1 = _addAndPublishCustomObjectDefinition();
		_objectDefinition2 = _addAndPublishCustomObjectDefinition();
		_objectDefinitionTreeFactory = new ObjectDefinitionTreeFactory(
			_objectDefinitionLocalService, _objectRelationshipLocalService);
		_systemObjectDefinition2 = _addSystemObjectDefinition(
			"/o/test-endpoint/entries");
	}

	@Test
	public void testAddObjectRelationship() throws Exception {
		//_testAddObjectRelationship(
		//	ObjectRelationshipConstants.TYPE_ONE_TO_ONE);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_objectDefinition1, _objectDefinition1, false);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_objectDefinition1, _objectDefinition1, true);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_objectDefinition1, _objectDefinition2, false);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_objectDefinition1, _objectDefinition2, true);
		_testAddObjectRelationshipOneToMany(
			_objectDefinition1, _objectDefinition1, false);
		_testAddObjectRelationshipOneToMany(
			_objectDefinition1, _objectDefinition1, true);
		_testAddObjectRelationshipOneToMany(
			_objectDefinition1, _objectDefinition2, false);
		_testAddObjectRelationshipOneToMany(
			_objectDefinition1, _objectDefinition2, true);
		_testCreateManyToManyObjectRelationshipTable(_objectDefinition1, false);
		_testCreateManyToManyObjectRelationshipTable(_objectDefinition1, true);

		ObjectDefinition depotObjectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).name(
						"a" + RandomTestUtil.randomString()
					).build()),
				ObjectDefinitionConstants.SCOPE_DEPOT);

		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"An object definition scoped by depot can only be related to " +
				"object definitions of the same scope",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				depotObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null));
		AssertUtils.assertFailure(
			ObjectDefinitionScopeException.class,
			"An object definition scoped by depot can only be related to " +
				"object definitions of the same scope",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				depotObjectDefinition.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null));

		_objectDefinitionLocalService.deleteObjectDefinition(
			depotObjectDefinition);

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY,
				null);

		AssertUtils.assertFailure(
			DuplicateObjectRelationshipException.class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", _objectDefinition1.getShortName(),
				"\""),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null));
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Inheritance between modifiable system and custom object " +
				"definitions is not allowed",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_modifiableSystemObjectDefinition.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Inheritance between modifiable system and custom object " +
				"definitions is not allowed",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_modifiableSystemObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		ObjectDefinition userObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), User.class.getName());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"System object definitions cannot inherit configurations",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_modifiableSystemObjectDefinition.getObjectDefinitionId(),
				userObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		String objectFieldName1 = "a" + RandomTestUtil.randomString();
		String objectFieldName2 = "a" + RandomTestUtil.randomString();

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				objectFieldName1
			).objectDefinitionId(
				_objectDefinition1.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());
		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				objectFieldName2
			).objectDefinitionId(
				_objectDefinition2.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		AssertUtils.assertFailure(
			ObjectRelationshipNameException.class,
			StringBundler.concat(
				"There is already an object field with this name in the ",
				"object definition \"", _objectDefinition1.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				objectFieldName1, false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipNameException.class,
			StringBundler.concat(
				"There is already an object field with this name in the ",
				"object definition \"", _objectDefinition2.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				objectFieldName2, false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipNameException.class,
			StringBundler.concat(
				"There is already an object field with this name in the ",
				"object definition \"", _objectDefinition1.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				objectFieldName1, false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipNameException.class,
			StringBundler.concat(
				"There is already an object field with this name in the ",
				"object definition \"", _objectDefinition2.getShortName(),
				".\" Object fields and object relationships cannot have the ",
				"same name."),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				objectFieldName2, false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		ObjectDefinition objectDefinition =
			_addAndPublishCustomObjectDefinition();

		AssertUtils.assertFailure(
			ObjectRelationshipNameException.class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", _objectDefinition1.getShortName(),
				".\" Parent and child object definitions cannot have the same ",
				"name."),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null));
		AssertUtils.assertFailure(
			ObjectRelationshipNameException.class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", _objectDefinition1.getShortName(),
				".\" Parent and child object definitions cannot have the same ",
				"name."),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null));

		AssertUtils.assertFailure(
			ObjectRelationshipNameException.class,
			StringBundler.concat(
				"There is already an object relationship with this name in ",
				"the object definition \"", _objectDefinition1.getShortName(),
				".\" Parent and child object definitions cannot have the same ",
				"name."),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition2.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null));

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		AssertUtils.assertFailure(
			ObjectRelationshipParameterObjectFieldIdException.class,
			"Object definition " + _objectDefinition1.getName() +
				" does not allow a parameter object field ID",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(),
				RandomTestUtil.randomLong(),
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipParameterObjectFieldIdException.class,
			"Object relationship type " +
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY +
					" does not allow a parameter object field ID",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(),
				RandomTestUtil.randomLong(),
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipSystemException.class, false,
			"Only allowed bundles can add system object relationships",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(),
				RandomTestUtil.randomLong(),
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), true,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));
	}

	@Test
	public void testAddSystemObjectRelationship() throws Exception {
		ObjectDefinition addressObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), Address.class.getName());

		AssertUtils.assertFailure(
			ObjectRelationshipTypeException.class,
			"Invalid type " + ObjectRelationshipConstants.TYPE_ONE_TO_ONE,
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_systemObjectDefinition2.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_ONE, null));
		AssertUtils.assertFailure(
			ObjectRelationshipTypeException.class,
			"Invalid type for system object definition " +
				addressObjectDefinition.getObjectDefinitionId(),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				addressObjectDefinition.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipParameterObjectFieldIdException.class,
			"Object relationship type " +
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY +
					" does not allow a parameter object field ID",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_systemObjectDefinition1.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(),
				RandomTestUtil.randomLong(),
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null));
		AssertUtils.assertFailure(
			ObjectRelationshipTypeException.class,
			"Relationships are not allowed between system objects",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_systemObjectDefinition2.getObjectDefinitionId(),
				_systemObjectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null));

		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			_objectDefinition1, _systemObjectDefinition2, false);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			_objectDefinition1, _systemObjectDefinition2, true);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			_objectDefinition1, _systemObjectDefinition2, false);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			_objectDefinition1, _systemObjectDefinition2, true);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_modifiableSystemObjectDefinition, _objectDefinition1, false);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_modifiableSystemObjectDefinition, _objectDefinition1, true);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_objectDefinition1, _systemObjectDefinition2, false);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_objectDefinition1, _systemObjectDefinition2, true);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_systemObjectDefinition2, _objectDefinition1, false);
		_testAddObjectRelationshipManyToMany(
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
			_systemObjectDefinition2, _objectDefinition1, true);
		_testAddObjectRelationshipOneToMany(
			_modifiableSystemObjectDefinition, _objectDefinition1, false);
		_testAddObjectRelationshipOneToMany(
			_modifiableSystemObjectDefinition, _objectDefinition1, true);
		_testAddObjectRelationshipOneToMany(
			_objectDefinition1, _systemObjectDefinition2, false);
		_testAddObjectRelationshipOneToMany(
			_objectDefinition1, _systemObjectDefinition2, true);
		_testAddObjectRelationshipOneToMany(
			_systemObjectDefinition2, _objectDefinition1, false);
		_testAddObjectRelationshipOneToMany(
			_systemObjectDefinition2, _objectDefinition1, true);

		_testCreateManyToManyObjectRelationshipTable(
			_systemObjectDefinition2, false);
		_testCreateManyToManyObjectRelationshipTable(
			_systemObjectDefinition2, true);

		_testSystemObjectRelationshipOneToMany();
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

		_bindObjectDefinitions(
			objectDefinitionAA.getObjectDefinitionId(),
			objectDefinitionAAA.getObjectDefinitionId());

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

		_asserScreenNavigationCategories(2, "C_AA");
		_asserScreenNavigationCategories(1, "C_AAA");
		_asserScreenNavigationCategories(0, "C_AAAA");

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

		_asserScreenNavigationCategories(0, "C_A");
		_asserScreenNavigationCategories(2, "C_AA");
		_asserScreenNavigationCategories(1, "C_AAA");

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

		_asserScreenNavigationCategories(0, "C_A");
		_asserScreenNavigationCategories(1, "C_AA");

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

		_asserScreenNavigationCategories(0, "C_A");
		_asserScreenNavigationCategories(1, "C_AA");

		long objectDefinitionId = objectDefinitionA.getObjectDefinitionId();

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			StringBundler.concat(
				"There must be no unrelated object entries when both object ",
				"definitions are published so that the object relationship ",
				"can be an edge to a root context"),
			() -> _objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(), objectDefinitionId));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind a draft object definition tree to a published object definition
		// tree

		objectDefinitionA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"A");
		objectDefinitionAA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"AA");

		_bindObjectDefinitions(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId());

		objectDefinitionAAA = _addAndPublishCustomObjectDefinition("AAA");
		ObjectDefinition objectDefinitionAAAA =
			_addAndPublishCustomObjectDefinition("AAAA");

		_bindObjectDefinitions(
			objectDefinitionAAA.getObjectDefinitionId(),
			objectDefinitionAAAA.getObjectDefinitionId());

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

		_asserScreenNavigationCategories(0, "C_A");
		_asserScreenNavigationCategories(0, "C_AA");
		_asserScreenNavigationCategories(2, "C_AAA");
		_asserScreenNavigationCategories(1, "C_AAAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind a published object definition to a draft object definition tree

		objectDefinitionA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"A");
		objectDefinitionAA = ObjectDefinitionTestUtil.addCustomObjectDefinition(
			"AA");

		_bindObjectDefinitions(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId());

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

		_asserScreenNavigationCategories(0, "C_A");
		_asserScreenNavigationCategories(0, "C_AA");
		_asserScreenNavigationCategories(1, "C_AAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA"}, _objectEntryLocalService,
			_objectRelationshipLocalService);
	}

	@Test
	public void testBindDraftObjectDefinitions() throws Exception {

		// Bind a draft object definition as a child node in a draft object
		// definition tree

		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AA");
		ObjectDefinition objectDefinitionAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAA");

		_bindObjectDefinitions(
			objectDefinitionAA.getObjectDefinitionId(),
			objectDefinitionAAA.getObjectDefinitionId());

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

		_bindObjectDefinitions(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId());

		objectDefinitionAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAA");
		ObjectDefinition objectDefinitionAAAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition("AAAA");

		_bindObjectDefinitions(
			objectDefinitionAAA.getObjectDefinitionId(),
			objectDefinitionAAAA.getObjectDefinitionId());

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
			() -> _bindObjectDefinitions(
				objectDefinitionAAAAA.getObjectDefinitionId(),
				objectDefinitionAAAAAA.getObjectDefinitionId()));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinitionAAAAAA);

		// Bind two object definition trees into one so that the height
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
			() -> _bindObjectDefinitions(
				objectDefinitionAAA.getObjectDefinitionId(),
				objectDefinitionAAAA.getObjectDefinitionId()));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				"C_A", "C_AA", "C_AAA", "C_AAAA", "C_AAAAA", "C_AAAAAA"
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testBindPublishedObjectDefinitions() throws Exception {

		// Bind a published object definition as a child node in a published
		// object definition tree

		ObjectDefinition objectDefinitionAA =
			_addAndPublishCustomObjectDefinition("AA");
		ObjectDefinition objectDefinitionAAA =
			_addAndPublishCustomObjectDefinition("AAA");

		_bindObjectDefinitions(
			objectDefinitionAA.getObjectDefinitionId(),
			objectDefinitionAAA.getObjectDefinitionId());

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

		_asserScreenNavigationCategories(2, "C_AA");
		_asserScreenNavigationCategories(2, "C_AAA");
		_asserScreenNavigationCategories(1, "C_AAAA");

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

		_asserScreenNavigationCategories(2, "C_A");
		_asserScreenNavigationCategories(2, "C_AA");
		_asserScreenNavigationCategories(2, "C_AAA");
		_asserScreenNavigationCategories(1, "C_AAAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAAA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		// Bind two published object definition trees

		ObjectDefinition objectDefinitionA =
			_addAndPublishCustomObjectDefinition("A");
		objectDefinitionAA = _addAndPublishCustomObjectDefinition("AA");

		_bindObjectDefinitions(
			objectDefinitionA.getObjectDefinitionId(),
			objectDefinitionAA.getObjectDefinitionId());

		objectDefinitionAAA = _addAndPublishCustomObjectDefinition("AAA");
		objectDefinitionAAAA = _addAndPublishCustomObjectDefinition("AAAA");

		_bindObjectDefinitions(
			objectDefinitionAAA.getObjectDefinitionId(),
			objectDefinitionAAAA.getObjectDefinitionId());

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

		_asserScreenNavigationCategories(2, "C_A");
		_asserScreenNavigationCategories(2, "C_AA");
		_asserScreenNavigationCategories(2, "C_AAA");
		_asserScreenNavigationCategories(1, "C_AAAA");

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

		_asserScreenNavigationCategories(2, "C_B");
		_asserScreenNavigationCategories(1, "C_BB");

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
			() -> _bindObjectDefinitions(
				objectDefinitionB.getObjectDefinitionId(),
				siteObjectDefinition.getObjectDefinitionId()));

		// Unable to bind the object definitions because the object relationship
		// must not create a circular reference in a root context

		ObjectDefinition objectDefinitionBBB =
			_addAndPublishCustomObjectDefinition("BBB");

		_bindObjectDefinitions(
			objectDefinitionBB.getObjectDefinitionId(),
			objectDefinitionBBB.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"The object relationship must not create a circular reference in " +
				"a root context",
			() -> _bindObjectDefinitions(
				objectDefinitionBBB.getObjectDefinitionId(),
				objectDefinitionB.getObjectDefinitionId()));

		// Unable to bind the object definitions when the child object
		// definition is bound to another object definition

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Unable to bind the object definitions when the child object " +
				"definition is bound to another object definition",
			() -> _bindObjectDefinitions(
				objectDefinitionB.getObjectDefinitionId(),
				objectDefinitionBBB.getObjectDefinitionId()));

		ObjectDefinition objectDefinitionC =
			_addAndPublishCustomObjectDefinition("C");
		ObjectDefinition objectDefinitionCC =
			_addAndPublishCustomObjectDefinition("CC");

		_bindObjectDefinitions(
			objectDefinitionC.getObjectDefinitionId(),
			objectDefinitionCC.getObjectDefinitionId());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Unable to bind the object definitions when the child object " +
				"definition is bound to another object definition",
			() -> _bindObjectDefinitions(
				objectDefinitionB.getObjectDefinitionId(),
				objectDefinitionCC.getObjectDefinitionId()));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_B", "C_BB", "C_BBB"}, _objectEntryLocalService,
			_objectRelationshipLocalService);
		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_C", "C_CC"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testBindPublishedObjectDefinitionsWithObjectEntries()
		throws Exception {

		ObjectDefinition objectDefinitionAA =
			_addAndPublishCustomObjectDefinition("AA");
		ObjectDefinition objectDefinitionAAA =
			_addAndPublishCustomObjectDefinition("AAA");

		ObjectRelationship objectRelationship1 =
			_objectRelationshipLocalService.addObjectRelationship(
				StringUtil.randomId(), TestPropsValues.getUserId(),
				objectDefinitionAA.getObjectDefinitionId(),
				objectDefinitionAAA.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectEntry objectDefinitionAAObjectEntry1 =
			ObjectEntryTestUtil.addObjectEntry(
				0, objectDefinitionAA.getObjectDefinitionId(),
				Collections.emptyMap());
		ObjectEntry objectDefinitionAAAObjectEntry1 =
			ObjectEntryTestUtil.addObjectEntry(
				0, objectDefinitionAAA.getObjectDefinitionId(),
				Collections.emptyMap());

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			StringBundler.concat(
				"There must be no unrelated object entries when both object ",
				"definitions are published so that the object relationship ",
				"can be an edge to a root context"),
			() -> _bindObjectDefinitions(objectRelationship1));

		ObjectField objectField1 = _objectFieldLocalService.getObjectField(
			objectRelationship1.getObjectFieldId2());

		objectDefinitionAAAObjectEntry1 =
			_objectEntryLocalService.updateObjectEntry(
				objectDefinitionAAAObjectEntry1.getUserId(),
				objectDefinitionAAAObjectEntry1.getObjectEntryId(),
				Collections.singletonMap(
					objectField1.getName(),
					objectDefinitionAAObjectEntry1.getObjectEntryId()),
				ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			0, objectDefinitionAAObjectEntry1.getRootObjectEntryId());
		Assert.assertEquals(
			0, objectDefinitionAAAObjectEntry1.getRootObjectEntryId());

		_bindObjectDefinitions(objectRelationship1);

		objectDefinitionAAObjectEntry1 =
			_objectEntryLocalService.getObjectEntry(
				objectDefinitionAAObjectEntry1.getObjectEntryId());
		objectDefinitionAAAObjectEntry1 =
			_objectEntryLocalService.getObjectEntry(
				objectDefinitionAAAObjectEntry1.getObjectEntryId());

		long expectedRootObjectEntryId =
			objectDefinitionAAObjectEntry1.getRootObjectEntryId();

		Assert.assertEquals(
			expectedRootObjectEntryId,
			objectDefinitionAAObjectEntry1.getRootObjectEntryId());
		Assert.assertEquals(
			expectedRootObjectEntryId,
			objectDefinitionAAAObjectEntry1.getRootObjectEntryId());

		ObjectDefinition objectDefinitionA =
			_addAndPublishCustomObjectDefinition("A");

		ObjectRelationship objectRelationship2 =
			_objectRelationshipLocalService.addObjectRelationship(
				StringUtil.randomId(), TestPropsValues.getUserId(),
				objectDefinitionA.getObjectDefinitionId(),
				objectDefinitionAA.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectField objectField2 = _objectFieldLocalService.getObjectField(
			objectRelationship2.getObjectFieldId2());

		ObjectEntry objectDefinitionAObjectEntry1 =
			ObjectEntryTestUtil.addObjectEntry(
				0, objectDefinitionA.getObjectDefinitionId(),
				Collections.emptyMap());

		_objectEntryLocalService.updateObjectEntry(
			objectDefinitionAAObjectEntry1.getUserId(),
			objectDefinitionAAObjectEntry1.getObjectEntryId(),
			Collections.singletonMap(
				objectField2.getName(),
				objectDefinitionAObjectEntry1.getObjectEntryId()),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			0, objectDefinitionAObjectEntry1.getRootObjectEntryId());

		_bindObjectDefinitions(objectRelationship2);

		objectDefinitionAObjectEntry1 = _objectEntryLocalService.getObjectEntry(
			objectDefinitionAObjectEntry1.getObjectEntryId());
		objectDefinitionAAObjectEntry1 =
			_objectEntryLocalService.getObjectEntry(
				objectDefinitionAAObjectEntry1.getObjectEntryId());
		objectDefinitionAAAObjectEntry1 =
			_objectEntryLocalService.getObjectEntry(
				objectDefinitionAAAObjectEntry1.getObjectEntryId());

		expectedRootObjectEntryId =
			objectDefinitionAObjectEntry1.getRootObjectEntryId();

		Assert.assertEquals(
			expectedRootObjectEntryId,
			objectDefinitionAObjectEntry1.getRootObjectEntryId());
		Assert.assertEquals(
			expectedRootObjectEntryId,
			objectDefinitionAAObjectEntry1.getRootObjectEntryId());
		Assert.assertEquals(
			expectedRootObjectEntryId,
			objectDefinitionAAAObjectEntry1.getRootObjectEntryId());

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinitionA.getName(), objectDefinitionAA.getName(),
				objectDefinitionAAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testDeleteObjectRelationship() throws Exception {
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationships cannot be deleted",
			() -> {
				ObjectRelationship objectRelationship =
					ObjectRelationshipTestUtil.addObjectRelationship(
						_objectRelationshipLocalService, _objectDefinition1,
						_objectDefinition2);

				_objectRelationshipLocalService.deleteObjectRelationship(
					_bindObjectDefinitions(objectRelationship));
			});
		AssertUtils.assertFailure(
			ObjectRelationshipReverseException.class,
			"Reverse object relationships cannot be deleted",
			() -> {
				ObjectRelationship objectRelationship =
					_objectRelationshipLocalService.addObjectRelationship(
						null, TestPropsValues.getUserId(),
						_objectDefinition1.getObjectDefinitionId(),
						_objectDefinition2.getObjectDefinitionId(), 0,
						ObjectRelationshipConstants.DELETION_TYPE_PREVENT,
						false,
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString()),
						StringUtil.randomId(), false,
						ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

				_objectRelationshipLocalService.deleteObjectRelationship(
					_objectRelationshipLocalService.
						fetchReverseObjectRelationship(
							objectRelationship, true));
			});

		ObjectRelationship systemObjectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), true,
				ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		AssertUtils.assertFailure(
			ObjectRelationshipSystemException.class, false,
			"Only allowed bundles can delete system object relationships",
			() -> _objectRelationshipLocalService.deleteObjectRelationship(
				systemObjectRelationship));

		_objectRelationshipLocalService.deleteObjectRelationship(
			systemObjectRelationship);
	}

	@Test
	public void testRegisterObjectRelationshipsRelatedInfoItemCollectionProviders()
		throws Exception {

		ObjectDefinition objectDefinition1 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName());
		ObjectDefinition objectDefinition2 =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				ObjectDefinitionTestUtil.getRandomName());

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			objectDefinition1.getObjectDefinitionId(),
			objectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		_objectRelationshipLocalService.
			registerObjectRelationshipsRelatedInfoCollectionProviders(
				objectDefinition1, _objectDefinitionLocalService, null);

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectRelationshipLocalServiceTest.class);

		ServiceTrackerMap<String, RelatedInfoItemCollectionProvider>
			serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
				bundle.getBundleContext(),
				RelatedInfoItemCollectionProvider.class, "item.class.name");

		Assert.assertNull(
			serviceTrackerMap.getService(objectDefinition1.getClassName()));
		Assert.assertNull(
			serviceTrackerMap.getService(objectDefinition2.getClassName()));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition1.getObjectDefinitionId());

		Assert.assertNull(
			serviceTrackerMap.getService(objectDefinition1.getClassName()));
		Assert.assertNull(
			serviceTrackerMap.getService(objectDefinition2.getClassName()));

		_objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition2.getObjectDefinitionId());

		RelatedInfoItemCollectionProvider relatedInfoItemCollectionProvider =
			serviceTrackerMap.getService(objectDefinition1.getClassName());

		Assert.assertEquals(
			objectDefinition1.getClassName(),
			relatedInfoItemCollectionProvider.getSourceItemClassName());

		relatedInfoItemCollectionProvider = serviceTrackerMap.getService(
			objectDefinition2.getClassName());

		Assert.assertEquals(
			objectDefinition2.getClassName(),
			relatedInfoItemCollectionProvider.getSourceItemClassName());

		serviceTrackerMap.close();

		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition1);
		_objectDefinitionLocalService.deleteObjectDefinition(objectDefinition2);
	}

	@Test
	public void testUnbindObjectDefinitions() throws Exception {
		Tree tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA", "AB"}
			).put(
				"AA", new String[] {"AAA"}
			).put(
				"AB", new String[0]
			).put(
				"AAA", new String[0]
			).build());

		_unbindObjectDefinitionNode("AA", tree);

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"A", new String[] {"AB"}
			).put(
				"AB", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		TreeTestUtil.assertObjectDefinitionTree(
			LinkedHashMapBuilder.put(
				"AA", new String[] {"AAA"}
			).put(
				"AAA", new String[0]
			).build(),
			_objectDefinitionTreeFactory.create(
				objectDefinitionAA.getObjectDefinitionId()),
			_objectDefinitionLocalService);

		_unbindObjectDefinitionNode("AB", tree);

		_assertRootObjectDefinitionIdIsZero("A");
		_assertRootObjectDefinitionIdIsZero("AB");

		_unbindObjectDefinitionNode("AAA", tree);

		_assertRootObjectDefinitionIdIsZero("AA");
		_assertRootObjectDefinitionIdIsZero("AAA");

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA", "C_AAB", "C_AB"},
			_objectEntryLocalService, _objectRelationshipLocalService);
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

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		ObjectEntry objectEntryA = _addObjectEntry(
			objectDefinitionA, Collections.emptyMap());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinitionA.getClassName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(objectEntryA.getObjectEntryId()), role.getRoleId(),
			new String[] {ActionKeys.UPDATE, ActionKeys.VIEW});

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		Node nodeAA = tree.getNode(objectDefinitionAA.getPrimaryKey());

		Edge edgeA_AA = nodeAA.getEdge();

		ObjectRelationship objectRelationshipA_AA =
			_objectRelationshipLocalService.getObjectRelationship(
				edgeA_AA.getObjectRelationshipId());

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectRelationshipA_AA.getObjectFieldId2());

		ObjectEntry objectEntryAA1 = _addObjectEntry(
			objectDefinitionAA,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), objectEntryA.getObjectEntryId()
			).build());
		ObjectEntry objectEntryAA2 = _addObjectEntry(
			objectDefinitionAA,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), objectEntryA.getObjectEntryId()
			).build());

		ObjectDefinition objectDefinitionAAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AAA");

		Node nodeAAA = tree.getNode(objectDefinitionAAA.getPrimaryKey());

		Edge edgeAA_AAA = nodeAAA.getEdge();

		ObjectRelationship objectRelationshipAA_AAA =
			_objectRelationshipLocalService.getObjectRelationship(
				edgeAA_AAA.getObjectRelationshipId());

		objectField = _objectFieldLocalService.fetchObjectField(
			objectRelationshipAA_AAA.getObjectFieldId2());

		ObjectEntry objectEntryAAA1 = _addObjectEntry(
			objectDefinitionAAA,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), objectEntryAA1.getObjectEntryId()
			).build());
		ObjectEntry objectEntryAAA2 = _addObjectEntry(
			objectDefinitionAAA,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), objectEntryAA2.getObjectEntryId()
			).build());

		_unbindObjectDefinitionNode("AA", tree);

		_entityCache.clearCache();

		_assertRootObjectEntryId(0, objectEntryA.getObjectEntryId());
		_assertRootObjectEntryId(
			objectEntryAA1.getObjectEntryId(),
			objectEntryAA1.getObjectEntryId());
		_assertRootObjectEntryId(
			objectEntryAA1.getObjectEntryId(),
			objectEntryAAA1.getObjectEntryId());
		_assertRootObjectEntryId(
			objectEntryAA2.getObjectEntryId(),
			objectEntryAA2.getObjectEntryId());
		_assertRootObjectEntryId(
			objectEntryAA2.getObjectEntryId(),
			objectEntryAAA2.getObjectEntryId());

		_assertHasResourcePermission(true, objectEntryA, role);
		_assertHasResourcePermission(true, objectEntryAA1, role);
		_assertHasResourcePermission(true, objectEntryAA2, role);
		_assertHasResourcePermission(false, objectEntryAAA1, role);
		_assertHasResourcePermission(false, objectEntryAAA2, role);

		_unbindObjectDefinitionNode("AAA", tree);

		_entityCache.clearCache();

		_assertRootObjectEntryId(0, objectEntryAA1.getObjectEntryId());
		_assertRootObjectEntryId(0, objectEntryAA2.getObjectEntryId());
		_assertRootObjectEntryId(0, objectEntryAAA1.getObjectEntryId());
		_assertRootObjectEntryId(0, objectEntryAAA2.getObjectEntryId());

		_assertHasResourcePermission(true, objectEntryAAA1, role);
		_assertHasResourcePermission(true, objectEntryAAA2, role);

		_objectEntryLocalService.deleteObjectEntry(objectEntryAAA1);
		_objectEntryLocalService.deleteObjectEntry(objectEntryAAA2);

		_objectEntryLocalService.deleteObjectEntry(objectEntryAA1);
		_objectEntryLocalService.deleteObjectEntry(objectEntryAA2);

		_objectEntryLocalService.deleteObjectEntry(objectEntryA);

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {"C_A", "C_AA", "C_AAA"}, _objectEntryLocalService,
			_objectRelationshipLocalService);
	}

	@Test
	public void testUnbindObjectDefinitionsWithResourcePermissions()
		throws Exception {

		Tree tree = TreeTestUtil.createObjectDefinitionTree(
			_objectDefinitionLocalService, _objectRelationshipLocalService,
			true,
			LinkedHashMapBuilder.put(
				"A", new String[] {"AA"}
			).put(
				"AA", new String[0]
			).build());

		Role organizationRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_ORGANIZATION);

		ObjectDefinition objectDefinitionA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_A");

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinitionA.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			organizationRole.getRoleId(), new String[] {ActionKeys.UPDATE});

		Role regularRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinitionA.getPortletId(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			regularRole.getRoleId(),
			new String[] {ActionKeys.ACCESS_IN_CONTROL_PANEL});

		Role siteRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinitionA.getResourceName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			siteRole.getRoleId(),
			new String[] {ObjectActionKeys.ADD_OBJECT_ENTRY});

		ObjectDefinition objectDefinitionAA =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(), "C_AA");

		ObjectAction objectAction = _objectActionLocalService.addObjectAction(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			objectDefinitionAA.getObjectDefinitionId(), true, null,
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

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), objectDefinitionAA.getClassName(),
			ResourceConstants.SCOPE_GROUP_TEMPLATE,
			String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			organizationRole.getRoleId(),
			new String[] {objectAction.getName()});

		_unbindObjectDefinitionNode("AA", tree);

		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinitionAA.getClassName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				organizationRole.getRoleId(), ActionKeys.UPDATE));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinitionAA.getClassName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				organizationRole.getRoleId(), objectAction.getName()));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinitionAA.getPortletId(),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(TestPropsValues.getCompanyId()),
				regularRole.getRoleId(), ActionKeys.ACCESS_IN_CONTROL_PANEL));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(),
				objectDefinitionAA.getResourceName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				siteRole.getRoleId(), ObjectActionKeys.ADD_OBJECT_ENTRY));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testUpdateObjectRelationship() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		ObjectRelationship objectRelationship1 =
			_objectRelationshipLocalService.addObjectRelationship(
				externalReferenceCode, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap("Able"), StringUtil.randomId(),
				false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		Assert.assertEquals(
			externalReferenceCode,
			objectRelationship1.getExternalReferenceCode());
		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap("Able"),
			objectRelationship1.getLabelMap());

		externalReferenceCode = RandomTestUtil.randomString();

		objectRelationship1 =
			_objectRelationshipLocalService.updateObjectRelationship(
				externalReferenceCode,
				objectRelationship1.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, false,
				LocalizedMapUtil.getLocalizedMap("Baker"), null);

		Assert.assertEquals(
			externalReferenceCode,
			objectRelationship1.getExternalReferenceCode());
		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap("Baker"),
			objectRelationship1.getLabelMap());

		ObjectRelationship reverseObjectRelationship =
			_objectRelationshipLocalService.fetchReverseObjectRelationship(
				objectRelationship1, true);

		Assert.assertEquals(
			objectRelationship1.getDeletionType(),
			reverseObjectRelationship.getDeletionType());
		Assert.assertEquals(
			objectRelationship1.getLabelMap(),
			reverseObjectRelationship.getLabelMap());

		externalReferenceCode = RandomTestUtil.randomString();

		reverseObjectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				externalReferenceCode,
				reverseObjectRelationship.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, false,
				LocalizedMapUtil.getLocalizedMap("Able"), null);

		Assert.assertEquals(
			externalReferenceCode,
			reverseObjectRelationship.getExternalReferenceCode());
		Assert.assertEquals(
			objectRelationship1.getDeletionType(),
			reverseObjectRelationship.getDeletionType());
		Assert.assertEquals(
			objectRelationship1.getLabelMap(),
			reverseObjectRelationship.getLabelMap());

		ObjectRelationship objectRelationship2 =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService,
				ObjectDefinitionTestUtil.addCustomObjectDefinition("A"),
				ObjectDefinitionTestUtil.addCustomObjectDefinition("AA"),
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT);

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(objectRelationship2));

		AssertUtils.assertFailure(
			ObjectRelationshipDeletionTypeException.MustHaveCascadeDeletionType.
				class,
			"Object relationship that belongs to a hierarchical structure " +
				"must have cascade deletion type",
			() -> _objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship2.getExternalReferenceCode(),
				objectRelationship2.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				null));

		AssertUtils.assertFailure(
			ObjectRelationshipDeletionTypeException.MustHaveCascadeDeletionType.
				class,
			"Object relationship that belongs to a hierarchical structure " +
				"must have cascade deletion type",
			() -> _objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship2.getExternalReferenceCode(),
				objectRelationship2.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				null));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService, new String[] {"C_A", "C_AA"},
			_objectEntryLocalService, _objectRelationshipLocalService);

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Inheritance between modifiable system and custom object " +
				"definitions is not allowed",
			() -> _bindObjectDefinitions(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService,
					_modifiableSystemObjectDefinition, _objectDefinition1)));
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Inheritance between modifiable system and custom object " +
				"definitions is not allowed",
			() -> _bindObjectDefinitions(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, _objectDefinition1,
					_modifiableSystemObjectDefinition)));

		ObjectRelationship objectRelationship3 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap("Able"), StringUtil.randomId(),
				false, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Object relationship must be one to many to be an edge of a root " +
				"context",
			() -> _bindObjectDefinitions(objectRelationship3));

		ObjectRelationship objectRelationship4 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, false,
				LocalizedMapUtil.getLocalizedMap("Able"), StringUtil.randomId(),
				false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Object relationship must not be a self-relationship to be an " +
				"edge of a root context",
			() -> _bindObjectDefinitions(objectRelationship4));

		ObjectRelationship objectRelationship5 =
			_addObjectRelationshipSystemObjectDefinition();

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"System object definitions cannot inherit configurations",
			() -> _objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship5.getExternalReferenceCode(),
				objectRelationship5.getObjectRelationshipId(),
				objectRelationship5.getParameterObjectFieldId(),
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				null));

		ObjectRelationship objectRelationship6 =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectField objectField2 = _objectFieldLocalService.updateRequired(
			objectRelationship6.getObjectFieldId2(), true);

		Assert.assertTrue(objectField2.isRequired());

		objectRelationship6 =
			_objectRelationshipLocalService.updateObjectRelationship(
				objectRelationship6.getExternalReferenceCode(),
				objectRelationship6.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, false,
				objectRelationship6.getLabelMap(), null);

		objectField2 = _objectFieldLocalService.fetchObjectField(
			objectRelationship6.getObjectFieldId2());

		Assert.assertFalse(objectField2.isRequired());

		ObjectRelationship systemObjectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap("Able"), StringUtil.randomId(),
				true, ObjectRelationshipConstants.TYPE_MANY_TO_MANY, null);

		systemObjectRelationship =
			_objectRelationshipLocalService.updateObjectRelationship(
				systemObjectRelationship.getExternalReferenceCode(),
				systemObjectRelationship.getObjectRelationshipId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE, false,
				LocalizedMapUtil.getLocalizedMap("Able"), null);

		Assert.assertEquals(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			systemObjectRelationship.getDeletionType());

		// Requests from forbidden bundles can only update the label

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.clear("liferay.mode");

		try {
			systemObjectRelationship =
				_objectRelationshipLocalService.updateObjectRelationship(
					systemObjectRelationship.getExternalReferenceCode(),
					systemObjectRelationship.getObjectRelationshipId(), 0,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
					LocalizedMapUtil.getLocalizedMap("Baker"), null);
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);
		}

		Assert.assertEquals(
			ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE,
			systemObjectRelationship.getDeletionType());
		Assert.assertEquals(
			LocalizedMapUtil.getLocalizedMap("Baker"),
			systemObjectRelationship.getLabelMap());

		_objectRelationshipLocalService.deleteObjectRelationship(
			systemObjectRelationship);
	}

	private static ObjectDefinition _addSystemObjectDefinition(
			String restContextPath)
		throws Exception {

		ObjectDefinition systemObjectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(),
				RandomTestUtil.randomString(), null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_COMPANY, null, 1,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				systemObjectDefinition.getCompanyId())) {

			Bundle bundle = FrameworkUtil.getBundle(
				ObjectRelationshipLocalServiceTest.class);

			BundleContext bundleContext = bundle.getBundleContext();

			bundleContext.registerService(
				SystemObjectDefinitionManager.class,
				new TestSystemObjectDefinitionManager(
					systemObjectDefinition.getModelClass(),
					systemObjectDefinition.getName(), restContextPath),
				new HashMapDictionary<>());
		}

		return systemObjectDefinition;
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
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		return _objectDefinitionLocalService.publishCustomObjectDefinition(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId());
	}

	private ObjectDefinition _addAndPublishModifiableSystemObjectDefinition()
		throws Exception {

		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		return _objectDefinitionLocalService.publishSystemObjectDefinition(
			TestPropsValues.getUserId(),
			modifiableSystemObjectDefinition.getObjectDefinitionId());
	}

	private ObjectEntry _addObjectEntry(
			ObjectDefinition objectDefinition, Map<String, Serializable> values)
		throws Exception {

		return _objectEntryLocalService.addObjectEntry(
			TestPropsValues.getUserId(), 0,
			objectDefinition.getObjectDefinitionId(), null, values,
			ServiceContextTestUtil.getServiceContext());
	}

	private ObjectRelationship _addObjectRelationshipSystemObjectDefinition()
		throws Exception {

		String objectRelationshipName = StringUtil.randomId();

		_objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_objectDefinition1.getObjectDefinitionId(),
			_objectDefinition2.getObjectDefinitionId(), 0,
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			objectRelationshipName, false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			_objectDefinition2.getObjectDefinitionId(),
			StringBundler.concat(
				"r_", objectRelationshipName, "_",
				_objectDefinition1.getPKObjectFieldName()));

		return _objectRelationshipLocalService.addObjectRelationship(
			null, TestPropsValues.getUserId(),
			_systemObjectDefinition1.getObjectDefinitionId(),
			_objectDefinition2.getObjectDefinitionId(),
			objectField.getObjectFieldId(),
			ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
	}

	private void _asserScreenNavigationCategories(
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

	private void _assertHasResourcePermission(
			boolean expectedHasResourcePermission, ObjectEntry objectEntry,
			Role role)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectEntry.getObjectDefinitionId());

		Assert.assertEquals(
			expectedHasResourcePermission,
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId(), ActionKeys.UPDATE));
		Assert.assertEquals(
			expectedHasResourcePermission,
			_resourcePermissionLocalService.hasResourcePermission(
				TestPropsValues.getCompanyId(), objectDefinition.getClassName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(objectEntry.getObjectEntryId()),
				role.getRoleId(), ActionKeys.VIEW));
	}

	private void _assertRootObjectDefinitionIdIsZero(
			String objectDefinitionShortName)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				TestPropsValues.getCompanyId(),
				"C_" + objectDefinitionShortName);

		Assert.assertEquals(0, objectDefinition.getRootObjectDefinitionId());
	}

	private void _assertRootObjectEntryId(
			long expectedRootObjectEntryId, long objectEntryId)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			objectEntryId);

		Assert.assertEquals(
			expectedRootObjectEntryId, objectEntry.getRootObjectEntryId());
	}

	private ObjectRelationship _bindObjectDefinitions(
			long objectDefinitionId1, long objectDefinitionId2)
		throws Exception {

		return _objectRelationshipLocalService.addObjectRelationship(
			StringUtil.randomId(), TestPropsValues.getUserId(),
			objectDefinitionId1, objectDefinitionId2, 0,
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE, true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			StringUtil.randomId(), false,
			ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null);
	}

	private ObjectRelationship _bindObjectDefinitions(
			ObjectRelationship objectRelationship)
		throws PortalException {

		return _objectRelationshipLocalService.updateObjectRelationship(
			objectRelationship.getExternalReferenceCode(),
			objectRelationship.getObjectRelationshipId(), 0,
			objectRelationship.getDeletionType(), true,
			objectRelationship.getLabelMap(), null);
	}

	private boolean _hasColumn(String tableName, String columnName)
		throws Exception {

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.hasColumn(tableName, columnName);
		}
	}

	private boolean _hasIndex(String tableName, String columnName)
		throws Exception {

		IndexMetadata indexMetadata =
			IndexMetadataFactoryUtil.createIndexMetadata(
				false, tableName, columnName);

		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.hasIndex(
				tableName, indexMetadata.getIndexName());
		}
	}

	private boolean _hasTable(String tableName) throws Exception {
		try (Connection connection = DataAccess.getConnection()) {
			DBInspector dbInspector = new DBInspector(connection);

			return dbInspector.hasTable(tableName);
		}
	}

	private void _testAddObjectRelationshipManyToMany(
			String deletionType, ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, boolean system)
		throws Exception {

		String name = StringUtil.randomId();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0, deletionType,
				false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, system, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null);

		Map<String, String> pkObjectFieldDBColumnNames =
			ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
				objectDefinition1, objectDefinition2, false);

		Assert.assertTrue(
			_hasColumn(
				objectRelationship.getDBTableName(),
				pkObjectFieldDBColumnNames.get("pkObjectFieldDBColumnName1")));
		Assert.assertTrue(
			_hasColumn(
				objectRelationship.getDBTableName(),
				pkObjectFieldDBColumnNames.get("pkObjectFieldDBColumnName2")));

		ObjectRelationship reverseObjectRelationship =
			_objectRelationshipLocalService.fetchReverseObjectRelationship(
				objectRelationship, true);

		Assert.assertNotNull(reverseObjectRelationship);

		Assert.assertEquals(
			objectRelationship.getDBTableName(),
			reverseObjectRelationship.getDBTableName());
		Assert.assertEquals(
			objectRelationship.getDeletionType(),
			reverseObjectRelationship.getDeletionType());
		Assert.assertEquals(
			objectRelationship.getType(), reverseObjectRelationship.getType());

		AssertUtils.assertFailure(
			ObjectRelationshipReverseException.class,
			"Reverse object relationships cannot be deleted",
			() -> _objectRelationshipLocalService.deleteObjectRelationship(
				reverseObjectRelationship));

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		Assert.assertFalse(_hasTable(objectRelationship.getDBTableName()));
	}

	private void _testAddObjectRelationshipOneToMany(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2, boolean system)
		throws Exception {

		String name = StringUtil.randomId();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition1.getObjectDefinitionId(),
				objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, system, ObjectRelationshipConstants.TYPE_ONE_TO_MANY,
				null);

		String objectFieldNamePrefix = "r_" + name + "_";

		Assert.assertTrue(
			_hasColumn(
				objectDefinition2.getExtensionDBTableName(),
				objectFieldNamePrefix +
					objectDefinition1.getPKObjectFieldName()));

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectDefinition2.getObjectDefinitionId(),
			objectFieldNamePrefix + objectDefinition1.getPKObjectFieldName());

		Assert.assertNotNull(objectField);

		Assert.assertTrue(
			_hasIndex(
				objectField.getDBTableName(), objectField.getDBColumnName()));

		ObjectFieldSetting objectFieldSetting =
			_objectFieldSettingLocalService.fetchObjectFieldSetting(
				objectRelationship.getObjectFieldId2(),
				ObjectFieldSettingConstants.
					NAME_OBJECT_DEFINITION_1_SHORT_NAME);

		Assert.assertNotNull(objectFieldSetting);

		Assert.assertEquals(
			objectDefinition1.getShortName(), objectFieldSetting.getValue());

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		Assert.assertFalse(
			_hasColumn(
				objectDefinition2.getExtensionDBTableName(),
				objectFieldNamePrefix +
					objectDefinition1.getPKObjectFieldName()));
		Assert.assertNull(
			_objectFieldLocalService.fetchObjectField(
				objectDefinition2.getObjectDefinitionId(),
				objectFieldNamePrefix +
					objectDefinition1.getPKObjectFieldName()));

		ObjectField expectedObjectField = new ObjectFieldBuilder(
		).externalReferenceCode(
			RandomTestUtil.randomString()
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).readOnly(
			ObjectFieldConstants.READ_ONLY_FALSE
		).required(
			RandomTestUtil.randomBoolean()
		).build();

		objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_objectDefinition1.getObjectDefinitionId(),
				_objectDefinition2.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"able", false, ObjectRelationshipConstants.TYPE_ONE_TO_MANY,
				expectedObjectField);

		ObjectField actualObjectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		Assert.assertEquals(
			expectedObjectField.getExternalReferenceCode(),
			actualObjectField.getExternalReferenceCode());
		Assert.assertEquals(
			expectedObjectField.getLabel(), actualObjectField.getLabel());
		Assert.assertEquals(
			expectedObjectField.getReadOnly(), actualObjectField.getReadOnly());
		Assert.assertEquals(
			expectedObjectField.isRequired(), actualObjectField.isRequired());

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);
	}

	private void _testBindObjectDefinitions(
			ObjectDefinition objectDefinition1,
			ObjectDefinition objectDefinition2,
			UnsafeBiConsumer<ObjectDefinition, ObjectDefinition, Exception>
				biConsumer)
		throws Exception {

		ObjectRelationship objectRelationship = _bindObjectDefinitions(
			objectDefinition1.getObjectDefinitionId(),
			objectDefinition2.getObjectDefinitionId());

		Assert.assertTrue(objectRelationship.isEdge());
		Assert.assertEquals(
			ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
			objectRelationship.getDeletionType());

		ObjectField objectField = _objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		Assert.assertTrue(objectField.isRequired());

		biConsumer.accept(
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition1.getObjectDefinitionId()),
			_objectDefinitionLocalService.getObjectDefinition(
				objectDefinition2.getObjectDefinitionId()));
	}

	private void _testCreateManyToManyObjectRelationshipTable(
			ObjectDefinition objectDefinition, boolean system)
		throws Exception {

		ObjectDefinition relatedObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition(
				false,
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())));

		String name = StringUtil.randomId();

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				relatedObjectDefinition.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				name, system, ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
				null);

		Assert.assertEquals(
			StringPool.BLANK, objectRelationship.getDBTableName());

		ObjectRelationship reverseObjectRelationship =
			_objectRelationshipLocalService.fetchReverseObjectRelationship(
				objectRelationship, true);

		Assert.assertNotNull(reverseObjectRelationship);
		Assert.assertEquals(
			StringPool.BLANK, reverseObjectRelationship.getDBTableName());

		relatedObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				relatedObjectDefinition.getObjectDefinitionId());

		objectRelationship =
			_objectRelationshipLocalService.fetchObjectRelationship(
				objectRelationship.getObjectRelationshipId());

		Assert.assertNotNull(objectRelationship.getDBTableName());

		reverseObjectRelationship =
			_objectRelationshipLocalService.fetchReverseObjectRelationship(
				objectRelationship, true);

		Assert.assertEquals(
			objectRelationship.getDBTableName(),
			reverseObjectRelationship.getDBTableName());

		Matcher matcher = _pattern.matcher(objectRelationship.getDBTableName());

		Assert.assertTrue(matcher.matches());

		Map<String, String> pkObjectFieldDBColumnNames =
			ObjectRelationshipUtil.getPKObjectFieldDBColumnNames(
				objectDefinition, relatedObjectDefinition, false);

		Assert.assertTrue(
			_hasColumn(
				objectRelationship.getDBTableName(),
				pkObjectFieldDBColumnNames.get("pkObjectFieldDBColumnName1")));
		Assert.assertTrue(
			_hasColumn(
				objectRelationship.getDBTableName(),
				pkObjectFieldDBColumnNames.get("pkObjectFieldDBColumnName2")));
		Assert.assertTrue(
			_hasIndex(
				objectRelationship.getDBTableName(),
				pkObjectFieldDBColumnNames.get("pkObjectFieldDBColumnName1")));
		Assert.assertTrue(
			_hasIndex(
				objectRelationship.getDBTableName(),
				pkObjectFieldDBColumnNames.get("pkObjectFieldDBColumnName2")));

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		Assert.assertFalse(_hasTable(objectRelationship.getDBTableName()));

		_objectDefinitionLocalService.deleteObjectDefinition(
			relatedObjectDefinition);
	}

	private void _testSystemObjectRelationshipOneToMany() throws Exception {
		AssertUtils.assertFailure(
			ObjectRelationshipParameterObjectFieldIdException.class,
			"Object definition " + _systemObjectDefinition1.getName() +
				" requires a parameter object field ID",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_systemObjectDefinition1.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(), 0,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		long parameterObjectFieldId = RandomTestUtil.randomLong();

		AssertUtils.assertFailure(
			ObjectRelationshipParameterObjectFieldIdException.class,
			"Parameter object field ID " + parameterObjectFieldId +
				" does not exist",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_systemObjectDefinition1.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(),
				parameterObjectFieldId,
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				_objectDefinition2.getObjectDefinitionId());

		ObjectField objectField1 = objectFields.get(0);

		AssertUtils.assertFailure(
			ObjectRelationshipParameterObjectFieldIdException.class,
			StringBundler.concat(
				"Parameter object field ID ", objectField1.getObjectFieldId(),
				" does not belong to object definition ",
				_objectDefinition1.getName()),
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_systemObjectDefinition1.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(),
				objectField1.getObjectFieldId(),
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		objectFields = _objectFieldLocalService.getObjectFields(
			_objectDefinition1.getObjectDefinitionId());

		ObjectField objectField2 = objectFields.get(0);

		AssertUtils.assertFailure(
			ObjectRelationshipParameterObjectFieldIdException.class,
			"Parameter object field ID " + objectField2.getObjectFieldId() +
				" does not belong to a relationship object field",
			() -> _objectRelationshipLocalService.addObjectRelationship(
				null, TestPropsValues.getUserId(),
				_systemObjectDefinition1.getObjectDefinitionId(),
				_objectDefinition1.getObjectDefinitionId(),
				objectField2.getObjectFieldId(),
				ObjectRelationshipConstants.DELETION_TYPE_PREVENT, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				StringUtil.randomId(), false,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY, null));

		_addObjectRelationshipSystemObjectDefinition();
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

		_unbindObjectDefinitionNode(objectDefinitionShortName, tree);

		objectAction = _objectActionLocalService.fetchObjectAction(
			objectAction.getObjectActionId());

		Assert.assertEquals(
			objectAction.getObjectActionTriggerKey(),
			ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE);
		Assert.assertFalse(objectAction.isActive());
	}

	private void _unbindObjectDefinitionNode(
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

		ObjectDefinition objectDefinition1 =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectRelationship.getObjectDefinitionId1());
		objectDefinition2 = _objectDefinitionLocalService.fetchObjectDefinition(
			objectRelationship.getObjectDefinitionId2());

		Assert.assertEquals(
			objectDefinition1.getScope(), objectDefinition2.getScope());

		ObjectField objectField = _objectFieldLocalService.fetchObjectField(
			objectRelationship.getObjectFieldId2());

		Assert.assertTrue(objectField.isRequired());
	}

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static final Pattern _pattern = Pattern.compile(
		"R_[A-Z][0-9][A-Z][0-9]$");
	private static ObjectDefinition _systemObjectDefinition1;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private ObjectDefinition _modifiableSystemObjectDefinition;

	@Inject
	private ObjectActionLocalService _objectActionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	private ObjectDefinitionTreeFactory _objectDefinitionTreeFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _systemObjectDefinition2;

}