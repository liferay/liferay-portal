/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.GroupKeyException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Miguel Pastor
 */
@RunWith(Arquillian.class)
public class GroupLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddGroup() throws Exception {
		String groupName = RandomTestUtil.randomString();

		Group group1 = _addGroup(groupName);

		AssertUtils.assertFailure(
			DuplicateGroupException.class,
			StringBundler.concat(
				"{companyId=", group1.getCompanyId(), ", groupId=",
				group1.getGroupId(), ", groupKey=", group1.getGroupKey(), "}"),
			() -> _addGroup(groupName));

		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("*"));
		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("22222"));
		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("null"));
		AssertUtils.assertFailure(
			GroupKeyException.class, null, () -> _addGroup("test*"));

		group1 = GroupTestUtil.addGroup();

		Group group2 = GroupTestUtil.addGroup(group1.getGroupId());

		Group group3 = GroupTestUtil.addGroup(group2.getGroupId());

		Group group4 = GroupTestUtil.addGroup(group1.getGroupId());

		_assertDescendantGroups(group1, group2, group3, group4);

		_assertDescendantGroups(group2, group3);
		_assertDescendantGroups(group3);
		_assertDescendantGroups(group4);
	}

	@Test
	public void testCheckSystemGroups() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					company.getCompanyId())) {

			// Verify initial state after company creation

			Group globalGroup = company.getGroup();

			long companyId = company.getCompanyId();

			_assertGlobalGroup(companyId, globalGroup);

			// Verify idempotency

			_groupLocalService.checkSystemGroups(companyId);

			Group afterCheckGlobalGroup = company.getGroup();

			Assert.assertEquals(
				globalGroup.getGroupId(), afterCheckGlobalGroup.getGroupId());

			_assertGlobalGroup(companyId, afterCheckGlobalGroup);
		}
		finally {
			_companyLocalService.deleteCompany(company);
		}
	}

	@Test
	public void testGetStagedSites() {
		List<Group> groups = _groupLocalService.getStagedSites();

		Assert.assertTrue(groups.toString(), groups.isEmpty());
	}

	private Group _addGroup(String name) throws Exception {
		return _groupLocalService.addGroup(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			null, 0, GroupConstants.DEFAULT_LIVE_GROUP_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			GroupConstants.TYPE_SITE_OPEN, true,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION,
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name), true,
			true, ServiceContextTestUtil.getServiceContext());
	}

	private void _assertDescendantGroups(
		Group parentGroup, Group... expectedDescendantGroups) {

		List<Group> actualDescendantGroups = parentGroup.getDescendants(true);

		Assert.assertEquals(
			actualDescendantGroups.toString(), expectedDescendantGroups.length,
			actualDescendantGroups.size());

		for (Group expectedDescendantGroup : expectedDescendantGroups) {
			Assert.assertTrue(
				"Missing descendant: " + expectedDescendantGroup.toString(),
				actualDescendantGroups.contains(expectedDescendantGroup));
		}
	}

	private void _assertGlobalGroup(long companyId, Group group)
		throws PortalException {

		Assert.assertEquals("L_GLOBAL", group.getExternalReferenceCode());
		Assert.assertEquals(
			_classNameLocalService.getClassNameId(Company.class),
			group.getClassNameId());
		Assert.assertEquals(companyId, group.getClassPK());
		Assert.assertEquals("Global", group.getName(LocaleUtil.getDefault()));
		Assert.assertEquals("/global", group.getFriendlyURL());

		Assert.assertNotNull(_groupLocalService.getCompanyGroup(companyId));
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

}