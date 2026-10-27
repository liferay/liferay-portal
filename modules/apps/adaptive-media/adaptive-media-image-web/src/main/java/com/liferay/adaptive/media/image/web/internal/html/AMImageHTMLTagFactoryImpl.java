/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.web.internal.html;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.adaptive.media.image.media.query.Condition;
import com.liferay.adaptive.media.image.media.query.MediaQuery;
import com.liferay.adaptive.media.image.media.query.MediaQueryProvider;
import com.liferay.adaptive.media.image.url.AMImageURLFactory;
import com.liferay.layout.responsive.ViewportSize;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.URI;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = AMImageHTMLTagFactory.class)
public class AMImageHTMLTagFactoryImpl implements AMImageHTMLTagFactory {

	@Override
	public String create(String originalImgTag, FileEntry fileEntry)
		throws PortalException {

		List<String> sourceElements = _getSourceElements(fileEntry);

		if (sourceElements.isEmpty()) {
			return originalImgTag;
		}

		StringBundler sb = new StringBundler(5 + sourceElements.size());

		sb.append("<picture ");
		sb.append(AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID);
		sb.append("=\"");
		sb.append(fileEntry.getFileEntryId());
		sb.append("\">");

		_appendViewportSourceElements(fileEntry, originalImgTag, sb);

		sourceElements.forEach(sb::append);

		sb.append(originalImgTag);

		sb.append("</picture>");

		return sb.toString();
	}

	private void _appendViewportSourceElements(
			FileEntry fileEntry, String originalImgTag, StringBundler sb)
		throws PortalException {

		ViewportSize[] viewportSizes = ViewportSize.values();

		for (int i = viewportSizes.length - 1; i >= 0; i--) {
			ViewportSize viewportSize = viewportSizes[i];

			String configuration = _getConfiguration(
				originalImgTag, viewportSize);

			if (configuration == null) {
				continue;
			}

			AMImageConfigurationEntry amImageConfigurationEntry =
				_amImageConfigurationHelper.getAMImageConfigurationEntry(
					fileEntry.getCompanyId(), configuration);

			if (amImageConfigurationEntry == null) {
				continue;
			}

			URI uri = _amImageURLFactory.createFileEntryURL(
				fileEntry.getFileVersion(), amImageConfigurationEntry);

			sb.append("<source media=\"");

			if (viewportSize != ViewportSize.DESKTOP) {
				sb.append("(max-width:");
				sb.append(viewportSize.getMaxWidth());
				sb.append("px) and ");
			}

			sb.append("(min-width:");
			sb.append(viewportSize.getMinWidth());
			sb.append("px)\" srcset=\"");
			sb.append(uri.toString());
			sb.append("\" />");
		}
	}

	private String _getConfiguration(
		String originalImgTag, ViewportSize viewportSize) {

		String attributeName = StringBundler.concat(
			" data-", StringUtil.toLowerCase(viewportSize.getViewportSizeId()),
			"-configuration=\"");

		int index = originalImgTag.indexOf(attributeName);

		if (index == -1) {
			return null;
		}

		index += attributeName.length();

		return originalImgTag.substring(
			index, originalImgTag.indexOf(CharPool.QUOTE, index));
	}

	private String _getMediaQueryString(MediaQuery mediaQuery) {
		List<Condition> conditions = mediaQuery.getConditions();

		if (conditions.isEmpty()) {
			return null;
		}

		String[] conditionStrings = new String[conditions.size()];

		for (int i = 0; i < conditionStrings.length; i++) {
			Condition condition = conditions.get(i);

			conditionStrings[i] = StringBundler.concat(
				StringPool.OPEN_PARENTHESIS, condition.getAttribute(),
				StringPool.COLON, condition.getValue(),
				StringPool.CLOSE_PARENTHESIS);
		}

		return StringUtil.merge(conditionStrings, " and ");
	}

	private String _getSourceElement(MediaQuery mediaQuery) {
		String mediaQueryString = _getMediaQueryString(mediaQuery);

		if (mediaQueryString == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(5);

		sb.append("<source media=\"");
		sb.append(mediaQueryString);
		sb.append("\" srcset=\"");
		sb.append(mediaQuery.getSrc());
		sb.append("\" />");

		return sb.toString();
	}

	private List<String> _getSourceElements(FileEntry fileEntry)
		throws PortalException {

		return TransformUtil.transform(
			_mediaQueryProvider.getMediaQueries(fileEntry),
			this::_getSourceElement);
	}

	@Reference
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Reference
	private AMImageURLFactory _amImageURLFactory;

	@Reference
	private MediaQueryProvider _mediaQueryProvider;

}