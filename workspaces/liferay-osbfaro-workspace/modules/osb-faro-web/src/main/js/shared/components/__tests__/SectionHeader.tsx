import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {SectionHeader} from '../SectionHeader';

describe('SectionHeader', () => {
	afterEach(cleanup);

	it('should render the title in upper case', () => {
		const {getByText} = render(
			<SectionHeader icon="megaphone" title="Campaigns" />
		);

		expect(getByText('CAMPAIGNS')).toBeTruthy();
	});

	it('should render a string passed as right content', () => {
		const {getByText} = render(
			<SectionHeader
				icon="megaphone"
				rightContent="All Time"
				title="Campaigns"
			/>
		);

		expect(getByText(/^all time$/i)).toBeTruthy();
	});

	it('should render a component passed as right content', () => {
		const {getByTestId} = render(
			<SectionHeader
				icon="megaphone"
				rightContent={<button data-testid="right-control" />}
				title="Campaigns"
			/>
		);

		expect(getByTestId('right-control')).toBeTruthy();
	});
});
