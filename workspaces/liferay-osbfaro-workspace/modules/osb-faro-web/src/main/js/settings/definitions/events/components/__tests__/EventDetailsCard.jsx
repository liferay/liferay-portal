import * as data from 'test/data';
import EventDetailsCard from '../EventDetailsCard';
import mockStore from 'test/mock-store';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {range} from 'lodash';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

describe('EventDetailsCard', () => {
	const eventAttributes = range(5).map(i =>
		data.mockEventAttributeDefinition(i)
	);

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<EventDetailsCard
						eventAttributes={eventAttributes}
						eventName='viewArticle'
						groupId='23'
					/>
				</MemoryRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should match the displayed code with the selected attributes', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<EventDetailsCard
						eventAttributes={eventAttributes}
						eventName='viewArticle'
						groupId='23'
					/>
				</MemoryRouter>
			</Provider>
		);

		fireEvent.click(container.querySelector('.clickable'));

		expect(container.querySelector('.copy-button')).toHaveAttribute(
			'data-clipboard-text',
			[
				"Analytics.track('viewArticle', {",
				"\n\t'name-1': 'samplevalue-1',",
				"\n\t'name-2': 'samplevalue-2',",
				"\n\t'name-3': 'samplevalue-3',",
				"\n\t'name-4': 'samplevalue-4',",
				'\n});'
			].join('')
		);
	});
});
