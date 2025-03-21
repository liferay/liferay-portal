/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.web.internal.servlet;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.exception.AMException;
import com.liferay.adaptive.media.handler.AMRequestHandler;
import com.liferay.adaptive.media.web.internal.constants.AMWebConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.util.FileEntryHttpHeaderCustomizerUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"osgi.http.whiteboard.servlet.name=com.liferay.adaptive.media.web.internal.servlet.AMServlet",
		"osgi.http.whiteboard.servlet.pattern=/" + AMWebConstants.SERVLET_PATH + "/*",
		"servlet.init.httpMethods=GET,HEAD"
	},
	service = Servlet.class
)
public class AMServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			AMRequestHandler<?> amRequestHandler =
				_amRequestHandlerLocator.locateForPattern(
					_getRequestHandlerPattern(httpServletRequest));

			if (amRequestHandler == null) {
				httpServletResponse.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					httpServletRequest.getRequestURI());

				return;
			}

			AdaptiveMedia<?> adaptiveMedia =
				(AdaptiveMedia<?>)amRequestHandler.handleRequest(
					httpServletRequest);

			if (adaptiveMedia == null) {
				throw new AMException.AMNotFound();
			}

			boolean download = ParamUtil.getBoolean(
				httpServletRequest, "download");

			long fileEntryId = _getFileEntryId(
				String.valueOf(adaptiveMedia.getURI()));

			if (fileEntryId > 0) {
				String cacheControlValue =
					HttpHeaders.CACHE_CONTROL_PRIVATE_VALUE;

				if (download) {
					cacheControlValue =
						HttpHeaders.CACHE_CONTROL_NO_CACHE_VALUE;
				}

				httpServletResponse.addHeader(
					HttpHeaders.CACHE_CONTROL,
					FileEntryHttpHeaderCustomizerUtil.getHttpHeaderValue(
						_dlAppLocalService.getFileEntry(fileEntryId),
						HttpHeaders.CACHE_CONTROL, cacheControlValue));
			}

			Long contentLength = adaptiveMedia.getValue(
				AMAttribute.getContentLengthAMAttribute());

			if (contentLength == null) {
				contentLength = 0L;
			}

			String contentType = adaptiveMedia.getValue(
				AMAttribute.getContentTypeAMAttribute());

			if (contentType == null) {
				contentType = ContentTypes.APPLICATION_OCTET_STREAM;
			}

			String fileName = adaptiveMedia.getValue(
				AMAttribute.getFileNameAMAttribute());

			if (download) {
				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, fileName,
					adaptiveMedia.getInputStream(), contentLength, contentType,
					HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT);
			}
			else {
				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, fileName,
					adaptiveMedia.getInputStream(), contentLength, contentType);
			}
		}
		catch (AMException.AMNotFound amException) {
			if (_log.isDebugEnabled()) {
				_log.debug(amException, amException);
			}

			httpServletResponse.sendError(
				HttpServletResponse.SC_NOT_FOUND,
				httpServletRequest.getRequestURI());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			Throwable throwable = exception.getCause();

			if (throwable instanceof PrincipalException) {
				httpServletResponse.sendError(
					HttpServletResponse.SC_FORBIDDEN,
					httpServletRequest.getRequestURI());
			}
			else {
				httpServletResponse.sendError(
					HttpServletResponse.SC_BAD_REQUEST,
					httpServletRequest.getRequestURI());
			}
		}
	}

	@Override
	protected void doHead(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		doGet(httpServletRequest, httpServletResponse);
	}

	private long _getFileEntryId(String uri) {
		Matcher matcher = _fileEntryIdPattern.matcher(uri);

		if (matcher.find()) {
			return Long.valueOf(matcher.group(2));
		}

		return 0;
	}

	private String _getRequestHandlerPattern(
		HttpServletRequest httpServletRequest) {

		Matcher matcher = _requestHandlerPattern.matcher(
			httpServletRequest.getPathInfo());

		if (matcher.find()) {
			return matcher.group(1);
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(AMServlet.class);

	private static final Pattern _fileEntryIdPattern = Pattern.compile(
		"(\\/image\\/)(\\d+)\\/");
	private static final Pattern _requestHandlerPattern = Pattern.compile(
		"^/([^/]*)");

	@Reference
	private AMRequestHandlerLocator _amRequestHandlerLocator;

	@Reference
	private DLAppLocalService _dlAppLocalService;

}