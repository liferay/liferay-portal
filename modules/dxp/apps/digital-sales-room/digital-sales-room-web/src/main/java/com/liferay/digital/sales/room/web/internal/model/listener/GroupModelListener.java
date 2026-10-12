/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.sales.room.web.internal.model.listener;

import com.liferay.digital.sales.room.constants.DSRTicketConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Ticket;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.TicketLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.io.Serializable;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onAfterUpdate(Group originalGroup, Group group)
		throws ModelListenerException {

		try {
			_onAfterUpdate(originalGroup, group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Override
	public void onBeforeRemove(Group group) throws ModelListenerException {
		try {
			_onBeforeRemove(group);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	private void _onAfterUpdate(Group originalGroup, Group group)
		throws Exception {

		if (Objects.equals(
				originalGroup.getName(LocaleUtil.getDefault()),
				group.getName(LocaleUtil.getDefault()))) {

			return;
		}

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", group.getCompanyId());

		if ((objectDefinition == null) ||
			!Objects.equals(
				group.getClassName(), objectDefinition.getClassName())) {

			return;
		}

		ObjectEntry objectEntry = _objectEntryLocalService.fetchObjectEntry(
			group.getClassPK());

		if (objectEntry == null) {
			return;
		}

		_objectEntryLocalService.partialUpdateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"name", group.getName(LocaleUtil.getDefault())
			).build(),
			new ServiceContext());
	}

	private void _onBeforeRemove(Group group) throws Exception {
		for (Ticket ticket :
				ListUtil.concat(
					_ticketLocalService.getTickets(
						Group.class.getName(), group.getGroupId(),
						DSRTicketConstants.TYPE_EXPIRE_MEMBERSHIP),
					_ticketLocalService.getTickets(
						Group.class.getName(), group.getGroupId(),
						DSRTicketConstants.TYPE_INVITE_MEMBER))) {

			_ticketLocalService.deleteTicket(ticket);
		}
	}

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private TicketLocalService _ticketLocalService;

}