import ClayModal from '@clayui/modal';
import IssueSubmitted from '../IssueSubmitted';
import React from 'react';
import {cleanup, screen} from '@testing-library/react';
import {noop} from 'lodash';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

describe('IssueSubmitted', () => {
	afterEach(cleanup);

	it('should render', async () => {
		await renderModal(observer => (
			<ClayModal observer={observer}>
				<IssueSubmitted onClose={noop} onNext={noop} />
			</ClayModal>
		));

		expect(document.body).toMatchSnapshot();
	});

	it('should thank the user for the contribution', async () => {
		await renderModal(observer => (
			<ClayModal observer={observer}>
				<IssueSubmitted onClose={noop} onNext={noop} />
			</ClayModal>
		));

		expect(screen.getByText('Message Received')).toBeInTheDocument();
	});
});
