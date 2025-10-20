/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.encryptor.Encryptor;
import com.liferay.portal.kernel.io.BigEndianCodec;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.security.ChecksumUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.RenderParametersPool;

import jakarta.portlet.PortletRequest;

import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class PortletURLImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDoAsUserId() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Company company = CompanyLocalServiceUtil.getCompany(
			TestPropsValues.getCompanyId());

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, company.getCompanyId());

		_group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setPlid(layout.getPlid());

		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		byte[] doAsUserIdBytes = new byte[Long.BYTES];

		BigEndianCodec.putLong(doAsUserIdBytes, 0, 1L);

		String doAsUserIdString = StringUtil.bytesToHexString(
			ChecksumUtil.appendChecksum(
				_encryptor.encryptUnencoded(
					company.getKeyObj(), doAsUserIdBytes)));

		LiferayPortletURL liferayPortletURL = _portletURLFactory.create(
			mockHttpServletRequest, PortletKeys.LOGIN, themeDisplay.getPlid(),
			PortletRequest.RENDER_PHASE);

		themeDisplay.setDoAsUserId(doAsUserIdString);

		String url = liferayPortletURL.toString();

		Assert.assertTrue(url.contains("doAsUserId=" + doAsUserIdString));

		ReflectionTestUtil.invoke(
			liferayPortletURL, "clearCache", new Class<?>[0]);

		long doAsUserId = 2;

		BigEndianCodec.putLong(doAsUserIdBytes, 0, doAsUserId);

		doAsUserIdString = StringUtil.bytesToHexString(
			ChecksumUtil.appendChecksum(
				_encryptor.encryptUnencoded(
					company.getKeyObj(), doAsUserIdBytes)));

		themeDisplay.setDoAsUserId(null);

		liferayPortletURL.setDoAsUserId(doAsUserId);

		url = liferayPortletURL.toString();

		Assert.assertTrue(url.contains("doAsUserId=" + doAsUserIdString));
	}

	@Test
	public void testToStringShouldNotReplicateExistingParamValues()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		ThemeDisplay themeDisplay = new ThemeDisplay();

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		themeDisplay.setLayout(layout);
		themeDisplay.setPlid(layout.getPlid());

		themeDisplay.setPortalURL("http://localhost:8080");
		themeDisplay.setScopeGroupId(_group.getGroupId());
		themeDisplay.setSiteGroupId(_group.getGroupId());

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, themeDisplay);

		RenderParametersPool.put(
			mockHttpServletRequest, themeDisplay.getPlid(), PortletKeys.LOGIN,
			Collections.singletonMap(
				"name", new String[] {"value1", "value2"}));

		LiferayPortletURL liferayPortletURL = _portletURLFactory.create(
			mockHttpServletRequest, PortletKeys.LOGIN, themeDisplay.getPlid(),
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setCopyCurrentRenderParameters(true);

		String expectedToString = StringBundler.concat(
			"http://localhost:8080/web", _group.getFriendlyURL(),
			layout.getFriendlyURL(), "?p_p_id=", PortletKeys.LOGIN,
			"&p_p_lifecycle=0&_", PortletKeys.LOGIN, "_name=value1&_",
			PortletKeys.LOGIN, "_name=value2");

		Assert.assertEquals(expectedToString, liferayPortletURL.toString());

		ReflectionTestUtil.invoke(
			liferayPortletURL, "clearCache", new Class<?>[0]);

		Assert.assertEquals(expectedToString, liferayPortletURL.toString());

		ReflectionTestUtil.invoke(
			liferayPortletURL, "clearCache", new Class<?>[0]);

		Assert.assertEquals(expectedToString, liferayPortletURL.toString());
	}

	@Inject
	private Encryptor _encryptor;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private PortletURLFactory _portletURLFactory;

}