import ActivatingDisplay from '../ActivatingDisplay';
import mockStore from 'test/mock-store';
import React from 'react';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

describe('ActivatingDisplay', () => {
	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<ActivatingDisplay groupId='123123' />
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});
});
