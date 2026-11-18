import ClayButton from '@clayui/button';
import getCN from 'classnames';
import React from 'react';
import {Conjunctions, SUPPORTED_CONJUNCTION_OPTIONS} from '../utils/constants';

interface IConjunctionProps {
	className?: string;
	conjunctionName: string;
	sequential?: boolean;
}

/**
 * The separator drawn between the items of a group. It only repeats the
 * operator of the group visually: the group operator is set by the
 * `GroupOperator` picker, which is what assistive technology reads. In a
 * sequential group there is no picker, so the separator stays exposed to
 * announce the "Then" between the steps.
 */
class Conjunction extends React.Component<IConjunctionProps> {
	getConjunctionLabel(conjunctionName: string, sequential?: boolean) {
		if (sequential && conjunctionName === Conjunctions.And) {
			return Liferay.Language.get('then');
		}

		const conjunction = SUPPORTED_CONJUNCTION_OPTIONS.find(
			({name}) => name === conjunctionName
		);

		return conjunction ? conjunction.label : undefined;
	}

	render() {
		const {className, conjunctionName, sequential} = this.props;

		const classnames = getCN(
			'button-root',
			'conjunction-button',
			'conjunction-label',
			className
		);

		return (
			<div className="conjunction-container">
				<ClayButton
					aria-hidden={!sequential}
					className={classnames}
					disabled
					displayType="secondary"
					size="sm"
				>
					{this.getConjunctionLabel(conjunctionName, sequential)}
				</ClayButton>

				<div className="separator" />
			</div>
		);
	}
}

export default Conjunction;
