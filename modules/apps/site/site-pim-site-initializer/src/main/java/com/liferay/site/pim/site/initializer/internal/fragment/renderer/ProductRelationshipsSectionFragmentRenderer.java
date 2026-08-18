/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.data.set.serializer.FDSSerializer;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.internal.display.context.ProductRelationshipsDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = FragmentRenderer.class)
public class ProductRelationshipsSectionFragmentRenderer
	implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "pim";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "product-relationships");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return FeatureFlagManagerUtil.isEnabled(
			themeDisplay.getCompanyId(), "LPD-96666");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Object infoItem = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (!(infoItem instanceof ObjectEntry)) {
			return;
		}

		httpServletRequest.setAttribute(
			ProductRelationshipsDisplayContext.class.getName(),
			new ProductRelationshipsDisplayContext(
				_fdsSerializer, httpServletRequest, _objectEntryLocalService));

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/product_relationships.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductRelationshipsSectionFragmentRenderer.class);

	@Reference
	private FDSSerializer _fdsSerializer;

	@Reference
	private Language _language;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.pim.site.initializer)"
	)
	private ServletContext _servletContext;

}