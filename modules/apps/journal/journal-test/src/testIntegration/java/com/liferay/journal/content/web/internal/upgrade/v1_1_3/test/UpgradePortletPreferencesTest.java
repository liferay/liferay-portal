/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.content.web.internal.upgrade.v1_1_3.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.constants.JournalContentPortletKeys;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.version.Version;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import jakarta.portlet.PortletPreferences;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mariano Álvaro Sáiz
 */
@RunWith(Arquillian.class)
public class UpgradePortletPreferencesTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testUpgradePortletPreferences() throws Exception {
		_group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(_group);

		Layout layout = LayoutTestUtil.addTypePortletLayout(_group);

		Group stagingGroup = _group.getStagingGroup();

		String stagingGroupPortletId = _addPortletToLayout(
			layout, stagingGroup.getExternalReferenceCode());

		Group companyGroup = _groupLocalService.getCompanyGroup(
			_group.getCompanyId());

		String companyGroupPortletId = _addPortletToLayout(
			layout, companyGroup.getExternalReferenceCode());

		UpgradeProcess[] upgradeProcesses = UpgradeTestUtil.getUpgradeSteps(
			_upgradeStepRegistrator, new Version(1, 1, 3));

		UpgradeProcess upgradeProcess = upgradeProcesses[0];

		upgradeProcess.upgrade();

		_multiVMPool.clear();

		_assertGroupExternalReferenceCode(null, layout, stagingGroupPortletId);
		_assertGroupExternalReferenceCode(
			companyGroup.getExternalReferenceCode(), layout,
			companyGroupPortletId);
	}

	private String _addPortletToLayout(
			Layout layout, String groupExternalReferenceCode)
		throws Exception {

		return LayoutTestUtil.addPortletToLayout(
			layout, JournalContentPortletKeys.JOURNAL_CONTENT,
			HashMapBuilder.put(
				"groupExternalReferenceCode",
				new String[] {groupExternalReferenceCode}
			).build());
	}

	private void _assertGroupExternalReferenceCode(
			String expectedGroupExternalReferenceCode, Layout layout,
			String portletId)
		throws Exception {

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(layout, portletId);

		Assert.assertEquals(
			expectedGroupExternalReferenceCode,
			portletPreferences.getValue("groupExternalReferenceCode", null));
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.journal.content.web.internal.upgrade.registry.JournalContentWebUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}