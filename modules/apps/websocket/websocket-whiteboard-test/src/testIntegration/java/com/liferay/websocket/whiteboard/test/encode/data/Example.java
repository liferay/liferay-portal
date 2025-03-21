/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.websocket.whiteboard.test.encode.data;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Cristina González
 */
@XmlRootElement
public class Example {

	public Example() {
	}

	public Example(int number, String data) {
		_number = number;
		_data = data;
	}

	public String getData() {
		return _data;
	}

	public int getNumber() {
		return _number;
	}

	public void setData(String data) {
		_data = data;
	}

	public void setNumber(int number) {
		_number = number;
	}

	private String _data;
	private int _number;

}