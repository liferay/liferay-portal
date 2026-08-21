/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import CreateTaskModal from '../../js/components/modal/CreateTaskModal';

const mockGetAllProjects = jest.fn();
const mockGetAllStates = jest.fn();
const mockPostTaskByScope = jest.fn();

jest.mock('@clayui/button', () => {
	const Button = ({children, ...props}: any) => (
		<button {...props}>{children}</button>
	);
	Button.Group = ({children}: any) => <div>{children}</div>;

	return {
		__esModule: true,
		default: Button,
	};
});

jest.mock('@clayui/form', () => ({
	__esModule: true,
	default: ({children, ...props}: any) => <form {...props}>{children}</form>,
}));

jest.mock('@clayui/modal', () => {
	const Modal = ({children}: any) => <div>{children}</div>;
	Modal.Header = ({children}: any) => <div>{children}</div>;
	Modal.Body = ({children}: any) => <div>{children}</div>;
	Modal.Footer = ({last}: any) => <div>{last}</div>;

	return {
		__esModule: true,
		default: Modal,
	};
});

jest.mock('@liferay/site-cms-site-initializer', () => ({
	FieldPicker: ({
		disabled,
		id,
		items,
		label,
		onSelectionChange,
		selectedKey,
	}: any) => (
		<div>
			<label htmlFor={id}>{label}</label>

			<select
				disabled={disabled}
				id={id}
				onChange={(event) => onSelectionChange(event.target.value)}
				value={selectedKey}
			>
				<option value="0">Select Project</option>

				{items.map(({label, value}: any) => (
					<option key={value} value={value}>
						{label}
					</option>
				))}
			</select>
		</div>
	),
	FieldText: () => {},
	FieldWrapper: ({children, label}: any) => (
		<div>
			<label>{label}</label>

			{children}
		</div>
	),
	displayCreateSuccessToast: () => {},
	displayErrorToast: () => {},
	required: () => {},
	validate: () => {},
}));

jest.mock('../../js/utils/api', () => ({
	getAllProjects: (...args: any[]) => mockGetAllProjects(...args),
	getAllStates: (...args: any[]) => mockGetAllStates(...args),
	postTaskByScope: (...args: any[]) => mockPostTaskByScope(...args),
}));

jest.mock('../../js/components/StateSelector', () => ({
	__esModule: true,
	default: ({initialSelectedKey, onChange}: any) => (
		<input
			data-testid="state-selector"
			onChange={(event) => onChange(event.target.value)}
			value={initialSelectedKey}
		/>
	),
}));

jest.mock('../../js/components/CustomAssignee', () => {
	const {useState} = jest.requireActual('react');

	const CustomAssignee = ({
		cmpProjectObjectEntryId,
		onChange,
		readOnly,
		value: initialValue,
	}: any) => {
		const [value, setValue] = useState(initialValue);

		return (
			<input
				data-cmp-project-object-entry-id={cmpProjectObjectEntryId}
				data-testid="custom-assignee"
				onChange={(event) => {
					setValue({name: event.target.value});
					onChange({name: event.target.value});
				}}
				readOnly={readOnly}
				value={value?.name || ''}
			/>
		);
	};

	return {
		__esModule: true,
		default: CustomAssignee,
	};
});

jest.mock('@liferay/object-js-components-web', () => ({
	DatePicker: () => {},
}));

describe('CreateTaskModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockGetAllProjects.mockResolvedValue({
			data: {
				items: [
					{
						embedded: {
							id: 1,
							scopeKey: 'scope-1',
							title: 'Project 1',
						},
					},
					{
						embedded: {
							id: 2,
							scopeKey: 'scope-2',
							title: 'Project 2',
						},
					},
				],
			},
		});

		mockGetAllStates.mockResolvedValue({
			data: {
				items: [{key: 'in-progress', name: 'In Progress'}],
			},
		});

		mockPostTaskByScope.mockResolvedValue({
			data: {id: 42, title: 'New Task'},
			error: null,
		});
	});

	const renderModal = (
		cmpProjectObjectEntryId?: string,
		props: Partial<React.ComponentProps<typeof CreateTaskModal>> = {}
	) =>
		render(
			<CreateTaskModal
				closeModal={() => {}}
				cmpProjectObjectDefinitionId={123}
				cmpProjectObjectEntryId={cmpProjectObjectEntryId}
				loadData={() => {}}
				state=""
				{...props}
			/>
		);

	it('disables the assignee field until a project is selected', async () => {
		const {getByLabelText, getByRole, getByTestId} = renderModal();

		await waitFor(() => {
			expect(getByRole('option', {name: 'Project 1'})).toBeVisible();
		});

		expect(getByTestId('custom-assignee')).toHaveAttribute('readonly');

		fireEvent.change(getByLabelText('project'), {target: {value: '1'}});

		await waitFor(() => {
			expect(getByTestId('custom-assignee')).not.toHaveAttribute(
				'readonly'
			);
		});

		expect(getByTestId('custom-assignee')).toHaveAttribute(
			'data-cmp-project-object-entry-id',
			'1'
		);
	});

	it('disables the project picker and uses the provided projectId as the initial value', async () => {
		const {getByLabelText} = renderModal('1');

		await waitFor(() => {
			const projectPicker = getByLabelText(
				'project'
			) as HTMLSelectElement;

			expect(projectPicker).toBeDisabled();
			expect(projectPicker.value).toBe('1');
		});
	});

	it('enables the project picker and applies a default value when no projectId is provided', async () => {
		const {getByLabelText} = renderModal();

		await waitFor(() => {
			const projectPicker = getByLabelText(
				'project'
			) as HTMLSelectElement;

			expect(projectPicker).not.toBeDisabled();
			expect(projectPicker.value).toBe('0');
		});
	});

	it('inserts the created task into the data set instead of reloading when onItemsChange is provided', async () => {
		const loadData = jest.fn();
		const onItemsChange = jest.fn();

		const {getByLabelText, getByText} = renderModal('1', {
			loadData,
			onItemsChange,
		});

		await waitFor(() => {
			expect(getByLabelText('project')).toBeDisabled();
		});

		fireEvent.click(getByText('save'));

		await waitFor(() => {
			expect(onItemsChange).toHaveBeenCalledWith({
				itemKey: 'embedded.id',
				items: [{embedded: {id: 42, title: 'New Task'}}],
			});
		});

		expect(loadData).not.toHaveBeenCalled();
	});

	it('reloads the data set when onItemsChange is not provided', async () => {
		const loadData = jest.fn();

		const {getByLabelText, getByText} = renderModal('1', {loadData});

		await waitFor(() => {
			expect(getByLabelText('project')).toBeDisabled();
		});

		fireEvent.click(getByText('save'));

		await waitFor(() => {
			expect(loadData).toHaveBeenCalled();
		});
	});

	it('remounts the assignee field cleared and scoped to the newly selected project', async () => {
		const {getByLabelText, getByRole, getByTestId} = renderModal();

		await waitFor(() => {
			expect(getByRole('option', {name: 'Project 1'})).toBeVisible();
		});

		fireEvent.change(getByLabelText('project'), {target: {value: '1'}});

		fireEvent.change(getByTestId('custom-assignee'), {
			target: {value: 'Test Assignee'},
		});

		expect(getByTestId('custom-assignee')).toHaveValue('Test Assignee');

		fireEvent.change(getByLabelText('project'), {target: {value: '2'}});

		expect(getByTestId('custom-assignee')).toHaveValue('');
		expect(getByTestId('custom-assignee')).toHaveAttribute(
			'data-cmp-project-object-entry-id',
			'2'
		);
	});
});
