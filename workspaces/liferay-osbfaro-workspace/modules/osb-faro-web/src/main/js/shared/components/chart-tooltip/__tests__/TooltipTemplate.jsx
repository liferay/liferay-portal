import React from 'react';
import TooltipTemplate from '../TooltipTemplate';
import {render} from '@testing-library/react';

describe('TooltipTemplate', () => {
	it('should render', () => {
		const {container} = render(
			<TooltipTemplate children='TooltipTemplate' />
		);

		expect(container).toMatchSnapshot();
	});
});

describe('TooltipTemplate.Body', () => {
	it('should render', () => {
		const {container} = render(<TooltipTemplate.Body children='Body' />);

		expect(container).toMatchSnapshot();
	});
});

describe('TooltipTemplate.Column', () => {
	it('should render', () => {
		const {container} = render(
			<TooltipTemplate.Column children='Column' />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render as truncated', () => {
		const {container} = render(
			<TooltipTemplate.Column children='Column' truncated />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ right alignment', () => {
		const {container} = render(
			<TooltipTemplate.Column alignment='right' children='Column' />
		);

		expect(container).toMatchSnapshot();
	});

	it('should render w/ weight of light', () => {
		const {container} = render(
			<TooltipTemplate.Column children='Column' weight='light' />
		);

		expect(container).toMatchSnapshot();
	});
});

describe('TooltipTemplate.Header', () => {
	it('should render', () => {
		const {container} = render(
			<TooltipTemplate.Header children='Header' />
		);

		expect(container).toMatchSnapshot();
	});
});

describe('TooltipTemplate.Row', () => {
	it('should render', () => {
		const {container} = render(<TooltipTemplate.Row children='Row' />);

		expect(container).toMatchSnapshot();
	});
});
