import React from 'react';
import SelectFieldInput from '../SelectFieldInput';
import {render} from '@testing-library/react';

describe('SelectFieldInput', () => {
	it('should render', () => {
		const {getByPlaceholderText} = render(
			<SelectFieldInput groupId='23' />
		);

		expect(
			getByPlaceholderText(Liferay.Language.get('select-field'))
		).toBeInTheDocument();
	});
});
