import React, {useContext} from 'react';
import {act, cleanup, fireEvent, render, screen} from '@testing-library/react';
import {Conjunctions, PropertyTypes} from '../../utils/constants';
import {
	KeyboardMovementContext,
	KeyboardMovementProvider
} from '../keyboardMovement';

jest.unmock('react-dom');

const source = {
	criterion: {
		defaultValue: 'default',
		propertyName: 'download',
		type: PropertyTypes.Event
	},
	property: {label: 'Download'}
};

const criteria = {
	conjunctionName: Conjunctions.And,
	criteriaGroupId: 'root',
	items: [{propertyName: 'click', rowId: 'row_a', type: PropertyTypes.Event}]
};

let movement;

const Probe = () => {
	movement = useContext(KeyboardMovementContext);

	return null;
};

const renderProvider = (props = {}) => {
	const onChange = jest.fn();

	render(
		<KeyboardMovementProvider
			criteria={criteria}
			onChange={onChange}
			sequential={false}
			{...props}
		>
			<Probe />
		</KeyboardMovementProvider>
	);

	return onChange;
};

const getAnnouncement = () =>
	document.querySelector('[role="status"]').textContent;

describe('KeyboardMovementProvider', () => {
	afterEach(cleanup);

	it('adds the criterion right away when the canvas is empty', () => {
		const onChange = renderProvider({criteria: null});

		act(() => movement.startMovement(source));

		expect(onChange).toHaveBeenCalledTimes(1);
		expect(onChange.mock.calls[0][0].items[0].propertyName).toBe(
			'download'
		);
		expect(movement.source).toBeNull();
		expect(getAnnouncement()).toBe(
			'Download was added to the segment criteria.'
		);
	});

	it('targets the last position and explains how to move', () => {
		renderProvider();

		act(() => movement.startMovement(source));

		expect(movement.source).toBe(source);
		expect(movement.target).toMatchObject({
			groupId: 'root',
			index: 1,
			position: 'bottom'
		});
		expect(getAnnouncement()).toBe(
			'Use the arrow keys to choose where to add Download, and press enter to confirm or escape to cancel.'
		);
	});

	it('moves through the targets and drops the criterion with Enter', () => {
		const onChange = renderProvider();

		act(() => movement.startMovement(source));

		fireEvent.keyDown(window, {key: 'Home'});

		expect(movement.target).toMatchObject({index: 0, position: 'top'});
		expect(getAnnouncement()).toBe('Targeting Top of click');

		fireEvent.keyDown(window, {key: 'ArrowDown'});

		expect(movement.target).toMatchObject({index: 0, position: 'middle'});

		fireEvent.keyDown(window, {key: 'ArrowUp'});
		fireEvent.keyDown(window, {key: 'Enter'});

		expect(
			onChange.mock.calls[0][0].items.map(({propertyName}) => propertyName)
		).toEqual(['download', 'click']);
		expect(movement.source).toBeNull();
		expect(movement.target).toBeNull();
	});

	it('cancels the movement with Escape', () => {
		const onChange = renderProvider();

		act(() => movement.startMovement(source));

		fireEvent.keyDown(window, {key: 'Escape'});

		expect(onChange).not.toHaveBeenCalled();
		expect(movement.source).toBeNull();
		expect(screen.queryByText(/Targeting/)).toBeNull();
	});

	it('names a row target by its full condition', () => {
		renderProvider({
			criteria: {
				conjunctionName: Conjunctions.And,
				criteriaGroupId: 'root',
				items: [
					{
						operatorName: 'eq',
						propertyName: 'knownSinceDate',
						rowId: 'row_a',
						value: '2026-01-15'
					}
				]
			}
		});

		act(() => movement.startMovement(source));

		fireEvent.keyDown(window, {key: 'Home'});

		expect(screen.getByRole('status')).toHaveTextContent(
			/^targeting top of individual known since date .*2026/i
		);
	});

	it('names a group target by the sentence of its operator', () => {
		renderProvider({
			criteria: {
				conjunctionName: Conjunctions.And,
				criteriaGroupId: 'root',
				items: [
					{
						conjunctionName: Conjunctions.Or,
						criteriaGroupId: 'nested',
						items: [
							{propertyName: 'click', rowId: 'row_a'},
							{propertyName: 'view', rowId: 'row_b'}
						]
					}
				]
			}
		});

		act(() => movement.startMovement(source));

		fireEvent.keyDown(window, {key: 'Home'});

		expect(screen.getByRole('status')).toHaveTextContent(
			/^targeting top of any of these criteria must be met\.$/i
		);
	});
});
