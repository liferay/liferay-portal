/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.ProductGroup;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.ProductGroupResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.ProductGroupSerDes;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

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

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseProductGroupResourceTestCase {

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

		_productGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productGroupResource = ProductGroupResource.builder(
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

		ProductGroup productGroup1 = randomProductGroup();

		String json = objectMapper.writeValueAsString(productGroup1);

		ProductGroup productGroup2 = ProductGroupSerDes.toDTO(json);

		Assert.assertTrue(equals(productGroup1, productGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductGroup productGroup = randomProductGroup();

		String json1 = objectMapper.writeValueAsString(productGroup);
		String json2 = ProductGroupSerDes.toJSON(productGroup);

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

		ProductGroup productGroup = randomProductGroup();

		String json = ProductGroupSerDes.toJSON(productGroup);

		Assert.assertFalse(json.contains(regex));

		productGroup = ProductGroupSerDes.toDTO(json);
	}

	@Test
	public void testGetDiscountProductGroupProductGroup() throws Exception {
		ProductGroup postProductGroup =
			testGetDiscountProductGroupProductGroup_addProductGroup();

		ProductGroup getProductGroup =
			productGroupResource.getDiscountProductGroupProductGroup(
				testGetDiscountProductGroupProductGroup_getDiscountProductGroupId());

		assertEquals(postProductGroup, getProductGroup);
		assertValid(getProductGroup);
	}

	protected Long
			testGetDiscountProductGroupProductGroup_getDiscountProductGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ProductGroup
			testGetDiscountProductGroupProductGroup_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDiscountProductGroupProductGroup()
		throws Exception {

		ProductGroup productGroup =
			testGraphQLGetDiscountProductGroupProductGroup_addProductGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"discountProductGroupProductGroup",
								new HashMap<String, Object>() {
									{
										put(
											"discountProductGroupId",
											testGraphQLGetDiscountProductGroupProductGroup_getDiscountProductGroupId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/discountProductGroupProductGroup"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"discountProductGroupProductGroup",
									new HashMap<String, Object>() {
										{
											put(
												"discountProductGroupId",
												testGraphQLGetDiscountProductGroupProductGroup_getDiscountProductGroupId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/discountProductGroupProductGroup"))));
	}

	protected Long
			testGraphQLGetDiscountProductGroupProductGroup_getDiscountProductGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDiscountProductGroupProductGroupNotFound()
		throws Exception {

		Long irrelevantDiscountProductGroupId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"discountProductGroupProductGroup",
						new HashMap<String, Object>() {
							{
								put(
									"discountProductGroupId",
									irrelevantDiscountProductGroupId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"discountProductGroupProductGroup",
							new HashMap<String, Object>() {
								{
									put(
										"discountProductGroupId",
										irrelevantDiscountProductGroupId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductGroup
			testGraphQLGetDiscountProductGroupProductGroup_addProductGroup()
		throws Exception {

		return testGraphQLProductGroup_addProductGroup();
	}

	@Test
	public void testGetPriceModifierProductGroupProductGroup()
		throws Exception {

		ProductGroup postProductGroup =
			testGetPriceModifierProductGroupProductGroup_addProductGroup();

		ProductGroup getProductGroup =
			productGroupResource.getPriceModifierProductGroupProductGroup(
				testGetPriceModifierProductGroupProductGroup_getPriceModifierProductGroupId());

		assertEquals(postProductGroup, getProductGroup);
		assertValid(getProductGroup);
	}

	protected Long
			testGetPriceModifierProductGroupProductGroup_getPriceModifierProductGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ProductGroup
			testGetPriceModifierProductGroupProductGroup_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceModifierProductGroupProductGroup()
		throws Exception {

		ProductGroup productGroup =
			testGraphQLGetPriceModifierProductGroupProductGroup_addProductGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"priceModifierProductGroupProductGroup",
								new HashMap<String, Object>() {
									{
										put(
											"priceModifierProductGroupId",
											testGraphQLGetPriceModifierProductGroupProductGroup_getPriceModifierProductGroupId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/priceModifierProductGroupProductGroup"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				productGroup,
				ProductGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"priceModifierProductGroupProductGroup",
									new HashMap<String, Object>() {
										{
											put(
												"priceModifierProductGroupId",
												testGraphQLGetPriceModifierProductGroupProductGroup_getPriceModifierProductGroupId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/priceModifierProductGroupProductGroup"))));
	}

	protected Long
			testGraphQLGetPriceModifierProductGroupProductGroup_getPriceModifierProductGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPriceModifierProductGroupProductGroupNotFound()
		throws Exception {

		Long irrelevantPriceModifierProductGroupId =
			RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"priceModifierProductGroupProductGroup",
						new HashMap<String, Object>() {
							{
								put(
									"priceModifierProductGroupId",
									irrelevantPriceModifierProductGroupId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"priceModifierProductGroupProductGroup",
							new HashMap<String, Object>() {
								{
									put(
										"priceModifierProductGroupId",
										irrelevantPriceModifierProductGroupId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductGroup
			testGraphQLGetPriceModifierProductGroupProductGroup_addProductGroup()
		throws Exception {

		return testGraphQLProductGroup_addProductGroup();
	}

	protected ProductGroup testGraphQLProductGroup_addProductGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductGroup productGroup, List<ProductGroup> productGroups) {

		boolean contains = false;

		for (ProductGroup item : productGroups) {
			if (equals(productGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productGroups + " does not contain " + productGroup, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductGroup productGroup1, ProductGroup productGroup2) {

		Assert.assertTrue(
			productGroup1 + " does not equal " + productGroup2,
			equals(productGroup1, productGroup2));
	}

	protected void assertEquals(
		List<ProductGroup> productGroups1, List<ProductGroup> productGroups2) {

		Assert.assertEquals(productGroups1.size(), productGroups2.size());

		for (int i = 0; i < productGroups1.size(); i++) {
			ProductGroup productGroup1 = productGroups1.get(i);
			ProductGroup productGroup2 = productGroups2.get(i);

			assertEquals(productGroup1, productGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductGroup> productGroups1, List<ProductGroup> productGroups2) {

		Assert.assertEquals(productGroups1.size(), productGroups2.size());

		for (ProductGroup productGroup1 : productGroups1) {
			boolean contains = false;

			for (ProductGroup productGroup2 : productGroups2) {
				if (equals(productGroup1, productGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productGroups2 + " does not contain " + productGroup1,
				contains);
		}
	}

	protected void assertValid(ProductGroup productGroup) throws Exception {
		boolean valid = true;

		if (productGroup.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("productsCount", additionalAssertFieldName)) {
				if (productGroup.getProductsCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (productGroup.getTitle() == null) {
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

	protected void assertValid(Page<ProductGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductGroup> productGroups = page.getItems();

		int size = productGroups.size();

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
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						ProductGroup.class)) {

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
		ProductGroup productGroup1, ProductGroup productGroup2) {

		if (productGroup1 == productGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productGroup1.getId(), productGroup2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productsCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productGroup1.getProductsCount(),
						productGroup2.getProductsCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!equals(
						(Map)productGroup1.getTitle(),
						(Map)productGroup2.getTitle())) {

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

		if (!(_productGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productGroupResource;

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
		EntityField entityField, String operator, ProductGroup productGroup) {

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

		if (entityFieldName.equals("productsCount")) {
			sb.append(String.valueOf(productGroup.getProductsCount()));

			return sb.toString();
		}

		if (entityFieldName.equals("title")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

	protected ProductGroup randomProductGroup() throws Exception {
		return new ProductGroup() {
			{
				id = RandomTestUtil.randomLong();
				productsCount = RandomTestUtil.randomInt();
			}
		};
	}

	protected ProductGroup randomIrrelevantProductGroup() throws Exception {
		ProductGroup randomIrrelevantProductGroup = randomProductGroup();

		return randomIrrelevantProductGroup;
	}

	protected ProductGroup randomPatchProductGroup() throws Exception {
		return randomProductGroup();
	}

	protected ProductGroupResource productGroupResource;
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
		LogFactoryUtil.getLog(BaseProductGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		ProductGroupResource _productGroupResource;

}