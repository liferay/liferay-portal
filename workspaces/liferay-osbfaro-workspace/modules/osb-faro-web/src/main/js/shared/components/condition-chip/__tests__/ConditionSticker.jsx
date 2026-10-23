import ConditionSticker from '../ConditionSticker';
import React from 'react';
import {DataTypes} from 'shared/types/DataTypes';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('ConditionSticker', () => {
	it('shows the same icon for every condition of a data type', () => {
		const {container} = render(
			<>
				<ConditionSticker dataType={DataTypes.String} />
				<ConditionSticker dataType={DataTypes.String} />
				<ConditionSticker dataType={DataTypes.Number} />
			</>
		);

		expect(container.querySelectorAll('.lexicon-icon-text')).toHaveLength(
			2
		);
		expect(
			container.querySelectorAll('.lexicon-icon-integer')
		).toHaveLength(1);
	});

	it('shows the given symbol when there is no data type', () => {
		const {container} = render(<ConditionSticker symbol="click" />);

		expect(
			container.querySelector('.lexicon-icon-click')
		).toBeInTheDocument();
	});

	it('uses the primary style by default', () => {
		const {container} = render(<ConditionSticker symbol="click" />);

		expect(container.querySelector('.sticker-primary')).toBeInTheDocument();
	});

	it('shows an outlined green check for success', () => {
		const {container} = render(
			<ConditionSticker displayType="success" symbol="check-circle" />
		);

		expect(container.querySelector('.sticker-success')).toBeInTheDocument();
		expect(
			container.querySelector('.lexicon-icon-check-circle')
		).toBeInTheDocument();
	});

	it('shows an outlined orange exclamation for warning', () => {
		const {container} = render(
			<ConditionSticker displayType="warning" symbol="exclamation-circle" />
		);

		expect(container.querySelector('.sticker-warning')).toBeInTheDocument();
		expect(
			container.querySelector('.lexicon-icon-exclamation-circle')
		).toBeInTheDocument();
	});
});
