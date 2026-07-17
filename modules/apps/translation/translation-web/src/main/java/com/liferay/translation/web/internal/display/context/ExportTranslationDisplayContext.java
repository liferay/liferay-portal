/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.display.context;

import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.localized.InfoLocalizedValue;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.url.URLBuilder;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.segments.service.SegmentsExperienceServiceUtil;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporter;
import com.liferay.translation.exporter.TranslationInfoItemFieldValuesExporterRegistry;
import com.liferay.translation.info.item.provider.InfoItemLanguagesProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Jorge González
 */
public class ExportTranslationDisplayContext {

	public ExportTranslationDisplayContext(
		long classNameId, long[] classPKs, long groupId,
		HttpServletRequest httpServletRequest,
		InfoItemServiceRegistry infoItemServiceRegistry,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse, List<Object> models,
		String title,
		TranslationInfoItemFieldValuesExporterRegistry
			translationInfoItemFieldValuesExporterRegistry) {

		_classNameId = classNameId;
		_classPKs = classPKs;
		_groupId = groupId;
		_httpServletRequest = httpServletRequest;
		_infoItemServiceRegistry = infoItemServiceRegistry;
		_liferayPortletResponse = liferayPortletResponse;
		_models = models;
		_title = title;
		_translationInfoItemFieldValuesExporterRegistry =
			translationInfoItemFieldValuesExporterRegistry;

		_className = PortalUtil.getClassName(classNameId);
		_themeDisplay = (ThemeDisplay)liferayPortletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public String getDisplayName(Locale currentLocale, Locale locale) {

		// LPS-138104

		String key = "language." + locale.getLanguage();

		String displayName = LanguageUtil.get(currentLocale, key);

		if (displayName.equals(key)) {
			return locale.getDisplayName(currentLocale);
		}

		return StringBundler.concat(
			displayName, " (", locale.getDisplayCountry(currentLocale), ")");
	}

	public List<Map<String, String>> getExperiences() throws PortalException {
		if (!Objects.equals(_className, Layout.class.getName())) {
			return null;
		}

		return TransformUtil.transform(
			_getSegmentsExperiences(),
			segmentsExperience -> HashMapBuilder.put(
				"label", segmentsExperience.getName(_themeDisplay.getLocale())
			).put(
				"segment",
				segmentsExperience.getSegmentsEntryName(
					_themeDisplay.getLocale())
			).put(
				"value",
				String.valueOf(segmentsExperience.getSegmentsExperienceId())
			).build());
	}

	public Map<String, Object> getExportTranslationData() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"availableExportFileFormats",
			() -> TransformUtil.transform(
				_translationInfoItemFieldValuesExporterRegistry.
					getTranslationInfoItemFieldValuesExporters(),
				this::_getExportFileFormatJSONObject)
		).put(
			"availableSourceLocales",
			_getLocalesJSONArray(
				_themeDisplay.getLocale(), _getAvailableSourceLocales())
		).put(
			"availableTargetLocales",
			_getLocalesJSONArray(
				_themeDisplay.getLocale(),
				LanguageUtil.getAvailableLocales(
					_themeDisplay.getSiteGroupId()))
		).put(
			"defaultSourceLanguageId", _getDefaultSourceLanguageId()
		).put(
			"experiences", getExperiences()
		).put(
			"exportTranslationURL", _getExportTranslationURLString()
		).put(
			"multipleExperiences", _isMultipleExperiences()
		).put(
			"multiplePagesSelected",
			() -> {
				if (_classPKs.length > 1) {
					return true;
				}

				return false;
			}
		).put(
			"pathModule", PortalUtil.getPathModule()
		).put(
			"redirectURL", PortalUtil.escapeRedirect(getRedirect())
		).build();
	}

	public String getRedirect() {
		if (Validator.isNotNull(_redirect)) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		return _redirect;
	}

	public String getTitle() throws PortalException {
		return _title;
	}

	private Set<Locale> _getAvailableSourceLocales() throws Exception {
		Set<Locale> availableSourceLocales = new HashSet<>();

		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, _className);

		List<String> languageIds = new ArrayList<>();

		for (Object model : _models) {
			_intersect(
				languageIds,
				Arrays.asList(
					infoItemLanguagesProvider.getAvailableLanguageIds(model)));
		}

		for (String languageId : languageIds) {
			availableSourceLocales.add(LocaleUtil.fromLanguageId(languageId));
		}

		availableSourceLocales.add(PortalUtil.getSiteDefaultLocale(_groupId));

		return availableSourceLocales;
	}

	private String _getDefaultSourceLanguageId() {
		InfoItemLanguagesProvider<Object> infoItemLanguagesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemLanguagesProvider.class, _className);

		if (infoItemLanguagesProvider == null) {
			return LanguageUtil.getLanguageId(
				_themeDisplay.getSiteDefaultLocale());
		}

		return infoItemLanguagesProvider.getDefaultLanguageId(_models.get(0));
	}

	private long _getDraftLayoutPlid(long classPK) {
		Layout layout = LayoutLocalServiceUtil.fetchLayout(classPK);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			return draftLayout.getPlid();
		}

		return classPK;
	}

	private JSONObject _getExportFileFormatJSONObject(
		TranslationInfoItemFieldValuesExporter
			translationInfoItemFieldValuesExporter) {

		return JSONUtil.put(
			"displayName",
			() -> {
				InfoLocalizedValue<String> labelInfoLocalizedValue =
					translationInfoItemFieldValuesExporter.
						getLabelInfoLocalizedValue();

				return labelInfoLocalizedValue.getValue(
					_themeDisplay.getLocale());
			}
		).put(
			"mimeType", translationInfoItemFieldValuesExporter.getMimeType()
		);
	}

	private String _getExportTranslationURLString() throws Exception {
		URLBuilder urlBuilder = URLBuilder.create(
			StringBundler.concat(
				_themeDisplay.getPortalURL(), PortalUtil.getPathContext(),
				Portal.PATH_MODULE, "/translation/export_translation")
		).addParameter(
			"classNameId", String.valueOf(_classNameId)
		);

		for (long classPK : _classPKs) {
			if (_className.equals(Layout.class.getName())) {
				urlBuilder.addParameter(
					"classPK", String.valueOf(_getDraftLayoutPlid(classPK)));
			}
			else {
				urlBuilder.addParameter("classPK", String.valueOf(classPK));
			}
		}

		urlBuilder.addParameter("groupId", String.valueOf(_groupId));

		return urlBuilder.build();
	}

	private JSONArray _getLocalesJSONArray(
		Locale locale, Collection<Locale> locales) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		locales.forEach(
			currentLocale -> jsonArray.put(
				JSONUtil.put(
					"displayName",
					LocaleUtil.getLocaleDisplayName(currentLocale, locale)
				).put(
					"languageId", LocaleUtil.toLanguageId(currentLocale)
				)));

		return jsonArray;
	}

	private List<SegmentsExperience> _getSegmentsExperiences()
		throws PortalException {

		List<SegmentsExperience> segmentsExperiences = new ArrayList<>();

		for (long classPK : _classPKs) {
			_intersect(
				segmentsExperiences,
				SegmentsExperienceServiceUtil.getSegmentsExperiences(
					_groupId, classPK, true));
		}

		return segmentsExperiences;
	}

	private <T> void _intersect(List<T> list1, List<T> list2) {
		if (list1.isEmpty()) {
			list1.addAll(list2);
		}
		else {
			list1.retainAll(list2);
		}
	}

	private boolean _isMultipleExperiences() {
		if (!_className.equals(Layout.class.getName())) {
			return false;
		}

		for (long classPK : _classPKs) {
			int segmentsExperiencesCount =
				SegmentsExperienceLocalServiceUtil.getSegmentsExperiencesCount(
					_groupId, classPK);

			if (segmentsExperiencesCount > 1) {
				return true;
			}
		}

		return false;
	}

	private final String _className;
	private final long _classNameId;
	private final long[] _classPKs;
	private final long _groupId;
	private final HttpServletRequest _httpServletRequest;
	private final InfoItemServiceRegistry _infoItemServiceRegistry;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final List<Object> _models;
	private String _redirect;
	private final ThemeDisplay _themeDisplay;
	private final String _title;
	private final TranslationInfoItemFieldValuesExporterRegistry
		_translationInfoItemFieldValuesExporterRegistry;

}