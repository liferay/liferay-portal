/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

document.addEventListener('DOMContentLoaded', () => {
	const tocContainer = document.getElementById('table-of-contents');

	if (!tocContainer) {
		return;
	}

	let isScrollHandlerActive = true;
	const scrollOffset = 150;

	let contentHeadings = document.querySelectorAll('h2');

	if (!contentHeadings.length) {
		contentHeadings = document.querySelectorAll('h3');
	}
	if (!contentHeadings.length) {
		const sideContainer = document.querySelector('.side-container');
		sideContainer.classList.remove('side-container');

		return;
	}

	const tocList = document.createElement('ul');

	tocContainer.appendChild(tocList);
	contentHeadings.forEach((heading) => {
		heading.id = heading.textContent
			.toLowerCase()
			.replace(/\s+/g, '-')
			.replace(/[^\w-]+/g, '')
			.replace(/^-+|-+$/g, '');

		const link = document.createElement('a');

		link.href = '#' + heading.id;
		link.textContent = heading.textContent.replace(/[^\w\s]+/g, '');

		const listItem = document.createElement('li');
		const headingClass = 'toc-' + heading.tagName.toLowerCase();

		listItem.classList.add(headingClass);
		listItem.appendChild(link);

		tocList.appendChild(listItem);
	});

	const updateActiveLink = (activeId) => {
		document
			.querySelectorAll('#table-of-contents li.active')
			.forEach((li) => li.classList.remove('active'));
		const activeIdSearch = tocList.querySelector(`a[href="#${activeId}"]`);

		activeIdSearch?.closest('li')?.classList.add('active');
	};

	const handleScroll = () => {
		if (!isScrollHandlerActive) {
			return;
		}

		let closestElement = null;
		let closestOffset = Number.POSITIVE_INFINITY;

		contentHeadings.forEach((element) => {
			const {top} = element.getBoundingClientRect();

			if (top - scrollOffset >= 0 && top < closestOffset) {
				closestOffset = top;
				closestElement = element;
			}
		});

		if (!closestElement && contentHeadings.length) {
			const firstHeading = contentHeadings[0];
			const firstHeadingTop = firstHeading.getBoundingClientRect().top;

			if (firstHeadingTop < scrollOffset && firstHeadingTop > 0) {
				closestElement = firstHeading;
			}
			else if (firstHeadingTop < 0 && contentHeadings.length) {
				let highestElement = null;
				let minTop = Number.POSITIVE_INFINITY;

				contentHeadings.forEach((element) => {
					const {top} = element.getBoundingClientRect();
					if (top < 0 && top > minTop) {
						minTop = top;
						highestElement = element;
					}
				});

				if (
					!highestElement &&
					window.scrollY + window.innerHeight >=
						document.documentElement.scrollHeight - 50
				) {
					highestElement =
						contentHeadings[contentHeadings.length - 1];
				}

				if (highestElement) {
					closestElement = highestElement;
				}
			}
		}

		if (closestElement) {
			updateActiveLink(closestElement.id);
		}
	};

	window.addEventListener('scroll', handleScroll);

	tocList.addEventListener('click', (event) => {
		const link = event.target.closest('a');

		if (link && link.hash) {
			event.preventDefault();
			const targetId = event.target.hash.substring(1);

			const targetElement = document.getElementById(targetId);

			if (targetElement) {
				isScrollHandlerActive = false;

				window.scrollTo({
					behavior: 'smooth',
					top: targetElement.offsetTop - 160,
				});

				updateActiveLink(targetId);

				setTimeout(() => {
					isScrollHandlerActive = true;
				}, 600);
			}
		}
	});

	window.addEventListener('beforeunload', () => {
		window.removeEventListener('scroll', handleScroll);
	});
});
