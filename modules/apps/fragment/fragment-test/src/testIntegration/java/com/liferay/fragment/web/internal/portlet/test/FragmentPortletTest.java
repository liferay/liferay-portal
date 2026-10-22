/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class FragmentPortletTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@After
	public void tearDown() {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testRenderFragmentEntryWithoutPermissions() throws Exception {
		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout, FragmentPortletKeys.FRAGMENT,
			"column-1", null);

		String html = RandomTestUtil.randomString();

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					_portletURLFactory.create(
						PortletContainerTestUtil.getHttpServletRequest(
							_group, layout),
						FragmentPortletKeys.FRAGMENT, layout.getPlid(),
						PortletRequest.RENDER_PHASE)
				).setMVCPath(
					"/render_fragment_entry.jsp"
				).setParameter(
					"html", html
				).setWindowState(
					LiferayWindowState.EXCLUSIVE
				).buildString());

		String body = response.getBody();

		Assert.assertFalse(body, body.contains(html));
		Assert.assertTrue(
			body,
			body.contains(
				_language.get(
					LocaleUtil.getDefault(),
					"you-do-not-have-the-roles-required-to-access-this-" +
						"portlet")));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Language _language;

	@Inject
	private PortletURLFactory _portletURLFactory;

}