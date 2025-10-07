/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import Checkbox from '../../../src/main/resources/META-INF/resources/js/Checkbox/Checkbox';

describe('Field Checkbox', () => {
	afterEach(cleanup);

	describe('Boolean Field', () => {
		describe('Non localizable Tooltip', () => {
			test.each`
				isLocalizationSupported | message
				${true}                 | ${'translation-is-disabled-for-this-field'}
				${false}                | ${'this-field-does-not-support-translations'}
				${undefined}            | ${'this-field-cannot-be-localized'}
			`(
				'shows message $message when isLocalizationSupported is $isLocalizationSupported',
				({isLocalizationSupported, message}) => {
					render(
						<Checkbox
							editOnlyInDefaultLanguage={true}
							isLocalizationSupported={isLocalizationSupported}
							readOnly={true}
							showLabel={true}
						/>
					);

					const tooltipMessage = screen.getByTitle(message);

					expect(tooltipMessage).toBeInTheDocument();
				}
			);

			it('hides when editOnlyInDefaultLanguage is false', () => {
				render(
					<Checkbox
						editOnlyInDefaultLanguage={false}
						readOnly={true}
						showLabel={true}
					/>
				);

				const tooltip = screen.queryByRole('presentation');

				expect(tooltip).not.toBeInTheDocument();
			});
		});

		it('check if the boolean field is checked if he has predefinedValue true', () => {
			const {queryByLabelText} = render(
				<Checkbox label="Boolean" predefinedValue={['true']} />
			);

			expect(queryByLabelText('Boolean')).toBeChecked();
		});

		it('check if the boolean field is not checked if he has predefinedValue false', () => {
			const {queryByLabelText} = render(
				<Checkbox label="Boolean" predefinedValue={['false']} />
			);

			expect(queryByLabelText('Boolean')).not.toBeChecked();
		});

		it('check if the required icon appears when the field is required', () => {
			render(<Checkbox required />);

			const requiredIcon = document.querySelector(
				'.lexicon-icon-asterisk'
			);

			expect(requiredIcon).toBeInTheDocument();
		});

		it('check if the required icon do not appears when the field is not required', () => {
			render(<Checkbox />);

			const requiredIcon = document.querySelector(
				'.lexicon-icon-asterisk'
			);

			expect(requiredIcon).not.toBeInTheDocument();
		});

		it('check it shows the label when we set it up', () => {
			const {queryByLabelText} = render(
				<Checkbox label="Boolean" showLabel />
			);

			expect(queryByLabelText('Boolean')).toBeInTheDocument();
		});

		it('check that with false predefinedValue the boolean field is checked when we enable it', () => {
			const onChange = jest.fn();

			const {queryByLabelText} = render(
				<Checkbox
					label="Boolean"
					onChange={onChange}
					predefinedValue={['false']}
				/>
			);

			const input = queryByLabelText('Boolean');

			userEvent.click(input);

			expect(onChange).toHaveBeenLastCalledWith({target: {value: true}});
		});

		it('check that with true predefinedValue the boolean field is not checked when we disabled it', () => {
			const onChange = jest.fn();

			const {queryByLabelText} = render(
				<Checkbox
					label="Boolean"
					onChange={onChange}
					predefinedValue={['true']}
				/>
			);

			const input = queryByLabelText('Boolean');

			userEvent.click(input);

			expect(onChange).toHaveBeenLastCalledWith({target: {value: false}});
		});

		it('verify if the switcher appears when he is enabled in boolean field', () => {
			render(<Checkbox />);

			const swithcerIcon = document.querySelector(
				'.toggle-switch-handle'
			);

			expect(swithcerIcon).toBeInTheDocument();
		});

		it('verify if the switcher do not appears when he is disabled in boolean field', () => {
			render(<Checkbox showAsSwitcher={false} />);

			const swithcerIcon = document.querySelector(
				'.toggle-switch-handle'
			);

			expect(swithcerIcon).not.toBeInTheDocument();
		});
	});

	describe('Maximum Repetitions Info', () => {
		it('does not show the maximum repetitions info', () => {
			const {container} = render(<Checkbox value />);

			const ddmInfo = container.querySelector('.ddm-info');

			expect(ddmInfo).toBeNull();
		});

		it('does not show the maximum repetitions info if the value is false', () => {
			const {container} = render(
				<Checkbox showMaximumRepetitionsInfo value={false} />
			);

			const ddmInfo = container.querySelector('.ddm-info');

			expect(ddmInfo).toBeNull();
		});

		it('shows the maximum repetitions info', () => {
			const {container} = render(
				<Checkbox showMaximumRepetitionsInfo value />
			);

			const ddmInfo = container.querySelector('.ddm-info');

			expect(ddmInfo).not.toBeNull();
		});
	});

	it('call the onChange callback on the field change', () => {
		const handleFieldEdited = jest.fn();

		render(<Checkbox onChange={handleFieldEdited} />);

		userEvent.click(document.body.querySelector('input'));

		expect(handleFieldEdited).toHaveBeenCalled();
	});

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(<Checkbox required={true} />);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const {container} = render(<Checkbox required={true} value={true} />);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('has a helptext', () => {
		const {container} = render(<Checkbox id="ID" tip="Type something" />);

		expect(container).toMatchSnapshot();
	});

	it('has a key', () => {
		const {container} = render(<Checkbox key="key" value />);

		expect(container).toMatchSnapshot();
	});

	it('has a label', () => {
		const {container} = render(<Checkbox label="label" />);

		expect(container).toMatchSnapshot();
	});

	it('has a predefined Value', () => {
		const {container} = render(<Checkbox placeholder="Option 1" />);

		expect(container).toMatchSnapshot();
	});

	it('has a value', () => {
		const {container} = render(<Checkbox value />);

		expect(container).toMatchSnapshot();
	});

	it('has an id', () => {
		const {container} = render(<Checkbox id="ID" />);

		expect(container).toMatchSnapshot();
	});

	it('is not editable', () => {
		const {container} = render(<Checkbox readOnly />);

		expect(container).toMatchSnapshot();
	});

	it('is not required', () => {
		const {container} = render(<Checkbox required={false} />);

		expect(container).toMatchSnapshot();
	});

	it('is shown as a switcher', () => {
		const {container} = render(<Checkbox showAsSwitcher />);

		expect(container).toMatchSnapshot();
	});

	it('is shown as checkbox', () => {
		const {container} = render(<Checkbox showAsSwitcher={false} />);

		expect(container).toMatchSnapshot();
	});

	it('renders Label if showLabel is true', () => {
		const {container} = render(<Checkbox label showLabel />);

		expect(container).toMatchSnapshot();
	});
});
