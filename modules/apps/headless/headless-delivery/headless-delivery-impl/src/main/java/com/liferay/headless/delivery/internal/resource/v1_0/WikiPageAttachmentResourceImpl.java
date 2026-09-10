/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.headless.delivery.dto.v1_0.WikiPageAttachment;
import com.liferay.headless.delivery.dto.v1_0.util.ContentValueUtil;
import com.liferay.headless.delivery.resource.v1_0.WikiPageAttachmentResource;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.wiki.constants.WikiConstants;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageService;

import jakarta.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/wiki-page-attachment.properties",
	scope = ServiceScope.PROTOTYPE, service = WikiPageAttachmentResource.class
)
public class WikiPageAttachmentResourceImpl
	extends BaseWikiPageAttachmentResourceImpl {

	@Override
	public void
			deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(
				Long siteId, String wikiPageExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		WikiPage wikiPage =
			_wikiPageService.getLatestPageByExternalReferenceCode(
				siteId, wikiPageExternalReferenceCode);

		_deleteWikiPageAttachment(
			wikiPage.getAttachmentsFileEntryByExternalReferenceCode(
				siteId, externalReferenceCode));
	}

	@Override
	public void deleteWikiPageAttachment(Long wikiPageAttachmentId)
		throws Exception {

		_deleteWikiPageAttachment(
			_portletFileRepository.getPortletFileEntry(wikiPageAttachmentId));
	}

	@Override
	public WikiPageAttachment
			getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(
				Long siteId, String wikiPageExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		WikiPage wikiPage =
			_wikiPageService.getLatestPageByExternalReferenceCode(
				siteId, wikiPageExternalReferenceCode);

		FileEntry fileEntry =
			wikiPage.getAttachmentsFileEntryByExternalReferenceCode(
				siteId, externalReferenceCode);

		return _toWikiPageAttachment(fileEntry);
	}

	@Override
	public WikiPageAttachment getWikiPageAttachment(Long wikiPageAttachmentId)
		throws Exception {

		FileEntry fileEntry = _portletFileRepository.getPortletFileEntry(
			wikiPageAttachmentId);

		WikiPage wikiPage = _wikiPageService.getPage(
			_getWikiPageResourcePrimKey(fileEntry));

		_checkAttachmentsFolder(fileEntry, wikiPage);

		return _toWikiPageAttachment(fileEntry);
	}

	@Override
	public Page<WikiPageAttachment> getWikiPageWikiPageAttachmentsPage(
			Long wikiPageId)
		throws Exception {

		WikiPage wikiPage = _wikiPageService.getPage(wikiPageId);

		return Page.of(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					ActionKeys.UPDATE, "postWikiPageWikiPageAttachmentBatch",
					WikiPage.class.getName(), wikiPageId)
			).build(),
			transform(
				wikiPage.getAttachmentsFileEntries(),
				this::_toWikiPageAttachment));
	}

	@Override
	public WikiPageAttachment postWikiPageWikiPageAttachment(
			Long wikiPageId, MultipartBody multipartBody)
		throws Exception {

		WikiPage wikiPage = _wikiPageService.getPage(wikiPageId);

		_wikiPageModelResourcePermission.check(
			PermissionThreadLocal.getPermissionChecker(), wikiPage,
			ActionKeys.UPDATE);

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile == null) {
			throw new BadRequestException("No file found in body");
		}

		String externalReferenceCode = null;

		WikiPageAttachment wikiPageAttachment =
			multipartBody.getValueAsNullableInstance(
				"wikiPageAttachment", WikiPageAttachment.class);

		if (wikiPageAttachment != null) {
			externalReferenceCode =
				wikiPageAttachment.getExternalReferenceCode();
		}

		Folder folder = wikiPage.addAttachmentsFolder();

		return _toWikiPageAttachment(
			_portletFileRepository.addPortletFileEntry(
				externalReferenceCode, wikiPage.getGroupId(),
				contextUser.getUserId(), WikiPage.class.getName(),
				wikiPage.getResourcePrimKey(), WikiConstants.SERVICE_NAME,
				folder.getFolderId(), binaryFile.getInputStream(),
				binaryFile.getFileName(), binaryFile.getContentType(), true));
	}

	private void _checkAttachmentsFolder(FileEntry fileEntry, WikiPage wikiPage)
		throws Exception {

		if (wikiPage.getAttachmentsFolderId() != fileEntry.getFolderId()) {
			throw new NoSuchFileEntryException(
				"No file entry exists with file entry ID " +
					fileEntry.getFileEntryId());
		}
	}

	private void _deleteWikiPageAttachment(FileEntry fileEntry)
		throws Exception {

		WikiPage wikiPage = _wikiPageService.getPage(
			_getWikiPageResourcePrimKey(fileEntry));

		_checkAttachmentsFolder(fileEntry, wikiPage);

		_wikiPageService.deletePageAttachment(
			wikiPage.getNodeId(), wikiPage.getTitle(), fileEntry.getTitle());
	}

	private long _getWikiPageResourcePrimKey(FileEntry fileEntry)
		throws Exception {

		Folder folder = fileEntry.getFolder();

		if (folder == null) {
			throw new NoSuchFileEntryException(
				"No file entry exists with file entry ID " +
					fileEntry.getFileEntryId());
		}

		return GetterUtil.getLong(folder.getName());
	}

	private WikiPageAttachment _toWikiPageAttachment(FileEntry fileEntry)
		throws Exception {

		return new WikiPageAttachment() {
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
	private PortletFileRepository _portletFileRepository;

	@Reference(target = "(model.class.name=com.liferay.wiki.model.WikiPage)")
	private ModelResourcePermission<WikiPage> _wikiPageModelResourcePermission;

	@Reference
	private WikiPageService _wikiPageService;

}