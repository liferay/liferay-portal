jest.mock(
	'../../SectionApp',
	() =>
		({path}: {path: string}) =>
			path
);

import React from 'react';
import {render, screen} from '@testing-library/react';
import {
	EditSegment,
	Segment,
	SegmentDistribution,
	SegmentInterest,
	SegmentInterests,
	SegmentMembership,
} from '../SegmentsApp';

jest.unmock('react-dom');

describe('SegmentsApp', () => {
	it.each([
		[{id: '1'}, 'contacts/segments/1/edit'],
		[{}, 'contacts/segments/create'],
	])('renders the route of EditSegment with %j', (params, path) => {
		render(<EditSegment channelId="321" groupId="23" params={params} />);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of Segment', () => {
		render(<Segment channelId="321" groupId="23" params={{id: '1'}} />);

		expect(screen.getByText('contacts/segments/1')).toBeInTheDocument();
	});

	it('renders the route of SegmentDistribution', () => {
		render(
			<SegmentDistribution
				channelId="321"
				groupId="23"
				params={{id: '1'}}
			/>
		);

		expect(
			screen.getByText('contacts/segments/1/distribution')
		).toBeInTheDocument();
	});

	it.each([
		[
			{id: '1', interestId: 'Liferay DXP'},
			'contacts/segments/1/interests/Liferay%20DXP',
		],
		[
			{id: '1', interestId: 'Liferay DXP', tabId: 'pages'},
			'contacts/segments/1/interests/Liferay%20DXP/pages',
		],
	])('renders the route of SegmentInterest with %j', (params, path) => {
		render(
			<SegmentInterest channelId="321" groupId="23" params={params} />
		);

		expect(screen.getByText(path)).toBeInTheDocument();
	});

	it('renders the route of SegmentInterests', () => {
		render(
			<SegmentInterests channelId="321" groupId="23" params={{id: '1'}} />
		);

		expect(
			screen.getByText('contacts/segments/1/interests')
		).toBeInTheDocument();
	});

	it('renders the route of SegmentMembership', () => {
		render(
			<SegmentMembership
				channelId="321"
				groupId="23"
				params={{id: '1'}}
			/>
		);

		expect(
			screen.getByText('contacts/segments/1/membership')
		).toBeInTheDocument();
	});
});
