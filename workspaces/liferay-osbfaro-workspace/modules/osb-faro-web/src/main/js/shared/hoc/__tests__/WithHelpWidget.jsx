import * as data from 'test/data';
import mockStore from 'test/mock-store';
import React from 'react';
import withHelpWidget from '../WithHelpWidget';
import {fromJS} from 'immutable';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

const faroSubscription = fromJS(data.mockSubscription());
const wrappedComponentText = () => 'wrapped component text';

describe('withHelpWidget', () => {
	it('should render a wrapped component', () => {
		const WrappedComponent = withHelpWidget(wrappedComponentText);

		const {container} = render(
			<Provider store={mockStore()}>
				<WrappedComponent
					faroSubscriptionIMap={faroSubscription}
					groupId='123'
				/>
			</Provider>
		);

		expect(container.textContent).toBe('wrapped component text');
	});

	it('should render a HelpWidget Component', () => {
		const WrappedComponent = withHelpWidget(wrappedComponentText);

		const {container} = render(
			<Provider store={mockStore()}>
				<WrappedComponent
					faroSubscriptionIMap={faroSubscription}
					groupId='123'
				/>
			</Provider>
		);

		expect(container.querySelector('.help-widget-root')).toBeTruthy();
	});
});
