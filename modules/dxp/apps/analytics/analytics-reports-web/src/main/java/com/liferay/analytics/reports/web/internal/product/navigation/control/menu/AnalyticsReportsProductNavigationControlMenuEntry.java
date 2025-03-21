/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.product.navigation.control.menu;

import com.liferay.analytics.reports.constants.AnalyticsReportsWebKeys;
import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItem;
import com.liferay.analytics.reports.info.item.AnalyticsReportsInfoItemRegistry;
import com.liferay.analytics.reports.info.item.ClassNameClassPKInfoItemIdentifier;
import com.liferay.analytics.reports.info.item.provider.AnalyticsReportsInfoItemObjectProvider;
import com.liferay.analytics.reports.web.internal.constants.AnalyticsReportsPortletKeys;
import com.liferay.analytics.reports.web.internal.constants.ProductNavigationControlMenuEntryConstants;
import com.liferay.analytics.reports.web.internal.info.item.provider.util.AnalyticsReportsInfoItemObjectProviderRegistryUtil;
import com.liferay.analytics.reports.web.internal.util.AnalyticsReportsUtil;
import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.frontend.taglib.clay.servlet.taglib.ButtonTag;
import com.liferay.frontend.taglib.clay.servlet.taglib.IconTag;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
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
import com.liferay.taglib.util.BodyBottomTag;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;
import java.io.Writer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sarai Díaz
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=400"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class AnalyticsReportsProductNavigationControlMenuEntry
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

		bodyBottomTag.setOutputKey("analyticsReportsPanel");

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

		Writer writer = httpServletResponse.getWriter();

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
					() -> {
						IconTag iconTag = new IconTag();

						iconTag.setCssClass("icon-monospaced");
						iconTag.setSymbol("analytics");

						return iconTag.doTagAsString(
							httpServletRequest, httpServletResponse);
					}
				).put(
					"nonceAttribute",
					ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
						httpServletRequest)
				).put(
					"portletNamespace", _portletNamespace
				).put(
					"title",
					() -> {
						ResourceBundle resourceBundle =
							ResourceBundleUtil.getBundle(
								_portal.getLocale(httpServletRequest),
								getClass());

						return HtmlUtil.escape(
							_language.get(
								resourceBundle, "content-performance"));
					}
				).build()));

		return true;
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		InfoItemReference infoItemReference = _getInfoItemReference(
			httpServletRequest);

		AnalyticsReportsInfoItemObjectProvider<Object>
			analyticsReportsInfoItemObjectProvider =
				(AnalyticsReportsInfoItemObjectProvider<Object>)
					AnalyticsReportsInfoItemObjectProviderRegistryUtil.
						getAnalyticsReportsInfoItemObjectProvider(
							infoItemReference.getClassName());

		if (analyticsReportsInfoItemObjectProvider == null) {
			return false;
		}

		Object analyticsReportsInfoItemObject =
			analyticsReportsInfoItemObjectProvider.
				getAnalyticsReportsInfoItemObject(infoItemReference);

		if (analyticsReportsInfoItemObject == null) {
			return false;
		}

		AnalyticsReportsInfoItem<Object> analyticsReportsInfoItem =
			(AnalyticsReportsInfoItem<Object>)
				_analyticsReportsInfoItemRegistry.getAnalyticsReportsInfoItem(
					infoItemReference.getClassName());
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if ((analyticsReportsInfoItem == null) ||
			(!analyticsReportsInfoItem.isShow(analyticsReportsInfoItemObject) &&
			 !_hasResourcePermission(
				 ActionKeys.UPDATE, httpServletRequest.getParameter("p_l_id"),
				 _resourceNames, themeDisplay))) {

			return false;
		}

		try {
			if (!AnalyticsReportsUtil.isShowAnalyticsReportsPanel(
					_analyticsSettingsManager, themeDisplay.getCompanyId(),
					httpServletRequest)) {

				return false;
			}
		}
		catch (PortalException portalException) {
			throw portalException;
		}
		catch (Exception exception) {
			_log.error(exception);

			return false;
		}

		return super.isShow(httpServletRequest);
	}

	@Activate
	protected void activate() {
		_portletNamespace = _portal.getPortletNamespace(
			AnalyticsReportsPortletKeys.ANALYTICS_REPORTS);
	}

	private String _getAnalyticsReportsURL(
		HttpServletRequest httpServletRequest) {

		InfoItemReference infoItemReference = _getInfoItemReference(
			httpServletRequest);

		if (infoItemReference.getInfoItemIdentifier() instanceof
				ClassNameClassPKInfoItemIdentifier) {

			ClassNameClassPKInfoItemIdentifier
				classNameClassPKInfoItemIdentifier =
					(ClassNameClassPKInfoItemIdentifier)
						infoItemReference.getInfoItemIdentifier();

			return PortletURLBuilder.create(
				_portletURLFactory.create(
					httpServletRequest,
					AnalyticsReportsPortletKeys.ANALYTICS_REPORTS,
					PortletRequest.RESOURCE_PHASE)
			).setParameter(
				"className", infoItemReference.getClassName()
			).setParameter(
				"classPK", classNameClassPKInfoItemIdentifier.getClassPK()
			).setParameter(
				"classTypeName",
				classNameClassPKInfoItemIdentifier.getClassName()
			).setParameter(
				"p_p_resource_id", "/analytics_reports/get_data"
			).buildString();
		}
		else if (infoItemReference.getInfoItemIdentifier() instanceof
					ClassPKInfoItemIdentifier) {

			return PortletURLBuilder.create(
				_portletURLFactory.create(
					httpServletRequest,
					AnalyticsReportsPortletKeys.ANALYTICS_REPORTS,
					PortletRequest.RESOURCE_PHASE)
			).setParameter(
				"className", infoItemReference.getClassName()
			).setParameter(
				"classPK",
				() -> {
					ClassPKInfoItemIdentifier classPKInfoItemIdentifier =
						(ClassPKInfoItemIdentifier)
							infoItemReference.getInfoItemIdentifier();

					return classPKInfoItemIdentifier.getClassPK();
				}
			).setParameter(
				"p_p_resource_id", "/analytics_reports/get_data"
			).buildString();
		}

		return StringPool.BLANK;
	}

	private InfoItemReference _getInfoItemReference(
		HttpServletRequest httpServletRequest) {

		InfoItemReference infoItemReference =
			(InfoItemReference)httpServletRequest.getAttribute(
				AnalyticsReportsWebKeys.ANALYTICS_INFO_ITEM_REFERENCE);

		if (infoItemReference == null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return new InfoItemReference(
				Layout.class.getName(), themeDisplay.getPlid());
		}

		return infoItemReference;
	}

	private List<String> _getResourceNames(
		String portletId, Map<String, List<String>> resourceNames) {

		for (Map.Entry<String, List<String>> entry : resourceNames.entrySet()) {
			if (portletId.contains(entry.getKey())) {
				return entry.getValue();
			}
		}

		return Collections.emptyList();
	}

	private boolean _hasResourcePermission(
		String actionId, String plid, Map<String, List<String>> resourceNames,
		ThemeDisplay themeDisplay) {

		if (!themeDisplay.isSignedIn()) {
			return false;
		}

		if (Validator.isNotNull(plid)) {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			Set<String> resourceNamesSet = new HashSet<>();

			List<PortletPreferences> portletPreferencesList =
				_portletPreferencesLocalService.getPortletPreferencesByPlid(
					GetterUtil.getLong(plid));

			for (PortletPreferences portletPreferences :
					portletPreferencesList) {

				resourceNamesSet.addAll(
					_getResourceNames(
						portletPreferences.getPortletId(), resourceNames));
			}

			for (String resourceName : resourceNamesSet) {
				if (permissionChecker.hasPermission(
						themeDisplay.getScopeGroupId(), resourceName, "0",
						actionId)) {

					return true;
				}
			}
		}

		return false;
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

			sb.append("<div class=\"");

			if (isPanelStateOpen(
					httpServletRequest,
					ProductNavigationControlMenuEntryConstants.
						SESSION_CLICKS_KEY)) {

				sb.append(
					"lfr-has-analytics-reports-panel open-admin-panel open ");
			}

			sb.append(
				StringBundler.concat(
					"cadmin d-print-none lfr-admin-panel ",
					"lfr-product-menu-panel lfr-analytics-reports-panel ",
					"sidenav-fixed sidenav-menu-slider sidenav-right\" id=\""));
			sb.append(_portletNamespace);
			sb.append("analyticsReportsPanelId\" ");
			sb.append("tabindex=\"-1\">");
			sb.append("<div class=\"sidebar sidebar-light sidenav-menu ");
			sb.append("sidebar-sm\">");
			sb.append("<div class=\"lfr-analytics-reports-sidebar\" ");
			sb.append("id=\"analyticsReportsSidebar\">");
			sb.append("<div class=\"d-flex justify-content-between p-3 ");
			sb.append("sidebar-header\">");
			sb.append("<h1 class=\"sr-only\">");
			sb.append(_language.get(httpServletRequest, "content-performance"));
			sb.append("</h1>");
			sb.append("<span class=\"font-weight-bold\">");
			sb.append(_language.get(httpServletRequest, "content-performance"));
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

			sb.append("</div>");
			sb.append("<div class=\"sidebar-body\">");
			sb.append("<span aria-hidden=\"true\" ");
			sb.append("className=\"loading-animation ");
			sb.append("loading-animation-sm\" />");

			jspWriter.write(sb.toString());

			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{AnalyticsReportsApp} from analytics-reports-web"),
				HashMapBuilder.<String, Object>put(
					"context",
					HashMapBuilder.<String, Object>put(
						"analyticsReportsDataURL",
						_getAnalyticsReportsURL(httpServletRequest)
					).put(
						"isPanelStateOpen",
						isPanelStateOpen(
							httpServletRequest,
							ProductNavigationControlMenuEntryConstants.
								SESSION_CLICKS_KEY)
					).build()
				).put(
					"portletNamespace", _portletNamespace
				).build(),
				httpServletRequest, jspWriter);

			jspWriter.write("</div></div></div></div>");
		}
		catch (Exception exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	private static final String _ICON_TMPL_CONTENT = StringUtil.read(
		AnalyticsReportsProductNavigationControlMenuEntry.class, "icon.tmpl");

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsReportsProductNavigationControlMenuEntry.class);

	@Reference
	private AnalyticsReportsInfoItemRegistry _analyticsReportsInfoItemRegistry;

	@Reference
	private AnalyticsSettingsManager _analyticsSettingsManager;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	private String _portletNamespace;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private PortletURLFactory _portletURLFactory;

	@Reference
	private ReactRenderer _reactRenderer;

	private final Map<String, List<String>> _resourceNames =
		HashMapBuilder.<String, List<String>>put(
			"com_liferay_blogs_web_portlet_BlogsPortlet",
			Arrays.asList("com.liferay.blogs.model.BlogsEntry")
		).put(
			"com_liferay_document_library_web_portlet_DLPortlet",
			Arrays.asList(
				"com.liferay.document.library",
				"com.liferay.document.library.kernel.model.DLFileEntry")
		).put(
			"com_liferay_journal_content_web_portlet_JournalContentPortlet",
			Arrays.asList(
				"com.liferay.journal",
				"com.liferay.journal.model.JournalArticle")
		).build();

}