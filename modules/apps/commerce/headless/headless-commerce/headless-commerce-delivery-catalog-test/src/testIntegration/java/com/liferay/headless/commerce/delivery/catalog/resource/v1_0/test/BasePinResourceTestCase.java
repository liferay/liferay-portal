/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Pin;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.PinResource;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.PinSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
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
public abstract class BasePinResourceTestCase {

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

		_pinResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		pinResource = PinResource.builder(
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

		Pin pin1 = randomPin();

		String json = objectMapper.writeValueAsString(pin1);

		Pin pin2 = PinSerDes.toDTO(json);

		Assert.assertTrue(equals(pin1, pin2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Pin pin = randomPin();

		String json1 = objectMapper.writeValueAsString(pin);
		String json2 = PinSerDes.toJSON(pin);

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

		Pin pin = randomPin();

		pin.setSequence(regex);

		String json = PinSerDes.toJSON(pin);

		Assert.assertFalse(json.contains(regex));

		pin = PinSerDes.toDTO(json);

		Assert.assertEquals(regex, pin.getSequence());
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getChannelExternalReferenceCode();
		String irrelevantChannelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getIrrelevantChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getProductExternalReferenceCode();
		String irrelevantProductExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getIrrelevantProductExternalReferenceCode();

		Page<Pin> page =
			pinResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelExternalReferenceCode != null) &&
			(irrelevantProductExternalReferenceCode != null)) {

			Pin irrelevantPin =
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode,
					randomIrrelevantPin());

			page =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						irrelevantChannelExternalReferenceCode,
						irrelevantProductExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPin, (List<Pin>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getExpectedActions(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode));
		}

		Pin pin1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomPin());

		Pin pin2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomPin());

		page =
			pinResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(pin1, (List<Pin>)page.getItems());
		assertContains(pin2, (List<Pin>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getExpectedActions(
				channelExternalReferenceCode, productExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getExpectedActions(
				String channelExternalReferenceCode,
				String productExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithPagination()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getProductExternalReferenceCode();

		Page<Pin> pinsPage =
			pinResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null, null, null, null);

		int totalCount = GetterUtil.getInteger(pinsPage.getTotalCount());

		Pin pin1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomPin());

		Pin pin2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomPin());

		Pin pin3 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomPin());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Pin> page1 =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(pin1, (List<Pin>)page1.getItems());

			Page<Pin> page2 =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(pin2, (List<Pin>)page2.getItems());

			Page<Pin> page3 =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(pin3, (List<Pin>)page3.getItems());
		}
		else {
			Page<Pin> page1 =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<Pin> pins1 = (List<Pin>)page1.getItems();

			Assert.assertEquals(pins1.toString(), totalCount + 2, pins1.size());

			Page<Pin> page2 =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Pin> pins2 = (List<Pin>)page2.getItems();

			Assert.assertEquals(pins2.toString(), 1, pins2.size());

			Page<Pin> page3 =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(pin1, (List<Pin>)page3.getItems());
			assertContains(pin2, (List<Pin>)page3.getItems());
			assertContains(pin3, (List<Pin>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSortDateTime()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, pin1, pin2) -> {
				BeanTestUtil.setProperty(
					pin1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSortDouble()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, pin1, pin2) -> {
				BeanTestUtil.setProperty(pin1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(pin2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSortInteger()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, pin1, pin2) -> {
				BeanTestUtil.setProperty(pin1, entityField.getName(), 0);
				BeanTestUtil.setProperty(pin2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSortString()
		throws Exception {

		testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSort(
			EntityField.Type.STRING,
			(entityField, pin1, pin2) -> {
				Class<?> clazz = pin1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						pin1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						pin2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						pin1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						pin2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						pin1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						pin2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer<EntityField, Pin, Pin, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getProductExternalReferenceCode();

		Pin pin1 = randomPin();
		Pin pin2 = randomPin();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, pin1, pin2);
		}

		pin1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				channelExternalReferenceCode, productExternalReferenceCode,
				pin1);

		pin2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				channelExternalReferenceCode, productExternalReferenceCode,
				pin2);

		Page<Pin> page =
			pinResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Pin> ascPage =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(pin1, (List<Pin>)ascPage.getItems());
			assertContains(pin2, (List<Pin>)ascPage.getItems());

			Page<Pin> descPage =
				pinResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(pin2, (List<Pin>)descPage.getItems());
			assertContains(pin1, (List<Pin>)descPage.getItems());
		}
	}

	protected Pin
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_addPin(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Pin pin)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getIrrelevantChannelExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getProductExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodePinsPage_getIrrelevantProductExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelProductPinsPage() throws Exception {
		Long channelId = testGetChannelProductPinsPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelProductPinsPage_getIrrelevantChannelId();
		Long productId = testGetChannelProductPinsPage_getProductId();
		Long irrelevantProductId =
			testGetChannelProductPinsPage_getIrrelevantProductId();

		Page<Pin> page = pinResource.getChannelProductPinsPage(
			channelId, productId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelId != null) && (irrelevantProductId != null)) {
			Pin irrelevantPin = testGetChannelProductPinsPage_addPin(
				irrelevantChannelId, irrelevantProductId,
				randomIrrelevantPin());

			page = pinResource.getChannelProductPinsPage(
				irrelevantChannelId, irrelevantProductId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPin, (List<Pin>)page.getItems());
			assertValid(
				page,
				testGetChannelProductPinsPage_getExpectedActions(
					irrelevantChannelId, irrelevantProductId));
		}

		Pin pin1 = testGetChannelProductPinsPage_addPin(
			channelId, productId, randomPin());

		Pin pin2 = testGetChannelProductPinsPage_addPin(
			channelId, productId, randomPin());

		page = pinResource.getChannelProductPinsPage(
			channelId, productId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(pin1, (List<Pin>)page.getItems());
		assertContains(pin2, (List<Pin>)page.getItems());
		assertValid(
			page,
			testGetChannelProductPinsPage_getExpectedActions(
				channelId, productId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelProductPinsPage_getExpectedActions(
				Long channelId, Long productId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelProductPinsPageWithPagination() throws Exception {
		Long channelId = testGetChannelProductPinsPage_getChannelId();
		Long productId = testGetChannelProductPinsPage_getProductId();

		Page<Pin> pinsPage = pinResource.getChannelProductPinsPage(
			channelId, productId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(pinsPage.getTotalCount());

		Pin pin1 = testGetChannelProductPinsPage_addPin(
			channelId, productId, randomPin());

		Pin pin2 = testGetChannelProductPinsPage_addPin(
			channelId, productId, randomPin());

		Pin pin3 = testGetChannelProductPinsPage_addPin(
			channelId, productId, randomPin());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Pin> page1 = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(pin1, (List<Pin>)page1.getItems());

			Page<Pin> page2 = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(pin2, (List<Pin>)page2.getItems());

			Page<Pin> page3 = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(pin3, (List<Pin>)page3.getItems());
		}
		else {
			Page<Pin> page1 = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Pin> pins1 = (List<Pin>)page1.getItems();

			Assert.assertEquals(pins1.toString(), totalCount + 2, pins1.size());

			Page<Pin> page2 = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Pin> pins2 = (List<Pin>)page2.getItems();

			Assert.assertEquals(pins2.toString(), 1, pins2.size());

			Page<Pin> page3 = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(pin1, (List<Pin>)page3.getItems());
			assertContains(pin2, (List<Pin>)page3.getItems());
			assertContains(pin3, (List<Pin>)page3.getItems());
		}
	}

	@Test
	public void testGetChannelProductPinsPageWithSortDateTime()
		throws Exception {

		testGetChannelProductPinsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, pin1, pin2) -> {
				BeanTestUtil.setProperty(
					pin1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetChannelProductPinsPageWithSortDouble() throws Exception {
		testGetChannelProductPinsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, pin1, pin2) -> {
				BeanTestUtil.setProperty(pin1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(pin2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetChannelProductPinsPageWithSortInteger()
		throws Exception {

		testGetChannelProductPinsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, pin1, pin2) -> {
				BeanTestUtil.setProperty(pin1, entityField.getName(), 0);
				BeanTestUtil.setProperty(pin2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetChannelProductPinsPageWithSortString() throws Exception {
		testGetChannelProductPinsPageWithSort(
			EntityField.Type.STRING,
			(entityField, pin1, pin2) -> {
				Class<?> clazz = pin1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						pin1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						pin2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						pin1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						pin2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						pin1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						pin2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetChannelProductPinsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Pin, Pin, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long channelId = testGetChannelProductPinsPage_getChannelId();
		Long productId = testGetChannelProductPinsPage_getProductId();

		Pin pin1 = randomPin();
		Pin pin2 = randomPin();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, pin1, pin2);
		}

		pin1 = testGetChannelProductPinsPage_addPin(channelId, productId, pin1);

		pin2 = testGetChannelProductPinsPage_addPin(channelId, productId, pin2);

		Page<Pin> page = pinResource.getChannelProductPinsPage(
			channelId, productId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Pin> ascPage = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(pin1, (List<Pin>)ascPage.getItems());
			assertContains(pin2, (List<Pin>)ascPage.getItems());

			Page<Pin> descPage = pinResource.getChannelProductPinsPage(
				channelId, productId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(pin2, (List<Pin>)descPage.getItems());
			assertContains(pin1, (List<Pin>)descPage.getItems());
		}
	}

	protected Pin testGetChannelProductPinsPage_addPin(
			Long channelId, Long productId, Pin pin)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductPinsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductPinsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected Long testGetChannelProductPinsPage_getProductId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductPinsPage_getIrrelevantProductId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(Pin pin, List<Pin> pins) {
		boolean contains = false;

		for (Pin item : pins) {
			if (equals(pin, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(pins + " does not contain " + pin, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Pin pin1, Pin pin2) {
		Assert.assertTrue(pin1 + " does not equal " + pin2, equals(pin1, pin2));
	}

	protected void assertEquals(List<Pin> pins1, List<Pin> pins2) {
		Assert.assertEquals(pins1.size(), pins2.size());

		for (int i = 0; i < pins1.size(); i++) {
			Pin pin1 = pins1.get(i);
			Pin pin2 = pins2.get(i);

			assertEquals(pin1, pin2);
		}
	}

	protected void assertEqualsIgnoringOrder(List<Pin> pins1, List<Pin> pins2) {
		Assert.assertEquals(pins1.size(), pins2.size());

		for (Pin pin1 : pins1) {
			boolean contains = false;

			for (Pin pin2 : pins2) {
				if (equals(pin1, pin2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(pins2 + " does not contain " + pin1, contains);
		}
	}

	protected void assertValid(Pin pin) throws Exception {
		boolean valid = true;

		if (pin.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("mappedProduct", additionalAssertFieldName)) {
				if (pin.getMappedProduct() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("positionX", additionalAssertFieldName)) {
				if (pin.getPositionX() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("positionY", additionalAssertFieldName)) {
				if (pin.getPositionY() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sequence", additionalAssertFieldName)) {
				if (pin.getSequence() == null) {
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

	protected void assertValid(Page<Pin> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Pin> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Pin> pins = page.getItems();

		int size = pins.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Pin.
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

	protected boolean equals(Pin pin1, Pin pin2) {
		if (pin1 == pin2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(pin1.getId(), pin2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("mappedProduct", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pin1.getMappedProduct(), pin2.getMappedProduct())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("positionX", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pin1.getPositionX(), pin2.getPositionX())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("positionY", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pin1.getPositionY(), pin2.getPositionY())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sequence", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pin1.getSequence(), pin2.getSequence())) {

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

		if (!(_pinResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_pinResource;

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
		EntityField entityField, String operator, Pin pin) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("mappedProduct")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("positionX")) {
			sb.append(String.valueOf(pin.getPositionX()));

			return sb.toString();
		}

		if (entityFieldName.equals("positionY")) {
			sb.append(String.valueOf(pin.getPositionY()));

			return sb.toString();
		}

		if (entityFieldName.equals("sequence")) {
			Object object = pin.getSequence();

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

	protected Pin randomPin() throws Exception {
		return new Pin() {
			{
				id = RandomTestUtil.randomLong();
				positionX = RandomTestUtil.randomDouble();
				positionY = RandomTestUtil.randomDouble();
				sequence = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Pin randomIrrelevantPin() throws Exception {
		Pin randomIrrelevantPin = randomPin();

		return randomIrrelevantPin;
	}

	protected Pin randomPatchPin() throws Exception {
		return randomPin();
	}

	protected PinResource pinResource;
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
		LogFactoryUtil.getLog(BasePinResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.delivery.catalog.resource.v1_0.PinResource
			_pinResource;

}