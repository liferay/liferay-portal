import React from 'react';
import {GenericBarsCard} from '../GenericBarsCard';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('GenericBarsCard', () => {
	it('should render the card title as text by default', () => {
		const {container, getByText} = render(
			<GenericBarsCard cardTitle="Number of Sites" />
		);

		expect(getByText('Number of Sites').tagName).toBe('H3');
		expect(container.querySelector('.card-title-rectangle')).toBeNull();
	});

	it('should render the card title as a rectangle when redactTitle is true', () => {
		const {container, getByText} = render(
			<GenericBarsCard cardTitle="Number of Sites" redactTitle />
		);

		expect(
			container.querySelector('.card-title-rectangle')
		).toBeTruthy();
		expect(getByText('Number of Sites')).toHaveClass('sr-only');
	});
});
