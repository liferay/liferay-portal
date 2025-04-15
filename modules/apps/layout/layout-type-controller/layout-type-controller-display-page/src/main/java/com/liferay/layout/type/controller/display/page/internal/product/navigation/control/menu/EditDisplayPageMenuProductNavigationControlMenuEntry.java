/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.display.page.internal.product.navigation.control.menu;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.display.url.provider.InfoEditURLProvider;
import com.liferay.info.display.url.provider.InfoEditURLProviderRegistry;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.type.controller.display.page.internal.constants.DisplayPageLayoutTypeControllerWebKeys;
import com.liferay.layout.type.controller.display.page.internal.display.context.EditDisplayPageMenuDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=50"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class EditDisplayPageMenuProductNavigationControlMenuEntry
	extends BaseJSPProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public String getIconJspPath() {
		return "/page_template/edit_menu.jsp";
	}

	@Override
	public String getLabel(Locale locale) {
		return null;
	}

	@Override
	public String getURL(HttpServletRequest httpServletRequest) {
		return null;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		InfoEditURLProvider<Object> infoEditURLProvider =
			_infoEditURLProviderRegistry.getInfoEditURLProvider(
				infoItemDetails.getClassName());

		httpServletRequest.setAttribute(
			DisplayPageLayoutTypeControllerWebKeys.
				EDIT_DISPLAY_PAGE_MENU_DISPLAY_CONTEXT,
			new EditDisplayPageMenuDisplayContext(
				httpServletRequest, infoEditURLProvider));

		return super.includeIcon(httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isTypeControlPanel() || !layout.isTypeAssetDisplay()) {
			return false;
		}

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (layoutMode.equals(Constants.EDIT)) {
			return false;
		}

		Group group = layout.getGroup();

		if (group.hasStagingGroup() && !group.isStagingGroup() &&
			PropsValues.STAGING_LIVE_GROUP_LOCKING_ENABLED) {

			return false;
		}

		InfoItemDetails infoItemDetails =
			(InfoItemDetails)httpServletRequest.getAttribute(
				InfoDisplayWebKeys.INFO_ITEM_DETAILS);

		if (infoItemDetails == null) {
			return false;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				infoItemDetails.getClassName());

		AssetRenderer<?> assetRenderer = null;

		if (assetRendererFactory != null) {
			InfoItemReference infoItemReference =
				infoItemDetails.getInfoItemReference();

			InfoItemIdentifier infoItemIdentifier =
				infoItemReference.getInfoItemIdentifier();

			if (!(infoItemIdentifier instanceof ClassPKInfoItemIdentifier)) {
				return false;
			}

			ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
				(ClassPKInfoItemIdentifier)
					infoItemReference.getInfoItemIdentifier();

			assetRenderer = assetRendererFactory.getAssetRenderer(
				classPKInfoItemIdentifier.getClassPK());
		}

		if (((assetRenderer == null) ||
			 !assetRenderer.hasEditPermission(
				 themeDisplay.getPermissionChecker())) &&
			!LayoutPermissionUtil.contains(
				themeDisplay.getPermissionChecker(), layout,
				ActionKeys.UPDATE)) {

			return false;
		}

		return true;
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private InfoEditURLProviderRegistry _infoEditURLProviderRegistry;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.display.page)"
	)
	private ServletContext _servletContext;

}