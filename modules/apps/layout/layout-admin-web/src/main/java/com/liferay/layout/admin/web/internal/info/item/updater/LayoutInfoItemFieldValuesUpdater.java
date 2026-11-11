/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.updater;

import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.layout.admin.web.internal.info.item.helper.LayoutInfoItemFieldValuesUpdaterHelper;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "item.class.name=com.liferay.portal.kernel.model.Layout",
	service = InfoItemFieldValuesUpdater.class
)
public class LayoutInfoItemFieldValuesUpdater
	implements InfoItemFieldValuesUpdater<Layout> {

	@Override
	public Layout updateFromInfoItemFieldValues(
		Layout layout, InfoItemFieldValues infoItemFieldValues) {

		long defaultSegmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		return _layoutInfoItemFieldValuesUpdaterHelper.
			updateFromInfoItemFieldValues(
				layout, infoItemFieldValues, defaultSegmentsExperienceId);
	}

	@Activate
	protected void activate() {
		_layoutInfoItemFieldValuesUpdaterHelper =
			new LayoutInfoItemFieldValuesUpdaterHelper(
				_fragmentEntryLinkLocalService, _layoutLocalService);
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	private volatile LayoutInfoItemFieldValuesUpdaterHelper
		_layoutInfoItemFieldValuesUpdaterHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}