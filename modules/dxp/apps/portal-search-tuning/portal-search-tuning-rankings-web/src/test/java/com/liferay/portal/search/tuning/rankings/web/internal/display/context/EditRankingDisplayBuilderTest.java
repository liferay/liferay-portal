/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.display.context;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.webcache.WebCachePoolUtil;
import com.liferay.portal.model.impl.GroupImpl;
import com.liferay.portal.search.tuning.rankings.constants.ResultRankingsConstants;
import com.liferay.portal.search.tuning.rankings.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class EditRankingDisplayBuilderTest extends BaseRankingsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpGroupLocalServiceUtil();
		_setUpHttpServletRequest();
		_setUpLearnMessages();

		_editRankingDisplayBuilder = new EditRankingDisplayBuilder(
			httpServletRequest, rankingIndexNameBuilder, _rankingIndexReader,
			_renderResponse);
	}

	@After
	public void tearDown() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testBuild() throws Exception {
		_setUpRenderResponse();
		_setUpThemeDisplay();

		setUpHttpServletRequestParamValue(
			httpServletRequest, "backURL", "backURL");
		setUpHttpServletRequestParamValue(
			httpServletRequest, "keywords", "keywords");
		setUpHttpServletRequestParamValue(
			httpServletRequest, "redirect", "redirect");
		setUpHttpServletRequestParamValue(
			httpServletRequest, "resultsRankingUid", "resultsRankingUid");

		setUpPropsUtil();

		EditRankingDisplayContext editRankingDisplayContext =
			_editRankingDisplayBuilder.build();

		Assert.assertEquals("backURL", editRankingDisplayContext.getBackURL());
		Assert.assertEquals(111L, editRankingDisplayContext.getCompanyId());
		Assert.assertEquals(
			"editResultRankingsFm", editRankingDisplayContext.getFormName());
		Assert.assertEquals(
			"keywords", editRankingDisplayContext.getKeywords());
		Assert.assertEquals(
			"redirect", editRankingDisplayContext.getRedirect());
		Assert.assertEquals(
			"resultsRankingUid",
			editRankingDisplayContext.getResultsRankingUid());
		Assert.assertEquals(
			ResultRankingsConstants.STATUS_ACTIVE,
			editRankingDisplayContext.getStatus());

		Assert.assertNotNull(editRankingDisplayContext.getData());
	}

	protected HttpServletRequest httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	protected ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

	private void _setUpGroupLocalServiceUtil() throws Exception {
		Group group = new GroupImpl();

		Mockito.when(
			GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				Mockito.anyString(), Mockito.anyLong())
		).thenReturn(
			group
		);
	}

	private void _setUpHttpServletRequest() {
		Mockito.doReturn(
			themeDisplay
		).when(
			httpServletRequest
		).getAttribute(
			Mockito.anyString()
		);
	}

	private void _setUpLearnMessages() {
		MockedStatic<WebCachePoolUtil> mockedStatic = Mockito.mockStatic(
			WebCachePoolUtil.class);

		mockedStatic.when(
			() -> WebCachePoolUtil.get(Mockito.anyString(), Mockito.any())
		).thenReturn(
			JSONUtil.put(
				"result-rankings",
				JSONUtil.put(
					"en_US",
					JSONUtil.put(
						"message", "Learn more."
					).put(
						"url", "https://learn.liferay.com"
					)))
		);
	}

	private void _setUpRenderResponse() {
		Mockito.doReturn(
			Mockito.mock(ResourceURL.class)
		).when(
			_renderResponse
		).createResourceURL();
	}

	private void _setUpThemeDisplay() {
		Mockito.doReturn(
			111L
		).when(
			themeDisplay
		).getCompanyId();
	}

	private EditRankingDisplayBuilder _editRankingDisplayBuilder;
	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private final RankingIndexReader _rankingIndexReader = Mockito.mock(
		RankingIndexReader.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);

}