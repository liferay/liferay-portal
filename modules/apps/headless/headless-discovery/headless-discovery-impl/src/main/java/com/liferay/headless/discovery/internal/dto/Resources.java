/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.discovery.internal.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 */
@XmlRootElement
public class Resources {

	public Resources() {
	}

	public Resources(Map<String, Resource> resources) {
		_resources = resources;
	}

	public Map<String, Resource> getResources() {
		return _resources;
	}

	public void setResources(Map<String, Resource> resources) {
		_resources = resources;
	}

	private Map<String, Resource> _resources = new TreeMap<>();

}