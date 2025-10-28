/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, waitFor} from '@testing-library/react';
import React from 'react';

import CriteriaRow from '../../../../src/main/resources/META-INF/resources/js/components/criteria_builder/CriteriaRow';
import {PROPERTY_TYPES} from '../../../../src/main/resources/META-INF/resources/js/utils/constants';

import '@testing-library/jest-dom';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

const connectDnd = jest.fn((element) => element);

describe('CriteriaRow', () => {
	it('renders', () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		const {asFragment} = render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.STRING,
						propertyName: 'test_prop',
						value: 'test_val',
					}}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={jest.fn()}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="user"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'test_prop',
							type: PROPERTY_TYPES.STRING,
						},
					]}
				/>
			</DndProvider>
		);

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders and inform there is an unknown property', () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		const {asFragment} = render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.STRING,
						propertyName: 'unknown_prop',
						value: 'test_val',
					}}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={jest.fn()}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="user"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'test_prop',
							type: PROPERTY_TYPES.STRING,
						},
					]}
				/>
			</DndProvider>
		);

		expect(asFragment()).toMatchSnapshot();
	});

	it('informs user of an unknown entity in edit mode', async () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		global.fetch = jest.fn(() =>
			Promise.resolve({
				json: () => Promise.resolve({}),
			})
		);

		const {getByDisplayValue, getByText} = render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.ID,
						propertyName: 'test_prop',
						unknownEntity: true,
						value: '1234',
					}}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={jest.fn()}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="user"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'test_prop',
							type: PROPERTY_TYPES.ID,
						},
					]}
				/>
			</DndProvider>
		);

		await waitFor(() =>
			expect(
				getByText('unknown-element-message-edit')
			).toBeInTheDocument()
		);

		expect(getByDisplayValue('1234')).toBeInTheDocument();
	});

	it('informs user of an unknown entity in view mode', async () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		global.fetch = jest.fn(() =>
			Promise.resolve({
				json: () => Promise.resolve({}),
			})
		);

		const {getByText} = render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.ID,
						propertyName: 'test_prop',
						unknownEntity: true,
						value: '1234',
					}}
					editing={false}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={jest.fn()}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="user"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'test_prop',
							type: PROPERTY_TYPES.ID,
						},
					]}
				/>
			</DndProvider>
		);

		await waitFor(() =>
			expect(
				getByText('unknown-element-message-view')
			).toBeInTheDocument()
		);

		expect(getByText('1234')).toBeInTheDocument();
	});

	it('informs user of warning on a context entity in view mode', async () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		global.fetch = jest.fn(() =>
			Promise.resolve({
				json: () => Promise.resolve({}),
			})
		);

		const {getByText} = render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.STRING,
						propertyName: 'warning_prop',
						value: 'value1',
					}}
					editing={false}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={jest.fn()}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="context"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'warning_prop',
							options: [
								{
									disabled: true,
									label: 'value1',
									value: 'value1',
								},
								{label: 'value2', value: 'value2'},
							],
							type: PROPERTY_TYPES.STRING,
						},
					]}
				/>
			</DndProvider>
		);

		await waitFor(() =>
			expect(
				getByText('criteria-warning-message-view')
			).toBeInTheDocument()
		);

		expect(getByText('value1')).toBeInTheDocument();
	});

	it('informs user of warning on a context entity in edit mode', async () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		global.fetch = jest.fn(() =>
			Promise.resolve({
				json: () => Promise.resolve({}),
			})
		);

		const {getByText} = render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.STRING,
						propertyName: 'warning_prop',
						value: 'value1',
					}}
					editing={true}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={jest.fn()}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="context"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'warning_prop',
							options: [
								{
									disabled: true,
									label: 'value1',
									value: 'value1',
								},
								{label: 'value2', value: 'value2'},
							],
							type: PROPERTY_TYPES.STRING,
						},
					]}
				/>
			</DndProvider>
		);

		await waitFor(() =>
			expect(
				getByText('criteria-warning-message-edit')
			).toBeInTheDocument()
		);

		expect(getByText('value1')).toBeInTheDocument();
	});

	it('reports change when it finds the name of an entity', async () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		global.fetch = jest.fn(() =>
			Promise.resolve({
				json: () =>
					Promise.resolve({
						fieldValueName: 'Known Entity',
					}),
			})
		);

		const onChangeMock = jest.fn(() => {});

		render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.ID,
						propertyName: 'test_prop',
						value: '1234',
					}}
					editing={false}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={onChangeMock}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="user"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'test_prop',
							type: PROPERTY_TYPES.ID,
						},
					]}
				/>
			</DndProvider>
		);

		await waitFor(() => expect(onChangeMock).toHaveBeenCalled());

		expect(onChangeMock).toHaveBeenCalledWith(
			expect.objectContaining({
				displayValue: 'Known Entity',
			})
		);
	});

	it('reports change when it cannot find an entity name', async () => {
		const OriginalCriteriaRow = CriteriaRow.DecoratedComponent;

		global.fetch = jest.fn(() =>
			Promise.resolve({
				json: () => Promise.resolve({}),
			})
		);

		const onChangeMock = jest.fn(() => {});

		render(
			<DndProvider backend={HTML5Backend}>
				<OriginalCriteriaRow
					connectDragPreview={connectDnd}
					connectDropTarget={connectDnd}
					criterion={{
						operatorName: PROPERTY_TYPES.ID,
						propertyName: 'test_prop',
						value: '1234',
					}}
					editing={false}
					groupId="group_01"
					index={0}
					onAdd={jest.fn()}
					onChange={onChangeMock}
					onDelete={jest.fn()}
					onMove={jest.fn()}
					propertyKey="user"
					supportedProperties={[
						{
							label: 'Test Property',
							name: 'test_prop',
							type: PROPERTY_TYPES.ID,
						},
					]}
				/>
			</DndProvider>
		);

		await waitFor(() => expect(onChangeMock).toHaveBeenCalled());

		expect(onChangeMock).toHaveBeenCalledWith(
			expect.objectContaining({
				displayValue: '1234',
				unknownEntity: true,
			})
		);
	});
});
