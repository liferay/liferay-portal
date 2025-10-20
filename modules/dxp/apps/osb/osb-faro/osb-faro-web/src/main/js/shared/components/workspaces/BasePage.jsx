import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import DocumentTitle from 'shared/components/DocumentTitle';
import getCN from 'classnames';
import React, {createContext} from 'react';
import UserDropdown from 'shared/components/user-dropdown';
import withCurrentUser from 'shared/hoc/WithCurrentUser';
import {Align} from '@clayui/drop-down';
import {LANGUAGES} from 'shared/util/constants';
import {PropTypes} from 'prop-types';
import {Routes} from 'shared/util/router';

export const BasePageContext = createContext({currentUser: {}});

export class WorkspacesBasePage extends React.Component {
	static defaultProps = {
		backLabel: Liferay.Language.get('back'),
		details: ''
	};

	static propTypes = {
		backLabel: PropTypes.string,
		backUrl: PropTypes.string,
		details: PropTypes.oneOfType([
			PropTypes.array,
			PropTypes.string,
			PropTypes.func
		]),
		title: PropTypes.string
	};

	getUserMenuItems() {
		const {
			currentUser: {emailAddress, languageId}
		} = this.props;

		return {
			base: [
				{
					items: [
						{
							childMenuId: 'language',
							divider: true,
							label: Liferay.Language.get('language')
						},
						{
							externalLink: true,
							label: Liferay.Language.get('account'),
							url: 'https://login.liferay.com/enduser/settings'
						},
						{
							externalLink: true,
							label: Liferay.Language.get('sign-out'),
							url: Routes.LOGOUT
						}
					],
					subheaderLabel: emailAddress
				}
			],
			language: [
				{
					items: LANGUAGES.map(({id, label}) => {
						const active = languageId === id;

						return {
							active,
							label,
							onClick: active
								? null
								: () =>
										API.user
											.updateLanguage({
												languageId: id
											})
											.then(() =>
												window.location.reload()
											)
						};
					})
				}
			]
		};
	}

	render() {
		const {
			backLabel,
			backURL,
			children,
			className,
			currentUser,
			details,
			title
		} = this.props;

		return (
			<BasePageContext.Provider value={{currentUser}}>
				<div className={getCN('workspaces-base-page-root', className)}>
					<DocumentTitle title={title} />

					<div className='header-container'>
						<ClayLink href='https://liferay.com' target='_blank'>
							<ClayIcon
								className='icon-root liferay-logo'
								symbol='liferay_logo'
							/>
						</ClayLink>

						<UserDropdown
							alignmentPosition={Align.BottomRight}
							initialActiveMenu='base'
							menus={this.getUserMenuItems()}
							showCaret
							userName={currentUser.name}
						/>
					</div>

					<div className='content'>
						<div className='content-container'>
							{backURL && (
								<div className='back-container'>
									<ClayButton
										className='button-root'
										displayType='unstyled'
										href={backURL}
									>
										<ClayIcon
											className='icon-root'
											symbol='angle-left-small'
										/>

										{backLabel}
									</ClayButton>
								</div>
							)}

							<div className='title-container'>
								<div className='logo-container'>
									<ClayIcon
										className='icon-root logo-icon'
										symbol='ac_logo'
									/>

									<span className='logo-text'>
										{Liferay.Language.get(
											'analytics-cloud'
										)}
									</span>
								</div>

								<h1 className='title'>{title}</h1>

								<div className='details-container'>
									{details}
								</div>
							</div>

							{children}
						</div>
					</div>
				</div>
			</BasePageContext.Provider>
		);
	}
}

export default withCurrentUser(WorkspacesBasePage);
