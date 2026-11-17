/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2025-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTRemote;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.service.CTRemoteService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kiana Suetani
 */
@RunWith(Arquillian.class)
public class CTRemoteServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctRemote = _ctRemoteLocalService.addCTRemote(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testGetCTRemotes() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_ctRemoteService.getCTRemotes(
				StringPool.BLANK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

			Assert.fail();
		}
		catch (PortalException portalException) {
		}

		List<CTRemote> ctRemotes = _ctRemoteService.getCTRemotes(
			StringPool.BLANK, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertTrue(ctRemotes.contains(_ctRemote));
	}

	@Test
	public void testGetCTRemotesCount() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user, PermissionCheckerFactoryUtil.create(_user))) {

			_ctRemoteService.getCTRemotesCount(StringPool.BLANK);

			Assert.fail();
		}
		catch (PortalException portalException) {
		}

		Assert.assertEquals(
			_ctRemoteLocalService.getCTRemotesCount(),
			_ctRemoteService.getCTRemotesCount(StringPool.BLANK));
	}

	private CTRemote _ctRemote;

	@Inject
	private CTRemoteLocalService _ctRemoteLocalService;

	@Inject
	private CTRemoteService _ctRemoteService;

	private User _user;

}