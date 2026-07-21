/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayEmptyState from '@clayui/empty-state';
import ClayLabel from '@clayui/label';
import ClayTable from '@clayui/table';
import ClayTabs from '@clayui/tabs';
import React, {useEffect, useState} from 'react';

import {Launch, listLaunches} from '../api/launches';

const WORKFLOW_STATUS_APPROVED = 0;
const WORKFLOW_STATUS_DRAFT = 2;
const WORKFLOW_STATUS_INACTIVE = 5;
const WORKFLOW_STATUS_PENDING = 1;

const STATUS_LABELS: Record<
	number,
	{displayType: 'danger' | 'info' | 'success'; label: string}
> = {
	[WORKFLOW_STATUS_APPROVED]: {
		displayType: 'success',
		label: Liferay.Language.get('published'),
	},
	[WORKFLOW_STATUS_DRAFT]: {
		displayType: 'info',
		label: Liferay.Language.get('in-progress'),
	},
	[WORKFLOW_STATUS_INACTIVE]: {
		displayType: 'danger',
		label: Liferay.Language.get('failed'),
	},
	[WORKFLOW_STATUS_PENDING]: {
		displayType: 'info',
		label: Liferay.Language.get('in-progress'),
	},
};

function StatusLabel({status}: {status?: number}) {
	const {displayType, label} =
		(status !== undefined && STATUS_LABELS[status]) ||
		STATUS_LABELS[WORKFLOW_STATUS_PENDING];

	return <ClayLabel displayType={displayType}>{label}</ClayLabel>;
}

interface Props {
	onNew: () => void;
	onSelect: (launch: Launch) => void;
}

export default function LaunchesLanding({onNew, onSelect}: Props) {
	const [activeTab, setActiveTab] = useState(0);
	const [launches, setLaunches] = useState<Launch[]>([]);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		listLaunches()
			.then((items) => setLaunches(items))
			.catch(() => setLaunches([]))
			.finally(() => setLoading(false));
	}, []);

	if (loading) {
		return (
			<div className="p-4">
				{Liferay.Language.get('loading-launches')}
			</div>
		);
	}

	return (
		<div className="launch-landing">
			<div className="container-fluid p-4">
				<h1 className="mb-4">{Liferay.Language.get('launches')}</h1>

				<ClayTabs modern>
					<ClayTabs.Item
						active={activeTab === 0}
						innerProps={{
							'aria-controls': 'tabpanel-ongoing',
						}}
						onClick={() => setActiveTab(0)}
					>
						{Liferay.Language.get('ongoing')}
					</ClayTabs.Item>

					<ClayTabs.Item
						active={activeTab === 1}
						innerProps={{
							'aria-controls': 'tabpanel-published',
						}}
						onClick={() => setActiveTab(1)}
					>
						{Liferay.Language.get('published')}
					</ClayTabs.Item>
				</ClayTabs>

				<ClayTabs.Content activeIndex={activeTab} fade>
					<ClayTabs.TabPane aria-labelledby="tab-ongoing">
						{!launches.length ? (
							<ClayEmptyState
								description={Liferay.Language.get(
									'click-new-to-create-your-first-launch'
								)}
								imgProps={{
									alt: Liferay.Language.get('no-launches'),
								}}
								imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
								title={Liferay.Language.get(
									'no-ongoing-launches'
								)}
							>
								<ClayButton
									displayType="primary"
									onClick={onNew}
								>
									{Liferay.Language.get('new-launch')}
								</ClayButton>
							</ClayEmptyState>
						) : (
							<LaunchTable
								launches={launches}
								onSelect={onSelect}
							/>
						)}
					</ClayTabs.TabPane>

					<ClayTabs.TabPane aria-labelledby="tab-published">
						<ClayEmptyState
							description={Liferay.Language.get(
								'no-published-launches'
							)}
							imgProps={{
								alt: Liferay.Language.get(
									'no-published-launches'
								),
							}}
							imgSrc={`${Liferay.ThemeDisplay.getPathThemeImages()}/states/empty_state.svg`}
							title={Liferay.Language.get(
								'no-published-launches'
							)}
						/>
					</ClayTabs.TabPane>
				</ClayTabs.Content>
			</div>

			{launches.length ? (
				<div className="d-flex justify-content-end p-3">
					<ClayButton displayType="primary" onClick={onNew}>
						{Liferay.Language.get('new-launch')}
					</ClayButton>
				</div>
			) : null}
		</div>
	);
}

interface LaunchTableProps {
	launches: Launch[];
	onSelect?: (launch: Launch) => void;
}

function LaunchTable({launches, onSelect}: LaunchTableProps) {
	return (
		<ClayTable>
			<ClayTable.Head>
				<ClayTable.Row>
					<ClayTable.Cell headingCell>
						{Liferay.Language.get('name')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('description')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('modified')}
					</ClayTable.Cell>

					<ClayTable.Cell headingCell>
						{Liferay.Language.get('status')}
					</ClayTable.Cell>
				</ClayTable.Row>
			</ClayTable.Head>

			<ClayTable.Body>
				{launches.map((launch) => (
					<ClayTable.Row
						key={launch.id}
						onClick={() => onSelect?.(launch)}
						style={{cursor: onSelect ? 'pointer' : 'default'}}
					>
						<ClayTable.Cell>
							<span className="text-truncate-inline">
								{launch.name}
							</span>
						</ClayTable.Cell>

						<ClayTable.Cell>
							{launch.description || '—'}
						</ClayTable.Cell>

						<ClayTable.Cell>
							{launch.dateModified
								? new Date(launch.dateModified).toLocaleString()
								: '—'}
						</ClayTable.Cell>

						<ClayTable.Cell>
							<StatusLabel status={launch.status?.code} />
						</ClayTable.Cell>
					</ClayTable.Row>
				))}
			</ClayTable.Body>
		</ClayTable>
	);
}
