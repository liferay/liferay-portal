import IndividualAttributesModal from '../IndividualAttributesModal';
import React from 'react';
import {cleanup, screen} from '@testing-library/react';
import {mockIndividualAttributes} from 'test/data';
import {renderModal} from 'test/render-modal';

const {dataSources, fieldName} = mockIndividualAttributes();

jest.unmock('react-dom');

describe('IndividualAttributesModal', () => {
	afterEach(cleanup);

	it('should render', async () => {
		await renderModal(observer => (
			<IndividualAttributesModal
				dataSources={dataSources}
				fieldName={fieldName}
				observer={observer}
			/>
		));

		expect(document.body).toMatchSnapshot();
	});

	it('should render the field name as the title', async () => {
		await renderModal(observer => (
			<IndividualAttributesModal
				dataSources={dataSources}
				fieldName={fieldName}
				observer={observer}
			/>
		));

		expect(
			screen.getByRole('heading', {name: fieldName})
		).toBeInTheDocument();
	});
});
