import ListItem from '../ListItem';
import React from 'react';
import {render} from '@testing-library/react';

describe('BaseDropdownListItem', () => {
	it('should render', () => {
		const {container} = render(
			<ListItem
				item={{
					displayName: 'Test Display Name',
					id: '0',
					name: 'testName',
					type: 'custom'
				}}
				onClick={jest.fn()}
				onEditClick={jest.fn()}
			/>
		);

		expect(container.querySelector('.active')).toBeNull();
		expect(container.querySelector('.disabled')).toBeNull();
		expect(container.querySelector('.options-button')).toBeNull();
		expect(container).toMatchSnapshot();
	});

	it('should render with an Attribute', () => {
		const {container} = render(
			<ListItem
				item={{
					dataType: 'string',
					displayName: 'Filed Ticket',
					id: '4',
					name: 'filedTicket'
				}}
				onClick={jest.fn()}
				onEditClick={jest.fn()}
				onOptionsClick={jest.fn()}
			/>
		);

		expect(container.querySelector('.options-button')).toBeTruthy();
	});

	it('should render as disabled', () => {
		const {container} = render(
			<ListItem
				disabled
				item={{
					dataType: 'string',
					displayName: 'Filed Ticket',
					id: '4',
					name: 'filedTicket'
				}}
				onClick={jest.fn()}
				onEditClick={jest.fn()}
				onOptionsClick={jest.fn()}
			/>
		);

		expect(container.querySelector('.disabled')).toBeTruthy();
	});
});
