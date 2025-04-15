/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0;

import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentBase64;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.AttachmentUrl;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.osgi.annotation.versioning.ProviderType;

/**
 * To access this resource, run:
 *
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-commerce-admin-catalog/v1.0
 *
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@ProviderType
public interface AttachmentResource {

	public void deleteAttachmentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public Attachment getAttachmentByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public Attachment patchAttachmentByExternalReferenceCode(
			String externalReferenceCode, Attachment attachment)
		throws Exception;

	public Attachment putAttachmentByExternalReferenceCode(
			String externalReferenceCode, Attachment attachment)
		throws Exception;

	public void deleteAttachment(Long id) throws Exception;

	public Response deleteAttachmentBatch(String callbackURL, Object object)
		throws Exception;

	public Page<Attachment> getProductByExternalReferenceCodeAttachmentsPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception;

	public Attachment postProductByExternalReferenceCodeAttachment(
			String externalReferenceCode, Attachment attachment)
		throws Exception;

	public Attachment postProductByExternalReferenceCodeAttachmentByBase64(
			String externalReferenceCode, AttachmentBase64 attachmentBase64)
		throws Exception;

	public Attachment postProductByExternalReferenceCodeAttachmentByUrl(
			String externalReferenceCode, AttachmentUrl attachmentUrl)
		throws Exception;

	public Page<Attachment> getProductByExternalReferenceCodeImagesPage(
			String externalReferenceCode, Pagination pagination)
		throws Exception;

	public Attachment postProductByExternalReferenceCodeImage(
			String externalReferenceCode, Attachment attachment)
		throws Exception;

	public Attachment postProductByExternalReferenceCodeImageByBase64(
			String externalReferenceCode, AttachmentBase64 attachmentBase64)
		throws Exception;

	public Attachment postProductByExternalReferenceCodeImageByUrl(
			String externalReferenceCode, AttachmentUrl attachmentUrl)
		throws Exception;

	public Page<Attachment> getProductIdAttachmentsPage(
			Long id, Pagination pagination)
		throws Exception;

	public Attachment postProductIdAttachment(Long id, Attachment attachment)
		throws Exception;

	public Response postProductIdAttachmentBatch(
			String callbackURL, Object object)
		throws Exception;

	public Attachment postProductIdAttachmentByBase64(
			Long id, AttachmentBase64 attachmentBase64)
		throws Exception;

	public Attachment postProductIdAttachmentByUrl(
			Long id, AttachmentUrl attachmentUrl)
		throws Exception;

	public Page<Attachment> getProductIdImagesPage(
			Long id, Pagination pagination)
		throws Exception;

	public Attachment postProductIdImage(Long id, Attachment attachment)
		throws Exception;

	public Attachment postProductIdImageByBase64(
			Long id, AttachmentBase64 attachmentBase64)
		throws Exception;

	public Attachment postProductIdImageByUrl(
			Long id, AttachmentUrl attachmentUrl)
		throws Exception;

	public default void setContextAcceptLanguage(
		AcceptLanguage contextAcceptLanguage) {
	}

	public void setContextCompany(
		com.liferay.portal.kernel.model.Company contextCompany);

	public default void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {
	}

	public default void setContextHttpServletResponse(
		HttpServletResponse contextHttpServletResponse) {
	}

	public default void setContextUriInfo(UriInfo contextUriInfo) {
	}

	public void setContextUser(
		com.liferay.portal.kernel.model.User contextUser);

	public void setExpressionConvert(
		ExpressionConvert<com.liferay.portal.kernel.search.filter.Filter>
			expressionConvert);

	public void setFilterParserProvider(
		FilterParserProvider filterParserProvider);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setResourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService);

	public void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService);

	public void setRoleLocalService(RoleLocalService roleLocalService);

	public void setSortParserProvider(SortParserProvider sortParserProvider);

	public void setVulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource);

	public void setVulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource);

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString) {

		return toFilter(
			filterString, Collections.<String, List<String>>emptyMap());
	}

	public default com.liferay.portal.kernel.search.filter.Filter toFilter(
		String filterString, Map<String, List<String>> multivaluedMap) {

		return null;
	}

	public default com.liferay.portal.kernel.search.Sort[] toSorts(
		String sortsString) {

		return new com.liferay.portal.kernel.search.Sort[0];
	}

	@ProviderType
	public interface Builder {

		public AttachmentResource build();

		public Builder checkPermissions(boolean checkPermissions);

		public Builder httpServletRequest(
			HttpServletRequest httpServletRequest);

		public Builder httpServletResponse(
			HttpServletResponse httpServletResponse);

		public Builder preferredLocale(Locale preferredLocale);

		public Builder uriInfo(UriInfo uriInfo);

		public Builder user(com.liferay.portal.kernel.model.User user);

	}

	@ProviderType
	public interface Factory {

		public Builder create();

	}

}