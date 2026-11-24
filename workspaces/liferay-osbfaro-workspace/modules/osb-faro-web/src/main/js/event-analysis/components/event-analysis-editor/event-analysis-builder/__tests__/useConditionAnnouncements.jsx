import React, {useState} from 'react';
import useConditionAnnouncements from '../useConditionAnnouncements';
import {AttributesContext} from '../../context/attributes';
import {render} from '@testing-library/react';

const attributes = {
	1: {displayName: 'Page Title', id: '1', name: 'pageTitle'},
	2: {displayName: '', id: '2', name: 'category'}
};

const event = {displayName: '', id: '10', name: 'assetClicked'};

const Announcement = ({event}) => {
	const [message, setMessage] = useState('');

	useConditionAnnouncements(event, setMessage);

	return <div>{message}</div>;
};

const WrappedComponent = ({breakdowns = {}, event, filters = {}}) => (
	<AttributesContext.Provider value={{attributes, breakdowns, filters}}>
		<Announcement event={event} />
	</AttributesContext.Provider>
);

describe('useConditionAnnouncements', () => {
	it('does not announce the initial conditions', () => {
		const {container} = render(
			<WrappedComponent
				breakdowns={{a: {attributeId: '1'}}}
				event={event}
			/>
		);

		expect(container).toHaveTextContent(/^$/);
	});

	it('announces an added event', () => {
		const {container, rerender} = render(<WrappedComponent />);

		rerender(<WrappedComponent event={event} />);

		expect(container).toHaveTextContent(/assetClicked/);
		expect(container).toHaveTextContent(/added/i);
	});

	it('announces added and removed breakdowns and filters by name', () => {
		const {container, rerender} = render(
			<WrappedComponent
				breakdowns={{a: {attributeId: '1'}}}
				event={event}
			/>
		);

		rerender(
			<WrappedComponent
				breakdowns={{a: {attributeId: '1'}}}
				event={event}
				filters={{b: {attributeId: '2'}}}
			/>
		);

		expect(container).toHaveTextContent(/added.*category/i);

		rerender(<WrappedComponent event={event} filters={{b: {attributeId: '2'}}} />);

		expect(container).toHaveTextContent(/removed.*page title/i);
	});
});
