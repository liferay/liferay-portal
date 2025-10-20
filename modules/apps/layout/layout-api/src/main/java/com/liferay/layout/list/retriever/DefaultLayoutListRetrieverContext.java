/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.list.retriever;

import com.liferay.info.filter.InfoFilter;
import com.liferay.info.pagination.Pagination;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Map;

/**
 * @author Eudaldo Alonso
 */
public class DefaultLayoutListRetrieverContext
	implements LayoutListRetrieverContext {

	@Override
	public Map<String, String[]> getConfiguration() {
		return _configuration;
	}

	@Override
	public Object getContextObject() {
		return _contextObject;
	}

	@Override
	public <T> T getInfoFilter(Class<? extends InfoFilter> clazz) {
		if (MapUtil.isEmpty(_infoFilters)) {
			return null;
		}

		InfoFilter infoFilter = _infoFilters.getOrDefault(
			clazz.getName(), null);

		if (infoFilter != null) {
			return (T)infoFilter;
		}

		return null;
	}

	@Override
	public Map<String, InfoFilter> getInfoFilters() {
		return _infoFilters;
	}

	@Override
	public Pagination getPagination() {
		return _pagination;
	}

	@Override
	public long getScopeGroupId() {
		return _scopeGroupId;
	}

	@Override
	public long[] getSegmentsEntryIds() {
		return _segmentsEntryIds;
	}

	public void setConfiguration(Map<String, String[]> configuration) {
		_configuration = configuration;
	}

	public void setContextObject(Object contextObject) {
		_contextObject = contextObject;
	}

	public void setInfoFilters(Map<String, InfoFilter> infoFilters) {
		_infoFilters = infoFilters;
	}

	public void setPagination(Pagination pagination) {
		_pagination = pagination;
	}

	public void setScopeGroupId(long scopeGroupId) {
		_scopeGroupId = scopeGroupId;
	}

	public void setSegmentsEntryIds(long[] segmentsEntryIds) {
		_segmentsEntryIds = segmentsEntryIds;
	}

	private Map<String, String[]> _configuration;
	private Object _contextObject;
	private Map<String, InfoFilter> _infoFilters;
	private Pagination _pagination;
	private long _scopeGroupId;
	private long[] _segmentsEntryIds;

}