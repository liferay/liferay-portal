/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.discovery.internal.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 */
@XmlRootElement
public class Hint {

	public Hint() {
	}

	public Hint(String[] allow, String[] formats) {
		_allow = allow;
		_formats = formats;
	}

	public String[] getAllow() {
		return _allow;
	}

	public String[] getFormats() {
		return _formats;
	}

	public void setAllow(String[] allow) {
		_allow = allow;
	}

	public void setFormats(String[] formats) {
		_formats = formats;
	}

	private String[] _allow;
	private String[] _formats;

}