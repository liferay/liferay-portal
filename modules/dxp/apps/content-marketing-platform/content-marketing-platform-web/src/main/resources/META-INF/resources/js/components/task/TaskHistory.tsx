/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FDS_EVENT} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useEffect, useRef} from 'react';

import History, {
	AuditEvent,
	EventType,
	Fields,
	HistoryHandle,
} from '../History';

const RELATED_ASSETS_SECTION =
	'com.liferay.site.cms.site.initializer-relatedAssetsSection';

enum TaskEventType {
	CMP_ADD_ASSET = 'CMP_ADD_ASSET',
	CMP_REMOVE_ASSET = 'CMP_REMOVE_ASSET',
}

type TaskEvent = EventType | TaskEventType;

const FIELDS = {
	assignTo: {
		label: Liferay.Language.get('assignee'),
		type: 'assignee',
	},
	description: {
		label: Liferay.Language.get('description'),
		type: 'richText',
	},
	dueDate: {
		label: Liferay.Language.get('due-date'),
		type: 'date',
	},
	r_cmpProjectToCMPTasks_c_cmpProjectId: {
		label: Liferay.Language.get('project'),
		type: 'relationship',
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

export default function TaskHistory({apiURL}: {apiURL: string}) {
	const historyRef = useRef<HistoryHandle>(null);

	/**
	 * Gets the label like: "Test Test updated the Task."
	 */
	const getAuditEventLabel = (auditEvent: AuditEvent<TaskEvent>) => {
		if (auditEvent.eventType === EventType.ADD) {
			return sub(Liferay.Language.get('x-created-a-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="type">{Liferay.Language.get('task')}</strong>,
			]);
		}
		else if (auditEvent.eventType === TaskEventType.CMP_ADD_ASSET) {
			return sub(Liferay.Language.get('x-added-the-asset-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="changedField">
					{auditEvent.auditFieldChanges?.[0].name}
				</strong>,
			]);
		}
		else if (auditEvent.eventType === TaskEventType.CMP_REMOVE_ASSET) {
			return sub(Liferay.Language.get('x-removed-the-asset-x'), [
				<strong key="creatorName">{auditEvent.creator?.name}</strong>,
				<strong key="changedField">
					{auditEvent.auditFieldChanges?.[0].name}
				</strong>,
			]);
		}

		let updatedFieldLabel = Liferay.Language.get('task');

		if (auditEvent.auditFieldChanges?.length === 1) {
			const fieldName = auditEvent.auditFieldChanges[0].name;

			updatedFieldLabel = FIELDS[fieldName]?.label ?? fieldName;
		}

		return sub(Liferay.Language.get('x-updated-the-x'), [
			<strong key="creatorName">{auditEvent.creator?.name}</strong>,
			<strong key="changedFields">{updatedFieldLabel}</strong>,
		]);
	};

	useEffect(() => {
		const handleFDSDisplayUpdated = ({id}: {id: string}) => {
			if (id === RELATED_ASSETS_SECTION) {
				historyRef.current?.refresh();
			}
		};

		Liferay.on(FDS_EVENT.DISPLAY_UPDATED, handleFDSDisplayUpdated);

		return () => {
			Liferay.detach(FDS_EVENT.DISPLAY_UPDATED, handleFDSDisplayUpdated);
		};
	}, []);

	return (
		<History<TaskEvent>
			apiURL={apiURL}
			fields={FIELDS}
			getAuditEventLabel={getAuditEventLabel}
			innerRef={historyRef}
		/>
	);
}
