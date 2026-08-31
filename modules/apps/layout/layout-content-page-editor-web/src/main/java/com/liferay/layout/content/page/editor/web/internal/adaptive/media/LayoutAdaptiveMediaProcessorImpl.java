/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.adaptive.media;

import com.liferay.adaptive.media.content.transformer.ContentTransformerHandler;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.layout.adaptive.media.LayoutAdaptiveMediaProcessor;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.net.URI;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
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

		if (!_needParsing(processedContent)) {
			return processedContent;
		}

		Document document = Jsoup.parse(processedContent);

		try {
			for (Map.Entry<ViewportSize, String> entry :
					_viewportSizeEnumMap.entrySet()) {

				Elements elements = document.getElementsByAttribute(
					entry.getValue());

				for (Element element : elements) {
					if (!StringUtil.equalsIgnoreCase(
							element.tagName(), "img")) {

						continue;
					}

					String configuration = element.attr(entry.getValue());

					long fileEntryId = GetterUtil.getLong(
						element.attr(
							AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID));

					if (fileEntryId <= 0) {
						continue;
					}

					FileEntry fileEntry = _dlAppService.getFileEntry(
						fileEntryId);

					AMImageConfigurationEntry amImageConfigurationEntry =
						_amImageConfigurationHelper.
							getAMImageConfigurationEntry(
								fileEntry.getCompanyId(), configuration);

					if (amImageConfigurationEntry == null) {
						continue;
					}

					URI uri = _amImageURLFactory.createFileEntryURL(
						fileEntry.getFileVersion(), amImageConfigurationEntry);

					_appendSourceElement(
						document, element, uri, entry.getKey());
				}
			}

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

	private static EnumMap<ViewportSize, String> _getViewportSizeMap() {
		EnumMap<ViewportSize, String> viewportSizeMap = new EnumMap<>(
			ViewportSize.class);

		for (ViewportSize viewportSize : ViewportSize.values()) {
			viewportSizeMap.put(
				viewportSize,
				"data-" + viewportSize.getViewportSizeId() + "-configuration");
		}

		return viewportSizeMap;
	}

	private void _appendSourceElement(
		Document document, Element element, URI uri,
		ViewportSize viewportSize) {

		Element sourceElement = document.createElement("source");

		Element parentElement = element.parent();

		StringBundler sb = new StringBundler(6);

		sb.append("(min-width:");
		sb.append(viewportSize.getMinWidth());
		sb.append("px)");

		if (viewportSize != ViewportSize.DESKTOP) {
			sb.append(" and (max-width:");
			sb.append(viewportSize.getMaxWidth());
			sb.append("px)");
		}

		sourceElement.attr("media", sb.toString());
		sourceElement.attr("srcset", uri.toString());

		parentElement.prependChild(sourceElement);
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

				if (conditions.indexOf(condition) != (conditions.size() - 1)) {
					sb.append(" and ");
				}
			}

			sb.append(StringPool.OPEN_CURLY_BRACE);
			sb.append(cssSelector);
			sb.append("{background-image: url(");
			sb.append(mediaQuery.getSrc());
			sb.append(") !important;}}");
		}

		return sb.toString();
	}

	private boolean _needParsing(String html) {
		for (String viewportSizeConfiguration : _viewportSizeEnumMap.values()) {
			if (html.contains(viewportSizeConfiguration)) {
				return true;
			}
		}

		return html.contains("--background-image-file-entry-id:");
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

			if (sb.length() > 0) {
				Element newStyleElement = styledElement.prependElement("style");

				newStyleElement.text(sb.toString());
			}
		}
	}

	private static final String _CSS_CLASS_PREFIX = "lfr-background-image-";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutAdaptiveMediaProcessorImpl.class);

	private static final Pattern _cssPropertyPattern = Pattern.compile(
		"--background-image-file-entry-id:\\s*(\\d+);");
	private static final EnumMap<ViewportSize, String> _viewportSizeEnumMap =
		_getViewportSizeMap();

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageURLFactory _amImageURLFactory;

	@Reference
	private ContentTransformerHandler _contentTransformerHandler;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private MediaQueryProvider _mediaQueryProvider;

}