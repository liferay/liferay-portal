/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.product.navigation.control.menu;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.frontend.taglib.clay.servlet.taglib.ButtonTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.IconTag;
import com.liferay.journal.model.JournalArticle;
import com.liferay.layout.reports.web.internal.configuration.provider.LayoutReportsGooglePageSpeedConfigurationProvider;
import com.liferay.layout.reports.web.internal.constants.ProductNavigationControlMenuEntryConstants;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.taglib.util.BodyBottomTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=550"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class LayoutReportsProductNavigationControlMenuEntry
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
	public boolean includeBody(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		BodyBottomTag bodyBottomTag = new BodyBottomTag();

		bodyBottomTag.setOutputKey("layoutReportsPanel");

		try {
			bodyBottomTag.doBodyTag(
				httpServletRequest, httpServletResponse,
				this::_processBodyBottomTagBody);
		}
		catch (JspException jspException) {
			throw new IOException(jspException);
		}

		return true;
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			Writer writer = httpServletResponse.getWriter();

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				_portal.getLocale(httpServletRequest), getClass());

			IconTag iconTag = new IconTag();

			iconTag.setCssClass("icon-monospaced");
			iconTag.setSymbol("search-experiences");

			writer.write(
				StringUtil.replace(
					_ICON_TMPL_CONTENT, "${", "}",
					HashMapBuilder.put(
						"cssClass",
						() -> {
							if (isPanelStateOpen(
									httpServletRequest,
									ProductNavigationControlMenuEntryConstants.
										SESSION_CLICKS_KEY)) {

								return "active";
							}

							return StringPool.BLANK;
						}
					).put(
						"iconTag",
						iconTag.doTagAsString(
							httpServletRequest, httpServletResponse)
					).put(
						"nonceAttribute",
						ContentSecurityPolicyNonceProviderUtil.
							getNonceAttribute(httpServletRequest)
					).put(
						"title", _language.get(resourceBundle, "page-audit")
					).build()));
		}
		catch (JspException jspException) {
			throw new IOException(jspException);
		}

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		if (!_layoutReportsGooglePageSpeedConfigurationProvider.isEnabled(
				themeDisplay.getScopeGroup()) &&
			!layout.isTypeContent() && !layout.isTypeAssetDisplay()) {

			return false;
		}

		if (!_isShow(themeDisplay) || !_isShowPanel(httpServletRequest)) {
			return false;
		}

		return super.isShow(httpServletRequest);
	}

	private boolean _hasEditPermission(
			Layout layout, PermissionChecker permissionChecker)
		throws PortalException {

		return LayoutPermissionUtil.containsLayoutRestrictedUpdatePermission(
			permissionChecker, layout);
	}

	private boolean _isShow(ThemeDisplay themeDisplay) {
		Layout layout = _layoutLocalService.fetchLayout(themeDisplay.getPlid());

		if (layout == null) {
			return false;
		}

		if ((layout.isTypeAssetDisplay() || layout.isTypeContent() ||
			 layout.isTypePortlet()) &&
			!layout.isEmbeddedPersonalApplication()) {

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			try {
				if (permissionChecker.hasPermission(
						themeDisplay.getScopeGroup(),
						BlogsEntry.class.getName(), BlogsEntry.class.getName(),
						ActionKeys.UPDATE) ||
					permissionChecker.hasPermission(
						themeDisplay.getScopeGroup(),
						DLFileEntry.class.getName(),
						DLFileEntry.class.getName(), ActionKeys.UPDATE) ||
					permissionChecker.hasPermission(
						themeDisplay.getScopeGroup(),
						JournalArticle.class.getName(),
						JournalArticle.class.getName(), ActionKeys.UPDATE)) {

					return true;
				}

				return _hasEditPermission(
					layout, PermissionThreadLocal.getPermissionChecker());
			}
			catch (PortalException portalException) {
				_log.error(portalException);

				return false;
			}
		}

		return false;
	}

	private boolean _isShowPanel(HttpServletRequest httpServletRequest) {
		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (layoutMode.equals(Constants.EDIT)) {
			return false;
		}

		boolean hidePanel = ParamUtil.getBoolean(
			httpServletRequest, "hide-panel");

		return !hidePanel;
	}

	private void _processBodyBottomTagBody(PageContext pageContext) {
		try {
			HttpServletRequest httpServletRequest =
				(HttpServletRequest)pageContext.getRequest();

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				_portal.getLocale(httpServletRequest), getClass());

			pageContext.setAttribute("resourceBundle", resourceBundle);

			JspWriter jspWriter = pageContext.getOut();

			StringBundler sb = new StringBundler(24);

			sb.append("<div aria-label=\"");
			sb.append(_language.get(resourceBundle, "page-audit"));
			sb.append("\" class=\"");

			if (isPanelStateOpen(
					httpServletRequest,
					ProductNavigationControlMenuEntryConstants.
						SESSION_CLICKS_KEY)) {

				sb.append(
					"lfr-has-layout-reports-panel open-admin-panel open ");
			}

			sb.append("cadmin d-print-none lfr-admin-panel ");
			sb.append("lfr-product-menu-panel lfr-layout-reports-panel ");
			sb.append("sidenav-fixed sidenav-menu-slider sidenav-right\" ");
			sb.append("id=\"");
			sb.append("layoutReportsPanelId\" tabindex=\"-1\">");
			sb.append("<div class=\"sidebar sidebar-light ");
			sb.append("sidenav-menu sidebar-sm\">");
			sb.append("<div class=\"sidebar-header\">");
			sb.append("<div class=\"autofit-row autofit-row-center\"><div ");
			sb.append("class=\"autofit-col autofit-col-expand\">");
			sb.append("<h1 class=\"sr-only\">");
			sb.append(_language.get(resourceBundle, "page-audit"));
			sb.append("</h1><span class=\"font-weight-bold\">");
			sb.append(_language.get(resourceBundle, "page-audit"));
			sb.append("</span></div>");
			sb.append("<div class=\"autofit-col\">");

			ButtonTag buttonTag = new ButtonTag();

			buttonTag.setCssClass("close sidenav-close");
			buttonTag.setDisplayType("unstyled");
			buttonTag.setDynamicAttribute(
				StringPool.BLANK, "aria-label",
				_language.get(
					(HttpServletRequest)pageContext.getRequest(), "close"));
			buttonTag.setIcon("times");

			sb.append(buttonTag.doTagAsString(pageContext));

			sb.append("</div></div></div><div class=\"sidebar-body\"><span ");
			sb.append("aria-hidden=\"true\" class=\"loading-animation ");
			sb.append("loading-animation-sm\"></span></div>");

			jspWriter.write(sb.toString());

			_reactRenderer.renderReact(
				new ComponentDescriptor("{App} from layout-reports-web"),
				HashMapBuilder.<String, Object>put(
					"isPanelStateOpen",
					isPanelStateOpen(
						httpServletRequest,
						ProductNavigationControlMenuEntryConstants.
							SESSION_CLICKS_KEY)
				).put(
					"layoutReportsDataURL",
					() -> {
						ThemeDisplay themeDisplay =
							(ThemeDisplay)httpServletRequest.getAttribute(
								WebKeys.THEME_DISPLAY);

						String layoutReportsDataURL =
							HttpComponentsUtil.addParameters(
								StringBundler.concat(
									themeDisplay.getPortalURL(),
									themeDisplay.getPathMain(),
									"/layout_reports",
									"/get_layout_reports_data"),
								"p_l_id", themeDisplay.getPlid());

						long segmentsExperienceId = ParamUtil.getLong(
							_portal.getOriginalServletRequest(
								httpServletRequest),
							"segmentsExperienceId", -1);

						if (segmentsExperienceId == -1) {
							return layoutReportsDataURL;
						}

						return HttpComponentsUtil.addParameter(
							layoutReportsDataURL, "segmentsExperienceId",
							segmentsExperienceId);
					}
				).build(),
				httpServletRequest, jspWriter);

			jspWriter.write("</div></div>");
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private static final String _ICON_TMPL_CONTENT = StringUtil.read(
		LayoutReportsProductNavigationControlMenuEntry.class, "icon.tmpl");

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutReportsProductNavigationControlMenuEntry.class);

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutReportsGooglePageSpeedConfigurationProvider
		_layoutReportsGooglePageSpeedConfigurationProvider;

	@Reference
	private Portal _portal;

	@Reference
	private ReactRenderer _reactRenderer;

}