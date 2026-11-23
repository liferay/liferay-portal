/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.exception.NoSuchEntryException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ScopeUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.fetchGroup(TestPropsValues.getGroupId());

		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		_fragmentEntry = FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());

		_layout = LayoutTestUtil.addTypeContentLayout(_group);
	}

	@Test
	public void testAddFragmentEntryLink() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(),
				_fragmentEntry.getExternalReferenceCode(), null,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, null, serviceContext);

		Assert.assertEquals(
			_fragmentEntry.getConfiguration(),
			fragmentEntryLink.getConfiguration());
		Assert.assertEquals(
			_fragmentEntry.getCss(), fragmentEntryLink.getCss());
		Assert.assertEquals(
			_fragmentEntry.getHtml(), fragmentEntryLink.getHtml());
		Assert.assertEquals(_fragmentEntry.getJs(), fragmentEntryLink.getJs());
	}

	@Test(expected = NoSuchEntryException.class)
	public void testAddFragmentEntryLinkWithInvalidFragmentEntryERC()
		throws Exception {

		_fragmentEntryLinkService.addFragmentEntryLink(
			null, _group.getGroupId(), RandomTestUtil.randomString(), null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			_layout.getPlid(), StringPool.BLANK, null,
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@Test(expected = NoSuchEntryException.class)
	public void testAddFragmentEntryLinkWithInvalidRendererKey()
		throws Exception {

		_fragmentEntryLinkService.addFragmentEntryLink(
			null, _group.getGroupId(), null, null,
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				_layout.getPlid()),
			_layout.getPlid(), StringPool.BLANK, RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));
	}

	@Test
	public void testAddTemplateWithoutAddPermission() throws Exception {
		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(),
				_fragmentEntry.getExternalReferenceCode(), null,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, null,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testDeleteFragmentEntryLink() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_fragmentEntry, _layout.getPlid());

		_fragmentEntryLinkService.deleteFragmentEntryLink(
			fragmentEntryLink.getFragmentEntryLinkId());

		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));
	}

	@Test
	public void testDeleteFragmentEntryLinkByExternalReferenceCode()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(),
				_fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					_fragmentEntry.getGroupId(), _group.getGroupId()),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, null,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		_fragmentEntryLinkService.deleteFragmentEntryLink(
			fragmentEntryLink.getExternalReferenceCode(),
			fragmentEntryLink.getGroupId());

		Assert.assertNull(
			_fragmentEntryLinkLocalService.fetchFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId()));
	}

	@Test
	public void testDeleteFragmentEntryLinkByExternalReferenceCodeWithoutDeletePermission()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(),
				_fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					_fragmentEntry.getGroupId(), _group.getGroupId()),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, null,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			_fragmentEntryLinkService.deleteFragmentEntryLink(
				fragmentEntryLink.getExternalReferenceCode(),
				fragmentEntryLink.getGroupId());

			Assert.fail();
		}
		catch (PrincipalException principalException) {
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testGetFragmentEntryLinkByExternalReferenceCode()
		throws Exception {

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(),
				_fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					_fragmentEntry.getGroupId(), _group.getGroupId()),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, null,
				ServiceContextTestUtil.getServiceContext(
					_group, TestPropsValues.getUserId()));

		FragmentEntryLink curFragmentEntryLink =
			_fragmentEntryLinkService.
				getFragmentEntryLinkByExternalReferenceCode(
					fragmentEntryLink.getExternalReferenceCode(),
					fragmentEntryLink.getGroupId());

		Assert.assertEquals(
			fragmentEntryLink.getFragmentEntryLinkId(),
			curFragmentEntryLink.getFragmentEntryLinkId());
	}

	@Test
	public void testGetFragmentEntryLinkByExternalReferenceCodeWithoutViewPermission()
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkService.addFragmentEntryLink(
				null, _group.getGroupId(),
				_fragmentEntry.getExternalReferenceCode(),
				ScopeUtil.getItemScopeExternalReferenceCode(
					_fragmentEntry.getGroupId(), _group.getGroupId()),
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(_layout.getPlid()),
				_layout.getPlid(), StringPool.BLANK, null, serviceContext);

		RoleTestUtil.removeResourcePermission(
			RoleConstants.GUEST, FragmentEntryLink.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
			ActionKeys.VIEW);
		RoleTestUtil.removeResourcePermission(
			RoleConstants.SITE_MEMBER, FragmentEntryLink.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
			ActionKeys.VIEW);

		try {
			UserTestUtil.setUser(
				UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

			Assert.assertNotNull(
				_fragmentEntryLinkService.
					getFragmentEntryLinkByExternalReferenceCode(
						fragmentEntryLink.getExternalReferenceCode(),
						fragmentEntryLink.getGroupId()));
		}
		finally {
			UserTestUtil.setUser(TestPropsValues.getUser());
		}
	}

	@Test
	public void testUpdateFragmentEntryLink() throws Exception {
		FragmentEntryLink fragmentEntryLink =
			FragmentTestUtil.addFragmentEntryLink(
				_fragmentEntry, _layout.getPlid());

		String editableValues = String.valueOf(
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()));

		FragmentEntryLink updatedFragmentEntryLink =
			_fragmentEntryLinkService.updateFragmentEntryLink(
				fragmentEntryLink.getFragmentEntryLinkId(), editableValues);

		Assert.assertEquals(
			editableValues, updatedFragmentEntryLink.getEditableValues());
	}

	private FragmentEntry _fragmentEntry;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	private Layout _layout;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}