import InfoCardPopover from '../InfoCardPopover';
import React from 'react';
import {render} from '@testing-library/react';

describe('InfoCardPopover', () => {
	it('render', () => {
		const {container} = render(
			<InfoCardPopover
				dataType='STRING'
				description='Test description'
				name='Test Name'
				onEditClick={jest.fn()}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
