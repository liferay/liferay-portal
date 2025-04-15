/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.servlet;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.translation.exception.TranslatorException;
import com.liferay.translation.translator.JSONTranslatorPacket;
import com.liferay.translation.translator.Translator;
import com.liferay.translation.translator.TranslatorPacket;
import com.liferay.translation.translator.TranslatorRegistry;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.translation.web.internal.servlet.AutoTranslateServlet",
		"osgi.http.whiteboard.servlet.pattern=/translation/auto_translate",
		"servlet.init.httpMethods=POST"
	},
	service = Servlet.class
)
public class AutoTranslateServlet extends HttpServlet {

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			String content = StreamUtil.toString(
				httpServletRequest.getInputStream());

			long companyId = _portal.getCompanyId(httpServletRequest);

			Translator translator = _translatorRegistry.getCompanyTranslator(
				companyId);

			if (translator != null) {
				TranslatorPacket translatedTranslatorPacket =
					translator.translate(
						new JSONTranslatorPacket(
							companyId, _jsonFactory.createJSONObject(content)));

				_writeJSON(
					httpServletResponse, _toJSON(translatedTranslatorPacket));
			}
			else {
				_writeErrorJSON(
					httpServletResponse,
					_language.get(
						_portal.getLocale(httpServletRequest),
						"there-is-no-translation-service-enabled.-please-" +
							"contact-your-administrator"));
			}
		}
		catch (TranslatorException translatorException) {
			_log.error(translatorException);

			_writeErrorJSON(
				httpServletResponse,
				StringUtil.replace(
					translatorException.getMessage(), CharPool.QUOTE, "\\\""));
		}
		catch (Exception exception) {
			_log.error(exception);

			_writeErrorJSON(
				httpServletResponse,
				_language.get(
					_portal.getLocale(httpServletRequest),
					"there-is-a-problem-with-the-translation-service.-please-" +
						"contact-your-administrator"));
		}
	}

	private JSONObject _getFieldsJSONObject(Map<String, ?> fieldsMap) {
		JSONObject jsonObject = _jsonFactory.createJSONObject();

		for (Map.Entry<String, ?> entry : fieldsMap.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}

		return jsonObject;
	}

	private String _toJSON(TranslatorPacket translatorPacket) {
		return JSONUtil.put(
			"fields", _getFieldsJSONObject(translatorPacket.getFieldsMap())
		).put(
			"html", _getFieldsJSONObject(translatorPacket.getHTMLMap())
		).put(
			"sourceLanguageId", translatorPacket.getSourceLanguageId()
		).put(
			"targetLanguageId", translatorPacket.getTargetLanguageId()
		).toString();
	}

	private void _writeErrorJSON(
			HttpServletResponse httpServletResponse, String message)
		throws IOException {

		_writeJSON(
			httpServletResponse,
			StringBundler.concat(
				"{\"error\": {\"message\": \"", message, "\"}}"));
	}

	private void _writeJSON(
			HttpServletResponse httpServletResponse, String json)
		throws IOException {

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(httpServletResponse, json);

		httpServletResponse.flushBuffer();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AutoTranslateServlet.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private TranslatorRegistry _translatorRegistry;

}