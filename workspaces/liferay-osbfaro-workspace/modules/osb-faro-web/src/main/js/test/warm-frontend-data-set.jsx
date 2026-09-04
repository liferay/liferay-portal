import React from 'react';
import {act, render} from '@testing-library/react';
import {FrontendDataSet} from 'shared/components/FrontendDataSet';

/**
 * Resolves the lazily loaded FrontendDataSet once, so the assertions in a suite
 * can stay synchronous.
 *
 * `shared/components/FrontendDataSet` loads the data set with `React.lazy`, so
 * the first render in a file resolves it a microtask late and anything asserted
 * straight after that render misses it. `React.lazy` then stays resolved for
 * the rest of the file, so without this only the first test to render a data
 * set pays the cost -- which test that is depends on execution order, and the
 * rest pass on its back. Warming it in `beforeAll` makes every test in the file
 * behave the same whether the suite runs whole, shuffled, or one test at a
 * time.
 */
export const warmFrontendDataSet = async () => {
	const {unmount} = render(<FrontendDataSet id="warm-up" views={[]} />);

	await act(async () => {});

	unmount();
};
