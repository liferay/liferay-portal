/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.asset.categories.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Victor Silvestre
 */
@Component(
	property = "screen.navigation.entry.order:Integer=20",
	service = ScreenNavigationEntry.class
)
public class CategoryCPAttachmentScreenNavigationEntry
	extends CategoryCPAttachmentScreenNavigationCategory
	implements ScreenNavigationEntry<AssetCategory> {

	@Override
	public String getEntryKey() {
		return getCategoryKey();
	}

	@Override
	public boolean isVisible(User user, AssetCategory assetCategory) {
		if (assetCategory == null) {
			return false;
		}

		return true;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		AssetCategory assetCategory = null;

		long categoryId = ParamUtil.getLong(httpServletRequest, "categoryId");

		try {
			assetCategory = _assetCategoryService.fetchCategory(categoryId);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		httpServletRequest.setAttribute(WebKeys.ASSET_CATEGORY, assetCategory);
		httpServletRequest.setAttribute(
			"cpAttachmentFileEntryService", cpAttachmentFileEntryService);

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/images.jsp");
	}

	@Reference
	protected CPAttachmentFileEntryService cpAttachmentFileEntryService;

	private static final Log _log = LogFactoryUtil.getLog(
		CategoryCPAttachmentScreenNavigationEntry.class);

	@Reference
	private AssetCategoryService _assetCategoryService;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.product.asset.categories.web)"
	)
	private ServletContext _servletContext;

}