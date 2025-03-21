/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet.taglib;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionPreviewThreadLocal;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.taglib.util.HtmlTopTag;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(service = DynamicInclude.class)
public class CTPreviewIndicatorDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		if (!CTCollectionPreviewThreadLocal.isIndicatorEnabled()) {
			return;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		try {
			if (!_ctSettingsConfigurationHelper.isEnabled(
					themeDisplay.getCompanyId()) ||
				!PortletPermissionUtil.contains(
					themeDisplay.getPermissionChecker(),
					CTPortletKeys.PUBLICATIONS, ActionKeys.VIEW)) {

				return;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}

			return;
		}

		Writer writer = httpServletResponse.getWriter();

		HtmlTopTag htmlTopTag = new HtmlTopTag();

		htmlTopTag.setOutputKey("change_tracking_indicator_css");
		htmlTopTag.setPosition("auto");

		try {
			htmlTopTag.doBodyTag(
				httpServletRequest, httpServletResponse,
				pageContext -> {
					try {
						writer.write("<link href=\"");
						writer.write(
							_portal.getStaticResourceURL(
								httpServletRequest,
								StringBundler.concat(
									_servletContext.getContextPath(),
									"/publications/css",
									"/ChangeTrackingIndicator.css")));
						writer.write(
							"\" rel=\"stylesheet\" type=\"text/css\" />");
					}
					catch (IOException ioException) {
						ReflectionUtil.throwException(ioException);
					}
				});

			writer.write(
				StringBundler.concat(
					"<nav aria-label=\"",
					_language.get(httpServletRequest, "control-menu"),
					"\" class=\"cadmin control-menu-container\"><div ",
					"class=\"change-tracking-indicator\"><div>",
					"<button class=\"change-tracking-indicator-button\">",
					"<span className=\"change-tracking-indicator-title\">"));

			CTCollection previewCTCollection =
				_ctCollectionLocalService.fetchCTCollection(
					CTCollectionThreadLocal.getCTCollectionId());

			if (previewCTCollection == null) {
				writer.write(
					_language.get(
						themeDisplay.getLocale(), "viewing-production"));
			}
			else {
				writer.write(
					_language.format(
						themeDisplay.getLocale(), "viewing-x",
						previewCTCollection.getName()));
			}

			writer.write("</span></button></div>");

			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{ChangeTrackingIndicator} from change-tracking-web",
					_portal.getPortletNamespace(CTPortletKeys.PUBLICATIONS) +
						"IndicatorComponent",
					null, true),
				_getReactData(
					httpServletRequest, previewCTCollection, themeDisplay),
				httpServletRequest, writer);

			writer.write("</div></nav>");
		}
		catch (JspException | PortalException exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/body_top.jsp#post");
	}

	private Map<String, Object> _getReactData(
			HttpServletRequest httpServletRequest, CTCollection ctCollection,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Map<String, Object> data = HashMapBuilder.<String, Object>put(
			"namespace", _portal.getPortletNamespace(CTPortletKeys.PUBLICATIONS)
		).build();

		if (ctCollection != null) {
			data.put("iconClass", "change-tracking-indicator-icon-publication");
			data.put("iconName", "radio-button");
			data.put(
				"title",
				_language.format(
					themeDisplay.getLocale(), "viewing-x",
					ctCollection.getName(), false));
		}
		else {
			data.put("iconClass", "change-tracking-indicator-icon-production");
			data.put("iconName", "simple-circle");
			data.put(
				"title",
				_language.get(themeDisplay.getLocale(), "viewing-production"));
		}

		CTPreferences ctPreferences =
			_ctPreferencesLocalService.fetchCTPreferences(
				themeDisplay.getCompanyId(), themeDisplay.getUserId());

		if (ctPreferences != null) {
			Layout layout = themeDisplay.getLayout();

			long originalCTCollectionId =
				CTCollectionPreviewThreadLocal.getCTCollectionId();

			try {
				CTCollectionPreviewThreadLocal.setCTCollectionId(-1);

				String url = _portal.getLayoutFriendlyURL(layout, themeDisplay);

				long segmentsExperienceId = ParamUtil.getLong(
					httpServletRequest, "segmentsExperienceId");

				if (segmentsExperienceId > 0) {
					url = HttpComponentsUtil.addParameter(
						url, "segmentsExperienceId", segmentsExperienceId);
				}

				CTCollection originalCTCollection =
					_ctCollectionLocalService.fetchCTCollection(
						ctPreferences.getCtCollectionId());

				data.put(
					"returnToPublicationDropdownItem",
					JSONUtil.put(
						"href", url
					).put(
						"label",
						_language.format(
							themeDisplay.getLocale(), "work-on-x",
							originalCTCollection.getName(), false)
					).put(
						"symbolLeft", "radio-button"
					));
			}
			finally {
				CTCollectionPreviewThreadLocal.setCTCollectionId(
					originalCTCollectionId);
			}
		}

		return data;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTPreviewIndicatorDynamicInclude.class);

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.change.tracking.web)"
	)
	private ServletContext _servletContext;

}