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
	contentRawText,
	getContent,
	keywords = ['tag1'],
	scopeId = 123,
}: {
	cmsGroupId?: number;
	collapsable?: boolean;
	contentRawText?: string;
	getContent?: (
		objectDefinitionExternalReferenceCode?: string
	) => Promise<string>;
	keywords?: string[];
	scopeId?: number;
} = {}) {
	return render(
		<AssetTags
			assetLibraryId={123}
			cmsGroupId={cmsGroupId}
			collapsable={collapsable}
			getContent={getContent}
			hasUpdatePermission={true}
			objectEntry={
				{
					contentRawText,
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

	it('builds the tags apiURL against the cmsGroup site with a groupIds filter when the scope is negative', () => {
		renderComponent({cmsGroupId: 456, scopeId: -1});

		const apiURL = screen
			.getByTestId('item-selector')
			.getAttribute('data-api-url');

		expect(apiURL).toContain(
			'/o/headless-admin-taxonomy/v1.0/sites/456/keywords'
		);
		expect(apiURL).toContain("groupIds in ('-1')");
	});

	it('builds the tags apiURL against the scope site when the scope is positive', () => {
		renderComponent({scopeId: 123});

		const apiURL = screen
			.getByTestId('item-selector')
			.getAttribute('data-api-url');

		expect(apiURL).toContain(
			'/o/headless-admin-taxonomy/v1.0/sites/123/keywords'
		);
		expect(apiURL).not.toContain('groupIds in');
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

	it('does not render the generate tags button when there is no content source', () => {
		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};

		renderComponent();

		expect(
			screen.queryByRole('button', {name: 'generate-tags-with-ai'})
		).not.toBeInTheDocument();
	});

	it('falls back to the persisted content when getContent returns nothing', async () => {
		const fire = jest.fn();
		const getContent = jest.fn().mockResolvedValue('');

		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};
		(global as any).Liferay.fire = fire;

		renderComponent({contentRawText: 'persisted content', getContent});

		fireEvent.click(
			screen.getByRole('button', {name: 'generate-tags-with-ai'})
		);

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				'cms:aiAssistant:categorize',
				expect.objectContaining({content: 'persisted content'})
			)
		);
	});

	it('fires the categorize event when the generate tags button is clicked', async () => {
		const fire = jest.fn();

		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};
		(global as any).Liferay.fire = fire;

		renderComponent({
			cmsGroupId: 456,
			contentRawText: 'persisted content',
			scopeId: 123,
		});

		fireEvent.click(
			screen.getByRole('button', {name: 'generate-tags-with-ai'})
		);

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				'cms:aiAssistant:categorize',
				expect.objectContaining({
					agent: 'L_GENERATE_TAGS',
					cmsGroupId: 456,
					scopeId: 123,
				})
			)
		);
	});

	it('prefers the edited content from getContent over the persisted content', async () => {
		const fire = jest.fn();
		const getContent = jest.fn().mockResolvedValue('edited content');

		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};
		(global as any).Liferay.fire = fire;

		renderComponent({contentRawText: 'persisted content', getContent});

		fireEvent.click(
			screen.getByRole('button', {name: 'generate-tags-with-ai'})
		);

		await waitFor(() =>
			expect(fire).toHaveBeenCalledWith(
				'cms:aiAssistant:categorize',
				expect.objectContaining({content: 'edited content'})
			)
		);
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

	it('renders existing keywords as labels', () => {
		renderComponent({keywords: ['keyword-a', 'keyword-b']});

		expect(screen.getByText('keyword-a')).toBeInTheDocument();
		expect(screen.getByText('keyword-b')).toBeInTheDocument();
	});

	it('renders the generate tags button when only getContent supplies the content', () => {
		(global as any).Liferay.FeatureFlags = {'LPD-62272': true};

		renderComponent({
			getContent: jest.fn().mockResolvedValue('edited content'),
		});

		expect(
			screen.getByRole('button', {name: 'generate-tags-with-ai'})
		).toBeInTheDocument();
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
});
