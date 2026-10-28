/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.deployer.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionDeployerImplTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testDeploy() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		List<ServiceReference<?>> serviceReferences = _getServiceReferences(
			"(osgi.jaxrs.name=" + _objectDefinition.getOSGiJaxRsName() + ")");

		Assert.assertEquals(
			serviceReferences.toString(), 4, serviceReferences.size());

		Set<String> implementationClassNames = new HashSet<>();

		for (ServiceReference<?> serviceReference : serviceReferences) {
			implementationClassNames.add(
				(String)serviceReference.getProperty(
					"implementation.class.name"));
		}

		Assert.assertEquals(
			SetUtil.fromArray(
				"com.liferay.object.rest.internal.resource.v1_0." +
					"CollaboratorResourceImpl",
				"com.liferay.object.rest.internal.resource.v1_0." +
					"CommentResourceImpl",
				"com.liferay.object.rest.internal.resource.v1_0." +
					"ObjectEntryRelatedObjectsResourceImpl",
				"com.liferay.object.rest.internal.resource.v1_0." +
					"ObjectEntryResourceImpl"),
			implementationClassNames);

		for (ServiceReference<?> serviceReference : serviceReferences) {
			Bundle bundle = serviceReference.getBundle();

			Class<?> implementationClass = bundle.loadClass(
				(String)serviceReference.getProperty(
					"implementation.class.name"));

			Assert.assertFalse(
				Modifier.isAbstract(implementationClass.getModifiers()));

			for (String objectClass :
					(String[])serviceReference.getProperty(
						Constants.OBJECTCLASS)) {

				Class<?> clazz = bundle.loadClass(objectClass);

				Assert.assertTrue(clazz.isAssignableFrom(implementationClass));
			}
		}
	}

	private List<ServiceReference<?>> _getServiceReferences(
			String applicationSelect)
		throws Exception {

		List<ServiceReference<?>> serviceReferences = new ArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectDefinitionDeployerImplTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<?>[] jaxRsResourceServiceReferences =
			bundleContext.getServiceReferences(
				(String)null, "(osgi.jaxrs.resource=true)");

		for (ServiceReference<?> serviceReference :
				jaxRsResourceServiceReferences) {

			if (!Objects.equals(
					applicationSelect,
					serviceReference.getProperty(
						"osgi.jaxrs.application.select"))) {

				continue;
			}

			serviceReferences.add(serviceReference);
		}

		return serviceReferences;
	}

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

}