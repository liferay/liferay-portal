/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.contributor;

import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.constants.FragmentExportImportConstants;
import com.liferay.fragment.exception.InvalidFragmentCompositionKeyException;
import com.liferay.fragment.model.FragmentComposition;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCompositionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoaderUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.ServletContext;

import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
public abstract class BaseFragmentCollectionContributor
	implements FragmentCollectionContributor {

	@Override
	public List<FragmentComposition> getFragmentCompositions() {
		_initialize();

		return Collections.unmodifiableList(_fragmentCompositions);
	}

	@Override
	public List<FragmentComposition> getFragmentCompositions(Locale locale) {
		return _getFragmentCompositions(getFragmentCompositions(), locale);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries() {
		_initialize();

		List<FragmentEntry> fragmentEntries = new ArrayList<>();

		for (Map.Entry<Integer, List<FragmentEntry>> entry :
				_fragmentEntries.entrySet()) {

			fragmentEntries.addAll(entry.getValue());
		}

		return fragmentEntries;
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int type) {
		_initialize();

		return _fragmentEntries.getOrDefault(type, Collections.emptyList());
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int type, Locale locale) {
		return _getFragmentEntries(getFragmentEntries(type), locale);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int[] types) {
		_initialize();

		List<FragmentEntry> fragmentEntries = new ArrayList<>();

		for (int type : types) {
			fragmentEntries.addAll(
				_fragmentEntries.getOrDefault(type, Collections.emptyList()));
		}

		return fragmentEntries;
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(int[] types, Locale locale) {
		return _getFragmentEntries(getFragmentEntries(types), locale);
	}

	@Override
	public List<FragmentEntry> getFragmentEntries(Locale locale) {
		return _getFragmentEntries(getFragmentEntries(), locale);
	}

	@Override
	public String getName() {
		_initialize();

		String name = _names.get(LocaleUtil.getDefault());

		if (Validator.isNotNull(name)) {
			return name;
		}

		return getFragmentCollectionKey();
	}

	@Override
	public String getName(Locale locale) {
		_initialize();

		String name = _names.get(locale);

		if (Validator.isNotNull(name)) {
			return name;
		}

		return getName();
	}

	@Override
	public Map<Locale, String> getNames() {
		_initialize();

		if (_names != null) {
			return Collections.unmodifiableMap(_names);
		}

		return Collections.emptyMap();
	}

	@Override
	public ResourceBundleLoader getResourceBundleLoader() {
		ServletContext servletContext = getServletContext();

		ResourceBundleLoader resourceBundleLoader =
			ResourceBundleLoaderUtil.
				getResourceBundleLoaderByServletContextName(
					servletContext.getServletContextName());

		if (resourceBundleLoader != null) {
			return resourceBundleLoader;
		}

		return ResourceBundleLoaderUtil.getPortalResourceBundleLoader();
	}

	public abstract ServletContext getServletContext();

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundle = bundleContext.getBundle();
	}

	protected void readAndCheckFragmentCollectionStructure() {
		try {
			Map<Locale, String> names = _getContributedCollectionNames();

			Enumeration<URL> fragmentEntriesEnumeration = _bundle.findEntries(
				StringPool.BLANK,
				FragmentExportImportConstants.FILE_NAME_FRAGMENT, true);

			Enumeration<URL> fragmentCompositionsEnumeration =
				_bundle.findEntries(
					StringPool.BLANK,
					FragmentExportImportConstants.
						FILE_NAME_FRAGMENT_COMPOSITION,
					true);

			_fragmentCompositionNames = new HashMap<>();
			_fragmentCompositions = new ArrayList<>();
			_fragmentEntries = new HashMap<>();
			_fragmentEntryNames = new HashMap<>();

			if (MapUtil.isEmpty(names) ||
				((fragmentCompositionsEnumeration != null) &&
				 !fragmentCompositionsEnumeration.hasMoreElements() &&
				 (fragmentEntriesEnumeration != null) &&
				 !fragmentEntriesEnumeration.hasMoreElements())) {

				return;
			}

			_names = names;

			if (fragmentEntriesEnumeration != null) {
				while (fragmentEntriesEnumeration.hasMoreElements()) {
					URL url = fragmentEntriesEnumeration.nextElement();

					FragmentEntry fragmentEntry = _getFragmentEntry(url);

					List<FragmentEntry> fragmentEntryList =
						_fragmentEntries.computeIfAbsent(
							fragmentEntry.getType(), type -> new ArrayList<>());

					fragmentEntryList.add(fragmentEntry);
				}
			}

			if (fragmentCompositionsEnumeration != null) {
				while (fragmentCompositionsEnumeration.hasMoreElements()) {
					URL url = fragmentCompositionsEnumeration.nextElement();

					_fragmentCompositions.add(_getFragmentComposition(url));
				}
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	@Reference
	protected FragmentCompositionLocalService fragmentCompositionLocalService;

	@Reference
	protected FragmentEntryLocalService fragmentEntryLocalService;

	private Map<Locale, String> _getContributedCollectionNames()
		throws Exception {

		Class<?> clazz = getClass();

		String json = StreamUtil.toString(
			clazz.getResourceAsStream(
				"dependencies/" +
					FragmentExportImportConstants.FILE_NAME_COLLECTION));

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(json);

		String name = jsonObject.getString("name");

		Map<Locale, String> names = new HashMap<>();

		_setLocalizedNames(name, names, getResourceBundleLoader());

		return names;
	}

	private FragmentComposition _getFragmentComposition(URL url)
		throws Exception {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			URLUtil.toString(url));

		String fragmentCompositionKey = jsonObject.getString(
			"fragmentCompositionKey");

		if (Validator.isNull(fragmentCompositionKey)) {
			throw new InvalidFragmentCompositionKeyException();
		}

		fragmentCompositionKey = StringBundler.concat(
			getFragmentCollectionKey(), "-composition-",
			jsonObject.getString("fragmentCompositionKey"));

		Map<Locale, String> names = _fragmentCompositionNames.getOrDefault(
			fragmentCompositionKey, new HashMap<>());

		String name = jsonObject.getString("name");

		_setLocalizedNames(name, names, getResourceBundleLoader());

		_fragmentCompositionNames.put(fragmentCompositionKey, names);

		String definition = _read(
			FileUtil.getPath(url.getPath()),
			jsonObject.getString("fragmentCompositionDefinitionPath"),
			"fragment-composition-definition.json");

		String thumbnailURL = _getImagePreviewURL(
			jsonObject.getString("thumbnail"));

		FragmentComposition fragmentComposition =
			fragmentCompositionLocalService.createFragmentComposition(0L);

		fragmentComposition.setCompanyId(CompanyConstants.SYSTEM);
		fragmentComposition.setFragmentCompositionKey(fragmentCompositionKey);
		fragmentComposition.setName(name);
		fragmentComposition.setData(definition);
		fragmentComposition.setIcon(
			jsonObject.getString("icon", "edit-layout"));
		fragmentComposition.setImagePreviewURL(thumbnailURL);

		return fragmentComposition;
	}

	private List<FragmentComposition> _getFragmentCompositions(
		List<FragmentComposition> fragmentCompositions, Locale locale) {

		for (FragmentComposition fragmentComposition : fragmentCompositions) {
			Map<Locale, String> names = _fragmentCompositionNames.getOrDefault(
				fragmentComposition.getFragmentCompositionKey(),
				Collections.emptyMap());

			fragmentComposition.setName(
				names.getOrDefault(
					locale,
					names.getOrDefault(
						LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
						fragmentComposition.getName())));
		}

		return fragmentCompositions;
	}

	private List<FragmentEntry> _getFragmentEntries(
		List<FragmentEntry> fragmentEntries, Locale locale) {

		for (FragmentEntry fragmentEntry : fragmentEntries) {
			Map<Locale, String> names = _fragmentEntryNames.getOrDefault(
				fragmentEntry.getFragmentEntryKey(), Collections.emptyMap());

			fragmentEntry.setName(
				names.getOrDefault(
					locale,
					names.getOrDefault(
						LocaleUtil.toLanguageId(LocaleUtil.getDefault()),
						fragmentEntry.getName())));
		}

		return fragmentEntries;
	}

	private FragmentEntry _getFragmentEntry(URL url) throws Exception {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
			URLUtil.toString(url));

		String fragmentEntryKey = StringBundler.concat(
			getFragmentCollectionKey(), StringPool.DASH,
			jsonObject.getString("fragmentEntryKey"));

		Map<Locale, String> names = _fragmentEntryNames.getOrDefault(
			fragmentEntryKey, new HashMap<>());

		String name = jsonObject.getString("name");

		_setLocalizedNames(name, names, getResourceBundleLoader());

		_fragmentEntryNames.put(fragmentEntryKey, names);

		String path = FileUtil.getPath(url.getPath());

		String css = _read(path, jsonObject.getString("cssPath"), "index.css");
		String html = _read(
			path, jsonObject.getString("htmlPath"), "index.html");
		String js = _read(path, jsonObject.getString("jsPath"), "index.js");

		boolean cacheable = jsonObject.getBoolean("cacheable");

		String configuration = _read(
			path, jsonObject.getString("configurationPath"),
			"configuration.json");

		if (Validator.isNull(configuration)) {
			configuration = _read(
				path, jsonObject.getString("configurationPath"), "index.json");
		}

		String thumbnailURL = _getImagePreviewURL(
			jsonObject.getString("thumbnail"));
		int type = FragmentConstants.getTypeFromLabel(
			jsonObject.getString("type"));

		FragmentEntry fragmentEntry =
			fragmentEntryLocalService.createFragmentEntry(0L);

		fragmentEntry.setCompanyId(CompanyConstants.SYSTEM);
		fragmentEntry.setFragmentEntryKey(fragmentEntryKey);
		fragmentEntry.setName(name);
		fragmentEntry.setCss(css);
		fragmentEntry.setHtml(html);
		fragmentEntry.setJs(js);
		fragmentEntry.setCacheable(cacheable);
		fragmentEntry.setConfiguration(configuration);
		fragmentEntry.setIcon(jsonObject.getString("icon", "code"));
		fragmentEntry.setType(type);
		fragmentEntry.setTypeOptions(jsonObject.getString("typeOptions"));
		fragmentEntry.setImagePreviewURL(thumbnailURL);

		return fragmentEntry;
	}

	private String _getImagePreviewURL(String fileName) {
		URL url = _bundle.getResource(
			"META-INF/resources/thumbnails/" + fileName);

		if (url == null) {
			return StringPool.BLANK;
		}

		ServletContext servletContext = getServletContext();

		return StringBundler.concat(
			PortalUtil.getPathProxy(), servletContext.getContextPath(),
			"/thumbnails/", fileName);
	}

	private void _initialize() {
		if (_initialized) {
			return;
		}

		synchronized (this) {
			if (_initialized) {
				return;
			}

			readAndCheckFragmentCollectionStructure();

			_initialized = true;
		}
	}

	private String _read(String path, String fileName, String defaultFileName)
		throws Exception {

		Class<?> clazz = getClass();

		StringBundler sb = new StringBundler(3);

		sb.append(path);
		sb.append("/");

		if (Validator.isNotNull(fileName)) {
			sb.append(fileName);
		}
		else {
			sb.append(defaultFileName);
		}

		InputStream inputStream = clazz.getResourceAsStream(sb.toString());

		if (inputStream != null) {
			return StringUtil.read(inputStream);
		}

		return StringPool.BLANK;
	}

	private void _setLocalizedNames(
		String name, Map<Locale, String> names,
		ResourceBundleLoader resourceBundleLoader) {

		Set<Locale> availableLocales = new HashSet<>(
			LanguageUtil.getAvailableLocales());

		availableLocales.add(LocaleUtil.getDefault());

		for (Locale locale : availableLocales) {
			if (Validator.isNotNull(name)) {
				String languageId = LocaleUtil.toLanguageId(locale);

				ResourceBundle resourceBundle =
					resourceBundleLoader.loadResourceBundle(
						LocaleUtil.fromLanguageId(languageId));

				names.put(
					LocaleUtil.fromLanguageId(languageId),
					LanguageUtil.get(resourceBundle, name, name));
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseFragmentCollectionContributor.class);

	private Bundle _bundle;
	private Map<String, Map<Locale, String>> _fragmentCompositionNames;
	private List<FragmentComposition> _fragmentCompositions;
	private Map<Integer, List<FragmentEntry>> _fragmentEntries;
	private Map<String, Map<Locale, String>> _fragmentEntryNames;
	private volatile boolean _initialized;
	private Map<Locale, String> _names;

}