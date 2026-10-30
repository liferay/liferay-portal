/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ChannelSite;
import com.liferay.headless.commerce.admin.channel.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.channel.client.pagination.Page;
import com.liferay.headless.commerce.admin.channel.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.channel.client.resource.v1_0.ChannelSiteResource;
import com.liferay.headless.commerce.admin.channel.client.serdes.v1_0.ChannelSiteSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseChannelSiteResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_channelSiteResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		channelSiteResource = ChannelSiteResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
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

		ChannelSite channelSite1 = randomChannelSite();

		String json = objectMapper.writeValueAsString(channelSite1);

		ChannelSite channelSite2 = ChannelSiteSerDes.toDTO(json);

		Assert.assertTrue(equals(channelSite1, channelSite2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ChannelSite channelSite = randomChannelSite();

		String json1 = objectMapper.writeValueAsString(channelSite);
		String json2 = ChannelSiteSerDes.toJSON(channelSite);

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

		ChannelSite channelSite = randomChannelSite();

		channelSite.setExternalReferenceCode(regex);
		channelSite.setFriendlyUrlPath(regex);
		channelSite.setName(regex);

		String json = ChannelSiteSerDes.toJSON(channelSite);

		Assert.assertFalse(json.contains(regex));

		channelSite = ChannelSiteSerDes.toDTO(json);

		Assert.assertEquals(regex, channelSite.getExternalReferenceCode());
		Assert.assertEquals(regex, channelSite.getFriendlyUrlPath());
		Assert.assertEquals(regex, channelSite.getName());
	}

	@Test
	public void testGetChannelAvailableSitesPage() throws Exception {
		Long channelId = testGetChannelAvailableSitesPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelAvailableSitesPage_getIrrelevantChannelId();

		Page<ChannelSite> page =
			channelSiteResource.getChannelAvailableSitesPage(
				channelId, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantChannelId != null) {
			ChannelSite irrelevantChannelSite =
				testGetChannelAvailableSitesPage_addChannelSite(
					irrelevantChannelId, randomIrrelevantChannelSite());

			page = channelSiteResource.getChannelAvailableSitesPage(
				irrelevantChannelId, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantChannelSite, (List<ChannelSite>)page.getItems());
			assertValid(
				page,
				testGetChannelAvailableSitesPage_getExpectedActions(
					irrelevantChannelId));
		}

		ChannelSite channelSite1 =
			testGetChannelAvailableSitesPage_addChannelSite(
				channelId, randomChannelSite());

		ChannelSite channelSite2 =
			testGetChannelAvailableSitesPage_addChannelSite(
				channelId, randomChannelSite());

		page = channelSiteResource.getChannelAvailableSitesPage(
			channelId, null, Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(channelSite1, (List<ChannelSite>)page.getItems());
		assertContains(channelSite2, (List<ChannelSite>)page.getItems());
		assertValid(
			page,
			testGetChannelAvailableSitesPage_getExpectedActions(channelId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelAvailableSitesPage_getExpectedActions(Long channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelAvailableSitesPageWithPagination()
		throws Exception {

		Long channelId = testGetChannelAvailableSitesPage_getChannelId();

		Page<ChannelSite> channelSitesPage =
			channelSiteResource.getChannelAvailableSitesPage(
				channelId, null, null);

		int totalCount = GetterUtil.getInteger(
			channelSitesPage.getTotalCount());

		ChannelSite channelSite1 =
			testGetChannelAvailableSitesPage_addChannelSite(
				channelId, randomChannelSite());

		ChannelSite channelSite2 =
			testGetChannelAvailableSitesPage_addChannelSite(
				channelId, randomChannelSite());

		ChannelSite channelSite3 =
			testGetChannelAvailableSitesPage_addChannelSite(
				channelId, randomChannelSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ChannelSite> page1 =
				channelSiteResource.getChannelAvailableSitesPage(
					channelId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(channelSite1, (List<ChannelSite>)page1.getItems());

			Page<ChannelSite> page2 =
				channelSiteResource.getChannelAvailableSitesPage(
					channelId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(channelSite2, (List<ChannelSite>)page2.getItems());

			Page<ChannelSite> page3 =
				channelSiteResource.getChannelAvailableSitesPage(
					channelId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(channelSite3, (List<ChannelSite>)page3.getItems());
		}
		else {
			Page<ChannelSite> page1 =
				channelSiteResource.getChannelAvailableSitesPage(
					channelId, null, Pagination.of(1, totalCount + 2));

			List<ChannelSite> channelSites1 =
				(List<ChannelSite>)page1.getItems();

			Assert.assertEquals(
				channelSites1.toString(), totalCount + 2, channelSites1.size());

			Page<ChannelSite> page2 =
				channelSiteResource.getChannelAvailableSitesPage(
					channelId, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ChannelSite> channelSites2 =
				(List<ChannelSite>)page2.getItems();

			Assert.assertEquals(
				channelSites2.toString(), 1, channelSites2.size());

			Page<ChannelSite> page3 =
				channelSiteResource.getChannelAvailableSitesPage(
					channelId, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(channelSite1, (List<ChannelSite>)page3.getItems());
			assertContains(channelSite2, (List<ChannelSite>)page3.getItems());
			assertContains(channelSite3, (List<ChannelSite>)page3.getItems());
		}
	}

	protected ChannelSite testGetChannelAvailableSitesPage_addChannelSite(
			Long channelId, ChannelSite channelSite)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelAvailableSitesPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelAvailableSitesPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeAvailableSitesPage()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getIrrelevantExternalReferenceCode();

		Page<ChannelSite> page =
			channelSiteResource.
				getChannelByExternalReferenceCodeAvailableSitesPage(
					externalReferenceCode, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ChannelSite irrelevantChannelSite =
				testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
					irrelevantExternalReferenceCode,
					randomIrrelevantChannelSite());

			page =
				channelSiteResource.
					getChannelByExternalReferenceCodeAvailableSitesPage(
						irrelevantExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantChannelSite, (List<ChannelSite>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeAvailableSitesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ChannelSite channelSite1 =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
				externalReferenceCode, randomChannelSite());

		ChannelSite channelSite2 =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
				externalReferenceCode, randomChannelSite());

		page =
			channelSiteResource.
				getChannelByExternalReferenceCodeAvailableSitesPage(
					externalReferenceCode, null,
					Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(channelSite1, (List<ChannelSite>)page.getItems());
		assertContains(channelSite2, (List<ChannelSite>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeAvailableSitesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getExternalReferenceCode();

		Page<ChannelSite> channelSitesPage =
			channelSiteResource.
				getChannelByExternalReferenceCodeAvailableSitesPage(
					externalReferenceCode, null, null);

		int totalCount = GetterUtil.getInteger(
			channelSitesPage.getTotalCount());

		ChannelSite channelSite1 =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
				externalReferenceCode, randomChannelSite());

		ChannelSite channelSite2 =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
				externalReferenceCode, randomChannelSite());

		ChannelSite channelSite3 =
			testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
				externalReferenceCode, randomChannelSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ChannelSite> page1 =
				channelSiteResource.
					getChannelByExternalReferenceCodeAvailableSitesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(channelSite1, (List<ChannelSite>)page1.getItems());

			Page<ChannelSite> page2 =
				channelSiteResource.
					getChannelByExternalReferenceCodeAvailableSitesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(channelSite2, (List<ChannelSite>)page2.getItems());

			Page<ChannelSite> page3 =
				channelSiteResource.
					getChannelByExternalReferenceCodeAvailableSitesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(channelSite3, (List<ChannelSite>)page3.getItems());
		}
		else {
			Page<ChannelSite> page1 =
				channelSiteResource.
					getChannelByExternalReferenceCodeAvailableSitesPage(
						externalReferenceCode, null,
						Pagination.of(1, totalCount + 2));

			List<ChannelSite> channelSites1 =
				(List<ChannelSite>)page1.getItems();

			Assert.assertEquals(
				channelSites1.toString(), totalCount + 2, channelSites1.size());

			Page<ChannelSite> page2 =
				channelSiteResource.
					getChannelByExternalReferenceCodeAvailableSitesPage(
						externalReferenceCode, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ChannelSite> channelSites2 =
				(List<ChannelSite>)page2.getItems();

			Assert.assertEquals(
				channelSites2.toString(), 1, channelSites2.size());

			Page<ChannelSite> page3 =
				channelSiteResource.
					getChannelByExternalReferenceCodeAvailableSitesPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(channelSite1, (List<ChannelSite>)page3.getItems());
			assertContains(channelSite2, (List<ChannelSite>)page3.getItems());
			assertContains(channelSite3, (List<ChannelSite>)page3.getItems());
		}
	}

	protected ChannelSite
			testGetChannelByExternalReferenceCodeAvailableSitesPage_addChannelSite(
				String externalReferenceCode, ChannelSite channelSite)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeAvailableSitesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		ChannelSite channelSite, List<ChannelSite> channelSites) {

		boolean contains = false;

		for (ChannelSite item : channelSites) {
			if (equals(channelSite, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			channelSites + " does not contain " + channelSite, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ChannelSite channelSite1, ChannelSite channelSite2) {

		Assert.assertTrue(
			channelSite1 + " does not equal " + channelSite2,
			equals(channelSite1, channelSite2));
	}

	protected void assertEquals(
		List<ChannelSite> channelSites1, List<ChannelSite> channelSites2) {

		Assert.assertEquals(channelSites1.size(), channelSites2.size());

		for (int i = 0; i < channelSites1.size(); i++) {
			ChannelSite channelSite1 = channelSites1.get(i);
			ChannelSite channelSite2 = channelSites2.get(i);

			assertEquals(channelSite1, channelSite2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ChannelSite> channelSites1, List<ChannelSite> channelSites2) {

		Assert.assertEquals(channelSites1.size(), channelSites2.size());

		for (ChannelSite channelSite1 : channelSites1) {
			boolean contains = false;

			for (ChannelSite channelSite2 : channelSites2) {
				if (equals(channelSite1, channelSite2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				channelSites2 + " does not contain " + channelSite1, contains);
		}
	}

	protected void assertValid(ChannelSite channelSite) throws Exception {
		boolean valid = true;

		if (channelSite.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (channelSite.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (channelSite.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (channelSite.getName() == null) {
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

	protected void assertValid(Page<ChannelSite> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ChannelSite> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ChannelSite> channelSites = page.getItems();

		int size = channelSites.size();

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

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.channel.dto.v1_0.
						ChannelSite.class)) {

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
		ChannelSite channelSite1, ChannelSite channelSite2) {

		if (channelSite1 == channelSite2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						channelSite1.getExternalReferenceCode(),
						channelSite2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channelSite1.getFriendlyUrlPath(),
						channelSite2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channelSite1.getId(), channelSite2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						channelSite1.getName(), channelSite2.getName())) {

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

		if (!(_channelSiteResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_channelSiteResource;

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
		EntityField entityField, String operator, ChannelSite channelSite) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = channelSite.getExternalReferenceCode();

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

		if (entityFieldName.equals("friendlyUrlPath")) {
			Object object = channelSite.getFriendlyUrlPath();

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

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = channelSite.getName();

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
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
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

	protected ChannelSite randomChannelSite() throws Exception {
		return new ChannelSite() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ChannelSite randomIrrelevantChannelSite() throws Exception {
		ChannelSite randomIrrelevantChannelSite = randomChannelSite();

		return randomIrrelevantChannelSite;
	}

	protected ChannelSite randomPatchChannelSite() throws Exception {
		return randomChannelSite();
	}

	protected ChannelSiteResource channelSiteResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
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
		LogFactoryUtil.getLog(BaseChannelSiteResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.channel.resource.v1_0.
		ChannelSiteResource _channelSiteResource;

}
// LIFERAY-REST-BUILDER-HASH:976982228