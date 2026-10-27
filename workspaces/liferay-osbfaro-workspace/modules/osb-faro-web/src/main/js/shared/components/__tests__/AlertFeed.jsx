import React from 'react';
import {Alert} from 'shared/types';
import {AlertFeed} from '../AlertFeed';
import {cleanup, render} from '@testing-library/react';
import {fromJS} from 'immutable';
import {sub} from 'shared/util/lang';

jest.unmock('react-dom');

const defaultProps = {
	alertsIMap: fromJS({
		1: {
			alertType: Alert.Types.Default,
			id: 1,
			message: 'foo bar'
		}
	}),
	removeAlert: jest.fn()
};

describe('AlertFeed', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<AlertFeed {...defaultProps} />);

		expect(container).toMatchSnapshot();
	});

	it('should render with a message comprised of language keys with jsx substitutions', () => {
		const props = {
			alertsIMap: fromJS({
				1: {
					alertType: Alert.Types.Default,
					id: 1,
					message: sub(
						Liferay.Language.get('x-usd'),
						[<b key='TEST'>{'Foo'}</b>],
						false
					)
				}
			})
		};

		const {container} = render(<AlertFeed {...defaultProps} {...props} />);
		expect(container).toMatchSnapshot();
	});
});
