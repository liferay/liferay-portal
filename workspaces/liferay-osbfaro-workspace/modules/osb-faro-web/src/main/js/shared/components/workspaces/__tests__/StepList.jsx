import React from 'react';
import StepList from '../StepList';
import {render} from '@testing-library/react';

const DefaultComponent = props => (
	<StepList steps={['step1', 'step2']} {...props} />
);

describe('StepList', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);

		expect(container).toMatchSnapshot();
	});

	it('should render with secondaryInfo and steps', () => {
		const {getByText} = render(<DefaultComponent secondaryInfo='test' />);

		expect(getByText('test')).toBeTruthy();
	});
});
