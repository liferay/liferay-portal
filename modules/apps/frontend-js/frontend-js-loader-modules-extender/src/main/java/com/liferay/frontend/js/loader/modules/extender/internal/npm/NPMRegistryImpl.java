/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.npm;

import com.github.yuchi.semver.Range;
import com.github.yuchi.semver.Version;

import com.liferay.frontend.js.loader.modules.extender.internal.config.generator.JSConfigGeneratorPackage;
import com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details;
import com.liferay.frontend.js.loader.modules.extender.npm.JSBundle;
import com.liferay.frontend.js.loader.modules.extender.npm.JSBundleProcessor;
import com.liferay.frontend.js.loader.modules.extender.npm.JSBundleTracker;
import com.liferay.frontend.js.loader.modules.extender.npm.JSModule;
import com.liferay.frontend.js.loader.modules.extender.npm.JSModuleAlias;
import com.liferay.frontend.js.loader.modules.extender.npm.JSPackage;
import com.liferay.frontend.js.loader.modules.extender.npm.JSPackageDependency;
import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMJavaScriptLastModifiedUtil;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistry;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistryUpdate;
import com.liferay.frontend.js.loader.modules.extender.npm.NPMRegistryUpdatesListener;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * Provides the central class for the NPM package support of Liferay Portal.
 * This class features a central registry where all NPM packages and modules
 * deployed with OSGi bundles are tracked.
 *
 * @author Iván Zaera
 */
@Component(
	configurationPid = "com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details",
	service = NPMRegistry.class
)
public class NPMRegistryImpl implements NPMRegistry {

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void addJSBundleTracker(JSBundleTracker jsBundleTracker) {
	}

	public void finishUpdate(NPMRegistryUpdate npmRegistryUpdate) {
		_setDataBagSupplier(() -> _createDataBag(null));

		_notifyNPMRegistryUpdatesListeners();
	}

	@Override
	public Map<String, String> getGlobalAliases() {
		return _globalAliases;
	}

	/**
	 * Returns the OSGi bundles containing NPM packages that have been deployed
	 * to the portal.
	 *
	 * @return the OSGi bundles
	 */
	public Collection<JSBundle> getJSBundles() {
		Map<Bundle, JSBundle> tracked = _bundleTracker.getTracked();

		return tracked.values();
	}

	/**
	 * Returns the NPM module descriptor with the ID.
	 *
	 * @param  identifier the NPM module's ID
	 * @return the NPM module descriptor with the ID
	 */
	@Override
	public JSModule getJSModule(String identifier) {
		DataBag dataBag = _getDataBag();

		Map<String, JSModule> jsModules = dataBag._jsModules;

		return jsModules.get(identifier);
	}

	/**
	 * Returns the NPM package with the ID.
	 *
	 * @param  identifier the NPM package's ID
	 * @return the NPM package descriptor with the ID
	 */
	@Override
	public JSPackage getJSPackage(String identifier) {
		DataBag dataBag = _getDataBag();

		Map<String, JSPackage> jsPackages = dataBag._jsPackages;

		return jsPackages.get(identifier);
	}

	/**
	 * Returns all deployed NPM packages.
	 *
	 * @return the deployed NPM packages
	 */
	@Override
	public Collection<JSPackage> getJSPackages() {
		DataBag dataBag = _getDataBag();

		Map<String, JSPackage> jsPackages = dataBag._jsPackages;

		return jsPackages.values();
	}

	/**
	 * Returns the resolved module with the ID.
	 *
	 * @param  identifier the resolved module's ID
	 * @return the resolved module with the ID
	 */
	@Override
	public JSModule getResolvedJSModule(String identifier) {
		DataBag dataBag = _getDataBag();

		Map<String, JSModule> resolvedJSModules = dataBag._resolvedJSModules;

		return resolvedJSModules.get(identifier);
	}

	/**
	 * Returns all resolved modules deployed to the portal.
	 *
	 * @return the resolved modules deployed to the portal
	 */
	@Override
	public Collection<JSModule> getResolvedJSModules() {
		DataBag dataBag = _getDataBag();

		Map<String, JSModule> resolvedJSModules = dataBag._resolvedJSModules;

		return resolvedJSModules.values();
	}

	@Override
	public JSPackage getResolvedJSPackage(String identifier) {
		DataBag dataBag = _getDataBag();

		Map<String, JSPackage> resolvedJSPackages = dataBag._resolvedJSPackages;

		return resolvedJSPackages.get(identifier);
	}

	/**
	 * Returns all resolved packages deployed to the portal.
	 *
	 * @return the resolved packages deployed to the portal
	 * @review
	 */
	@Override
	public Collection<JSPackage> getResolvedJSPackages() {
		DataBag dataBag = _getDataBag();

		Map<String, JSPackage> resolvedJSPackages = dataBag._resolvedJSPackages;

		return resolvedJSPackages.values();
	}

	@Override
	public String mapModuleName(String moduleName) {
		DataBag dataBag = _getDataBag();

		Map<String, String> exactMatchMap = dataBag._exactMatchMap;

		String mappedModuleName = exactMatchMap.get(moduleName);

		if (Validator.isNotNull(mappedModuleName)) {
			return mapModuleName(mappedModuleName);
		}

		for (Map.Entry<String, String> entry : _globalAliases.entrySet()) {
			String resolvedId = entry.getKey();

			if (resolvedId.equals(moduleName) ||
				moduleName.startsWith(resolvedId + StringPool.SLASH)) {

				return mapModuleName(
					entry.getValue() +
						moduleName.substring(resolvedId.length()));
			}
		}

		for (Map.Entry<String, String> entry : _partialMatchMap.entrySet()) {
			String resolvedId = entry.getKey();

			if (resolvedId.equals(moduleName) ||
				moduleName.startsWith(resolvedId + StringPool.SLASH)) {

				return mapModuleName(
					entry.getValue() +
						moduleName.substring(resolvedId.length()));
			}
		}

		return moduleName;
	}

	/**
	 * @deprecated As of Mueller (7.2.x), with no direct replacement
	 */
	@Deprecated
	@Override
	public void removeJSBundleTracker(JSBundleTracker jsBundleTracker) {
	}

	@Override
	public JSPackage resolveJSPackageDependency(
		JSPackageDependency jsPackageDependency) {

		String packageName = jsPackageDependency.getPackageName();
		String versionConstraints = jsPackageDependency.getVersionConstraints();

		String cacheKey = StringBundler.concat(
			packageName, StringPool.UNDERLINE, versionConstraints);

		DataBag dataBag = _getDataBag();

		Map<String, JSPackage> dependencyJSPackages =
			dataBag._dependencyJSPackages;

		JSPackage jsPackage = dependencyJSPackages.get(cacheKey);

		if (jsPackage != null) {
			if (jsPackage == _NULL_JS_PACKAGE) {
				return null;
			}

			return jsPackage;
		}

		Range range = Range.from(versionConstraints, true);

		List<JSPackageVersion> jsPackageVersions = dataBag._jsPackageVersions;

		for (JSPackageVersion jsPackageVersion : jsPackageVersions) {
			JSPackage innerJSPackage = jsPackageVersion._jsPackage;
			Version version = jsPackageVersion._version;

			if (packageName.equals(innerJSPackage.getName()) &&
				range.test(version)) {

				jsPackage = innerJSPackage;

				break;
			}
		}

		if (jsPackage == null) {
			dependencyJSPackages.put(cacheKey, _NULL_JS_PACKAGE);
		}
		else {
			dependencyJSPackages.put(cacheKey, jsPackage);
		}

		return jsPackage;
	}

	@Override
	public NPMRegistryUpdate update() {
		return new NPMRegistryUpdateImpl(this);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_bundleTracker = new BundleTracker<>(
			_bundleContext, Bundle.ACTIVE,
			new NPMRegistryBundleTrackerCustomizer());

		_activation.set(Boolean.TRUE);

		_bundleTracker.open();

		_activation.set(Boolean.FALSE);

		Map<Bundle, JSBundle> tracked = _bundleTracker.getTracked();

		_setDataBagSupplier(() -> _createDataBag(tracked.values()));

		Details details = ConfigurableUtil.createConfigurable(
			Details.class, properties);

		_applyVersioning = details.applyVersioning();

		_serviceTracker = _openServiceTracker();
	}

	@Deactivate
	protected void deactivate() {
		if (_npmRegistryUpdatesListeners != null) {
			_npmRegistryUpdatesListeners.close();
		}

		_serviceTracker.close();

		_activation.set(Boolean.TRUE);

		_bundleTracker.close();

		_activation.set(Boolean.FALSE);
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		Details details = ConfigurableUtil.createConfigurable(
			Details.class, properties);

		if (details.applyVersioning() != _applyVersioning) {
			_applyVersioning = details.applyVersioning();

			_serviceTracker.close();

			_serviceTracker = _openServiceTracker();
		}
	}

	private DataBag _createDataBag(Collection<JSBundle> jsBundles) {
		if (jsBundles == null) {
			Map<Bundle, JSBundle> tracked = _bundleTracker.getTracked();

			jsBundles = tracked.values();
		}

		Map<String, JSModule> jsModules = new HashMap<>();
		Map<String, JSPackage> jsPackages = new HashMap<>();
		List<JSPackageVersion> jsPackageVersions = new ArrayList<>();
		Map<String, JSModule> resolvedJSModules = new HashMap<>();
		Map<String, JSPackage> resolvedJSPackages = new HashMap<>();
		Map<String, String> exactMatchMap = new HashMap<>();

		for (JSBundle jsBundle : jsBundles) {
			for (JSPackage jsPackage : jsBundle.getJSPackages()) {
				jsPackages.put(jsPackage.getId(), jsPackage);
				jsPackageVersions.add(new JSPackageVersion(jsPackage));

				String resolvedId = jsPackage.getResolvedId();

				resolvedJSPackages.put(resolvedId, jsPackage);

				exactMatchMap.put(
					resolvedId,
					ModuleNameUtil.getModuleResolvedId(
						jsPackage, jsPackage.getMainModuleName()));

				for (JSModuleAlias jsModuleAlias :
						jsPackage.getJSModuleAliases()) {

					String aliasResolvedId = ModuleNameUtil.getModuleResolvedId(
						jsPackage, jsModuleAlias.getAlias());

					exactMatchMap.put(
						aliasResolvedId,
						ModuleNameUtil.getModuleResolvedId(
							jsPackage, jsModuleAlias.getModuleName()));
				}

				for (JSModule jsModule : jsPackage.getJSModules()) {
					jsModules.put(jsModule.getId(), jsModule);
					resolvedJSModules.put(jsModule.getResolvedId(), jsModule);
				}
			}
		}

		Comparator<JSPackageVersion> comparator = Comparator.comparing(
			JSPackageVersion::getVersion);

		jsPackageVersions.sort(comparator.reversed());

		return new DataBag(
			exactMatchMap, jsModules, jsPackages, jsPackageVersions,
			resolvedJSModules, resolvedJSPackages);
	}

	private DataBag _getDataBag() {
		return _dataBagDCLSingleton.getSingleton(_dataBagSupplier);
	}

	private ServiceTrackerList<NPMRegistryUpdatesListener>
		_getNPMRegistryUpdatesListeners() {

		if (_npmRegistryUpdatesListeners == null) {
			_npmRegistryUpdatesListeners = ServiceTrackerListFactory.open(
				_bundleContext, NPMRegistryUpdatesListener.class);
		}

		return _npmRegistryUpdatesListeners;
	}

	private JSONObject _getPackageJSONObject(Bundle bundle) {
		try {
			URL url = bundle.getEntry("package.json");

			if (url == null) {
				return null;
			}

			return _jsonFactory.createJSONObject(URLUtil.toString(url));
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private void _notifyNPMRegistryUpdatesListeners() {
		for (NPMRegistryUpdatesListener npmRegistryUpdatesListener :
				_getNPMRegistryUpdatesListeners()) {

			npmRegistryUpdatesListener.onAfterUpdate();
		}
	}

	private ServiceTracker<ServletContext, JSConfigGeneratorPackage>
		_openServiceTracker() {

		return ServiceTrackerFactory.open(
			_bundleContext,
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
							_applyVersioning, serviceReference.getBundle(),
							(String)serviceReference.getProperty(
								"osgi.web.contextpath"));

					String jsConfigGeneratorPackageResolvedId =
						jsConfigGeneratorPackage.getName() + StringPool.AT +
							jsConfigGeneratorPackage.getVersion();

					_partialMatchMap.put(
						jsConfigGeneratorPackage.getName(),
						jsConfigGeneratorPackageResolvedId);

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

					_partialMatchMap.remove(jsConfigGeneratorPackage.getName());
				}

			});
	}

	private void _processLegacyBridges(Bundle bundle) {
		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		String jsSubmodulesBridge = GetterUtil.getString(
			headers.get("Liferay-JS-Submodules-Bridge"));

		if (Validator.isNotNull(jsSubmodulesBridge)) {
			String[] bridges = jsSubmodulesBridge.split(",");

			JSONObject packageJSONObject = _getPackageJSONObject(bundle);

			for (String bridge : bridges) {
				bridge = bridge.trim();

				_globalAliases.put(
					bridge,
					StringBundler.concat(
						packageJSONObject.getString("name"), StringPool.AT,
						packageJSONObject.getString("version"), "/bridge/",
						bridge));
			}
		}
	}

	private void _setDataBagSupplier(Supplier<DataBag> dataBagSupplier) {
		_dataBagSupplier = dataBagSupplier;
		_dataBagDCLSingleton.destroy(null);
	}

	private static final JSPackage _NULL_JS_PACKAGE =
		ProxyFactory.newDummyInstance(JSPackage.class);

	private static final Log _log = LogFactoryUtil.getLog(
		NPMRegistryImpl.class);

	private static final ThreadLocal<Boolean> _activation =
		new CentralizedThreadLocal<>(
			NPMRegistryImpl.class.getName() + "._activation",
			() -> Boolean.FALSE);

	private volatile Boolean _applyVersioning;
	private BundleContext _bundleContext;
	private BundleTracker<JSBundle> _bundleTracker;
	private final DCLSingleton<DataBag> _dataBagDCLSingleton =
		new DCLSingleton<>();
	private volatile Supplier<DataBag> _dataBagSupplier;
	private final Map<String, String> _globalAliases = new HashMap<>();

	@Reference
	private JSBundleProcessor _jsBundleProcessor;

	@Reference
	private JSONFactory _jsonFactory;

	private ServiceTrackerList<NPMRegistryUpdatesListener>
		_npmRegistryUpdatesListeners;
	private final Map<String, String> _partialMatchMap =
		new ConcurrentHashMap<>();
	private volatile ServiceTracker<ServletContext, JSConfigGeneratorPackage>
		_serviceTracker;

	private static class DataBag {

		private DataBag(
			Map<String, String> exactMatchMap, Map<String, JSModule> jsModules,
			Map<String, JSPackage> jsPackages,
			List<JSPackageVersion> jsPackageVersions,
			Map<String, JSModule> resolvedJSModules,
			Map<String, JSPackage> resolvedJSPackages) {

			_exactMatchMap = exactMatchMap;
			_jsModules = jsModules;
			_jsPackages = jsPackages;
			_jsPackageVersions = jsPackageVersions;
			_resolvedJSModules = resolvedJSModules;
			_resolvedJSPackages = resolvedJSPackages;
		}

		private final Map<String, JSPackage> _dependencyJSPackages =
			new ConcurrentHashMap<>();
		private final Map<String, String> _exactMatchMap;
		private final Map<String, JSModule> _jsModules;
		private final Map<String, JSPackage> _jsPackages;
		private final List<JSPackageVersion> _jsPackageVersions;
		private final Map<String, JSModule> _resolvedJSModules;
		private final Map<String, JSPackage> _resolvedJSPackages;

	}

	private static class JSPackageVersion {

		public Version getVersion() {
			return _version;
		}

		private JSPackageVersion(JSPackage jsPackage) {
			_jsPackage = jsPackage;

			_version = Version.from(jsPackage.getVersion(), true);
		}

		private final JSPackage _jsPackage;
		private final Version _version;

	}

	private class NPMRegistryBundleTrackerCustomizer
		implements BundleTrackerCustomizer<JSBundle> {

		@Override
		public JSBundle addingBundle(Bundle bundle, BundleEvent bundleEvent) {
			JSBundle jsBundle = _jsBundleProcessor.process(bundle);

			if (jsBundle == null) {
				return null;
			}

			_processLegacyBridges(bundle);

			if (!_activation.get()) {
				Map<Bundle, JSBundle> tracked = _bundleTracker.getTracked();

				Collection<JSBundle> jsBundles = new ArrayList<>(
					tracked.values());

				jsBundles.add(jsBundle);

				_setDataBagSupplier(() -> _createDataBag(jsBundles));

				_notifyNPMRegistryUpdatesListeners();

				NPMJavaScriptLastModifiedUtil.updateLastModified(
					bundle.getLastModified());
			}

			return jsBundle;
		}

		@Override
		public void modifiedBundle(
			Bundle bundle, BundleEvent bundleEvent, JSBundle jsBundle) {
		}

		@Override
		public void removedBundle(
			Bundle bundle, BundleEvent bundleEvent, JSBundle jsBundle) {

			if (!_activation.get()) {
				_setDataBagSupplier(() -> _createDataBag(null));

				_notifyNPMRegistryUpdatesListeners();
			}
		}

	}

}