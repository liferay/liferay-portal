/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
	public void testAddSiteSitemapRegenerationEntry() throws Throwable {
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
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntries(companyId);

		Assert.assertEquals(
			siteSitemapRegenerationEntries.toString(), 1,
			siteSitemapRegenerationEntries.size());

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry =
			siteSitemapRegenerationEntries.get(0);

		Assert.assertEquals(
			assetTypeKey, siteSitemapRegenerationEntry.getAssetTypeKey());
		Assert.assertEquals(groupId, siteSitemapRegenerationEntry.getGroupId());

		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(
				RandomTestUtil.randomString(), companyId, groupId);

		Assert.assertEquals(
			2,
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntriesCount(companyId));

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

		Assert.assertEquals(
			2,
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntriesCount(companyId));
	}

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	@Inject
	private SiteSitemapRegenerationEntryLocalService
		_siteSitemapRegenerationEntryLocalService;

}