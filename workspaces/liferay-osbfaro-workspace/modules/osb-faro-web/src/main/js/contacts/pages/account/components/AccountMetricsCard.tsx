import Card from 'shared/components/Card';
import classNames from 'classnames';
import ErrorDisplay from 'shared/components/ErrorDisplay';
import Loading from 'shared/components/Loading';
import React from 'react';
import {getPluralMessage} from 'shared/util/lang';
import {Text} from '@clayui/core';
import {toThousands} from 'shared/util/numbers';

interface IAccountMetricsCardItem {
	label: string;
	singularLabel?: string;
	value?: number;
}

interface IAccountMetricsCardProps {
	className?: string;
	error?: boolean;
	loading?: boolean;
	metrics: IAccountMetricsCardItem[];
	refetch?: () => void;
	title: string;
}

const AccountMetricsCard: React.FC<IAccountMetricsCardProps> = ({
	className,
	error = false,
	loading = false,
	metrics,
	refetch,
	title,
}) => {
	const renderBody = () => {
		if (loading) {
			return <Loading />;
		}
		else if (error) {
			return <ErrorDisplay onReload={refetch} spacer />;
		}

		return (
			<div className="align-items-baseline c-gap-3 d-flex justify-content-between">
				{metrics.map(({label, singularLabel, value}) => (
					<div className="mt-4 text-nowrap" key={label}>
						<Text size={4}>
							{getPluralMessage(
								singularLabel || label,
								label,
								value,
								false,
								[
									<Text
										key="value"
										size={6}
										weight="semi-bold"
									>
										{toThousands(value ?? 0)}
									</Text>,
								]
							)}
						</Text>
					</div>
				))}
			</div>
		);
	};

	return (
		<Card className={classNames(className, 'flex-fill p-3 w-100')}>
			<Card.Title>
				<div className="font-weight-semi-bold">
					<Text size={5}>{title.toUpperCase()}</Text>
				</div>
			</Card.Title>

			<Card.Body noPadding>{renderBody()}</Card.Body>
		</Card>
	);
};

export default AccountMetricsCard;
