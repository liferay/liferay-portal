/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.loader.modules.extender.internal.npm.flat;

import com.liferay.frontend.js.loader.modules.extender.npm.JSBundle;
import com.liferay.frontend.js.loader.modules.extender.npm.JSBundleProcessor;
import com.liferay.frontend.js.loader.modules.extender.npm.JSModuleAlias;
import com.liferay.frontend.js.loader.modules.extender.npm.JSPackageDependency;
import com.liferay.frontend.js.loader.modules.extender.npm.ModuleNameUtil;
import com.liferay.petra.executor.PortalExecutorManager;
import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.URL;

import java.nio.ByteBuffer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides an implementation of {@link JSBundleProcessor} that assumes the
 * <code>flat</code> format for the OSGi bundles containing NPM packages.
 *
 * <p>
 * See this package's summary for an explanation of the <code>flat</code>
 * format.
 * </p>
 *
 * @author Iván Zaera
 */
@Component(service = JSBundleProcessor.class)
public class FlatNPMBundleProcessor implements JSBundleProcessor {

	@Override
	public JSBundle process(Bundle bundle) {
		URL url = bundle.getEntry("META-INF/resources/package.json");

		if (url == null) {
			return null;
		}

		Enumeration<URL> enumeration = bundle.findEntries(
			"META-INF/resources", "package.json", true);

		if (enumeration == null) {
			_log.error("No package.json files found in " + bundle);

			return null;
		}

		return new FlatJSBundle(
			bundle,
			flatJSBundle -> {
				URL manifestJSONURL = bundle.getEntry(
					"META-INF/resources/manifest.json");

				Map<URL, JSONObject> jsonObjects = _loadJSONObjects(
					bundle, enumeration, manifestJSONURL);

				JSONObject packagesJSONObject = _removeByURL(
					jsonObjects, manifestJSONURL);

				Manifest manifest = new Manifest(packagesJSONObject);

				JSONObject packageJSONObject = _removeByURL(jsonObjects, url);

				Map<URL, Collection<String>> moduleDependenciesMap =
					_loadModuleDependenciesMap(bundle);

				_processPackage(
					flatJSBundle, manifest, packageJSONObject, jsonObjects,
					moduleDependenciesMap, "/META-INF/resources", true);

				_processNodePackages(
					flatJSBundle, manifest, jsonObjects, moduleDependenciesMap);
			});
	}

	@Activate
	protected void activate() {
		_executorService = _portalExecutorManager.getPortalExecutor(
			FlatNPMBundleProcessor.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		_executorService.shutdownNow();
	}

	private String _canonicalizePath(String path) {
		int parents = 0;
		String[] parts = path.split(StringPool.SLASH);
		List<String> processedParts = new ArrayList<>();

		for (int i = parts.length - 1; i >= 0; i--) {
			String part = parts[i];

			if (part.equals(".")) {
				continue;
			}

			if (part.equals("..")) {
				parents++;
			}
			else {
				if (parents > 0) {
					parents--;
				}
				else {
					processedParts.add(part);
				}
			}
		}

		Collections.reverse(processedParts);

		StringBundler sb = new StringBundler((2 * processedParts.size()) - 1);

		for (String processedPart : processedParts) {
			if (sb.length() != 0) {
				sb.append(StringPool.SLASH);
			}

			sb.append(processedPart);
		}

		return sb.toString();
	}

	/**
	 * Get the arguments passed to the AMD define() call.
	 *
	 * @param  url URL of module file
	 * @return the arguments or <code>null</code> if not found or read failed
	 * @review
	 */
	private String _getDefineArgs(URL url) {
		try {
			String urlContent = _normalizeModuleContent(URLUtil.toString(url));

			int x = urlContent.indexOf("Liferay.Loader.define");

			if (x < 0) {
				return null;
			}

			x = urlContent.indexOf(CharPool.OPEN_BRACKET, x + 21);

			if (x < 0) {
				return null;
			}

			int y = urlContent.indexOf(CharPool.CLOSE_BRACKET, x + 1);

			if (y < 0) {
				return null;
			}

			return urlContent.substring(x + 1, y);
		}
		catch (IOException ioException) {
			_log.error("Unable to read URL: " + url, ioException);

			return null;
		}
	}

	private Map<URL, JSONObject> _loadJSONObjects(
		Bundle bundle, Enumeration<URL> enumeration, URL manifestJSONURL) {

		BundleContext bundleContext = bundle.getBundleContext();

		File cacheFile = bundleContext.getDataFile("cache_json_objects");

		if (cacheFile.exists()) {
			try {
				Deserializer deserializer = new Deserializer(
					ByteBuffer.wrap(FileUtil.getBytes(cacheFile)));

				if (deserializer.readLong() == bundle.getLastModified()) {
					Map<URL, JSONObject> jsonObjects = new HashMap<>();

					int size = deserializer.readInt();

					for (int i = 0; i < size; i++) {
						jsonObjects.put(
							new URL(deserializer.readString()),
							_jsonFactory.createJSONObject(
								deserializer.readString()));
					}

					return jsonObjects;
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to load cached JSON objects", exception);
				}
			}
		}

		List<Future<Map.Entry<URL, JSONObject>>> futures = new ArrayList<>();

		while (enumeration.hasMoreElements()) {
			URL packageJSONURL = enumeration.nextElement();

			futures.add(
				_executorService.submit(
					() -> new AbstractMap.SimpleImmutableEntry<>(
						packageJSONURL,
						_jsonFactory.createJSONObject(
							URLUtil.toString(packageJSONURL)))));
		}

		if (manifestJSONURL != null) {
			futures.add(
				_executorService.submit(
					() -> {
						String content = URLUtil.toString(manifestJSONURL);

						if (!content.contains("\"flags\"")) {
							return new AbstractMap.SimpleImmutableEntry<>(
								manifestJSONURL, null);
						}

						JSONObject jsonObject = _jsonFactory.createJSONObject(
							content);

						return new AbstractMap.SimpleImmutableEntry<>(
							manifestJSONURL,
							jsonObject.getJSONObject("packages"));
					}));
		}

		HashMap<URL, JSONObject> jsonObjects = new HashMap<>();

		for (Future<Map.Entry<URL, JSONObject>> future : futures) {
			try {
				Map.Entry<URL, JSONObject> entry = future.get();

				JSONObject jsonObject = entry.getValue();

				if (jsonObject != null) {
					jsonObjects.put(entry.getKey(), jsonObject);
				}
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		Serializer serializer = new Serializer();

		serializer.writeLong(bundle.getLastModified());

		serializer.writeInt(jsonObjects.size());

		for (Map.Entry<URL, JSONObject> entry : jsonObjects.entrySet()) {
			URL url = entry.getKey();

			serializer.writeString(url.toExternalForm());

			JSONObject jsonObject = entry.getValue();

			serializer.writeString(jsonObject.toString());
		}

		try (OutputStream outputStream = new FileOutputStream(cacheFile)) {
			serializer.writeTo(outputStream);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to write JSON objects cache file", exception);
			}
		}

		return jsonObjects;
	}

	private Map<URL, Collection<String>> _loadModuleDependenciesMap(
		Bundle bundle) {

		BundleContext bundleContext = bundle.getBundleContext();

		File cacheFile = bundleContext.getDataFile("cache_model_dependencies");

		if (cacheFile.exists()) {
			try {
				Deserializer deserializer = new Deserializer(
					ByteBuffer.wrap(FileUtil.getBytes(cacheFile)));

				if (deserializer.readLong() == bundle.getLastModified()) {
					Map<URL, Collection<String>> moduleDependenciesMap =
						new HashMap<>();

					int size = deserializer.readInt();

					for (int i = 0; i < size; i++) {
						moduleDependenciesMap.put(
							new URL(deserializer.readString()),
							Arrays.asList(
								StringUtil.split(deserializer.readString())));
					}

					return moduleDependenciesMap;
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to load cached model dependencies", exception);
				}
			}
		}

		Map<URL, Collection<String>> moduleDependenciesMap = new HashMap<>();

		Map<String, URL> jsURLsMap = new HashMap<>();

		Enumeration<URL> enumeration = bundle.findEntries(
			"META-INF/resources", "*.js", true);

		if (enumeration != null) {
			while (enumeration.hasMoreElements()) {
				URL jsURL = enumeration.nextElement();

				String path = jsURL.getPath();

				URL bundleURL = bundle.getEntry(path);

				if (bundleURL == null) {
					continue;
				}

				jsURLsMap.putIfAbsent(path, bundleURL);
			}
		}

		List<Future<Map.Entry<URL, Collection<String>>>>
			moduleDepedenciesFutures = new ArrayList<>();

		for (URL jsURL : jsURLsMap.values()) {
			moduleDepedenciesFutures.add(
				_executorService.submit(
					() -> new AbstractMap.SimpleImmutableEntry<>(
						jsURL,
						_parseModuleDependencies(_getDefineArgs(jsURL)))));
		}

		for (Future<Map.Entry<URL, Collection<String>>> future :
				moduleDepedenciesFutures) {

			try {
				Map.Entry<URL, Collection<String>> entry = future.get();

				moduleDependenciesMap.put(entry.getKey(), entry.getValue());
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}

		Serializer serializer = new Serializer();

		serializer.writeLong(bundle.getLastModified());

		serializer.writeInt(moduleDependenciesMap.size());

		for (Map.Entry<URL, Collection<String>> entry :
				moduleDependenciesMap.entrySet()) {

			URL url = entry.getKey();

			serializer.writeString(url.toExternalForm());

			serializer.writeString(StringUtil.merge(entry.getValue()));
		}

		try (OutputStream outputStream = new FileOutputStream(cacheFile)) {
			serializer.writeTo(outputStream);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to write model dependencies cache file", exception);
			}
		}

		return moduleDependenciesMap;
	}

	private String _normalizeModuleContent(String moduleContent) {
		moduleContent = StringUtil.replace(moduleContent, '\n', ' ');

		int index = moduleContent.indexOf("Liferay.Loader.define(");

		if (index == -1) {
			return StringPool.BLANK;
		}

		moduleContent = moduleContent.substring(index);

		index = 0;

		while (true) {
			index = moduleContent.indexOf("function", index);

			if ((index == -1) || (index >= moduleContent.length())) {
				return StringPool.BLANK;
			}

			char nextChar = moduleContent.charAt(index + 8);

			if (Character.isWhitespace(nextChar) || (nextChar == '(')) {
				moduleContent = moduleContent.substring(0, index);

				break;
			}

			index = index + 8;
		}

		return moduleContent;
	}

	/**
	 * Returns the dependencies of a module given its URL. The dependencies are
	 * parsed by reading the module's JavaScript code.
	 *
	 * @param  defineArgs the arguments to the AMD's define() call
	 * @return the dependencies of the module
	 */
	private Collection<String> _parseModuleDependencies(String defineArgs) {
		String[] dependencies = StringUtil.split(defineArgs);

		if ((dependencies.length == 1) && dependencies[0].equals("")) {
			return Collections.emptyList();
		}

		for (int i = 0; i < dependencies.length; i++) {
			dependencies[i] = dependencies[i].trim();

			dependencies[i] = StringUtil.unquote(dependencies[i]);
		}

		return Arrays.asList(dependencies);
	}

	/**
	 * Processes the <code>dependencies</code> type entry of a
	 * <code>package.json</code> file and adds them to the {@link
	 * FlatJSPackage}.
	 *
	 * @param flatJSPackage the NPM package descriptor
	 * @param packageJSONObject the parsed <code>package.json</code>
	 * @param key the key of the <code>dependencies</code> type property
	 */
	private void _processDependencies(
		FlatJSPackage flatJSPackage, JSONObject packageJSONObject, String key) {

		JSONObject dependenciesJSONObject = packageJSONObject.getJSONObject(
			key);

		if (dependenciesJSONObject != null) {
			Iterator<String> iterator = dependenciesJSONObject.keys();

			while (iterator.hasNext()) {
				String dependencyName = iterator.next();

				String versionConstraints = dependenciesJSONObject.getString(
					dependencyName);

				flatJSPackage.addJSPackageDependency(
					new JSPackageDependency(
						flatJSPackage, dependencyName, versionConstraints));
			}
		}
	}

	private void _processModuleAliases(
		FlatJSPackage flatJSPackage, String location,
		Map<URL, JSONObject> jsonObjects,
		Map<URL, Collection<String>> moduleDependenciesMap) {

		Set<String> processedFolderPaths = new HashSet<>();

		for (Map.Entry<URL, JSONObject> entry : jsonObjects.entrySet()) {
			URL url = entry.getKey();

			String filePath = url.getPath();

			if (!filePath.startsWith(location)) {
				continue;
			}

			filePath = filePath.substring(location.length());

			if (filePath.equals("/package.json")) {
				continue;
			}

			JSONObject jsonObject = entry.getValue();

			String main = jsonObject.getString("main", null);

			if (Validator.isNull(main)) {
				continue;
			}

			String folderPath = filePath.substring(
				0, filePath.lastIndexOf(StringPool.SLASH));

			String alias = folderPath.substring(1);

			if (!main.startsWith(StringPool.SLASH)) {
				main = _canonicalizePath(alias + StringPool.SLASH + main);
			}

			main = ModuleNameUtil.toModuleName(main);

			JSModuleAlias jsModuleAlias = new JSModuleAlias(
				flatJSPackage, main, alias);

			flatJSPackage.addJSModuleAlias(jsModuleAlias);

			processedFolderPaths.add(folderPath);
		}

		for (URL url : moduleDependenciesMap.keySet()) {
			String folderPath = url.getPath();

			if (!folderPath.startsWith(location) ||
				!folderPath.endsWith("/index.js")) {

				continue;
			}

			folderPath = folderPath.substring(location.length());

			folderPath = folderPath.substring(
				0, folderPath.lastIndexOf(StringPool.SLASH));

			if (folderPath.isEmpty()) {
				continue;
			}

			String alias = folderPath.substring(1);

			if (processedFolderPaths.contains(folderPath)) {
				continue;
			}

			flatJSPackage.addJSModuleAlias(
				new JSModuleAlias(flatJSPackage, alias + "/index", alias));
		}
	}

	/**
	 * Processes the modules of a package and adds them to their {@link
	 * FlatJSPackage} descriptor.
	 *
	 * @param flatJSPackage the NPM package descriptor
	 * @param location the bundle's relative path of the package folder
	 */
	private void _processModules(
		FlatJSPackage flatJSPackage, Manifest manifest, String location,
		Map<URL, Collection<String>> moduleDependenciesMap, boolean root) {

		String nodeModulesPath = location + "/node_modules/";

		for (Map.Entry<URL, Collection<String>> entry :
				moduleDependenciesMap.entrySet()) {

			URL url = entry.getKey();

			String path = url.getPath();

			if (!path.startsWith(location) ||
				path.startsWith(nodeModulesPath)) {

				continue;
			}

			Collection<String> dependencies = entry.getValue();

			if (dependencies == null) {
				continue;
			}

			String fileName = path.substring(location.length() + 1);

			String name = ModuleNameUtil.toModuleName(fileName);

			String packageId =
				root ? StringPool.SLASH : flatJSPackage.getResolvedId();

			FlatJSModule flatJSModule = new FlatJSModule(
				flatJSPackage, name, dependencies,
				manifest.getFlagsJSONObject(packageId, fileName));

			if (_log.isDebugEnabled()) {
				_log.debug("Adding NPM module: " + flatJSModule);
			}

			flatJSPackage.addJSModule(flatJSModule);
		}
	}

	/**
	 * Processes the bundle's packages and adds them to their {@link
	 * FlatJSBundle} descriptor.
	 *
	 * @param flatJSBundle the bundle containing the node packages
	 */
	private void _processNodePackages(
		FlatJSBundle flatJSBundle, Manifest manifest,
		Map<URL, JSONObject> jsonObjects,
		Map<URL, Collection<String>> moduleDependenciesMap) {

		for (Map.Entry<URL, JSONObject> entry : jsonObjects.entrySet()) {
			URL url = entry.getKey();

			String path = url.getPath();

			String location = path.substring(1, path.length() - 13);

			String[] parts = location.split(StringPool.SLASH);

			String lastFolderPath = parts[parts.length - 2];

			if (!lastFolderPath.equals("node_modules")) {
				continue;
			}

			_processPackage(
				flatJSBundle, manifest, entry.getValue(), jsonObjects,
				moduleDependenciesMap, StringPool.SLASH.concat(location),
				false);
		}
	}

	/**
	 * Processes a the bundle's package and adds it to its {@link FlatJSBundle}
	 * descriptor.
	 *
	 * @param flatJSBundle the bundle containing the package
	 * @param location the bundle's relative path to a <code>package.json</code>
	 *        file
	 */
	private void _processPackage(
		FlatJSBundle flatJSBundle, Manifest manifest,
		JSONObject packageJSONObject, Map<URL, JSONObject> jsonObjects,
		Map<URL, Collection<String>> moduleDependenciesMap, String location,
		boolean root) {

		String name = packageJSONObject.getString("name");

		if (Validator.isNull(name)) {
			return;
		}

		String version = packageJSONObject.getString("version");

		if (Validator.isNull(version)) {
			return;
		}

		String mainModuleName = null;

		String main = packageJSONObject.getString("main");

		if (Validator.isNull(main)) {
			mainModuleName = "index";
		}
		else {
			mainModuleName = ModuleNameUtil.toModuleName(main);

			if (mainModuleName.startsWith("./")) {
				mainModuleName = mainModuleName.substring(2);
			}
		}

		FlatJSPackage flatJSPackage = new FlatJSPackage(
			flatJSBundle, name, version, mainModuleName, root);

		if (_log.isInfoEnabled()) {
			_log.info("Adding NPM package: " + flatJSPackage);
		}

		_processDependencies(flatJSPackage, packageJSONObject, "dependencies");

		_processDependencies(
			flatJSPackage, packageJSONObject, "peerDependencies");

		_processModules(
			flatJSPackage, manifest, location, moduleDependenciesMap, root);

		if (!root) {
			_processModuleAliases(
				flatJSPackage, location, jsonObjects, moduleDependenciesMap);
		}

		flatJSBundle.addJSPackage(flatJSPackage);
	}

	private JSONObject _removeByURL(Map<URL, JSONObject> jsonObjects, URL url) {
		JSONObject jsonObject = jsonObjects.remove(url);

		if (jsonObject != null) {
			return jsonObject;
		}

		Set<Map.Entry<URL, JSONObject>> entries = jsonObjects.entrySet();

		Iterator<Map.Entry<URL, JSONObject>> iterator = entries.iterator();

		while (iterator.hasNext()) {
			Map.Entry<URL, JSONObject> entry = iterator.next();

			URL entryURL = entry.getKey();

			if (Objects.equals(url.getPath(), entryURL.getPath()) &&
				Objects.equals(
					_trimFwkHash(url.getHost()),
					_trimFwkHash(entryURL.getHost()))) {

				iterator.remove();

				return entry.getValue();
			}
		}

		return null;
	}

	private String _trimFwkHash(String host) {
		int index = host.indexOf(".fwk");

		if (index != -1) {
			return host.substring(0, index);
		}

		return host;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FlatNPMBundleProcessor.class);

	private ExecutorService _executorService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private PortalExecutorManager _portalExecutorManager;

}