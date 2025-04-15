/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.frontend.taglib.clay.servlet.taglib;

import com.liferay.fragment.model.FragmentEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

/**
 * @author Diego Hu
 */
public class BasicFragmentEntryVerticalCardTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_setUpLanguageUtil();
		_setUpPortalUtil();
	}

	@Test
	@TestInfo("LPD-48427")
	public void testGetLabels() {
		Mockito.when(
			_fragmentEntry.getStatus()
		).thenReturn(
			WorkflowConstants.STATUS_DRAFT
		);

		_testGetLabels("draft");

		Mockito.when(
			_fragmentEntry.isCacheable()
		).thenReturn(
			true
		);

		_testGetLabels("cached", "draft");

		Mockito.when(
			_fragmentEntry.isApproved()
		).thenReturn(
			true
		);

		Mockito.when(
			_fragmentEntry.fetchDraftFragmentEntry()
		).thenReturn(
			Mockito.mock(FragmentEntry.class)
		);

		_testGetLabels("approved", "cached", "draft");
	}

	private void _setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.eq(_httpServletRequest), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			(Answer<String>)invocationOnMock -> invocationOnMock.getArgument(
				1, String.class)
		);

		languageUtil.setLanguage(language);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private void _testGetLabels(String... expectedLabels) {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			_portal.getHttpServletRequest(renderRequest)
		).thenReturn(
			_httpServletRequest
		);

		BasicFragmentEntryVerticalCard basicFragmentEntryVerticalCard =
			new BasicFragmentEntryVerticalCard(
				_fragmentEntry, renderRequest,
				Mockito.mock(RenderResponse.class),
				Mockito.mock(RowChecker.class));

		List<String> labels = TransformUtil.transform(
			basicFragmentEntryVerticalCard.getLabels(),
			labelItem -> GetterUtil.getString(labelItem.get("label"), null));

		Assert.assertEquals(
			labels.toString(), expectedLabels.length, labels.size());
		Assert.assertTrue(
			labels.toString(),
			labels.containsAll(ListUtil.fromArray(expectedLabels)));
	}

	private final FragmentEntry _fragmentEntry = Mockito.mock(
		FragmentEntry.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Portal _portal = Mockito.mock(Portal.class);

}