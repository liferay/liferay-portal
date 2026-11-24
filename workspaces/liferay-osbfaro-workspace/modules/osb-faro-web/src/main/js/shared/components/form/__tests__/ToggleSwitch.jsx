import toggleSwitch from '../ToggleSwitch';
import {render} from '@testing-library/react';

describe('ToggleSwitch', () => {
	it('should render', () => {
		const {container} = render(toggleSwitch({field: {}}));

		expect(container).toMatchSnapshot();
	});

	it('should render with an initial value', () => {
		const {getByTestId} = render(toggleSwitch({field: {value: true}}));

		expect(getByTestId('toggle-switch-input')).toHaveAttribute(
			'value',
			'true'
		);
	});
});
