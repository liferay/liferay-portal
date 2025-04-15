/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet.util;

import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.QName;

import jakarta.portlet.annotations.PortletQName;

/**
 * @author Shuyang Zhou
 */
public class PortletQNameUtil {

	public static javax.xml.namespace.QName getQName(
		String defaultNamespace, Element qNameElement, Element nameElement) {

		QName qName =
			com.liferay.portal.kernel.portlet.PortletQNameUtil.getQName(
				qNameElement, nameElement, defaultNamespace);

		return new javax.xml.namespace.QName(
			qName.getNamespaceURI(), qName.getLocalPart(),
			qName.getNamespacePrefix());
	}

	public static javax.xml.namespace.QName toQName(PortletQName portletQName) {
		return new javax.xml.namespace.QName(
			portletQName.namespaceURI(), portletQName.localPart());
	}

}