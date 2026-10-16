/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.creator.openai.internal.upgrade.v1_2_0.test;

import com.liferay.ai.creator.openai.configuration.AICreatorOpenAICompanyConfiguration;
import com.liferay.ai.creator.openai.configuration.AICreatorOpenAIGroupConfiguration;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Akhash Ramprakash
 */
@RunWith(Arquillian.class)
public class AICreatorOpenAIConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.ai.creator.openai.internal.upgrade.v1_2_0." +
				"AICreatorOpenAIConfigurationUpgradeProcess");
	}

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_companyPid);
		ConfigurationTestUtil.deleteConfiguration(_groupPid);
	}

	@Test
	public void testUpgrade() throws Exception {
		String apiKey = RandomTestUtil.randomString();

		_companyPid = ConfigurationTestUtil.createFactoryConfiguration(
			AICreatorOpenAICompanyConfiguration.class.getName() + ".scoped",
			StringPool.QUESTION,
			HashMapDictionaryBuilder.<String, Object>put(
				"apiKey", apiKey
			).put(
				"companyId", TestPropsValues.getCompanyId()
			).put(
				"enableDALLEToCreateImages", true
			).build());

		_groupPid = ConfigurationTestUtil.createFactoryConfiguration(
			AICreatorOpenAIGroupConfiguration.class.getName() + ".scoped",
			StringPool.QUESTION,
			HashMapDictionaryBuilder.<String, Object>put(
				"enableDALLEToCreateImages", false
			).put(
				"groupId", _group.getGroupId()
			).build());

		_upgradeProcess.upgrade();

		Dictionary<String, Object> companyProperties = _getProperties(
			_companyPid);

		Assert.assertEquals(apiKey, companyProperties.get("apiKey"));
		Assert.assertNull(companyProperties.get("enableDALLEToCreateImages"));
		Assert.assertEquals(
			Boolean.TRUE, companyProperties.get("enableOpenAIImageGeneration"));

		Dictionary<String, Object> groupProperties = _getProperties(_groupPid);

		Assert.assertNull(groupProperties.get("enableDALLEToCreateImages"));
		Assert.assertEquals(
			Boolean.FALSE, groupProperties.get("enableOpenAIImageGeneration"));
	}

	private Dictionary<String, Object> _getProperties(String pid)
		throws Exception {

		Configuration configuration = _configurationAdmin.getConfiguration(
			pid, StringPool.QUESTION);

		return configuration.getProperties();
	}

	private String _companyPid;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@DeleteAfterTestRun
	private Group _group;

	private String _groupPid;
	private UpgradeProcess _upgradeProcess;

	@Inject(
		filter = "(&(component.name=com.liferay.ai.creator.openai.internal.upgrade.registry.AICreatorOpenAIUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}