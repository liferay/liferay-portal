/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';

import propsTransformer from '../src/main/resources/META-INF/resources/js/StagingVersionPropsTransformer';

const LAYOUT_REVISION_DETAILS_URL = 'http://localhost/layout-revision-details';

const LAYOUT_REVISION_STATUS_URL = 'http://localhost/layout-revision-status';

function renderStagingBar(portletNamespace) {
	const layoutRevisionDetails = document.createElement('div');

	layoutRevisionDetails.id = `${portletNamespace}layoutRevisionDetails`;

	const layoutRevisionStatus = document.createElement('div');

	layoutRevisionStatus.id = `${portletNamespace}layoutRevisionStatus`;

	document.body.append(layoutRevisionDetails, layoutRevisionStatus);
}

function transformProps(portletNamespace) {
	return propsTransformer({
		additionalProps: {
			layoutRevisionStatusURL: `${LAYOUT_REVISION_STATUS_URL}?ns=${portletNamespace}`,
			markAsReadyForPublicationURL: `${LAYOUT_REVISION_DETAILS_URL}?ns=${portletNamespace}`,
		},
		items: [],
		portletNamespace,
	});
}

function getFetchedURLs(baseURL) {
	return fetch.mock.calls
		.map(([url]) => String(url))
		.filter((url) => url.startsWith(baseURL));
}

describe('StagingVersionPropsTransformer', () => {
	let afterImplementation;
	let listeners;

	beforeEach(() => {
		afterImplementation = Liferay.after.getMockImplementation();
		listeners = [];

		Liferay.after.mockImplementation((eventName, callback) => {
			const listener = {callback, eventName};

			listeners.push(listener);

			return {
				detach: () => {
					listeners = listeners.filter((item) => item !== listener);
				},
			};
		});

		Liferay.fire.mockImplementation((eventName) => {
			listeners
				.filter((listener) => listener.eventName === eventName)
				.forEach((listener) => listener.callback());
		});

		fetch.mockResponse('<div></div>');
	});

	afterEach(() => {
		document.body.innerHTML = '';

		Liferay.after.mockImplementation(afterImplementation);
		Liferay.fire.mockReset();
	});

	it('reloads the staging bar once per layout update after it is rendered several times', async () => {
		renderStagingBar('_staging_');

		transformProps('_staging_');
		transformProps('_staging_');
		transformProps('_staging_');

		Liferay.fire('updatedLayout');

		await waitFor(() =>
			expect(getFetchedURLs(LAYOUT_REVISION_STATUS_URL)).toHaveLength(1)
		);

		expect(getFetchedURLs(LAYOUT_REVISION_DETAILS_URL)).toHaveLength(1);
	});
});
