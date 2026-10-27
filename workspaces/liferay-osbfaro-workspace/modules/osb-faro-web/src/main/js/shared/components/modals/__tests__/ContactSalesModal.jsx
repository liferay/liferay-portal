import ContactSalesModal from '../ContactSalesModal';
import mockStore from 'test/mock-store';
import React from 'react';
import {addAlert} from 'shared/actions/alerts';
import {cleanup, fireEvent, screen} from '@testing-library/react';
import {noop} from 'lodash';
import {Provider} from 'react-redux';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const DefaultComponent = ({observer, ...props}) => (
	<Provider store={mockStore()}>
		<ContactSalesModal
			addAlert={addAlert}
			observer={observer}
			onClose={noop}
			{...props}
		/>
	</Provider>
);

describe('ContactSalesModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		await renderModal(observer => <DefaultComponent observer={observer} />);

		expect(document.body).toMatchSnapshot();
	});

	it('should render the contact sales title', async () => {
		await renderModal(observer => <DefaultComponent observer={observer} />);

		expect(
			screen.getByRole('heading', {name: 'Contact Sales'})
		).toBeInTheDocument();
	});
});
