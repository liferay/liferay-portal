/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.util;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Noor Najjar
 */
public class CTPreviewURLUtil {

	public static String addPreviewCTCollectionId(
		long companyId, long ctCollectionId, String html) {

		if (html == null) {
			return null;
		}

		Matcher matcher = _urlAttributePattern.matcher(html);

		return matcher.replaceAll(
			matchResult -> {
				String value = matchResult.group(2);

				if (Objects.equals(matchResult.group(1), "srcset")) {
					List<String> candidates = TransformUtil.transformToList(
						StringUtil.split(value),
						candidate -> {
							candidate = StringUtil.trim(candidate);

							int index = candidate.indexOf(CharPool.SPACE);

							if (index == -1) {
								index = candidate.length();
							}

							String previewURL = _getPreviewURL(
								companyId, ctCollectionId,
								candidate.substring(0, index));

							return previewURL + candidate.substring(index);
						});

					value = StringUtil.merge(
						candidates, StringPool.COMMA_AND_SPACE);
				}
				else {
					value = _getPreviewURL(companyId, ctCollectionId, value);
				}

				return Matcher.quoteReplacement(
					StringBundler.concat(
						matchResult.group(1), "=\"", value, "\""));
			});
	}

	public static String addUnifiedPreviewCTCollectionId(
		long companyId, String html, long leftCTCollectionId,
		long rightCTCollectionId) {

		StringBundler sb = new StringBundler();

		Matcher matcher = _removedImagePattern.matcher(html);

		int index = 0;

		while (matcher.find()) {
			sb.append(
				addPreviewCTCollectionId(
					companyId, rightCTCollectionId,
					html.substring(index, matcher.start())));
			sb.append(
				addPreviewCTCollectionId(
					companyId, leftCTCollectionId, matcher.group()));

			index = matcher.end();
		}

		sb.append(
			addPreviewCTCollectionId(
				companyId, rightCTCollectionId, html.substring(index)));

		return sb.toString();
	}

	public static boolean isDocumentURL(long companyId, String url) {
		Matcher matcher = _documentURLPattern.matcher(url);

		if (!matcher.lookingAt()) {
			return false;
		}

		String host = matcher.group(1);

		if (((host != null) &&
			 !PortalUtil.isValidPortalDomain(companyId, host)) ||
			!Objects.equals(matcher.group(2), PortalUtil.getPathContext())) {

			return false;
		}

		return true;
	}

	private static String _getPreviewURL(
		long companyId, long ctCollectionId, String url) {

		if (!isDocumentURL(companyId, url)) {
			return url;
		}

		String decodedURL = StringUtil.replace(
			url, StringPool.AMPERSAND_ENCODED, StringPool.AMPERSAND);

		if (Validator.isNotNull(
				HttpComponentsUtil.getParameter(
					decodedURL, "previewCTCollectionId", false))) {

			url = HttpComponentsUtil.removeParameter(
				decodedURL, "previewCTCollectionId");
		}

		url = HttpComponentsUtil.addParameter(
			url, "p_l_mode", Constants.PREVIEW);

		return HttpComponentsUtil.addParameter(
			url, "previewCTCollectionId", ctCollectionId);
	}

	private static final Pattern _documentURLPattern = Pattern.compile(
		"(?:https?://([^/:\"]+)(?::\\d+)?)?(.*?)/(?:documents/" +
			"(?:\\d+|d|portlet_file_entry)(?=[/?#]|$)|" +
				"o/adaptive-media/image/\\d+/)");
	private static final Pattern _removedImagePattern = Pattern.compile(
		"<img\\b[^>]*\\bchangeType=\"diff-removed-image\"[^>]*>");
	private static final Pattern _urlAttributePattern = Pattern.compile(
		"(href|src|srcset)=\"([^\"]*)\"");

}