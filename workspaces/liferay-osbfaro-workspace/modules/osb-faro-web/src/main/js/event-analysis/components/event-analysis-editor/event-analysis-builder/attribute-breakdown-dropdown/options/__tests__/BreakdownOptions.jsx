import BreakdownOptions from '../index';
import React from 'react';
import {AttributesProvider} from 'event-analysis/components/event-analysis-editor/context/attributes';
import {render} from '@testing-library/react';

describe('FilterOptions', () => {
	it('should render', () => {
		const {container} = render(
			<AttributesProvider>
				<BreakdownOptions
					attribute={{
						dataType: 'DATE',
						displayName: 'Filed Ticket',
						id: '4',
						name: 'filedTicket'
					}}
					onActiveChange={jest.fn()}
					onAttributeChange={jest.fn()}
					onEditClick={jest.fn()}
				/>
			</AttributesProvider>
		);

		expect(container).toMatchSnapshot();
	});
});
