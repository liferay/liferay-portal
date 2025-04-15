/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.oauth2.provider.util.OAuth2SecureRandomGenerator;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN,
		"mvc.command.name=/oauth2_provider/generate_random_secret"
	},
	service = MVCResourceCommand.class
)
public class GenerateRandomSecretMVCResourceCommand
	implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		resourceResponse.setContentType("text/plain");

		try {
			PrintWriter printWriter = resourceResponse.getWriter();

			String randomSecret =
				OAuth2SecureRandomGenerator.generateClientSecret();

			printWriter.write(randomSecret);
		}
		catch (IOException ioException) {
			throw new PortletException(ioException);
		}

		return false;
	}

}