/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import History, {EventType} from '../../js/components/History';
import {mockFetch} from './__mocks__/frontend-js-web';

jest.mock('@liferay/object-dynamic-data-mapping-form-field-type', () => ({
	AssigneeAvatar: ({name}: {name: string}) => <span>{name}</span>,
}));

const FIELDS = {
	description: {label: 'description', type: 'richText' as const},
};

function renderHistory(newValue: string, oldValue: string | null) {
	mockFetch.mockResolvedValueOnce({
		json: async () => ({
			auditEvents: [
				{
					auditFieldChanges: [
						{name: 'description', newValue, oldValue},
					],
					creator: {name: 'Test Test'},
					dateCreated: '2026-10-22T00:00:00Z',
					eventType: EventType.UPDATE,
				},
			],
		}),
	} as Response);

	render(
		<History
			apiURL="/o/cmp/tasks/1"
			fields={FIELDS}
			getAuditEventLabel={() => 'updated the description'}
		/>
	);
}

describe('History', () => {
	beforeAll(() => {
		window.IntersectionObserver = class {
			callback: IntersectionObserverCallback;

			constructor(callback: IntersectionObserverCallback) {
				this.callback = callback;
			}

			disconnect() {}

			observe() {
				this.callback(
					[{isIntersecting: true} as IntersectionObserverEntry],
					this as unknown as IntersectionObserver
				);
			}
		} as unknown as typeof window.IntersectionObserver;
	});

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renders none when a rich text value has no content', async () => {
		renderHistory('<p></p>', null);

		await userEvent.click(await screen.findByText('view-details'));

		expect(screen.getAllByText('none')).toHaveLength(2);
	});

	it('renders rich text values as plain text', async () => {
		renderHistory(
			'<p>First</p><p>Second &amp; <em>third</em></p>',
			'<p>Old <strong>text</strong></p>'
		);

		await userEvent.click(await screen.findByText('view-details'));

		expect(screen.getByText('First Second & third')).toBeInTheDocument();
		expect(screen.getByText('Old text')).toBeInTheDocument();
		expect(screen.queryByText(/<p>/)).not.toBeInTheDocument();
	});

	it('renders the file names of images and videos in rich text values', async () => {
		renderHistory(
			'<p>Brief</p><figure class="media"><div data-oembed-url="https://www.youtube.com/watch?v=video"></div></figure>',
			'<p>Brief <img src="/documents/20117/0/IMG_8276.jpg/a1b2c3?version=1.0"></p><figure class="media"><div><video src="/documents/20117/0/demo.mp4/d4e5f6"></video></div></figure>'
		);

		await userEvent.click(await screen.findByText('view-details'));

		expect(
			screen.getByText('Brief https://www.youtube.com/watch?v=video')
		).toBeInTheDocument();
		expect(
			screen.getByText('Brief IMG_8276.jpg demo.mp4')
		).toBeInTheDocument();
	});
});
