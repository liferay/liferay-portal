import CopyButton from '../CopyButton';
import mockStore from 'test/mock-store';
import React from 'react';
import StopClickPropagation from 'shared/components/table/cell-components/StopClickPropagation';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

// A successful copy announces itself through the alert store.

const renderCopyButton = (children) => {
	const store = mockStore();

	return {...render(<Provider store={store}>{children}</Provider>), store};
};

const getAlertMessages = (store) =>
	store
		.getState()
		.get('alerts')
		.toList()
		.map((alert) => alert.get('message'))
		.toArray();

describe('CopyButton', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = renderCopyButton(
			<CopyButton displayType='secondary' text='foo' />
		);

		expect(container).toMatchSnapshot();
	});

	it('should copy the text when an ancestor stops the click propagation', () => {
		document.execCommand = jest.fn(() => true);

		const {getByRole} = renderCopyButton(
			<StopClickPropagation>
				<CopyButton displayType='secondary' text='foo' />
			</StopClickPropagation>
		);

		fireEvent.click(getByRole('button'));

		expect(document.execCommand).toHaveBeenCalledWith('copy');
	});

	it('announces a copy that succeeded', () => {
		document.execCommand = jest.fn(() => true);

		const {getByRole, store} = renderCopyButton(
			<CopyButton displayType='secondary' text='foo' />
		);

		fireEvent.click(getByRole('button'));

		expect(getAlertMessages(store)).toContain(
			'Copied successfully to the clipboard.'
		);
	});

	it('says nothing when the copy failed', () => {
		document.execCommand = jest.fn(() => false);

		const {getByRole, store} = renderCopyButton(
			<CopyButton displayType='secondary' text='foo' />
		);

		fireEvent.click(getByRole('button'));

		expect(getAlertMessages(store)).toEqual([]);
	});
});
