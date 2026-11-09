/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.ORMException;
import com.liferay.portal.kernel.encryptor.CompanyKeyResolverUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.fips.FIPSApplicationStateMachineUtil;
import com.liferay.portal.kernel.service.VirtualHostLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.CompanyInfoPersistence;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.model.impl.CompanyImpl;
import com.liferay.portal.model.impl.CompanyInfoImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;
import java.util.concurrent.Callable;

import org.hibernate.StaleStateException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Raymond Augé
 */
public class CompanyLocalServiceImplTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		BundleContext bundleContext = Mockito.mock(BundleContext.class);

		_systemBundleUtilMockedStatic = Mockito.mockStatic(
			SystemBundleUtil.class);

		_systemBundleUtilMockedStatic.when(
			SystemBundleUtil::getBundleContext
		).thenReturn(
			bundleContext
		);

		VirtualHost virtualHost = Mockito.mock(VirtualHost.class);

		_virtualHostLocalServiceUtilMockedStatic = Mockito.mockStatic(
			VirtualHostLocalServiceUtil.class);

		_virtualHostLocalServiceUtilMockedStatic.when(
			() -> VirtualHostLocalServiceUtil.fetchCompanyDefaultVirtualHost(
				Mockito.anyLong())
		).thenReturn(
			virtualHost
		);
	}

	@After
	public void tearDown() {
		_systemBundleUtilMockedStatic.close();
		_virtualHostLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testCheckCompanyKey() throws Exception {
		Assert.assertNull(
			_testCheckCompanyKey(
				RandomTestUtil.randomString(),
				new ORMException(
					new StaleStateException(RandomTestUtil.randomString())),
				1));
		Assert.assertNull(
			_testCheckCompanyKey(
				_FIPS_KEY_PREFIX + RandomTestUtil.randomString() + "}",
				new ORMException(
					new StaleStateException(RandomTestUtil.randomString())),
				1));

		ORMException ormException = new ORMException(
			new StaleStateException(RandomTestUtil.randomString()));

		Throwable throwable = _testCheckCompanyKey(
			RandomTestUtil.randomString(), ormException, 0);

		Assert.assertSame(ormException, throwable.getCause());

		ormException = new ORMException();

		throwable = _testCheckCompanyKey(
			_FIPS_KEY_PREFIX + RandomTestUtil.randomString() + "}",
			ormException, 1);

		Assert.assertSame(ormException, throwable.getCause());
	}

	@Test
	public void testSyncVirtualHost() throws Exception {
		String domain = RandomTestUtil.randomString();

		Company company = new CompanyImpl();

		company.setWebId("liferay.com");

		CompanyLocalServiceImpl companyLocalServiceImpl =
			new CompanyLocalServiceImpl() {

				@Override
				public Company updateCompany(
						long companyId, String virtualHostname, String mx,
						int maxUsers, boolean active)
					throws PortalException {

					company.setCompanyId(companyId);
					company.setMx(mx);
					company.setMaxUsers(maxUsers);
					company.setActive(active);
					company.setVirtualHostname(virtualHostname);

					return company;
				}

			};

		try (SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_VIRTUAL_HOST_MAIL_DOMAIN", domain);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_VIRTUAL_HOST_NAME", domain);
			SafeCloseable safeCloseable3 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_VIRTUAL_HOST_SYNC_ON_STARTUP", true)) {

			Company checkedCompany = companyLocalServiceImpl.syncVirtualHost(
				company);

			Assert.assertEquals(domain, checkedCompany.getMx());
			Assert.assertEquals(domain, checkedCompany.getVirtualHostname());
		}
	}

	private Throwable _testCheckCompanyKey(
			String concurrentKey, ORMException ormException, int retries)
		throws Exception {

		CompanyLocalServiceImpl companyLocalServiceImpl =
			new CompanyLocalServiceImpl();

		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			company.getKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			company.getWebId()
		).thenReturn(
			PropsValues.COMPANY_DEFAULT_WEB_ID
		);

		CompanyPersistence companyPersistence = Mockito.mock(
			CompanyPersistence.class);

		Mockito.when(
			companyPersistence.findByPrimaryKey(_COMPANY_ID)
		).thenReturn(
			company
		);

		ReflectionTestUtil.setFieldValue(
			companyLocalServiceImpl, "companyPersistence", companyPersistence);

		CompanyInfoPersistence companyInfoPersistence = Mockito.mock(
			CompanyInfoPersistence.class);

		CompanyInfo companyInfo = new CompanyInfoImpl();

		companyInfo.setKey(RandomTestUtil.randomString());

		CompanyInfo concurrentCompanyInfo = new CompanyInfoImpl();

		concurrentCompanyInfo.setKey(concurrentKey);

		Mockito.when(
			companyInfoPersistence.fetchByCompanyId(_COMPANY_ID, false)
		).thenReturn(
			companyInfo, concurrentCompanyInfo
		);

		Mockito.when(
			companyInfoPersistence.update(Mockito.any(CompanyInfo.class))
		).thenThrow(
			ormException
		).thenAnswer(
			AdditionalAnswers.returnsFirstArg()
		);

		ReflectionTestUtil.setFieldValue(
			companyLocalServiceImpl, "_companyInfoPersistence",
			companyInfoPersistence);

		try (MockedStatic<CompanyKeyResolverUtil>
				companyKeyResolverUtilMockedStatic = Mockito.mockStatic(
					CompanyKeyResolverUtil.class, Mockito.CALLS_REAL_METHODS);
			MockedStatic<FIPSApplicationStateMachineUtil>
				fipsApplicationStateMachineUtilMockedStatic =
					Mockito.mockStatic(FIPSApplicationStateMachineUtil.class);
			MockedStatic<TransactionInvokerUtil>
				transactionInvokerUtilMockedStatic = Mockito.mockStatic(
					TransactionInvokerUtil.class);
			SafeCloseable safeCloseable1 =
				PropsValuesTestUtil.swapWithSafeCloseable("FIPS_ENABLED", true);
			SafeCloseable safeCloseable2 =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"RETRY_ADVICE_MAX_RETRIES", retries)) {

			companyKeyResolverUtilMockedStatic.when(
				() -> CompanyKeyResolverUtil.generateKey(_COMPANY_ID)
			).thenReturn(
				_FIPS_KEY_PREFIX + RandomTestUtil.randomString() + "}"
			);

			transactionInvokerUtilMockedStatic.when(
				() -> TransactionInvokerUtil.invoke(
					Mockito.any(TransactionConfig.class),
					Mockito.any(Callable.class))
			).thenAnswer(
				invocation -> {
					Callable<?> callable = invocation.getArgument(1);

					return callable.call();
				}
			);

			companyLocalServiceImpl.checkCompanyKey(_COMPANY_ID);

			ArgumentCaptor<Throwable> argumentCaptor = ArgumentCaptor.forClass(
				Throwable.class);

			fipsApplicationStateMachineUtilMockedStatic.verify(
				() -> FIPSApplicationStateMachineUtil.error(
					Mockito.anyString(), argumentCaptor.capture()),
				Mockito.atMost(1));

			List<Throwable> throwables = argumentCaptor.getAllValues();

			if (throwables.isEmpty()) {
				return null;
			}

			return throwables.get(0);
		}
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final String _FIPS_KEY_PREFIX = "${fipsKey:";

	private MockedStatic<SystemBundleUtil> _systemBundleUtilMockedStatic;
	private MockedStatic<VirtualHostLocalServiceUtil>
		_virtualHostLocalServiceUtilMockedStatic;

}