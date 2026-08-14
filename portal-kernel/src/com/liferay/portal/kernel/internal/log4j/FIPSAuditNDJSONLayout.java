/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;

import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.layout.Encoder;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ObjectMessage;

/**
 * @author Rafael Praxedes
 * @see    LiferayXmlLayout
 */
@Plugin(
	category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE,
	name = FIPSAuditNDJSONLayout.PLUGIN_NAME, printObject = true
)
public final class FIPSAuditNDJSONLayout extends AbstractStringLayout {

	public static final String PLUGIN_NAME = "FIPSAuditNDJSONLayout";

	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public void encode(
		LogEvent logEvent, ByteBufferDestination byteBufferDestination) {

		Encoder<StringBuilder> encoder = getStringBuilderEncoder();

		encoder.encode(_toNDJSON(logEvent), byteBufferDestination);
	}

	@Override
	public String getContentType() {
		return "application/x-ndjson; charset=UTF-8";
	}

	@Override
	public String toSerializable(LogEvent logEvent) {
		StringBuilder sb = _toNDJSON(logEvent);

		return sb.toString();
	}

	public static class Builder
		implements org.apache.logging.log4j.core.util.Builder
			<FIPSAuditNDJSONLayout> {

		@Override
		public FIPSAuditNDJSONLayout build() {
			return new FIPSAuditNDJSONLayout();
		}

	}

	private FIPSAuditNDJSONLayout() {
		super(StandardCharsets.UTF_8);
	}

	private StringBuilder _toNDJSON(LogEvent logEvent) {
		Message message = logEvent.getMessage();

		Object parameter = null;

		if (message instanceof ObjectMessage) {
			ObjectMessage objectMessage = (ObjectMessage)message;

			parameter = objectMessage.getParameter();
		}

		if (!(parameter instanceof Map)) {
			throw new IllegalStateException(
				StringBundler.concat(
					"Unable to write the log event from the logger \"",
					logEvent.getLoggerName(),
					"\" to the FIPS audit log because its message carries no ",
					"FIPS audit event"));
		}

		StringBuilder sb = getStringBuilder();

		sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)parameter));

		sb.append(CharPool.NEW_LINE);

		return sb;
	}

}