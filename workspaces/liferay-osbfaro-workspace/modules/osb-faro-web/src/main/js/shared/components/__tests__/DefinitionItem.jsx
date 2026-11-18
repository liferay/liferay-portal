import DefinitionItem from '../DefinitionItem';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('DefinitionItem', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefinitionItem />);

		expect(container).toMatchSnapshot();
	});

	it('should render with a label and value', () => {
		const {queryByText} = render(
			<DefinitionItem label='foo' value='bar' />
		);

		expect(queryByText('foo')).toBeTruthy();
		expect(queryByText('bar')).toBeTruthy();
	});

	it('should render with an edit button', () => {
		const {queryByLabelText} = render(<DefinitionItem editable />);

		expect(queryByLabelText(/^edit$/i)).toBeTruthy();
	});

	it('should render as an input field with a cancel and submit button', () => {
		const {getByLabelText, queryByLabelText} = render(
			<DefinitionItem editable />
		);

		fireEvent.click(getByLabelText(/^edit$/i));

		jest.runAllTimers();

		expect(queryByLabelText(/^cancel$/i)).toBeTruthy();
		expect(queryByLabelText(/^submit$/i)).toBeTruthy();
		expect(queryByLabelText(/^edit$/i)).toBeNull();
	});
});
