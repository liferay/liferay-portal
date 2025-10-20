/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.label.key.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.label.key.provider.ObjectDefinitionLabelKeyProvider;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionLabelKeyProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetObjectDefinitionLabelKey() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Bundle bundle = FrameworkUtil.getBundle(
			ObjectDefinitionLabelKeyProviderTest.class);

		ServiceTrackerList<ObjectDefinitionLabelKeyProvider>
			systemObjectDefinitionLabelKeyProviders =
				ServiceTrackerListFactory.open(
					bundle.getBundleContext(),
					ObjectDefinitionLabelKeyProvider.class);

		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringPool.BLANK,
			_getObjectDefinitionLabelKey(
				systemObjectDefinitionLabelKeyProviders,
				externalReferenceCode));

		String labelKey = RandomTestUtil.randomString();

		ServiceRegistration<ObjectDefinitionLabelKeyProvider>
			serviceRegistration = bundleContext.registerService(
				ObjectDefinitionLabelKeyProvider.class,
				new TestObjectDefinitionLabelKeyProvider(
					externalReferenceCode, labelKey),
				null);

		Assert.assertEquals(
			labelKey,
			_getObjectDefinitionLabelKey(
				systemObjectDefinitionLabelKeyProviders,
				externalReferenceCode));

		serviceRegistration.unregister();
	}

	private String _getObjectDefinitionLabelKey(
		ServiceTrackerList<ObjectDefinitionLabelKeyProvider>
			systemObjectDefinitionLabelKeyProviders,
		String externalReferenceCode) {

		for (ObjectDefinitionLabelKeyProvider objectDefinitionLabelKeyProvider :
				systemObjectDefinitionLabelKeyProviders) {

			String objectDefinitionLabelKey =
				objectDefinitionLabelKeyProvider.getObjectDefinitionLabelKey(
					externalReferenceCode);

			if (Validator.isNotNull(objectDefinitionLabelKey)) {
				return objectDefinitionLabelKey;
			}
		}

		return StringPool.BLANK;
	}

	private static class TestObjectDefinitionLabelKeyProvider
		implements ObjectDefinitionLabelKeyProvider {

		public TestObjectDefinitionLabelKeyProvider(
			String externalReferenceCode, String labelKey) {

			_externalReferenceCode = externalReferenceCode;
			_labelKey = labelKey;
		}

		@Override
		public String getObjectDefinitionLabelKey(
			String externalReferenceCode) {

			if (_externalReferenceCode.equals(externalReferenceCode)) {
				return _labelKey;
			}

			return RandomTestUtil.randomString();
		}

		private final String _externalReferenceCode;
		private final String _labelKey;

	}

}