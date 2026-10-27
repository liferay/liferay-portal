import * as data from 'test/data';
import EditAttributeEventModal from '../EditAttributeEventModal';
import EVENT_DEFINITION_QUERY, {
	UPDATE_EVENT_DEFINITION
} from 'event-analysis/queries/EventDefinitionQuery';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, fireEvent, screen} from '@testing-library/react';
import {MockedProvider} from '@apollo/client/testing';
import {mockEventDefinitionReq} from 'test/graphql-data';
import {Provider} from 'react-redux';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const definition = data.mockEventDefinition(0, {
	displayName: 'Display Name',
	eventAttributeDefinitions: []
});

const DefaultComponent = ({observer, ...props}) => (
	<Provider store={mockStore()}>
		<MockedProvider
			mocks={[mockEventDefinitionReq(definition, {id: '0'})]}
		>
			<EditAttributeEventModal
				id='0'
				mutation={UPDATE_EVENT_DEFINITION}
				observer={observer}
				onClose={jest.fn()}
				query={EVENT_DEFINITION_QUERY}
				{...props}
			/>
		</MockedProvider>
	</Provider>
);

describe('EditAttributeEventModal', () => {
	afterEach(cleanup);

	it('renders the name and the display name of the event as the title', async () => {
		await renderModal(observer => <DefaultComponent observer={observer} />);

		expect(
			await screen.findByRole('heading', {
				name: `${definition.name} - Display Name`
			})
		).toBeInTheDocument();
	});

	it('closes without saving when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderModal(observer => (
			<DefaultComponent observer={observer} onClose={onClose} />
		));

		fireEvent.click(await screen.findByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
		expect(onClose).toHaveBeenCalledWith(false);
	});
});
