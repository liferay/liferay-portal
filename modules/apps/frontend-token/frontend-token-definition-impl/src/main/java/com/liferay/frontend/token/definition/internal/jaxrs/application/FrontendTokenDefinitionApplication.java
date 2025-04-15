/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.token.definition.internal.jaxrs.application;

import com.liferay.frontend.token.definition.FrontendToken;
import com.liferay.frontend.token.definition.FrontendTokenCategory;
import com.liferay.frontend.token.definition.FrontendTokenDefinition;
import com.liferay.frontend.token.definition.FrontendTokenSet;
import com.liferay.frontend.token.definition.internal.FrontendTokenDefinitionImpl;
import com.liferay.frontend.token.definition.internal.validator.FrontendTokenDefinitionJSONValidator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.validator.JSONValidatorException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.jaxrs.whiteboard.JaxrsWhiteboardConstants;

/**
 * @author Anderson Luiz
 * @author Thiago Buarque
 */
@Component(
	property = {
		JaxrsWhiteboardConstants.JAX_RS_APPLICATION_BASE + "=/frontend-token-definition",
		JaxrsWhiteboardConstants.JAX_RS_NAME + "=com.liferay.frontend.token.definition.internal.jaxrs.application.FrontendTokenDefinitionApplication",
		"auth.verifier.auth.verifier.PortalSessionAuthVerifier.urls.includes=/*",
		"auth.verifier.guest.allowed=false", "liferay.oauth2=false"
	},
	service = Application.class
)
public class FrontendTokenDefinitionApplication extends Application {

	public Set<Object> getSingletons() {
		return Collections.singleton(this);
	}

	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Path("/validate-file")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response validateFile(
		@Context HttpServletRequest httpServletRequest) {

		Locale locale = _portal.getLocale(httpServletRequest);

		try {
			UploadServletRequest uploadServletRequest =
				_portal.getUploadServletRequest(httpServletRequest);

			return _getResponse(uploadServletRequest.getFile("file"), locale);
		}
		catch (IOException ioException) {
			_log.error(ioException);

			return _getResponse(
				_language.get(locale, "your-upload-failed-to-complete"),
				Response.Status.INTERNAL_SERVER_ERROR);
		}
		catch (JSONException | JSONValidatorException exception) {
			_log.error(exception);

			return _getResponse(
				_language.get(
					locale,
					"the-format-is-invalid.-please-upload-a-valid-frontend-" +
						"token-definition-json-file"),
				Response.Status.BAD_REQUEST);
		}
	}

	private FrontendTokenDefinition _getFrontendTokenDefinition(File file)
		throws IOException, JSONException, JSONValidatorException {

		String json = StringPool.BLANK;

		if (file.exists()) {
			json = new String(Files.readAllBytes(file.toPath()));
		}

		_frontendTokenDefinitionJSONValidator.validate(json);

		return new FrontendTokenDefinitionImpl(
			_jsonFactory.createJSONObject(json), _jsonFactory, null,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK);
	}

	private Response _getResponse(File file, Locale locale)
		throws IOException, JSONException, JSONValidatorException {

		if ((file == null) || !StringUtil.endsWith(file.getName(), ".json")) {
			return _getResponse(
				_language.get(locale, "please-upload-a-json-file"),
				Response.Status.BAD_REQUEST);
		}

		FrontendTokenDefinition frontendTokenDefinition =
			_getFrontendTokenDefinition(file);

		Collection<FrontendTokenCategory> frontendTokenCategories =
			frontendTokenDefinition.getFrontendTokenCategories();
		Collection<FrontendToken> frontendTokens =
			frontendTokenDefinition.getFrontendTokens();
		Collection<FrontendTokenSet> frontendTokenSets =
			frontendTokenDefinition.getFrontendTokenSets();

		return _getResponse(
			_language.format(
				locale,
				"the-frontend-token-definition-json-file-was-uploaded-and-" +
					"contributed-x-token-categories-x-token-sets-and-x-tokens",
				new Object[] {
					frontendTokenCategories.size(), frontendTokenSets.size(),
					frontendTokens.size()
				}),
			Response.Status.OK);
	}

	private Response _getResponse(String message, Response.Status status) {
		return Response.status(
			status
		).entity(
			HashMapBuilder.put(
				"message", message
			).build()
		).type(
			MediaType.APPLICATION_JSON
		).build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FrontendTokenDefinitionApplication.class);

	private final FrontendTokenDefinitionJSONValidator
		_frontendTokenDefinitionJSONValidator =
			new FrontendTokenDefinitionJSONValidator();

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}