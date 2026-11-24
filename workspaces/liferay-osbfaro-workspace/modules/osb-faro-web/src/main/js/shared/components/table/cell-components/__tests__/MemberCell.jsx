import MemberCell from '../MemberCell';
import React from 'react';
import {render} from '@testing-library/react';

const LONG_EMAIL = 'abderrahmane.boutitaou@tamarisoft.com';

const FilledComponent = props => (
	<table>
		<tbody>
			<tr>
				<MemberCell
					data={{
						id: 'test',
						name: 'foo',
						properties: {email: LONG_EMAIL}
					}}
					{...props}
				/>
			</tr>
		</tbody>
	</table>
);

describe('MemberCell', () => {
	it('should render the name and the email', () => {
		const {getByText} = render(<FilledComponent />);

		expect(getByText('foo')).toBeTruthy();
		expect(getByText(LONG_EMAIL)).toBeTruthy();
	});

	it('should truncate the name and the email so they do not overlap the next column', () => {
		const {getByText} = render(<FilledComponent />);

		expect(getByText('foo').closest('.text-truncate')).toBeTruthy();
		expect(getByText(LONG_EMAIL).closest('.text-truncate')).toBeTruthy();
	});

	it('should render the name as a link if a route is passed', () => {
		const {container} = render(
			<FilledComponent routeFn={({data: {id}}) => `/foo/${id}`} />
		);

		expect(container.querySelector('a')).toHaveAttribute(
			'href',
			'/foo/test'
		);
	});

	it('should not render an email when the individual is anonymous', () => {
		const {queryByText} = render(
			<FilledComponent data={{id: 'test', name: 'foo', properties: {}}} />
		);

		expect(queryByText(LONG_EMAIL)).toBeFalsy();
	});

	it('should fall back to emailAddress when email is missing', () => {
		const {getByText} = render(
			<FilledComponent
				data={{
					id: 'test',
					name: 'foo',
					properties: {emailAddress: LONG_EMAIL}
				}}
			/>
		);

		expect(getByText(LONG_EMAIL)).toBeTruthy();
	});
});
