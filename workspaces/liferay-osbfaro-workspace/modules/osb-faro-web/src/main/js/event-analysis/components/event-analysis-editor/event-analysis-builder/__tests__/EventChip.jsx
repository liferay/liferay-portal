import client from 'shared/apollo/client';
import EventChip from '../EventChip';
import mockStore from 'test/mock-store';
import React from 'react';
import {ApolloProvider} from '@apollo/client';
import {fireEvent, render, screen} from '@testing-library/react';
import {Provider} from 'react-redux';

describe('EventChip', () => {
	it('renders the event name without an overline', () => {
		const {container} = render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<EventChip event={{name: 'View Article'}} />
				</Provider>
			</ApolloProvider>
		);

		expect(container.querySelector('.condition-chip')).toHaveTextContent(
			/^View Article$/
		);
		expect(container.querySelector('.text-uppercase')).toBeNull();
	});

	it('calls onEventChange with null when the remove button is clicked', () => {
		const onEventChange = jest.fn();

		render(
			<ApolloProvider client={client}>
				<Provider store={mockStore()}>
					<EventChip
						event={{id: '1', name: 'View Article'}}
						onEventChange={onEventChange}
					/>
				</Provider>
			</ApolloProvider>
		);

		fireEvent.click(
			screen.getByRole('button', {name: /remove.view article/i})
		);

		expect(onEventChange).toHaveBeenCalledTimes(1);
		expect(onEventChange).toHaveBeenCalledWith(null);
	});
});
