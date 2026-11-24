import Main from '../Main';
import React from 'react';
import {render, screen} from '@testing-library/react';

describe('BaseEditPage.Main', () => {
	it('keeps the given class name', () => {
		render(
			<Main className="lifecycle-form">
				<p>{'Editor'}</p>
			</Main>
		);

		const main = screen.getByText(/^editor$/i).parentElement;

		expect(main).toHaveClass('lifecycle-form');
	});
});
