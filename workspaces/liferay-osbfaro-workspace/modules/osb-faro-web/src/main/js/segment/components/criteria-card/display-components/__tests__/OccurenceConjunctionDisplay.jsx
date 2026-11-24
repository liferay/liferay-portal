import OccurenceConjunctionDisplay from '../OccurenceConjunctionDisplay';
import React from 'react';
import {RelationalOperators} from 'segment/segment-editor/dynamic/utils/constants';
import {render} from '@testing-library/react';

describe('OccurenceConjunctionDisplay', () => {
	it('should render', () => {
		const {container} = render(
			<OccurenceConjunctionDisplay
				operatorName={RelationalOperators.GT}
				value={13}
			/>
		);

		expect(container).toMatchSnapshot();
	});
});
