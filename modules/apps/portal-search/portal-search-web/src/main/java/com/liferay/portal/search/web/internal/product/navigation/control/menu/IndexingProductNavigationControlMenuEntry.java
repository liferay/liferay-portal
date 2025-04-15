/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.product.navigation.control.menu;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Julio Camarero
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.TOOLS,
		"product.navigation.control.menu.entry.order:Integer=500"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class IndexingProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public Map<String, Object> getData(HttpServletRequest httpServletRequest) {
		return _data;
	}

	@Override
	public String getIcon(HttpServletRequest httpServletRequest) {
		return "reload";
	}

	@Override
	public String getLabel(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return _language.get(
			resourceBundle, "the-portal-is-currently-reindexing");
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		int count = _indexWriterHelper.getReindexTaskCount(
			CompanyConstants.SYSTEM, false);

		if (count == 0) {
			return false;
		}

		return super.isShow(httpServletRequest);
	}

	private static final Map<String, Object> _data =
		Collections.<String, Object>singletonMap("qa-id", "indexing");

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private Language _language;

}