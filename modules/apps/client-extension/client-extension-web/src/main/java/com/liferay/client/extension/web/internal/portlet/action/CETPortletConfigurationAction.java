/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.portlet.action;

import com.liferay.client.extension.web.internal.type.deployer.Registrable;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Dictionary;

/**
 * @author Iván Zaera Avellón
 */
public class CETPortletConfigurationAction
	extends DefaultConfigurationAction implements Registrable {

	public CETPortletConfigurationAction(String jspPath, String portletId) {
		_jspPath = jspPath;
		_portletId = portletId;
	}

	@Override
	public Dictionary<String, Object> getDictionary() {
		return HashMapDictionaryBuilder.<String, Object>put(
			"jakarta.portlet.name", _portletId
		).build();
	}

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return _jspPath;
	}

	private final String _jspPath;
	private final String _portletId;

}