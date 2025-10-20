import React from 'react';
import StringMatchInput from '../StringMatchInput';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('StringMatchInput', () => {
	it('should render', () => {
		const {container} = render(
			<StringMatchInput
				metadata='url'
				onStringMatchChange={jest.fn()}
				stringMatch='.*custom-assets'
			/>
		);

		expect(container.querySelector('.base-select-input-root')).toBeNull();
		expect(container).toMatchSnapshot();
	});

	it('should render w/ BaseSelect', () => {
		const {container} = render(
			<StringMatchInput onStringMatchChange={jest.fn()} />
		);

		expect(container.querySelector('.base-select-input-root')).toBeTruthy();
	});
});
