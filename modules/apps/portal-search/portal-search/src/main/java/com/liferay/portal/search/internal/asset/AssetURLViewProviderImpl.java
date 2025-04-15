/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.asset;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.asset.AssetURLViewProvider;
import com.liferay.portal.search.web.constants.SearchResultsPortletKeys;

import jakarta.portlet.MutableRenderParameters;
import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gustavo Lima
 */
@Component(service = AssetURLViewProvider.class)
public class AssetURLViewProviderImpl implements AssetURLViewProvider {

	@Override
	public String getAssetURLView(
		AssetRenderer<?> assetRenderer,
		AssetRendererFactory<?> assetRendererFactory, String className,
		long classPK, LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		try {
			PortletURL viewContentURL =
				PortletURLBuilder.createLiferayPortletURL(
					liferayPortletResponse,
					SearchResultsPortletKeys.SEARCH_RESULTS,
					PortletRequest.RENDER_PHASE
				).setRedirect(
					_portal.getCurrentURL(liferayPortletRequest)
				).setPortletMode(
					PortletMode.VIEW
				).setWindowState(
					WindowState.MAXIMIZED
				).buildPortletURL();

			MutableRenderParameters mutableRenderParameters =
				viewContentURL.getRenderParameters();

			mutableRenderParameters.setValue("mvcPath", "/view_content.jsp");

			AssetEntry assetEntry = _assetEntryLocalService.getEntry(
				className, classPK);

			mutableRenderParameters.setValue(
				"assetEntryId", String.valueOf(assetEntry.getEntryId()));

			mutableRenderParameters.setValue(
				"type", assetRendererFactory.getType());

			String viewURL = null;

			if (assetRenderer != null) {
				viewURL = assetRenderer.getURLViewInContext(
					liferayPortletRequest, liferayPortletResponse,
					viewContentURL.toString());
			}

			if (Validator.isNull(viewURL)) {
				viewURL = viewContentURL.toString();
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)liferayPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout previousLayout = themeDisplay.getLayout();

			return HttpComponentsUtil.addParameters(
				viewURL, "p_l_back_url", themeDisplay.getURLCurrent(),
				"p_l_back_url_title",
				previousLayout.getName(themeDisplay.getLocale()));
		}
		catch (Exception exception) {
			_log.error(
				StringBundler.concat(
					"Unable to get asset view URL for class ", className,
					" with primary key ", classPK),
				exception);
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetURLViewProviderImpl.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private Portal _portal;

}