/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.document.library.repository.external.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.repository.registry.RepositoryClassDefinitionCatalogUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class ScopedSharepointRepositoryDefinerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetClassName() throws Exception {
		String name = RandomTestUtil.randomString();

		String pid = ConfigurationTestUtil.createFactoryConfiguration(
			_FACTORY_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"name", name
			).build());

		try {
			Assert.assertTrue(
				_hasExternalRepositoryClassName(
					TestPropsValues.getCompanyId(), name));
			Assert.assertFalse(
				_hasExternalRepositoryClassName(
					RandomTestUtil.randomLong(), name));
		}
		finally {
			ConfigurationTestUtil.deleteFactoryConfiguration(pid, _FACTORY_PID);
		}
	}

	private boolean _hasExternalRepositoryClassName(
		long companyId, String name) {

		for (String className :
				RepositoryClassDefinitionCatalogUtil.
					getExternalRepositoryClassNames(companyId)) {

			if (className.endsWith(name)) {
				return true;
			}
		}

		return false;
	}

	private static final String _FACTORY_PID =
		"com.liferay.sharepoint.rest.repository.internal.configuration." +
			"SharepointRepositoryConfiguration.scoped";

}