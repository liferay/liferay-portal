/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal;

import com.liferay.client.extension.constants.ClientExtensionEntryConstants;
import com.liferay.client.extension.model.ClientExtensionEntryRel;
import com.liferay.client.extension.service.ClientExtensionEntryRelLocalService;
import com.liferay.client.extension.type.ThemeCSSCET;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.frontend.token.definition.constants.FrontendTokenDefinitionConstants;
import com.liferay.frontend.token.definition.validator.FrontendTokenDefinitionJSONValidator;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Iván Zaera
 */
@Component(service = FrontendTokenDefinitionRegistry.class)
public class FrontendTokenDefinitionRegistryImpl
	implements FrontendTokenDefinitionRegistry {

	@Override
	public FrontendTokenDefinition getFrontendTokenDefinition(Layout layout) {
		long classNameId = _portal.getClassNameId(Layout.class);

		String cetExternalReferenceCode = _getCETExternalReferenceCode(
			classNameId, layout.getPlid());

		if (cetExternalReferenceCode != null) {
			return _serviceTrackerMap.getService(
				_getKey(layout.getCompanyId(), cetExternalReferenceCode));
		}

		if (layout.getMasterLayoutPlid() > 0) {
			cetExternalReferenceCode = _getCETExternalReferenceCode(
				classNameId, layout.getMasterLayoutPlid());

			if (cetExternalReferenceCode != null) {
				return _serviceTrackerMap.getService(
					_getKey(layout.getCompanyId(), cetExternalReferenceCode));
			}
		}

		LayoutSet layoutSet = layout.getLayoutSet();

		cetExternalReferenceCode = _getCETExternalReferenceCode(
			_portal.getClassNameId(LayoutSet.class),
			layoutSet.getLayoutSetId());

		if (cetExternalReferenceCode != null) {
			return _serviceTrackerMap.getService(
				_getKey(layoutSet.getCompanyId(), cetExternalReferenceCode));
		}

		Theme theme = null;

		try {
			theme = layout.getTheme();

			return _getBundleFrontendTokenDefinition(theme.getThemeId());
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to get the theme for layout with layout ID " +
					layout.getLayoutId(),
				portalException);
		}

		return null;
	}

	public FrontendTokenDefinition getFrontendTokenDefinition(
		LayoutSet layoutSet) {

		return _getFrontendTokenDefinition(
			layoutSet.getCompanyId(),
			_getCETExternalReferenceCode(
				_portal.getClassNameId(LayoutSet.class),
				layoutSet.getLayoutSetId()),
			layoutSet.getThemeId());
	}

	@Override
	public FrontendTokenDefinition getFrontendTokenDefinition(
		long companyId, String themeId) {

		FrontendTokenDefinition frontendTokenDefinition =
			_getBundleFrontendTokenDefinition(themeId);

		if ((frontendTokenDefinition != null) &&
			(!Objects.equals(
				frontendTokenDefinition.getThemeType(),
				FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL) ||
			 FeatureFlagManagerUtil.isEnabled(companyId, "LPD-84497"))) {

			return frontendTokenDefinition;
		}

		return _serviceTrackerMap.getService(_getKey(companyId, themeId));
	}

	@Override
	public List<FrontendTokenDefinition> getFrontendTokenDefinitions(
		long companyId) {

		Map<String, FrontendTokenDefinition> bundleFrontendTokenDefinitions =
			_getBundleFrontendTokenDefinitions();

		List<FrontendTokenDefinition> frontendTokenDefinitions =
			new ArrayList<>(bundleFrontendTokenDefinitions.values());

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-84497")) {
			frontendTokenDefinitions.removeIf(
				frontendTokenDefinition -> Objects.equals(
					frontendTokenDefinition.getThemeType(),
					FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL));
		}

		String keyPrefix = companyId + StringPool.POUND;

		for (String key : _serviceTrackerMap.keySet()) {
			if (key.startsWith(keyPrefix)) {
				FrontendTokenDefinition frontendTokenDefinition =
					_serviceTrackerMap.getService(key);

				if (frontendTokenDefinition != null) {
					frontendTokenDefinitions.add(frontendTokenDefinition);
				}
			}
		}

		return frontendTokenDefinitions;
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, _bundleTrackerCustomizer);

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ThemeCSSCET.class, null,
			(serviceReference, emitter) -> {
				ThemeCSSCET themeCSSCET = bundleContext.getService(
					serviceReference);

				if (Validator.isNotNull(
						themeCSSCET.getFrontendTokenDefinitionJSON())) {

					emitter.emit(
						_getKey(
							themeCSSCET.getCompanyId(),
							themeCSSCET.getExternalReferenceCode()));
				}

				bundleContext.ungetService(serviceReference);
			},
			new ServiceTrackerCustomizer
				<ThemeCSSCET, FrontendTokenDefinition>() {

				@Override
				public FrontendTokenDefinition addingService(
					ServiceReference<ThemeCSSCET> serviceReference) {

					ThemeCSSCET themeCSSCET = bundleContext.getService(
						serviceReference);

					try {
						_frontendTokenDefinitionJSONValidator.validate(
							themeCSSCET.getFrontendTokenDefinitionJSON());

						return new FrontendTokenDefinitionImpl(
							jsonFactory.createJSONObject(
								themeCSSCET.getFrontendTokenDefinitionJSON()),
							jsonFactory,
							ResourceBundleLoaderUtil.
								getPortalResourceBundleLoader(),
							themeCSSCET.getExternalReferenceCode(),
							themeCSSCET.getName(),
							FrontendTokenDefinitionConstants.
								THEME_TYPE_THEME_CSS_CET);
					}
					catch (JSONException | JSONValidatorException exception) {
						_log.error(
							"Unable to parse theme CSS client extension " +
								"frontend token definition",
							exception);
					}

					bundleContext.ungetService(serviceReference);

					return null;
				}

				@Override
				public void modifiedService(
					ServiceReference<ThemeCSSCET> serviceReference,
					FrontendTokenDefinition frontendTokenDefinition) {
				}

				@Override
				public void removedService(
					ServiceReference<ThemeCSSCET> serviceReference,
					FrontendTokenDefinition frontendTokenDefinition) {

					bundleContext.ungetService(serviceReference);
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();

		_serviceTrackerMap.close();
	}

	protected List<FrontendTokenDefinitionImpl> getFrontendTokenDefinitionImpls(
		Bundle bundle) {

		String json = _getFrontendTokenDefinitionJSON(bundle);

		if (json == null) {
			return Collections.emptyList();
		}

		try {
			List<FrontendTokenDefinitionImpl> frontendTokenDefinitionImpls =
				new ArrayList<>();

			JSONObject jsonObject = jsonFactory.createJSONObject(json);

			ResourceBundleLoader resourceBundleLoader =
				ResourceBundleLoaderUtil.
					getResourceBundleLoaderByBundleSymbolicName(
						bundle.getSymbolicName());

			if (resourceBundleLoader == null) {
				resourceBundleLoader =
					ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
			}

			List<Map<String, String>> themeMaps = getThemeMaps(bundle);

			if (themeMaps.isEmpty()) {
				frontendTokenDefinitionImpls.add(
					new FrontendTokenDefinitionImpl(
						jsonObject, jsonFactory, resourceBundleLoader,
						bundle.getSymbolicName(),
						jsonObject.getString("name", bundle.getSymbolicName()),
						FrontendTokenDefinitionConstants.THEME_TYPE_GLOBAL));
			}
			else {
				for (Map<String, String> themeMap : themeMaps) {
					frontendTokenDefinitionImpls.add(
						new FrontendTokenDefinitionImpl(
							jsonObject, jsonFactory, resourceBundleLoader,
							themeMap.get("id"), themeMap.get("name"),
							FrontendTokenDefinitionConstants.
								THEME_TYPE_BUNDLE));
				}
			}

			return frontendTokenDefinitionImpls;
		}
		catch (JSONException | RuntimeException exception) {
			_log.error(
				"Unable to parse frontend token definitions for bundle " +
					bundle.getSymbolicName(),
				exception);
		}

		return Collections.emptyList();
	}

	protected String getServletContextName(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String webContextPath = headers.get("Web-ContextPath");

		if (webContextPath == null) {
			return null;
		}

		if (webContextPath.startsWith(StringPool.SLASH)) {
			webContextPath = webContextPath.substring(1);
		}

		return webContextPath;
	}

	protected List<Map<String, String>> getThemeMaps(Bundle bundle) {
		URL url = bundle.getEntry("WEB-INF/liferay-look-and-feel.xml");

		if (url == null) {
			return Collections.emptyList();
		}

		try {
			List<Map<String, String>> themeMaps = new ArrayList<>();

			String servletContextName = getServletContextName(bundle);

			String xml = URLUtil.toString(url);

			xml = xml.replaceAll(StringPool.NEW_LINE, StringPool.SPACE);

			Matcher matcher = _themePattern.matcher(xml);

			while (matcher.find()) {
				String themeId = matcher.group(1);

				if (servletContextName != null) {
					themeId +=
						PortletConstants.WAR_SEPARATOR + servletContextName;
				}

				themeMaps.add(
					HashMapBuilder.put(
						"id", portal.getJsSafePortletId(themeId)
					).put(
						"name", matcher.group(2)
					).build());
			}

			return themeMaps;
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read WEB-INF/liferay-look-and-feel.xml",
				ioException);
		}
	}

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Portal portal;

	private FrontendTokenDefinition _getBundleFrontendTokenDefinition(
		String themeId) {

		Map<String, FrontendTokenDefinition> bundleFrontendTokenDefinitions =
			_getBundleFrontendTokenDefinitions();

		return bundleFrontendTokenDefinitions.get(themeId);
	}

	private Map<String, FrontendTokenDefinition>
		_getBundleFrontendTokenDefinitions() {

		return _frontendTokenDefinitionsDCLSingleton.getSingleton(
			() -> {
				if (_bundleTracker != null) {
					_bundleTracker.open();
				}

				return _frontendTokenDefinitions;
			});
	}

	private String _getCETExternalReferenceCode(
		long classNameId, long classPK) {

		ClientExtensionEntryRel clientExtensionEntryRel =
			_clientExtensionEntryRelLocalService.fetchClientExtensionEntryRel(
				classNameId, classPK,
				ClientExtensionEntryConstants.TYPE_THEME_CSS);

		if (clientExtensionEntryRel == null) {
			return null;
		}

		return clientExtensionEntryRel.getCETExternalReferenceCode();
	}

	private FrontendTokenDefinition _getFrontendTokenDefinition(
		long companyId, String externalReferenceCode, String themeId) {

		if (externalReferenceCode != null) {
			FrontendTokenDefinition frontendTokenDefinition =
				_serviceTrackerMap.getService(
					_getKey(companyId, externalReferenceCode));

			if (frontendTokenDefinition != null) {
				return frontendTokenDefinition;
			}
		}

		return _getBundleFrontendTokenDefinition(themeId);
	}

	private String _getFrontendTokenDefinitionJSON(Bundle bundle) {
		URL url = bundle.getEntry("WEB-INF/frontend-token-definition.json");

		if (url == null) {
			return null;
		}

		try {
			return URLUtil.toString(url);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to read WEB-INF/frontend-token-definition.json",
				ioException);
		}
	}

	private String _getKey(long companyId, String themeId) {
		return companyId + StringPool.POUND + themeId;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionRegistryImpl.class);

	private static final Pattern _themePattern = Pattern.compile(
		"<theme id=\"([^\"]*)\"[^>]* name=\"([^\"]*)\"[^>]*>");

	private BundleTracker<List<FrontendTokenDefinitionImpl>> _bundleTracker;

	private final BundleTrackerCustomizer<List<FrontendTokenDefinitionImpl>>
		_bundleTrackerCustomizer =
			new BundleTrackerCustomizer<List<FrontendTokenDefinitionImpl>>() {

				@Override
				public List<FrontendTokenDefinitionImpl> addingBundle(
					Bundle bundle, BundleEvent bundleEvent) {

					List<FrontendTokenDefinitionImpl>
						frontendTokenDefinitionImpls =
							getFrontendTokenDefinitionImpls(bundle);

					for (FrontendTokenDefinitionImpl
							frontendTokenDefinitionImpl :
								frontendTokenDefinitionImpls) {

						if (frontendTokenDefinitionImpl.getThemeId() == null) {
							continue;
						}

						_frontendTokenDefinitions.put(
							frontendTokenDefinitionImpl.getThemeId(),
							frontendTokenDefinitionImpl);
					}

					return frontendTokenDefinitionImpls;
				}

				@Override
				public void modifiedBundle(
					Bundle bundle, BundleEvent bundleEvent,
					List<FrontendTokenDefinitionImpl>
						frontendTokenDefinitionImpls) {
				}

				@Override
				public void removedBundle(
					Bundle bundle, BundleEvent bundleEvent,
					List<FrontendTokenDefinitionImpl>
						frontendTokenDefinitionImpls) {

					for (FrontendTokenDefinitionImpl
							frontendTokenDefinitionImpl :
								frontendTokenDefinitionImpls) {

						_frontendTokenDefinitions.remove(
							frontendTokenDefinitionImpl.getThemeId());
					}
				}

			};

	@Reference
	private ClientExtensionEntryRelLocalService
		_clientExtensionEntryRelLocalService;

	private final FrontendTokenDefinitionJSONValidator
		_frontendTokenDefinitionJSONValidator =
			new FrontendTokenDefinitionJSONValidator();
	private final Map<String, FrontendTokenDefinition>
		_frontendTokenDefinitions = new ConcurrentHashMap<>();
	private final DCLSingleton<Map<String, FrontendTokenDefinition>>
		_frontendTokenDefinitionsDCLSingleton = new DCLSingleton<>();

	@Reference
	private Portal _portal;

	private ServiceTrackerMap<String, FrontendTokenDefinition>
		_serviceTrackerMap;

}