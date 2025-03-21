/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.util.Portal;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Alejandro Hernández
 * @author Cristina González
 */
@Provider
public class CompanyContextProvider implements ContextProvider<Company> {

	public CompanyContextProvider(Portal portal) {
		_portal = portal;
	}

	@Override
	public Company createContext(Message message) {
		try {
			return _portal.getCompany(
				ContextProviderUtil.getHttpServletRequest(message));
		}
		catch (PortalException portalException) {
			throw new ServerErrorException(500, portalException);
		}
	}

	private final Portal _portal;

}