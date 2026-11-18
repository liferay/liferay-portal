import FormNavigation from '../FormNavigation';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('FormNavigation', () => {
	it('should render', () => {
		const {container} = render(<FormNavigation cancelHref='' />);

		expect(container).toMatchSnapshot();
	});

	it('should render with a custom submit text', () => {
		const submitMessage = 'Submit me';

		const {getByText} = render(
			<FormNavigation submitMessage={submitMessage} />
		);

		expect(getByText(submitMessage)).toBeTruthy();
	});

	it('should render with a previous button', () => {
		const {getByText} = render(
			<FormNavigation onPreviousStep={() => jest.fn()} />
		);

		expect(getByText(/^previous$/i)).toBeTruthy();
	});
});
