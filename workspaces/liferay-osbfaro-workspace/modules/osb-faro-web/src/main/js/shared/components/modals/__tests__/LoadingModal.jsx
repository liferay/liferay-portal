import LoadingModal from '../LoadingModal';
import React from 'react';
import {cleanup} from '@testing-library/react';
import {renderModal} from 'test/render-modal';

jest.unmock('react-dom');

const renderLoadingModal = props =>
	renderModal(observer => <LoadingModal observer={observer} {...props} />);

describe('LoadingModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		await renderLoadingModal();

		expect(document.body).toMatchSnapshot();
	});

	it('should render with a message and a title', async () => {
		await renderLoadingModal({message: 'foo', title: 'bar'});

		expect(document.body).toHaveTextContent('foo');
		expect(document.body).toHaveTextContent('bar');
	});

	it('should render an icon', async () => {
		await renderLoadingModal({icon: 'foo'});

		expect(document.querySelector('.icon-root')).toBeTruthy();
	});
});
