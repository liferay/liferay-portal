/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

import AttributesSidebar from '../../../src/main/resources/META-INF/resources/js/components/AttributesSidebar';
import {AudiencesCriteriaType} from '../../../src/main/resources/META-INF/resources/js/types';

const DragAndDropProvider = DndProvider as unknown as React.FC<
	React.PropsWithChildren<{backend: typeof HTML5Backend}>
>;

const AUDIENCES_CRITERIA_TYPES: AudiencesCriteriaType[] = [
	{
		audiencesCriterias: [
			{
				icon: 'user',
				inputType: 'text',
				key: 'age',
				label: 'Age',
				options: [],
				type: 'number',
			},
			{
				icon: 'user',
				inputType: 'text',
				key: 'city',
				label: 'City',
				options: [],
				type: 'string',
			},
		],
		key: 'user',
		label: 'User',
	},
	{
		audiencesCriterias: [
			{
				icon: 'globe',
				inputType: 'text',
				key: 'browser',
				label: 'Browser',
				options: [],
				type: 'string',
			},
		],
		key: 'session',
		label: 'Session',
	},
];

describe('AttributesSidebar', () => {
	it('lists and filters the attributes', async () => {
		render(
			<DragAndDropProvider backend={HTML5Backend}>
				<AttributesSidebar
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
				/>
			</DragAndDropProvider>
		);

		expect(screen.getByText('Age')).toBeTruthy();
		expect(screen.getByText('City')).toBeTruthy();
		expect(screen.queryByText('Browser')).toBeNull();

		await userEvent.selectOptions(
			screen.getByLabelText('attributes-types'),
			screen.getByRole('option', {name: 'Session'})
		);

		expect(screen.getByText('Browser')).toBeTruthy();
		expect(screen.queryByText('Age')).toBeNull();

		await userEvent.selectOptions(
			screen.getByLabelText('attributes-types'),
			screen.getByRole('option', {name: 'User'})
		);

		await userEvent.type(screen.getByLabelText('search-attributes'), 'cit');

		await waitFor(() => expect(screen.queryByText('Age')).toBeNull());

		expect(screen.getByText('City')).toBeTruthy();

		await userEvent.clear(screen.getByLabelText('search-attributes'));
		await userEvent.type(screen.getByLabelText('search-attributes'), 'zzz');

		await waitFor(() =>
			expect(screen.getByText('no-attributes-were-found')).toBeTruthy()
		);
	});

	it('moves the focus through the attributes with the arrow keys', async () => {
		render(
			<DragAndDropProvider backend={HTML5Backend}>
				<AttributesSidebar
					audiencesCriteriaTypes={AUDIENCES_CRITERIA_TYPES}
				/>
			</DragAndDropProvider>
		);

		const age = screen.getByText('Age').closest('[role="menuitem"]');
		const city = screen.getByText('City').closest('[role="menuitem"]');

		expect(age).toHaveAttribute('tabindex', '0');
		expect(city).toHaveAttribute('tabindex', '-1');

		await userEvent.click(age as HTMLElement);
		await userEvent.keyboard('{ArrowDown}');

		expect(city).toHaveFocus();
		expect(city).toHaveAttribute('tabindex', '0');
		expect(age).toHaveAttribute('tabindex', '-1');

		await userEvent.keyboard('{ArrowUp}');

		expect(age).toHaveFocus();
	});
});
