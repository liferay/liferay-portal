/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.ServletContextClassLoaderPool;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.ServletContext;

import java.io.File;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dante Wang
 */
@RunWith(Arquillian.class)
public class PortalDirTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPortalDir() {
		ServletContext servletContext = ServletContextPool.get(
			ServletContextClassLoaderPool.getServletContextName(
				PortalClassLoaderUtil.getClassLoader()));

		File portalWebInfDir = new File(
			servletContext.getRealPath(StringPool.SLASH), "WEB-INF");

		Assert.assertTrue(portalWebInfDir.isDirectory());

		Assert.assertTrue(
			PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR.endsWith(
				"WEB-INF/shielded-container-lib/"));
	}

}