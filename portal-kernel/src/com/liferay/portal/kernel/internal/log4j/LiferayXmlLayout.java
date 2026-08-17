/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.internal.log4j;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.xml.XMLUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.nio.charset.StandardCharsets;

import java.util.List;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.layout.Encoder;
import org.apache.logging.log4j.core.util.Transform;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.Strings;

/**
 * @author Hai Yu
 * @see org.apache.log4j.layout.Log4j1XmlLayout
 */
@Plugin(
	category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE,
	name = LiferayXmlLayout.PLUGIN_NAME, printObject = true
)
public final class LiferayXmlLayout extends AbstractStringLayout {

	public static final String PLUGIN_NAME = "LiferayXmlLayout";

	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}

	@Override
	public void encode(
		LogEvent logEvent, ByteBufferDestination byteBufferDestination) {

		StringBuilder sb = getStringBuilder();

		_generateXMLLog(logEvent, sb);

		Encoder<StringBuilder> encoder = getStringBuilderEncoder();

		encoder.encode(sb, byteBufferDestination);
	}

	@Override
	public String toSerializable(LogEvent logEvent) {
		StringBuilder sb = getStringBuilder();

		_generateXMLLog(logEvent, sb);

		return sb.toString();
	}

	public static class Builder
		implements org.apache.logging.log4j.core.util.Builder
			<LiferayXmlLayout> {

		@Override
		public LiferayXmlLayout build() {
			return new LiferayXmlLayout(_locationInfo, _properties);
		}

		@PluginBuilderAttribute("locationInfo")
		private boolean _locationInfo;

		@PluginBuilderAttribute("properties")
		private boolean _properties;

	}

	private LiferayXmlLayout(boolean locationInfo, boolean properties) {
		super(StandardCharsets.UTF_8);

		_locationInfo = locationInfo;
		_properties = properties;
	}

	private void _generateXMLLog(LogEvent logEvent, StringBuilder sb) {
		sb.append("<log4j:event logger=\"");
		sb.append(HtmlUtil.escapeAttribute(logEvent.getLoggerName()));
		sb.append("\" timestamp=\"");
		sb.append(logEvent.getTimeMillis());
		sb.append("\" level=\"");
		sb.append(
			HtmlUtil.escapeAttribute(String.valueOf(logEvent.getLevel())));
		sb.append("\" thread=\"");
		sb.append(HtmlUtil.escapeAttribute(logEvent.getThreadName()));
		sb.append("\">");
		sb.append(StringPool.RETURN_NEW_LINE);
		sb.append("<log4j:message>");
		sb.append(StringPool.CDATA_OPEN);

		Message message = logEvent.getMessage();

		Transform.appendEscapingCData(
			sb, XMLUtil.stripInvalidChars(message.getFormattedMessage()));

		sb.append(StringPool.CDATA_CLOSE);
		sb.append("</log4j:message>");
		sb.append(StringPool.RETURN_NEW_LINE);

		ThreadContext.ContextStack contextStack = logEvent.getContextStack();

		List<String> ndc = contextStack.asList();

		if (!ndc.isEmpty()) {
			sb.append("<log4j:NDC>");
			sb.append(StringPool.CDATA_OPEN);

			Transform.appendEscapingCData(
				sb,
				XMLUtil.stripInvalidChars(Strings.join(ndc, CharPool.SPACE)));

			sb.append(StringPool.CDATA_CLOSE);
			sb.append("</log4j:NDC>");
			sb.append(StringPool.RETURN_NEW_LINE);
		}

		Throwable throwable = logEvent.getThrown();

		if (throwable != null) {
			sb.append("<log4j:throwable>");
			sb.append(StringPool.CDATA_OPEN);

			StringWriter stringWriter = new StringWriter();

			throwable.printStackTrace(new PrintWriter(stringWriter));

			Transform.appendEscapingCData(
				sb, XMLUtil.stripInvalidChars(stringWriter.toString()));

			sb.append(StringPool.CDATA_CLOSE);
			sb.append("</log4j:throwable>");
			sb.append(StringPool.RETURN_NEW_LINE);
		}

		if (_locationInfo) {
			StackTraceElement stackTraceElement = logEvent.getSource();

			if (stackTraceElement != null) {
				sb.append("<log4j:locationInfo class=\"");
				sb.append(
					HtmlUtil.escapeAttribute(stackTraceElement.getClassName()));
				sb.append("\" method=\"");
				sb.append(
					HtmlUtil.escapeAttribute(
						stackTraceElement.getMethodName()));
				sb.append("\" file=\"");
				sb.append(
					HtmlUtil.escapeAttribute(stackTraceElement.getFileName()));
				sb.append("\" line=\"");
				sb.append(stackTraceElement.getLineNumber());
				sb.append("\"/>");
				sb.append(StringPool.RETURN_NEW_LINE);
			}
		}

		if (_properties) {
			ReadOnlyStringMap contextMap = logEvent.getContextData();

			if (!contextMap.isEmpty()) {
				sb.append("<log4j:properties>");
				sb.append(StringPool.RETURN_NEW_LINE);

				contextMap.forEach(
					(key, value) -> {
						if (value != null) {
							sb.append("<log4j:data name=\"");
							sb.append(HtmlUtil.escapeAttribute(key));
							sb.append("\" value=\"");
							sb.append(
								HtmlUtil.escapeAttribute(
									String.valueOf(value)));
							sb.append("\"/>");
							sb.append(StringPool.RETURN_NEW_LINE);
						}
					});

				sb.append("</log4j:properties>");
				sb.append(StringPool.RETURN_NEW_LINE);
			}
		}

		sb.append("</log4j:event>");
		sb.append(StringPool.RETURN_NEW_LINE);
		sb.append(StringPool.RETURN_NEW_LINE);
	}

	private final boolean _locationInfo;
	private final boolean _properties;

}