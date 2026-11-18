import BaseToolbar from '../BaseToolbar';
import React from 'react';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const renderBaseToolbar = (className?: string) =>
	render(
		<BaseToolbar className={className}>
			<BaseToolbar.Item>
				<BaseToolbar.Section>{'Settings'}</BaseToolbar.Section>
			</BaseToolbar.Item>
		</BaseToolbar>
	);

describe('BaseToolbar', () => {
	it('renders its items inside the toolbar navigation', () => {
		renderBaseToolbar();

		expect(screen.getByRole('navigation')).toContainElement(
			screen.getByText(/^settings$/i)
		);
	});

	it('stays fixed at the top with the bottom border of the top bar', () => {
		renderBaseToolbar();

		expect(screen.getByRole('navigation')).toHaveClass(
			'sticky-top',
			'toolbar-root'
		);
	});

	it('keeps the class names of the page that renders it', () => {
		renderBaseToolbar('mb-4');

		expect(screen.getByRole('navigation')).toHaveClass(
			'mb-4',
			'sticky-top',
			'toolbar-root'
		);
	});
});
