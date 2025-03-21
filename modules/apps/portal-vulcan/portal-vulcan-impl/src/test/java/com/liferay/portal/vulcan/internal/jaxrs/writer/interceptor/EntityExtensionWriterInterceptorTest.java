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
import com.liferay.portal.vulcan.extension.EntityExtensionThreadLocal;
import com.liferay.portal.vulcan.internal.jaxrs.context.resolver.EntityExtensionHandlerContextResolver;
import com.liferay.portal.vulcan.jaxrs.extension.ExtendedEntity;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import java.util.Collections;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Javier de Arcos
 */
public class EntityExtensionWriterInterceptorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		EntityExtensionThreadLocal.setExtendedProperties(null);

		ReflectionTestUtil.setFieldValue(
			_entityExtensionWriterInterceptor, "_company", _company);
		ReflectionTestUtil.setFieldValue(
			_entityExtensionWriterInterceptor, "_providers", _providers);
		ReflectionTestUtil.setFieldValue(
			_entityExtensionWriterInterceptor, "_user", _user);
	}

	@Test
	public void testAroundWrite() throws Exception {
		EntityExtensionThreadLocal.setExtendedProperties(
			Collections.singletonMap(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		Mockito.when(
			_company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
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
			_TEST_ENTITY
		);

		Mockito.when(
			_writerInterceptorContext.getMediaType()
		).thenReturn(
			MediaType.APPLICATION_JSON_TYPE
		);

		Mockito.when(
			_writerInterceptorContext.getType()
		).thenReturn(
			(Class)TestEntity.class
		);

		_entityExtensionWriterInterceptor.aroundWriteTo(
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
			Mockito.any(ExtendedEntity.class)
		);

		Mockito.verify(
			_writerInterceptorContext
		).setGenericType(
			ExtendedEntity.class
		);
	}

	@Test
	public void testAroundWriteWithNoEntityExtensionHandler() throws Exception {
		Mockito.when(
			_writerInterceptorContext.getMediaType()
		).thenReturn(
			MediaType.APPLICATION_JSON_TYPE
		);

		Mockito.when(
			_writerInterceptorContext.getType()
		).thenReturn(
			(Class)TestEntity.class
		);

		_entityExtensionWriterInterceptor.aroundWriteTo(
			_writerInterceptorContext);

		Mockito.verify(
			_writerInterceptorContext
		).proceed();

		Mockito.verify(
			_writerInterceptorContext, Mockito.never()
		).setEntity(
			Mockito.any()
		);

		Mockito.verify(
			_writerInterceptorContext, Mockito.never()
		).setGenericType(
			Mockito.any()
		);
	}

	@Test
	public void testAroundWriteWithNoExtendedProperties() throws Exception {
		Mockito.when(
			_company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			_entityExtensionHandlerContextResolver.getContext(Mockito.any())
		).thenReturn(
			_entityExtensionHandler
		);

		Mockito.when(
			_user.getUserId()
		).thenReturn(
			_USER_ID
		);

		Mockito.when(
			_providers.getContextResolver(
				Mockito.eq(EntityExtensionHandler.class),
				Mockito.any(MediaType.class))
		).thenReturn(
			_entityExtensionHandlerContextResolver
		);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			_TEST_ENTITY
		);

		Mockito.when(
			_writerInterceptorContext.getMediaType()
		).thenReturn(
			MediaType.APPLICATION_JSON_TYPE
		);

		Mockito.when(
			_writerInterceptorContext.getType()
		).thenReturn(
			(Class)TestEntity.class
		);

		_entityExtensionWriterInterceptor.aroundWriteTo(
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
		).proceed();

		Mockito.verify(
			_writerInterceptorContext
		).setEntity(
			Mockito.any(ExtendedEntity.class)
		);

		Mockito.verify(
			_writerInterceptorContext
		).setGenericType(
			ExtendedEntity.class
		);
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
	private final EntityExtensionWriterInterceptor
		_entityExtensionWriterInterceptor =
			new EntityExtensionWriterInterceptor();
	private final Providers _providers = Mockito.mock(Providers.class);
	private final User _user = Mockito.mock(User.class);
	private final WriterInterceptorContext _writerInterceptorContext =
		Mockito.mock(WriterInterceptorContext.class);

	private static final class TestEntity {
	}

}