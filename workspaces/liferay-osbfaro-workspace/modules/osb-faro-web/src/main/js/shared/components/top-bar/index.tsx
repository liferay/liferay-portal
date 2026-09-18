import * as API from 'shared/api';
import ClayButton from '@clayui/button';
import ClayDropDown, {Align} from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import getCN from 'classnames';
import React, {useRef, useState} from 'react';
import UserDropdown, {Menus} from 'shared/components/user-dropdown';
import {LANGUAGES} from 'shared/util/constants';
import {Link} from 'react-router-dom';
import {getLanguageDisplayName, getLanguageLabel} from 'shared/util/locale';
import {Routes, toRoute} from 'shared/util/router';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {User} from 'shared/util/records';

interface ITopBarProps {
	className?: string;
	collapsed: boolean;
	currentUser: User;
	groupId: string;
	onToggle: () => void;
}

const TopBar: React.FC<ITopBarProps> = ({
	className,
	collapsed = false,
	currentUser = new User(),
	groupId,
	onToggle,
}) => {
	const [active, setActive] = useState(false);

	const triggerElementRef = useRef(null);

	const LDPEnabled = useLDPEnabled({groupId});

	const {emailAddress, languageId} = currentUser;

	const userMenus: Menus = {
		base: [
			{
				items: [
					{
						label: Liferay.Language.get('switch-workspaces'),
						url: Routes.BASE,
					},
					{
						externalLink: true,
						label: Liferay.Language.get('sign-out'),
						url: Routes.LOGOUT,
					},
				],
				subheaderLabel: emailAddress,
			},
		],
	};

	return (
		<div
			className={getCN(
				'align-items-center bg-white border-bottom d-flex fixed-top justify-content-between px-3 top-bar-root',
				className
			)}
		>
			<div className="align-items-center d-flex">
				<ClayButton
					aria-label={Liferay.Language.get('menu')}
					borderless
					data-tooltip-align="bottom"
					displayType="secondary"
					monospaced
					onClick={onToggle}
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

				<ClayIcon
					className="icon-root ml-2 top-bar-logo"
					symbol={LDPEnabled ? 'ldp_logo' : 'ac_logo'}
				/>

				<span className="font-weight-semi-bold ml-2 text-nowrap top-bar-product-name">
					{LDPEnabled
						? Liferay.Language.get('liferay-data-platform')
						: Liferay.Language.get('analytics-cloud')}
				</span>
			</div>

			<div className="align-items-center d-flex">
				<Link
					aria-label={Liferay.Language.get('settings')}
					className="btn btn-monospaced btn-outline-borderless btn-outline-secondary btn-sm"
					data-tooltip-align="bottom"
					title={Liferay.Language.get('settings')}
					to={toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {groupId})}
				>
					<ClayIcon symbol="cog" />
				</Link>

				<span className="mx-2 top-bar-divider" />

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
						className="inline-item inline-item-before top-bar-language-icon"
						symbol="automatic-translate"
					/>

					<span className="top-bar-language-label">
						{getLanguageLabel(languageId)}
					</span>

					<ClayIcon
						className="inline-item inline-item-after top-bar-language-caret"
						symbol="caret-bottom"
					/>
				</ClayButton>

				<ClayDropDown.Menu
					active={active}
					alignElementRef={triggerElementRef}
					alignmentPosition={Align.BottomRight}
					onSetActive={setActive}
				>
					<ClayDropDown.ItemList>
						{LANGUAGES.map((id) => (
							<ClayDropDown.Item
								active={languageId === id}
								key={id}
								onClick={() => {
									setActive(false);

									if (languageId === id) {
										return;
									}

									API.user
										.updateLanguage({languageId: id})
										.then(() => window.location.reload());
								}}
							>
								{getLanguageDisplayName(id)}
							</ClayDropDown.Item>
						))}
					</ClayDropDown.ItemList>
				</ClayDropDown.Menu>

				<UserDropdown
					alignmentPosition={Align.BottomRight}
					className="ml-2 top-bar-user"
					initialActiveMenu="base"
					menus={userMenus}
					symbol="user"
					userName={currentUser.name}
				/>
			</div>
		</div>
	);
};

export default TopBar;
