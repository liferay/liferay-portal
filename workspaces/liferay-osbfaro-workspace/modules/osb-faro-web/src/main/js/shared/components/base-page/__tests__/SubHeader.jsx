import React from 'react';
import SubHeader from '../SubHeader';
import {cleanup, render} from '@testing-library/react';

describe('BasePage.SubHeader', () => {
	afterEach(cleanup);

	it('renders SubHeader', () => {
		const {container} = render(<SubHeader>{'Test Test'}</SubHeader>);

		expect(container).toMatchSnapshot();
	});
});
