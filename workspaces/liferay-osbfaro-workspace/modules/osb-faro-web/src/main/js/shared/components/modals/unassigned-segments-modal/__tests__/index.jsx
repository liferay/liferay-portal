import React from 'react';
import UnassignedSegmentsModal from '../index';
import {cleanup, render, waitFor} from '@testing-library/react';
import {noop} from 'lodash';
import {useModal} from '@clayui/modal';

jest.unmock('react-dom');

// The welcome screen has no header, so wait for the body instead.
const Harness = () => {
	const {observer} = useModal({defaultOpen: true});

	return <UnassignedSegmentsModal observer={observer} onClose={noop} />;
};

describe('UnassignedSegmentsModal', () => {
	afterEach(cleanup);

	it('renders', async () => {
		render(<Harness />);

		await waitFor(() =>
			expect(document.querySelector('.modal-body')).not.toBeNull()
		);

		expect(document.body).toMatchSnapshot();
	});
});
