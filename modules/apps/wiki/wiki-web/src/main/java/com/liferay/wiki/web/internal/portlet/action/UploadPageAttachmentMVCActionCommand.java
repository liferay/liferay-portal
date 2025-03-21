/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.util.DLValidator;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.servlet.ServletResponseConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;
import com.liferay.upload.UploadResponseHandler;
import com.liferay.wiki.configuration.WikiFileUploadConfiguration;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.exception.WikiAttachmentMimeTypeException;
import com.liferay.wiki.exception.WikiAttachmentSizeException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletRequest;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiFileUploadConfiguration",
	property = {
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI,
		"mvc.command.name=/wiki/image_editor",
		"mvc.command.name=/wiki/upload_page_attachment"
	},
	service = MVCActionCommand.class
)
public class UploadPageAttachmentMVCActionCommand extends BaseMVCActionCommand {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_wikiFileUploadConfiguration = ConfigurableUtil.createConfigurable(
			WikiFileUploadConfiguration.class, properties);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_pageAttachmentWikiUploadFileEntryHandler,
			_pageAttachmentWikiUploadResponseHandler, actionRequest,
			actionResponse);
	}

	@Reference
	protected DLValidator dlValidator;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	private final PageAttachmentWikiUploadFileEntryHandler
		_pageAttachmentWikiUploadFileEntryHandler =
			new PageAttachmentWikiUploadFileEntryHandler();
	private final PageAttachmentWikiUploadResponseHandler
		_pageAttachmentWikiUploadResponseHandler =
			new PageAttachmentWikiUploadResponseHandler();

	@Reference
	private UploadHandler _uploadHandler;

	private volatile WikiFileUploadConfiguration _wikiFileUploadConfiguration;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiNode)")
	private ModelResourcePermission<WikiNode> _wikiNodeModelResourcePermission;

	@Reference
	private WikiPageService _wikiPageService;

	private class PageAttachmentWikiUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			if (Validator.isNotNull(
					uploadPortletRequest.getFileName(
						"imageSelectorFileName"))) {

				return _addPageAttachment(
					uploadPortletRequest, themeDisplay,
					uploadPortletRequest.getFileName("imageSelectorFileName"),
					"imageSelectorFileName");
			}

			return _editImageFileEntry(uploadPortletRequest, themeDisplay);
		}

		private FileEntry _addPageAttachment(
				UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay, String fileName,
				String parameterName)
			throws IOException, PortalException {

			dlValidator.validateFileSize(
				themeDisplay.getScopeGroupId(), fileName,
				uploadPortletRequest.getContentType(parameterName),
				uploadPortletRequest.getSize(parameterName));

			long resourcePrimKey = ParamUtil.getLong(
				uploadPortletRequest, "resourcePrimKey");

			WikiPage page = _wikiPageService.getPage(resourcePrimKey);

			_wikiNodeModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), page.getNodeId(),
				ActionKeys.ADD_ATTACHMENT);

			String contentType = uploadPortletRequest.getContentType(
				parameterName);

			String[] mimeTypes = ParamUtil.getParameterValues(
				uploadPortletRequest, "mimeTypes");

			_validateFile(
				fileName, contentType, mimeTypes,
				uploadPortletRequest.getSize(parameterName));

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					parameterName)) {

				return _wikiPageService.addPageAttachment(
					page.getNodeId(), page.getTitle(), fileName, inputStream,
					contentType);
			}
		}

		private FileEntry _editImageFileEntry(
				UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay)
			throws IOException, PortalException {

			long fileEntryId = ParamUtil.getLong(
				uploadPortletRequest, "fileEntryId");

			FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

			return _addPageAttachment(
				uploadPortletRequest, themeDisplay, fileEntry.getFileName(),
				"imageBlob");
		}

		private String[] _getValidMimeTypes(
			String[] mimeTypes, List<String> wikiAttachmentMimeTypes) {

			if (wikiAttachmentMimeTypes.contains(StringPool.STAR)) {
				return mimeTypes;
			}

			List<String> validMimeTypes = new ArrayList<>();

			for (String mimeType : mimeTypes) {
				if (wikiAttachmentMimeTypes.contains(mimeType)) {
					validMimeTypes.add(mimeType);
				}
			}

			return validMimeTypes.toArray(new String[0]);
		}

		private void _validateFile(
				String fileName, String contentType, String[] mimeTypes,
				long size)
			throws PortalException {

			long wikiAttachmentMaxSize =
				_wikiFileUploadConfiguration.attachmentMaxSize();

			if ((wikiAttachmentMaxSize > 0) && (size > wikiAttachmentMaxSize)) {
				throw new WikiAttachmentSizeException();
			}

			List<String> wikiAttachmentMimeTypes = ListUtil.fromArray(
				_wikiFileUploadConfiguration.attachmentMimeTypes());

			if (ArrayUtil.isEmpty(mimeTypes) &&
				ListUtil.isNull(wikiAttachmentMimeTypes)) {

				return;
			}

			for (String mimeType :
					_getValidMimeTypes(mimeTypes, wikiAttachmentMimeTypes)) {

				if (mimeType.equals(contentType)) {
					return;
				}
			}

			throw new WikiAttachmentMimeTypeException(
				StringBundler.concat(
					"Invalid MIME type ", contentType, " for file name ",
					fileName));
		}

	}

	private class PageAttachmentWikiUploadResponseHandler
		implements UploadResponseHandler {

		@Override
		public JSONObject onFailure(
				PortletRequest portletRequest, PortalException portalException)
			throws PortalException {

			JSONObject jsonObject =
				_itemSelectorUploadResponseHandler.onFailure(
					portletRequest, portalException);

			JSONObject errorJSONObject = null;

			if (portalException instanceof WikiAttachmentMimeTypeException) {
				errorJSONObject = JSONUtil.put(
					"errorType",
					ServletResponseConstants.SC_FILE_EXTENSION_EXCEPTION);
			}
			else if (portalException instanceof WikiAttachmentSizeException) {
				errorJSONObject = JSONUtil.put(
					"errorType",
					ServletResponseConstants.SC_FILE_SIZE_EXCEPTION);
			}

			jsonObject.put("error", errorJSONObject);

			return jsonObject;
		}

		@Override
		public JSONObject onSuccess(
				UploadPortletRequest uploadPortletRequest, FileEntry fileEntry)
			throws PortalException {

			return _itemSelectorUploadResponseHandler.onSuccess(
				uploadPortletRequest, fileEntry);
		}

	}

}