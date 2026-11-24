import React from 'react';
import Toolbar from '../Toolbar';
import {fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

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
			screen.getByRole('heading', {
				level: 1,
				name: /^new event analysis$/i,
			})
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
		expect(screen.getByTestId('toolbar-divider')).toBeInTheDocument();
	});

	it('groups the actions so they wrap together below the title', () => {
		renderToolbar(
			<>
				<Toolbar.Cancel href="/list" />

				<Toolbar.Save label="Save" />
			</>
		);

		const actions = screen.getByTestId('toolbar-actions');

		expect(actions).toContainElement(
			screen.getByRole('link', {name: /cancel/i})
		);
		expect(actions).toContainElement(
			screen.getByRole('button', {name: /^save$/i})
		);
		expect(actions).not.toContainElement(
			screen.getByRole('heading', {level: 1})
		);
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
			screen.getByRole('button', {name: /^save analysis$/i})
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

		fireEvent.click(screen.getByRole('button', {name: /^save analysis$/i}));

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

		expect(screen.getByRole('button', {name: /^save$/i})).toBeDisabled();

		rerender(
			<MemoryRouter>
				<Toolbar backURL="/back" title="Title">
					<Toolbar.Save label="Save" onClick={onClick} />
				</Toolbar>
			</MemoryRouter>
		);

		fireEvent.click(screen.getByRole('button', {name: /^save$/i}));

		expect(onClick).toHaveBeenCalledTimes(1);
	});
});
