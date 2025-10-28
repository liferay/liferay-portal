/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import {LockedKBArticleModal} from '../../../../src/main/resources/META-INF/resources/js';

const bridgeComponentId = '_portletNamespace_LockedKBArticleModal';

const components = {};
Liferay = {
	...Liferay,
	component(id, component) {
		components[id] = component;
	},
	componentReady(id) {
		return Promise.resolve(components[id]);
	},
	destroyComponent() {},
};

describe('LockedKBArticleModal', () => {
	beforeAll(() => {
		jest.useFakeTimers();
	});

	it('does not render the modal first', () => {
		const {queryByText} = render(<LockedKBArticleModal open={false} />);

		act(() => {
			jest.runAllTimers();
		});

		expect(queryByText('article-in-edition')).not.toBeInTheDocument();
	});

	it('renders the modal as non group admin when try to edit/expire/delete a locked article', () => {
		const {getByText} = render(
			<LockedKBArticleModal groupAdmin={false} open={true} />
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(getByText('article-in-edition')).toBeInTheDocument();
		expect(getByText('ok')).toBeInTheDocument();
	});

	it('renders the modal as group admin when try to edit/expire/delete a locked article', () => {
		const actionURL = 'action-url';

		const {getByRole, getByText} = render(
			<LockedKBArticleModal
				actionURL={actionURL}
				groupAdmin={true}
				open={true}
			/>
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(
			getByText('article-in-edition-by-user-x-description')
		).toBeInTheDocument();
		expect(getByText('cancel')).toBeInTheDocument();
		expect(getByText('take-control-and-x')).toBeInTheDocument();
		expect(getByRole('link', {name: 'take-control-and-x'})).toHaveAttribute(
			'href',
			actionURL
		);
	});

	it('renders the modal as group admin when try to move a locked article', async () => {
		const actionURL = 'action-url';

		const {getByRole, getByText} = await render(
			<LockedKBArticleModal
				actionURL={actionURL}
				groupAdmin={true}
				open={false}
				portletNamespace="_portletNamespace_"
			/>
		);

		await act(() =>
			Liferay.componentReady(bridgeComponentId).then(({open}) => {
				open('actionLabel', actionURL, 'userName');
			})
		);

		act(() => {
			jest.runAllTimers();
		});

		expect(
			getByText('article-in-edition-by-user-x-description')
		).toBeInTheDocument();
		expect(getByText('cancel')).toBeInTheDocument();
		expect(getByText('take-control-and-x')).toBeInTheDocument();
		expect(getByRole('link', {name: 'take-control-and-x'})).toHaveAttribute(
			'href',
			actionURL
		);
	});
});
