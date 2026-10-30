/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class TemplateContextContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPrepare() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			TemplateContextContributorTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<TemplateContextContributor>>
			serviceReferences = bundleContext.getServiceReferences(
				TemplateContextContributor.class,
				"(type=" + TemplateContextContributor.TYPE_GLOBAL + ")");

		Assert.assertFalse(serviceReferences.isEmpty());

		List<String> violations = new ArrayList<>();

		for (ServiceReference<TemplateContextContributor> serviceReference :
				serviceReferences) {

			Bundle serviceBundle = serviceReference.getBundle();

			String symbolicName = serviceBundle.getSymbolicName();

			if (!symbolicName.startsWith("com.liferay.")) {
				continue;
			}

			String componentName = (String)serviceReference.getProperty(
				"component.name");
			Object templateContextKeys = serviceReference.getProperty(
				"template.context.keys");

			if (templateContextKeys == null) {
				violations.add(
					componentName + ": declared no \"template.context.keys\"");

				continue;
			}

			Set<String> declaredKeys = new TreeSet<>();

			if (templateContextKeys instanceof String[] keys) {
				Collections.addAll(declaredKeys, keys);
			}
			else {
				declaredKeys.add((String)templateContextKeys);
			}

			TemplateContextContributor templateContextContributor =
				bundleContext.getService(serviceReference);

			Map<String, Object> contextObjects = new HashMap<>();

			try {
				MockHttpServletRequest mockHttpServletRequest =
					new MockHttpServletRequest();

				mockHttpServletRequest.setAttribute(
					WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());

				templateContextContributor.prepare(
					contextObjects, mockHttpServletRequest);
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}

			Set<String> putKeys = new TreeSet<>(contextObjects.keySet());

			if (declaredKeys.equals(putKeys)) {
				continue;
			}

			violations.add(
				StringBundler.concat(
					componentName, ": declared ", declaredKeys, ", put ",
					putKeys));
		}

		Assert.assertEquals(violations.toString(), 0, violations.size());
	}

}