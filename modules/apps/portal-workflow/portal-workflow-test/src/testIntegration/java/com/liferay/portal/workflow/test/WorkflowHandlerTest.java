/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.workflow.WorkflowHandler;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Objects;

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
public class WorkflowHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetClassName() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(WorkflowHandlerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		List<String> classNames = TransformUtil.transform(
			bundleContext.getServiceReferences(WorkflowHandler.class, null),
			serviceReference -> {
				Bundle serviceBundle = serviceReference.getBundle();

				String symbolicName = serviceBundle.getSymbolicName();

				if (!symbolicName.startsWith("com.liferay")) {
					return null;
				}

				WorkflowHandler<?> workflowHandler = bundleContext.getService(
					serviceReference);

				try {
					String className = workflowHandler.getClassName();

					if (Objects.equals(
							className,
							serviceReference.getProperty("model.class.name"))) {

						return null;
					}

					return className;
				}
				finally {
					bundleContext.ungetService(serviceReference);
				}
			});

		Assert.assertEquals(classNames.toString(), 0, classNames.size());
	}

}