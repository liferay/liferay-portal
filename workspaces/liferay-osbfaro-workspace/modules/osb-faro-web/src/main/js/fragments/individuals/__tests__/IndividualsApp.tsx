jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {
	Individual,
	IndividualDetails,
	IndividualInterest,
	IndividualInterests,
	IndividualSegments,
	IndividualsInterest,
} from '../IndividualsApp';

jest.unmock('react-dom');

describe('IndividualsApp', () => {
	it('renders the route of Individual', () => {
		render(<Individual channelId="321" groupId="23" params={{id: '1'}} />);

		expect(
			screen.getByText('contacts/individuals/known-individuals/1')
		).toBeInTheDocument();
	});

	it('renders the route of IndividualDetails', () => {
		render(
			<IndividualDetails
				channelId="321"
				groupId="23"
				params={{id: '1'}}
			/>
		);

		expect(
			screen.getByText('contacts/individuals/known-individuals/1/details')
		).toBeInTheDocument();
	});

	it('renders the route of IndividualInterest', () => {
		render(
			<IndividualInterest
				channelId="321"
				groupId="23"
				params={{id: '1', interestId: 'Liferay DXP'}}
			/>
		);

		expect(
			screen.getByText(
				'contacts/individuals/known-individuals/1/interests/Liferay%20DXP'
			)
		).toBeInTheDocument();
	});

	it('renders the route of IndividualInterests', () => {
		render(
			<IndividualInterests
				channelId="321"
				groupId="23"
				params={{id: '1'}}
			/>
		);

		expect(
			screen.getByText(
				'contacts/individuals/known-individuals/1/interests'
			)
		).toBeInTheDocument();
	});

	it('renders the route of IndividualSegments', () => {
		render(
			<IndividualSegments
				channelId="321"
				groupId="23"
				params={{id: '1'}}
			/>
		);

		expect(
			screen.getByText(
				'contacts/individuals/known-individuals/1/segments'
			)
		).toBeInTheDocument();
	});

	it('renders the route of IndividualsInterest', () => {
		render(
			<IndividualsInterest
				channelId="321"
				groupId="23"
				params={{interestId: 'Liferay DXP'}}
			/>
		);

		expect(
			screen.getByText('contacts/individuals/interests/Liferay%20DXP')
		).toBeInTheDocument();
	});
});
