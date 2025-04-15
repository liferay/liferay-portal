/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.resource;

import com.liferay.headless.batch.engine.resource.v1_0.ExportTaskResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

/**
 * @author Carlos Correa
 */
public class VulcanBatchEngineExportTaskResourceImpl
	implements VulcanBatchEngineExportTaskResource {

	public VulcanBatchEngineExportTaskResourceImpl(
		ExportTaskResource.Factory factory) {

		_factory = factory;
	}

	@Override
	public Object postExportTask(
			String name, String callbackURL, String contentType,
			String fieldNames)
		throws Exception {

		ExportTaskResource exportTaskResource = _getExportTaskResource();

		return exportTaskResource.postExportTask(
			name, contentType, null, callbackURL,
			_getQueryParameterValue("externalReferenceCode"), fieldNames,
			_getTaskItemDelegateName());
	}

	@Override
	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage) {
		_contextAcceptLanguage = contextAcceptLanguage;
	}

	@Override
	public void setContextCompany(Company contextCompany) {
		_contextCompany = contextCompany;
	}

	@Override
	public void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest) {

		_contextHttpServletRequest = contextHttpServletRequest;
	}

	@Override
	public void setContextUriInfo(UriInfo contextUriInfo) {
		_contextUriInfo = contextUriInfo;
	}

	@Override
	public void setContextUser(User contextUser) {
		_contextUser = contextUser;
	}

	@Override
	public void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Override
	public void setTaskItemDelegateName(String taskItemDelegateName) {
		_taskItemDelegateName = taskItemDelegateName;
	}

	private ExportTaskResource _getExportTaskResource() {
		return _factory.create(
		).httpServletRequest(
			_contextHttpServletRequest
		).preferredLocale(
			_contextAcceptLanguage.getPreferredLocale()
		).uriInfo(
			_contextUriInfo
		).user(
			_contextUser
		).build();
	}

	private String _getQueryParameterValue(String queryParameterName) {
		MultivaluedMap<String, String> queryParameters =
			_contextUriInfo.getQueryParameters();

		return queryParameters.getFirst(queryParameterName);
	}

	private String _getTaskItemDelegateName() {
		if (_taskItemDelegateName == null) {
			return _getQueryParameterValue("taskItemDelegateName");
		}

		return _taskItemDelegateName;
	}

	private AcceptLanguage _contextAcceptLanguage;
	private Company _contextCompany;
	private HttpServletRequest _contextHttpServletRequest;
	private UriInfo _contextUriInfo;
	private User _contextUser;
	private final ExportTaskResource.Factory _factory;
	private GroupLocalService _groupLocalService;
	private String _taskItemDelegateName;

}