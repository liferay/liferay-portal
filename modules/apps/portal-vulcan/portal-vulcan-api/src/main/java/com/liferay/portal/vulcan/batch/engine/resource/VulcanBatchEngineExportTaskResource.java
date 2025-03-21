/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.batch.engine.resource;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.UriInfo;

/**
 * @author Carlos Correa
 */
public interface VulcanBatchEngineExportTaskResource {

	public Object postExportTask(
			String name, String callbackURL, String contentType,
			String fieldNames)
		throws Exception;

	public void setContextAcceptLanguage(AcceptLanguage contextAcceptLanguage);

	public void setContextCompany(Company contextCompany);

	public void setContextHttpServletRequest(
		HttpServletRequest contextHttpServletRequest);

	public void setContextUriInfo(UriInfo contextUriInfo);

	public void setContextUser(User contextUser);

	public void setGroupLocalService(GroupLocalService groupLocalService);

	public void setTaskItemDelegateName(String taskItemDelegateName);

}