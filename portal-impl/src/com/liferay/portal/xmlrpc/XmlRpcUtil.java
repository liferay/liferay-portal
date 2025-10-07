/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.xmlrpc;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.ReleaseInfo;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.xml.StAXReaderUtil;
import com.liferay.portal.kernel.xmlrpc.Response;
import com.liferay.portal.kernel.xmlrpc.XmlRpcException;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * @author Brian Wing Shun Chan
 */
public class XmlRpcUtil {

	public static String buildMethod(String methodName, Object[] arguments)
		throws XmlRpcException {

		StringBundler sb = new StringBundler((arguments.length * 3) + 5);

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodCall>");
		sb.append("<methodName>");
		sb.append(methodName);
		sb.append("</methodName><params>");

		for (Object argument : arguments) {
			sb.append("<param>");
			sb.append(wrapValue(argument));
			sb.append("</param>");
		}

		sb.append("</params></methodCall>");

		return sb.toString();
	}

	public static Fault createFault(int code, String description) {
		return new Fault(code, description);
	}

	public static Success createSuccess(String description) {
		return new Success(description);
	}

	public static Response executeMethod(
			String url, String methodName, Object[] arguments)
		throws XmlRpcException {

		try {
			if (_log.isDebugEnabled()) {
				StringBundler sb = new StringBundler();

				sb.append("XML-RPC invoking ");
				sb.append(methodName);
				sb.append(" ");

				if (arguments != null) {
					for (int i = 0; i < arguments.length; i++) {
						sb.append(arguments[i]);

						if (i < (arguments.length - 1)) {
							sb.append(", ");
						}
					}
				}

				_log.debug(sb.toString());
			}

			String requestXML = buildMethod(methodName, arguments);

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

			options.setBody(requestXML, ContentTypes.TEXT_XML, StringPool.UTF8);
			options.setLocation(url);
			options.setPost(true);

			return parseResponse(HttpUtil.URLtoString(options));
		}
		catch (Exception exception) {
			throw new XmlRpcException(exception);
		}
	}

	public static Tuple parseMethod(String xml) throws IOException {
		XMLStreamReader xmlStreamReader = null;

		try {
			int paramCount = 0;

			XMLInputFactory xmlInputFactory =
				StAXReaderUtil.getXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			xmlStreamReader.nextTag();
			xmlStreamReader.nextTag();
			xmlStreamReader.next();

			String methodName = xmlStreamReader.getText();

			List<Object> arguments = new ArrayList<>();

			xmlStreamReader.nextTag();

			String name = xmlStreamReader.getLocalName();

			while (!name.equals("methodCall")) {
				xmlStreamReader.nextTag();

				name = xmlStreamReader.getLocalName();

				if (!name.equals("param")) {
					continue;
				}

				paramCount++;

				if ((PropsValues.XML_RPC_MAX_PARAMETERS != -1) &&
					(paramCount > PropsValues.XML_RPC_MAX_PARAMETERS)) {

					throw new IOException("Too many XML-RPC parameters");
				}

				xmlStreamReader.nextTag();

				name = xmlStreamReader.getLocalName();

				int event = xmlStreamReader.next();

				if (event == XMLStreamConstants.START_ELEMENT) {
					name = xmlStreamReader.getLocalName();

					xmlStreamReader.next();

					String text = xmlStreamReader.getText();

					if (name.equals("string")) {
						arguments.add(text);
					}
					else if (name.equals("int") || name.equals("i4")) {
						arguments.add(GetterUtil.getInteger(text));
					}
					else if (name.equals("double")) {
						arguments.add(GetterUtil.getDouble(text));
					}
					else if (name.equals("boolean")) {
						arguments.add(GetterUtil.getBoolean(text));
					}
					else {
						throw new IOException(
							"XML-RPC not implemented for " + name);
					}

					xmlStreamReader.nextTag();
					xmlStreamReader.nextTag();
					xmlStreamReader.nextTag();
				}
				else {
					arguments.add(xmlStreamReader.getText());

					xmlStreamReader.nextTag();
					xmlStreamReader.nextTag();
				}

				name = xmlStreamReader.getLocalName();
			}

			return new Tuple(methodName, arguments.toArray());
		}
		catch (Exception exception) {
			throw new IOException(exception);
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
	}

	public static Response parseResponse(String xml) throws XmlRpcException {
		XMLStreamReader xmlStreamReader = null;

		try {
			XMLInputFactory xmlInputFactory =
				StAXReaderUtil.getXMLInputFactory();

			xmlStreamReader = xmlInputFactory.createXMLStreamReader(
				new UnsyncStringReader(xml));

			xmlStreamReader.nextTag();
			xmlStreamReader.nextTag();

			String name = xmlStreamReader.getLocalName();

			if (name.equals("params")) {
				String description = null;

				xmlStreamReader.nextTag();
				xmlStreamReader.nextTag();

				int event = xmlStreamReader.next();

				if (event == XMLStreamConstants.START_ELEMENT) {
					xmlStreamReader.next();

					description = xmlStreamReader.getText();
				}
				else {
					description = xmlStreamReader.getText();
				}

				return createSuccess(description);
			}
			else if (name.equals("fault")) {
				int code = 0;
				String description = null;

				xmlStreamReader.nextTag();
				xmlStreamReader.nextTag();

				for (int i = 0; i < 2; i++) {
					xmlStreamReader.nextTag();
					xmlStreamReader.nextTag();

					xmlStreamReader.next();

					String valueName = xmlStreamReader.getText();

					if (valueName.equals("faultCode")) {
						xmlStreamReader.nextTag();
						xmlStreamReader.nextTag();
						xmlStreamReader.nextTag();

						name = xmlStreamReader.getLocalName();

						if (name.equals("int") || name.equals("i4")) {
							xmlStreamReader.next();

							code = GetterUtil.getInteger(
								xmlStreamReader.getText());
						}

						xmlStreamReader.nextTag();
						xmlStreamReader.nextTag();
						xmlStreamReader.nextTag();
					}
					else if (valueName.equals("faultString")) {
						xmlStreamReader.nextTag();
						xmlStreamReader.nextTag();

						int event = xmlStreamReader.next();

						if (event == XMLStreamConstants.START_ELEMENT) {
							xmlStreamReader.next();

							description = xmlStreamReader.getText();

							xmlStreamReader.nextTag();
						}
						else {
							description = xmlStreamReader.getText();
						}

						xmlStreamReader.nextTag();
						xmlStreamReader.nextTag();
					}
				}

				return createFault(code, description);
			}

			return null;
		}
		catch (Exception exception) {
			throw new XmlRpcException(xml, exception);
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
	}

	public static String wrapValue(Object value) throws XmlRpcException {
		if (value == null) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler(5);

		sb.append("<value>");

		if (value instanceof String) {
			sb.append("<string>");
			sb.append(value.toString());
			sb.append("</string>");
		}
		else if (value instanceof Integer || value instanceof Short) {
			sb.append("<i4>");
			sb.append(value.toString());
			sb.append("</i4>");
		}
		else if (value instanceof Double || value instanceof Float) {
			sb.append("<double>");
			sb.append(value.toString());
			sb.append("</double>");
		}
		else if (value instanceof Boolean) {
			sb.append("<boolean>");

			if ((Boolean)value) {
				sb.append(CharPool.NUMBER_1);
			}
			else {
				sb.append(CharPool.NUMBER_0);
			}

			sb.append("</boolean>");
		}
		else {
			throw new XmlRpcException("Unsupported type " + value.getClass());
		}

		sb.append("</value>");

		return sb.toString();
	}

	private static final boolean _HTTP_HEADER_VERSION_VERBOSITY_DEFAULT =
		StringUtil.equalsIgnoreCase(
			PropsUtil.get(PropsKeys.HTTP_HEADER_VERSION_VERBOSITY), "off");

	private static final boolean _HTTP_HEADER_VERSION_VERBOSITY_PARTIAL =
		StringUtil.equalsIgnoreCase(
			PropsUtil.get(PropsKeys.HTTP_HEADER_VERSION_VERBOSITY), "partial");

	private static final Log _log = LogFactoryUtil.getLog(XmlRpcUtil.class);

}