/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.helper;

import com.liferay.document.library.constants.DLFileVersionPreviewConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.ImageProcessorUtil;
import com.liferay.document.library.kernel.processor.PDFProcessorUtil;
import com.liferay.document.library.kernel.processor.VideoProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.util.DL;
import com.liferay.document.library.service.DLFileVersionPreviewLocalService;
import com.liferay.document.library.url.provider.DLFileVersionURLProvider;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.webdav.DLWebDAVUtil;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = DLURLHelper.class)
public class DLURLHelperImpl implements DLURLHelper {

	@Override
	public String getDownloadURL(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString) {

		return getDownloadURL(
			fileEntry, fileVersion, themeDisplay, queryString, true, true);
	}

	@Override
	public String getDownloadURL(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString, boolean appendVersion, boolean absoluteURL) {

		String url = _getDLFileVersionURLProviderURL(
			fileVersion, themeDisplay, DLFileVersionURLProvider.Type.DOWNLOAD);

		if (Validator.isNotNull(url)) {
			return url;
		}

		return HttpComponentsUtil.addParameter(
			getPreviewURL(
				fileEntry, fileVersion, themeDisplay, queryString,
				appendVersion, absoluteURL),
			"download", true);
	}

	@Override
	public String getFileEntryControlPanelLink(
		PortletRequest portletRequest, long fileEntryId) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest,
				PortletProviderUtil.getPortletId(
					FileEntry.class.getName(), PortletProvider.Action.MANAGE),
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/document_library/view_file_entry"
		).setParameter(
			"fileEntryId", fileEntryId
		).buildString();
	}

	@Override
	public String getFolderControlPanelLink(
		PortletRequest portletRequest, long folderId) {

		PortletURL portletURL = _portal.getControlPanelPortletURL(
			portletRequest,
			PortletProviderUtil.getPortletId(
				Folder.class.getName(), PortletProvider.Action.MANAGE),
			PortletRequest.RENDER_PHASE);

		if (folderId == DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view");
		}
		else {
			portletURL.setParameter(
				"mvcRenderCommandName", "/document_library/view_folder");
		}

		portletURL.setParameter("folderId", String.valueOf(folderId));

		return portletURL.toString();
	}

	@Override
	public String getImagePreviewURL(
		FileEntry fileEntry, FileVersion fileVersion,
		ThemeDisplay themeDisplay) {

		return getImagePreviewURL(
			fileEntry, fileVersion, themeDisplay, null, true, true);
	}

	@Override
	public String getImagePreviewURL(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString, boolean appendVersion, boolean absoluteURL) {

		String url = _getDLFileVersionURLProviderURL(
			fileVersion, themeDisplay,
			DLFileVersionURLProvider.Type.IMAGE_PREVIEW);

		if (Validator.isNotNull(url)) {
			return url;
		}

		if (_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
				fileEntry.getFileEntryId(), fileVersion.getFileVersionId(),
				DLFileVersionPreviewConstants.STATUS_FAILURE)) {

			return StringPool.BLANK;
		}

		String previewQueryString = queryString;

		if (Validator.isNull(previewQueryString)) {
			previewQueryString = StringPool.BLANK;
		}

		if (ImageProcessorUtil.isSupported(fileVersion.getMimeType()) ||
			ArrayUtil.contains(
				PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES,
				fileEntry.getMimeType())) {

			previewQueryString = previewQueryString.concat("&imagePreview=1");
		}
		else if (PropsValues.DL_FILE_ENTRY_PREVIEW_ENABLED) {
			if (PDFProcessorUtil.hasImages(fileVersion)) {
				previewQueryString = previewQueryString.concat(
					"&previewFileIndex=1");
			}
			else if (VideoProcessorUtil.hasVideo(fileVersion)) {
				previewQueryString = previewQueryString.concat(
					"&videoThumbnail=1");
			}
		}

		return _getImageSrc(
			fileEntry, fileVersion, themeDisplay, previewQueryString,
			appendVersion, absoluteURL);
	}

	@Override
	public String getImagePreviewURL(
			FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		return getImagePreviewURL(
			fileEntry, fileEntry.getFileVersion(), themeDisplay);
	}

	@Override
	public String getPreviewURL(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString) {

		return getPreviewURL(
			fileEntry, fileVersion, themeDisplay, queryString, true, true);
	}

	@Override
	public String getPreviewURL(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString, boolean appendVersion, boolean absoluteURL) {

		String previewURLPrefix = _getPreviewURLPrefix(
			themeDisplay, absoluteURL);

		String previewURL = _getFriendlyURL(
			fileEntry, previewURLPrefix, queryString, appendVersion);

		if (Validator.isNull(previewURL)) {
			previewURL = _getPreviewUuidURL(
				fileEntry, fileVersion, previewURLPrefix, queryString,
				appendVersion);
		}

		if ((themeDisplay != null) && themeDisplay.isAddSessionIdToURL()) {
			return _portal.getURLWithSessionId(
				previewURL, themeDisplay.getSessionId());
		}

		return previewURL;
	}

	@Override
	public String getThumbnailSrc(
		FileEntry fileEntry, FileVersion fileVersion,
		ThemeDisplay themeDisplay) {

		String url = _getDLFileVersionURLProviderURL(
			fileVersion, themeDisplay, DLFileVersionURLProvider.Type.THUMBNAIL);

		if (Validator.isNotNull(url)) {
			return url;
		}

		if (_dlFileVersionPreviewLocalService.hasDLFileVersionPreview(
				fileEntry.getFileEntryId(), fileVersion.getFileVersionId(),
				DLFileVersionPreviewConstants.STATUS_FAILURE)) {

			return StringPool.BLANK;
		}

		String thumbnailQueryString = null;

		if (PropsValues.DL_FILE_ENTRY_THUMBNAIL_ENABLED) {
			if (ImageProcessorUtil.hasImages(fileVersion)) {
				thumbnailQueryString = "&imageThumbnail=1";
			}
			else if (PDFProcessorUtil.hasImages(fileVersion)) {
				thumbnailQueryString = "&documentThumbnail=1";
			}
			else if (VideoProcessorUtil.hasVideo(fileVersion)) {
				thumbnailQueryString = "&videoThumbnail=1";
			}
		}

		return _getImageSrc(
			fileEntry, fileVersion, themeDisplay, thumbnailQueryString);
	}

	@Override
	public String getThumbnailSrc(
			FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		return getThumbnailSrc(
			fileEntry, fileEntry.getFileVersion(), themeDisplay);
	}

	@Override
	public String getWebDavURL(
			ThemeDisplay themeDisplay, Folder folder, FileEntry fileEntry)
		throws PortalException {

		return getWebDavURL(themeDisplay, folder, fileEntry, false);
	}

	@Override
	public String getWebDavURL(
			ThemeDisplay themeDisplay, Folder folder, FileEntry fileEntry,
			boolean manualCheckInRequired)
		throws PortalException {

		return getWebDavURL(
			themeDisplay, folder, fileEntry, manualCheckInRequired, false);
	}

	@Override
	public String getWebDavURL(
			ThemeDisplay themeDisplay, Folder folder, FileEntry fileEntry,
			boolean manualCheckInRequired, boolean openDocumentUrl)
		throws PortalException {

		StringBundler webDavURLSB = new StringBundler(7);

		boolean secure = false;

		if (themeDisplay.isSecure() ||
			PropsValues.WEBDAV_SERVLET_HTTPS_REQUIRED) {

			secure = true;
		}

		webDavURLSB.append(
			_portal.getPortalURL(
				themeDisplay.getServerName(), themeDisplay.getServerPort(),
				secure));
		webDavURLSB.append(themeDisplay.getPathContext());
		webDavURLSB.append("/webdav");

		if (manualCheckInRequired) {
			webDavURLSB.append(DL.MANUAL_CHECK_IN_REQUIRED_PATH);
		}

		Group group = null;

		if (fileEntry != null) {
			group = _groupLocalService.getGroup(fileEntry.getGroupId());
		}
		else {
			group = themeDisplay.getScopeGroup();
		}

		webDavURLSB.append(group.getFriendlyURL());
		webDavURLSB.append("/document_library");

		StringBuilder sb = new StringBuilder();

		if ((folder != null) &&
			(folder.getFolderId() !=
				DLFolderConstants.DEFAULT_PARENT_FOLDER_ID)) {

			Folder curFolder = folder;

			while (true) {
				sb.insert(0, URLCodec.encodeURL(curFolder.getName(), true));
				sb.insert(0, StringPool.SLASH);

				if (curFolder.getParentFolderId() ==
						DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

					break;
				}

				curFolder = _dlAppLocalService.getFolder(
					curFolder.getParentFolderId());
			}
		}

		if (fileEntry != null) {
			sb.append(StringPool.SLASH);
			sb.append(DLWebDAVUtil.escapeURLTitle(fileEntry.getFileName()));
		}

		webDavURLSB.append(sb.toString());

		return webDavURLSB.toString();
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DLFileVersionURLProvider.class, null,
			(serviceReference, emitter) -> {
				DLFileVersionURLProvider dlFileVersionURLProvider =
					bundleContext.getService(serviceReference);

				List<DLFileVersionURLProvider.Type> types =
					dlFileVersionURLProvider.getTypes();

				types.forEach(emitter::emit);
			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private String _getDLFileVersionURLProviderURL(
		FileVersion fileVersion, ThemeDisplay themeDisplay,
		DLFileVersionURLProvider.Type type) {

		DLFileVersionURLProvider dlFileVersionURLProvider =
			_serviceTrackerMap.getService(type);

		if (dlFileVersionURLProvider == null) {
			return null;
		}

		String url = dlFileVersionURLProvider.getURL(fileVersion, themeDisplay);

		if (Validator.isNotNull(url)) {
			return url;
		}

		return null;
	}

	private String _getFriendlyURL(
		FileEntry fileEntry, String previewURLPrefix, String queryString,
		boolean appendVersion) {

		if (appendVersion || (fileEntry == null) ||
			(fileEntry.getFileEntryId() == 0)) {

			return null;
		}

		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				_portal.getClassNameId(FileEntry.class),
				fileEntry.getFileEntryId());

		if (friendlyURLEntry == null) {
			return null;
		}

		StringBundler sb = new StringBundler(6);

		sb.append(previewURLPrefix);
		sb.append(FriendlyURLResolverConstants.URL_SEPARATOR_Y_FILE_ENTRY);

		Group group = _groupLocalService.fetchGroup(fileEntry.getGroupId());

		if (group == null) {
			group = _groupLocalService.fetchGroup(
				friendlyURLEntry.getGroupId());
		}

		sb.append(group.getFriendlyURL());

		sb.append(StringPool.SLASH);
		sb.append(friendlyURLEntry.getUrlTitle());

		if (Validator.isNotNull(queryString)) {
			sb.append(queryString.replaceFirst("&", "?"));
		}

		return sb.toString();
	}

	private String _getImageSrc(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString) {

		return _getImageSrc(
			fileEntry, fileVersion, themeDisplay, queryString, true, true);
	}

	private String _getImageSrc(
		FileEntry fileEntry, FileVersion fileVersion, ThemeDisplay themeDisplay,
		String queryString, boolean appendVersion, boolean absoluteURL) {

		String thumbnailSrc = StringPool.BLANK;

		if (Validator.isNotNull(queryString)) {
			thumbnailSrc = getPreviewURL(
				fileEntry, fileVersion, themeDisplay, queryString,
				appendVersion, absoluteURL);
		}

		return thumbnailSrc;
	}

	private String _getPreviewURLPrefix(
		ThemeDisplay themeDisplay, boolean absoluteURL) {

		StringBundler sb = new StringBundler(3);

		if ((themeDisplay != null) && absoluteURL) {
			sb.append(themeDisplay.getPortalURL());
		}

		sb.append(_portal.getPathContext());
		sb.append("/documents/");

		return sb.toString();
	}

	private String _getPreviewUuidURL(
		FileEntry fileEntry, FileVersion fileVersion, String previewURLPrefix,
		String queryString, boolean appendVersion) {

		StringBundler sb = new StringBundler(13);

		sb.append(previewURLPrefix);

		sb.append(fileEntry.getRepositoryId());
		sb.append(StringPool.SLASH);
		sb.append(fileEntry.getFolderId());
		sb.append(StringPool.SLASH);

		String fileName = fileEntry.getFileName();

		if (fileEntry.isInTrash()) {
			fileName = _trashHelper.getOriginalTitle(fileEntry.getFileName());
		}

		sb.append(URLCodec.encodeURL(HtmlUtil.unescape(fileName)));

		sb.append(StringPool.SLASH);
		sb.append(URLCodec.encodeURL(fileEntry.getUuid()));

		if (appendVersion) {
			sb.append("?version=");
			sb.append(fileVersion.getVersion());
			sb.append("&t=");
		}
		else {
			sb.append("?t=");
		}

		Date modifiedDate = fileVersion.getModifiedDate();

		sb.append(modifiedDate.getTime());

		sb.append(queryString);

		return sb.toString();
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

	@Reference
	private DLFileVersionPreviewLocalService _dlFileVersionPreviewLocalService;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	private ServiceTrackerMap
		<DLFileVersionURLProvider.Type, DLFileVersionURLProvider>
			_serviceTrackerMap;

	@Reference
	private TrashHelper _trashHelper;

}