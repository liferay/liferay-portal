import IndividualLinkCell from '../IndividualLink';
import React from 'react';
import {render} from '@testing-library/react';
import {StaticRouter} from 'react-router';

jest.unmock('react-dom');

const DefaultComponent = props => (
	<StaticRouter>
		<IndividualLinkCell groupId='123' {...props} />
	</StaticRouter>
);

const tableRow = document.createElement('tr');

describe('IndividualLinkCell', () => {
	it('should render', () => {
		const {container} = render(
			<DefaultComponent
				data={{
					emailAddress: 'foo456@email',
					id: '456',
					name: 'Test Test'
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(container).toMatchSnapshot();
	});

	it('should render with individual data', () => {
		const {container} = render(
			<DefaultComponent
				data={{
					individualDeleted: false,
					individualEmail: 'foo456@email',
					individualId: 'individual456',
					individualName: 'individual Test'
				}}
			/>,
			{container: document.body.appendChild(tableRow)}
		);

		expect(container).toMatchSnapshot();
	});

	it('should NOT render as a link if the individual was deleted', () => {
		const {container} = render(
			<DefaultComponent
				data={{
					individualDeleted: true,
					individualEmail: 'foo456@email',
					individualId: 'individual456',
					individualName: 'individual Test'
				}}
			/>
		);

		expect(container.querySelectorAll('a').length).toEqual(0);
	});

	it('should render as a link if the individual is anonymous', () => {
		const individualId = 'individual456';

		const {container} = render(
			<DefaultComponent
				data={{
					individualDeleted: false,
					individualId,
					individualName: 'individual Test'
				}}
			/>
		);

		expect(container.querySelector('a').getAttribute('href')).toContain(
			individualId
		);
	});

	it('should render with individualId in the link', () => {
		const individualId = 'individual456';

		const {container} = render(
			<DefaultComponent
				data={{
					id: 'id123',
					individualDeleted: false,
					individualEmail: 'foo456@email',
					individualId,
					individualName: 'individual Test'
				}}
			/>
		);

		expect(container.querySelector('a').getAttribute('href')).toContain(
			individualId
		);
	});
});
