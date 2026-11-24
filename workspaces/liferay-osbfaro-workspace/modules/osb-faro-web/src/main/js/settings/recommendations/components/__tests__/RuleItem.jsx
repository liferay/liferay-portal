import React from 'react';
import RuleItem from '../RuleItem';
import {render} from '@testing-library/react';

describe('RuleItem', () => {
	it('should render', () => {
		const {container} = render(
			<RuleItem
				name='includeFilter'
				value='og:url = https://www.liferay.com'
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
