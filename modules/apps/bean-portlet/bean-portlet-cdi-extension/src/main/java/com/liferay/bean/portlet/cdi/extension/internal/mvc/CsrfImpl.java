/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.mvc.security.Csrf;

/**
 * @author Neil Griffin
 */
public class CsrfImpl implements Csrf {

	public CsrfImpl(String name, String token) {
		_name = name;
		_token = token;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getToken() {
		return _token;
	}

	private final String _name;
	private final String _token;

}