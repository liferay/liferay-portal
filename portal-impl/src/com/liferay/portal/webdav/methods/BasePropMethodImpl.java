/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.webdav.methods;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.lock.Lock;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.WebDAVProps;
import com.liferay.portal.kernel.service.WebDAVPropsLocalServiceUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.webdav.Resource;
import com.liferay.portal.kernel.webdav.WebDAVRequest;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Alexander Chow
 */
public abstract class BasePropMethodImpl implements Method {

	public static final QName ALLPROP = createQName("allprop");

	public static final QName CREATIONDATE = createQName("creationdate");

	public static final QName DISPLAYNAME = createQName("displayname");

	public static final QName GETCONTENTLENGTH = createQName(
		"getcontentlength");

	public static final QName GETCONTENTTYPE = createQName("getcontenttype");

	public static final QName GETLASTMODIFIED = createQName("getlastmodified");

	public static final QName ISREADONLY = createQName("isreadonly");

	public static final QName LOCKDISCOVERY = createQName("lockdiscovery");

	public static final QName RESOURCETYPE = createQName("resourcetype");

	protected static QName createQName(String name) {
		return SAXReaderUtil.createQName(name, WebDAVUtil.DAV_URI);
	}

	protected void addResponse(
			WebDAVRequest webDAVRequest, Resource resource, Set<QName> props,
			Element multistatus)
		throws Exception {

		// Make a deep copy of the props

		props = new HashSet<>(props);

		// Start building multistatus response

		Element responseElement = multistatus.addElement(
			createQName("response"));

		Element hrefElement = responseElement.addElement(createQName("href"));

		hrefElement.addText(GetterUtil.getString(resource.getHREF()));

		// Build success and failure propstat elements

		Element successStatElement = responseElement.addElement(
			createQName("propstat"));

		Element successPropElement = successStatElement.addElement(
			createQName("prop"));

		Element failureStatElement = responseElement.addElement(
			createQName("propstat"));

		Element failurePropElement = failureStatElement.addElement(
			createQName("prop"));

		boolean hasSuccess = false;
		boolean hasFailure = false;

		// Check DAV properties

		if (props.contains(ALLPROP)) {
			props.remove(ALLPROP);

			if (resource.isCollection()) {
				props.addAll(_allCollectionProps);
			}
			else {
				props.addAll(_allSimpleProps);
			}
		}

		if (props.contains(CREATIONDATE)) {
			props.remove(CREATIONDATE);

			Element successCreationDateElement = successPropElement.addElement(
				CREATIONDATE);

			successCreationDateElement.addText(
				GetterUtil.getString(resource.getCreateDateString()));

			hasSuccess = true;
		}

		if (props.contains(DISPLAYNAME)) {
			props.remove(DISPLAYNAME);

			Element successDisplayNameElement = successPropElement.addElement(
				DISPLAYNAME);

			successDisplayNameElement.addText(
				GetterUtil.getString(resource.getDisplayName()));

			hasSuccess = true;
		}

		if (props.contains(GETLASTMODIFIED)) {
			props.remove(GETLASTMODIFIED);

			Element successGetLastModifiedElement =
				successPropElement.addElement(GETLASTMODIFIED);

			successGetLastModifiedElement.addText(
				GetterUtil.getString(resource.getModifiedDate()));

			hasSuccess = true;
		}

		if (props.contains(GETCONTENTTYPE)) {
			props.remove(GETCONTENTTYPE);

			Element successGetContentTypeElement =
				successPropElement.addElement(GETCONTENTTYPE);

			successGetContentTypeElement.addText(
				GetterUtil.getString(resource.getContentType()));

			hasSuccess = true;
		}

		if (props.contains(GETCONTENTLENGTH)) {
			props.remove(GETCONTENTLENGTH);

			if (!resource.isCollection()) {
				Element successGetContentLengthElement =
					successPropElement.addElement(GETCONTENTLENGTH);

				successGetContentLengthElement.addText(
					String.valueOf(resource.getSize()));

				hasSuccess = true;
			}
			else {
				failurePropElement.addElement(GETCONTENTLENGTH);

				hasFailure = true;
			}
		}

		if (props.contains(ISREADONLY)) {
			props.remove(ISREADONLY);

			Element successIsReadOnlyElement = successPropElement.addElement(
				ISREADONLY);

			Lock lock = resource.getLock();

			if ((lock == null) || resource.isLocked()) {
				successIsReadOnlyElement.addText(Boolean.FALSE.toString());
			}
			else {
				successIsReadOnlyElement.addText(Boolean.TRUE.toString());
			}

			hasSuccess = true;
		}

		if (props.contains(LOCKDISCOVERY)) {
			props.remove(LOCKDISCOVERY);

			Lock lock = resource.getLock();

			if (lock != null) {
				Element lockDiscoveryElement = successPropElement.addElement(
					LOCKDISCOVERY);

				Element activeLockElement = lockDiscoveryElement.addElement(
					createQName("activelock"));

				Element lockTypeElement = activeLockElement.addElement(
					createQName("locktype"));

				lockTypeElement.addElement(createQName("write"));

				Element lockScopeElement = activeLockElement.addElement(
					createQName("lockscope"));

				lockScopeElement.addElement(createQName("exclusive"));

				if (resource.isCollection()) {
					Element depthElement = activeLockElement.addElement(
						createQName("depth"));

					depthElement.addText("Infinity");
				}

				Element ownerElement = activeLockElement.addElement(
					createQName("owner"));

				ownerElement.addText(GetterUtil.getString(lock.getOwner()));

				long timeRemaining = 0;

				Date expirationDate = lock.getExpirationDate();

				if (expirationDate != null) {
					long now = System.currentTimeMillis();

					timeRemaining =
						(expirationDate.getTime() - now) / Time.SECOND;

					if (timeRemaining <= 0) {
						timeRemaining = 1;
					}
				}

				Element timeoutElement = activeLockElement.addElement(
					createQName("timeout"));

				if (timeRemaining > 0) {
					timeoutElement.addText("Second-" + timeRemaining);
				}
				else {
					timeoutElement.addText("Infinite");
				}

				if (webDAVRequest.getUserId() == lock.getUserId()) {
					Element lockTokenElement = activeLockElement.addElement(
						createQName("locktoken"));

					hrefElement = lockTokenElement.addElement(
						createQName("href"));

					hrefElement.addText(
						GetterUtil.getString(
							"opaquelocktoken:" + lock.getUuid()));
				}

				hasSuccess = true;
			}
			else {
				failurePropElement.addElement(LOCKDISCOVERY);

				hasFailure = true;
			}
		}

		if (props.contains(RESOURCETYPE)) {
			props.remove(RESOURCETYPE);

			Element resourceTypeElement = successPropElement.addElement(
				RESOURCETYPE);

			if (resource.isCollection()) {
				resourceTypeElement.addElement(createQName("collection"));
			}

			hasSuccess = true;
		}

		// Check remaining properties against custom properties

		WebDAVProps webDAVProps = WebDAVPropsLocalServiceUtil.getWebDAVProps(
			webDAVRequest.getCompanyId(), resource.getClassName(),
			resource.getPrimaryKey());

		Set<QName> customProps = webDAVProps.getPropsSet();

		for (QName qName : props) {
			if (customProps.contains(qName)) {
				Element qNameElement = successPropElement.addElement(qName);

				Namespace namespace = qName.getNamespace();

				qNameElement.addText(
					GetterUtil.getString(
						webDAVProps.getText(
							qName.getName(), namespace.getPrefix(),
							namespace.getURI())));

				hasSuccess = true;
			}
			else {
				failurePropElement.addElement(qName);

				hasFailure = true;
			}
		}

		// Clean up propstats

		if (hasSuccess) {
			Element successStatusElement = successStatElement.addElement(
				createQName("status"));

			successStatusElement.addText("HTTP/1.1 200 OK");
		}
		else {
			responseElement.remove(successStatElement);
		}

		if (!hasSuccess && hasFailure) {
			Element failureStatusElement = failureStatElement.addElement(
				createQName("status"));

			failureStatusElement.addText("HTTP/1.1 404 Not Found");
		}
		else {
			responseElement.remove(failureStatElement);
		}
	}

	protected void addResponse(
			WebDAVStorage storage, WebDAVRequest webDAVRequest,
			Resource resource, Set<QName> props, Element multistatusElement,
			long depth)
		throws Exception {

		addResponse(webDAVRequest, resource, props, multistatusElement);

		if (resource.isCollection() && (depth != 0)) {
			List<Resource> storageResources = storage.getResources(
				webDAVRequest);

			for (Resource storageResource : storageResources) {
				addResponse(
					webDAVRequest, storageResource, props, multistatusElement);
			}
		}
	}

	protected int writeResponseXML(
			WebDAVRequest webDAVRequest, Set<QName> props)
		throws Exception {

		WebDAVStorage storage = webDAVRequest.getWebDAVStorage();

		Document document = SAXReaderUtil.createDocument();

		Element multistatusElement = SAXReaderUtil.createElement(
			createQName("multistatus"));

		document.setRootElement(multistatusElement);

		Resource resource = storage.getResource(webDAVRequest);

		if (resource != null) {
			long depth = WebDAVUtil.getDepth(
				webDAVRequest.getHttpServletRequest());

			addResponse(
				storage, webDAVRequest, resource, props, multistatusElement,
				depth);

			String xml = document.formattedString(StringPool.FOUR_SPACES);

			if (_log.isDebugEnabled()) {
				_log.debug("Response XML\n" + xml);
			}

			// Set the status prior to writing the XML

			int status = WebDAVUtil.SC_MULTI_STATUS;

			HttpServletResponse httpServletResponse =
				webDAVRequest.getHttpServletResponse();

			httpServletResponse.setContentType(ContentTypes.TEXT_XML_UTF8);
			httpServletResponse.setStatus(status);

			try {
				ServletResponseUtil.write(httpServletResponse, xml);

				httpServletResponse.flushBuffer();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(exception);
				}
			}

			return status;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				"No resource found for " + storage.getRootPath() +
					webDAVRequest.getPath());
		}

		return HttpServletResponse.SC_NOT_FOUND;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BasePropMethodImpl.class);

	private static final List<QName> _allCollectionProps = Arrays.asList(
		CREATIONDATE, DISPLAYNAME, GETLASTMODIFIED, GETCONTENTTYPE,
		LOCKDISCOVERY, RESOURCETYPE);
	private static final List<QName> _allSimpleProps = Arrays.asList(
		CREATIONDATE, DISPLAYNAME, GETLASTMODIFIED, GETCONTENTTYPE,
		GETCONTENTLENGTH, ISREADONLY, LOCKDISCOVERY, RESOURCETYPE);

}