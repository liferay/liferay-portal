/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.trash;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.trash.BaseTrashRenderer;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Eduardo García
 */
public class MBCategoryTrashRenderer extends BaseTrashRenderer {

	public static final String TYPE = "category";

	public MBCategoryTrashRenderer(MBCategory category) {
		_category = category;
	}

	@Override
	public String getClassName() {
		return MBCategory.class.getName();
	}

	@Override
	public long getClassPK() {
		return _category.getPrimaryKey();
	}

	@Override
	public String getIconCssClass() {
		return "comments";
	}

	@Override
	public String getPortletId() {
		AssetRendererFactory<MBCategory> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
				MBCategory.class);

		return assetRendererFactory.getPortletId();
	}

	@Override
	public String getSummary(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		return HtmlUtil.stripHtml(_category.getDescription());
	}

	@Override
	public String getTitle(Locale locale) {
		return _category.getName();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String template)
		throws Exception {

		return false;
	}

	private final MBCategory _category;

}