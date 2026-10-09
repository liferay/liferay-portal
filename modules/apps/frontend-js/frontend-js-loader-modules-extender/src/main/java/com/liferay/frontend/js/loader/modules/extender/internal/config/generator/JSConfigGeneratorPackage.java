/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.config.generator;

import com.liferay.frontend.js.loader.modules.extender.internal.configuration.Details;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

/**
 * @author Carlos Sierra Andrés
 */
public class JSConfigGeneratorPackage {

	public JSConfigGeneratorPackage(
		boolean applyVersioning, Bundle bundle, String contextPath) {

		_applyVersioning = applyVersioning;
		_bundle = bundle;
		_contextPath = contextPath;

		String jsVersion = String.valueOf(bundle.getVersion());

		int index = jsVersion.indexOf(".hotfix");

		if (index > -1) {
			jsVersion = jsVersion.substring(0, index);
		}

		_version = jsVersion;

		BundleWiring bundleWiring = _bundle.adapt(BundleWiring.class);

		List<BundleCapability> bundleCapabilities =
			bundleWiring.getCapabilities(Details.OSGI_WEBRESOURCE);

		if (bundleCapabilities.isEmpty()) {
			_name = _bundle.getSymbolicName();

			return;
		}

		BundleCapability bundleCapability = bundleCapabilities.get(0);

		Map<String, Object> attributes = bundleCapability.getAttributes();

		_name = (String)attributes.get(Details.OSGI_WEBRESOURCE);

		URL url = _bundle.getEntry(Details.CONFIG_JSON);

		_urlToConfiguration(url, bundleWiring);
	}

	/**
	 * Returns the path to the web context containing the package (usually
	 * something like '/o/web-context-name').
	 *
	 * @review
	 */
	public String getContextPath() {
		return _contextPath;
	}

	public List<JSConfigGeneratorModule> getJSConfigGeneratorModules() {
		return _jsConfigGeneratorModules;
	}

	/**
	 * Returns the name of the package, which is defined by the {@link
	 * Details#OSGI_WEBRESOURCE} property provided by the OSGi bundle and
	 * usually looks like 'osgi-bundle-name'.
	 *
	 * Note that the name of the package may be different from the one that its
	 * modules use. For example, you may have a package named 'my-osgi-bundle'
	 * that exports modules with ID 'my-js-package@2.0.0/path/to/my-module' (it
	 * all depends on what is in the MANIFEST.MF and config.json files).
	 *
	 * This is because even though the tools used to build legacy JARs had a
	 * convention for names, it is not enforced by this code so, to be safe,
	 * we interpret and use each attribute strictly.
	 *
	 * @review
	 */
	public String getName() {
		return _name;
	}

	public String getUnversionedConfiguration() {
		return _unversionedConfiguration;
	}

	public String getUnversionedMapsConfiguration() {
		return _unversionedMapsConfiguration;
	}

	public String getVersion() {
		return _version;
	}

	public String getVersionedConfiguration() {
		return _versionedConfiguration;
	}

	private JSONObject _generateConfigurationJSONObject(
		JSONObject jsonObject, BundleWiring bundleWiring,
		boolean versionedModuleName) {

		if (!_applyVersioning) {
			if (versionedModuleName) {
				return new JSONObject();
			}

			return jsonObject;
		}

		JSONArray namesJSONArray = jsonObject.names();

		if (namesJSONArray == null) {
			return jsonObject;
		}

		List<BundleWire> bundleWires = bundleWiring.getRequiredWires(
			Details.OSGI_WEBRESOURCE);

		for (int i = 0; i < namesJSONArray.length(); i++) {
			String name = (String)namesJSONArray.get(i);

			int index = name.indexOf(StringPool.SLASH);

			if (index == -1) {
				continue;
			}

			String moduleName = name.substring(0, index);

			if (!moduleName.equals(getName())) {
				continue;
			}

			String modulePath = name.substring(index);

			moduleName = StringBundler.concat(
				getName(), StringPool.AT, getVersion(), modulePath);

			JSONObject nameJSONObject = jsonObject.getJSONObject(name);

			JSONArray dependenciesJSONArray = nameJSONObject.getJSONArray(
				"dependencies");

			for (int j = 0; j < dependenciesJSONArray.length(); j++) {
				String dependency = dependenciesJSONArray.getString(j);

				index = dependency.indexOf('/');

				if (index == -1) {
					continue;
				}

				String dependencyName = dependency.substring(0, index);
				String dependencyPath = dependency.substring(index);

				if (dependencyName.equals(getName())) {
					dependencyName = StringBundler.concat(
						getName(), StringPool.AT, getVersion(), dependencyPath);

					dependenciesJSONArray.put(j, dependencyName);
				}
				else {
					_normalizeDependencies(
						dependencyName, dependencyPath, dependenciesJSONArray,
						j, bundleWires);
				}
			}

			if (versionedModuleName) {
				jsonObject.remove(name);

				jsonObject.put(moduleName, nameJSONObject);
			}
			else {
				jsonObject.put(name, nameJSONObject);
			}
		}

		return jsonObject;
	}

	private JSONObject _generateMapsConfigurationJSONObject(
		String configuration, String[] jsSubmodulesExport) {

		JSONObject mapsConfigurationJSONObject = new JSONObject();

		boolean exportAll = ArrayUtil.contains(
			jsSubmodulesExport, StringPool.STAR);

		JSONObject configurationJSONObject = new JSONObject(
			StringPool.OPEN_CURLY_BRACE + configuration +
				StringPool.CLOSE_CURLY_BRACE);

		JSONArray namesJSONArray = configurationJSONObject.names();

		for (int i = 0; i < namesJSONArray.length(); i++) {
			String name = (String)namesJSONArray.get(i);

			int x = name.indexOf(StringPool.SLASH);

			String moduleRootPath = name.substring(0, x + 1);

			String submodulePath = name.substring(x + 1);

			int index = submodulePath.indexOf(StringPool.SLASH);

			if (index == -1) {
				continue;
			}

			String submoduleName = submodulePath.substring(0, index);

			if (!exportAll &&
				!ArrayUtil.exists(
					jsSubmodulesExport,
					item -> _matchesWildcard(submoduleName, item))) {

				continue;
			}

			mapsConfigurationJSONObject.put(
				submoduleName, moduleRootPath.concat(submoduleName));
		}

		return mapsConfigurationJSONObject;
	}

	private boolean _matchesWildcard(String text, String pattern) {
		pattern = StringUtil.replace(
			pattern, new String[] {StringPool.QUESTION, StringPool.STAR},
			new String[] {".?", ".*"});

		return text.matches(pattern);
	}

	private void _normalizeDependencies(
		String dependencyName, String dependencyPath, JSONArray jsonArray,
		int index, List<BundleWire> bundleWires) {

		for (BundleWire bundleWire : bundleWires) {
			BundleCapability bundleCapability = bundleWire.getCapability();

			Map<String, Object> attributes = bundleCapability.getAttributes();

			String attributesDependencyName = (String)attributes.get(
				Details.OSGI_WEBRESOURCE);

			if (!attributesDependencyName.equals(dependencyName)) {
				continue;
			}

			Version version = (Version)attributes.get(
				Constants.VERSION_ATTRIBUTE);

			dependencyName = StringBundler.concat(
				dependencyName, StringPool.AT, version, dependencyPath);

			jsonArray.put(index, dependencyName);

			return;
		}
	}

	private void _populateJSConfigGeneratorModules(
		JSONObject unversionedConfigurationJSONObject) {

		for (Object key : unversionedConfigurationJSONObject.keySet()) {
			String moduleId = (String)key;

			List<String> dependencies = new ArrayList<>();

			JSONObject moduleJSONObject =
				unversionedConfigurationJSONObject.getJSONObject(moduleId);

			JSONArray dependenciesJSONArray = moduleJSONObject.getJSONArray(
				"dependencies");

			for (int i = 0; i < dependenciesJSONArray.length(); i++) {
				dependencies.add((String)dependenciesJSONArray.get(i));
			}

			_jsConfigGeneratorModules.add(
				new JSConfigGeneratorModule(
					this, moduleId, dependencies, _contextPath));
		}
	}

	private String _removeEnclosingCurlyBraces(JSONObject jsonObject) {
		String json = jsonObject.toString();

		return json.substring(1, json.length() - 1);
	}

	private void _urlToConfiguration(URL url, BundleWiring bundleWiring) {
		if (url == null) {
			return;
		}

		try (InputStream inputStream = url.openStream();

			Reader reader = new InputStreamReader(inputStream)) {

			JSONTokener jsonTokener = new JSONTokener(reader);

			JSONObject jsonObject = new JSONObject(jsonTokener);

			JSONObject unversionedConfigurationJSONObject =
				_generateConfigurationJSONObject(
					jsonObject, bundleWiring, false);

			_unversionedConfiguration = _removeEnclosingCurlyBraces(
				unversionedConfigurationJSONObject);

			_versionedConfiguration = _removeEnclosingCurlyBraces(
				_generateConfigurationJSONObject(
					jsonObject, bundleWiring, true));

			Dictionary<String, String> headers = _bundle.getHeaders(
				StringPool.BLANK);

			String jsSubmodulesExport = GetterUtil.getString(
				headers.get("Liferay-JS-Submodules-Export"));

			if (Validator.isNotNull(jsSubmodulesExport)) {
				_unversionedMapsConfiguration = _removeEnclosingCurlyBraces(
					_generateMapsConfigurationJSONObject(
						_unversionedConfiguration,
						StringUtil.split(jsSubmodulesExport)));

				if (_log.isWarnEnabled()) {
					_log.warn(
						"Liferay-JS-Submodules-Export is deprecated and " +
							"replaced with Liferay-JS-Submodules-Bridge");
				}
			}

			_populateJSConfigGeneratorModules(
				unversionedConfigurationJSONObject);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSConfigGeneratorPackage.class);

	private final boolean _applyVersioning;
	private final Bundle _bundle;
	private final String _contextPath;
	private final List<JSConfigGeneratorModule> _jsConfigGeneratorModules =
		new ArrayList<>();
	private final String _name;
	private String _unversionedConfiguration = "";
	private String _unversionedMapsConfiguration = "";
	private final String _version;
	private String _versionedConfiguration = "";

}