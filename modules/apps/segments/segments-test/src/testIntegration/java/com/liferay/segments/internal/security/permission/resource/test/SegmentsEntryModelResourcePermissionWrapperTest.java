/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.security.permission.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Cristina González
 */
@RunWith(Arquillian.class)
public class SegmentsEntryModelResourcePermissionWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testContains() throws PortalException {
		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId());

		Assert.assertTrue(
			_segmentsEntryModelResourcePermission.contains(
				_permissionCheckerFactory.create(TestPropsValues.getUser()),
				segmentsEntry.getSegmentsEntryId(), ActionKeys.UPDATE));
	}

	@Test
	public void testContainsWithoutPermissions() throws Exception {
		User user = UserTestUtil.addUser(_group.getGroupId());

		try {
			SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
				_group.getGroupId());

			Assert.assertFalse(
				_segmentsEntryModelResourcePermission.contains(
					_permissionCheckerFactory.create(user),
					segmentsEntry.getSegmentsEntryId(), ActionKeys.UPDATE));
		}
		finally {
			_userLocalService.deleteUser(user);
		}
	}

	@Test
	public void testContainsWithSourceAsahFaroBackend() throws Exception {
		SegmentsEntry segmentsEntry =
			_segmentsEntryLocalService.addSegmentsEntry(
				null, RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.getDefault(), RandomTestUtil.randomString()),
				true, null, SegmentsEntryConstants.SOURCE_ASAH_FARO_BACKEND,
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertFalse(
			_segmentsEntryModelResourcePermission.contains(
				_permissionCheckerFactory.create(TestPropsValues.getUser()),
				segmentsEntry.getSegmentsEntryId(), ActionKeys.UPDATE));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Inject(
		filter = "model.class.name=com.liferay.segments.model.SegmentsEntry"
	)
	private ModelResourcePermission<SegmentsEntry>
		_segmentsEntryModelResourcePermission;

	@Inject
	private UserLocalService _userLocalService;

}