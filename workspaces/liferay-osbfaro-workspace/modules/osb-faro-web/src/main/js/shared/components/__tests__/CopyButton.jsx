import CopyButton from '../CopyButton';
import React from 'react';
import StopClickPropagation from 'shared/components/table/cell-components/StopClickPropagation';
import {cleanup, fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('CopyButton', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<CopyButton displayType='secondary' text='foo' />
		);

		expect(container).toMatchSnapshot();
	});

	it('should copy the text when an ancestor stops the click propagation', () => {
		document.execCommand = jest.fn(() => true);

		const {getByRole} = render(
			<StopClickPropagation>
				<CopyButton displayType='secondary' text='foo' />
			</StopClickPropagation>
		);

		fireEvent.click(getByRole('button'));

		expect(document.execCommand).toHaveBeenCalledWith('copy');
	});
});
