/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public class CompanyModelListenerTest {

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
	public void testOnBeforeRemove() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_siteSitemapRegenerationEntryLocalService.
			addSiteSitemapRegenerationEntry(
				RandomTestUtil.randomString(), companyId,
				RandomTestUtil.randomLong());

		_companyModelListener.onBeforeRemove(
			_companyLocalService.getCompany(companyId));

		List<SiteSitemapRegenerationEntry> siteSitemapRegenerationEntries =
			_siteSitemapRegenerationEntryLocalService.
				getSiteSitemapRegenerationEntries(companyId);

		Assert.assertTrue(
			siteSitemapRegenerationEntries.toString(),
			siteSitemapRegenerationEntries.isEmpty());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "component.name=com.liferay.site.internal.model.listener.CompanyModelListener"
	)
	private ModelListener<Company> _companyModelListener;

	@Inject
	private SiteSitemapRegenerationEntryLocalService
		_siteSitemapRegenerationEntryLocalService;

}