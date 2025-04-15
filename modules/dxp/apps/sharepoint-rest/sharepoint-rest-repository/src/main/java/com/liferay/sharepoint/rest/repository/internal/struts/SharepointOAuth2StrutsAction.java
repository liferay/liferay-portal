/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.rest.repository.internal.struts;

import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.repository.authorization.capability.AuthorizationCapability;
import com.liferay.portal.kernel.repository.Repository;
import com.liferay.portal.kernel.repository.RepositoryProviderUtil;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.sharepoint.rest.repository.internal.document.library.repository.authorization.oauth2.SharepointRepositoryRequestState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "path=/document_library/sharepoint/oauth2",
	service = StrutsAction.class
)
public class SharepointOAuth2StrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		Repository repository = _getRepository(httpServletRequest);

		if (repository.isCapabilityProvided(AuthorizationCapability.class)) {
			AuthorizationCapability authorizationCapability =
				repository.getCapability(AuthorizationCapability.class);

			authorizationCapability.authorize(
				httpServletRequest, httpServletResponse);
		}

		return null;
	}

	private Repository _getRepository(HttpServletRequest httpServletRequest)
		throws Exception {

		SharepointRepositoryRequestState sharepointRepositoryRequestState =
			SharepointRepositoryRequestState.get(httpServletRequest);

		Folder folder = _dlAppLocalService.getFolder(
			sharepointRepositoryRequestState.getFolderId());

		return RepositoryProviderUtil.getRepository(folder.getRepositoryId());
	}

	@Reference
	private DLAppLocalService _dlAppLocalService;

}