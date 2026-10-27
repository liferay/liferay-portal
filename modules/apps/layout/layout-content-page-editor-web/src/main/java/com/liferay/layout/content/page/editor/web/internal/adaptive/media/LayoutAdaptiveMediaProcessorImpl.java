/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.adaptive.media;

import com.liferay.adaptive.media.content.transformer.ContentTransformerHandler;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.layout.adaptive.media.LayoutAdaptiveMediaProcessor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(service = LayoutAdaptiveMediaProcessor.class)
public class LayoutAdaptiveMediaProcessorImpl
	implements LayoutAdaptiveMediaProcessor {

	@Override
	public String processAdaptiveMediaContent(String content) {
		String processedContent = _contentTransformerHandler.transform(content);

		if (!processedContent.contains("--background-image-file-entry-id:")) {
			return processedContent;
		}

		Document document = Jsoup.parse(processedContent);

		try {
			_replaceCSSProperties(document);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to process adaptive media content", exception);
			}
		}

		Element bodyElement = document.body();

		return bodyElement.html();
	}

	private String _getCSSClass(long fileEntryId, Element styledElement) {
		String layoutStructureItemId = styledElement.attr(
			"data-layout-structure-item-id");

		if (Validator.isNull(layoutStructureItemId)) {
			return _CSS_CLASS_PREFIX + fileEntryId;
		}

		return StringBundler.concat(
			_CSS_CLASS_PREFIX, layoutStructureItemId, StringPool.DASH,
			fileEntryId);
	}

	private String _getMediaQuery(String cssSelector, long fileEntryId)
		throws PortalException {

		StringBundler sb = new StringBundler();

		List<MediaQuery> mediaQueries = _mediaQueryProvider.getMediaQueries(
			_dlAppService.getFileEntry(fileEntryId));

		for (MediaQuery mediaQuery : mediaQueries) {
			List<Condition> conditions = mediaQuery.getConditions();

			sb.append("@media ");

			for (Condition condition : conditions) {
				sb.append(StringPool.OPEN_PARENTHESIS);
				sb.append(condition.getAttribute());
				sb.append(StringPool.COLON);
				sb.append(condition.getValue());
				sb.append(StringPool.CLOSE_PARENTHESIS);

				if (conditions.indexOf(condition) == (conditions.size() - 1)) {
					continue;
				}

				sb.append(" and ");
			}

			sb.append(StringPool.OPEN_CURLY_BRACE);
			sb.append(cssSelector);
			sb.append("{background-image: url(");
			sb.append(mediaQuery.getSrc());
			sb.append(") !important;}}");
		}

		return sb.toString();
	}

	private void _replaceCSSProperties(Document document)
		throws PortalException {

		Elements styledElements = document.select("*[style]");

		for (Element styledElement : styledElements) {
			String styleText = styledElement.attr("style");

			if (!styleText.contains("--background-image-file-entry-id:")) {
				continue;
			}

			StringBundler sb = new StringBundler();

			Matcher matcher = _cssPropertyPattern.matcher(styleText);

			while (matcher.find()) {
				long fileEntryId = GetterUtil.getLong(matcher.group(1));

				String cssClass = _getCSSClass(fileEntryId, styledElement);

				styledElement.addClass(cssClass);

				sb.append(
					_getMediaQuery(StringPool.PERIOD + cssClass, fileEntryId));
			}

			if (sb.length() <= 0) {
				continue;
			}

			Element newStyleElement = styledElement.prependElement("style");

			newStyleElement.text(sb.toString());
		}
	}

	private static final String _CSS_CLASS_PREFIX = "lfr-background-image-";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutAdaptiveMediaProcessorImpl.class);

	private static final Pattern _cssPropertyPattern = Pattern.compile(
		"--background-image-file-entry-id:\\s*(\\d+);");

	@Reference
	private ContentTransformerHandler _contentTransformerHandler;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private MediaQueryProvider _mediaQueryProvider;

}