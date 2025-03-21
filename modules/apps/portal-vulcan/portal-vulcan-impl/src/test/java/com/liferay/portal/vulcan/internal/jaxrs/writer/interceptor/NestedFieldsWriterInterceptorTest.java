/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.internal.fields.servlet.NestedFieldsHttpServletRequestWrapper;
import com.liferay.portal.vulcan.internal.fields.servlet.NestedFieldsHttpServletRequestWrapperTest;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.validation.constraints.NotNull;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.IOException;

import java.lang.reflect.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.jaxrs.provider.ProviderFactory;
import org.apache.cxf.message.ExchangeImpl;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;

/**
 * @author Ivica Cardic
 */
public class NestedFieldsWriterInterceptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		BundleContext bundleContext = Mockito.mock(BundleContext.class);

		Filter filter = Mockito.mock(Filter.class);

		Mockito.when(
			bundleContext.createFilter(Mockito.anyString())
		).thenReturn(
			filter
		);

		_nestedFieldServiceTrackerCustomizer = Mockito.spy(
			new NestedFieldsWriterInterceptor.
				NestedFieldServiceTrackerCustomizer(bundleContext));

		_nestedFieldsWriterInterceptor = Mockito.spy(
			new NestedFieldsWriterInterceptor(
				bundleContext, _nestedFieldServiceTrackerCustomizer));

		Mockito.doReturn(
			new MockProviderFactory()
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getProviderFactory(
			Mockito.any(Message.class)
		);

		ServiceReference<Object> serviceReference1 = new MockServiceReference();

		_productResource_v1_0_Impl = new ProductResource_v1_0_Impl();

		Mockito.doReturn(
			_productResource_v1_0_Impl
		).when(
			bundleContext
		).getService(
			serviceReference1
		);

		Mockito.doReturn(
			new MockServiceObjects<>(_productResource_v1_0_Impl)
		).when(
			bundleContext
		).getServiceObjects(
			serviceReference1
		);

		_nestedFieldServiceTrackerCustomizer.addingService(serviceReference1);

		ServiceReference<Object> serviceReference2 = new MockServiceReference();

		_productResource_v2_0_Impl = new ProductResource_v2_0_Impl();

		Mockito.doReturn(
			_productResource_v2_0_Impl
		).when(
			bundleContext
		).getService(
			serviceReference2
		);

		Mockito.doReturn(
			new MockServiceObjects<>(_productResource_v2_0_Impl)
		).when(
			bundleContext
		).getServiceObjects(
			serviceReference2
		);

		_nestedFieldServiceTrackerCustomizer.addingService(serviceReference2);

		_writerInterceptorContext = Mockito.mock(
			WriterInterceptorContext.class);

		_writerInterceptorContext = Mockito.mock(
			WriterInterceptorContext.class);
	}

	@Test
	public void testGetNestedFieldsForMultipleItems() throws Exception {
		_testGetNestedFieldsForMultipleItems(false);
		_testGetNestedFieldsForMultipleItems(true);
	}

	@Test
	public void testGetNestedFieldsForSingleItem() throws Exception {
		_testGetNestedFieldsForSingleItem(false);
		_testGetNestedFieldsForSingleItem(true);
	}

	@Test
	public void testGetNestedFieldsWithDeeplyNestedFields() throws Exception {
		_testGetNestedFieldsWithDeeplyNestedFields(false);
		_testGetNestedFieldsWithDeeplyNestedFields(true);
	}

	@Test
	public void testGetNestedFieldsWithNestedFieldId() throws Exception {
		_testGetNestedFieldsWithNestedFieldId(false);
		_testGetNestedFieldsWithNestedFieldId(true);
	}

	@Test
	public void testGetNestedFieldsWithNonexistendFieldName() throws Exception {
		_testGetNestedFieldsWithNonexistendFieldName(false);
		_testGetNestedFieldsWithNonexistendFieldName(true);
	}

	@Test
	public void testGetNestedFieldsWithoutContextAnnotation() throws Exception {
		_testGetNestedFieldsWithoutContextAnnotation(false);
		_testGetNestedFieldsWithoutContextAnnotation(true);
	}

	@Test
	public void testGetNestedFieldsWithoutOverridingMethod()
		throws IOException {

		_testGetNestedFieldsWithoutOverridingMethod(false);
		_testGetNestedFieldsWithoutOverridingMethod(true);
	}

	@Test
	public void testGetNestedFieldsWithPagination() throws Exception {
		_testGetNestedFieldsWithPagination(false);
		_testGetNestedFieldsWithPagination(true);
	}

	@Test
	public void testGetNestedFieldsWithQueryParameter() throws IOException {
		_testGetNestedFieldsWithQueryParameter(false);
		_testGetNestedFieldsWithQueryParameter(true);
	}

	@Test
	public void testGetNestedFieldsWithResourceVersioning() throws Exception {
		_testGetNestedFieldsWithResourceVersioning(false);
		_testGetNestedFieldsWithResourceVersioning(true);
	}

	@Test
	public void testGetNestedFieldsWithSubitem() throws Exception {
		_testGetNestedFieldsWithSubitem(false);
		_testGetNestedFieldsWithSubitem(true);
	}

	@Test
	public void testInjectResourceContexts() throws Exception {
		_testInjectResourceContexts(false);
		_testInjectResourceContexts(true);
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

	private MessageImpl _getMessageImpl() {
		MessageImpl messageImpl = new MessageImpl();

		messageImpl.setExchange(new ExchangeImpl());

		return messageImpl;
	}

	private MultivaluedHashMap<String, String> _getPathParameters() {
		return new MultivaluedHashMap<String, String>() {
			{
				putSingle("id", "1");
			}
		};
	}

	private void _testGetNestedFieldsForMultipleItems(
			boolean inlineInitialization)
		throws Exception {

		Product product1 = _toProduct(null, 1L, inlineInitialization);
		Product product2 = _toProduct(null, 2L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			Arrays.asList(product1, product2)
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Arrays.asList("productOptions", "skus"),
				new MultivaluedHashMap<>(), new MultivaluedHashMap<>(),
				"v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product1.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 4, skus.length);

		ProductOption[] productOptions = product1.getProductOptions();

		Assert.assertEquals(
			Arrays.toString(productOptions), 3, productOptions.length);
	}

	private void _testGetNestedFieldsForSingleItem(boolean inlineInitialization)
		throws Exception {

		Product product = _toProduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Arrays.asList("productOptions", "skus"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 4, skus.length);

		ProductOption[] productOptions = product.getProductOptions();

		Assert.assertEquals(
			Arrays.toString(productOptions), 3, productOptions.length);
	}

	private void _testGetNestedFieldsWithDeeplyNestedFields(
			boolean inlineInitialization)
		throws Exception {

		Product product1 = _toProduct(null, 1L, inlineInitialization);
		Product product2 = _toProduct(null, 2L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			Arrays.asList(product1, product2)
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(),
				Arrays.asList(
					"productOptions", "productOptions.productOptionValues"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		ProductOption[] productOptions = product1.getProductOptions();

		Assert.assertEquals(
			Arrays.toString(productOptions), 3, productOptions.length);

		ProductOptionValue[] productOptionValues =
			productOptions[0].getProductOptionValues();

		Assert.assertEquals(
			Arrays.toString(productOptionValues), 3,
			productOptionValues.length);

		productOptionValues = productOptions[1].getProductOptionValues();

		Assert.assertEquals(
			Arrays.toString(productOptionValues), 2,
			productOptionValues.length);

		productOptionValues = productOptions[2].getProductOptionValues();

		Assert.assertEquals(
			Arrays.toString(productOptionValues), 0,
			productOptionValues.length);
	}

	private void _testGetNestedFieldsWithNestedFieldId(
			boolean inlineInitialization)
		throws Exception {

		Product product = _toProduct("externalCode", 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.singletonList("categories"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Category[] categories = product.getCategories();

		Assert.assertEquals(Arrays.toString(categories), 3, categories.length);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.singletonList("categories"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v2.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		categories = product.getCategories();

		Assert.assertEquals(Arrays.toString(categories), 2, categories.length);
	}

	private void _testGetNestedFieldsWithNonexistendFieldName(
			boolean inlineInitialization)
		throws Exception {

		Product product = _toProduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.emptyList(),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Assert.assertNull(product.getSkus());

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.singletonList("nonexistent"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Assert.assertNull(product.getSkus());
	}

	private void _testGetNestedFieldsWithoutContextAnnotation(
			boolean inlineInitialization)
		throws Exception {

		_productResource_v1_0_Impl.contextThemeDisplay = null;

		Product product = _toProduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest("skus")
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Arrays.asList("productOptions", "skus"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		Assert.assertNull(_productResource_v1_0_Impl.contextThemeDisplay);

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Assert.assertNotNull(_productResource_v1_0_Impl.contextThemeDisplay);
	}

	private void _testGetNestedFieldsWithoutOverridingMethod(
			boolean inlineInitialization)
		throws IOException {

		Product product = _toProduct("externalCode", 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest("externalCode")
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		MultivaluedHashMap<String, String> queryParameters =
			new MultivaluedHashMap<String, String>() {
				{
					putSingle("externalCode.AcceptLanguage", "es_ES");
				}
			};

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.singletonList("externalCode"),
				_getPathParameters(), queryParameters, "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Assert.assertEquals("codigoExterno", product.getExternalCode());
	}

	private void _testGetNestedFieldsWithPagination(
			boolean inlineInitialization)
		throws Exception {

		Product product = _toProduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest(
					"skus", "page", String.valueOf(1), "pageSize",
					String.valueOf(2))
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.singletonList("skus"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 2, skus.length);
	}

	private void _testGetNestedFieldsWithQueryParameter(
			boolean inlineInitialization)
		throws IOException {

		Product product = _toProduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest("productOptions")
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		MultivaluedHashMap<String, String> queryParameters =
			new MultivaluedHashMap<String, String>() {
				{
					putSingle(
						"productOptions.createDate",
						"2019-02-19T08:03:11.763Z");
					putSingle("productOptions.name", "test2");
				}
			};

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(),
				Collections.singletonList("productOptions"),
				_getPathParameters(), queryParameters, "v1.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		ProductOption[] productOptions = product.getProductOptions();

		Assert.assertEquals(
			Arrays.toString(productOptions), 1, productOptions.length);

		ProductOption productOption = productOptions[0];

		Assert.assertEquals("test2", productOption.getName());
	}

	private void _testGetNestedFieldsWithResourceVersioning(
			boolean inlineInitialization)
		throws Exception {

		Product product = _toProduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.singletonList("skus"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v2.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 6, skus.length);
	}

	private void _testGetNestedFieldsWithSubitem(boolean inlineInitialization)
		throws Exception {

		Subproduct subproduct = _toSubproduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			subproduct
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Collections.singletonList("skus"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v2.0"));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = subproduct.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 6, skus.length);
	}

	private void _testInjectResourceContexts(boolean inlineInitialization)
		throws Exception {

		_productResource_v1_0_Impl.themeDisplay = null;

		Product product = _toProduct(null, 1L, inlineInitialization);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest("skus")
		).when(
			_nestedFieldServiceTrackerCustomizer
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				1, _getMessageImpl(), Arrays.asList("productOptions", "skus"),
				_getPathParameters(), new MultivaluedHashMap<>(), "v1.0"));

		Assert.assertNull(_productResource_v1_0_Impl.themeDisplay);

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Assert.assertNotNull(_productResource_v1_0_Impl.themeDisplay);
	}

	private Subproduct _toSubproduct(
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

	private NestedFieldsWriterInterceptor.NestedFieldServiceTrackerCustomizer
		_nestedFieldServiceTrackerCustomizer;
	private NestedFieldsWriterInterceptor _nestedFieldsWriterInterceptor;
	private ProductResource_v1_0_Impl _productResource_v1_0_Impl;
	private ProductResource_v2_0_Impl _productResource_v2_0_Impl;
	private WriterInterceptorContext _writerInterceptorContext;

	@Path("/v1.0")
	private static class BaseProductResource_v1_0_Impl
		implements ProductResource_v1_0 {

		@GET
		@Path("/products/{id}/productOptions")
		@Produces("application/*")
		public List<ProductOption> getProductOptions(
			@NotNull @PathParam("id") Long id,
			@QueryParam("name") String name) {

			return Collections.emptyList();
		}

		@GET
		@Path("/productOptions/{id}/productOptionValues")
		@Produces("application/*")
		public List<ProductOptionValue> getProductOptionValues(
			@NotNull @PathParam("id") Long id) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products")
		@Produces("application/*")
		public List<Product> getProducts() {
			return Collections.emptyList();
		}

		@GET
		@Path("/products/{id}/skus")
		@Produces("application/*")
		public Page<Sku> getSkus(
			@NotNull @PathParam("id") Long id,
			@Context @NotNull Pagination pagination) {

			return Page.of(Collections.emptyList());
		}

	}

	@Path("/v2.0")
	private static class BaseProductResource_v2_0_Impl
		implements ProductResource_v2_0 {

		@GET
		@Path("/products/{productExternalCode}/categories")
		@Produces("application/*")
		public List<Category> getCategories(
			@NotNull @PathParam("productExternalCode") String
				productExternalCode) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products/{id}/productOptions")
		@Produces("application/*")
		public List<ProductOption> getProductOptions(
			@NotNull @PathParam("id") Long id,
			@QueryParam("name") String name) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products")
		@Produces("application/*")
		public List<Product> getProducts() {
			return Collections.emptyList();
		}

		@GET
		@Path("/products/{id}/skus")
		@Produces("application/*")
		public Page<Sku> getSkus(
			@NotNull @PathParam("id") Long id,
			@Context @NotNull Pagination pagination) {

			return Page.of(Collections.emptyList());
		}

	}

	private static class Category {

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

	@SuppressWarnings("unchecked")
	private static class MockProviderFactory extends ProviderFactory {

		@Override
		public <T> ContextProvider<T> createContextProvider(
			Type contextType, Message message) {

			if (Objects.equals(
					contextType.getTypeName(), Pagination.class.getName())) {

				return (ContextProvider<T>)new ContextProvider<Pagination>() {

					@Override
					public Pagination createContext(Message message) {
						NestedFieldsHttpServletRequestWrapper
							nestedFieldsHttpServletRequestWrapper =
								(NestedFieldsHttpServletRequestWrapper)
									message.get("HTTP.REQUEST");

						return Pagination.of(
							GetterUtil.getInteger(
								nestedFieldsHttpServletRequestWrapper.
									getParameter("page"),
								1),
							GetterUtil.getInteger(
								nestedFieldsHttpServletRequestWrapper.
									getParameter("pageSize"),
								20));
					}

				};
			}
			else if (Objects.equals(
						contextType.getTypeName(),
						ThemeDisplay.class.getName())) {

				return (ContextProvider<T>)new ThemeDisplayContextProvider();
			}

			return null;
		}

		@Override
		public Configuration getConfiguration(Message message) {
			return null;
		}

		@Override
		protected void setProviders(
			boolean custom, boolean busGlobal, Object... providers) {
		}

		private MockProviderFactory() {
			super(Mockito.mock(Bus.class));
		}

	}

	private static class Product {

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

		public void setCategories(Category[] categories) {
			_categories = categories;
		}

		public void setExternalCode(String externalCode) {
			_externalCode = externalCode;
		}

		public void setId(Long id) {
			_id = id;
		}

		public void setProductOptions(ProductOption[] productOptions) {
			_productOptions = productOptions;
		}

		public void setSkus(Sku[] skus) {
			_skus = skus;
		}

		private Category[] _categories;
		private String _externalCode;
		private Long _id;
		private ProductOption[] _productOptions;
		private Sku[] _skus;

	}

	private static class ProductOption {

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

		public void setProductOptionValues(
			ProductOptionValue[] productOptionValues) {

			_productOptionValues = productOptionValues;
		}

		private Long _id;
		private String _name;
		private ProductOptionValue[] _productOptionValues;

	}

	private static class ProductOptionValue {

		public Long getId() {
			return _id;
		}

		public void setId(Long id) {
			_id = id;
		}

		private Long _id;

	}

	private static class ProductResource_v1_0_Impl
		extends BaseProductResource_v1_0_Impl {

		@NestedField("productOptions")
		@Override
		public List<ProductOption> getProductOptions(Long id, String name) {
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

		@NestedField("productOptionValues")
		public List<ProductOptionValue> getProductOptionValues(Long id) {
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

		@GET
		@Path("/products")
		@Produces("application/*")
		public List<Product> getProducts() {
			return Arrays.asList(
				_toProduct(null, 1, false), _toProduct(null, 2, false));
		}

		@NestedField("skus")
		@Override
		public Page<Sku> getSkus(Long id, Pagination pagination) {
			if (!Objects.equals(id, 1L)) {
				return Page.of(Collections.emptyList());
			}

			List<Sku> skus = Arrays.asList(
				_toSku(1L), _toSku(2L), _toSku(3L), _toSku(4L));

			skus = skus.subList(
				pagination.getStartPosition(),
				Math.min(pagination.getEndPosition(), skus.size()));

			return Page.of(skus);
		}

		public ThemeDisplay contextThemeDisplay;

		@Context
		public ThemeDisplay themeDisplay;

		@NestedField("categories")
		protected List<Category> getCategories(
			@NestedFieldId("externalCode") String externalCode) {

			if (!Objects.equals(externalCode, "externalCode")) {
				return Collections.emptyList();
			}

			return Arrays.asList(
				_toCategory(1L), _toCategory(2L), _toCategory(3L));
		}

		@NestedField("externalCode")
		protected String getExternalCodeByQueryParam(
			@QueryParam("AcceptLanguage") String acceptLanguage) {

			if (!Objects.equals(acceptLanguage, "es_ES")) {
				return "";
			}

			return "codigoExterno";
		}

	}

	private static class ProductResource_v2_0_Impl
		extends BaseProductResource_v2_0_Impl {

		@NestedField("categories")
		public List<Category> getCategories(
			@NestedFieldId("externalCode") String productExternalCode) {

			if (!Objects.equals(productExternalCode, "externalCode")) {
				return Collections.emptyList();
			}

			return Arrays.asList(_toCategory(1L), _toCategory(2L));
		}

		@NestedField("productOptions")
		@Override
		public List<ProductOption> getProductOptions(Long id, String name) {
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

		@GET
		@Path("/products")
		@Produces("application/*")
		public List<Product> getProducts() {
			return Arrays.asList(
				_toProduct(null, 1, false), _toProduct(null, 2, false));
		}

		@NestedField(parentClass = Product.class, value = "skus")
		@Override
		public Page<Sku> getSkus(Long id, Pagination pagination) {
			if (!Objects.equals(id, 1L)) {
				return Page.of(Collections.emptyList());
			}

			List<Sku> skus = Arrays.asList(
				_toSku(1L), _toSku(2L), _toSku(3L), _toSku(4L), _toSku(5L),
				_toSku(6L));

			skus = skus.subList(
				pagination.getStartPosition(),
				Math.min(pagination.getEndPosition(), skus.size()));

			return Page.of(skus);
		}

		@Context
		public ThemeDisplay themeDisplay;

		private ProductOption _toProductOption(long id, String name) {
			ProductOption productOption = new ProductOption();

			productOption.setId(id);
			productOption.setName(name);

			return productOption;
		}

	}

	private static class Sku {

		public Long getId() {
			return _id;
		}

		public void setId(Long id) {
			_id = id;
		}

		private Long _id;

	}

	private static class Subproduct extends Product {
	}

	private static class ThemeDisplayContextProvider
		implements ContextProvider<ThemeDisplay> {

		@Override
		public ThemeDisplay createContext(Message message) {
			return new ThemeDisplay();
		}

	}

	private class MockServiceObjects<S> implements ServiceObjects<S> {

		@Override
		public S getService() {
			return _service;
		}

		@Override
		public ServiceReference<S> getServiceReference() {
			return null;
		}

		@Override
		public void ungetService(S service) {
		}

		private MockServiceObjects(S service) {
			_service = service;
		}

		private final S _service;

	}

	private class MockServiceReference<S> implements ServiceReference<S> {

		@Override
		public int compareTo(Object reference) {
			return 0;
		}

		@Override
		public Bundle getBundle() {
			return null;
		}

		@Override
		public Object getProperty(String key) {
			return null;
		}

		@Override
		public String[] getPropertyKeys() {
			return new String[0];
		}

		@Override
		public Bundle[] getUsingBundles() {
			return new Bundle[0];
		}

		@Override
		public boolean isAssignableTo(Bundle bundle, String className) {
			return false;
		}

	}

	private interface ProductResource_v1_0 {

		public List<ProductOption> getProductOptions(Long id, String name);

		public List<ProductOptionValue> getProductOptionValues(Long id);

		public List<Product> getProducts();

		public Page<Sku> getSkus(Long id, Pagination pagination);

	}

	private interface ProductResource_v2_0 {

		public List<Category> getCategories(String externalCode);

		public List<ProductOption> getProductOptions(Long id, String name);

		public List<Product> getProducts();

		public Page<Sku> getSkus(Long id, Pagination pagination);

	}

}