import Name from '../Name';
import React from 'react';
import {render} from '@testing-library/react';
import {MemoryRouter} from 'react-router';

jest.unmock('react-dom');

const FilledComponent = props => (
	<Name
		data={{id: 'test', name: 'foo'}}
		routeFn={({data: {id}}) => `/foo/${id}`}
		{...props}
	/>
);

describe('Name', () => {
	it('should render', () => {
		const {container} = render(
			<Name data={{name: 'foo'}} renderSecondaryInfo={() => 'bar'} />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render without a link in the name if disabled is true', () => {
		const {container, getAllByText} = render(<FilledComponent disabled />);

		expect(container.querySelector('a')).toBeFalsy();
		expect(getAllByText('foo')).toBeTruthy();
	});

	it('should render the name as a link if a route is passed', () => {
		const {container} = render(
			<MemoryRouter>
				<FilledComponent />
			</MemoryRouter>
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/foo/test'
		);
	});

	it('should render a link even if it has no asset title', () => {
		const {container} = render(
			<MemoryRouter>
				<FilledComponent data={{assetId: '123', name: 'foo'}} />
			</MemoryRouter>
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/foo/undefined'
		);
	});

	it('should render with secondary info', () => {
		const {container} = render(
			<MemoryRouter>
				<FilledComponent renderSecondaryInfo={() => 'bar'} />
			</MemoryRouter>
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with an icon', () => {
		const {container, getByText} = render(
			<Name
				data={{id: 'test', name: 'foo'}}
				renderIcon={() => <div>{'foo icon'}</div>}
			/>
		);

		expect(container.querySelector('.icon-container')).toBeTruthy();
		expect(getByText('foo icon')).toBeTruthy();
	});

	it('should render using the nameKey', () => {
		const {getByText} = render(
			<Name data={{id: 'test', title: 'foo'}} nameKey='title' />
		);

		expect(getByText('foo')).toBeTruthy();
	});

	it('should render the display name in TextTruncate if the tooltip prop is true', () => {
		const {getByText} = render(
			<Name
				data={{id: 'test', name: 'Name in text-truncate', title: 'foo'}}
				tooltip
			/>
		);

		expect(getByText('Name in text-truncate')).toHaveClass('text-truncate');
	});

	it.each([null, undefined, ''])(
		'should render a %p name as no data',
		name => {
			const {getByText} = render(<Name data={{id: 'test', name}} />);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it('should render a missing name as no data if the tooltip prop is true', () => {
		const {getByText} = render(
			<Name data={{id: 'test', name: null}} tooltip />
		);

		expect(getByText('–')).toHaveClass('text-secondary');
	});

	it.each([null, undefined, ''])(
		'should render a %p secondary info as no data',
		secondaryInfo => {
			const {getByText} = render(
				<Name
					data={{id: 'test', name: 'foo'}}
					renderSecondaryInfo={() => secondaryInfo}
				/>
			);

			expect(getByText('–')).toHaveClass('text-secondary');
		}
	);

	it('should render a zero secondary info', () => {
		const {getByText} = render(
			<Name
				data={{id: 'test', name: 'foo'}}
				renderSecondaryInfo={() => 0}
			/>
		);

		expect(getByText('0')).toBeTruthy();
	});
});
