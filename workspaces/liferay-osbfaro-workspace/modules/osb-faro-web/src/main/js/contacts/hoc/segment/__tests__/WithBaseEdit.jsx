jest.unmock('shared/components/DocumentTitle');

import * as data from 'test/data';
import React from 'react';
import withBaseEdit from '../WithBaseEdit';
import {compose} from 'redux';
import {renderWithStore} from 'test/mock-store';
import {withChannelProvider} from 'test/mock-channel-context';
import {withStaticRouter} from 'test/mock-router';

class TestComponent extends React.Component {
	render() {
		return <div>{'foobar'}</div>;
	}
}

describe('WithBaseEdit', () => {
	it('should render the wrapped component', () => {
		const WrappedComponent = compose(
			withChannelProvider,
			withStaticRouter,
			withBaseEdit
		)(TestComponent);

		const {getByText} = renderWithStore(WrappedComponent, {
			groupId: '23',
			id: '123',
			segment: data.mockSegment()
		});

		expect(getByText('foobar')).toBeInTheDocument();
	});
});
