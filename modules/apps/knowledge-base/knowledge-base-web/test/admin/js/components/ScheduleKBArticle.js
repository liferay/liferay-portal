/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import ScheduleKBArticle from '../../../../src/main/resources/META-INF/resources/js/admin/components/ScheduleKBArticle';

const bridgeComponentId = '_portletNamespace_ScheduleKBArticleComponent';

const newArticleProps = {
	displayDate: '',
	portletNamespace: '_portletNamespace_',
	scheduled: false,
};

function getTomorrowFormattedDate() {
	const tomorrow = new Date();
	tomorrow.setDate(tomorrow.getDate() + 1);

	const isoFormat = tomorrow.toISOString().split('T');
	const date = isoFormat[0];
	const time = isoFormat[1].substring(0, 5);

	return `${date} ${time}`;
}

const tomorrowFormattedDate = getTomorrowFormattedDate();

const scheduledArticleProps = {
	displayDate: tomorrowFormattedDate,
	portletNamespace: '_portletNamespace_',
	scheduled: true,
};

describe('ScheduleKBArticle', () => {
	beforeEach(() => {
		const components = {};

		Liferay.component = (id, component) => {
			components[id] = component;
		};
		Liferay.componentReady = (id) => Promise.resolve(components[id]);

		Liferay.destroyComponent = jest.fn();
	});

	describe('when the article is new or not scheduled', () => {
		let result;
		let callback;

		beforeAll(() => {
			jest.useFakeTimers();
		});

		beforeEach(() => {
			callback = jest.fn();

			result = render(<ScheduleKBArticle {...newArticleProps} />);

			return act(() =>
				Liferay.componentReady(bridgeComponentId).then(({open}) => {
					open(callback);
				})
			);
		});

		it('renders the modal with Schedule Publication title', async () => {
			act(() => {
				jest.runAllTimers();
			});

			const title = await result.getByText('schedule-publication');

			expect(title).toBeInTheDocument();
		});

		it('renders the modal with Cancel and Schedule buttons', async () => {
			act(() => {
				jest.runAllTimers();
			});

			const cancelButton = await result.getByText('cancel');
			const scheduleButton = await result.getByText('schedule');

			expect(cancelButton).toBeInTheDocument();
			expect(scheduleButton).toBeInTheDocument();
			expect(scheduleButton).toBeDisabled();
		});
	});

	describe('when the article is already scheduled', () => {
		let result;
		let callback;

		beforeAll(() => {
			jest.useFakeTimers();
		});

		beforeEach(() => {
			callback = jest.fn();

			result = render(<ScheduleKBArticle {...scheduledArticleProps} />);

			return act(() =>
				Liferay.componentReady(bridgeComponentId).then(({open}) => {
					open(callback);
				})
			);
		});

		it('renders the modal with Edit Scheduled Publication title', async () => {
			act(() => {
				jest.runAllTimers();
			});

			const title = await result.getByText('edit-scheduled-publication');

			expect(title).toBeInTheDocument();
		});

		it('renders the modal with Publish Now button', async () => {
			act(() => {
				jest.runAllTimers();
			});
			const publishNowButton = await result.getByText('publish-now');

			expect(publishNowButton).toBeInTheDocument();
		});

		it('when click on Publish Now button callback is called', async () => {
			act(() => {
				jest.runAllTimers();
			});
			const publishNowButton = await result.getByText('publish-now');

			act(() => {
				fireEvent.click(publishNowButton);
			});

			expect(callback).toHaveBeenCalledWith('');
		});

		it('Schedule button is enabled', async () => {
			act(() => {
				jest.runAllTimers();
			});
			const scheduleButton = await result.getByText('schedule');

			expect(scheduleButton).not.toBeDisabled();
		});

		it('when click on Schedule Now button callback is called', async () => {
			act(() => {
				jest.runAllTimers();
			});
			const scheduleButton = await result.getByText('schedule');

			act(() => {
				fireEvent.click(scheduleButton);
			});

			expect(callback).toHaveBeenCalledWith(tomorrowFormattedDate);
		});
	});
});
