import AddPropertyForm from '../AddPropertyForm';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

describe('DistributionCard AddPropertyForm', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {getByText} = render(<AddPropertyForm />);

		expect(
			getByText(
				Liferay.Language.get('add-a-breakdown-by-individual-attribute')
			)
		).toBeInTheDocument();
		expect(getByText(Liferay.Language.get('save'))).toBeInTheDocument();
	});

	it.skip('renders w/ context dropdown', () => {
		const {container} = render(<AddPropertyForm showContext />);

		expect(container.querySelector('.context-select')).toBeTruthy();
	});
});
