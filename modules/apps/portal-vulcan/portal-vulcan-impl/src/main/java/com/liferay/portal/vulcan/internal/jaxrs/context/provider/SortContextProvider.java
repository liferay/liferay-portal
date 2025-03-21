/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.sort.SortParserProvider;
import com.liferay.portal.vulcan.internal.accept.language.AcceptLanguageImpl;
import com.liferay.portal.vulcan.util.SortUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Brian Wing Shun Chan
 */
@Provider
public class SortContextProvider implements ContextProvider<Sort[]> {

	public SortContextProvider(
		Language language, Portal portal,
		SortParserProvider sortParserProvider) {

		_language = language;
		_portal = portal;
		_sortParserProvider = sortParserProvider;
	}

	@Override
	public Sort[] createContext(Message message) {
		try {
			HttpServletRequest httpServletRequest =
				ContextProviderUtil.getHttpServletRequest(message);

			EntityModel entityModel = ContextProviderUtil.getEntityModel(
				message);

			return SortUtil.getSorts(
				new AcceptLanguageImpl(httpServletRequest, _language, _portal),
				entityModel, _sortParserProvider.provide(entityModel),
				ParamUtil.getString(httpServletRequest, "sort"));
		}
		catch (WebApplicationException webApplicationException) {
			throw webApplicationException;
		}
		catch (Exception exception) {
			throw new ServerErrorException(500, exception);
		}
	}

	private final Language _language;
	private final Portal _portal;
	private final SortParserProvider _sortParserProvider;

}