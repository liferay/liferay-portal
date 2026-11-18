import React from 'react';
import UnableDeletePropertyModal from '../UnableDeletePropertyModal';
import {cleanup, fireEvent, screen} from '@testing-library/react';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

describe('UnableDeletePropertyModal', () => {
	afterEach(cleanup);

	it('renders the title, the documentation link and the danger status', async () => {
		await renderModal(observer => (
			<UnableDeletePropertyModal observer={observer} onClose={jest.fn()} />
		));

		expect(screen.getByText(/^unable to delete property$/i)).toBeInTheDocument();

		expect(document.querySelector('.modal-danger')).toBeTruthy();

		expect(
			document.querySelector('.lexicon-icon-exclamation-full')
		).toBeTruthy();

		expect(screen.getByRole('link')).toHaveAttribute('target', '_blank');
	});

	it('closes when done is clicked', async () => {
		const onClose = jest.fn();

		await renderModal(observer => (
			<UnableDeletePropertyModal observer={observer} onClose={onClose} />
		));

		fireEvent.click(screen.getByText(/^done$/i));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
