import CodeSnippet from '../CodeSnippet';
import mockStore from 'test/mock-store';
import React from 'react';
import {Provider} from 'react-redux';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

// The copy button announces a successful copy through the alert store.

const renderCodeSnippet = (props) =>
	render(
		<Provider store={mockStore()}>
			<CodeSnippet {...props} />
		</Provider>
	);

describe('CodeSnippet', () => {
	it('should render', () => {
		const {container} = renderCodeSnippet({
			codeLines: ['console.log(variable);']
		});

		expect(container).toMatchSnapshot();
	});

	it('should represent as a string when receiving a list of code lines', () => {
		const {container} = renderCodeSnippet({
			codeLines: [
				"Analytics.send('viewArticle', {",
				"'firstTest': '1',",
				'});'
			]
		});

		expect(container.querySelector('.copy-button')).toHaveAttribute(
			'data-clipboard-text',
			[
				"Analytics.send('viewArticle', {",
				"\n\t'firstTest': '1',",
				'\n});'
			].join('')
		);
	});
});
