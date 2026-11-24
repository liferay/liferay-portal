import EmbeddedAlertList from '../EmbeddedAlertList';
import React from 'react';
import {AlertTypes} from '../Alert';
import {render} from '@testing-library/react';

describe('EmbeddedAlertList', () => {
	it('should render', () => {
		const {container} = render(
			<EmbeddedAlertList
				alerts={[
					{
						iconSymbol: 'exclamation-full',
						message: 'foo bar',
						title: 'Test Title',
						type: AlertTypes.Danger
					}
				]}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
