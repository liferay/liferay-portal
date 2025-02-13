/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.vulcan.extension.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.user.dto.v1_0.UserAccount;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.field.builder.BooleanObjectFieldBuilder;
import com.liferay.object.field.builder.DateObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
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
			"boolean", PropertyDefinition.PropertyType.BOOLEAN, false,
			extendedPropertyDefinitions.get("boolean"));
		_assertPropertyDefinition(
			"date", PropertyDefinition.PropertyType.DATE_TIME, true,
			extendedPropertyDefinitions.get("date"));
		_assertPropertyDefinition(
			"decimal", PropertyDefinition.PropertyType.DOUBLE, false,
			extendedPropertyDefinitions.get("decimal"));
		_assertPropertyDefinition(
			"precisionDecimal", PropertyDefinition.PropertyType.BIG_DECIMAL,
			true, extendedPropertyDefinitions.get("precisionDecimal"));
	}

	@Test
	public void testIsApplicableExtension() throws Exception {
		Assert.assertFalse(
			_extensionProvider.isApplicableExtension(
				TestPropsValues.getCompanyId(), null));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				TestPropsValues.getUserId(), 0, null, false, false, true, false,
				false,
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
						RandomTestUtil.randomString(), StringUtil.randomId())));

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
				"precisionDecimal", 20.55
			).build());
	}

	private void _assertPropertyDefinition(
		String expectedPropertyName,
		PropertyDefinition.PropertyType expectedPropertyType,
		boolean expectedRequired, PropertyDefinition propertyDefinition) {

		Assert.assertEquals(
			expectedPropertyName, propertyDefinition.getPropertyName());
		Assert.assertEquals(
			expectedPropertyType, propertyDefinition.getPropertyType());
		Assert.assertEquals(expectedRequired, propertyDefinition.isRequired());
	}

	private void _testSetExtendedProperties(
			UserAccount userAccount, Map<String, Serializable> values)
		throws Exception {

		_extensionProvider.setExtendedProperties(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			UserAccount.class.getName(), userAccount, values);

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
			new BigDecimal(String.valueOf(values.get("precisionDecimal"))),
			extendedProperties.get("precisionDecimal"));
	}

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private static ObjectFieldLocalService _objectFieldLocalService;

	private static User _user;

	@Inject(
		filter = "component.name=com.liferay.object.rest.internal.vulcan.extension.v1_0.ObjectEntryExtensionProvider"
	)
	private ExtensionProvider _extensionProvider;

}