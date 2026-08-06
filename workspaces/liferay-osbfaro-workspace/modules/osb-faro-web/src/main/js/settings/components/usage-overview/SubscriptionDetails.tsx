import Card from 'shared/components/Card';
import getCN from 'classnames';
import React from 'react';
import {
	getPlanAddOns,
	getPropLabel,
	INDIVIDUALS,
	PAGEVIEWS,
	PLANS,
} from 'shared/util/subscriptions';
import {Text} from '@clayui/core';
import {toLocale} from 'shared/util/numbers';

interface IItemRendererProps {
	name: React.ReactNode;
	value: React.ReactNode;
}

const ItemRenderer = ({name, value}: IItemRendererProps) => (
	<div className="d-flex justify-content-between mb-1">
		<Text color="secondary" size={3} weight="semi-bold">
			{name}
		</Text>

		<Text color="secondary" size={3}>
			{value}
		</Text>
	</div>
);

interface ISubscriptionDetailsProps {
	currentPlan: any;
	planType: string;
}

export const SubscriptionDetails = ({
	currentPlan,
	planType,
}: ISubscriptionDetailsProps) => {
	const addOns = getPlanAddOns(currentPlan);

	const LDPEnabled = planType?.includes('Data Platform');

	const showAddOns = !!Object.keys(addOns).length && !LDPEnabled;

	return (
		<Card testId="subscription-details">
			<Card.Header className="py-2">
				<Card.Title>
					<Text color="secondary" size={3}>
						{Liferay.Language.get(
							'subscription-details'
						).toUpperCase()}
					</Text>
				</Card.Title>
			</Card.Header>

			<hr className="m-0" />

			<Card.Body className={getCN({'pb-0': showAddOns})}>
				<div className="mb-2">
					<Text size={3} weight="semi-bold">
						{getPropLabel(currentPlan.name)}
					</Text>
				</div>

				<ItemRenderer
					name={Liferay.Language.get('individuals')}
					value={toLocale(
						(PLANS as {[key: string]: any})[planType].limits[
							INDIVIDUALS
						]
					)}
				/>

				<ItemRenderer
					name={Liferay.Language.get('page-views')}
					value={toLocale(
						(PLANS as {[key: string]: any})[planType].limits[
							PAGEVIEWS
						]
					)}
				/>

				<ItemRenderer
					name={Liferay.Language.get('workspace-users')}
					value={Liferay.Language.get('unlimited')}
				/>
			</Card.Body>

			{showAddOns && (
				<>
					<Card.Header className="pb-2">
						<Card.Title>
							<Text color="secondary" size={3}>
								{Liferay.Language.get(
									'purchased-add-ons'
								).toUpperCase()}
							</Text>
						</Card.Title>
					</Card.Header>

					<Card.Body>
						<hr className="mt-0" />

						<div className="mb-2">
							<Text color="secondary" size={3}>
								{Liferay.Language.get(
									'tailor-limits-to-business-needs.-incrementally-increase-individual-or-page-view-limits-as-needed-without-committing-to-a-new-plan'
								)}
							</Text>
						</div>

						<ItemRenderer
							name={Liferay.Language.get('individuals')}
							value={addOns[INDIVIDUALS]}
						/>

						<ItemRenderer
							name={Liferay.Language.get('page-views')}
							value={addOns[PAGEVIEWS]}
						/>
					</Card.Body>
				</>
			)}
		</Card>
	);
};
