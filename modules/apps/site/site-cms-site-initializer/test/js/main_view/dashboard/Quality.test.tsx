/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {FrontendDataSet} from '@liferay/frontend-data-set-web';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {openCMSModal} from '../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import {GovernanceContext} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceContext';
import {Quality} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/Quality';
import {getBrokenLinksFDSProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/BrokenLinksFDSPropsTransformer';
import {getMissingRequiredFieldsFDSProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/MissingRequiredFieldsFDSPropsTransformer';

jest.mock('frontend-js-components-web', () => ({
	...(jest.requireActual('frontend-js-components-web') as object),
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: (template: string, ...values: string[]) =>
		values.reduce(
			(result, value, index) => result.replace(`{${index}}`, value),
			template
		),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	FrontendDataSet: jest.fn(() => null),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent',
	() => ({__esModule: true, default: jest.fn(() => null)})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/BrokenLinksFDSPropsTransformer',
	() => ({getBrokenLinksFDSProps: jest.fn(() => ({}))})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/MissingRequiredFieldsFDSPropsTransformer',
	() => ({getMissingRequiredFieldsFDSProps: jest.fn(() => ({}))})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/getDashboardAssetListFDSProps',
	() => ({__esModule: true, default: jest.fn(() => ({}))})
);

const BROKEN_LINKS_FDS_ACTION_DROPDOWN_ITEMS = [
	{data: {id: 'edit'}, href: '/broken-links/edit/{id}'},
];

const MISSING_REQUIRED_FIELDS_FDS_ACTION_DROPDOWN_ITEMS = [
	{data: {id: 'edit'}, href: '/missing-required-data/edit/{id}'},
];

const ADDITIONAL_PROPS = {
	brokenLinksFDSActionDropdownItems: BROKEN_LINKS_FDS_ACTION_DROPDOWN_ITEMS,
	missingRequiredFieldsFDSActionDropdownItems:
		MISSING_REQUIRED_FIELDS_FDS_ACTION_DROPDOWN_ITEMS,
} as any;

const CARDS = [
	{
		apiPath: 'broken-link-assets',
		getFDSProps: getBrokenLinksFDSProps,
		itemsActions: BROKEN_LINKS_FDS_ACTION_DROPDOWN_ITEMS,
		percentage: '4.6% of 350 Assets',
		sectionPath: 'broken-links',
		title: 'broken-links',
		value: '16',
	},
	{
		apiPath: 'missing-required-fields-assets',
		getFDSProps: getMissingRequiredFieldsFDSProps,
		itemsActions: MISSING_REQUIRED_FIELDS_FDS_ACTION_DROPDOWN_ITEMS,
		percentage: '0.3% of 350 Assets',
		sectionPath: 'missing-required-data',
		title: 'missing-required-data',
		value: '1',
	},
];

const LANGUAGE_KEYS: Record<string, string> = {
	'showing-x-of-x-items': 'Showing {0} of {1} Items',
	'view-all-x': 'View All {0}',
	'x-of-x-assets': '{0} of {1} Assets',
};

function renderQuality({
	space = {value: 'all'},
	statistics = {
		brokenLinksCount: 16,
		missingRequiredFieldsCount: 1,
		totalCount: 350,
	},
}: {
	space?: {label?: string; siteId?: number; value: string};
	statistics?: {
		brokenLinksCount?: number;
		missingRequiredFieldsCount?: number;
		totalCount?: number;
	};
} = {}) {
	return render(
		<GovernanceContext.Provider
			value={
				{
					loadingStatistics: false,
					reloadStatistics: jest.fn(),
					space,
					statistics,
				} as any
			}
		>
			<Quality additionalProps={ADDITIONAL_PROPS} />
		</GovernanceContext.Provider>
	);
}

function getCard(title = 'broken-links') {
	return screen.getByRole('button', {name: new RegExp(`^${title} `)});
}

function getFDSPropsArguments(getFDSProps: unknown = getBrokenLinksFDSProps) {
	return (getFDSProps as jest.Mock).mock.calls[0][0];
}

async function clickViewAction(itemData: {id: number}, items: {id: number}[]) {
	renderQuality();

	await userEvent.click(getCard());

	const viewAction = getFDSPropsArguments().itemsActions.find(
		({data}: {data: {id: string}}) => data.id === 'view'
	);

	viewAction.onClick({itemData, items});
}

describe('[CMS Dashboard] Quality', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(Liferay.Language, 'get').mockImplementation(
			(key: string) => LANGUAGE_KEYS[key] ?? key
		);
	});

	it.each(CARDS)(
		'shows the $title count and its share of all assets',
		({percentage, title, value}) => {
			renderQuality();

			expect(getCard(title)).toHaveTextContent(value);
			expect(getCard(title)).toHaveTextContent(percentage);
		}
	);

	it('expands the broken links list on click and collapses it on the second click', async () => {
		renderQuality();

		expect(screen.queryByRole('region')).not.toBeInTheDocument();

		await userEvent.click(getCard());

		expect(getCard()).toHaveAttribute('aria-expanded', 'true');
		expect(
			screen.getByRole('region', {name: 'broken-links'})
		).toBeInTheDocument();
		expect(screen.getByText('Showing 8 of 16 Items')).toBeInTheDocument();

		await userEvent.click(getCard());

		expect(getCard()).toHaveAttribute('aria-expanded', 'false');
		expect(screen.queryByRole('region')).not.toBeInTheDocument();
	});

	it.each(CARDS)(
		'lists the $title assets of all spaces with their actions',
		async ({apiPath, getFDSProps, itemsActions, sectionPath, title}) => {
			renderQuality();

			await userEvent.click(getCard(title));

			expect(getFDSPropsArguments(getFDSProps)).toMatchObject({
				apiURL: `/o/headless-cms/v1.0/${apiPath}?pageSize=8&sort=title%3Aasc`,
				itemsActions: [
					...itemsActions,
					expect.objectContaining({
						data: {id: 'view'},
						onClick: expect.any(Function),
					}),
				],
				searchAsYouType: false,
				searchSuggestionsEnabled: false,
			});

			expect(
				screen.getByRole('link', {name: `View All ${title}`})
			).toHaveAttribute('href', `/web/cms/${sectionPath}`);
		}
	);

	it('replaces the open list with the list of the other card', async () => {
		renderQuality();

		await userEvent.click(getCard('broken-links'));
		await userEvent.click(getCard('missing-required-data'));

		expect(getCard('broken-links')).toHaveAttribute(
			'aria-expanded',
			'false'
		);
		expect(getCard('missing-required-data')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
		expect(screen.getAllByRole('region')).toHaveLength(1);
		expect(
			screen.getByRole('region', {name: 'missing-required-data'})
		).toBeInTheDocument();
	});

	it('shows the empty state of the missing required data list when no asset is missing a required field', async () => {
		renderQuality({
			statistics: {
				brokenLinksCount: 16,
				missingRequiredFieldsCount: 0,
				totalCount: 350,
			},
		});

		await userEvent.click(getCard('missing-required-data'));

		expect((FrontendDataSet as jest.Mock).mock.calls[0][0]).toMatchObject({
			emptyState: {
				description: 'there-is-no-missing-required-data',
				title: 'no-missing-required-data',
			},
		});
		expect(screen.queryByText(/Showing/)).not.toBeInTheDocument();
	});

	it('renders the broken link assets as the other lists of the dashboard', async () => {
		renderQuality();

		await userEvent.click(getCard());

		const {customRenderers, views} = (FrontendDataSet as jest.Mock).mock
			.calls[0][0];

		expect(customRenderers.tableCell[0].name).toBe('assetRenderer');
		expect(views[0]).toMatchObject({
			contentRenderer: 'table',
			schema: {
				fields: [
					{
						actionId: 'edit',
						contentRenderer: 'assetRenderer',
						fieldName: 'title',
					},
				],
			},
		});
	});

	it('opens the CMS view modal of the clicked asset among the listed ones', async () => {
		const getSpy = jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [
					{embedded: {id: 2, title: 'Second'}},
					{embedded: {id: 1, title: 'First'}},
				],
			},
			error: null,
		} as any);

		await clickViewAction({id: 2}, [{id: 1}, {id: 2}]);

		await waitFor(() => expect(openCMSModal).toHaveBeenCalled());

		const searchParams = new URL(
			getSpy.mock.calls[0][0] as string,
			'http://localhost'
		).searchParams;

		expect(searchParams.get('filter')).toBe(
			'entryClassPK eq 1 or entryClassPK eq 2'
		);

		const [{contentComponent}] = (openCMSModal as jest.Mock).mock.calls[0];

		contentComponent();

		expect(
			jest.requireMock(
				'../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent'
			).default
		).toHaveBeenCalledWith(
			expect.objectContaining({
				currentIndex: 1,
				items: [
					{embedded: {id: 1, title: 'First'}},
					{embedded: {id: 2, title: 'Second'}},
				],
			})
		);
	});

	it('shows the escaped error instead of the view modal when the assets cannot be fetched', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: null,
			error: '<b>Request failed</b>',
		} as any);

		await clickViewAction({id: 2}, [{id: 2}]);

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith({
				message: '&lt;b&gt;Request failed&lt;&#047;b&gt;',
				type: 'danger',
			})
		);

		expect(openCMSModal).not.toHaveBeenCalled();
	});

	it('shows an error instead of the view modal when the search does not return the asset', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: []},
			error: null,
		} as any);

		await clickViewAction({id: 2}, [{id: 2}]);

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith({
				message: 'an-unexpected-error-occurred',
				type: 'danger',
			})
		);

		expect(openCMSModal).not.toHaveBeenCalled();
	});

	it.each(CARDS)(
		'scopes the $title list and its view all link by the selected space',
		async ({apiPath, getFDSProps, sectionPath, title}) => {
			renderQuality({space: {siteId: 12345, value: '999'}});

			await userEvent.click(getCard(title));

			expect(getFDSPropsArguments(getFDSProps).apiURL).toBe(
				`/o/headless-cms/v1.0/${apiPath}?pageSize=8&sort=title%3Aasc&assetLibraryId=999`
			);

			expect(
				screen.getByRole('link', {name: `View All ${title}`})
			).toHaveAttribute('href', `/web/cms/${sectionPath}?groupId=12345`);
		}
	);

	it.each(CARDS)(
		'has no accessibility violations with the $title list expanded',
		async ({title}) => {
			const {container} = renderQuality();

			await userEvent.click(getCard(title));

			await checkAccessibility({bestPractices: true, context: container});
		}
	);
});
