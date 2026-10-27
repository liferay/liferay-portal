import InviteUsersModal from '../InviteUsersModal';
import React from 'react';
import {cleanup, fireEvent, screen} from '@testing-library/react';
import {noop} from 'lodash';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

describe('InviteUsersModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		await renderModal(observer => (
			<InviteUsersModal observer={observer} onClose={noop} />
		));

		expect(document.body).toMatchSnapshot();
	});

	it('should render with custom class', async () => {
		await renderModal(observer => (
			<InviteUsersModal
				className='custom-class'
				observer={observer}
				onClose={noop}
			/>
		));

		expect(document.querySelector('.custom-class')).toBeTruthy();
	});

	it('should close when cancel is clicked', async () => {
		const onClose = jest.fn();

		await renderModal(observer => (
			<InviteUsersModal observer={observer} onClose={onClose} />
		));

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
