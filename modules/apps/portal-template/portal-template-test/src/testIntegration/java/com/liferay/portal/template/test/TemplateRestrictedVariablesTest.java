/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.portal.kernel.template.StringTemplateResource;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collection;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Dante Wang
 * @author Debora Buriti
 */
@RunWith(Arquillian.class)
public class TemplateRestrictedVariablesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetRestrictedVariables() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			TemplateRestrictedVariablesTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<TemplateManager>> serviceReferences =
			bundleContext.getServiceReferences(TemplateManager.class, null);

		Assert.assertFalse(serviceReferences.isEmpty());

		for (ServiceReference<TemplateManager> serviceReference :
				serviceReferences) {

			TemplateManager templateManager = bundleContext.getService(
				serviceReference);

			try {
				String name = templateManager.getName();
				Template template = templateManager.getTemplate(
					new StringTemplateResource(
						RandomTestUtil.randomString(),
						RandomTestUtil.randomString()),
					true);

				for (String restrictedVariable :
						templateManager.getRestrictedVariables()) {

					Assert.assertFalse(
						restrictedVariable + " accessible in " + name,
						template.containsKey(restrictedVariable));

					template.put(
						restrictedVariable, RandomTestUtil.randomString());

					Assert.assertFalse(
						restrictedVariable + " accessible in " + name,
						template.containsKey(restrictedVariable));
				}
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}
		}
	}

	@Test
	public void testRestrictedSAXReaderUtil() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			TemplateRestrictedVariablesTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Collection<ServiceReference<TemplateManager>> serviceReferences =
			bundleContext.getServiceReferences(
				TemplateManager.class,
				"(language.type=" + TemplateConstants.LANG_TYPE_FTL + ")");

		Assert.assertFalse(serviceReferences.isEmpty());

		for (ServiceReference<TemplateManager> serviceReference :
				serviceReferences) {

			TemplateManager templateManager = bundleContext.getService(
				serviceReference);

			try {
				_testRestrictedSAXReaderUtil(
					"ACCESSIBLE", false, templateManager);
				_testRestrictedSAXReaderUtil(
					"DENIED", true, templateManager);
			}
			finally {
				bundleContext.ungetService(serviceReference);
			}
		}
	}

	private void _testRestrictedSAXReaderUtil(
			String expected, boolean restricted,
			TemplateManager templateManager)
		throws Exception {

		Template template = templateManager.getTemplate(
			new StringTemplateResource(
				RandomTestUtil.randomString(),
				"<#if saxReaderUtil??>ACCESSIBLE<#else>DENIED</#if>"),
			restricted);

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		template.processTemplate(unsyncStringWriter);

		Assert.assertEquals(expected, unsyncStringWriter.toString());
	}

}