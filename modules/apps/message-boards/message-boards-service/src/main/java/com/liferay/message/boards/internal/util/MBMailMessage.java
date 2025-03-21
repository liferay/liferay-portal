/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.internal.util;

import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParser;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.Validator;

import jakarta.mail.internet.MimeUtility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorge Ferrer
 */
public class MBMailMessage {

	public void addBytes(String fileName, byte[] bytes) {
		try {
			fileName = MimeUtility.decodeText(fileName);
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to decode file name " + fileName,
					unsupportedEncodingException);
			}
		}

		_bytesOVPs.add(new ObjectValuePair<String, byte[]>(fileName, bytes));
	}

	public String getBody(HtmlParser htmlParser) {
		String body = null;

		if (MBMessageConstants.DEFAULT_FORMAT.equals("bbcode")) {
			if (Validator.isNotNull(_plainBody)) {
				body = GetterUtil.getString(_plainBody);
			}
			else if (Validator.isNotNull(_htmlBody)) {
				body = htmlParser.extractText(_htmlBody);
			}
		}
		else if (MBMessageConstants.DEFAULT_FORMAT.equals("html")) {
			if (Validator.isNotNull(_htmlBody)) {
				body = GetterUtil.getString(_htmlBody);
			}
			else if (Validator.isNotNull(_plainBody)) {
				body = GetterUtil.getString(_plainBody);
			}
		}

		if (Validator.isNull(body)) {
			body = "-";
		}

		return body;
	}

	public String getHtmlBody() {
		return _htmlBody;
	}

	public List<ObjectValuePair<String, InputStream>> getInputStreamOVPs() {
		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			new ArrayList<>(_bytesOVPs.size());

		for (ObjectValuePair<String, byte[]> bytesOVP : _bytesOVPs) {
			String key = bytesOVP.getKey();

			byte[] bytes = bytesOVP.getValue();

			ByteArrayInputStream byteArrayInputStream =
				new ByteArrayInputStream(bytes);

			ObjectValuePair<String, InputStream> inputStreamOVP =
				new ObjectValuePair<>(key, byteArrayInputStream);

			inputStreamOVPs.add(inputStreamOVP);
		}

		return inputStreamOVPs;
	}

	public String getPlainBody() {
		return _plainBody;
	}

	public void setHtmlBody(String htmlBody) {
		_htmlBody = htmlBody;
	}

	public void setPlainBody(String plainBody) {
		_plainBody = plainBody;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBMailMessage.class.getName());

	private final List<ObjectValuePair<String, byte[]>> _bytesOVPs =
		new ArrayList<>();
	private String _htmlBody;
	private String _plainBody;

}