/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.portlet.route;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.wiki.constants.WikiPortletKeys;
import com.liferay.wiki.escape.WikiEscapeUtil;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shinn Lok
 * @author Levente Hudák
 */
@Component(
	property = {
		"com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/routes.xml",
		"jakarta.portlet.name=" + WikiPortletKeys.WIKI
	},
	service = FriendlyURLMapper.class
)
public class WikiFriendlyURLMapper extends DefaultFriendlyURLMapper {

	@Override
	public String buildPath(LiferayPortletURL liferayPortletURL) {
		Map<String, String> routeParameters = new HashMap<>();

		buildRouteParameters(liferayPortletURL, routeParameters);

		_addParameter(routeParameters, "nodeName", true);
		_addParameter(routeParameters, "title", true);

		String friendlyURLPath = router.parametersToUrl(routeParameters);

		if (Validator.isNull(friendlyURLPath)) {
			return null;
		}

		addParametersIncludedInPath(liferayPortletURL, routeParameters);

		return StringBundler.concat(
			StringPool.SLASH, getMapping(), friendlyURLPath);
	}

	@Override
	public String getMapping() {
		return _MAPPING;
	}

	@Override
	protected void populateParams(
		Map<String, String[]> parameterMap, String namespace,
		Map<String, String> routeParameters) {

		_addParameter(routeParameters, "nodeName", false);
		_addParameter(routeParameters, "title", false);

		super.populateParams(parameterMap, namespace, routeParameters);
	}

	private void _addParameter(
		Map<String, String> routeParameters, String name, boolean escape) {

		if (!routeParameters.containsKey(name)) {
			return;
		}

		String value = routeParameters.get(name);

		if (escape) {
			value = WikiEscapeUtil.escapeName(value);
		}
		else {
			value = WikiEscapeUtil.unescapeName(value);
		}

		routeParameters.put(name, value);
	}

	private static final String _MAPPING = "wiki";

}