/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.cleanup.internal.upgrade;

import com.liferay.layout.admin.kernel.model.LayoutTypePortletConstants;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTable;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Georgel Pop
 */
public class WidgetLayoutTypeSettingsUpgradeProcess extends UpgradeProcess {

	public WidgetLayoutTypeSettingsUpgradeProcess(
		LayoutLocalService layoutLocalService) {

		_layoutLocalService = layoutLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		List<Layout> layouts = _layoutLocalService.getLayouts(
			_layoutLocalService.dslQuery(
				DSLQueryFactoryUtil.selectDistinct(
					LayoutTable.INSTANCE.plid
				).from(
					LayoutTable.INSTANCE
				).where(
					LayoutTable.INSTANCE.type.eq(
						LayoutConstants.TYPE_CONTENT
					).and(
						LayoutTable.INSTANCE.typeSettings.like(
							"%CUSTOMIZABLE_LAYOUT%"
						).or(
							LayoutTable.INSTANCE.typeSettings.like(
								"%column-%"
							).or(
								LayoutTable.INSTANCE.typeSettings.like(
									"%layout-template-id%")
							)
						).withParentheses()
					)
				)));

		for (Layout layout : layouts) {
			UnicodeProperties typeSettingsUnicodeProperties =
				layout.getTypeSettingsProperties();

			if (!_isWidgetLayoutSettingsCleared(
					typeSettingsUnicodeProperties)) {

				continue;
			}

			_layoutLocalService.updateTypeSettings(
				layout, typeSettingsUnicodeProperties.toString());
		}
	}

	private boolean _isWidgetLayoutSettingsCleared(
		UnicodeProperties typeSettingsUnicodeProperties) {

		int count = typeSettingsUnicodeProperties.size();

		typeSettingsUnicodeProperties.remove(
			LayoutConstants.CUSTOMIZABLE_LAYOUT);
		typeSettingsUnicodeProperties.remove(
			LayoutTypePortletConstants.LAYOUT_TEMPLATE_ID);

		Set<Map.Entry<String, String>> entries =
			typeSettingsUnicodeProperties.entrySet();

		entries.removeIf(
			entry -> {
				String key = entry.getKey();

				return key.startsWith("column-");
			});

		if (count != typeSettingsUnicodeProperties.size()) {
			return true;
		}

		return false;
	}

	private final LayoutLocalService _layoutLocalService;

}