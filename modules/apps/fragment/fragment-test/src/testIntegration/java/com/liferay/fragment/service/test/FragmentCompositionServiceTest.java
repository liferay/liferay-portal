/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.service.FragmentCompositionService;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
 * @author Binh Tran
 */
@RunWith(Arquillian.class)
public class FragmentCompositionServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_fragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());

		_updatedFragmentCollection = FragmentTestUtil.addFragmentCollection(
			_group.getGroupId());
	}

	@Test
	public void testAddFragmentCompositionWithoutAddPermission()
		throws Exception {

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentCompositionService.addFragmentComposition(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomId(), RandomTestUtil.randomString(),
				StringPool.BLANK, StringPool.BLANK, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testDeleteFragmentCompositionByExternalReferenceCode()
		throws Exception {

		FragmentComposition fragmentComposition =
			_fragmentCompositionService.addFragmentComposition(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomId(), RandomTestUtil.randomString(),
				StringPool.BLANK, StringPool.BLANK, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		_fragmentCompositionService.deleteFragmentComposition(
			fragmentComposition.getExternalReferenceCode(),
			fragmentComposition.getGroupId());

		Assert.assertNull(
			_fragmentCompositionLocalService.fetchFragmentComposition(
				fragmentComposition.getFragmentCompositionId()));
	}

	@Test
	public void testDeleteFragmentCompositionByExternalReferenceCodeWithoutDeletePermission()
		throws Exception {

		FragmentComposition fragmentComposition =
			_fragmentCompositionService.addFragmentComposition(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomId(), RandomTestUtil.randomString(),
				StringPool.BLANK, StringPool.BLANK, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentCompositionService.deleteFragmentComposition(
				fragmentComposition.getExternalReferenceCode(),
				fragmentComposition.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testGetFragmentCompositionByExternalReferenceCode()
		throws Exception {

		FragmentComposition fragmentComposition =
			_fragmentCompositionService.addFragmentComposition(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomId(), RandomTestUtil.randomString(),
				StringPool.BLANK, StringPool.BLANK, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		FragmentComposition curFragmentComposition =
			_fragmentCompositionService.
				getFragmentCompositionByExternalReferenceCode(
					fragmentComposition.getExternalReferenceCode(),
					fragmentComposition.getGroupId());

		Assert.assertEquals(
			fragmentComposition.getFragmentCompositionId(),
			curFragmentComposition.getFragmentCompositionId());
	}

	@Test
	public void testGetFragmentCompositionByExternalReferenceCodeWithoutViewPermission()
		throws Exception {

		FragmentComposition fragmentComposition =
			_fragmentCompositionService.addFragmentComposition(
				RandomTestUtil.randomString(), _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				StringUtil.randomId(), RandomTestUtil.randomString(),
				StringPool.BLANK, StringPool.BLANK, 0,
				WorkflowConstants.STATUS_APPROVED,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, FragmentComposition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fragmentComposition.getFragmentCompositionId()),
			ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, FragmentComposition.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fragmentComposition.getFragmentCompositionId()),
			ActionKeys.VIEW);

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentCompositionService.
				getFragmentCompositionByExternalReferenceCode(
					fragmentComposition.getExternalReferenceCode(),
					fragmentComposition.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test(expected = PrincipalException.MustHavePermission.class)
	@TestInfo("LPD-109774")
	public void testGetFragmentCompositionsWithoutViewPermission()
		throws Exception {

		_fragmentCompositionService.addFragmentComposition(
			RandomTestUtil.randomString(), _group.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(),
			StringUtil.randomId(), RandomTestUtil.randomString(),
			StringPool.BLANK, StringPool.BLANK, 0,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		_fragmentCompositionService.getFragmentCompositions(
			_group.getGroupId(), _fragmentCollection.getFragmentCollectionId(),
			WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testUpdateFragmentCollectionId() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		String fragmentCompositionKey = RandomTestUtil.randomString();
		String name = RandomTestUtil.randomString();
		String description = RandomTestUtil.randomString();
		String data = RandomTestUtil.randomString();
		long previewFileEntryId = RandomTestUtil.randomLong();
		int status = WorkflowConstants.STATUS_APPROVED;

		FragmentComposition fragmentComposition =
			_fragmentCompositionService.addFragmentComposition(
				null, _group.getGroupId(),
				_fragmentCollection.getFragmentCollectionId(),
				fragmentCompositionKey, name, description, data,
				previewFileEntryId, status, serviceContext);

		fragmentComposition =
			_fragmentCompositionService.updateFragmentComposition(
				fragmentComposition.getFragmentCompositionId(),
				_updatedFragmentCollection.getFragmentCollectionId(),
				fragmentComposition.getName(),
				fragmentComposition.getDescription(),
				fragmentComposition.getData(),
				fragmentComposition.getPreviewFileEntryId(),
				fragmentComposition.getStatus());

		Assert.assertEquals(
			fragmentComposition.getFragmentCollectionId(),
			_updatedFragmentCollection.getFragmentCollectionId());
	}

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCompositionLocalService _fragmentCompositionLocalService;

	@Inject
	private FragmentCompositionService _fragmentCompositionService;

	@DeleteAfterTestRun
	private Group _group;

	private FragmentCollection _updatedFragmentCollection;

}