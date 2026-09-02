/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.product.navigation.omni.search.constants.OmniSearchConstants;

import java.util.Collections;
import java.util.List;

/**
 * @author Marcos Castro
 * @author Thiago Buarque
 */
public class OmniSearchResult {

	public OmniSearchResult(
		String icon, List<OmniSearchResult> omniSearchResults, String title) {

		_icon = icon;
		_omniSearchResults = ListUtil.subList(
			omniSearchResults, 0, OmniSearchConstants.MAX_ENTRIES_PER_SECTION);
		_title = title;

		_type = Type.SECTION;
	}

	public OmniSearchResult(
		String description, String icon, String title, String url) {

		_description = description;
		_icon = icon;
		_title = title;
		_url = url;

		_omniSearchResults = Collections.emptyList();

		_type = Type.ENTRY;
	}

	public String getDescription() {
		return _description;
	}

	public String getIcon() {
		return _icon;
	}

	public List<OmniSearchResult> getOmniSearchResults() {
		return List.copyOf(_omniSearchResults);
	}

	public String getTitle() {
		return _title;
	}

	public Type getType() {
		return _type;
	}

	public String getURL() {
		return _url;
	}

	public enum Type {

		ENTRY("ENTRY"), SECTION("SECTION");

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

	private String _description;
	private final String _icon;
	private final List<OmniSearchResult> _omniSearchResults;
	private final String _title;
	private final Type _type;
	private String _url;

}