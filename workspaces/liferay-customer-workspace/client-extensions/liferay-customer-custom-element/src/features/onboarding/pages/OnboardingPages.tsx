/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';
import {useAppPropertiesContext} from '~/contexts/AppPropertiesContext';
import InviteTeamMembersForm from '~/features/project/containers/InviteTeamMembersForm';
import SetupAnalyticsCloudForm from '~/features/project/containers/SetupAnalyticsCloudForm';
import SetupDXPCloudForm from '~/features/project/containers/SetupDXPCloudForm';
import useUserAccountsByAccountExternalReferenceCode from '~/features/project/pages/Project/TeamMembers/components/TeamMembersTable/hooks/useUserAccountsByAccountExternalReferenceCode';
import {getOrRequestToken} from '~/services/liferay/security/auth/getOrRequestToken';
import i18n from '~/utils/I18n';
import {PAGE_ROUTER_TYPES} from '~/utils/constants';
import {IAccountSubscriptionGroup} from '~/utils/types';

import ConfirmationMessageModal from '../../project/containers/ActivationStatus/LiferayExperienceCloud/components/ConfirmationMessageModal';
import SetupLiferayExperienceCloudForm from '../../project/containers/ActivationStatus/LiferayExperienceCloud/components/SetupLXCForm';
import {LIST_TYPES, PRODUCT_TYPES} from '../../project/utils/constants';
import {useOnboarding} from '../context';
import {ActionPayload, actionTypes} from '../context/reducer';
import {ONBOARDING_STEP_TYPES} from '../utils/constants';
import SuccessCloud from './SuccessCloud';
import Welcome from './Welcome';

interface IStepLayout {
	Component: React.JSX.Element;
	Skeleton?: React.JSX.Element;
}

const OnboardingPages: React.FC = () => {
	const [
		{
			analyticsCloudActivationSubmittedStatus,
			dxpCloudActivationSubmittedStatus,
			liferayExperienceCloudActivationSubmittedStatus,
			project,
			step,
			subscriptionGroups,
		},
		dispatch,
	] = useOnboarding();

	const [oAuthToken, setOAuthToken] = useState<string | undefined>();

	useEffect(() => {
		const fetchToken = async () => {
			const token = await getOrRequestToken();

			setOAuthToken(token);
		};

		fetchToken();
	}, []);

	const [supportSeatsCount] = useUserAccountsByAccountExternalReferenceCode(
		project?.accountKey
	);

	const {client} = useAppPropertiesContext();

	const subscriptionDXPCloud = subscriptionGroups?.find(
		(subscriptionGroup: IAccountSubscriptionGroup) =>
			subscriptionGroup.name === PRODUCT_TYPES.dxpCloud
	);

	const subscriptionAnalyticsCloud = subscriptionGroups?.find(
		(subscriptionGroup: IAccountSubscriptionGroup) =>
			subscriptionGroup.name === PRODUCT_TYPES.analyticsCloud
	);

	const subscriptionLiferayExperienceCloud = subscriptionGroups?.find(
		(subscriptionGroup: IAccountSubscriptionGroup) =>
			subscriptionGroup.name === PRODUCT_TYPES.liferayExperienceCloud
	);

	const pageHandle = () => {
		window.location.href = PAGE_ROUTER_TYPES.project(
			project?.accountKey || ''
		);
	};

	const invitesPageHandle = () => {
		if (
			subscriptionLiferayExperienceCloud &&
			!liferayExperienceCloudActivationSubmittedStatus
		) {
			dispatch({
				payload:
					ONBOARDING_STEP_TYPES.liferayExperienceCloud as unknown as ActionPayload,
				type: actionTypes.CHANGE_STEP as keyof typeof actionTypes,
			});
		}
		else {
			if (subscriptionDXPCloud && !dxpCloudActivationSubmittedStatus) {
				return dispatch({
					payload:
						ONBOARDING_STEP_TYPES.dxpCloud as unknown as ActionPayload,
					type: actionTypes.CHANGE_STEP as keyof typeof actionTypes,
				});
			}

			if (
				subscriptionAnalyticsCloud &&
				!analyticsCloudActivationSubmittedStatus
			) {
				return dispatch({
					payload:
						ONBOARDING_STEP_TYPES.analyticsCloud as unknown as ActionPayload,
					type: actionTypes.CHANGE_STEP as keyof typeof actionTypes,
				});
			}
		}

		pageHandle();
	};

	const dxpCloudPageHandle = () => {
		if (
			subscriptionAnalyticsCloud &&
			!analyticsCloudActivationSubmittedStatus
		) {
			dispatch({
				payload:
					ONBOARDING_STEP_TYPES.analyticsCloud as unknown as ActionPayload,
				type: actionTypes.CHANGE_STEP as keyof typeof actionTypes,
			});
		}

		pageHandle();
	};

	let availableSupportSeatsCount =
		(project &&
			Number(project.maxRequestors) - Number(supportSeatsCount)) ||
		0;

	if (availableSupportSeatsCount < 0) {
		availableSupportSeatsCount = 0;
	}

	const StepsLayout: Record<string, IStepLayout> = {
		[ONBOARDING_STEP_TYPES.invites]: {
			Component: (
				<InviteTeamMembersForm
					availableSupportSeatsCount={availableSupportSeatsCount}
					handlePage={invitesPageHandle}
					leftButton={i18n.translate('skip-for-now')}
					oAuthToken={oAuthToken}
					project={project}
				/>
			),
		},

		[ONBOARDING_STEP_TYPES.liferayExperienceCloud]: {
			Component: (
				<SetupLiferayExperienceCloudForm
					client={client}
					handleChangeForm={() => pageHandle()}
					handleOnLeftButtonClick={() => pageHandle()}
					leftButton={i18n.translate('skip-for-now')}
					project={project}
					subscriptionGroupLxcId={
						subscriptionLiferayExperienceCloud?.accountSubscriptionGroupId
					}
				/>
			),
		},
		[ONBOARDING_STEP_TYPES.successliferayExperienceCloud]: {
			Component: <ConfirmationMessageModal onClose={pageHandle} />,
		},
		[ONBOARDING_STEP_TYPES.dxpCloud]: {
			Component: (
				<SetupDXPCloudForm
					client={client}
					dxpVersion={project?.dxpVersion}
					handlePage={(isSuccess: boolean) => {
						if (isSuccess) {
							return dispatch({
								payload:
									ONBOARDING_STEP_TYPES.successDxpCloud as unknown as ActionPayload,
								type: actionTypes.CHANGE_STEP as keyof typeof actionTypes,
							});
						}

						dxpCloudPageHandle();
					}}
					leftButton={i18n.translate('skip-for-now')}
					listType={LIST_TYPES.dxpMajorVersion}
					project={project}
					subscriptionGroupId={
						subscriptionDXPCloud?.accountSubscriptionGroupId
					}
				/>
			),
		},
		[ONBOARDING_STEP_TYPES.successDxpCloud]: {
			Component: (
				<SuccessCloud
					handlePage={dxpCloudPageHandle}
					productType={PRODUCT_TYPES.dxpCloud}
				/>
			),
		},
		[ONBOARDING_STEP_TYPES.welcome]: {
			Component: <Welcome />,
			Skeleton: <Welcome.Skeleton />,
		},
		[ONBOARDING_STEP_TYPES.analyticsCloud]: {
			Component: (
				<SetupAnalyticsCloudForm
					client={client}
					handlePage={(isSuccess: boolean) => {
						if (isSuccess) {
							return dispatch({
								payload:
									ONBOARDING_STEP_TYPES.successAnalyticsCloud as unknown as ActionPayload,
								type: actionTypes.CHANGE_STEP as keyof typeof actionTypes,
							});
						}

						pageHandle();
					}}
					leftButton={i18n.translate('skip-for-now')}
					project={project}
					subscriptionGroupId={
						subscriptionAnalyticsCloud?.accountSubscriptionGroupId
					}
				/>
			),
		},
		[ONBOARDING_STEP_TYPES.successAnalyticsCloud]: {
			Component: (
				<SuccessCloud
					handlePage={pageHandle}
					productType={PRODUCT_TYPES.analyticsCloud}
				/>
			),
		},
	};

	if (project && subscriptionGroups) {
		const currentStep = StepsLayout[step];

		return (
			currentStep?.Component ?? (
				<div>Component not found for step: {step}</div>
			)
		);
	}

	return StepsLayout[ONBOARDING_STEP_TYPES.welcome]?.Skeleton ?? null;
};

export default OnboardingPages;
