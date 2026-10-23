import ConditionChip from '../ConditionChip';
import React from 'react';
import {DataTypes} from 'shared/types/DataTypes';
import {fireEvent, render, screen} from '@testing-library/react';

jest.unmock('react-dom');

const defaultProps = {
	label: 'contains "manager"',
	name: 'Job Title',
	onRemove: jest.fn(),
	overline: 'Individual | Job Title',
	sticker: {dataType: DataTypes.String}
};

describe('ConditionChip', () => {
	it('renders the overline and the label in a labeled, focusable chip', () => {
		render(<ConditionChip {...defaultProps} />);

		const chip = screen.getByRole('group', {name: /job title/i});

		expect(chip).toHaveAttribute('tabindex', '0');
		expect(chip).toHaveTextContent(/contains "manager"/i);
		expect(chip.querySelector('.text-uppercase')).toHaveTextContent(
			/individual/i
		);
	});

	it('has no edit action', () => {
		render(<ConditionChip {...defaultProps} />);

		expect(
			screen.queryByRole('button', {name: /manager/i})
		).not.toBeInTheDocument();
	});

	it('wraps long labels instead of truncating them', () => {
		const {container} = render(
			<ConditionChip {...defaultProps} label={'a'.repeat(200)} />
		);

		expect(container.querySelector('.text-break')).toBeInTheDocument();
		expect(container.querySelector('.text-truncate')).toBeNull();
	});

	it('removes the condition from a labeled remove button', () => {
		const onRemove = jest.fn();

		render(<ConditionChip {...defaultProps} onRemove={onRemove} />);

		fireEvent.click(screen.getByRole('button', {name: /remove.job title/i}));

		expect(onRemove).toHaveBeenCalledTimes(1);
	});

	it('renders the drag handle only when one is given', () => {
		const {container, rerender} = render(
			<ConditionChip {...defaultProps} />
		);

		expect(container.querySelector('.drag-handle')).toBeNull();

		rerender(
			<ConditionChip
				{...defaultProps}
				handle={<span className="drag-handle" />}
			/>
		);

		expect(container.querySelector('.drag-handle')).toBeInTheDocument();
	});

	it('enters the chip with Space or Enter, moves between its controls with the arrows and leaves with Escape', () => {
		render(
			<ConditionChip
				{...defaultProps}
				handle={
					<button aria-label="drag" data-chip-control tabIndex={-1} />
				}
			/>
		);

		const chip = screen.getByRole('group', {name: /job title/i});
		const dragButton = screen.getByRole('button', {name: 'drag'});
		const removeButton = screen.getByRole('button', {
			name: /remove.job title/i
		});

		expect(removeButton).toHaveAttribute('tabindex', '-1');

		chip.focus();

		fireEvent.keyDown(chip, {key: ' '});

		expect(document.activeElement).toBe(dragButton);

		fireEvent.keyDown(dragButton, {key: 'ArrowRight'});

		expect(document.activeElement).toBe(removeButton);

		fireEvent.keyDown(removeButton, {key: 'ArrowLeft'});

		expect(document.activeElement).toBe(dragButton);

		fireEvent.keyDown(dragButton, {key: 'Escape'});

		expect(document.activeElement).toBe(chip);

		fireEvent.keyDown(chip, {key: 'Enter'});

		expect(document.activeElement).toBe(dragButton);
	});

	it('renders no remove button when the chip cannot be removed', () => {
		render(<ConditionChip {...defaultProps} onRemove={undefined} />);

		expect(
			screen.queryByRole('button', {name: /remove/i})
		).not.toBeInTheDocument();
	});

	it('selects the chip on click, but not from its controls', () => {
		const onSelect = jest.fn();

		render(<ConditionChip {...defaultProps} onSelect={onSelect} />);

		fireEvent.click(screen.getByRole('button', {name: /remove.job title/i}));

		expect(onSelect).not.toHaveBeenCalled();

		fireEvent.click(screen.getByText(/contains "manager"/i));

		expect(onSelect).toHaveBeenCalledTimes(1);
	});

	it('selects the chip with Enter and still enters its controls with Space', () => {
		const onSelect = jest.fn();

		render(<ConditionChip {...defaultProps} onSelect={onSelect} />);

		const chip = screen.getByRole('group', {name: /job title/i});

		chip.focus();

		fireEvent.keyDown(chip, {key: 'Enter'});

		expect(onSelect).toHaveBeenCalledTimes(1);
		expect(document.activeElement).toBe(chip);

		fireEvent.keyDown(chip, {key: ' '});

		expect(onSelect).toHaveBeenCalledTimes(1);
		expect(document.activeElement).toBe(
			screen.getByRole('button', {name: /remove.job title/i})
		);
	});

	it('marks only the selected chip as current', () => {
		const {rerender} = render(
			<ConditionChip {...defaultProps} onSelect={jest.fn()} />
		);

		const chip = screen.getByRole('group', {name: /job title/i});

		expect(chip).not.toHaveAttribute('aria-current');

		rerender(
			<ConditionChip {...defaultProps} onSelect={jest.fn()} selected />
		);

		expect(chip).toHaveAttribute('aria-current', 'true');
	});

	it('keeps the status of its sticker when selected', () => {
		const {container} = render(
			<ConditionChip
				{...defaultProps}
				onSelect={jest.fn()}
				selected
				sticker={{displayType: 'warning', symbol: 'exclamation-circle'}}
			/>
		);

		expect(container.querySelector('.sticker-warning')).toBeInTheDocument();
		expect(
			container.querySelector('.lexicon-icon-exclamation-circle')
		).toBeInTheDocument();
	});

	it('marks a chip whose sticker stays as is when selected', () => {
		const {rerender} = render(
			<ConditionChip {...defaultProps} onSelect={jest.fn()} selected />
		);

		const chip = screen.getByRole('group', {name: /job title/i});

		expect(chip).not.toHaveClass('condition-chip-static-sticker');

		rerender(
			<ConditionChip
				{...defaultProps}
				onSelect={jest.fn()}
				selected
				staticSticker
			/>
		);

		expect(chip).toHaveClass('condition-chip-static-sticker');
	});
});
