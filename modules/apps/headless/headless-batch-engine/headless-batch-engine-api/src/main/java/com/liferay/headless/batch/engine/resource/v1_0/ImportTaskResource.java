/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.batch.engine.resource.v1_0;

import com.liferay.headless.batch.engine.dto.v1_0.ImportTask;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.odata.filter.ExpressionConvert;
import com.liferay.portal.odata.filter.FilterParserProvider;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.multipart.MultipartBody;

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
 *     curl -u your@email.com:yourpassword -D - http://localhost:8080/o/headless-batch-engine/v1.0
 *
 * @author Ivica Cardic
 * @generated
 */
@Generated("")
@ProviderType
public interface ImportTaskResource {

	public ImportTask getImportTaskByExternalReferenceCode(
			String externalReferenceCode)
		throws Exception;

	public Response getImportTaskByExternalReferenceCodeContent(
			String externalReferenceCode)
		throws Exception;

	public Response getImportTaskByExternalReferenceCodeFailedItemReport(
			String externalReferenceCode)
		throws Exception;

	public ImportTask deleteImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName, Object object)
		throws Exception;

	public ImportTask deleteImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			MultipartBody multipartBody)
		throws Exception;

	public ImportTask postImportTask(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, Object object)
		throws Exception;

	public ImportTask postImportTask(
			String className, String batchExternalReferenceCode,
			String batchRestrictFields, String callbackURL,
			String createStrategy, String externalReferenceCode,
			String fieldNameMapping, String importStrategy,
			String taskItemDelegateName, MultipartBody multipartBody)
		throws Exception;

	public ImportTask putImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, Object object)
		throws Exception;

	public ImportTask putImportTask(
			String className, String callbackURL, String externalReferenceCode,
			String importStrategy, String taskItemDelegateName,
			String updateStrategy, MultipartBody multipartBody)
		throws Exception;

	public ImportTask getImportTask(Long importTaskId) throws Exception;

	public Response getImportTaskContent(Long importTaskId) throws Exception;

	public Response getImportTaskFailedItemReport(Long importTaskId)
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

		public ImportTaskResource build();

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