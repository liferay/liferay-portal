/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.product.navigation.control.menu;

import com.liferay.layout.content.page.editor.constants.ContentPageEditorWebKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.security.permission.resource.LayoutContentModelResourcePermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.service.SegmentsEntryLocalService;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentLocalService;
import com.liferay.segments.service.SegmentsExperimentRelLocalService;
import com.liferay.segments.web.internal.display.context.SegmentsExperienceSelectorDisplayContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pablo Molina
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.EXP,
		"product.navigation.control.menu.entry.order:Integer=110"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class SegmentsExperienceSelectorProductNavigationControlMenuEntry
	extends BaseProductNavigationControlMenuEntry {

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

		try {
			SegmentsExperienceSelectorDisplayContext
				segmentsExperienceSelectorDisplayContext =
					new SegmentsExperienceSelectorDisplayContext(
						httpServletRequest, _jsonFactory, _language, _portal,
						_segmentsEntryLocalService,
						new SegmentsExperienceManager(
							_segmentsExperienceLocalService),
						_segmentsExperienceLocalService,
						_segmentsExperimentLocalService,
						_segmentsExperimentRelLocalService);

			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.write("<div class=\"border-left border-secondary ");
			printWriter.write("control-menu-nav-item\">");

			_reactRenderer.renderReact(
				new ComponentDescriptor("{ExperiencePicker} from segments-web"),
				segmentsExperienceSelectorDisplayContext.getData(),
				httpServletRequest, printWriter);

			printWriter.write("</div>");
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return false;
		}

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest) {
		String mode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (Objects.equals(mode, Constants.EDIT)) {
			return false;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		LayoutTypePortlet layoutTypePortlet =
			themeDisplay.getLayoutTypePortlet();

		LayoutTypeController layoutTypeController =
			layoutTypePortlet.getLayoutTypeController();

		if (layoutTypeController.isFullPageDisplayable()) {
			return false;
		}

		Layout layout = themeDisplay.getLayout();

		if ((layout instanceof VirtualLayout) || !layout.isLayoutUpdateable() ||
			!layout.isTypeContent()) {

			return false;
		}

		long segmentsExperiencesCount =
			_segmentsExperienceLocalService.getSegmentsExperiencesCount(
				themeDisplay.getScopeGroupId(), themeDisplay.getPlid(), true);

		if (segmentsExperiencesCount <= 1) {
			return false;
		}

		String className = (String)httpServletRequest.getAttribute(
			ContentPageEditorWebKeys.CLASS_NAME);

		if (Objects.equals(
				className, LayoutPageTemplateEntry.class.getName())) {

			return false;
		}

		try {
			if (layout.isSystem() && layout.isTypeContent()) {
				layout = _layoutLocalService.getLayout(layout.getClassPK());
			}

			if (_layoutPermission.containsLayoutUpdatePermission(
					themeDisplay.getPermissionChecker(), layout) ||
				_modelResourcePermission.contains(
					themeDisplay.getPermissionChecker(), layout.getPlid(),
					ActionKeys.UPDATE)) {

				return true;
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperienceSelectorProductNavigationControlMenuEntry.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPermission _layoutPermission;

	@Reference
	private LayoutContentModelResourcePermission _modelResourcePermission;

	@Reference
	private Portal _portal;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperimentLocalService _segmentsExperimentLocalService;

	@Reference
	private SegmentsExperimentRelLocalService
		_segmentsExperimentRelLocalService;

}