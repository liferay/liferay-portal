/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.item.selector.ItemSelectorUploadResponseHandler;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.upload.UniqueFileNameProvider;
import com.liferay.upload.UploadFileEntryHandler;
import com.liferay.upload.UploadHandler;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
		"jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ARTICLE,
		"mvc.command.name=/knowledge_base/upload_kb_article_attachments"
	},
	service = MVCActionCommand.class
)
public class UploadKBArticleAttachmentsMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		_uploadHandler.upload(
			_kbArticleAttachmentKBUploadFileEntryHandler,
			_itemSelectorUploadResponseHandler, actionRequest, actionResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadKBArticleAttachmentsMVCActionCommand.class);

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private ItemSelectorUploadResponseHandler
		_itemSelectorUploadResponseHandler;

	private final KBArticleAttachmentKBUploadFileEntryHandler
		_kbArticleAttachmentKBUploadFileEntryHandler =
			new KBArticleAttachmentKBUploadFileEntryHandler();

	@Reference
	private KBArticleLocalService _kbArticleLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.knowledge.base.model.KBArticle)"
	)
	private ModelResourcePermission<KBArticle>
		_kbArticleModelResourcePermission;

	@Reference
	private UniqueFileNameProvider _uniqueFileNameProvider;

	@Reference
	private UploadHandler _uploadHandler;

	private class KBArticleAttachmentKBUploadFileEntryHandler
		implements UploadFileEntryHandler {

		@Override
		public FileEntry upload(UploadPortletRequest uploadPortletRequest)
			throws IOException, PortalException {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)uploadPortletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			long resourcePrimKey = ParamUtil.getLong(
				uploadPortletRequest, "resourcePrimKey");

			KBArticle kbArticle = _kbArticleLocalService.getLatestKBArticle(
				resourcePrimKey, WorkflowConstants.STATUS_APPROVED);

			_kbArticleModelResourcePermission.check(
				themeDisplay.getPermissionChecker(), kbArticle,
				KBActionKeys.UPDATE);

			String fileName = uploadPortletRequest.getFileName(
				"imageSelectorFileName");

			if (Validator.isNotNull(fileName)) {
				try (InputStream inputStream =
						uploadPortletRequest.getFileAsStream(
							"imageSelectorFileName")) {

					return _addKBAttachment(
						fileName, inputStream, kbArticle,
						"imageSelectorFileName", resourcePrimKey,
						uploadPortletRequest, themeDisplay);
				}
			}

			return _addKBAttachment(
				kbArticle, resourcePrimKey, uploadPortletRequest, themeDisplay);
		}

		private FileEntry _addKBAttachment(
				KBArticle kbArticle, long resourcePrimKey,
				UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay)
			throws IOException, PortalException {

			try (InputStream inputStream = uploadPortletRequest.getFileAsStream(
					"imageBlob")) {

				long fileEntryId = ParamUtil.getLong(
					uploadPortletRequest, "fileEntryId");

				FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

				return _addKBAttachment(
					fileEntry.getFileName(), inputStream, kbArticle,
					"imageBlob", resourcePrimKey, uploadPortletRequest,
					themeDisplay);
			}
		}

		private FileEntry _addKBAttachment(
				String fileName, InputStream inputStream, KBArticle kbArticle,
				String parameterName, long resourcePrimKey,
				UploadPortletRequest uploadPortletRequest,
				ThemeDisplay themeDisplay)
			throws PortalException {

			String uniqueFileName = _uniqueFileNameProvider.provide(
				fileName,
				curFileName -> _exists(themeDisplay, kbArticle, curFileName));

			return _kbArticleLocalService.addAttachment(
				themeDisplay.getUserId(), resourcePrimKey, uniqueFileName,
				inputStream,
				uploadPortletRequest.getContentType(parameterName));
		}

		private boolean _exists(
			ThemeDisplay themeDisplay, KBArticle kbArticle, String fileName) {

			try {
				FileEntry fileEntry =
					PortletFileRepositoryUtil.getPortletFileEntry(
						themeDisplay.getScopeGroupId(),
						kbArticle.getAttachmentsFolderId(), fileName);

				if (fileEntry != null) {
					return true;
				}

				return false;
			}
			catch (PortalException portalException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}

				return false;
			}
		}

	}

}