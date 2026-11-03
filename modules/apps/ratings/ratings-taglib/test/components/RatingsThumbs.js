/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import TYPES from '../../src/main/resources/META-INF/resources/js/RATINGS_TYPES';
import {Ratings} from '../../src/main/resources/META-INF/resources/js/index';
import {formDataToObj} from '../utils';

const baseProps = {
	className: 'com.liferay.model.RateableEntry',
	classPK: 'classPK',
	enabled: true,
	signedIn: true,
	type: TYPES.THUMBS,
	url: 'http://url',
};

const renderComponent = (props) =>
	render(<Ratings {...baseProps} {...props} />);

describe('RatingsThumbs', () => {
	afterEach(cleanup);

	describe('when rendered with the default props', () => {
		let thumbUpButton;
		let thumbDownButton;

		beforeEach(() => {
			[thumbUpButton, thumbDownButton] =
				renderComponent().getAllByRole('button');
		});

		it('is enabled', () => {
			expect(thumbUpButton.disabled).toBe(false);
			expect(thumbDownButton.disabled).toBe(false);
		});

		it('has default votes', () => {
			expect(thumbUpButton.value).toBe('0');
			expect(thumbDownButton.value).toBe('0');
		});

		it('thumb-up button has unrate title', () => {
			expect(thumbUpButton.title).toBe('rate-this-as-good');
		});

		it('thumb-down button has rate title', () => {
			expect(thumbDownButton.title).toBe('rate-this-as-bad');
		});
	});

	describe('when rendered with enabled = false', () => {
		let thumbUpButton;
		let thumbDownButton;

		beforeEach(() => {
			[thumbUpButton, thumbDownButton] = renderComponent({
				enabled: false,
			}).getAllByRole('button');
		});

		it('is enabled', () => {
			expect(thumbUpButton.disabled).toBe(true);
			expect(thumbDownButton.disabled).toBe(true);
		});

		it('has disabled titles', () => {
			expect(thumbUpButton.title).toBe('ratings-are-disabled-in-staging');
			expect(thumbDownButton.title).toBe(
				'ratings-are-disabled-in-staging'
			);
		});
	});

	describe('when rendered with signedIn = false', () => {
		it('is disabled', () => {
			const [thumbUpButton, thumbDownButton] = renderComponent({
				signedIn: false,
			}).getAllByRole('button');

			expect(thumbUpButton.disabled).toBe(true);
			expect(thumbDownButton.disabled).toBe(true);
		});
	});

	describe('when there is no server response', () => {
		beforeEach(() => {
			fetch.mockResponse(JSON.stringify({}));
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user votes up', () => {
			let thumbUpButton;
			let thumbDownButton;

			beforeEach(() => {
				[thumbUpButton, thumbDownButton] = renderComponent({
					initialNegativeVotes: 10,
					initialPositiveVotes: 26,
				}).getAllByRole('button');

				act(() => {
					fireEvent.click(thumbUpButton);
				});
			});

			it('increases the up counter', () => {
				expect(thumbUpButton.value).toBe('27');
			});

			it('keeps the down counter', () => {
				expect(thumbDownButton.value).toBe('10');
			});

			it('thumb-up button has unrate title', () => {
				expect(thumbUpButton.title).toBe('you-have-rated-this-as-good');
			});

			it('thumb-down button has rate title', () => {
				expect(thumbDownButton.title).toBe('rate-this-as-bad');
			});

			describe('and the user votes down', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(thumbDownButton);
					});
				});

				it('decreases the up counter', () => {
					expect(thumbUpButton.value).toBe('26');
				});

				it('increases the down counter', () => {
					expect(thumbDownButton.value).toBe('11');
				});
			});

			describe('and the user votes up again', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(thumbUpButton);
					});
				});

				it('decreases the up counter', () => {
					expect(thumbUpButton.value).toBe('26');
				});

				it('keeps the down counter', () => {
					expect(thumbDownButton.value).toBe('10');
				});
			});
		});

		describe('and the user votes down', () => {
			let thumbUpButton;
			let thumbDownButton;

			beforeEach(() => {
				[thumbUpButton, thumbDownButton] = renderComponent({
					initialNegativeVotes: 10,
					initialPositiveVotes: 26,
				}).getAllByRole('button');

				act(() => {
					fireEvent.click(thumbDownButton);
				});
			});

			it('keeps the up counter', () => {
				expect(thumbUpButton.value).toBe('26');
			});

			it('increases the down counter', () => {
				expect(thumbDownButton.value).toBe('11');
			});

			describe('and the user votes up', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(thumbUpButton);
					});
				});

				it('increases the up counter', () => {
					expect(thumbUpButton.value).toBe('27');
				});

				it('decreases the down counter', () => {
					expect(thumbDownButton.value).toBe('10');
				});
			});

			describe('and the user votes down again', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(thumbDownButton);
					});
				});

				it('keeps the up counter', () => {
					expect(thumbUpButton.value).toBe('26');
				});

				it('decreases the down counter', () => {
					expect(thumbDownButton.value).toBe('10');
				});
			});
		});
	});

	describe('when there is a valid server response', () => {
		beforeEach(() => {
			fetch.mockResponseOnce(
				JSON.stringify({totalEntries: 59 + 27, totalScore: 26.7})
			);
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user votes up', () => {
			let thumbUpButton;
			let thumbDownButton;

			beforeEach(async () => {
				[thumbUpButton, thumbDownButton] =
					renderComponent().getAllByRole('button');

				await act(async () => {
					fireEvent.click(thumbUpButton);
				});
			});

			it('sends a POST request to the server', () => {
				const [url, {body}] = fetch.mock.calls[0];
				const objFormData = formDataToObj(body);

				expect(url).toBe(baseProps.url);
				expect(objFormData.className).toBe(baseProps.className);
				expect(objFormData.score).toBe('1');
			});

			it('updates the rounded counters with the ones from the server', () => {
				expect(thumbUpButton.value).toBe('27');
				expect(thumbDownButton.value).toBe('59');
			});
		});
	});
});
