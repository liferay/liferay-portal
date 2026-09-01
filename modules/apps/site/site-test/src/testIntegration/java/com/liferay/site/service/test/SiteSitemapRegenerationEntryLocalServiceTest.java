/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.model.SiteSitemapRegenerationEntry;
import com.liferay.site.service.SiteSitemapRegenerationEntryLocalService;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
@Sync
public class SiteSitemapRegenerationEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		_siteSitemapRegenerationEntryLocalService.
			deleteSiteSitemapRegenerationEntries(
				TestPropsValues.getCompanyId());
	}

	@Test
	public void testAddSiteSitemapRegenerationEntryDeduplicatesInTransaction()
		throws Throwable {

		String assetTypeKey = RandomTestUtil.randomString();
		long companyId = TestPropsValues.getCompanyId();
		long groupId = RandomTestUtil.randomLong();

		TransactionInvokerUtil.invoke(
			_transactionConfig,
			() -> {
				_siteSitemapRegenerationEntryLocalService.
					addSiteSitemapRegenerationEntry(
						assetTypeKey, companyId, groupId);
				_siteSitemapRegenerationEntryLocalService.
					addSiteSitemapRegenerationEntry(
						assetTypeKey, companyId, groupId);

				return null;
			});

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_getSiteSitemapRegenerationEntries(companyId);

		Assert.assertEquals(
			siteSitemapRegenerationEntries.toString(), 1,
			siteSitemapRegenerationEntries.size());

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry =
			siteSitemapRegenerationEntries.get(0);

		Assert.assertEquals(
			assetTypeKey, siteSitemapRegenerationEntry.getAssetTypeKey());
		Assert.assertEquals(groupId, siteSitemapRegenerationEntry.getGroupId());
	}

	@Test
	public void testAddSiteSitemapRegenerationEntryDiscardedOnRollback()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();

		try {
			TransactionInvokerUtil.invoke(
				_transactionConfig,
				() -> {
					_siteSitemapRegenerationEntryLocalService.
						addSiteSitemapRegenerationEntry(
							RandomTestUtil.randomString(), companyId,
							RandomTestUtil.randomLong());

					throw new IllegalStateException();
				});

			Assert.fail();
		}
		catch (Throwable throwable) {
			Assert.assertTrue(
				throwable.toString(),
				throwable instanceof IllegalStateException);
		}

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_getSiteSitemapRegenerationEntries(companyId);

		Assert.assertTrue(
			siteSitemapRegenerationEntries.toString(),
			siteSitemapRegenerationEntries.isEmpty());
	}

	@Test
	public void testAddSiteSitemapRegenerationEntryWithDistinctKeys()
		throws Exception {

		long companyId = TestPropsValues.getCompanyId();
		long groupId = RandomTestUtil.randomLong();

		String assetTypeKey1 = RandomTestUtil.randomString();
		String assetTypeKey2 = RandomTestUtil.randomString();

		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(assetTypeKey1, companyId, groupId);
		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(assetTypeKey2, companyId, groupId);

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_getSiteSitemapRegenerationEntries(companyId);

		Assert.assertEquals(
			siteSitemapRegenerationEntries.toString(), 2,
			siteSitemapRegenerationEntries.size());
	}

	@Test
	public void testCompanyModelListenerDeletesEntries() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(
				RandomTestUtil.randomString(), companyId,
				RandomTestUtil.randomLong());

		_companyModelListener.onBeforeRemove(
			_companyLocalService.getCompany(companyId));

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_getSiteSitemapRegenerationEntries(companyId);

		Assert.assertTrue(
			siteSitemapRegenerationEntries.toString(),
			siteSitemapRegenerationEntries.isEmpty());
	}

	@Test
	public void testSchedulerJobConfigurationDrainsEntries() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry1 =
			_siteSitemapRegenerationEntryLocalService.
				addSiteSitemapRegenerationEntry(
					RandomTestUtil.randomString(), companyId,
					RandomTestUtil.randomLong());

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry2 =
			_siteSitemapRegenerationEntryLocalService.
				addSiteSitemapRegenerationEntry(
					RandomTestUtil.randomString(), companyId,
					RandomTestUtil.randomLong());

		UnsafeConsumer<Long, Exception> unsafeConsumer =
			_schedulerJobConfiguration.getCompanyJobExecutorUnsafeConsumer();

		unsafeConsumer.accept(companyId);

		Assert.assertNull(
			_siteSitemapRegenerationEntryLocalService.
				fetchSiteSitemapRegenerationEntry(
					siteSitemapRegenerationEntry1.
						getSiteSitemapRegenerationEntryId()));
		Assert.assertNull(
			_siteSitemapRegenerationEntryLocalService.
				fetchSiteSitemapRegenerationEntry(
					siteSitemapRegenerationEntry2.
						getSiteSitemapRegenerationEntryId()));
	}

	private List<SiteSitemapRegenerationEntry>
		_getSiteSitemapRegenerationEntries(long companyId) {

		return _siteSitemapRegenerationEntryLocalService.
			getSiteSitemapRegenerationEntries(companyId);
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.internal.model.listener.CompanyModelListener"
	)
	private ModelListener<Company> _companyModelListener;

	@Inject(
		filter = "component.name=com.liferay.site.internal.scheduler.XMLSitemapRegenerationSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

	@Inject
	private SiteSitemapRegenerationEntryLocalService
		_siteSitemapRegenerationEntryLocalService;

}