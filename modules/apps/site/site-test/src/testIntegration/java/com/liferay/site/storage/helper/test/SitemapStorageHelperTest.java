/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.storage.helper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.site.constants.SitemapConstants;
import com.liferay.site.storage.helper.SitemapStorageHelper;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cheryl Tang
 */
@RunWith(Arquillian.class)
public class SitemapStorageHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_sitemapStorageHelper.deleteSitemaps(TestPropsValues.getCompanyId());
	}

	@Test
	public void testDeleteSitemaps() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_storeSitemapFiles(companyId, _group1.getGroupId());
		_storeSitemapFiles(companyId, _group2.getGroupId());

		_sitemapStorageHelper.storeLastRegenerateSitemapDateFile(companyId);

		_sitemapStorageHelper.deleteSitemaps(companyId);

		Assert.assertFalse(_sitemapStorageHelper.hasSitemapFiles(companyId));
		Assert.assertNull(
			_sitemapStorageHelper.getLastRegenerateSitemapDate(companyId));
	}

	@Test
	public void testDeleteSitemapsByGroup() throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		_storeSitemapFiles(companyId, _group1.getGroupId());
		_storeSitemapFiles(companyId, _group2.getGroupId());

		_sitemapStorageHelper.deleteSitemaps(companyId, _group1.getGroupId());

		_assertHasSitemapFiles(false, companyId, _group1.getGroupId());
		_assertHasSitemapFiles(true, companyId, _group2.getGroupId());
	}

	private void _assertHasSitemapFiles(
			boolean expected, long companyId, long groupId)
		throws Exception {

		Assert.assertEquals(
			expected, _sitemapStorageHelper.hasSitemapFile(companyId, groupId));

		for (int page = 1; page <= 2; page++) {
			Assert.assertEquals(
				expected,
				_sitemapStorageHelper.hasSitemapFile(
					companyId, groupId,
					SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT, page));
		}
	}

	private void _storeSitemapFiles(long companyId, long groupId)
		throws Exception {

		_sitemapStorageHelper.storeSitemapFile(
			companyId, groupId, RandomTestUtil.randomString());

		for (int page = 1; page <= 2; page++) {
			_sitemapStorageHelper.storeSitemapFile(
				companyId, groupId, SitemapConstants.ASSET_TYPE_KEY_WEB_CONTENT,
				page, RandomTestUtil.randomString());
		}

		_assertHasSitemapFiles(true, companyId, groupId);
	}

	@DeleteAfterTestRun
	private Group _group1;

	@DeleteAfterTestRun
	private Group _group2;

	@Inject
	private SitemapStorageHelper _sitemapStorageHelper;

}