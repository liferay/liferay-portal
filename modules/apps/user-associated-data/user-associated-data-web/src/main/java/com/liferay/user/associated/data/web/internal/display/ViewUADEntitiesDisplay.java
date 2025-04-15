/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.display;

import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.dao.search.ResultRowSplitter;
import com.liferay.portal.kernel.dao.search.SearchContainer;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Drew Brokke
 */
public class ViewUADEntitiesDisplay {

	public String getApplicationKey() {
		return _applicationKey;
	}

	public long[] getGroupIds() {
		return _groupIds;
	}

	public ResultRowSplitter getResultRowSplitter() {
		return _resultRowSplitter;
	}

	public String getScope() {
		return _scope;
	}

	public SearchContainer<UADEntity<?>> getSearchContainer() {
		return _searchContainer;
	}

	public String getSearchContainerID(
		HttpServletRequest httpServletRequest, String namespace) {

		if (_searchContainer == null) {
			return StringPool.BLANK;
		}

		return _searchContainer.getId(httpServletRequest, namespace);
	}

	public String[] getTypeKeys() {
		return _typeKeys;
	}

	public String getTypeName() {
		return _typeName;
	}

	public String getUADRegistryKey() {
		return _uadRegistryKey;
	}

	public String getUserOwnedEntityPKsString() {
		if (_searchContainer == null) {
			return StringPool.BLANK;
		}

		List<String> userOwnedPKs = new ArrayList<>();

		List<UADEntity<?>> entities = _searchContainer.getResults();

		for (UADEntity<?> entity : entities) {
			if (entity.isUserOwned()) {
				userOwnedPKs.add(String.valueOf(entity.getPrimaryKey()));
			}
		}

		return StringUtil.merge(
			userOwnedPKs.toArray(new String[0]), StringPool.COMMA);
	}

	public boolean isHierarchy() {
		return _hierarchy;
	}

	public void setApplicationKey(String applicationKey) {
		_applicationKey = applicationKey;
	}

	public void setGroupIds(long[] groupIds) {
		_groupIds = groupIds;
	}

	public void setHierarchy(boolean hierarchy) {
		_hierarchy = hierarchy;
	}

	public void setResultRowSplitter(ResultRowSplitter resultRowSplitter) {
		_resultRowSplitter = resultRowSplitter;
	}

	public void setScope(String scope) {
		_scope = scope;
	}

	public void setSearchContainer(
		SearchContainer<UADEntity<?>> searchContainer) {

		_searchContainer = searchContainer;
	}

	public void setTypeKeys(String[] typeKeys) {
		_typeKeys = typeKeys;
	}

	public void setTypeName(String typeName) {
		_typeName = typeName;
	}

	public void setUADRegistryKey(String uadRegistryKey) {
		_uadRegistryKey = uadRegistryKey;
	}

	private String _applicationKey;
	private long[] _groupIds;
	private boolean _hierarchy;
	private ResultRowSplitter _resultRowSplitter;
	private String _scope;
	private SearchContainer<UADEntity<?>> _searchContainer;
	private String[] _typeKeys;
	private String _typeName;
	private String _uadRegistryKey = StringPool.BLANK;

}