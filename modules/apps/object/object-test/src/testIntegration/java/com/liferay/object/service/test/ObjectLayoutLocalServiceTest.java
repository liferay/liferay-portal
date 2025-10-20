/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationCategory;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationRegistryUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectLayoutBoxConstants;
import com.liferay.object.exception.DefaultObjectLayoutException;
import com.liferay.object.exception.ObjectDefinitionModifiableException;
import com.liferay.object.exception.ObjectLayoutBoxTypeException;
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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

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
		_customObjectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();
		_modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null, false,
				RandomTestUtil.randomLocaleStringMap(), "TestA", null, null,
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"x" + RandomTestUtil.randomString()
					).build()));
		_unmodifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addUnmodifiableSystemObjectDefinition(
				null, TestPropsValues.getUserId(), "Test", null,
				RandomTestUtil.randomLocaleStringMap(), "TestB", null, null,
				RandomTestUtil.randomLocaleStringMap(),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"x" + RandomTestUtil.randomString()
					).build()));
	}

	@Test
	public void testAddObjectLayout() throws Exception {
		ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab(
			_createObjectLayoutBox(
				_createObjectLayoutColumn(_customObjectDefinition)));

		ObjectField objectField = _addCustomObjectField(
			"x" + RandomTestUtil.randomString(),
			_customObjectDefinition.getObjectDefinitionId());

		AssertUtils.assertFailure(
			DefaultObjectLayoutException.class,
			"All required object fields must be associated to the first tab " +
				"of a default object layout",
			() -> _addObjectLayout(
				_customObjectDefinition, true, objectLayoutTab,
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						_createObjectLayoutColumn(
							_customObjectDefinition, objectField)))));

		_objectFieldLocalService.deleteObjectField(objectField);

		_addObjectLayout(_customObjectDefinition, true, objectLayoutTab);

		AssertUtils.assertFailure(
			DefaultObjectLayoutException.class,
			"There can only be one default object layout",
			() -> _addObjectLayout(
				_customObjectDefinition, true, objectLayoutTab));

		AssertUtils.assertFailure(
			ObjectDefinitionModifiableException.class,
			"A modifiable object definition is required",
			() -> _addObjectLayout(_unmodifiableSystemObjectDefinition));
		AssertUtils.assertFailure(
			ObjectLayoutBoxTypeException.class,
			"Object layout box must have a type",
			() -> _addObjectLayout(
				_customObjectDefinition, objectLayoutTab,
				_createObjectLayoutTab(
					_createObjectLayoutBox(StringPool.BLANK))));

		_assertFailureObjectLayoutBoxType(
			ObjectLayoutBoxConstants.TYPE_CATEGORIZATION, "Categorization");
		_assertFailureObjectLayoutBoxType(
			ObjectLayoutBoxConstants.TYPE_SEO, "SEO");

		AssertUtils.assertFailure(
			ObjectLayoutColumnSizeException.class,
			"Object layout column size must be more than 0 and less than 12",
			() -> _addObjectLayout(
				_customObjectDefinition,
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						_createObjectLayoutColumn(
							_customObjectDefinition, null, 13)))));
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationship object fields cannot be associated " +
				"with object layouts",
			() -> _addObjectLayout(
				_objectDefinitionAA,
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						_createObjectLayoutColumn(
							_objectDefinitionAA,
							_objectFieldLocalService.fetchObjectField(
								_objectRelationshipA_AA.
									getObjectFieldId2()))))));
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationships cannot be associated with object " +
				"layout tabs",
			() -> _addObjectLayout(
				_objectDefinitionAA,
				_createObjectLayoutTab(
					_objectRelationshipA_AA.getObjectRelationshipId(),
					_createObjectLayoutBox(
						_createObjectLayoutColumn(_objectDefinitionAA)))));

		_assertObjectLayout(
			false, _addObjectLayout(_customObjectDefinition, objectLayoutTab));
		_assertObjectLayout(
			false,
			_addObjectLayout(
				_modifiableSystemObjectDefinition,
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						_createObjectLayoutColumn(
							_modifiableSystemObjectDefinition)))));
	}

	@Test
	public void testDeleteObjectLayout() throws Exception {
		ObjectLayout objectLayout = _addObjectLayout(
			_customObjectDefinition,
			_createObjectLayoutTab(
				_createObjectLayoutBox(
					_createObjectLayoutColumn(_customObjectDefinition))));

		_assertObjectLayout(false, objectLayout);

		_objectLayoutLocalService.deleteObjectLayout(
			objectLayout.getObjectLayoutId());

		_assertObjectLayout(true, objectLayout);
	}

	@Test
	public void testUpdateObjectLayout() throws Exception {
		ObjectLayout objectLayout1 = _addObjectLayout(
			_objectDefinitionAA,
			_createObjectLayoutTab(
				_createObjectLayoutBox(
					_createObjectLayoutColumn(_objectDefinitionAA))));

		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationship object fields cannot be associated " +
				"with object layouts",
			() -> _objectLayoutLocalService.updateObjectLayout(
				objectLayout1.getObjectLayoutId(), false,
				RandomTestUtil.randomLocaleStringMap(),
				Collections.singletonList(
					_createObjectLayoutTab(
						_createObjectLayoutBox(
							_createObjectLayoutColumn(
								_objectDefinitionAA,
								_objectFieldLocalService.fetchObjectField(
									_objectRelationshipA_AA.
										getObjectFieldId2())))))));
		AssertUtils.assertFailure(
			ObjectRelationshipEdgeException.class,
			"Edge object relationships cannot be associated with object " +
				"layout tabs",
			() -> _objectLayoutLocalService.updateObjectLayout(
				objectLayout1.getObjectLayoutId(), false,
				RandomTestUtil.randomLocaleStringMap(),
				Collections.singletonList(
					_createObjectLayoutTab(
						_objectRelationshipA_AA.getObjectRelationshipId(),
						_createObjectLayoutBox(
							_createObjectLayoutColumn(_objectDefinitionAA))))));

		_assertScreenNavigationCategories(0);

		ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab(
			_createObjectLayoutBox(
				_createObjectLayoutColumn(_customObjectDefinition)));

		ObjectLayout objectLayout2 = _addObjectLayout(
			_customObjectDefinition, true, objectLayoutTab);

		_assertScreenNavigationCategories(1);

		_addObjectLayout(_customObjectDefinition, objectLayoutTab);

		_assertScreenNavigationCategories(1);

		_objectLayoutLocalService.updateObjectLayout(
			objectLayout2.getObjectLayoutId(), false,
			objectLayout2.getNameMap(),
			Collections.singletonList(objectLayoutTab));

		_assertScreenNavigationCategories(0);
	}

	private ObjectField _addCustomObjectField(
			String name, long objectDefinitionId)
		throws Exception {

		return ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				name
			).objectDefinitionId(
				objectDefinitionId
			).required(
				true
			).userId(
				TestPropsValues.getUserId()
			).build());
	}

	private ObjectLayout _addObjectLayout(
			ObjectDefinition objectDefinition, boolean defaultObjectLayout,
			ObjectLayoutTab... objectLayoutTabs)
		throws Exception {

		return _objectLayoutLocalService.addObjectLayout(
			TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), defaultObjectLayout,
			RandomTestUtil.randomLocaleStringMap(),
			Arrays.asList(objectLayoutTabs));
	}

	private ObjectLayout _addObjectLayout(
			ObjectDefinition objectDefinition,
			ObjectLayoutTab... objectLayoutTabs)
		throws Exception {

		return _addObjectLayout(objectDefinition, false, objectLayoutTabs);
	}

	private void _assertFailureObjectLayoutBoxType(
		String objectLayoutBoxType, String objectLayoutBoxTypeLabel) {

		_updateCustomObjectDefinition(
			true, true, RandomTestUtil.randomString());

		ObjectLayoutTab objectLayoutTab = _createObjectLayoutTab(
			_createObjectLayoutBox(objectLayoutBoxType));

		AssertUtils.assertFailure(
			ObjectLayoutBoxTypeException.class,
			objectLayoutBoxTypeLabel +
				" layout box can only be used in object definitions with a " +
					"default storage type",
			() -> _addObjectLayout(_customObjectDefinition, objectLayoutTab));

		_updateCustomObjectDefinition(
			false, false, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);

		AssertUtils.assertFailure(
			ObjectLayoutBoxTypeException.class,
			objectLayoutBoxTypeLabel + " layout box must be enabled to be used",
			() -> _addObjectLayout(_customObjectDefinition, objectLayoutTab));

		_updateCustomObjectDefinition(
			true, true, ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT);

		AssertUtils.assertFailure(
			ObjectLayoutBoxTypeException.class,
			objectLayoutBoxTypeLabel + " layout box must not have layout rows",
			() -> _addObjectLayout(
				_customObjectDefinition,
				_createObjectLayoutTab(
					_createObjectLayoutBox(
						objectLayoutBoxType,
						_createObjectLayoutColumn(_customObjectDefinition)))));
		AssertUtils.assertFailure(
			ObjectLayoutBoxTypeException.class,
			"There can only be one " + objectLayoutBoxType +
				" layout box per layout",
			() -> _addObjectLayout(
				_customObjectDefinition, false, objectLayoutTab,
				objectLayoutTab));
	}

	private void _assertObjectLayout(boolean empty, ObjectLayout objectLayout) {
		Assert.assertEquals(
			empty,
			ListUtil.isEmpty(
				_objectLayoutTabPersistence.findByObjectLayoutId(
					objectLayout.getObjectLayoutId())));

		List<ObjectLayoutTab> objectLayoutTabs =
			objectLayout.getObjectLayoutTabs();

		ObjectLayoutTab objectLayoutTab = objectLayoutTabs.get(0);

		Assert.assertEquals(
			empty,
			ListUtil.isEmpty(
				_objectLayoutBoxPersistence.findByObjectLayoutTabId(
					objectLayoutTab.getObjectLayoutTabId())));

		List<ObjectLayoutBox> objectLayoutBoxes =
			objectLayoutTab.getObjectLayoutBoxes();

		ObjectLayoutBox objectLayoutBox = objectLayoutBoxes.get(0);

		Assert.assertEquals(
			empty,
			ListUtil.isEmpty(
				_objectLayoutRowPersistence.findByObjectLayoutBoxId(
					objectLayoutBox.getObjectLayoutBoxId())));

		List<ObjectLayoutRow> objectLayoutRows =
			objectLayoutBox.getObjectLayoutRows();

		ObjectLayoutRow objectLayoutRow = objectLayoutRows.get(0);

		Assert.assertEquals(
			empty,
			ListUtil.isEmpty(
				_objectLayoutColumnPersistence.findByObjectLayoutRowId(
					objectLayoutRow.getObjectLayoutRowId())));
	}

	private void _assertScreenNavigationCategories(int expectedSize)
		throws Exception {

		List<ScreenNavigationCategory> screenNavigationCategories =
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				_customObjectDefinition.getClassName(),
				TestPropsValues.getUser(), null);

		Assert.assertEquals(
			screenNavigationCategories.toString(), expectedSize,
			screenNavigationCategories.size());
	}

	private ObjectLayoutBox _createObjectLayoutBox(
		ObjectLayoutColumn... objectLayoutColumns) {

		return _createObjectLayoutBox(
			ObjectLayoutBoxConstants.TYPE_REGULAR, objectLayoutColumns);
	}

	private ObjectLayoutBox _createObjectLayoutBox(
		String type, ObjectLayoutColumn... objectLayoutColumns) {

		ObjectLayoutBox objectLayoutBox = _objectLayoutBoxPersistence.create(0);

		objectLayoutBox.setNameMap(RandomTestUtil.randomLocaleStringMap());
		objectLayoutBox.setPriority(0);
		objectLayoutBox.setType(type);

		if (objectLayoutColumns.length > 0) {
			objectLayoutBox.setObjectLayoutRows(
				Collections.singletonList(
					_createObjectLayoutRow(objectLayoutColumns)));
		}

		return objectLayoutBox;
	}

	private ObjectLayoutColumn _createObjectLayoutColumn(
			ObjectDefinition objectDefinition)
		throws Exception {

		return _createObjectLayoutColumn(objectDefinition, null);
	}

	private ObjectLayoutColumn _createObjectLayoutColumn(
			ObjectDefinition objectDefinition, ObjectField objectField)
		throws Exception {

		return _createObjectLayoutColumn(
			objectDefinition, objectField, RandomTestUtil.randomInt(0, 12));
	}

	private ObjectLayoutColumn _createObjectLayoutColumn(
			ObjectDefinition objectDefinition, ObjectField objectField,
			int size)
		throws Exception {

		ObjectLayoutColumn objectLayoutColumn =
			_objectLayoutColumnPersistence.create(0);

		if (objectField == null) {
			objectField = _objectFieldLocalService.fetchObjectField(
				objectDefinition.getObjectDefinitionId(), _OBJECT_FIELD_NAME);

			if (objectField == null) {
				objectField = _addCustomObjectField(
					_OBJECT_FIELD_NAME,
					objectDefinition.getObjectDefinitionId());
			}
		}

		objectLayoutColumn.setObjectFieldId(objectField.getObjectFieldId());

		objectLayoutColumn.setPriority(0);
		objectLayoutColumn.setSize(size);

		return objectLayoutColumn;
	}

	private ObjectLayoutRow _createObjectLayoutRow(
		ObjectLayoutColumn... objectLayoutColumns) {

		ObjectLayoutRow objectLayoutRow = _objectLayoutRowPersistence.create(0);

		objectLayoutRow.setPriority(0);
		objectLayoutRow.setObjectLayoutColumns(
			Arrays.asList(objectLayoutColumns));

		return objectLayoutRow;
	}

	private ObjectLayoutTab _createObjectLayoutTab(
		long objectRelationshipId, ObjectLayoutBox... objectLayoutBoxes) {

		ObjectLayoutTab objectLayoutTab = _objectLayoutTabPersistence.create(0);

		objectLayoutTab.setObjectRelationshipId(objectRelationshipId);
		objectLayoutTab.setNameMap(RandomTestUtil.randomLocaleStringMap());
		objectLayoutTab.setObjectLayoutBoxes(Arrays.asList(objectLayoutBoxes));

		return objectLayoutTab;
	}

	private ObjectLayoutTab _createObjectLayoutTab(
		ObjectLayoutBox... objectLayoutBoxes) {

		return _createObjectLayoutTab(0, objectLayoutBoxes);
	}

	private void _updateCustomObjectDefinition(
		boolean enableCategorization, boolean enableFriendlyURLCustomization,
		String storageType) {

		_customObjectDefinition.setEnableCategorization(enableCategorization);
		_customObjectDefinition.setEnableFriendlyURLCustomization(
			enableFriendlyURLCustomization);
		_customObjectDefinition.setStorageType(storageType);

		_objectDefinitionLocalService.updateObjectDefinition(
			_customObjectDefinition);
	}

	private static final String _OBJECT_FIELD_NAME =
		"x" + RandomTestUtil.randomString();

	private static ObjectDefinition _objectDefinitionA;
	private static ObjectDefinition _objectDefinitionAA;
	private static ObjectRelationship _objectRelationshipA_AA;

	@Inject
	private static ObjectRelationshipLocalService
		_objectRelationshipLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _customObjectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _modifiableSystemObjectDefinition;

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

	@DeleteAfterTestRun
	private ObjectDefinition _unmodifiableSystemObjectDefinition;

}