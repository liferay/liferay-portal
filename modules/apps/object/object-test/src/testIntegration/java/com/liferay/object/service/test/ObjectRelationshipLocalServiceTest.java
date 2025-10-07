/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.info.collection.provider.RelatedInfoItemCollectionProvider;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectLayoutBoxConstants;
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
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectFieldSetting;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutBox;
import com.liferay.object.model.ObjectLayoutColumn;
import com.liferay.object.model.ObjectLayoutRow;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.relationship.util.ObjectRelationshipUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectFieldSettingLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.persistence.ObjectLayoutBoxPersistence;
import com.liferay.object.service.persistence.ObjectLayoutColumnPersistence;
import com.liferay.object.service.persistence.ObjectLayoutRowPersistence;
import com.liferay.object.service.persistence.ObjectLayoutTabPersistence;
import com.liferay.object.service.test.system.TestSystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.dao.db.IndexMetadata;
import com.liferay.portal.kernel.dao.db.IndexMetadataFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.sql.Connection;

import java.util.ArrayList;
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
import org.osgi.framework.ServiceReference;

/**
 * @author Brian Wing Shun Chan
 */
@FeatureFlag("LPD-34594")
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
		_objectDefinition3 = _addAndPublishCustomObjectDefinition();
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
	public void testDeleteObjectRelationshipWithObjectLayout()
		throws Exception {

		ObjectRelationship objectRelationship =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinition1,
				_objectDefinition2,
				ObjectRelationshipConstants.TYPE_ONE_TO_MANY);

		ObjectLayout objectLayout = _objectLayoutLocalService.addObjectLayout(
			TestPropsValues.getUserId(),
			_objectDefinition1.getObjectDefinitionId(), true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Arrays.asList(
				_addObjectLayoutTab(0),
				_addObjectLayoutTab(
					objectRelationship.getObjectRelationshipId())));

		List<ObjectLayoutTab> objectLayoutTabs =
			objectLayout.getObjectLayoutTabs();

		_assertObjectLayoutTab(1, objectLayoutTabs.get(1));

		_objectRelationshipLocalService.deleteObjectRelationship(
			objectRelationship);

		_assertObjectLayoutTab(0, objectLayoutTabs.get(1));
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

	private ObjectLayoutBox _addObjectLayoutBox() throws Exception {
		ObjectLayoutBox objectLayoutBox = _objectLayoutBoxPersistence.create(0);

		objectLayoutBox.setCollapsable(false);
		objectLayoutBox.setNameMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()));
		objectLayoutBox.setPriority(0);
		objectLayoutBox.setType(ObjectLayoutBoxConstants.TYPE_REGULAR);
		objectLayoutBox.setObjectLayoutRows(
			Arrays.asList(
				_addObjectLayoutRow(), _addObjectLayoutRow(),
				_addObjectLayoutRow()));

		return objectLayoutBox;
	}

	private ObjectLayoutColumn _addObjectLayoutColumn() throws Exception {
		ObjectLayoutColumn objectLayoutColumn =
			_objectLayoutColumnPersistence.create(0);

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"x" + RandomTestUtil.randomString()
			).objectDefinitionId(
				_objectDefinition1.getObjectDefinitionId()
			).required(
				true
			).userId(
				TestPropsValues.getUserId()
			).build());

		objectLayoutColumn.setObjectFieldId(objectField.getObjectFieldId());

		return objectLayoutColumn;
	}

	private ObjectLayoutRow _addObjectLayoutRow() throws Exception {
		ObjectLayoutRow objectLayoutRow = _objectLayoutRowPersistence.create(0);

		objectLayoutRow.setPriority(0);
		objectLayoutRow.setObjectLayoutColumns(
			Collections.singletonList(_addObjectLayoutColumn()));

		return objectLayoutRow;
	}

	private ObjectLayoutTab _addObjectLayoutTab(long objectRelationshipId)
		throws Exception {

		ObjectLayoutTab objectLayoutTab = _objectLayoutTabPersistence.create(0);

		objectLayoutTab.setObjectRelationshipId(objectRelationshipId);
		objectLayoutTab.setNameMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()));

		if (objectRelationshipId == 0) {
			objectLayoutTab.setObjectLayoutBoxes(
				Collections.singletonList(_addObjectLayoutBox()));
		}

		return objectLayoutTab;
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

	private void _assertObjectLayoutTab(
			int expectedSize, ObjectLayoutTab objectLayoutTab)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectRelationshipLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<ServiceReference<?>> serviceReferences = new ArrayList<>(
			bundleContext.getServiceReferences(
				ScreenNavigationCategory.class,
				"(screen.navigation.category.order:Integer=" +
					objectLayoutTab.getObjectLayoutTabId() + ")"));

		Assert.assertEquals(
			serviceReferences.toString(), expectedSize,
			serviceReferences.size());
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

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static final Pattern _pattern = Pattern.compile(
		"R_[A-Z][0-9][A-Z][0-9]$");
	private static ObjectDefinition _systemObjectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _modifiableSystemObjectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition1;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition2;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition3;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectFieldSettingLocalService _objectFieldSettingLocalService;

	@Inject
	private ObjectLayoutBoxPersistence _objectLayoutBoxPersistence;

	@Inject
	private ObjectLayoutColumnPersistence _objectLayoutColumnPersistence;

	@Inject
	private ObjectLayoutLocalService _objectLayoutLocalService;

	@Inject
	private ObjectLayoutRowPersistence _objectLayoutRowPersistence;

	@Inject
	private ObjectLayoutTabPersistence _objectLayoutTabPersistence;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _systemObjectDefinition2;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}