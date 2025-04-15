/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.internal.accept.language.AcceptLanguageImpl;

import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Cristina González
 */
@Provider
public class AcceptLanguageContextProvider
	implements ContextProvider<AcceptLanguage> {

	public AcceptLanguageContextProvider(Language language, Portal portal) {
		_language = language;
		_portal = portal;
	}

	@Override
	public AcceptLanguage createContext(Message message) {
		return new AcceptLanguageImpl(
			ContextProviderUtil.getHttpServletRequest(message), _language,
			_portal);
	}

	private final Language _language;
	private final Portal _portal;

}