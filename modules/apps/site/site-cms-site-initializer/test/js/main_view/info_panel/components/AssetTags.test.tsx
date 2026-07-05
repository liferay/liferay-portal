/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import AssetTags from '../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/components/AssetTags';

function MockItemSelector({
	apiURL,
	onChange,
	primaryAction,
}: {
	apiURL?: string;
	onChange: (value: string) => void;
	primaryAction?: {
		label: string;
		onClick: () => void;
	};
}) {
	return (
		<div data-api-url={apiURL} data-testid="item-selector">
			<input
				data-testid="item-selector-input"
				onChange={(event) => onChange(event.target.value)}
			/>

			{primaryAction && (
				<button
					data-testid="primary-action"
					onClick={primaryAction.onClick}
				>
					{primaryAction.label}
				</button>
			)}
		</div>
	);
}

function MockItemSelectorItem({children}: {children: React.ReactNode}) {
	return <div>{children}</div>;
}

jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper'
);

jest.mock('@liferay/frontend-js-item-selector-web', () => {
	return {
		ItemSelector: Object.assign(MockItemSelector, {
			Item: MockItemSelectorItem,
		}),
	};
});

function renderComponent({
	cmsGroupId = 456,
	collapsable,
	keywords = ['tag1'],
	scopeId = 123,
}: {
	cmsGroupId?: number;
	collapsable?: boolean;
	keywords?: string[];
	scopeId?: number;
} = {}) {
	return render(
		<AssetTags
			assetLibraryId={123}
			cmsGroupId={cmsGroupId}
			collapsable={collapsable}
			hasUpdatePermission={true}
			objectEntry={
				{
					keywords,
					scopeId,
				} as any
			}
			updateObjectEntry={jest.fn()}
		/>
	);
}

describe('AssetTags', () => {
	beforeEach(() => {
		(global as any).Liferay = {
			Language: {
				get: jest.fn((key: string) => key),
			},
			ThemeDisplay: {
				getPortalURL: () => 'https://www.liferay.com',
			},
		};

		(ApiHelper.get as jest.Mock).mockResolvedValue({
			data: {actions: {}},
			error: null,
		});
	});

	afterEach(() => {
		jest.resetAllMocks();
	});

	it('render primaryAction if hasCreatePermission is true and value is typed', async () => {
		(ApiHelper.get as jest.Mock).mockResolvedValue({
			data: {
				actions: {
					create: true,
				},
			},
			error: null,
		});

		renderComponent();

		await waitFor(() => expect(ApiHelper.get).toHaveBeenCalled());

		const input = screen.getByTestId('item-selector-input');

		fireEvent.change(input, {target: {value: 'new-tag'}});

		expect(screen.getByTestId('primary-action')).toBeInTheDocument();
		expect(screen.getByText('create-new-tag-x')).toBeInTheDocument();
	});

	it('do not render primaryAction if hasCreatePermission is false even if value is typed', async () => {
		(ApiHelper.get as jest.Mock).mockResolvedValue({
			data: {
				actions: {},
			},
			error: null,
		});

		renderComponent();

		await waitFor(() => expect(ApiHelper.get).toHaveBeenCalled());

		const input = screen.getByTestId('item-selector-input');

		fireEvent.change(input, {target: {value: 'new-tag'}});

		expect(screen.queryByTestId('primary-action')).not.toBeInTheDocument();
	});

	it('do not render primaryAction if the typed value is already in the keywords list', async () => {
		(ApiHelper.get as jest.Mock).mockResolvedValue({
			data: {
				actions: {
					create: true,
				},
			},
			error: null,
		});

		renderComponent();

		await waitFor(() => expect(ApiHelper.get).toHaveBeenCalled());

		const input = screen.getByTestId('item-selector-input');

		fireEvent.change(input, {target: {value: 'tag1'}});

		expect(screen.queryByTestId('primary-action')).not.toBeInTheDocument();
	});

	it('renders existing keywords as labels', () => {
		renderComponent({keywords: ['keyword-a', 'keyword-b']});

		expect(screen.getByText('keyword-a')).toBeInTheDocument();
		expect(screen.getByText('keyword-b')).toBeInTheDocument();
	});

	it('renders the panel as collapsable by default', () => {
		renderComponent();

		const toggle = screen.getByRole('button', {name: 'tags'});

		expect(toggle).toHaveAttribute('aria-expanded', 'true');
	});

	it('renders the panel as non-collapsable when collapsable is false', () => {
		renderComponent({collapsable: false});

		expect(
			screen.queryByRole('button', {name: 'tags'})
		).not.toBeInTheDocument();

		expect(screen.getByText('tags')).toBeInTheDocument();
	});

	it('builds the tags apiURL against the CMS group site', () => {
		renderComponent({cmsGroupId: 456, scopeId: 123});

		const apiURL = screen
			.getByTestId('item-selector')
			.getAttribute('data-api-url');

		expect(apiURL).toContain(
			'/o/headless-admin-taxonomy/v1.0/sites/456/keywords'
		);
		expect(apiURL).not.toContain('/sites/123/keywords');
		expect(apiURL).not.toContain('groupIds in');
	});

	it('uses the CMS group scope even for a negative asset scope', () => {
		renderComponent({cmsGroupId: 456, scopeId: -1});

		const apiURL = screen
			.getByTestId('item-selector')
			.getAttribute('data-api-url');

		expect(apiURL).toContain(
			'/o/headless-admin-taxonomy/v1.0/sites/456/keywords'
		);
		expect(apiURL).not.toContain('groupIds in');
	});

	it('fires the categorize event when the sparkle is clicked', () => {
		const fire = jest.fn();

		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};
		(global as any).Liferay.fire = fire;

		renderComponent({cmsGroupId: 456, scopeId: 123});

		fireEvent.click(
			screen.getByRole('button', {name: 'generate-tags-with-ai'})
		);

		expect(fire).toHaveBeenCalledWith(
			'cms:aiAssistant:categorize',
			expect.objectContaining({
				agent: 'L_GENERATE_TAGS',
				cmsGroupId: 456,
				scopeId: 123,
			})
		);
	});
});
