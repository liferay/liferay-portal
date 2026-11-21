/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.registration.portlet;

import javax.xml.namespace.QName;

/**
 * @author Neil Griffin
 */
public class PublicRenderParameter {

	public PublicRenderParameter(String identifier, QName qName) {
		_identifier = identifier;
		_qName = qName;
	}

	public String getIdentifier() {
		return _identifier;
	}

	public QName getQName() {
		return _qName;
	}

	private final String _identifier;
	private final QName _qName;

}