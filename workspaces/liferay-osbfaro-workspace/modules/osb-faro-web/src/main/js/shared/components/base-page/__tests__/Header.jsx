import Header from '../Header';
import mockStore from 'test/mock-store';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {cleanup, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {Routes} from 'shared/util/router';
import {MemoryRouter} from 'react-router';
jest.unmock('react-dom');

const WrappedComponent = props => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<Header {...props}></Header>
		</MemoryRouter>
	</Provider>
);

describe('BasePage.Header', () => {
	afterEach(cleanup);

	beforeAll(() => {
		delete window.location;

		window.location = {
			pathname: '/workspace/123/sites/contacts/accounts/321'
		};
	});

	it('renders Header', () => {
		const {container} = render(
			<WrappedComponent>{'Test Test'}</WrappedComponent>
		);

		expect(container).toMatchSnapshot();
	});

	it('renders Header w/ Breadcrumbs', () => {
		const {container} = render(
			<WrappedComponent
				breadcrumbs={[
					{active: false, label: 'Foo'},
					{active: true, label: 'Bar'}
				]}
			>
				{'Test Test'}
			</WrappedComponent>
		);

		expect(container).toMatchSnapshot();
	});

	describe('NavBar', () => {
		it('renders NavBar', () => {
			const {container} = render(
				<BrowserRouter>
					<Header.NavBar
						items={[
							{
								exact: true,
								label: 'Test',
								route: Routes.CONTACTS_ACCOUNT
							}
						]}
						routeParams={{
							groupId: '123',
							id: '321'
						}}
						routeQueries={{rangeKey: '30'}}
					/>
				</BrowserRouter>
			);

			expect(container).toMatchSnapshot();
		});

		it('renders NavBar with deprecated label', () => {
			const {container} = render(
				<BrowserRouter>
					<Header.NavBar
						items={[
							{
								deprecated: true,
								exact: true,
								label: 'Test',
								route: Routes.CONTACTS_ACCOUNT
							}
						]}
						routeParams={{
							groupId: '123',
							id: '321'
						}}
						routeQueries={{rangeKey: '30'}}
					/>
				</BrowserRouter>
			);

			const deprecatedBadge = document.querySelector(
				'.badge.badge-warning.badge-translucent'
			);

			expect(container).toMatchSnapshot();
			expect(deprecatedBadge).toBeInTheDocument();
			expect(deprecatedBadge).toHaveTextContent('DEPRECATED');
		});

		it('renders NavBar w/o routeQueries', () => {
			const {container} = render(
				<BrowserRouter>
					<Header.NavBar
						items={[
							{
								exact: true,
								label: 'Test',
								route: Routes.CONTACTS_ACCOUNT
							}
						]}
						routeParams={{groupId: '123', id: '321'}}
					/>
				</BrowserRouter>
			);

			expect(container).toMatchSnapshot();
		});
	});

	describe('PageActions', () => {
		it('renders PageActions', () => {
			const {container} = render(
				<Header.PageActions
					actions={[{label: 'Test Action'}]}
					label='Test Label'
				/>
			);

			expect(container).toMatchSnapshot();
		});
	});

	describe('Section', () => {
		it('renders Section', () => {
			const {container} = render(
				<Header.Section>{'Test Test'}</Header.Section>
			);

			expect(container).toMatchSnapshot();
		});
	});

	describe('TitleSection', () => {
		it('renders TitleSection w/ Title', () => {
			const {container} = render(
				<Header.TitleSection>{'Test Test'}</Header.TitleSection>
			);

			expect(container).toMatchSnapshot();
		});

		it('renders TitleSection w/ title & no children', () => {
			const {container} = render(
				<Header.TitleSection title='Test Title' />
			);

			expect(container).toMatchSnapshot();
		});

		it('renders TitleSection w/ subtitle', () => {
			const {container} = render(
				<Header.TitleSection subtitle='Test Subtitle'>
					{'Test Test'}
				</Header.TitleSection>
			);

			expect(container).toMatchSnapshot();
		});

		it('renders TitleSection w/ subtitle & label', () => {
			const {container} = render(
				<Header.TitleSection label subtitle='Test Label'>
					{'Test Test'}
				</Header.TitleSection>
			);

			expect(container).toMatchSnapshot();
		});

		it('renders TitleSection w/ label & no subtitle', () => {
			const {container} = render(
				<Header.TitleSection label>{'Test Test'}</Header.TitleSection>
			);

			expect(container).toMatchSnapshot();
			expect(document.querySelector('.label')).toBeNull();
		});
	});
});
