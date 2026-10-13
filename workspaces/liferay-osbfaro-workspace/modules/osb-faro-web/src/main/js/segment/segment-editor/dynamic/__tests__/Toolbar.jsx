import React from 'react';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router';
import {Toolbar} from '../Toolbar';

jest.unmock('react-dom');

const renderToolbar = (props = {}) =>
	render(
		<MemoryRouter>
			<Toolbar channelId='321' groupId='123' {...props} />
		</MemoryRouter>
	);

describe('Toolbar', () => {
	afterEach(() => {
		jest.clearAllMocks();

		cleanup();
	});

	it('should render the new segment title and actions', () => {
		renderToolbar();

		expect(
			screen.getByRole('heading', {level: 1, name: /new segment/i})
		).toBeInTheDocument();
		expect(screen.getByRole('link', {name: /cancel/i})).toBeInTheDocument();
		expect(screen.getByRole('button', {name: /save segment/i})).toHaveAttribute(
			'type',
			'submit'
		);
	});

	it('should render the edit segment title and the delete action when editing', () => {
		const onDeleteSegment = jest.fn();

		renderToolbar({id: '1', onDeleteSegment});

		expect(
			screen.getByRole('heading', {level: 1, name: /edit segment/i})
		).toBeInTheDocument();

		const deleteButton = screen.getByRole('button', {
			name: /delete segment/i
		});

		expect(deleteButton).toHaveAttribute('type', 'button');

		fireEvent.click(deleteButton);

		expect(onDeleteSegment).toHaveBeenCalledTimes(1);
	});

	it('should not render the membership controls', () => {
		renderToolbar({id: '1', onDeleteSegment: jest.fn()});

		expect(screen.queryByRole('switch')).not.toBeInTheDocument();
		expect(screen.queryByText(/total members/i)).not.toBeInTheDocument();
		expect(
			screen.queryByTestId('preview-criteria-button')
		).not.toBeInTheDocument();
	});
});
