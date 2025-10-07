/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.item.selector.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 * @author Roberto Díaz
 */
@FeatureFlag("LPD-17564")
@RunWith(Arquillian.class)
public class SpaceDepotGroupItemSelectorProviderTest
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

	@Override
	protected int getDepotType() {
		return DepotConstants.TYPE_SPACE;
	}

	@Override
	protected String getLabel() {
		return "Spaces";
	}

	@Inject(
		filter = "component.name=com.liferay.depot.web.internal.item.selector.provider.SpaceDepotGroupItemSelectorProvider",
		type = GroupItemSelectorProvider.class
	)
	private GroupItemSelectorProvider _groupItemSelectorProvider;

}