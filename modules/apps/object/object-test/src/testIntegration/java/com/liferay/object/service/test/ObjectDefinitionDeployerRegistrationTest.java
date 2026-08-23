/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.related.models.ObjectRelatedModelsProviderRegistryUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionDeployerRegistrationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();
	}

	@Test
	public void testDeployObjectDefinition() throws Exception {
		_objectDefinitionLocalService.deployInactiveObjectDefinition(
			_objectDefinition);

		_objectDefinitionLocalService.deployObjectDefinition(_objectDefinition);

		for (String objectRelationshipType : _OBJECT_RELATIONSHIP_TYPES) {
			ServiceReference<?>[] serviceReferences = _getServiceReferences(
				objectRelationshipType);

			Assert.assertEquals(
				Arrays.toString(serviceReferences), 1,
				serviceReferences.length);
		}
	}

	@Test
	public void testUndeployObjectDefinition() throws Exception {
		_objectDefinitionLocalService.deployInactiveObjectDefinition(
			_objectDefinition);

		_objectDefinitionLocalService.undeployObjectDefinition(
			_objectDefinition);

		for (String objectRelationshipType : _OBJECT_RELATIONSHIP_TYPES) {
			ServiceReference<?>[] serviceReferences = _getServiceReferences(
				objectRelationshipType);

			Assert.assertNull(
				Arrays.toString(serviceReferences), serviceReferences);
		}
	}

	private ServiceReference<?>[] _getServiceReferences(
			String objectRelationshipType)
		throws Exception {

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectDefinitionDeployerRegistrationTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		return bundleContext.getServiceReferences(
			ObjectRelatedModelsProvider.class.getName(),
			StringBundler.concat(
				"(&(",
				ObjectRelatedModelsProviderRegistryUtil.
					KEY_OBJECT_DEFINITION_ERC,
				"=", _objectDefinition.getExternalReferenceCode(), ")(",
				ObjectRelatedModelsProviderRegistryUtil.KEY_RELATIONSHIP_TYPE,
				"=", objectRelationshipType, "))"));
	}

	private static final String[] _OBJECT_RELATIONSHIP_TYPES = {
		ObjectRelationshipConstants.TYPE_MANY_TO_MANY,
		ObjectRelationshipConstants.TYPE_ONE_TO_MANY,
		ObjectRelationshipConstants.TYPE_ONE_TO_ONE
	};

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}