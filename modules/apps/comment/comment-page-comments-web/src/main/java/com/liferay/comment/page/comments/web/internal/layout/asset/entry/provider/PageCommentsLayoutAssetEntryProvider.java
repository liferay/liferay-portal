/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.comment.page.comments.web.internal.layout.asset.entry.provider;

import com.liferay.asset.display.page.layout.asset.entry.provider.LayoutAssetEntryProvider;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.comment.page.comments.web.internal.constants.PageCommentsPortletKeys;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "jakarta.portlet.name=" + PageCommentsPortletKeys.PAGE_COMMENTS,
	service = LayoutAssetEntryProvider.class
)
public class PageCommentsLayoutAssetEntryProvider
	implements LayoutAssetEntryProvider {

	@Override
	public AssetEntry getLayoutAssetEntry(
		HttpServletRequest httpServletRequest, Layout layout) {

		if (!layout.isTypeAssetDisplay()) {
			return null;
		}

		String portletNamespace = _portal.getPortletNamespace(
			ParamUtil.getString(httpServletRequest, "p_p_id"));

		String className = ParamUtil.getString(
			httpServletRequest, portletNamespace + "className");

		long classPK = ParamUtil.getLong(
			httpServletRequest, portletNamespace + "classPK");

		if (Validator.isNotNull(className) && (classPK != 0)) {
			return _assetEntryLocalService.fetchEntry(className, classPK);
		}

		return null;
	}

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private Portal _portal;

}