/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import React, {useRef} from 'react';

import History, {
	AuditEvent,
	EventType,
	Fields,
	HistoryHandle,
} from '../History';

enum ProjectEventType {
	CMP_ADD_MEMBER = 'CMP_ADD_MEMBER',
	CMP_REMOVE_MEMBER = 'CMP_REMOVE_MEMBER',
}

type ProjectEvent = EventType | ProjectEventType;

const FIELDS = {
	description: {
		label: Liferay.Language.get('description'),
		type: 'richText',
	},
	dueDate: {
		label: Liferay.Language.get('due-date'),
		type: 'date',
	},
	r_userToCMPProjectManager_userId: {
		label: Liferay.Language.get('manager'),
		type: 'user',
	},
	r_userToCMPProjectSponsor_userId: {
		label: Liferay.Language.get('sponsor'),
		type: 'user',
	},
	state: {
		label: Liferay.Language.get('state'),
		type: 'picklist',
	},
	title: {
		label: Liferay.Language.get('title'),
		type: 'text',
	},
} as Fields;

export default function ProjectHistory({apiURL}: {apiURL: string}) {
	const historyRef = useRef<HistoryHandle>(null);

	const getAuditEventLabel = (auditEvent: AuditEvent<ProjectEvent>) => {
		if (auditEvent.eventType === EventType.ADD) {
			return sub(Liferay.Language.get('x-created-a-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="type">{Liferay.Language.get('project')}</strong>,
			]);
		}
		else if (
			auditEvent.eventType === ProjectEventType.CMP_ADD_MEMBER ||
			auditEvent.eventType === ProjectEventType.CMP_REMOVE_MEMBER
		) {
			return sub(Liferay.Language.get('x-updated-the-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="type">
					{Liferay.Language.get('project-members')}
				</strong>,
			]);
		}

		let updatedFieldLabel = Liferay.Language.get('project');

		if (auditEvent.auditFieldChanges?.length === 1) {
			const fieldName = auditEvent.auditFieldChanges[0].name;

			updatedFieldLabel = FIELDS[fieldName]?.label ?? fieldName;
		}

		return sub(Liferay.Language.get('x-updated-the-x'), [
			<strong key="creatorName">{auditEvent.creator?.name}</strong>,
			<strong key="changedFields">{updatedFieldLabel}</strong>,
		]);
	};

	return (
		<History<ProjectEvent>
			apiURL={apiURL}
			fields={FIELDS}
			getAuditEventLabel={getAuditEventLabel}
			innerRef={historyRef}
		/>
	);
}
