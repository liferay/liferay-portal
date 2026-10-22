/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.url.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.digital.signature.url.DSRequestURLProvider;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Danny Situ
 */
@RunWith(Arquillian.class)
public class DSRequestURLProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetSignURL() throws Exception {
		LayoutTestUtil.addTypePortletLayout(_group);

		String virtualHostname = StringUtil.toLowerCase(
			RandomTestUtil.randomString() + ".com");

		_layoutSetLocalService.updateVirtualHosts(
			_group.getGroupId(), false,
			TreeMapBuilder.put(
				virtualHostname, StringPool.BLANK
			).build());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());
		long dsRequestId = RandomTestUtil.randomLong();

		String signURL = _dsRequestURLProvider.getSignURL(
			company.getCompanyId(), dsRequestId, _group.getGroupId());

		Assert.assertTrue(
			signURL,
			signURL.endsWith(
				"/c/digital_signature/open_ds_request?dsRequestId=" +
					dsRequestId));
		Assert.assertTrue(
			signURL,
			signURL.startsWith(
				_portal.getPortalURL(
					virtualHostname, _portal.getPortalServerPort(false),
					false)));

		_testGetSignURLWhenGroupIsDeleted();
	}

	private void _testGetSignURLWhenGroupIsDeleted() throws Exception {
		Group deletedGroup = GroupTestUtil.addGroup();

		_groupLocalService.deleteGroup(deletedGroup);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group guestGroup = _groupLocalService.getGroup(
			company.getCompanyId(), GroupConstants.GUEST);

		String signURL = _dsRequestURLProvider.getSignURL(
			company.getCompanyId(), RandomTestUtil.randomLong(),
			deletedGroup.getGroupId());

		Assert.assertTrue(
			signURL,
			signURL.startsWith(company.getPortalURL(guestGroup.getGroupId())));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private DSRequestURLProvider _dsRequestURLProvider;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

}