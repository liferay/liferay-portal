import OnboardingModal, {
	getOnboardingState,
} from 'shared/components/modals/onboarding-modal';
import React, {useContext, useEffect, useState} from 'react';
import SitesDashboardQuery from 'shared/queries/SitesDashboardQuery';
import {isArray} from 'lodash';
import {OnboardingContext} from 'shared/context/onboarding';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useModal} from '@clayui/modal';
import {useQuery} from '@apollo/client';

/**
 * Opens the onboarding for an administrator of a workspace with no data
 * sources, once, and resumes the onboarding a previous page left open. The
 * caller renders the returned modal.
 */
export const useOnboardingModal = ({
	groupId,
}: {
	groupId: string;
}): React.ReactNode => {
	const modal = useModal();

	const {onboardingTriggered, setOnboardingTriggered} =
		useContext(OnboardingContext);

	const currentUser = useCurrentUser();

	const {data, loading} = useQuery(SitesDashboardQuery, {
		variables: {type: null},
	});

	const [onboardingState] = useState(() => getOnboardingState(groupId));

	useEffect(() => {
		if (onboardingState) {
			modal.onOpenChange(true);
		}
		else if (
			!onboardingTriggered &&
			currentUser.isAdmin() &&
			!loading &&
			isArray(data?.dataSources) &&
			!data.dataSources.length
		) {
			modal.onOpenChange(true);

			setOnboardingTriggered();
		}
	}, [data]);

	if (!modal.open) {
		return null;
	}

	return (
		<OnboardingModal
			groupId={groupId}
			initialState={onboardingState}
			observer={modal.observer}
			onClose={() => modal.onOpenChange(false)}
		/>
	);
};
