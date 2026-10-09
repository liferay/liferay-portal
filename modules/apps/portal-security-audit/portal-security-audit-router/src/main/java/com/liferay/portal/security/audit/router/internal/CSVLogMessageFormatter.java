/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.security.audit.formatter.LogMessageFormatter;
import com.liferay.portal.security.audit.router.configuration.CSVLogMessageFormatterConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Mika Koivisto
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.router.configuration.CSVLogMessageFormatterConfiguration",
	property = "format=CSV", service = LogMessageFormatter.class
)
public class CSVLogMessageFormatter implements LogMessageFormatter {

	@Override
	public String format(AuditMessage auditMessage) {
		StringBundler sb = new StringBundler((_columns.length * 4) - 1);

		JSONObject jsonObject = auditMessage.toJSONObject();

		for (int i = 0; i < _columns.length; i++) {
			sb.append(StringPool.QUOTE);
			sb.append(jsonObject.getString(_columns[i]));
			sb.append(StringPool.QUOTE);

			if ((i + 1) >= _columns.length) {
				continue;
			}

			sb.append(StringPool.COMMA);
		}

		return sb.toString();
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		CSVLogMessageFormatterConfiguration
			csvLogMessageFormatterConfiguration =
				ConfigurableUtil.createConfigurable(
					CSVLogMessageFormatterConfiguration.class, properties);

		_columns = csvLogMessageFormatterConfiguration.columns();
	}

	private volatile String[] _columns;

}