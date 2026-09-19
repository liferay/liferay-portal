/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.Namespace;
import com.liferay.portal.kernel.xml.QName;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletQNameImpl implements PortletQName {

	@Override
	public String getKey(QName qName) {
		return getKey(qName.getNamespaceURI(), qName.getLocalPart());
	}

	@Override
	public String getKey(String uri, String localPart) {
		return StringBundler.concat(uri, _KEY_SEPARATOR, localPart);
	}

	@Override
	public String getPublicRenderParameterIdentifier(
		String publicRenderParameterName) {

		if (!publicRenderParameterName.startsWith(
				PUBLIC_RENDER_PARAMETER_NAMESPACE) &&
			!publicRenderParameterName.startsWith(
				REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE)) {

			return null;
		}

		return _identifiers.get(publicRenderParameterName);
	}

	@Override
	public String getPublicRenderParameterName(QName qName) {
		String publicRenderParameterName = _qNameStrings.get(qName);

		if (publicRenderParameterName == null) {
			publicRenderParameterName = _toString(
				PUBLIC_RENDER_PARAMETER_NAMESPACE, qName);

			_qNames.put(publicRenderParameterName, qName);
			_qNameStrings.put(qName, publicRenderParameterName);
		}

		return publicRenderParameterName;
	}

	@Override
	public QName getQName(
		Element qNameEl, Element nameEl, String defaultNamespace) {

		if ((qNameEl == null) && (nameEl == null)) {
			_log.error("both qname and name elements are null");

			return null;
		}

		if (qNameEl == null) {
			return SAXReaderUtil.createQName(
				nameEl.getTextTrim(),
				SAXReaderUtil.createNamespace(defaultNamespace));
		}

		String localPart = qNameEl.getTextTrim();

		int pos = localPart.indexOf(CharPool.COLON);

		if (pos == -1) {
			if (_log.isDebugEnabled()) {
				_log.debug("qname " + localPart + " does not have a prefix");
			}

			return SAXReaderUtil.createQName(localPart);
		}

		String prefix = localPart.substring(0, pos);

		Namespace namespace = qNameEl.getNamespaceForPrefix(prefix);

		if (namespace == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"qname " + localPart + " does not have a valid namespace");
			}

			return null;
		}

		localPart = localPart.substring(prefix.length() + 1);

		return SAXReaderUtil.createQName(localPart, namespace);
	}

	@Override
	public QName getQName(String publicRenderParameterName) {
		if (!publicRenderParameterName.startsWith(
				PUBLIC_RENDER_PARAMETER_NAMESPACE) &&
			!publicRenderParameterName.startsWith(
				REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE)) {

			return null;
		}

		return _qNames.get(publicRenderParameterName);
	}

	@Override
	public String getRemovePublicRenderParameterName(QName qName) {
		String removePublicRenderParameterName = _qNameStrings.get(qName);

		if (removePublicRenderParameterName == null) {
			removePublicRenderParameterName = _toString(
				REMOVE_PUBLIC_RENDER_PARAMETER_NAMESPACE, qName);

			_qNames.put(removePublicRenderParameterName, qName);
			_qNameStrings.put(qName, removePublicRenderParameterName);
		}

		return removePublicRenderParameterName;
	}

	@Override
	public void setPublicRenderParameterIdentifier(
		String publicRenderParameterName, String identifier) {

		if (!Objects.equals(
				identifier, _identifiers.get(publicRenderParameterName))) {

			_identifiers.put(publicRenderParameterName, identifier);
		}
	}

	private String _toString(String prefix, QName qName) {
		StringBundler sb = new StringBundler(6);

		sb.append(prefix);

		String namespacePrefix = qName.getNamespacePrefix();

		if (!Validator.isBlank(namespacePrefix)) {
			sb.append(namespacePrefix);
			sb.append(StringPool.UNDERLINE);
		}

		if (!Validator.isBlank(qName.getNamespaceURI())) {
			sb.append(qName.getNamespaceURI());
			sb.append(StringPool.UNDERLINE);
		}

		sb.append(qName.getLocalPart());

		return sb.toString();
	}

	private static final String _KEY_SEPARATOR = "_KEY_";

	private static final Log _log = LogFactoryUtil.getLog(
		PortletQNameImpl.class);

	private final Map<String, String> _identifiers = new ConcurrentHashMap<>();
	private final Map<QName, String> _qNameStrings = new ConcurrentHashMap<>();
	private final Map<String, QName> _qNames = new ConcurrentHashMap<>();

}