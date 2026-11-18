import getCN from 'classnames';
import React from 'react';
import RowActions from '../RowActions';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const WrappedComponent = props => (
	<td className={getCN(props.className)}>
		<RowActions {...props} />
	</td>
);

describe('RowActions', () => {
	it('should render', () => {
		const {getByRole} = render(
			<WrappedComponent actions={[{label: 'foo'}]} />
		);

		expect(getByRole('button', {name: /^menu$/i})).toBeInTheDocument();
	});

	it('should render with quick actions', () => {
		const {container} = render(
			<WrappedComponent
				actions={[{label: 'foo'}]}
				quickActions={[{iconSymbol: 'pencil', label: 'foo'}]}
			/>
		);

		expect(container.querySelector('.quick-action-menu')).toBeTruthy();
	});
});
