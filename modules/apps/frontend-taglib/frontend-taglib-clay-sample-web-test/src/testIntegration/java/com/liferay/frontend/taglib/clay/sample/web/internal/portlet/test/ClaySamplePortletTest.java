/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.sample.web.internal.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.PortletRequest;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Fortunato Maldonado
 */
@RunWith(Arquillian.class)
public class ClaySamplePortletTest {

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

	@Test
	public void testCheckboxIsDisabled() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout, _PORTLET_NAME, "column-1",
			null);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					_portletURLFactory.create(
						PortletContainerTestUtil.getHttpServletRequest(
							_group, layout),
						_PORTLET_NAME, layout.getPlid(),
						PortletRequest.RENDER_PHASE)
				).buildString());

		String body = response.getBody();

		Assert.assertTrue(
			body.contains(
				"<li class=\"nav-item\"><div class=\"custom-control " +
					"custom-checkbox\"><label><input disabled"));
	}

	@Test
	public void testPublishLayoutWithClaySamplePortlet() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout, _PORTLET_NAME, "column-1",
			null);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"net.htmlparser.jericho", LoggerTestUtil.ERROR)) {

			PortletContainerTestUtil.Response response =
				PortletContainerTestUtil.request(
					PortletURLBuilder.create(
						_portletURLFactory.create(
							PortletContainerTestUtil.getHttpServletRequest(
								_group, layout),
							_PORTLET_NAME, layout.getPlid(),
							PortletRequest.RENDER_PHASE)
					).buildString());

			HtmlParserUtil.extractText(response.getBody());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

	@Test
	public void testSearchBarIsDisabled() throws Exception {
		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		LayoutTestUtil.addPortletToLayout(
			TestPropsValues.getUserId(), layout, _PORTLET_NAME, "column-1",
			null);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					_portletURLFactory.create(
						PortletContainerTestUtil.getHttpServletRequest(
							_group, layout),
						_PORTLET_NAME, layout.getPlid(),
						PortletRequest.RENDER_PHASE)
				).buildString());

		String body = response.getBody();

		Assert.assertTrue(
			body.contains(
				"<input class=\"form-control form-control input-group-inset " +
					"input-group-inset-after\" disabled"));
	}

	private static final String _PORTLET_NAME =
		"com_liferay_clay_sample_web_portlet_ClaySamplePortlet";

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private PortletURLFactory _portletURLFactory;

}