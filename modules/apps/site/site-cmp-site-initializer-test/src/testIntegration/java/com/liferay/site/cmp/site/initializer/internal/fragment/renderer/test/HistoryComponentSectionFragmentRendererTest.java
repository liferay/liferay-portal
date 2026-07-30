/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.fragment.renderer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class HistoryComponentSectionFragmentRendererTest
	extends BaseComponentSectionFragmentRendererTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testGetComponentName() throws Exception {
		Assert.assertEquals("ProjectHistory", _getComponentName());

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition, cmpTaskObjectEntry);

		Assert.assertEquals("TaskHistory", _getComponentName());
	}

	@Test
	public void testGetProps() throws Exception {
		Assert.assertEquals(
			StringBundler.concat(
				"/o", cmpProjectObjectDefinition.getRESTContextPath(),
				StringPool.SLASH, cmpProjectObjectEntry.getObjectEntryId(),
				"?fields=auditEvents&nestedFields=auditEvents"),
			MapUtil.getString(getProps(), "apiURL"));

		mockHttpServletRequest = getMockHttpServletRequest(
			cmpTaskObjectDefinition, cmpTaskObjectEntry);

		Assert.assertEquals(
			StringBundler.concat(
				"/o", cmpTaskObjectDefinition.getRESTContextPath(),
				StringPool.SLASH, cmpTaskObjectEntry.getObjectEntryId(),
				"?fields=auditEvents&nestedFields=auditEvents"),
			MapUtil.getString(getProps(), "apiURL"));
	}

	@Override
	protected FragmentRenderer getFragmentRenderer() {
		return _fragmentRenderer;
	}

	private String _getComponentName() {
		return ReflectionTestUtil.invoke(
			getFragmentRenderer(), "getComponentName",
			new Class<?>[] {HttpServletRequest.class}, mockHttpServletRequest);
	}

	@Inject(
		filter = "component.name=com.liferay.site.cmp.site.initializer.internal.fragment.renderer.HistoryComponentSectionFragmentRenderer"
	)
	private FragmentRenderer _fragmentRenderer;

}