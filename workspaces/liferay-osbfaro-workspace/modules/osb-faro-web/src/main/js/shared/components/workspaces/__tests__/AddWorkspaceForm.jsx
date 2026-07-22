import * as data from 'test/data';
import AddWorkspaceForm from '../AddWorkspaceForm';
import mockStore from 'test/mock-store';
import React from 'react';
import {BasePageContext} from '../BasePage';
import {MemoryRouter} from 'react-router-dom';
import {Project, User} from 'shared/util/records';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useBlocker: () => ({state: 'unblocked'}),
}));

const mockUser = new User(data.mockUser());

const DefaultComponent = props => (
	<Provider store={mockStore()}>
		<BasePageContext.Provider value={{currentUser: mockUser}}>
			<MemoryRouter>
				<AddWorkspaceForm
					onSubmit={jest.fn()}
					project={new Project(data.mockProject())}
					{...props}
				/>
			</MemoryRouter>
		</BasePageContext.Provider>
	</Provider>
);

describe('AddWorkspaceForm', () => {
	it('should render', () => {
		const {container} = render(<DefaultComponent />);
		expect(container).toMatchSnapshot();
	});

	it('should render the edit version', () => {
		const {container, getByTestId, queryByText} = render(
			<DefaultComponent
				editing
				project={data.getImmutableMock(Project, data.mockProject)}
			/>
		);

		expect(
			queryByText('You can only set your friendly workspace url once')
		).toBeNull();

		expect(container.querySelector('.select-root')).toBeDisabled();
		expect(queryByText('Save')).not.toBeNull();
		expect(getByTestId('server-location-input')).toBeDisabled();
	});

	it('should disable friendlyURL input if friendlyURL exists on Project', () => {
		const {getByTestId} = render(
			<DefaultComponent
				project={data.getImmutableMock(Project, data.mockProject, 1, {
					friendlyURL: 'foo'
				})}
			/>
		);

		expect(getByTestId('friendly-url-input')).toBeDisabled();
	});
});
