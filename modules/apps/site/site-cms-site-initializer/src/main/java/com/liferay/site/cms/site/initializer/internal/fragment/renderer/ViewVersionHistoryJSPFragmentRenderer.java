/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.cms.site.initializer.internal.display.context.ViewVersionHistoryDisplayContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = FragmentRenderer.class)
public class ViewVersionHistoryJSPFragmentRenderer
	extends BaseJSPSectionFragmentRenderer<ViewVersionHistoryDisplayContext> {

	@Override
	public String getCollectionKey() {
		return "sections";
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (!fragmentRendererContext.isViewMode() ||
			(ParamUtil.getLong(httpServletRequest, "objectEntryId") > 0)) {

			super.render(
				fragmentRendererContext, httpServletRequest,
				httpServletResponse);

			return;
		}

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			httpServletResponse.sendRedirect(
				_portal.getLayoutFullURL(
					_layoutLocalService.getLayoutByFriendlyURL(
						themeDisplay.getScopeGroupId(), false, "/home"),
					themeDisplay));
		}
		catch (PortalException portalException) {
			throw new IOException(portalException);
		}
	}

	@Override
	protected ViewVersionHistoryDisplayContext getDisplayContext(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ObjectEntry objectEntry = _objectEntryService.getObjectEntry(
			ParamUtil.getLong(httpServletRequest, "objectEntryId"));

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.getObjectDefinition(
				objectEntry.getObjectDefinitionId());

		return new ViewVersionHistoryDisplayContext(
			httpServletRequest, _language, objectDefinition, objectEntry);
	}

	@Override
	protected String getLabelKey() {
		return "view-version-history";
	}

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

}