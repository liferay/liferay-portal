/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.struts;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.info.field.InfoFieldValue;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.layout.reports.web.internal.configuration.LayoutReportsGooglePageSpeedGroupConfiguration;
import com.liferay.layout.reports.web.internal.configuration.provider.LayoutReportsGooglePageSpeedConfigurationProvider;
import com.liferay.layout.reports.web.internal.data.provider.LayoutReportsDataProvider;
import com.liferay.layout.seo.canonical.url.LayoutSEOCanonicalURLProvider;
import com.liferay.layout.seo.kernel.LayoutSEOLink;
import com.liferay.layout.seo.kernel.LayoutSEOLinkManager;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "path=/layout_reports/get_google_page_speed_data",
	service = StrutsAction.class
)
public class GetLayoutReportsGooglePageSpeedDataStrutsAction
	implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Layout layout = _layoutLocalService.fetchLayout(
			ParamUtil.getLong(httpServletRequest, "p_l_id"));

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		ServletResponseUtil.write(
			httpServletResponse,
			JSONUtil.put(
				"configureGooglePageSpeedURL",
				_getConfigureGooglePageSpeedURL(httpServletRequest)
			).put(
				"defaultLanguageId",
				LocaleUtil.toW3cLanguageId(_getDefaultLocale(layout))
			).put(
				"imagesPath",
				_portal.getPathContext(_servletContext.getContextPath()) +
					"/images/"
			).put(
				"pageURLs", _getPageURLsJSONArray(httpServletRequest, layout)
			).put(
				"privateLayout", layout.isPrivateLayout()
			).put(
				"validConnection",
				() -> {
					LayoutReportsDataProvider layoutReportsDataProvider =
						new LayoutReportsDataProvider(
							_layoutReportsGooglePageSpeedConfigurationProvider.
								getApiKey(themeDisplay.getScopeGroup()),
							_layoutReportsGooglePageSpeedConfigurationProvider.
								getStrategy(themeDisplay.getScopeGroup()));

					return layoutReportsDataProvider.isValidConnection();
				}
			).toString());

		return null;
	}

	private String _getCanonicalURL(
		String canonicalURL, Layout layout, Locale locale,
		ThemeDisplay themeDisplay) {

		try {
			LayoutSEOLink layoutSEOLink =
				_layoutSEOLinkManager.getCanonicalLayoutSEOLink(
					layout, locale, canonicalURL, themeDisplay);

			return layoutSEOLink.getHref();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return canonicalURL;
		}
	}

	private String _getCanonicalURL(
		String currentCompleteURL, Layout layout, ThemeDisplay themeDisplay) {

		try {
			return _portal.getCanonicalURL(
				currentCompleteURL, themeDisplay, layout, false, false);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return StringPool.BLANK;
	}

	private String _getCompleteURL(HttpServletRequest httpServletRequest) {
		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return _portal.getLayoutURL(themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return _portal.getCurrentCompleteURL(httpServletRequest);
		}
	}

	private String _getConfigureGooglePageSpeedURL(
		HttpServletRequest httpServletRequest) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (!_isGroupAdmin(themeDisplay.getScopeGroupId())) {
			return null;
		}

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, ConfigurationAdminPortletKeys.SITE_SETTINGS,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/configuration_admin/edit_configuration"
		).setRedirect(
			_getCompleteURL(httpServletRequest)
		).setParameter(
			"factoryPid",
			LayoutReportsGooglePageSpeedGroupConfiguration.class.getName()
		).setParameter(
			"pid",
			LayoutReportsGooglePageSpeedGroupConfiguration.class.getName()
		).buildString();
	}

	private Locale _getDefaultLocale(Layout layout) {
		try {
			return _portal.getSiteDefaultLocale(layout.getGroupId());
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return LocaleUtil.getSiteDefault();
		}
	}

	private String _getLayoutReportsIssuesURL(
		long groupId, String url, ThemeDisplay themeDisplay) {

		return HttpComponentsUtil.addParameters(
			themeDisplay.getPortalURL() + themeDisplay.getPathMain() +
				"/layout_reports/get_layout_reports_issues",
			"groupId", String.valueOf(groupId), "url", url);
	}

	private String _getLocaleURL(
		String canonicalURL, Locale defaultLocale, Layout layout, Locale locale,
		ThemeDisplay themeDisplay) {

		if (defaultLocale.equals(locale)) {
			return _getCanonicalURL(canonicalURL, layout, locale, themeDisplay);
		}

		try {
			return _layoutSEOCanonicalURLProvider.getCanonicalURL(
				layout, locale, canonicalURL, themeDisplay);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return canonicalURL;
		}
	}

	private JSONArray _getPageURLsJSONArray(
		HttpServletRequest httpServletRequest, Layout layout) {

		List<Locale> availableLocales = new ArrayList<>();

		Group group = _groupLocalService.fetchGroup(layout.getGroupId());

		if (group != null) {
			availableLocales.addAll(
				_language.getAvailableLocales(group.getGroupId()));
		}

		Locale defaultLocale = _getDefaultLocale(layout);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String canonicalURL = _getCanonicalURL(
			_getCompleteURL(httpServletRequest), layout, themeDisplay);

		return JSONUtil.putAll(
			(Object[])TransformUtil.transformToArray(
				ListUtil.sort(
					availableLocales,
					(locale1, locale2) -> {
						if (Objects.equals(locale1, defaultLocale)) {
							return -1;
						}

						if (Objects.equals(locale2, defaultLocale)) {
							return 1;
						}

						Locale locale = themeDisplay.getLocale();

						String displayLanguage1 = locale1.getDisplayLanguage(
							locale);
						String displayLanguage2 = locale2.getDisplayLanguage(
							locale);

						if (StringUtil.equalsIgnoreCase(
								displayLanguage1, displayLanguage2)) {

							return -1;
						}

						return 1;
					}),
				locale -> {
					String url = _getLocaleURL(
						canonicalURL, defaultLocale, layout, locale,
						themeDisplay);

					return HashMapBuilder.<String, Object>put(
						"languageId", LocaleUtil.toW3cLanguageId(locale)
					).put(
						"languageLabel",
						StringBundler.concat(
							locale.getDisplayLanguage(themeDisplay.getLocale()),
							StringPool.SPACE, StringPool.OPEN_PARENTHESIS,
							locale.getDisplayCountry(themeDisplay.getLocale()),
							StringPool.CLOSE_PARENTHESIS)
					).put(
						"layoutReportsIssuesURL",
						_getLayoutReportsIssuesURL(
							layout.getGroupId(), url, themeDisplay)
					).put(
						"title", _getTitle(httpServletRequest, layout, locale)
					).put(
						"url", url
					).build();
				},
				Object.class));
	}

	private String _getTitle(
		HttpServletRequest httpServletRequest, Layout layout, Locale locale) {

		if (layout.isTypeAssetDisplay()) {
			InfoItemDetails infoItemDetails =
				(InfoItemDetails)httpServletRequest.getAttribute(
					InfoDisplayWebKeys.INFO_ITEM_DETAILS);

			if (infoItemDetails != null) {
				InfoItemFieldValuesProvider infoItemFieldValuesProvider =
					_infoItemServiceRegistry.getFirstInfoItemService(
						InfoItemFieldValuesProvider.class,
						infoItemDetails.getClassName());

				if (infoItemFieldValuesProvider != null) {
					InfoFieldValue<Object> infoFieldValue =
						infoItemFieldValuesProvider.getInfoFieldValue(
							httpServletRequest.getAttribute(
								InfoDisplayWebKeys.INFO_ITEM),
							"title");

					if (infoFieldValue != null) {
						String value = (String)infoFieldValue.getValue(locale);

						if (value != null) {
							return value;
						}
					}
				}
			}

			return StringPool.BLANK;
		}
		else if (layout.isTypeContent() || layout.isTypePortlet()) {
			String title = layout.getTitle(locale);

			if (Validator.isNotNull(title)) {
				return title;
			}

			return layout.getName(locale);
		}

		return StringPool.BLANK;
	}

	private boolean _isGroupAdmin(long groupId) {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return permissionChecker.isGroupAdmin(groupId);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GetLayoutReportsGooglePageSpeedDataStrutsAction.class);

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutReportsGooglePageSpeedConfigurationProvider
		_layoutReportsGooglePageSpeedConfigurationProvider;

	@Reference
	private LayoutSEOCanonicalURLProvider _layoutSEOCanonicalURLProvider;

	@Reference
	private LayoutSEOLinkManager _layoutSEOLinkManager;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.reports.web)"
	)
	private ServletContext _servletContext;

}