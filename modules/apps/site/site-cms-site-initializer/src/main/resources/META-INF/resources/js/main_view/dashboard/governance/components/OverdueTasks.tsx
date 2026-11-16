/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {
	IInternalRenderer,
	findAction,
	replaceTokens,
} from '@liferay/frontend-data-set-web';
import {sub} from 'frontend-js-web';
import React, {useContext, useMemo} from 'react';

import {onWorkflowTaskActionClick} from '../../../tasks/onWorkflowTaskActionClick';
import {TASK_QUICK_FILTER_UPDATES} from '../../../tasks/taskQuickFilterUpdates';
import InteractiveCard from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import GovernanceService, {MILLISECONDS_PER_DAY} from '../GovernanceService';
import getSectionHref, {toFDSConfigFilters} from '../getSectionHref';
import {GovernanceAdditionalProps} from '../types';
import {
	GovernanceListPanel,
	getGovernanceAssetListPanelId,
	getGovernanceAssetListViews,
	useReloadStatisticsOnListRefresh,
} from './GovernanceAssetList';
import {PercentageOfTotal} from './PercentageOfTotal';

const LIST_ID = 'overdueTasks';

type OverdueTask = {
	embedded?: {
		assigneePerson?: {name: string};
		assigneeRoles?: Array<{name: string}>;
		dateDue?: string;
		label?: string;
		objectReviewed?: {assetTitle?: string};
	};
};

function getDescription() {
	return Liferay.Language.get(
		'these-are-workflow-tasks-that-have-passed-their-due-date'
	);
}

function getOverdueTaskSubtitle(task: OverdueTask) {
	const {
		assigneePerson,
		assigneeRoles = [],
		dateDue = '',
		label,
	} = task.embedded ?? {};

	const daysOverdue = Math.floor(
		(Date.now() - new Date(dateDue).getTime()) / MILLISECONDS_PER_DAY
	);

	return [
		label,
		assigneePerson?.name ??
			assigneeRoles.map((assigneeRole) => assigneeRole.name).join(', '),
		sub(
			daysOverdue === 1
				? Liferay.Language.get('x-day-overdue')
				: Liferay.Language.get('x-days-overdue'),
			daysOverdue
		),
	]
		.filter(Boolean)
		.join(' · ');
}

function OverdueTaskRenderer({
	actions,
	itemData,
}: {
	actions: Array<{data: {id: string}; href?: string}>;
	itemData: OverdueTask;
}) {
	const assetTitle = itemData.embedded?.objectReviewed?.assetTitle;
	const href = findAction(actions, 'actionLinkWorkflowTask')?.href;

	return (
		<div>
			<div className="table-list-title">
				{href ? (
					<ClayLink
						className="text-decoration-underline"
						data-senna-off
						href={replaceTokens(href, itemData)}
					>
						{assetTitle}
					</ClayLink>
				) : (
					assetTitle
				)}
			</div>

			<div className="text-3 text-secondary">
				{getOverdueTaskSubtitle(itemData)}
			</div>
		</div>
	);
}

export function OverdueTasksCard({
	expanded,
	onClick,
}: {
	expanded: boolean;
	onClick: () => void;
}) {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);

	const count = statistics?.overdueWorkflowTasksCount ?? 0;

	return (
		<InteractiveCard
			active={expanded}
			aria-controls={getGovernanceAssetListPanelId(LIST_ID)}
			aria-expanded={expanded}
			color="orange"
			description={getDescription()}
			icon="time"
			loading={loadingStatistics}
			onClick={onClick}
			title={Liferay.Language.get('overdue-tasks')}
			value={count}
		>
			<PercentageOfTotal
				count={count}
				template={Liferay.Language.get('x-of-x-workflow-tasks')}
				total={statistics?.workflowTasksCount ?? 0}
			/>
		</InteractiveCard>
	);
}

export function OverdueTasksList({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	const {selectedSpaceGroupIds, statistics} = useContext(GovernanceContext);

	const reloadStatistics = useReloadStatisticsOnListRefresh(LIST_ID);

	const {tasksSectionFDSName, workflowTaskFDSActionDropdownItems} =
		additionalProps;

	const fdsProps = useMemo(
		() => ({
			...(selectedSpaceGroupIds.length
				? {
						apiURL: GovernanceService.getOverdueTasksURL(
							selectedSpaceGroupIds
						),
					}
				: {items: []}),
			customRenderers: {
				tableCell: [
					{
						component: OverdueTaskRenderer,
						name: 'overdueTaskRenderer',
						type: 'internal',
					} as IInternalRenderer,
				],
			},
			itemsActions: workflowTaskFDSActionDropdownItems,
			onActionDropdownItemClick: ({
				loadData,
				...otherProps
			}: Parameters<typeof onWorkflowTaskActionClick>[0]) =>
				onWorkflowTaskActionClick({
					...otherProps,
					loadData: () => loadData().then(reloadStatistics),
				}),
			views: getGovernanceAssetListViews({
				actionId: 'actionLinkWorkflowTask',
				contentRenderer: 'overdueTaskRenderer',
				fieldName: 'embedded.objectReviewed.assetTitle',
			}),
		}),
		[
			reloadStatistics,
			selectedSpaceGroupIds,
			workflowTaskFDSActionDropdownItems,
		]
	);

	return (
		<GovernanceListPanel
			count={statistics?.overdueWorkflowTasksCount ?? 0}
			description={getDescription()}
			emptyStateDescription={Liferay.Language.get(
				'all-workflow-tasks-are-within-their-due-date'
			)}
			emptyStateTitle={Liferay.Language.get('no-overdue-tasks')}
			fdsProps={fdsProps}
			id={LIST_ID}
			title={Liferay.Language.get('overdue-tasks')}
			viewAllHref={getSectionHref('tasks', tasksSectionFDSName, {
				filters: toFDSConfigFilters(
					TASK_QUICK_FILTER_UPDATES.overdue()
				),
			})}
		/>
	);
}
