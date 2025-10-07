/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import {
	CONSTANTS,
	accessibilityMenuAtom,
} from '@liferay/accessibility-settings-state-web';
import {checkCookieConsentForTypes} from '@liferay/cookies-banner-web';
import {useLiferayState} from '@liferay/frontend-js-state-web/react';
import {
	COOKIE_TYPES,
	checkConsent,
	localStorage,
	setSessionValue,
} from 'frontend-js-web';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import AccessibilitySetting from './AccessibilitySetting';
import {getSettingValue, toggleClassName} from './util';

type KEYS = keyof typeof CONSTANTS;

type Setting = {
	className: string;
	defaultValue: boolean;
	description: string;
	key: KEYS;
	label: string;
	sessionClicksValue: boolean;
};

type AccessibilityMenuSetting = {
	className: string;
	description: string;
	key: KEYS;
	label: string;
	updating?: boolean;
	value: boolean;
};

type Props = {
	settings: Array<Setting>;
};

const BEFORE_NAVIGATE_EVENT_NAME = 'beforeNavigate';
const OPEN_ACCESSIBILITY_MENU_EVENT_NAME = 'openAccessibilityMenu';

const AccessibilityMenu = (props: Props) => {
	const [settings, setSettings] = useLiferayState(accessibilityMenuAtom);

	const [hasFunctionalCookiesConsent, setHasFunctionalCookiesConsent] =
		useState(checkConsent(COOKIE_TYPES.FUNCTIONAL));

	const {observer, onOpenChange, open} = useModal();

	useEffect(() => {
		setSettings(
			props.settings.reduce<Record<KEYS, AccessibilityMenuSetting>>(
				(prev, setting) => {
					const {
						className,
						defaultValue,
						description,
						key,
						label,
						sessionClicksValue,
					} = setting;

					const value = getSettingValue(
						defaultValue,
						sessionClicksValue,
						key
					);

					toggleClassName(className, value);

					prev[key] = {className, description, key, label, value};

					return prev;
				},
				{} as any
			)
		);
	}, [setSettings, props.settings]);

	useEffect(() => {
		const openAccessibilityMenu = () => onOpenChange(true);

		Liferay.on(OPEN_ACCESSIBILITY_MENU_EVENT_NAME, openAccessibilityMenu);

		const detachOpenAccessibilityMenuEvent = () => {
			Liferay.detach(
				OPEN_ACCESSIBILITY_MENU_EVENT_NAME,
				openAccessibilityMenu
			);
		};

		Liferay.once(
			BEFORE_NAVIGATE_EVENT_NAME,
			detachOpenAccessibilityMenuEvent
		);

		return () => {
			detachOpenAccessibilityMenuEvent();

			Liferay.detach(
				BEFORE_NAVIGATE_EVENT_NAME,
				detachOpenAccessibilityMenuEvent
			);
		};
	}, [onOpenChange]);

	const updateSetting = useCallback(
		(settingKey: KEYS, settingUpdates: Partial<AccessibilityMenuSetting>) =>
			setSettings({
				...settings,
				[settingKey]: {
					...settings[settingKey],
					...settingUpdates,
				},
			}),
		[settings, setSettings]
	);

	const afterSettingValueChange = useCallback(
		(value: any, setting: any) => {
			toggleClassName(setting.className, value);

			updateSetting(setting.key, {updating: false, value});
		},
		[updateSetting]
	);

	const handleAccessibilitySettingChange = useCallback(
		(value: boolean, setting: AccessibilityMenuSetting) => {
			if (setting.updating) {
				return;
			}

			updateSetting(setting.key, {updating: true});

			if (window.themeDisplay.isSignedIn()) {
				return setSessionValue(setting.key, value).then(() => {
					afterSettingValueChange(value, setting);
				});
			}
			else {
				localStorage.setItem(
					setting.key,
					value,
					localStorage.TYPES.FUNCTIONAL
				);

				afterSettingValueChange(value, setting);
			}
		},
		[afterSettingValueChange, updateSetting]
	);

	const handleReviewCookies = useCallback(() => {
		checkCookieConsentForTypes(COOKIE_TYPES.FUNCTIONAL)
			.then(() => {
				setHasFunctionalCookiesConsent(true);
			})
			.catch(() => {
				setHasFunctionalCookiesConsent(false);
			});
	}, []);

	const isSettingsDisabled =
		!hasFunctionalCookiesConsent && !window.themeDisplay.isSignedIn();

	return (
		<>
			{open && (
				<ClayModal observer={observer}>
					<ClayModal.Header>
						{Liferay.Language.get('accessibility-menu')}
					</ClayModal.Header>

					<ClayModal.Body>
						{isSettingsDisabled && (
							<ClayAlert
								className="mb-4"
								displayType="info"
								title={`${Liferay.Language.get('info')}:`}
							>
								{Liferay.Language.get(
									'accessibility-menu-cookies-alert'
								)}

								<ClayAlert.Footer>
									<ClayButton.Group>
										<ClayButton
											alert
											onClick={handleReviewCookies}
										>
											{Liferay.Language.get(
												'review-cookies'
											)}
										</ClayButton>
									</ClayButton.Group>
								</ClayAlert.Footer>
							</ClayAlert>
						)}

						<ul className="list-unstyled mb-0">
							{(Object.keys(settings) as Array<KEYS>).map(
								(key, index) => (
									<AccessibilitySetting
										description={settings[key].description}
										disabled={isSettingsDisabled}
										index={index}
										key={settings[key].key}
										label={settings[key].label}
										onChange={(value) =>
											handleAccessibilitySettingChange(
												value,
												settings[key]
											)
										}
										value={settings[key].value}
									/>
								)
							)}
						</ul>
					</ClayModal.Body>
				</ClayModal>
			)}
		</>
	);
};

AccessibilityMenu.propTypes = {
	settings: PropTypes.arrayOf(
		PropTypes.shape({
			className: PropTypes.string,
			defaultValue: PropTypes.bool,
			key: PropTypes.string,
			label: PropTypes.string,
			sessionClicksValue: PropTypes.bool,
		})
	).isRequired,
};

export default AccessibilityMenu;
