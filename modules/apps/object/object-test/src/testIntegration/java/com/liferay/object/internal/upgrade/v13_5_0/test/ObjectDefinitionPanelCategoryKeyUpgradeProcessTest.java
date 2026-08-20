/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_5_0.test;

import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gabriel Prates
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionPanelCategoryKeyUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		_blankObjectDefinition = _addObjectDefinition(StringPool.BLANK);
		_mergedObjectDefinition = _addObjectDefinition(
			"control_panel.search_tuning");
		_removedObjectDefinition = _addObjectDefinition(
			"applications_menu.applications.custom.apps");
		_supportedObjectDefinition = _addObjectDefinition(
			PanelCategoryKeys.CONTROL_PANEL_WORKFLOW);

		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(13, 5, 0));

		for (UpgradeProcess upgradeProcess : upgradeProcesses) {
			upgradeProcess.upgrade();
		}

		_multiVMPool.clear();

		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_OBJECT,
			_getPanelCategoryKey(_mergedObjectDefinition));
		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_OBJECT,
			_getPanelCategoryKey(_removedObjectDefinition));

		Assert.assertEquals(
			StringPool.BLANK, _getPanelCategoryKey(_blankObjectDefinition));
		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_WORKFLOW,
			_getPanelCategoryKey(_supportedObjectDefinition));
	}

	private ObjectDefinition _addObjectDefinition(String panelCategoryKey)
		throws Exception {

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.addCustomObjectDefinition();

		objectDefinition.setPanelCategoryKey(panelCategoryKey);

		return _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);
	}

	private String _getPanelCategoryKey(ObjectDefinition objectDefinition) {
		ObjectDefinition reloadedObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition.getObjectDefinitionId());

		return reloadedObjectDefinition.getPanelCategoryKey();
	}

	@DeleteAfterTestRun
	private ObjectDefinition _blankObjectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _mergedObjectDefinition;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@DeleteAfterTestRun
	private ObjectDefinition _removedObjectDefinition;

	@DeleteAfterTestRun
	private ObjectDefinition _supportedObjectDefinition;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}