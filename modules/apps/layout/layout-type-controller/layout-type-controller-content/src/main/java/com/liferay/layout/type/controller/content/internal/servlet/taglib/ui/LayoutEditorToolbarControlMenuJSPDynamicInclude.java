/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.type.controller.content.internal.servlet.taglib.ui;

import com.liferay.layout.security.permission.resource.LayoutContentModelResourcePermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(service = DynamicInclude.class)
public class LayoutEditorToolbarControlMenuJSPDynamicInclude
	extends BaseJSPDynamicInclude {

	@Override
	public ServletContext getServletContext() {
		return _servletContext;
	}

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			if (!isShow(httpServletRequest)) {
				return;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		super.include(httpServletRequest, httpServletResponse, key);
	}

	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (layout.isSystem() && layout.isTypeContent() &&
			!_isConversionLayout(layout)) {

			layout = _layoutLocalService.getLayout(layout.getClassPK());
		}

		if (!layout.isTypeAssetDisplay() && !layout.isTypeContent()) {
			return false;
		}

		String mode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (!Objects.equals(mode, Constants.EDIT) ||
			(!LayoutPermissionUtil.containsLayoutUpdatePermission(
				themeDisplay.getPermissionChecker(), layout) &&
			 !_modelResourcePermission.contains(
				 themeDisplay.getPermissionChecker(), layout.getPlid(),
				 ActionKeys.UPDATE))) {

			return false;
		}

		return true;
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.product.navigation.taglib#/page.jsp#post");
	}

	@Override
	protected String getJspPath() {
		return "/dynamic_include/entry.jsp";
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	private boolean _isConversionLayout(Layout layout) throws PortalException {
		Layout publishedLayout = _layoutLocalService.getLayout(
			layout.getClassPK());

		if (layout.isTypeContent() &&
			Objects.equals(
				publishedLayout.getType(), LayoutConstants.TYPE_PORTLET)) {

			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutEditorToolbarControlMenuJSPDynamicInclude.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutContentModelResourcePermission _modelResourcePermission;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.type.controller.content)"
	)
	private ServletContext _servletContext;

}