/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.content.processor.test.util;

import com.liferay.exportimport.internal.content.processor.test.DefaultExportImportContentProcessorTest;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.VirtualLayoutConstants;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Carlos Correa
 */
public class ExportImportContentProcessorTestUtil {

	public static String getContent(String fileName) throws Exception {
		Class<?> clazz = DefaultExportImportContentProcessorTest.class;

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/" + fileName);

		Scanner scanner = new Scanner(inputStream);

		scanner.useDelimiter("\\Z");

		return scanner.next();
	}

	public static Locale getNondefaultLocale() throws Exception {
		Locale defaultLocale = LocaleUtil.getDefault();

		for (Locale locale : _locales) {
			if (!locale.equals(defaultLocale)) {
				return locale;
			}
		}

		throw new Exception("Unable to get nondefault locale");
	}

	public static List<String> getURLs(String content) {
		Matcher matcher = _pattern.matcher(StringPool.BLANK);

		return TransformUtil.transformToList(
			StringUtil.split(content, StringPool.NEW_LINE),
			line -> {
				matcher.reset(line);

				if (matcher.find()) {
					return line;
				}

				return null;
			});
	}

	public static String replaceParameters(
			Group externalGroup,
			ObjectValuePair<Layout, Layout> externalPrivatePublicLayout,
			FileEntry fileEntry, String fileName, Group sourceGroup,
			ObjectValuePair<Layout, Layout> sourcePrivatePublicLayout,
			Group targetGroup,
			ObjectValuePair<Layout, Layout> targetPrivatePublicLayout)
		throws Exception {

		String content = getContent(fileName);

		Company company = CompanyLocalServiceUtil.fetchCompany(
			fileEntry.getCompanyId());

		content = _replaceExternalGroupFriendlyURLs(content);
		content = _replaceMultiLocaleLayoutFriendlyURLs(content);

		String externalPrivateLayoutFriendlyURL = "";
		String externalPublicLayoutFriendlyURL = "";

		if (externalPrivatePublicLayout != null) {
			externalPrivateLayoutFriendlyURL = _getFriendlyURL(
				externalPrivatePublicLayout.getKey());

			externalPublicLayoutFriendlyURL = _getFriendlyURL(
				externalPrivatePublicLayout.getValue());
		}

		Locale nondefaultLocale = getNondefaultLocale();

		String sourcePrivateLayoutFriendlyURL = "";
		String sourcePrivateLayoutLocaleFriendlyURL = "";
		String sourcePublicLayoutFriendlyURL = "";
		String sourcePublicLayoutLocaleFriendlyURL = "";

		if (sourcePrivatePublicLayout != null) {
			Layout sourcePrivateLayout = sourcePrivatePublicLayout.getKey();

			sourcePrivateLayoutFriendlyURL = _getFriendlyURL(
				sourcePrivateLayout);

			Map<Locale, String> sourcePrivateLayoutFriendlyURLMap =
				sourcePrivateLayout.getFriendlyURLMap();

			sourcePrivateLayoutLocaleFriendlyURL =
				sourcePrivateLayoutFriendlyURLMap.get(nondefaultLocale);

			Layout sourcePublicLayout = sourcePrivatePublicLayout.getValue();

			sourcePublicLayoutFriendlyURL = _getFriendlyURL(sourcePublicLayout);

			Map<Locale, String> sourcePublicLayoutFriendlyURLMap =
				sourcePublicLayout.getFriendlyURLMap();

			sourcePublicLayoutLocaleFriendlyURL =
				sourcePublicLayoutFriendlyURLMap.get(nondefaultLocale);
		}

		LayoutSet sourcePrivateLayoutSet = sourceGroup.getPrivateLayoutSet();
		LayoutSet sourcePublicLayoutSet = sourceGroup.getPublicLayoutSet();

		String targetPublicLayoutFriendlyURL = "";
		String targetPublicLayoutLocaleFriendlyURL = "";

		if (targetPrivatePublicLayout != null) {
			Layout targetPublicLayout = targetPrivatePublicLayout.getValue();

			targetPublicLayoutFriendlyURL = _getFriendlyURL(targetPublicLayout);

			Map<Locale, String> targetPublicLayoutFriendlyURLMap =
				targetPublicLayout.getFriendlyURLMap();

			targetPublicLayoutLocaleFriendlyURL =
				targetPublicLayoutFriendlyURLMap.get(nondefaultLocale);
		}

		content = StringUtil.replace(
			content,
			new String[] {
				"[$BLOG_ENTRY_DISPLAY_SERVLET_MAPPING$]",
				"[$CANONICAL_URL_SEPARATOR$]", "[$CONTROL_PANEL_FRIENDLY_URL$]",
				"[$CONTROL_PANEL_LAYOUT_FRIENDLY_URL$]",
				"[$DL_ENTRY_DISPLAY_SERVLET_MAPPING$]",
				"[$EXTERNAL_GROUP_FRIENDLY_URL$]",
				"[$EXTERNAL_PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$EXTERNAL_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$FILE_ENTRY_FRIENDLY_URL$]", "[$FILE_NAME$]",
				"[$FRIENDLY_URL_SEPARATOR$]", "[$GROUP_FRIENDLY_URL$]",
				"[$GROUP_ID$]", "[$GROUP_NAME$]",
				"[$GROUP_PRIVATE_PAGES_VIRTUAL_HOST$]",
				"[$GROUP_PUBLIC_PAGES_VIRTUAL_HOST$]", "[$IMAGE_ID$]",
				"[$LIVE_GROUP_FRIENDLY_URL$]", "[$LIVE_GROUP_ID$]",
				"[$LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$NONREPLACEABLE_PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$NONREPLACEABLE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$NON_DEFAULT_LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$NON_DEFAULT_PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$NON_DEFAULT_PUBLIC_LAYOUT_FRIENDLY_URL$]",
				"[$PATH_CONTEXT$]", "[$PATH_FRIENDLY_URL_PRIVATE_GROUP$]",
				"[$PATH_FRIENDLY_URL_PRIVATE_USER$]",
				"[$PATH_FRIENDLY_URL_PUBLIC$]",
				"[$PRIVATE_LAYOUT_FRIENDLY_URL$]",
				"[$PUBLIC_LAYOUT_FRIENDLY_URL$]", "[$TITLE$]", "[$UUID$]",
				"[$WEB_CONTENT_DISPLAY_SERVLET_MAPPING$]", "[$WEB_ID$]"
			},
			new String[] {
				FriendlyURLResolverConstants.URL_SEPARATOR_X_BLOGS_ENTRY,
				VirtualLayoutConstants.CANONICAL_URL_SEPARATOR,
				GroupConstants.CONTROL_PANEL_FRIENDLY_URL,
				PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL,
				FriendlyURLResolverConstants.URL_SEPARATOR_X_FILE_ENTRY,
				externalGroup.getFriendlyURL(),
				externalPrivateLayoutFriendlyURL,
				externalPublicLayoutFriendlyURL,
				FriendlyURLNormalizerUtil.normalizeWithPeriodsAndSlashes(
					fileEntry.getTitle()),
				fileEntry.getFileName(), Portal.FRIENDLY_URL_SEPARATOR,
				sourceGroup.getFriendlyURL(),
				String.valueOf(fileEntry.getGroupId()),
				StringUtil.removeFirst(
					sourceGroup.getFriendlyURL(), StringPool.SLASH),
				PortalUtil.getDefaultVirtualHostname(
					false, sourcePrivateLayoutSet),
				PortalUtil.getDefaultVirtualHostname(
					false, sourcePublicLayoutSet),
				String.valueOf(fileEntry.getFileEntryId()),
				targetGroup.getFriendlyURL(),
				String.valueOf(targetGroup.getGroupId()),
				targetPublicLayoutFriendlyURL, sourcePrivateLayoutFriendlyURL,
				sourcePublicLayoutFriendlyURL,
				targetPublicLayoutLocaleFriendlyURL,
				sourcePrivateLayoutLocaleFriendlyURL,
				sourcePublicLayoutLocaleFriendlyURL,
				PortalUtil.getPathContext(),
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_GROUP_SERVLET_MAPPING,
				PropsValues.LAYOUT_FRIENDLY_URL_PRIVATE_USER_SERVLET_MAPPING,
				PropsValues.LAYOUT_FRIENDLY_URL_PUBLIC_SERVLET_MAPPING,
				sourcePrivateLayoutFriendlyURL, sourcePublicLayoutFriendlyURL,
				fileEntry.getTitle(), fileEntry.getUuid(),
				FriendlyURLResolverConstants.URL_SEPARATOR_X_JOURNAL_ARTICLE,
				company.getWebId()
			});

		if (!content.contains("[$TIMESTAMP")) {
			return _extractValidContent(content);
		}

		return _replaceTimestampParameters(content);
	}

	private static void _addPermutations(
		int index, List<String> list, List<List<String>> result) {

		if (index == list.size()) {
			result.add(new ArrayList<>(list));

			return;
		}

		for (int i = index; i < list.size(); i++) {
			Collections.swap(list, index, i);
			_addPermutations(index + 1, list, result);
			Collections.swap(list, index, i);
		}
	}

	private static String _duplicateLinesWithParamNames(
		String[] addParams, String content, String[] findParams) {

		if (StringUtil.indexOfAny(content, findParams) <= -1) {
			return content;
		}

		List<String> urls = ListUtil.fromArray(StringUtil.splitLines(content));

		List<String> outURLs = new ArrayList<>();

		for (String url : urls) {
			outURLs.add(url);

			if (StringUtil.indexOfAny(url, findParams) <= -1) {
				continue;
			}

			outURLs.add(StringUtil.replace(url, findParams, addParams));
		}

		return StringUtil.merge(outURLs, StringPool.NEW_LINE);
	}

	private static String _extractValidContent(String content) {
		return StringUtil.merge(
			TransformUtil.transform(
				ListUtil.fromArray(StringUtil.splitLines(content)),
				line -> {
					if (Validator.isNotNull(line) &&
						!line.endsWith(StringPool.COLON)) {

						return line;
					}

					return null;
				}),
			StringPool.NEW_LINE);
	}

	private static List<List<String>> _generatePermutations(
		String... parameters) {

		List<List<String>> permutations = new ArrayList<>();

		_addPermutations(0, ListUtil.fromArray(parameters), permutations);

		return permutations;
	}

	private static String _getFriendlyURL(Layout layout) {
		return layout.getFriendlyURL();
	}

	private static String _replaceExternalGroupFriendlyURLs(String content) {
		return _duplicateLinesWithParamNames(
			_EXTERNAL_GROUP_FRIENDLY_URL_VARIABLES, content,
			_GROUP_FRIENDLY_URL_VARIABLES);
	}

	private static String _replaceMultiLocaleLayoutFriendlyURLs(
		String content) {

		return _duplicateLinesWithParamNames(
			_NONDEFAULT_MULTI_LOCALE_LAYOUT_VARIABLES, content,
			_MULTI_LOCALE_LAYOUT_VARIABLES);
	}

	private static String _replaceTimestampParameters(String content) {
		List<String> urls = new ArrayList<>();

		List<List<String>> permutations = _generatePermutations(
			_HEIGHT_PARAMETER, _TIMESTAMP_PARAMETER, _WIDTH_PARAMETER);

		for (String url : ListUtil.fromArray(StringUtil.splitLines(content))) {
			if (Validator.isNotNull(url) && !url.contains("[$TIMESTAMP") &&
				!url.endsWith(StringPool.COLON)) {

				urls.add(url);

				continue;
			}

			for (List<String> permutation : permutations) {
				String parameters = String.join("&", permutation);

				urls.add(
					StringUtil.replace(
						url,
						new String[] {"[$TIMESTAMP$]", "[$TIMESTAMP_ONLY$]"},
						new String[] {"&" + parameters, "?" + parameters}));

				if (!url.contains("[$TIMESTAMP")) {
					continue;
				}

				urls.add(
					StringUtil.replace(
						url,
						new String[] {"[$TIMESTAMP$]", "[$TIMESTAMP_ONLY$]"},
						new String[] {
							StringPool.BLANK,
							StringBundler.concat(
								"?", _TIMESTAMP_PARAMETER, "?", parameters)
						}));
			}
		}

		return StringUtil.merge(urls, StringPool.NEW_LINE);
	}

	private static final String[] _EXTERNAL_GROUP_FRIENDLY_URL_VARIABLES = {
		"[$EXTERNAL_GROUP_FRIENDLY_URL$]",
		"[$EXTERNAL_PRIVATE_LAYOUT_FRIENDLY_URL$]",
		"[$EXTERNAL_PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final String[] _GROUP_FRIENDLY_URL_VARIABLES = {
		"[$GROUP_FRIENDLY_URL$]", "[$PRIVATE_LAYOUT_FRIENDLY_URL$]",
		"[$PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final String _HEIGHT_PARAMETER = "height=100";

	private static final String[] _MULTI_LOCALE_LAYOUT_VARIABLES = {
		"[$LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
		"[$PRIVATE_LAYOUT_FRIENDLY_URL$]", "[$PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final String[] _NONDEFAULT_MULTI_LOCALE_LAYOUT_VARIABLES = {
		"[$NON_DEFAULT_LIVE_PUBLIC_LAYOUT_FRIENDLY_URL$]",
		"[$NON_DEFAULT_PRIVATE_LAYOUT_FRIENDLY_URL$]",
		"[$NON_DEFAULT_PUBLIC_LAYOUT_FRIENDLY_URL$]"
	};

	private static final String _TIMESTAMP_PARAMETER = "t=123456789";

	private static final String _WIDTH_PARAMETER = "width=100";

	private static final Locale[] _locales = {
		LocaleUtil.US, LocaleUtil.GERMANY, LocaleUtil.SPAIN
	};
	private static final Pattern _pattern = Pattern.compile(
		"href=|url\\(|\\{|\\[");

}