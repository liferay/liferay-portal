/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.exception.NoSuchExperienceException;
import com.liferay.segments.exception.SegmentsExperienceAudienceEntryRelAudienceEntryERCException;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelLocalService;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class SegmentsExperienceAudienceEntryRelServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpdateSegmentsExperienceAudienceEntryRels()
		throws Exception {

		UserTestUtil.setUser(TestPropsValues.getUser());

		Group group = _groupLocalService.getGroup(TestPropsValues.getGroupId());

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			group.getGroupId());

		SegmentsExperience segmentsExperience =
			SegmentsTestUtil.addSegmentsExperience(
				group.getGroupId(), segmentsEntry.getExternalReferenceCode(),
				null, layout.getPlid());

		List<SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels =
				_segmentsExperienceAudienceEntryRelService.
					updateSegmentsExperienceAudienceEntryRels(
						group.getGroupId(),
						new String[] {"audience1", "audience2", "audience3"},
						segmentsExperience.getExternalReferenceCode());

		_assertSegmentsExperienceAudienceEntryRels(
			new String[] {"audience1", "audience2", "audience3"},
			segmentsExperienceAudienceEntryRels);

		segmentsExperienceAudienceEntryRels =
			_segmentsExperienceAudienceEntryRelService.
				updateSegmentsExperienceAudienceEntryRels(
					group.getGroupId(), new String[] {"audience3", "audience1"},
					segmentsExperience.getExternalReferenceCode());

		_assertSegmentsExperienceAudienceEntryRels(
			new String[] {"audience3", "audience1"},
			segmentsExperienceAudienceEntryRels);

		_segmentsExperienceAudienceEntryRelService.
			updateSegmentsExperienceAudienceEntryRels(
				group.getGroupId(), new String[0],
				segmentsExperience.getExternalReferenceCode());

		segmentsExperienceAudienceEntryRels =
			_segmentsExperienceAudienceEntryRelLocalService.
				getSegmentsExperienceAudienceEntryRels(
					group.getGroupId(),
					segmentsExperience.getExternalReferenceCode());

		Assert.assertTrue(
			segmentsExperienceAudienceEntryRels.toString(),
			segmentsExperienceAudienceEntryRels.isEmpty());

		String audienceEntryERC = RandomTestUtil.randomString();

		Assert.assertThrows(
			SegmentsExperienceAudienceEntryRelAudienceEntryERCException.class,
			() ->
				_segmentsExperienceAudienceEntryRelService.
					updateSegmentsExperienceAudienceEntryRels(
						group.getGroupId(),
						new String[] {audienceEntryERC, StringPool.BLANK},
						segmentsExperience.getExternalReferenceCode()));

		Assert.assertThrows(
			SegmentsExperienceAudienceEntryRelAudienceEntryERCException.class,
			() ->
				_segmentsExperienceAudienceEntryRelService.
					updateSegmentsExperienceAudienceEntryRels(
						group.getGroupId(),
						new String[] {audienceEntryERC, audienceEntryERC},
						segmentsExperience.getExternalReferenceCode()));

		Assert.assertThrows(
			NoSuchExperienceException.class,
			() ->
				_segmentsExperienceAudienceEntryRelService.
					updateSegmentsExperienceAudienceEntryRels(
						group.getGroupId(), new String[] {audienceEntryERC},
						RandomTestUtil.randomString()));

		User user = UserTestUtil.addGroupUser(group, RoleConstants.SITE_MEMBER);

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_segmentsExperienceAudienceEntryRelService.
						updateSegmentsExperienceAudienceEntryRels(
							group.getGroupId(), new String[] {audienceEntryERC},
							segmentsExperience.getExternalReferenceCode()));
		}

		segmentsExperienceAudienceEntryRels =
			_segmentsExperienceAudienceEntryRelLocalService.
				getSegmentsExperienceAudienceEntryRels(
					group.getGroupId(),
					segmentsExperience.getExternalReferenceCode());

		Assert.assertTrue(
			segmentsExperienceAudienceEntryRels.toString(),
			segmentsExperienceAudienceEntryRels.isEmpty());
	}

	private void _assertSegmentsExperienceAudienceEntryRels(
		String[] expectedAudienceEntryERCs,
		List<SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels) {

		for (int i = 0; i < expectedAudienceEntryERCs.length; i++) {
			SegmentsExperienceAudienceEntryRel
				segmentsExperienceAudienceEntryRel =
					segmentsExperienceAudienceEntryRels.get(i);

			Assert.assertEquals(
				expectedAudienceEntryERCs[i],
				segmentsExperienceAudienceEntryRel.getAudienceEntryERC());
			Assert.assertEquals(
				expectedAudienceEntryERCs.length - i,
				segmentsExperienceAudienceEntryRel.getPriority());
		}

		Assert.assertEquals(
			segmentsExperienceAudienceEntryRels.toString(),
			expectedAudienceEntryERCs.length,
			segmentsExperienceAudienceEntryRels.size());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private SegmentsExperienceAudienceEntryRelLocalService
		_segmentsExperienceAudienceEntryRelLocalService;

	@Inject
	private SegmentsExperienceAudienceEntryRelService
		_segmentsExperienceAudienceEntryRelService;

}