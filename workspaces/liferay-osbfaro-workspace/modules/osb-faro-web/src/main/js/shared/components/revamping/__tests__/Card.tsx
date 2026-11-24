import React from 'react';
import {Card} from '../Card';
import {cleanup, render, screen} from '@testing-library/react';

describe('Card', () => {
	afterEach(cleanup);

	it('renders', () => {
		render(<Card title="Card Title">{'Card Content'}</Card>);

		expect(screen.getByText('Card Title')).toBeInTheDocument();
		expect(screen.getByText('Card Content')).toBeInTheDocument();
	});

	it('renders Card.SubHeader', () => {
		render(
			<Card title="Card Title">
				<Card.SubHeader title="Card SubHeader Title" />

				{'Card Content'}
			</Card>
		);

		expect(screen.getByText('Card Title')).toBeInTheDocument();
		expect(screen.getByText('CARD SUBHEADER TITLE')).toBeInTheDocument();
		expect(screen.getByText('Card Content')).toBeInTheDocument();
	});
});
