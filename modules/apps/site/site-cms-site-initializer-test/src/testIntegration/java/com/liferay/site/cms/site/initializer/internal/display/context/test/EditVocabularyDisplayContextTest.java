/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
@Sync
public class EditVocabularyDisplayContextTest
	extends BaseDisplayContextTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-110617")
	public void testGetBackURL() throws Exception {
		String backURL = RandomTestUtil.randomString();

		Assert.assertEquals(backURL, _getBackURL(backURL));

		Assert.assertEquals(
			portal.getLayoutFullURL(
				_layoutLocalService.getLayoutByFriendlyURL(
					group.getGroupId(), false,
					"/categorization/view-vocabularies"),
				themeDisplay),
			_getBackURL("javascript:alert(document.cookie)"));
	}

	private String _getBackURL(String backURL) throws Exception {
		mockHttpServletRequest.setParameter("backURL", backURL);

		_fragmentRenderer.render(
			fragmentRendererContext, mockHttpServletRequest,
			new MockHttpServletResponse());

		return ReflectionTestUtil.invoke(
			mockHttpServletRequest.getAttribute(
				"com.liferay.site.cms.site.initializer.internal.display." +
					"context.EditVocabularyDisplayContext"),
			"getBackURL", new Class<?>[0]);
	}

	@Inject(
		filter = "component.name=com.liferay.site.cms.site.initializer.internal.fragment.renderer.EditVocabularyJSPSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

	@Inject
	private LayoutLocalService _layoutLocalService;

}