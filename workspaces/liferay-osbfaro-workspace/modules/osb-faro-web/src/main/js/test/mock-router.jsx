import React from 'react';
import {MemoryRouter} from 'react-router-dom';

export default {
	params: {},
	query: {}
};

export function withStaticRouter(Component) {
	return props => (
		<MemoryRouter>
			<Component {...props} />
		</MemoryRouter>
	);
}
