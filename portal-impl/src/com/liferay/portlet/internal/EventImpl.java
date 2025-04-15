/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.internal;

import jakarta.portlet.Event;

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * @author Brian Wing Shun Chan
 */
public class EventImpl implements Event, Serializable {

	public EventImpl(String name, QName qName, Serializable value) {
		_name = name;
		_qName = qName;
		_value = value;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public QName getQName() {
		return _qName;
	}

	@Override
	public Serializable getValue() {
		return _value;
	}

	private final String _name;
	private final QName _qName;
	private final Serializable _value;

}