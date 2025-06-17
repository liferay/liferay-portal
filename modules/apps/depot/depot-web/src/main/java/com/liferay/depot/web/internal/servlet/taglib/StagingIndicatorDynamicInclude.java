/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.servlet.taglib;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.depot.web.internal.constants.DepotPortletKeys;
import com.liferay.depot.web.internal.util.StagingIndicatorUtil;
import com.liferay.exportimport.kernel.staging.Staging;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.site.provider.GroupURLProvider;
import com.liferay.taglib.util.HtmlTopTag;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;

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
 * @author Adolfo Pérez
 */
@Component(service = DynamicInclude.class)
public class StagingIndicatorDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		try {
			if (StagingIndicatorUtil.isShowStagingIndicator(
					httpServletRequest)) {

				ThemeDisplay themeDisplay =
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				_includeStagingIndicator(
					httpServletRequest, httpServletResponse, themeDisplay);
			}
		}
		catch (JspException jspException) {
			ReflectionUtil.throwException(jspException);
		}
		catch (PortalException | PortletException exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.product.navigation.taglib#/page.jsp#pre");
	}

	private <T> JSONArray _createJSONArray(T... values) {
		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (T value : values) {
			if (value != null) {
				jsonArray.put(value);
			}
		}

		return jsonArray;
	}

	private String _getDepotDashboardGroupURL(
			Group group, HttpServletRequest httpServletRequest)
		throws PortalException {

		if (_depotEntryLocalService == null) {
			return null;
		}

		DepotEntry depotEntry = _depotEntryLocalService.getGroupDepotEntry(
			group.getGroupId());

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, group, DepotPortletKeys.DEPOT_ADMIN, 0, 0,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/depot/view_depot_dashboard"
		).setParameter(
			"depotEntryId", depotEntry.getDepotEntryId()
		).buildString();
	}

	private JSONObject _getLiveGroupItemJSONObject(
		HttpServletRequest httpServletRequest, Group scopeGroup,
		String liveGroupURL) {

		JSONObject publishToLiveItemJSONObject = JSONUtil.put(
			"href", liveGroupURL
		).put(
			"label", _language.get(httpServletRequest, _getLiveKey(scopeGroup))
		).put(
			"symbolLeft", "radio-button"
		).put(
			"symbolRight", _getSymbolRight(liveGroupURL)
		);

		if (Validator.isNull(liveGroupURL)) {
			publishToLiveItemJSONObject.put(
				"className", "lfr-portal-tooltip"
			).put(
				"title",
				_language.get(
					ResourceBundleUtil.getBundle(
						"content.Language",
						_portal.getLocale(httpServletRequest), getClass()),
					"the-connection-to-the-remote-live-asset-library-cannot-" +
						"be-established-due-to-a-network-problem")
			);
		}

		return publishToLiveItemJSONObject;
	}

	private String _getLiveGroupURL(
			Group group, HttpServletRequest httpServletRequest)
		throws PortalException {

		if (group.isStagedRemotely()) {
			return _staging.getRemoteSiteURL(group, false);
		}
		else if (group.isStagingGroup()) {
			Group liveGroup = _staging.getLiveGroup(group.getGroupId());

			if (liveGroup != null) {
				return _groupURLProvider.getLiveGroupURL(
					liveGroup,
					(PortletRequest)httpServletRequest.getAttribute(
						JavaConstants.JAKARTA_PORTLET_REQUEST));
			}
		}

		return null;
	}

	private String _getLiveKey(Group group) {
		if (group.isStagedRemotely()) {
			return "remote-live";
		}

		return "live";
	}

	private JSONObject _getPublishToLiveItemJSONObject(
			HttpServletRequest httpServletRequest, Group stagingGroupId)
		throws PortalException, PortletException {

		if (!GroupPermissionUtil.contains(
				PermissionThreadLocal.getPermissionChecker(), stagingGroupId,
				ActionKeys.PUBLISH_STAGING)) {

			return null;
		}

		return JSONUtil.put(
			"action", "publishToLive"
		).put(
			"label", _language.get(httpServletRequest, "publish-to-live")
		).put(
			"publishURL",
			_getPublishToLiveURL(stagingGroupId, httpServletRequest)
		).put(
			"symbolLeft", "cards2"
		);
	}

	private String _getPublishToLiveURL(
			Group group, HttpServletRequest httpServletRequest)
		throws PortletException {

		LiferayPortletURL liferayPortletURL = PortletURLFactoryUtil.create(
			httpServletRequest, PortletKeys.EXPORT_IMPORT,
			PortletRequest.RENDER_PHASE);

		liferayPortletURL.setParameter(
			"mvcRenderCommandName", "/export_import/publish_layouts_simple");

		String cmd = Constants.PUBLISH_TO_LIVE;

		if (group.isStagedRemotely()) {
			cmd = Constants.PUBLISH_TO_REMOTE;
		}

		liferayPortletURL.setParameter(Constants.CMD, cmd);
		liferayPortletURL.setParameter(
			"groupId", String.valueOf(group.getGroupId()));
		liferayPortletURL.setParameter(
			"localPublishing", String.valueOf(!group.isStagedRemotely()));
		liferayPortletURL.setParameter("quickPublish", Boolean.TRUE.toString());
		liferayPortletURL.setParameter(
			"remoteAddress", group.getTypeSettingsProperty("remoteAddress"));
		liferayPortletURL.setParameter(
			"remotePort", group.getTypeSettingsProperty("remotePort"));
		liferayPortletURL.setParameter(
			"remotePathContext",
			group.getTypeSettingsProperty("remotePathContext"));
		liferayPortletURL.setParameter(
			"remoteGroupId", group.getTypeSettingsProperty("remoteGroupId"));
		liferayPortletURL.setParameter(
			"secureConnection",
			group.getTypeSettingsProperty("secureConnection"));
		liferayPortletURL.setParameter(
			"secureConnection",
			group.getTypeSettingsProperty("secureConnection"));
		liferayPortletURL.setParameter(
			"sourceGroupId", String.valueOf(group.getGroupId()));

		Group liveGroup = _staging.getLiveGroup(group.getGroupId());

		liferayPortletURL.setParameter(
			"targetGroupId", String.valueOf(liveGroup.getGroupId()));

		liferayPortletURL.setWindowState(LiferayWindowState.POP_UP);

		return liferayPortletURL.toString();
	}

	private Map<String, Object> _getReactData(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws PortalException, PortletException {

		Group scopeGroup = themeDisplay.getScopeGroup();

		String liveGroupURL = null;

		try {
			liveGroupURL = _getLiveGroupURL(scopeGroup, httpServletRequest);
		}
		catch (SystemException systemException) {
			_log.error(systemException);
		}

		if (Validator.isNotNull(liveGroupURL) ||
			scopeGroup.isStagedRemotely()) {

			return HashMapBuilder.<String, Object>put(
				"iconClass", "staging-indicator-icon-staging"
			).put(
				"iconName", "radio-button"
			).put(
				"items",
				_createJSONArray(
					_getLiveGroupItemJSONObject(
						httpServletRequest, scopeGroup, liveGroupURL),
					_getPublishToLiveItemJSONObject(
						httpServletRequest, scopeGroup))
			).put(
				"title", _language.get(httpServletRequest, "staging")
			).build();
		}

		return HashMapBuilder.<String, Object>put(
			"iconClass", "staging-indicator-icon-live"
		).put(
			"iconName", "simple-circle"
		).put(
			"items",
			_createJSONArray(
				JSONUtil.put(
					"href",
					_getDepotDashboardGroupURL(
						scopeGroup.getStagingGroup(), httpServletRequest)
				).put(
					"label", _language.get(httpServletRequest, "staging")
				).put(
					"symbolLeft", "simple-circle"
				))
		).put(
			"title", _language.get(httpServletRequest, "live")
		).build();
	}

	private String _getSymbolRight(String liveGroupURL) {
		if (Validator.isNull(liveGroupURL)) {
			return "exclamation-full";
		}

		return null;
	}

	private void _includeStagingIndicator(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, ThemeDisplay themeDisplay)
		throws IOException, JspException, PortalException, PortletException {

		Writer writer = httpServletResponse.getWriter();

		HtmlTopTag htmlTopTag = new HtmlTopTag();

		htmlTopTag.setOutputKey("staging_indicator_css");
		htmlTopTag.setPosition("auto");

		htmlTopTag.doBodyTag(
			httpServletRequest, httpServletResponse,
			pageContext -> {
				try {
					writer.write("<link href=\"");
					writer.write(
						_portal.getStaticResourceURL(
							httpServletRequest,
							_servletContext.getContextPath() +
								"/dynamic_include/StagingIndicator.css"));
					writer.write(StringPool.QUOTE);
					writer.write(
						ContentSecurityPolicyNonceProviderUtil.getNonce(
							httpServletRequest));
					writer.write(" rel=\"stylesheet\" type=\"text/css\" />");
				}
				catch (IOException ioException) {
					ReflectionUtil.throwException(ioException);
				}
			});

		writer.write(
			"<div class=\"staging-indicator\"><div>" +
				"<button class=\"staging-indicator-button\">" +
					"<span className=\"staging-indicator-title\">");

		Group scopeGroup = themeDisplay.getScopeGroup();

		if (scopeGroup.isStagingGroup() || scopeGroup.isStagedRemotely()) {
			writer.write(_language.get(httpServletRequest, "staging"));
		}
		else {
			writer.write(_language.get(httpServletRequest, "live"));
		}

		writer.write("</span></button></div>");

		String componentId =
			_portal.getPortletNamespace(DepotPortletKeys.DEPOT_ADMIN) +
				"IndicatorComponent";

		_reactRenderer.renderReact(
			new ComponentDescriptor(
				"{StagingIndicator} from depot-web", componentId),
			_getReactData(httpServletRequest, themeDisplay), httpServletRequest,
			writer);

		writer.write("</div>");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StagingIndicatorDynamicInclude.class);

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.depot.web)")
	private ServletContext _servletContext;

	@Reference
	private Staging _staging;

}