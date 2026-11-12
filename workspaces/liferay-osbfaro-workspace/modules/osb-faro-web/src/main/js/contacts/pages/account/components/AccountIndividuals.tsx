import Card from 'shared/components/Card';
import classNames from 'classnames';
import IndividualsDataSet from './IndividualsDataSet';
import React from 'react';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {Text} from '@clayui/core';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {useRangeSelectorChange} from 'shared/hooks/useRangeSelectorChange';

interface IAccountIndividualsProps {
	className?: string;
}

const AccountIndividuals: React.FC<IAccountIndividualsProps> = ({
	className,
}) => {
	const rangeSelectors = useQueryRangeSelectors();

	const {handleRangeSelectorChange} = useRangeSelectorChange();

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
