import Card from 'shared/components/Card';
import classNames from 'classnames';
import Loading from 'shared/components/Loading';
import React from 'react';
import {sub} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {toThousands} from 'shared/util/numbers';

interface IAccountMetricsCardItem {
	label: string;
	value?: number;
}

interface IAccountMetricsCardProps {
	className?: string;
	loading?: boolean;
	metrics: IAccountMetricsCardItem[];
	title: string;
}

const AccountMetricsCard: React.FC<IAccountMetricsCardProps> = ({
	className,
	loading = false,
	metrics,
	title,
}) => (
	<Card className={classNames(className, 'flex-fill p-3 w-100')}>
		<Card.Title>
			<div className="font-weight-semi-bold">
				<Text size={5}>{title.toUpperCase()}</Text>
			</div>
		</Card.Title>

		<Card.Body noPadding>
			{loading ? (
				<Loading />
			) : (
				<div className="align-items-baseline c-gap-3 d-flex justify-content-between">
					{metrics.map(({label, value}) => (
						<div className="mt-4 text-nowrap" key={label}>
							<Text size={4}>
								{sub(
									label,
									[
										<Text
											key="value"
											size={6}
											weight="semi-bold"
										>
											{toThousands(value ?? 0)}
										</Text>,
									],
									false
								)}
							</Text>
						</div>
					))}
				</div>
			)}
		</Card.Body>
	</Card>
);

export default AccountMetricsCard;
