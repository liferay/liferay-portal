/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {FormWithControls} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items';
import ContainerWithControls from '../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/ContainerWithControls';
import {LAYOUT_DATA_ITEM_TYPES} from '../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/layoutDataItemTypes';
import {openInfoFieldSelector} from '../../../../../src/main/resources/META-INF/resources/page_editor/common/openInfoFieldSelector';
import StoreMother from '../../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/components/layout_data_items/ContainerWithControls',
	() =>
		jest.fn(({children}) => (
			<div className="ContainerWithControls">{children}</div>
		))
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/common/openInfoFieldSelector',
	() => ({
		openInfoFieldSelector: jest.fn(() => {}),
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/services/FormService',
	() => ({
		getFormFields: jest.fn(() => Promise.resolve({})),
	})
);

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			formTypes: [
				{
					isRestricted: false,
					label: 'Form Type 1',
					subtypes: [
						{
							label: 'Subtype',
							value: '11111',
						},
					],
					value: '11111',
				},
				{
					isRestricted: true,
					label: 'Form Type 2',
					subtypes: [
						{
							label: 'Subtype',
							value: '22222',
						},
					],
					value: '22222',
				},
				{
					className: '33333-className',
					label: 'Form Type 3',
					subtypes: [],
					value: '33333',
				},
			],
		},
	})
);

const DEFAULT_CONFIG = {classNameId: '0'};

describe('FormWithControls', () => {
	it('renders a container inside a form', () => {
		const {container} = render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: [],
						config: DEFAULT_CONFIG,
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		expect(container.querySelector('form')).toBeInTheDocument();
		expect(ContainerWithControls).toHaveBeenCalled();
	});

	it('shows mapping instructions by default', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: [],
						config: DEFAULT_CONFIG,
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		expect(
			screen.getByText('select-a-content-type-to-start-creating-the-form')
		).toBeInTheDocument();
	});

	it('shows empty state if it has no children', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: [],
						config: {
							classNameId: '11111',
							classTypeId: '11111',
						},
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		expect(
			screen.getByText('drag-and-drop-fragments-or-widgets-here')
		).toBeInTheDocument();
	});

	it('renders children inside container', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: ['child'],
						config: {
							classNameId: '11111',
							classTypeId: '11111',
						},
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				>
					Form Child
				</FormWithControls>
			</StoreMother.Component>
		);

		expect(screen.getByText('Form Child')).toBeInTheDocument();
	});

	it('ignores children if it is not mapped', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: ['child'],
						config: DEFAULT_CONFIG,
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				>
					Form Child
				</FormWithControls>
			</StoreMother.Component>
		);

		expect(screen.queryByText('Form Child')).not.toBeInTheDocument();
	});

	it('allows selecting content type if it is not mapped', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: [],
						config: {
							classNameId: '0',
							classTypeId: '0',
						},
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		expect(screen.getByLabelText('content-type')).toBeInTheDocument();
		expect(screen.getByText('map-your-form')).toBeInTheDocument();
	});

	it('only shows as options the form types that have permissions', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: [],
						config: {
							classNameId: '0',
							classTypeId: '0',
						},
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		expect(screen.getByText('Form Type 1')).toBeInTheDocument();
		expect(screen.queryByText('Form Type 2')).not.toBeInTheDocument();
	});

	it('shows a permission restriction message when the form type does not have permissions', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: ['fragment'],
						config: {
							classNameId: '22222',
							classTypeId: '0',
						},
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		expect(
			screen.getByText(
				'this-content-cannot-be-displayed-due-to-permission-restrictions'
			)
		).toBeInTheDocument();
	});

	it('shows a warning if it is mapped to an object that does not exist anymore', () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: [],
						config: {
							classNameId: '44444',
							classTypeId: '44444',
						},
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		expect(
			screen.getByText(
				'this-content-is-currently-unavailable-or-has-been-deleted.-users-cannot-see-this-fragment'
			)
		).toBeInTheDocument();
	});

	it('opens field selection modal with correct type when mapping the form', async () => {
		render(
			<StoreMother.Component>
				<FormWithControls
					item={{
						children: [],
						config: {
							classNameId: '0',
							classTypeId: '0',
						},
						itemId: 'form',
						type: LAYOUT_DATA_ITEM_TYPES.form,
					}}
				/>
			</StoreMother.Component>
		);

		const select = screen.getByLabelText('content-type');

		await userEvent.selectOptions(select, '33333');
		fireEvent.change(select);

		expect(openInfoFieldSelector).toBeCalledWith(
			expect.objectContaining({
				itemType: '33333-className',
			})
		);
	});
});
