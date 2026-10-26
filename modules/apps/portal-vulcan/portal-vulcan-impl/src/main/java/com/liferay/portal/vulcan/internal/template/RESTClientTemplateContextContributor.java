/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.template;

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.template.TemplateContextContributor;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"template.context.keys=restClient",
		"type=" + TemplateContextContributor.TYPE_GLOBAL
	},
	service = TemplateContextContributor.class
)
public class RESTClientTemplateContextContributor
	implements TemplateContextContributor {

	@Override
	public void prepare(
		Map<String, Object> contextObjects,
		HttpServletRequest httpServletRequest) {

		contextObjects.put(
			"restClient", new RESTClient(contextObjects, httpServletRequest));
	}

	public class RESTClient {

		public RESTClient(
			Map<String, Object> contextObjects,
			HttpServletRequest httpServletRequest) {

			_contextObjects = contextObjects;
			_httpServletRequest = httpServletRequest;
		}

		public Object get(String path) throws Exception {
			try {
				return _get(path);
			}
			catch (Throwable throwable) {
				_log.error(throwable, throwable);

				throw throwable;
			}
		}

		private Object _get(String path) throws Exception {
			VulcanRequestForwarder.Response response =
				_vulcanRequestForwarder.forward(
					_httpServletRequest,
					new VulcanRequestForwarder.Request() {

						@Override
						public String getMethod() {
							return "GET";
						}

						@Override
						public String getPath() {
							return path;
						}

						@Override
						public User getUser() {
							return (User)_contextObjects.get("user");
						}

					});

			if (Objects.equals(
					response.getContentType(), ContentTypes.APPLICATION_JSON)) {

				return _jsonFactory.looseDeserialize(response.getContent());
			}

			return response.getContent();
		}

		private final Map<String, Object> _contextObjects;
		private final HttpServletRequest _httpServletRequest;

	}

	private static final Log _log = LogFactoryUtil.getLog(
		RESTClientTemplateContextContributor.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private VulcanRequestForwarder _vulcanRequestForwarder;

}