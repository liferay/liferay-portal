import AddDataSource from '../AddDataSource';
import React from 'react';
import {BrowserRouter} from 'react-router-dom';
import {cleanup, render} from '@testing-library/react';

describe('DistributionCard AddDataSource', () => {
	afterEach(cleanup);

	it('renders', () => {
		const {getByText} = render(
			<BrowserRouter>
				<AddDataSource groupId='123' />
			</BrowserRouter>
		);

		expect(
			getByText(Liferay.Language.get('add-data-source'))
		).toBeInTheDocument();
		expect(
			getByText(Liferay.Language.get('distribution'))
		).toBeInTheDocument();
	});
});
