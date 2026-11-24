import BaseDropdown from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {act, fireEvent, render} from '@testing-library/react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<MockedProvider freezeResults={false}>
				<BaseDropdown
					onActiveChange={jest.fn()}
					trigger={<button data-testid='target'>{'click me'}</button>}
					{...props}
				>
					{() => <div>{'Child contents'}</div>}
				</BaseDropdown>
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('BaseDropdown', () => {
	it('should render', () => {
		const {container, getByTestId} = render(<DefaultComponent />);

		fireEvent.click(getByTestId('target'));

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(container).toMatchSnapshot();

		const dropdownMenu = document.body.getElementsByClassName(
			'event-analysis-dropdown-menu-root'
		)[0];

		expect(dropdownMenu).toMatchSnapshot();
	});

	it('should return the focus to the trigger when it closes', () => {
		const {getByTestId, getByText} = render(
			<BaseDropdown
				trigger={<button data-testid='target'>{'click me'}</button>}
			>
				{({setActive}) => (
					<button onClick={() => setActive(false)}>{'close'}</button>
				)}
			</BaseDropdown>
		);

		const trigger = getByTestId('target');

		trigger.focus();

		fireEvent.click(trigger);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		const closeButton = getByText('close');

		closeButton.focus();

		fireEvent.click(closeButton);

		closeButton.blur();

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(document.activeElement).toBe(trigger);
	});

	it('moves the focus to the enclosing section when the dropdown goes away', () => {
		const Section = ({showDropdown}) => (
			<section data-testid='section' tabIndex={-1}>
				{showDropdown && (
					<BaseDropdown
						trigger={
							<button data-testid='target'>{'click me'}</button>
						}
					>
						{() => <div>{'Child contents'}</div>}
					</BaseDropdown>
				)}
			</section>
		);

		const {getByTestId, rerender} = render(<Section showDropdown />);

		const trigger = getByTestId('target');

		trigger.focus();

		fireEvent.click(trigger);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		rerender(<Section showDropdown={false} />);

		act(() => {
			jest.advanceTimersByTime(250);
		});

		expect(document.activeElement).toBe(getByTestId('section'));
	});
});
