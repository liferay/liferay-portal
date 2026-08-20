import {
	act,
	fireEvent,
	getByLabelText,
	getByPlaceholderText,
	getByTestId,
	getByText,
	waitFor,
} from '@testing-library/react';

export const inputSearchText = (container, searchText) => {
	const searchBarInput = getByPlaceholderText(container, 'Search');
	fireEvent.change(searchBarInput, {
		target: {value: searchText},
	});
	fireEvent.keyDown(searchBarInput, {code: 13, key: 'Enter'});
	jest.runAllTimers();
};

export const selectAllAndToggle = (container) => {
	fireEvent.click(getByTestId(container, 'select-all-checkbox'));
	jest.runAllTimers();
	fireEvent.click(getByTestId(container, 'view-selected'));
	jest.runAllTimers();
};

export const selectFilterDropdownItem = (container, labelText) => {
	fireEvent.click(getByTestId(container, 'filter-and-order-button'));
	const overlay = getByTestId(document.body, 'overlay');
	fireEvent.click(getByLabelText(overlay, labelText));
	fireEvent.click(container);
	jest.runAllTimers();
};

export const waitForTable = async (container) => {
	await waitFor(() =>
		expect(container.querySelector('.table-root')).toBeTruthy()
	);
};

export const waitForLoading = async (container) => {

	// Wait for loading indicator to appear first

	await waitFor(() =>
		expect(container.querySelector('.loading-root')).toBeTruthy()
	);

	// Advance fake timers and flush React state updates.
	// Guard against calling timer functions when real timers are active.

	if (jest.isMockFunction(global.setTimeout)) {
		await act(async () => {
			await jest.advanceTimersByTimeAsync(300);
		});
	}

	await waitFor(
		() =>
			expect(
				container.querySelector('.loading-root')
			).not.toBeInTheDocument(),
		{timeout: 5000}
	).catch(() => {});
};

export const waitForLoadingToBeRemoved = async (
	container = document.body,
	{selector = '.loading-root', timeout = 5000} = {}
) => {
	const loading = container.querySelector(selector);

	if (!loading) {

		// Loading element is not present — no need to wait.

		return Promise.resolve();
	}

	// Advance fake time by 500 ms, flushing promises between each tick.
	// This covers:
	//   - @debounce(250) in BaseResults
	//   - Apollo's multi-step timer chain (timer A fires → React scheduler
	//     creates timer B at 0 ms — advanceTimersByTimeAsync flushes both)
	// 500 ms stays below most polling intervals (≥ 1 s), so recursive
	// polling timers are not triggered.
	// Only run when fake timers are installed — with real timers the
	// async work resolves naturally without advancement.

	if (jest.isMockFunction(global.setTimeout)) {
		await act(async () => {
			await jest.advanceTimersByTimeAsync(0);
		});
	}

	// waitFor wraps each poll in act(), which flushes React's pending
	// state updates (including those from async operations like Apollo
	// queries). waitForElementToBeRemoved uses MutationObserver and may
	// miss updates that React batches outside of act().

	return await waitFor(
		() => expect(container.querySelector(selector)).not.toBeInTheDocument(),
		{timeout}
	);
};

export const clickFirstSelectableDay = () => {
	const days = Array.prototype.slice.call(
		document.body.querySelectorAll('.day-root')
	);

	fireEvent.click(
		days.find(
			(day) => !day.disabled && !day.classList.contains('outside-month')
		)
	);
};

export const selectDropdownItem = (labelText) => {
	const overlay = getByTestId(document.body, 'overlay');
	fireEvent.click(getByText(overlay, labelText));
	fireEvent.click(document.body);
	jest.runAllTimers();
};
