/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.opener.onedrive.web.internal.oauth;

import com.liferay.document.library.opener.oauth.OAuth2State;
import com.liferay.document.library.opener.onedrive.web.internal.constants.DLOpenerOneDriveWebKeys;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PwdGenerator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 */
@Component(service = OAuth2ControllerFactory.class)
public class OAuth2ControllerFactory {

	public OAuth2Controller getJSONOAuth2Controller(
		Function<PortletRequest, String> function) {

		return new JSONOAuth2Controller(function);
	}

	public OAuth2Controller getRedirectingOAuth2Controller() {
		return new RedirectingOAuth2Controller();
	}

	private String _getFailureURL(PortletRequest portletRequest)
		throws PortalException {

		LiferayPortletURL liferayPortletURL = _portletURLFactory.create(
			portletRequest, _portal.getPortletId(portletRequest),
			_portal.getControlPanelPlid(portletRequest),
			PortletRequest.RENDER_PHASE);

		return liferayPortletURL.toString();
	}

	private OAuth2Result _getOAuth2Result(
		PortletRequest portletRequest,
		UnsafeFunction<PortletRequest, JSONObject, PortalException>
			unsafeFunction,
		Function<PortletRequest, String> function) {

		try {
			long companyId = _portal.getCompanyId(portletRequest);
			long userId = _portal.getUserId(portletRequest);

			if (_oAuth2Manager.hasAccessToken(companyId, userId)) {
				return new OAuth2Result(unsafeFunction.apply(portletRequest));
			}

			String state = PwdGenerator.getPassword(5);

			OAuth2StateUtil.save(
				_portal.getOriginalServletRequest(
					_portal.getHttpServletRequest(portletRequest)),
				new OAuth2State(
					userId, function.apply(portletRequest),
					_getFailureURL(portletRequest), state));

			return new OAuth2Result(
				_oAuth2Manager.getAuthorizationURL(
					companyId, _portal.getPortalURL(portletRequest), state));
		}
		catch (PortalException portalException) {
			return new OAuth2Result(portalException);
		}
	}

	private String _getRenderURL(PortletRequest portletRequest) {
		return PortletURLBuilder.create(
			_portletURLFactory.create(
				portletRequest, _portal.getPortletId(portletRequest),
				PortletRequest.RENDER_PHASE)
		).setParameter(
			"folderId", ParamUtil.getString(portletRequest, "folderId")
		).setParameter(
			"repositoryId", ParamUtil.getString(portletRequest, "repositoryId")
		).buildString();
	}

	private String _translate(Locale locale, String key) {
		return _language.get(locale, key);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2ControllerFactory.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private OAuth2Manager _oAuth2Manager;

	@Reference
	private Portal _portal;

	@Reference
	private PortletURLFactory _portletURLFactory;

	private class JSONOAuth2Controller implements OAuth2Controller {

		public JSONOAuth2Controller(Function<PortletRequest, String> function) {
			_function = function;
		}

		@Override
		public void execute(
				PortletRequest portletRequest, PortletResponse portletResponse,
				UnsafeFunction<PortletRequest, JSONObject, PortalException>
					unsafeFunction)
			throws PortalException {

			try {
				OAuth2Result oAuth2Result = _getOAuth2Result(
					portletRequest, unsafeFunction, _function);

				PortalException portalException =
					oAuth2Result.getPortalException();

				if (Objects.nonNull(portalException)) {
					_log.error(portalException);

					JSONPortletResponseUtil.writeJSON(
						portletRequest, portletResponse,
						JSONUtil.put(
							"error",
							_translate(
								_portal.getLocale(portletRequest),
								"your-request-failed-to-complete")));
				}
				else {
					JSONPortletResponseUtil.writeJSON(
						portletRequest, portletResponse,
						oAuth2Result.getResponseJSONObject());
				}
			}
			catch (IOException ioException) {
				throw new PortalException(ioException);
			}
		}

		private final Function<PortletRequest, String> _function;

	}

	private class OAuth2Result {

		public OAuth2Result(JSONObject responseJSONObject) {
			_responseJSONObject = responseJSONObject;

			_portalException = null;
			_redirectURL = null;
		}

		public OAuth2Result(PortalException portalException) {
			_portalException = portalException;

			_responseJSONObject = null;
			_redirectURL = null;
		}

		public OAuth2Result(String redirectURL) {
			_redirectURL = redirectURL;

			_portalException = null;
			_responseJSONObject = null;
		}

		public PortalException getPortalException() {
			return _portalException;
		}

		public String getRedirectURL() {
			return _redirectURL;
		}

		public JSONObject getResponseJSONObject() {
			if (_redirectURL != null) {
				return JSONUtil.put("redirectURL", _redirectURL);
			}

			if (_responseJSONObject == null) {
				return _jsonFactory.createJSONObject();
			}

			return _responseJSONObject;
		}

		private final PortalException _portalException;
		private final String _redirectURL;
		private final JSONObject _responseJSONObject;

	}

	private class RedirectingOAuth2Controller implements OAuth2Controller {

		public RedirectingOAuth2Controller() {
			_function = portletRequest -> _portal.getCurrentURL(
				_portal.getHttpServletRequest(portletRequest));
		}

		@Override
		public void execute(
				PortletRequest portletRequest, PortletResponse portletResponse,
				UnsafeFunction<PortletRequest, JSONObject, PortalException>
					unsafeFunction)
			throws PortalException {

			OAuth2Result oAuth2Result = _getOAuth2Result(
				portletRequest, unsafeFunction, _function);

			PortalException portalException = oAuth2Result.getPortalException();

			if (Objects.nonNull(portalException)) {
				_log.error(portalException);

				throw portalException;
			}

			String url = oAuth2Result.getRedirectURL();

			if (url == null) {
				JSONObject jsonObject = oAuth2Result.getResponseJSONObject();

				if (jsonObject.length() > 0) {
					HttpServletRequest httpServletRequest =
						_portal.getOriginalServletRequest(
							_portal.getHttpServletRequest(portletRequest));

					HttpSession httpSession = httpServletRequest.getSession();

					httpSession.setAttribute(
						DLOpenerOneDriveWebKeys.
							DL_OPENER_ONE_DRIVE_REDIRECTING_OAUTH2_JSON_OBJECT,
						oAuth2Result.getResponseJSONObject());
				}

				url = _getRenderURL(portletRequest);
			}

			portletRequest.setAttribute(WebKeys.REDIRECT, url);
		}

		private final Function<PortletRequest, String> _function;

	}

}