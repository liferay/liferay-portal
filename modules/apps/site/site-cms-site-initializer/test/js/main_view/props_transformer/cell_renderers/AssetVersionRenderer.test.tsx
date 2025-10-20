/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import {openModal} from 'frontend-js-components-web';
import React from 'react';

import formatActionURL from '../../../../../src/main/resources/META-INF/resources/js/common/utils/formatActionURL';
import FilePreviewerModalContent from '../../../../../src/main/resources/META-INF/resources/js/main_view/modal/FilePreviewerModalContent';
import AssetVersionRenderer from '../../../../../src/main/resources/META-INF/resources/js/main_view/props_transformer/cell_renderers/AssetVersionRenderer';

jest.mock('frontend-js-components-web', () => ({
	openModal: jest.fn(),
}));

const mockFilePreviewerModalContent = FilePreviewerModalContent as jest.Mock;
jest.mock(
	'../../../../../src/main/resources/META-INF/resources/js/main_view/modal/FilePreviewerModalContent',
	() => jest.fn()
);

const testIrrelevantAction = {
	data: {id: 'view'},
};

const testViewContentAction = {
	data: {id: 'view-content'},
	href: 'http://www.test.com',
};

const testViewContentActionWithoutHREF = {
	data: {id: 'view-content'},
};

const testContentProps = {
	actions: [testViewContentAction],
	itemData: {
		systemProperties: {
			version: {
				number: '1',
			},
		},
		title: 'Content Title',
	},
	value: 'Content Title',
};

const testContentPropsWithouValue = {
	actions: [],
	itemData: {
		systemProperties: {
			version: {
				number: '1',
			},
		},
		title: 'Content Title',
	},
	value: '',
};

const testFileProps = {
	actions: [],
	itemData: {
		file: {
			thumbnailURL: '/thumbs/image1?version=1.0&imageThumbnail=1',
		},
		systemProperties: {
			version: {
				number: '1',
			},
		},
		title: 'File Title',
	},
	value: 'File Title',
};

describe('AssetVersionRenderer', () => {
	it('render a content version link', () => {
		render(<AssetVersionRenderer {...testContentProps} />);

		expect(
			screen.getByRole('button', {name: testContentProps.value})
		).toBeInTheDocument();
	});

	it('not render a content version link without actions', () => {
		render(<AssetVersionRenderer {...testContentProps} actions={[]} />);

		expect(screen.queryByRole('button')).not.toBeInTheDocument();

		expect(screen.getByText(testContentProps.value)).toBeInTheDocument();
	});

	it('not render a content version link without actions and value', () => {
		const {container} = render(
			<AssetVersionRenderer {...testContentPropsWithouValue} />
		);

		expect(container.textContent).toBeFalsy();
	});

	it('not render a content version link with irrelevant action', () => {
		render(
			<AssetVersionRenderer
				{...testContentProps}
				actions={[testIrrelevantAction]}
			/>
		);

		expect(screen.queryByRole('button')).not.toBeInTheDocument();

		expect(screen.getByText(testContentProps.value)).toBeInTheDocument();
	});

	it('not render a content version link when action has no href', () => {
		render(
			<AssetVersionRenderer
				{...testContentProps}
				actions={[testViewContentActionWithoutHREF]}
			/>
		);

		expect(screen.queryByRole('button')).not.toBeInTheDocument();

		expect(screen.getByText(testContentProps.value)).toBeInTheDocument();
	});

	it('render a file version link', () => {
		render(<AssetVersionRenderer {...testFileProps} />);

		expect(
			screen.getByRole('button', {name: testFileProps.value})
		).toBeInTheDocument();
	});
});

describe('AssetVersionRenderer modals', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('open content modal', () => {
		render(<AssetVersionRenderer {...testContentProps} />);

		fireEvent.click(
			screen.getByRole('button', {name: testContentProps.value})
		);

		expect(openModal).toHaveBeenCalledWith(
			expect.objectContaining({
				size: 'full-screen',
				title: 'x-version-x',
				url: formatActionURL(
					testContentProps.itemData,
					testViewContentAction.href
				),
			})
		);
	});

	it('open file modal', () => {
		render(<AssetVersionRenderer {...testFileProps} />);

		fireEvent.click(
			screen.getByRole('button', {name: testFileProps.value})
		);

		expect(openModal).toHaveBeenCalledTimes(1);

		const openModalConfig = (openModal as jest.Mock).mock.calls[0][0];

		expect(openModalConfig.size).toBe('full-screen');

		const Content = openModalConfig.contentComponent;

		Content();

		expect(mockFilePreviewerModalContent).toHaveBeenCalledWith({
			file: testFileProps.itemData.file,
			headerName: 'x-version-x',
		});
	});
});
