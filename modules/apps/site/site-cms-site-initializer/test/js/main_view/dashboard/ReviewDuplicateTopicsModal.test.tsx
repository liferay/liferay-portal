/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';
import ReviewDuplicateTopicsModal, {
	DuplicateTopicsList,
} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ReviewDuplicateTopicsModal';
import {mockNavigate} from '../../__mocks__/frontend-js-web';

const mockFrontendDataSet = jest.fn();

jest.mock('@liferay/frontend-data-set-web', () => ({
	...(jest.requireActual('@liferay/frontend-data-set-web') as any),
	FrontendDataSet: (props: any) => mockFrontendDataSet(props),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService'
);

const mockedGovernanceService = GovernanceService as jest.Mocked<
	typeof GovernanceService
>;

const ENTRY_CLASS_NAMES = 'com.liferay.object.model.ObjectDefinition#H4T4';

const TITLES = [
	{frequency: 2, term: 'brand marketing guidelines'},
	{frequency: 2, term: 'product feature summary'},
];

const ITEMS_ACTIONS = [
	{
		data: {id: 'edit'},
		href: '/c/cms/edit_content_item?objectEntryId={embedded.id}',
		icon: 'pencil',
		label: 'edit',
	},
];

const ASSETS = [
	{
		dateModified: '2024-03-17T14:53:00Z',
		embedded: {id: 101},
		entryClassName: ENTRY_CLASS_NAMES,
		title: 'Brand Marketing Guidelines',
	},
	{
		dateModified: '2024-05-21T14:53:00Z',
		embedded: {id: 102},
		entryClassName: ENTRY_CLASS_NAMES,
		title: 'Brand Marketing Guidelines',
	},
	{
		dateModified: '2024-03-11T14:53:00Z',
		embedded: {id: 103},
		entryClassName: ENTRY_CLASS_NAMES,
		title: 'Product Feature Summary',
	},
];

describe('[CMS Dashboard] ReviewDuplicateTopicsModal', () => {
	const closeModal = jest.fn();

	beforeEach(() => {
		jest.clearAllMocks();

		mockedGovernanceService.getDuplicateTitles.mockResolvedValue(TITLES);
	});

	function renderComponent() {
		return render(
			<ReviewDuplicateTopicsModal
				closeModal={closeModal}
				entryClassNames={ENTRY_CLASS_NAMES}
			/>
		);
	}

	it('keeps the assets of a group together across pages', async () => {
		renderComponent();

		await waitFor(() => expect(mockFrontendDataSet).toHaveBeenCalled());

		expect(mockFrontendDataSet.mock.calls[0][0].sorts).toEqual([
			{direction: 'asc', key: 'title', label: 'title'},
		]);
	});

	it('filters the search by the repeated titles', async () => {
		renderComponent();

		await waitFor(() => expect(mockFrontendDataSet).toHaveBeenCalled());

		const {additionalAPIURLParameters, apiURL} =
			mockFrontendDataSet.mock.calls[0][0];

		expect(
			new URL(apiURL, 'http://localhost').searchParams.get('filter')
		).toBe(
			"(title eq 'brand marketing guidelines' or title eq 'product feature summary')"
		);

		expect(additionalAPIURLParameters).toContain(
			`entryClassNames=${ENTRY_CLASS_NAMES}`
		);
	});

	it('filters by a title that holds a query string delimiter', async () => {
		mockedGovernanceService.getDuplicateTitles.mockResolvedValue([
			{frequency: 2, term: 'marketing & sales'},
		]);

		renderComponent();

		await waitFor(() => expect(mockFrontendDataSet).toHaveBeenCalled());

		const {apiURL} = mockFrontendDataSet.mock.calls[0][0];

		expect(
			new URL(apiURL, 'http://localhost').searchParams.get('filter')
		).toBe("(title eq 'marketing & sales')");
	});

	it('offers both a grouped list and a flat table', async () => {
		renderComponent();

		await waitFor(() => expect(mockFrontendDataSet).toHaveBeenCalled());

		const {views} = mockFrontendDataSet.mock.calls[0][0];

		expect(views.map(({name}: {name: string}) => name)).toEqual([
			'list',
			'table',
		]);

		expect(views[0].default).toBe(true);
	});

	it('reports that nothing repeats a title instead of an empty list', async () => {
		mockedGovernanceService.getDuplicateTitles.mockResolvedValue([]);

		renderComponent();

		expect(
			await screen.findByText('no-duplicated-topics-yet')
		).toBeInTheDocument();

		expect(mockFrontendDataSet).not.toHaveBeenCalled();
	});

	it('keeps the search out of the dashboard URL', async () => {
		renderComponent();

		await waitFor(() => expect(mockFrontendDataSet).toHaveBeenCalled());

		expect(mockFrontendDataSet.mock.calls[0][0].configInURLBehavior).toBe(
			'off'
		);
	});

	it('closes without changing anything', async () => {
		renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'cancel'}));

		expect(closeModal).toHaveBeenCalled();
	});
});

describe('[CMS Dashboard] DuplicateTopicsList', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('groups the assets under the title they repeat, with the size of the group', () => {
		render(
			<DuplicateTopicsList
				items={ASSETS}
				itemsActions={ITEMS_ACTIONS}
				titles={TITLES}
			/>
		);

		expect(
			screen.getByText('Brand Marketing Guidelines (2)')
		).toBeInTheDocument();

		expect(
			screen.getByText('Product Feature Summary (2)')
		).toBeInTheDocument();
	});

	it('edits the asset the action belongs to', async () => {
		render(
			<DuplicateTopicsList
				items={ASSETS}
				itemsActions={ITEMS_ACTIONS}
				titles={TITLES}
			/>
		);

		await userEvent.click(
			screen.getAllByRole('button', {name: 'edit-x'})[0]
		);

		expect(mockNavigate).toHaveBeenCalledWith(
			expect.stringContaining('/cms/edit_content_item?objectEntryId=101')
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<DuplicateTopicsList
				items={ASSETS}
				itemsActions={ITEMS_ACTIONS}
				titles={TITLES}
			/>
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
