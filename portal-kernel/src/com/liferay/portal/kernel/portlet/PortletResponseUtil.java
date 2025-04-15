/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StreamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletResponseUtil {

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, byte[] bytes)
		throws IOException {

		sendFile(portletRequest, mimeResponse, fileName, bytes, null);
	}

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, byte[] bytes, String contentType)
		throws IOException {

		sendFile(
			portletRequest, mimeResponse, fileName, bytes, contentType, null);
	}

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, byte[] bytes, String contentType,
			String contentDispositionType)
		throws IOException {

		setHeaders(
			portletRequest, mimeResponse, null, contentDispositionType,
			contentType, fileName);

		write(mimeResponse, bytes);
	}

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, byte[] bytes, String contentType,
			String contentDispositionType, String cacheControlValue)
		throws IOException {

		setHeaders(
			portletRequest, mimeResponse, cacheControlValue,
			contentDispositionType, contentType, fileName);

		write(mimeResponse, bytes);
	}

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, InputStream inputStream)
		throws IOException {

		sendFile(portletRequest, mimeResponse, fileName, inputStream, null);
	}

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, InputStream inputStream, int contentLength,
			String contentType)
		throws IOException {

		sendFile(
			portletRequest, mimeResponse, fileName, inputStream, contentLength,
			contentType, null);
	}

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, InputStream inputStream, int contentLength,
			String contentType, String contentDispositionType)
		throws IOException {

		setHeaders(
			portletRequest, mimeResponse, null, contentDispositionType,
			contentType, fileName);

		write(mimeResponse, inputStream, contentLength);
	}

	public static void sendFile(
			PortletRequest portletRequest, MimeResponse mimeResponse,
			String fileName, InputStream inputStream, String contentType)
		throws IOException {

		sendFile(
			portletRequest, mimeResponse, fileName, inputStream, 0,
			contentType);
	}

	public static void write(MimeResponse mimeResponse, byte[] bytes)
		throws IOException {

		write(mimeResponse, bytes, 0, 0);
	}

	public static void write(
			MimeResponse mimeResponse, byte[] bytes, int offset,
			int contentLength)
		throws IOException {

		// LEP-3122

		if (!mimeResponse.isCommitted()) {

			// LEP-536

			if (contentLength == 0) {
				contentLength = bytes.length;
			}

			if (mimeResponse instanceof ResourceResponse) {
				ResourceResponse resourceResponse =
					(ResourceResponse)mimeResponse;

				resourceResponse.setContentLength(contentLength);
			}

			OutputStream outputStream = mimeResponse.getPortletOutputStream();

			outputStream.write(bytes, offset, contentLength);
		}
	}

	public static void write(MimeResponse mimeResponse, byte[][] bytesArray)
		throws IOException {

		// LEP-3122

		if (mimeResponse.isCommitted()) {
			return;
		}

		// LEP-536

		long contentLength = 0;

		for (byte[] bytes : bytesArray) {
			contentLength += bytes.length;
		}

		if (mimeResponse instanceof ResourceResponse) {
			ResourceResponse resourceResponse = (ResourceResponse)mimeResponse;

			setContentLength(resourceResponse, contentLength);
		}

		OutputStream outputStream = mimeResponse.getPortletOutputStream();

		for (byte[] bytes : bytesArray) {
			outputStream.write(bytes);
		}
	}

	public static void write(MimeResponse mimeResponse, File file)
		throws IOException {

		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			FileChannel fileChannel = fileInputStream.getChannel();

			long contentLength = fileChannel.size();

			if (mimeResponse instanceof ResourceResponse) {
				ResourceResponse resourceResponse =
					(ResourceResponse)mimeResponse;

				setContentLength(resourceResponse, contentLength);
			}

			fileChannel.transferTo(
				0, contentLength,
				Channels.newChannel(mimeResponse.getPortletOutputStream()));
		}
	}

	public static void write(MimeResponse mimeResponse, InputStream inputStream)
		throws IOException {

		write(mimeResponse, inputStream, 0);
	}

	public static void write(
			MimeResponse mimeResponse, InputStream inputStream,
			int contentLength)
		throws IOException {

		if (mimeResponse.isCommitted()) {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException ioException) {
					if (_log.isWarnEnabled()) {
						_log.warn(ioException);
					}
				}
			}

			return;
		}

		if ((contentLength > 0) && (mimeResponse instanceof ResourceResponse)) {
			ResourceResponse resourceResponse = (ResourceResponse)mimeResponse;

			resourceResponse.setContentLength(contentLength);
		}

		StreamUtil.transfer(inputStream, mimeResponse.getPortletOutputStream());
	}

	public static void write(MimeResponse mimeResponse, String s)
		throws IOException {

		write(mimeResponse, s.getBytes(StringPool.UTF8));
	}

	protected static void setContentLength(
		ResourceResponse response, long contentLength) {

		response.setProperty(
			HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
	}

	protected static void setHeaders(
		PortletRequest portletRequest, MimeResponse mimeResponse,
		String cacheControlValue, String contentDispositionType,
		String contentType, String fileName) {

		if (_log.isDebugEnabled()) {
			_log.debug("Sending file of type " + contentType);
		}

		// LEP-2201

		if (Validator.isNotNull(contentType)) {
			mimeResponse.setContentType(contentType);
		}

		if (Validator.isNull(cacheControlValue)) {
			mimeResponse.setProperty(
				HttpHeaders.CACHE_CONTROL,
				HttpHeaders.CACHE_CONTROL_PRIVATE_VALUE);
		}
		else {
			mimeResponse.setProperty(
				HttpHeaders.CACHE_CONTROL, cacheControlValue);
		}

		if (Validator.isNull(fileName)) {
			return;
		}

		String contentDispositionFileName = "filename=\"" + fileName + "\"";

		// If necessary for non-ASCII characters, encode based on RFC 2184.
		// However, not all browsers support RFC 2184. See LEP-3127.

		boolean ascii = true;

		for (int i = 0; i < fileName.length(); i++) {
			if (!Validator.isAscii(fileName.charAt(i))) {
				ascii = false;

				break;
			}
		}

		try {
			if (!ascii) {
				String encodedFileName = URLCodec.encodeURL(fileName, true);

				contentDispositionFileName =
					"filename*=UTF-8''" + encodedFileName;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}
		}

		if (Validator.isNull(contentDispositionType)) {
			String extension = GetterUtil.getString(
				FileUtil.getExtension(fileName));

			extension = StringUtil.toLowerCase(extension);

			String[] mimeTypesContentDispositionInline = null;

			try {
				mimeTypesContentDispositionInline = PropsUtil.getArray(
					"mime.types.content.disposition.inline");
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}

				mimeTypesContentDispositionInline = new String[0];
			}

			if (ArrayUtil.contains(
					mimeTypesContentDispositionInline, extension)) {

				contentDispositionType = HttpHeaders.CONTENT_DISPOSITION_INLINE;
			}
			else {
				contentDispositionType =
					HttpHeaders.CONTENT_DISPOSITION_ATTACHMENT;
			}
		}

		mimeResponse.setProperty(
			HttpHeaders.CONTENT_DISPOSITION,
			StringBundler.concat(
				contentDispositionType, StringPool.SEMICOLON, StringPool.SPACE,
				contentDispositionFileName));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletResponseUtil.class);

}