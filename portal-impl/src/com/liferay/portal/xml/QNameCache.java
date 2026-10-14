/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.xml;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.DocumentFactory;
import org.dom4j.QName;

/**
 * @author Jorge Díaz
 */
public class QNameCache extends org.dom4j.tree.QNameCache {

	public QNameCache(DocumentFactory documentFactory) {
		super(documentFactory);

		namespaceCache = new ConcurrentHashMap<>();
		noNamespaceCache = new ConcurrentHashMap<>();
	}

	@Override
	protected Map<String, QName> createMap() {
		return new ConcurrentHashMap<>();
	}

}