/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {delegate} from 'frontend-js-web';

export default function ({namespace}) {
	const delegateHandler = delegate(
		document.body,
		'change',
		'input[type="checkbox"]',
		(event) => {
			const consentRenewalPeriod = document.getElementById(
				`${namespace}consentRenewalPeriod`
			);

			const consentRenewalPeriodLabel = document.getElementById(
				`${namespace}consentRenewalPeriodLabel`
			);

			const consentRenewalPeriodTimeUnit = document.getElementById(
				`${namespace}consentRenewalPeriodTimeUnit`
			);

			const dissentRenewalPeriod = document.getElementById(
				`${namespace}dissentRenewalPeriod`
			);

			const dissentRenewalPeriodLabel = document.getElementById(
				`${namespace}dissentRenewalPeriodLabel`
			);

			const dissentRenewalPeriodTimeUnit = document.getElementById(
				`${namespace}dissentRenewalPeriodTimeUnit`
			);

			const explicitConsentMode = document.querySelector(
				`input[type='checkbox'][name='${namespace}explicitConsentMode']`
			);

			const floatingIconEnabled = document.querySelector(
				`input[type='checkbox'][name='${namespace}floatingIconEnabled']`
			);

			const floatingIcons = document.querySelectorAll(
				`input[type='radio'][name='${namespace}floatingIcon']`
			);

			const forcedReconsentButton = document.getElementById(
				`${namespace}forcedReconsentButton`
			);

			const globalPrivacyControlEnabled = document.querySelector(
				`input[type='checkbox'][name='${namespace}globalPrivacyControlEnabled']`
			);

			const logoSelectorContainer = document.getElementById(
				`${namespace}logoSelectorContainer`
			);

			const storeConsent = document.querySelector(
				`input[type='checkbox'][name='${namespace}storeConsent']`
			);

			if (event.delegateTarget.id === `${namespace}enabled`) {
				if (event.delegateTarget.checked) {
					consentRenewalPeriod.classList.remove('disabled');
					consentRenewalPeriod.removeAttribute('disabled');
					consentRenewalPeriod.required = true;
					consentRenewalPeriodLabel?.classList.remove('disabled');
					consentRenewalPeriodTimeUnit.classList.remove('disabled');
					consentRenewalPeriodTimeUnit.removeAttribute('disabled');
					dissentRenewalPeriod.classList.remove('disabled');
					dissentRenewalPeriod.removeAttribute('disabled');
					dissentRenewalPeriod.required = true;
					dissentRenewalPeriodLabel?.classList.remove('disabled');
					dissentRenewalPeriodTimeUnit.classList.remove('disabled');
					dissentRenewalPeriodTimeUnit.removeAttribute('disabled');
					explicitConsentMode.removeAttribute('disabled');
					floatingIconEnabled.removeAttribute('disabled');
					forcedReconsentButton.removeAttribute('disabled');
					forcedReconsentButton.classList.remove('disabled');
					globalPrivacyControlEnabled.removeAttribute('disabled');
					storeConsent.removeAttribute('disabled');

					floatingIcons.forEach((iconInput) => {
						iconInput.removeAttribute('disabled');

						const label = document.querySelector(
							`label[for='${iconInput.id}']`
						);
						if (label) {
							label.classList.remove('disabled');
						}
					});

					if (logoSelectorContainer) {
						logoSelectorContainer.classList.remove('disabled');
						logoSelectorContainer
							.querySelectorAll('input, button')
							.forEach((element) => {
								element.removeAttribute('disabled');
							});
					}
				}
				else {
					consentRenewalPeriod.classList.add('disabled');
					consentRenewalPeriod.required = false;
					consentRenewalPeriod.setAttribute('disabled', '');
					consentRenewalPeriodLabel?.classList.add('disabled');
					consentRenewalPeriodTimeUnit.classList.add('disabled');
					consentRenewalPeriodTimeUnit.setAttribute('disabled', '');
					dissentRenewalPeriod.classList.add('disabled');
					dissentRenewalPeriod.required = false;
					dissentRenewalPeriod.setAttribute('disabled', '');
					dissentRenewalPeriodLabel?.classList.add('disabled');
					dissentRenewalPeriodTimeUnit.classList.add('disabled');
					dissentRenewalPeriodTimeUnit.setAttribute('disabled', '');
					explicitConsentMode.setAttribute('disabled', '');
					floatingIconEnabled.setAttribute('disabled', '');
					forcedReconsentButton.classList.add('disabled');
					forcedReconsentButton.setAttribute('disabled', '');
					globalPrivacyControlEnabled.setAttribute('disabled', '');
					storeConsent.checked = false;
					storeConsent.setAttribute('disabled', '');

					floatingIcons.forEach((iconInput) => {
						iconInput.setAttribute('disabled', '');

						const label = document.querySelector(
							`label[for='${iconInput.id}']`
						);
						if (label) {
							label.classList.add('disabled');
						}
					});

					if (logoSelectorContainer) {
						logoSelectorContainer.classList.add('disabled');
						logoSelectorContainer
							.querySelectorAll('input, button')
							.forEach((element) => {
								element.setAttribute('disabled', '');
							});
					}
				}
			}
		}
	);

	return {
		dispose() {
			delegateHandler.dispose();
		},
	};
}
