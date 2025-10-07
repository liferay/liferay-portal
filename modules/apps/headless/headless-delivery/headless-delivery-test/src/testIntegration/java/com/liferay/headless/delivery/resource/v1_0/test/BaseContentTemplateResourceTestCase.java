/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.delivery.client.dto.v1_0.ContentTemplate;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.ContentTemplateResource;
import com.liferay.headless.delivery.client.serdes.v1_0.ContentTemplateSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseContentTemplateResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_contentTemplateResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		contentTemplateResource = ContentTemplateResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ContentTemplate contentTemplate1 = randomContentTemplate();

		String json = objectMapper.writeValueAsString(contentTemplate1);

		ContentTemplate contentTemplate2 = ContentTemplateSerDes.toDTO(json);

		Assert.assertTrue(equals(contentTemplate1, contentTemplate2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ContentTemplate contentTemplate = randomContentTemplate();

		String json1 = objectMapper.writeValueAsString(contentTemplate);
		String json2 = ContentTemplateSerDes.toJSON(contentTemplate);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		ContentTemplate contentTemplate = randomContentTemplate();

		contentTemplate.setAssetLibraryKey(regex);
		contentTemplate.setDescription(regex);
		contentTemplate.setId(regex);
		contentTemplate.setName(regex);
		contentTemplate.setProgrammingLanguage(regex);
		contentTemplate.setTemplateScript(regex);

		String json = ContentTemplateSerDes.toJSON(contentTemplate);

		Assert.assertFalse(json.contains(regex));

		contentTemplate = ContentTemplateSerDes.toDTO(json);

		Assert.assertEquals(regex, contentTemplate.getAssetLibraryKey());
		Assert.assertEquals(regex, contentTemplate.getDescription());
		Assert.assertEquals(regex, contentTemplate.getId());
		Assert.assertEquals(regex, contentTemplate.getName());
		Assert.assertEquals(regex, contentTemplate.getProgrammingLanguage());
		Assert.assertEquals(regex, contentTemplate.getTemplateScript());
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryContentTemplatesPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryContentTemplatesPage_getIrrelevantAssetLibraryId();

		Page<ContentTemplate> page =
			contentTemplateResource.getAssetLibraryContentTemplatesPage(
				assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			ContentTemplate irrelevantContentTemplate =
				testGetAssetLibraryContentTemplatesPage_addContentTemplate(
					irrelevantAssetLibraryId,
					randomIrrelevantContentTemplate());

			page = contentTemplateResource.getAssetLibraryContentTemplatesPage(
				irrelevantAssetLibraryId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentTemplate,
				(List<ContentTemplate>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryContentTemplatesPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		ContentTemplate contentTemplate1 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, randomContentTemplate());

		ContentTemplate contentTemplate2 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, randomContentTemplate());

		page = contentTemplateResource.getAssetLibraryContentTemplatesPage(
			assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentTemplate1, (List<ContentTemplate>)page.getItems());
		assertContains(
			contentTemplate2, (List<ContentTemplate>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryContentTemplatesPage_getExpectedActions(
				assetLibraryId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryContentTemplatesPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentTemplatesPage_getAssetLibraryId();

		ContentTemplate contentTemplate1 = randomContentTemplate();

		contentTemplate1 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, contentTemplate1);

		for (EntityField entityField : entityFields) {
			Page<ContentTemplate> page =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null,
					getFilterString(entityField, "between", contentTemplate1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentTemplate1),
				(List<ContentTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryContentTemplatesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentTemplatesPage_getAssetLibraryId();

		ContentTemplate contentTemplate1 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, randomContentTemplate());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentTemplate contentTemplate2 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, randomContentTemplate());

		for (EntityField entityField : entityFields) {
			Page<ContentTemplate> page =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null,
					getFilterString(entityField, operator, contentTemplate1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentTemplate1),
				(List<ContentTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryContentTemplatesPage_getAssetLibraryId();

		Page<ContentTemplate> contentTemplatesPage =
			contentTemplateResource.getAssetLibraryContentTemplatesPage(
				assetLibraryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			contentTemplatesPage.getTotalCount());

		ContentTemplate contentTemplate1 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, randomContentTemplate());

		ContentTemplate contentTemplate2 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, randomContentTemplate());

		ContentTemplate contentTemplate3 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, randomContentTemplate());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentTemplate> page1 =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentTemplate1, (List<ContentTemplate>)page1.getItems());

			Page<ContentTemplate> page2 =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentTemplate2, (List<ContentTemplate>)page2.getItems());

			Page<ContentTemplate> page3 =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentTemplate3, (List<ContentTemplate>)page3.getItems());
		}
		else {
			Page<ContentTemplate> page1 =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ContentTemplate> contentTemplates1 =
				(List<ContentTemplate>)page1.getItems();

			Assert.assertEquals(
				contentTemplates1.toString(), totalCount + 2,
				contentTemplates1.size());

			Page<ContentTemplate> page2 =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentTemplate> contentTemplates2 =
				(List<ContentTemplate>)page2.getItems();

			Assert.assertEquals(
				contentTemplates2.toString(), 1, contentTemplates2.size());

			Page<ContentTemplate> page3 =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				contentTemplate1, (List<ContentTemplate>)page3.getItems());
			assertContains(
				contentTemplate2, (List<ContentTemplate>)page3.getItems());
			assertContains(
				contentTemplate3, (List<ContentTemplate>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, contentTemplate1, contentTemplate2) -> {
				BeanTestUtil.setProperty(
					contentTemplate1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, contentTemplate1, contentTemplate2) -> {
				BeanTestUtil.setProperty(
					contentTemplate1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					contentTemplate2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentTemplate1, contentTemplate2) -> {
				BeanTestUtil.setProperty(
					contentTemplate1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					contentTemplate2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryContentTemplatesPageWithSortString()
		throws Exception {

		testGetAssetLibraryContentTemplatesPageWithSort(
			EntityField.Type.STRING,
			(entityField, contentTemplate1, contentTemplate2) -> {
				Class<?> clazz = contentTemplate1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						contentTemplate1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						contentTemplate2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						contentTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						contentTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						contentTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						contentTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryContentTemplatesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ContentTemplate, ContentTemplate, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentTemplatesPage_getAssetLibraryId();

		ContentTemplate contentTemplate1 = randomContentTemplate();
		ContentTemplate contentTemplate2 = randomContentTemplate();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, contentTemplate1, contentTemplate2);
		}

		contentTemplate1 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, contentTemplate1);

		contentTemplate2 =
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				assetLibraryId, contentTemplate2);

		Page<ContentTemplate> page =
			contentTemplateResource.getAssetLibraryContentTemplatesPage(
				assetLibraryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ContentTemplate> ascPage =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				contentTemplate1, (List<ContentTemplate>)ascPage.getItems());
			assertContains(
				contentTemplate2, (List<ContentTemplate>)ascPage.getItems());

			Page<ContentTemplate> descPage =
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				contentTemplate2, (List<ContentTemplate>)descPage.getItems());
			assertContains(
				contentTemplate1, (List<ContentTemplate>)descPage.getItems());
		}
	}

	protected ContentTemplate
			testGetAssetLibraryContentTemplatesPage_addContentTemplate(
				Long assetLibraryId, ContentTemplate contentTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibraryContentTemplatesPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryContentTemplatesPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGetSiteContentTemplate() throws Exception {
		ContentTemplate postContentTemplate =
			testGetSiteContentTemplate_addContentTemplate();

		ContentTemplate getContentTemplate =
			contentTemplateResource.getSiteContentTemplate(
				postContentTemplate.getSiteId(), postContentTemplate.getId());

		assertEquals(postContentTemplate, getContentTemplate);
		assertValid(getContentTemplate);
	}

	protected ContentTemplate testGetSiteContentTemplate_addContentTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteContentTemplate() throws Exception {
		ContentTemplate contentTemplate =
			testGraphQLGetSiteContentTemplate_addContentTemplate();

		// No namespace

		Assert.assertTrue(
			equals(
				contentTemplate,
				ContentTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"contentTemplate",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + contentTemplate.getSiteId() +
												"\"");
										put(
											"contentTemplateId",
											"\"" + contentTemplate.getId() +
												"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/contentTemplate"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				contentTemplate,
				ContentTemplateSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"contentTemplate",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													contentTemplate.
														getSiteId() + "\"");
											put(
												"contentTemplateId",
												"\"" + contentTemplate.getId() +
													"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/contentTemplate"))));
	}

	@Test
	public void testGraphQLGetSiteContentTemplateNotFound() throws Exception {
		String irrelevantContentTemplateId =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"contentTemplate",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"contentTemplateId",
									irrelevantContentTemplateId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"contentTemplate",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"contentTemplateId",
										irrelevantContentTemplateId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ContentTemplate
			testGraphQLGetSiteContentTemplate_addContentTemplate()
		throws Exception {

		return testGraphQLSiteContentTemplate_addContentTemplate();
	}

	@Test
	public void testGetSiteContentTemplatesPage() throws Exception {
		Long siteId = testGetSiteContentTemplatesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteContentTemplatesPage_getIrrelevantSiteId();

		Page<ContentTemplate> page =
			contentTemplateResource.getSiteContentTemplatesPage(
				siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			ContentTemplate irrelevantContentTemplate =
				testGetSiteContentTemplatesPage_addContentTemplate(
					irrelevantSiteId, randomIrrelevantContentTemplate());

			page = contentTemplateResource.getSiteContentTemplatesPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentTemplate,
				(List<ContentTemplate>)page.getItems());
			assertValid(
				page,
				testGetSiteContentTemplatesPage_getExpectedActions(
					irrelevantSiteId));
		}

		ContentTemplate contentTemplate1 =
			testGetSiteContentTemplatesPage_addContentTemplate(
				siteId, randomContentTemplate());

		ContentTemplate contentTemplate2 =
			testGetSiteContentTemplatesPage_addContentTemplate(
				siteId, randomContentTemplate());

		page = contentTemplateResource.getSiteContentTemplatesPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentTemplate1, (List<ContentTemplate>)page.getItems());
		assertContains(
			contentTemplate2, (List<ContentTemplate>)page.getItems());
		assertValid(
			page, testGetSiteContentTemplatesPage_getExpectedActions(siteId));
	}

	protected Map<String, Map<String, String>>
			testGetSiteContentTemplatesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteContentTemplatesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentTemplatesPage_getSiteId();

		ContentTemplate contentTemplate1 = randomContentTemplate();

		contentTemplate1 = testGetSiteContentTemplatesPage_addContentTemplate(
			siteId, contentTemplate1);

		for (EntityField entityField : entityFields) {
			Page<ContentTemplate> page =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null,
					getFilterString(entityField, "between", contentTemplate1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentTemplate1),
				(List<ContentTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetSiteContentTemplatesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteContentTemplatesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteContentTemplatesPageWithFilterStringContains()
		throws Exception {

		testGetSiteContentTemplatesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteContentTemplatesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteContentTemplatesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteContentTemplatesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteContentTemplatesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteContentTemplatesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentTemplatesPage_getSiteId();

		ContentTemplate contentTemplate1 =
			testGetSiteContentTemplatesPage_addContentTemplate(
				siteId, randomContentTemplate());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentTemplate contentTemplate2 =
			testGetSiteContentTemplatesPage_addContentTemplate(
				siteId, randomContentTemplate());

		for (EntityField entityField : entityFields) {
			Page<ContentTemplate> page =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null,
					getFilterString(entityField, operator, contentTemplate1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentTemplate1),
				(List<ContentTemplate>)page.getItems());
		}
	}

	@Test
	public void testGetSiteContentTemplatesPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteContentTemplatesPage_getSiteId();

		Page<ContentTemplate> contentTemplatesPage =
			contentTemplateResource.getSiteContentTemplatesPage(
				siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			contentTemplatesPage.getTotalCount());

		ContentTemplate contentTemplate1 =
			testGetSiteContentTemplatesPage_addContentTemplate(
				siteId, randomContentTemplate());

		ContentTemplate contentTemplate2 =
			testGetSiteContentTemplatesPage_addContentTemplate(
				siteId, randomContentTemplate());

		ContentTemplate contentTemplate3 =
			testGetSiteContentTemplatesPage_addContentTemplate(
				siteId, randomContentTemplate());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentTemplate> page1 =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentTemplate1, (List<ContentTemplate>)page1.getItems());

			Page<ContentTemplate> page2 =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentTemplate2, (List<ContentTemplate>)page2.getItems());

			Page<ContentTemplate> page3 =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentTemplate3, (List<ContentTemplate>)page3.getItems());
		}
		else {
			Page<ContentTemplate> page1 =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<ContentTemplate> contentTemplates1 =
				(List<ContentTemplate>)page1.getItems();

			Assert.assertEquals(
				contentTemplates1.toString(), totalCount + 2,
				contentTemplates1.size());

			Page<ContentTemplate> page2 =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentTemplate> contentTemplates2 =
				(List<ContentTemplate>)page2.getItems();

			Assert.assertEquals(
				contentTemplates2.toString(), 1, contentTemplates2.size());

			Page<ContentTemplate> page3 =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				contentTemplate1, (List<ContentTemplate>)page3.getItems());
			assertContains(
				contentTemplate2, (List<ContentTemplate>)page3.getItems());
			assertContains(
				contentTemplate3, (List<ContentTemplate>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteContentTemplatesPageWithSortDateTime()
		throws Exception {

		testGetSiteContentTemplatesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, contentTemplate1, contentTemplate2) -> {
				BeanTestUtil.setProperty(
					contentTemplate1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteContentTemplatesPageWithSortDouble()
		throws Exception {

		testGetSiteContentTemplatesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, contentTemplate1, contentTemplate2) -> {
				BeanTestUtil.setProperty(
					contentTemplate1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					contentTemplate2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteContentTemplatesPageWithSortInteger()
		throws Exception {

		testGetSiteContentTemplatesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentTemplate1, contentTemplate2) -> {
				BeanTestUtil.setProperty(
					contentTemplate1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					contentTemplate2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteContentTemplatesPageWithSortString()
		throws Exception {

		testGetSiteContentTemplatesPageWithSort(
			EntityField.Type.STRING,
			(entityField, contentTemplate1, contentTemplate2) -> {
				Class<?> clazz = contentTemplate1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						contentTemplate1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						contentTemplate2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						contentTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						contentTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						contentTemplate1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						contentTemplate2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteContentTemplatesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ContentTemplate, ContentTemplate, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentTemplatesPage_getSiteId();

		ContentTemplate contentTemplate1 = randomContentTemplate();
		ContentTemplate contentTemplate2 = randomContentTemplate();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, contentTemplate1, contentTemplate2);
		}

		contentTemplate1 = testGetSiteContentTemplatesPage_addContentTemplate(
			siteId, contentTemplate1);

		contentTemplate2 = testGetSiteContentTemplatesPage_addContentTemplate(
			siteId, contentTemplate2);

		Page<ContentTemplate> page =
			contentTemplateResource.getSiteContentTemplatesPage(
				siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ContentTemplate> ascPage =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				contentTemplate1, (List<ContentTemplate>)ascPage.getItems());
			assertContains(
				contentTemplate2, (List<ContentTemplate>)ascPage.getItems());

			Page<ContentTemplate> descPage =
				contentTemplateResource.getSiteContentTemplatesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				contentTemplate2, (List<ContentTemplate>)descPage.getItems());
			assertContains(
				contentTemplate1, (List<ContentTemplate>)descPage.getItems());
		}
	}

	protected ContentTemplate
			testGetSiteContentTemplatesPage_addContentTemplate(
				Long siteId, ContentTemplate contentTemplate)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteContentTemplatesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteContentTemplatesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected ContentTemplate
			testGraphQLSiteContentTemplate_addContentTemplate()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ContentTemplate contentTemplate,
		List<ContentTemplate> contentTemplates) {

		boolean contains = false;

		for (ContentTemplate item : contentTemplates) {
			if (equals(contentTemplate, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			contentTemplates + " does not contain " + contentTemplate,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ContentTemplate contentTemplate1, ContentTemplate contentTemplate2) {

		Assert.assertTrue(
			contentTemplate1 + " does not equal " + contentTemplate2,
			equals(contentTemplate1, contentTemplate2));
	}

	protected void assertEquals(
		List<ContentTemplate> contentTemplates1,
		List<ContentTemplate> contentTemplates2) {

		Assert.assertEquals(contentTemplates1.size(), contentTemplates2.size());

		for (int i = 0; i < contentTemplates1.size(); i++) {
			ContentTemplate contentTemplate1 = contentTemplates1.get(i);
			ContentTemplate contentTemplate2 = contentTemplates2.get(i);

			assertEquals(contentTemplate1, contentTemplate2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ContentTemplate> contentTemplates1,
		List<ContentTemplate> contentTemplates2) {

		Assert.assertEquals(contentTemplates1.size(), contentTemplates2.size());

		for (ContentTemplate contentTemplate1 : contentTemplates1) {
			boolean contains = false;

			for (ContentTemplate contentTemplate2 : contentTemplates2) {
				if (equals(contentTemplate1, contentTemplate2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				contentTemplates2 + " does not contain " + contentTemplate1,
				contains);
		}
	}

	protected void assertValid(ContentTemplate contentTemplate)
		throws Exception {

		boolean valid = true;

		if (contentTemplate.getDateCreated() == null) {
			valid = false;
		}

		if (contentTemplate.getDateModified() == null) {
			valid = false;
		}

		if (contentTemplate.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				contentTemplate.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				contentTemplate.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (contentTemplate.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (contentTemplate.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (contentTemplate.getAvailableLanguages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"contentStructureId", additionalAssertFieldName)) {

				if (contentTemplate.getContentStructureId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (contentTemplate.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (contentTemplate.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (contentTemplate.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (contentTemplate.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (contentTemplate.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"programmingLanguage", additionalAssertFieldName)) {

				if (contentTemplate.getProgrammingLanguage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("templateScript", additionalAssertFieldName)) {
				if (contentTemplate.getTemplateScript() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<ContentTemplate> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ContentTemplate> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ContentTemplate> contentTemplates =
			page.getItems();

		int size = contentTemplates.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.ContentTemplate.
						class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		ContentTemplate contentTemplate1, ContentTemplate contentTemplate2) {

		if (contentTemplate1 == contentTemplate2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)contentTemplate1.getActions(),
						(Map)contentTemplate2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						contentTemplate1.getAvailableLanguages(),
						contentTemplate2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"contentStructureId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						contentTemplate1.getContentStructureId(),
						contentTemplate2.getContentStructureId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentTemplate1.getCreator(),
						contentTemplate2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentTemplate1.getDateCreated(),
						contentTemplate2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentTemplate1.getDateModified(),
						contentTemplate2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentTemplate1.getDescription(),
						contentTemplate2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)contentTemplate1.getDescription_i18n(),
						(Map)contentTemplate2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentTemplate1.getId(), contentTemplate2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentTemplate1.getName(),
						contentTemplate2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)contentTemplate1.getName_i18n(),
						(Map)contentTemplate2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"programmingLanguage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						contentTemplate1.getProgrammingLanguage(),
						contentTemplate2.getProgrammingLanguage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("templateScript", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentTemplate1.getTemplateScript(),
						contentTemplate2.getTemplateScript())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_contentTemplateResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_contentTemplateResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		ContentTemplate contentTemplate) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = contentTemplate.getAssetLibraryKey();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("contentStructureId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = contentTemplate.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(contentTemplate.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = contentTemplate.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(contentTemplate.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = contentTemplate.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			Object object = contentTemplate.getId();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = contentTemplate.getName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("programmingLanguage")) {
			Object object = contentTemplate.getProgrammingLanguage();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("templateScript")) {
			Object object = contentTemplate.getTemplateScript();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected ContentTemplate randomContentTemplate() throws Exception {
		return new ContentTemplate() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				contentStructureId = RandomTestUtil.randomLong();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				programmingLanguage = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
				templateScript = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ContentTemplate randomIrrelevantContentTemplate()
		throws Exception {

		ContentTemplate randomIrrelevantContentTemplate =
			randomContentTemplate();

		randomIrrelevantContentTemplate.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantContentTemplate.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantContentTemplate;
	}

	protected ContentTemplate randomPatchContentTemplate() throws Exception {
		return randomContentTemplate();
	}

	protected ContentTemplateResource contentTemplateResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseContentTemplateResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.ContentTemplateResource
		_contentTemplateResource;

}