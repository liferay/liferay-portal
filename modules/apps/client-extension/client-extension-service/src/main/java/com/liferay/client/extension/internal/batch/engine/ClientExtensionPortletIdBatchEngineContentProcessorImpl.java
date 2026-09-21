/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.internal.batch.engine;

import com.liferay.batch.engine.BatchEngineContentProcessor;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dániel Szimkó
 */
@Component(
	service = BatchEngineContentProcessor.class
)
public class ClientExtensionPortletIdBatchEngineContentProcessorImpl
	implements BatchEngineContentProcessor {

	@Override
	public String process(String content) {
		if (!ExportImportThreadLocal.isImportInProcess() || (content == null) ||
			!content.contains(_PORTLET_ID_PREFIX)) {

			return content;
		}

		long companyId1 = CompanyThreadLocal.getCompanyId();

		if (companyId1 <= 0) {
			return content;
		}

		Matcher matcher = _pattern.matcher(content);

		StringBuilder sb = new StringBuilder(content.length());

		while (matcher.find()) {
			String replacement = matcher.group();

			long companyId2 = GetterUtil.getLong(matcher.group(1));

			if ((companyId2 > 0) && (companyId2 != companyId1)) {
				replacement = StringBundler.concat(
					_PORTLET_ID_PREFIX, companyId1, StringPool.UNDERLINE);
			}

			matcher.appendReplacement(
				sb, Matcher.quoteReplacement(replacement));
		}

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final String _PORTLET_ID_PREFIX =
		"com_liferay_client_extension_web_internal_portlet_" +
			"ClientExtensionEntryPortlet_";

	private static final Pattern _pattern = Pattern.compile(
		_PORTLET_ID_PREFIX + "([0-9]+)_");

}