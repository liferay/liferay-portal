/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import QuestionRow from '../../../src/main/resources/META-INF/resources/js/components/QuestionRow.es';

import '@testing-library/jest-dom';
import {cleanup} from '@testing-library/react';

import {renderComponent} from '../../helpers.es';

const mockQuestionWithValidAnswer = {
	aggregateRating: null,
	articleBody:
		'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus\n  dictum ex eu tellus iaculis tristique. Etiam sapien sem, pharetra et\n  congue quis, efficitur pulvinar arcu.</p>',
	creator: {
		id: 20126,
		image: null,
		name: 'Test Test',
	},
	dateModified: '2020-07-31T12:48:56Z',
	friendlyUrlPath: 'new-question',
	hasValidAnswer: true,
	headline: 'new question',
	id: 36695,
	keywords: ['test'],
	messageBoardSection: {
		numberOfMessageBoardSections: 0,
		parentMessageBoardSectionId: null,
		title: 'Portal',
	},
	numberOfMessageBoardMessages: 2,
	seen: true,
	viewCount: 48,
};

const mockQuestionWithNoValidAnswer = {
	aggregateRating: null,
	articleBody:
		'<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus\n  dictum ex eu tellus iaculis tristique. Etiam sapien sem, pharetra et\n  congue quis, efficitur pulvinar arcu.</p>',
	creator: {
		id: 20126,
		image: null,
		name: 'Test Test',
	},
	dateModified: '2020-07-31T12:48:56Z',
	friendlyUrlPath: 'new-question',
	hasValidAnswer: false,
	headline: 'new question',
	id: 36695,
	keywords: ['test'],
	messageBoardSection: {
		numberOfMessageBoardSections: 0,
		parentMessageBoardSectionId: null,
		title: 'Portal',
	},
	numberOfMessageBoardMessages: 2,
	seen: true,
	viewCount: 54,
};

const render = ({currentSection, mock}) => ({
	...renderComponent({
		ui: (
			<QuestionRow
				currentSection={currentSection}
				key={mock.id}
				question={mock}
				showSectionLabel={!!mock.numberOfMessageBoardSections}
			/>
		),
	}),
});

describe('QuestionRow', () => {
	afterEach(() => {
		cleanup();
	});

	it('Shows a check icon if the question has a valid answer', () => {
		const {getByTestId} = render({
			currentSection: 'portal',
			mock: mockQuestionWithValidAnswer,
		});

		const hasValidAnswerBadge = getByTestId('has-valid-answer-badge');
		const checkSymbolDivElement =
			hasValidAnswerBadge.querySelector('div').className;

		expect(
			checkSymbolDivElement.includes('alert-success border-0')
		).toBeTruthy();

		const checkSymbol = hasValidAnswerBadge.querySelector('svg');
		expect(checkSymbol.className.baseVal).toMatch(
			'lexicon-icon-check-circle-full'
		);
	});

	it('Shows a message icon if the question does not have a valid answer', () => {
		const {getByTestId} = render({
			currentSection: 'portal',
			mock: mockQuestionWithNoValidAnswer,
		});

		const hasValidAnswerBadge = getByTestId('has-valid-answer-badge');
		const checkSymbolStyle = hasValidAnswerBadge.className;

		expect(checkSymbolStyle.includes('alert-success border-0')).toBeFalsy();

		const checkSymbol = hasValidAnswerBadge.querySelector('svg');
		expect(checkSymbol.className.baseVal).toMatch('lexicon-icon-message');
	});
});
