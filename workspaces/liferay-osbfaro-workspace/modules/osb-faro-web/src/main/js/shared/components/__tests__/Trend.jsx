import React from 'react';
import Trend from '../Trend';
import {render} from '@testing-library/react';

describe('Trend', () => {
	it('should render', () => {
		const {container, getByText} = render(
			<Trend color='red' label='Trend component' />
		);

		expect(container.querySelector('.analytics-trend')).toHaveStyle(
			'color: red'
		);
		expect(getByText('Trend component')).toBeTruthy();
		expect(container).toMatchSnapshot();
	});

	it('should render w/ icon', () => {
		const {container} = render(
			<Trend color='red' icon='home' label='Trend component' />
		);

		expect(container.querySelector('.lexicon-icon-home')).toBeTruthy();
	});
});
