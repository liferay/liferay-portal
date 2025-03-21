/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class AddResultsRankingsMVCRenderCommandTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_addResultsRankingsMVCRenderCommand =
			new AddResultsRankingsMVCRenderCommand();
	}

	@Test
	public void testRender() throws Exception {
		Assert.assertEquals(
			"/add_results_rankings.jsp",
			_addResultsRankingsMVCRenderCommand.render(
				Mockito.mock(RenderRequest.class),
				Mockito.mock(RenderResponse.class)));
	}

	private AddResultsRankingsMVCRenderCommand
		_addResultsRankingsMVCRenderCommand;

}