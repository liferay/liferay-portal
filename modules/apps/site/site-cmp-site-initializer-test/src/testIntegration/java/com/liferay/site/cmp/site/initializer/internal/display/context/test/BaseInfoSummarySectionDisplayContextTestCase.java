/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context.test;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cmp.site.initializer.test.util.CMPTestUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.junit.Before;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseInfoSummarySectionDisplayContextTestCase {

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(
			BaseInfoSummarySectionDisplayContextTestCase.class);

		cmpProjectObjectEntry = CMPTestUtil.addCMPProjectObjectEntry();
	}

	protected abstract FragmentRenderer getFragmentRenderer();

	protected Map<String, Object> getProperties(ObjectEntry objectEntry)
		throws Exception {

		FragmentRenderer fragmentRenderer = getFragmentRenderer();

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			InfoDisplayWebKeys.INFO_ITEM, objectEntry);

		themeDisplay = new ThemeDisplay() {
			{
				setLocale(LocaleUtil.US);
				setPermissionChecker(
					PermissionCheckerFactoryUtil.create(
						TestPropsValues.getUser()));
			}
		};

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		fragmentRenderer.render(
			null, httpServletRequest, new MockHttpServletResponse());

		return ReflectionTestUtil.invoke(
			getSectionDisplayContext(httpServletRequest), "getProperties",
			new Class<?>[0]);
	}

	protected abstract Object getSectionDisplayContext(
			HttpServletRequest httpServletRequest)
		throws Exception;

	protected ObjectEntry cmpProjectObjectEntry;
	protected ThemeDisplay themeDisplay;

}