import {audiences} from '../../frontend-js-audiences-web/__liferay__/index.js';

const languageId = themeDisplay.getLanguageId();

function getLocalizedValue(values, defaultLanguageId) {
	if (!values) {
		return null;
	}

	if (values[languageId] != null) {
		return values[languageId];
	}

	return values[defaultLanguageId];
}

function applyElementVariation(elementVariation) {
	let element = null;

	if (elementVariation.targetElement) {
		element = document.querySelector(elementVariation.targetElement);

		if (!element) {
			return;
		}

		if (elementVariation.hide === 'true') {
			element.style.display = 'none';
		}

		const html = getLocalizedValue(
			elementVariation.html,
			elementVariation.defaultLanguageId
		);

		if (html != null) {
			element.innerHTML = html;
		}
	}

	const js = getLocalizedValue(
		elementVariation.js,
		elementVariation.defaultLanguageId
	);

	if (js) {
		js(element);
	}
}

function getGroupAudienceEntryERCs(targetElement) {
	const groupAudienceEntryERCs = new Set();

	elementVariations.forEach((elementVariation) => {
		if (elementVariation.targetElement === targetElement) {
			elementVariation.audienceEntryERCs.forEach((audienceEntryERC) => {
				groupAudienceEntryERCs.add(audienceEntryERC);
			});
		}
	});

	return groupAudienceEntryERCs;
}

function getScore(audienceEntryERC) {
	const index = sortedAudienceEntryERCs.indexOf(audienceEntryERC);

	if (index !== -1) {
		return index;
	}

	return (
		sortedAudienceEntryERCs.length +
		audiences.getPriority(audienceEntryERC)
	);
}

function getWinnerAudienceEntryERC(targetElement) {
	const matchedAudienceEntryERCs = audiences.get();

	let winnerAudienceEntryERC = null;
	let winnerScore = Infinity;

	getGroupAudienceEntryERCs(targetElement).forEach((audienceEntryERC) => {
		if (!matchedAudienceEntryERCs.has(audienceEntryERC)) {
			return;
		}

		const score = getScore(audienceEntryERC);

		if (score < winnerScore) {
			winnerScore = score;
			winnerAudienceEntryERC = audienceEntryERC;
		}
	});

	return winnerAudienceEntryERC;
}

const appliedElementVariations = new Set();

function applyElementVariationOnce(elementVariation) {
	if (appliedElementVariations.has(elementVariation)) {
		return;
	}

	appliedElementVariations.add(elementVariation);

	applyElementVariation(elementVariation);
}

const elementVariations = [$ELEMENT_VARIATIONS$];
const sortedAudienceEntryERCs = [$SORTED_AUDIENCE_ENTRY_ERCS$];

export function register() {
	appliedElementVariations.clear();

	elementVariations.forEach((elementVariation) => {
		elementVariation.audienceEntryERCs.forEach((audienceEntryERC) => {
			audiences.on(audienceEntryERC, () => {
				if (
					elementVariation.targetElement &&
					getWinnerAudienceEntryERC(elementVariation.targetElement) !==
						audienceEntryERC
				) {
					return;
				}

				applyElementVariationOnce(elementVariation);
			});
		});
	});
}