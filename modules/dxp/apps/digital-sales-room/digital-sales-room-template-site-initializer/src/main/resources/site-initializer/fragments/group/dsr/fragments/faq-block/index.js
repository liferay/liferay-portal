/* eslint-disable no-undef */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function toggleVisibility() {
	const index = this.parentNode.parentNode.dataset.index;

	const dsrFaqAnswer = fragmentElement.querySelector(
		`.dsr-faq-answer-${index}`
	);
	const minusIcon = fragmentElement.querySelector(`.minus-icon-${index}`);
	const plusIcon = fragmentElement.querySelector(`.plus-icon-${index}`);

	if (dsrFaqAnswer.classList.contains('collapse')) {
		dsrFaqAnswer.classList.remove('collapse');
		minusIcon.classList.remove('d-none');
		plusIcon.classList.add('d-none');
	}
	else {
		dsrFaqAnswer.classList.add('collapse');
		minusIcon.classList.add('d-none');
		plusIcon.classList.remove('d-none');
	}
}

fragmentElement.querySelectorAll('.dsr-faq-entry').forEach((item) => {
	const index = item.dataset.index;

	['.dsr-faq-question', '.minus-icon', '.plus-icon'].forEach((item) => {
		fragmentElement.querySelector(`${item}-${index}`).onclick =
			toggleVisibility;
	});
});
