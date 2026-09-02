/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import BulkUpdateWorkflowStateModalContent from '../../js/components/modal/BulkUpdateWorkflowStateModalContent';

jest.mock('@clayui/button', () => {
	const Button = ({children, ...props}: any) => (
		<button {...props}>{children}</button>
	);
	Button.Group = ({children}: any) => <div>{children}</div>;

	return {__esModule: true, default: Button};
});

jest.mock('@clayui/core', () => ({
	...(jest.requireActual('@clayui/core') as object),
	Option: ({children, ...props}: any) => (
		<option {...props}>{children}</option>
	),
	Picker: ({
		children,
		items,
		onSelectionChange,
		selectedKey,
		...props
	}: any) => (
		<select
			{...props}
			onChange={(event) => onSelectionChange(event.target.value)}
			value={selectedKey ?? ''}
		>
			<option value="" />

			{items.map((item: any) => children(item))}
		</select>
	),
}));

jest.mock('@clayui/modal', () => {
	const Modal = ({children}: any) => <div>{children}</div>;
	Modal.Body = ({children}: any) => <div>{children}</div>;
	Modal.Footer = ({last}: any) => <div>{last}</div>;
	Modal.Header = ({children}: any) => <div>{children}</div>;

	return {__esModule: true, default: Modal};
});

const mockOpenCMPModal = jest.fn();

jest.mock('../../js/utils/openCMPModal', () => ({
	openCMPModal: (...args: any[]) => mockOpenCMPModal(...args),
}));

let taskId = 0;

function renderModal(items: any[]) {
	return render(
		<BulkUpdateWorkflowStateModalContent
			closeModal={jest.fn()}
			getTaskURL={(item: any) => `/task/${(item as any).embedded.id}`}
			loadData={jest.fn()}
			selectedData={{items} as any}
		/>
	);
}

function submit() {
	fireEvent.submit(
		screen.getByText('update-state').closest('form') as HTMLFormElement
	);
}

function task({
	assetTitle,
	step = 'review',
	transitions = ['approve', 'reject'],
	workflowDefinitionName = 'Workflow A',
	workflowDefinitionVersion = '1',
}: {
	assetTitle: string;
	step?: string;
	transitions?: string[];
	workflowDefinitionName?: string;
	workflowDefinitionVersion?: string;
}) {
	return {
		embedded: {
			actions: transitions.reduce(
				(actions, transition) => ({
					...actions,
					[`workflow_${transition}`]: {
						label: transition,
						name: transition,
					},
				}),
				{}
			),
			assignedToMe: true,
			id: ++taskId,
			label: step,
			name: step,
			objectReviewed: {assetTitle},
			workflowDefinitionName,
			workflowDefinitionVersion,
		},
	};
}

describe('BulkUpdateWorkflowStateModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		taskId = 0;
	});

	it('excludes a deselected task from the transitions it hands over', () => {
		renderModal([task({assetTitle: 'One'}), task({assetTitle: 'Two'})]);

		fireEvent.change(screen.getByRole('combobox'), {
			target: {value: 'approve'},
		});

		fireEvent.click(screen.getByRole('checkbox', {name: 'Two'}));

		submit();

		const [[{contentComponent}]] = mockOpenCMPModal.mock.calls;

		expect(
			contentComponent({closeModal: jest.fn()}).props.changeTransitions
		).toEqual([{transitionName: 'approve', workflowTaskId: 1}]);
	});

	it('follows the translation when the step comes first in the sentence', () => {
		const {get} = Liferay.Language;

		Liferay.Language.get = (key: string) =>
			key === 'transition-from-x-to' ? '{0} kara no sen-i' : key;

		try {
			const {container} = renderModal([task({assetTitle: 'One'})]);

			expect(
				container.querySelector(
					'.lfr-cmp__bulk-update-state-transition'
				)?.textContent
			).toContain('review kara no sen-i');
		}
		finally {
			Liferay.Language.get = get;
		}
	});

	it('groups the tasks by workflow and then by step', () => {
		renderModal([
			task({assetTitle: 'One', step: 'review'}),
			task({assetTitle: 'Two', step: 'publish'}),
		]);

		expect(screen.getAllByText('current-step-x')).toHaveLength(2);
		expect(screen.getAllByRole('combobox')).toHaveLength(2);
	});

	it('keeps the update state button disabled until a transition is chosen', () => {
		renderModal([task({assetTitle: 'One'})]);

		expect(screen.getByText('update-state')).toBeDisabled();

		fireEvent.change(screen.getByRole('combobox'), {
			target: {value: 'approve'},
		});

		expect(screen.getByText('update-state')).toBeEnabled();
	});

	it('leaves out the tasks that have no transitions left', () => {
		renderModal([
			task({assetTitle: 'Live'}),
			task({assetTitle: 'Finished', transitions: []}),
		]);

		expect(screen.getByText('Live')).toBeInTheDocument();
		expect(screen.queryByText('Finished')).not.toBeInTheDocument();
	});

	it('links every task to its own workflow task', () => {
		renderModal([task({assetTitle: 'One'}), task({assetTitle: 'Two'})]);

		expect(screen.getByText('One').closest('a')).toHaveAttribute(
			'href',
			'/task/1'
		);
		expect(screen.getByText('Two').closest('a')).toHaveAttribute(
			'href',
			'/task/2'
		);
	});

	it('sorts the groups by workflow name and then by version', () => {
		renderModal([
			task({
				assetTitle: 'A2',
				workflowDefinitionName: 'Workflow A',
				workflowDefinitionVersion: '2',
			}),
			task({assetTitle: 'B1', workflowDefinitionName: 'Workflow B'}),
			task({
				assetTitle: 'A10',
				workflowDefinitionName: 'Workflow A',
				workflowDefinitionVersion: '10',
			}),
			task({
				assetTitle: 'A1',
				workflowDefinitionName: 'Workflow A',
				workflowDefinitionVersion: '1',
			}),
		]);

		expect(
			screen.getAllByText(/Workflow [AB]/).map((node) => node.textContent)
		).toEqual(['Workflow A', 'Workflow A', 'Workflow A', 'Workflow B']);

		expect(
			screen
				.getAllByRole('link')
				.map((node) => node.textContent?.split('(')[0])
		).toEqual(['A1', 'A2', 'A10', 'B1']);
	});
});
