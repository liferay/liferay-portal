/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import ContentTypeModal, {
	ModalContent,
} from '../../../src/main/resources/META-INF/resources/js/components/ContentTypeModal';

const DEFAULT_PROPS = {
	mappingTypes: [
		{
			id: 'type-with-subtype',
			label: 'Type with subtype',
			subtypes: [
				{
					id: 'subtype',
					label: 'Subtype',
				},
			],
		},
		{
			id: 'type-without-subtype',
			label: 'Type without subtype',
			subtypes: [],
		},
	],
	namespace: 'namespace',
	type: 'edit',
};

const renderComponent = (props) => {
	const componentProps = {...DEFAULT_PROPS, ...props};

	return render(<ContentTypeModal {...componentProps} />);
};

describe('ContentTypeModal', () => {
	afterAll(() => {
		jest.useRealTimers();
	});

	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('shows warning message if specified', () => {
		renderComponent({warningMessage: 'warning-text'});

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.getByText('warning-text')).toBeInTheDocument();
	});

	it('shows specified modal title', () => {
		renderComponent({title: 'Modal Title'});

		act(() => {
			jest.runAllTimers();
		});

		const modalTitle = document.querySelector('.modal-title');

		expect(modalTitle.innerHTML).toBe('Modal Title');
	});

	it('shows the current type and subtype as default values', () => {
		renderComponent({
			selectedSubtype: 'subtype',
			selectedType: 'type-with-subtype',
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(
			screen.getByRole('option', {name: 'Type with subtype'}).selected
		).toBe(true);

		expect(screen.getByRole('option', {name: 'Subtype'}).selected).toBe(
			true
		);
	});

	it('does not show subtype selector when selected type has no subtypes', () => {
		renderComponent({
			selectedType: 'type-without-subtype',
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.queryByLabelText('subtype')).not.toBeInTheDocument();
	});

	it('does not show any default value on selects when creating a DPT', () => {
		renderComponent({
			type: 'create',
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(
			screen.getByRole('option', {name: '-- not-selected --'}).selected
		).toBe(true);
	});

	it('allows introducing a name when creating a DPT', () => {
		renderComponent({
			type: 'create',
		});

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.getByLabelText('name')).toBeInTheDocument();
	});

	it('does not allow introducing a name when changing content type', () => {
		renderComponent();

		act(() => {
			jest.runAllTimers();
		});

		expect(screen.queryByLabelText('name')).not.toBeInTheDocument();
	});
});

describe('ContentTypeModal Accessibility', () => {
	it('checks accesibility of modal content', async () => {
		const componentProps = {
			disableWarning: false,
			error: {other: 'error'},
			setError: jest.fn(),
			setWarningVisible: jest.fn(),
			warningMessage: 'warning message',
			warningVisible: true,
		};

		const {container} = render(<ModalContent {...componentProps} />);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
