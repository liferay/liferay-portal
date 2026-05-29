/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.request.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.ai.hub.agent.SupervisorAgent;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

import java.io.Serializable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@FeatureFlag("LPD-62272")
@RunWith(Arquillian.class)
public class RequestUtilTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class<?> clazz = _supervisorAgent.getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		Class<?> requestUtilClass = classLoader.loadClass(
			"com.liferay.ai.hub.internal.request.RequestUtil");

		_acquireMethod = requestUtilClass.getMethod(
			"acquire", long.class, long.class, long.class);
		_releaseMethod = requestUtilClass.getMethod("release", Lock.class);

		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_originalName = PrincipalThreadLocal.getName();

		PrincipalThreadLocal.setName(TestPropsValues.getUserId());

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		SiteInitializer siteInitializer =
			_siteInitializerRegistry.getSiteInitializer(
				"com.liferay.ai.hub.site.initializer");

		siteInitializer.initialize(TestPropsValues.getGroupId());
	}

	@AfterClass
	public static void tearDownClass() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
		ServiceContextThreadLocal.popServiceContext();
	}

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();

		_accountEntry = _addAccountEntry(_user);

		_maxRequests = RandomTestUtil.randomInt(2, 20);

		_objectEntry = _addObjectEntry(_accountEntry, _maxRequests);
	}

	@After
	public void tearDown() throws Exception {
		for (Lock lock : _locks) {
			_release(lock);
		}
	}

	@Test
	public void testAcquireAndRelease() throws Exception {
		for (int i = 0; i < _maxRequests; i++) {
			Lock lock = _acquire(
				TestPropsValues.getCompanyId(), Time.MINUTE, _user.getUserId());

			Assert.assertNotNull(lock);

			_locks.add(lock);
		}

		_assertLocks();

		try {
			_acquire(
				TestPropsValues.getCompanyId(), Time.MINUTE, _user.getUserId());

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
			Assert.assertEquals(
				"You have exceeded your concurrent request limit",
				unsupportedOperationException.getMessage());
		}

		Lock lock = _locks.remove(0);

		_release(lock);

		Lock newLock = _acquire(
			TestPropsValues.getCompanyId(), Time.MINUTE, _user.getUserId());

		_locks.add(newLock);

		Assert.assertEquals(lock.getKey(), newLock.getKey());
		Assert.assertNotEquals(lock.getOwner(), newLock.getOwner());
	}

	@Test
	public void testAcquireWithConcurrentRequests() throws Exception {
		int threadCount = _maxRequests + RandomTestUtil.randomInt(1, 10);

		List<ExecutionException> executionExceptions = new ArrayList<>();

		CountDownLatch countDownLatch = new CountDownLatch(1);

		ExecutorService executorService = Executors.newFixedThreadPool(
			threadCount);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SqlExceptionHelper.class.getName(), LoggerTestUtil.OFF)) {

			List<Future<Lock>> futures = new ArrayList<>();

			for (int i = 0; i < threadCount; i++) {
				futures.add(
					executorService.submit(
						() -> {
							countDownLatch.await();

							return _acquire(
								TestPropsValues.getCompanyId(), Time.MINUTE,
								_user.getUserId());
						}));
			}

			countDownLatch.countDown();

			for (Future<Lock> future : futures) {
				try {
					_locks.add(future.get());
				}
				catch (ExecutionException executionException) {
					executionExceptions.add(executionException);
				}
			}
		}
		finally {
			executorService.shutdown();
		}

		_assertLocks();

		Assert.assertEquals(
			executionExceptions.toString(), threadCount - _maxRequests,
			executionExceptions.size());
	}

	private Lock _acquire(long companyId, long timeout, long userId)
		throws Exception {

		try {
			return (Lock)_acquireMethod.invoke(
				null, companyId, timeout, userId);
		}
		catch (InvocationTargetException invocationTargetException) {
			Throwable throwable = invocationTargetException.getCause();

			if (throwable instanceof Exception) {
				throw (Exception)throwable;
			}

			throw invocationTargetException;
		}
	}

	private AccountEntry _addAccountEntry(User user) throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			null, TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), null, null, null, null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));

		AccountEntry aiHubAccountEntry =
			_accountEntryLocalService.getAccountEntryByExternalReferenceCode(
				"L_AI_HUB", TestPropsValues.getCompanyId());

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			aiHubAccountEntry.getAccountEntryId(),
			new long[] {user.getUserId()});

		_accountEntryUserRelLocalService.addAccountEntryUserRels(
			accountEntry.getAccountEntryId(), new long[] {user.getUserId()});

		return accountEntry;
	}

	private ObjectEntry _addObjectEntry(
			AccountEntry accountEntry, int maxRequests)
		throws Exception {

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_REQUEST", TestPropsValues.getCompanyId());

		return _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), 0,
			LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
			HashMapBuilder.<String, Serializable>put(
				"externalReferenceCode",
				"request-" + accountEntry.getAccountEntryId()
			).put(
				"maxRequests", maxRequests
			).put(
				"r_accountToAIHubRequests_accountEntryId",
				accountEntry.getAccountEntryId()
			).build(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertLocks() {
		Assert.assertEquals(_locks.toString(), _maxRequests, _locks.size());

		String prefix =
			_objectEntry.getExternalReferenceCode() + StringPool.COLON;

		String[] expectedKeys = new String[_maxRequests];

		for (int i = 0; i < _maxRequests; i++) {
			expectedKeys[i] = prefix + i;
		}

		Arrays.sort(expectedKeys);

		String[] keys = new String[_locks.size()];

		for (int i = 0; i < keys.length; i++) {
			Lock lock = _locks.get(i);

			keys[i] = lock.getKey();
		}

		Arrays.sort(keys);

		Assert.assertArrayEquals(expectedKeys, keys);
	}

	private void _release(Lock lock) throws Exception {
		_releaseMethod.invoke(null, lock);
	}

	private static Method _acquireMethod;
	private static String _originalName;
	private static PermissionChecker _originalPermissionChecker;
	private static Method _releaseMethod;

	@Inject
	private static SiteInitializerRegistry _siteInitializerRegistry;

	@Inject
	private static SupervisorAgent _supervisorAgent;

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private final List<Lock> _locks = new ArrayList<>();
	private int _maxRequests;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@DeleteAfterTestRun
	private User _user;

}