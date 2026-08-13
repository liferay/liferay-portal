/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.change.tracking.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRelElementVariation;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class LayoutPageTemplateStructureRelElementVariationCTDisplayRenderer
	extends BaseCTDisplayRenderer
		<LayoutPageTemplateStructureRelElementVariation> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			LayoutPageTemplateStructureRelElementVariation
				layoutPageTemplateStructureRelElementVariation)
		throws PortalException {

		Layout layout = _layoutLocalService.fetchLayout(
			layoutPageTemplateStructureRelElementVariation.getPlid());

		if (layout == null) {
			return null;
		}

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout == null) {
			draftLayout = layout;
		}

		return HttpComponentsUtil.addParameters(
			_portal.getLayoutFullURL(
				draftLayout,
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY)),
			"p_l_back_url", _portal.getCurrentURL(httpServletRequest),
			"p_l_mode", Constants.EDIT);
	}

	@Override
	public Class<LayoutPageTemplateStructureRelElementVariation>
		getModelClass() {

		return LayoutPageTemplateStructureRelElementVariation.class;
	}

	@Override
	public String getTitle(
		Locale locale,
		LayoutPageTemplateStructureRelElementVariation
			layoutPageTemplateStructureRelElementVariation) {

		return layoutPageTemplateStructureRelElementVariation.getName();
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}