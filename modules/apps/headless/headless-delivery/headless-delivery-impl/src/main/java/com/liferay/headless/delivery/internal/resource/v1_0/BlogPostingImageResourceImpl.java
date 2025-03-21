/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.resource.v1_0;

import com.liferay.blogs.constants.BlogsConstants;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.delivery.dto.v1_0.BlogPostingImage;
import com.liferay.headless.delivery.dto.v1_0.util.ContentValueUtil;
import com.liferay.headless.delivery.internal.odata.entity.v1_0.BlogPostingImageEntityModel;
import com.liferay.headless.delivery.resource.v1_0.BlogPostingImageResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.MultivaluedMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/blog-posting-image.properties",
	scope = ServiceScope.PROTOTYPE, service = BlogPostingImageResource.class
)
@CTAware
public class BlogPostingImageResourceImpl
	extends BaseBlogPostingImageResourceImpl {

	@Override
	public void deleteBlogPostingImage(Long blogPostingImageId)
		throws Exception {

		_blogsEntryService.deleteAttachmentFileEntry(blogPostingImageId);
	}

	@Override
	public void deleteSiteBlogPostingImageByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		super.deleteSiteBlogPostingImageByExternalReferenceCode(
			siteId, externalReferenceCode);

		FileEntry fileEntry =
			_blogsEntryService.getAttachmentFileEntryByExternalReferenceCode(
				externalReferenceCode, siteId);

		_blogsEntryService.deleteAttachmentFileEntry(
			fileEntry.getFileEntryId());
	}

	@Override
	public BlogPostingImage getBlogPostingImage(Long blogPostingImageId)
		throws Exception {

		return _toBlogPostingImage(
			_blogsEntryService.getAttachmentFileEntry(blogPostingImageId));
	}

	@Override
	public EntityModel getEntityModel(MultivaluedMap multivaluedMap) {
		return _entityModel;
	}

	@Override
	public BlogPostingImage getSiteBlogPostingImageByExternalReferenceCode(
			Long siteId, String externalReferenceCode)
		throws Exception {

		return _toBlogPostingImage(
			_blogsEntryService.getAttachmentFileEntryByExternalReferenceCode(
				externalReferenceCode, siteId));
	}

	@Override
	public Page<BlogPostingImage> getSiteBlogPostingImagesPage(
			Long siteId, String search, Aggregation aggregation, Filter filter,
			Pagination pagination, Sort[] sorts)
		throws Exception {

		Folder folder = _blogsEntryService.addAttachmentsFolder(siteId);

		return SearchUtil.search(
			HashMapBuilder.put(
				"createBatch",
				addAction(
					ActionKeys.ADD_ENTRY, "postSiteBlogPostingImageBatch",
					BlogsConstants.RESOURCE_NAME, siteId)
			).build(),
			booleanQuery -> {
			},
			filter, DLFileEntry.class.getName(), search, pagination,
			queryConfig -> queryConfig.setSelectedFieldNames(
				Field.ENTRY_CLASS_PK),
			searchContext -> {
				searchContext.addVulcanAggregation(aggregation);
				searchContext.setCompanyId(contextCompany.getCompanyId());
				searchContext.setFolderIds(new long[] {folder.getFolderId()});
			},
			sorts,
			document -> _toBlogPostingImage(
				_blogsEntryService.getAttachmentFileEntry(
					GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)))));
	}

	@Override
	public BlogPostingImage postSiteBlogPostingImage(
			Long siteId, MultipartBody multipartBody)
		throws Exception {

		BinaryFile binaryFile = multipartBody.getBinaryFile("file");

		if (binaryFile == null) {
			throw new BadRequestException("No file found in body");
		}

		String externalReferenceCode = null;
		String title = null;

		BlogPostingImage blogPostingImage =
			multipartBody.getValueAsNullableInstance(
				"blogPostingImage", BlogPostingImage.class);

		if (blogPostingImage != null) {
			externalReferenceCode = blogPostingImage.getExternalReferenceCode();
			title = blogPostingImage.getTitle();
		}

		if (title == null) {
			title = binaryFile.getFileName();
		}

		return _toBlogPostingImage(
			_blogsEntryService.addAttachmentFileEntry(
				externalReferenceCode, siteId, title,
				binaryFile.getContentType(), binaryFile.getInputStream()));
	}

	private BlogPostingImage _toBlogPostingImage(FileEntry fileEntry)
		throws Exception {

		return new BlogPostingImage() {
			{
				setContentUrl(
					() -> _dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null, ""));
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

	private static final EntityModel _entityModel =
		new BlogPostingImageEntityModel();

	@Reference
	private BlogsEntryService _blogsEntryService;

	@Reference
	private DLURLHelper _dlURLHelper;

}