/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.encryptor.EncryptorException;
import com.liferay.portal.kernel.encryptor.EncryptorUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.security.Key;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 */
public class EncryptedServletRequest extends HttpServletRequestWrapper {

	public EncryptedServletRequest(
		HttpServletRequest httpServletRequest, Key key) {

		super(httpServletRequest);

		_key = key;

		Map<String, String[]> parameters = httpServletRequest.getParameterMap();

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String name = entry.getKey();

			String[] values = entry.getValue();

			for (int i = 0; i < values.length; i++) {
				if (Validator.isNotNull(values[i])) {
					try {
						values[i] = EncryptorUtil.decrypt(_key, values[i]);
					}
					catch (EncryptorException encryptorException) {
						if (_log.isDebugEnabled()) {
							_log.debug(encryptorException);
						}

						values[i] = StringPool.BLANK;
					}
				}
			}

			_params.put(name, values);
		}
	}

	@Override
	public String getParameter(String name) {
		String[] values = _params.get(name);

		if (ArrayUtil.isNotEmpty(values)) {
			return values[0];
		}

		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(_params);
	}

	@Override
	public String[] getParameterValues(String name) {
		return _params.get(name);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EncryptedServletRequest.class);

	private final Key _key;
	private final Map<String, String[]> _params = new HashMap<>();

}