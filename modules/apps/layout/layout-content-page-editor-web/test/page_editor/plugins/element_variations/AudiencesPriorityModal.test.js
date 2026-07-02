/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import AudiencesPriorityModal from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/AudiencesPriorityModal';

const AUDIENCES = [
	{label: 'VIP', value: 'vip'},
	{label: 'Audience 1', value: 'audience-1'},
	{label: 'Audience 3', value: 'audience-3'},
	{label: 'Audience 2', value: 'audience-2'},
];

function dispatchKey(element, eventType, key) {
	act(() => {
		element.dispatchEvent(
			new KeyboardEvent(eventType, {
				bubbles: true,
				key,
			})
		);
	});
}

const renderComponent = ({onClose = () => {}, onSave = () => {}} = {}) =>
	render(
		<AudiencesPriorityModal
			audiences={AUDIENCES}
			onClose={onClose}
			onSave={onSave}
		/>
	);

describe('AudiencesPriorityModal', () => {
	beforeAll(() => {
		Liferay.Language.direction = {en_US: 'ltr'};

		jest.useFakeTimers();
	});

	afterAll(() => {
		jest.useRealTimers();
	});

	it('renders the title and every audience', () => {
		renderComponent();

		act(() => jest.runAllTimers());

		expect(screen.getByText('audiences-priority')).toBeInTheDocument();
		expect(screen.getByText('VIP')).toBeInTheDocument();
		expect(screen.getByText('Audience 1')).toBeInTheDocument();
		expect(screen.getByText('Audience 2')).toBeInTheDocument();
		expect(screen.getByText('Audience 3')).toBeInTheDocument();
	});

	it('returns the new audience order on save', () => {
		const onSave = jest.fn();

		const {getByRole} = renderComponent({onSave});

		act(() => jest.runAllTimers());

		const reorderButton = getByRole('button', {name: /reorder vip/i});

		reorderButton.focus();

		dispatchKey(reorderButton, 'keyup', 'Enter');
		dispatchKey(reorderButton, 'keyup', 'ArrowDown');
		dispatchKey(reorderButton, 'keyup', 'ArrowDown');
		dispatchKey(reorderButton, 'keyup', 'Enter');

		fireEvent.click(screen.getByText('save'));

		expect(onSave).toHaveBeenCalledWith([
			{label: 'Audience 1', value: 'audience-1'},
			{label: 'VIP', value: 'vip'},
			{label: 'Audience 3', value: 'audience-3'},
			{label: 'Audience 2', value: 'audience-2'},
		]);
	});

	it('does not save when cancelled', () => {
		const onClose = jest.fn();
		const onSave = jest.fn();

		renderComponent({onClose, onSave});

		act(() => jest.runAllTimers());

		fireEvent.click(screen.getByText('cancel'));

		act(() => jest.runAllTimers());

		expect(onSave).not.toHaveBeenCalled();
		expect(onClose).toHaveBeenCalled();
	});
});
