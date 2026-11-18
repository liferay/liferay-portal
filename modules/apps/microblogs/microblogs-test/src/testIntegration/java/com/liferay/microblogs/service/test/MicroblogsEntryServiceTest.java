/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.microblogs.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.microblogs.constants.MicroblogsEntryConstants;
import com.liferay.microblogs.model.MicroblogsEntry;
import com.liferay.microblogs.service.MicroblogsEntryService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class MicroblogsEntryServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-116204")
	public void testAddMicroblogsEntry() throws Exception {
		Group group = GroupTestUtil.addGroup();

		User user1 = UserTestUtil.addGroupAdminUser(group);

		User user2 = UserTestUtil.addUser();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user1.getUserId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user1, PermissionCheckerFactoryUtil.create(user1))) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> _microblogsEntryService.addMicroblogsEntry(
					user2.getUserId(), RandomTestUtil.randomString(),
					MicroblogsEntryConstants.TYPE_EVERYONE, 0,
					MicroblogsEntryConstants.TYPE_EVERYONE, serviceContext));

			MicroblogsEntry microblogsEntry =
				_microblogsEntryService.addMicroblogsEntry(
					user1.getUserId(), RandomTestUtil.randomString(),
					MicroblogsEntryConstants.TYPE_EVERYONE, 0,
					MicroblogsEntryConstants.TYPE_EVERYONE, serviceContext);

			Assert.assertEquals(user1.getUserId(), microblogsEntry.getUserId());
		}
	}

	@Inject
	private MicroblogsEntryService _microblogsEntryService;

}