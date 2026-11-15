/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.annotations.PortletSerializable;
import jakarta.portlet.annotations.RenderStateScoped;

import java.util.Collections;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 */
public class PortletURLImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@Test
	public void testSetBeanParameter() {
		PortletImpl portletImpl = new PortletImpl();

		portletImpl.setUndeployedPortlet(true);

		PortletAppImpl portletAppImpl = new PortletAppImpl(
			RandomTestUtil.randomString());

		portletAppImpl.setSpecMajorVersion(3);

		portletImpl.setPortletApp(portletAppImpl);

		PortletURLImpl portletURLImpl = new PortletURLImpl(
			new MockHttpServletRequest(), portletImpl, null,
			PortletRequest.RENDER_PHASE, MimeResponse.Copy.NONE);

		portletURLImpl.setBeanParameter(new TestBean$$SpringCGLIB$$0());

		MutableRenderParameters mutableRenderParameters =
			portletURLImpl.getRenderParameters();

		Assert.assertEquals(
			Collections.singleton("TestBean"),
			mutableRenderParameters.getNames());
	}

	@RenderStateScoped
	public static class TestBean implements PortletSerializable {

		@Override
		public void deserialize(String[] values) {
		}

		@Override
		public String[] serialize() {
			return new String[] {RandomTestUtil.randomString()};
		}

	}

	public static class TestBean$$SpringCGLIB$$0 extends TestBean {
	}

}