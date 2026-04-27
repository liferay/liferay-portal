/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Brian Wing Shun Chan
 * @author Dennis Ju
 */
public class PortletCategory implements Serializable {

	public PortletCategory() {
		this("root");
	}

	public PortletCategory(String name) {
		this(name, new HashSet<String>());
	}

	public PortletCategory(String name, Set<String> portletIds) {
		_portletCategories = new ConcurrentHashMap<>();

		_portletIds = ConcurrentHashMap.newKeySet();

		_portletIds.addAll(portletIds);

		if (name.contains(_DELIMITER)) {
			int index = name.lastIndexOf(_DELIMITER);

			_name = name.substring(index + _DELIMITER.length());

			String parentName = name.substring(0, index);

			PortletCategory parentPortletCategory = new PortletCategory(
				parentName);

			parentPortletCategory.addCategory(this);
		}
		else {
			_name = name;

			_parentPortletCategory = null;
			_path = name;
		}
	}

	public void addCategory(PortletCategory portletCategory) {
		portletCategory.setParentCategory(this);

		String path = StringBundler.concat(
			_path, _DELIMITER, portletCategory.getName());

		portletCategory.setPath(path);

		_portletCategories.put(portletCategory.getName(), portletCategory);
	}

	public Collection<PortletCategory> getCategories() {
		return Collections.unmodifiableCollection(_portletCategories.values());
	}

	public PortletCategory getCategory(String name) {
		int index = name.indexOf(_DELIMITER);

		if (index == -1) {
			return _portletCategories.get(name);
		}

		PortletCategory portletCategory = _portletCategories.get(
			name.substring(0, index));

		if (portletCategory == null) {
			return null;
		}

		return portletCategory.getCategory(
			name.substring(index + _DELIMITER.length()));
	}

	public String getName() {
		return _name;
	}

	public PortletCategory getParentCategory() {
		return _parentPortletCategory;
	}

	public String getPath() {
		return _path;
	}

	public Set<String> getPortletIds() {
		return _portletIds;
	}

	public PortletCategory getRootCategory() {
		if (_parentPortletCategory == null) {
			return this;
		}

		return _parentPortletCategory.getRootCategory();
	}

	public boolean isHidden() {
		return _name.equals(PortletCategoryConstants.NAME_HIDDEN);
	}

	public void merge(PortletCategory newPortletCategory) {
		merge(this, newPortletCategory);
	}

	public void separate(Set<String> portletIds) {
		for (PortletCategory portletCategory : _portletCategories.values()) {
			portletCategory.separate(portletIds);
		}

		Iterator<String> iterator = _portletIds.iterator();

		while (iterator.hasNext()) {
			String portletId = iterator.next();

			if (portletIds.contains(portletId)) {
				iterator.remove();
			}
		}
	}

	public void separate(String portletId) {
		Set<String> portletIds = new HashSet<>();

		portletIds.add(portletId);

		separate(portletIds);
	}

	public void setPortletIds(Set<String> portletIds) {
		_portletIds.clear();

		_portletIds.addAll(portletIds);
	}

	protected void merge(
		PortletCategory portletCategory1, PortletCategory portletCategory2) {

		Collection<PortletCategory> portletCategories =
			portletCategory2.getCategories();

		for (PortletCategory curPortletCategory2 : portletCategories) {
			PortletCategory curPortletCategory1 = portletCategory1.getCategory(
				curPortletCategory2.getName());

			if (curPortletCategory1 != null) {
				merge(curPortletCategory1, curPortletCategory2);
			}
			else {
				portletCategory1.addCategory(curPortletCategory2);
			}
		}

		Set<String> portletIds1 = portletCategory1.getPortletIds();
		Set<String> portletIds2 = portletCategory2.getPortletIds();

		portletIds1.addAll(portletIds2);
	}

	protected void setParentCategory(PortletCategory portletCategory) {
		_parentPortletCategory = portletCategory;
	}

	protected void setPath(String path) {
		_path = path;

		for (PortletCategory portletCategory : getCategories()) {
			portletCategory.setPath(
				StringBundler.concat(
					_path, _DELIMITER, portletCategory.getName()));
		}
	}

	private static final String _DELIMITER = StringPool.DOUBLE_SLASH;

	private final String _name;
	private PortletCategory _parentPortletCategory;
	private String _path;
	private final Map<String, PortletCategory> _portletCategories;
	private final Set<String> _portletIds;

}