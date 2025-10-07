/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lpkg.deployer.container.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.util.PropsValues;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Matthew Tambara
 */
@RunWith(Arquillian.class)
public class LPKGContainerVerifyTest {

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(LPKGContainerVerifyTest.class);

		_bundleContext = bundle.getBundleContext();
	}

	@Test
	public void testLPKGController() throws Exception {
		Path path = Paths.get(
			PropsValues.MODULE_FRAMEWORK_MARKETPLACE_DIR,
			"Liferay Container Test.lpkg");

		Assert.assertTrue(Files.notExists(path));

		boolean found = false;

		for (Bundle bundle : _bundleContext.getBundles()) {
			if (_LPKG_INNER_NAME.equals(bundle.getSymbolicName())) {
				found = true;

				break;
			}
		}

		Assert.assertTrue("Inner LPKG bundle not installed", found);
	}

	private static final String _LPKG_INNER_NAME =
		"Liferay Inner Container Test";

	private BundleContext _bundleContext;

}