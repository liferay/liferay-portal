/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharepoint.soap.repository.connector.operation;

import com.liferay.petra.io.unsync.UnsyncStringReader;
import com.liferay.sharepoint.soap.repository.connector.SharepointException;
import com.liferay.sharepoint.soap.repository.connector.operation.constants.SharepointConstants;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Iván Zaera
 */
public class QueryServiceStubResult {

	public QueryServiceStubResult(String queryServiceSoapResultString)
		throws SharepointException {

		try {
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();

			xmlReader.setContentHandler(_defaultHandler);
			xmlReader.setErrorHandler(_defaultHandler);

			_parse(xmlReader, queryServiceSoapResultString);
		}
		catch (IOException | SAXException exception) {
			throw new SharepointException(
				"Unable to parse response from the Sharepoint server",
				exception);
		}
	}

	public String getDebugErrorMessage() {
		return _debugErrorMessage;
	}

	public List<String> getLinkURLs() {
		return _linkURLs;
	}

	public String getStatus() {
		return _status;
	}

	public boolean isEmpty() {
		if (isSuccess() &&
			_status.equals(
				SharepointConstants.SYMBOLIC_STATUS_NO_RESULTS_FOUND)) {

			return true;
		}

		return false;
	}

	public boolean isSuccess() {
		if (!_status.equals(SharepointConstants.SYMBOLIC_STATUS_SUCCESS) &&
			!_status.equals(
				SharepointConstants.SYMBOLIC_STATUS_NO_RESULTS_FOUND)) {

			return true;
		}

		return false;
	}

	private void _parse(
			XMLReader xmlReader, String queryServiceSoapResultString)
		throws IOException, SAXException {

		xmlReader.parse(
			new InputSource(
				new UnsyncStringReader(queryServiceSoapResultString)));
	}

	private void _setDebugErrorMessage(String debugErrorMessage) {
		_debugErrorMessage = debugErrorMessage;
	}

	private void _setStatus(String status) {
		_status = status;
	}

	private String _debugErrorMessage;

	private final DefaultHandler _defaultHandler = new DefaultHandler() {

		@Override
		public void characters(char[] chars, int start, int length) {
			_sb.append(chars, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			if (localName.equals("DebugErrorMessage")) {
				_setDebugErrorMessage(_sb.toString());
			}
			else if (localName.equals("LinkUrl")) {
				_linkURLs.add(_sb.toString());
			}
			else if (localName.equals("Status")) {
				_setStatus(_sb.toString());
			}
		}

		@Override
		public void startElement(
			String uri, String localName, String qName, Attributes attributes) {

			_sb.setLength(0);
		}

		private final StringBuilder _sb = new StringBuilder();

	};

	private final List<String> _linkURLs = new ArrayList<>();
	private String _status;

}