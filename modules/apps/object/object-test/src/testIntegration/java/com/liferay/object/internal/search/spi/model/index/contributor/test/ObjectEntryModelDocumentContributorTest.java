/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.AssigneeObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.FieldArray;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Jhosseph Gonzalez
 */
@RunWith(Arquillian.class)
public class ObjectEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testContributeWithAssigneeObjectField() throws Exception {
		String objectFieldName = "a" + RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectFieldUtil.addCustomObjectField(
			new AssigneeObjectFieldBuilder(
			).indexed(
				true
			).labelMap(
				RandomTestUtil.randomLocaleStringMap()
			).name(
				objectFieldName
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).userId(
				TestPropsValues.getUserId()
			).build());

		objectDefinition = _objectDefinitionLocalService.getObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		ModelDocumentContributor<ObjectEntry>
			objectEntryModelDocumentContributor =
				_getObjectEntryModelDocumentContributor(objectDefinition);

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		long roleClassNameId = _classNameLocalService.getClassNameId(
			Role.class.getName());
		long roleClassPK = role.getRoleId();

		ObjectEntry roleObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName,
				HashMapBuilder.put(
					"classNameId", roleClassNameId
				).put(
					"classPK", roleClassPK
				).build()
			).build());

		Document roleDocument = new DocumentImpl();

		objectEntryModelDocumentContributor.contribute(
			roleDocument, roleObjectEntry);

		Field roleField = roleDocument.getField("objectEntryContent");

		Assert.assertNotNull(roleField);

		String roleValue = roleField.getValue();

		Assert.assertTrue(
			roleValue,
			roleValue.contains(
				StringBundler.concat(
					objectFieldName, ": ", roleClassNameId, "_", roleClassPK)));
		Assert.assertTrue(
			roleValue,
			roleValue.contains(
				StringBundler.concat(objectFieldName, ": ", role.getName())));

		User user = UserTestUtil.addUser();

		long userClassNameId = _classNameLocalService.getClassNameId(
			User.class.getName());
		long userClassPK = user.getUserId();

		ObjectEntry userObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName,
				HashMapBuilder.put(
					"classNameId", userClassNameId
				).put(
					"classPK", userClassPK
				).build()
			).build());

		Document userDocument = new DocumentImpl();

		objectEntryModelDocumentContributor.contribute(
			userDocument, userObjectEntry);

		Field userField = userDocument.getField("objectEntryContent");

		Assert.assertNotNull(userField);

		String value = userField.getValue();

		Assert.assertTrue(
			value,
			value.contains(
				StringBundler.concat(
					objectFieldName, ": ", userClassNameId, "_", userClassPK)));
		Assert.assertTrue(
			value,
			value.contains(
				StringBundler.concat(
					objectFieldName, ": ", user.getFullName())));
	}

	@Test
	public void testContributeWithDateField() throws Exception {
		ObjectDefinition objectDefinition =
			_addModifiableSystemObjectDefinition(
				false, "a" + RandomTestUtil.randomString());

		ModelDocumentContributor<ObjectEntry>
			objectEntryModelDocumentContributor =
				_getObjectEntryModelDocumentContributor(objectDefinition);

		Document document = new DocumentImpl();

		Date displayDate = new Date();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			TestPropsValues.getGroupId(), objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				Field.DISPLAY_DATE, displayDate
			).build());

		objectEntryModelDocumentContributor.contribute(document, objectEntry);

		Field field = document.getField(Field.DISPLAY_DATE);

		Assert.assertEquals(
			DateUtil.getDate(displayDate, "yyyyMMddHHmmss", LocaleUtil.US),
			field.getValue());
	}

	@Test
	public void testContributeWithLocalizedFields() throws Exception {
		String objectFieldName = "a" + RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			_addModifiableSystemObjectDefinition(true, objectFieldName);

		ModelDocumentContributor<ObjectEntry>
			objectEntryModelDocumentContributor =
				_getObjectEntryModelDocumentContributor(objectDefinition);

		Document document = new DocumentImpl();

		String englishObjectFieldValue = RandomTestUtil.randomString();
		String portugueseObjectFieldValue =
			objectFieldName + RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			TestPropsValues.getGroupId(), objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, englishObjectFieldValue
			).put(
				objectFieldName + "_i18n",
				HashMapBuilder.<String, Serializable>put(
					"en_US", englishObjectFieldValue
				).put(
					"pt_BR", portugueseObjectFieldValue
				).build()
			).build());

		objectEntryModelDocumentContributor.contribute(document, objectEntry);

		_assertObjectEntryContentField(
			document, englishObjectFieldValue,
			Field.getLocalizedName(LocaleUtil.US, "objectEntryContent"),
			objectFieldName);
		_assertObjectEntryContentField(
			document, portugueseObjectFieldValue,
			Field.getLocalizedName(LocaleUtil.BRAZIL, "objectEntryContent"),
			objectFieldName);

		Assert.assertNull(document.getField("objectEntryContent"));
	}

	@Test
	public void testContributeWithMultiselectPicklistObjectField()
		throws Exception {

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				false, Collections.emptyList(), new ServiceContext());

		for (String listTypeEntryKey : _LIST_TYPE_ENTRY_KEYS) {
			_listTypeEntryLocalService.addListTypeEntry(
				null, TestPropsValues.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(), listTypeEntryKey,
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				listTypeDefinition.isSystem());
		}

		String keywordIndexedObjectFieldName =
			"a" + RandomTestUtil.randomString();
		String textIndexedObjectFieldName = "a" + RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Arrays.asList(
					new MultiselectPicklistObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).listTypeDefinitionId(
						listTypeDefinition.getListTypeDefinitionId()
					).name(
						keywordIndexedObjectFieldName
					).build(),
					new MultiselectPicklistObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).listTypeDefinitionId(
						listTypeDefinition.getListTypeDefinitionId()
					).name(
						textIndexedObjectFieldName
					).build()),
				false);

		ModelDocumentContributor<ObjectEntry>
			objectEntryModelDocumentContributor =
				_getObjectEntryModelDocumentContributor(objectDefinition);

		Document document = new DocumentImpl();

		String objectFieldValue = StringUtil.merge(
			_LIST_TYPE_ENTRY_KEYS, StringPool.COMMA_AND_SPACE);

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				keywordIndexedObjectFieldName, objectFieldValue
			).put(
				textIndexedObjectFieldName, objectFieldValue
			).build());

		objectEntryModelDocumentContributor.contribute(document, objectEntry);

		_testContributeWithMultiselectPicklistObjectField(
			document, keywordIndexedObjectFieldName);
		_testContributeWithMultiselectPicklistObjectField(
			document, textIndexedObjectFieldName);

		Field valueField = _getNestedField(
			document, textIndexedObjectFieldName, "value_en_US");

		Assert.assertEquals(objectFieldValue, valueField.getValue());

		valueField = _getNestedField(
			document, textIndexedObjectFieldName, "value_keyword_lowercase");

		Assert.assertEquals(objectFieldValue, valueField.getValue());
	}

	@Test
	public void testContributeWithNonlocalizedFields() throws Exception {
		String objectFieldName = "a" + RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			_addModifiableSystemObjectDefinition(false, objectFieldName);

		ModelDocumentContributor<ObjectEntry>
			objectEntryModelDocumentContributor =
				_getObjectEntryModelDocumentContributor(objectDefinition);

		Document document = new DocumentImpl();

		String objectFieldValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			TestPropsValues.getGroupId(), objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, objectFieldValue
			).build());

		objectEntryModelDocumentContributor.contribute(document, objectEntry);

		_assertObjectEntryContentField(
			document, objectFieldValue, "objectEntryContent", objectFieldName);

		Assert.assertNull(
			document.getField(
				Field.getLocalizedName(LocaleUtil.US, "objectEntryContent")));
	}

	@Test
	public void testContributeWithObjectEntryFolder() throws Exception {
		String objectFieldName = "a" + RandomTestUtil.randomString();

		ObjectDefinition objectDefinition =
			_addModifiableSystemObjectDefinition(false, objectFieldName);

		ModelDocumentContributor<ObjectEntry>
			objectEntryModelDocumentContributor =
				_getObjectEntryModelDocumentContributor(objectDefinition);

		Document document = new DocumentImpl();

		ObjectEntryFolder parentObjectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder();

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder(
				TestPropsValues.getGroupId(),
				parentObjectEntryFolder.getObjectEntryFolderId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			TestPropsValues.getGroupId(), objectDefinition,
			objectEntryFolder.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				objectFieldName, RandomTestUtil.randomString()
			).build());

		objectEntryModelDocumentContributor.contribute(document, objectEntry);

		Field field = document.getField(Field.TREE_PATH);

		Assert.assertArrayEquals(
			new String[] {
				StringPool.BLANK,
				String.valueOf(
					parentObjectEntryFolder.getObjectEntryFolderId()),
				String.valueOf(objectEntryFolder.getObjectEntryFolderId())
			},
			field.getValues());
	}

	private ObjectDefinition _addModifiableSystemObjectDefinition(
			boolean localized, String objectFieldName)
		throws Exception {

		ObjectField objectField = ObjectFieldUtil.createObjectField(
			0, ObjectFieldConstants.BUSINESS_TYPE_TEXT, null,
			ObjectFieldConstants.DB_TYPE_STRING, true, false, null,
			RandomTestUtil.randomString(), objectFieldName, false, true);

		objectField.setLocalized(localized);

		ObjectDefinition modifiableSystemObjectDefinition =
			ObjectDefinitionTestUtil.addModifiableSystemObjectDefinition(
				TestPropsValues.getUserId(), null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test" + ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionConstants.SCOPE_SITE, null, 1,
				Arrays.asList(objectField));

		return _objectDefinitionLocalService.publishSystemObjectDefinition(
			TestPropsValues.getUserId(),
			modifiableSystemObjectDefinition.getObjectDefinitionId());
	}

	private void _assertObjectEntryContentField(
		Document document, String expectedValue, String fieldName,
		String objectFieldName) {

		Field field = document.getField(fieldName);

		String value = field.getValue();

		Assert.assertTrue(
			value,
			value.contains(
				StringBundler.concat(objectFieldName, ": ", expectedValue)));
	}

	private Field _getNestedField(
		Document document, String objectFieldName, String valueFieldName) {

		FieldArray fieldArray = (FieldArray)document.getField(
			"nestedFieldArray");

		for (Field field : fieldArray.getFields()) {
			Field valueField = null;

			String fieldName = null;

			for (Field childField : field.getFields()) {
				if (StringUtil.equals(childField.getName(), "fieldName")) {
					fieldName = childField.getValue();
				}
				else if (StringUtil.equals(
							childField.getName(), valueFieldName)) {

					valueField = childField;
				}
			}

			if (StringUtil.equals(fieldName, objectFieldName) &&
				(valueField != null)) {

				return valueField;
			}
		}

		return null;
	}

	private ModelDocumentContributor<ObjectEntry>
			_getObjectEntryModelDocumentContributor(
				ObjectDefinition objectDefinition)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryModelDocumentContributorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<ServiceReference<ModelDocumentContributor<ObjectEntry>>>
			serviceReferences = new ArrayList<>(
				bundleContext.getServiceReferences(
					(Class<ModelDocumentContributor<ObjectEntry>>)
						(Class<?>)ModelDocumentContributor.class,
					"(indexer.class.name=" + objectDefinition.getClassName() +
						")"));

		return bundleContext.getService(serviceReferences.get(0));
	}

	private void _testContributeWithMultiselectPicklistObjectField(
		Document document, String objectFieldName) {

		Field valueKeywordField = _getNestedField(
			document, objectFieldName, "value_keyword");

		String[] expectedValues = _LIST_TYPE_ENTRY_KEYS.clone();

		StringUtil.lowerCase(expectedValues);

		Assert.assertArrayEquals(expectedValues, valueKeywordField.getValues());
	}

	private static final String[] _LIST_TYPE_ENTRY_KEYS = {
		"listTypeEntryKey1", "listTypeEntryKey2"
	};

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}