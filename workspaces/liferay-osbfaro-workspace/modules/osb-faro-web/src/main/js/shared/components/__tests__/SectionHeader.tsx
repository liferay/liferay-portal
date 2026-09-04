import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {SectionHeader} from '../SectionHeader';

jest.unmock('react-dom');

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

		expect(getByText('All Time')).toBeTruthy();
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

	it('should lay the header out as a row when right content is given', () => {
		const {container} = render(
			<SectionHeader
				icon="megaphone"
				rightContent="All Time"
				title="Campaigns"
			/>
		);

		expect(container.firstChild).toHaveClass('d-flex');
	});

	it('should leave the layout untouched when no right content is given', () => {
		const {container} = render(
			<SectionHeader icon="megaphone" title="Campaigns" />
		);

		expect(container.firstChild).not.toHaveClass('d-flex');
	});
});
