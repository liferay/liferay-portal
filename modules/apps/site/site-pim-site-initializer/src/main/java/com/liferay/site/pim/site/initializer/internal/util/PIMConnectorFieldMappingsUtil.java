/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.object.service.ObjectRelationshipLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.site.pim.site.initializer.constants.PIMObjectDefinitionConstants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Stefano Motta
 */
public class PIMConnectorFieldMappingsUtil {

	public static final String TYPE_FIXED_VALUE = "fixedValue";

	public static ObjectRelationship fetchObjectRelationship(
		long objectDefinitionId) {

		return ObjectRelationshipLocalServiceUtil.
			fetchObjectRelationshipByExternalReferenceCode(
				"L_PIM_CONNECTOR_TO_PIM_CONNECTOR_FIELD_MAPPINGS",
				objectDefinitionId);
	}

	public static String getAPIURL(long companyId) {
		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					PIMObjectDefinitionConstants.
						EXTERNAL_REFERENCE_CODE_CONNECTOR_FIELD_MAPPING,
					companyId);

		if (objectDefinition == null) {
			return StringPool.BLANK;
		}

		return "/o" + objectDefinition.getRESTContextPath();
	}

	public static List<ObjectEntry> getObjectEntries(ObjectEntry objectEntry)
		throws PortalException {

		ObjectRelationship objectRelationship = fetchObjectRelationship(
			objectEntry.getObjectDefinitionId());

		if (objectRelationship == null) {
			return Collections.emptyList();
		}

		return ListUtil.sort(
			ObjectEntryLocalServiceUtil.getOneToManyObjectEntries(
				objectEntry.getGroupId(),
				objectRelationship.getObjectRelationshipId(), null, false,
				objectEntry.getObjectEntryId(), true, null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null),
			Comparator.comparingInt(
				curObjectEntry -> GetterUtil.getInteger(
					MapUtil.getString(
						curObjectEntry.getValues(), "priority"))));
	}

}