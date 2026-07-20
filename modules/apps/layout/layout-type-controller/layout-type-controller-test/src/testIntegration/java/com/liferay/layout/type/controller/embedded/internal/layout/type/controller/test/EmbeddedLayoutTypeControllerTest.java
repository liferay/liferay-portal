/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.embedded.internal.layout.type.controller.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.LayoutTypeControllerTracker;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class EmbeddedLayoutTypeControllerTest {

	@ClassRule
	@Rule
	public static AggregateTestRule aggregateTestRule = new AggregateTestRule(
		new LiferayIntegrationTestRule(),
		PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeEmbeddedLayout(_group.getGroupId());

		_layoutTypeController =
			LayoutTypeControllerTracker.getLayoutTypeController(
				LayoutConstants.TYPE_EMBEDDED);
	}

	@Test
	@TestInfo("LPD-98455")
	public void testIncludeLayoutContent() throws Exception {
		String queryString = StringBundler.concat(
			"parameter1=", RandomTestUtil.randomString(),
			"&parameter2=</script><script>alert(",
			RandomTestUtil.randomString(), ")</script>");

		String content = _includeLayoutContent(
			StringBundler.concat(
				"http://", RandomTestUtil.randomString(), "?", queryString));

		Assert.assertTrue(
			content,
			content.contains(
				StringUtil.replace(queryString, CharPool.SLASH, "\\/")));

		String randomString = RandomTestUtil.randomString();

		content = _includeLayoutContent(
			"javascript:alert(" + randomString + ")");

		Assert.assertFalse(
			content, content.contains("javascript:alert(" + randomString));
		Assert.assertTrue(
			content, content.contains("javascript%3aalert(" + randomString));
	}

	private String _includeLayoutContent(String embeddedLayoutURL)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			_layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"embeddedLayoutURL", embeddedLayoutURL);

		_layout.setTypeSettingsProperties(typeSettingsUnicodeProperties);

		_layout = _layoutLocalService.updateLayout(_layout);

		MockHttpServletRequest mockHttpServletRequest =
			ContentLayoutTestUtil.getMockHttpServletRequest(
				_companyLocalService.fetchCompany(
					TestPropsValues.getCompanyId()),
				_group, _layout);

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		_layoutTypeController.includeLayoutContent(
			mockHttpServletRequest, new MockHttpServletResponse(), _layout);

		ScriptData scriptData = (ScriptData)mockHttpServletRequest.getAttribute(
			WebKeys.AUI_SCRIPT_DATA);

		StringWriter stringWriter = new StringWriter();

		scriptData.writeTo(stringWriter);

		return String.valueOf(stringWriter.getBuffer());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutLocalService _layoutLocalService;

	private LayoutTypeController _layoutTypeController;

}