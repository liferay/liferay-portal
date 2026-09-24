import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import ClayToolbar from '@clayui/toolbar';
import getCN from 'classnames';
import React, {useRef, useState} from 'react';
import {collapseSidebar} from 'shared/actions/sidebar';
import {getLanguageLabel} from 'shared/util/locale';
import {Link} from 'react-router-dom';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDispatch, useSelector} from 'react-redux';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';

interface IToolbarProps {
	className?: string;
	groupId: string;
}

const Toolbar: React.FC<IToolbarProps> = ({className, groupId}) => {
	const currentUser = useCurrentUser();

	const collapsed = useSelector<any, boolean>((state) =>
		state.getIn(['sidebar', String(currentUser.id), 'collapsed'], false)
	);

	const dispatch = useDispatch();

	const [active, setActive] = useState(false);

	const triggerElementRef = useRef(null);

	const LDPEnabled = useLDPEnabled({groupId});

	const {emailAddress, languageId} = currentUser;

	return (
		<ClayToolbar
			className={getCN(
				'align-items-center bg-white fixed-top toolbar-root',
				className
			)}
		>
			<ClayToolbar.Nav className="align-items-center mx-3">
				<ClayToolbar.Item>
					<ClayButton
						aria-label={Liferay.Language.get('menu')}
						borderless
						data-tooltip-align="bottom"
						displayType="secondary"
						monospaced
						onClick={() =>
							dispatch(
								collapseSidebar({
									collapsed: !collapsed,
									currentUserId: currentUser.id,
								})
							)
						}
						size="sm"
						title={Liferay.Language.get('menu')}
					>
						<ClayIcon
							symbol={
								collapsed
									? 'product-menu-closed'
									: 'product-menu-open'
							}
						/>
					</ClayButton>
				</ClayToolbar.Item>

				<ClayToolbar.Item>
					<ClayIcon
						className="icon-root text-6"
						symbol={LDPEnabled ? 'ldp_logo' : 'ac_logo'}
					/>
				</ClayToolbar.Item>

				<ClayToolbar.Item className="d-none d-sm-block pl-0">
					<ClayToolbar.Section>
						<span className="font-weight-semi-bold text-5 text-dark text-nowrap">
							{LDPEnabled
								? Liferay.Language.get('liferay-data-platform')
								: Liferay.Language.get('analytics-cloud')}
						</span>
					</ClayToolbar.Section>
				</ClayToolbar.Item>

				<ClayToolbar.Item expand />

				<ClayToolbar.Item>
					<Link
						aria-label={Liferay.Language.get('settings')}
						className="btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm"
						data-tooltip-align="bottom"
						title={Liferay.Language.get('settings')}
						to={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
							groupId,
						})}
					>
						<ClayIcon symbol="cog" />
					</Link>
				</ClayToolbar.Item>

				<ClayToolbar.Item className="align-self-stretch border-left my-1 p-0" />

				<ClayToolbar.Item>
					<ClayButton
						aria-expanded={active}
						aria-haspopup="true"
						borderless
						className="text-nowrap"
						data-tooltip-align="bottom"
						displayType="secondary"
						onClick={() => setActive(!active)}
						ref={triggerElementRef}
						size="sm"
						title={Liferay.Language.get('language')}
					>
						<ClayIcon
							className="inline-item mr-sm-1"
							symbol="automatic-translate"
						/>

						<span className="d-none d-sm-inline">
							{getLanguageLabel(languageId)}
						</span>

						<ClayIcon
							className="d-none d-sm-inline-flex inline-item inline-item-after"
							symbol="caret-bottom"
						/>
					</ClayButton>

					<ClayDropDown.Menu
						active={active}
						alignElementRef={triggerElementRef}
						alignmentPosition={Align.BottomRight}
					>
						<ClayDropDown.ItemList>
							{Object.entries(Liferay.Language.available).map(
								([id, label]) => (
									<ClayDropDown.Item
										active={languageId === id}
										key={id}
										onClick={() => {
											setActive(false);

											if (languageId === id) {
												return;
											}

											API.user
												.updateLanguage({
													languageId: id,
												})
												.then(() =>
													window.location.reload()
												);
										}}
									>
										{label}
									</ClayDropDown.Item>
								)
							)}
						</ClayDropDown.ItemList>
					</ClayDropDown.Menu>
				</ClayToolbar.Item>

				<ClayToolbar.Item>
					<ClayDropDown
						alignmentPosition={Align.BottomRight}
						trigger={
							<ClayButton
								aria-label={currentUser.name}
								displayType="unstyled"
							>
								<ClaySticker
									className="border text-secondary"
									displayType="unstyled"
									shape="circle"
								>
									<ClayIcon symbol="user" />
								</ClaySticker>
							</ClayButton>
						}
					>
						<ClayDropDown.ItemList>
							<ClayDropDown.Group header={emailAddress}>
								<ClayDropDown.Item href={Routes.BASE}>
									{Liferay.Language.get('switch-workspaces')}
								</ClayDropDown.Item>

								<ClayDropDown.Item href={Routes.LOGOUT}>
									{Liferay.Language.get('sign-out')}
								</ClayDropDown.Item>
							</ClayDropDown.Group>
						</ClayDropDown.ItemList>
					</ClayDropDown>
				</ClayToolbar.Item>
			</ClayToolbar.Nav>
		</ClayToolbar>
	);
};

export default Toolbar;
