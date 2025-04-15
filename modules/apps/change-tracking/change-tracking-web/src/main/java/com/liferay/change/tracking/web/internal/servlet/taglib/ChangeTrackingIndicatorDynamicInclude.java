/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.servlet.taglib;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.model.CTRemote;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.service.CTRemoteLocalService;
import com.liferay.change.tracking.spi.constants.CTTimelineKeys;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRendererRegistry;
import com.liferay.change.tracking.web.internal.configuration.CTConfiguration;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTPermission;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;
import com.liferay.taglib.util.HtmlTopTag;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

import java.io.IOException;
import java.io.Writer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	configurationPid = "com.liferay.change.tracking.web.internal.configuration.CTConfiguration",
	service = DynamicInclude.class
)
public class ChangeTrackingIndicatorDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		try {
			if (!_ctSettingsConfigurationHelper.isEnabled(
					themeDisplay.getCompanyId()) ||
				user.isOnDemandUser() ||
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
						writer.write(StringPool.QUOTE);
						writer.write(
							ContentSecurityPolicyNonceProviderUtil.getNonce(
								httpServletRequest));
						writer.write(" rel=\"stylesheet\" type=\"text/css\"/>");
					}
					catch (IOException ioException) {
						ReflectionUtil.throwException(ioException);
					}
				});

			writer.write(
				"<div class=\"change-tracking-indicator\"><div>" +
					"<button class=\"change-tracking-indicator-button\">" +
						"<span className=\"change-tracking-indicator-title\">");

			CTCollection ctCollection = null;

			CTPreferences ctPreferences =
				_ctPreferencesLocalService.fetchCTPreferences(
					themeDisplay.getCompanyId(), themeDisplay.getUserId());

			if (ctPreferences != null) {
				ctCollection = _ctCollectionLocalService.fetchCTCollection(
					ctPreferences.getCtCollectionId());
			}

			if (ctCollection == null) {
				writer.write(
					_language.get(themeDisplay.getLocale(), "production"));
			}
			else {
				writer.write(HtmlUtil.escape(ctCollection.getName()));
			}

			writer.write("</span></button></div>");

			CTConfiguration ctConfiguration = _getCTConfiguration(
				themeDisplay.getCompanyId());
			String portletId = ParamUtil.getString(
				httpServletRequest, "p_p_id");

			_reactRenderer.renderReact(
				new ComponentDescriptor(
					"{ChangeTrackingIndicator} from change-tracking-web",
					_portal.getPortletNamespace(CTPortletKeys.PUBLICATIONS) +
						"IndicatorComponent",
					null, true),
				_getReactData(
					httpServletRequest, ctCollection, ctPreferences,
					Validator.isNotNull(portletId) &&
					ArrayUtil.contains(
						ctConfiguration.productionOnlyApplication(), portletId),
					_ctSettingsConfigurationHelper.isSandboxEnabled(
						themeDisplay.getCompanyId()),
					_isShowContextChangePopover(
						httpServletRequest, themeDisplay),
					themeDisplay,
					Validator.isNotNull(portletId) &&
					ArrayUtil.contains(
						ctConfiguration.unsupportedApplication(), portletId)),
				httpServletRequest, writer);

			writer.write("</div>");
		}
		catch (JspException | PortalException exception) {
			ReflectionUtil.throwException(exception);
		}
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.product.navigation.taglib#/page.jsp#pre");
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_defaultCTConfiguration = ConfigurableUtil.createConfigurable(
			CTConfiguration.class, properties);
	}

	private CTConfiguration _getCTConfiguration(long companyId) {
		try {
			return _configurationProvider.getCompanyConfiguration(
				CTConfiguration.class, companyId);
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);
		}

		return _defaultCTConfiguration;
	}

	private Map<String, Object> _getReactData(
			HttpServletRequest httpServletRequest, CTCollection ctCollection,
			CTPreferences ctPreferences, boolean productionOnlyApplication,
			boolean sandboxOnlyEnabled, boolean showContextChangePopover,
			ThemeDisplay themeDisplay, boolean unsupportedApplication)
		throws PortalException {

		PortletURL checkoutURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest, themeDisplay.getScopeGroup(),
				CTPortletKeys.PUBLICATIONS, 0, 0, PortletRequest.ACTION_PHASE)
		).setActionName(
			"/change_tracking/checkout_ct_collection"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).buildPortletURL();

		PortalPreferences portalPreferences =
			PortletPreferencesFactoryUtil.getPortalPreferences(
				httpServletRequest);

		Map<String, Object> data = HashMapBuilder.<String, Object>put(
			"getSelectPublicationsURL",
			() -> {
				ResourceURL getSelectPublicationsURL =
					(ResourceURL)_portal.getControlPanelPortletURL(
						httpServletRequest, themeDisplay.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.RESOURCE_PHASE);

				getSelectPublicationsURL.setResourceID(
					"/change_tracking/get_select_publications");

				return getSelectPublicationsURL.toString();
			}
		).put(
			"namespace", _portal.getPortletNamespace(CTPortletKeys.PUBLICATIONS)
		).put(
			"orderByAscending",
			portalPreferences.getValue(
				CTPortletKeys.PUBLICATIONS, "select-order-by-ascending")
		).put(
			"orderByColumn",
			portalPreferences.getValue(
				CTPortletKeys.PUBLICATIONS, "select-order-by-column")
		).put(
			"preferencesPrefix", "select"
		).put(
			"saveDisplayPreferenceURL",
			() -> {
				ResourceURL saveDisplayPreferenceURL =
					(ResourceURL)_portal.getControlPanelPortletURL(
						httpServletRequest, themeDisplay.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.RESOURCE_PHASE);

				saveDisplayPreferenceURL.setResourceID(
					"/change_tracking/save_display_preference");

				return saveDisplayPreferenceURL.toString();
			}
		).put(
			"spritemap", themeDisplay.getPathThemeSpritemap()
		).build();

		long ctCollectionId = CTConstants.CT_COLLECTION_ID_PRODUCTION;

		if (ctCollection != null) {
			ctCollectionId = ctCollection.getCtCollectionId();

			data.put("iconClass", "change-tracking-indicator-icon-publication");
			data.put("iconName", "radio-button");

			if (productionOnlyApplication) {
				data.put(
					"title",
					StringBundler.concat(
						ctCollection.getName(), " (",
						_language.get(
							themeDisplay.getLocale(), "production-only-title"),
						")"));
				data.put(
					"warningBody",
					_language.get(
						themeDisplay.getLocale(), "production-only-message"));
				data.put("warningButton", false);
				data.put(
					"warningHeader",
					_language.get(
						themeDisplay.getLocale(), "production-only-title"));
				data.put("warningLearnLink", null);
			}
			else if (unsupportedApplication) {
				data.put(
					"title",
					StringBundler.concat(
						ctCollection.getName(), " (",
						_language.get(
							themeDisplay.getLocale(),
							"unsupported-application-title"),
						")"));
				data.put(
					"warningBody",
					_language.get(
						themeDisplay.getLocale(),
						"unsupported-application-message"));
				data.put("warningButton", true);
				data.put(
					"warningHeader",
					_language.get(
						themeDisplay.getLocale(),
						"unsupported-application-title"));
				data.put("warningLearnLink", null);
			}
			else if (showContextChangePopover) {
				data.put("contextChangeButtons", true);
				data.put("title", ctCollection.getName());
				data.put(
					"warningBody",
					_language.get(
						themeDisplay.getLocale(),
						"you-just-switched-contexts.-do-you-want-to-keep-" +
							"working-in-this-publication"));
				data.put("warningButton", true);
				data.put(
					"warningHeader",
					_language.get(
						themeDisplay.getLocale(),
						"keep-working-in-this-publication"));
			}
			else {
				data.put("title", ctCollection.getName());
			}
		}
		else {
			data.put("iconClass", "change-tracking-indicator-icon-production");
			data.put("iconName", "simple-circle");
			data.put(
				"title", _language.get(themeDisplay.getLocale(), "production"));
		}

		if (ctPreferences != null) {
			if (ctCollectionId == CTConstants.CT_COLLECTION_ID_PRODUCTION) {
				long previousCtCollectionId =
					ctPreferences.getPreviousCtCollectionId();

				CTCollection previousCTCollection =
					_ctCollectionLocalService.fetchCTCollection(
						previousCtCollectionId);

				if (previousCTCollection != null) {
					checkoutURL.setParameter(
						"ctCollectionId",
						String.valueOf(previousCtCollectionId));

					data.put(
						"checkoutDropdownItem",
						JSONUtil.put(
							"href", checkoutURL.toString()
						).put(
							"label",
							_language.format(
								themeDisplay.getLocale(), "work-on-x",
								previousCTCollection.getName(), false)
						).put(
							"symbolLeft", "radio-button"
						));
				}
			}
			else {
				if (!sandboxOnlyEnabled ||
					PortletPermissionUtil.contains(
						themeDisplay.getPermissionChecker(),
						CTPortletKeys.PUBLICATIONS,
						CTActionKeys.WORK_ON_PRODUCTION)) {

					checkoutURL.setParameter(
						"ctCollectionId",
						String.valueOf(
							CTConstants.CT_COLLECTION_ID_PRODUCTION));

					data.put(
						"checkoutDropdownItem",
						JSONUtil.put(
							"confirmationMessage",
							_language.get(
								themeDisplay.getLocale(),
								"any-changes-made-in-production-will-" +
									"immediately-be-live.-continue-to-" +
										"production")
						).put(
							"href", checkoutURL.toString()
						).put(
							"label",
							_language.get(
								themeDisplay.getLocale(), "work-on-production")
						).put(
							"symbolLeft", "simple-circle"
						));
				}
				else if (FeatureFlagManagerUtil.isEnabled(
							themeDisplay.getCompanyId(), "LPD-20556")) {

					Layout layout = themeDisplay.getLayout();

					Layout previewLayout = null;

					try (SafeCloseable safeCloseable =
							CTCollectionThreadLocal.
								setProductionModeWithSafeCloseable()) {

						previewLayout = _layoutLocalService.fetchLayout(
							layout.getPlid());
					}

					if (previewLayout != null) {
						String url = HttpComponentsUtil.addParameter(
							_portal.getLayoutFriendlyURL(
								previewLayout, themeDisplay),
							"p_l_mode", "preview");

						url = HttpComponentsUtil.addParameter(
							url, "previewCTCollectionId",
							previewLayout.getCtCollectionId());
						url = HttpComponentsUtil.addParameter(
							url, "previewCTIndicator", true);

						long segmentsExperienceId = ParamUtil.getLong(
							httpServletRequest, "segmentsExperienceId");

						if (segmentsExperienceId > 0) {
							url = HttpComponentsUtil.addParameter(
								url, "segmentsExperienceId",
								segmentsExperienceId);
						}

						data.put(
							"previewProductionDropdownItem",
							JSONUtil.put(
								"href", url
							).put(
								"label",
								_language.get(
									themeDisplay.getLocale(),
									"view-on-production")
							).put(
								"symbolLeft", "simple-circle"
							));
					}
				}
			}
		}

		if (CTPermission.contains(
				themeDisplay.getPermissionChecker(),
				CTActionKeys.ADD_PUBLICATION)) {

			List<CTRemote> ctRemotes = _ctRemoteLocalService.getCTRemotes(
				themeDisplay.getCompanyId());

			if (!ctRemotes.isEmpty()) {
				JSONArray jsonArray = _jsonFactory.createJSONArray();

				jsonArray.put(
					JSONUtil.put(
						"href",
						PortletURLBuilder.create(
							_portal.getControlPanelPortletURL(
								httpServletRequest,
								themeDisplay.getScopeGroup(),
								CTPortletKeys.PUBLICATIONS, 0, 0,
								PortletRequest.RENDER_PHASE)
						).setMVCRenderCommandName(
							"/change_tracking/add_ct_collection"
						).setRedirect(
							themeDisplay.getURLCurrent()
						).buildString()
					).put(
						"label",
						_language.get(themeDisplay.getLocale(), "local")
					));

				for (CTRemote ctRemote : ctRemotes) {
					jsonArray.put(
						JSONUtil.put(
							"href",
							PortletURLBuilder.create(
								_portal.getControlPanelPortletURL(
									httpServletRequest,
									themeDisplay.getScopeGroup(),
									CTPortletKeys.PUBLICATIONS, 0, 0,
									PortletRequest.RENDER_PHASE)
							).setMVCRenderCommandName(
								"/change_tracking/add_ct_collection"
							).setRedirect(
								themeDisplay.getURLCurrent()
							).setParameter(
								"ctRemoteId", ctRemote.getCtRemoteId()
							).buildString()
						).put(
							"label", ctRemote.getName()
						));
				}

				data.put(
					"createDropdownItem",
					JSONUtil.put(
						"items", jsonArray
					).put(
						"label",
						_language.get(
							themeDisplay.getLocale(), "create-new-publication")
					).put(
						"symbolLeft", "plus"
					).put(
						"type", "contextual"
					));
			}
			else {
				data.put(
					"createDropdownItem",
					JSONUtil.put(
						"href",
						PortletURLBuilder.create(
							_portal.getControlPanelPortletURL(
								httpServletRequest,
								themeDisplay.getScopeGroup(),
								CTPortletKeys.PUBLICATIONS, 0, 0,
								PortletRequest.RENDER_PHASE)
						).setMVCRenderCommandName(
							"/change_tracking/add_ct_collection"
						).setRedirect(
							themeDisplay.getURLCurrent()
						).buildString()
					).put(
						"label",
						_language.get(
							themeDisplay.getLocale(), "create-new-publication")
					).put(
						"symbolLeft", "plus"
					));
			}
		}

		if (ctCollection != null) {
			data.put(
				"reviewDropdownItem",
				JSONUtil.put(
					"href",
					PortletURLBuilder.create(
						_portal.getControlPanelPortletURL(
							httpServletRequest, themeDisplay.getScopeGroup(),
							CTPortletKeys.PUBLICATIONS, 0, 0,
							PortletRequest.RENDER_PHASE)
					).setMVCRenderCommandName(
						"/change_tracking/view_changes"
					).setParameter(
						"ctCollectionId", ctCollectionId
					).buildString()
				).put(
					"label",
					_language.get(themeDisplay.getLocale(), "review-changes")
				).put(
					"symbolLeft", "list-ul"
				));
		}

		_getTimelineData(ctCollection, data, httpServletRequest, themeDisplay);

		return data;
	}

	private void _getTimelineData(
			CTCollection currentCTCollection, Map<String, Object> data,
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		String className = (String)httpServletRequest.getAttribute(
			CTTimelineKeys.CLASS_NAME);

		long classPK = GetterUtil.getLong(
			httpServletRequest.getAttribute(CTTimelineKeys.CLASS_PK));

		if ((className == null) && (classPK == 0)) {
			Layout layout = themeDisplay.getLayout();

			if (!layout.isTypeControlPanel()) {
				className = Layout.class.getName();
				classPK = layout.getPlid();
			}
		}

		if (className != null) {
			long classNameId = _portal.getClassNameId(className);

			if (currentCTCollection != null) {
				ResourceURL getConflictInfoURL =
					(ResourceURL)_portal.getControlPanelPortletURL(
						httpServletRequest, themeDisplay.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.RESOURCE_PHASE);

				getConflictInfoURL.setParameter(
					"classNameId", String.valueOf(classNameId));
				getConflictInfoURL.setParameter(
					"classPK", String.valueOf(classPK));
				getConflictInfoURL.setParameter(
					"currentCTCollectionId",
					String.valueOf(currentCTCollection.getCtCollectionId()));
				getConflictInfoURL.setResourceID(
					"/change_tracking/get_conflict_info");

				if (FeatureFlagManagerUtil.isEnabled(
						themeDisplay.getCompanyId(), "LPD-20556")) {

					data.put(
						"getConflictInfoURL", getConflictInfoURL.toString());
				}
			}

			data.put("timelineClassNameId", classNameId);
			data.put("timelineClassPK", classPK);
			data.put(
				"timelineDeleteURL",
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						httpServletRequest, themeDisplay.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/change_tracking/delete_ct_collection"
				).buildString());

			String timelineEditURL = null;

			if (FeatureFlagManagerUtil.isEnabled(
					themeDisplay.getCompanyId(), "LPD-20556")) {

				timelineEditURL = PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						httpServletRequest, themeDisplay.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.ACTION_PHASE)
				).setActionName(
					"/change_tracking/checkout_ct_collection"
				).setRedirect(
					_portal.getCurrentURL(httpServletRequest)
				).buildString();
			}

			data.put("timelineEditURL", timelineEditURL);

			data.put("timelineIconClass", "change-tracking-timeline-icon");
			data.put("timelineIconName", "time");

			String timelineItemsURL = StringBundler.concat(
				_portal.getPortalURL(themeDisplay),
				"/o/change-tracking-rest/v1.0/ct-entries/history?",
				"classNameId=", classNameId);

			if (classPK != 0) {
				timelineItemsURL = StringBundler.concat(
					timelineItemsURL, "&classPK=", classPK);
			}

			data.put("timelineItemsURL", timelineItemsURL);

			CTDisplayRenderer<?> ctDisplayRenderer =
				_ctDisplayRendererRegistry.getCTDisplayRenderer(classNameId);

			data.put(
				"timelineType",
				ctDisplayRenderer.getTypeName(themeDisplay.getLocale()));

			data.put(
				"viewTimelineHistoryURL",
				PortletURLBuilder.create(
					_portal.getControlPanelPortletURL(
						httpServletRequest, themeDisplay.getScopeGroup(),
						CTPortletKeys.PUBLICATIONS, 0, 0,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/change_tracking/view_timeline_history"
				).setRedirect(
					_portal.getCurrentURL(httpServletRequest)
				).setParameter(
					"classNameId", classNameId
				).setParameter(
					"classPK", classPK
				).setWindowState(
					LiferayWindowState.POP_UP
				).buildString());
		}
	}

	private boolean _isShowContextChangePopover(
		HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay) {

		Group group = themeDisplay.getScopeGroup();

		if (CTCollectionThreadLocal.isProductionMode() ||
			!FeatureFlagManagerUtil.isEnabled(
				themeDisplay.getCompanyId(), "LPD-20131") ||
			!group.isSite()) {

			return false;
		}

		HttpSession httpSession = httpServletRequest.getSession();

		long ctLastGroupId = GetterUtil.getLong(
			httpSession.getAttribute(CTWebKeys.CT_LAST_GROUP_ID));

		if (ctLastGroupId == 0) {
			ctLastGroupId = group.getGroupId();

			httpSession.setAttribute(CTWebKeys.CT_LAST_GROUP_ID, ctLastGroupId);
		}

		if (ctLastGroupId != group.getGroupId()) {
			httpSession.setAttribute(
				CTWebKeys.CT_LAST_GROUP_ID, group.getGroupId());

			PortalPreferences portalPreferences =
				_portletPreferencesFactory.getPortalPreferences(
					httpServletRequest);

			String hideContextChangeWarningExpiryTime =
				portalPreferences.getValue(
					CTPortletKeys.PUBLICATIONS,
					"hideContextChangeWarningExpiryTime");

			if (Validator.isNull(hideContextChangeWarningExpiryTime)) {
				return true;
			}

			if (Objects.equals(hideContextChangeWarningExpiryTime, "-1")) {
				return false;
			}

			if (GetterUtil.getLong(hideContextChangeWarningExpiryTime) <=
					System.currentTimeMillis()) {

				return true;
			}

			return false;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ChangeTrackingIndicatorDynamicInclude.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CTCollectionLocalService _ctCollectionLocalService;

	@Reference
	private CTDisplayRendererRegistry _ctDisplayRendererRegistry;

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTRemoteLocalService _ctRemoteLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	private volatile CTConfiguration _defaultCTConfiguration;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesFactory _portletPreferencesFactory;

	@Reference
	private ReactRenderer _reactRenderer;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.change.tracking.web)"
	)
	private ServletContext _servletContext;

}