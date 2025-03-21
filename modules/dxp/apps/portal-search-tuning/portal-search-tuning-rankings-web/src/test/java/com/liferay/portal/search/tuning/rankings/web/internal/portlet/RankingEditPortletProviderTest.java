/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet;

import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
import com.liferay.portal.search.tuning.rankings.web.internal.constants.ResultRankingsPortletKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class RankingEditPortletProviderTest extends BaseRankingsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_rankingEditPortletProvider = new RankingEditPortletProvider();
	}

	@Test
	public void testGetPortletName() {
		Assert.assertEquals(
			ResultRankingsPortletKeys.RESULT_RANKINGS,
			_rankingEditPortletProvider.getPortletName());
	}

	@Test
	public void testGetPortletURL() throws Exception {
		setUpPortalUtil();

		Assert.assertEquals(
			setUpPortalPortletURL(),
			_rankingEditPortletProvider.getPortletURL(
				Mockito.mock(HttpServletRequest.class)));
	}

	private RankingEditPortletProvider _rankingEditPortletProvider;

}