/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.display.context;

import com.liferay.object.field.business.type.ObjectFieldBusinessType;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.io.Serializable;

import java.util.Map;

/**
 * @author Pedro Leite
 */
public class ViewTaskInfoSummarySectionDisplayContext
	extends BaseInfoSummarySectionDisplayContext {

	public ViewTaskInfoSummarySectionDisplayContext(
		ObjectFieldBusinessType assigneeObjectFieldBusinessType,
		ObjectEntry objectEntry, ThemeDisplay themeDisplay) {

		super(objectEntry, themeDisplay);

		_assigneeObjectFieldBusinessType = assigneeObjectFieldBusinessType;
	}

	@Override
	public Map<String, Object> getProperties() throws Exception {
		return HashMapBuilder.<String, Object>put(
			"assignTo",
			() -> {
				Map<String, Serializable> values = objectEntry.getValues();

				Assignee assignee =
					(Assignee)_assigneeObjectFieldBusinessType.getDTOValue(
						null, null, null, null, values.get("assignTo"));

				if (assignee == null) {
					return null;
				}

				return JSONFactoryUtil.createJSONObject(assignee.toString());
			}
		).put(
			"cmpTaskObjectEntryId", objectEntry.getObjectEntryId()
		).putAll(
			super.getProperties()
		).build();
	}

	private final ObjectFieldBusinessType _assigneeObjectFieldBusinessType;

}