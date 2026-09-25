/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

import ContentsMessageBalloon from '../../../../src/main/resources/META-INF/resources/js/AIAssistantChat/components/ContentsMessageBalloon';

const DRAFTS_MESSAGE =
	'I created these contents for you:\n\n' +
	'- [Travelling around Japan](/cms/edit_content_item/1)';

describe('ContentsMessageBalloon', () => {
	it('disables the feedback actions once feedback is given', () => {
		render(
			<ContentsMessageBalloon
				feedbackGiven
				message={DRAFTS_MESSAGE}
				onReport={jest.fn()}
				onThumbsUp={jest.fn()}
			/>
		);

		expect(
			screen.getByRole('button', {name: 'give-positive-feedback'})
		).toBeDisabled();
		expect(
			screen.getByRole('button', {
				name: 'send-negative-feedback-or-report-legal-concern',
			})
		).toBeDisabled();
	});

	it('does not list links that are not content edit pages', () => {
		render(
			<ContentsMessageBalloon message="Here is the [documentation](https://liferay.com/docs)." />
		);

		expect(screen.queryByRole('link', {name: 'documentation'})).toBeNull();
		expect(screen.queryByText('draft')).toBeNull();
	});

	it('does not render the feedback actions without a report handler', () => {
		render(<ContentsMessageBalloon message={DRAFTS_MESSAGE} />);

		expect(
			screen.queryByRole('button', {name: 'give-positive-feedback'})
		).toBeNull();
	});

	it('renders each content edit link from the markdown message as a draft', () => {
		render(
			<ContentsMessageBalloon
				message={
					'I created these contents for you:\n\n' +
					'- [Travelling around Japan](/cms/edit_content_item/1)\n' +
					'- [North Japan](/cms/edit_content_item/2)'
				}
			/>
		);

		expect(
			screen.getByRole('link', {name: 'Travelling around Japan'})
		).toHaveAttribute('href', '/cms/edit_content_item/1');
		expect(screen.getByRole('link', {name: 'North Japan'})).toHaveAttribute(
			'href',
			'/cms/edit_content_item/2'
		);
		expect(screen.getAllByText('draft')).toHaveLength(2);
	});

	it('renders the feedback actions below the drafts', () => {
		const onReport = jest.fn();
		const onThumbsUp = jest.fn();

		render(
			<ContentsMessageBalloon
				message={DRAFTS_MESSAGE}
				onReport={onReport}
				onThumbsUp={onThumbsUp}
			/>
		);

		fireEvent.click(
			screen.getByRole('button', {name: 'give-positive-feedback'})
		);
		fireEvent.click(
			screen.getByRole('button', {
				name: 'send-negative-feedback-or-report-legal-concern',
			})
		);

		expect(onThumbsUp).toHaveBeenCalledTimes(1);
		expect(onReport).toHaveBeenCalledTimes(1);
	});
});
