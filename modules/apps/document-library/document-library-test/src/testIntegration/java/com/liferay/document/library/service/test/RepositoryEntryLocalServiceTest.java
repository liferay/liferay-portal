/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.kernel.security.auth.CompanyInheritableThreadLocalCallable;
import com.liferay.portal.kernel.service.RepositoryEntryLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.concurrent.FutureTask;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class RepositoryEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-115672")
	public void testGetRepositoryEntryWhenAddedConcurrently() throws Exception {
		long groupId = TestPropsValues.getGroupId();
		String mappedId = RandomTestUtil.randomString();
		long repositoryId = RandomTestUtil.randomLong();
		long userId = TestPropsValues.getUserId();

		ConcurrentAddModelListener concurrentAddModelListener =
			new ConcurrentAddModelListener(groupId, repositoryId, userId);

		Bundle bundle = FrameworkUtil.getBundle(
			RepositoryEntryLocalServiceTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<?> serviceRegistration =
			bundleContext.registerService(
				ModelListener.class, concurrentAddModelListener, null);

		try {
			RepositoryEntry repositoryEntry =
				_repositoryEntryLocalService.getRepositoryEntry(
					userId, groupId, repositoryId, mappedId);

			RepositoryEntry concurrentRepositoryEntry =
				concurrentAddModelListener._repositoryEntry;

			Assert.assertEquals(
				concurrentRepositoryEntry.getRepositoryEntryId(),
				repositoryEntry.getRepositoryEntryId());
		}
		finally {
			serviceRegistration.unregister();

			_repositoryEntryLocalService.deleteRepositoryEntries(
				repositoryId, List.of(mappedId));
		}
	}

	@Inject
	private RepositoryEntryLocalService _repositoryEntryLocalService;

	private class ConcurrentAddModelListener
		extends BaseModelListener<RepositoryEntry> {

		@Override
		public void onBeforeCreate(RepositoryEntry repositoryEntry)
			throws ModelListenerException {

			if ((_repositoryEntry != null) ||
				(repositoryEntry.getRepositoryId() != _repositoryId)) {

				return;
			}

			_repositoryEntry = repositoryEntry;

			FutureTask<RepositoryEntry> futureTask = new FutureTask<>(
				new CompanyInheritableThreadLocalCallable<>(
					() -> _repositoryEntryLocalService.addRepositoryEntry(
						_userId, _groupId, _repositoryId,
						repositoryEntry.getMappedId(), new ServiceContext())));

			Thread thread = new Thread(futureTask);

			thread.start();

			try {
				_repositoryEntry = futureTask.get();
			}
			catch (Exception exception) {
				throw new ModelListenerException(exception);
			}
		}

		private ConcurrentAddModelListener(
			long groupId, long repositoryId, long userId) {

			_groupId = groupId;
			_repositoryId = repositoryId;
			_userId = userId;
		}

		private final long _groupId;
		private RepositoryEntry _repositoryEntry;
		private final long _repositoryId;
		private final long _userId;

	}

}