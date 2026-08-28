/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.upgrade.registry.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.repository.RepositoryFactory;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.service.component.runtime.dto.ReferenceDTO;

/**
 * @author István András Dézsi
 */
@RunWith(Arquillian.class)
public class DLWebUpgradeStepRegistratorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testRegister() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Collection<ServiceReference<UpgradeStepRegistrator>> serviceReferences =
			bundleContext.getServiceReferences(
				UpgradeStepRegistrator.class,
				"(component.name=" + _CLASS_NAME + ")");

		Assert.assertEquals(
			serviceReferences.toString(), 1, serviceReferences.size());

		for (ServiceReference<UpgradeStepRegistrator> serviceReference :
				serviceReferences) {

			ComponentDescriptionDTO componentDescriptionDTO =
				_serviceComponentRuntime.getComponentDescriptionDTO(
					serviceReference.getBundle(), _CLASS_NAME);

			Assert.assertTrue(
				Arrays.toString(componentDescriptionDTO.references),
				_hasReference(
					componentDescriptionDTO, RepositoryFactory.class.getName(),
					"(class.name=com.liferay.portal.repository." +
						"liferayrepository.LiferayRepository)"));
		}
	}

	private boolean _hasReference(
		ComponentDescriptionDTO componentDescriptionDTO, String interfaceName,
		String target) {

		for (ReferenceDTO referenceDTO : componentDescriptionDTO.references) {
			if (Objects.equals(referenceDTO.interfaceName, interfaceName) &&
				Objects.equals(referenceDTO.target, target)) {

				return true;
			}
		}

		return false;
	}

	private static final String _CLASS_NAME =
		"com.liferay.document.library.web.internal.upgrade.registry." +
			"DLWebUpgradeStepRegistrator";

	@Inject
	private ServiceComponentRuntime _serviceComponentRuntime;

}