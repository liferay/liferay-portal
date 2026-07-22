import BasePage from '../base-page/BasePage';
import mockStore from 'test/mock-store';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useParams: () => ({
		groupId: '23'
	})
}));

const mockBreadcrumbItems = [
	{
		active: true,
		href: 'test123',
		label: 'testLabelBreadcrumbItems'
	}
];

const mockPageActions = [
	{
		actions: [{label: 'Test Action'}],
		label: 'testLabelPageActions '
	}
];

describe('BasePage', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<BasePage />
				</MemoryRouter>
			</Provider>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with a description', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<BasePage pageDescription='testPageDescription' />
				</MemoryRouter>
			</Provider>
		);

		expect(getByText('testPageDescription')).toBeTruthy();
	});

	it('should render with a breadcrumb', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<BasePage breadcrumbItems={mockBreadcrumbItems} />
				</MemoryRouter>
			</Provider>
		);

		expect(getByText('testLabelBreadcrumbItems')).toBeTruthy();
	});

	it('should render with a page action', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<BasePage groupId='23' pageActions={mockPageActions} />
				</MemoryRouter>
			</Provider>
		);

		expect(getByText('testLabelPageActions')).toBeTruthy();
	});

	it('should render with a title', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<BasePage groupId='23' pageTitle='testPageTitle' />
				</MemoryRouter>
			</Provider>
		);

		expect(getByText('testPageTitle')).toBeTruthy();
	});

	it('should render with an inline title with subtitle', () => {
		const {getByText} = render(
			<Provider store={mockStore()}>
				<MemoryRouter>
					<BasePage
						groupId='23'
						pageTitle='testPageTitle'
						subTitle='mysubtitle'
					/>
				</MemoryRouter>
			</Provider>
		);

		expect(getByText('testPageTitle').parentElement).toHaveClass(
			'title-text'
		);
		expect(getByText('mysubtitle').parentElement).toBeTruthy();
	});
});
