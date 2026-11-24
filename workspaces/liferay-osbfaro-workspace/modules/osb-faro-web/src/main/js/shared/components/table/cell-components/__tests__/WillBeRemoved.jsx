import React from 'react';
import WillBeRemovedCell from '../WillBeRemoved';
import {render} from '@testing-library/react';

describe('WillBeRemovedCell', () => {
	it('should render', () => {
		const {container} = render(
			<WillBeRemovedCell
				data={{
					dataSourceIndividualPKs: []
				}}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render as will be removed', () => {
		const {container} = render(
			<WillBeRemovedCell
				data={{
					dataSourceIndividualPKs: ['test']
				}}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
