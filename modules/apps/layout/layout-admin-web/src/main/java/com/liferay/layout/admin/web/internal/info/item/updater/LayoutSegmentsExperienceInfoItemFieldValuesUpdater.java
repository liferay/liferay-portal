/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.updater;

import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.updater.InfoItemFieldValuesUpdater;
import com.liferay.layout.admin.web.internal.info.item.helper.LayoutInfoItemFieldValuesUpdaterHelper;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.segments.model.SegmentsExperience;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "item.class.name=com.liferay.segments.model.SegmentsExperience",
	service = InfoItemFieldValuesUpdater.class
)
public class LayoutSegmentsExperienceInfoItemFieldValuesUpdater
	implements InfoItemFieldValuesUpdater<SegmentsExperience> {

	@Override
	public SegmentsExperience updateFromInfoItemFieldValues(
		SegmentsExperience segmentsExperience,
		InfoItemFieldValues infoItemFieldValues) {

		_layoutInfoItemFieldValuesUpdaterHelper.updateFromInfoItemFieldValues(
			_getLayout(segmentsExperience), infoItemFieldValues,
			segmentsExperience.getSegmentsExperienceId());

		return segmentsExperience;
	}

	@Activate
	protected void activate() {
		_layoutInfoItemFieldValuesUpdaterHelper =
			new LayoutInfoItemFieldValuesUpdaterHelper(
				_fragmentEntryLinkLocalService, _layoutLocalService);
	}

	private Layout _getLayout(SegmentsExperience segmentsExperience) {
		try {
			Layout layout = _layoutLocalService.getLayout(
				segmentsExperience.getPlid());

			if (layout.isDraftLayout()) {
				return layout;
			}

			Layout draftLayout = layout.fetchDraftLayout();

			if (draftLayout != null) {
				return draftLayout;
			}

			return layout;
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Reference
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	private volatile LayoutInfoItemFieldValuesUpdaterHelper
		_layoutInfoItemFieldValuesUpdaterHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

}