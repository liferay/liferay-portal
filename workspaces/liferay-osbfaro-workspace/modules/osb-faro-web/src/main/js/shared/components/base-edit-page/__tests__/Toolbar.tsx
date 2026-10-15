import React from 'react';
import Toolbar from '../Toolbar';
import {fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

jest.unmock('react-dom');

const renderToolbar = (children?: React.ReactNode) =>
	render(
		<MemoryRouter>
			<Toolbar backURL="/back" title="New Event Analysis">
				{children}
			</Toolbar>
		</MemoryRouter>
	);

describe('BaseEditPage.Toolbar', () => {
	it('renders a back link to the given URL', () => {
		renderToolbar();

		expect(screen.getByRole('link', {name: /back/i})).toHaveAttribute(
			'href',
			'/back'
		);
	});

	it('renders the title as the page heading', () => {
		renderToolbar();

		expect(
			screen.getByRole('heading', {level: 1, name: 'New Event Analysis'})
		).toBeInTheDocument();
	});

	it('renders the composed children', () => {
		renderToolbar(
			<>
				<Toolbar.Item>
					<span>{'Download Report'}</span>
				</Toolbar.Item>

				<Toolbar.Divider />
			</>
		);

		expect(screen.getByText('Download Report')).toBeInTheDocument();
		expect(screen.getByTestId('toolbar-divider')).toHaveClass(
			'border-left'
		);
	});

	it('groups the actions so they wrap together below the title', () => {
		const {container} = renderToolbar(
			<>
				<Toolbar.Cancel href="/list" />

				<Toolbar.Save label="Save" />
			</>
		);

		const actions = screen.getByTestId('toolbar-actions');

		expect(container.firstChild).toHaveClass('base-edit-page-toolbar');
		expect(actions).toHaveClass('base-edit-page-toolbar-actions');
		expect(actions).toContainElement(
			screen.getByRole('link', {name: /cancel/i})
		);
		expect(actions).toContainElement(
			screen.getByRole('button', {name: 'Save'})
		);
		expect(actions).not.toContainElement(
			screen.getByRole('heading', {level: 1})
		);
	});

	it('hides Cancel and the divider below the large breakpoint', () => {
		renderToolbar(
			<>
				<Toolbar.Divider />

				<Toolbar.Cancel href="/list" />

				<Toolbar.Save label="Save" />
			</>
		);

		expect(
			screen.getByRole('link', {name: /cancel/i}).closest('li')
		).toHaveClass('d-none', 'd-lg-flex');
		expect(screen.getByTestId('toolbar-divider')).toHaveClass(
			'd-none',
			'd-lg-flex'
		);
		expect(
			screen.getByRole('button', {name: 'Save'}).closest('li')
		).not.toHaveClass('d-none');
	});

	it('renders Cancel as a link to the given URL', () => {
		renderToolbar(<Toolbar.Cancel href="/list" />);

		expect(screen.getByRole('link', {name: /cancel/i})).toHaveAttribute(
			'href',
			'/list'
		);
	});

	it('renders Save as a submit button when asked', () => {
		renderToolbar(<Toolbar.Save label="Save Analysis" type="submit" />);

		expect(
			screen.getByRole('button', {name: 'Save Analysis'})
		).toHaveAttribute('type', 'submit');
	});

	it('submits the form Save names even when the toolbar is outside it', () => {
		const onSubmit = jest.fn((event) => event.preventDefault());

		render(
			<MemoryRouter>
				<Toolbar backURL="/back" title="Title">
					<Toolbar.Save
						form="analysisForm"
						label="Save Analysis"
						type="submit"
					/>
				</Toolbar>

				<form id="analysisForm" onSubmit={onSubmit} />
			</MemoryRouter>
		);

		fireEvent.click(screen.getByRole('button', {name: 'Save Analysis'}));

		expect(onSubmit).toHaveBeenCalledTimes(1);
	});

	it('disables Save and calls onClick only when enabled', () => {
		const onClick = jest.fn();

		const {rerender} = render(
			<MemoryRouter>
				<Toolbar backURL="/back" title="Title">
					<Toolbar.Save disabled label="Save" onClick={onClick} />
				</Toolbar>
			</MemoryRouter>
		);

		expect(screen.getByRole('button', {name: 'Save'})).toBeDisabled();

		rerender(
			<MemoryRouter>
				<Toolbar backURL="/back" title="Title">
					<Toolbar.Save label="Save" onClick={onClick} />
				</Toolbar>
			</MemoryRouter>
		);

		fireEvent.click(screen.getByRole('button', {name: 'Save'}));

		expect(onClick).toHaveBeenCalledTimes(1);
	});
});
