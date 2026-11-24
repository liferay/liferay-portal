import MetadataTag from '../MetadataTag';
import React from 'react';
import {render} from '@testing-library/react';

describe('MetadataTag', () => {
	it('should render', () => {
		const {container} = render(<MetadataTag value='og:url' />);

		expect(container).toMatchSnapshot();
	});
});
