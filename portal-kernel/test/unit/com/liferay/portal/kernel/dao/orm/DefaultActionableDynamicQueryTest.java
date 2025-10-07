/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mariano Álvaro Sáiz
 */
public class DefaultActionableDynamicQueryTest {

	@BeforeClass
	public static void setUpClass() {
		new DynamicQueryFactoryUtil(
		).setDynamicQueryFactory(
			Mockito.mock(DynamicQueryFactory.class, Mockito.RETURNS_MOCKS)
		);

		new OrderFactoryUtil(
		).setOrderFactory(
			Mockito.mock(OrderFactory.class, Mockito.RETURNS_MOCKS)
		);

		new PropertyFactoryUtil(
		).setPropertyFactory(
			Mockito.mock(PropertyFactory.class, Mockito.RETURNS_MOCKS)
		);

		_portalExecutorManagerSnapshot = ReflectionTestUtil.getAndSetFieldValue(
			DefaultActionableDynamicQuery.class,
			"_portalExecutorManagerSnapshot",
			new Snapshot<PortalExecutorManager>(
				DefaultActionableDynamicQuery.class,
				PortalExecutorManager.class) {

				@Override
				public PortalExecutorManager get() {
					return Mockito.mock(PortalExecutorManager.class);
				}

			});
	}

	@AfterClass
	public static void tearDownClass() {
		new DynamicQueryFactoryUtil(
		).setDynamicQueryFactory(
			_dynamicQueryFactory
		);

		new OrderFactoryUtil(
		).setOrderFactory(
			_orderFactory
		);

		new PropertyFactoryUtil(
		).setPropertyFactory(
			_propertyFactory
		);

		ReflectionTestUtil.setFieldValue(
			DefaultActionableDynamicQuery.class,
			"_portalExecutorManagerSnapshot", _portalExecutorManagerSnapshot);
	}

	@Test
	public void testSetCompanyIdInParallelExecution() throws Exception {
		ClassNameLocalService classNameLocalService = Mockito.mock(
			ClassNameLocalService.class);

		Mockito.when(
			classNameLocalService.dynamicQuery(Mockito.any())
		).thenReturn(
			Collections.singletonList(new Object())
		);

		long expectedCompanyId = 1L;

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					expectedCompanyId)) {

			DefaultActionableDynamicQuery defaultActionableDynamicQuery =
				new DefaultActionableDynamicQuery() {
					{
						setBaseLocalService(classNameLocalService);
						setClassLoader(
							DefaultActionableDynamicQueryTest.class.
								getClassLoader());
						setModelClass(ClassName.class);
					}
				};

			defaultActionableDynamicQuery.setPerformActionMethod(
				Void -> _companyId = CompanyThreadLocal.getCompanyId());

			defaultActionableDynamicQuery.setParallel(true);

			defaultActionableDynamicQuery.performActions();
		}

		Assert.assertEquals(expectedCompanyId, _companyId);
	}

	private static DynamicQueryFactory _dynamicQueryFactory;
	private static OrderFactory _orderFactory;
	private static Snapshot<PortalExecutorManager>
		_portalExecutorManagerSnapshot;
	private static PropertyFactory _propertyFactory;

	private long _companyId;

}