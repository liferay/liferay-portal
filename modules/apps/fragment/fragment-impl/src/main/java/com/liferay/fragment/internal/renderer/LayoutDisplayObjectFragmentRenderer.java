/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.internal.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.item.renderer.InfoItemRendererRegistry;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = FragmentRenderer.class)
public class LayoutDisplayObjectFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "content-display";
	}

	@Override
	public String getIcon() {
		return "web-content";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "display-page-content");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		return layout.isTypeAssetDisplay();
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Object infoItem = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		if (infoItem == null) {
			if (fragmentRendererContext.isEditMode()) {
				FragmentRendererUtil.printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"the-display-page-content-will-be-shown-here");
			}

			return;
		}

		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		InfoItemRenderer<Object> infoItemRenderer = _getInfoItemRenderer(
			infoItemDetails.getClassName());

		if (infoItemRenderer == null) {
			if (fragmentRendererContext.isEditMode()) {
				FragmentRendererUtil.printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"there-are-no-available-renderers-for-the-display-page-" +
						"content");
			}

			return;
		}

		infoItemRenderer.render(
			infoItem, httpServletRequest, httpServletResponse);
	}

	private InfoItemRenderer<Object> _getInfoItemRenderer(
		String displayObjectClassName) {

		List<InfoItemRenderer<?>> infoItemRenderers =
			_infoItemRendererRegistry.getInfoItemRenderers(
				displayObjectClassName);

		if (infoItemRenderers == null) {
			return null;
		}

		return (InfoItemRenderer<Object>)infoItemRenderers.get(0);
	}

	@Reference
	private InfoItemRendererRegistry _infoItemRendererRegistry;

	@Reference
	private Language _language;

}