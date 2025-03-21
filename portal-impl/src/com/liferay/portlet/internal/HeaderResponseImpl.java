/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.portlet.PortletDependency;
import com.liferay.portal.kernel.model.portlet.PortletDependencyFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayHeaderResponse;
import com.liferay.portal.kernel.servlet.taglib.util.OutputData;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.xml.StAXReaderUtil;

import jakarta.portlet.HeaderRequest;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Neil Griffin
 */
public class HeaderResponseImpl
	extends MimeResponseImpl implements LiferayHeaderResponse {

	@Override
	public void addDependency(String name, String scope, String version) {
		addDependency(name, scope, version, null);
	}

	@Override
	public void addDependency(
		String name, String scope, String version, String xml) {

		if (Validator.isNull(name)) {
			throw new IllegalArgumentException();
		}

		if (Objects.equals(name, "PortletHub") &&
			Objects.equals(scope, "jakarta.portlet")) {

			return;
		}

		if (xml != null) {
			xml = StringUtil.trim(xml);

			if (xml.isEmpty()) {
				return;
			}

			xml = _addClosingTags(xml);

			if (_validateParsedElements(xml) == 0) {
				return;
			}
		}

		PortletDependency portletDependency =
			PortletDependencyFactoryUtil.createPortletDependency(
				name, scope, version, xml, portletRequestImpl);

		if (PortletDependency.Type.OTHER == portletDependency.getType()) {
			if (xml != null) {
				throw new IllegalArgumentException(
					"Invalid dependency markup: " + xml);
			}

			_log.error("Unable to add dependency: " + portletDependency);

			return;
		}

		Portlet portlet = getPortlet();

		if (((PortletDependency.Type.CSS == portletDependency.getType()) &&
			 portlet.isPortletDependencyCssEnabled()) ||
			((PortletDependency.Type.JAVASCRIPT ==
				portletDependency.getType()) &&
			 portlet.isPortletDependencyJavaScriptEnabled()) ||
			(PortletDependency.Type.OTHER == portletDependency.getType())) {

			_addDependencyToHead(name, scope, version, null, portletDependency);
		}
	}

	@Override
	public void flushBuffer() {
		if (_printWriter != null) {
			_printWriter.flush();
		}

		_calledFlushBuffer = true;
	}

	@Override
	public int getBufferSize() {
		return _bufferSize;
	}

	@Override
	public String getLifecycle() {
		return PortletRequest.HEADER_PHASE;
	}

	@Override
	public OutputStream getPortletOutputStream() {
		if (_calledGetWriter) {
			throw new IllegalStateException(
				"Unable to obtain OutputStream because Writer is already in " +
					"use");
		}

		if (_portletOutputStream == null) {
			_portletOutputStream = new HeaderOutputStream();
		}

		_calledGetPortletOutputStream = true;

		return _portletOutputStream;
	}

	@Override
	public PrintWriter getWriter() {
		if (_calledGetPortletOutputStream) {
			throw new IllegalStateException(
				"Unable to obtain Writer because OutputStream is already in " +
					"use");
		}

		if (_printWriter == null) {
			_printWriter = new HeaderPrintWriter(new UnsyncStringWriter());
		}

		_calledGetWriter = true;

		return _printWriter;
	}

	public void init(
		PortletRequestImpl portletRequestImpl,
		HttpServletResponse httpServletResponse,
		List<PortletDependency> portletDependencies) {

		super.init(portletRequestImpl, httpServletResponse);

		if (portletDependencies != null) {
			for (PortletDependency portletDependency : portletDependencies) {
				addDependency(
					portletDependency.getName(), portletDependency.getScope(),
					portletDependency.getVersion());
			}
		}
	}

	@Override
	public boolean isCalledFlushBuffer() {
		return _calledFlushBuffer;
	}

	@Override
	public boolean isCalledGetPortletOutputStream() {
		return _calledGetPortletOutputStream;
	}

	@Override
	public boolean isCalledGetWriter() {
		return _calledGetWriter;
	}

	@Override
	public boolean isCommitted() {
		if (_calledFlushBuffer || super.isCommitted()) {
			return true;
		}

		return false;
	}

	@Override
	public void reset() {
		if (_calledFlushBuffer) {
			throw new IllegalStateException(
				"Unable to reset a buffer that has been flushed");
		}

		_portletOutputStream = null;
		_printWriter = null;
	}

	@Override
	public void resetBuffer() {
		reset();
	}

	@Override
	public void setBufferSize(int bufferSize) {
		if (_calledFlushBuffer) {
			throw new IllegalStateException(
				"Unable to set buffer size because buffer has been flushed");
		}

		_bufferSize = bufferSize;
	}

	@Override
	public void setContentType(String contentType) {
		if ((_printWriter != null) || (_portletOutputStream != null)) {
			return;
		}

		super.setContentType(contentType);
	}

	@Override
	public void setTitle(String title) {

		// See LEP-2188

		ThemeDisplay themeDisplay =
			(ThemeDisplay)portletRequestImpl.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		portletDisplay.setTitle(title);
	}

	@Override
	public void writeToHead() {
		if (_portletOutputStream != null) {
			_addXMLToHead(
				portletName, getNamespace(), _portletOutputStream.toString());
		}
		else if (_printWriter != null) {
			_addXMLToHead(portletName, getNamespace(), _printWriter.toString());
		}
	}

	private static String _addClosingTags(String xml) {
		xml = _addClosingTags(xml, "<link", "</link>");
		xml = _addClosingTags(xml, "<LINK", "</LINK>");
		xml = _addClosingTags(xml, "<meta", "</meta>");
		xml = _addClosingTags(xml, "<META", "</META>");

		return xml;
	}

	private static String _addClosingTags(
		String xml, String opening, String closing) {

		StringBundler sb = null;

		int fromIndex = 0;
		int index = 0;
		int sbIndex = 0;

		while ((index = xml.indexOf(opening, fromIndex)) != -1) {
			int openingEnd = xml.indexOf(CharPool.GREATER_THAN, index);

			if (openingEnd == -1) {
				break;
			}

			fromIndex = openingEnd + 1;

			if (xml.regionMatches(fromIndex, closing, 0, closing.length())) {
				fromIndex += closing.length();
			}
			else {
				if (sb == null) {
					sb = new StringBundler();
				}

				if (xml.charAt(openingEnd - 1) == CharPool.FORWARD_SLASH) {
					sb.append(xml.substring(sbIndex, fromIndex - 2));
					sb.append(StringPool.GREATER_THAN);
				}
				else {
					sb.append(xml.substring(sbIndex, fromIndex));
				}

				sbIndex = fromIndex;

				sb.append(closing);
			}
		}

		if (sb == null) {
			return xml;
		}

		sb.append(xml.substring(sbIndex));

		return sb.toString();
	}

	private void _addDependencyToHead(
		String name, String scope, String version, ParsedElement parsedElement,
		PortletDependency portletDependency) {

		if (Validator.isNull(scope)) {
			scope = StringPool.BLANK;
		}

		SemVer semVer = SemVer._DEFAULT;
		String versionKey = version;

		if (Validator.isNull(version)) {
			versionKey = StringUtil.randomId();
		}
		else {
			semVer = new SemVer(version);
		}

		HeaderRequest headerRequest = (HeaderRequest)getPortletRequest();

		HttpServletRequest httpServletRequest =
			PortalUtil.getHttpServletRequest(headerRequest);

		OutputData outputData = (OutputData)httpServletRequest.getAttribute(
			WebKeys.OUTPUT_DATA);

		if (outputData == null) {
			outputData = new OutputData();

			httpServletRequest.setAttribute(WebKeys.OUTPUT_DATA, outputData);
		}

		Set<String> outputKeys = outputData.getOutputKeys();

		Iterator<String> iterator = outputKeys.iterator();

		while (iterator.hasNext()) {
			String existingKey = iterator.next();

			String[] existingKeyParts = StringUtil.split(
				existingKey, CharPool.COLON);

			if (existingKeyParts.length == 3) {
				String existingName = existingKeyParts[0];
				String existingScope = existingKeyParts[1];

				if (name.equals(existingName) && scope.equals(existingScope)) {
					String existingVersion = existingKeyParts[2];

					SemVer existingSemVer = new SemVer(existingVersion);

					if (existingSemVer.compareTo(semVer) < 0) {
						iterator.remove();
						outputData.setDataSB(
							existingKey, WebKeys.PAGE_TOP, null);
					}

					break;
				}
			}
		}

		String outputKey = StringBundler.concat(
			name, StringPool.COLON, scope, StringPool.COLON, versionKey);

		if (outputData.addOutputKey(outputKey)) {
			if (parsedElement != null) {
				outputData.addDataSB(
					outputKey, WebKeys.PAGE_TOP,
					parsedElement.toStringBundler());
			}
			else if (portletDependency != null) {
				outputData.addDataSB(
					outputKey, WebKeys.PAGE_TOP,
					portletDependency.toStringBundler());
			}
		}
	}

	private void _addXMLToHead(String name, String scope, String xml) {
		xml = StringUtil.trim(xml);

		if (Validator.isBlank(xml)) {
			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Markup before conversion to well-formed XML:" + xml);
		}

		if (!xml.startsWith("<head>")) {
			xml = "<head>".concat(xml);
		}

		if (!xml.endsWith("</head>")) {
			xml = xml.concat("</head>");
		}

		xml = _addClosingTags(xml);

		if (_log.isDebugEnabled()) {
			_log.debug("Markup after conversion to well-formed XML:" + xml);
		}

		for (ParsedElement parsedElement : _parseElements(xml)) {
			_addDependencyToHead(name, scope, null, parsedElement, null);
		}
	}

	private List<ParsedElement> _parseElements(String xml) {
		List<ParsedElement> parsedElements = new ArrayList<>();

		XMLStreamReader xmlStreamReader = null;

		try {
			XMLInputFactory xmlInputFactory =
				StAXReaderUtil.getXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			Map<String, String> elementAttributeValues = null;
			String elementName = null;
			StringBundler elementTextSB = null;
			boolean parsingScriptTemplate = false;

			while (xmlStreamReader.hasNext()) {
				int event = xmlStreamReader.next();

				if (elementTextSB == null) {
					elementTextSB = new StringBundler();
				}

				if (event == XMLStreamConstants.CHARACTERS) {
					String text = xmlStreamReader.getText();

					if (text != null) {
						text = StringUtil.trim(text);

						if (!text.isEmpty()) {
							elementTextSB.append(text);
						}
					}
				}
				else if (event == XMLStreamConstants.COMMENT) {
					if (elementName != null) {
						elementTextSB.append("\n<!--");
						elementTextSB.append(xmlStreamReader.getText());
						elementTextSB.append("-->");
					}
				}
				else if (event == XMLStreamConstants.END_ELEMENT) {
					if (elementName != null) {
						parsedElements.add(
							new ParsedElement(
								elementName, elementAttributeValues,
								parsingScriptTemplate, elementTextSB.toString(),
								true));

						if (parsingScriptTemplate &&
							elementName.equals("script")) {

							parsingScriptTemplate = false;
						}

						elementAttributeValues = null;
						elementName = null;
						elementTextSB.setIndex(0);
					}
				}
				else if (event == XMLStreamConstants.START_ELEMENT) {
					String localName = xmlStreamReader.getLocalName();

					if (!localName.equals("head")) {
						if (parsingScriptTemplate || localName.equals("link") ||
							localName.equals("meta") ||
							localName.equals("script") ||
							localName.equals("style")) {

							elementName = localName;

							elementAttributeValues = new LinkedHashMap<>();

							int attributeCount =
								xmlStreamReader.getAttributeCount();

							for (int i = 0; i < attributeCount; i++) {
								String name =
									xmlStreamReader.getAttributeLocalName(i);
								String value =
									xmlStreamReader.getAttributeValue(i);

								if (localName.equals("script") &&
									Objects.equals(name, "type") &&
									(Objects.equals(value, "data/template") ||
									 Objects.equals(value, "text/template"))) {

									parsingScriptTemplate = true;
								}

								elementAttributeValues.put(name, value);
							}
						}
						else {
							throw new XMLStreamException(
								"Invalid element: " + localName);
						}
					}
				}
			}
		}
		catch (XMLStreamException xmlStreamException) {
			_log.error(xmlStreamException);

			parsedElements.add(
				new ParsedElement(null, null, false, null, false));
		}
		finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				}
				catch (XMLStreamException xmlStreamException) {
					_log.error(xmlStreamException);
				}
			}
		}

		return parsedElements;
	}

	private int _validateParsedElements(String xml) {
		List<ParsedElement> parsedElements = _parseElements(xml);

		int totalParsedElements = parsedElements.size();

		if (totalParsedElements == 0) {
			return totalParsedElements;
		}

		ParsedElement firstParsedElement = parsedElements.get(0);

		if (!firstParsedElement.isScriptTemplate() &&
			(totalParsedElements > 1)) {

			throw new IllegalArgumentException(
				"More than one element in markup: " + xml);
		}

		for (ParsedElement parsedElement : parsedElements) {
			if (!parsedElement.isValid()) {
				throw new IllegalArgumentException(
					"Invalid dependency: " + xml);
			}
		}

		return totalParsedElements;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		HeaderResponseImpl.class);

	private int _bufferSize;
	private boolean _calledFlushBuffer;
	private boolean _calledGetPortletOutputStream;
	private boolean _calledGetWriter;
	private OutputStream _portletOutputStream;
	private PrintWriter _printWriter;

	private static class ParsedElement {

		public boolean isScriptTemplate() {
			return _scriptTemplate;
		}

		public boolean isValid() {
			return _valid;
		}

		public StringBundler toStringBundler() {
			StringBundler sb = new StringBundler((_attributes.size() * 5) + 7);

			sb.append("\n<");
			sb.append(_name);

			for (Map.Entry<String, String> entry : _attributes.entrySet()) {
				sb.append(StringPool.SPACE);
				sb.append(entry.getKey());
				sb.append("=\"");
				sb.append(entry.getValue());
				sb.append(StringPool.QUOTE);
			}

			sb.append(StringPool.GREATER_THAN);

			if (!Objects.equals(_name, "link") &&
				!Objects.equals(_name, "meta")) {

				if (_text != null) {
					sb.append(_text);
				}

				sb.append("\n</");
				sb.append(_name);
				sb.append(StringPool.GREATER_THAN);
			}

			return sb;
		}

		private ParsedElement(
			String name, Map<String, String> attributes, boolean scriptTemplate,
			String text, boolean valid) {

			_name = name;

			if (attributes == null) {
				_attributes = Collections.emptyMap();
			}
			else {
				_attributes = attributes;
			}

			_scriptTemplate = scriptTemplate;
			_text = text;
			_valid = valid;
		}

		private final Map<String, String> _attributes;
		private final String _name;
		private final boolean _scriptTemplate;
		private final String _text;
		private final boolean _valid;

	}

	private static class SemVer implements Comparable<SemVer> {

		@Override
		public int compareTo(SemVer semVer) {
			int result = Integer.compare(_major, semVer._major);

			if (result != 0) {
				return result;
			}

			result = Integer.compare(_minor, semVer._minor);

			if (result != 0) {
				return result;
			}

			return Integer.compare(_micro, semVer._micro);
		}

		private SemVer(int major, int minor, int micro) {
			_major = major;
			_minor = minor;
			_micro = micro;
		}

		private SemVer(String version) {
			String[] parts = StringUtil.split(version, CharPool.PERIOD);

			if (parts.length > 0) {
				_major = GetterUtil.getInteger(parts[0]);
			}
			else {
				_major = 0;
			}

			if (parts.length > 1) {
				_minor = GetterUtil.getInteger(parts[1]);
			}
			else {
				_minor = 0;
			}

			if (parts.length > 2) {
				_micro = GetterUtil.getInteger(parts[2]);
			}
			else {
				_micro = 0;
			}
		}

		private static final SemVer _DEFAULT = new SemVer(0, 0, 0);

		private final int _major;
		private final int _micro;
		private final int _minor;

	}

	private class HeaderOutputStream extends UnsyncByteArrayOutputStream {

		@Override
		public String toString() {
			try {
				return toString(getCharacterEncoding());
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				_log.error(unsupportedEncodingException);
			}

			return StringPool.BLANK;
		}

	}

	private class HeaderPrintWriter extends UnsyncPrintWriter {

		@Override
		public String toString() {
			return _unsyncStringWriter.toString();
		}

		private HeaderPrintWriter(UnsyncStringWriter unsyncStringWriter) {
			super(unsyncStringWriter);

			_unsyncStringWriter = unsyncStringWriter;
		}

		private final UnsyncStringWriter _unsyncStringWriter;

	}

}