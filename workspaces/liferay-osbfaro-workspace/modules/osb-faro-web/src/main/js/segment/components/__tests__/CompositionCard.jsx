import CompositionCard from '../CompositionCard';
import React from 'react';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('CompositionCard', () => {
	it('should render', () => {
		const {container} = render(
			<CompositionCard
				activeIndividualCount={20}
				individualCount={100}
				knownIndividualCount={50}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render the counts and percentages as processing while the membership is processing', () => {
		render(
			<CompositionCard
				activeIndividualCount={0}
				individualCount={0}
				knownIndividualCount={0}
				membershipProcessing
			/>
		);

		expect(screen.getAllByText('Processing')).toHaveLength(4);
		expect(screen.queryByText('0')).not.toBeInTheDocument();
	});
});
