/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.extension.EntityExtensionHandler;
import com.liferay.portal.vulcan.internal.jaxrs.context.resolver.EntityExtensionHandlerContextResolver;
import com.liferay.portal.vulcan.jaxrs.extension.ExtendedEntity;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.io.Serializable;

import java.lang.reflect.Type;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Javier de Arcos
 */
public class PageEntityExtensionWriterInterceptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_pageEntityExtensionWriterInterceptor =
			new PageEntityExtensionWriterInterceptor();

		ReflectionTestUtil.setFieldValue(
			_pageEntityExtensionWriterInterceptor, "_company", _company);
		ReflectionTestUtil.setFieldValue(
			_pageEntityExtensionWriterInterceptor, "_providers", _providers);
		ReflectionTestUtil.setFieldValue(
			_pageEntityExtensionWriterInterceptor, "_user", _user);
	}

	@Test
	public void testAroundWrite() throws Exception {
		ArgumentCaptor<Page<ExtendedEntity>> argumentCaptor =
			ArgumentCaptor.forClass(
				(Class<Page<ExtendedEntity>>)(Class<?>)Page.class);
		Map<String, Serializable> extendedProperties = Collections.singletonMap(
			"test", "test");
		Page<TestEntity> page = Page.of(Collections.singleton(_TEST_ENTITY));

		Mockito.when(
			_company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_entityExtensionHandler.getExtendedProperties(
				Mockito.anyLong(), Mockito.anyLong(), Mockito.any())
		).thenReturn(
			extendedProperties
		);

		Mockito.when(
			_entityExtensionHandlerContextResolver.getContext(Mockito.any())
		).thenReturn(
			_entityExtensionHandler
		);

		Mockito.when(
			_providers.getContextResolver(
				Mockito.eq(EntityExtensionHandler.class),
				Mockito.any(MediaType.class))
		).thenReturn(
			_entityExtensionHandlerContextResolver
		);

		Mockito.when(
			_user.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			Page.of(Collections.singleton(_TEST_ENTITY))
		);

		Mockito.when(
			_writerInterceptorContext.getGenericType()
		).thenReturn(
			new GenericType<Page<TestEntity>>() {
			}.getType()
		);

		Mockito.when(
			_writerInterceptorContext.getMediaType()
		).thenReturn(
			MediaType.APPLICATION_JSON_TYPE
		);

		Mockito.when(
			_writerInterceptorContext.getType()
		).thenReturn(
			(Class)Page.class
		);

		_pageEntityExtensionWriterInterceptor.aroundWriteTo(
			_writerInterceptorContext);

		Mockito.verify(
			_entityExtensionHandler
		).getExtendedProperties(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_USER_ID),
			Mockito.eq(_TEST_ENTITY)
		);

		Mockito.verify(
			_entityExtensionHandler
		).getFilteredPropertyNames(
			Mockito.eq(_COMPANY_ID), Mockito.eq(_TEST_ENTITY)
		);

		Mockito.verify(
			_entityExtensionHandlerContextResolver
		).getContext(
			Mockito.eq(TestEntity.class)
		);

		Mockito.verify(
			_providers
		).getContextResolver(
			Mockito.eq(EntityExtensionHandler.class),
			Mockito.eq(MediaType.APPLICATION_JSON_TYPE)
		);

		Mockito.verify(
			_writerInterceptorContext
		).setEntity(
			argumentCaptor.capture()
		);

		Page<ExtendedEntity> extendedEntityPage = argumentCaptor.getValue();

		Assert.assertEquals(page.getActions(), extendedEntityPage.getActions());

		Collection<ExtendedEntity> extendedEntities =
			extendedEntityPage.getItems();

		ExtendedEntity extendedEntity =
			extendedEntities.toArray(new ExtendedEntity[0])[0];

		Assert.assertEquals(_TEST_ENTITY, extendedEntity.getEntity());
		Assert.assertEquals(
			extendedProperties, extendedEntity.getExtendedProperties());

		Assert.assertEquals(page.getLastPage(), page.getLastPage());
		Assert.assertEquals(page.getPage(), extendedEntityPage.getPage());
		Assert.assertEquals(
			page.getPageSize(), extendedEntityPage.getPageSize());
		Assert.assertEquals(page.getTotalCount(), page.getTotalCount());

		Mockito.verify(
			_writerInterceptorContext
		).setGenericType(
			Mockito.eq(
				new GenericType<Page<ExtendedEntity>>() {
				}.getType())
		);

		Mockito.verify(
			_writerInterceptorContext
		).proceed();
	}

	@Test
	public void testAroundWriteWithNoEntityExtensionHandler() throws Exception {
		Mockito.when(
			_writerInterceptorContext.getGenericType()
		).thenReturn(
			new GenericType<Page<TestEntity>>() {
			}.getType()
		);

		Mockito.when(
			_writerInterceptorContext.getMediaType()
		).thenReturn(
			MediaType.APPLICATION_JSON_TYPE
		);

		Mockito.when(
			_writerInterceptorContext.getType()
		).thenReturn(
			(Class)Page.class
		);

		_pageEntityExtensionWriterInterceptor.aroundWriteTo(
			_writerInterceptorContext);

		Mockito.verify(
			_providers
		).getContextResolver(
			Mockito.eq(EntityExtensionHandler.class),
			Mockito.eq(MediaType.APPLICATION_JSON_TYPE)
		);

		Mockito.verify(
			_writerInterceptorContext, Mockito.never()
		).setEntity(
			Mockito.any()
		);

		Mockito.verify(
			_writerInterceptorContext, Mockito.never()
		).setGenericType(
			Mockito.any(Type.class)
		);

		Mockito.verify(
			_writerInterceptorContext
		).proceed();
	}

	@Test
	public void testAroundWriteWithNoPageType() throws Exception {
		Mockito.when(
			_writerInterceptorContext.getType()
		).thenReturn(
			(Class)TestEntity.class
		);

		_pageEntityExtensionWriterInterceptor.aroundWriteTo(
			_writerInterceptorContext);

		Mockito.verifyNoInteractions(_providers);

		Mockito.verify(
			_writerInterceptorContext, Mockito.never()
		).setEntity(
			Mockito.any()
		);

		Mockito.verify(
			_writerInterceptorContext, Mockito.never()
		).setGenericType(
			Mockito.any(Type.class)
		);

		Mockito.verify(
			_writerInterceptorContext
		).proceed();
	}

	private static final long _COMPANY_ID = 11111;

	private static final TestEntity _TEST_ENTITY = new TestEntity();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private final Company _company = Mockito.mock(Company.class);
	private final EntityExtensionHandler _entityExtensionHandler = Mockito.mock(
		EntityExtensionHandler.class);
	private final EntityExtensionHandlerContextResolver
		_entityExtensionHandlerContextResolver = Mockito.mock(
			EntityExtensionHandlerContextResolver.class);
	private PageEntityExtensionWriterInterceptor
		_pageEntityExtensionWriterInterceptor;
	private final Providers _providers = Mockito.mock(Providers.class);
	private final User _user = Mockito.mock(User.class);
	private final WriterInterceptorContext _writerInterceptorContext =
		Mockito.mock(WriterInterceptorContext.class);

	private static final class TestEntity {
	}

}