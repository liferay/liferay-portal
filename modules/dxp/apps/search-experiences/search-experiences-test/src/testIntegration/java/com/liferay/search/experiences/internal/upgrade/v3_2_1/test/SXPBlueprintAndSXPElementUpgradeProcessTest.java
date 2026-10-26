/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v3_2_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.search.experiences.constants.SXPBlueprintConstants;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.model.SXPElement;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;
import com.liferay.search.experiences.service.SXPElementLocalService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Rodrigo Guedes de Souza
 */
@RunWith(Arquillian.class)
public class SXPBlueprintAndSXPElementUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testUpgrade() throws Exception {
		SXPBlueprint legacySXPBlueprint = _addSXPBlueprint(
			_readJSON("configuration"), _readJSON("elementInstances"));
		SXPBlueprint sxpBlueprint = _addSXPBlueprint(
			_readJSON("configurationUpgraded"),
			_readJSON("elementInstancesUpgraded"));

		SXPElement invalidSXPElement = _addSXPElement("{invalid");
		SXPElement legacySXPElement = _addSXPElement(
			_readJSON("elementDefinition"));
		SXPElement sxpElement = _addSXPElement(
			_readJSON("elementDefinitionUpgraded"));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.search.experiences.internal.upgrade.v3_2_1." +
					"SXPBlueprintAndSXPElementUpgradeProcess",
				LoggerTestUtil.WARN)) {

			UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
				_upgradeStepRegistrator,
				"com.liferay.search.experiences.internal.upgrade.v3_2_1." +
					"SXPBlueprintAndSXPElementUpgradeProcess");

			upgradeProcess.upgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to upgrade element " +
					invalidSXPElement.getSXPElementId(),
				logEntry.getMessage());
		}

		_multiVMPool.clear();

		legacySXPBlueprint = _sxpBlueprintLocalService.getSXPBlueprint(
			legacySXPBlueprint.getSXPBlueprintId());

		JSONAssert.assertEquals(
			_readJSON("configurationUpgraded"),
			legacySXPBlueprint.getConfigurationJSON(), JSONCompareMode.STRICT);
		JSONAssert.assertEquals(
			_readJSON("elementInstancesUpgraded"),
			legacySXPBlueprint.getElementInstancesJSON(),
			JSONCompareMode.STRICT);

		sxpBlueprint = _sxpBlueprintLocalService.getSXPBlueprint(
			sxpBlueprint.getSXPBlueprintId());

		Assert.assertEquals(
			_readJSON("configurationUpgraded"),
			sxpBlueprint.getConfigurationJSON());
		Assert.assertEquals(
			_readJSON("elementInstancesUpgraded"),
			sxpBlueprint.getElementInstancesJSON());

		invalidSXPElement = _sxpElementLocalService.getSXPElement(
			invalidSXPElement.getSXPElementId());

		Assert.assertEquals(
			"{invalid", invalidSXPElement.getElementDefinitionJSON());

		legacySXPElement = _sxpElementLocalService.getSXPElement(
			legacySXPElement.getSXPElementId());

		JSONAssert.assertEquals(
			_readJSON("elementDefinitionUpgraded"),
			legacySXPElement.getElementDefinitionJSON(),
			JSONCompareMode.STRICT);

		sxpElement = _sxpElementLocalService.getSXPElement(
			sxpElement.getSXPElementId());

		Assert.assertEquals(
			_readJSON("elementDefinitionUpgraded"),
			sxpElement.getElementDefinitionJSON());
	}

	private SXPBlueprint _addSXPBlueprint(
			String configurationJSON, String elementInstancesJSON)
		throws Exception {

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			null, TestPropsValues.getUserId(), "{}",
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			"[]", SXPBlueprintConstants.SCHEMA_VERSION,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		sxpBlueprint.setConfigurationJSON(configurationJSON);
		sxpBlueprint.setElementInstancesJSON(elementInstancesJSON);

		sxpBlueprint = _sxpBlueprintLocalService.updateSXPBlueprint(
			sxpBlueprint);

		_sxpBlueprints.add(sxpBlueprint);

		return sxpBlueprint;
	}

	private SXPElement _addSXPElement(String elementDefinitionJSON)
		throws Exception {

		SXPElement sxpElement = _sxpElementLocalService.addSXPElement(
			null, TestPropsValues.getUserId(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			elementDefinitionJSON, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			RandomTestUtil.randomInt(),
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId()));

		_sxpElements.add(sxpElement);

		return sxpElement;
	}

	private String _readJSON(String name) {
		return StringUtil.read(
			_clazz,
			StringBundler.concat(
				"dependencies/", _clazz.getSimpleName(), StringPool.PERIOD,
				name, ".json"));
	}

	private final Class<?> _clazz = getClass();

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

	@DeleteAfterTestRun
	private final List<SXPBlueprint> _sxpBlueprints = new ArrayList<>();

	@Inject
	private SXPElementLocalService _sxpElementLocalService;

	@DeleteAfterTestRun
	private final List<SXPElement> _sxpElements = new ArrayList<>();

	@Inject(
		filter = "(&(component.name=com.liferay.search.experiences.internal.upgrade.registry.SXPServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}