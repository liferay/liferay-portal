/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.configuration.persistence.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.RepositoryLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
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
public class SharepointRepositoryConfigurationModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testOnBeforeDelete() throws Exception {
		String pid = _addFactoryConfiguration(RandomTestUtil.randomString());

		ConfigurationTestUtil.deleteFactoryConfiguration(pid, _FACTORY_PID);
	}

	@Test
	public void testOnBeforeDeleteWithRepository() throws Exception {
		String name = RandomTestUtil.randomString();

		String pid = _addFactoryConfiguration(name);

		Repository repository = _addRepository(name);

		try {
			ConfigurationTestUtil.deleteFactoryConfiguration(pid, _FACTORY_PID);

			Assert.fail();
		}
		catch (ConfigurationModelListenerException
					configurationModelListenerException) {
		}
		finally {
			_repositoryLocalService.deleteRepository(repository);

			ConfigurationTestUtil.deleteFactoryConfiguration(pid, _FACTORY_PID);
		}
	}

	private String _addFactoryConfiguration(String name) throws Exception {
		return ConfigurationTestUtil.createFactoryConfiguration(
			_FACTORY_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"name", name
			).build());
	}

	private Repository _addRepository(String name) throws Exception {
		return _repositoryLocalService.addRepository(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			_classNameLocalService.getClassNameId(
				_CLASS_NAME_SHAREPOINT_EXT_REPOSITORY + name),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(),
			UnicodePropertiesBuilder.create(
				true
			).put(
				"library-path", RandomTestUtil.randomString()
			).put(
				"site-absolute-url", "https://liferay.com"
			).build(),
			true, ServiceContextTestUtil.getServiceContext());
	}

	private static final String _CLASS_NAME_SHAREPOINT_EXT_REPOSITORY =
		"com.liferay.sharepoint.rest.repository.internal.document.library." +
			"repository.external.SharepointExtRepository";

	private static final String _FACTORY_PID =
		"com.liferay.sharepoint.rest.repository.internal.configuration." +
			"SharepointRepositoryConfiguration";

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private RepositoryLocalService _repositoryLocalService;

}