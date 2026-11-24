import React from 'react';
import withPaginationBar from '../WithPaginationBar';
import {compose} from 'redux';
import {render} from '@testing-library/react';
import {withStaticRouter} from 'test/mock-router';

describe('withPaginationBar', () => {
	it('renders', () => {
		const WrappedComponent = compose(
			withStaticRouter,
			withPaginationBar({defaultDelta: 10})
		)(() => <div>{'foobar'}</div>);

		const {container} = render(
			<WrappedComponent delta={5} page={1} total={15} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders w/o the pagination bar', () => {
		const WrappedComponent = compose(
			withStaticRouter,
			withPaginationBar({defaultDelta: 10})
		)(() => <div>{'foobar'}</div>);

		const {container} = render(<WrappedComponent total={0} />);

		expect(container).toMatchSnapshot();
	});
});
