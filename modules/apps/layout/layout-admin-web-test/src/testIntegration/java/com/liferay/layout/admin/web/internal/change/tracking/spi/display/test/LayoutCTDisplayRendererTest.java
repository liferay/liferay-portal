/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.change.tracking.spi.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class LayoutCTDisplayRendererTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@FeatureFlag("LPD-84028")
	@Test
	public void testRenderPreview() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		ServiceContextThreadLocal.pushServiceContext(serviceContext);

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.layout.admin.web.internal.change.tracking.spi." +
					"display.LayoutCTDisplayRenderer",
				LoggerTestUtil.WARN)) {

			CTDisplayRenderer<Layout> ctDisplayRenderer =
				_ctDisplayRendererRegistry.getCTDisplayRenderer(
					_classNameLocalService.getClassNameId(Layout.class));
			IllegalStateException illegalStateException =
				new IllegalStateException();
			Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

			Assert.assertNull(
				ctDisplayRenderer.renderPreview(
					_createDisplayContext(illegalStateException, layout)));

			Assert.assertSame(
				serviceContext, ServiceContextThreadLocal.getServiceContext());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to render preview for layout " + layout.getPlid(),
				logEntry.getMessage());
			Assert.assertEquals(LoggerTestUtil.WARN, logEntry.getPriority());
			Assert.assertSame(illegalStateException, logEntry.getThrowable());
		}
		finally {
			ServiceContextThreadLocal.popServiceContext();
		}
	}

	private DisplayContext<Layout> _createDisplayContext(
			IllegalStateException illegalStateException, Layout layout)
		throws Exception {

		HttpServletRequest httpServletRequest = new HttpServletRequestWrapper(
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.getCompany(TestPropsValues.getCompanyId()),
				_group, layout)) {

			@Override
			public String getParameter(String name) {
				if (name.equals("segmentsExperienceId")) {
					throw illegalStateException;
				}

				return super.getParameter(name);
			}

		};
		HttpServletResponse httpServletResponse = new MockHttpServletResponse();

		return new DisplayContext<Layout>() {

			@Override
			public String getDownloadURL(String key, long size, String title) {
				return null;
			}

			@Override
			public HttpServletRequest getHttpServletRequest() {
				return httpServletRequest;
			}

			@Override
			public HttpServletResponse getHttpServletResponse() {
				return httpServletResponse;
			}

			@Override
			public Locale getLocale() {
				return LocaleUtil.US;
			}

			@Override
			public Layout getModel() {
				return layout;
			}

			@Override
			public void render(BaseModel<?> baseModel, Locale locale) {
			}

			@Override
			public String renderPreview(BaseModel<?> baseModel, Locale locale) {
				return null;
			}

		};
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@DeleteAfterTestRun
	private Group _group;

}