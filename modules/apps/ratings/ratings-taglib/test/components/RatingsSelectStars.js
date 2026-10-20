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
	numberOfStars: 5,
	randomNamespace: '_random_namespace_',
	signedIn: true,
	type: TYPES.STARS,
	url: 'http://url',
};

const renderComponent = (props) =>
	render(<Ratings {...baseProps} {...props} />);

describe('RatingsSelectStars', () => {
	afterEach(cleanup);

	describe('when rendered with the default props', () => {
		let starsDropdownToggle;
		let starsButtons;

		beforeEach(() => {
			starsButtons = renderComponent().getAllByRole('button');
			starsDropdownToggle = starsButtons[0];
		});

		it('is enabled', () => {
			expect(starsDropdownToggle.disabled).toBe(false);
		});

		it('has default user score', () => {
			expect(starsDropdownToggle.value).toBe('0');
		});

		it('has vote title', () => {
			expect(starsDropdownToggle.title).toBe('vote[action]');
		});

		it('has delete option disabled', () => {
			const starsOptions = renderComponent().getAllByRole('option', {
				hidden: true,
			});

			expect(starsOptions[5]).toHaveProperty('disabled', true);
		});
	});

	describe('when rendered with enabled = false', () => {
		let starsDropdownToggle;

		beforeEach(() => {
			starsDropdownToggle = renderComponent({
				enabled: false,
			}).getAllByRole('button')[0];
		});

		it('is disabled', () => {
			expect(starsDropdownToggle.disabled).toBe(true);
		});

		it('has disabled title', () => {
			expect(starsDropdownToggle.title).toBe(
				'ratings-are-disabled-in-staging'
			);
		});
	});

	describe('when there is no server response', () => {
		beforeEach(() => {
			fetch.mockResponse(JSON.stringify({}));
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user votes 1/5 stars', () => {
			let starsDropdownToggle;
			let starsButtons;
			let result;

			beforeEach(() => {
				result = renderComponent({
					userScore: 0.2,
				});

				starsButtons = result.getAllByRole('option', {hidden: true});
				starsDropdownToggle = result.getAllByRole('button')[0];

				act(() => {
					fireEvent.click(starsButtons[0]);
				});
			});

			it('increases the user score', () => {
				expect(starsDropdownToggle.value).toBe('1');
			});

			it('has voted singular title', () => {
				expect(starsDropdownToggle.title).toBe(
					'you-have-rated-this-x-star-out-of-x'
				);
			});

			it('has delete option enabled', () => {
				expect(starsButtons[5]).toHaveProperty('disabled', false);
			});

			describe('later the user vote 5/5 stars', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(starsButtons[4]);
					});
				});

				it('increases the user score', () => {
					expect(starsDropdownToggle.value).toBe('5');
				});

				it('has voted plural title', () => {
					expect(starsDropdownToggle.title).toBe(
						'you-have-rated-this-x-stars-out-of-x'
					);
				});
			});

			describe('finally the user unvote', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(starsButtons[5]);
					});
				});

				it('deletes the user score', () => {
					expect(starsDropdownToggle.value).toBe('0');
				});

				it('has delete option disabled', () => {
					expect(starsButtons[5]).toHaveProperty('disabled', true);
				});
			});
		});

		describe('when the user score is 5/5', () => {
			let starsDropdownToggle;
			let starsButtons;
			let result;

			beforeEach(() => {
				result = renderComponent({
					userScore: 1,
				});

				starsButtons = result.getAllByRole('option', {hidden: true});
				starsDropdownToggle = result.getAllByRole('button')[0];
			});

			it('shows the initial user score', () => {
				expect(starsDropdownToggle.value).toBe('5');
			});

			describe('and the user vote 2/5 stars', () => {
				beforeEach(() => {
					act(() => {
						fireEvent.click(starsButtons[1]);
					});
				});

				it('decreases the user score', () => {
					expect(starsDropdownToggle.value).toBe('2');
				});
			});
		});
	});

	describe('when there is a valid server response', () => {
		beforeEach(() => {
			fetch.mockResponseOnce(
				JSON.stringify({
					averageScore: 0.5,
					score: 0.6,
					totalEntries: 2,
				})
			);
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user vote 3/5 stars', () => {
			let starsDropdownToggle;
			let starsButtons;
			let result;

			beforeEach(async () => {
				result = renderComponent({
					userScore: 0.4,
				});
				starsButtons = result.getAllByRole('option', {hidden: true});
				starsDropdownToggle = result.getAllByRole('button')[0];

				await act(async () => {
					fireEvent.click(starsButtons[2]);
				});
			});

			it('sends a POST request to the server', () => {
				const [url, {body}] = fetch.mock.calls[0];
				const objFormData = formDataToObj(body);

				expect(url).toBe(baseProps.url);
				expect(objFormData.className).toBe(baseProps.className);
				expect(objFormData.score).toBe('0.6');
			});

			it('updates the average score of 2 votes', () => {
				expect(result.queryByText('2.5 (2 votes)')).toBeTruthy();
			});

			it('updates the user score with the one from the server', () => {
				expect(starsDropdownToggle.value).toBe('3');
			});

			describe('later the user change her vote to 5/5 stars', () => {
				beforeEach(async () => {
					fetch.mockResponseOnce(
						JSON.stringify({
							averageScore: 1,
							score: 1,
							totalEntries: 1,
						})
					);

					await act(async () => {
						fireEvent.click(starsButtons[4]);
					});
				});

				it('updates the user score with the one from the server', () => {
					expect(starsDropdownToggle.value).toBe('5');
				});

				it('updates the average score of 1 vote', async () => {
					expect(
						result.queryByText('5.0 (1 vote[noun])')
					).toBeTruthy();
				});
			});

			describe('finally the user unvote', () => {
				beforeEach(async () => {
					fetch.mockResponseOnce(
						JSON.stringify({
							averageScore: 0,
							score: -1,
							totalEntries: 0,
						})
					);

					await act(async () => {
						fireEvent.click(starsButtons[5]);
					});
				});

				it('deletes the user score', () => {
					expect(starsDropdownToggle.value).toBe('0');
				});

				it('has delete option disabled', () => {
					expect(starsButtons[5]).toHaveProperty('disabled', true);
				});
			});
		});
	});

	describe('when a decimal score is set from the API', () => {
		it('it rounds the score to the nearest valid rating', () => {
			const result = renderComponent({
				userScore: 0.5,
			});
			const starsDropdownToggle = result.getAllByRole('button')[0];

			expect(starsDropdownToggle.value).toBe('3');
		});
	});
});
