/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v13_13_0.test;

import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
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
 * @author Mario Leandro
 */
@RunWith(Arquillian.class)
public class ObjectDefinitionPanelCategoryKeyUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgrade() throws Exception {
		ObjectDefinition blankObjectDefinition = _addObjectDefinition(
			StringPool.BLANK);
		ObjectDefinition movedObjectDefinition = _addObjectDefinition(
			"control_panel.configuration");
		ObjectDefinition supportedObjectDefinition = _addObjectDefinition(
			PanelCategoryKeys.CONTROL_PANEL_WORKFLOW);

		for (UpgradeProcess upgradeProcess :
				UpgradeTestUtil.getUpgradeSteps(
					_upgradeStepRegistrator, new Version(13, 13, 0))) {

			upgradeProcess.upgrade();
		}

		EntityCacheUtil.clearCache();

		Assert.assertEquals(
			StringPool.BLANK, _getPanelCategoryKey(blankObjectDefinition));
		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_OBJECT,
			_getPanelCategoryKey(movedObjectDefinition));
		Assert.assertEquals(
			PanelCategoryKeys.CONTROL_PANEL_WORKFLOW,
			_getPanelCategoryKey(supportedObjectDefinition));
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
		ObjectDefinition persistedObjectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinition(
				objectDefinition.getObjectDefinitionId());

		return persistedObjectDefinition.getPanelCategoryKey();
	}

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.upgrade.registry.ObjectServiceUpgradeStepRegistrator"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}