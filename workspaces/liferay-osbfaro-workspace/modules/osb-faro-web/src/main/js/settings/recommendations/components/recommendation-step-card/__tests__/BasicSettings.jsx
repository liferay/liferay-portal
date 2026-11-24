import BasicSettings from '../BasicSettings';
import Form from 'shared/components/form';
import React from 'react';
import {render} from '@testing-library/react';

describe('BasicSettings', () => {
	it('should render', () => {
		const {container} = render(
			<Form initialValues={{name: ''}}>
				{({errors}) => (
					<Form.Form>
						<BasicSettings
							errors={errors}
							name='Test'
							onSetDisabled={jest.fn()}
						/>
					</Form.Form>
				)}
			</Form>
		);

		expect(container).toMatchSnapshot();
	});
});
