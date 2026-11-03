/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.view.table;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Marco Leo
 */
public class FDSTableSchemaField {

	public String getActionId() {
		return _actionId;
	}

	public String getContentRenderer() {
		return _contentRenderer;
	}

	public String getContentRendererModuleURL() {
		return _contentRendererModuleURL;
	}

	public String getFieldName() {
		return _fieldName;
	}

	public String getLabel() {
		return _label;
	}

	public SortingOrder getSortingOrder() {
		return _sortingOrder;
	}

	public boolean isContentRendererClientExtension() {
		return _contentRendererClientExtension;
	}

	public boolean isHidden() {
		return _hidden;
	}

	public boolean isLocalizeLabel() {
		return _localizeLabel;
	}

	public boolean isSortable() {
		return _sortable;
	}

	public FDSTableSchemaField setActionId(String actionId) {
		_actionId = actionId;

		return this;
	}

	public FDSTableSchemaField setContentRenderer(String contentRenderer) {
		_contentRenderer = contentRenderer;

		return this;
	}

	public FDSTableSchemaField setContentRendererClientExtension(
		boolean contentRendererClientExtension) {

		_contentRendererClientExtension = contentRendererClientExtension;

		return this;
	}

	public FDSTableSchemaField setContentRendererModuleURL(
		String contentRendererModuleURL) {

		_contentRendererModuleURL = contentRendererModuleURL;

		return this;
	}

	public FDSTableSchemaField setFieldName(String fieldName) {
		_fieldName = fieldName;

		return this;
	}

	public FDSTableSchemaField setHidden(boolean hidden) {
		_hidden = hidden;

		return this;
	}

	public FDSTableSchemaField setLabel(String label) {
		_label = label;

		return this;
	}

	public FDSTableSchemaField setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;

		return this;
	}

	public FDSTableSchemaField setSortable(boolean sortable) {
		_sortable = sortable;

		return this;
	}

	public FDSTableSchemaField setSortingOrder(SortingOrder sortingOrder) {
		_sortingOrder = sortingOrder;

		return this;
	}

	public JSONObject toJSONObject() {
		return JSONUtil.put(
			"actionId", getActionId()
		).put(
			"contentRenderer", getContentRenderer()
		).put(
			"contentRendererClientExtension", isContentRendererClientExtension()
		).put(
			"contentRendererModuleURL", getContentRendererModuleURL()
		).put(
			"fieldName",
			() -> {
				String fieldName = getFieldName();

				if (fieldName.contains(StringPool.PERIOD)) {
					return StringUtil.split(fieldName, StringPool.PERIOD);
				}

				return fieldName;
			}
		).put(
			"hidden", isHidden()
		).put(
			"localizeLabel", isLocalizeLabel()
		).put(
			"sortable", isSortable()
		).put(
			"sortingOrder",
			() -> {
				FDSTableSchemaField.SortingOrder sortingOrder =
					getSortingOrder();

				if (sortingOrder != null) {
					return StringUtil.toLowerCase(sortingOrder.toString());
				}

				return null;
			}
		);
	}

	public enum SortingOrder {

		ASC, DESC

	}

	private String _actionId;
	private String _contentRenderer;
	private boolean _contentRendererClientExtension;
	private String _contentRendererModuleURL;
	private String _fieldName;
	private boolean _hidden;
	private String _label;
	private boolean _localizeLabel = true;
	private boolean _sortable;
	private SortingOrder _sortingOrder;

}