/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.fields.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineExportTaskExecutor;
import com.liferay.batch.engine.BatchEngineTaskContentType;
import com.liferay.batch.engine.BatchEngineTaskExecuteStatus;
import com.liferay.batch.engine.service.BatchEngineExportTaskLocalService;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.internal.jaxrs.container.request.filter.test.TransactionContainerRequestFilterTest;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import java.io.Serializable;

import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Alejandro Tardín
 * @author Ivica Cardic
 */
@RunWith(Arquillian.class)
public class NestedFieldsSetterUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			NestedFieldsSetterUtilTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistrations = Arrays.asList(
			bundleContext.registerService(
				Application.class,
				new TransactionContainerRequestFilterTest.TestApplication(),
				HashMapDictionaryBuilder.<String, Object>put(
					"liferay.auth.verifier", true
				).put(
					"liferay.oauth2", false
				).put(
					"osgi.jaxrs.application.base", "/test-vulcan"
				).put(
					"osgi.jaxrs.extension.select",
					"(osgi.jaxrs.name=Liferay.Vulcan)"
				).put(
					"osgi.jaxrs.name", "Test.Vulcan"
				).build()),
			bundleContext.registerService(
				ProductResource_v1_0.class, new ProductResource_v1_0_Impl(),
				HashMapDictionaryBuilder.<String, Object>put(
					"api.version", "v1.0"
				).put(
					"batch.engine.entity.class.name", Product.class.getName()
				).put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.name", "Test.Vulcan.v1.0"
				).put(
					"entity.class.name", Product.class.getName()
				).put(
					"nested.field.support", "true"
				).put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan)"
				).put(
					"osgi.jaxrs.resource", "true"
				).build()),
			bundleContext.registerService(
				ProductResource_v2_0.class, new ProductResource_v2_0_Impl(),
				HashMapDictionaryBuilder.<String, Object>put(
					"api.version", "v2.0"
				).put(
					"batch.engine.entity.class.name", Product.class.getName()
				).put(
					"batch.engine.task.item.delegate", "true"
				).put(
					"batch.engine.task.item.delegate.name", "Test.Vulcan.v2.0"
				).put(
					"entity.class.name", Product.class.getName()
				).put(
					"nested.field.support", "true"
				).put(
					"osgi.jaxrs.application.select",
					"(osgi.jaxrs.name=Test.Vulcan)"
				).put(
					"osgi.jaxrs.resource", "true"
				).build()));
	}

	@After
	public void tearDown() {
		_serviceRegistrations.forEach(ServiceRegistration::unregister);
	}

	@Test
	public void testGetNestedFields() throws Exception {
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"id", 1
				).put(
					"productOptions",
					JSONUtil.putAll(
						JSONUtil.put(
							"id", 10
						).put(
							"name", "test1"
						),
						JSONUtil.put(
							"id", 20
						).put(
							"name", "test2"
						),
						JSONUtil.put(
							"id", 30
						).put(
							"name", "test3"
						))
				).put(
					"skus",
					JSONUtil.putAll(
						JSONUtil.put("id", 1), JSONUtil.put("id", 2),
						JSONUtil.put("id", 3), JSONUtil.put("id", 4))
				),
				JSONUtil.put(
					"id", 2
				).put(
					"productOptions", JSONUtil.putAll()
				).put(
					"skus", JSONUtil.putAll()
				)),
			HashMapBuilder.put(
				"nestedFields", "productOptions,skus"
			).build(),
			"v1.0");
	}

	@Test
	public void testGetNestedFieldsWithDeeplyNestedFields() throws Exception {
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"id", 1
				).put(
					"productOptions",
					JSONUtil.putAll(
						JSONUtil.put(
							"id", 10
						).put(
							"name", "test1"
						).put(
							"productOptionValues",
							JSONUtil.putAll(
								JSONUtil.put("id", 100),
								JSONUtil.put("id", 200),
								JSONUtil.put("id", 300))
						),
						JSONUtil.put(
							"id", 20
						).put(
							"name", "test2"
						).put(
							"productOptionValues",
							JSONUtil.putAll(
								JSONUtil.put("id", 400),
								JSONUtil.put("id", 500))
						),
						JSONUtil.put(
							"id", 30
						).put(
							"name", "test3"
						).put(
							"productOptionValues", JSONUtil.putAll()
						))
				),
				JSONUtil.put(
					"id", 2
				).put(
					"productOptions", JSONUtil.putAll()
				)),
			HashMapBuilder.put(
				"nestedFields",
				"productOptions,productOptions.productOptionValues"
			).build(),
			"v1.0");
	}

	@Test
	public void testGetNestedFieldsWithNestedFieldId() throws Exception {
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"categories",
					JSONUtil.putAll(
						JSONUtil.put("id", 1), JSONUtil.put("id", 2),
						JSONUtil.put("id", 3))
				).put(
					"id", 1
				),
				JSONUtil.put(
					"categories", JSONUtil.putAll()
				).put(
					"id", 2
				)),
			HashMapBuilder.put(
				"nestedFields", "categories"
			).build(),
			"v1.0");
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"categories",
					JSONUtil.putAll(
						JSONUtil.put("id", 1), JSONUtil.put("id", 2))
				).put(
					"id", 1
				),
				JSONUtil.put(
					"categories", JSONUtil.putAll()
				).put(
					"id", 2
				)),
			HashMapBuilder.put(
				"nestedFields", "categories"
			).build(),
			"v2.0");
	}

	@Test
	public void testGetNestedFieldsWithNonexistentFieldName() throws Exception {
		_test(
			JSONUtil.putAll(JSONUtil.put("id", 1), JSONUtil.put("id", 2)),
			Collections.emptyMap(), "v1.0");
		_test(
			JSONUtil.putAll(JSONUtil.put("id", 1), JSONUtil.put("id", 2)),
			HashMapBuilder.put(
				"nestedFields", "nonexistentField"
			).build(),
			"v1.0");
	}

	@Test
	public void testGetNestedFieldsWithPagination() throws Exception {
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"id", 1
				).put(
					"skus",
					JSONUtil.putAll(
						JSONUtil.put("id", 1), JSONUtil.put("id", 2))
				),
				JSONUtil.put(
					"id", 2
				).put(
					"skus", JSONUtil.putAll()
				)),
			HashMapBuilder.put(
				"nestedFields", "skus"
			).put(
				"skus.page", "1"
			).put(
				"skus.pageSize", "2"
			).build(),
			false, "v1.0");
	}

	@Test
	public void testGetNestedFieldsWithQueryParameter() throws Exception {
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"id", 1
				).put(
					"productOptions",
					JSONUtil.putAll(
						JSONUtil.put(
							"id", 20
						).put(
							"name", "test2"
						))
				),
				JSONUtil.put(
					"id", 2
				).put(
					"productOptions", JSONUtil.putAll()
				)),
			HashMapBuilder.put(
				"nestedFields", "productOptions"
			).put(
				"productOptions.name", "test2"
			).build(),
			false, "v1.0");
	}

	@Test
	public void testGetNestedFieldsWithResourceVersioning() throws Exception {
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"id", 1
				).put(
					"skus",
					JSONUtil.putAll(
						JSONUtil.put("id", 1), JSONUtil.put("id", 2),
						JSONUtil.put("id", 3), JSONUtil.put("id", 4),
						JSONUtil.put("id", 5), JSONUtil.put("id", 6))
				),
				JSONUtil.put("id", 2)),
			HashMapBuilder.put(
				"nestedFields", "skus"
			).build(),
			"v2.0");
	}

	@Test
	public void testGetNestedFieldsWithoutOverridingMethod() throws Exception {
		_test(
			JSONUtil.putAll(
				JSONUtil.put(
					"externalCode", "codigoExterno"
				).put(
					"id", 1
				),
				JSONUtil.put("id", 2)),
			HashMapBuilder.put(
				"externalCode.AcceptLanguage", "es_ES"
			).put(
				"nestedFields", "externalCode"
			).build(),
			false, "v1.0");
	}

	@Path("/v1.0")
	public abstract static class BaseProductResource_v1_0_Impl
		implements ProductResource_v1_0,
				   VulcanBatchEngineTaskItemDelegate<Product> {

		@Override
		public void create(
			Collection<Product> items, Map<String, Serializable> parameters) {
		}

		@Override
		public void delete(
			Collection<Product> items, Map<String, Serializable> parameters) {
		}

		@Override
		public EntityModel getEntityModel(
			Map<String, List<String>> multivaluedMap) {

			return null;
		}

		@GET
		@Path("/productOptions/{id}/productOptionValues")
		@Produces("application/*")
		public List<ProductOptionValue> getProductOptionValues(
			@PathParam("id") Long id) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products/{id}/productOptions")
		@Produces("application/*")
		public List<ProductOption> getProductOptions(
			@PathParam("id") Long id, @QueryParam("name") String name) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products")
		@Produces("application/json")
		public List<Product> getProducts(
			@QueryParam("inlineInitialization") boolean inlineInitialization) {

			return Collections.emptyList();
		}

		@Override
		public Class<?> getResourceClass() {
			return getClass();
		}

		@Override
		public String getResourceName() {
			return Product.class.getName();
		}

		@GET
		@Path("/products/{id}/skus")
		@Produces("application/*")
		public Page<Sku> getSkus(
			@PathParam("id") Long id, @Context Pagination pagination) {

			return Page.of(Collections.emptyList());
		}

		@Override
		public String getVersion() {
			return "v1.0";
		}

		@Override
		public Page<Product> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search) {

			return Page.of(
				getProducts(
					GetterUtil.getBoolean(
						parameters.get("inlineInitialization"))));
		}

		@Override
		public void setContextBatchUnsafeBiConsumer(
			UnsafeBiConsumer
				<Collection<Product>,
				 UnsafeFunction<Product, Product, Exception>, Exception>
					contextBatchUnsafeBiConsumer) {
		}

		@Override
		public void setContextCompany(Company contextCompany) {
		}

		@Override
		public void setContextUriInfo(UriInfo uriInfo) {
		}

		@Override
		public void setContextUser(User contextUser) {
		}

		@Override
		public void setGroupLocalService(GroupLocalService groupLocalService) {
		}

		@Override
		public void setLanguageId(String languageId) {
		}

		@Override
		public void setResourceActionLocalService(
			ResourceActionLocalService resourceActionLocalService) {
		}

		@Override
		public void setResourcePermissionLocalService(
			ResourcePermissionLocalService resourcePermissionLocalService) {
		}

		@Override
		public void setRoleLocalService(RoleLocalService roleLocalService) {
		}

		@Override
		public void update(
			Collection<Product> items, Map<String, Serializable> parameters) {
		}

	}

	@Path("/v2.0")
	public abstract static class BaseProductResource_v2_0_Impl
		implements ProductResource_v2_0,
				   VulcanBatchEngineTaskItemDelegate<Product> {

		@Override
		public void create(
			Collection<Product> items, Map<String, Serializable> parameters) {
		}

		@Override
		public void delete(
			Collection<Product> items, Map<String, Serializable> parameters) {
		}

		@GET
		@Path("/products/{productExternalCode}/categories")
		@Produces("application/json")
		public List<Category> getCategories(
			@PathParam("productExternalCode") String productExternalCode) {

			return Collections.emptyList();
		}

		@Override
		public EntityModel getEntityModel(
				Map<String, List<String>> multivaluedMap)
			throws Exception {

			return null;
		}

		@GET
		@Path("/products/{id}/productOptions")
		@Produces("application/*")
		public List<ProductOption> getProductOptions(
			@PathParam("id") Long id, @QueryParam("name") String name) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products")
		@Produces("application/json")
		public List<Product> getProducts(
			@QueryParam("inlineInitialization") boolean inlineInitialization) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products/{id}/skus")
		@Produces("application/*")
		public Page<Sku> getSkus(
			@PathParam("id") Long id, @Context Pagination pagination) {

			return Page.of(Collections.emptyList());
		}

		@Override
		public String getVersion() {
			return "v2.0";
		}

		@Override
		public Page<Product> read(
			Filter filter, Pagination pagination, Sort[] sorts,
			Map<String, Serializable> parameters, String search) {

			return Page.of(
				getProducts(
					GetterUtil.getBoolean(
						parameters.get("inlineInitialization"))));
		}

		@Override
		public void setContextBatchUnsafeBiConsumer(
			UnsafeBiConsumer
				<Collection<Product>,
				 UnsafeFunction<Product, Product, Exception>, Exception>
					contextBatchUnsafeBiConsumer) {
		}

		@Override
		public void setContextCompany(Company contextCompany) {
		}

		@Override
		public void setContextUriInfo(UriInfo uriInfo) {
		}

		@Override
		public void setContextUser(User contextUser) {
		}

		@Override
		public void setGroupLocalService(GroupLocalService groupLocalService) {
		}

		@Override
		public void setLanguageId(String languageId) {
		}

		@Override
		public void setResourceActionLocalService(
			ResourceActionLocalService resourceActionLocalService) {
		}

		@Override
		public void setResourcePermissionLocalService(
			ResourcePermissionLocalService resourcePermissionLocalService) {
		}

		@Override
		public void setRoleLocalService(RoleLocalService roleLocalService) {
		}

		@Override
		public void update(
			Collection<Product> items, Map<String, Serializable> parameters) {
		}

	}

	public static class Category {

		public Long getId() {
			return _id;
		}

		public String getName() {
			return _name;
		}

		public void setId(Long id) {
			_id = id;
		}

		public void setName(String name) {
			_name = name;
		}

		private Long _id;
		private String _name;

	}

	public static class Product {

		public Category[] getCategories() {
			return _categories;
		}

		public String getExternalCode() {
			return _externalCode;
		}

		public Long getId() {
			return _id;
		}

		public ProductOption[] getProductOptions() {
			return _productOptions;
		}

		public Sku[] getSkus() {
			return _skus;
		}

		public void setExternalCode(String externalCode) {
			_externalCode = externalCode;
		}

		public void setId(Long id) {
			_id = id;
		}

		private Category[] _categories;
		private String _externalCode;
		private Long _id;
		private ProductOption[] _productOptions;
		private Sku[] _skus;

	}

	public static class ProductOption {

		public Long getId() {
			return _id;
		}

		public String getName() {
			return _name;
		}

		public ProductOptionValue[] getProductOptionValues() {
			return _productOptionValues;
		}

		public void setId(Long id) {
			_id = id;
		}

		public void setName(String name) {
			_name = name;
		}

		private Long _id;
		private String _name;
		private ProductOptionValue[] _productOptionValues;

	}

	public static class ProductOptionValue {

		public Long getId() {
			return _id;
		}

		public void setId(Long id) {
			_id = id;
		}

		private Long _id;

	}

	public static class ProductResource_v1_0_Impl
		extends BaseProductResource_v1_0_Impl {

		@NestedField("categories")
		public List<Category> getCategories(
			@NestedFieldId("externalCode") String externalCode) {

			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (!Objects.equals(externalCode, "externalCode")) {
				return Collections.emptyList();
			}

			return Arrays.asList(
				_toCategory(1L), _toCategory(2L), _toCategory(3L));
		}

		@NestedField("externalCode")
		public String getExternalCodeByQueryParam(
			@QueryParam("AcceptLanguage") String acceptLanguage) {

			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (!Objects.equals(acceptLanguage, "es_ES")) {
				return "";
			}

			return "codigoExterno";
		}

		@NestedField("productOptionValues")
		@Override
		public List<ProductOptionValue> getProductOptionValues(Long id) {
			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (id == 10) {
				return Arrays.asList(
					_toProductOptionValue(100L), _toProductOptionValue(200L),
					_toProductOptionValue(300L));
			}
			else if (id == 20) {
				return Arrays.asList(
					_toProductOptionValue(400L), _toProductOptionValue(500L));
			}

			return Collections.emptyList();
		}

		@NestedField("productOptions")
		@Override
		public List<ProductOption> getProductOptions(Long id, String name) {
			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (id != 1) {
				return Collections.emptyList();
			}

			List<ProductOption> productOptions = Arrays.asList(
				_toProductOption(10L, "test1"), _toProductOption(20L, "test2"),
				_toProductOption(30L, "test3"));

			if (name != null) {
				productOptions = ListUtil.filter(
					productOptions,
					productOption -> Objects.equals(
						productOption.getName(), name));
			}

			return productOptions;
		}

		@Override
		public List<Product> getProducts(boolean inlineInitialization) {
			return Arrays.asList(
				_toProduct("externalCode", 1L, inlineInitialization),
				_toProduct(null, 2L, inlineInitialization));
		}

		@NestedField("skus")
		@Override
		public Page<Sku> getSkus(Long id, Pagination pagination) {
			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (!Objects.equals(id, 1L)) {
				return Page.of(Collections.emptyList());
			}

			List<Sku> skus = Arrays.asList(
				_toSku(1L), _toSku(2L), _toSku(3L), _toSku(4L));

			if ((pagination.getStartPosition() == -1) &&
				(pagination.getEndPosition() == -1)) {

				return Page.of(skus);
			}

			skus = skus.subList(
				pagination.getStartPosition(),
				Math.min(pagination.getEndPosition(), skus.size()));

			return Page.of(skus);
		}

		protected Company contextCompany;

		@Context
		private Company _company;

	}

	public static class ProductResource_v2_0_Impl
		extends BaseProductResource_v2_0_Impl {

		@NestedField("categories")
		public List<Category> getCategories(
			@NestedFieldId("externalCode") String productExternalCode) {

			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (!Objects.equals(productExternalCode, "externalCode")) {
				return Collections.emptyList();
			}

			return Arrays.asList(_toCategory(1L), _toCategory(2L));
		}

		@NestedField("productOptions")
		@Override
		public List<ProductOption> getProductOptions(Long id, String name) {
			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (id != 1) {
				return Collections.emptyList();
			}

			List<ProductOption> productOptions = Arrays.asList(
				_toProductOption(1L, "test1"), _toProductOption(2L, "test2"),
				_toProductOption(3L, "test3"));

			if (name != null) {
				productOptions = ListUtil.filter(
					productOptions,
					productOption -> Objects.equals(
						productOption.getName(), name));
			}

			return productOptions;
		}

		@Override
		public List<Product> getProducts(boolean inlineInitialization) {
			return Arrays.asList(
				_toSubproduct("externalCode", 1L, inlineInitialization),
				_toProduct(null, 2L, inlineInitialization));
		}

		@NestedField(parentClass = Product.class, value = "skus")
		@Override
		public Page<Sku> getSkus(Long id, Pagination pagination) {
			Assert.assertNotNull(_company);
			Assert.assertNotNull(contextCompany);

			if (!Objects.equals(id, 1L)) {
				return Page.of(Collections.emptyList());
			}

			List<Sku> skus = Arrays.asList(
				_toSku(1L), _toSku(2L), _toSku(3L), _toSku(4L), _toSku(5L),
				_toSku(6L));

			if ((pagination.getStartPosition() == -1) &&
				(pagination.getEndPosition() == -1)) {

				return Page.of(skus);
			}

			skus = skus.subList(
				pagination.getStartPosition(),
				Math.min(pagination.getEndPosition(), skus.size()));

			return Page.of(skus);
		}

		protected Company contextCompany;

		@Context
		private Company _company;

	}

	public static class Sku {

		public Long getId() {
			return _id;
		}

		public void setId(Long id) {
			_id = id;
		}

		private Long _id;

	}

	public static class Subproduct extends Product {
	}

	public interface ProductResource_v1_0 {

		public List<ProductOptionValue> getProductOptionValues(Long id);

		public List<ProductOption> getProductOptions(Long id, String name);

		public List<Product> getProducts(boolean inlineInitialization);

		public Page<Sku> getSkus(Long id, Pagination pagination);

	}

	public interface ProductResource_v2_0 {

		public List<Category> getCategories(String externalCode);

		public List<ProductOption> getProductOptions(Long id, String name);

		public List<Product> getProducts(boolean inlineInitialization);

		public Page<Sku> getSkus(Long id, Pagination pagination);

	}

	private static Category _toCategory(long id) {
		Category category = new Category();

		category.setId(id);

		return category;
	}

	private static Product _toProduct(
		String externalCode, long id, boolean inlineInitialization) {

		if (inlineInitialization) {
			return new Product() {
				{
					setExternalCode(externalCode);
					setId(id);
				}
			};
		}

		Product product = new Product();

		product.setExternalCode(externalCode);
		product.setId(id);

		return product;
	}

	private static ProductOption _toProductOption(long id, String name) {
		ProductOption productOption = new ProductOption();

		productOption.setId(id);
		productOption.setName(name);

		return productOption;
	}

	private static ProductOptionValue _toProductOptionValue(long id) {
		ProductOptionValue productOptionValue = new ProductOptionValue();

		productOptionValue.setId(id);

		return productOptionValue;
	}

	private static Sku _toSku(long id) {
		Sku sku = new Sku();

		sku.setId(id);

		return sku;
	}

	private static Subproduct _toSubproduct(
		String externalCode, long id, boolean inlineInitialization) {

		if (inlineInitialization) {
			return new Subproduct() {
				{
					setExternalCode(externalCode);
					setId(id);
				}
			};
		}

		Subproduct subproduct = new Subproduct();

		subproduct.setExternalCode(externalCode);
		subproduct.setId(id);

		return subproduct;
	}

	private String _getQueryString(Map<String, String> parameters) {
		String queryString = StringUtil.merge(
			TransformUtil.transform(
				HashMapBuilder.create(
					parameters
				).build(
				).entrySet(),
				entry -> StringBundler.concat(
					URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8),
					"=",
					URLEncoder.encode(
						entry.getValue(), StandardCharsets.UTF_8))),
			"&");

		if (Validator.isNotNull(queryString)) {
			return "?" + queryString;
		}

		return StringPool.BLANK;
	}

	private void _test(
			JSONArray expectedJSONArray, Map<String, String> queryParameters,
			boolean testBatch, String version)
		throws Exception {

		for (boolean inlineInitialization : new boolean[] {false, true}) {
			HashMap<String, String> parameters = HashMapBuilder.putAll(
				queryParameters
			).put(
				"inlineInitialization", String.valueOf(inlineInitialization)
			).build();

			JSONAssert.assertEquals(
				expectedJSONArray.toString(),
				HTTPTestUtil.invokeToString(
					null,
					StringBundler.concat(
						"test-vulcan/", version, "/products",
						_getQueryString(parameters)),
					Http.Method.GET),
				JSONCompareMode.STRICT_ORDER);

			if (!testBatch) {
				continue;
			}

			BatchEngineExportTaskExecutor.Result result =
				_batchEngineExportTaskExecutor.execute(
					_batchEngineExportTaskLocalService.addBatchEngineExportTask(
						null, TestPropsValues.getCompanyId(),
						TestPropsValues.getUserId(), null,
						Product.class.getName(),
						BatchEngineTaskContentType.JSON.toString(),
						BatchEngineTaskExecuteStatus.INITIAL.name(), null,
						HashMapBuilder.<String, Serializable>putAll(
							parameters
						).put(
							"batchNestedFields", parameters.get("nestedFields")
						).build(),
						"Test.Vulcan." + version),
					new BatchEngineExportTaskExecutor.Settings() {

						@Override
						public boolean isCompressContent() {
							return false;
						}

						@Override
						public boolean isPersist() {
							return false;
						}

					});

			JSONAssert.assertEquals(
				expectedJSONArray.toString(),
				StringUtil.read(result.getInputStream()),
				JSONCompareMode.STRICT_ORDER);
		}
	}

	private void _test(
			JSONArray expectedJSONArray, Map<String, String> queryParameters,
			String version)
		throws Exception {

		_test(expectedJSONArray, queryParameters, true, version);
	}

	@Inject
	private BatchEngineExportTaskExecutor _batchEngineExportTaskExecutor;

	@Inject
	private BatchEngineExportTaskLocalService
		_batchEngineExportTaskLocalService;

	private List<ServiceRegistration<?>> _serviceRegistrations;

}