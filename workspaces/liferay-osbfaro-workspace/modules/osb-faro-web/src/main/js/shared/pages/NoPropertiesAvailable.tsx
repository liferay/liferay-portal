import * as API from 'shared/api';
import BasePage from 'shared/components/base-page';
import ClayButton from '@clayui/button';
import Constants, {Sizes} from 'shared/util/constants';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import OnboardingModal from 'shared/components/modals/onboarding-modal';
import React from 'react';
import {compose} from 'redux';
import {User} from 'shared/util/records';
import {useModal} from '@clayui/modal';
import {withRequest} from 'shared/hoc';

const {
	pagination: {cur: defaultPage},
} = Constants;

interface INoPropertiesAvailableProps
	extends React.HTMLAttributes<HTMLDivElement> {
	currentUser: User;
	dataSources: boolean;
	groupId: string;
}

const NoPropertiesAvailable: React.FC<INoPropertiesAvailableProps> = ({
	currentUser,
	dataSources,
	groupId,
}) => {
	const onboardingModal = useModal();

	const admin = currentUser.isAdmin();

	const description = admin
		? Liferay.Language.get(
				'to-get-your-workspace-set-up-you-will-need-to-create-a-property-and-sync-your-dxp-sites'
			)
		: Liferay.Language.get(
				'you-have-not-been-added-to-any-properties.-please-contact-your-analytics-cloud-administrator'
			);

	const title = admin
		? Liferay.Language.get('first-connect-your-dxp-sites')
		: Liferay.Language.get('no-properties-found');

	return (
		<BasePage
			className="no-properties-available-root"
			documentTitle={Liferay.Language.get('no-properties-available')}
		>
			<BasePage.Header breadcrumbs={[]} groupId={groupId}>
				<BasePage.Header.TitleSection
					title={Liferay.Language.get('no-properties-available')}
				/>
			</BasePage.Header>

			<BasePage.Body>
				<NoResultsDisplay
					description={
						<>
							<p>
								{dataSources
									? description
									: Liferay.Language.get(
											'first-complete-the-onboarding-to-get-your-workspace-set-up'
										)}
							</p>

							{admin && (
								<ClayButton
									className="button-root"
									displayType="primary"
									onClick={() =>
										onboardingModal.onOpenChange(true)
									}
								>
									{Liferay.Language.get('start')}
								</ClayButton>
							)}
						</>
					}
					displayCard
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite',
					}}
					spacer
					title={
						dataSources
							? title
							: Liferay.Language.get('you-are-almost-there')
					}
				/>
			</BasePage.Body>

			{onboardingModal.open && (
				<OnboardingModal
					groupId={groupId}
					observer={onboardingModal.observer}
					onClose={() => onboardingModal.onOpenChange(false)}
				/>
			)}
		</BasePage>
	);
};

export default compose<any>(
	withRequest(
		({groupId}: {groupId: string}) =>
			API.dataSource.search({
				delta: 1,
				groupId,
				page: defaultPage,
				query: '',
			}),
		({total}: {total: number}) => ({
			dataSources: !!total,
		})
	)
)(NoPropertiesAvailable);
