/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.methods;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.service.WebDAVPropsLocalServiceUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webdav.Resource;
import com.liferay.portal.kernel.webdav.WebDAVException;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.webdav.InvalidRequestException;
import com.liferay.portal.webdav.LockException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alexander Chow
 */
public class ProppatchMethodImpl extends BasePropMethodImpl {

	@Override
	public int process(WebDAVRequest webDAVRequest) throws WebDAVException {
		try {
			Set<QName> props = processInstructions(webDAVRequest);

			return writeResponseXML(webDAVRequest, props);
		}
		catch (InvalidRequestException invalidRequestException) {
			if (_log.isInfoEnabled()) {
				_log.info(invalidRequestException);
			}

			return HttpServletResponse.SC_BAD_REQUEST;
		}
		catch (LockException lockException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(lockException);
			}

			return WebDAVUtil.SC_LOCKED;
		}
		catch (Exception exception) {
			throw new WebDAVException(exception);
		}
	}

	protected WebDAVProps getStoredProperties(WebDAVRequest webDAVRequest)
		throws PortalException {

		WebDAVStorage storage = webDAVRequest.getWebDAVStorage();

		Resource resource = storage.getResource(webDAVRequest);

		if (resource.getPrimaryKey() <= 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("There is no primary key set for resource");
			}

			throw new InvalidRequestException();
		}
		else if (resource.isLocked()) {
			Lock lock = resource.getLock();

			if (lock == null) {
				throw new LockException();
			}

			String uuid = lock.getUuid();

			if (!uuid.equals(webDAVRequest.getLockUuid())) {
				throw new LockException();
			}
		}

		return WebDAVPropsLocalServiceUtil.getWebDAVProps(
			webDAVRequest.getCompanyId(), resource.getClassName(),
			resource.getPrimaryKey());
	}

	protected Set<QName> processInstructions(WebDAVRequest webDAVRequest)
		throws InvalidRequestException, LockException {

		try {
			Set<QName> newProps = new HashSet<>();

			HttpServletRequest httpServletRequest =
				webDAVRequest.getHttpServletRequest();

			String xml = new String(
				FileUtil.getBytes(httpServletRequest.getInputStream()));

			if (Validator.isNull(xml)) {
				return newProps;
			}

			WebDAVProps webDAVProps = getStoredProperties(webDAVRequest);

			Document document = SAXReaderUtil.read(xml);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Request XML: \n" +
						document.formattedString(StringPool.FOUR_SPACES));
			}

			Element rootElement = document.getRootElement();

			List<Element> instructionElements = rootElement.elements();

			for (Element instructionElement : instructionElements) {
				List<Element> propElements = instructionElement.elements();

				if (propElements.size() != 1) {
					throw new InvalidRequestException(
						"There should only be one <prop /> per set or remove " +
							"instruction");
				}

				Element propElement = propElements.get(0);

				String propElementName = propElement.getName();
				String propElementNamespaceURI = propElement.getNamespaceURI();

				if (!propElementName.equals("prop") ||
					!propElementNamespaceURI.equals(
						WebDAVUtil.DAV_URI.getURI())) {

					throw new InvalidRequestException(
						"Invalid <prop /> element " + propElement);
				}

				List<Element> customPropElements = propElement.elements();

				for (Element customPropElement : customPropElements) {
					String prefix = customPropElement.getNamespacePrefix();
					String uri = customPropElement.getNamespaceURI();

					Namespace namespace = WebDAVUtil.createNamespace(
						prefix, uri);

					String name = customPropElement.getName();

					String instructionElementName =
						instructionElement.getName();

					if (instructionElementName.equals("set")) {
						String text = customPropElement.getText();

						if (Validator.isNull(text)) {
							webDAVProps.addProp(name, prefix, uri);
						}
						else {
							webDAVProps.addProp(name, prefix, uri, text);
						}

						newProps.add(
							SAXReaderUtil.createQName(
								customPropElement.getName(), namespace));
					}
					else if (instructionElementName.equals("remove")) {
						webDAVProps.removeProp(name, prefix, uri);
					}
					else {
						throw new InvalidRequestException(
							"Instead of set/remove instruction, received " +
								instructionElement);
					}
				}
			}

			WebDAVPropsLocalServiceUtil.storeWebDAVProps(webDAVProps);

			return newProps;
		}
		catch (LockException lockException) {
			throw lockException;
		}
		catch (Exception exception) {
			throw new InvalidRequestException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProppatchMethodImpl.class);

}