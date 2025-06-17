import BasePage from 'settings/components/BasePage';
import Card from 'shared/components/Card';
import ClayAlert from '@clayui/alert';
import ClayLayout from '@clayui/layout';
import moment from 'moment';
import React, {useState} from 'react';
import {AlertTypes} from 'shared/components/Alert';
import {compose, withProject} from 'shared/hoc';
import {CUSTOM_DATE_FORMAT, formatDateToTimeZone} from 'shared/util/date';
import {
	formatPlanData,
	isBasicPlan,
	PLAN_TYPES,
	PLANS
} from 'shared/util/subscriptions';
import {KnownIndividualsSession} from 'settings/components/usage-overview/KnownIndividualsSession';
import {PageViewsSession} from 'settings/components/usage-overview/PageViewsSession';
import {sub} from 'shared/util/lang';
import {SubscriptionDetails} from 'settings/components/usage-overview/SubscriptionDetails';
import {SubscriptionStatuses} from 'shared/util/constants';
import {Text} from '@clayui/core';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useTimeZone} from 'shared/hooks/useTimeZone';

const subscriptionStatuses = admin => ({
	[SubscriptionStatuses.Approaching]: {
		message: admin
			? Liferay.Language.get(
					'usage-limit-is-approaching.-please-contact-your-sales-representative-at-the-earliest-convenience'
			  )
			: Liferay.Language.get(
					'usage-limit-is-approaching.-please-contact-your-workspace-administrator-at-the-earliest-convenience'
			  ),
		title: Liferay.Language.get('alert')
	},
	[SubscriptionStatuses.Over]: {
		message: admin
			? Liferay.Language.get(
					'usage-limit-exceeded.-please-contact-your-sales-representative-to-upgrade-the-plan'
			  )
			: Liferay.Language.get(
					'usage-limit-exceeded.-please-contact-your-workspace-administrator-to-upgrade-the-plan'
			  ),
		title: Liferay.Language.get('alert')
	}
});

const getAlertStatusCode = currentPlan => {
	const individuals = currentPlan.metrics.get('individuals');
	const pageViews = currentPlan.metrics.get('pageViews');

	if (individuals.status !== pageViews.status) {
		if (individuals.status !== SubscriptionStatuses.Ok) {
			return individuals.status;
		}

		if (
			individuals.status === SubscriptionStatuses.Ok ||
			pageViews.status === SubscriptionStatuses.Over
		) {
			return pageViews.status;
		}
	}

	return null;
};

export const UsageOverview = ({groupId, project}) => {
	const [showAlert, setShowAlert] = useState(true);
	const currentUser = useCurrentUser();
	const admin = currentUser.isAdmin();
	const currentPlan = formatPlanData(project.faroSubscription);
	const {timeZoneId} = useTimeZone();
	const planType =
		PLAN_TYPES[currentPlan.name] || PLAN_TYPES[PLANS.basic.name];

	let pageActions = [];

	if (currentUser.isAdmin()) {
		pageActions = [
			{
				displayType: 'primary',
				href: 'https://support.liferay.com/',
				icon: {
					symbol: 'shortcut'
				},
				label: Liferay.Language.get('go-to-customer-portal'),
				target: '_blank'
			}
		];
	}

	const alertContent = subscriptionStatuses(admin)[
		getAlertStatusCode(currentPlan)
	];

	return (
		<BasePage
			groupId={groupId}
			key='UsageOverview'
			pageActions={pageActions}
			pageDescription={Liferay.Language.get(
				'plans-are-limited-by-the-total-amount-of-individuals-and-page-views'
			)}
			pageTitle={Liferay.Language.get('subscription-&-usage')}
		>
			{showAlert && alertContent && (
				<ClayLayout.Row>
					<ClayLayout.Col xl={12}>
						<ClayAlert
							displayType={AlertTypes.Warning}
							onClose={() => setShowAlert(false)}
							title={alertContent.title}
						>
							{alertContent.message}
						</ClayAlert>
					</ClayLayout.Col>
				</ClayLayout.Row>
			)}

			<ClayLayout.Row>
				<ClayLayout.Col xl={8}>
					<Card>
						<Card.Header>
							<Card.Title>
								{Liferay.Language.get('usage-limits')}
							</Card.Title>
						</Card.Header>

						<Card.Body>
							{isBasicPlan(currentPlan) ? (
								<p className='mb-0'>
									<Text color='secondary' size={3}>
										{Liferay.Language.get(
											'when-either-limit-is-exceeded-the-current-plan-will-have-to-be-upgraded-to-business-or-enterprise'
										)}
									</Text>
								</p>
							) : (
								<>
									<p>
										<Text color='secondary' size={3}>
											{Liferay.Language.get(
												'when-either-limit-is-exceeded-the-current-plan-will-either-have-to-be-upgraded-or-add-ons-will-have-to-be-purchased-to-accommodate-the-overage'
											)}
										</Text>
									</p>

									<p
										className='mb-0'
										data-testid='next-anniversary-date'
									>
										<Text color='secondary' size={3}>
											{sub(
												Liferay.Language.get(
													'plan-usage-resets-on-x'
												),
												[
													<b key='DATE'>
														{formatDateToTimeZone(
															moment(
																currentPlan.startDate
															).add(1, 'year'),
															CUSTOM_DATE_FORMAT,
															timeZoneId
														)}
													</b>
												],
												false
											)}
										</Text>
									</p>
								</>
							)}

							<KnownIndividualsSession
								currentPlan={currentPlan}
							/>

							<PageViewsSession currentPlan={currentPlan} />
						</Card.Body>
					</Card>
				</ClayLayout.Col>

				<ClayLayout.Col xl={4}>
					<SubscriptionDetails
						currentPlan={currentPlan}
						planType={planType}
					/>
				</ClayLayout.Col>
			</ClayLayout.Row>
		</BasePage>
	);
};

export default compose(withProject)(UsageOverview);
