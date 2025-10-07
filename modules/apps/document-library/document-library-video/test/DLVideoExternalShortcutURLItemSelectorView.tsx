/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitForElementToBeRemoved} from '@testing-library/dom';
import {type RenderResult, fireEvent, render} from '@testing-library/react';
import {getOpener} from 'frontend-js-web';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom/extend-expect';

import DLVideoExternalShortcutURLItemSelectorView from '../src/main/resources/META-INF/resources/js/DLVideoExternalShortcutURLItemSelectorView';

const mockLiferayOpenerFire = jest.fn();

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	getOpener: jest.fn(() => ({
		Liferay: {
			fire: mockLiferayOpenerFire,
		},
	})),
}));

const defaultProps = {
	eventName: 'eventName',
	getDLVideoExternalShortcutFieldsURL: '/getDLVideoExternalShortcutFieldsURL',
	namespace: 'namespace',
	returnType: 'returnType',
};

const renderComponent = (
	props: React.ComponentProps<
		typeof DLVideoExternalShortcutURLItemSelectorView
	>
) => render(<DLVideoExternalShortcutURLItemSelectorView {...props} />);

describe('DLVideoExternalShortcutURLItemSelectorView', () => {
	describe('when rendered with the default props', () => {
		let result: RenderResult;

		beforeEach(() => {
			result = renderComponent(defaultProps);
		});

		it('has an add button disabled', () => {
			const addButton = result.getByRole('button');

			expect(addButton).toBeInTheDocument();
			expect(addButton).toBeDisabled();
		});
	});

	describe('when there is a valid server response', () => {
		const responseFields = {
			DESCRIPTION: 'DESCRIPTION',
			HTML: '<iframe data-video-liferay></iframe>',
			THUMBNAIL_URL: 'https://thumbnail-url',
			TITLE: 'VIDEO TITLE',
			URL: 'https://video-url.com',
		};

		let result: RenderResult;

		beforeEach(async () => {
			jest.useFakeTimers();

			fetch.mockResponseOnce(JSON.stringify(responseFields));

			result = renderComponent(defaultProps);

			const {getByLabelText} = result;

			fireEvent.change(getByLabelText('video-url'), {
				target: {value: 'https://video-url.com'},
			});

			jest.advanceTimersByTime(600);

			await waitForElementToBeRemoved(() =>
				document.querySelector('span.loading-animation')
			);
		});

		afterEach(() => {
			jest.clearAllTimers();
			jest.clearAllMocks();
		});

		afterAll(() => {
			jest.useRealTimers();
		});

		it('has an add button enabled', () => {
			const add = result.getByRole('button');

			expect(add).toBeInTheDocument();
			expect(add).toBeEnabled();
		});

		describe('when add button is clicked', () => {
			beforeEach(async () => {
				fireEvent.click(result.getByRole('button'));
			});

			it('fires an event in the opener', () => {
				expect(getOpener).toHaveBeenCalled();

				expect(mockLiferayOpenerFire).toHaveBeenCalledWith(
					defaultProps.eventName,
					{
						data: {
							returnType: defaultProps.returnType,
							value: {
								html: responseFields.HTML,
								title: responseFields.TITLE,
							},
						},
					}
				);
			});
		});
	});

	describe('when there is an invalid server response', () => {
		let result: RenderResult;

		beforeEach(async () => {
			jest.useFakeTimers();
			fetch.mockResponseOnce('');

			result = renderComponent(defaultProps);

			const {getByLabelText} = result;

			fireEvent.change(getByLabelText('video-url'), {
				target: {value: 'https://unsupported-video-url.com'},
			});

			jest.advanceTimersByTime(600);

			await waitForElementToBeRemoved(() =>
				document.querySelector('span.loading-animation')
			);
		});

		afterEach(() => {
			jest.clearAllTimers();
			jest.clearAllMocks();
		});

		afterAll(() => {
			jest.useRealTimers();
		});

		it('has an add button disabled', () => {
			const addButton = result.getByRole('button');

			expect(addButton).toBeInTheDocument();
			expect(addButton).toBeDisabled();
		});
	});
});
