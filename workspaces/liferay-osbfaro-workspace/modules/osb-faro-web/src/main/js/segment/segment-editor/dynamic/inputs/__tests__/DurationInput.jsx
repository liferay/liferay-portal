import DurationInput from '../DurationInput';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Property} from 'shared/util/records';

describe('DurationInput', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<DurationInput
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid={false}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with data', () => {
		const {container} = render(
			<DurationInput
				displayValue='Time on Page'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched={false}
				valid
				value='123123123'
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ has-error when touched and not valid', () => {
		const {container} = render(
			<DurationInput
				displayValue='Time on Page'
				operatorRenderer={() => <div>{'operator'}</div>}
				property={new Property()}
				touched
				valid={false}
				value=''
			/>
		);

		expect(container.querySelector('.has-error')).toBeTruthy();
	});
});
