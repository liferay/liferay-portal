/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.vulcan.extension.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Product;
import com.liferay.list.type.entry.util.ListTypeEntryUtil;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.MultiselectPicklistObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ListEntry;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.extension.ExtensionProvider;
import com.liferay.portal.vulcan.extension.PropertyDefinition;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.sql.Timestamp;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class ObjectEntryExtensionProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), User.class.getName());

		ObjectFieldUtil.addCustomObjectField(
			new BooleanObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).descriptionMap(
				Collections.singletonMap(
					LocaleUtil.US, _OBJECT_FIELD_DESCRIPTION)
			).indexed(
				RandomTestUtil.randomBoolean()
			).indexedAsKeyword(
				RandomTestUtil.randomBoolean()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"boolean"
			).build());
		ObjectFieldUtil.addCustomObjectField(
			new DateObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).indexed(
				RandomTestUtil.randomBoolean()
			).indexedAsKeyword(
				RandomTestUtil.randomBoolean()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"date"
			).required(
				true
			).build());
		ObjectFieldUtil.addCustomObjectField(
			new DecimalObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).indexed(
				RandomTestUtil.randomBoolean()
			).indexedAsKeyword(
				RandomTestUtil.randomBoolean()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"decimal"
			).build());

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				null, TestPropsValues.getUserId(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false,
				Arrays.asList(
					ListTypeEntryUtil.createListTypeEntry(
						_LIST_TYPE_ENTRY_KEY_1),
					ListTypeEntryUtil.createListTypeEntry(
						_LIST_TYPE_ENTRY_KEY_2)),
				new ServiceContext());

		ObjectFieldUtil.addCustomObjectField(
			new MultiselectPicklistObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).indexed(
				RandomTestUtil.randomBoolean()
			).indexedAsKeyword(
				RandomTestUtil.randomBoolean()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).listTypeDefinitionId(
				listTypeDefinition.getListTypeDefinitionId()
			).name(
				"multiselectPicklist"
			).build());

		ObjectFieldUtil.addCustomObjectField(
			new PrecisionDecimalObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).objectDefinitionId(
				objectDefinition.getObjectDefinitionId()
			).indexed(
				RandomTestUtil.randomBoolean()
			).indexedAsKeyword(
				RandomTestUtil.randomBoolean()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"precisionDecimal"
			).required(
				true
			).build());

		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetExtendedPropertyDefinitions() throws Exception {
		Map<String, PropertyDefinition> extendedPropertyDefinitions =
			_extensionProvider.getExtendedPropertyDefinitions(
				TestPropsValues.getCompanyId(), UserAccount.class.getName());

		_assertPropertyDefinition(
			null, _OBJECT_FIELD_DESCRIPTION, "boolean",
			PropertyDefinition.PropertyType.BOOLEAN, false,
			extendedPropertyDefinitions.get("boolean"));
		_assertPropertyDefinition(
			null, null, "date", PropertyDefinition.PropertyType.DATE_TIME, true,
			extendedPropertyDefinitions.get("date"));
		_assertPropertyDefinition(
			null, null, "decimal", PropertyDefinition.PropertyType.DOUBLE,
			false, extendedPropertyDefinitions.get("decimal"));
		_assertPropertyDefinition(
			ListEntry.class.getSimpleName(), null, "multiselectPicklist",
			PropertyDefinition.PropertyType.MULTIPLE_ELEMENT, false,
			extendedPropertyDefinitions.get("multiselectPicklist"));
		_assertPropertyDefinition(
			null, null, "precisionDecimal",
			PropertyDefinition.PropertyType.BIG_DECIMAL, true,
			extendedPropertyDefinitions.get("precisionDecimal"));
	}

	@Test
	public void testIsApplicableExtension() throws Exception {
		Assert.assertFalse(
			_extensionProvider.isApplicableExtension(
				TestPropsValues.getCompanyId(), null));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, null, true, false,
				true, false, true, false, false, false, false, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				ObjectDefinitionTestUtil.getRandomName(), null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				true, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Arrays.asList(
					ObjectFieldUtil.createObjectField(
						ObjectFieldConstants.BUSINESS_TYPE_TEXT,
						ObjectFieldConstants.DB_TYPE_STRING,
						RandomTestUtil.randomString(), StringUtil.randomId())),
				Collections.emptyList(), new ServiceContext());

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		Assert.assertFalse(
			_extensionProvider.isApplicableExtension(
				TestPropsValues.getCompanyId(),
				ObjectEntry.class.getName() + "#" +
					objectDefinition.getName()));

		_objectDefinitionLocalService.deleteObjectDefinition(
			objectDefinition.getObjectDefinitionId());

		Assert.assertTrue(
			_extensionProvider.isApplicableExtension(
				TestPropsValues.getCompanyId(), UserAccount.class.getName()));
	}

	@Test
	public void testSetAndGetExtendedProperties() throws Exception {
		_testSetAndGetExtendedPropertiesWithCommerceProduct();
		_testSetAndGetExtendedPropertiesWithUserAccount();
	}

	private void _assertPropertyDefinition(
		String expectedPropertyClassName, String expectedPropertyDescription,
		String expectedPropertyName,
		PropertyDefinition.PropertyType expectedPropertyType,
		boolean expectedRequired, PropertyDefinition propertyDefinition) {

		Assert.assertEquals(
			expectedPropertyClassName,
			propertyDefinition.getPropertyClassName());
		Assert.assertEquals(
			expectedPropertyDescription,
			propertyDefinition.getPropertyDescription());
		Assert.assertEquals(
			expectedPropertyName, propertyDefinition.getPropertyName());
		Assert.assertEquals(
			expectedPropertyType, propertyDefinition.getPropertyType());
		Assert.assertEquals(expectedRequired, propertyDefinition.isRequired());
	}

	private void _assertSetAndGetExtendedPropertiesWithCommerceProduct(
			String customFieldName, Object entity)
		throws Exception {

		String customFieldValue = RandomTestUtil.randomString();

		_extensionProvider.setExtendedProperties(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			Product.class.getName(), entity,
			HashMapBuilder.<String, Serializable>put(
				customFieldName, customFieldValue
			).build());

		Map<String, Serializable> extendedProperties =
			_extensionProvider.getExtendedProperties(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				Product.class.getName(), entity);

		Assert.assertEquals(
			customFieldValue, extendedProperties.get(customFieldName));
	}

	private void _testSetAndGetExtendedPropertiesWithCommerceProduct()
		throws Exception {

		CommerceCatalog commerceCatalog = CPTestUtil.getSystemCommerceCatalog(
			TestPropsValues.getCompanyId());

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		ObjectDefinition cpDefinitionObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), CPDefinition.class.getName());

		String customFieldName = "x" + RandomTestUtil.randomString();

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).objectDefinitionId(
				cpDefinitionObjectDefinition.getObjectDefinitionId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				customFieldName
			).build());

		try {
			Product product = new Product() {
				{
					id = cpDefinition.getCPDefinitionId();
					productId = cpDefinition.getCProductId();
				}
			};

			_assertSetAndGetExtendedPropertiesWithCommerceProduct(
				customFieldName,
				HashMapBuilder.<String, Object>put(
					"id", cpDefinition.getCPDefinitionId()
				).put(
					"productId", cpDefinition.getCProductId()
				).build());
			_assertSetAndGetExtendedPropertiesWithCommerceProduct(
				customFieldName, product);
		}
		finally {
			PersistedModelLocalService persistedModelLocalService =
				PersistedModelLocalServiceRegistryUtil.
					getPersistedModelLocalService(CPDefinition.class.getName());

			persistedModelLocalService.deletePersistedModel(cpDefinition);

			_objectFieldLocalService.deleteObjectField(objectField);
		}
	}

	private void _testSetAndGetExtendedPropertiesWithUserAccount()
		throws Exception {

		UserAccount userAccount = new UserAccount() {
			{
				id = _user.getUserId();
			}
		};

		_testSetExtendedProperties(
			userAccount,
			HashMapBuilder.<String, Serializable>put(
				"boolean", true
			).put(
				"date", "2010-12-25"
			).put(
				"decimal", 1.2
			).put(
				"multiselectPicklist",
				new String[] {_LIST_TYPE_ENTRY_KEY_1, _LIST_TYPE_ENTRY_KEY_2}
			).put(
				"precisionDecimal", 100.5
			).build());
		_testSetExtendedProperties(
			userAccount,
			HashMapBuilder.<String, Serializable>put(
				"boolean", false
			).put(
				"date", "2020-07-30"
			).put(
				"decimal", 10.8
			).put(
				"multiselectPicklist", new String[] {_LIST_TYPE_ENTRY_KEY_1}
			).put(
				"precisionDecimal", 20.55
			).build());
	}

	private void _testSetExtendedProperties(
			UserAccount userAccount, Map<String, Serializable> values)
		throws Exception {

		_extensionProvider.setExtendedProperties(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			UserAccount.class.getName(), userAccount, new HashMap<>(values));

		Map<String, Serializable> extendedProperties =
			_extensionProvider.getExtendedProperties(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				UserAccount.class.getName(), userAccount);

		Assert.assertEquals(
			values.get("boolean"), extendedProperties.get("boolean"));

		Date date = DateUtil.parseDate(
			"yyyy-MM-dd", String.valueOf(values.get("date")),
			LocaleUtil.getSiteDefault());

		Assert.assertEquals(
			new Timestamp(date.getTime()), extendedProperties.get("date"));

		Assert.assertEquals(
			values.get("decimal"), extendedProperties.get("decimal"));

		Assert.assertEquals(
			Arrays.asList((String[])values.get("multiselectPicklist")),
			TransformUtil.transform(
				(List<ListEntry>)extendedProperties.get("multiselectPicklist"),
				ListEntry::getKey));

		Assert.assertEquals(
			new BigDecimal(String.valueOf(values.get("precisionDecimal"))),
			extendedProperties.get("precisionDecimal"));
	}

	private static final String _LIST_TYPE_ENTRY_KEY_1 =
		RandomTestUtil.randomString();

	private static final String _LIST_TYPE_ENTRY_KEY_2 =
		RandomTestUtil.randomString();

	private static final String _OBJECT_FIELD_DESCRIPTION =
		RandomTestUtil.randomString();

	@Inject
	private static ListTypeDefinitionLocalService
		_listTypeDefinitionLocalService;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static User _user;

	@Inject(
		filter = "component.name=com.liferay.object.rest.internal.vulcan.extension.v1_0.ObjectEntryExtensionProvider"
	)
	private ExtensionProvider _extensionProvider;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

}