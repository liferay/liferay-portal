import Card from 'shared/components/Card';
import classNames from 'classnames';
import IndividualsDataSet from './IndividualsDataSet';
import React from 'react';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {removeUriQueryParam, setUriQueryValues} from 'shared/util/router';
import {Text} from '@clayui/core';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';

interface IAccountIndividualsProps {
	className?: string;
}

const AccountIndividuals: React.FC<IAccountIndividualsProps> = ({
	className,
}) => {
	const history = useHistoryAdapter();

	const rangeSelectors = useQueryRangeSelectors();

	const handleRangeSelectorChange = ({
		rangeEnd,
		rangeKey,
		rangeStart,
	}: RangeSelectors) => {

		// The bounds belong to a custom range only, so drop the previous ones
		// before writing rather than leaving them behind to narrow a preset.

		history.push(
			setUriQueryValues(
				pickBy({rangeEnd, rangeKey, rangeStart}),
				removeUriQueryParam(
					window.location.href,
					'rangeEnd',
					'rangeStart'
				)
			)
		);
	};

	return (
		<Card className={classNames(className)} minHeight={300}>
			<Card.Title className="align-items-center d-flex justify-content-between mt-3 mx-3">
				<Text size={4} weight="semi-bold">
					<span className="text-uppercase">
						{Liferay.Language.get('account-individuals')}
					</span>
				</Text>

				<DropdownRangeKey
					legacy={false}
					onRangeSelectorChange={handleRangeSelectorChange}
					rangeSelectors={rangeSelectors}
				/>
			</Card.Title>
			<Card.Body noPadding>
				<div className="mt-1 mx-3">
					<Text color="secondary" size={3}>
						{Liferay.Language.get(
							'lists-all-individuals-associated-with-this-account'
						)}
					</Text>
				</div>
				<div className="mt-3">
					<IndividualsDataSet />
				</div>
			</Card.Body>
		</Card>
	);
};

export default AccountIndividuals;
