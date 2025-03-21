/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.lang.builder;

/**
 * @author Hugo Huijser
 */
public enum LangBuilderCategory {

	ACTION_NAMES("Action Names", "action.", 5),
	CATEGORY_TITLES("Category Titles", "category.", 3),
	COUNTRIES("Country", "country.", 7), CURRENCIES("Currency", "currency.", 8),
	LANGUAGE_SETTINGS("Language Settings", "lang.", 1),
	LANGUAGES("Language", "language.", 9), MESSAGES("Messages", "", 6),
	MODEL_RESOURCES("Model Resources", "model.resource.", 4),
	PORLET_INFORMATION(
		"Portlet Descriptions and Titles", "jakarta.portlet.", 2);

	public String getDescription() {
		return _description;
	}

	public int getIndex() {
		return _index;
	}

	public String getPrefix() {
		return _prefix;
	}

	private LangBuilderCategory(String description, String prefix, int index) {
		_description = description;
		_prefix = prefix;
		_index = index;
	}

	private final String _description;
	private final int _index;
	private final String _prefix;

}