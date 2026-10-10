/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.store.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.store.DLStoreRequest;
import com.liferay.document.library.kernel.store.DLStoreUtil;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
@Sync(cleanTransaction = true)
public class DLStoreTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeleteDirectory() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			long repositoryId = RandomTestUtil.nextLong();
			String fileName = RandomTestUtil.randomString();

			DLStoreUtil.addFile(
				DLStoreRequest.builder(
					TestPropsValues.getCompanyId(), repositoryId, fileName
				).build(),
				new byte[0]);

			Exception exception = new Exception();

			Assert.assertSame(
				exception,
				Assert.assertThrows(
					Exception.class,
					() -> TransactionInvokerUtil.invoke(
						TransactionConfig.Factory.create(
							Propagation.REQUIRED,
							new Class<?>[] {Exception.class}),
						() -> {
							DLStoreUtil.deleteDirectory(
								TestPropsValues.getCompanyId(), repositoryId,
								StringPool.BLANK);

							Assert.assertFalse(
								DLStoreUtil.hasFile(
									TestPropsValues.getCompanyId(),
									repositoryId, fileName,
									Store.VERSION_DEFAULT));

							throw exception;
						})));

			Assert.assertTrue(
				DLStoreUtil.hasFile(
					TestPropsValues.getCompanyId(), repositoryId, fileName,
					Store.VERSION_DEFAULT));
		}
	}

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

}