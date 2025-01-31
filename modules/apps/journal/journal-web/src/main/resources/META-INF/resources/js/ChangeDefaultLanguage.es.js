/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClayLayout from '@clayui/layout';
import PropTypes from 'prop-types';
import React, {useCallback, useState} from 'react';

function ChangeDefaultLanguage(props) {
	const [active, setActive] = useState(false);

	const [selectedDefaultLanguage, setSelectedDefaultLanguage] = useState(
		props.defaultLanguage
	);

	const onItemClick = useCallback((event, language) => {
		setSelectedDefaultLanguage(language);
		setActive(false);

		Liferay.fire('inputLocalized:defaultLocaleChanged', {
			item: event.currentTarget,
		});
	}, []);

	return (
		<div className="article-default-language">
			<p className="mb-0">
				<b>{`${Liferay.Language.get(
					'web-content-default-language'
				)}: `}</b>

				{props.strings[selectedDefaultLanguage]}
			</p>

			<ClayAlert className="border-0 mt-3 p-0" displayType="info">
				{Liferay.Language.get(
					"changing-the-default-language-will-reset-the-article's-history-making-previous-changes-untrackable"
				)}
			</ClayAlert>

			<ClayDropDown
				active={active}
				className="mt-2"
				onActiveChange={setActive}
				trigger={
					<ClayButton
						aria-expanded={active}
						aria-haspopup="true"
						className="dropdown-toggle"
						displayType="secondary"
					>
						<strong>{Liferay.Language.get('change')}</strong>

						<ClayIcon
							className="inline-item inline-item-after"
							symbol="caret-bottom"
						/>
					</ClayButton>
				}
			>
				<ClayDropDown.ItemList>
					{props.languages.map((item) => (
						<ClayDropDown.Item
							className="autofit-row"
							data-value={item.label}
							key={item.label}
							onClick={(event) => onItemClick(event, item.label)}
							title={item.label}
						>
							<ClayLayout.ContentCol expand>
								<ClayLayout.ContentSection>
									<span className="inline-item inline-item-before">
										<ClayIcon symbol={item.icon}></ClayIcon>
									</span>

									{item.label}
								</ClayLayout.ContentSection>
							</ClayLayout.ContentCol>

							{item.label === selectedDefaultLanguage && (
								<ClayLayout.ContentCol>
									<ClayLabel displayType="info">
										{Liferay.Language.get('default')}
									</ClayLabel>
								</ClayLayout.ContentCol>
							)}
						</ClayDropDown.Item>
					))}
				</ClayDropDown.ItemList>
			</ClayDropDown>
		</div>
	);
}

ChangeDefaultLanguage.propTypes = {
	languages: PropTypes.arrayOf(
		PropTypes.shape({
			icon: PropTypes.string,
			label: PropTypes.string,
		})
	).isRequired,
	strings: PropTypes.object.isRequired,
};

export default ChangeDefaultLanguage;
