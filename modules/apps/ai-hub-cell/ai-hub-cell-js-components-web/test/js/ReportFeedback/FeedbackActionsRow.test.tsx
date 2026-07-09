/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import FeedbackActionsRow from '../../../src/main/resources/META-INF/resources/js/ReportFeedback/FeedbackActionsRow';

describe('FeedbackActionsRow', () => {
	it('invokes the thumbs-up and report callbacks on click', () => {
		const onReport = jest.fn();
		const onThumbsUp = jest.fn();

		render(
			<FeedbackActionsRow onReport={onReport} onThumbsUp={onThumbsUp} />
		);

		fireEvent.click(
			screen.getByRole('button', {name: 'give-positive-feedback'})
		);

		expect(onThumbsUp).toHaveBeenCalledTimes(1);

		fireEvent.click(
			screen.getByRole('button', {
				name: 'send-negative-feedback-or-report-legal-concern',
			})
		);

		expect(onReport).toHaveBeenCalledTimes(1);
	});

	it('disables both feedback buttons once feedback is given', () => {
		const onReport = jest.fn();
		const onThumbsUp = jest.fn();

		render(
			<FeedbackActionsRow
				feedbackGiven
				onReport={onReport}
				onThumbsUp={onThumbsUp}
			/>
		);

		const thumbsUpButton = screen.getByRole('button', {
			name: 'give-positive-feedback',
		});
		const reportButton = screen.getByRole('button', {
			name: 'send-negative-feedback-or-report-legal-concern',
		});

		expect(thumbsUpButton).toBeDisabled();
		expect(reportButton).toBeDisabled();

		fireEvent.click(thumbsUpButton);
		fireEvent.click(reportButton);

		expect(onThumbsUp).not.toHaveBeenCalled();
		expect(onReport).not.toHaveBeenCalled();
	});
});
