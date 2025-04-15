/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardAttachment;
import com.liferay.headless.delivery.dto.v1_0.util.ContentValueUtil;
import com.liferay.headless.delivery.resource.v1_0.MessageBoardAttachmentResource;
import com.liferay.message.boards.constants.MBConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBMessageService;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;

import jakarta.ws.rs.BadRequestException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/message-board-attachment.properties",
	scope = ServiceScope.PROTOTYPE,
	service = MessageBoardAttachmentResource.class
)
public class MessageBoardAttachmentResourceImpl
	extends BaseMessageBoardAttachmentResourceImpl {

	@Override
	public void deleteMessageBoardAttachment(Long messageBoardAttachmentId)
		throws Exception {

		_portletFileRepository.deletePortletFileEntry(messageBoardAttachmentId);
	}

	@Override
	public void
			deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(
				Long siteId, String messageBoardMessageExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		MBMessage mbMessage =
			_mbMessageService.getMBMessageByExternalReferenceCode(
				messageBoardMessageExternalReferenceCode, siteId);

		FileEntry fileEntry =
			mbMessage.getAttachmentsFileEntryByExternalReferenceCode(
				externalReferenceCode, siteId);

		_portletFileRepository.deletePortletFileEntry(
			fileEntry.getFileEntryId());
	}

	@Override
	public MessageBoardAttachment getMessageBoardAttachment(
			Long messageBoardAttachmentId)
		throws Exception {

		return _toMessageBoardAttachment(
			_portletFileRepository.getPortletFileEntry(
				messageBoardAttachmentId));
	}

	@Override
	public Page<MessageBoardAttachment>
			getMessageBoardMessageMessageBoardAttachmentsPage(
				Long messageBoardMessageId)
		throws Exception {

		MBMessage mbMessage = _mbMessageService.getMessage(
			messageBoardMessageId);

		return _getMessageBoardAttachmentsPage(
			HashMapBuilder.<String, Map<String, String>>put(
				"createBatch",
				addAction(
					ActionKeys.VIEW, mbMessage.getMessageId(),
					"postMessageBoardMessageMessageBoardAttachmentBatch",
					mbMessage.getUserId(), MBConstants.RESOURCE_NAME,
					mbMessage.getGroupId())
			).build(),
			mbMessage);
	}

	@Override
	public Page<MessageBoardAttachment>
			getMessageBoardThreadMessageBoardAttachmentsPage(
				Long messageBoardThreadId)
		throws Exception {

		MBThread mbThread = _mbThreadLocalService.getMBThread(
			messageBoardThreadId);

		MBMessage mbMessage = _mbMessageService.getMessage(
			mbThread.getRootMessageId());

		return _getMessageBoardAttachmentsPage(
			HashMapBuilder.<String, Map<String, String>>put(
				"createBatch",
				addAction(
					ActionKeys.ADD_MESSAGE, mbThread.getThreadId(),
					"postMessageBoardThreadMessageBoardAttachmentBatch",
					mbThread.getUserId(), MBConstants.RESOURCE_NAME,
					mbThread.getGroupId())
			).build(),
			mbMessage);
	}

	@Override
	public MessageBoardAttachment
			getSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(
				Long siteId, String messageBoardMessageExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		MBMessage mbMessage =
			_mbMessageService.getMBMessageByExternalReferenceCode(
				messageBoardMessageExternalReferenceCode, siteId);

		FileEntry fileEntry =
			mbMessage.getAttachmentsFileEntryByExternalReferenceCode(
				externalReferenceCode, siteId);

		return _toMessageBoardAttachment(fileEntry);
	}

	@Override
	public MessageBoardAttachment postMessageBoardMessageMessageBoardAttachment(
			Long messageBoardMessageId, MultipartBody multipartBody)
		throws Exception {

		return _addMessageBoardAttachment(messageBoardMessageId, multipartBody);
	}

	@Override
	public MessageBoardAttachment postMessageBoardThreadMessageBoardAttachment(
			Long messageBoardThreadId, MultipartBody multipartBody)
		throws Exception {

		MBThread mbThread = _mbThreadLocalService.getMBThread(
			messageBoardThreadId);

		return _addMessageBoardAttachment(
			mbThread.getRootMessageId(), multipartBody);
	}

	private MessageBoardAttachment _addMessageBoardAttachment(
			Long messageBoardMessageId, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile == null) {
			throw new BadRequestException("No file found in body");
		}

		MBMessage mbMessage = _mbMessageService.getMessage(
			messageBoardMessageId);

		Folder folder = mbMessage.addAttachmentsFolder();

		return _toMessageBoardAttachment(
			_portletFileRepository.addPortletFileEntry(
				null, mbMessage.getGroupId(), contextUser.getUserId(),
				MBMessage.class.getName(), mbMessage.getClassPK(),
				MBConstants.SERVICE_NAME, folder.getFolderId(),
				binaryFile.getInputStream(), binaryFile.getFileName(),
				binaryFile.getFileName(), false));
	}

	private Page<MessageBoardAttachment> _getMessageBoardAttachmentsPage(
			Map<String, Map<String, String>> actions, MBMessage mbMessage)
		throws Exception {

		return Page.of(
			actions,
			transform(
				mbMessage.getAttachmentsFileEntries(),
				this::_toMessageBoardAttachment));
	}

	private MessageBoardAttachment _toMessageBoardAttachment(
			FileEntry fileEntry)
		throws Exception {

		return new MessageBoardAttachment() {
			{
				setContentUrl(
					() -> _dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null, "", false,
						false));
				setContentValue(
					() -> ContentValueUtil.toContentValue(
						"contentValue", fileEntry::getContentStream,
						contextUriInfo));
				setEncodingFormat(fileEntry::getMimeType);
				setExternalReferenceCode(fileEntry::getExternalReferenceCode);
				setFileExtension(fileEntry::getExtension);
				setId(fileEntry::getFileEntryId);
				setSizeInBytes(fileEntry::getSize);
				setTitle(fileEntry::getTitle);
			}
		};
	}

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private MBMessageService _mbMessageService;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

	@Reference
	private PortletFileRepository _portletFileRepository;

}