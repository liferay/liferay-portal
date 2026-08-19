const isRTL = document.documentElement.classList.contains('rtl');

const dropdownElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentElementId}-dropdown`
);
const listboxElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentElementId}-listbox`
);
const loadingResultsElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentElementId}-loading-results-message`
);
const noResultsElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentElementId}-no-results-message`
);
const searchInputElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentElementId}-assignee-input`
);
const triggerElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentElementId}-trigger`
);
const valueInputElement = document.getElementById(

	// eslint-disable-next-line no-undef
	`${fragmentElementId}-value-input`
);

const EMPTY_VALUE = '{}';

const GUEST_ROLE_NAME = 'Guest';

const KEYS = {
	ArrowDown: 'ArrowDown',
	ArrowUp: 'ArrowUp',
	End: 'End',
	Enter: 'Enter',
	Escape: 'Escape',
	Home: 'Home',
};

const PAGE_SIZE = 20;

const ROLES_URL = '/o/headless-admin-user/v1.0/roles';

const USERS_URL = '/o/headless-admin-user/v1.0/user-accounts';

let assignees = [];
let lastSearchAbortController = new AbortController();
let lastSearchQuery = null;

function parseSelectedAssignee(value) {
	if (!value) {
		return null;
	}

	try {
		const assignee = JSON.parse(value);

		if (!assignee || !assignee.externalReferenceCode) {
			return null;
		}

		return assignee;
	}
	catch (error) {
		return null;
	}
}

function checkIsSelectedAssignee(assignee) {
	const selectedAssignee = parseSelectedAssignee(valueInputElement.value);

	if (!selectedAssignee) {
		return false;
	}

	return (
		selectedAssignee.externalReferenceCode ===
			assignee.externalReferenceCode &&
		selectedAssignee.type === assignee.type
	);
}

function setSelectedAssignee(assignee) {
	if (!assignee) {
		searchInputElement.value = '';
		valueInputElement.value = EMPTY_VALUE;

		return;
	}

	searchInputElement.value = assignee.name;
	valueInputElement.value = JSON.stringify({
		externalReferenceCode: assignee.externalReferenceCode,
		name: assignee.name,
		type: assignee.type,
	});
}

function fetchAssignees(url, query, abortController, type) {
	const searchURL = new URL(url, window.location.origin);

	searchURL.searchParams.set('page', '1');
	searchURL.searchParams.set('pageSize', `${PAGE_SIZE}`);

	if (query) {
		searchURL.searchParams.set('search', query);
	}

	return Liferay.Util.fetch(searchURL, {
		headers: new Headers({
			'Accept': 'application/json',
			'Accept-Language': Liferay.ThemeDisplay.getBCP47LanguageId(),
		}),
		method: 'GET',
		signal: abortController.signal,
	})
		.then((response) => response.json())
		.then((result) =>
			(result.items || [])
				.filter(
					(item) =>
						item.externalReferenceCode &&
						item.name &&
						!(type === 'Role' && item.name === GUEST_ROLE_NAME)
				)
				.map((item) => ({
					externalReferenceCode: item.externalReferenceCode,
					name: item.name,
					type,
				}))
		)
		.catch(() => []);
}

function searchAssignees(query, abortController) {
	return Promise.all([
		fetchAssignees(USERS_URL, query, abortController, 'User'),
		fetchAssignees(ROLES_URL, query, abortController, 'Role'),
	]).then(([users, roles]) => [...users, ...roles]);
}

function createOptionElement(assignee, index) {
	const optionElement = document.createElement('li');

	optionElement.classList.add('dropdown-item');
	optionElement.dataset.index = `${index}`;

	// eslint-disable-next-line no-undef
	optionElement.id = `${fragmentElementId}-option-${index}`;
	optionElement.textContent = assignee.name;

	optionElement.setAttribute('role', 'option');

	if (checkIsSelectedAssignee(assignee)) {
		optionElement.classList.add('active');
	}

	return optionElement;
}

function renderOptionList() {
	listboxElement.innerHTML = '';

	assignees.forEach((assignee, index) =>
		listboxElement.appendChild(createOptionElement(assignee, index))
	);
}

function setFocusedOption(
	optionElement,
	{scrollToElement = true} = {scrollToElement: true}
) {
	const currentFocusedOption = document.getElementById(
		listboxElement.getAttribute('aria-activedescendant')
	);

	if (currentFocusedOption) {
		currentFocusedOption.removeAttribute('aria-selected');
	}

	if (optionElement) {
		listboxElement.setAttribute('aria-activedescendant', optionElement.id);

		optionElement.setAttribute('aria-selected', 'true');

		if (scrollToElement) {
			optionElement.scrollIntoView({block: 'nearest'});
		}
	}
	else {
		listboxElement.removeAttribute('aria-activedescendant');
	}
}

function selectOption(optionElement) {
	const assignee = assignees[Number(optionElement.dataset.index)];

	if (!assignee) {
		return;
	}

	setSelectedAssignee(assignee);

	lastSearchQuery = assignee.name;

	closeDropdown();
}

function checkIsOpenDropdown() {
	return (
		searchInputElement.getAttribute('aria-expanded') === 'true' &&
		triggerElement.getAttribute('aria-expanded') === 'true'
	);
}

function openDropdown() {
	dropdownElement.classList.replace('d-none', 'show');
	searchInputElement.setAttribute('aria-expanded', 'true');
	triggerElement.setAttribute('aria-expanded', 'true');

	const wrapperWidth = `${fragmentElement.getBoundingClientRect().width}px`;

	dropdownElement.style.maxWidth = wrapperWidth;
	dropdownElement.style.minWidth = wrapperWidth;
	dropdownElement.style.width = wrapperWidth;

	requestAnimationFrame(repositionDropdownElement);
}

function closeDropdown() {
	dropdownElement.classList.replace('show', 'd-none');
	searchInputElement.setAttribute('aria-expanded', 'false');
	triggerElement.setAttribute('aria-expanded', 'false');

	setFocusedOption(null);
}

function toggleDropdown() {
	if (checkIsOpenDropdown()) {
		closeDropdown();
	}
	else {
		openDropdown();
		handleSearch();
	}
}

function repositionDropdownElement() {
	if (!document.body.contains(fragmentElement)) {
		if (document.body.contains(dropdownElement)) {
			dropdownElement.parentNode.removeChild(dropdownElement);
		}

		return;
	}

	if (fragmentElement.contains(dropdownElement)) {
		document.body.appendChild(dropdownElement);
	}

	const searchInputRect = searchInputElement.getBoundingClientRect();

	dropdownElement.style.transform = `
		translateX(${
			(isRTL
				? searchInputRect.right - window.innerWidth
				: searchInputRect.left) + window.scrollX
		}px)
		translateY(${searchInputRect.bottom + window.scrollY}px)
	`;
}

function handleSearch() {
	const query = searchInputElement.value.trim();

	if (!checkIsOpenDropdown()) {
		openDropdown();
	}

	if (query === lastSearchQuery) {
		return;
	}

	lastSearchQuery = query;

	loadingResultsElement.classList.remove('d-none');
	noResultsElement.classList.add('d-none');
	listboxElement.innerHTML = '';

	lastSearchAbortController.abort();
	lastSearchAbortController = new AbortController();

	searchAssignees(query, lastSearchAbortController).then((items) => {
		assignees = items;

		loadingResultsElement.classList.add('d-none');

		renderOptionList();

		if (assignees.length) {
			noResultsElement.classList.add('d-none');

			setFocusedOption(listboxElement.firstElementChild, {
				scrollToElement: false,
			});
		}
		else {
			noResultsElement.classList.remove('d-none');
		}
	});
}

function handleKeydown(event) {
	const currentFocusedOption = document.getElementById(
		listboxElement.getAttribute('aria-activedescendant')
	);

	if (event.key === KEYS.ArrowDown && !event.altKey) {
		setFocusedOption(
			currentFocusedOption
				? currentFocusedOption.nextElementSibling ||
						listboxElement.firstElementChild
				: listboxElement.firstElementChild
		);
	}
	else if (event.key === KEYS.ArrowUp) {
		setFocusedOption(
			currentFocusedOption
				? currentFocusedOption.previousElementSibling ||
						listboxElement.lastElementChild
				: listboxElement.lastElementChild
		);
	}
	else if (event.key === KEYS.Home) {
		setFocusedOption(listboxElement.firstElementChild);
	}
	else if (event.key === KEYS.End) {
		setFocusedOption(listboxElement.lastElementChild);
	}
	else if (event.key === KEYS.Enter && currentFocusedOption) {
		selectOption(currentFocusedOption);
	}
	else if (event.key === KEYS.Escape) {
		closeDropdown();
	}
}

function handleSearchInputKeydown(event) {
	if (KEYS[event.key] && !checkIsOpenDropdown()) {
		handleSearch();
		event.preventDefault();

		return;
	}

	if (event.key === KEYS.Enter) {
		event.preventDefault();
	}

	handleKeydown(event);
}

function handleSearchInputBlur() {
	if (searchInputElement.value.trim()) {
		setSelectedAssignee(parseSelectedAssignee(valueInputElement.value));
	}
	else {
		setSelectedAssignee(null);
	}

	if (checkIsOpenDropdown()) {
		setTimeout(() => closeDropdown(), 500);
	}
}

function handleTriggerBlur() {
	if (checkIsOpenDropdown()) {
		setTimeout(() => closeDropdown(), 500);
	}
}

function handleListboxClick(event) {
	const optionElement = event.target.closest('.dropdown-item');

	if (optionElement) {
		setFocusedOption(optionElement, {scrollToElement: false});
		selectOption(optionElement);
	}
}

function handleWindowResizeOrScroll() {
	if (!document.body.contains(fragmentElement)) {
		window.removeEventListener('resize', handleWindowResizeOrScroll);
		window.removeEventListener('scroll', handleWindowResizeOrScroll);

		if (document.body.contains(dropdownElement)) {
			dropdownElement.parentNode.removeChild(dropdownElement);
		}

		return;
	}

	if (checkIsOpenDropdown()) {
		repositionDropdownElement();
	}
}

function debounce(fn, delay) {
	let debounceId = null;

	return function (...args) {
		clearTimeout(debounceId);
		debounceId = setTimeout(() => fn(...args), delay);
	};
}

function main() {

	// eslint-disable-next-line no-undef
	if (layoutMode === 'edit' && !input.readOnly) {
		searchInputElement.setAttribute('disabled', true);
		triggerElement.setAttribute('disabled', true);

		return;
	}

	setSelectedAssignee(parseSelectedAssignee(valueInputElement.value));

	if (input.readOnly) {
		return;
	}

	searchInputElement.addEventListener('blur', handleSearchInputBlur);
	searchInputElement.addEventListener('click', handleSearch);
	searchInputElement.addEventListener('input', debounce(handleSearch, 500));
	searchInputElement.addEventListener('keydown', handleSearchInputKeydown);

	triggerElement.addEventListener('blur', handleTriggerBlur);
	triggerElement.addEventListener('click', toggleDropdown);

	listboxElement.addEventListener('click', handleListboxClick);
	listboxElement.addEventListener('mousedown', (event) =>
		event.preventDefault()
	);

	window.addEventListener('resize', handleWindowResizeOrScroll, {
		passive: true,
	});
	window.addEventListener('scroll', handleWindowResizeOrScroll, {
		passive: true,
	});

	import('@liferay/fragment-impl/api').then(({registerUnlocalizedInput}) => {
		registerUnlocalizedInput({
			defaultLanguageId: input.attributes.defaultLanguageId,
			inputElement: searchInputElement,
			readOnlyInputLabel: document.getElementById(

				// eslint-disable-next-line no-undef
				`${fragmentElementId}-assignee-input-read-only`
			),
			unlocalizedFieldsState: input.attributes.unlocalizedFieldsState,
			unlocalizedMessageContainer: document.getElementById(

				// eslint-disable-next-line no-undef
				`${fragmentElementId}-unlocalized-info`
			),
		});
	});
}

main();
