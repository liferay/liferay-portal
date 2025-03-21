/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.info.display.internal.url.provider;

import com.liferay.asset.info.display.url.provider.AssetInfoEditURLProvider;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = AssetInfoEditURLProvider.class)
public class AssetInfoEditURLProviderImpl implements AssetInfoEditURLProvider {

	@Override
	public String getURL(
		String className, long classPK, HttpServletRequest httpServletRequest) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory == null) {
			return StringPool.BLANK;
		}

		try {
			AssetRenderer<?> assetRenderer =
				assetRendererFactory.getAssetRenderer(classPK);

			if (assetRenderer == null) {
				return StringPool.BLANK;
			}

			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (!assetRenderer.hasEditPermission(
					themeDisplay.getPermissionChecker())) {

				return StringPool.BLANK;
			}

			String redirect = ParamUtil.getString(
				httpServletRequest, "redirect");

			if (Validator.isNull(redirect)) {
				Layout layout = themeDisplay.getLayout();

				if (layout.isTypeAssetDisplay()) {
					redirect = themeDisplay.getURLCurrent();
				}
				else {
					String mode = ParamUtil.getString(
						_portal.getOriginalServletRequest(httpServletRequest),
						"p_l_mode", Constants.VIEW);

					redirect = HttpComponentsUtil.setParameter(
						_portal.getLayoutRelativeURL(layout, themeDisplay),
						"p_l_mode", mode);
				}
			}

			if (Validator.isNotNull(redirect)) {
				String backURL = ParamUtil.getString(
					httpServletRequest, "backURL");

				if (Validator.isNotNull(backURL)) {
					redirect = HttpComponentsUtil.addParameter(
						redirect, "p_l_back_url", backURL);
				}

				String backURLTitle = ParamUtil.getString(
					httpServletRequest, "backURLTitle");

				if (Validator.isNotNull(backURLTitle)) {
					redirect = HttpComponentsUtil.addParameter(
						redirect, "p_l_back_url_title", backURLTitle);
				}
			}

			PortletURL editAssetEntryURL = assetRenderer.getURLEdit(
				httpServletRequest, LiferayWindowState.NORMAL, redirect);

			if (editAssetEntryURL == null) {
				return StringPool.BLANK;
			}

			editAssetEntryURL.setParameter(
				"portletResource", assetRendererFactory.getPortletId());

			return editAssetEntryURL.toString();
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetInfoEditURLProviderImpl.class);

	@Reference
	private Portal _portal;

}