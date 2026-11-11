/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.provider;

import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.info.item.InfoItemFieldValues;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.layout.admin.web.internal.info.item.helper.LayoutInfoItemFieldValuesProviderHelper;
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
@Component(service = InfoItemFieldValuesProvider.class)
public class LayoutSegmentsExperienceInfoItemFieldValuesProvider
	implements InfoItemFieldValuesProvider<SegmentsExperience> {

	@Override
	public InfoItemFieldValues getInfoItemFieldValues(
		SegmentsExperience segmentsExperience) {

		return _layoutInfoItemFieldValuesProviderHelper.getInfoItemFieldValues(
			_getLayout(segmentsExperience),
			segmentsExperience.getSegmentsExperienceId());
	}

	@Activate
	protected void activate() {
		_layoutInfoItemFieldValuesProviderHelper =
			new LayoutInfoItemFieldValuesProviderHelper(
				_fragmentRendererController);
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
	private FragmentRendererController _fragmentRendererController;

	private volatile LayoutInfoItemFieldValuesProviderHelper
		_layoutInfoItemFieldValuesProviderHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

}