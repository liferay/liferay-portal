import DeleteRecommendationModal from '../DeleteRecommendationModal';
import React from 'react';
import {cleanup, fireEvent, screen} from '@testing-library/react';
import {renderModal} from 'test/render-modal';
import {sub} from 'shared/util/lang';

jest.unmock('react-dom');

const renderDeleteRecommendation = (props) =>
	renderModal((observer) => (
		<DeleteRecommendationModal
			description="Delete it and its data"
			name="My Model"
			observer={observer}
			onClose={jest.fn()}
			onSubmit={jest.fn()}
			{...props}
		/>
	));

describe('DeleteRecommendationModal', () => {
	afterEach(cleanup);

	it('renders as a warning with the name in the title', async () => {
		await renderDeleteRecommendation();

		expect(document.querySelector('.modal-warning')).toBeTruthy();

		expect(
			screen.getByText(sub(Liferay.Language.get('deleting-x'), ['My Model']))
		).toBeInTheDocument();
	});

	it('shows the description and the message of one model', async () => {
		await renderDeleteRecommendation();

		expect(screen.getByText('Delete it and its data')).toBeInTheDocument();

		expect(
			screen.getByText(
				Liferay.Language.get(
					'components-using-this-model-will-need-to-be-reconfigured'
				)
			)
		).toBeInTheDocument();
	});

	it('shows the message of several models', async () => {
		await renderDeleteRecommendation({multiple: true});

		expect(
			screen.getByText(
				Liferay.Language.get(
					'components-using-these-models-will-need-to-be-reconfigured'
				)
			)
		).toBeInTheDocument();
	});

	it('submits and closes through its buttons', async () => {
		const onClose = jest.fn();
		const onSubmit = jest.fn();

		await renderDeleteRecommendation({onClose, onSubmit});

		fireEvent.click(screen.getByRole('button', {name: 'Delete'}));

		expect(onSubmit).toHaveBeenCalledTimes(1);

		fireEvent.click(screen.getByText('Cancel'));

		expect(onClose).toHaveBeenCalled();
	});
});
