/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.portlet.action;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class ValidateRankingMVCResourceCommandTest
	extends BaseRankingsPortletActionTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_validateRankingMVCResourceCommand =
			new ValidateRankingMVCResourceCommand();

		ReflectionTestUtil.setFieldValue(
			_validateRankingMVCResourceCommand,
			"_duplicateQueryStringsDetector", duplicateQueryStringsDetector);
		ReflectionTestUtil.setFieldValue(
			_validateRankingMVCResourceCommand, "indexNameBuilder",
			indexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_validateRankingMVCResourceCommand, "portal", portal);
		ReflectionTestUtil.setFieldValue(
			_validateRankingMVCResourceCommand, "rankingIndexNameBuilder",
			rankingIndexNameBuilder);
		ReflectionTestUtil.setFieldValue(
			_validateRankingMVCResourceCommand, "searchRequestBuilderFactory",
			searchRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_validateRankingMVCResourceCommand, "_jsonFactory",
			new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_validateRankingMVCResourceCommand, "_rankingHelper",
			rankingHelper);
	}

	@Test
	public void testServeResource() throws Exception {
		setUpDuplicateQueryStringsDetector();
		setUpIndexNameBuilder();
		setUpPortalUtil();
		setUpResourceResponse();

		HttpServletRequest httpServletRequest =
			setUpPortalGetHttpServletRequest();

		setUpHttpServletRequestParamValues(
			httpServletRequest, "aliase", new String[] {"aliase"});

		_validateRankingMVCResourceCommand.serveResource(
			resourceRequest, resourceResponse);

		Mockito.verify(
			resourceResponse, Mockito.times(1)
		).isCommitted();
	}

	private ValidateRankingMVCResourceCommand
		_validateRankingMVCResourceCommand;

}