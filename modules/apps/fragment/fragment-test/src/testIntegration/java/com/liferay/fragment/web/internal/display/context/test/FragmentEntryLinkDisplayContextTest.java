/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.test.util.FragmentEntryTestUtil;
import com.liferay.fragment.test.util.FragmentTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.module.util.BundleUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Constructor;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Lourdes Fernández Besada
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkDisplayContextTest {

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

	@Test
	@TestInfo("LPD-99652")
	public void testGetFragmentEntryLinkName() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry();

		Object fragmentEntryLinkDisplayContext =
			_getFragmentEntryLinkDisplayContext(
				fragmentEntry.getFragmentEntryId());

		_testGetFragmentEntryLinkName(
			FragmentTestUtil.addFragmentEntryLink(
				fragmentEntry, RandomTestUtil.randomLong()),
			fragmentEntryLinkDisplayContext, null);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_testGetFragmentEntryLinkName(
			FragmentTestUtil.addFragmentEntryLink(
				fragmentEntry, layout.getPlid()),
			fragmentEntryLinkDisplayContext, layout);
	}

	@Test
	@TestInfo("LPD-99652")
	public void testGetFragmentEntryLinkTypeLabel() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry();

		Object fragmentEntryLinkDisplayContext =
			_getFragmentEntryLinkDisplayContext(
				fragmentEntry.getFragmentEntryId());

		_testGetFragmentEntryLinkTypeLabel(
			FragmentTestUtil.addFragmentEntryLink(
				fragmentEntry, RandomTestUtil.randomLong()),
			fragmentEntryLinkDisplayContext, null);

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		_testGetFragmentEntryLinkTypeLabel(
			FragmentTestUtil.addFragmentEntryLink(
				fragmentEntry, layout.getPlid()),
			fragmentEntryLinkDisplayContext, layout);
	}

	private FragmentEntry _addFragmentEntry() throws Exception {
		FragmentCollection fragmentCollection =
			FragmentTestUtil.addFragmentCollection(_group.getGroupId());

		return FragmentEntryTestUtil.addFragmentEntry(
			fragmentCollection.getFragmentCollectionId());
	}

	private void _assertWarnMessages(
			FragmentEntryLink fragmentEntryLink,
			Object fragmentEntryLinkDisplayContext, String methodName,
			UnsafeConsumer<String, Exception> unsafeConsumer,
			String... warnMessages)
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			unsafeConsumer.accept(
				ReflectionTestUtil.invoke(
					fragmentEntryLinkDisplayContext, methodName,
					new Class<?>[] {FragmentEntryLink.class},
					fragmentEntryLink));

			List<String> logMessages = TransformUtil.transform(
				logCapture.getLogEntries(), LogEntry::getMessage);

			for (String warnMessage : warnMessages) {
				Assert.assertTrue(
					logMessages.toString(), logMessages.remove(warnMessage));
			}

			Assert.assertTrue(logMessages.toString(), logMessages.isEmpty());
		}
	}

	private String[] _getExpectedWarnMessages(
		FragmentEntryLink fragmentEntryLink, Layout layout) {

		if (layout != null) {
			return new String[0];
		}

		return new String[] {
			StringBundler.concat(
				"Unable to find layout with PLID ", fragmentEntryLink.getPlid(),
				" for fragment entry link ",
				fragmentEntryLink.getFragmentEntryLinkId())
		};
	}

	private Object _getFragmentEntryLinkDisplayContext(long fragmentEntryId)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			_companyLocalService.getCompany(_group.getCompanyId()));
		themeDisplay.setLocale(LocaleUtil.getDefault());
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);
		mockHttpServletRequest.setParameter(
			"fragmentEntryId", String.valueOf(fragmentEntryId));

		MockLiferayPortletRenderRequest mockLiferayPortletRenderRequest =
			new MockLiferayPortletRenderRequest(mockHttpServletRequest);

		mockLiferayPortletRenderRequest.setAttribute(
			StringBundler.concat(
				mockLiferayPortletRenderRequest.getPortletName(),
				StringPool.DASH, WebKeys.CURRENT_PORTLET_URL),
			new MockLiferayPortletURL());

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		bundle = BundleUtil.getBundle(
			bundle.getBundleContext(), "com.liferay.fragment.web");

		Class<?> clazz = bundle.loadClass(_CLASS_NAME);

		Constructor<?> constructor = clazz.getConstructor(
			HttpServletRequest.class, RenderRequest.class,
			RenderResponse.class);

		return constructor.newInstance(
			mockHttpServletRequest, mockLiferayPortletRenderRequest,
			new MockLiferayPortletRenderResponse());
	}

	private void _testGetFragmentEntryLinkName(
			FragmentEntryLink fragmentEntryLink,
			Object fragmentEntryLinkDisplayContext, Layout layout)
		throws Exception {

		_assertWarnMessages(
			fragmentEntryLink, fragmentEntryLinkDisplayContext,
			"getFragmentEntryLinkName",
			name -> Assert.assertEquals(
				(layout == null) ? StringPool.BLANK :
					layout.getName(LocaleUtil.getDefault()),
				name),
			_getExpectedWarnMessages(fragmentEntryLink, layout));
	}

	private void _testGetFragmentEntryLinkTypeLabel(
			FragmentEntryLink fragmentEntryLink,
			Object fragmentEntryLinkDisplayContext, Layout layout)
		throws Exception {

		_assertWarnMessages(
			fragmentEntryLink, fragmentEntryLinkDisplayContext,
			"getFragmentEntryLinkTypeLabel",
			typeLabel -> Assert.assertEquals(
				(layout == null) ? StringPool.BLANK : "page", typeLabel),
			_getExpectedWarnMessages(fragmentEntryLink, layout));
	}

	private static final String _CLASS_NAME =
		"com.liferay.fragment.web.internal.display.context." +
			"FragmentEntryLinkDisplayContext";

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

}