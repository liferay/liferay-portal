/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.resource.v1_0.test;

import com.liferay.analytics.settings.security.constants.AnalyticsSecurityConstants;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.asah.rest.dto.v1_0.AsahSegmentsEntry;
import com.liferay.segments.asah.rest.dto.v1_0.Membership;
import com.liferay.segments.asah.rest.resource.v1_0.AsahSegmentsEntryResource;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsEntryRelLocalService;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Nilton Vieira
 */
@RunWith(Arquillian.class)
public class AsahSegmentsEntryResourceSecurityTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_analyticsAdministratorUser = _userLocalService.fetchUserByScreenName(
			_company.getCompanyId(),
			AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN);

		if (_analyticsAdministratorUser == null) {
			_analyticsAdministratorUser = UserTestUtil.addUser(
				_company.getCompanyId(), TestPropsValues.getUserId(),
				AnalyticsSecurityConstants.SCREEN_NAME_ANALYTICS_ADMIN,
				LocaleUtil.getDefault(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), new long[0],
				ServiceContextTestUtil.getServiceContext());

			_addedAnalyticsAdministratorUser = true;
		}

		_asahSegmentsEntryResource.setContextCompany(_company);
	}

	@After
	public void tearDown() throws Exception {
		if (_addedAnalyticsAdministratorUser) {
			_userLocalService.deleteUser(_analyticsAdministratorUser);
		}
	}

	@Test
	public void testPostAsahSegmentsEntry() throws Exception {
		_testPostAsahSegmentsEntryWithUnknownMembershipUserId();
		_testPostAsahSegmentsEntryWithAnalyticsAdministratorUser();
		_testPostAsahSegmentsEntryWithUnauthorizedUser();
	}

	private SegmentsEntry _postAsahSegmentsEntry(
			Membership membership, User user)
		throws Exception {

		String segmentsEntryKey = RandomTestUtil.randomString();

		AsahSegmentsEntry asahSegmentsEntry = new AsahSegmentsEntry();

		asahSegmentsEntry.setActive(true);
		asahSegmentsEntry.setId(segmentsEntryKey);
		asahSegmentsEntry.setMemberships(new Membership[] {membership});
		asahSegmentsEntry.setName(segmentsEntryKey);

		_asahSegmentsEntryResource.setContextUser(user);

		_asahSegmentsEntryResource.postAsahSegmentsEntry(asahSegmentsEntry);

		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.fetchSegmentsEntry(
				_company.getGroupId(), segmentsEntryKey);

		if (segmentsEntry != null) {
			_segmentsEntries.add(segmentsEntry);
		}

		return segmentsEntry;
	}

	private void _testPostAsahSegmentsEntryWithAnalyticsAdministratorUser()
		throws Exception {

		SegmentsEntry segmentsEntry = _postAsahSegmentsEntry(
			_toMembership(TestPropsValues.getUserId()),
			_analyticsAdministratorUser);

		Assert.assertEquals(
			_analyticsAdministratorUser.getUserId(), segmentsEntry.getUserId());
		Assert.assertEquals(
			1,
			_segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
				segmentsEntry.getSegmentsEntryId()));
	}

	private void _testPostAsahSegmentsEntryWithUnauthorizedUser() {
		Assert.assertThrows(
			PrincipalException.class,
			() -> _postAsahSegmentsEntry(
				_toMembership(TestPropsValues.getUserId()),
				TestPropsValues.getUser()));
	}

	private void _testPostAsahSegmentsEntryWithUnknownMembershipUserId()
		throws Exception {

		SegmentsEntry segmentsEntry = _postAsahSegmentsEntry(
			_toMembership(Long.MAX_VALUE), _analyticsAdministratorUser);

		Assert.assertEquals(
			0,
			_segmentsEntryRelLocalService.getSegmentsEntryRelsCount(
				segmentsEntry.getSegmentsEntryId()));
	}

	private Membership _toMembership(long userId) {
		Membership membership = new Membership();

		membership.setIndividualPK(RandomTestUtil.randomString());
		membership.setRemoved(false);
		membership.setUserId(userId);

		return membership;
	}

	private boolean _addedAnalyticsAdministratorUser;
	private User _analyticsAdministratorUser;

	@Inject
	private AsahSegmentsEntryResource _asahSegmentsEntryResource;

	private Company _company;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private final List<SegmentsEntry> _segmentsEntries = new ArrayList<>();

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	@Inject
	private UserLocalService _userLocalService;

}