/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.storage.sugarcrm.internal.rest.manager.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.dto.v1_0.ObjectEntry;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.rest.test.util.BaseObjectEntryManagerImplTestCase;
import com.liferay.object.storage.sugarcrm.configuration.SugarCRMConfiguration;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Maurice Sepe
 */
@FeatureFlags("LPS-135430")
@RunWith(Arquillian.class)
public class SugarCRMObjectEntryManagerImplTest
	extends BaseObjectEntryManagerImplTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		adminUser = TestPropsValues.getUser();

		companyId = TestPropsValues.getCompanyId();

		_configurationProvider.saveCompanyConfiguration(
			SugarCRMConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"accessTokenURL",
				TestPropsUtil.get("object.storage.sugarcrm.access.token.url")
			).put(
				"baseURL", TestPropsUtil.get("object.storage.sugarcrm.base.url")
			).put(
				"clientId",
				TestPropsUtil.get("object.storage.sugarcrm.client.id")
			).put(
				"grantType",
				TestPropsUtil.get("object.storage.sugarcrm.grant.type")
			).put(
				"password",
				TestPropsUtil.get("object.storage.sugarcrm.password")
			).put(
				"username",
				TestPropsUtil.get("object.storage.sugarcrm.username")
			).build());

		_simpleDateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXXX");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_configurationProvider.saveCompanyConfiguration(
			SugarCRMConfiguration.class, companyId,
			HashMapDictionaryBuilder.<String, Object>put(
				"accessTokenUrl", ""
			).put(
				"baseUrl", ""
			).put(
				"clientId", ""
			).put(
				"grantType", ""
			).put(
				"password", ""
			).put(
				"username", ""
			).build());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_objectDefinition =
			objectDefinitionLocalService.addCustomObjectDefinition(
				adminUser.getUserId(), 0, null, false, false, true, false,
				false, LocalizedMapUtil.getLocalizedMap("Contact"), "Contact",
				null, null, LocalizedMapUtil.getLocalizedMap("Contacts"), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_SUGARCRM,
				Collections.emptyList(), Collections.emptyList());

		_objectDefinition.setExternalReferenceCode("Contacts");

		ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).externalReferenceCode(
				"first_name"
			).userId(
				adminUser.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("First Name")
			).name(
				"firstName"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).externalReferenceCode(
				"last_name"
			).userId(
				adminUser.getUserId()
			).labelMap(
				LocalizedMapUtil.getLocalizedMap("Last Name")
			).name(
				"lastName"
			).objectDefinitionId(
				_objectDefinition.getObjectDefinitionId()
			).build());

		_objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		_objectDefinition = objectDefinitionLocalService.updateObjectDefinition(
			_objectDefinition);

		_objectDefinition =
			objectDefinitionLocalService.publishCustomObjectDefinition(
				adminUser.getUserId(),
				_objectDefinition.getObjectDefinitionId());
	}

	@After
	public void tearDown() throws Exception {
		for (ObjectEntry objectEntry : _objectEntries) {
			_objectEntryManager.deleteObjectEntry(
				companyId, dtoConverterContext,
				objectEntry.getExternalReferenceCode(), _objectDefinition,
				ObjectDefinitionConstants.SCOPE_COMPANY);
		}

		if (_objectDefinition != null) {
			objectDefinitionLocalService.deleteObjectDefinition(
				_objectDefinition.getObjectDefinitionId());
		}
	}

	@Test
	public void testAddObjectEntry() throws Exception {
		String firstName = RandomTestUtil.randomString();
		String lastName = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(firstName, lastName);

		Assert.assertNotNull(objectEntry.getExternalReferenceCode());
	}

	@Test
	public void testGetObjectEntries() throws Exception {
		String firstName1 = "a" + RandomTestUtil.randomString();
		String lastName1 = "a" + RandomTestUtil.randomString();

		ObjectEntry objectEntry1 = _addObjectEntry(firstName1, lastName1);

		testGetObjectEntries(
			HashMapBuilder.put(
				"filter", "firstName eq " + StringUtil.quote(firstName1)
			).build(),
			objectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					"firstName eq ", StringUtil.quote(firstName1), " and ",
					"lastName eq ", StringUtil.quote(lastName1))
			).build(),
			objectEntry1);
		testGetObjectEntries(
			HashMapBuilder.put(
				"filter",
				StringBundler.concat(
					"firstName eq ", StringUtil.quote(firstName1), " or ",
					"lastName eq ",
					StringUtil.quote(RandomTestUtil.randomString()))
			).build(),
			objectEntry1);
	}

	@Test
	public void testGetObjectEntry() throws Exception {
		String firstName = RandomTestUtil.randomString();
		String lastName = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(firstName, lastName);

		_assertObjectEntry(
			objectEntry.getExternalReferenceCode(), firstName, lastName);
	}

	@Test
	public void testUpdateObjectEntry() throws Exception {
		String firstName = RandomTestUtil.randomString();
		String lastName = RandomTestUtil.randomString();

		ObjectEntry objectEntry = _addObjectEntry(firstName, lastName);

		String updatedFirstName = RandomTestUtil.randomString();

		objectEntry.setProperties(
			HashMapBuilder.putAll(
				objectEntry.getProperties()
			).put(
				"firstName", updatedFirstName
			).build());

		objectEntry = _objectEntryManager.updateObjectEntry(
			companyId, dtoConverterContext,
			objectEntry.getExternalReferenceCode(), _objectDefinition,
			objectEntry, ObjectDefinitionConstants.SCOPE_COMPANY);

		Assert.assertEquals(
			updatedFirstName,
			MapUtil.getString(objectEntry.getProperties(), "firstName"));
	}

	@Override
	protected Page<ObjectEntry> getObjectEntries(
			Map<String, String> context, Sort[] sorts)
		throws Exception {

		sorts = new Sort[] {SortFactoryUtil.create("firstName", false)};

		return _objectEntryManager.getObjectEntries(
			companyId, _objectDefinition, null, null, dtoConverterContext,
			context.get("filter"), Pagination.of(1, 20), context.get("search"),
			sorts);
	}

	private ObjectEntry _addObjectEntry(String firstName, String lastName)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryManager.addObjectEntry(
			dtoConverterContext, _objectDefinition,
			new ObjectEntry() {
				{
					properties = HashMapBuilder.<String, Object>put(
						"firstName", firstName
					).put(
						"lastName", lastName
					).build();
				}
			},
			ObjectDefinitionConstants.SCOPE_COMPANY);

		_objectEntries.add(objectEntry);

		return objectEntry;
	}

	private void _assertObjectEntry(
			String externalReferenceCode, String firstName, String lastName)
		throws Exception {

		ObjectEntry objectEntry = _objectEntryManager.getObjectEntry(
			companyId, dtoConverterContext, externalReferenceCode,
			_objectDefinition, ObjectDefinitionConstants.SCOPE_COMPANY);

		Assert.assertEquals(
			firstName,
			MapUtil.getString(objectEntry.getProperties(), "firstName"));
		Assert.assertEquals(
			lastName,
			MapUtil.getString(objectEntry.getProperties(), "lastName"));
	}

	@Inject
	private static ConfigurationProvider _configurationProvider;

	private static DateFormat _simpleDateFormat;

	private ObjectDefinition _objectDefinition;
	private final List<ObjectEntry> _objectEntries = new ArrayList<>();

	@Inject(
		filter = "object.entry.manager.storage.type=" + ObjectDefinitionConstants.STORAGE_TYPE_SUGARCRM
	)
	private ObjectEntryManager _objectEntryManager;

}