/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.linkback;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.StAXReaderUtil;
import com.liferay.portal.kernel.xmlrpc.Response;
import com.liferay.portal.kernel.xmlrpc.XmlRpcException;
import com.liferay.portal.xmlrpc.XmlRpcUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Alexander Chow
 */
public class LinkbackProducerUtil {

	public static void sendPingback(String sourceUri, String targetUri)
		throws Exception {

		_pingbackQueue.add(new Tuple(new Date(), sourceUri, targetUri));
	}

	public static synchronized void sendQueuedPingbacks()
		throws XmlRpcException {

		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.MINUTE, -1);

		Date expiration = cal.getTime();

		while (!_pingbackQueue.isEmpty()) {
			Tuple tuple = _pingbackQueue.get(0);

			Date time = (Date)tuple.getObject(0);

			if (time.before(expiration)) {
				_pingbackQueue.remove(0);

				String targetUri = (String)tuple.getObject(2);

				String serverUri = _discoverPingbackServer(targetUri);

				if (Validator.isNull(serverUri)) {
					continue;
				}

				String sourceUri = (String)tuple.getObject(1);

				if (_log.isInfoEnabled()) {
					_log.info(
						StringBundler.concat(
							"XML-RPC pingback ", serverUri, ", source ",
							sourceUri, ", target ", targetUri));
				}

				Response response = XmlRpcUtil.executeMethod(
					serverUri, "pingback.ping",
					new Object[] {sourceUri, targetUri});

				if (_log.isInfoEnabled()) {
					_log.info(response.toString());
				}
			}
			else {
				break;
			}
		}
	}

	public static boolean sendTrackback(
			String trackback, Map<String, String> parts)
		throws Exception {

		if (_log.isInfoEnabled()) {
			_log.info("Pinging trackback " + trackback);
		}

		Http.Options options = new Http.Options();

		if (_HTTP_HEADER_VERSION_VERBOSITY_DEFAULT) {
		}
		else if (_HTTP_HEADER_VERSION_VERBOSITY_PARTIAL) {
			options.addHeader(HttpHeaders.USER_AGENT, ReleaseInfo.getName());
		}
		else {
			options.addHeader(
				HttpHeaders.USER_AGENT, ReleaseInfo.getServerInfo());
		}

		options.setLocation(trackback);
		options.setParts(parts);
		options.setPost(true);

		String xml = HttpUtil.URLtoString(options);

		if (_log.isDebugEnabled()) {
			_log.debug(xml);
		}

		String error = xml;

		XMLStreamReader xmlStreamReader = null;

		try {
			XMLInputFactory xmlInputFactory =
				StAXReaderUtil.getXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			xmlStreamReader.nextTag();
			xmlStreamReader.nextTag();

			String name = xmlStreamReader.getLocalName();

			if (name.equals("error")) {
				int status = GetterUtil.getInteger(
					xmlStreamReader.getElementText(), 1);

				if (status == 0) {
					if (_log.isInfoEnabled()) {
						_log.info("Trackback accepted");
					}

					return true;
				}

				xmlStreamReader.nextTag();

				name = xmlStreamReader.getLocalName();

				if (name.equals("message")) {
					error = xmlStreamReader.getElementText();
				}
			}
		}
		finally {
			if (xmlStreamReader != null) {
				try {
					xmlStreamReader.close();
				}
				catch (Exception exception) {
					if (_log.isDebugEnabled()) {
						_log.debug(exception);
					}
				}
			}
		}

		_log.error(
			StringBundler.concat(
				"Error while pinging trackback at ", trackback, ": ", error));

		return false;
	}

	private static String _discoverPingbackServer(String targetUri) {
		String serverUri = null;

		try {
			Http.Options options = new Http.Options();

			if (_HTTP_HEADER_VERSION_VERBOSITY_DEFAULT) {
			}
			else if (_HTTP_HEADER_VERSION_VERBOSITY_PARTIAL) {
				options.addHeader(
					HttpHeaders.USER_AGENT, ReleaseInfo.getName());
			}
			else {
				options.addHeader(
					HttpHeaders.USER_AGENT, ReleaseInfo.getServerInfo());
			}

			options.setLocation(targetUri);
			options.setHead(true);

			HttpUtil.URLtoByteArray(options);

			Http.Response response = options.getResponse();

			serverUri = response.getHeader("X-Pingback");
		}
		catch (Exception exception) {
			_log.error("Unable to call HEAD of " + targetUri, exception);
		}

		if (Validator.isNotNull(serverUri)) {
			return serverUri;
		}

		try {
			serverUri = HtmlParserUtil.findAttributeValue(
				getAttributeValueFunction -> StringUtil.equalsIgnoreCase(
					getAttributeValueFunction.apply("rel"), "pingback"),
				getAttributeValueFunction -> HtmlUtil.escape(
					getAttributeValueFunction.apply("href")),
				HttpUtil.URLtoString(targetUri), "link");
		}
		catch (Exception exception) {
			_log.error("Unable to call GET of " + targetUri, exception);
		}

		return serverUri;
	}

	private static final boolean _HTTP_HEADER_VERSION_VERBOSITY_DEFAULT =
		StringUtil.equalsIgnoreCase(
			PropsValues.HTTP_HEADER_VERSION_VERBOSITY, "off");

	private static final boolean _HTTP_HEADER_VERSION_VERBOSITY_PARTIAL =
		StringUtil.equalsIgnoreCase(
			PropsValues.HTTP_HEADER_VERSION_VERBOSITY, "partial");

	private static final Log _log = LogFactoryUtil.getLog(
		LinkbackProducerUtil.class);

	private static final List<Tuple> _pingbackQueue =
		Collections.synchronizedList(new ArrayList<Tuple>());

}