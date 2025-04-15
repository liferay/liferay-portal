/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.service.permission.PortalPermissionUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringParser;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.override.constants.PLOActionKeys;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;
import com.liferay.portal.language.override.web.internal.constants.PLOPortletKeys;
import com.liferay.portal.language.override.web.internal.display.LanguageItemDisplay;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Drew Brokke
 */
public class ViewDisplayContextFactory {

	public ViewDisplayContextFactory(
		PermissionCheckerFactory permissionCheckerFactory,
		PLOEntryLocalService ploEntryLocalService, Portal portal) {

		_permissionCheckerFactory = permissionCheckerFactory;
		_ploEntryLocalService = ploEntryLocalService;
		_portal = portal;
	}

	public ViewDisplayContext create(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ViewDisplayContext viewDisplayContext = new ViewDisplayContext();

		viewDisplayContext.setDisplayStyle(
			ParamUtil.getString(renderRequest, "displayStyle", "descriptive"));

		try {
			viewDisplayContext.setHasManageLanguageOverridesPermission(
				PortalPermissionUtil.contains(
					_permissionCheckerFactory.create(
						_portal.getUser(renderRequest)),
					PLOActionKeys.MANAGE_LANGUAGE_OVERRIDES));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		String selectedLanguageId = ParamUtil.getString(
			renderRequest, "selectedLanguageId",
			LanguageUtil.getLanguageId(_portal.getLocale(renderRequest)));

		viewDisplayContext.setSearchContainer(
			_createSearchContainer(
				renderRequest, renderResponse, selectedLanguageId));
		viewDisplayContext.setSelectedLanguageId(selectedLanguageId);
		viewDisplayContext.setTranslationLanguageDropdownItems(
			_getTranslationLanguageDropdownItems(
				_portal.getCurrentURL(renderRequest),
				renderResponse.getNamespace(), selectedLanguageId,
				LanguageUtil.getCompanyAvailableLocales(
					_portal.getCompanyId(renderRequest))));

		return viewDisplayContext;
	}

	private SearchContainer<LanguageItemDisplay> _createSearchContainer(
		RenderRequest renderRequest, RenderResponse renderResponse,
		String selectedLanguageId) {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);

		SearchContainer<LanguageItemDisplay> searchContainer =
			new SearchContainer<>(
				liferayPortletRequest,
				PortletURLUtil.getCurrent(
					liferayPortletRequest,
					_portal.getLiferayPortletResponse(renderResponse)),
				Arrays.asList("key", "value"),
				"no-language-entries-were-found");

		searchContainer.setId("portalLanguageOverrideEntries");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				liferayPortletRequest, PLOPortletKeys.PORTAL_LANGUAGE_OVERRIDE,
				"name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				liferayPortletRequest, PLOPortletKeys.PORTAL_LANGUAGE_OVERRIDE,
				"asc"));

		List<LanguageItemDisplay> languageItemDisplays =
			_getLanguageItemDisplays(
				_portal.getCompanyId(renderRequest),
				ParamUtil.getString(renderRequest, "navigation", "all"),
				ParamUtil.getString(renderRequest, "keywords"),
				selectedLanguageId);

		_sortLanguageItemDisplays(
			languageItemDisplays, searchContainer.getOrderByType());

		searchContainer.setResultsAndTotal(languageItemDisplays);

		return searchContainer;
	}

	private List<LanguageItemDisplay> _getAllLanguageItemDisplays(
		Predicate<String> keyMatchPredicate,
		Map<String, List<PLOEntry>> keyPLOEntriesMap, String selectedLanguageId,
		Predicate<String> valueMatchPredicate) {

		List<LanguageItemDisplay> languageItemDisplays = new ArrayList<>();

		ResourceBundle resourceBundle = LanguageResources.getResourceBundle(
			_toLocale(selectedLanguageId));

		for (String key : resourceBundle.keySet()) {
			String value = ResourceBundleUtil.getString(resourceBundle, key);

			if (!keyMatchPredicate.test(key) &&
				!valueMatchPredicate.test(value)) {

				continue;
			}

			LanguageItemDisplay languageItemDisplay = new LanguageItemDisplay(
				key, value);

			if (keyPLOEntriesMap.containsKey(key)) {
				languageItemDisplay.setOverride(true);

				Set<String> overrideLanguageIds = new TreeSet<>();

				for (PLOEntry ploEntry : keyPLOEntriesMap.get(key)) {
					overrideLanguageIds.add(ploEntry.getLanguageId());

					if (Objects.equals(
							selectedLanguageId, ploEntry.getLanguageId())) {

						languageItemDisplay.setOverrideSelectedLanguageId(true);
					}
				}

				languageItemDisplay.setOverrideLanguageIdsString(
					_getLanguageIdsString(
						overrideLanguageIds, selectedLanguageId));
			}

			languageItemDisplays.add(languageItemDisplay);
		}

		return languageItemDisplays;
	}

	private Map<String, List<PLOEntry>> _getKeyPLOEntriesMap(long companyId) {
		Map<String, List<PLOEntry>> keyPLOEntriesMap = new HashMap<>();

		for (PLOEntry ploEntry :
				_ploEntryLocalService.getPLOEntries(companyId)) {

			List<PLOEntry> ploEntries = keyPLOEntriesMap.computeIfAbsent(
				ploEntry.getKey(), key -> new ArrayList<>());

			ploEntries.add(ploEntry);
		}

		return keyPLOEntriesMap;
	}

	private String _getLanguageIdsString(
		Set<String> overrideLanguageIds, String selectedLanguageId) {

		if (overrideLanguageIds.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(
			(2 * overrideLanguageIds.size()) - 1);

		Iterator<String> iterator = overrideLanguageIds.iterator();

		while (iterator.hasNext()) {
			String overrideLanguageId = iterator.next();

			if (Objects.equals(selectedLanguageId, overrideLanguageId)) {
				sb.append(
					String.format("<strong>%s</strong>", overrideLanguageId));
			}
			else {
				sb.append(overrideLanguageId);
			}

			if (iterator.hasNext()) {
				sb.append(StringPool.COMMA_AND_SPACE);
			}
		}

		return sb.toString();
	}

	private List<LanguageItemDisplay> _getLanguageItemDisplays(
		long companyId, String filter, String keywords,
		String selectedLanguageId) {

		Predicate<String> keyMatchPredicate = _getPredicate(
			keywords, _keyMatchPatternFunction);
		Map<String, List<PLOEntry>> keyPLOEntriesMap = _getKeyPLOEntriesMap(
			companyId);
		Predicate<String> valueMatchPredicate = _getPredicate(
			keywords, _valueMatchPatternFunction);

		if (filter.equals("any-language")) {
			return _getOverrideLanguageItemDisplays(
				keyMatchPredicate, keyPLOEntriesMap, selectedLanguageId,
				valueMatchPredicate, false);
		}
		else if (filter.equals("selected-language")) {
			return _getOverrideLanguageItemDisplays(
				keyMatchPredicate, keyPLOEntriesMap, selectedLanguageId,
				valueMatchPredicate, true);
		}

		return _getAllLanguageItemDisplays(
			keyMatchPredicate, keyPLOEntriesMap, selectedLanguageId,
			valueMatchPredicate);
	}

	private List<LanguageItemDisplay> _getOverrideLanguageItemDisplays(
		Predicate<String> keyMatchPredicate,
		Map<String, List<PLOEntry>> keyPLOEntriesMap, String selectedLanguageId,
		Predicate<String> valueMatchPredicate,
		boolean limitToSelectedLanguage) {

		List<LanguageItemDisplay> languageItemDisplays = new ArrayList<>();

		Locale selectedLocale = _toLocale(selectedLanguageId);

		for (Map.Entry<String, List<PLOEntry>> entry :
				keyPLOEntriesMap.entrySet()) {

			String key = entry.getKey();

			String value = LanguageUtil.get(
				selectedLocale, key, StringPool.BLANK);

			if (keyMatchPredicate.test(key) ||
				valueMatchPredicate.test(value)) {

				LanguageItemDisplay languageItemDisplay =
					new LanguageItemDisplay(key, value);

				languageItemDisplay.setOverride(true);

				Set<String> overrideLanguageIds = new TreeSet<>();

				for (PLOEntry ploEntry : entry.getValue()) {
					overrideLanguageIds.add(ploEntry.getLanguageId());

					if (Objects.equals(
							selectedLanguageId, ploEntry.getLanguageId())) {

						languageItemDisplay.setOverrideSelectedLanguageId(true);
					}
				}

				if (limitToSelectedLanguage &&
					!languageItemDisplay.isOverrideSelectedLanguageId()) {

					continue;
				}

				languageItemDisplay.setOverrideLanguageIdsString(
					_getLanguageIdsString(
						overrideLanguageIds, selectedLanguageId));

				languageItemDisplays.add(languageItemDisplay);
			}
		}

		return languageItemDisplays;
	}

	private Predicate<String> _getPredicate(
		String keywords, Function<String, Pattern> patternFunction) {

		if (Validator.isBlank(keywords)) {
			return s -> true;
		}

		Pattern pattern = patternFunction.apply(keywords);

		return pattern.asPredicate();
	}

	private List<DropdownItem> _getTranslationLanguageDropdownItems(
		String currentURL, String namespace, String selectedLanguageId,
		Collection<Locale> availableLocales) {

		DropdownItemList dropdownItemList = new DropdownItemList();

		for (Locale locale : availableLocales) {
			String languageId = LanguageUtil.getLanguageId(locale);

			String icon = StringUtil.toLowerCase(
				TextFormatter.format(languageId, TextFormatter.O));

			dropdownItemList.add(
				dropdownItem -> {
					dropdownItem.put("symbolLeft", icon);
					dropdownItem.setActive(
						Objects.equals(selectedLanguageId, languageId));
					dropdownItem.setHref(
						HttpComponentsUtil.setParameter(
							currentURL, namespace + "selectedLanguageId",
							languageId));
					dropdownItem.setIcon(icon);
					dropdownItem.setLabel(
						TextFormatter.format(languageId, TextFormatter.O));
				});
		}

		return dropdownItemList;
	}

	private void _sortLanguageItemDisplays(
		List<LanguageItemDisplay> languageItemDisplays, String orderByType) {

		Comparator<LanguageItemDisplay> comparator = Comparator.comparing(
			LanguageItemDisplay::getKey);

		if (Objects.equals(orderByType, "desc")) {
			comparator = comparator.reversed();
		}

		languageItemDisplays.sort(comparator);
	}

	private Locale _toLocale(String languageId) {
		return LocaleUtil.fromLanguageId(languageId, true, true);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewDisplayContextFactory.class);

	private final Function<String, Pattern> _keyMatchPatternFunction =
		s -> Pattern.compile(
			".*" + StringParser.escapeRegex(s) + ".*",
			Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);
	private final PermissionCheckerFactory _permissionCheckerFactory;
	private final PLOEntryLocalService _ploEntryLocalService;
	private final Portal _portal;
	private final Function<String, Pattern> _valueMatchPatternFunction =
		s -> Pattern.compile(
			".*\\b" + StringParser.escapeRegex(s) + ".*",
			Pattern.CASE_INSENSITIVE + Pattern.UNICODE_CASE);

}