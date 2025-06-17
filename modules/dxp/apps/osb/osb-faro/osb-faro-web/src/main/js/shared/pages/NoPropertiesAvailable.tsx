import * as API from 'shared/api';
import BasePage from 'shared/components/base-page';
import ClayButton from '@clayui/button';
import ClayLink from '@clayui/link';
import Constants, {Sizes} from 'shared/util/constants';
import NoResultsDisplay from 'shared/components/NoResultsDisplay';
import React from 'react';
import {close, modalTypes, open} from 'shared/actions/modals';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {Routes, toRoute} from 'shared/util/router';
import {setBackURL} from 'shared/actions/settings';
import {User} from 'shared/util/records';
import {withRequest} from 'shared/hoc';

const {
	pagination: {cur: defaultPage}
} = Constants;

interface INoPropertiesAvailableProps
	extends React.HTMLAttributes<HTMLDivElement> {
	close: () => void;
	currentUser: User;
	dataSources: boolean;
	groupId: string;
	open: (modalType: string, config: object) => void;
	setBackURL: (url: string) => void;
}

const NoPropertiesAvailable: React.FC<INoPropertiesAvailableProps> = ({
	close,
	currentUser,
	dataSources,
	groupId,
	open,
	setBackURL
}) => {
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
			className='no-properties-available-root'
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

							{admin &&
								(dataSources ? (
									<ClayLink
										button
										className='button-root'
										displayType='primary'
										onClick={() =>
											setBackURL(
												toRoute(
													Routes.WORKSPACE_WITH_ID,
													{
														groupId
													}
												)
											)
										}
									>
										{Liferay.Language.get(
											'create-property'
										)}
									</ClayLink>
								) : (
									<ClayButton
										className='button-root'
										displayType='primary'
										onClick={() =>
											open(modalTypes.ONBOARDING_MODAL, {
												groupId,
												onClose: close
											})
										}
									>
										{Liferay.Language.get('start')}
									</ClayButton>
								))}
						</>
					}
					displayCard
					icon={{
						border: false,
						size: Sizes.XXXLarge,
						symbol: 'ac_satellite'
					}}
					spacer
					title={
						dataSources
							? title
							: Liferay.Language.get('you-are-almost-there')
					}
				/>
			</BasePage.Body>
		</BasePage>
	);
};

export default compose<any>(
	withRequest(
		({groupId}) =>
			API.dataSource.search({
				delta: 1,
				groupId,
				page: defaultPage,
				query: ''
			}),
		({total}) => ({
			dataSources: !!total
		})
	),
	connect(null, {close, open, setBackURL})
)(NoPropertiesAvailable);
