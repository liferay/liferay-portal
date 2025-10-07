/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';
import DropDown from '@clayui/drop-down';
import Label from '@clayui/label';
import ClayPanel from '@clayui/panel';
import {sub} from 'frontend-js-web';
import moment from 'moment';
import React from 'react';

import {IBulkActionTask} from '../../../common/types/BulkActionTask';
import {
	LABELS_BULK_ACTIONS,
	TASK_STATUS_PROPS,
	URL_TASKS_REPORT_DETAIL,
} from '../util/constants';

function BulkActionsMonitorItemList({
	classNameId,
	items,
}: {
	classNameId: number;
	items: IBulkActionTask[];
}) {
	return (
		<DropDown.ItemList className="task-status" items={items}>
			{(bulkActionTask: unknown) => {
				const {dateModified, executionStatus, id, numberOfItems, type} =
					bulkActionTask as IBulkActionTask;

				const {
					component: Component,
					displayType,
					icon,
					label,
				} = TASK_STATUS_PROPS[executionStatus.key];

				return (
					<ClayPanel
						collapsable
						displayTitle={
							<ClayPanel.Title className="d-flex task-status-item">
								<div className="task-status-item-icon">
									<Component
										className={
											icon
												? `text-${displayType}`
												: 'loading-animation text-info'
										}
										displayType={displayType}
										size="sm"
										symbol={icon}
									/>
								</div>

								<div className="task-status-item-text">
									<p className="mb-1 text-secondary">
										{LABELS_BULK_ACTIONS[type]}
									</p>

									<span className="d-flex text-1 text-secondary text-uppercase">
										<span>
											{sub(
												Liferay.Language.get('x-items'),
												[numberOfItems]
											)}
										</span>

										<span className="mx-1">-</span>

										<span className="text-secondary">
											{moment(dateModified).fromNow(
												false
											)}
										</span>
									</span>

									<Label
										className="align-self-start"
										displayType={displayType}
									>
										{label}
									</Label>
								</div>
							</ClayPanel.Title>
						}
						key={id}
						showCollapseIcon={false}
					>
						<ClayPanel.Body className="d-flex">
							<a
								href={`${URL_TASKS_REPORT_DETAIL}${classNameId}/${id}`}
							>
								<Button
									className={`border-${displayType} btn-xs`}
									displayType={displayType}
								>
									{Liferay.Language.get('view')}
								</Button>
							</a>
						</ClayPanel.Body>
					</ClayPanel>
				);
			}}
		</DropDown.ItemList>
	);
}

export default BulkActionsMonitorItemList;
