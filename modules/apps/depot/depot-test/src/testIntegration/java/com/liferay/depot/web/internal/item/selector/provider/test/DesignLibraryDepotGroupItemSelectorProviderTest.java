/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class DesignLibraryDepotGroupItemSelectorProviderTest
	extends BaseDepotGroupItemSelectorProviderTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Override
	public GroupItemSelectorProvider getGroupItemSelectorProvider() {
		return _groupItemSelectorProvider;
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo("LPD-106074")
	public void testIsEnabled() {
		Assert.assertTrue(_groupItemSelectorProvider.isEnabled());
	}

	@FeatureFlag(enable = false, value = "LPD-57283")
	@Test
	@TestInfo("LPD-106074")
	public void testIsEnabledWhenFeatureFlagIsDisabled() {
		Assert.assertFalse(_groupItemSelectorProvider.isEnabled());
	}

	@Override
	protected int getDepotType() {
		return DepotConstants.TYPE_DESIGN_LIBRARY;
	}

	@Override
	protected String getLabel() {
		return "Design Libraries";
	}

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.item.selector.provider.DesignLibraryDepotGroupItemSelectorProvider",
		type = GroupItemSelectorProvider.class
	)
	private GroupItemSelectorProvider _groupItemSelectorProvider;

}