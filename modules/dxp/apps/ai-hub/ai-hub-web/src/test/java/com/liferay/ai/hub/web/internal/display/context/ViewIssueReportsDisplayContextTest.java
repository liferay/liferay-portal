/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.rest.manager.v1_0.ObjectEntryManager;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Davyson Melo
 */
public class ViewIssueReportsDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		Mockito.when(
			_httpServletRequest.getAttribute(WebKeys.THEME_DISPLAY)
		).thenReturn(
			_themeDisplay
		);

		Mockito.when(
			_themeDisplay.getCompanyId()
		).thenReturn(
			1L
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		_viewIssueReportsDisplayContext = new ViewIssueReportsDisplayContext(
			_httpServletRequest, _objectDefinitionLocalService,
			_objectEntryManager);
	}

	@Test
	public void testGetAPIURL() {
		Assert.assertEquals(
			"/o/ai-hub/reports?nestedFields=" +
				"aiHubAgentDefinitionsToAIHubReports",
			_viewIssueReportsDisplayContext.getAPIURL());
	}

	@Test
	public void testGetCardsReactDataCountsFacetsAndComputesPercentages()
		throws Exception {

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_REPORT", 1L)
		).thenReturn(
			Mockito.mock(ObjectDefinition.class)
		);

		Page<?> page = Mockito.mock(Page.class);

		Mockito.when(
			page.getTotalCount()
		).thenReturn(
			10L
		);

		Mockito.when(
			page.getFacets()
		).thenReturn(
			List.of(
				new Facet(
					"level", List.of(new Facet.FacetValue(2, "critical"))),
				new Facet(
					"feedback",
					List.of(
						new Facet.FacetValue(3, "negative"),
						new Facet.FacetValue(6, "positive"))))
		);

		Mockito.when(
			_objectEntryManager.getObjectEntries(
				Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
				Mockito.any())
		).thenReturn(
			(Page)page
		);

		Map<String, Object> cardsReactData =
			_viewIssueReportsDisplayContext.getCardsReactData();

		Assert.assertEquals(2L, cardsReactData.get("criticalIssuesCount"));
		Assert.assertEquals(30, cardsReactData.get("dislikeRatingPercent"));
		Assert.assertEquals(60, cardsReactData.get("positiveRatingPercent"));
	}

	@Test
	public void testGetCardsReactDataWhenObjectDefinitionIsNull()
		throws Exception {

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_AI_HUB_REPORT", 1L)
		).thenReturn(
			null
		);

		Map<String, Object> cardsReactData =
			_viewIssueReportsDisplayContext.getCardsReactData();

		Assert.assertEquals(0L, cardsReactData.get("criticalIssuesCount"));
		Assert.assertEquals(0, cardsReactData.get("dislikeRatingPercent"));
		Assert.assertEquals(0, cardsReactData.get("positiveRatingPercent"));
	}

	@Mock
	private HttpServletRequest _httpServletRequest;

	@Mock
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Mock
	private ObjectEntryManager _objectEntryManager;

	@Mock
	private ThemeDisplay _themeDisplay;

	private ViewIssueReportsDisplayContext _viewIssueReportsDisplayContext;

}