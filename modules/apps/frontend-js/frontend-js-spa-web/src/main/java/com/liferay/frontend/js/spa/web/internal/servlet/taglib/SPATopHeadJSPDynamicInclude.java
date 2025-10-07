/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.spa.web.internal.servlet.taglib;

import com.liferay.frontend.js.loader.modules.extender.esm.ESImportUtil;
import com.liferay.frontend.js.spa.web.internal.configuration.SPAConfiguration;
import com.liferay.osgi.util.StringPlus;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.servlet.taglib.BaseJSPDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.aui.JSFragment;
import com.liferay.portal.kernel.servlet.taglib.aui.ScriptData;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Bruno Basto
 */
@Component(service = DynamicInclude.class)
public class SPATopHeadJSPDynamicInclude extends BaseJSPDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		SPAConfiguration spaConfiguration = _getSPAConfiguration(themeDisplay);

		if (!spaConfiguration.enabled()) {
			return;
		}

		JSONArray excludedPathsJSONArray = _getExcludedPathsJSONArray(
			spaConfiguration);

		String currentURL = _portal.getCurrentURL(httpServletRequest);

		for (Object excludedPath : excludedPathsJSONArray) {
			if (currentURL.equals(excludedPath)) {
				return;
			}
		}

		ResourceBundle resourceBundle = _getLanguageResourceBundle(
			"frontend-js-spa-web", themeDisplay.getLocale());

		JSONObject configJSONObject = JSONUtil.put(
			"cacheExpirationTime", spaConfiguration.cacheExpirationTime()
		).put(
			"clearScreensCache",
			_isClearScreensCache(
				httpServletRequest, httpServletRequest.getSession())
		).put(
			"debugEnabled", _log.isDebugEnabled()
		).put(
			"excludedPaths", excludedPathsJSONArray
		).put(
			"excludedTargetPortlets", _getExcludedTargetPortletsJSONArray()
		).put(
			"loginRedirect", _getLoginRedirect(httpServletRequest)
		).put(
			"navigationExceptionSelectors",
			_getNavigationExceptionSelectors(spaConfiguration)
		).put(
			"portletsBlacklist",
			_getPortletsBlacklistJSONArray(themeDisplay.getCompanyId())
		).put(
			"preloadCSS", spaConfiguration.preloadCSS()
		).put(
			"requestTimeout", spaConfiguration.requestTimeout()
		).put(
			"userNotification",
			JSONUtil.put(
				"message",
				_language.get(
					resourceBundle,
					"it-looks-like-this-is-taking-longer-than-expected")
			).put(
				"timeout", spaConfiguration.userNotificationTimeout()
			).put(
				"title", _language.get(resourceBundle, "oops")
			)
		).put(
			"validStatusCodes", _validStatusCodesJSONArray
		);

		ScriptData initScriptData = new ScriptData();

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		initScriptData.append(
			null,
			new JSFragment(
				"init(" + configJSONObject.toString() + ");",
				List.of(
					ESImportUtil.getESImport(
						absolutePortalURLBuilder,
						"{init} from frontend-js-spa-web"))));

		initScriptData.writeTo(httpServletResponse.getWriter());
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_head.jsp#post");
	}

	@Activate
	protected void activate(BundleContext bundleContext) throws Exception {
		String portletNamespace = _portal.getPortletNamespace(
			PropsUtil.get(PropsKeys.AUTH_LOGIN_PORTLET_NAME));

		_redirectParamName = portletNamespace.concat("redirect");

		_validStatusCodesJSONArray = _jsonFactory.createJSONArray();

		Class<?> clazz = ServletResponseConstants.class;

		for (Field field : clazz.getDeclaredFields()) {
			try {
				_validStatusCodesJSONArray.put(field.getInt(null));
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}
		}

		_globalNavigationExceptionSelectors = new CopyOnWriteArrayList<>();

		_navigationExceptionSelectorTracker =
			_getNavigationExceptionSelectorTracker(bundleContext);

		_navigationExceptionSelectorTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		if (_navigationExceptionSelectorTracker != null) {
			_navigationExceptionSelectorTracker.close();
		}

		_navigationExceptionSelectorTracker = null;
	}

	@Override
	protected String getJspPath() {
		return null;
	}

	@Override
	protected Log getLog() {
		return null;
	}

	@Override
	protected ServletContext getServletContext() {
		return null;
	}

	private JSONArray _getExcludedPathsJSONArray(
		SPAConfiguration spaConfiguration) {

		JSONArray jsonArray = _jsonFactory.createJSONArray();

		for (String excludedPath : _SPA_DEFAULT_EXCLUDED_PATHS) {
			jsonArray.put(_portal.getPathContext() + excludedPath);
		}

		String[] customExcludedPaths = spaConfiguration.customExcludedPaths();

		if (ArrayUtil.isEmpty(customExcludedPaths)) {
			return jsonArray;
		}

		for (String customExcludedPath : customExcludedPaths) {
			jsonArray.put(_portal.getPathContext() + customExcludedPath);
		}

		return jsonArray;
	}

	private JSONArray _getExcludedTargetPortletsJSONArray() {
		return _jsonFactory.createJSONArray(
			new String[] {PortletKeys.USERS_ADMIN, PortletKeys.SERVER_ADMIN});
	}

	private ResourceBundle _getLanguageResourceBundle(
		String servletContextName, Locale locale) {

		ResourceBundleLoader resourceBundleLoader =
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByServletContextName(servletContextName);

		if (resourceBundleLoader == null) {
			resourceBundleLoader =
				ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
		}

		return resourceBundleLoader.loadResourceBundle(locale);
	}

	private String _getLoginRedirect(HttpServletRequest httpServletRequest) {
		return HtmlUtil.escapeJS(
			ParamUtil.getString(httpServletRequest, _redirectParamName));
	}

	private String _getNavigationExceptionSelectors(
		SPAConfiguration spaConfiguration) {

		List<String> navigationExceptionSelectors = new ArrayList<>();

		navigationExceptionSelectors.addAll(
			_globalNavigationExceptionSelectors);

		Collections.addAll(
			navigationExceptionSelectors,
			spaConfiguration.navigationExceptionSelectors());

		return ListUtil.toString(
			navigationExceptionSelectors, (String)null, StringPool.BLANK);
	}

	private ServiceTracker<Object, Object>
			_getNavigationExceptionSelectorTracker(BundleContext bundleContext)
		throws Exception {

		Filter filter = bundleContext.createFilter(
			"(&(objectClass=java.lang.Object)(" +
				_SPA_NAVIGATION_EXCEPTION_SELECTOR_KEY + "=*))");

		return new ServiceTracker<>(
			bundleContext, filter,
			new ServiceTrackerCustomizer<>() {

				@Override
				public Object addingService(
					ServiceReference<Object> serviceReference) {

					_globalNavigationExceptionSelectors.addAll(
						StringPlus.asList(
							serviceReference.getProperty(
								_SPA_NAVIGATION_EXCEPTION_SELECTOR_KEY)));

					return bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<Object> serviceReference, Object service) {

					removedService(serviceReference, service);

					addingService(serviceReference);
				}

				@Override
				public void removedService(
					ServiceReference<Object> serviceReference, Object service) {

					_globalNavigationExceptionSelectors.removeAll(
						StringPlus.asList(
							serviceReference.getProperty(
								_SPA_NAVIGATION_EXCEPTION_SELECTOR_KEY)));

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	private JSONArray _getPortletsBlacklistJSONArray(long companyId) {
		return _portletsBlacklistJSONArrays.computeIfAbsent(
			companyId,
			companyId2 -> {
				JSONArray portletsBlacklistJSONArray =
					_jsonFactory.createJSONArray();

				_portletLocalService.visitPortlets(
					companyId,
					portlet -> {
						if (!portlet.isSinglePageApplication() &&
							!portlet.isUndeployedPortlet() &&
							portlet.isActive() && portlet.isReady()) {

							portletsBlacklistJSONArray.put(
								portlet.getPortletId());
						}
					});

				return portletsBlacklistJSONArray;
			});
	}

	private SPAConfiguration _getSPAConfiguration(ThemeDisplay themeDisplay) {
		SPAConfiguration spaConfiguration;

		try {
			spaConfiguration = _configurationProvider.getCompanyConfiguration(
				SPAConfiguration.class, themeDisplay.getCompanyId());
		}
		catch (ConfigurationException configurationException) {
			_log.error(configurationException);

			spaConfiguration = _defaultSPAConfiguration;
		}

		return spaConfiguration;
	}

	private boolean _isClearScreensCache(
		HttpServletRequest httpServletRequest, HttpSession httpSession) {

		boolean singlePageApplicationClearCache = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(
				WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE));

		if (singlePageApplicationClearCache) {
			return true;
		}

		String portletId = httpServletRequest.getParameter("p_p_id");

		if (Validator.isNull(portletId)) {
			return false;
		}

		String singlePageApplicationLastPortletId =
			(String)httpSession.getAttribute(
				WebKeys.SINGLE_PAGE_APPLICATION_LAST_PORTLET_ID);

		if (Validator.isNotNull(singlePageApplicationLastPortletId) &&
			!Objects.equals(portletId, singlePageApplicationLastPortletId)) {

			return true;
		}

		return false;
	}

	private static final String[] _SPA_DEFAULT_EXCLUDED_PATHS = {
		"/c/document_library", "/documents", "/image", "/o/cms/download-folder"
	};

	private static final String _SPA_NAVIGATION_EXCEPTION_SELECTOR_KEY =
		"javascript.single.page.application.navigation.exception.selector";

	private static final Log _log = LogFactoryUtil.getLog(
		SPATopHeadJSPDynamicInclude.class);

	private static final SPAConfiguration _defaultSPAConfiguration =
		ConfigurableUtil.createConfigurable(
			SPAConfiguration.class, Collections.emptyMap());

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private ConfigurationProvider _configurationProvider;

	private List<String> _globalNavigationExceptionSelectors;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	private ServiceTracker<Object, Object> _navigationExceptionSelectorTracker;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	private final Map<Long, JSONArray> _portletsBlacklistJSONArrays =
		new ConcurrentHashMap<>();
	private String _redirectParamName;
	private JSONArray _validStatusCodesJSONArray;

}