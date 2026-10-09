/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.document.conversion.internal;

import com.liferay.petra.io.AutoDeleteFileInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.File;
import java.io.InputStream;

import java.util.Scanner;

/**
 * @author Daniel Sanz
 * @author István András Dézsi
 */
public class DocumentHTMLProcessor {

	public InputStream process(InputStream inputStream) {
		File tempFile = null;

		InputStream processedInputStream = null;

		String replacement = "";

		try (Scanner scanner = new Scanner(inputStream)) {
			scanner.useDelimiter(">");

			tempFile = FileUtil.createTempFile();

			String imageRequestToken = ImageRequestTokenUtil.createToken(
				PrincipalThreadLocal.getUserId());

			while (scanner.hasNext()) {
				String token = scanner.next();

				if (Validator.isNull(token)) {
					continue;
				}

				token += ">";

				replacement = token.replaceAll(
					_DOCUMENTS_REGEX,
					"$1&auth_token=" + imageRequestToken + "$3");

				replacement = replacement.replaceAll(
					_IMAGE_REGEX, "$1&auth_token=" + imageRequestToken + "$3");

				replacement = replacement.replaceAll(
					_PORTLET_FILE_ENTRY_REGEX,
					"$1?auth_token=" + imageRequestToken + "$3");

				replacement = replacement.replaceAll(
					_WIKI_PAGE_ATTACHMENT_REGEX,
					"$1$3&auth_token=" + imageRequestToken + "$6");

				FileUtil.write(tempFile, replacement, true, true);
			}

			processedInputStream = new AutoDeleteFileInputStream(tempFile);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return processedInputStream;
	}

	private static final String _DOCUMENTS_REGEX =
		"(<img .*?src=\"\\/(documents\\/\\d+)\\/[^\\s]+)(\"[^&]+)";

	private static final String _IMAGE_REGEX =
		"(<img .*?src=\"\\/(image)\\/[^\\s]+)(\"[^&]+)";

	private static final String _PORTLET_FILE_ENTRY_REGEX =
		"(<img .*?src=\"\\/(documents\\/portlet_file_entry)\\/[^&]+)(\"[^&]+)";

	private static final String _WIKI_PAGE_ATTACHMENT_REGEX =
		"(<img ([^=]*(?<!src)=\\\"[^\\\"]+\\\")*[^=]*)((?<= src)=\\\"[^\\/]*(" +
			"\\/(?!c/wiki/get_page_attachment)[^\\/]*)*(\\/(?=" +
				"c/wiki/get_page_attachment))[^&]+)([^>]+>)";

	private static final Log _log = LogFactoryUtil.getLog(
		DocumentHTMLProcessor.class);

}