/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionIdSupplier;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.GroupThreadLocal;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.TimeZoneThreadLocal;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Alberto Chaparro
 */
public class CompanyThreadLocalTest {

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		bundleContext.registerService(
			CTCollectionIdSupplier.class,
			ProxyFactory.newDummyInstance(CTCollectionIdSupplier.class), null);
	}

	@Test
	public void testLock() {
		_testLock(CompanyThreadLocal::setCompanyId);
	}

	@Test
	public void testLockWithSetWithSafeCloseable() {
		_testLock(CompanyThreadLocal::setCompanyIdWithSafeCloseable);
	}

	@Test
	public void testSetCompanyId() {
		GroupThreadLocal.setGroupId(1L);

		LocaleThreadLocal.setDefaultLocale(LocaleUtil.GERMAN);

		PasswordModificationThreadLocal.setPasswordUnencrypted("passwordTest");

		PrincipalThreadLocal.setName("userTest");
		PrincipalThreadLocal.setPassword("passwordTest");

		ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		TimeZone pstTimeZone = TimeZone.getTimeZone("PST");

		TimeZoneThreadLocal.setDefaultTimeZone(pstTimeZone);

		_originalCompanyId = CompanyThreadLocal.getCompanyId();

		try {
			CompanyThreadLocal.setCompanyId(1L);

			for (CompanyCentralizedThreadLocal<?>
					companyCentralizedThreadLocal :
						CompanyCentralizedThreadLocal.
							getCompanyCentralizedThreadLocals()) {

				Object initialValue = ReflectionTestUtil.invoke(
					companyCentralizedThreadLocal, "initialValue",
					new Class<?>[0]);

				Assert.assertEquals(
					initialValue, companyCentralizedThreadLocal.get());
			}
		}
		finally {
			CompanyThreadLocal.setCompanyId(_originalCompanyId);
		}
	}

	@Test
	public void testSetCompanyIdWithSafeCloseable() {
		GroupThreadLocal.setGroupId(1L);

		LocaleThreadLocal.setDefaultLocale(LocaleUtil.GERMAN);

		PasswordModificationThreadLocal.setPasswordUnencrypted("passwordTest");

		PrincipalThreadLocal.setName("userTest");
		PrincipalThreadLocal.setPassword("passwordTest");

		ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		TimeZone pstTimeZone = TimeZone.getTimeZone("PST");

		TimeZoneThreadLocal.setDefaultTimeZone(pstTimeZone);

		Map<CompanyCentralizedThreadLocal<?>, Object>
			companyCentralizedThreadLocalMap = new HashMap<>();

		for (CompanyCentralizedThreadLocal<?> companyCentralizedThreadLocal :
				CompanyCentralizedThreadLocal.
					getCompanyCentralizedThreadLocals()) {

			companyCentralizedThreadLocalMap.put(
				companyCentralizedThreadLocal,
				companyCentralizedThreadLocal.get());
		}

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(1L)) {

			for (CompanyCentralizedThreadLocal<?>
					companyCentralizedThreadLocal :
						CompanyCentralizedThreadLocal.
							getCompanyCentralizedThreadLocals()) {

				Object initialValue = ReflectionTestUtil.invoke(
					companyCentralizedThreadLocal, "initialValue",
					new Class<?>[0]);

				Assert.assertEquals(
					initialValue, companyCentralizedThreadLocal.get());
			}
		}

		for (CompanyCentralizedThreadLocal<?> companyCentralizedThreadLocal :
				CompanyCentralizedThreadLocal.
					getCompanyCentralizedThreadLocals()) {

			Assert.assertEquals(
				companyCentralizedThreadLocalMap.get(
					companyCentralizedThreadLocal),
				companyCentralizedThreadLocal.get());
		}
	}

	private void _testLock(Consumer<Long> consumer) {
		try (SafeCloseable safeCloseable = CompanyThreadLocal.lock(
				CompanyConstants.SYSTEM)) {

			consumer.accept(1L);

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertNotNull(unsupportedOperationException);
		}
	}

	private long _originalCompanyId;

}