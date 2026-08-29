/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.model.listener;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Collections;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseModelListener<T extends BaseModel<T>>
	extends com.liferay.portal.kernel.model.BaseModelListener<T> {

	protected void route(Attribute attribute, String eventType, long groupId)
		throws PortalException {

		Group group = GroupLocalServiceUtil.fetchGroup(groupId);

		if ((group == null) ||
			!FeatureFlagManagerUtil.isEnabled(
				group.getCompanyId(), "LPD-58677") ||
			!group.isDepot()) {

			return;
		}

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
			groupId);

		if ((depotEntry == null) ||
			!Objects.equals(
				depotEntry.getType(), DepotConstants.TYPE_PROJECT)) {

			return;
		}

		ObjectDefinition objectDefinition =
			ObjectDefinitionLocalServiceUtil.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_CMP_PROJECT", group.getCompanyId());

		if (objectDefinition == null) {
			return;
		}

		ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.fetchObjectEntry(
			groupId, objectDefinition.getObjectDefinitionId());

		if (objectEntry == null) {
			return;
		}

		auditRouter.route(
			AuditMessageBuilder.buildAuditMessage(
				objectEntry.getModelClassName(), objectEntry.getObjectEntryId(),
				eventType, Collections.singletonList(attribute)));
	}

	@Reference
	protected AuditRouter auditRouter;

}