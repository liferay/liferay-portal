import Card from 'shared/components/Card';
import classNames from 'classnames';
import IndividualsDataSet from './IndividualsDataSet';
import React from 'react';
import {Text} from '@clayui/core';

interface IAccountIndividualsProps {
	className?: string;
}

const AccountIndividuals: React.FC<IAccountIndividualsProps> = ({
	className,
}) => (
	<Card className={classNames(className)} minHeight={300}>
		<Card.Title className="mt-3 mx-3">
			<Text size={4} weight="semi-bold">
				<span className="text-uppercase">
					{Liferay.Language.get('account-individuals')}
				</span>
			</Text>
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

export default AccountIndividuals;
