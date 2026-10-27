import InterestTopicsModal from '../InterestTopicsModal';
import React from 'react';
import {cleanup, fireEvent, screen} from '@testing-library/react';
import {noop} from 'lodash';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

describe('InterestTopicsModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		await renderModal(observer => (
			<InterestTopicsModal observer={observer} onClose={noop} />
		));

		expect(document.body).toMatchSnapshot();
	});

	it('should close when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderModal(observer => (
			<InterestTopicsModal observer={observer} onClose={onClose} />
		));

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
