import NoResultsDisplay, {getFormattedTitle} from '../NoResultsDisplay';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('NoResultsDisplay', () => {
	afterEach(cleanup);

	it('should render component', () => {
		const {container} = render(<NoResultsDisplay />);

		expect(container).toMatchSnapshot();
	});

	it('should render component with primary color', () => {
		const {container} = render(<NoResultsDisplay primary />);

		expect(container.querySelector('.no-results-root')).toHaveClass(
			'no-results-primary'
		);
	});

	it('should render component with description', () => {
		const description = 'No results with description';

		const {getByText} = render(
			<NoResultsDisplay description={description} />
		);

		expect(getByText(description)).toBeTruthy();
	});

	it('should render component with title', () => {
		const title = 'No results with title';

		const {getByText} = render(<NoResultsDisplay title={title} />);

		expect(getByText(title)).toBeTruthy();
	});

	it('should render component with icon', () => {
		const {container} = render(
			<NoResultsDisplay icon={{symbol: 'home'}} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render component with icon without border', () => {
		const {container} = render(
			<NoResultsDisplay icon={{border: false, symbol: 'home'}} />
		);

		expect(container.querySelector('.no-results-icon-border')).toBeNull();
	});

	it('should render component with icon and size sm', () => {
		const {container} = render(
			<NoResultsDisplay icon={{size: 'sm', symbol: 'home'}} />
		);

		expect(
			container.querySelector('.lexicon-icon-home.icon-size-sm')
		).toBeTruthy();
	});
});

describe('NoResultsDisplay - getFormattedTitle util', () => {
	it('should return formatted title', () => {
		expect(getFormattedTitle()).toEqual('No items were found.');
	});

	it('should return formatted title passing a title', () => {
		expect(
			getFormattedTitle(
				undefined,
				Liferay.Language.get(
					'there-are-no-x-that-match-the-selected-criteria'
				)
			)
		).toEqual('There are no items that match the selected criteria.');
	});

	it('should return formatted title passing a name', () => {
		expect(getFormattedTitle(Liferay.Language.get('segments'))).toEqual(
			'No Segments were found.'
		);
	});
});
