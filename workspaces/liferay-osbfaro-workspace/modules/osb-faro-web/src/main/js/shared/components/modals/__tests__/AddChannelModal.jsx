import * as pedantic from 'test/pedantic';
import AddChannelModal from '../AddChannelModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {
	act,
	cleanup,
	fireEvent,
	screen,
	waitFor
} from '@testing-library/react';
import {InMemoryCache} from '@apollo/client';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const DefaultComponent = ({observer, ...props}) => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<MockedProvider
				cache={
					new InMemoryCache({
						addTypename: false
					})
				}
				mocks={[]}
			>
				<AddChannelModal
					groupId='23'
					observer={observer}
					onClose={jest.fn()}
					onSubmit={jest.fn()}
					{...props}
				/>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('AddChannelModal', () => {
	beforeEach(() => {
		pedantic.disable();
	});

	afterEach(() => {
		pedantic.enable();
		cleanup();
	});

	it('renders', async () => {
		await renderModal(observer => (
			<DefaultComponent observer={observer} />
		));

		expect(document.body).toMatchSnapshot();
	});

	it('renders the title and the property name field', async () => {
		await renderModal(observer => (
			<DefaultComponent observer={observer} />
		));

		expect(screen.getByText('New Property')).toBeInTheDocument();
		expect(screen.getByLabelText('Property Name')).toBeInTheDocument();
	});

	it('focuses the property name field without showing its error', async () => {
		await renderModal(observer => (
			<DefaultComponent observer={observer} />
		));

		act(() => {
			jest.runOnlyPendingTimers();
		});

		expect(screen.getByLabelText('Property Name')).toHaveFocus();
		expect(document.querySelector('.modal .has-error')).toBeNull();
		expect(screen.queryByText('Required')).toBeNull();
	});

	it('closes when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderModal(observer => (
			<DefaultComponent observer={observer} onClose={onClose} />
		));

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('keeps save disabled until the name is valid', async () => {
		await renderModal(observer => (
			<DefaultComponent observer={observer} />
		));

		const save = screen.getByText('Save').closest('button');
		const input = screen.getByLabelText('Property Name');

		fireEvent.change(input, {target: {value: 'ab'}});
		fireEvent.blur(input);

		await waitFor(() => expect(save).toBeDisabled());

		fireEvent.change(input, {target: {value: 'My property'}});

		await waitFor(() => expect(save).not.toBeDisabled());
	});
});
