/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.info.item.provider;

import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.info.form.InfoForm;
import com.liferay.info.item.provider.InfoItemFormProvider;
import com.liferay.layout.admin.web.internal.info.item.helper.LayoutInfoItemFormProviderHelper;
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
@Component(service = InfoItemFormProvider.class)
public class LayoutSegmentsExperienceInfoItemFormProvider
	implements InfoItemFormProvider<SegmentsExperience> {

	@Override
	public InfoForm getInfoForm() {
		return _layoutInfoItemFormProviderHelper.getInfoForm();
	}

	@Override
	public InfoForm getInfoForm(SegmentsExperience segmentsExperience) {
		return _layoutInfoItemFormProviderHelper.getInfoForm(
			_getLayout(segmentsExperience),
			segmentsExperience.getSegmentsExperienceId());
	}

	@Activate
	protected void activate() {
		_layoutInfoItemFormProviderHelper =
			new LayoutInfoItemFormProviderHelper(_fragmentRendererController);
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

	private volatile LayoutInfoItemFormProviderHelper
		_layoutInfoItemFormProviderHelper;

	@Reference
	private LayoutLocalService _layoutLocalService;

}