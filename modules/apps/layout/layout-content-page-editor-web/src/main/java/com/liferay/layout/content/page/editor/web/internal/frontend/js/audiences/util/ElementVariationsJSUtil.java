/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.page.editor.web.internal.frontend.js.audiences.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.InputStream;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class ElementVariationsJSUtil {

	public static String getContent(
		String elementVariationsJS, List<String> sortedAudienceEntryERCs) {

		return StringUtil.replace(
			_TPL_ELEMENT_VARIATIONS_JS,
			new String[] {
				"[$ELEMENT_VARIATIONS$]", "[$SORTED_AUDIENCE_ENTRY_ERCS$]"
			},
			new String[] {
				elementVariationsJS,
				JSONUtil.putAll(
					sortedAudienceEntryERCs.toArray()
				).toString()
			});
	}

	private static String _read(String name) {
		try (InputStream inputStream =
				ElementVariationsJSUtil.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private static final String _TPL_ELEMENT_VARIATIONS_JS;

	private static final Log _log = LogFactoryUtil.getLog(
		ElementVariationsJSUtil.class);

	static {
		_TPL_ELEMENT_VARIATIONS_JS = _read("element_variations.js.tpl");
	}

}