import Card from 'shared/components/Card';
import ClayIcon from 'shared/components/Icon';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import InterestPagesList from 'contacts/components/InterestPagesList';
import React from 'react';
import {EntityTypes} from 'shared/util/constants';
import {getSafeDecodedURIComponent} from 'shared/util/util';
import {Individual} from 'shared/util/records';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';
import {setUriQueryValue} from 'shared/util/router';
import {sub} from 'shared/util/lang';

export default class InterestDetails extends React.Component {
	static defaultProps = {
		active: 'true'
	};

	static propTypes = {
		active: PropTypes.string,
		channelId: PropTypes.string,
		groupId: PropTypes.string.isRequired,
		id: PropTypes.string.isRequired,
		individual: PropTypes.instanceOf(Individual).isRequired,
		interestId: PropTypes.string.isRequired
	};

	getNavigationItems() {
		const {channelId, groupId, id, interestId} = this.props;

		const active = this.props.active === 'true';

		return [
			{
				active,
				href: setUriQueryValue(
					toRoute(Routes.CONTACTS_INDIVIDUAL_INTEREST_DETAILS, {
						channelId,
						groupId,
						id,
						interestId
					}),
					'active',
					true
				),
				label: Liferay.Language.get('active-pages')
			},
			{
				active: !active,
				href: setUriQueryValue(
					toRoute(Routes.CONTACTS_INDIVIDUAL_INTEREST_DETAILS, {
						channelId,
						groupId,
						id,
						interestId
					}),
					'active',
					false
				),
				label: Liferay.Language.get('inactive-pages')
			}
		];
	}

	render() {
		const {
			props: {channelId, groupId, id, interestId}
		} = this;

		const interestName = getSafeDecodedURIComponent(interestId);

		return (
			<>
				<div className='back-button-root mb-2'>
					<ClayLink
						borderless
						button
						displayType='secondary'
						href={toRoute(Routes.CONTACTS_INDIVIDUAL_INTERESTS, {
							channelId,
							groupId,
							id
						})}
					>
						<ClayIcon
							className='icon-root mr-2'
							symbol='angle-left-small'
						/>

						{Liferay.Language.get('back-to-interests')}
					</ClayLink>
				</div>

				<Card pageDisplay>
					<Card.Header>
						<Card.Title>
							{sub(
								Liferay.Language.get('pages-containing-x'),
								[
									<span
										className='interest-name'
										key='INTEREST_NAME'
									>
										{`"${interestName}"`}
									</span>
								],
								false
							)}
						</Card.Title>
					</Card.Header>

					<Card.Header>
						<ClayNavigationBar
							className='page-subnav'
							triggerLabel='label'
						>
							{this.getNavigationItems().map(
								({active, href, label}) => (
									<ClayNavigationBar.Item
										active={active}
										key={label}
									>
										<ClayLink href={href}>{label}</ClayLink>
									</ClayNavigationBar.Item>
								)
							)}
						</ClayNavigationBar>
					</Card.Header>

					<InterestPagesList
						channelId={channelId}
						className='interest-history-table'
						dataSourceParams={{
							active: this.props.active === 'true',
							channelId,
							contactsEntityId: id,
							contactsEntityType: EntityTypes.Individual,
							groupId,
							interestName
						}}
						groupId={groupId}
					/>
				</Card>
			</>
		);
	}
}
