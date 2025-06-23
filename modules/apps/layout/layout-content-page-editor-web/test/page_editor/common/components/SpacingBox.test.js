/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {VIEWPORT_SIZES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import SpacingBox from '../../../../src/main/resources/META-INF/resources/page_editor/common/components/SpacingBox';
import {StyleBookContextProvider} from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/page_design_options/hooks/useStyleBook';
import StoreMother from '../../../../src/main/resources/META-INF/resources/page_editor/test_utils/StoreMother';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableViewportSizes: {
				desktop: {label: 'desktop'},
				landscapeMobile: {label: 'landscapeMobile'},
				portraitMobile: {label: 'portraitMobile'},
				tablet: {label: 'tablet'},
			},
			frontendTokens: {
				spacer0: {
					defaultValue: '3rem',
					label: 'Spacer 0',
					value: '3rem',
				},
				spacer10: {
					defaultValue: '5rem',
					label: 'Spacer 10',
					value: '5rem',
				},
			},
		},
	})
);

const SpacingBoxTest = ({
	itemConfig = {},
	canSetCustomValue = true,
	onChange = () => {},
	value = {},
	selectedViewportSize = VIEWPORT_SIZES.desktop,
}) => (
	<StoreMother.Component
		getState={() => ({
			selectedViewportSize,
		})}
	>
		<StyleBookContextProvider>
			<SpacingBox
				canSetCustomValue={canSetCustomValue}
				fields={{
					marginBottom: {
						cssProperty: 'margin-bottom',
						defaultValue: '0',
						label: 'margin-bottom',
						name: 'marginTop',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '10', value: '10'},
							],
						},
					},
					marginLeft: {
						cssProperty: 'margin-left',
						defaultValue: '0',
						label: 'margin-left',
						name: 'marginLeft',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '10', value: '10'},
							],
						},
					},
					marginRight: {
						cssProperty: 'margin-right',
						defaultValue: '0',
						label: 'margin-right',
						name: 'marginRight',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '10', value: '10'},
							],
						},
					},
					marginTop: {
						cssProperty: 'margin-top',
						defaultValue: '0',
						label: 'margin-top',
						name: 'marginTop',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '10', value: '10'},
							],
						},
					},
					paddingBottom: {
						cssProperty: 'padding-bottom',
						defaultValue: '0',
						label: 'padding-bottom',
						name: 'paddingBottom',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '10', value: '10'},
							],
						},
					},
					paddingLeft: {
						cssProperty: 'padding-left',
						defaultValue: '0',
						label: 'padding-left',
						name: 'paddingLeft',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '10', value: '10'},
							],
						},
					},
					paddingRight: {
						cssProperty: 'padding-right',
						defaultValue: '0',
						label: 'padding-right',
						name: 'paddingRight',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '5', value: '5'},
							],
						},
					},
					paddingTop: {
						cssProperty: 'padding-top',
						defaultValue: '0',
						label: 'padding-top',
						name: 'paddingTop',
						typeOptions: {
							validValues: [
								{label: '0', value: '0'},
								{label: '10', value: '10'},
							],
						},
					},
				}}
				item={{config: itemConfig}}
				onChange={onChange}
				value={value}
			/>
		</StyleBookContextProvider>
	</StoreMother.Component>
);

describe('SpacingBox', () => {
	let _getComputedStyle;

	beforeEach(() => {
		_getComputedStyle = window.getComputedStyle;
	});

	afterEach(() => {
		window.getComputedStyle = _getComputedStyle;
	});

	it('renders given spacing values from StyleBook', () => {
		render(<SpacingBoxTest value={{marginTop: '10'}} />);

		expect(screen.getByLabelText('padding-left')).toHaveTextContent('3rem');
		expect(screen.getByLabelText('margin-top')).toHaveTextContent('5rem');
	});

	it('can be navigated with keyboard', () => {
		render(<SpacingBoxTest />);

		const grid = screen.getByRole('grid');

		screen.getByLabelText('margin-top').focus();

		fireEvent.keyDown(grid, {key: 'ArrowDown'});
		fireEvent.keyDown(grid, {key: 'ArrowRight'});
		fireEvent.keyDown(grid, {key: 'ArrowRight'});
		fireEvent.keyDown(grid, {key: 'ArrowDown'});
		fireEvent.keyDown(grid, {key: 'ArrowLeft'});
		fireEvent.keyDown(grid, {key: 'ArrowRight'});

		expect(screen.getByLabelText('padding-left')).toHaveFocus();
	});

	it('can be used to update spacing', () => {
		const onChange = jest.fn();
		render(<SpacingBoxTest onChange={onChange} />);

		fireEvent.click(screen.getByLabelText('padding-left'));
		fireEvent.click(screen.getByLabelText('set-padding-left-to-10'));

		expect(onChange).toHaveBeenCalledWith('paddingLeft', '10');
	});

	it('shows token value next to token name in the dropdown', async () => {
		render(<SpacingBoxTest />);

		await userEvent.click(screen.getByLabelText('padding-left'));

		expect(screen.getByText('5rem')).toBeInTheDocument();
	});

	it('focuses the selected option when the dropdown is opened', () => {
		render(<SpacingBoxTest value={{marginTop: '10'}} />);

		jest.useFakeTimers();

		fireEvent.click(screen.getByLabelText('margin-top'));

		act(() => {
			jest.runAllTimers();
		});

		jest.useRealTimers();

		expect(
			screen.getByRole('menuitem', {name: /set-margin-top-to-10/i})
		).toHaveFocus();
	});

	it('gets the corresponding value if the token value does not exist', async () => {
		window.getComputedStyle = () => {
			return {
				getPropertyValue: (key) => {
					return {'padding-right': '111px'}[key];
				},
			};
		};

		render(<SpacingBoxTest />);

		await userEvent.click(screen.getByLabelText('padding-right'));

		expect(screen.getByText('111px')).toBeInTheDocument();
	});

	describe('LenghtInput inside SpacingBox', () => {
		it('does not render the input when user does not have update permission', () => {
			render(<SpacingBoxTest canSetCustomValue={false} />);

			userEvent.click(screen.getByLabelText('padding-left'));

			expect(screen.queryByTitle('select-units')).not.toBeInTheDocument();
		});

		it('calls onChange when setting a custom value', async () => {
			const onChange = jest.fn();
			render(<SpacingBoxTest onChange={onChange} />);

			const button = screen.getByLabelText('margin-top');

			await userEvent.click(button);

			const input = screen.getByLabelText('margin-top', {
				selector: 'input',
			});

			await userEvent.clear(input);
			await userEvent.type(input, '12');
			fireEvent.blur(input);

			expect(onChange).toHaveBeenCalledWith('marginTop', '12px');
		});

		it('calls onChange and closes the dropdown when the Enter button is pressed', async () => {
			const onChange = jest.fn();
			render(<SpacingBoxTest onChange={onChange} />);

			const button = screen.getByLabelText('padding-top');

			await userEvent.click(button);

			const input = screen.getByLabelText('padding-top', {
				selector: 'input',
			});

			await userEvent.clear(input);
			await userEvent.type(input, '20');
			fireEvent.keyUp(input, {key: 'Enter'});

			expect(onChange).toHaveBeenCalledWith('paddingTop', '20px');
			expect(screen.queryByText('10rem')).not.toBeInTheDocument();
		});
	});

	describe('Reset button inside SpacingBox', () => {
		it('does not show reset button if no value is selected', async () => {
			render(<SpacingBoxTest />);

			const button = screen.getByLabelText('margin-top');

			await userEvent.click(button);

			expect(
				screen.queryByTitle('reset-to-initial-value')
			).not.toBeInTheDocument();
		});

		it('reset value when pressing the button', async () => {
			const onChange = jest.fn();

			render(
				<SpacingBoxTest onChange={onChange} value={{marginTop: '10'}} />
			);

			const button = screen.getByLabelText('margin-top');

			await userEvent.click(button);

			await userEvent.click(screen.getByTitle('reset-to-initial-value'));

			expect(onChange).toHaveBeenCalledWith(
				'marginTop',
				null,
				expect.anything()
			);
		});

		it('renders correct label if we are in Tablet viewport', async () => {
			const onChange = jest.fn();

			render(
				<SpacingBoxTest
					itemConfig={{marginTop: '2px'}}
					onChange={onChange}
					selectedViewportSize={VIEWPORT_SIZES.tablet}
					value={{marginTop: '10'}}
				/>
			);

			await userEvent.click(screen.getByLabelText('margin-top'));

			expect(
				screen.getByTitle('reset-to-desktop-value')
			).toBeInTheDocument();
		});

		it('renders correct label if we are in Landscape viewport', async () => {
			const onChange = jest.fn();

			render(
				<SpacingBoxTest
					itemConfig={{marginTop: '2px'}}
					onChange={onChange}
					selectedViewportSize={VIEWPORT_SIZES.landscapeMobile}
					value={{marginTop: '10'}}
				/>
			);

			await userEvent.click(screen.getByLabelText('margin-top'));

			expect(
				screen.getByTitle('reset-to-tablet-value')
			).toBeInTheDocument();
		});

		it('renders correct label if we are in Portrait viewport', async () => {
			const onChange = jest.fn();

			render(
				<SpacingBoxTest
					itemConfig={{marginTop: '2px'}}
					onChange={onChange}
					selectedViewportSize={VIEWPORT_SIZES.portraitMobile}
					value={{marginTop: '10'}}
				/>
			);

			await userEvent.click(screen.getByLabelText('margin-top'));

			expect(
				screen.getByTitle('reset-to-landscapeMobile-value')
			).toBeInTheDocument();
		});
	});
});
