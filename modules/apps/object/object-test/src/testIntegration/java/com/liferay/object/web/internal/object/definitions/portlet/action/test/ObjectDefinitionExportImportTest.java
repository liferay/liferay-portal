/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.object.definitions.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.admin.list.type.dto.v1_0.ListTypeDefinition;
import com.liferay.headless.admin.list.type.resource.v1_0.ListTypeDefinitionResource;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.dto.v1_0.Status;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.web.internal.BaseExportImportTestCase;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Gabriel Albuquerque
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionExportImportTest extends BaseExportImportTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@AfterClass
	public static void tearDownClass() throws Exception {
		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionResource.
				getListTypeDefinitionByExternalReferenceCode(
					"LIST_APPLICATION_STATES");

		_listTypeDefinitionResource.deleteListTypeDefinition(
			listTypeDefinition.getId());
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ListTypeDefinitionResource.Builder builder =
			_listTypeDefinitionResourceFactory.create();

		_listTypeDefinitionResource = builder.user(
			user
		).build();
	}

	@Test
	public void testExportImportLocalizedObjectDefinition() throws Exception {

		// Localized modifiable system object definition

		testExportImport(
			"test-modifiable-system-object-definition.portuguese-default-" +
				"locale.json",
			"test-modifiable-system-object-definition.site-default-locale.json",
			"TESTMODIFIABLESYSTEMOBJECTDEFINITIONPORTUGUESE",
			"TestModifiableSystemObjectDefinitionptBR");

		objectDefinitionResource.deleteObjectDefinition(
			getId("TestModifiableSystemObjectDefinitionptBR"));

		// Localized object definition

		testExportImport(
			"test-object-definition.portuguese-default-locale.json",
			"test-object-definition.site-default-locale.json",
			"TESTOBJECTDEFINITIONPORTUGUESE", "TestObjectDefinitionptBR");

		// Localized object definition update

		testExportImport(
			"test-object-definition.portuguese-default-locale.json",
			"test-object-definition.site-default-locale.json",
			"TESTOBJECTDEFINITIONPORTUGUESE", "TestObjectDefinitionptBR");

		objectDefinitionResource.deleteObjectDefinition(
			getId("TestObjectDefinitionptBR"));

		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			User user = UserTestUtil.getAdminUser(company.getCompanyId());

			PrincipalThreadLocal.setName(user.getUserId());

			// Localized object definition with portuguese locale removed from
			// the company available locales

			CompanyTestUtil.resetCompanyLocales(
				company.getCompanyId(),
				SetUtil.fromArray(LocaleUtil.SPAIN, LocaleUtil.US),
				LocaleUtil.US);

			LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.US);

			String expectedJSON = read(
				"test-object-definition.site-default-locale.json");

			testExportImportJSON(
				read("test-object-definition.portuguese-default-locale.json"),
				expectedJSON.replaceAll(
					",[\n\t]+\"pt_BR\": \"(.*?)\"", StringPool.BLANK),
				"TESTOBJECTDEFINITIONPORTUGUESEREMOVED",
				"TestObjectDefinitionptBRRemoved");

			objectDefinitionResource.deleteObjectDefinition(
				getId("TestObjectDefinitionptBRRemoved"));

			// Localized object definition with United States english locale
			// removed from the company available locales

			CompanyTestUtil.resetCompanyLocales(
				company.getCompanyId(), SetUtil.fromArray(LocaleUtil.UK),
				LocaleUtil.UK);

			LocaleThreadLocal.setSiteDefaultLocale(LocaleUtil.UK);

			testExportImportJSON(
				read("test-object-definition.json"),
				StringUtil.replace(
					StringUtil.replace(
						read("test-object-definition.json"),
						"TestObjectDefinition",
						"TestObjectDefinitionenUSRemoved"),
					"\"en_US\":", "\"en_GB\":"),
				"TESTOBJECTDEFINITIONENGLISHREMOVED",
				"TestObjectDefinitionenUSRemoved");

			objectDefinitionResource.deleteObjectDefinition(
				getId("TestObjectDefinitionenUSRemoved"));
		}
	}

	@Test
	public void testExportImportObjectDefinition() throws Exception {

		// Account restricted object definition

		String externalReferenceCode = RandomTestUtil.randomString();
		String name = ObjectDefinitionTestUtil.getRandomName();

		JSONObject accountRestrictedObjectDefinitionJSONObject =
			jsonFactory.createJSONObject(
				defaultObjectDefinitionJSON
			).put(
				"accountEntryRestricted", true
			).put(
				"accountEntryRestrictedObjectFieldName",
				"r_testAccountRelationship_accountEntryId"
			).put(
				"active", true
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"name", name
			).put(
				"status",
				jsonFactory.createJSONObject(
				).put(
					"code", 0
				).put(
					"label", "approved"
				).put(
					"label_i18n", "Approved"
				)
			);

		String objectFieldName = "r_testAccountRelationship_accountEntryId";
		String objectRelationshipExternalReferenceCode =
			RandomTestUtil.randomString();

		accountRestrictedObjectDefinitionJSONObject.put(
			"objectFields",
			JSONUtil.concat(
				accountRestrictedObjectDefinitionJSONObject.getJSONArray(
					"objectFields"),
				JSONUtil.putAll(
					JSONUtil.put(
						"businessType", "Relationship"
					).put(
						"name", objectFieldName
					).put(
						"objectDefinitionExternalReferenceCode1", "L_ACCOUNT"
					).put(
						"objectRelationshipExternalReferenceCode",
						objectRelationshipExternalReferenceCode
					))));

		testExportImportJSON(
			accountRestrictedObjectDefinitionJSONObject.toString(),
			accountRestrictedObjectDefinitionJSONObject.toString(),
			externalReferenceCode, name);

		ObjectDefinition accountObjectDefinition =
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				"L_ACCOUNT");

		ObjectRelationship objectRelationship =
			_objectRelationshipLocalService.
				getObjectRelationshipByExternalReferenceCode(
					objectRelationshipExternalReferenceCode,
					user.getCompanyId(), accountObjectDefinition.getId());

		Assert.assertEquals(
			objectRelationship.getName(),
			objectFieldName.split(StringPool.UNDERLINE)[1]);

		ObjectDefinition accountRestrictedObjectDefinition =
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				externalReferenceCode);

		Assert.assertTrue(
			accountRestrictedObjectDefinition.getAccountEntryRestricted());

		// Custom object definition

		testExportImport(
			"test-object-definition.json", "test-object-definition.json",
			"TestObjectDefinition", "TestObjectDefinition");

		ObjectDefinition testObjectDefinition =
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				"TestObjectDefinition");

		Status status = testObjectDefinition.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_DRAFT, (int)status.getCode());

		objectDefinitionResource.postObjectDefinitionPublish(
			testObjectDefinition.getId());

		testObjectDefinition = objectDefinitionResource.getObjectDefinition(
			testObjectDefinition.getId());

		status = testObjectDefinition.getStatus();

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, (int)status.getCode());

		objectDefinitionResource.deleteObjectDefinition(
			testObjectDefinition.getId());

		// Published object definition

		externalReferenceCode = RandomTestUtil.randomString();
		name = ObjectDefinitionTestUtil.getRandomName();

		String publishedObjectDefinitionJSON = jsonFactory.createJSONObject(
			defaultObjectDefinitionJSON
		).put(
			"active", true
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"name", name
		).put(
			"status",
			jsonFactory.createJSONObject(
			).put(
				"code", 0
			).put(
				"label", "approved"
			).put(
				"label_i18n", "Approved"
			)
		).toString();

		testExportImportJSON(
			publishedObjectDefinitionJSON, publishedObjectDefinitionJSON,
			externalReferenceCode, name);

		// Published object definition cannot be a draft

		testFailedImportJSON(
			jsonFactory.createJSONObject(
				publishedObjectDefinitionJSON
			).put(
				"status",
				JSONUtil.put(
					"code", 2
				).put(
					"label", "draft"
				).put(
					"label_i18n", "Draft"
				)
			).toString(),
			read("test-object-definition.status-error-message.json"),
			externalReferenceCode, name);

		// Root node object definition

		String objectDefinition1JSON = jsonFactory.createJSONObject(
			defaultObjectDefinitionJSON
		).put(
			"externalReferenceCode", "TESTOBJECTDEFINITION1"
		).put(
			"name", "TestObjectDefinition1"
		).put(
			"objectRelationships",
			JSONUtil.put(
				createOneToManyObjectRelationship(
					"TESTOBJECTDEFINITION1", "TESTOBJECTDEFINITION2",
					"TESTOBJECTDEFINITION2",
					ObjectDefinitionConstants.SCOPE_COMPANY,
					"objectRelationship1"))
		).put(
			"rootObjectDefinitionExternalReferenceCode", "TESTOBJECTDEFINITION1"
		).toString();

		testExportImportJSON(
			objectDefinition1JSON, objectDefinition1JSON,
			"TESTOBJECTDEFINITION1", "TestObjectDefinition1");

		Assert.assertNotNull(
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				"TESTOBJECTDEFINITION2"));

		// Root node object definition, override descendant

		JSONObject objectDefinition2JSONObject = jsonFactory.createJSONObject(
			defaultObjectDefinitionJSON
		).put(
			"externalReferenceCode", "TESTOBJECTDEFINITION2"
		).put(
			"name", "TestObjectDefinition2"
		).put(
			"rootObjectDefinitionExternalReferenceCode", "TESTOBJECTDEFINITION1"
		);

		testExportImportJSON(
			objectDefinition2JSONObject.toString(),
			objectDefinition2JSONObject.put(
				"objectFields",
				JSONUtil.concat(
					objectDefinition2JSONObject.getJSONArray("objectFields"),
					JSONUtil.putAll(
						JSONUtil.put(
							"name",
							"r_objectRelationship1_c_testObjectDefinition1Id")))
			).toString(),
			"TESTOBJECTDEFINITION2", "TestObjectDefinition2");

		// System object definition

		ObjectDefinition objectDefinition = _getObjectDefinition(
			"AccountEntry");

		_testExportImportSystemObjectDefinition(
			"test-account-entry-system-object-definition.json",
			objectDefinition.getExternalReferenceCode(), "AccountEntry");
	}

	@Override
	protected ClassLoader getClassLoader() {
		return ObjectDefinitionExportImportTest.class.getClassLoader();
	}

	@Override
	protected Class<?> getClazz() {
		return getClass();
	}

	@Override
	protected long getId(String name) throws Exception {
		ObjectDefinition objectDefinition = _getObjectDefinition(name);

		return objectDefinition.getId();
	}

	@Override
	protected String getIdentifierName() {
		return "objectDefinitionId";
	}

	@Override
	protected String getJSONName() {
		return "objectDefinitionJSON";
	}

	@Override
	protected MVCActionCommand getMVCActionCommand() {
		return _mvcActionCommand;
	}

	@Override
	protected MVCResourceCommand getMVCResourceCommand() {
		return _mvcResourceCommand;
	}

	private ObjectDefinition _getObjectDefinition(String name)
		throws Exception {

		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				name, null, null, Pagination.of(1, 1), null);

		List<ObjectDefinition> items = (List<ObjectDefinition>)page.getItems();

		return items.get(0);
	}

	private void _testExportImportSystemObjectDefinition(
			String fileName, String externalReferenceCode, String name)
		throws Exception {

		String json = read(fileName);

		MockLiferayPortletActionResponse mockLiferayPortletActionResponse =
			importJSON(externalReferenceCode, json, name);

		MockHttpServletResponse mockHttpServletResponse =
			(MockHttpServletResponse)
				mockLiferayPortletActionResponse.getHttpServletResponse();

		Assert.assertEquals("{}", mockHttpServletResponse.getContentAsString());

		// A shared system object definition like account entry can be extended
		// with relationships by other deployed modules: an account entry
		// restricted object definition adds a relationship to account entry.
		// Its relationship set is therefore not deterministic, so exclude it
		// from the comparison.

		JSONObject expectedJSONObject = jsonFactory.createJSONObject(json);

		expectedJSONObject.remove("objectRelationships");

		JSONObject exportJSONObject = jsonFactory.createJSONObject(
			getExportJSON(name));

		exportJSONObject.remove("objectRelationships");

		JSONAssert.assertEquals(
			expectedJSONObject.toString(), exportJSONObject.toString(),
			JSONCompareMode.LENIENT);
	}

	private static ListTypeDefinitionResource _listTypeDefinitionResource;

	@Inject
	private ListTypeDefinitionResource.Factory
		_listTypeDefinitionResourceFactory;

	@Inject(
		filter = "mvc.command.name=/object_definitions/import_object_definition"
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject(
		filter = "mvc.command.name=/object_definitions/export_object_definition"
	)
	private MVCResourceCommand _mvcResourceCommand;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

}