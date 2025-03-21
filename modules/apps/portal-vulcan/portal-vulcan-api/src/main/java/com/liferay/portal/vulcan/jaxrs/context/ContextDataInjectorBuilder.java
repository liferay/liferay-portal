/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.jaxrs.context;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

/**
 * @author Carlos Correa
 */
public interface ContextDataInjectorBuilder {

	public ContextDataInjectorBuilder acceptLanguage(
		AcceptLanguage acceptLanguage);

	public ContextDataInjector build();

	public ContextDataInjectorBuilder company(Company company);

	public ContextDataInjectorBuilder expressionConvert(
		ExpressionConvert<?> expressionConvert);

	public ContextDataInjectorBuilder filterParserProvider(
		FilterParserProvider filterParserProvider);

	public ContextDataInjectorBuilder groupLocalService(
		GroupLocalService groupLocalService);

	public ContextDataInjectorBuilder httpServletRequest(
		HttpServletRequest httpServletRequest);

	public ContextDataInjectorBuilder httpServletResponse(
		HttpServletResponse httpServletResponse);

	public ContextDataInjectorBuilder resourceActionLocalService(
		ResourceActionLocalService resourceActionLocalService);

	public ContextDataInjectorBuilder resourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService);

	public ContextDataInjectorBuilder roleLocalService(
		RoleLocalService roleLocalService);

	public ContextDataInjectorBuilder scopeChecker(Object scopeChecker);

	public ContextDataInjectorBuilder sortParserProvider(
		SortParserProvider sortParserProvider);

	public ContextDataInjectorBuilder uriInfo(UriInfo uriInfo);

	public ContextDataInjectorBuilder user(User user);

	public ContextDataInjectorBuilder vulcanBatchEngineExportTaskResource(
		VulcanBatchEngineExportTaskResource
			vulcanBatchEngineExportTaskResource);

	public ContextDataInjectorBuilder vulcanBatchEngineImportTaskResource(
		VulcanBatchEngineImportTaskResource
			vulcanBatchEngineImportTaskResource);

}