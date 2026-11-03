/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';

import ApiHelper from '../../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {openCMSModal} from '../../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import AssetNavigationModalContent from '../../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent';
import {createAssetListFDSPropsBuilder} from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/utils/createAssetListFDSPropsBuilder';

jest.mock('frontend-js-components-web', () => ({
	...(jest.requireActual('frontend-js-components-web') as object),
	openToast: jest.fn(),
}));

jest.mock('@liferay/frontend-data-set-web', () => ({
	findAction: jest.fn(),
	replaceTokens: jest.fn(),
	useFDSRecordVisit: () => jest.fn(),
}));

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal',
	() => ({openCMSModal: jest.fn()})
);
jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/modal/asset_navigation_view/AssetNavigationModalContent',
	() => ({__esModule: true, default: jest.fn(() => null)})
);

const CONTENT_VIEW_URL = '/view-content';

const EDIT_ACTION = {data: {id: 'edit'}, href: '/edit/{id}'};

const getAssetListFDSProps = createAssetListFDSPropsBuilder<{
	id?: number;
	title?: string;
}>({
	renderSubtitle: () => null,
	titleRendererName: 'assetTitle',
});

function getItemsActions() {
	return getAssetListFDSProps({
		additionalProps: {contentViewURL: CONTENT_VIEW_URL},
		itemsActions: [EDIT_ACTION],
	}).itemsActions as any[];
}

function clickViewAction(itemData: {id: number}, items: {id: number}[]) {
	const viewAction = getItemsActions().find(({data}) => data.id === 'view');

	viewAction.onClick({itemData, items});
}

describe('[CMS Asset Lists] createAssetListFDSPropsBuilder', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('appends the view action after the actions of the list', () => {
		expect(getItemsActions()).toEqual([
			EDIT_ACTION,
			expect.objectContaining({
				data: {id: 'view'},
				icon: 'view',
				label: 'view',
				onClick: expect.any(Function),
			}),
		]);
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

		clickViewAction({id: 2}, [{id: 1}, {id: 2}]);

		await waitFor(() => expect(openCMSModal).toHaveBeenCalled());

		expect(getSpy).toHaveBeenCalledWith(
			'/o/search/v1.0/search?emptySearch=true&filter=entryClassPK+eq+1+or+entryClassPK+eq+2&nestedFields=embedded%2CsystemProperties.objectDefinitionBrief&pageSize=2'
		);

		const [{contentComponent, size}] = (openCMSModal as jest.Mock).mock
			.calls[0];

		expect(size).toBe('full-screen');

		contentComponent();

		expect(AssetNavigationModalContent).toHaveBeenCalledWith(
			expect.objectContaining({
				contentViewURL: CONTENT_VIEW_URL,
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

		clickViewAction({id: 2}, [{id: 2}]);

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

		clickViewAction({id: 2}, [{id: 2}]);

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith({
				message: 'an-unexpected-error-occurred',
				type: 'danger',
			})
		);

		expect(openCMSModal).not.toHaveBeenCalled();
	});
});
