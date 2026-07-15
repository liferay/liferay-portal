/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import PropTypes from 'prop-types';
import React from 'react';

const Header = ({
	disabledAddButton = false,
	handleClickAdd,
	handleClickBack,
	handleClickEdit,
	headerTitle,
	infoButtonRef,
	showEditIcon,
	showInfoIcon,
	showNavbar,
}) => (
	<div className="navbar navigation-bar navigation-bar-light">
		<ClayLayout.ContainerFluid className="header">
			<nav className="navbar navbar-expand-md navbar-underline navigation-bar navigation-bar-light">
				<ClayLayout.ContainerFluid>
					<ul className="navbar-nav">
						<li className="nav-item">
							<ClayButton
								aria-label={Liferay.Language.get('back')}
								borderless
								displayType="secondary"
								monospaced
								onClick={handleClickBack}
							>
								<ClayIcon symbol="angle-left" />
							</ClayButton>
						</li>

						<li className="d-none d-sm-inline-flex nav-item">
							<strong>{headerTitle}</strong>
						</li>
					</ul>
				</ClayLayout.ContainerFluid>
			</nav>

			{showNavbar && (
				<nav className="navbar navbar-expand-md navbar-underline navigation-bar navigation-bar-light">
					<ClayLayout.ContainerFluid>
						<ul className="navbar-nav">
							{showEditIcon && (
								<li className="btn-group-item nav-item">
									<ClayButton
										aria-label={Liferay.Language.get(
											'edit'
										)}
										borderless
										displayType="secondary"
										monospaced
										onClick={handleClickEdit}
									>
										<ClayIcon symbol="pencil" />
									</ClayButton>
								</li>
							)}

							{showInfoIcon && (
								<li className="btn-group-item nav-item">
									<ClayButton
										aria-label={Liferay.Language.get(
											'info'
										)}
										borderless
										displayType="secondary"
										id="infoButtonRef"
										monospaced
										ref={infoButtonRef}
									>
										<ClayIcon symbol="info-circle-open" />
									</ClayButton>
								</li>
							)}

							<li className="nav-item">
								<ClayButton
									disabled={disabledAddButton}
									displayType="primary"
									onClick={handleClickAdd}
								>
									{Liferay.Language.get('add')}
								</ClayButton>
							</li>
						</ul>
					</ClayLayout.ContainerFluid>
				</nav>
			)}
		</ClayLayout.ContainerFluid>
	</div>
);

Header.propTypes = {
	disabledAddButton: PropTypes.bool,
	handleClickAdd: PropTypes.func.isRequired,
	handleClickBack: PropTypes.func.isRequired,
	headerTitle: PropTypes.string.isRequired,
	showEditIcon: PropTypes.bool,
	showInfoIcon: PropTypes.bool,
};

export default Header;
