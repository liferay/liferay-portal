/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.experiment.web.internal.product.navigation.control.menu;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.frontend.taglib.clay.servlet.taglib.ButtonTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.IconTag;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.LayoutPermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.product.navigation.control.menu.BaseProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.experiment.web.internal.constants.ProductNavigationControlMenuEntryConstants;
import com.liferay.segments.manager.SegmentsExperienceManager;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.model.SegmentsExperiment;
import com.liferay.segments.service.SegmentsExperienceLocalService;
import com.liferay.segments.service.SegmentsExperimentService;
import com.liferay.taglib.util.BodyBottomTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.Writer;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=500"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class SegmentsExperimentProductNavigationControlMenuEntry
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

		bodyBottomTag.setOutputKey("segmentsExperimentPanel");

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

			IconTag iconTag = new IconTag();

			iconTag.setCssClass("icon-monospaced");
			iconTag.setSymbol("test");

			ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
				_portal.getLocale(httpServletRequest), getClass());

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
						"portletNamespace", _portletNamespace
					).put(
						"title",
						HtmlUtil.escape(
							_language.get(resourceBundle, "ab-test"))
					).build()));
		}
		catch (JspException jspException) {
			throw new IOException(jspException);
		}

		return true;
	}

	@Override
	public boolean isPanelStateOpen(
		HttpServletRequest httpServletRequest, String key) {

		if (super.isPanelStateOpen(httpServletRequest, key)) {
			return true;
		}

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		String segmentsExperimentKey = ParamUtil.getString(
			originalHttpServletRequest, "segmentsExperimentKey");

		return Validator.isNotNull(segmentsExperimentKey);
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn()) {
			return false;
		}

		Layout layout = themeDisplay.getLayout();

		if (layout.isEmbeddedPersonalApplication() || !layout.isTypeContent() ||
			layout.isTypeControlPanel() ||
			!LayoutPermissionUtil.containsLayoutRestrictedUpdatePermission(
				themeDisplay.getPermissionChecker(), layout)) {

			return false;
		}

		String layoutMode = ParamUtil.getString(
			httpServletRequest, "p_l_mode", Constants.VIEW);

		if (layoutMode.equals(Constants.EDIT)) {
			return false;
		}

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		boolean hidePanel = GetterUtil.getBoolean(
			portalPreferences.getValue(
				SegmentsPortletKeys.SEGMENTS_EXPERIMENT, "hide-panel"));

		try {
			if (!_analyticsSettingsManager.isAnalyticsEnabled(
					themeDisplay.getCompanyId()) &&
				hidePanel) {

				return false;
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}

		return super.isShow(httpServletRequest);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_portletNamespace = _portal.getPortletNamespace(
			SegmentsPortletKeys.SEGMENTS_EXPERIMENT);
	}

	private Map<String, Object> _getData(
			HttpServletRequest httpServletRequest, boolean panelStateOpen)
		throws Exception {

		return HashMapBuilder.<String, Object>put(
			"context",
			HashMapBuilder.<String, Object>put(
				"isPanelStateOpen", panelStateOpen
			).put(
				"namespace",
				_portal.getPortletNamespace(
					SegmentsPortletKeys.SEGMENTS_EXPERIMENT)
			).put(
				"segmentExperimentDataURL",
				_getSegmentExperimentDataURL(httpServletRequest)
			).build()
		).build();
	}

	private String _getRedirect(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		Layout draftLayout = _layoutLocalService.fetchDraftLayout(
			themeDisplay.getPlid());

		if (draftLayout == null) {
			return StringPool.BLANK;
		}

		String layoutFullURL = _portal.getLayoutFullURL(
			draftLayout, themeDisplay);

		String layoutURL = _portal.getLayoutURL(themeDisplay);

		long segmentsExperienceId = _getSegmentsExperienceId(
			httpServletRequest, themeDisplay);

		if (segmentsExperienceId != -1) {
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "segmentsExperienceId", segmentsExperienceId);
		}

		layoutFullURL = HttpComponentsUtil.setParameter(
			layoutFullURL, "p_l_back_url", layoutURL);

		layoutFullURL = HttpComponentsUtil.setParameter(
			layoutFullURL, "p_l_mode", Constants.EDIT);
		layoutFullURL = HttpComponentsUtil.setParameter(
			layoutFullURL, "redirect", layoutFullURL);

		return layoutFullURL;
	}

	private String _getSegmentExperimentDataURL(
			HttpServletRequest httpServletRequest)
		throws Exception {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String layoutURL = _portal.getLayoutURL(themeDisplay);

		long segmentsExperienceId = _getSegmentsExperienceId(
			httpServletRequest, themeDisplay);

		if (segmentsExperienceId != -1) {
			layoutURL = HttpComponentsUtil.setParameter(
				layoutURL, "segmentsExperienceId", segmentsExperienceId);
		}

		ResourceURL resourceURL = (ResourceURL)PortletURLBuilder.create(
			_portletURLFactory.create(
				httpServletRequest, SegmentsPortletKeys.SEGMENTS_EXPERIMENT,
				PortletRequest.RESOURCE_PHASE)
		).setRedirect(
			_getRedirect(httpServletRequest, themeDisplay)
		).setBackURL(
			layoutURL
		).setParameter(
			"backURLTitle",
			() -> {
				Layout layout = themeDisplay.getLayout();

				return layout.getName(themeDisplay.getLocale());
			}
		).setParameter(
			"plid", themeDisplay.getPlid()
		).setParameter(
			"segmentsExperienceId",
			_getSelectedSegmentsExperienceId(httpServletRequest)
		).buildPortletURL();

		resourceURL.setResourceID("/segments_experiment/get_data");

		return resourceURL.toString();
	}

	private long _getSegmentsExperienceId(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws Exception {

		SegmentsExperience segmentsExperience =
			_segmentsExperienceLocalService.fetchSegmentsExperience(
				_getSelectedSegmentsExperienceId(httpServletRequest));

		SegmentsExperiment segmentsExperiment =
			_segmentsExperimentService.fetchSegmentsExperiment(
				themeDisplay.getScopeGroupId(),
				segmentsExperience.getSegmentsExperienceKey(),
				themeDisplay.getPlid());

		if (segmentsExperiment != null) {
			return segmentsExperiment.getSegmentsExperienceId();
		}

		return segmentsExperience.getSegmentsExperienceId();
	}

	private long _getSelectedSegmentsExperienceId(
		HttpServletRequest httpServletRequest) {

		HttpServletRequest originalHttpServletRequest =
			_portal.getOriginalServletRequest(httpServletRequest);

		long segmentsExperienceId = ParamUtil.getLong(
			originalHttpServletRequest, "segmentsExperienceId", -1);

		if (segmentsExperienceId != -1) {
			return segmentsExperienceId;
		}

		SegmentsExperienceManager segmentsExperienceManager =
			new SegmentsExperienceManager(_segmentsExperienceLocalService);

		return segmentsExperienceManager.getSegmentsExperienceId(
			httpServletRequest);
	}

	private void _processBodyBottomTagBody(PageContext pageContext) {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			_portal.getLocale(httpServletRequest), getClass());

		pageContext.setAttribute("resourceBundle", resourceBundle);

		JspWriter jspWriter = pageContext.getOut();

		try {
			StringBundler sb = new StringBundler(20);

			sb.append("<div class=\"");

			boolean panelStateOpen = isPanelStateOpen(
				httpServletRequest,
				ProductNavigationControlMenuEntryConstants.SESSION_CLICKS_KEY);

			if (panelStateOpen) {
				sb.append(
					"lfr-has-segments-experiment-panel open-admin-panel open ");
			}

			sb.append("cadmin d-print-none lfr-admin-panel lfr-product-menu-");
			sb.append("panel lfr-segments-experiment-panel sidenav-fixed ");
			sb.append("sidenav-menu-slider sidenav-right\" id=\"");
			sb.append(_portletNamespace);
			sb.append("segmentsExperimentPanelId\" tabindex=\"-1\"><div class");
			sb.append("=\"sidebar sidebar-light sidenav-menu sidebar-sm\">");
			sb.append("<div class=\"lfr-segments-experiment-sidebar\" id=\"");
			sb.append("segmentsExperimentSidebar\"><div class=\"d-flex ");
			sb.append("justify-content-between p-3 sidebar-header\"><h1 class");
			sb.append("=\"sr-only\">");
			sb.append(_language.get(httpServletRequest, "tests-panel"));
			sb.append("</h1><span class=\"font-weight-bold\">");
			sb.append(_language.get(httpServletRequest, "tests"));
			sb.append("</span>");

			ButtonTag buttonTag = new ButtonTag();

			buttonTag.setCssClass("close sidenav-close");
			buttonTag.setDisplayType("unstyled");
			buttonTag.setDynamicAttribute(
				StringPool.BLANK, "aria-label",
				_language.get(
					(HttpServletRequest)pageContext.getRequest(), "close"));
			buttonTag.setIcon("times");

			sb.append(buttonTag.doTagAsString(pageContext));

			sb.append("</div><div class=\"sidebar-body\"><span aria-hidden=\"");
			sb.append("true\" className=\"loading-animation loading-");
			sb.append("animation-sm\" />");

			jspWriter.write(sb.toString());

			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{SegmentsExperimentApp} from segments-experiment-web"),
				_getData(httpServletRequest, panelStateOpen),
				httpServletRequest, jspWriter);

			jspWriter.write("</div></div></div></div>");
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private static final String _ICON_TMPL_CONTENT = StringUtil.read(
		SegmentsExperimentProductNavigationControlMenuEntry.class, "icon.tmpl");

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperimentProductNavigationControlMenuEntry.class);

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	private String _portletNamespace;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

	@Reference
	private SegmentsExperimentService _segmentsExperimentService;

}