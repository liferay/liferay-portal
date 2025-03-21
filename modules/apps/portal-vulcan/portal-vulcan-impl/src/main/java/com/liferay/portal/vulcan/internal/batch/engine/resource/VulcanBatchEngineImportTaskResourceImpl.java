/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.batch.engine.resource;

import com.liferay.headless.batch.engine.resource.v1_0.ImportTaskResource;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 */
public class VulcanBatchEngineImportTaskResourceImpl
	implements VulcanBatchEngineImportTaskResource {

	public VulcanBatchEngineImportTaskResourceImpl(
		ImportTaskResource.Factory factory) {

		_factory = factory;
	}

	@Override
	public Object deleteImportTask(
			String name, String callbackURL, Object object)
		throws Exception {

		ImportTaskResource importTaskResource = _getImportTaskResource();

		return importTaskResource.deleteImportTask(
			name, callbackURL, _getExternalReferenceCode(),
			_getImportStrategy(), _getTaskItemDelegateName(), object);
	}

	@Override
	public Object postImportTask(
			String name, String callbackURL, String fields, Object object)
		throws Exception {

		ImportTaskResource importTaskResource = _getImportTaskResource();

		return importTaskResource.postImportTask(
			name, null, null, callbackURL,
			_getQueryParameterValue("createStrategy"),
			_getExternalReferenceCode(), fields, _getImportStrategy(),
			_getTaskItemDelegateName(), _getItemsArray(object));
	}

	@Override
	public Object putImportTask(String name, String callbackURL, Object object)
		throws Exception {

		ImportTaskResource importTaskResource = _getImportTaskResource();

		return importTaskResource.putImportTask(
			name, callbackURL, _getExternalReferenceCode(),
			_getImportStrategy(), _getTaskItemDelegateName(),
			_getQueryParameterValue("updateStrategy"), object);
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
	public void setTaskItemDelegateName(String taskItemDelegateName) {
		_taskItemDelegateName = taskItemDelegateName;
	}

	private String _getExternalReferenceCode() {
		return _getQueryParameterValue("externalReferenceCode");
	}

	private String _getImportStrategy() {
		return _getQueryParameterValue("importStrategy");
	}

	private ImportTaskResource _getImportTaskResource() {
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

	private Object _getItemsArray(Object object) {
		if (!(object instanceof Map)) {
			return object;
		}

		Map<?, ?> map = (Map)object;

		return map.get("items");
	}

	private String _getQueryParameterValue(String queryParameterName) {
		MultivaluedMap<String, String> queryParameters =
			_contextUriInfo.getQueryParameters();

		for (Map.Entry<String, List<String>> entry :
				queryParameters.entrySet()) {

			if (!Objects.equals(entry.getKey(), queryParameterName)) {
				continue;
			}

			List<String> values = entry.getValue();

			if (values.isEmpty()) {
				continue;
			}

			return values.get(0);
		}

		return null;
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
	private final ImportTaskResource.Factory _factory;
	private String _taskItemDelegateName;

}