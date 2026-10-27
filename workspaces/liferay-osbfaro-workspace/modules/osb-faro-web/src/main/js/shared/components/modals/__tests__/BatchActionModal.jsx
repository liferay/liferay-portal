import BatchActionModal from '../BatchActionModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, fireEvent, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const DefaultComponent = ({observer, ...props}) => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<MockedProvider addTypename={false}>
				<BatchActionModal
					actionOptions={{
						actionCountString: '',
						options: [],
						optionsLabel: ''
					}}
					columns={[]}
					editableAttr=''
					fitContent={false}
					items={[]}
					observer={observer}
					onClose={jest.fn()}
					onSave={jest.fn()}
					title='Batch Action'
					{...props}
				/>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('BatchActionModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		await renderModal(observer => <DefaultComponent observer={observer} />);

		expect(document.body).toMatchSnapshot();
	});

	it('should render the title', async () => {
		await renderModal(observer => <DefaultComponent observer={observer} />);

		expect(
			screen.getByRole('heading', {name: 'Batch Action'})
		).toBeInTheDocument();
	});

	it('should close when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderModal(observer => (
			<DefaultComponent observer={observer} onClose={onClose} />
		));

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
