import BaseCard from '../index';
import mockStore from 'test/mock-store';
import React from 'react';
import {MemoryRouter} from 'react-router-dom';
import {MockedProvider} from '@apollo/client/testing';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const Wrapper = ({children}) => (
	<Provider store={mockStore()}>
		<MemoryRouter>
			<MockedProvider addTypename={false} freezeResults={false}>
				{children}
			</MockedProvider>
		</MemoryRouter>
	</Provider>
);

describe('BaseCard', () => {
	it('should render component', () => {
		const {container} = render(
			<Wrapper>
				<BaseCard className='my-component-classname' label='My title'>
					{() => <div>{'My body component'}</div>}
				</BaseCard>
			</Wrapper>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render component with custom Header', () => {
		const Header = () => <div>{'My custom header component'}</div>;

		const {getByText} = render(
			<Wrapper>
				<BaseCard Header={Header} label='My title'>
					{() => <div>{'My body component'}</div>}
				</BaseCard>
			</Wrapper>
		);

		expect(getByText('My body component')).toBeTruthy();
	});

	it('should return the props in Body component', () => {
		let customBodyProps = {};

		render(
			<Wrapper>
				<BaseCard label='My title'>
					{props => {
						customBodyProps = props;

						return <div>{'My custom body component'}</div>;
					}}
				</BaseCard>
			</Wrapper>
		);

		expect(customBodyProps).toEqual(
			expect.objectContaining({
				experienceId: null,
				filters: expect.any(Object),
				interval: expect.any(String),
				onChangeInterval: expect.any(Function),
				rangeSelectors: expect.any(Object),
				router: expect.any(Object)
			})
		);
	});

	it('should render a Card Header with an interval selector', () => {
		const {container, getByText} = render(
			<Wrapper>
				<BaseCard label='My title' showInterval>
					{() => <div>{'My body component'}</div>}
				</BaseCard>
			</Wrapper>
		);

		expect(container.querySelector('.interval-selector-root')).toBeTruthy();
		expect(getByText('D')).toBeTruthy();
		expect(getByText(/^w$/i)).toBeTruthy();
		expect(getByText(/^m$/i)).toBeTruthy();
	});
});
