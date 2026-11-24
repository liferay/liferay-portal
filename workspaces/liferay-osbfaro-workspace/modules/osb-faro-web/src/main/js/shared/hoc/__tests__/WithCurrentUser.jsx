import React from 'react';
import withCurrentUser from '../WithCurrentUser';
import {render} from '@testing-library/react';

jest.mock('shared/hooks/useCurrentUser', () => ({
	useCurrentUser: () => ({name: 'fooUser'})
}));

const MockComponent = ({className, currentUser}) => (
	<div className={className}>{currentUser.name}</div>
);

describe('WithCurrentUser', () => {
	it('should pass the currentUser to the WrappedComponent', () => {
		const WrappedComponent = withCurrentUser(MockComponent);

		const {getByText} = render(<WrappedComponent className='my-class' />);

		expect(getByText('fooUser')).toBeInTheDocument();
		expect(getByText('fooUser')).toHaveClass('my-class');
	});
});
