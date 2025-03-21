/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseAttachment;
import com.liferay.headless.delivery.dto.v1_0.util.ContentValueUtil;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseAttachmentResource;
import com.liferay.knowledge.base.constants.KBActionKeys;
import com.liferay.knowledge.base.constants.KBConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
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
	properties = "OSGI-INF/liferay/rest/v1_0/knowledge-base-attachment.properties",
	scope = ServiceScope.PROTOTYPE,
	service = KnowledgeBaseAttachmentResource.class
)
public class KnowledgeBaseAttachmentResourceImpl
	extends BaseKnowledgeBaseAttachmentResourceImpl {

	@Override
	public void deleteKnowledgeBaseAttachment(Long knowledgeBaseAttachmentId)
		throws Exception {

		_portletFileRepository.deletePortletFileEntry(
			knowledgeBaseAttachmentId);
	}

	@Override
	public void
			deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(
				Long siteId, String knowledgeBaseArticleExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		KBArticle kbArticle =
			_kbArticleService.getLatestKBArticleByExternalReferenceCode(
				siteId, knowledgeBaseArticleExternalReferenceCode);

		FileEntry fileEntry =
			kbArticle.getAttachmentsFileEntryByExternalReferenceCode(
				externalReferenceCode);

		_portletFileRepository.deletePortletFileEntry(
			fileEntry.getFileEntryId());
	}

	@Override
	public Page<KnowledgeBaseAttachment>
			getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage(
				Long knowledgeBaseArticleId)
		throws Exception {

		KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
			knowledgeBaseArticleId, WorkflowConstants.STATUS_APPROVED);

		return Page.of(
			HashMapBuilder.<String, Map<String, String>>put(
				"createBatch",
				addAction(
					KBActionKeys.ADD_KB_ARTICLE, kbArticle.getResourcePrimKey(),
					"postKnowledgeBaseArticleKnowledgeBaseAttachmentBatch",
					kbArticle.getUserId(), KBConstants.RESOURCE_NAME_ADMIN,
					kbArticle.getGroupId())
			).build(),
			transform(
				kbArticle.getAttachmentsFileEntries(),
				this::_toKnowledgeBaseAttachment));
	}

	@Override
	public KnowledgeBaseAttachment getKnowledgeBaseAttachment(
			Long knowledgeBaseAttachmentId)
		throws Exception {

		return _toKnowledgeBaseAttachment(
			_portletFileRepository.getPortletFileEntry(
				knowledgeBaseAttachmentId));
	}

	@Override
	public KnowledgeBaseAttachment
			getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(
				Long siteId, String knowledgeBaseArticleExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		KBArticle kbArticle =
			_kbArticleService.getLatestKBArticleByExternalReferenceCode(
				siteId, knowledgeBaseArticleExternalReferenceCode);

		return _toKnowledgeBaseAttachment(
			kbArticle.getAttachmentsFileEntryByExternalReferenceCode(
				externalReferenceCode));
	}

	@Override
	public KnowledgeBaseAttachment
			postKnowledgeBaseArticleKnowledgeBaseAttachment(
				Long knowledgeBaseArticleId, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile == null) {
			throw new BadRequestException("No file found in body");
		}

		KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
			knowledgeBaseArticleId, WorkflowConstants.STATUS_APPROVED);

		return _toKnowledgeBaseAttachment(
			_portletFileRepository.addPortletFileEntry(
				_getKnowledgeBaseAttachmentExternalReferenceCode(multipartBody),
				kbArticle.getGroupId(), contextUser.getUserId(),
				KBArticle.class.getName(), kbArticle.getClassPK(),
				KBConstants.SERVICE_NAME, kbArticle.getAttachmentsFolderId(),
				binaryFile.getInputStream(), binaryFile.getFileName(),
				binaryFile.getContentType(), false));
	}

	private String _getKnowledgeBaseAttachmentExternalReferenceCode(
			MultipartBody multipartBody)
		throws Exception {

		KnowledgeBaseAttachment knowledgeBaseAttachment =
			multipartBody.getValueAsInstance(
				"knowledgeBaseAttachment", KnowledgeBaseAttachment.class);

		if (knowledgeBaseAttachment == null) {
			return null;
		}

		return knowledgeBaseAttachment.getExternalReferenceCode();
	}

	private KnowledgeBaseAttachment _toKnowledgeBaseAttachment(
			FileEntry fileEntry)
		throws Exception {

		return new KnowledgeBaseAttachment() {
			{
				setContentUrl(
					() -> _portletFileRepository.getPortletFileEntryURL(
						null, fileEntry, null));
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
	private KBArticleService _kbArticleService;

	@Reference
	private PortletFileRepository _portletFileRepository;

}