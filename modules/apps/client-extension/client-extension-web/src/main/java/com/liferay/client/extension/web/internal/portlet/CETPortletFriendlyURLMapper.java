/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.portlet;

import com.liferay.client.extension.web.internal.type.deployer.Registrable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.Route;
import com.liferay.portal.kernel.portlet.Router;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portlet.RouterImpl;

import jakarta.portlet.WindowState;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Iván Zaera Avellón
 */
public class CETPortletFriendlyURLMapper
	extends DefaultFriendlyURLMapper implements FriendlyURLMapper, Registrable {

	public CETPortletFriendlyURLMapper(String mapping, String portletId) {
		_mapping = mapping;
		_portletId = portletId;

		Router router = new RouterImpl();

		Route route = router.addRoute(StringPool.BLANK);

		route.addImplicitParameter("p_p_lifecycle", "0");
		route.addImplicitParameter("p_p_state", WindowState.NORMAL.toString());

		super.router = router;
	}

	@Override
	public String buildPath(LiferayPortletURL liferayPortletURL) {
		Map<String, String> routeParameters = new HashMap<>();

		buildRouteParameters(liferayPortletURL, routeParameters);

		String friendlyURLPath = router.parametersToUrl(routeParameters);

		if (friendlyURLPath == null) {
			return null;
		}

		addParametersIncludedInPath(liferayPortletURL, routeParameters);

		return StringBundler.concat(
			StringPool.SLASH, getMapping(), friendlyURLPath);
	}

	@Override
	public Dictionary<String, Object> getDictionary() {
		return HashMapDictionaryBuilder.<String, Object>put(
			"jakarta.portlet.name", _portletId
		).build();
	}

	@Override
	public String getMapping() {
		return _mapping;
	}

	@Override
	public void setRouter(Router router) {
	}

	private final String _mapping;
	private final String _portletId;

}