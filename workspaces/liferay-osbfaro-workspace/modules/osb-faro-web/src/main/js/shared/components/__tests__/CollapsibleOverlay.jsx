import CollapsibleOverlay from '../CollapsibleOverlay';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('CollapsibleOverlay', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(<CollapsibleOverlay title='foo title' />);
		expect(container).toMatchSnapshot();
	});

	it('should render as not visible', () => {
		const {container} = render(<CollapsibleOverlay visible={false} />);

		expect(
			container.querySelector('.collapsible-overlay-root')
		).not.toBeVisible();
	});

	it('should call onClose when the close button is clicked', () => {
		const onCloseSpy = jest.fn();
		const {getByLabelText} = render(
			<CollapsibleOverlay onClose={onCloseSpy} visible />
		);

		fireEvent.click(getByLabelText(/^close$/i));

		expect(onCloseSpy).toHaveBeenCalled();
	});
});
