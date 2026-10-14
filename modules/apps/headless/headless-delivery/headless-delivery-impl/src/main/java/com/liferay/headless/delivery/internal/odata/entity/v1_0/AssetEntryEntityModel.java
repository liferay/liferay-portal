/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.odata.entity.v1_0;

import com.liferay.headless.common.spi.odata.entity.EntityFieldsMapFactory;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.odata.entity.IntegerEntityField;

import java.util.Map;

/**
 * @author Luis Ortiz
 */
public class AssetEntryEntityModel implements EntityModel {

	public AssetEntryEntityModel() {
		_entityFieldsMap = EntityFieldsMapFactory.create(
			new EntityField(
				"classNameId", EntityField.Type.INTEGER,
				locale -> Field.ENTRY_CLASS_NAME,
				locale -> Field.ENTRY_CLASS_NAME,
				value -> PortalUtil.getClassName(GetterUtil.getLong(value))),
			new EntityField(
				"status", EntityField.Type.STRING, locale -> Field.STATUS,
				locale -> Field.STATUS,
				value -> String.valueOf(
					WorkflowConstants.getLabelStatus(String.valueOf(value)))),
			new IntegerEntityField("classPK", locale -> Field.ENTRY_CLASS_PK),
			new IntegerEntityField(
				"classTypeId", locale -> Field.CLASS_TYPE_ID),
			new IntegerEntityField("groupId", locale -> Field.GROUP_ID));
	}

	@Override
	public Map<String, EntityField> getEntityFieldsMap() {
		return _entityFieldsMap;
	}

	private final Map<String, EntityField> _entityFieldsMap;

}