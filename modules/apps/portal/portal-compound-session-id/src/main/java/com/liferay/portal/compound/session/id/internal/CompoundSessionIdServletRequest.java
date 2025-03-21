/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.compound.session.id.internal;

import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.servlet.filters.compoundsessionid.CompoundSessionIdHttpSession;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * @author Michael C. Han
 */
public class CompoundSessionIdServletRequest
	extends PersistentHttpServletRequestWrapper {

	public CompoundSessionIdServletRequest(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);
	}

	@Override
	public HttpSession getSession() {
		return _getCompoundSessionIdHttpSession(super.getSession());
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession httpSession = super.getSession(create);

		if (httpSession == null) {
			return httpSession;
		}

		return _getCompoundSessionIdHttpSession(httpSession);
	}

	private CompoundSessionIdHttpSession _getCompoundSessionIdHttpSession(
		HttpSession httpSession) {

		if ((_compoundSessionIdHttpSession != null) &&
			(httpSession ==
				_compoundSessionIdHttpSession.getWrappedSession())) {

			return _compoundSessionIdHttpSession;
		}

		_compoundSessionIdHttpSession = new CompoundSessionIdHttpSession(
			httpSession);

		return _compoundSessionIdHttpSession;
	}

	private CompoundSessionIdHttpSession _compoundSessionIdHttpSession;

}