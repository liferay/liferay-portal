/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.discovery.internal.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Javier Gamarra
 */
@XmlRootElement
public class Resource {

	public Hint getHint() {
		return _hint;
	}

	public String getHref() {
		return _href;
	}

	public String getHrefTemplate() {
		return _hrefTemplate;
	}

	public Map<String, String> getHrefVars() {
		return _hrefVars;
	}

	public void setHint(Hint hint) {
		_hint = hint;
	}

	public void setHref(String href) {
		_href = href;
	}

	public void setHrefTemplate(String hrefTemplate) {
		_hrefTemplate = hrefTemplate;
	}

	public void setHrefVars(Map<String, String> hrefVars) {
		_hrefVars = hrefVars;
	}

	private Hint _hint;
	private String _href;
	private String _hrefTemplate;
	private Map<String, String> _hrefVars = new HashMap<>();

}