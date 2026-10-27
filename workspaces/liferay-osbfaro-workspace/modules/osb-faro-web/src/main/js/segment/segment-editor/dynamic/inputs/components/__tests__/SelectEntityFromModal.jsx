import {
	getModalProps,
	isModalOpen,
	resetModalProps
} from 'test/mock-searchable-table-modal';

import mockStore from 'test/mock-store';
import React from 'react';
import SelectEntityFromModal from '../SelectEntityFromModal';
import {cleanup, fireEvent, render} from '@testing-library/react';
import {Property} from 'shared/util/records';
import {Provider} from 'react-redux';

jest.unmock('react-dom');

const defaultProps = {
	columns: [],
	entity: {name: 'fooEntity'},
	property: new Property(),
	renderEntity: ({name}) => name
};

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<SelectEntityFromModal {...defaultProps} {...props} />
	</Provider>
);

describe('SelectEntityFromModal', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should trigger modal when pressing Select button', () => {
		const {getByText} = render(<DefaultComponent />);

		fireEvent.click(getByText('Select'));

		jest.runAllTimers();

		expect(isModalOpen()).toBe(true);
	});

	it('should render with a preselected entity', () => {
		const {queryByText} = render(<DefaultComponent />);

		expect(queryByText('fooEntity')).toBeTruthy();
	});
});
