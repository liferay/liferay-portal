/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Leite
 */
@Component(service = ModelListener.class)
public class PortalPreferenceValueModelListener
	extends BaseModelListener<PortalPreferenceValue> {

	@Override
	public void onAfterCreate(PortalPreferenceValue portalPreferenceValue) {
		_updateLayoutSet(
			portalPreferenceValue.getCompanyId(),
			portalPreferenceValue.getKey(), portalPreferenceValue.getValue());
	}

	@Override
	public void onAfterUpdate(
			PortalPreferenceValue originalPortalPreferenceValue,
			PortalPreferenceValue portalPreferenceValue)
		throws ModelListenerException {

		_updateLayoutSet(
			portalPreferenceValue.getCompanyId(),
			portalPreferenceValue.getKey(), portalPreferenceValue.getValue());
	}

	private void _updateLayoutSet(long companyId, String key, String value) {
		if (!StringUtil.equals(key, PropsKeys.DEFAULT_REGULAR_THEME_ID)) {
			return;
		}

		Group group = _groupLocalService.fetchGroup(
			companyId, GroupConstants.FORMS);

		if (group == null) {
			return;
		}

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/shared");

		if (layout == null) {
			return;
		}

		LayoutSet layoutSet = layout.getLayoutSet();

		layoutSet.setThemeId(value);

		_layoutSetLocalService.updateLayoutSet(layoutSet);
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutSetLocalService _layoutSetLocalService;

}