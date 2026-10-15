import Main from '../Main';
import React from 'react';
import {render, screen} from '@testing-library/react';

jest.unmock('react-dom');

describe('BaseEditPage.Main', () => {
	it('holds the editor content in a centered container', () => {
		render(
			<Main className="lifecycle-form">
				<p>{'Editor'}</p>
			</Main>
		);

		const main = screen.getByText('Editor').parentElement;

		expect(main).toHaveClass(
			'base-edit-page-main',
			'container-fluid',
			'container-fluid-max-xl',
			'lifecycle-form'
		);
	});
});
