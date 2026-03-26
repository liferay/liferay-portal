/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.cookies.banner.web.internal.constants.CookiesBannerWebKeys;
import com.liferay.cookies.banner.web.internal.display.context.CookiesPreferenceHandlingConfigurationDisplayContext;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.configuration.CookiesPreferenceHandlingConfiguration;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ImageLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Thiago Buarque
 */
@Component(service = ConfigurationFormRenderer.class)
public class CookiesPreferenceHandlingConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return CookiesPreferenceHandlingConfiguration.class.getName();
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		if (!ParamUtil.getBoolean(httpServletRequest, "enabled")) {
			return Map.of("enabled", false);
		}

		long companyId = _portal.getCompanyId(httpServletRequest);

		long customFloatingIconImageId =
			_cookiesConfigurationProvider.
				getCookiesPreferenceHandlingCustomFloatingIconImageId(
					_scope, companyId);

		if (FeatureFlagManagerUtil.isEnabled(companyId, "LPD-75027")) {
			long fileEntryId = ParamUtil.getLong(
				httpServletRequest, "fileEntryId");

			try {
				if (ParamUtil.getBoolean(httpServletRequest, "deleteLogo")) {
					_imageLocalService.deleteImage(customFloatingIconImageId);

					customFloatingIconImageId = 0;
				}
				else if (fileEntryId > 0) {
					FileEntry fileEntry = _dlAppLocalService.getFileEntry(
						fileEntryId);

					byte[] bytes = FileUtil.getBytes(
						fileEntry.getContentStream());

					Image image = null;

					if (customFloatingIconImageId > 0) {
						image = _imageLocalService.moveImage(
							customFloatingIconImageId, bytes);
					}
					else {
						image = _imageLocalService.updateImage(
							companyId, _counterLocalService.increment(), bytes);
					}

					customFloatingIconImageId = image.getImageId();
				}
			}
			catch (IOException | PortalException exception) {
				throw new RuntimeException(exception);
			}
		}

		return HashMapBuilder.<String, Object>put(
			"consentRenewalPeriod",
			ParamUtil.getInteger(httpServletRequest, "consentRenewalPeriod", 12)
		).put(
			"customFloatingIconImageId", customFloatingIconImageId
		).put(
			"dissentRenewalPeriod",
			ParamUtil.getInteger(httpServletRequest, "dissentRenewalPeriod", 12)
		).put(
			"enabled", true
		).put(
			"explicitConsentMode",
			ParamUtil.getBoolean(httpServletRequest, "explicitConsentMode")
		).put(
			"floatingIcon",
			ParamUtil.getString(httpServletRequest, "floatingIcon", "cookie")
		).put(
			"floatingIconEnabled",
			() -> {
				if (FeatureFlagManagerUtil.isEnabled(
						_portal.getCompanyId(httpServletRequest),
						"LPD-75027")) {

					return ParamUtil.getBoolean(
						httpServletRequest, "floatingIconEnabled");
				}

				return true;
			}
		).put(
			"modifiedDate",
			() -> {
				long modifiedDate = ParamUtil.getLong(
					httpServletRequest, "modifiedDate");

				if (modifiedDate <= 0) {
					return null;
				}

				return modifiedDate;
			}
		).put(
			"storeConsent",
			() -> {
				if (FeatureFlagManagerUtil.isEnabled(
						_portal.getCompanyId(httpServletRequest),
						"LPD-75032")) {

					return ParamUtil.getBoolean(
						httpServletRequest, "storeConsent");
				}

				return null;
			}
		).build();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			_render(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render /cookies_preference_handling_configuration" +
					"/view.jsp",
				exception);
		}
	}

	private void _render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/cookies_preference_handling_configuration/view.jsp");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		String portletId = PortalUtil.getPortletId(
			(PortletRequest)httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_PORTLET_REQUEST));

		if (portletId.equals(ConfigurationAdminPortletKeys.INSTANCE_SETTINGS)) {
			_scope = ExtendedObjectClassDefinition.Scope.COMPANY;

			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.
					COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT,
				new CookiesPreferenceHandlingConfigurationDisplayContext(
					_cookiesConfigurationProvider, _scope,
					themeDisplay.getCompanyId()));
		}
		else if (portletId.equals(
					ConfigurationAdminPortletKeys.SITE_SETTINGS)) {

			_scope = ExtendedObjectClassDefinition.Scope.GROUP;

			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.
					COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT,
				new CookiesPreferenceHandlingConfigurationDisplayContext(
					_cookiesConfigurationProvider, _scope,
					themeDisplay.getScopeGroupId()));
		}
		else {
			_scope = ExtendedObjectClassDefinition.Scope.SYSTEM;

			httpServletRequest.setAttribute(
				CookiesBannerWebKeys.
					COOKIES_PREFERENCE_HANDLING_CONFIGURATION_DISPLAY_CONTEXT,
				new CookiesPreferenceHandlingConfigurationDisplayContext(
					_cookiesConfigurationProvider, _scope, 0L));
		}

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference
	private CounterLocalService _counterLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private ImageLocalService _imageLocalService;

	@Reference
	private Portal _portal;

	private ExtendedObjectClassDefinition.Scope _scope;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.cookies.banner.web)"
	)
	private ServletContext _servletContext;

}