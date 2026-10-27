import mockStore from 'test/mock-store';
import NewRequestModal from '../NewRequestModal';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';
import {useModal} from '@clayui/modal';
import {Provider} from 'react-redux';
import {waitForLoadingToBeRemoved} from 'test/helpers';
import {waitForModal} from 'test/render-modal';

jest.unmock('react-dom');

const TestModal = () => {
	const {observer} = useModal({defaultOpen: true});

	return <NewRequestModal observer={observer} />;
};

describe('NewRequestModal', () => {
	afterEach(cleanup);

	it('renders', async () => {
		render(
			<Provider store={mockStore()}>
				<DndProvider backend={HTML5Backend}>
					<TestModal />
				</DndProvider>
			</Provider>
		);

		await waitForModal();

		await waitForLoadingToBeRemoved(document.body, {
			selector: '.loading-animation'
		});

		expect(document.body).toMatchSnapshot();
	});

	it('checks both Delete & Suppress checkbox when Delete is clicked', async () => {
		const {getByLabelText} = render(
			<Provider store={mockStore()}>
				<DndProvider backend={HTML5Backend}>
					<TestModal />
				</DndProvider>
			</Provider>
		);

		await waitForModal();

		await waitForLoadingToBeRemoved(document.body, {
			selector: '.loading-animation'
		});

		const deleteCheckbox = getByLabelText(/Delete/);
		const suppressCheckbox = getByLabelText(/Suppress/);

		expect(deleteCheckbox.checked).toBeFalse();
		expect(suppressCheckbox.checked).toBeFalse();

		fireEvent.click(deleteCheckbox);

		expect(deleteCheckbox.checked).toBeTrue();
		expect(suppressCheckbox.checked).toBeTrue();
	});

	it('checks if download sample CSV link is present', async () => {
		render(
			<Provider store={mockStore()}>
				<DndProvider backend={HTML5Backend}>
					<TestModal />
				</DndProvider>
			</Provider>
		);

		await waitForModal();

		await waitForLoadingToBeRemoved(document.body, {
			selector: '.loading-animation'
		});

		const anchor = document.querySelector('a[download="example_user_request.csv"]');

		expect(anchor).toHaveAttribute(
			'href',
			'data:text/octet-stream;charset=utf-8,user@example.com\nuser1@example.com\nuser2@example.com'
		);
	});
});
