import React from 'react';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';
import {User} from 'shared/util/records';
import {WorkspacesBasePage} from '../BasePage';

jest.unmock('react-dom');

const currentUser = new User({
	emailAddress: 'test@test.com',
	name: 'Test Test'
});

const DefaultComponent = props => (
	<StaticRouter>
		<WorkspacesBasePage
			currentUser={currentUser}
			title='Test Title'
			{...props}
		/>
	</StaticRouter>
);

describe('WorkspacesBasePage', () => {
	it('should render', () => {
		const {container} = render(
			<DefaultComponent
				details={[
					<p key='1'>{'Test Details'}</p>,
					<p key='2'>{'More Test Details'}</p>
				]}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render when details is jsx', () => {
		const {queryByText} = render(
			<DefaultComponent details={<b>{'Test0'}</b>} />
		);

		expect(queryByText('Test0')).toBeTruthy();
	});

	it('should render when details is a string', () => {
		const {queryByText} = render(
			<DefaultComponent details='Test Details' />
		);

		expect(queryByText('Test Details')).toBeTruthy();
	});

	it('should hide the logo container while the LDP plan check is loading', () => {
		const {container} = render(<DefaultComponent loadingLDPEnabled />);

		expect(
			container.querySelector('.logo-container').className
		).toContain('loading');
	});

	it('should show the logo container once the LDP plan check resolves', () => {
		const {container} = render(<DefaultComponent />);

		expect(
			container.querySelector('.logo-container').className
		).not.toContain('loading');
	});

	it('should render with back button', () => {
		const {queryByText} = render(
			<DefaultComponent
				backLabel='Back to Test'
				backURL='#'
				details={['Test Details. ', 'More Test Details']}
			/>
		);

		expect(queryByText('Back to Test')).toBeTruthy();
	});
});
