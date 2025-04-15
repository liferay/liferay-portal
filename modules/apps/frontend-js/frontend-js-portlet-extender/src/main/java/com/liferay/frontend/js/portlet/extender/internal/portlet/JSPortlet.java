/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.portlet.extender.internal.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.service.cm.ManagedService;

/**
 * @author Ray Augé
 * @author Iván Zaera Avellón
 * @author Gustavo Mantuan
 */
public class JSPortlet extends MVCPortlet implements ManagedService {

	public JSPortlet(
		JSONFactory jsonFactory, String packageName, String packageVersion,
		Portal portal, Set<String> portletPreferencesFieldNames) {

		_jsonFactory = jsonFactory;
		_packageName = packageName;
		_packageVersion = packageVersion;
		_portal = portal;
		_portletPreferencesFieldNames = portletPreferencesFieldNames;
	}

	@Override
	public void render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		try {
			PrintWriter printWriter = renderResponse.getWriter();

			String portletElementId =
				"js-portlet-" + renderResponse.getNamespace();

			printWriter.print(
				StringUtil.replace(
					_TPL_HTML, new String[] {"[$PORTLET_ELEMENT_ID$]"},
					new String[] {portletElementId}));

			printWriter.print(
				StringUtil.replace(
					_TPL_JAVA_SCRIPT,
					new String[] {
						"[$CONTEXT_PATH$]", "[$NONCE_ATTRIBUTE$]",
						"[$PACKAGE_NAME$]", "[$PACKAGE_VERSION$]",
						"[$PORTLET_ELEMENT_ID$]",
						"[$PORTLET_INSTANCE_CONFIGURATION$]",
						"[$PORTLET_NAMESPACE$]", "[$SYSTEM_CONFIGURATION$]"
					},
					new String[] {
						renderRequest.getContextPath(),
						ContentSecurityPolicyNonceProviderUtil.
							getNonceAttribute(
								_portal.getHttpServletRequest(renderRequest)),
						_packageName, _packageVersion, portletElementId,
						_getPortletInstanceConfiguration(renderRequest),
						renderResponse.getNamespace(), _getSystemConfiguration()
					}));

			printWriter.flush();
		}
		catch (IOException ioException) {
			_log.error("Unable to render HTML output", ioException);
		}
	}

	@Override
	public void updated(Dictionary<String, ?> properties) {
		if (properties == null) {
			_configuration.set(Collections.emptyMap());

			return;
		}

		Map<String, Object> configuration = new HashMap<>();

		Enumeration<String> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (key.equals("service.pid")) {
				continue;
			}

			Object value = properties.get(key);

			if (value instanceof Vector) {
				value = new ArrayList<>((Vector)value);
			}
			else {
				value = String.valueOf(value);
			}

			configuration.put(key, value);
		}

		_configuration.set(configuration);
	}

	private static String _loadTemplate(String name) {
		try (InputStream inputStream = JSPortlet.class.getResourceAsStream(
				"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private String _escapeJSON(String json) {
		json = json.replaceAll(StringPool.DOUBLE_BACK_SLASH, "\\\\\\\\");

		json = json.replaceAll(StringPool.APOSTROPHE, "\\\\'");

		return json;
	}

	private String _getPortletInstanceConfiguration(
		RenderRequest renderRequest) {

		PortletPreferences portletPreferences = renderRequest.getPreferences();

		JSONObject portletPreferencesJSONObject =
			_jsonFactory.createJSONObject();

		Enumeration<String> enumeration = portletPreferences.getNames();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (!_portletPreferencesFieldNames.contains(key)) {
				continue;
			}

			String[] values = portletPreferences.getValues(
				key, StringPool.EMPTY_ARRAY);

			if (values.length > 1) {
				portletPreferencesJSONObject.put(key, values);
			}
			else {
				portletPreferencesJSONObject.put(key, values[0]);
			}
		}

		return _escapeJSON(portletPreferencesJSONObject.toString());
	}

	private String _getSystemConfiguration() {
		return _escapeJSON(
			_jsonFactory.looseSerializeDeep(_configuration.get()));
	}

	private static final String _TPL_HTML;

	private static final String _TPL_JAVA_SCRIPT;

	private static final Log _log = LogFactoryUtil.getLog(JSPortlet.class);

	static {
		_TPL_HTML = _loadTemplate("bootstrap.html.tpl");
		_TPL_JAVA_SCRIPT = _loadTemplate("bootstrap.js.tpl");
	}

	private final AtomicReference<Map<String, Object>> _configuration =
		new AtomicReference<>();
	private final JSONFactory _jsonFactory;
	private final String _packageName;
	private final String _packageVersion;
	private final Portal _portal;
	private final Set<String> _portletPreferencesFieldNames;

}