/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class DynamicIncludeTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testRegister() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(DynamicIncludeTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<String> componentNames = TransformUtil.transform(
			bundleContext.getServiceReferences(DynamicInclude.class, null),
			serviceReference -> {
				Bundle serviceBundle = serviceReference.getBundle();

				String symbolicName = serviceBundle.getSymbolicName();

				if (!symbolicName.startsWith("com.liferay") ||
					(serviceReference.getProperty("dynamic.include.key") !=
						null)) {

					return null;
				}

				return GetterUtil.getString(
					serviceReference.getProperty("component.name"));
			});

		Assert.assertEquals(
			componentNames.toString(), 0, componentNames.size());
	}

}