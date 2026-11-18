import React from 'react';
import UserDropdown, {Menus} from '../index';
import {act, cleanup, fireEvent, render, screen} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';

jest.unmock('react-dom');

const mockMenus = (): Menus => ({
	base: [
		{
			items: [
				{
					childMenuId: 'language',
					label: 'Language',
				},
				{
					externalLink: true,
					label: 'Link 1',
					url: '/link-1',
				},
				{
					label: 'Link 2',
					url: '/link-externo-2',
				},
			],
			subheaderLabel: 'test@test.com',
		},
	],
	language: [
		{
			items: [
				{
					label: 'English',
				},
				{
					label: 'Japanese',
				},
			],
		},
	],
});

const Wrapper = ({children}: {children: React.ReactNode}) => (
	<MemoryRouter>{children}</MemoryRouter>
);

describe('UserDropdown', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<Wrapper>
				<UserDropdown
					initialActiveMenu="base"
					menus={mockMenus()}
					userName="Test Test"
				/>
			</Wrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render dropdown menu when clicked', () => {
		const {container} = render(
			<Wrapper>
				<UserDropdown
					initialActiveMenu="base"
					menus={mockMenus()}
					userName="Test Test"
				/>
			</Wrapper>
		);

		const toggleButton = container.querySelector('.user-menu');

		fireEvent.click(toggleButton!);

		expect(document.body.querySelector('.user-menu-dropdown')).toBeTruthy();
		expect(document.body).toMatchSnapshot();
	});

	it('should descend into nested menu when child button is clicked', async () => {
		const {container} = render(
			<Wrapper>
				<UserDropdown
					initialActiveMenu="base"
					menus={mockMenus()}
					userName="Test Test"
				/>
			</Wrapper>
		);

		const toggleButton = container.querySelector('.user-menu');

		fireEvent.click(toggleButton!);

		const languageBtn = screen.getByText(/^language$/i);

		fireEvent.click(languageBtn);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(screen.getByText(/^english$/i)).toBeInTheDocument();
		expect(screen.getByText(/^japanese$/i)).toBeInTheDocument();
	});

	it('should ascend from nested menu when back button is clicked', async () => {
		const {container} = render(
			<Wrapper>
				<UserDropdown
					initialActiveMenu="base"
					menus={mockMenus()}
					userName="Test Test"
				/>
			</Wrapper>
		);

		const toggleButton = container.querySelector('.user-menu');

		fireEvent.click(toggleButton!);

		const languageBtn = screen.getByText(/^language$/i);

		fireEvent.click(languageBtn);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(screen.getByText(/^english$/i)).toBeInTheDocument();

		const backBtn = screen.getByLabelText(/^back$/i);

		fireEvent.click(backBtn);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(screen.getByText(/^language$/i)).toBeInTheDocument();
	});

	it('should go back to the initialActiveMenu on close', async () => {
		const {container} = render(
			<Wrapper>
				<UserDropdown
					initialActiveMenu="base"
					menus={mockMenus()}
					userName="Test Test"
				/>
			</Wrapper>
		);

		const toggleButton = container.querySelector('.user-menu');

		fireEvent.click(toggleButton!);

		const languageBtn = screen.getByText(/^language$/i);

		fireEvent.click(languageBtn);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(screen.getByText(/^english$/i)).toBeInTheDocument();

		// Close

		fireEvent.click(toggleButton!);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		// Open again

		fireEvent.click(toggleButton!);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(screen.getByText(/^language$/i)).toBeInTheDocument();
	});
});
