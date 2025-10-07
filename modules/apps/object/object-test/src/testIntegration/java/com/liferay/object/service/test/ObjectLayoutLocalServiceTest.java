/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationRegistryUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.constants.ObjectLayoutBoxConstants;
import com.liferay.object.exception.DefaultObjectLayoutException;
import com.liferay.object.exception.ObjectDefinitionModifiableException;
import com.liferay.object.exception.ObjectLayoutBoxCategorizationTypeException;
import com.liferay.object.exception.ObjectLayoutColumnSizeException;
import com.liferay.object.exception.ObjectRelationshipEdgeException;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.model.ObjectLayoutBox;
import com.liferay.object.model.ObjectLayoutColumn;
import com.liferay.object.model.ObjectLayoutRow;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectLayoutLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.service.persistence.ObjectLayoutBoxPersistence;
import com.liferay.object.service.persistence.ObjectLayoutColumnPersistence;
import com.liferay.object.service.persistence.ObjectLayoutRowPersistence;
import com.liferay.object.service.persistence.ObjectLayoutTabPersistence;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Albuquerque
 */
@FeatureFlag("LPD-34594")
@RunWith(Arquillian.class)
public class ObjectLayoutLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectDefinitionA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
		_objectDefinitionAA =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_objectRelationshipA_AA =
			ObjectRelationshipTestUtil.addObjectRelationship(
				_objectRelationshipLocalService, _objectDefinitionA,
				_objectDefinitionAA);

		TreeTestUtil.bind(
			_objectRelationshipLocalService, List.of(_objectRelationshipA_AA));
	}

	@Before
	public void setUp() throws Exception {
		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
	}

	@Test
	public void testAddObjectLayout() throws Exception {
		AssertUtils.assertFailure(
			DefaultObjectLayoutException.class,
			"All required object fields must be associated to the first tab " +
				"of a default object layout",
			() -> _objectLayoutLocalService.addObjectLayout(
				TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), true,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				Arrays.asList(
					_createObjectLayoutTab(), _createObjectLayoutTab())));

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), "Test", null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString())));

		AssertUtils.assertFailure(
			ObjectDefinitionModifiableException.class,
			"A modifiable object definition is required",
			() -> _objectLayoutLocalService.addObjectLayout(
				TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId(), false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				Collections.singletonList(_createObjectLayoutTab())));

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_objectDefinition.setStorageType(RandomTestUtil.randomString());

		_objectDefinitionLocalService.updateObjectDefinition(_objectDefinition);

		AssertUtils.assertFailure(
			ObjectLayoutBoxCategorizationTypeException.class,
			"Categorization layout box can only be used in object " +
				"definitions with a default storage type",
			() -> {
				ObjectLayoutTab objectLayoutTab =
					_objectLayoutTabPersistence.create(0);

				objectLayoutTab.setNameMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()));
				objectLayoutTab.setPriority(0);
				objectLayoutTab.setObjectLayoutBoxes(
					Arrays.asList(
						_createObjectLayoutBox(),
						_createObjectLayoutBox(
							ObjectLayoutBoxConstants.TYPE_CATEGORIZATION)));

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Collections.singletonList(objectLayoutTab));
			});

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		_objectDefinition.setEnableCategorization(false);

		_objectDefinitionLocalService.updateObjectDefinition(_objectDefinition);

		AssertUtils.assertFailure(
			ObjectLayoutBoxCategorizationTypeException.class,
			"Categorization layout box must be enabled to be used",
			() -> {
				ObjectLayoutTab objectLayoutTab =
					_objectLayoutTabPersistence.create(0);

				objectLayoutTab.setNameMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()));
				objectLayoutTab.setPriority(0);
				objectLayoutTab.setObjectLayoutBoxes(
					Arrays.asList(
						_createObjectLayoutBox(),
						_createObjectLayoutBox(
							ObjectLayoutBoxConstants.TYPE_CATEGORIZATION)));

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Collections.singletonList(objectLayoutTab));
			});

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		AssertUtils.assertFailure(
			ObjectLayoutBoxCategorizationTypeException.class,
			"Categorization layout box must not have layout rows",
			() -> {
				ObjectLayoutTab objectLayoutTab =
					_objectLayoutTabPersistence.create(0);

				objectLayoutTab.setNameMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()));
				objectLayoutTab.setPriority(0);

				ObjectLayoutBox objectLayoutBox = _createObjectLayoutBox(
					ObjectLayoutBoxConstants.TYPE_CATEGORIZATION);

				objectLayoutBox.setObjectLayoutRows(
					Collections.singletonList(_createObjectLayoutRow()));

				objectLayoutTab.setObjectLayoutBoxes(
					Arrays.asList(_createObjectLayoutBox(), objectLayoutBox));

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Collections.singletonList(objectLayoutTab));
			});

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationships cannot be associated with object " +
				"layout tabs",
			() -> {
				ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab();

				objectLayoutTab.setObjectRelationshipId(
					_objectRelationshipA_AA.getObjectRelationshipId());

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinitionA.getObjectDefinitionId(), false,
					RandomTestUtil.randomLocaleStringMap(),
					List.of(objectLayoutTab));
			});
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationship object fields cannot be associated " +
				"with object layouts",
			() -> {
				ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab();

				ObjectLayoutBox objectLayoutBox = _createObjectLayoutBox();

				ObjectLayoutRow objectLayoutRow = _createObjectLayoutRow();

				ObjectLayoutColumn objectLayoutColumn =
					_createObjectLayoutColumn(false);

				objectLayoutColumn.setObjectFieldId(
					_objectRelationshipA_AA.getObjectFieldId2());

				objectLayoutRow.setObjectLayoutColumns(
					List.of(objectLayoutColumn));

				objectLayoutBox.setObjectLayoutRows(List.of(objectLayoutRow));

				objectLayoutTab.setObjectLayoutBoxes(List.of(objectLayoutBox));

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinitionAA.getObjectDefinitionId(), true,
					RandomTestUtil.randomLocaleStringMap(),
					List.of(objectLayoutTab));
			});

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		AssertUtils.assertFailure(
			ObjectLayoutBoxCategorizationTypeException.class,
			"Object layout box must have a type",
			() -> {
				ObjectLayoutTab objectLayoutTab =
					_objectLayoutTabPersistence.create(0);

				objectLayoutTab.setNameMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()));
				objectLayoutTab.setPriority(0);
				objectLayoutTab.setObjectLayoutBoxes(
					Arrays.asList(
						_createObjectLayoutBox(),
						_createObjectLayoutBox(null)));

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Collections.singletonList(objectLayoutTab));
			});

		AssertUtils.assertFailure(
			ObjectLayoutColumnSizeException.class,
			"Object layout column size must be more than 0 and less than 12",
			() -> {
				ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab();

				List<ObjectLayoutBox> objectLayoutBoxes =
					objectLayoutTab.getObjectLayoutBoxes();

				ObjectLayoutBox objectLayoutBox = objectLayoutBoxes.get(0);

				List<ObjectLayoutRow> objectLayoutRows =
					objectLayoutBox.getObjectLayoutRows();

				ObjectLayoutRow objectLayoutRow = objectLayoutRows.get(0);

				List<ObjectLayoutColumn> objectLayoutColumns =
					objectLayoutRow.getObjectLayoutColumns();

				ObjectLayoutColumn objectLayoutColumn = objectLayoutColumns.get(
					0);

				objectLayoutColumn.setSize(13);

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Collections.singletonList(objectLayoutTab));
			});

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		AssertUtils.assertFailure(
			ObjectLayoutBoxCategorizationTypeException.class,
			"There can only be one categorization layout box per layout",
			() -> {
				ObjectLayoutTab objectLayoutTab1 =
					_objectLayoutTabPersistence.create(0);

				objectLayoutTab1.setNameMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()));
				objectLayoutTab1.setPriority(0);
				objectLayoutTab1.setObjectLayoutBoxes(
					Arrays.asList(
						_createObjectLayoutBox(),
						_createObjectLayoutBox(
							ObjectLayoutBoxConstants.TYPE_CATEGORIZATION)));

				ObjectLayoutTab objectLayoutTab2 =
					_objectLayoutTabPersistence.create(0);

				objectLayoutTab2.setNameMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()));
				objectLayoutTab2.setObjectLayoutBoxes(
					Arrays.asList(
						_createObjectLayoutBox(),
						_createObjectLayoutBox(
							ObjectLayoutBoxConstants.TYPE_CATEGORIZATION)));

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), false,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Arrays.asList(objectLayoutTab1, objectLayoutTab2));
			});

		_deleteObjectFields();

		AssertUtils.assertFailure(
			DefaultObjectLayoutException.class,
			"There can only be one default object layout",
			() -> {
				ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab();

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), true,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Collections.singletonList(objectLayoutTab));

				_objectLayoutLocalService.addObjectLayout(
					TestPropsValues.getUserId(),
					_objectDefinition.getObjectDefinitionId(), true,
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString()),
					Collections.singletonList(objectLayoutTab));
			});

		ObjectLayout objectLayout = _addObjectLayout();

		_assertObjectLayout(objectLayout);

		_deleteObjectFields();

		_objectDefinitionLocalService.deleteObjectDefinition(
			_objectDefinition.getObjectDefinitionId());

		_objectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(),
						"x" + RandomTestUtil.randomString())));

		objectLayout = _addObjectLayout();

		_assertObjectLayout(objectLayout);

		_objectLayoutLocalService.deleteObjectLayout(
			objectLayout.getObjectLayoutId());
	}

	@Test
	public void testDeleteObjectLayout() throws Exception {
		ObjectLayout objectLayout = _addObjectLayout();

		List<ObjectLayoutTab> objectLayoutTabs =
			_objectLayoutTabPersistence.findByObjectLayoutId(
				objectLayout.getObjectLayoutId());

		Assert.assertFalse(objectLayoutTabs.isEmpty());

		for (ObjectLayoutTab objectLayoutTab : objectLayoutTabs) {
			List<ObjectLayoutBox> objectLayoutBoxes =
				_objectLayoutBoxPersistence.findByObjectLayoutTabId(
					objectLayoutTab.getObjectLayoutTabId());

			objectLayoutTab.setObjectLayoutBoxes(objectLayoutBoxes);

			Assert.assertFalse(objectLayoutBoxes.isEmpty());

			for (ObjectLayoutBox objectLayoutBox : objectLayoutBoxes) {
				List<ObjectLayoutRow> objectLayoutRows =
					_objectLayoutRowPersistence.findByObjectLayoutBoxId(
						objectLayoutBox.getObjectLayoutBoxId());

				Assert.assertFalse(objectLayoutRows.isEmpty());
			}
		}

		_objectLayoutLocalService.deleteObjectLayout(objectLayout);

		for (ObjectLayoutTab objectLayoutTab : objectLayoutTabs) {
			List<ObjectLayoutBox> objectLayoutBoxes =
				_objectLayoutBoxPersistence.findByObjectLayoutTabId(
					objectLayoutTab.getObjectLayoutTabId());

			Assert.assertTrue(objectLayoutBoxes.isEmpty());

			for (ObjectLayoutBox objectLayoutBox :
					objectLayoutTab.getObjectLayoutBoxes()) {

				List<ObjectLayoutRow> objectLayoutRows =
					_objectLayoutRowPersistence.findByObjectLayoutBoxId(
						objectLayoutBox.getObjectLayoutBoxId());

				Assert.assertTrue(objectLayoutRows.isEmpty());
			}
		}
	}

	@Test
	public void testGetObjectLayout() throws Exception {
		_addObjectLayout();

		List<ObjectLayout> objectLayouts =
			_objectLayoutLocalService.getObjectLayouts(
				_objectDefinition.getObjectDefinitionId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		ObjectLayout objectLayout = objectLayouts.get(0);

		_assertObjectLayout(objectLayout);

		_objectLayoutLocalService.deleteObjectLayout(
			objectLayout.getObjectLayoutId());
	}

	@Test
	public void testUpdateObjectLayout() throws Exception {
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationships cannot be associated with object " +
				"layout tabs",
			() -> {
				ObjectLayout objectLayout = _addObjectLayout();

				ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab();

				objectLayoutTab.setObjectRelationshipId(
					_objectRelationshipA_AA.getObjectRelationshipId());

				_objectLayoutLocalService.updateObjectLayout(
					objectLayout.getObjectLayoutId(), false,
					RandomTestUtil.randomLocaleStringMap(),
					List.of(objectLayoutTab));
			});
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationship object fields cannot be associated " +
				"with object layouts",
			() -> {
				ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab();

				ObjectLayoutBox objectLayoutBox = _createObjectLayoutBox();

				ObjectLayoutRow objectLayoutRow = _createObjectLayoutRow();

				ObjectLayoutColumn objectLayoutColumn =
					_createObjectLayoutColumn(false);

				ObjectField objectField =
					_objectFieldLocalService.getObjectField(
						_objectDefinitionAA.getObjectDefinitionId(),
						"externalReferenceCode");

				objectLayoutColumn.setObjectFieldId(
					objectField.getObjectFieldId());

				objectLayoutRow.setObjectLayoutColumns(
					List.of(objectLayoutColumn));

				objectLayoutBox.setObjectLayoutRows(List.of(objectLayoutRow));

				objectLayoutTab.setObjectLayoutBoxes(List.of(objectLayoutBox));

				ObjectLayout objectLayout =
					_objectLayoutLocalService.addObjectLayout(
						TestPropsValues.getUserId(),
						_objectDefinitionAA.getObjectDefinitionId(), false,
						RandomTestUtil.randomLocaleStringMap(),
						Collections.singletonList(objectLayoutTab));

				objectLayoutColumn.setObjectFieldId(
					_objectRelationshipA_AA.getObjectFieldId2());

				_objectLayoutLocalService.updateObjectLayout(
					objectLayout.getObjectLayoutId(), false,
					RandomTestUtil.randomLocaleStringMap(),
					List.of(objectLayoutTab));
			});

		_objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		List<ScreenNavigationCategory> screenNavigationCategories =
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				_objectDefinition.getClassName(), TestPropsValues.getUser(),
				null);

		Assert.assertTrue(screenNavigationCategories.isEmpty());

		ObjectLayoutTab objectLayoutTab1 = _createObjectLayoutTab();

		ObjectLayout objectLayout = _objectLayoutLocalService.addObjectLayout(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), true,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Collections.singletonList(objectLayoutTab1));

		screenNavigationCategories =
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				_objectDefinition.getClassName(), TestPropsValues.getUser(),
				null);

		Assert.assertEquals(
			screenNavigationCategories.toString(), 1,
			screenNavigationCategories.size());

		_addObjectLayout();

		screenNavigationCategories =
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				_objectDefinition.getClassName(), TestPropsValues.getUser(),
				null);

		Assert.assertEquals(
			screenNavigationCategories.toString(), 1,
			screenNavigationCategories.size());

		_objectLayoutLocalService.updateObjectLayout(
			objectLayout.getObjectLayoutId(), false, objectLayout.getNameMap(),
			Collections.singletonList(objectLayoutTab1));

		screenNavigationCategories =
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				_objectDefinition.getClassName(), TestPropsValues.getUser(),
				null);

		Assert.assertTrue(screenNavigationCategories.isEmpty());
	}

	private long _addObjectField(boolean system) throws Exception {
		ObjectField objectField = null;

		if (system) {
			objectField = _objectFieldLocalService.addSystemObjectField(
				null, TestPropsValues.getUserId(), 0,
				_objectDefinition.getObjectDefinitionId(),
				ObjectFieldConstants.BUSINESS_TYPE_TEXT, null, null,
				ObjectFieldConstants.DB_TYPE_STRING, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, "x" + RandomTestUtil.randomString(),
				ObjectFieldConstants.READ_ONLY_FALSE, null, false, false,
				Collections.emptyList());
		}
		else {
			objectField = ObjectFieldUtil.addCustomObjectField(
				new TextObjectFieldBuilder(
				).userId(
					TestPropsValues.getUserId()
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"x" + RandomTestUtil.randomString()
				).objectDefinitionId(
					_objectDefinition.getObjectDefinitionId()
				).required(
					true
				).build());
		}

		return objectField.getObjectFieldId();
	}

	private ObjectLayout _addObjectLayout() throws Exception {
		return _objectLayoutLocalService.addObjectLayout(
			TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(), false,
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			Collections.singletonList(_createObjectLayoutTab()));
	}

	private void _assertObjectLayout(ObjectLayout objectLayout) {
		List<ObjectLayoutTab> objectLayoutTabs =
			objectLayout.getObjectLayoutTabs();

		Assert.assertEquals(
			objectLayoutTabs.toString(), 1, objectLayoutTabs.size());

		ObjectLayoutTab objectLayoutTab = objectLayoutTabs.get(0);

		List<ObjectLayoutBox> objectLayoutBoxes =
			objectLayoutTab.getObjectLayoutBoxes();

		Assert.assertEquals(
			objectLayoutBoxes.toString(), 2, objectLayoutBoxes.size());

		ObjectLayoutBox objectLayoutBox = objectLayoutBoxes.get(0);

		List<ObjectLayoutRow> objectLayoutRows =
			objectLayoutBox.getObjectLayoutRows();

		Assert.assertEquals(
			objectLayoutRows.toString(), 3, objectLayoutRows.size());

		ObjectLayoutRow objectLayoutRow = objectLayoutRows.get(0);

		List<ObjectLayoutColumn> objectLayoutColumns =
			objectLayoutRow.getObjectLayoutColumns();

		Assert.assertEquals(
			objectLayoutColumns.toString(), 4, objectLayoutColumns.size());
	}

	private ObjectLayoutBox _createObjectLayoutBox() throws Exception {
		return _createObjectLayoutBox(ObjectLayoutBoxConstants.TYPE_REGULAR);
	}

	private ObjectLayoutBox _createObjectLayoutBox(String type)
		throws Exception {

		ObjectLayoutBox objectLayoutBox = _objectLayoutBoxPersistence.create(0);

		objectLayoutBox.setCollapsable(false);
		objectLayoutBox.setNameMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()));
		objectLayoutBox.setPriority(0);
		objectLayoutBox.setType(type);

		if (StringUtil.equals(type, ObjectLayoutBoxConstants.TYPE_REGULAR)) {
			objectLayoutBox.setObjectLayoutRows(
				Arrays.asList(
					_createObjectLayoutRow(), _createObjectLayoutRow(),
					_createObjectLayoutRow()));
		}

		return objectLayoutBox;
	}

	private ObjectLayoutColumn _createObjectLayoutColumn(boolean system)
		throws Exception {

		ObjectLayoutColumn objectLayoutColumn =
			_objectLayoutColumnPersistence.create(0);

		objectLayoutColumn.setObjectFieldId(_addObjectField(system));
		objectLayoutColumn.setPriority(0);

		return objectLayoutColumn;
	}

	private ObjectLayoutColumn _createObjectLayoutColumn(long objectFieldId)
		throws Exception {

		ObjectLayoutColumn objectLayoutColumn =
			_objectLayoutColumnPersistence.create(0);

		objectLayoutColumn.setObjectFieldId(objectFieldId);
		objectLayoutColumn.setPriority(0);

		return objectLayoutColumn;
	}

	private ObjectLayoutRow _createObjectLayoutRow() throws Exception {
		ObjectLayoutRow objectLayoutRow = _objectLayoutRowPersistence.create(0);

		objectLayoutRow.setPriority(0);
		objectLayoutRow.setObjectLayoutColumns(
			Arrays.asList(
				_createObjectLayoutColumn(false),
				_createObjectLayoutColumn(false),
				_createObjectLayoutColumn(true),
				_createObjectLayoutColumn(true)));

		return objectLayoutRow;
	}

	private ObjectLayoutTab _createObjectLayoutTab() throws Exception {
		ObjectLayoutTab objectLayoutTab = _objectLayoutTabPersistence.create(0);

		objectLayoutTab.setNameMap(RandomTestUtil.randomLocaleStringMap());
		objectLayoutTab.setObjectLayoutBoxes(
			Arrays.asList(_createObjectLayoutBox(), _createObjectLayoutBox()));

		return objectLayoutTab;
	}

	private void _deleteObjectFields() throws Exception {
		List<ObjectField> objectFields =
			_objectFieldLocalService.getObjectFields(
				_objectDefinition.getObjectDefinitionId(), false);

		for (ObjectField objectField : objectFields) {
			_objectFieldLocalService.deleteObjectField(objectField);
		}
	}

	private static ObjectDefinition _objectDefinitionA;
	private static ObjectDefinition _objectDefinitionAA;
	private static ObjectRelationship _objectRelationshipA_AA;

	@Inject
	private static ObjectRelationshipLocalService
		_objectRelationshipLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

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

}