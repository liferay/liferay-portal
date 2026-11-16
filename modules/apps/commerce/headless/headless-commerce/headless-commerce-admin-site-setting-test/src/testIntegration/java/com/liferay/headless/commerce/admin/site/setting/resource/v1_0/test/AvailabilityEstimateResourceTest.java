/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.site.setting.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.AvailabilityEstimate;
import com.liferay.headless.commerce.admin.site.setting.client.dto.v1_0.Creator;
import com.liferay.headless.commerce.admin.site.setting.client.pagination.Page;
import com.liferay.headless.commerce.admin.site.setting.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.site.setting.client.problem.Problem;
import com.liferay.headless.commerce.admin.site.setting.client.resource.v1_0.AvailabilityEstimateResource;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Michele Vigilante
 */
@RunWith(Arquillian.class)
public class AvailabilityEstimateResourceTest
	extends BaseAvailabilityEstimateResourceTestCase {

	@Override
	@Test
	public void testGetAvailabilityEstimatesPage() throws Exception {
		super.testGetAvailabilityEstimatesPage();

		_testGetAvailabilityEstimatesPageWithFilter();
	}

	@Ignore
	@Override
	@Test
	public void testGetAvailabilityEstimatesPageWithFilterDateTimeEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAvailabilityEstimate() throws Exception {
	}

	@Override
	@Test
	public void testPatchAvailabilityEstimateByExternalReferenceCode()
		throws Exception {

		super.testPatchAvailabilityEstimateByExternalReferenceCode();

		_testPatchAvailabilityEstimateByExternalReferenceCodeWithNewExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostCommerceAdminSiteSettingGroupAvailabilityEstimate()
		throws Exception {

		super.testPostCommerceAdminSiteSettingGroupAvailabilityEstimate();

		_testPostCommerceAdminSiteSettingGroupAvailabilityEstimateWithCreator();
		_testPostCommerceAdminSiteSettingGroupAvailabilityEstimateWithDuplicateExternalReferenceCode();
		_testPostCommerceAdminSiteSettingGroupAvailabilityEstimateWithoutExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutAvailabilityEstimateByExternalReferenceCode()
		throws Exception {

		super.testPutAvailabilityEstimateByExternalReferenceCode();

		_testPutAvailabilityEstimateByExternalReferenceCodeWithExistingExternalReferenceCode();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"priority", "title"};
	}

	@Override
	protected AvailabilityEstimate randomAvailabilityEstimate()
		throws Exception {

		AvailabilityEstimate availabilityEstimate =
			super.randomAvailabilityEstimate();

		availabilityEstimate.setTitle(
			HashMapBuilder.put(
				LocaleUtil.US.toString(), RandomTestUtil.randomString()
			).build());

		return availabilityEstimate;
	}

	@Override
	protected AvailabilityEstimate
			testDeleteAvailabilityEstimate_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testDeleteAvailabilityEstimateByExternalReferenceCode_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testGetAvailabilityEstimate_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testGetAvailabilityEstimateByExternalReferenceCode_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testGetAvailabilityEstimatePermissionsPage_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testGetAvailabilityEstimatesPage_addAvailabilityEstimate(
				AvailabilityEstimate availabilityEstimate)
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), availabilityEstimate);
	}

	@Override
	protected AvailabilityEstimate
			testGetCommerceAdminSiteSettingGroupAvailabilityEstimatePage_addAvailabilityEstimate(
				Long groupId, AvailabilityEstimate availabilityEstimate)
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), availabilityEstimate);
	}

	@Override
	protected Long
			testGetCommerceAdminSiteSettingGroupAvailabilityEstimatePage_getGroupId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected AvailabilityEstimate
			testGraphQLAvailabilityEstimate_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testPatchAvailabilityEstimateByExternalReferenceCode_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testPostCommerceAdminSiteSettingGroupAvailabilityEstimate_addAvailabilityEstimate(
				AvailabilityEstimate availabilityEstimate)
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), availabilityEstimate);
	}

	@Override
	protected AvailabilityEstimate
			testPutAvailabilityEstimate_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testPutAvailabilityEstimateByExternalReferenceCode_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	@Override
	protected AvailabilityEstimate
			testPutAvailabilityEstimatePermissionsPage_addAvailabilityEstimate()
		throws Exception {

		return availabilityEstimateResource.
			postCommerceAdminSiteSettingGroupAvailabilityEstimate(
				testGroup.getGroupId(), randomAvailabilityEstimate());
	}

	private void _testGetAvailabilityEstimatesPageWithFilter()
		throws Exception {

		AvailabilityEstimate randomAvailabilityEstimate =
			randomAvailabilityEstimate();

		randomAvailabilityEstimate.setExternalReferenceCode(
			"ERC-" + RandomTestUtil.randomString());

		AvailabilityEstimate availabilityEstimate =
			testGetAvailabilityEstimatesPage_addAvailabilityEstimate(
				randomAvailabilityEstimate);

		Page<AvailabilityEstimate> availabilityEstimatesPage =
			availabilityEstimateResource.getAvailabilityEstimatesPage(
				"externalReferenceCode eq '" +
					availabilityEstimate.getExternalReferenceCode() + "'",
				Pagination.of(1, 10), null);

		Assert.assertEquals(1, availabilityEstimatesPage.getTotalCount());

		assertEquals(
			Collections.singletonList(availabilityEstimate),
			(List<AvailabilityEstimate>)availabilityEstimatesPage.getItems());
	}

	private void _testPatchAvailabilityEstimateByExternalReferenceCodeWithNewExternalReferenceCode()
		throws Exception {

		AvailabilityEstimate availabilityEstimate =
			availabilityEstimateResource.
				postCommerceAdminSiteSettingGroupAvailabilityEstimate(
					testGroup.getGroupId(), randomAvailabilityEstimate());

		String oldExternalReferenceCode =
			availabilityEstimate.getExternalReferenceCode();

		availabilityEstimate.setExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		availabilityEstimate.setPriority(RandomTestUtil.randomDouble());
		availabilityEstimate.setTitle(
			HashMapBuilder.put(
				LocaleUtil.US.toString(), RandomTestUtil.randomString()
			).build());

		availabilityEstimateResource.
			patchAvailabilityEstimateByExternalReferenceCode(
				oldExternalReferenceCode, availabilityEstimate);

		AvailabilityEstimate getAvailabilityEstimate =
			availabilityEstimateResource.getAvailabilityEstimate(
				availabilityEstimate.getId());

		Assert.assertTrue(
			equals(availabilityEstimate, getAvailabilityEstimate));
		Assert.assertEquals(
			availabilityEstimate.getExternalReferenceCode(),
			getAvailabilityEstimate.getExternalReferenceCode());
	}

	private void _testPostCommerceAdminSiteSettingGroupAvailabilityEstimateWithCreator()
		throws Exception {

		String password = RandomTestUtil.randomString();
		User user = UserTestUtil.addOmniadminUser();

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		AvailabilityEstimateResource availabilityEstimateResource =
			AvailabilityEstimateResource.builder(
			).authentication(
				user.getEmailAddress(), password
			).locale(
				LocaleUtil.getDefault()
			).parameters(
				"nestedFields", "creator"
			).build();

		AvailabilityEstimate postAvailabilityEstimate =
			availabilityEstimateResource.
				postCommerceAdminSiteSettingGroupAvailabilityEstimate(
					testGroup.getGroupId(), randomAvailabilityEstimate());

		Creator creator = postAvailabilityEstimate.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());
	}

	private void _testPostCommerceAdminSiteSettingGroupAvailabilityEstimateWithDuplicateExternalReferenceCode()
		throws Exception {

		AvailabilityEstimate availabilityEstimate =
			availabilityEstimateResource.
				postCommerceAdminSiteSettingGroupAvailabilityEstimate(
					testGroup.getGroupId(), randomAvailabilityEstimate());

		AvailabilityEstimate duplicateAvailabilityEstimate =
			randomAvailabilityEstimate();

		duplicateAvailabilityEstimate.setExternalReferenceCode(
			availabilityEstimate.getExternalReferenceCode());

		try {
			availabilityEstimateResource.
				postCommerceAdminSiteSettingGroupAvailabilityEstimate(
					testGroup.getGroupId(), duplicateAvailabilityEstimate);

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("BAD_REQUEST", problem.getStatus());
		}
	}

	private void _testPostCommerceAdminSiteSettingGroupAvailabilityEstimateWithoutExternalReferenceCode()
		throws Exception {

		AvailabilityEstimate availabilityEstimate =
			randomAvailabilityEstimate();

		availabilityEstimate.setExternalReferenceCode((String)null);

		AvailabilityEstimate postAvailabilityEstimate =
			availabilityEstimateResource.
				postCommerceAdminSiteSettingGroupAvailabilityEstimate(
					testGroup.getGroupId(), availabilityEstimate);

		AvailabilityEstimate getAvailabilityEstimate =
			availabilityEstimateResource.
				getAvailabilityEstimateByExternalReferenceCode(
					postAvailabilityEstimate.getExternalReferenceCode());

		Assert.assertEquals(
			postAvailabilityEstimate.getId(), getAvailabilityEstimate.getId());
	}

	private void _testPutAvailabilityEstimateByExternalReferenceCodeWithExistingExternalReferenceCode()
		throws Exception {

		AvailabilityEstimate postAvailabilityEstimate =
			availabilityEstimateResource.
				postCommerceAdminSiteSettingGroupAvailabilityEstimate(
					testGroup.getGroupId(), randomAvailabilityEstimate());

		String externalReferenceCode =
			postAvailabilityEstimate.getExternalReferenceCode();

		AvailabilityEstimate availabilityEstimate =
			randomAvailabilityEstimate();

		availabilityEstimate.setExternalReferenceCode(externalReferenceCode);

		AvailabilityEstimate putAvailabilityEstimate =
			availabilityEstimateResource.
				putAvailabilityEstimateByExternalReferenceCode(
					externalReferenceCode, availabilityEstimate);

		Assert.assertEquals(
			postAvailabilityEstimate.getId(), putAvailabilityEstimate.getId());
	}

	@Inject
	private UserLocalService _userLocalService;

}