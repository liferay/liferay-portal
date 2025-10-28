/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import CompareCheckbox from '../../../src/main/resources/META-INF/resources/components/compare_checkbox/CompareCheckbox';
import {
	ITEM_REMOVED_FROM_COMPARE,
	PRODUCT_COMPARISON_TOGGLED,
	TOGGLE_ITEM_IN_PRODUCT_COMPARISON,
} from '../../../src/main/resources/META-INF/resources/utilities/eventsDefinitions';

describe('CompareCheckbox', () => {
	const BASE_PROPS = {
		itemId: 12345,
		pictureUrl: 'http://some.url/picture.png',
	};

	let removeFromCompareTrigger;
	let toggleCompareTrigger;

	beforeEach(() => {
		window.Liferay = {
			Language: {
				get: jest.fn(),
			},
			detach: jest.fn(),
			fire: jest.fn(),
			on: jest.fn((eventName, callback) => {
				if (eventName === ITEM_REMOVED_FROM_COMPARE) {
					removeFromCompareTrigger = callback;
				}
				else if (eventName === PRODUCT_COMPARISON_TOGGLED) {
					toggleCompareTrigger = callback;
				}
			}),
		};
	});

	afterEach(() => {
		jest.resetAllMocks();
	});

	describe('by default', () => {
		it('renders a checkbox that is enabled and not checked', () => {
			const {asFragment, getByRole} = render(
				<CompareCheckbox {...BASE_PROPS} />
			);

			expect(asFragment()).toMatchSnapshot();
			expect(getByRole('checkbox')).not.toBeChecked();
		});
	});

	describe('by data flow', () => {
		it('renders a disabled checkbox if "disabled" is true and the product is not in the MiniCompare widget', () => {
			const PROPS = {
				...BASE_PROPS,
				disabled: true,
				inCompare: false,
			};

			const {asFragment, getByRole} = render(
				<CompareCheckbox {...PROPS} />
			);

			const ComponentElement = getByRole('checkbox');

			expect(asFragment()).toMatchSnapshot();
			expect(ComponentElement).toBeDisabled();
		});

		it('renders an enabled checkbox if "disabled" is true but the product is in the MiniCompare widget', () => {
			const PROPS = {
				...BASE_PROPS,
				disabled: true,
				inCompare: true,
			};

			const {asFragment, getByRole} = render(
				<CompareCheckbox {...PROPS} />
			);

			const ComponentElement = getByRole('checkbox');

			expect(asFragment()).toMatchSnapshot();
			expect(ComponentElement).not.toBeDisabled();
		});
	});

	describe('by interaction', () => {
		it(`on click, triggers the ${TOGGLE_ITEM_IN_PRODUCT_COMPARISON} event sending out the ID of the product and its thumbnail`, async () => {
			const {getByRole} = render(<CompareCheckbox {...BASE_PROPS} />);

			const ComponentElement = getByRole('checkbox');

			await act(async () => {
				fireEvent.click(ComponentElement);
			});

			await waitFor(() => {
				expect(window.Liferay.fire).toHaveBeenCalledTimes(1);
				expect(window.Liferay.fire).toHaveBeenCalledWith(
					TOGGLE_ITEM_IN_PRODUCT_COMPARISON,
					{
						id: BASE_PROPS.itemId,
						thumbnail: BASE_PROPS.pictureUrl,
					}
				);
			});
		});
	});

	describe('by event', () => {
		describe(`on "${ITEM_REMOVED_FROM_COMPARE}"`, () => {
			it('if product ID coincides, unchecks the checkbox and calls the "onUpdate" custom callback', async () => {
				const PROPS = {
					...BASE_PROPS,
					inCompare: true,
					onUpdate: jest.fn(),
				};

				const {getByRole} = render(<CompareCheckbox {...PROPS} />);

				const ComponentElement = getByRole('checkbox');

				await act(async () => {
					removeFromCompareTrigger({id: PROPS.itemId});
				});

				await waitFor(() => {
					expect(ComponentElement).not.toBeChecked();
					expect(PROPS.onUpdate).toHaveBeenCalledWith({
						disabled: false,
						inCompare: false,
					});
				});
			});

			it('if product ID does not coincide, does nothing', async () => {
				const FAILING_ITEM_ID = 1111;

				const PROPS = {
					...BASE_PROPS,
					inCompare: true,
					onUpdate: jest.fn(),
				};

				const {getByRole} = render(<CompareCheckbox {...PROPS} />);

				const ComponentElement = getByRole('checkbox');

				await act(async () => {
					removeFromCompareTrigger({id: FAILING_ITEM_ID});
				});

				await waitFor(() => {
					expect(ComponentElement).toBeChecked();
					expect(PROPS.onUpdate).not.toHaveBeenCalled();
				});
			});
		});

		describe(`on ${PRODUCT_COMPARISON_TOGGLED}`, () => {
			it('if product comparison is available, renders an enabled checkbox and calls the "onUpdate" custom callback', async () => {
				const PROPS = {
					...BASE_PROPS,
					onUpdate: jest.fn(),
				};

				const {getByRole} = render(<CompareCheckbox {...PROPS} />);

				const ComponentElement = getByRole('checkbox');

				await act(async () => {
					toggleCompareTrigger({disabled: false});
				});

				await waitFor(() => {
					expect(ComponentElement).not.toBeDisabled();
					expect(PROPS.onUpdate).toHaveBeenCalledWith({
						disabled: false,
						inCompare: false,
					});
				});
			});

			it('if product comparison is not available, renders a disabled checkbox and calls the "onUpdate" custom callback', async () => {
				const PROPS = {
					...BASE_PROPS,
					onUpdate: jest.fn(),
				};

				const {getByRole} = render(<CompareCheckbox {...PROPS} />);

				const ComponentElement = getByRole('checkbox');

				await act(async () => {
					toggleCompareTrigger({disabled: true});
				});

				await waitFor(() => {
					expect(ComponentElement).toBeDisabled();
					expect(PROPS.onUpdate).toHaveBeenCalledWith({
						disabled: true,
						inCompare: false,
					});
				});
			});

			it('if product comparison availability is not specified, renders a disabled checkbox and calls the "onUpdate" custom callback', async () => {
				const PROPS = {
					...BASE_PROPS,
					onUpdate: jest.fn(),
				};

				const {getByRole} = render(<CompareCheckbox {...PROPS} />);

				const ComponentElement = getByRole('checkbox');

				await act(async () => {
					toggleCompareTrigger({});
				});

				await waitFor(() => {
					expect(ComponentElement).toBeDisabled();
					expect(PROPS.onUpdate).toHaveBeenCalledWith({
						disabled: true,
						inCompare: false,
					});
				});
			});
		});
	});
});
