/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.resource.type;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 * @author Thiago Buarque
 */
public class DesignLibraryResourceCreationItem {

	public DesignLibraryResourceCreationItem(
		String href, String id, String label) {

		this(href, id, label, null, null);
	}

	public DesignLibraryResourceCreationItem(
		String id, String label, String module,
		Map<String, Object> moduleProps) {

		this(null, id, label, module, moduleProps);
	}

	public String getHref() {
		return _href;
	}

	public String getId() {
		return _id;
	}

	public String getLabel() {
		return _label;
	}

	public String getModule() {
		return _module;
	}

	public Map<String, Object> getModuleProps() {
		return _moduleProps;
	}

	private DesignLibraryResourceCreationItem(
		String href, String id, String label, String module,
		Map<String, Object> moduleProps) {

		_href = href;
		_id = id;
		_label = label;
		_module = module;
		_moduleProps = moduleProps;
	}

	private final String _href;
	private final String _id;
	private final String _label;
	private final String _module;
	private final Map<String, Object> _moduleProps;

}