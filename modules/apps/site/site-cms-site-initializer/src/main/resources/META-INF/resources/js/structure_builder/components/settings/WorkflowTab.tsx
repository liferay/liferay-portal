/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayForm, {ClaySelectWithOption} from '@clayui/form';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import ClayPanel from '@clayui/panel';
import ClayTable from '@clayui/table';
import {SearchForm} from '@liferay/layout-js-components-web';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useState} from 'react';

import {Space} from '../../../common/types/Space';
import {Workflow} from '../../../common/types/Workflow';
import {useCache} from '../../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../../contexts/StateContext';
import selectStructureSpaces from '../../selectors/selectStructureSpaces';
import selectStructureWorkflows from '../../selectors/selectStructureWorkflows';

export default function WorkflowTab({disabled = false}: {disabled?: boolean}) {
	const {data: spaces, status: spacesStatus} = useCache('spaces');
	const {data: workflows, status: workflowsStatus} = useCache('workflows');

	const structureSpaces = useSelector(selectStructureSpaces);

	if (spacesStatus === 'saving' || workflowsStatus === 'saving') {
		return <ClayLoadingIndicator className="my-6" />;
	}

	const availableSpaces =
		structureSpaces === 'all'
			? spaces
			: spaces.filter((space) =>
					structureSpaces.includes(space.externalReferenceCode)
				);

	return (
		<div>
			<p className="text-secondary">
				{Liferay.Language.get(
					'set-the-default-workflow-for-entries-created-with-this-content-structure'
				)}
			</p>

			<DefaultWorkflowSelector
				disabled={disabled}
				workflows={workflows}
			/>

			<SpaceWorkflowPanel
				disabled={disabled}
				spaces={availableSpaces}
				workflows={workflows}
			/>
		</div>
	);
}

function DefaultWorkflowSelector({
	disabled,
	workflows,
}: {
	disabled: boolean;
	workflows: Workflow[];
}) {
	const dispatch = useStateDispatch();

	const structureWorkflows = useSelector(selectStructureWorkflows);

	const selectId = useId();

	const options = [
		{label: Liferay.Language.get('no-workflow'), value: ''},
		...workflows.map((workflow) => ({
			label: workflow.name,
			value: workflow.name,
		})),
	];

	return (
		<ClayForm.Group className="mb-5">
			<label htmlFor={selectId}>
				{Liferay.Language.get('default-workflow')}
			</label>

			<ClaySelectWithOption
				disabled={disabled}
				id={selectId}
				onChange={(event) =>
					dispatch({
						name: event.target.value,
						type: 'set-workflow',
					})
				}
				options={options}
				value={structureWorkflows['']}
			/>
		</ClayForm.Group>
	);
}

function SpaceWorkflowPanel({
	disabled,
	spaces,
	workflows,
}: {
	disabled: boolean;
	spaces: Space[];
	workflows: Workflow[];
}) {
	const [search, setSearch] = useState('');

	return (
		<ClayPanel
			collapsable
			defaultExpanded={true}
			displayTitle={
				<ClayPanel.Title className="panel-title text-secondary">
					{Liferay.Language.get('workflow-per-space')}
				</ClayPanel.Title>
			}
			displayType="unstyled"
			showCollapseIcon={true}
		>
			<ClayPanel.Body className="pt-4">
				<p className="mb-4 text-secondary">
					{Liferay.Language.get(
						'assign-a-specific-workflow-to-each-space-for-entries-created-with-this-content-structure'
					)}
				</p>

				<SearchForm
					className="mb-3"
					label={Liferay.Language.get('search-space')}
					onChange={setSearch}
					placeholder={Liferay.Language.get('search-space')}
				/>

				<ClayTable
					borderedColumns={true}
					responsive={true}
					striped={false}
				>
					<ClayTable.Head>
						<ClayTable.Cell className="font-weight-semi-bold text-secondary">
							{Liferay.Language.get('space')}
						</ClayTable.Cell>

						<ClayTable.Cell className="font-weight-semi-bold text-secondary">
							{Liferay.Language.get('workflow')}
						</ClayTable.Cell>
					</ClayTable.Head>

					<ClayTable.Body>
						{spaces
							.filter(
								(space) =>
									!search || space.name.includes(search)
							)
							.map((space) => (
								<ClayTable.Row key={space.id}>
									<ClayTable.Cell className="font-weight-semi-bold">
										{space.name}
									</ClayTable.Cell>

									<ClayTable.Cell>
										<SpaceWorkflowSelector
											disabled={disabled}
											spaceERC={
												space.externalReferenceCode
											}
											workflows={workflows}
										/>
									</ClayTable.Cell>
								</ClayTable.Row>
							))}
					</ClayTable.Body>
				</ClayTable>
			</ClayPanel.Body>
		</ClayPanel>
	);
}

function SpaceWorkflowSelector({
	disabled,
	spaceERC,
	workflows,
}: {
	disabled: boolean;
	spaceERC: Space['externalReferenceCode'];
	workflows: Workflow[];
}) {
	const dispatch = useStateDispatch();

	const structureWorkflows = useSelector(selectStructureWorkflows);

	const defaultWorkflow =
		structureWorkflows[''] || Liferay.Language.get('no-workflow');

	const options = [
		{
			label: sub(Liferay.Language.get('default-x'), defaultWorkflow),
			value: '',
		},
		...workflows.map((workflow) => ({
			label: workflow.name,
			value: workflow.name,
		})),
	];

	return (
		<ClayForm.Group className="mb-0">
			<ClaySelectWithOption
				aria-label={Liferay.Language.get('select-workflow')}
				disabled={disabled}
				onChange={(event) =>
					dispatch({
						name: event.target.value,
						spaceERC,
						type: 'set-workflow',
					})
				}
				options={options}
				sizing="sm"
				value={structureWorkflows[spaceERC]}
			/>
		</ClayForm.Group>
	);
}
