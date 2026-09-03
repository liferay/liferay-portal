/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import CMSFilesItemSelectorModal, {
	CMSFile,
} from '../../js/Attachment/util/CMSFilesItemSelectorModal';

const SPACES_API_URL = '/o/headless-asset-library/v1.0/asset-libraries';

function mockCMSFileUploaderComponent() {
	return null;
}

let lastItemSelectorProps: any = null;

jest.mock('@liferay/frontend-js-item-selector-web', () => ({
	CMSFileUploaderComponent: mockCMSFileUploaderComponent,
	ItemSelectorModal: (props: any) => {
		lastItemSelectorProps = props;

		if (!props.open) {
			return null;
		}

		return (
			<div data-testid="item-selector">
				<span data-testid="api-url">{props.apiURL}</span>

				{props.breadcrumbs && (
					<nav data-testid="breadcrumbs">
						{props.breadcrumbs.map((crumb: any, i: number) => (
							<button
								data-testid={`breadcrumb-${i}`}
								key={i}
								onClick={crumb.onClick}
							>
								{crumb.label}
							</button>
						))}
					</nav>
				)}

				<button
					data-testid="simulate-space-click"
					onClick={() => {
						const spaceItem = {
							id: '42',
							name: 'Space A',
							siteId: '42',
						};
						const view = props.fdsProps?.views?.[0];
						const itemProps = view?.setItemComponentProps?.({
							item: spaceItem,
							props: {},
						});

						itemProps?.onClick?.();
					}}
				>
					Click Space
				</button>

				<button
					data-testid="simulate-folder-click"
					onClick={() => {
						const folderItem = {
							embedded: {
								id: 'folder-1',
								title: 'Folder A',
							},
							entryClassName:
								'com.liferay.object.model.ObjectEntryFolder',
						};
						const view = props.fdsProps?.views?.[0];
						const itemProps = view?.setItemComponentProps?.({
							item: folderItem,
							props: {},
						});

						itemProps?.onClick?.();
					}}
				>
					Click Folder
				</button>
			</div>
		);
	},
	getCMSItemSelectorFilters: jest.fn().mockReturnValue([]),
	getCMSItemSelectorGroupedFilters: jest.fn().mockReturnValue([]),
}));

jest.mock('uuid', () => ({v4: () => 'test-uuid'}));

(globalThis as any).Liferay = {
	Language: {
		get: (key: string) => key,
	},
	ThemeDisplay: {
		getPathContext: () => '',
		getSiteGroupId: () => '12345',
	},
};

const mockFile: CMSFile = {
	embedded: {
		file: {
			fileURL: 'http://example.com/file.png',
			id: 10,
			mimeType: 'image/png',
		},
		id: 10,
		title: 'file.png',
	},
	id: 10,
	title: 'file.png',
};

const defaultProps = {
	items: [] as CMSFile[],
	observer: {},
	onItemsChange: jest.fn(),
	onOpenChange: jest.fn(),
};

afterEach(() => {
	jest.clearAllMocks();
	lastItemSelectorProps = null;
});

describe('CMSFilesItemSelectorModal — initial state', () => {
	it('passes SPACES_API_URL when opening for the first time', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		expect(screen.getByTestId('api-url')).toHaveTextContent(SPACES_API_URL);
	});

	it('does not render modal content when closed', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={false} />);

		expect(screen.queryByTestId('item-selector')).not.toBeInTheDocument();
	});

	it('shows no breadcrumbs when in space list view', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		expect(screen.queryByTestId('breadcrumbs')).not.toBeInTheDocument();
	});
});

describe('CMSFilesItemSelectorModal — space navigation', () => {
	it('switches to space files URL after clicking a space card', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		fireEvent.click(screen.getByTestId('simulate-space-click'));

		expect(screen.getByTestId('api-url').textContent).not.toBe(
			SPACES_API_URL
		);
		expect(screen.getByTestId('api-url').textContent).toContain(
			'scopeGroupId+eq+42'
		);
	});

	it('shows breadcrumbs after navigating into a space', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		fireEvent.click(screen.getByTestId('simulate-space-click'));

		expect(screen.getByTestId('breadcrumbs')).toBeInTheDocument();
		expect(screen.getByTestId('breadcrumb-0')).toHaveTextContent('spaces');
		expect(screen.getByTestId('breadcrumb-1')).toHaveTextContent('Space A');
	});

	it('returns to space list when the Spaces breadcrumb is clicked', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		fireEvent.click(screen.getByTestId('simulate-space-click'));

		expect(screen.getByTestId('api-url').textContent).not.toBe(
			SPACES_API_URL
		);

		fireEvent.click(screen.getByTestId('breadcrumb-0'));

		expect(screen.getByTestId('api-url')).toHaveTextContent(SPACES_API_URL);
		expect(screen.queryByTestId('breadcrumbs')).not.toBeInTheDocument();
	});
});

describe('CMSFilesItemSelectorModal — expired content filtering', () => {
	it('excludes non-approved and expired content from the space root URL', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		fireEvent.click(screen.getByTestId('simulate-space-click'));

		const apiURL = screen.getByTestId('api-url').textContent ?? '';

		expect(apiURL).toContain('status+in+%280%29');
		expect(apiURL).toContain(
			'dateExpiration+eq+null+or+dateExpiration+gt+'
		);
	});

	it('excludes non-approved and expired content from the child folder URL', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		fireEvent.click(screen.getByTestId('simulate-space-click'));
		fireEvent.click(screen.getByTestId('simulate-folder-click'));

		const apiURL = screen.getByTestId('api-url').textContent ?? '';

		expect(apiURL).toContain('folderId+eq+folder-1');
		expect(apiURL).toContain('status+in+%280%29');
		expect(apiURL).toContain(
			'dateExpiration+eq+null+or+dateExpiration+gt+'
		);
	});
});

describe('CMSFilesItemSelectorModal — open/close state management', () => {
	it('resets to space list when reopened with no selected items', () => {
		const {rerender} = render(
			<CMSFilesItemSelectorModal
				{...defaultProps}
				items={[]}
				open={true}
			/>
		);

		fireEvent.click(screen.getByTestId('simulate-space-click'));

		expect(screen.getByTestId('api-url').textContent).not.toBe(
			SPACES_API_URL
		);

		rerender(
			<CMSFilesItemSelectorModal
				{...defaultProps}
				items={[]}
				open={false}
			/>
		);

		act(() => {
			rerender(
				<CMSFilesItemSelectorModal
					{...defaultProps}
					items={[]}
					open={true}
				/>
			);
		});

		expect(screen.getByTestId('api-url')).toHaveTextContent(SPACES_API_URL);
	});

	it('does not reset when reopened with selected items', () => {
		const {rerender} = render(
			<CMSFilesItemSelectorModal
				{...defaultProps}
				items={[]}
				open={true}
			/>
		);

		fireEvent.click(screen.getByTestId('simulate-space-click'));

		const urlAfterNavigation =
			screen.getByTestId('api-url').textContent ?? '';

		rerender(
			<CMSFilesItemSelectorModal
				{...defaultProps}
				items={[mockFile]}
				open={false}
			/>
		);

		act(() => {
			rerender(
				<CMSFilesItemSelectorModal
					{...defaultProps}
					items={[mockFile]}
					open={true}
				/>
			);
		});

		expect(screen.getByTestId('api-url').textContent).toBe(
			urlAfterNavigation
		);
	});
});

describe('CMSFilesItemSelectorModal — upload integration', () => {
	it('enables the uploader when in space list view', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		expect(lastItemSelectorProps?.filesUploaderComponent).toBe(
			mockCMSFileUploaderComponent
		);
		expect(lastItemSelectorProps?.groupId).toBeUndefined();
	});

	it('passes CMSFileUploaderComponent and groupId after navigating into a space', () => {
		render(<CMSFilesItemSelectorModal {...defaultProps} open={true} />);

		fireEvent.click(screen.getByTestId('simulate-space-click'));

		expect(lastItemSelectorProps?.filesUploaderComponent).toBe(
			mockCMSFileUploaderComponent
		);
		expect(lastItemSelectorProps?.groupId).toBe(42);
	});
});
