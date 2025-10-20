/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.ToXContent;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;

/**
 * @author Bryan Engler
 */
public class DebugStringsUtil {

	public static String getPrettyPrintedJSONString(ToXContent toXContent) {
		try {
			XContentBuilder xContentBuilder = XContentFactory.jsonBuilder();

			xContentBuilder.prettyPrint();

			toXContent.toXContent(xContentBuilder, ToXContent.EMPTY_PARAMS);

			return Strings.toString(xContentBuilder);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(ioException);
			}

			return ioException.getMessage();
		}
	}

	public static String getSearchRequestString(
		SearchSourceBuilder searchSourceBuilder) {

		try {
			return searchSourceBuilder.toString();
		}
		catch (ElasticsearchException elasticsearchException) {
			if (_log.isDebugEnabled()) {
				_log.debug(elasticsearchException);
			}

			return elasticsearchException.getMessage();
		}
	}

	public static String getStackTraceString() {
		StringBundler sb = new StringBundler(100);

		Thread thread = Thread.currentThread();

		for (StackTraceElement stackTraceElement : thread.getStackTrace()) {
			sb.append(stackTraceElement.toString());
			sb.append(" @@ ");

			if (sb.index() == 100) {
				break;
			}
		}

		sb.setIndex(sb.index() - 1);

		sb.setStringAt(StringPool.BLANK, 0);
		sb.setStringAt(StringPool.BLANK, 1);
		sb.setStringAt(StringPool.BLANK, 2);
		sb.setStringAt(StringPool.BLANK, 3);

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DebugStringsUtil.class);

}