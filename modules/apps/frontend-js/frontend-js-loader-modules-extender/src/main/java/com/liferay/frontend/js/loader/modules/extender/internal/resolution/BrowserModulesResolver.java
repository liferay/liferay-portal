/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.resolution;

import com.liferay.frontend.js.loader.modules.extender.internal.config.generator.JSConfigGeneratorModule;
import com.liferay.frontend.js.loader.modules.extender.internal.config.generator.JSConfigGeneratorPackage;
import com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details;
import com.liferay.frontend.js.loader.modules.extender.internal.resolution.adapter.JSConfigGeneratorBrowserModule;
import com.liferay.frontend.js.loader.modules.extender.npm.JSModuleAlias;
import com.liferay.frontend.js.loader.modules.extender.npm.JSPackage;
import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistry;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Rodolfo Roza Miranda
 */
@Component(
	configurationPid = "com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details",
	service = BrowserModulesResolver.class
)
public class BrowserModulesResolver {

	public BrowserModulesResolution resolve(
		List<String> moduleNames, HttpServletRequest httpServletRequest) {

		BrowserModulesResolution browserModulesResolution =
			new BrowserModulesResolution(
				_jsonFactory, _details.explainResolutions());

		BrowserModulesMap browserModulesMap = new BrowserModulesMap(
			browserModulesResolution, _npmRegistry);

		for (String moduleName : moduleNames) {
			_resolve(
				browserModulesMap, moduleName, browserModulesResolution,
				httpServletRequest);
		}

		_populateMappedModuleNames(browserModulesResolution);

		return browserModulesResolution;
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_details = ConfigurableUtil.createConfigurable(
			Details.class, properties);

		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext,
			"(&(objectClass=" + ServletContext.class.getName() +
				")(osgi.web.contextpath=*))",
			new ServiceTrackerCustomizer
				<ServletContext, JSConfigGeneratorPackage>() {

				@Override
				public JSConfigGeneratorPackage addingService(
					ServiceReference<ServletContext> serviceReference) {

					Bundle bundle = serviceReference.getBundle();

					URL url = bundle.getEntry(Details.CONFIG_JSON);

					if (url == null) {
						return null;
					}

					JSConfigGeneratorPackage jsConfigGeneratorPackage =
						new JSConfigGeneratorPackage(
							_details.applyVersioning(),
							serviceReference.getBundle(),
							(String)serviceReference.getProperty(
								"osgi.web.contextpath"));

					for (JSConfigGeneratorModule jsConfigGeneratorModule :
							jsConfigGeneratorPackage.
								getJSConfigGeneratorModules()) {

						JSConfigGeneratorBrowserModule
							jsConfigGeneratorBrowserModule =
								new JSConfigGeneratorBrowserModule(
									jsConfigGeneratorModule);

						_browserModulesMap.put(
							jsConfigGeneratorBrowserModule.getName(),
							jsConfigGeneratorBrowserModule);
					}

					return jsConfigGeneratorPackage;
				}

				@Override
				public void modifiedService(
					ServiceReference<ServletContext> serviceReference,
					JSConfigGeneratorPackage jsConfigGeneratorPackage) {
				}

				@Override
				public void removedService(
					ServiceReference<ServletContext> serviceReference,
					JSConfigGeneratorPackage jsConfigGeneratorPackage) {

					for (JSConfigGeneratorModule jsConfigGeneratorModule :
							jsConfigGeneratorPackage.
								getJSConfigGeneratorModules()) {

						_browserModulesMap.remove(
							jsConfigGeneratorModule.getId());
					}
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();
	}

	private void _populateMappedModuleNames(
		BrowserModulesResolution browserModulesResolution) {

		Set<JSPackage> jsPackages = new HashSet<>();

		for (String moduleName :
				browserModulesResolution.getResolvedModuleNames()) {

			JSPackage jsPackage = _npmRegistry.getResolvedJSPackage(
				ModuleNameUtil.getPackageName(moduleName));

			if (jsPackage == null) {
				continue;
			}

			jsPackages.add(jsPackage);
		}

		for (JSPackage jsPackage : jsPackages) {
			for (JSModuleAlias jsModuleAlias : jsPackage.getJSModuleAliases()) {
				browserModulesResolution.putMappedModuleName(
					jsPackage.getResolvedId() + StringPool.SLASH +
						jsModuleAlias.getAlias(),
					jsPackage.getResolvedId() + StringPool.SLASH +
						jsModuleAlias.getModuleName(),
					true);
			}
		}
	}

	private boolean _processBrowserModule(
		BrowserModulesMap browserModulesMap, BrowserModule browserModule,
		BrowserModulesResolution browserModulesResolution,
		HttpServletRequest httpServletRequest) {

		String moduleName = browserModule.getName();

		if (browserModulesResolution.isProcessedModuleName(moduleName)) {
			return false;
		}

		browserModulesResolution.addProcessedModuleName(moduleName);

		Map<String, String> dependenciesMap = new HashMap<>();

		browserModulesResolution.indentExplanation();

		for (String dependency : browserModule.getDependencies()) {
			if (ModuleNameUtil.isReservedModuleName(dependency)) {
				continue;
			}

			String dependencyModuleName = ModuleNameUtil.getDependencyPath(
				moduleName, dependency);

			dependencyModuleName = BrowserModuleNameMapper.mapModuleName(
				_npmRegistry, dependencyModuleName,
				browserModule.getDependenciesMap());

			dependenciesMap.put(dependency, dependencyModuleName);

			BrowserModule dependencyBrowserModule = browserModulesMap.get(
				dependencyModuleName);

			if (dependencyBrowserModule != null) {
				_processBrowserModule(
					browserModulesMap, dependencyBrowserModule,
					browserModulesResolution, httpServletRequest);
			}
			else {
				browserModulesResolution.addError(
					StringBundler.concat(
						"Missing dependency '", dependencyModuleName, "' of '",
						moduleName, "'"));
			}
		}

		browserModulesResolution.dedentExplanation();

		browserModulesResolution.putDependenciesMap(
			moduleName, dependenciesMap);

		JSONObject flagsJSONObject = browserModule.getFlagsJSONObject();

		if (flagsJSONObject != null) {
			browserModulesResolution.putModuleFlags(
				moduleName, flagsJSONObject);
		}

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		browserModulesResolution.putPath(
			moduleName,
			absolutePortalURLBuilder.forBrowserModule(
				browserModule.getPath()
			).build());

		browserModulesResolution.addResolvedModuleName(moduleName);

		return true;
	}

	private void _resolve(
		BrowserModulesMap browserModulesMap, String moduleName,
		BrowserModulesResolution browserModulesResolution,
		HttpServletRequest httpServletRequest) {

		String mappedModuleName = BrowserModuleNameMapper.mapModuleName(
			_npmRegistry, moduleName);

		BrowserModule browserModule = browserModulesMap.get(mappedModuleName);

		if (browserModule == null) {
			browserModulesResolution.addError(
				StringBundler.concat(
					"Missing required module '", moduleName, "'"));

			return;
		}

		if (!moduleName.equals(mappedModuleName)) {
			browserModulesResolution.putMappedModuleName(
				moduleName, mappedModuleName, true);
		}

		_processBrowserModule(
			browserModulesMap, browserModule, browserModulesResolution,
			httpServletRequest);
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	private final Map<String, BrowserModule> _browserModulesMap =
		new ConcurrentHashMap<>();
	private Details _details;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private NPMRegistry _npmRegistry;

	private ServiceTracker<ServletContext, JSConfigGeneratorPackage>
		_serviceTracker;

}