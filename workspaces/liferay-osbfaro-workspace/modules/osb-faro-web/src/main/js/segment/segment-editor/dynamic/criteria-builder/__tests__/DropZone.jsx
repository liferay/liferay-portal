import DropZone from '../DropZone';
import React from 'react';
import {cleanup, render} from '@testing-library/react';
import {DndProvider} from 'react-dnd';
import {HTML5Backend} from 'react-dnd-html5-backend';

describe('DropZone', () => {
	afterEach(cleanup);

	it('should render', () => {
		const {container} = render(
			<DndProvider backend={HTML5Backend}>
				<DropZone />
			</DndProvider>
		);

		expect(container).toMatchSnapshot();
	});
});
