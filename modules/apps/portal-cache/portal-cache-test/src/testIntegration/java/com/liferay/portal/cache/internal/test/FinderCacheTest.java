/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.model.TicketConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class FinderCacheTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPutResult() throws Exception {
		long classPK = RandomTestUtil.randomLong();

		List<Ticket> tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), User.class.getName(), classPK);

		Assert.assertTrue(tickets.toString(), tickets.isEmpty());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		Ticket ticket = _addTicket(classPK, serviceContext);

		Object[] finderArgs = _getFinderArgs(classPK);

		Assert.assertNull(
			_finderCache.getResult(_finderPath, finderArgs, null));

		_addTicket(classPK, serviceContext);

		_finderCache.putResult(
			_finderPath, finderArgs, Collections.singletonList(ticket));

		tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), User.class.getName(), classPK);

		Assert.assertEquals(tickets.toString(), 2, tickets.size());
	}

	@Test
	public void testPutResultForNewFinderPath() throws Throwable {
		long classPK = RandomTestUtil.randomLong();

		List<Ticket> tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), User.class.getName(), classPK);

		Assert.assertTrue(tickets.toString(), tickets.isEmpty());

		FinderPath finderPath = new FinderPath(
			"com.liferay.portal.model.impl.TicketImpl.List2",
			RandomTestUtil.randomString(),
			new String[] {
				Long.class.getName(), Long.class.getName(), Long.class.getName()
			},
			new String[] {"companyId", "classNameId", "classPK"}, true);

		Object[] finderArgs = _getFinderArgs(classPK);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		CountDownLatch addedCountDownLatch = new CountDownLatch(1);
		CountDownLatch putCountDownLatch = new CountDownLatch(1);

		FutureTask<Void> futureTask = new FutureTask<>(
			() -> {
				try {
					TransactionInvokerUtil.invoke(
						TransactionConfig.Factory.create(
							Propagation.REQUIRED,
							new Class<?>[] {Exception.class}),
						(Callable<Void>)() -> {
							_addTicket(classPK, serviceContext);

							addedCountDownLatch.countDown();

							putCountDownLatch.await();

							return null;
						});
				}
				catch (Throwable throwable) {
					throw new Exception(throwable);
				}
				finally {
					addedCountDownLatch.countDown();
				}

				return null;
			});

		Thread thread = new Thread(futureTask);

		thread.start();

		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setReadOnly(true);
		builder.setRollbackForClasses(Exception.class);

		try {
			TransactionInvokerUtil.invoke(
				builder.build(),
				(Callable<Void>)() -> {
					addedCountDownLatch.await();

					_finderCache.putResult(
						finderPath, finderArgs, Collections.emptyList());

					putCountDownLatch.countDown();

					futureTask.get();

					return null;
				});
		}
		finally {
			putCountDownLatch.countDown();

			thread.join();
		}

		Assert.assertNull(_finderCache.getResult(finderPath, finderArgs, null));
	}

	@Test
	public void testPutResultInReadOnlyTransaction() throws Throwable {
		long classPK = RandomTestUtil.randomLong();

		List<Ticket> tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), User.class.getName(), classPK);

		Assert.assertTrue(tickets.toString(), tickets.isEmpty());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		Ticket ticket = _addTicket(classPK, serviceContext);

		Object[] finderArgs = _getFinderArgs(classPK);

		TransactionConfig.Builder builder = new TransactionConfig.Builder();

		builder.setReadOnly(true);
		builder.setRollbackForClasses(Exception.class);

		TransactionInvokerUtil.invoke(
			builder.build(),
			(Callable<Void>)() -> {
				Assert.assertNull(
					_finderCache.getResult(_finderPath, finderArgs, null));

				try {
					TransactionInvokerUtil.invoke(
						TransactionConfig.Factory.create(
							Propagation.REQUIRES_NEW,
							new Class<?>[] {Exception.class}),
						(Callable<Ticket>)() -> _addTicket(
							classPK, serviceContext));
				}
				catch (Throwable throwable) {
					throw new Exception(throwable);
				}

				_finderCache.putResult(
					_finderPath, finderArgs, Collections.singletonList(ticket));

				return null;
			});

		tickets = _ticketLocalService.getTickets(
			TestPropsValues.getCompanyId(), User.class.getName(), classPK);

		Assert.assertEquals(tickets.toString(), 2, tickets.size());
	}

	private Ticket _addTicket(long classPK, ServiceContext serviceContext)
		throws Exception {

		Ticket ticket = _ticketLocalService.addTicket(
			TestPropsValues.getCompanyId(), User.class.getName(), classPK,
			TicketConstants.TYPE_PASSWORD, null, null, null, serviceContext);

		_tickets.add(ticket);

		return ticket;
	}

	private Object[] _getFinderArgs(long classPK) throws Exception {
		return new Object[] {
			TestPropsValues.getCompanyId(),
			_classNameLocalService.getClassNameId(User.class), classPK
		};
	}

	private static final FinderPath _finderPath = new FinderPath(
		"com.liferay.portal.model.impl.TicketImpl.List2", "findByC_C_C",
		new String[] {
			Long.class.getName(), Long.class.getName(), Long.class.getName()
		},
		new String[] {"companyId", "classNameId", "classPK"}, true);

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private TicketLocalService _ticketLocalService;

	@DeleteAfterTestRun
	private final List<Ticket> _tickets = new ArrayList<>();

}