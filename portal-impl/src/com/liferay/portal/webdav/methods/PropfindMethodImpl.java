/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.methods;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.webdav.InvalidRequestException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 */
public class PropfindMethodImpl extends BasePropMethodImpl implements Method {

	@Override
	public int process(WebDAVRequest webDAVRequest) throws WebDAVException {
		try {
			return writeResponseXML(webDAVRequest, getProps(webDAVRequest));
		}
		catch (InvalidRequestException invalidRequestException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(invalidRequestException);
			}

			return HttpServletResponse.SC_BAD_REQUEST;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	protected Set<QName> generateProps(Set<QName> props) {
		props.add(DISPLAYNAME);
		props.add(GETCONTENTLENGTH);
		props.add(GETCONTENTTYPE);
		props.add(GETLASTMODIFIED);
		props.add(LOCKDISCOVERY);
		props.add(RESOURCETYPE);

		// RFC 3253 Currently Unsupported

		//props.add(new Tuple("checked-in", WebDAVUtil.DAV_URI));
		//props.add(new Tuple("checked-out", WebDAVUtil.DAV_URI));
		//props.add(new Tuple("version-name", WebDAVUtil.DAV_URI));

		return props;
	}

	protected Set<QName> getProps(WebDAVRequest webDAVRequest)
		throws InvalidRequestException {

		try {
			Set<QName> props = new HashSet<>();

			HttpServletRequest httpServletRequest =
				webDAVRequest.getHttpServletRequest();

			String xml = new String(
				FileUtil.getBytes(httpServletRequest.getInputStream()));

			if (Validator.isNull(xml)) {

				// Windows XP does not generate an xml request so the PROPFIND
				// must be generated manually. See LEP-4920.

				return generateProps(props);
			}

			Document document = SAXReaderUtil.read(xml);

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Request XML: \n" +
						document.formattedString(StringPool.FOUR_SPACES));
			}

			Element rootElement = document.getRootElement();

			if (rootElement.element(ALLPROP.getName()) != null) {

				// Generate props if <allprop> tag is used. See LEP-6162.

				return generateProps(props);
			}

			Element propElement = rootElement.element("prop");

			List<Element> elements = propElement.elements();

			for (Element element : elements) {
				String prefix = element.getNamespacePrefix();
				String uri = element.getNamespaceURI();

				Namespace namespace = WebDAVUtil.createNamespace(prefix, uri);

				props.add(
					SAXReaderUtil.createQName(element.getName(), namespace));
			}

			return props;
		}
		catch (Exception exception) {
			throw new InvalidRequestException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PropfindMethodImpl.class);

}