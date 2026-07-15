/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.content.transformer.backwards.compatibility.internal;

import com.liferay.adaptive.media.content.transformer.ContentTransformer;
import com.liferay.adaptive.media.image.content.transformer.backwards.compatibility.internal.configuration.AMBackwardsCompatibilityHtmlContentTransformerConfiguration;
import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.adaptive.media.image.html.constants.AMImageHTMLConstants;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.repository.friendly.url.resolver.FileEntryFriendlyURLResolver;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	configurationPid = "com.liferay.adaptive.media.image.content.transformer.backwards.compatibility.internal.configuration.AMBackwardsCompatibilityHtmlContentTransformerConfiguration",
	service = ContentTransformer.class
)
public class AMBackwardsCompatibilityHtmlContentTransformer
	implements ContentTransformer {

	@Override
	public String transform(String html) throws PortalException {
		if (!_amBackwardsCompatibilityHtmlContentTransformerConfiguration.
				enabled()) {

			return html;
		}

		if (html == null) {
			return null;
		}

		if (!html.contains("/documents/") ||
			!html.contains(_OPEN_TAG_TOKEN_IMG)) {

			return html;
		}

		StringBundler sb = new StringBundler();

		int lastIndex = 0;

		while (lastIndex < html.length()) {
			int pictureStart = html.indexOf(_OPEN_TAG_TOKEN_PICTURE, lastIndex);

			if (pictureStart == -1) {
				pictureStart = html.length();
			}

			_transformImgTags(html, lastIndex, pictureStart, sb);

			if (pictureStart < html.length()) {
				int pictureEnd = html.indexOf(
					_CLOSE_TAG_TOKEN_PICTURE,
					pictureStart + _OPEN_TAG_TOKEN_PICTURE.length());

				if (pictureEnd == -1) {
					pictureEnd = html.length();
				}
				else {
					pictureEnd += _CLOSE_TAG_TOKEN_PICTURE.length();
				}

				sb.append(html.substring(pictureStart, pictureEnd));

				lastIndex = pictureEnd;
			}
			else {
				lastIndex = pictureStart;
			}
		}

		return sb.toString();
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_amBackwardsCompatibilityHtmlContentTransformerConfiguration =
			ConfigurableUtil.createConfigurable(
				AMBackwardsCompatibilityHtmlContentTransformerConfiguration.
					class,
				properties);
	}

	private FileEntry _getFileEntry(Matcher matcher) throws PortalException {
		if (Objects.equals(
				FriendlyURLResolverConstants.URL_SEPARATOR_Y_FILE_ENTRY,
				matcher.group(7))) {

			FileEntry fileEntry = _resolveFileEntry(
				matcher.group(9), matcher.group(8));

			if (fileEntry == null) {
				throw new PortalException(
					"No file entry found for friendly URL " + matcher.group(0));
			}

			return fileEntry;
		}

		if (matcher.group(5) != null) {
			long groupId = GetterUtil.getLong(matcher.group(2));

			String uuid = matcher.group(5);

			return _dlAppLocalService.getFileEntryByUuidAndGroupId(
				uuid, groupId);
		}

		long groupId = GetterUtil.getLong(matcher.group(2));
		long folderId = GetterUtil.getLong(matcher.group(3));
		String title = matcher.group(4);

		FileEntry fileEntry = _dlAppLocalService.fetchFileEntry(
			groupId, folderId, title);

		if (fileEntry != null) {
			return fileEntry;
		}

		return _dlAppLocalService.getFileEntryByFileName(
			groupId, folderId, title);
	}

	private Group _getGroup(long companyId, String name)
		throws PortalException {

		Group group = _groupLocalService.fetchFriendlyURLGroup(
			companyId, StringPool.SLASH + name);

		if (group != null) {
			return group;
		}

		User user = _userLocalService.getUserByScreenName(companyId, name);

		return user.getGroup();
	}

	private String _getReplacement(String originalImgTag, FileEntry fileEntry)
		throws PortalException {

		if ((fileEntry == null) ||
			!_amImageMimeTypeProvider.isMimeTypeSupported(
				fileEntry.getMimeType())) {

			return originalImgTag;
		}

		return _amImageHTMLTagFactory.create(originalImgTag, fileEntry);
	}

	private FileEntry _resolveFileEntry(String friendlyURL, String groupName)
		throws PortalException {

		Group group = _getGroup(CompanyThreadLocal.getCompanyId(), groupName);

		return _fileEntryFriendlyURLResolver.resolveFriendlyURL(
			group.getGroupId(), friendlyURL);
	}

	private String _transform(String imgElementString, String src)
		throws PortalException {

		// Check if the src starts with "data:image/" first because "data:image"
		// indicates a Base64 URL which can potentially be millions of
		// characters. So it is faster to run startsWith first to return early
		// on these strings first so that we do not have to call "contains" over
		// a very long string.

		if (src.startsWith("data:image/")) {
			return imgElementString;
		}

		// If we got past the above check, we have a URL. Now we can do a quick
		// check if the URL contains "/documents" as a crude way of bypassing
		// most non-Liferay URLs before we have to get into the less performant
		// regex logic.

		if (!src.contains("/documents")) {
			return imgElementString;
		}

		String replacement = imgElementString;

		StringBuffer sb = null;

		Matcher matcher = _pattern.matcher(src);

		while (matcher.find()) {
			if (sb == null) {
				sb = new StringBuffer(imgElementString.length());
			}

			FileEntry fileEntry = null;

			if (!imgElementString.contains(
					AMImageHTMLConstants.ATTRIBUTE_NAME_FILE_ENTRY_ID)) {

				fileEntry = _getFileEntry(matcher);
			}

			replacement = _getReplacement(imgElementString, fileEntry);

			matcher.appendReplacement(
				sb, Matcher.quoteReplacement(replacement));
		}

		if (sb != null) {
			matcher.appendTail(sb);

			replacement = sb.toString();
		}

		return replacement;
	}

	private void _transformImgTags(
			String html, int start, int end, StringBundler sb)
		throws PortalException {

		int lastIndex = start;

		while (lastIndex < end) {
			int imgStart = html.indexOf(_OPEN_TAG_TOKEN_IMG, lastIndex);

			if ((imgStart == -1) || (imgStart > end)) {
				sb.append(html.substring(lastIndex, end));

				return;
			}

			sb.append(html.substring(lastIndex, imgStart));

			int imgEnd = html.indexOf(CharPool.GREATER_THAN, imgStart) + 1;

			int attributeListPos = imgStart + _OPEN_TAG_TOKEN_IMG.length();

			int srcStart = html.indexOf(_ATTRIBUTE_TOKEN_SRC, attributeListPos);

			if ((srcStart == -1) || (srcStart > imgEnd)) {
				sb.append(html.substring(imgStart, imgEnd));

				lastIndex = imgEnd;

				continue;
			}

			int quotePos = srcStart + _ATTRIBUTE_TOKEN_SRC.length();

			int srcEnd = html.indexOf(html.charAt(quotePos), quotePos + 1);

			sb.append(
				_transform(
					html.substring(imgStart, imgEnd),
					html.substring(quotePos + 1, srcEnd)));

			lastIndex = imgEnd;
		}
	}

	private static final String _ATTRIBUTE_TOKEN_SRC = "src=";

	private static final String _CLOSE_TAG_TOKEN_PICTURE = "</picture>";

	private static final String _OPEN_TAG_TOKEN_IMG = "<img";

	private static final String _OPEN_TAG_TOKEN_PICTURE = "<picture";

	private static final Pattern _pattern = Pattern.compile(
		"((?:/?[^\\s]*)/documents/(\\d+)/(\\d+)/([^/?]+)(?:/([-0-9a-fA-F]+))?" +
			"(?:\\?.*$)?)|((?:/?[^\\s]*)/documents/(d)/(.*)/" +
				"([_A-Za-z0-9-]+)?(?:\\?.*$)?)");

	private volatile AMBackwardsCompatibilityHtmlContentTransformerConfiguration
		_amBackwardsCompatibilityHtmlContentTransformerConfiguration;

	@Reference
	private AMImageHTMLTagFactory _amImageHTMLTagFactory;

	@Reference
	private AMImageMimeTypeProvider _amImageMimeTypeProvider;

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private FileEntryFriendlyURLResolver _fileEntryFriendlyURLResolver;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private UserLocalService _userLocalService;

}